package com.nhom.restaurant.models;

import com.nhom.restaurant.utils.DatabaseConnector;

import java.sql.*;
import java.util.*;
public class Employees {
    private int id;
    private String full_name;
    private String workshift;

    public Employees(){
    }
    public Employees(int id, String full_name, String workshift){
        this.id = id;
        this.full_name = full_name;
        this.workshift = workshift;
    }
    public int getId(){
        return this.id;
    }
    public String getFull_name(){
        return this.full_name;
    }
    public String getWorkshift(){
        return this.workshift;
    }
    public void setId(int a){
        if(a>0) this.id = a;
    }
    public void setFull_name(String a){
        this.full_name = a;
    }
    public void setWorkshift(String a){
        this.workshift = a;
    }
}

