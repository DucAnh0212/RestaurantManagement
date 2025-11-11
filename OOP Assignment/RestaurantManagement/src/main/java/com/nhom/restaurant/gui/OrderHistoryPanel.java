package com.nhom.restaurant.gui;

import com.nhom.restaurant.manager.ReportManager;
import com.nhom.restaurant.models.DailyReport;
import com.nhom.restaurant.models.OrderItems;
import com.nhom.restaurant.models.Orders;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.List;
import java.util.Locale;

public class OrderHistoryPanel extends JPanel {

    private JTable table;
    private DefaultTableModel tableModel;
    private JLabel lblRevenue;
    private JLabel lblCurrentDate;
    private JButton btnSelectDate;

    private LocalDate selectedDate;
    private NumberFormat currencyFormatter;
    private SimpleDateFormat timeFormatter;

    public OrderHistoryPanel() {
        this.selectedDate = LocalDate.now();
        this.currencyFormatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        this.timeFormatter = new SimpleDateFormat("HH:mm:ss");

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        setBackground(Color.decode("#f0f4f8"));

        add(createHeaderPanel(), BorderLayout.NORTH);
        add(createTablePanel(), BorderLayout.CENTER);

        refreshData();
    }

    public void refreshData() {
        loadDataByDate(selectedDate);
    }

    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);

        JPanel datePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        datePanel.setOpaque(false);

        lblCurrentDate = new JLabel("Ngày: " + selectedDate.toString());
        lblCurrentDate.setFont(new Font("Arial", Font.BOLD, 18)); // Chữ to hơn

        btnSelectDate = new JButton("Chọn Ngày");
        btnSelectDate.setFont(new Font("Arial", Font.BOLD, 14));
        btnSelectDate.setBackground(Color.WHITE);
        btnSelectDate.setFocusPainted(false);

        datePanel.add(lblCurrentDate);
        datePanel.add(Box.createHorizontalStrut(10));
        datePanel.add(btnSelectDate);

        JPanel revenuePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        revenuePanel.setOpaque(false);
        lblRevenue = new JLabel("Doanh thu: 0 đ");
        lblRevenue.setFont(new Font("Arial", Font.BOLD, 22)); // Chữ to hơn
        lblRevenue.setForeground(Color.decode("#d32f2f"));
        revenuePanel.add(lblRevenue);

        panel.add(datePanel, BorderLayout.WEST);
        panel.add(revenuePanel, BorderLayout.EAST);

        btnSelectDate.addActionListener(e -> showDatePicker());

        return panel;
    }

    private JScrollPane createTablePanel() {
        String[] columns = {"Order ID", "Bàn số", "Nhân viên", "Giờ tạo", "Tổng tiền", "Trạng thái"};

        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        table = new JTable(tableModel);
        table.setRowHeight(40); // Hàng cao hơn
        table.setFont(new Font("Arial", Font.PLAIN, 16)); // Chữ trong bảng to hơn
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Arial", Font.BOLD, 16));
        header.setReorderingAllowed(false);
        header.setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel lbl = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                lbl.setBackground(Color.decode("#007bff"));
                lbl.setForeground(Color.WHITE);
                lbl.setFont(new Font("Arial", Font.BOLD, 16));
                lbl.setHorizontalAlignment(CENTER);
                lbl.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
                return lbl;
            }
        });

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);

        table.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(1).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(3).setCellRenderer(centerRenderer);

        table.getColumnModel().getColumn(5).setCellRenderer(new StatusColumnRenderer());

        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 1) {
                    int row = table.getSelectedRow();
                    if (row != -1) {
                        int orderId = (int) table.getValueAt(row, 0);
                        showOrderDetailPopup(orderId);
                    }
                }
            }
        });

        return new JScrollPane(table);
    }

    private void loadDataByDate(LocalDate date) {
        tableModel.setRowCount(0);
        DailyReport report = ReportManager.genDailyReport(date);

        if (report != null) {
            List<Orders> ordersList = report.getCompletedOrders();
            for (Orders order : ordersList) {
                Object[] row = {
                        order.getID(),
                        order.getTable_id(),
                        order.getEmployee_name(),
                        timeFormatter.format(order.getTime()),
                        currencyFormatter.format(order.getTotal_amount()),
                        order.getStatus()
                };
                tableModel.addRow(row);
            }
            lblRevenue.setText("Doanh thu: " + currencyFormatter.format(report.getTotalRevenue()));
            lblCurrentDate.setText("Ngày: " + date.toString());
        }
    }

    private void showDatePicker() {
        JPanel p = new JPanel();
        JSpinner daySpinner = new JSpinner(new SpinnerNumberModel(selectedDate.getDayOfMonth(), 1, 31, 1));
        JSpinner monthSpinner = new JSpinner(new SpinnerNumberModel(selectedDate.getMonthValue(), 1, 12, 1));
        JSpinner yearSpinner = new JSpinner(new SpinnerNumberModel(selectedDate.getYear(), 2020, 2030, 1));

        p.add(new JLabel("Ngày:")); p.add(daySpinner);
        p.add(new JLabel("Tháng:")); p.add(monthSpinner);
        p.add(new JLabel("Năm:")); p.add(yearSpinner);

        int result = JOptionPane.showConfirmDialog(this, p, "Chọn ngày xem lịch sử", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            int d = (int) daySpinner.getValue();
            int m = (int) monthSpinner.getValue();
            int y = (int) yearSpinner.getValue();
            this.selectedDate = LocalDate.of(y, m, d);
            refreshData();
        }
    }

    private void showOrderDetailPopup(int orderId) {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Chi tiết Order #" + orderId, true);
        dialog.setSize(500, 400);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        DefaultListModel<String> listModel = new DefaultListModel<>();
        JList<String> list = new JList<>(listModel);
        list.setFont(new Font("Arial", Font.PLAIN, 16)); // Chữ popup to hơn

        try {
            Orders order = Orders.findById(orderId);
            if (order != null) {
                List<OrderItems> items = order.getItems();
                if (items.isEmpty()) {
                    listModel.addElement("Không có món ăn nào.");
                } else {
                    for (OrderItems item : items) {
                        String line = String.format("● %s (x%d) - %s",
                                item.getMenu_item_name(),
                                item.getQuantity(),
                                currencyFormatter.format(item.getPrice() * item.getQuantity()));
                        listModel.addElement(line);
                    }
                }
                listModel.addElement("------------------------------------------------");
                listModel.addElement("TỔNG CỘNG: " + currencyFormatter.format(order.getTotal_amount()));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            listModel.addElement("Lỗi tải chi tiết: " + e.getMessage());
        }

        dialog.add(new JScrollPane(list), BorderLayout.CENTER);
        dialog.setVisible(true);
    }

    class StatusColumnRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            String status = (String) value;
            setFont(new Font("Arial", Font.BOLD, 16)); // Chữ trạng thái to hơn
            setHorizontalAlignment(JLabel.CENTER);

            if ("Đang hoạt động".equals(status)) {
                setForeground(Color.BLUE);
            } else if ("Đã thanh toán".equals(status)) {
                setForeground(new Color(0, 153, 51));
            } else if ("Đã hủy".equals(status) || "Đã huỷ".equals(status)) {
                setForeground(Color.RED);
            } else {
                setForeground(Color.BLACK);
            }

            if (isSelected) {
                setBackground(table.getSelectionBackground());
                setForeground(table.getSelectionForeground());
            } else {
                setBackground(Color.WHITE);
            }
            return c;
        }
    }
}