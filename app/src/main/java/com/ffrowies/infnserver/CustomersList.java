package com.ffrowies.infnserver;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.ffrowies.infnserver.Interface.ItemClickListener;
import com.ffrowies.infnserver.Models.User;
import com.ffrowies.infnserver.Utils.Common;
import com.ffrowies.infnserver.ViewHolder.CustomerViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.rengwuxian.materialedittext.MaterialEditText;

public class CustomersList extends AppCompatActivity {

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;

    //Firebase
    FirebaseDatabase db;
    DatabaseReference customersList;
    FirebaseRecyclerAdapter<User, CustomerViewHolder> adapter;

    User newCustomer;

    //Add New Product Layout
    MaterialEditText edtName, edtAddress, edtEmail, edtPhone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customers_list);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAddCustomerDialog();
            }
        });

        //Firebase
        db = FirebaseDatabase.getInstance();
        customersList = db.getReference("User");

        //Init
        recyclerView = (RecyclerView) findViewById(R.id.recyclerCustomer);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

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

                viewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        //Code late
                    }
                });
            }
        };
        adapter.notifyDataSetChanged();
        recyclerView.setAdapter(adapter);
    }

    private void showAddCustomerDialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(CustomersList.this);
        alertDialog.setTitle("Add new Customer");
        alertDialog.setMessage("Please fill full information");

        LayoutInflater inflater = this.getLayoutInflater();
        View add_customer_layout = inflater.inflate(R.layout.add_new_customer, null);

        edtName = (MaterialEditText) add_customer_layout.findViewById(R.id.edtName);
        edtAddress = (MaterialEditText) add_customer_layout.findViewById(R.id.edtAddress);
        edtEmail = (MaterialEditText) add_customer_layout.findViewById(R.id.edtEmail);
        edtPhone = (MaterialEditText) add_customer_layout.findViewById(R.id.edtPhone);

        alertDialog.setView(add_customer_layout);
        alertDialog.setIcon(R.drawable.ic_person_add_black_24dp);

        //Set button
        alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                newCustomer = new User();
                newCustomer.setName(edtName.getText().toString());
                newCustomer.setAddress(edtAddress.getText().toString());
                newCustomer.setEmail(edtEmail.getText().toString());
                newCustomer.setPhone(edtPhone.getText().toString());
                newCustomer.setPhone(edtPhone.getText().toString());
                newCustomer.setPassword(edtPhone.getText().toString());  // until Client side
                newCustomer.setIsStaff("false");

                dialog.dismiss();

                //Create new Product
                if (newCustomer != null)
                {
                    customersList.push().setValue(newCustomer);
                    Toast.makeText(CustomersList.this, "New customer "+newCustomer.getName()+" was added", Toast.LENGTH_SHORT).show();
                }
            }
        });
        alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();
            }
        });
        alertDialog.show();
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        if (item.getTitle().equals(Common.UPDATE))
        {
            showUpdateCustomerDialog(adapter.getRef(item.getOrder()).getKey(), adapter.getItem(item.getOrder()));
        }
        else if (item.getTitle().equals(Common.DELETE))
        {
            deleteProduct(adapter.getRef(item.getOrder()).getKey());
        }

        return super.onContextItemSelected(item);
    }

    private void deleteProduct(String key) {
        customersList.child(key).removeValue();
    }

    private void showUpdateCustomerDialog(final String key, final User item) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(CustomersList.this);
        alertDialog.setTitle("Update Customer");
        alertDialog.setMessage("Please fill full information");

        LayoutInflater inflater = this.getLayoutInflater();
        View add_customer_layout = inflater.inflate(R.layout.add_new_customer, null);

        edtName = (MaterialEditText) add_customer_layout.findViewById(R.id.edtName);
        edtAddress = (MaterialEditText) add_customer_layout.findViewById(R.id.edtAddress);
        edtEmail = (MaterialEditText) add_customer_layout.findViewById(R.id.edtEmail);
        edtPhone = (MaterialEditText) add_customer_layout.findViewById(R.id.edtPhone);

        //set default value for view
        edtName.setText(item.getName());
        edtAddress.setText(item.getAddress());
        edtEmail.setText(item.getEmail());
        edtPhone.setText(item.getPhone());

        alertDialog.setView(add_customer_layout);
        alertDialog.setIcon(R.drawable.ic_person_add_black_24dp);

        //Set button
        alertDialog.setPositiveButton("UPDATE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();

                //Update information
                item.setName(edtName.getText().toString());
                item.setAddress(edtAddress.getText().toString());
                item.setEmail(edtEmail.getText().toString());
                item.setPhone(edtPhone.getText().toString());

                customersList.child(key).setValue(item);
                Toast.makeText(CustomersList.this, "Customer "+item.getName()+" was updated", Toast.LENGTH_SHORT).show();
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
