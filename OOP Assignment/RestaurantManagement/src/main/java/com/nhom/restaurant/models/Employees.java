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
        this.id = a;
    }
    public void setFull_name(String a){
        this.full_name = a;
    }
    public void setWorkshift(String a){
        this.workshift = a;
    }
    public static Employees findById(int id){
        String sql = "SELECT * FROM Employees WHERE ID = ?";
        Employees foundemployee = null;
        try(Connection connection = DatabaseConnector.getConnection();
            PreparedStatement pstmt = connection.prepareStatement(sql)){
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            if(rs.next()){
                foundemployee = new Employees();
                foundemployee.setId(rs.getInt("ID"));
                foundemployee.setFull_name(rs.getString("FULLNAME"));
                foundemployee.setWorkshift(rs.getString("WORKSHIFT"));
            }
        }
        catch(SQLException e){
            e.printStackTrace();
        }
        return foundemployee;
    }

    public static List<Employees> findAll(){
        String sql = "SELECT * FROM Employees";
        List<Employees> allEmployee = new ArrayList<>();
        try(Connection connection = DatabaseConnector.getConnection();
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(sql)){
            while(rs.next()){
                Employees employee = new Employees();
                employee.setId(rs.getInt("ID"));
                employee.setFull_name(rs.getString("FULLNAME"));
                employee.setWorkshift(rs.getString("WORKSHIFT"));
                allEmployee.add(employee);
            }
        }
        catch(SQLException e){
            e.printStackTrace();
        }
        return allEmployee;
    }
}
