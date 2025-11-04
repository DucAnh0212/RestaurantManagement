package com.nhom.restaurant.manager;

import com.nhom.restaurant.models.*;
import java.sql.SQLException;

public class OrderManager {
    public Orders startNewOrder(int tableId, int employeeId) throws SQLException{
        Tables table = Tables.findById(tableId);
        if (table != null && table.getStatus().equals("Trống")){
            table.markAsOccupied();
            Orders newOrder = new Orders();
            newOrder.setTable_id(tableId);
            newOrder.setEmployee_id(employeeId);
            newOrder.save();
            return newOrder;
        }
        else {
            return null;
        }
    }
    public void checkOut(int orderId) throws SQLException{
        Orders orderToPay = Orders.findById(orderId);
        if(orderToPay != null){
            orderToPay.updateStatus("Đã thanh toán");
            Tables table = Tables.findById(orderToPay.getTable_id());
            if(table != null){
                table.markAsAvailable();
            }
        }
    }
    public void cancelOrder (int orderId) throws SQLException{
        Orders orderToCancel = Orders.findById(orderId);
        if(orderToCancel != null){
            orderToCancel.updateStatus("Đã huỷ");
            Tables table = Tables.findById(orderToCancel.getTable_id());
            if(table != null){
                table.markAsAvailable();
            }
        }
    }
}
