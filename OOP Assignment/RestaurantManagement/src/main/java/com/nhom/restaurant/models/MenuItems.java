package com.nhom.restaurant.models;

import com.nhom.restaurant.utils.DatabaseConnector;
import java.sql.ResultSet;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;
public class MenuItems {
    private int id;
    private String name;
    private int price;

    public MenuItems(){
    }
    public MenuItems(int id, String name, int price){
        this.id = id;
        this.name = name;
        this.price = price;
    }
    public int getId(){
        return this.id;
    }
    public int getPrice(){
        return this.price;
    }
    public String getName(){
        return this.name;
    }
    public void setId(int a){
        if(a>0) this.id = a;
    }
    public void setName(String a){
        this.name = a;
    }
    public void setPrice(int a){
        if(a>0) this.price = a;
    }
}

