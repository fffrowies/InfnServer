package com.ffrowies.infnserver.Models;

public class InvoiceItems {
    private String idInvoice;
    private String itemNumber;
    private String itemAmount;
    private String itemDescription;

    public InvoiceItems() {
    }

    public InvoiceItems(String idInvoice, String itemNumber, String itemAmount, String itemDescription) {
        this.idInvoice = idInvoice;
        this.itemNumber = itemNumber;
        this.itemAmount = itemAmount;
        this.itemDescription = itemDescription;
    }

    public String getIdInvoice() {
        return idInvoice;
    }

    public void setIdInvoice(String idInvoice) {
        this.idInvoice = idInvoice;
    }

    public String getItemNumber() {
        return itemNumber;
    }

    public void setItemNumber(String itemNumber) {
        this.itemNumber = itemNumber;
    }

    public String getItemAmount() {
        return itemAmount;
    }

    public void setItemAmount(String itemAmount) {
        this.itemAmount = itemAmount;
    }

    public String getItemDescription() {
        return itemDescription;
    }

    public void setItemDescription(String itemDescription) {
        this.itemDescription = itemDescription;
    }
}
