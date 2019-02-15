package com.ffrowies.infnserver;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import com.ffrowies.infnserver.Database.Database;
import com.ffrowies.infnserver.Models.Invoice;
import com.ffrowies.infnserver.Models.Order;
import com.ffrowies.infnserver.ViewHolder.CartAdapter;
import com.ffrowies.infnserver.ViewHolder.InvoiceViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class InvoiceDetail extends AppCompatActivity {

    RecyclerView recycler_cart;
    RecyclerView.LayoutManager layoutManager;

    FirebaseDatabase database;
    DatabaseReference invoices;
//    FirebaseRecyclerAdapter<Invoice, InvoiceDetailViewHolder> adapter;

    TextView txvTotalPrice;

    List<Order> cart = new ArrayList<>();

    String invoiceId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invoice_detail);

        //Firebase
        database = FirebaseDatabase.getInstance();
        invoices = database.getReference("Invoices");

        //Init
        recycler_cart = (RecyclerView) findViewById(R.id.listCart);
        recycler_cart.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recycler_cart.setLayoutManager(layoutManager);

        //Get customer Id from Intent
        if (getIntent() != null)
            invoiceId = getIntent().getStringExtra("InvoiceId");

        new Database(this).cleanCart();

        txvTotalPrice = (TextView) findViewById(R.id.txvTotalPrice);

        loadListItems();
    }

    private void loadListItems() {
//        cart = new Database(this).getCarts();
//        adapter = new CartAdapter(cart, this);
//        adapter.notifyDataSetChanged();
//        recycler_cart.setAdapter(adapter);

        //Calculate total price
        int total = 0;
        for(Order order:cart)
            total += Integer.parseInt(order.getPrice());
        Locale locale = new Locale("en", "US");
        NumberFormat fmt = NumberFormat.getCurrencyInstance(locale);

        txvTotalPrice.setText(fmt.format(total));
    }

    private void deleteCart(int position) {
        //remove item at List<Order> by position
        cart.remove(position);
        // delete all old data from SQLite
        new Database(this).cleanCart();
        // update new data from List<Order> to SQLite
        for (Order item:cart)
            new Database(this).addToCart(item);
        //Refresh
        loadListItems();
    }

}
