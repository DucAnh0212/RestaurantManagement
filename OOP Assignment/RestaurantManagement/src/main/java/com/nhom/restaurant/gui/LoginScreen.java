package com.nhom.restaurant.gui;

import com.nhom.restaurant.models.Employees;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;

public class LoginScreen extends JPanel {

    private MainApplication mainApp;
    private JTextField idField;
    private JTextField nameField;
    private JCheckBox rememberMeCheckBox;

    public LoginScreen(MainApplication mainApp) {
        this.mainApp = mainApp;
        setLayout(new BorderLayout());

        JLabel titleLabel = new JLabel("Hệ thống quản lý nhà hàng", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 40));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(50, 0, 0, 0));
        add(titleLabel, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        JPanel loginPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        loginPanel.setBorder(BorderFactory.createTitledBorder("Đăng nhập Nhân viên"));

        JLabel idLabel = new JLabel("ID Nhân viên:");
        idLabel.setFont(new Font("Arial", Font.PLAIN, 18));
        idField = new JTextField(20);
        idField.setFont(new Font("Arial", Font.PLAIN, 18));

        JLabel nameLabel = new JLabel("Tên Nhân viên:");
        nameLabel.setFont(new Font("Arial", Font.PLAIN, 18));
        nameField = new JTextField(20);
        nameField.setFont(new Font("Arial", Font.PLAIN, 18));

        // === THÊM CHECKBOX ===
        rememberMeCheckBox = new JCheckBox("Ghi nhớ đăng nhập");
        rememberMeCheckBox.setFont(new Font("Arial", Font.PLAIN, 16));
        // === KẾT THÚC THÊM CHECKBOX ===

        JButton loginButton = new JButton("Đăng nhập");
        loginButton.setFont(new Font("Arial", Font.BOLD, 18));

        loginPanel.add(idLabel);
        loginPanel.add(idField);
        loginPanel.add(nameLabel);
        loginPanel.add(nameField);
        loginPanel.add(rememberMeCheckBox); // <-- THÊM VÀO PANEL
        loginPanel.add(loginButton); // <-- Nút Login giờ ở hàng cuối cùng

        centerPanel.add(loginPanel, gbc);
        add(centerPanel, BorderLayout.CENTER);

        loginButton.addActionListener(e -> handleLogin());
    }

    private void handleLogin() {
        String idInput = idField.getText();
        String nameInput = nameField.getText().trim();

        if (idInput.isEmpty() || nameInput.isEmpty()) {
            showError("Vui lòng nhập cả ID và Tên Nhân viên.");
            return;
        }

        try {
            int employeeId = Integer.parseInt(idInput);
            Employees employee = Employees.findById(employeeId); //

            if (employee == null) {
                showError("ID nhân viên không tồn tại.");
            } else if (employee.getFull_name().equalsIgnoreCase(nameInput)) {

                if (rememberMeCheckBox.isSelected()) {
                    AuthService.saveLogin(employee.getId());
                }
                else {
                    AuthService.clearLogin();
                }
                JOptionPane.showMessageDialog(this, "Đăng nhập thành công! Xin chào " + employee.getFull_name());
                mainApp.loginSuccess(employee);
            }
            else {
                showError("Tên nhân viên hoặc ID không trùng khớp.");
            }
        } catch (NumberFormatException e) {
            showError("ID Nhân viên phải là một con số.");
        }
    }
    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Lỗi Đăng nhập", JOptionPane.ERROR_MESSAGE);
    }
}