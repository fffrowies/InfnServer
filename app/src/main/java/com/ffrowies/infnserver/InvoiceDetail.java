package com.ffrowies.infnserver;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.widget.TextView;

import com.ffrowies.infnserver.Utils.Common;
import com.ffrowies.infnserver.ViewHolder.InvoiceDetailAdapter;

import java.util.Date;

public class InvoiceDetail extends AppCompatActivity {

    TextView txvCustomerName, txvInvoiceDate, txvInvoiceTotal;
    String invoiceIdValue = "";
    String customerName = "";
    RecyclerView lstInvoiceDetail;
    RecyclerView.LayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invoice_detail);

        txvCustomerName = (TextView) findViewById(R.id.txvCustomerName);
        txvInvoiceDate = (TextView) findViewById(R.id.txvInvoiceDate);
        txvInvoiceTotal = (TextView) findViewById(R.id.txvInvoiceTotal);

        lstInvoiceDetail = (RecyclerView) findViewById(R.id.lstInvoiceDetail);
        lstInvoiceDetail.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        lstInvoiceDetail.setLayoutManager(layoutManager);

        if (getIntent() != null)
        {
            invoiceIdValue = getIntent().getStringExtra("InvoiceId");
            customerName = getIntent().getStringExtra("CustomerName");
        }

        String longValueDate = Common.currentInvoice.getDate();
        long millisecond = Long.parseLong(longValueDate);
        String dateString = DateFormat.format("dd/MM/yyyy", new Date(millisecond)).toString();
        txvCustomerName.setText(customerName);
        txvInvoiceDate.setText(dateString);
        txvInvoiceTotal.setText(Common.currentInvoice.getTotal());

        InvoiceDetailAdapter adapter = new InvoiceDetailAdapter(Common.currentInvoice.getItems());
        adapter.notifyDataSetChanged();
        lstInvoiceDetail.setAdapter(adapter);

    }
}
