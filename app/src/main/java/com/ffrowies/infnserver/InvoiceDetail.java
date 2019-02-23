package com.ffrowies.infnserver;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.format.DateFormat;
import android.util.Log;
import android.widget.TextView;

import com.ffrowies.infnserver.Models.Invoice;
import com.ffrowies.infnserver.Utils.Common;
import com.ffrowies.infnserver.ViewHolder.InvoiceDetailAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Date;

public class InvoiceDetail extends AppCompatActivity {

    TextView txvInvoiceDate, txvInvoiceTotal;
    String invoiceIdValue = "";
    RecyclerView lstInvoiceDetail;
    RecyclerView.LayoutManager layoutManager;
    Toolbar toolbar;
    Invoice currentInvoice;

    Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invoice_detail);

        txvInvoiceDate = (TextView) findViewById(R.id.txvInvoiceDate);
        txvInvoiceTotal = (TextView) findViewById(R.id.txvInvoiceTotal);

        toolbar = (Toolbar) findViewById(R.id.toolbar);

        toolbar.setTitle(Common.currentCustomer.getName().toUpperCase());

        lstInvoiceDetail = (RecyclerView) findViewById(R.id.lstInvoiceDetail);
        lstInvoiceDetail.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        lstInvoiceDetail.setLayoutManager(layoutManager);

        final ProgressDialog mDialog = new ProgressDialog(InvoiceDetail.this);
        mDialog.setMessage("Please waiting...");
        mDialog.show();

        if (getIntent() != null) {
            invoiceIdValue = getIntent().getStringExtra("InvoiceId");
        }

        getInvoiceDataFromFirebase();

        if (currentInvoice == null) {
            handler = new Handler();
            Runnable r = new Runnable() {
                public void run() {
                    mDialog.dismiss();

                    txvInvoiceDate.setText(currentInvoice.getDate());
                    txvInvoiceTotal.setText(currentInvoice.getTotal());

                    InvoiceDetailAdapter adapter = new InvoiceDetailAdapter(currentInvoice.getItems());
                    adapter.notifyDataSetChanged();
                    lstInvoiceDetail.setAdapter(adapter);
                }
            };
            handler.postDelayed(r,3000);
        }
    }

    private void getInvoiceDataFromFirebase() {
        Query getKey = FirebaseDatabase.getInstance().getReference()
                .child("Invoices")
                .orderByChild("id")
                .equalTo(invoiceIdValue);

        getKey.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot invoiceSnapshot : dataSnapshot.getChildren()) {
                    currentInvoice = invoiceSnapshot.getValue(Invoice.class);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
