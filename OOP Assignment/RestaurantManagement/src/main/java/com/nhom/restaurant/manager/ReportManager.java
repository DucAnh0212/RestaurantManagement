package com.nhom.restaurant.manager;

import com.nhom.restaurant.models.DailyReport;
import com.nhom.restaurant.models.Orders;
import com.nhom.restaurant.utils.DatabaseConnector;
import java.sql.*;
import java.sql.Date;
import java.time.LocalDate;
import java.util.*;

public class ReportManager {
    public static DailyReport genDailyReport(LocalDate date){
        List<Orders> historyList = new ArrayList<>();
        int totalRevenue = 0;
        String sql = "SELECT * FROM Orders WHERE DATE(CREATED_AT) = ?";
        try(Connection connection = DatabaseConnector.getConnection();
            PreparedStatement pstmt = connection.prepareStatement(sql)){
            pstmt.setDate(1, Date.valueOf(date));
            ResultSet rs = pstmt.executeQuery();
            while(rs.next()){
                Orders selectedOrder = new Orders();
                selectedOrder.setId(rs.getInt("ID"));
                selectedOrder.setTable_id(rs.getInt("TABLE_ID"));
                selectedOrder.setStatus(rs.getString("STATUS"));
                selectedOrder.setTotal_amount(rs.getInt("TOTAL_AMOUNT"));
                selectedOrder.setCreated_at(rs.getTimestamp("CREATED_AT"));
                historyList.add(selectedOrder);
                totalRevenue += selectedOrder.getTotal_amount();
            }
        }
        catch(SQLException e){
            e.printStackTrace();
        }
        return new DailyReport(historyList, totalRevenue);
    }
}
