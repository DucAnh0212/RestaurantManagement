package com.nhom.restaurant.gui;

import com.nhom.restaurant.manager.OrderManager;
import com.nhom.restaurant.models.MenuItems;
import com.nhom.restaurant.models.OrderItems;
import com.nhom.restaurant.models.Orders;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.net.URL;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class OrderScreen extends JPanel {

    private MainApplication mainApp;
    private OrderManager orderManager;
    private Orders currentOrder;

    private JPanel menuGridPanel;
    private DefaultListModel<String> orderItemsListModel;
    private JLabel totalLabel;

    private NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));

    public OrderScreen(MainApplication mainApp) {
        this.mainApp = mainApp;
        this.orderManager = new OrderManager();

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel titleLabel = new JLabel("Chọn Món Ăn", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 32));
        add(titleLabel, BorderLayout.NORTH);

        JPanel categoryPanel = createCategoryPanel();
        add(categoryPanel, BorderLayout.WEST);

        menuGridPanel = new JPanel(new GridLayout(0, 5, 10, 10));
        menuGridPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        JScrollPane menuScrollPane = new JScrollPane(menuGridPanel);
        add(menuScrollPane, BorderLayout.CENTER);

        JPanel currentOrderPanel = createCurrentOrderPanel();
        add(currentOrderPanel, BorderLayout.EAST);
    }

    public void loadData(Orders order) {
        this.currentOrder = order;
        ((JLabel)getComponent(0)).setText("Chọn Món - Bàn " + order.getTable_id());
        displayMenuItemsByCategory("Tất cả");
        updateCurrentOrderUI();
    }

    private JPanel createCategoryPanel() {
        JPanel panel = new JPanel(new GridLayout(0, 1));
        panel.setBorder(BorderFactory.createTitledBorder("Danh Mục"));
        String[] categories = {"Tất cả", "Món khai vị", "Món chính", "Món rau", "Đồ uống", "Tráng miệng"};
        Dimension buttonSize = new Dimension(150, 60);

        for (String category : categories) {
            JButton button = new JButton(category);
            button.setPreferredSize(buttonSize);
            button.addActionListener(e -> displayMenuItemsByCategory(category));
            panel.add(button);
        }

        JPanel wrapperPanel = new JPanel(new BorderLayout());
        wrapperPanel.add(panel, BorderLayout.NORTH);
        return wrapperPanel;
    }

    private JPanel createCurrentOrderPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setPreferredSize(new Dimension(300, 0));
        panel.setBorder(BorderFactory.createTitledBorder("Order Hiện Tại"));

        orderItemsListModel = new DefaultListModel<>();
        JList<String> list = new JList<>(orderItemsListModel);
        panel.add(new JScrollPane(list), BorderLayout.CENTER);

        JPanel southPanel = new JPanel(new BorderLayout(5, 5));
        totalLabel = new JLabel("Tổng tiền: 0 VND");
        totalLabel.setFont(new Font("Arial", Font.BOLD, 18));
        southPanel.add(totalLabel, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel(new GridLayout(1, 3));

        JButton checkoutButton = new JButton("Thanh toán");
        checkoutButton.setBackground(new Color(76, 175, 80));
        checkoutButton.setForeground(Color.WHITE);
        checkoutButton.addActionListener(e -> handleCheckout());

        JButton cancelButton = new JButton("Huỷ");
        cancelButton.setBackground(new Color(244, 67, 54));
        cancelButton.setForeground(Color.WHITE);
        cancelButton.addActionListener(e -> handleCancel());

        JButton completeButton = new JButton("Hoàn thành");
        completeButton.setBackground(new Color(33, 150, 243));
        completeButton.setForeground(Color.WHITE);
        completeButton.addActionListener(e -> mainApp.switchToTableScreen());

        buttonPanel.add(checkoutButton);
        buttonPanel.add(cancelButton);
        buttonPanel.add(completeButton);
        southPanel.add(buttonPanel, BorderLayout.CENTER);
        panel.add(southPanel, BorderLayout.SOUTH);

        return panel;
    }

    private void displayMenuItemsByCategory(String category) {
        menuGridPanel.removeAll();
        List<MenuItems> filteredList;
        try {
            if (category.equals("Tất cả")) {
                filteredList = MenuItems.findAll();
            }
            else {
                filteredList = MenuItems.findAllByCategory(category);
            }
            for (MenuItems item : filteredList) {
                JPanel card = createMenuItemCard(item);
                menuGridPanel.add(card);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            showError("Lỗi CSDL khi tải danh sách món ăn!");
        }
        menuGridPanel.revalidate();
        menuGridPanel.repaint();
    }

    private JPanel createMenuItemCard(MenuItems item) {
        JPanel card = new JPanel(new BorderLayout(5, 5));
        card.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1, true));

        try {
            URL imageUrl = new URL("https://placehold.co/150x100?text=" + item.getName().replace(" ", "+"));
            ImageIcon icon = new ImageIcon(imageUrl);
            JLabel imgLabel = new JLabel(icon);
            card.add(imgLabel, BorderLayout.NORTH);
        } catch (Exception e) { e.printStackTrace(); }

        JPanel infoPanel = new JPanel(new GridLayout(2, 1));
        infoPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        JLabel nameLabel = new JLabel("<html><b>" + item.getName() + "</b></html>");
        JLabel priceLabel = new JLabel(currencyFormatter.format(item.getPrice()));
        infoPanel.add(nameLabel);
        infoPanel.add(priceLabel);
        card.add(infoPanel, BorderLayout.CENTER);

        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5));
        JSpinner quantitySpinner = new JSpinner(new SpinnerNumberModel(1, 1, 20, 1));
        quantitySpinner.setPreferredSize(new Dimension(50, 30));

        JButton addButton = new JButton("Thêm");
        addButton.addActionListener(e -> {
            int quantity = (int) quantitySpinner.getValue();
            handleAddItem(item, quantity);
        });

        controlPanel.add(new JLabel("SL:"));
        controlPanel.add(quantitySpinner);
        controlPanel.add(addButton);
        card.add(controlPanel, BorderLayout.SOUTH);

        return card;
    }

    private void handleAddItem(MenuItems item, int quantity) {
        try {
            currentOrder.addItem(item, quantity);
            updateCurrentOrderUI();
        } catch (SQLException ex) {
            ex.printStackTrace();
            showError("Lỗi CSDL khi thêm món!");
        }
    }

    private void updateCurrentOrderUI() {
        orderItemsListModel.clear();
        List<OrderItems> items = currentOrder.getItems();
        if (items.isEmpty()) {
            orderItemsListModel.addElement("Chưa có món nào...");
        }
        try {
            for (OrderItems oi : items) {
                MenuItems menuItem = MenuItems.findById(oi.getMenu_item_id());
                if (menuItem != null) {
                    String itemName = menuItem.getName();
                    String entry = String.format("<html><b>%s</b> (x%d)<br>&nbsp;&nbsp;%s</html>",
                            itemName,
                            oi.getQuantity(),
                            currencyFormatter.format(oi.getPrice() * oi.getQuantity()));
                    orderItemsListModel.addElement(entry);
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            showError("Lỗi CSDL khi tải chi tiết giỏ hàng!");
        }

        totalLabel.setText("Tổng tiền: " + currencyFormatter.format(currentOrder.calculateTotal()));
    }

    private void handleCheckout() {
        try {
            orderManager.checkOut(currentOrder.getID());
            JOptionPane.showMessageDialog(this, "Thanh toán thành công!");
            mainApp.switchToTableScreen();
        } catch (SQLException ex) {
            ex.printStackTrace();
            showError("Lỗi CSDL khi thanh toán!");
        }
    }

    private void handleCancel() {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Bạn có chắc muốn huỷ order này?", "Xác nhận huỷ", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                orderManager.cancelOrder(currentOrder.getID());
                JOptionPane.showMessageDialog(this, "Đã huỷ order.");
                mainApp.switchToTableScreen();
            } catch (SQLException ex) {
                ex.printStackTrace();
                showError("Lỗi CSDL khi huỷ order!");
            }
        }
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Lỗi", JOptionPane.ERROR_MESSAGE);
    }
}
