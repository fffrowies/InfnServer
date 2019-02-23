package com.ffrowies.infnserver;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;

import android.widget.TextView;
import android.widget.Toast;

import com.ffrowies.infnserver.Utils.Common;
import com.ffrowies.infnserver.ViewHolder.InvoicesAdapter;
import com.ffrowies.infnserver.Models.Invoice;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class InvoicesList extends AppCompatActivity {

    RecyclerView recyclerView;

    final int ITEM_LOAD_COUNT = 24;
    final int ITEM_VIEW_COUNT = 16;         //aprox fill screen
    int totalItem = 0, lastVisibleItem;
    InvoicesAdapter adapter;
    boolean isLoading = false, isMaxData = false;

    String lastNode = "", lastNodeIn = "0", lastNodeOut = "0", lastKey = "", customerId;

    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invoices_list);

        recyclerView = (RecyclerView) findViewById(R.id.invoicesRecyclerView);
        toolbar = (Toolbar) findViewById(R.id.toolbar);

        toolbar.setTitle(Common.currentCustomer.getName().toUpperCase());

        customerId = Common.currentCustomer.getId();

        getLastKeyFromFirebase();

        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        DividerItemDecoration dividerItemDecoration =
                new DividerItemDecoration(recyclerView.getContext(), layoutManager.getOrientation());
        recyclerView.addItemDecoration(dividerItemDecoration);

        adapter = new InvoicesAdapter(this);
        recyclerView.setAdapter(adapter);

        getInvoices();

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                totalItem = layoutManager.getItemCount();
                lastVisibleItem = layoutManager.findLastVisibleItemPosition();

                if (!isLoading && totalItem <= (lastVisibleItem + ITEM_LOAD_COUNT))
                {
                    getInvoices();
                    isLoading = true;
                }
            }
        });
    }

    private void getInvoices() {
        if (!isMaxData)
        {
            Query query;

            if (TextUtils.isEmpty(lastNode))
                query = FirebaseDatabase.getInstance().getReference()
                        .child("Invoices")
                        .orderByChild("customerId")
                        .equalTo(customerId)
                        .limitToFirst(ITEM_LOAD_COUNT);
            else
                query = FirebaseDatabase.getInstance().getReference()
                        .child("Invoices")
                        .orderByKey()
                        .startAt(lastNode)
                        .limitToFirst(ITEM_LOAD_COUNT);

            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.hasChildren()) {
                        List<Invoice> newInvoices = new ArrayList<>();
                        for (DataSnapshot invoiceSnapshot : dataSnapshot.getChildren()) {
                            if (invoiceSnapshot.getValue(Invoice.class).getCustomerId().equals(customerId))
                            {
                                newInvoices.add(invoiceSnapshot.getValue(Invoice.class));
                                lastNodeIn = invoiceSnapshot.getValue(Invoice.class).getId();
                            }
                            else
                                lastNodeOut = invoiceSnapshot.getValue(Invoice.class).getId();

                            if (newInvoices.size() == ITEM_VIEW_COUNT)
                                break;
                        }


                        if (!newInvoices.isEmpty()) {
                            if (lastNodeIn.equals(lastKey))
                                lastNode = "end";   //Fix error infinity load final item
                            else {
                                if (Long.parseLong(lastNodeIn) > Long.parseLong(lastNodeOut))
                                    lastNode = lastNodeIn;
                                else
                                    lastNode = lastNodeOut;

                                newInvoices.remove(newInvoices.size() - 1);
                            }

                            adapter.addAll(newInvoices);
                        }
                        else
                            lastNode = lastNodeOut;

                        isLoading = false;
                    }
                    else
                    {
                        isLoading = false;
                        isMaxData = true;
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    isLoading = false;
                }
            });
        }
    }

    private void getLastKeyFromFirebase() {

        Query getLastKey = FirebaseDatabase.getInstance().getReference()
                .child("Invoices")
                .orderByChild("customerId")
                .equalTo(customerId)
                .limitToLast(1);

        getLastKey.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot lastKeySnapshot : dataSnapshot.getChildren()) {
                    lastKey = lastKeySnapshot.getKey();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(InvoicesList.this, "Cannot get last key", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
