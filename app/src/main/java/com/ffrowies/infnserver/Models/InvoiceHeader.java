package com.ffrowies.infnserver.Models;

public class InvoiceHeader {
    private String idCustomer;
    private String idInvoice;
    private String invoiceDate;

    public InvoiceHeader() {

    }

    public InvoiceHeader(String idCustomer, String idInvoice, String invoiceDate) {
        this.idCustomer = idCustomer;
        this.idInvoice = idInvoice;
        this.invoiceDate = invoiceDate;
    }

    public String getIdCustomer() {
        return idCustomer;
    }

    public void setIdCustomer(String idCustomer) {
        this.idCustomer = idCustomer;
    }

    public String getIdInvoice() {
        return idInvoice;
    }

    public void setIdInvoice(String idInvoice) {
        this.idInvoice = idInvoice;
    }

    public String getInvoiceDate() {
        return invoiceDate;
    }

    public void setInvoiceDate(String invoiceDate) {
        this.invoiceDate = invoiceDate;
    }
}
