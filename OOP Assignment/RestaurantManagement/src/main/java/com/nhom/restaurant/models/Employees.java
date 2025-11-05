package com.nhom.restaurant.models;

import com.nhom.restaurant.utils.DatabaseConnector;

import java.sql.*;
import java.util.*;

public class Employees {
    private int id;
    private String full_name;
    private String workshift;
    private String phone_number;
    private String email;
    private String address;

    public Employees(){
    }

    public Employees(int id, String full_name, String workshift, String phone, String email, String address){
        this.id = id;
        this.full_name = full_name;
        this.workshift = workshift;
        this.phone_number = phone;
        this.email = email;
        this.address = address;
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

    public String getPhone_number() {
        return phone_number;
    }
    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public String getAddress() {
        return address;
    }
    public void setAddress(String address) {
        this.address = address;
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

                foundemployee.setPhone_number(rs.getString("PHONE_NUMBER"));
                foundemployee.setEmail(rs.getString("EMAIL"));
                foundemployee.setAddress(rs.getString("ADDRESS"));

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
                employee.setPhone_number(rs.getString("PHONE_NUMBER"));
                employee.setEmail(rs.getString("EMAIL"));
                employee.setAddress(rs.getString("ADDRESS"));

                allEmployee.add(employee);
            }
        }
        catch(SQLException e){
            e.printStackTrace();
        }
        return allEmployee;
    }
}
