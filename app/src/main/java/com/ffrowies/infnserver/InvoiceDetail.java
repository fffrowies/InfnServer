package com.ffrowies.infnserver;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.widget.TextView;

import com.ffrowies.infnserver.Utils.Common;
import com.ffrowies.infnserver.ViewHolder.InvoiceDetailAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Date;

public class InvoiceDetail extends AppCompatActivity {

    TextView txvCustomerName, txvInvoiceDate, txvInvoiceTotal;
    String invoiceIdValue = "", invoiceKey = "";
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
            invoiceIdValue = getIntent().getStringExtra("InvoiceId");

        getInvoiceKeyFromFirebase();

        txvCustomerName.setText(Common.currentCustomer.getName());
        txvInvoiceDate.setText(invoiceKey.getDate());
        txvInvoiceTotal.setText(Common.currentInvoice.getTotal());

        InvoiceDetailAdapter adapter = new InvoiceDetailAdapter(Common.currentInvoice.getItems());
        adapter.notifyDataSetChanged();
        lstInvoiceDetail.setAdapter(adapter);

    }

    private void getInvoiceKeyFromFirebase() {
        Query getKey = FirebaseDatabase.getInstance().getReference()
                .child("Invoices")
                .orderByChild("id")
                .equalTo(invoiceIdValue);

        getKey.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot invoiceSnapshot : dataSnapshot.getChildren()) {
                    invoiceKey = invoiceSnapshot.getKey();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
