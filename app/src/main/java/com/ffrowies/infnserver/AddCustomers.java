package com.ffrowies.infnserver;

import android.content.Intent;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.ffrowies.infnserver.Models.User;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.regex.Pattern;

public class AddCustomers extends AppCompatActivity {

    private TextInputLayout tilName, tilAddress, tilEmail, tilPhone;
    private EditText edtName, edtAddress, edtEmail, edtPhone;

    //Firebase
    FirebaseDatabase db;
    DatabaseReference customers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_customers);

        //Init Firebase
        db = FirebaseDatabase.getInstance();
        customers = db.getReference("User");

        // til References
        tilName = (TextInputLayout) findViewById(R.id.tilName);
        tilAddress = (TextInputLayout) findViewById(R.id.tilAddress);
        tilEmail = (TextInputLayout) findViewById(R.id.tilEmail);
        tilPhone = (TextInputLayout) findViewById(R.id.tilPhone);

        // btn Reference
        Button btnAdd = (Button) findViewById(R.id.btnAdd);

        // edt References
        edtName = (EditText) findViewById(R.id.edtName);
        edtAddress = (EditText) findViewById(R.id.edtAddress);
        edtEmail = (EditText) findViewById(R.id.edtEmail);
        edtPhone = (EditText) findViewById(R.id.edtPhone);

        edtName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                tilName.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        edtAddress.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                tilAddress.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        edtEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                isValidEmail(String.valueOf(s));
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        edtPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                isValidPhone(String.valueOf(s));
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private boolean isValidName(String name) {
        Pattern pattern = Pattern.compile("^[a-zA-Z ]+$");
        if (!pattern.matcher(name).matches() || name.length() > 30) {
            tilName.setError("Invalid name");
            return false;
        } else {
            tilName.setError(null);
        }

        return true;
    }

    private boolean isValidAddress(String address) {
        Pattern pattern = Pattern.compile("^[0-9a-zA-Z,. ]+$");
        if (!pattern.matcher(address).matches() || address.length() > 40) {
            tilAddress.setError("Invalid address");
            return false;
        } else {
            tilAddress.setError(null);
        }

        return true;
    }

    private boolean isValidEmail(String email) {
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            tilEmail.setError("Invalid email");
            return false;
        } else {
            tilEmail.setError(null);
        }

        return true;
    }

    private boolean isValidPhone(String phone) {
        if (!Patterns.PHONE.matcher(phone).matches()) {
            tilPhone.setError("Invalid phone number");
            return false;
        } else {
            tilPhone.setError(null);
        }

        return true;
    }

    public void addCustomer(View view) {
        String name = edtName.getText().toString();
        String address = edtAddress.getText().toString();
        String email = edtEmail.getText().toString();
        String phone = edtPhone.getText().toString();

        boolean testName = isValidName(name);
        boolean testAddress = isValidAddress(address);
        boolean testEmail = isValidEmail(email);
        boolean testPhone = isValidPhone(phone);

        if (testName && testAddress && testEmail && testPhone) {
            User newCustomer = new User(name, phone, phone, email, address, "false");
            customers.push().setValue(newCustomer);
            returnToCustomersList();
        }
    }

    public void cancelAddCustomer(View view) {
        returnToCustomersList();
    }

    @Override
    public void onBackPressed() {
        returnToCustomersList();
    }

    private void returnToCustomersList() {
        Intent intent = new Intent(this, CustomersList.class)
                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        startActivity(intent);
    }
}
