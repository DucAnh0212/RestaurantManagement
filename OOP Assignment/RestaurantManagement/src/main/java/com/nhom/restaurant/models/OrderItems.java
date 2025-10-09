package com.nhom.restaurant.models;

public class OrderItems {
    private int id;
    private int order_id;
    private String menu_item_name;
    private int quantity;
    private int price;

    public OrderItems(int id, int order_id, String menu_item_name, int quantity, int price){
        this.id = id;
        this.order_id = order_id;
        this.menu_item_name = menu_item_name;
        this.quantity = quantity;
        this.price=price;
    }

    public void save(){}

    public void update(){}
}
