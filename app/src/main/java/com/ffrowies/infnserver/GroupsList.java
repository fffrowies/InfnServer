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
import com.ffrowies.infnserver.Models.Groups;
import com.ffrowies.infnserver.Utils.Common;
import com.ffrowies.infnserver.ViewHolder.CustomerViewHolder;
import com.ffrowies.infnserver.ViewHolder.GroupViewHolder;
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

public class GroupsList extends AppCompatActivity {

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;

    FloatingActionButton fab;

    //Firebase
    FirebaseDatabase db;
    DatabaseReference groupsList;
    FirebaseStorage storage;
    StorageReference storageReference;
    FirebaseRecyclerAdapter<Groups, GroupViewHolder> adapter;

    Groups newGroup;

    //Add New Group Layout
    MaterialEditText edtGroupName;
    RoundedImageView rivGroup;
    Button btnGroupSelect, btnGroupUpload;

    //Search functionality
    FirebaseRecyclerAdapter<Groups, GroupViewHolder> searchAdapter;

    Date date = new Date();

    Uri saveUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_groups_list);

        fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAddCustomerDialog();
            }
        });

        //Firebase
        db = FirebaseDatabase.getInstance();
        groupsList = db.getReference("Groups");
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        //Init
        recyclerView = (RecyclerView) findViewById(R.id.recyclerGroup);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        rivGroup = new RoundedImageView(GroupsList.this);

        loadGroupsList();
    }

    private void loadGroupsList() {
        adapter = new FirebaseRecyclerAdapter<Groups, GroupViewHolder>(
                Groups.class,
                R.layout.layout_group_item,
                GroupViewHolder.class,
                groupsList.orderByChild("name")
        ) {
            @Override
            protected void populateViewHolder(GroupViewHolder viewHolder, Groups model, int position) {
                viewHolder.txvGroupName.setText(model.getName());

                Picasso.with(getBaseContext())
                        .load(model.getImage())
                        .into(viewHolder.rivGroup);

                final Groups local = model;
                viewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        Intent intent = new Intent(GroupsList.this, GroupDetail.class);
                        intent.putExtra("GroupId", local.getId());
                        startActivity(intent);
                    }
                });
            }
        };
        adapter.notifyDataSetChanged();
        recyclerView.setAdapter(adapter);
    }

    private void showAddGroupDialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(GroupsList.this);
        alertDialog.setTitle(getString(R.string.add_new_group));
        alertDialog.setMessage(getString(R.string.please_fill_full_info));

        LayoutInflater inflater = this.getLayoutInflater();
        View add_group_layout = inflater.inflate(R.layout.add_new_group, null);

        edtName = (MaterialEditText) add_group_layout.findViewById(R.id.edtGroupName);
        btnSelect = (Button) add_group_layout.findViewById(R.id.btnGroupSelect);
        btnUpload = (Button) add_group_layout.findViewById(R.id.btnGroupUpload);

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

        alertDialog.setView(add_group_layout);
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
