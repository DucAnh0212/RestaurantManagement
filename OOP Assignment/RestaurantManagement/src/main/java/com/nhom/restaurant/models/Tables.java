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
    public void setId(int a){
        this.id = a;
    }
    public void setFloor(int a){
        this.floor = a;
    }
    public void setStatus(String a){
        this.status = a;
    }
    public void markAsOccupied(){
        this.status = "Occupied";
    }
    public void markAsAvailable(){
        this.status = "Available";
    }
    public static Tables findById(int tableId) {
        String sql = "SELECT * FROM Tables WHERE ID = ?";
        Tables foundTable = null;
        try(Connection connection = DatabaseConnector.getConnection();
            PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, tableId);
            ResultSet rs = pstmt.executeQuery();
            if(rs.next()){
                foundTable = new Tables();
                foundTable.setId(rs.getInt("ID"));
                foundTable.setFloor(rs.getInt("FLOOR"));
                foundTable.setStatus(rs.getString("STATUS"));
            }
        }
        catch(SQLException e){
            e.printStackTrace();
        }
        return foundTable;
    }
    public static List<Tables> findAll(){
        String sql = "SELECT * FROM Tables";
        List<Tables> allTable = new ArrayList<>();
        try(Connection connection = DatabaseConnector.getConnection();
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(sql)){
            while (rs.next()) {
                Tables A = new Tables();
                A.setId(rs.getInt("ID"));
                A.setFloor(rs.getInt("FLOOR"));
                A.setStatus(rs.getString("STATUS"));
                allTable.add(A);
            }
        }
        catch (SQLException e){
            e.printStackTrace();
        }
        return allTable;
    }
}
