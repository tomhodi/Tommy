package com.example.tommy.tommy;

import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class RegisterActivity extends AppCompatActivity {
    private EditText etName, etUsername, etDateOfBirth, etPassword;
    private String username, password, name, dateOfBirth;
    Button bRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        etName = (EditText) findViewById(R.id.etName);
        etUsername = (EditText) findViewById(R.id.etUsername);
        etDateOfBirth = (EditText) findViewById(R.id.etDateOfBirth);
        etPassword = (EditText) findViewById(R.id.etPassword);
        bRegister = (Button) findViewById(R.id.bRegister);

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
                username = etUsername.getText().toString();
                password = etPassword.getText().toString();
                name = etName.getText().toString();
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

                RegisterRequest registerRequest = new RegisterRequest(username, password, name, dateOfBirth, responseListener);
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
        if (name.isEmpty()) {
            etName.setError("Please enter a valid name");
            valid = false;
        }
        if (username.isEmpty()) {
            etUsername.setError("Please enter a valid username");
            valid = false;
        }
        if (dateOfBirth.isEmpty()) {
            etDateOfBirth.setError("Please enter a valid date of birth");
            valid = false;
        }
        if (password.isEmpty()) {
            etPassword.setError("Please enter a valid password");
            valid = false;
        }

        return valid;
    }
}
