package com.nhom.restaurant.models;

import com.nhom.restaurant.utils.DatabaseConnector;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class OrderItems {
    private int id;
    private int orderid;
    private int menu_item_id;
    private int quantity;
    private int price;
    private String menu_item_name;

    public OrderItems(){
    }
    public OrderItems(int id, int orderid, int menu_item_id, int quantity, int price){
        this.id = id;
        this.orderid = orderid;
        this.menu_item_id = menu_item_id;
        this.quantity = quantity;
        this.price = price;
    }
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public int getOrderId() {
        return orderid;
    }
    public void setOrderId(int a) {
        this.orderid = a;
    }
    public int getMenu_item_id() {
        return menu_item_id;
    }
    public void setMenu_item_id(int menu_item_id) {
        this.menu_item_id = menu_item_id;
    }
    public int getQuantity() {
        return quantity;
    }
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
    public int getPrice() {
        return price;
    }
    public void setPrice(int price) {
        this.price = price;
    }
    public String getMenu_item_name() {
        return this.menu_item_name;
    }

    public void setMenu_item_name(String a) {
        this.menu_item_name = a;
    }

    public void save() throws SQLException {
        String sql = "INSERT INTO OrderItems(ORDER_ID, MENU_ITEM_ID, QUANTITY, PRICE) VALUES (?, ?, ?, ?)";
        try (Connection connection = DatabaseConnector.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)){
            preparedStatement.setInt(1, this.orderid);
            preparedStatement.setInt(2, this.menu_item_id);
            preparedStatement.setInt(3, this.quantity);
            preparedStatement.setInt(4, this.price);
            preparedStatement.executeUpdate();
        }
    }
    public void update() throws SQLException {
        String sql = "UPDATE OrderItems SET QUANTITY = ? WHERE ID = ?";
        try(Connection connection = DatabaseConnector.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(sql)){
            preparedStatement.setInt(1, this.quantity);
            preparedStatement.setInt(2, this.id);
            preparedStatement.executeUpdate();
        }
    }
}

