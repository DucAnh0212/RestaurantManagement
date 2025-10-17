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
        if(id>0) this.id = id;
    }
    public int getOrderId() {
        return orderid;
    }
    public void setOrderId(int a) {
        if(id>0) this.orderid = a;
    }
    public int getMenu_item_id() {
        return menu_item_id;
    }
    public void setMenu_item_id(int menu_item_id) {
        if(menu_item_id>0) this.menu_item_id = menu_item_id;
    }
    public int getQuantity() {
        return quantity;
    }
    public void setQuantity(int quantity) {
        if(quantity >=0) this.quantity = quantity;
    }
    public int getPrice() {
        return price;
    }
    public void setPrice(int price) {
        if(price>0) this.price = price;
    } 
}


