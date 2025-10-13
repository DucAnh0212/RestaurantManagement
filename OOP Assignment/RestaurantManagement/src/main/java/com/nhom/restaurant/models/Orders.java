package models;
import com.nhom.restaurant.utils.DatabaseConnector;

import java.sql.*;
import java.util.*;

public class Orders {
    private int id;
    private int table_id;
    private double total_amount;
    private String status;
    private String created_at;
    private List<OrderItems> list;

    public Orders(int id, int tableId, int totalAmount, String status, String createdAt){
        this.id=id;
        this.table_id=table_id;
        this.total_amount=total_amount;
        this.status=status;
        this.created_at = createdAt;
        this.list = new ArrayList<>();
    }

    public int getId() {
        return this.id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public int getTableId() {
        return this.table_id;
    }

    public void setTableId(int tableId) {
        this.table_id = tableId;
    }

    public String getStatus() {
        return this.status;
    }
    public void setStatus(String status) {
        this.status = status;
    }

    public double getTotalAmount() {
        return this.total_amount;
    }
    public void setTotalAmount(double totalAmount) {
        this.total_amount = totalAmount;
    }

    public String getCreatedAt() {
        return this.created_at;
    }
    public void setCreatedAt(String createdAt) {
        this.created_at = createdAt;
    }

    public List<OrderItems> getItems() {
        return this.list;
    }
    public void setItems(List<OrderItems> items) {
        this.list = items;
    }

    public static Orders findActiveByTableId(int table_id) throws SQLException {
        String sql = "SELECT * FROM Orders Where ID = ? AND STATUS = 'Đang hoạt động'";
        Orders foundTable = null;
        try(Connection connection = DatabaseConnector.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(sql)){
            preparedStatement.setInt(1, id);
            ResultSet rs = preparedStatement.executeQuery();
            if(rs.next()) {
                foundTable = new Orders();
                foundTable.setId(rs.getInt("ID"));
                foundTable.setTableId(rs.getInt("TABLE_ID"));
                foundTable.setTotalAmount(rs.getInt("TOTAL_AMOUNT"));
                foundTable.setStatus(rs.getInt("STATUS"));
                foundTable.setCreatedAt(rs.getInt("CREATED_AT"));
            }
        } catch (SQLException e){
            e.printStackTrace();
        }
        return foundTable;
    }

    public static void List<OrderItems>

    public void calculateTotal(){
        int currentTotal = 0;
        for (OrderItems item : this.list) {
            currentTotal += list.get();
        }
        this.total_amount = currentTotal;
    }

    public void save(){}

    public void markAsPaid(){}

    public void markAsCancel(){}

    public void collectOrderItem(){}

    public void setTime(){}
}
