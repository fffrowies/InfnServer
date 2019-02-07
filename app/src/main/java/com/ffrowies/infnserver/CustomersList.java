package com.ffrowies.infnserver;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import com.ffrowies.infnserver.Interface.ItemClickListener;
import com.ffrowies.infnserver.Models.User;
import com.ffrowies.infnserver.ViewHolder.CustomerViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class CustomersList extends AppCompatActivity {

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;

    FirebaseDatabase db;
    DatabaseReference customersList;

    FirebaseRecyclerAdapter<User, CustomerViewHolder> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customers_list);

        //Firebase Init
        db = FirebaseDatabase.getInstance();
        customersList = db.getReference("User");

        recyclerView = (RecyclerView) findViewById(R.id.recyclerCustomer);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), AddCustomers.class);
                startActivity(intent);
            }
        });

        loadCustomersList();
    }

    private void loadCustomersList() {
        adapter = new FirebaseRecyclerAdapter<User, CustomerViewHolder>(
                User.class,
                R.layout.layout_customer_item,
                CustomerViewHolder.class,
                customersList.orderByChild("name")  //TODO filter isStaff false
        ) {
            @Override
            protected void populateViewHolder(CustomerViewHolder viewHolder, User model, int position) {
                viewHolder.txvName.setText(model.getName());
                viewHolder.txvEmail.setText(model.getEmail());
                viewHolder.txvPhone.setText(model.getPhone());
                viewHolder.txvAddress.setText(model.getAddress());

                final User local = model;
                viewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        Toast.makeText(CustomersList.this, ""+local.getName(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        };

//        adapter.notifyDataSetChanged();
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onBackPressed() {
        returnToMain();
    }

    private void returnToMain() {
        Intent intent = new Intent(this, MainActivity.class)
                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        startActivity(intent);
    }
}
