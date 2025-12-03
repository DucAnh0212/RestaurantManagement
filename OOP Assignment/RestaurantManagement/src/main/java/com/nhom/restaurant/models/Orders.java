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
    private int employee_id;
    private String employee_name;
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
    public List<OrderItems> getItems() {
        return this.items;
    }
    public int getEmployee_id(){
        return this.employee_id;
    }
    public void setEmployee_id(int a){
        this.employee_id = a;
    }
    public String getEmployee_name(){
        return this.employee_name;
    }
    public void setEmployee_name(String a){
        this.employee_name = a;
    }
    public String getStatus(){
        return this.status;
    }
    public Timestamp getTime(){
        return created_at;
    }
    public void setId(int a){
        this.id = a;
    }
    public void setStatus(String a){
        this.status = a;
    }
    public void setTable_id(int a){
        this.table_id = a;
    }
    public void setTotal_amount(int a){
        this.total_amount = a;
    }
    public void setCreated_at(Timestamp a){
        this.created_at = a;
    }
    public void setItems(List<OrderItems> a){
        this.items = a;
    }
    private OrderItems findItemInCart(int menuItemId){
        for (OrderItems item : this.items){
            if(item.getMenu_item_id() == menuItemId){
                return item;
            }
        }
        return null;
    }

    private OrderItems findItemCartbyOrderItemId(int orderItemId){
        for(OrderItems item : this.items){
            if(item.getId() == orderItemId){
                return item;
            }
        }
        return null;
    }
    public void addItem(MenuItems menuItem, int quantity) throws SQLException{
        OrderItems existingItem = findItemInCart(menuItem.getId());
        if (existingItem != null) {
            existingItem.setQuantity(existingItem.getQuantity() + quantity);
            existingItem.update();
        }
        else {
            OrderItems newItem = new OrderItems();
            newItem.setOrderId(this.id);
            newItem.setMenu_item_id(menuItem.getId());
            newItem.setQuantity(quantity);
            newItem.setPrice(menuItem.getPrice());
            newItem.setMenu_item_name(menuItem.getName());
            newItem.save();
            this.items.add(newItem);
            this.fetchOrderItems();
        }
        this.updateTotalAmount();
    }
    public void deleteItem(int orderItemId) throws SQLException{
        OrderItems itemToDelete = findItemCartbyOrderItemId(orderItemId);
        if(itemToDelete != null){
            itemToDelete.delete();
            this.items.remove(itemToDelete);
            this.updateTotalAmount();
        }
    }
    public void reduceItemQuantity(int orderItemId) throws  SQLException {
        OrderItems itemToReduce = findItemCartbyOrderItemId(orderItemId);
        if(itemToReduce != null){
            int currentQuantity = itemToReduce.getQuantity();
            if(currentQuantity > 1){
                itemToReduce.setQuantity((currentQuantity - 1));
                itemToReduce.update();
            }
            else{
                deleteItem(orderItemId);
            }
            this.updateTotalAmount();
        }
    }

    public int calculateTotal(){
        int total = 0;
        for(OrderItems b : this.items){
            total += b.getPrice() * b.getQuantity();
        }
        this.total_amount = total;
        return this.total_amount;
    }
    public void save() throws  SQLException {
        String sql = "INSERT INTO Orders (TABLE_ID, EMPLOYEE_ID, STATUS, CREATED_AT) VALUES (?, ?, ?, ?)";
        try(Connection connection = DatabaseConnector.getConnection();
            PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)){
            pstmt.setInt(1, this.table_id);
            pstmt.setInt(2, this.employee_id);
            pstmt.setString(3, "Đang hoạt động");
            pstmt.setTimestamp(4, new Timestamp(System.currentTimeMillis()));
            pstmt.executeUpdate();

            try(ResultSet gereratedKeys = pstmt.getGeneratedKeys()){
                if(gereratedKeys.next()){
                    this.id = gereratedKeys.getInt(1);
                }
            }
        }
    }
    public void updateStatus(String newStatus) throws  SQLException {
        String sql = "UPDATE Orders SET STATUS = ?, TOTAL_AMOUNT = ? WHERE ID = ?";
        try (Connection connection = DatabaseConnector.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {
            this.status = newStatus;
            if(newStatus.equals("Đã thanh toán")){
                this.calculateTotal();
            }
            if(newStatus.equals("Đang hoạt động")){
                this.total_amount = 0;
            }
            if(newStatus.equals("Đã huỷ")){
                this.total_amount = 0;
            }
            pstmt.setString(1, this.status);
            pstmt.setInt(2, this.total_amount);
            pstmt.setInt(3, this.id);
            pstmt.executeUpdate();
        }
    }
    public void fetchOrderItems() throws  SQLException {
        this.items.clear();
        String sql = "SELECT OI.*, MI.NAME FROM OrderItems OI " +
                "JOIN MenuItems MI ON OI.MENU_ITEM_ID = MI.ID " +
                "WHERE OI.ORDER_ID = ?";
        try (Connection connection = DatabaseConnector.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)){
            pstmt.setInt(1, this.id);
            ResultSet rs = pstmt.executeQuery();
            while(rs.next()){
                OrderItems item = new OrderItems();
                item.setId((rs.getInt("ID")));
                item.setOrderId(rs.getInt("ORDER_ID"));
                item.setMenu_item_id(rs.getInt("MENU_ITEM_ID"));
                item.setMenu_item_name(rs.getString("NAME"));
                item.setQuantity(rs.getInt("QUANTITY"));
                item.setPrice(rs.getInt("PRICE"));
                this.items.add(item);
            }
        }
    }
    public static Orders findById(int idToFind) throws  SQLException{
        String sql = "SELECT O.*, E.FULLNAME FROM Orders O " +
                     "JOIN Employees E ON O.EMPLOYEE_ID = E.ID " +
                     "WHERE O.ID = ?";
        Orders OrderToFind = null;
        try(Connection connection = DatabaseConnector.getConnection();
            PreparedStatement pstmt = connection.prepareStatement(sql)){
            pstmt.setInt(1, idToFind);
            ResultSet rs = pstmt.executeQuery();
            if(rs.next()) {
                OrderToFind = new Orders();
                OrderToFind.setId(rs.getInt("ID"));
                OrderToFind.setTable_id(rs.getInt("TABLE_ID"));
                OrderToFind.setEmployee_id(rs.getInt("EMPLOYEE_ID"));
                OrderToFind.setEmployee_name(rs.getString("FULLNAME"));
                OrderToFind.setStatus(rs.getString("STATUS"));
                OrderToFind.setTotal_amount(rs.getInt("TOTAL_AMOUNT"));
                OrderToFind.setCreated_at(rs.getTimestamp("CREATED_AT"));
                OrderToFind.fetchOrderItems();
            }
        }
        return OrderToFind;
    }
    public static Orders findActiveByTableId(int table_id) throws  SQLException{
        String sql = "SELECT O.*, E.FULLNAME FROM Orders O " +
                     "JOIN Employees E ON O.EMPLOYEE_ID = E.ID " +
                     "WHERE O.TABLE_ID = ? AND O.STATUS = 'Đang hoạt động' LIMIT 1";
        Orders activeOrder = null;
        try (Connection connection = DatabaseConnector.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)){
            pstmt.setInt(1, table_id);
            ResultSet rs = pstmt.executeQuery();
            if(rs.next()){
                activeOrder = new Orders();
                activeOrder.setId(rs.getInt("ID"));
                activeOrder.setTable_id(rs.getInt("TABLE_ID"));
                activeOrder.setStatus(rs.getString("STATUS"));
                activeOrder.setTotal_amount(rs.getInt("TOTAL_AMOUNT"));
                activeOrder.setCreated_at(rs.getTimestamp("CREATED_AT"));
                activeOrder.fetchOrderItems();
            }
        }
        return activeOrder;
    }
    public void updateTotalAmount() throws SQLException {
        int currentTotal = this.calculateTotal();
        String sql = "UPDATE Orders SET total_amount = ? WHERE id = ?";
        try (Connection connection = DatabaseConnector.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, currentTotal);
            pstmt.setInt(2, this.id);
            pstmt.executeUpdate();
        }
    }
}

