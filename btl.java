import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.geom.Ellipse2D;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class btl extends JFrame {

    private CardLayout cardLayout = new CardLayout();
    private JPanel cardPanel = new JPanel(cardLayout);

    public static final String CUSTOMER_INFO_VIEW = "CustomerInfo";
    public static final String TABLE_SELECTION_VIEW = "TableSelection";
    public static final String MENU_ORDER_VIEW = "MenuOrder";

    private CustomerData currentCustomerData = new CustomerData();
    private String selectedTableName;

    // Map tĩnh để lưu trữ các đơn hàng của từng bàn
    public static final Map<String, Map<String, Integer>> ordersByTable = new HashMap<>();

    public btl() {
        setTitle("Bước 1: Chọn Bàn");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 700);

        TableSelectionPanel tableSelection = new TableSelectionPanel(this);
        cardPanel.add(tableSelection, TABLE_SELECTION_VIEW);

        CustomerInfoPanel customerInfo = new CustomerInfoPanel(this);
        cardPanel.add(customerInfo, CUSTOMER_INFO_VIEW);

        add(cardPanel);
        setLocationRelativeTo(null);

        cardLayout.show(cardPanel, TABLE_SELECTION_VIEW);

        setVisible(true);
    }

    public void showCustomerInfoView(String tableName) {
        this.selectedTableName = tableName;
        setTitle("Bước 2: Nhập thông tin cho " + tableName);
        cardLayout.show(cardPanel, CUSTOMER_INFO_VIEW);
    }

    // Xử lý đơn hàng mới (với giỏ hàng trống)
    public void showMenuOrderView(CustomerData data) {
        this.currentCustomerData = data;
        setTitle("Bước 3: Đặt Món - " + selectedTableName + " | Khách: " + data.name);

        Map<String, Integer> newCart = new HashMap<>();
        MenuOrderPanel menuOrder = new MenuOrderPanel(selectedTableName, this, newCart);

        String panelKey = MENU_ORDER_VIEW + "_" + selectedTableName;
        cardPanel.add(menuOrder, panelKey);
        cardLayout.show(cardPanel, panelKey);
    }

    // Xem/chỉnh sửa đơn hàng của bàn đã có khách
    public void showMenuOrderViewForExistingTable(String tableName) {
        this.selectedTableName = tableName;
        setTitle("Xem/Thêm Món - " + tableName);

        Map<String, Integer> existingCart = ordersByTable.getOrDefault(tableName, new HashMap<>());
        MenuOrderPanel menuOrder = new MenuOrderPanel(tableName, this, existingCart);

        String panelKey = MENU_ORDER_VIEW + "_" + tableName;
        cardPanel.add(menuOrder, panelKey);
        cardLayout.show(cardPanel, panelKey);
    }

    public void showTableSelectionView() {
        // Làm mới lại giao diện chọn bàn để cập nhật màu sắc
        TableSelectionPanel tableSelection = new TableSelectionPanel(this);
        cardPanel.add(tableSelection, TABLE_SELECTION_VIEW);
        setTitle("Bước 1: Chọn Bàn");
        cardLayout.show(cardPanel, TABLE_SELECTION_VIEW);
    }

    public void startNewSession() {
        setTitle("Bước 1: Chọn Bàn");
        cardLayout.show(cardPanel, TABLE_SELECTION_VIEW);
    }

    public static class CustomerData {
        public String name = "";
        public String phone = "";
        public int headcount = 1;
    }

    public static class RoundButton extends JButton {
        public RoundButton(String text) {
            super(text);
            setContentAreaFilled(false);
            setFocusPainted(false);
            setBorderPainted(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            if (getModel().isArmed()) {
                g2.setColor(getBackground().darker());
            } else {
                g2.setColor(getBackground());
            }
            g2.fillOval(0, 0, getSize().width - 1, getSize().height - 1);
            super.paintComponent(g);
            g2.dispose();
        }

        @Override
        public boolean contains(int x, int y) {
            return new Ellipse2D.Float(0, 0, getWidth(), getHeight()).contains(x, y);
        }
    }

    public static class CustomerInfoPanel extends JPanel {
        private btl mainApp;
        private JTextField nameField, phoneField, headcountField;

        public CustomerInfoPanel(btl app) {
            this.mainApp = app;
            setLayout(new BorderLayout());
            setBorder(BorderFactory.createEmptyBorder(100, 200, 100, 200));
            JPanel formPanel = new JPanel(new GridLayout(6, 1, 10, 10));

            Font titleFont = new Font("Arial", Font.BOLD, 20);
            formPanel.setBorder(BorderFactory.createTitledBorder(
                    null, "Nhập Thông Tin Khách Hàng",
                    javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
                    javax.swing.border.TitledBorder.DEFAULT_POSITION,
                    titleFont
            ));

            formPanel.add(new JLabel("Họ và Tên:"));
            nameField = new JTextField(20);
            formPanel.add(nameField);

            formPanel.add(new JLabel("Số Điện Thoại:"));
            phoneField = new JTextField(20);
            formPanel.add(phoneField);

            formPanel.add(new JLabel("Số Người Ăn:"));
            headcountField = new JTextField("1", 5);
            formPanel.add(headcountField);
            add(formPanel, BorderLayout.CENTER);

            JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 10, 0));

            JButton backButton = new JButton("CHỌN LẠI BÀN");
            backButton.addActionListener(e -> mainApp.showTableSelectionView());
            buttonPanel.add(backButton);

            JButton nextButton = new JButton("CONTINUE");
            nextButton.addActionListener(this::handleNext);
            buttonPanel.add(nextButton);

            add(buttonPanel, BorderLayout.SOUTH);
        }

        private void handleNext(ActionEvent e) {
            String name = nameField.getText().trim();
            String phone = phoneField.getText().trim();
            if (name.isEmpty() || phone.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ Họ tên và SĐT.", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
            try {
                int headcount = Integer.parseInt(headcountField.getText().trim());
                if (headcount <= 0) throw new NumberFormatException();
                CustomerData data = new CustomerData();
                data.name = name;
                data.phone = phone;
                data.headcount = headcount;
                mainApp.showMenuOrderView(data);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Số Người Ăn không hợp lệ.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public static class TableSelectionPanel extends JPanel {
        private btl mainApp;
        private JComboBox<String> floorSelector;
        private JPanel tableGrid;
        private static final String[] FLOORS = {"Tầng 1", "Tầng 2"};
        public static final Map<String, Boolean> GLOBAL_BOOKING_STATUS = createMockBookingData();

        public TableSelectionPanel(btl app) {
            this.mainApp = app;
            setLayout(new BorderLayout(20, 20));
            setBorder(BorderFactory.createEmptyBorder(20, 50, 50, 50));

            JPanel floorSelectionPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
            floorSelectionPanel.add(new JLabel("Chọn Tầng:"));
            floorSelector = new JComboBox<>(FLOORS);
            floorSelector.setPreferredSize(new Dimension(150, 30));
            floorSelectionPanel.add(floorSelector);
            add(floorSelectionPanel, BorderLayout.NORTH);

            tableGrid = new JPanel(new GridLayout(3, 4, 20, 20));
            tableGrid.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
            add(tableGrid, BorderLayout.CENTER);

            updateTableGrid();
            floorSelector.addActionListener(e -> updateTableGrid());
        }

        private static Map<String, Boolean> createMockBookingData() {
            Map<String, Boolean> data = new HashMap<>();
            for (int f = 1; f <= 2; f++) {
                for (int b = 1; b <= 12; b++) {
                    data.put("T" + f + "-B" + b, false); // false = bàn trống
                }
            }
            return data;
        }

        private void updateTableGrid() {
            String selectedFloorItem = (String) floorSelector.getSelectedItem();
            if (selectedFloorItem == null) return;
            int floorIndex = Integer.parseInt(selectedFloorItem.replaceAll("\\D+", ""));
            tableGrid.removeAll();
            for (int b = 1; b <= 12; b++) {
                String fullTableName = "T" + floorIndex + "-B" + b;
                String displayTableName = "Bàn " + b;
                Boolean isOccupied = GLOBAL_BOOKING_STATUS.getOrDefault(fullTableName, false);
                tableGrid.add(createTableButton(displayTableName, fullTableName, isOccupied));
            }
            tableGrid.revalidate();
            tableGrid.repaint();
        }

        private RoundButton createTableButton(String display, String fullId, boolean isOccupied) {
            RoundButton tableButton = new RoundButton(display);
            tableButton.setFont(new Font("Arial", Font.BOLD, 16));
            tableButton.setForeground(Color.WHITE);
            tableButton.setPreferredSize(new Dimension(100, 100));

            if (isOccupied) {
                tableButton.setBackground(new Color(220, 53, 69)); // Màu đỏ
                tableButton.setText("<html><center>" + display + "<br>(Đã Đặt)</center></html>");
                tableButton.setEnabled(true);
                tableButton.addActionListener(e -> mainApp.showMenuOrderViewForExistingTable(fullId));
            } else {
                tableButton.setBackground(new Color(39, 174, 96)); // Màu xanh
                tableButton.setEnabled(true);
                tableButton.addActionListener(e -> mainApp.showCustomerInfoView(fullId));
            }
            return tableButton;
        }
    }

    public static class MenuOrderPanel extends JPanel {
        private String tableName;
        private btl mainApp;
        private JPanel itemsGrid;
        private JTextArea cartTextArea;
        private JLabel totalLabel;
        private Map<String, Integer> cartItems;
        private Map<String, Integer> menuPrices = new HashMap<>();
        private Map<String, String> menuCategories = new HashMap<>();

        public MenuOrderPanel(String tableName, btl app, Map<String, Integer> initialCart) {
            this.tableName = tableName;
            this.mainApp = app;
            this.cartItems = initialCart;
            setLayout(new BorderLayout(10, 10));
            setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

            setupMenuData();

            JPanel headerPanel = new JPanel(new BorderLayout(10, 0));
            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));

            JButton backButton = new JButton("Chọn Bàn Khác");
            backButton.addActionListener(e -> mainApp.showTableSelectionView());
            buttonPanel.add(backButton);

            JButton logoutButton = new JButton("Phiên Mới");
            logoutButton.addActionListener(e -> {
                if (JOptionPane.showConfirmDialog(mainApp, "Bắt đầu một phiên mới?", "Xác nhận", JOptionPane.YES_NO_OPTION) == 0)
                    mainApp.startNewSession();
            });
            buttonPanel.add(logoutButton);

            headerPanel.add(buttonPanel, BorderLayout.WEST);
            headerPanel.add(new JLabel("Menu cho " + tableName, JLabel.CENTER), BorderLayout.CENTER);
            add(headerPanel, BorderLayout.NORTH);

            add(createCategoryPanel(), BorderLayout.WEST);
            add(createMenuPanel(), BorderLayout.CENTER);
            add(createCartPanel(), BorderLayout.EAST);

            updateMenuDisplay("Tất cả");
            updateCartDisplay();
        }

        // --- BẮT ĐẦU PHẦN CODE ĐÃ CẬP NHẬT THỰC ĐƠN ---
        private void setupMenuData() {
            // === DANH MỤC "KHAI VỊ" ===
            addMenuItemData("Khai vị", "Hến xúc bánh đa", 155000);
            addMenuItemData("Khai vị", "Ốc bươu nhồi thịt", 165000);
            addMenuItemData("Khai vị", "Mực chiên giòn", 175000);
            addMenuItemData("Khai vị", "Sụn gà chiên nước mắm", 155000);
            addMenuItemData("Khai vị", "Cơm cháy mỡ hành chà bông", 150000);
            addMenuItemData("Khai vị", "Tôm bọc cốm xanh", 165000);
            addMenuItemData("Khai vị", "Sụn gà rang muối", 155000);
            addMenuItemData("Khai vị", "Chả giò hải sản", 165000);
            addMenuItemData("Khai vị", "Chả mực Hạ Long", 155000);
            addMenuItemData("Khai vị", "Cơm cháy chảo kho quẹt", 150000);
            addMenuItemData("Khai vị", "Bánh xèo", 145000);
            addMenuItemData("Khai vị", "Bánh khọt nước dừa", 145000);

            // === DANH MỤC "ĂN CHƠI" ===
            addMenuItemData("Ăn Chơi", "Bắp giò heo rút xương lên mẹt", 189000);
            addMenuItemData("Ăn Chơi", "Chả cá lên mẹt", 189000);
            addMenuItemData("Ăn Chơi", "Gân bò xào cần", 165000);
            addMenuItemData("Ăn Chơi", "Nem nướng lên mẹt", 189000);
            addMenuItemData("Ăn Chơi", "Bò viên nước lèo", 150000);
            addMenuItemData("Ăn Chơi", "Bò viên gân chiên", 150000);
            addMenuItemData("Ăn Chơi", "Chả giò tôm đất", 150000);
            addMenuItemData("Ăn Chơi", "Ếch chiên nước mắm", 180000);
            addMenuItemData("Ăn Chơi", "Dồi sụn chấm mắm tôm", 160000);

            // === DANH MỤC "MÓN GỎI" ===
            addMenuItemData("Món Gỏi", "Salad dầu giấm", 110000);
            addMenuItemData("Món Gỏi", "Bò trộn salad", 160000);
            addMenuItemData("Món Gỏi", "Gỏi mực chua cay", 160000);
            addMenuItemData("Món Gỏi", "Gỏi cuốn tôm thịt", 150000);
            addMenuItemData("Món Gỏi", "Gỏi dưa leo tôm khô", 150000);
            addMenuItemData("Món Gỏi", "Gỏi tôm sốt thái", 175000);
            addMenuItemData("Món Gỏi", "Gỏi gà bắp chuối", 160000);
            addMenuItemData("Món Gỏi", "Gỏi xoài tôm khô", 160000);
            addMenuItemData("Món Gỏi", "Gỏi ngó sen tôm thịt", 160000);
            addMenuItemData("Món Gỏi", "Gỏi cuốn bò áp chảo", 160000);

            // === DANH MỤC "MÓN CHÍNH" (MỚI CẬP NHẬT) ===
            addMenuItemData("Món Chính", "Sườn xào chua ngọt", 175000);
            addMenuItemData("Món Chính", "Sườn ram mặn", 150000);
            addMenuItemData("Món Chính", "Sườn cọng chiên muối ớt", 205000);
            addMenuItemData("Món Chính", "Đậu hũ nhồi thịt sốt cà", 165000);
            addMenuItemData("Món Chính", "Đậu hũ chiên giòn chấm mắm tôm", 120000);
            addMenuItemData("Món Chính", "Mực chiên giòn", 190000);
            addMenuItemData("Món Chính", "Mực xào chua ngọt", 175000);
            addMenuItemData("Món Chính", "Mực ống nhồi thịt", 220000);
            addMenuItemData("Món Chính", "Mực chiên nước mắm", 185000);
            addMenuItemData("Món Chính", "Mực trứng hấp gừng", 210000);
            addMenuItemData("Món Chính", "Tôm rim mặn", 165000);
            addMenuItemData("Món Chính", "Tôm rim thịt", 150000);
            addMenuItemData("Món Chính", "Tép rong rang khế", 165000);
            addMenuItemData("Món Chính", "Tôm rang muối cay", 190000);
            addMenuItemData("Món Chính", "Bò xào rau muống", 150000);
            addMenuItemData("Món Chính", "Bò xào bí nụ", 150000);
            addMenuItemData("Món Chính", "Bò xào cải thìa", 120000);
            addMenuItemData("Món Chính", "Đọt su xào bò", 150000);
            addMenuItemData("Món Chính", "Bò xào cải ngồng", 150000);
            addMenuItemData("Món Chính", "Bò xào hành cần", 150000);
            addMenuItemData("Món Chính", "Cải bó xôi xào bò", 150000);
            addMenuItemData("Món Chính", "Bông hẹ xào bò", 150000);
            addMenuItemData("Món Chính", "Cá lóc kho tộ", 180000);
            addMenuItemData("Món Chính", "Cá basa kho tộ", 180000);
            addMenuItemData("Món Chính", "Cá thu kho tộ", 170000);
            addMenuItemData("Món Chính", "Cá trứng chiên mắm me", 160000);
            addMenuItemData("Món Chính", "Cá bống trứng kho tiêu", 170000);
            addMenuItemData("Món Chính", "Cá hú kho tộ", 175000);
            addMenuItemData("Món Chính", "Cá ngừ kho thơm", 150000);
            addMenuItemData("Món Chính", "Cá nục kho khô", 150000);
            addMenuItemData("Món Chính", "Cá nục kho măng", 150000);
            addMenuItemData("Món Chính", "Cá diêu hồng chiên sả", 150000);
            addMenuItemData("Món Chính", "Cá thu sốt cà", 175000);
            addMenuItemData("Món Chính", "Cá trê chiên mắm xoài", 150000);
            addMenuItemData("Món Chính", "Cá diêu hồng chiên xù cuốn bánh tráng", 220000);
            addMenuItemData("Món Chính", "Cá sặc trộn xoài", 150000);
            addMenuItemData("Món Chính", "Mắm kho miền tây", 150000);
            addMenuItemData("Món Chính", "Cá thu chiên mắm xoài", 170000);
            addMenuItemData("Món Chính", "Gà tre hấp mắm nhĩ", 375000);
            addMenuItemData("Món Chính", "Gà kho gừng", 150000);
            addMenuItemData("Món Chính", "Gà sả ớt", 150000);
            addMenuItemData("Món Chính", "Cánh gà chiên nước mắm", 150000);
            addMenuItemData("Món Chính", "Vịt kho gừng", 155000);
            addMenuItemData("Món Chính", "Trứng chiên thịt", 120000);
            addMenuItemData("Món Chính", "Trứng chiên hến", 135000);
            addMenuItemData("Món Chính", "Trứng chiên cà chua", 120000);
            addMenuItemData("Món Chính", "Trứng chiên hành", 110000);
            addMenuItemData("Món Chính", "Thịt kho tiêu", 150000);
            addMenuItemData("Món Chính", "Thịt kho trứng", 145000);
            addMenuItemData("Món Chính", "Ba rọi cháy cạnh", 150000);
            addMenuItemData("Món Chính", "Thịt luộc cà pháo mắm tôm", 150000);
            addMenuItemData("Món Chính", "Mắm chưng", 135000);
            addMenuItemData("Món Chính", "Ba rọi mắm ruốc", 150000);
            addMenuItemData("Món Chính", "Ba rọi rim dừa", 150000);
            addMenuItemData("Món Chính", "Nụ bí xào tỏi", 115000);
            addMenuItemData("Món Chính", "Rau muống xào tỏi", 115000);
            addMenuItemData("Món Chính", "Đọt su xào tỏi", 115000);
            addMenuItemData("Món Chính", "Cải bó xôi xào tỏi", 115000);
            addMenuItemData("Món Chính", "Đậu bắp luộc chấm chao", 115000);
            addMenuItemData("Món Chính", "Rau muống xào chao", 115000);
            addMenuItemData("Món Chính", "Bông hẹ xào tỏi", 115000);
        }
        // --- KẾT THÚC PHẦN CODE ĐÃ CẬP NHẬT THỰC ĐƠN ---

        private void addMenuItemData(String category, String name, int price) {
            menuPrices.put(name, price);
            menuCategories.put(name, category);
        }

        private JPanel createCategoryPanel() {
            JPanel panel = new JPanel(new GridLayout(8, 1, 10, 10));
            panel.setBorder(BorderFactory.createTitledBorder("Danh Mục"));
            String[] categories = {"Tất cả", "Khai vị", "Ăn Chơi", "Món Gỏi", "Món Chính", "Món canh", "Món rau", "Lẩu"};
            for (String cat : categories) {
                JButton btn = new JButton(cat);
                btn.addActionListener(e -> updateMenuDisplay(cat));
                panel.add(btn);
            }
            return panel;
        }

        private void updateMenuDisplay(String category) {
            itemsGrid.removeAll();
            Map<String, Integer> filteredMenu = new LinkedHashMap<>();
            for (String itemName : menuPrices.keySet()) {
                if (category.equals("Tất cả") || menuCategories.get(itemName).equals(category)) {
                    filteredMenu.put(itemName, menuPrices.get(itemName));
                }
            }
            for (Map.Entry<String, Integer> entry : filteredMenu.entrySet()) {
                itemsGrid.add(createMenuItemPanel(entry.getKey(), entry.getValue()));
            }
            itemsGrid.revalidate();
            itemsGrid.repaint();
        }

        private JScrollPane createMenuPanel() {
            JPanel panel = new JPanel(new BorderLayout());
            panel.setBorder(BorderFactory.createTitledBorder("Thực Đơn"));
            itemsGrid = new JPanel(new GridLayout(0, 2, 10, 10));
            itemsGrid.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            return new JScrollPane(itemsGrid);
        }

        private JPanel createMenuItemPanel(String name, int price) {
            JPanel itemPanel = new JPanel(new BorderLayout(5, 5));
            itemPanel.setBorder(BorderFactory.createLineBorder(Color.GRAY));

            JPanel infoPanel = new JPanel(new GridLayout(2, 1));
            infoPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
            infoPanel.add(new JLabel("<html><b>" + name + "</b></html>"));
            infoPanel.add(new JLabel(String.format("%,d VNĐ", price)));

            JPanel controlPanel = new JPanel(new BorderLayout(5, 0));

            SpinnerModel spinnerModel = new SpinnerNumberModel(1, 1, 100, 1);
            JSpinner quantitySpinner = new JSpinner(spinnerModel);
            quantitySpinner.setPreferredSize(new Dimension(45, 22));

            JButton addButton = new JButton("Thêm");
            addButton.setFont(new Font("Arial", Font.PLAIN, 10));
            addButton.setMargin(new Insets(1, 4, 1, 4));

            controlPanel.add(quantitySpinner, BorderLayout.CENTER);
            controlPanel.add(addButton, BorderLayout.EAST);

            addButton.addActionListener(e -> {
                int quantity = (Integer) quantitySpinner.getValue();
                cartItems.put(name, cartItems.getOrDefault(name, 0) + quantity);
                updateCartDisplay();
                quantitySpinner.setValue(1);
            });

            itemPanel.add(infoPanel, BorderLayout.CENTER);
            itemPanel.add(controlPanel, BorderLayout.EAST);
            return itemPanel;
        }

        private JPanel createCartPanel() {
            JPanel panel = new JPanel(new BorderLayout(10, 10));
            panel.setPreferredSize(new Dimension(420, 0));
            panel.setBorder(BorderFactory.createTitledBorder("Giỏ Hàng"));
            cartTextArea = new JTextArea("Chưa có món nào...");
            cartTextArea.setEditable(false);
            cartTextArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
            panel.add(new JScrollPane(cartTextArea), BorderLayout.CENTER);

            JPanel southPanel = new JPanel(new BorderLayout());
            totalLabel = new JLabel("Tổng tiền: 0 VNĐ", JLabel.CENTER);
            totalLabel.setFont(new Font("Arial", Font.BOLD, 16));
            JButton checkoutButton = new JButton("ĐẶT HÀNG");
            checkoutButton.setFont(new Font("Arial", Font.BOLD, 16));
            checkoutButton.setPreferredSize(new Dimension(0, 50));

            checkoutButton.addActionListener(e -> {
                if (cartItems.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Giỏ hàng trống!", "Thông báo", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                if (JOptionPane.showConfirmDialog(this, "Xác nhận đặt hàng/cập nhật đơn?", "Xác nhận", JOptionPane.YES_NO_OPTION) == 0) {
                    btl.ordersByTable.put(tableName, cartItems);
                    TableSelectionPanel.GLOBAL_BOOKING_STATUS.put(tableName, true);
                    JOptionPane.showMessageDialog(this, "Đã gửi đơn hàng thành công!");
                    mainApp.showTableSelectionView();
                }
            });

            southPanel.add(totalLabel, BorderLayout.NORTH);
            southPanel.add(checkoutButton, BorderLayout.CENTER);
            panel.add(southPanel, BorderLayout.SOUTH);
            return panel;
        }

        private void updateCartDisplay() {
            if (cartItems.isEmpty()) {
                cartTextArea.setText("Chưa có món nào...");
                totalLabel.setText("Tổng tiền: 0 VNĐ");
                return;
            }
            StringBuilder sb = new StringBuilder();
            long total = 0;

            sb.append(String.format("%-18s %-4s %22s\n", "Tên món", "SL", "Thành tiền"));
            sb.append("--------------------------------------------------\n");

            for (Map.Entry<String, Integer> entry : cartItems.entrySet()) {
                String itemName = entry.getKey();
                int quantity = entry.getValue();
                int price = menuPrices.get(itemName);
                long subtotal = (long) quantity * price;
                total += subtotal;
                sb.append(String.format("%-18.18s %-4d %,18d VNĐ\n", itemName, quantity, subtotal));
            }
            cartTextArea.setText(sb.toString());
            totalLabel.setText(String.format("Tổng tiền: %,d VNĐ", total));
        }
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        SwingUtilities.invokeLater(() -> new btl());
    }
}