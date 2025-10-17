package com.nhom.restaurant.models;

import com.nhom.restaurant.utils.DatabaseConnector;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
public class Tables {
    private int id;
    private int floor;
    private String status;

    public Tables(){
    }
    public Tables(int id, int floor, String status){
        this.id = id;
        this.floor = floor;
        this.status = status;
    }
    public int getId(){
        return this.id;
    }
    public int getFloor(){
        return this.floor;
    }
    public String getStatus(){
        return this.status;
    }
    public void setId(int a){
        if(id>0) this.id = a;
    }
    public void setFloor(int a){
        if(0<a && a<4) this.floor = a;
    }
    public void setStatus(String a){
        this.status = a;
    }
}

