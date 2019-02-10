package com.ffrowies.infnserver;

import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class CustomerDetail extends AppCompatActivity {

    TextView txvCustomerName, txvCustomerAddress, txvCustomerEmail, txvCustomerPhone;
    ImageView customerImage;
    CollapsingToolbarLayout collapsingToolbarLayout;
    FloatingActionButton btnCart;

    FirebaseDatabase db;
    DatabaseReference customers;
    
    String customerId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_detail);

        //Firebase
        db = FirebaseDatabase.getInstance();
        customers = db.getReference("Customers");

        //Init View
        txvCustomerName = (TextView) findViewById(R.id.txvCustomerName);
        txvCustomerAddress = (TextView) findViewById(R.id.txvCustomerAddress);
        txvCustomerEmail = (TextView) findViewById(R.id.txvCustomerEmail);
        txvCustomerPhone = (TextView) findViewById(R.id.txvCustomerPhone);

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

    }
}
