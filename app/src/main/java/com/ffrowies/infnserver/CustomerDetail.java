package com.ffrowies.infnserver;

import android.support.annotation.NonNull;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.ffrowies.infnserver.Models.Customers;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class CustomerDetail extends AppCompatActivity {

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

        //Get customer Id from Intent
        if (getIntent() != null)
            customerId = getIntent().getStringExtra("CustomerId");

        if (!customerId.isEmpty())
        {
            getDetailCustomer(customerId);
        }

    }

    private void getDetailCustomer(String customerId) {
        customers.child(customerId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                currentCustomer = dataSnapshot.getValue(Customers.class);

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


}
