package com.example.tommy.tommy;

import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Register page activity.
 * Transfers to:
 * <ul>
 * <li>Login page, by entering valid user information and clicking the register button </li>
 * </ul>
 */

public class RegisterActivity extends AppCompatActivity {
    private static final int nameMinLength = 1;
    private static final int usernameMinLength = 4;
    private static final int passwordMinLength = 4;
    private static final int commonMaxLength = 16;
    private static final String firstNameStr = "First name";
    private static final String lastNameStr = "Last name";
    private static final String UsernameStr = "Username";
    private static final String PasswordStr = "Password";
    private static final String invalidStrLenFormat = "%s must be %d to %d characters long.";
    private static final String invalidStrFirstCharFormat = "%s shouldn't start with a white space.";
    private static final String invalidDateFormat = "Invalid Date, please pick a date in dd/mm/yyyy format.";
    private static final String usernameOccupiedFormat = "%s is already occupied, please pick another username.";

    private EditText etFirstName, etLastName, etUsername, etDateOfBirth, etPassword;
    private String name, username, password, firstName, lastName, dateOfBirth;
    private Button bRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // get Views
        etFirstName = (EditText) findViewById(R.id.etFirstName);
        etLastName = (EditText) findViewById(R.id.etLastName);
        etUsername = (EditText) findViewById(R.id.etUsername);
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
        // set register button listener
        bRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firstName = etFirstName.getText().toString();
                lastName = etLastName.getText().toString();
                username = etUsername.getText().toString();
                password = etPassword.getText().toString();
                dateOfBirth = etDateOfBirth.getText().toString();
                if (!validateRegister()) {
                    registerAlertDialog.show();
                    return;
                }

                Response.Listener<String> responseListener = new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            boolean success = jsonResponse.getBoolean("success");
                            boolean usernameExist = jsonResponse.getBoolean("usernameExist");
                            if (success) {
                                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                                RegisterActivity.this.startActivity(intent);
                            } else if (usernameExist) {
                                etUsername.setError(String.format(usernameOccupiedFormat, username));
                                return;
                            } else {
                                registerAlertDialog.show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                };

                setNameUpperCase();
                RegisterRequest registerRequest = new RegisterRequest(username, password, name, DateDialog.convertDateToSqlFormat(dateOfBirth), responseListener);
                RequestQueue queue = Volley.newRequestQueue(RegisterActivity.this);
                queue.add(registerRequest);
            }
        });
    }

    private void setNameUpperCase() {
        char first = firstName.charAt(0);
        char last = lastName.charAt(0);
        first = Character.toUpperCase(first);
        last = Character.toUpperCase(last);
        name = first + firstName.substring(1, firstName.length()) + ' ' + last + lastName.substring(1, lastName.length());
    }

    /**
     * Terminates the activity when clicking the back button.
     */
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
        if (firstName.length() < nameMinLength || firstName.length() > commonMaxLength) {
            etFirstName.setError(String.format(invalidStrLenFormat, firstNameStr, nameMinLength, commonMaxLength));
            valid = false;
        }
        if (!firstName.isEmpty() && Character.isWhitespace(firstName.charAt(0))) {
            etFirstName.setError(String.format(invalidStrFirstCharFormat, firstNameStr));
            valid = false;
        }
        if (lastName.length() < nameMinLength || lastName.length() > commonMaxLength) {
            etLastName.setError(String.format(invalidStrLenFormat, lastNameStr, nameMinLength, commonMaxLength));
            valid = false;
        }
        if (!lastName.isEmpty() && Character.isWhitespace(lastName.charAt(0))) {
            etLastName.setError(String.format(invalidStrFirstCharFormat, lastNameStr));
            valid = false;
        }
        if (username.length() < usernameMinLength || username.length() > commonMaxLength) {
            etUsername.setError(String.format(invalidStrLenFormat, UsernameStr, usernameMinLength, commonMaxLength));
            valid = false;
        }
        if (dateOfBirth.isEmpty() || !DateDialog.validateDate(dateOfBirth)) {
            etDateOfBirth.setError(invalidDateFormat);
            valid = false;
        }
        if (password.length() < passwordMinLength || password.length() > commonMaxLength) {
            etPassword.setError(String.format(invalidStrLenFormat, PasswordStr, passwordMinLength, commonMaxLength));
            valid = false;
        }

        return valid;
    }
}