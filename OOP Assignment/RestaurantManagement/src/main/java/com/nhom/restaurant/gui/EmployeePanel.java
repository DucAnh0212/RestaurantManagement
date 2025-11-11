package com.nhom.restaurant.gui;

import com.nhom.restaurant.models.Employees;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.util.List;

public class EmployeePanel extends JPanel {

    private JTable table;
    private DefaultTableModel tableModel;

    public EmployeePanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        setBackground(Color.decode("#f0f4f8")); // Màu nền nhẹ

        JLabel lblTitle = new JLabel("Danh Sách Nhân Viên");
        lblTitle.setFont(new Font("Arial", Font.BOLD, 24));
        lblTitle.setForeground(Color.decode("#007bff"));
        lblTitle.setHorizontalAlignment(SwingConstants.CENTER);
        add(lblTitle, BorderLayout.NORTH);

        createEmployeeTable();

        loadData();
    }

    private void createEmployeeTable() {
        String[] columnNames = {"ID", "Họ và Tên", "Số Điện Thoại", "Email", "Ca Làm Việc", "Địa Chỉ"};

        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(tableModel);

        table.setFont(new Font("Arial", Font.PLAIN, 14));
        table.setRowHeight(30);
        table.setFillsViewportHeight(true);

        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Arial", Font.BOLD, 14));
        header.setBackground(Color.decode("#007bff"));
        header.setForeground(Color.WHITE);
        header.setReorderingAllowed(false);

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);

        DefaultTableCellRenderer leftRenderer = new DefaultTableCellRenderer();
        leftRenderer.setHorizontalAlignment(JLabel.LEFT);
        leftRenderer.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 0));

        table.getColumnModel().getColumn(0).setCellRenderer(centerRenderer); // ID
        table.getColumnModel().getColumn(1).setCellRenderer(leftRenderer);   // Tên
        table.getColumnModel().getColumn(2).setCellRenderer(centerRenderer); // SĐT
        table.getColumnModel().getColumn(3).setCellRenderer(leftRenderer);   // Email
        table.getColumnModel().getColumn(4).setCellRenderer(centerRenderer); // Ca
        table.getColumnModel().getColumn(5).setCellRenderer(leftRenderer);   // Địa chỉ

        table.getColumnModel().getColumn(0).setPreferredWidth(50);  // ID
        table.getColumnModel().getColumn(1).setPreferredWidth(150); // Tên
        table.getColumnModel().getColumn(2).setPreferredWidth(100); // SĐT
        table.getColumnModel().getColumn(3).setPreferredWidth(150); // Email
        table.getColumnModel().getColumn(4).setPreferredWidth(100); // Ca
        table.getColumnModel().getColumn(5).setPreferredWidth(200); // Địa chỉ

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        add(scrollPane, BorderLayout.CENTER);
    }

    public void loadData() {
        tableModel.setRowCount(0);

        List<Employees> employees = Employees.findAll();

        for (Employees emp : employees) {
            Object[] row = {
                    emp.getId(),
                    emp.getFull_name(),
                    emp.getPhone_number(),
                    emp.getEmail(),
                    emp.getWorkshift(),
                    emp.getAddress()
            };
            tableModel.addRow(row);
        }
    }
}