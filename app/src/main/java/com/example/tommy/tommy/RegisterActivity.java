package com.example.tommy.tommy;

import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import static android.R.color.white;


public class RegisterActivity extends AppCompatActivity {
    private static final int viewMaxLength = 16;
    private static final String invalidStrLen = "must be none empty and contain at most " + String.valueOf(viewMaxLength) + " characters.";
    private static final String invalidStrFirstChar = "first character must not be white space.";
    private static final String invalidDate = "Please pick a Date.";

    private EditText etFirstName, etLastName, etUserName, etDateOfBirth, etPassword;
    private String name, userName, password, firstName, lastName, dateOfBirth;
    private Button bRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // get Views
        etFirstName = (EditText) findViewById(R.id.etFirstName);
        etLastName = (EditText) findViewById(R.id.etLastName);
        etUserName = (EditText) findViewById(R.id.etUserName);
        etDateOfBirth = (EditText) findViewById(R.id.etDateOfBirth);
        etPassword = (EditText) findViewById(R.id.etPassword);
        bRegister = (Button) findViewById(R.id.bRegister);

        // set date of birth listener
        etDateOfBirth.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    DateDialog dateDialog = new DateDialog(v);

                    FragmentTransaction ft = getFragmentManager().beginTransaction();
                    dateDialog.show(ft, "DatePicker");
                }
            }
        });

        final AlertDialog registerAlertDialog = new AlertDialog.Builder(RegisterActivity.this)
                .setMessage("Register Failed")
                .setNegativeButton("Retry", null)
                .create();

        bRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                firstName = etFirstName.getText().toString();
                lastName = etLastName.getText().toString();
                userName = etUserName.getText().toString();
                password = etPassword.getText().toString();
                dateOfBirth = etDateOfBirth.getText().toString();
                if (!validateRegister()) {
                    registerAlertDialog.show();
                    return;
                }

                Response.Listener<String> responseListener = new Response.Listener<String>(){

                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            boolean success = jsonResponse.getBoolean("success");
                            if (success){
                                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                                RegisterActivity.this.startActivity(intent);
                            }
                            else{
                                registerAlertDialog.show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                };
                name = firstName.concat(lastName);
                RegisterRequest registerRequest = new RegisterRequest(userName, password, name, dateOfBirth, responseListener);
                RequestQueue queue = Volley.newRequestQueue(RegisterActivity.this);
                queue.add(registerRequest);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private boolean validateRegister() {
        boolean valid = true;
        if (firstName.length() > viewMaxLength || firstName.isEmpty()) {
            etFirstName.setError("First Name " + invalidStrLen);
            valid = false;
        }
        if (!firstName.isEmpty() && Character.isWhitespace(firstName.charAt(0))) {
            etFirstName.setError("First Name " + invalidStrFirstChar);
            valid = false;
        }
        if (lastName.length() > viewMaxLength || lastName.isEmpty()) {
            etLastName.setError("Last Name " + invalidStrLen);
            valid = false;
        }
        if (!firstName.isEmpty() && Character.isWhitespace(lastName.charAt(0))) {
            etFirstName.setError("Last Name " + invalidStrFirstChar);
            valid = false;
        }
        if (userName.length() > viewMaxLength || userName.isEmpty()) {
            etUserName.setError("userName " + invalidStrLen);
            valid = false;
        }
        if (dateOfBirth.isEmpty()) {
            etDateOfBirth.setError(invalidDate);
            valid = false;
        }
        Log.d("password: ", password);
        if (password.length() > viewMaxLength || password.isEmpty()) {
            Log.d("password: ", password);
            etFirstName.setError("Password " + invalidStrLen);
            valid = false;
        }

        return valid;
    }
}
