package com.ffrowies.infnserver;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.ffrowies.infnserver.Interface.ItemClickListener;
import com.ffrowies.infnserver.Models.Customers;
import com.ffrowies.infnserver.Utils.Common;
import com.ffrowies.infnserver.ViewHolder.CustomerViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.makeramen.roundedimageview.RoundedImageView;
import com.mancj.materialsearchbar.MaterialSearchBar;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.squareup.picasso.Picasso;

import org.apache.commons.text.WordUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class CustomersList extends AppCompatActivity {

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;

    FloatingActionButton fab;

    //Firebase
    FirebaseDatabase db;
    DatabaseReference customersList;
    FirebaseStorage storage;
    StorageReference storageReference;
    FirebaseRecyclerAdapter<Customers, CustomerViewHolder> adapter;

    Customers newCustomer;

    //Add New Customer Layout
    MaterialEditText edtName, edtAddress, edtEmail, edtPhone;
    RoundedImageView rivCustomer;
    Button btnSelect, btnUpload;

    //Search functionality
    FirebaseRecyclerAdapter<Customers, CustomerViewHolder> searchAdapter;

    List<String> suggestList = new ArrayList<String>();
    MaterialSearchBar materialSearchBar;

    Date date = new Date();

    Uri saveUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customers_list);

        fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAddCustomerDialog();
            }
        });

        //Firebase
        db = FirebaseDatabase.getInstance();
        customersList = db.getReference("Customers");
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        //Init
        recyclerView = (RecyclerView) findViewById(R.id.recyclerCustomer);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        rivCustomer = new RoundedImageView(CustomersList.this);

        loadCustomersList();

        //Search
        materialSearchBar = (MaterialSearchBar) findViewById(R.id.searchBar);
        materialSearchBar.setHint(getString(R.string.enter_customer_name));
        loadSuggest();
        materialSearchBar.setLastSuggestions(suggestList);
        materialSearchBar.setCardViewElevation(10);

        materialSearchBar.addTextChangeListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //To change suggest list while user type text
                List<String> suggest = new ArrayList<String>();

                for (String search:suggestList)
                {
                    if (search.toLowerCase().contains(materialSearchBar.getText().toLowerCase()))
                        suggest.add(search);
                }
                materialSearchBar.setLastSuggestions(suggest);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        materialSearchBar.setOnSearchActionListener(new MaterialSearchBar.OnSearchActionListener() {
            @Override
            public void onSearchStateChanged(boolean enabled) {
                //When Search Bar is closed
                //restore original adapter
                if (!enabled)
                    recyclerView.setAdapter(adapter);
            }

            @Override
            public void onSearchConfirmed(CharSequence text) {
                //When search finish
                // show result of search
                startSearch(text);
            }

            @Override
            public void onButtonClicked(int buttonCode) {

            }
        });
    }

    private void startSearch(CharSequence text) {
        searchAdapter = new FirebaseRecyclerAdapter<Customers, CustomerViewHolder>(
                Customers.class,
                R.layout.layout_customer_item,
                CustomerViewHolder.class,
                customersList.orderByChild("name").equalTo(text.toString())     //Compare name
        ) {
            @Override
            protected void populateViewHolder(CustomerViewHolder viewHolder, Customers model, int position) {
                viewHolder.txvName.setText(model.getName());
                viewHolder.txvEmail.setText(model.getEmail());
                viewHolder.txvPhone.setText(model.getPhone());
                viewHolder.txvAddress.setText(model.getAddress());

                Picasso.with(getBaseContext())
                        .load(model.getImage())
                        .into(viewHolder.rivCustomer);

                final Customers local = model;
                viewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        Intent intent = new Intent(CustomersList.this, CustomerDetail.class);
                        intent.putExtra("CustomerId", local.getId());
                        startActivity(intent);
                    }
                });
            }
        };
        recyclerView.setAdapter(searchAdapter);     //Set adapter for Recycler View is Search result
    }

    private void loadSuggest() {
        customersList.orderByChild("Name")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot postSnapshot:dataSnapshot.getChildren())
                        {
                            Customers item = postSnapshot.getValue(Customers.class);

                            suggestList.add(item.getName());        //Add name of customer/user
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    private void loadCustomersList() {
        adapter = new FirebaseRecyclerAdapter<Customers, CustomerViewHolder>(
        Customers.class,
        R.layout.layout_customer_item,
        CustomerViewHolder.class,
        customersList.orderByChild("name")
        ) {
            @Override
            protected void populateViewHolder(CustomerViewHolder viewHolder, Customers model, int position) {
                viewHolder.txvName.setText(model.getName());
                viewHolder.txvEmail.setText(model.getEmail());
                viewHolder.txvPhone.setText(model.getPhone());
                viewHolder.txvAddress.setText(model.getAddress());

                Picasso.with(getBaseContext())
                        .load(model.getImage())
                        .into(viewHolder.rivCustomer);

                final Customers local = model;
                viewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        Intent intent = new Intent(CustomersList.this, CustomerDetail.class);
                        intent.putExtra("CustomerId", local.getId());
                        startActivity(intent);
                    }
                });
            }
        };
        adapter.notifyDataSetChanged();
        recyclerView.setAdapter(adapter);
    }

    private void showAddCustomerDialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(CustomersList.this);
        alertDialog.setTitle(getString(R.string.add_new_customer));
        alertDialog.setMessage(getString(R.string.please_fill_full_info));

        LayoutInflater inflater = this.getLayoutInflater();
        View add_customer_layout = inflater.inflate(R.layout.add_new_customer, null);

        edtName = (MaterialEditText) add_customer_layout.findViewById(R.id.edtName);
        edtAddress = (MaterialEditText) add_customer_layout.findViewById(R.id.edtAddress);
        edtEmail = (MaterialEditText) add_customer_layout.findViewById(R.id.edtEmail);
        edtPhone = (MaterialEditText) add_customer_layout.findViewById(R.id.edtPhone);
        btnSelect = (Button) add_customer_layout.findViewById(R.id.btnSelect);
        btnUpload = (Button) add_customer_layout.findViewById(R.id.btnUpload);

        //Event for button
        btnSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage();  //user select from gallery and save Uri of this image
            }
        });

        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadImage();
            }
        });

        alertDialog.setView(add_customer_layout);
        alertDialog.setIcon(R.drawable.ic_person_add_black_24dp);

        //Set button
        alertDialog.setPositiveButton(getString(R.string.add).toUpperCase(), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();

                //Create new Customer
                if (newCustomer != null)
                {
                    customersList.push().setValue(newCustomer);
                }
            }
        });
        alertDialog.setNegativeButton(getString(R.string.cancel).toUpperCase(), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //TODO delete image on storage if was uploaded
                dialog.dismiss();
            }
        });
        alertDialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Common.PICK_IMAGE_REQUEST
                && resultCode == RESULT_OK
                && data != null
                && data.getData() != null)
        {
            saveUri = data.getData();
            btnSelect.setText(getString(R.string.selected) + "!!!");
        }
    }

    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, getString(R.string.select_picture)), Common.PICK_IMAGE_REQUEST);
    }

    private void uploadImage() {

        if (saveUri != null)
        {
            final ProgressDialog mDialog = new ProgressDialog(CustomersList.this);
            mDialog.setMessage(getString(R.string.uploading) + "...");
            mDialog.show();

            String imageName = UUID.randomUUID().toString();
            final StorageReference imageFolder = storageReference.child("images/" + imageName);
            imageFolder.putFile(saveUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            mDialog.dismiss();
                            btnUpload.setText(getString(R.string.uploaded) + "!!!");

                            imageFolder.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    //set value for new customer if image upload and we can get download link
                                    newCustomer = new Customers();

                                    newCustomer.setId(Long.toString(date.getTime()));
                                    newCustomer.setName(WordUtils.capitalizeFully(edtName.getText().toString()));
                                    newCustomer.setAddress(WordUtils.capitalizeFully(edtAddress.getText().toString()));
                                    newCustomer.setEmail(edtEmail.getText().toString());
                                    newCustomer.setPhone(edtPhone.getText().toString());
                                    newCustomer.setImage(uri.toString());
                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            mDialog.dismiss();
                            Toast.makeText(CustomersList.this, "ERROR " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                            mDialog.setMessage(getString(R.string.uploaded) + " " + progress + "%");
                        }
                    });
        }
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
