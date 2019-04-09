package com.ffrowies.infnserver;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.darwindeveloper.horizontalscrollmenulibrary.custom_views.HorizontalScrollMenuView;
import com.ffrowies.infnserver.Models.Customers;
import com.ffrowies.infnserver.Utils.Common;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.squareup.picasso.Picasso;

import org.apache.commons.text.WordUtils;

import java.util.UUID;

import static com.ffrowies.infnserver.Utils.Common.currentCustomer;

public class CustomerDetail extends AppCompatActivity {

    private static final String TAG = "CustomerDetail";

    //This layout
    TextView txvCustomerAddress, txvCustomerEmail, txvCustomerPhone;
    ImageView imvCustomer;
    CollapsingToolbarLayout collapsingToolbarLayout;
    FloatingActionButton btnCart;

    //Inflated layout (to Update Customer: add_customer_layout)
    MaterialEditText edtName, edtAddress, edtEmail, edtPhone;
    Button btnSelect, btnUpload;

    FirebaseDatabase db;
    DatabaseReference customers;
    FirebaseStorage storage;
    StorageReference storageReference;
    
    String customerId, currentKey;

    Uri saveUri;

    HorizontalScrollMenuView menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_detail);

        //Firebase
        db = FirebaseDatabase.getInstance();
        customers = db.getReference("Customers");

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

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

        //Get customer Id from Intent
        if (getIntent() != null) {
            customerId = getIntent().getStringExtra("CustomerId");
        }

        if (!customerId.isEmpty())
        {
            getDetailCustomer();
        }

        btnCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Cart
                Intent intentCart = new Intent(CustomerDetail.this, Cart.class);
                intentCart.putExtra("CustomerId", customerId);
                startActivity(intentCart);
            }
        });

        //Create horizontal menu
        initMenu();
    }

    private void initMenu() {
        menu.addItem(getString(R.string.home), R.drawable.ic_home_black_24dp);
        menu.addItem(getString(R.string.exit), R.drawable.ic_exit_to_app_black_24dp);
        menu.addItem(getString(R.string.customers), R.drawable.ic_contacts_black_24dp);
        menu.addItem(getString(R.string.account), R.drawable.ic_attach_money_black_24dp);
        menu.addItem("Whatsapp", R.drawable.ic_iconfinder_whatsapp_115679);
        menu.addItem(getString(R.string.call), R.drawable.ic_phone_black_24dp);
        menu.addItem("SMS", R.drawable.ic_sms_black_24dp);
        menu.addItem("Email", R.drawable.ic_email_black_24dp);
        menu.addItem(getString(R.string.update), R.drawable.ic_person_outline_white_24dp);
        menu.addItem(getString(R.string.delete), R.drawable.ic_delete_black_24dp);

        menu.setOnHSMenuClickListener(new HorizontalScrollMenuView.OnHSMenuClickListener() {
            @Override
            public void onHSMClick(com.darwindeveloper.horizontalscrollmenulibrary.extras.MenuItem menuItem, int position) {
                String phone = ((TextView) findViewById(R.id.txvCustomerPhone)).getText().toString();
                String email = ((TextView) findViewById(R.id.txvCustomerEmail)).getText().toString();

                switch (position)
                {
                    case 0:
                        //Home
                        startActivity(new Intent(CustomerDetail.this, MainActivity.class));
                        break;
                    case 1:
                        //Exit
                        startActivity(
                                new Intent(CustomerDetail.this, SignIn.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                        break;
                    case 2:
                        //Customers
                        startActivity(new Intent(CustomerDetail.this, CustomersList.class));
                        break;
                    case 3:
                        //Account
                        Intent intentInvoicesList = new Intent(CustomerDetail.this, InvoicesList.class);
//                        intentInvoicesList.putExtra("CustomerId", customerId);
                        startActivity(intentInvoicesList);
                        break;
                    case 4:
                        //TODO (test Whatsapp)
                        openWhatsappContact(phone);
                        break;
                    case 5:
                        //Phone Call
                        startActivity(new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phone, null)));
                        break;
                    case 6:
                        //SMS
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.fromParts("sms", phone, null)));
                        break;
                    case 7:
                        //TODO (test Email)
                        Intent i = new Intent(Intent.ACTION_SEND);
                        i.setType("message/rfc822");
                        i.putExtra(Intent.EXTRA_EMAIL  , new String[]{email});
                        i.putExtra(Intent.EXTRA_SUBJECT, "subject of email");
                        i.putExtra(Intent.EXTRA_TEXT   , "body of email");
                        try {
                            startActivity(Intent.createChooser(i, "Send mail..."));
                        } catch (android.content.ActivityNotFoundException ex) {
                            Toast.makeText(CustomerDetail.this, getString(R.string.there_no_email_cli_inst), Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case 8:
                        //Update
                        showUpdateCustomerDialog();
                        break;
                    case 9:
                        //Delete
                        deleteCustomer();
                        Intent intentDeleted = new Intent(CustomerDetail.this, CustomersList.class);
                        startActivity(intentDeleted);
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
                    currentKey = idSnapshot.getKey();
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

    private void deleteCustomer() {
        //TODO delete image on storage
        customers.child(currentKey).removeValue();
    }

    private void showUpdateCustomerDialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(CustomerDetail.this);
        alertDialog.setTitle(getString(R.string.update_customer));
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

        //set default value for view
        edtName.setText(currentCustomer.getName());
        edtAddress.setText(currentCustomer.getAddress());
        edtEmail.setText(currentCustomer.getEmail());
        edtPhone.setText(currentCustomer.getPhone());

        alertDialog.setView(add_customer_layout);
        alertDialog.setIcon(R.drawable.ic_person_outline_black_24dp);

        //Set button
        alertDialog.setPositiveButton(getString(R.string.update).toUpperCase(), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                currentCustomer.setName(WordUtils.capitalizeFully(edtName.getText().toString()));
                currentCustomer.setAddress(WordUtils.capitalizeFully(edtAddress.getText().toString()));
                currentCustomer.setEmail(edtEmail.getText().toString());
                currentCustomer.setPhone(edtPhone.getText().toString());

                customers.child(currentKey).setValue(currentCustomer);

                dialog.dismiss();

                getDetailCustomer();
                Toast.makeText(CustomerDetail.this, "Customer "+currentCustomer.getName()+" was updated", Toast.LENGTH_SHORT).show();
            }
        });
        alertDialog.setNegativeButton(getString(R.string.cancel).toUpperCase(), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //TODO delete image if recent uploaded
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
            btnUpload.setText(getString(R.string.upload));
            btnSelect.setText(getString(R.string.selected) + " !!!");
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
            final ProgressDialog mDialog = new ProgressDialog(CustomerDetail.this);
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
                            btnSelect.setText(getString(R.string.select));
                            Toast.makeText(CustomerDetail.this, "Uploaded !!!", Toast.LENGTH_SHORT).show();

                            imageFolder.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    //set value for customer if image upload and we can get download link
                                    currentCustomer.setImage(uri.toString());
                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            mDialog.dismiss();
                            Toast.makeText(CustomerDetail.this, "ERROR " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                            mDialog.setMessage(getString(R.string.uploaded) + " " + progress + " %");
                        }
                    });
        }
    }
}
