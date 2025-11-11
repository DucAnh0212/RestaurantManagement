package com.nhom.restaurant.gui;

import com.nhom.restaurant.models.Employees;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginScreen extends JFrame {

    private JTextField txtUsername;

    private JTextField txtPassword; // <--- ĐÃ SỬA

    private JButton btnLogin;

    public LoginScreen() {
        setTitle("Đăng nhập hệ thống quản lý nhà hàng");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setUndecorated(false);

        Container contentPane = getContentPane();
        contentPane.setLayout(new GridBagLayout());
        contentPane.setBackground(Color.decode("#f0f4f8"));

        JPanel loginPanel = new JPanel();
        loginPanel.setLayout(new GridBagLayout());
        loginPanel.setBackground(Color.WHITE);
        loginPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "Đăng nhập nhân viên"));
        loginPanel.setPreferredSize(new Dimension(450, 250));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        Font labelFont = new Font("Arial", Font.BOLD, 16);
        Font textFont = new Font("Arial", Font.PLAIN, 16);

        JLabel lblUsername = new JLabel("Tài khoản (ID):");
        lblUsername.setFont(labelFont);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        loginPanel.add(lblUsername, gbc);

        txtUsername = new JTextField(20);
        txtUsername.setFont(textFont);
        gbc.gridx = 1;
        gbc.gridy = 0;
        loginPanel.add(txtUsername, gbc);

        JLabel lblPassword = new JLabel("Mật khẩu (Tên):");
        lblPassword.setFont(labelFont);
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        loginPanel.add(lblPassword, gbc);

        txtPassword = new JTextField(20);
        txtPassword.setFont(textFont);
        gbc.gridx = 1;
        gbc.gridy = 1;
        loginPanel.add(txtPassword, gbc);

        btnLogin = new JButton("Đăng nhập");
        btnLogin.setFont(labelFont);
        btnLogin.setBackground(Color.decode("#007bff"));
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btnLogin.setOpaque(true);
        btnLogin.setBorderPainted(false);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.NONE;
        gbc.ipadx = 50;
        gbc.ipady = 10;
        loginPanel.add(btnLogin, gbc);

        contentPane.add(loginPanel, new GridBagConstraints());

        addLoginActionListener();
        this.getRootPane().setDefaultButton(btnLogin);
    }

    private void addLoginActionListener() {
        btnLogin.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleLogin();
            }
        });
    }

    private void handleLogin() {
        String idText = txtUsername.getText().trim();

        String password = txtPassword.getText().trim(); // <--- ĐÃ SỬA (bỏ .trim() để nhận dấu cách)

        if (idText.isEmpty()) {
            showMessage("Vui lòng nhập ID tài khoản.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int employeeId;
        try {
            employeeId = Integer.parseInt(idText);
        } catch (NumberFormatException ex) {
            showMessage("ID tài khoản phải là một con số.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Employees employee = Employees.findById(employeeId);

        if (employee == null) {
            showMessage("Nhân viên không tồn tại (ID: " + employeeId + ")", "Lỗi Đăng nhập", JOptionPane.ERROR_MESSAGE);
        }
        else {
            if (employee.getFull_name().trim().equals(password)) {
                showMessage("Đăng nhập thành công! Xin chào " + employee.getFull_name(), "Thành công", JOptionPane.INFORMATION_MESSAGE);

                MainApplication mainScreen = new MainApplication(employee);
                mainScreen.setVisible(true);

                dispose();

            } else {
                showMessage("Tài khoản hoặc mật khẩu không chính xác.", "Lỗi Đăng nhập", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void showMessage(String message, String title, int messageType) {
        JOptionPane.showMessageDialog(this, message, title, messageType);
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new LoginScreen().setVisible(true);
            }
        });
    }
}