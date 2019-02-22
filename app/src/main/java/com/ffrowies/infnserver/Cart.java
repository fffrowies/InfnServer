package com.ffrowies.infnserver;

import android.content.DialogInterface;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.ffrowies.infnserver.Database.Database;
import com.ffrowies.infnserver.Models.Invoice;
import com.ffrowies.infnserver.Models.Order;
import com.ffrowies.infnserver.Utils.Common;
import com.ffrowies.infnserver.ViewHolder.CartAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class Cart extends AppCompatActivity {

    RecyclerView recycler_cart;
    RecyclerView.LayoutManager layoutManager;

    FloatingActionButton fab;

    FirebaseDatabase database;
    DatabaseReference invoices;

    TextView txvTotalPrice;
    Button btnPlaceOrder;

    //Inflated layout (to add item invoice: add_item_layout)
    MaterialEditText edtDescription, edtAmount;

    List<Order> cart = new ArrayList<>();

    CartAdapter adapter;

    String customerId;
    Date date = new Date();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addCartItem();
            }
        });

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
            customerId = getIntent().getStringExtra("CustomerId");

        new Database(this).cleanCart();

        txvTotalPrice = (TextView) findViewById(R.id.txvTotalPrice);
        btnPlaceOrder = (Button) findViewById(R.id.btnPlaceOrder);

        btnPlaceOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (cart.size() > 0)
                    registerInvoice();
                else
                    Toast.makeText(Cart.this, getString(R.string.your_cart_is_empty) + "!!!", Toast.LENGTH_SHORT).show();
            }
        });

        loadListItems();
        addCartItem();

    }

    private void registerInvoice() {

        //Using System.CurrentMilli to key and date
        String currentSystemTime = String.valueOf(System.currentTimeMillis());

        String dateString = DateFormat.format("dd/MM/yyyy", new Date(System.currentTimeMillis())).toString();

        Invoice invoice = new Invoice(
                customerId,
                currentSystemTime,
                dateString,
                txvTotalPrice.getText().toString(),
                cart
        );

        //Submit to Firebase
        invoices.child(currentSystemTime).setValue(invoice);
        //Delete cart
        new Database(getBaseContext()).cleanCart();
        Toast.makeText(Cart.this, getString(R.string.thank_you), Toast.LENGTH_SHORT).show();
        finish();
    }

    private void addCartItem() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(Cart.this);
        alertDialog.setTitle(getString(R.string.product_service));
        alertDialog.setMessage(getString(R.string.please_fill_full_info));

        LayoutInflater inflater = this.getLayoutInflater();
        View add_item_layout = inflater.inflate(R.layout.add_new_item, null);

        edtDescription = (MaterialEditText) add_item_layout.findViewById(R.id.edtDescription);
        edtAmount = (MaterialEditText) add_item_layout.findViewById(R.id.edtAmount);

        alertDialog.setView(add_item_layout);
        alertDialog.setIcon(R.drawable.ic_shopping_cart_black_24dp);

        //Set button
        alertDialog.setPositiveButton(getString(R.string.add).toUpperCase(), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (!edtDescription.getText().toString().isEmpty() && !edtAmount.getText().toString().isEmpty())
                {
                    //add to sqlite db and recyclerview listCart
                    new Database(getBaseContext()).addToCart(new Order(
                            edtDescription.getText().toString(),
                            edtAmount.getText().toString()));

                    dialog.dismiss();
                    loadListItems();
                }
            }
        });
        alertDialog.setNegativeButton(getString(R.string.cancel).toUpperCase(), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();
            }
        });
        alertDialog.show();
    }

    private void loadListItems() {
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
        loadListItems();
    }
}
