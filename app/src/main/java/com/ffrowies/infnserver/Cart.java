package com.ffrowies.infnserver;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.ffrowies.infnserver.Database.Database;
import com.ffrowies.infnserver.Models.Order;
import com.ffrowies.infnserver.Utils.Common;
import com.ffrowies.infnserver.ViewHolder.CartAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Cart extends AppCompatActivity {

    RecyclerView recycler_cart;
    RecyclerView.LayoutManager layoutManager;

    FirebaseDatabase database;
    DatabaseReference invoices;

    TextView txvTotalPrice;
    Button btnPlaceOrder;

    List<Order> cart = new ArrayList<>();

    CartAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        //Firebase
        database = FirebaseDatabase.getInstance();
        invoices = database.getReference("Invoices");

        //Init
        recycler_cart = (RecyclerView) findViewById(R.id.listCart);
        recycler_cart.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recycler_cart.setLayoutManager(layoutManager);

        txvTotalPrice = (TextView) findViewById(R.id.txvTotalPrice);
        btnPlaceOrder = (Button) findViewById(R.id.btnPlaceOrder);

        btnPlaceOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (cart.size() > 0)
                    registerInvoice();
                else
                    Toast.makeText(Cart.this, "Your cart is empty!!!", Toast.LENGTH_SHORT).show();
            }
        });

        loadListProducts();

    }

    private void registerInvoice() {

        //Create new invoice
        //txvTotalPrice.getText().toString();

        //Submit to Firebase
    }

    private void loadListProducts() {
        cart = new Database(this).getCarts();
        adapter = new CartAdapter(cart, this);
        adapter.notifyDataSetChanged();
        recycler_cart.setAdapter(adapter);

        //Calculate total price
        int total = 0;
        for(Order order:cart)
            total += Integer.parseInt(order.getPrice());
        Locale locale = new Locale("en", "US");
        NumberFormat fmt = NumberFormat.getCurrencyInstance(locale);

        txvTotalPrice.setText(fmt.format(total));
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (item.getTitle().equals(Common.DELETE))
            deleteCart(item.getOrder());

        return super.onContextItemSelected(item);
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
        loadListProducts();
    }
}
