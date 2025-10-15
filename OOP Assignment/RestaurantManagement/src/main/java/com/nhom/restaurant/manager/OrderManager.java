package com.nhom.restaurant.manager;

import com.nhom.restaurant.models.*;
import java.sql.SQLException;

public class OrderManager {
    public Orders startNewOrder(int idToFind){
        try{
            Tables table = Tables.findById(idToFind);
            if (table != null && table.getStatus().equals("Trống")){
                table.markAsOccupied();
                Orders newOrder = new Orders();
                newOrder.setTable_id(idToFind);
                newOrder.save();
                return newOrder;
            }
            else {
                return null;
            }
        }
        catch(SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
    public void checkOut(int orderId){
        try {
            Orders orderToPay = Orders.findById(orderId);
            if(orderToPay != null){
                orderToPay.updateStatus("Đã thanh toán");
                Tables table = Tables.findById(orderToPay.getTable_id());
                if(table != null){
                    table.markAsAvailable();
                }
            }
        }
        catch (SQLException e){
            e.printStackTrace();
        }
    }
    public void cancelOrder (int orderId){
        try {
            Orders orderToCancel = Orders.findById(orderId);
            if(orderToCancel != null){
                orderToCancel.updateStatus("Đã huỷ");
                Tables table = Tables.findById(orderToCancel.getTable_id());
                if(table != null){
                    table.markAsAvailable();
                }
            }
        }
        catch(SQLException e){
            e.printStackTrace();
        }
    }
}
