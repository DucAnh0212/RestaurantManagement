package com.nhom.restaurant.models;

import com.nhom.restaurant.utils.DatabaseConnector;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class OrderItems {
    private int id;
    private int orderid;
    private String menu_item_name;
    private int quantity;
    private int price;

    public OrderItems(int id, int orderid, String menu_item_name, int quantity, int price){
        this.id=id;
        this.orderid=orderid;
        this.menu_item_name=menu_item_name;
        this.quantity=quantity;
        this.price=price;
    }

    public int getId() { return id;}
    public void setId(int id) {this.id = id;}

    public int getOrderId() { return orderid;}
    public void setOrderid(int orderid) {this.orderid = orderid;}

    public String getMenuItemName() { return menu_item_name;}
    public void setMenu_item_name(String menu_item_name) {this.menu_item_name = menu_item_name;}

    public int getQuantity() { return quantity;}
    public void setQuantity(int quantity) {this.quantity = quantity;}

    public int getPrice() { return price;}
    public void setPrice(int price) {this.price = price;}

    public void save() throws SQLException {
        String sql = "INSERT INTO OrderItems(ORDER_ID, MENU_ITEM_NAME, QUANTITY, PRICE) VALUES (?, ?, ?, ?)";

        try (Connection connection = DatabaseConnector.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(sql)){

            preparedStatement.setInt(1, this.orderid);
            preparedStatement.setString(2, this.menu_item_name);
            preparedStatement.setInt(3, this.quantity);
            preparedStatement.setInt(4, this.price);

            preparedStatement.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void update() throws SQLException {
        String sql = "UPDATE OrderItems SET QUANTITY = ? WHERE ID = ?";

        try(Connection connection = DatabaseConnector.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(sql)){

            preparedStatement.setInt(1, this.quantity);
            preparedStatement.setInt(2, this.id);

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}


