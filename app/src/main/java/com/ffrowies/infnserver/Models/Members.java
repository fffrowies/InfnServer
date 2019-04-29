package com.ffrowies.infnserver.Models;

public class Members {
    private String cust_id;
    private String name;

    public Members() {
    }

    public Members(String cust_id, String name) {
        this.cust_id = cust_id;
        this.name = name;
    }

    public String getCust_id() {
        return cust_id;
    }

    public void setCust_id(String cust_id) {
        this.cust_id = cust_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
