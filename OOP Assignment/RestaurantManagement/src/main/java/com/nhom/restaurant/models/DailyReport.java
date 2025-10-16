package com.nhom.restaurant.models;

import com.nhom.restaurant.models.Orders;
import java.util.*;

public class DailyReport {
    private final List<Orders> completedOrders;
    private final int totalRevenue;

    public DailyReport(List<Orders> completedOrders, int totalRevenue) {
        this.completedOrders = completedOrders;
        this.totalRevenue = totalRevenue;
    }
    public List<Orders> getCompletedOrders() {
        return completedOrders;
    }
    public int getTotalRevenue() {
        return totalRevenue;
    }
}
