package com.nhom.restaurant.gui;

import com.nhom.restaurant.models.Employees;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MainApplication extends JFrame {

    private Employees loggedInEmployee;

    private JButton btnDatBan, btnMenu, btnNhanVien, btnLichSu, btnLogout;
    private JButton[] navButtons;
    private JPanel mainContentPanel;
    private CardLayout cardLayout;

    private TableSelectionPanel tableSelectionPanel;
    private MenuPanel menuPanel;
    private EmployeePanel employeePanel;
    private OrderHistoryPanel orderHistoryPanel;

    private static final String DAT_BAN_PANEL = "DAT_BAN";
    private static final String MENU_PANEL = "MENU";
    private static final String NHAN_VIEN_PANEL = "NHAN_VIEN";
    private static final String LICH_SU_PANEL = "LICH_SU";

    private Font navFontActive, navFontInactive;

    public MainApplication(Employees employee) {
        this.loggedInEmployee = employee;
        navFontInactive = new Font("Arial", Font.BOLD, 16);
        navFontActive = new Font("Arial", Font.BOLD | Font.ITALIC, 16);

        setTitle("Hệ thống Quản lý Nhà hàng - Chào, " + loggedInEmployee.getFull_name());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLayout(new BorderLayout());

        add(createHeaderPanel(), BorderLayout.NORTH);
        mainContentPanel = createMainContentPanel();
        add(mainContentPanel, BorderLayout.CENTER);

        createActionListeners();
        setActiveButton(btnDatBan);
        cardLayout.show(mainContentPanel, DAT_BAN_PANEL);
    }

    public Employees getLoggedInEmployee() { return loggedInEmployee; }

    public void triggerRefreshHistory() {
        if (orderHistoryPanel != null) {
            orderHistoryPanel.refreshData();
        }
    }

    private JPanel createHeaderPanel() {

        JPanel headerContainer = new JPanel(new BorderLayout());
        headerContainer.setBorder(BorderFactory.createEtchedBorder());
        JPanel logoutPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        logoutPanel.setBackground(Color.WHITE);
        btnLogout = new JButton("Thoát");
        btnLogout.setBackground(Color.RED); btnLogout.setForeground(Color.BLACK);
        btnLogout.setOpaque(true); btnLogout.setBorderPainted(false);
        btnLogout.setFocusPainted(false); btnLogout.setFont(new Font("Arial", Font.BOLD, 12));
        logoutPanel.add(btnLogout);
        headerContainer.add(logoutPanel, BorderLayout.NORTH);
        JPanel navPanel = new JPanel(new GridLayout(1, 4));
        btnDatBan = new JButton("Đặt bàn"); btnMenu = new JButton("Menu");
        btnNhanVien = new JButton("Nhân viên"); btnLichSu = new JButton("Lịch sử");
        navButtons = new JButton[]{btnDatBan, btnMenu, btnNhanVien, btnLichSu};
        for (JButton btn : navButtons) {
            btn.setFont(navFontInactive); btn.setFocusPainted(false);
            btn.setCursor(new Cursor(Cursor.HAND_CURSOR)); navPanel.add(btn);
        }
        headerContainer.add(navPanel, BorderLayout.CENTER);
        return headerContainer;
    }

    private JPanel createMainContentPanel() {
        cardLayout = new CardLayout();
        JPanel panel = new JPanel(cardLayout);
        panel.setBackground(Color.WHITE);

        tableSelectionPanel = new TableSelectionPanel(this);
        panel.add(tableSelectionPanel, DAT_BAN_PANEL);

        panel.add(new JPanel(), MENU_PANEL);
        panel.add(new JPanel(), NHAN_VIEN_PANEL);
        panel.add(new JPanel(), LICH_SU_PANEL);

        return panel;
    }

    private void createActionListeners() {
        btnLogout.addActionListener(e -> {
            int choice = JOptionPane.showConfirmDialog(MainApplication.this, "Bạn có chắc muốn đăng xuất?", "Xác nhận", JOptionPane.YES_NO_OPTION);
            if (choice == JOptionPane.YES_OPTION) { dispose(); new LoginScreen().setVisible(true); }
        });

        btnDatBan.addActionListener(e -> {
            setActiveButton(btnDatBan);
            cardLayout.show(mainContentPanel, DAT_BAN_PANEL);
        });

        btnMenu.addActionListener(e -> {
            setActiveButton(btnMenu);
            if (menuPanel == null) {
                menuPanel = new MenuPanel();
                mainContentPanel.add(menuPanel, MENU_PANEL);
            }
            cardLayout.show(mainContentPanel, MENU_PANEL);
        });

        btnNhanVien.addActionListener(e -> {
            setActiveButton(btnNhanVien);

            if (employeePanel == null) {
                employeePanel = new EmployeePanel();
                mainContentPanel.add(employeePanel, NHAN_VIEN_PANEL);
            }
            cardLayout.show(mainContentPanel, NHAN_VIEN_PANEL);
        });

        btnLichSu.addActionListener(e -> {
            setActiveButton(btnLichSu);

            if (orderHistoryPanel == null) {
                orderHistoryPanel = new OrderHistoryPanel();
                mainContentPanel.add(orderHistoryPanel, LICH_SU_PANEL);
            } else {
                orderHistoryPanel.refreshData();
            }
            cardLayout.show(mainContentPanel, LICH_SU_PANEL);
        });
    }

    private void setActiveButton(JButton activeButton) {
        Color activeColor = Color.decode("#007bff"); Color inactiveColor = Color.BLACK;
        for (JButton btn : navButtons) {
            if (btn == activeButton) { btn.setForeground(activeColor); btn.setFont(navFontActive); }
            else { btn.setForeground(inactiveColor); btn.setFont(navFontInactive); }
        }
    }

    public static void main(String[] args) {
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } catch (Exception e) { e.printStackTrace(); }
        SwingUtilities.invokeLater(() -> new LoginScreen().setVisible(true));
    }
}