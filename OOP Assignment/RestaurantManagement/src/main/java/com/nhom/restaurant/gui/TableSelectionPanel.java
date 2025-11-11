package com.nhom.restaurant.gui;

import com.nhom.restaurant.manager.OrderManager;
import com.nhom.restaurant.models.Employees;
import com.nhom.restaurant.models.Orders;
import com.nhom.restaurant.models.Tables;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

public class TableSelectionPanel extends JPanel {

    private MainApplication mainApp;
    private OrderManager orderManager;

    private CardLayout viewSwitcherLayout;
    private MenuSelectionPanel menuSelectionView; // Card 2
    private JPanel tableSelectionView;            // Card 1

    private JPanel floorButtonsPanel;
    private JPanel tableGridContainer;
    private CardLayout tableGridCardLayout;

    private final Color COLOR_TRONG = new Color(0, 153, 51);
    private final Color COLOR_CO_KHACH = new Color(204, 0, 0);

    private static final String VIEW_TABLES = "VIEW_TABLES";
    private static final String VIEW_MENU = "VIEW_MENU";

    public MainApplication getMainApp() {
        return mainApp;
    }

    public TableSelectionPanel(MainApplication mainApp) {
        this.mainApp = mainApp;
        this.orderManager = new OrderManager();

        viewSwitcherLayout = new CardLayout();
        setLayout(viewSwitcherLayout);

        // --- CARD 1: CHỌN BÀN ---
        tableSelectionView = new JPanel(new BorderLayout(10, 10));
        tableSelectionView.setBackground(Color.WHITE);
        tableSelectionView.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        floorButtonsPanel = new JPanel(new GridLayout(1, 3, 10, 0));

        tableGridCardLayout = new CardLayout();
        tableGridContainer = new JPanel(tableGridCardLayout);

        tableSelectionView.add(floorButtonsPanel, BorderLayout.NORTH);
        tableSelectionView.add(tableGridContainer, BorderLayout.CENTER);

        // --- CARD 2: CHỌN MÓN ---
        menuSelectionView = new MenuSelectionPanel(this, orderManager, mainApp.getLoggedInEmployee());

        add(tableSelectionView, VIEW_TABLES);
        add(menuSelectionView, VIEW_MENU);

        loadTableData();

        viewSwitcherLayout.show(this, VIEW_TABLES);
    }

    public void loadTableData() {
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

        new SwingWorker<List<Tables>, Void>() {
            @Override
            protected List<Tables> doInBackground() throws Exception {
                return Tables.findAll();
            }

            @Override
            protected void done() {
                try {
                    List<Tables> allTables = get();
                    updateTableUI(allTables);
                } catch (Exception e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(TableSelectionPanel.this,
                            "Lỗi tải dữ liệu bàn: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
                } finally {
                    setCursor(Cursor.getDefaultCursor());
                }
            }
        }.execute();
    }

    private void updateTableUI(List<Tables> allTables) {
        floorButtonsPanel.removeAll();
        tableGridContainer.removeAll();

        Map<Integer, List<Tables>> tablesByFloor = allTables.stream()
                .collect(Collectors.groupingBy(Tables::getFloor));
        List<Integer> sortedFloors = new ArrayList<>(tablesByFloor.keySet());
        sortedFloors.sort(Integer::compareTo);

        for (int floorNum : sortedFloors) {
            String floorCardName = "FLOOR_" + floorNum;

            JButton floorButton = new JButton("Tầng " + floorNum);
            floorButton.setFont(new Font("Arial", Font.BOLD, 16));
            floorButton.setFocusPainted(false);
            floorButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

            floorButton.addActionListener(e -> {
                tableGridCardLayout.show(tableGridContainer, floorCardName);
            });
            floorButtonsPanel.add(floorButton);

            JPanel singleFloorGrid = new JPanel(new GridLayout(3, 4, 15, 15));
            singleFloorGrid.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
            singleFloorGrid.setBackground(Color.decode("#f0f4f8"));

            List<Tables> tablesOnThisFloor = tablesByFloor.get(floorNum);
            for (Tables table : tablesOnThisFloor) {
                JButton tableButton = createTableButton(table);
                singleFloorGrid.add(tableButton);
            }
            tableGridContainer.add(singleFloorGrid, floorCardName);
        }

        if (!sortedFloors.isEmpty()) {
            tableGridCardLayout.show(tableGridContainer, "FLOOR_" + sortedFloors.get(0));
        }

        tableSelectionView.revalidate();
        tableSelectionView.repaint();
    }

    private JButton createTableButton(Tables table) {
        String statusText = table.getStatus();
        JButton button = new JButton(String.format("<html><center>Bàn %d<br>(%s)</center></html>", table.getId(), statusText));
        button.setFont(new Font("Arial", Font.BOLD, 18));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        if (statusText.equals("Trống")) {
            button.setBackground(COLOR_TRONG);
            button.setForeground(Color.WHITE);
        } else {
            button.setBackground(COLOR_CO_KHACH);
            button.setForeground(Color.WHITE);
        }
        button.setOpaque(true);
        button.setBorderPainted(false);

        button.addActionListener(e -> handleTableClick(table));
        return button;
    }

    private void handleTableClick(Tables table) {
        mainApp.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

        new SwingWorker<Orders, Void>() {
            boolean isNewOrderCreated = false;
            String errorMessage = null;

            @Override
            protected Orders doInBackground() throws Exception {
                Employees currentUser = mainApp.getLoggedInEmployee();
                Orders orderResult = null;

                if (table.getStatus().equals("Trống")) {
                    orderResult = orderManager.startNewOrder(table.getId(), currentUser.getId());
                    if (orderResult != null) {
                        isNewOrderCreated = true;
                    } else {
                        errorMessage = "Không thể tạo order mới (Lỗi logic server).";
                    }
                } else {
                    orderResult = Orders.findActiveByTableId(table.getId());
                    if (orderResult == null) {
                        errorMessage = "Lỗi dữ liệu: Bàn có khách nhưng không tìm thấy Order.";
                    }
                }
                return orderResult;
            }

            @Override
            protected void done() {
                mainApp.setCursor(Cursor.getDefaultCursor());

                try {
                    Orders order = get();

                    if (errorMessage != null) {
                        JOptionPane.showMessageDialog(mainApp, errorMessage, "Lỗi", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    if (order != null) {
                        if (isNewOrderCreated) {
                            mainApp.triggerRefreshHistory();
                        }
                        menuSelectionView.loadOrder(order);
                        viewSwitcherLayout.show(TableSelectionPanel.this, VIEW_MENU);
                    }

                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(mainApp, "Lỗi hệ thống: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            }
        }.execute();
    }

    public void showTableGrid(boolean refreshTables) {
        if (refreshTables) {
            loadTableData();
        }
        viewSwitcherLayout.show(this, VIEW_TABLES);
    }
}