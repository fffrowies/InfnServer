package com.ffrowies.infnserver.Models;

import java.util.List;

public class Invoice {
    private String customerId;
    private String date;
    private String total;
    private List<Order> items;

    public Invoice() {
    }

    public Invoice(String customerId, String date, String total, List<Order> items) {
        this.customerId = customerId;
        this.date = date;
        this.total = total;
        this.items = items;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public List<Order> getItems() {
        return items;
    }

    public void setItems(List<Order> items) {
        this.items = items;
    }
}
