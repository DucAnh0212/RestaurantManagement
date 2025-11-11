package com.nhom.restaurant.gui;

import com.nhom.restaurant.manager.OrderManager;
import com.nhom.restaurant.models.Employees;
import com.nhom.restaurant.models.MenuItems;
import com.nhom.restaurant.models.OrderItems;
import com.nhom.restaurant.models.Orders;
import com.nhom.restaurant.utils.ImageCache; // Đảm bảo đã import ImageCache

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.sql.SQLException;
import java.text.Normalizer;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class MenuSelectionPanel extends JPanel {

    private TableSelectionPanel parentPanel;
    private Orders currentOrder;
    private Employees currentUser;
    private OrderManager orderManager;

    private JPanel categoryButtonPanel;
    private JButton selectedCategoryButton;

    private Font categoryFontInactive = new Font("Arial", Font.BOLD, 18);
    private Font categoryFontActive = new Font("Arial", Font.BOLD | Font.ITALIC, 18);

    private JPanel menuItemsGridPanel;
    private JList<OrderItems> cartList;
    private DefaultListModel<OrderItems> cartListModel;
    private JLabel lblTotalAmount;
    private JLabel lblPanelTitle;
    private JButton btnTang, btnGiam, btnXoa;
    private JButton btnHoanThanh, btnHuy, btnThanhToan;

    private NumberFormat currencyFormatter;
    private final Pattern DIACRITICS_PATTERN = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");

    public MenuSelectionPanel(TableSelectionPanel parent, OrderManager manager, Employees employee) {
        this.parentPanel = parent;
        this.orderManager = manager;
        this.currentUser = employee;
        this.currencyFormatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        lblPanelTitle = new JLabel("Quản lý Order");
        lblPanelTitle.setFont(new Font("Arial", Font.BOLD, 24));
        lblPanelTitle.setHorizontalAlignment(SwingConstants.CENTER);
        add(lblPanelTitle, BorderLayout.NORTH);

        add(createCategoryPanel(), BorderLayout.WEST);
        add(createMenuGridPanel(), BorderLayout.CENTER);
        add(createCartPanel(), BorderLayout.EAST);
        add(createBottomButtonPanel(), BorderLayout.SOUTH);
    }

    public void loadOrder(Orders order) {
        this.currentOrder = order;
        lblPanelTitle.setText(String.format("Bàn %d - Nhân viên: %s (Order ID: %d)",
                currentOrder.getTable_id(), currentUser.getFull_name(), currentOrder.getID()));
        try {
            loadCategories();
            updateCartAndTotal();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private JComponent createCategoryPanel() {
        categoryButtonPanel = new JPanel(new GridLayout(0, 1, 0, 5));
        categoryButtonPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 5));
        JScrollPane scrollPane = new JScrollPane(categoryButtonPanel);
        scrollPane.setPreferredSize(new Dimension(200, 0));
        scrollPane.setBorder(null);
        return scrollPane;
    }

    private JComponent createMenuGridPanel() {
        menuItemsGridPanel = new JPanel(new GridLayout(0, 4, 15, 15));
        menuItemsGridPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        JScrollPane scrollPane = new JScrollPane(menuItemsGridPanel);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Chọn món"));
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        return scrollPane;
    }

    private JComponent createCartPanel() {
        JPanel cartPanel = new JPanel(new BorderLayout(5, 5));
        cartPanel.setPreferredSize(new Dimension(350, 0));
        cartPanel.setBorder(BorderFactory.createTitledBorder("Order Hiện Tại"));
        cartListModel = new DefaultListModel<>();
        cartList = new JList<>(cartListModel);
        cartList.setFont(new Font("Arial", Font.PLAIN, 14));
        cartList.setCellRenderer(new OrderItemCellRenderer());
        cartPanel.add(new JScrollPane(cartList), BorderLayout.CENTER);

        JPanel cartControlPanel = new JPanel(new GridLayout(2, 1, 5, 5));
        JPanel topButtons = new JPanel(new FlowLayout());
        btnTang = new JButton("+1"); btnGiam = new JButton("-1"); btnXoa = new JButton("Xóa Món");
        topButtons.add(btnTang); topButtons.add(btnGiam); topButtons.add(btnXoa);
        cartControlPanel.add(topButtons);

        lblTotalAmount = new JLabel("Tổng tiền: 0 đ");
        lblTotalAmount.setFont(new Font("Arial", Font.BOLD, 20));
        lblTotalAmount.setHorizontalAlignment(SwingConstants.CENTER);
        cartControlPanel.add(lblTotalAmount);

        cartPanel.add(cartControlPanel, BorderLayout.SOUTH);
        addCartButtonActions();
        return cartPanel;
    }

    private JComponent createBottomButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        panel.setBorder(BorderFactory.createEtchedBorder());

        btnHoanThanh = new JButton("Hoàn thành (Quay lại)");
        btnHoanThanh.setFont(new Font("Arial", Font.BOLD, 14));
        btnHoanThanh.setBackground(Color.GRAY); btnHoanThanh.setForeground(Color.WHITE);
        btnHoanThanh.setOpaque(true); btnHoanThanh.setBorderPainted(false);

        btnHuy = new JButton("Hủy Bàn");
        btnHuy.setFont(new Font("Arial", Font.BOLD, 14));
        btnHuy.setBackground(Color.ORANGE); btnHuy.setForeground(Color.BLACK);
        btnHuy.setOpaque(true); btnHuy.setBorderPainted(false);

        btnThanhToan = new JButton("Thanh Toán");
        btnThanhToan.setFont(new Font("Arial", Font.BOLD, 14));
        btnThanhToan.setBackground(Color.BLUE); btnThanhToan.setForeground(Color.WHITE);
        btnThanhToan.setOpaque(true); btnThanhToan.setBorderPainted(false);

        btnHoanThanh.addActionListener(e -> parentPanel.showTableGrid(true));
        btnHuy.addActionListener(e -> handleCancelOrder());
        btnThanhToan.addActionListener(e -> handleCheckout());

        panel.add(btnHoanThanh); panel.add(btnHuy); panel.add(btnThanhToan);
        return panel;
    }

    // --- LOGIC ---
    private String normalizeCategory(String category) {
        if (category == null) return "Không xác định";
        if (category.equalsIgnoreCase("Đồ UỐng") || category.equalsIgnoreCase("Đồ Uống")) return "Đồ Uống";
        return category;
    }

    private void loadCategories() throws SQLException {
        categoryButtonPanel.removeAll(); selectedCategoryButton = null;
        JButton allButton = createCategoryButton("Tất cả");
        List<MenuItems> allItems = MenuItems.findAll();
        List<String> categories = allItems.stream().map(MenuItems::getCategory).map(this::normalizeCategory).distinct().sorted().collect(Collectors.toList());
        for (String category : categories) createCategoryButton(category);
        allButton.doClick();
        categoryButtonPanel.revalidate(); categoryButtonPanel.repaint();
    }

    private JButton createCategoryButton(String categoryName) {
        JButton button = new JButton(categoryName);
        button.setFont(categoryFontInactive);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(0, 50));
        button.setForeground(Color.BLACK);
        button.setOpaque(false);
        button.setContentAreaFilled(false);
        button.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));

        button.addActionListener(e -> {
            try {
                if (selectedCategoryButton != null) {
                    selectedCategoryButton.setFont(categoryFontInactive);
                    selectedCategoryButton.setForeground(Color.BLACK);
                }
                button.setFont(categoryFontActive);
                button.setForeground(Color.BLACK);
                selectedCategoryButton = button;
                loadMenuItems(categoryName);
            } catch (SQLException ex) { ex.printStackTrace(); }
        });
        categoryButtonPanel.add(button); return button;
    }

    private String normalizeName(String s) {
        if (s == null) return "";
        String nfdNormalizedString = Normalizer.normalize(s, Normalizer.Form.NFD);
        String noAccents = DIACRITICS_PATTERN.matcher(nfdNormalizedString).replaceAll("");
        noAccents = noAccents.replaceAll("Đ", "D").replaceAll("đ", "d");
        String lower = noAccents.toLowerCase();
        String withUnderscores = lower.replaceAll("\\s+", "_");
        String clean = withUnderscores.replaceAll("[^a-z0-9_]", "");
        return clean;
    }

    private String getImagePathForItem(MenuItems item) {
        if (item == null || item.getName() == null) return null;
        String normalizedName = normalizeName(item.getName());
        String basePath = "images/" + normalizedName;
        File pngFile = new File(basePath + ".png");
        if (pngFile.exists()) return basePath + ".png";
        File jpgFile = new File(basePath + ".jpg");
        if (jpgFile.exists()) return basePath + ".jpg";
        File jpegFile = new File(basePath + ".jpeg");
        if (jpegFile.exists()) return basePath + ".jpeg";
        return null;
    }

    private void loadMenuItems(String category) throws SQLException {
        menuItemsGridPanel.removeAll();
        List<MenuItems> itemsToDisplay;
        if (category.equals("Tất cả")) itemsToDisplay = MenuItems.findAll();
        else if (category.equals("Đồ Uống")) {
            itemsToDisplay = MenuItems.findAllByCategory("Đồ Uống");
            itemsToDisplay.addAll(MenuItems.findAllByCategory("Đồ UỐng"));
        } else itemsToDisplay = MenuItems.findAllByCategory(category);
        for (MenuItems item : itemsToDisplay) menuItemsGridPanel.add(createMenuItemCard(item));
        menuItemsGridPanel.revalidate(); menuItemsGridPanel.repaint();
    }

    private JPanel createMenuItemCard(MenuItems item) {
        JPanel card = new JPanel(new BorderLayout(0, 0));

        card.setPreferredSize(new Dimension(220, 280));

        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                BorderFactory.createEmptyBorder(10, 10, 5, 10)
        ));
        card.setBackground(Color.WHITE);

        JLabel imageLabel = new JLabel();
        imageLabel.setHorizontalAlignment(SwingConstants.CENTER);

        imageLabel.setPreferredSize(new Dimension(200, 130));

        String imagePath = getImagePathForItem(item);
        if (imagePath != null) {
            // Load ảnh từ Cache và ép kích thước 200x130
            ImageIcon icon = ImageCache.getImage(imagePath, 200, 130);
            if (icon != null) {
                imageLabel.setIcon(icon);
            } else {
                imageLabel.setText("<html><center><i>Lỗi ảnh</i></center></html>");
            }
        } else {
            imageLabel.setText("<html><center><i>Chưa có<br>hình ảnh</i></center></html>");
            imageLabel.setForeground(Color.GRAY);
        }
        card.add(imageLabel, BorderLayout.NORTH);

        String priceFormatted = currencyFormatter.format(item.getPrice());
        String htmlText = String.format("<html><center><font size='5'><b>%s</b></font><br><font size='4' color='red'>%s</font></center></html>",
                item.getName(), priceFormatted);

        JLabel nameLabel = new JLabel(htmlText);
        nameLabel.setHorizontalAlignment(SwingConstants.CENTER);
        nameLabel.setVerticalAlignment(SwingConstants.TOP);
        card.add(nameLabel, BorderLayout.CENTER);

        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
        controlPanel.setBackground(Color.WHITE);
        JLabel lblSL = new JLabel("SL:");
        JSpinner quantitySpinner = new JSpinner(new SpinnerNumberModel(1, 1, 99, 1));
        quantitySpinner.setPreferredSize(new Dimension(50, 30));

        JButton btnThem = new JButton("Thêm");
        btnThem.setFont(new Font("Arial", Font.BOLD, 14));
        btnThem.setBackground(Color.decode("#28a745"));
        btnThem.setForeground(Color.WHITE);
        btnThem.setFocusPainted(false);

        btnThem.setOpaque(true);
        btnThem.setBorderPainted(false);

        btnThem.addActionListener(e -> {
            try {
                int quantity = (int) quantitySpinner.getValue();
                currentOrder.addItem(item, quantity);
                updateCartAndTotal();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage());
            }
        });

        controlPanel.add(lblSL);
        controlPanel.add(quantitySpinner);
        controlPanel.add(btnThem);

        card.add(controlPanel, BorderLayout.SOUTH);
        return card;
    }

    private void updateCartAndTotal() {
        try {
            if (currentOrder == null) return;
            currentOrder.fetchOrderItems();
            cartListModel.clear();
            for (OrderItems item : currentOrder.getItems()) cartListModel.addElement(item);
            int total = currentOrder.calculateTotal();
            lblTotalAmount.setText(currencyFormatter.format(total));
            if (parentPanel != null && parentPanel.getMainApp() != null) {
                parentPanel.getMainApp().triggerRefreshHistory();
            }
        } catch (SQLException e) { e.printStackTrace(); }
    }
    private void addCartButtonActions() {
        btnTang.addActionListener(e -> {
            OrderItems selectedItem = cartList.getSelectedValue();
            if (selectedItem == null) return;
            try {
                MenuItems menuItem = MenuItems.findById(selectedItem.getMenu_item_id());
                currentOrder.addItem(menuItem, 1); updateCartAndTotal();
            } catch (SQLException ex) { ex.printStackTrace(); }
        });
        btnGiam.addActionListener(e -> {
            OrderItems selectedItem = cartList.getSelectedValue();
            if (selectedItem == null) return;
            try {
                currentOrder.reduceItemQuantity(selectedItem.getId()); updateCartAndTotal();
            } catch (SQLException ex) { ex.printStackTrace(); }
        });
        btnXoa.addActionListener(e -> {
            OrderItems selectedItem = cartList.getSelectedValue();
            if (selectedItem == null) return;
            try {
                currentOrder.deleteItem(selectedItem.getId()); updateCartAndTotal();
            } catch (SQLException ex) { ex.printStackTrace(); }
        });
    }
    private void handleCancelOrder() {
        int choice = JOptionPane.showConfirmDialog(this, "Bạn có chắc muốn HỦY order này?", "Xác nhận Hủy", JOptionPane.YES_NO_OPTION);
        if (choice == JOptionPane.YES_OPTION) {
            try {
                orderManager.cancelOrder(currentOrder.getID());
                JOptionPane.showMessageDialog(this, "Đã hủy order thành công.");
                parentPanel.getMainApp().triggerRefreshHistory();
                parentPanel.showTableGrid(true);
            } catch (SQLException ex) { JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage()); }
        }
    }
    private void handleCheckout() {
        int choice = JOptionPane.showConfirmDialog(this, "Xác nhận thanh toán hóa đơn này?", "Xác nhận Thanh Toán", JOptionPane.YES_NO_OPTION);
        if (choice == JOptionPane.YES_OPTION) {
            try {
                orderManager.checkOut(currentOrder.getID());
                JOptionPane.showMessageDialog(this, "Đã thanh toán thành công.");
                parentPanel.getMainApp().triggerRefreshHistory();
                parentPanel.showTableGrid(true);
            } catch (SQLException ex) { JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage()); }
        }
    }
}

class OrderItemCellRenderer extends JLabel implements ListCellRenderer<OrderItems> {
    private NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
    public OrderItemCellRenderer() { setOpaque(true); }
    @Override
    public Component getListCellRendererComponent(JList<? extends OrderItems> list, OrderItems item, int index, boolean isSelected, boolean cellHasFocus) {
        String text = String.format("<html><b>%d x %s</b><br>%s</html>",
                item.getQuantity(), item.getMenu_item_name(),
                currencyFormatter.format(item.getPrice() * item.getQuantity()));
        setText(text);
        setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        if (isSelected) {
            setBackground(list.getSelectionBackground());
            setForeground(list.getSelectionForeground());
        } else {
            setBackground(list.getBackground());
            setForeground(list.getForeground());
        }
        return this;
    }
}