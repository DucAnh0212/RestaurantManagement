package com.nhom.restaurant.models;

import java.util.*;
public class Orders {
    private int id;
    private int table_id;
    private int total_amount;
    private String status;
    private String created_at;
    private List<OrderItems> list;

    public Orders(int id, int table_id, int total_amount, String status){
        this.id = id;
        this.table_id = table_id;
        this.total_amount = total_amount;
        this.status = status;
    }

    public static Orders findActiveByTableId(int table_id){

    }

    public void calculateTotal(){

    }

    public void save(){}

    public void markAsPaid(){}

    public void markAsCancel(){}

    public void collectOrderItem(){}

    public void setTime(){}
}
