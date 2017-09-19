package com.example.tommy.tommy;

import android.app.AlertDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import org.json.JSONException;
import org.json.JSONObject;

public class MyProfileActivity extends AppCompatActivity {
    private TextView tvKnow, tvNameContent, tvDateOfBirthContent;
    private CheckBox cbGlutenFree, cbKosher;
    private String name, username, dateOfBirth, kosher, glutenFree;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);
        // get Views
        tvKnow = (TextView) findViewById(R.id.tvKnow);
        tvNameContent = (TextView) findViewById(R.id.tvNameContent);
        tvDateOfBirthContent = (TextView) findViewById(R.id.tvDateOfBirthContent);
        cbGlutenFree = (CheckBox) findViewById(R.id.cbGlutenFree);
        cbKosher = (CheckBox) findViewById(R.id.cbKosher);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Intent intent = getIntent();
        username = intent.getStringExtra("username");
        tvKnow.setText("Hey " + username + ", here is what I know about you so far: ");
        // connect to SQL and extract user information
        extractUserData();
    }

    public void populateProfile() {
        tvNameContent.setText(name);
        tvDateOfBirthContent.setText(dateOfBirth);
        if (kosher.equals("1")) {
            cbKosher.setChecked(true);
        }
        if (glutenFree.equals("1")) {
            cbGlutenFree.setChecked(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                updatePref();
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void selectItem(View view) {
        boolean isSelected = ((CheckBox) view).isChecked();
        int viewId = view.getId();
        switch (viewId) {
            case (R.id.cbKosher):
                if (isSelected) {
                    kosher = "1";
                } else {
                    kosher = "0";
                }
                break;

            case (R.id.cbGlutenFree):
                if (isSelected) {
                    glutenFree = "1";
                } else {
                    glutenFree = "0";
                }
                break;
        }
    }

    public void updatePref() {
        Response.Listener<String> responseListener = new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    boolean success = jsonResponse.getBoolean("success");
                    if (success) {
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        updateRequest updateRequest = new updateRequest(username, kosher, glutenFree, responseListener);
        RequestQueue queue = Volley.newRequestQueue(MyProfileActivity.this);
        queue.add(updateRequest);
    }

    public void extractUserData() {
        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    boolean success = jsonResponse.getBoolean("success");
                    if (success) {
                        name = jsonResponse.getString("name");
                        dateOfBirth = DateDialog.convertDateFromSqlFormat(jsonResponse.getString("date_of_birth"));
                        kosher = jsonResponse.getString("kosher");
                        glutenFree = jsonResponse.getString("gluten_free");
                        populateProfile();
                    } else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(MyProfileActivity.this);
                        builder.setMessage("User information unavailable")
                                .setNegativeButton("Close", null)
                                .create()
                                .show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        ExtractRequest extractRequest = new ExtractRequest(username, responseListener);
        RequestQueue queue = Volley.newRequestQueue(MyProfileActivity.this);
        queue.add(extractRequest);
    }
}
