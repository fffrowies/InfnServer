package com.ffrowies.infnserver;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ffrowies.infnserver.Models.Customers;
import com.ffrowies.infnserver.Utils.Common;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class CustomerDetail extends AppCompatActivity {

    private static final String TAG = "CustomerDetail";

    TextView txvCustomerAddress, txvCustomerEmail, txvCustomerPhone;
    ImageView imvCustomer;
    CollapsingToolbarLayout collapsingToolbarLayout;
    FloatingActionButton btnCart;

    FirebaseDatabase db;
    DatabaseReference customers;
    
    String customerId;

    Customers currentCustomer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_detail);

        //Firebase
        db = FirebaseDatabase.getInstance();
        customers = db.getReference("Customers");

        //Init View
        txvCustomerAddress = (TextView) findViewById(R.id.txvCustomerAddress);
        txvCustomerEmail = (TextView) findViewById(R.id.txvCustomerEmail);
        txvCustomerPhone = (TextView) findViewById(R.id.txvCustomerPhone);
        imvCustomer = (ImageView) findViewById(R.id.imvCustomer);

        btnCart = (FloatingActionButton) findViewById(R.id.btnCart);

        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing);
        collapsingToolbarLayout.setExpandedTitleTextAppearance(R.style.ExpandedAppbar);
        collapsingToolbarLayout.setCollapsedTitleTextAppearance(R.style.CollapsedAppbar);

        btnCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = deleteCustomer();
                Toast.makeText(CustomerDetail.this, "Customer " + name + " was deleted", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(CustomerDetail.this, CustomersList.class);
                startActivity(intent);
            }
        });

        //Get customer Id from Intent
        if (getIntent() != null)
            customerId = getIntent().getStringExtra("CustomerId");

        if (!customerId.isEmpty())
        {
            getDetailCustomer();
        }
    }

    private void getDetailCustomer() {
        Query customersQuery = customers.orderByChild("id").equalTo(customerId);
        customersQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot idSnapshot: dataSnapshot.getChildren()) {
                    currentCustomer = idSnapshot.getValue(Customers.class);
                }

                //Set Image
                Picasso.with(getBaseContext()).load(currentCustomer.getImage()).into(imvCustomer);
                collapsingToolbarLayout.setTitle(currentCustomer.getName());
                txvCustomerAddress.setText(currentCustomer.getAddress());
                txvCustomerEmail.setText(currentCustomer.getEmail());
                txvCustomerPhone.setText(currentCustomer.getPhone());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    @Override
    public boolean onContextItemSelected(MenuItem item) {

        if (item.getTitle().equals(Common.UPDATE))
        {
//            showUpdateCustomerDialog(customerId);
        }
        else if (item.getTitle().equals(Common.DELETE))
        {
//            deleteCustomer();
        }

        return super.onContextItemSelected(item);
    }

    private String deleteCustomer() {

        Query customersQuery = customers.orderByChild("id").equalTo(customerId);
        customersQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot idSnapshot: dataSnapshot.getChildren()) {
                    currentCustomer = idSnapshot.getValue(Customers.class);  //to get name and return it
                    idSnapshot.getRef().removeValue();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "onCancelled", databaseError.toException());
            }
        });
        return currentCustomer.getName();       //return it to inform who was deleted
    }
}
