package com.nhom.restaurant.gui;

import com.nhom.restaurant.models.Orders;
import javax.swing.*;
import java.awt.*;

public class MainApplication extends JFrame {
    private CardLayout cardLayout;
    private JPanel mainPanel;
    private TableSelectionScreen tableSelectionScreen;
    private OrderScreen orderScreen;

    public static final String TABLE_SCREEN = "Table Screen";
    public static final String ORDER_SCREEN = "Order Screen";

    public MainApplication(){
        setTitle("Hệ Thống Quản Lý Nhà Hàng");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        tableSelectionScreen = new TableSelectionScreen(this);
        mainPanel.add(tableSelectionScreen, TABLE_SCREEN);
        this.add(mainPanel);
        cardLayout.show(mainPanel, TABLE_SCREEN);
    }
    public void switchToOrderScreen(Orders order){
        if(orderScreen == null){
            orderScreen = new OrderScreen(this);
            mainPanel.add(orderScreen, ORDER_SCREEN);
        }
        orderScreen.loadData(order);
        cardLayout.show(mainPanel, ORDER_SCREEN);
    }
    public void switchToTableScreen(){
        tableSelectionScreen.loadData();
        cardLayout.show(mainPanel, TABLE_SCREEN);
    }
    public static void main(String[] args){
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new MainApplication().setVisible(true);
            }
        });
    }
}
