package com.ffrowies.infnserver;

import android.content.DialogInterface;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
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
import com.rengwuxian.materialedittext.MaterialEditText;

import java.text.NumberFormat;
import java.util.ArrayList;
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

        new Database(this).cleanCart();

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

        loadListItems();
        addCartItem();

    }

    private void registerInvoice() {

        //Create new invoice
        //txvTotalPrice.getText().toString();

        //Submit to Firebase
    }

    private void addCartItem() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(Cart.this);
        alertDialog.setTitle("Product | Service");
        alertDialog.setMessage("Please fill full information");

        LayoutInflater inflater = this.getLayoutInflater();
        View add_item_layout = inflater.inflate(R.layout.add_new_item, null);

        edtDescription = (MaterialEditText) add_item_layout.findViewById(R.id.edtDescription);
        edtAmount = (MaterialEditText) add_item_layout.findViewById(R.id.edtAmount);

        alertDialog.setView(add_item_layout);
        alertDialog.setIcon(R.drawable.ic_add_shopping_cart_black_24dp);

        //Set button
        alertDialog.setPositiveButton("ADD", new DialogInterface.OnClickListener() {
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
        alertDialog.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
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
