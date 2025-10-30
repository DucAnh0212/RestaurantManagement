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
    private String name, category;
    private int price;

    public MenuItems(){
    }
    public MenuItems(int id, String name, String category, int price){
        this.id = id;
        this.name = name;
        this.price = price;
        this.category = category;
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
    public String getCategory(){
        return this.category;
    }
    public void setId(int a){
        this.id = a;
    }
    public void setName(String a){
        this.name = a;
    }
    public void setCategory(String a){ this.category = a; }
    public void setPrice(int a){
        this.price = a;
    }
    public static MenuItems findById(int id){
        String sql = "SELECT * FROM MenuItems WHERE ID = ?";
        MenuItems foundItem = null;
        try(Connection connection = DatabaseConnector.getConnection();
            PreparedStatement ptsmt = connection.prepareStatement(sql)){
            ptsmt.setInt(1, id);
            ResultSet rs = ptsmt.executeQuery();
            if(rs.next()){
                foundItem = new MenuItems();
                foundItem.setId(rs.getInt("ID"));
                foundItem.setName(rs.getString("NAME"));
                foundItem.setCategory(rs.getString("CATEGORY"));
                foundItem.setPrice(rs.getInt("PRICE"));
            }
        }
        catch(SQLException e){
            e.printStackTrace();
        }
        return foundItem;
    }
    public static List<MenuItems> findAll(){
        String sql = "SELECT * FROM MenuItems";
        List<MenuItems> allItem = new ArrayList<>();
        try(Connection connection = DatabaseConnector.getConnection();
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(sql)){
            while(rs.next()){
                MenuItems menuItem = new MenuItems();
                menuItem.setId(rs.getInt("ID"));
                menuItem.setName(rs.getString("NAME"));
                menuItem.setCategory(rs.getString("CATEGORY"));
                menuItem.setPrice(rs.getInt("PRICE"));
                allItem.add(menuItem);
            }

        }
        catch(SQLException e){
            e.printStackTrace();
        }
        return allItem;
    }

    public static List<MenuItems> findAllByCategory(String s){
        String sql = "SELECT * FROM MenuItems WHERE CATEGORY = ?";
        List<MenuItems> allItems = new ArrayList<>();
        try(Connection connection = DatabaseConnector.getConnection();
            PreparedStatement pstmt = connection.prepareStatement(sql)){
            pstmt.setString(1, s);
            ResultSet rs = pstmt.executeQuery();
            while(rs.next()){
                MenuItems menuItem = new MenuItems();
                menuItem.setId(rs.getInt("ID"));
                menuItem.setName(rs.getString("NAME"));
                menuItem.setCategory(rs.getString("CATEGORY"));
                menuItem.setPrice(rs.getInt("PRICE"));
                allItems.add(menuItem);
            }

        }
        catch(SQLException e){
            e.printStackTrace();
        }
        return allItems;
    }
}
