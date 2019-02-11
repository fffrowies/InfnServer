package com.ffrowies.infnserver;

import android.content.Intent;
import android.net.Uri;
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

import com.darwindeveloper.horizontalscrollmenulibrary.custom_views.HorizontalScrollMenuView;
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

    HorizontalScrollMenuView menu;

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

        menu = (HorizontalScrollMenuView) findViewById(R.id.menu);

        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing);
        collapsingToolbarLayout.setExpandedTitleTextAppearance(R.style.ExpandedAppbar);
        collapsingToolbarLayout.setCollapsedTitleTextAppearance(R.style.CollapsedAppbar);

        btnCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        //Get customer Id from Intent
        if (getIntent() != null)
            customerId = getIntent().getStringExtra("CustomerId");

        if (!customerId.isEmpty())
        {
            getDetailCustomer();
        }

        //Create menu
        initMenu();
    }

    private void initMenu() {
        menu.addItem("Customers", R.drawable.ic_contacts_black_24dp);
        menu.addItem("Account", R.drawable.ic_attach_money_black_24dp);
        menu.addItem("Cart", R.drawable.ic_shopping_cart_black_24dp);
        menu.addItem("Whatsapp", R.drawable.ic_iconfinder_whatsapp_115679);
        menu.addItem("Call", R.drawable.ic_phone_black_24dp);
        menu.addItem("SMS", R.drawable.ic_sms_black_24dp);
        menu.addItem("Update", R.drawable.ic_person_outline_black_24dp);
        menu.addItem("Delete", R.drawable.ic_delete_black_24dp);
        menu.addItem("Exit", R.drawable.ic_exit_to_app_black_24dp);

        menu.setOnHSMenuClickListener(new HorizontalScrollMenuView.OnHSMenuClickListener() {
            @Override
            public void onHSMClick(com.darwindeveloper.horizontalscrollmenulibrary.extras.MenuItem menuItem, int position) {
                Toast.makeText(CustomerDetail.this, ""+menuItem.getText(), Toast.LENGTH_SHORT).show();

                String phone = ((TextView) findViewById(R.id.txvCustomerPhone)).getText().toString();

                switch (position)
                {
                    case 0:
                        //Customers
                        Intent intentCustomersList = new Intent(CustomerDetail.this, CustomersList.class);
                        startActivity(intentCustomersList);
                        break;
                    case 1:
                        //TODO Account
                        break;
                    case 2:
                        //TODO Cart
                        break;
                    case 3:
                        //TODO test Whatsapp
                        openWhatsappContact(phone);
                        break;
                    case 4:
                        //Phone Call
                        Intent intentCall = new Intent(
                                Intent.ACTION_DIAL, Uri.fromParts("tel", phone, null));
                        startActivity(intentCall);
                        break;
                    case 5:
                        //TODO SMS
                        break;
                    case 6:
                        //TODO Email
                        break;
                    case 7:
                        //TODO Update
                        break;
                    case 8:
                        //Delete
                        String name = deleteCustomer();
                        Toast.makeText(CustomerDetail.this, "Customer " + name + " was deleted", Toast.LENGTH_SHORT).show();
                        Intent intentDeleted = new Intent(CustomerDetail.this, CustomersList.class);
                        startActivity(intentDeleted);
                        break;
                    case 9:
                        //Exit
                        Intent intentExit = new Intent(CustomerDetail.this, MainActivity.class)
                                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intentExit);
                        break;
                }
            }
        });
    }

    private void openWhatsappContact(String number) {
        Uri uri = Uri.parse("smsto:" + number);
        Intent i = new Intent(Intent.ACTION_SENDTO, uri);
        i.setPackage("com.whatsapp");
        startActivity(Intent.createChooser(i, ""));
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
