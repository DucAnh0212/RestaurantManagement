package com.nhom.restaurant.models;

import com.nhom.restaurant.utils.DatabaseConnector;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.util.*;
public class Orders {
    private int id;
    private int table_id;
    private int total_amount;
    private String status;
    private Timestamp created_at;
    private List<OrderItems> items = new ArrayList<>();

    public Orders(){
    }
    public int getID(){
        return this.id;
    }
    public int getTable_id(){
        return this.table_id;
    }
    public int getTotal_amount(){
        return this.total_amount;
    }
    public String getStatus(){
        return this.status;
    }
    public Timestamp getTime(){
        return created_at;
    }
    public void setId(int a){
        if(id>0) this.id = a;
    }
    public void setStatus(String a){
        this.status = a;
    }
    public void setTable_id(int a){
        if(a>0) this.table_id = a;
    }
    public void setTotal_amount(int a){
        if(a>=0) this.total_amount = a;
    }
    public void setCreated_at(Timestamp a){
        this.created_at = a;
    }
    public void setItems(List<OrderItems> a){
        this.items = a;
    }   
}

