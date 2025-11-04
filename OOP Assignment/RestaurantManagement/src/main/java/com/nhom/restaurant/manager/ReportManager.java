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
        String sql = "SELECT O.*, E.FULLNAME FROM Orders O " +
                     "JOIN Employees E ON O.EMPLOYEE_ID = E.ID " +
                     "WHERE DATE(O.CREATED_AT) = ?";
        try(Connection connection = DatabaseConnector.getConnection();
            PreparedStatement pstmt = connection.prepareStatement(sql)){
            pstmt.setDate(1, Date.valueOf(date));
            ResultSet rs = pstmt.executeQuery();
            while(rs.next()){
                Orders selectedOrder = new Orders();
                selectedOrder.setId(rs.getInt("ID"));
                selectedOrder.setTable_id(rs.getInt("TABLE_ID"));
                selectedOrder.setEmployee_id(rs.getInt("EMPLOYEE_ID"));
                selectedOrder.setEmployee_name(rs.getString("FULLNAME"));
                selectedOrder.setStatus(rs.getString("STATUS"));
                selectedOrder.setTotal_amount(rs.getInt("TOTAL_AMOUNT"));
                selectedOrder.setCreated_at(rs.getTimestamp("CREATED_AT"));
                if (selectedOrder.getStatus().equals("Đã thanh toán")) {
                    totalRevenue += selectedOrder.getTotal_amount();
                }
                historyList.add(selectedOrder);
            }
        }
        catch(SQLException e){
            e.printStackTrace();
        }
        return new DailyReport(historyList, totalRevenue);
    }
}
