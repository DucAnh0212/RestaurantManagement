package com.nhom.restaurant.gui;

import com.nhom.restaurant.manager.OrderManager;
import com.nhom.restaurant.models.Employees;
import com.nhom.restaurant.models.Orders;
import com.nhom.restaurant.models.Tables;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

public class TableSelectionScreen extends JPanel {

    private MainApplication mainApp;
    private OrderManager orderManager;
    private JPanel tableGridPanel; // Panel trung tâm để chứa các nút bàn

    private List<Tables> allTablesList;

    public TableSelectionScreen(MainApplication mainApp) {
        this.mainApp = mainApp;
        this.orderManager = new OrderManager();

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel titleLabel = new JLabel("Danh Sách Bàn", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 32));
        add(titleLabel, BorderLayout.NORTH);

        JPanel floorSelectionPanel = createFloorSelectionPanel();
        add(floorSelectionPanel, BorderLayout.WEST);

        tableGridPanel = new JPanel(new GridLayout(0, 4, 15, 15)); // 15px khoảng cách
        tableGridPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JScrollPane scrollPane = new JScrollPane(tableGridPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        add(scrollPane, BorderLayout.CENTER);

    }

    public void loadData() {
        try {
            this.allTablesList = Tables.findAll();
            displayTablesForFloor(1); // Hiển thị tầng 1 làm mặc định

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Lỗi nghiêm trọng: Không thể tải danh sách bàn từ CSDL.",
                    "Lỗi CSDL",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private JPanel createFloorSelectionPanel() {
        JPanel panel = new JPanel(new GridLayout(3, 1)); // 3 hàng, 1 cột
        panel.setBorder(BorderFactory.createTitledBorder("Chọn Tầng"));

        JButton floor1Button = new JButton("Tầng 1");
        JButton floor2Button = new JButton("Tầng 2");
        JButton floor3Button = new JButton("Tầng 3");

        Dimension buttonSize = new Dimension(150, 80);
        floor1Button.setPreferredSize(buttonSize);
        floor2Button.setPreferredSize(buttonSize);
        floor3Button.setPreferredSize(buttonSize);

        floor1Button.addActionListener(e -> displayTablesForFloor(1));
        floor2Button.addActionListener(e -> displayTablesForFloor(2));
        floor3Button.addActionListener(e -> displayTablesForFloor(3));

        panel.add(floor1Button);
        panel.add(floor2Button);
        panel.add(floor3Button);

        JPanel wrapperPanel = new JPanel(new BorderLayout());
        wrapperPanel.add(panel, BorderLayout.NORTH);
        return wrapperPanel;
    }

    private void displayTablesForFloor(int floor) {
        tableGridPanel.removeAll();
        int startId = (floor - 1) * 12 + 1;
        int endId = floor * 12;

        List<Tables> floorTables = allTablesList.stream()
                .filter(t -> t.getId() >= startId && t.getId() <= endId)
                .collect(Collectors.toList());

        for (Tables table : floorTables) {
            String buttonText = String.format("<html><center>Bàn %d<br>(%s)</center></html>",
                    table.getId(), table.getStatus());
            JButton tableButton = new JButton(buttonText);
            tableButton.setFont(new Font("Arial", Font.BOLD, 18));

            if (table.getStatus().equals("Trống")) {
                tableButton.setBackground(new Color(102, 204, 153));
            } else {
                tableButton.setBackground(new Color(255, 102, 102));
            }
            tableButton.setOpaque(true);
            tableButton.setBorderPainted(false);

            tableButton.addActionListener(e -> handleTableClick(table));

            tableGridPanel.add(tableButton);
        }

        tableGridPanel.revalidate();
        tableGridPanel.repaint();
    }

    private void handleTableClick(Tables selectedTable) {
        try {
            if (selectedTable.getStatus().equals("Trống")) {
                int confirm = JOptionPane.showConfirmDialog(this,
                        "Mở order mới cho Bàn " + selectedTable.getId() + "?",
                        "Xác nhận mở bàn",
                        JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    Employees currentUser = mainApp.getLoggedInEmployee();

                    if (currentUser == null) {
                        JOptionPane.showMessageDialog(this, "Lỗi: Không tìm thấy nhân viên đã đăng nhập.", "Lỗi hệ thống", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    Orders newOrder = orderManager.startNewOrder(selectedTable.getId(), currentUser.getId());

                    if (newOrder != null) {
                        newOrder.setEmployee_name(currentUser.getFull_name());
                        mainApp.switchToOrderScreen(newOrder);
                    }
                    else {
                        JOptionPane.showMessageDialog(this, "Không thể tạo order.", "Lỗi", JOptionPane.ERROR_MESSAGE);
                        loadData();
                    }
                }
            }
            else {
                Orders activeOrder = Orders.findActiveByTableId(selectedTable.getId());
                if (activeOrder != null) {
                    mainApp.switchToOrderScreen(activeOrder);
                }
                else {
                    JOptionPane.showMessageDialog(this,
                            "Lỗi: Không tìm thấy order đang hoạt động cho bàn này. Đang reset bàn...",
                            "Lỗi dữ liệu",
                            JOptionPane.ERROR_MESSAGE);
                    selectedTable.markAsAvailable();
                    loadData();
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Lỗi CSDL khi xử lý bàn!",
                    "Lỗi CSDL",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}
