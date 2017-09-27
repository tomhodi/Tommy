package com.example.tommy.tommy;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * My Profile page activity.
 * Transfers to:
 * <ul>
 * <li>Home page, by clicking the back button </li>
 * </ul>
 */

public class MyProfileActivity extends AppCompatActivity {
    private static final String falseStr = "0";
    private static final String trueStr = "1";

    private TextView tvKnow, tvNameContent, tvDateOfBirthContent;
    private CheckBox cbGlutenFree, cbKosher, cbVegetarian;
    private String name, username, dateOfBirth, kosher, glutenFree, vegetarian;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);
        // get Views
        tvKnow = (TextView) findViewById(R.id.tvKnow);
        tvNameContent = (TextView) findViewById(R.id.tvNameContent);
        tvDateOfBirthContent = (TextView) findViewById(R.id.tvDateOfBirthContent);
        cbKosher = (CheckBox) findViewById(R.id.cbKosher);
        cbGlutenFree = (CheckBox) findViewById(R.id.cbGlutenFree);
        cbVegetarian = (CheckBox) findViewById(R.id.cbVegetarian);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Intent intent = getIntent();
        username = intent.getStringExtra("username");
        tvKnow.setText("Hey " + username + ", here is what I know about you so far: ");
        // connect to SQL and extract user information
        extractUserData();
    }

    private void populateProfile() {
        tvNameContent.setText(name);
        tvDateOfBirthContent.setText(dateOfBirth);
        if (kosher.equals(trueStr)) {
            cbKosher.setChecked(true);
        }
        if (glutenFree.equals(trueStr)) {
            cbGlutenFree.setChecked(true);
        }
        if (vegetarian.equals(trueStr)) {
            cbVegetarian.setChecked(true);
        }
    }

    /**
     * Updates the user Preferences if needed and terminates the activity when
     * clicking the back button.
     */
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

    /**
     * Updates kosher, gluten_free and vegetarian values when their buttons are pressed.
     */
    public void selectItem(View view) {
        boolean isSelected = ((CheckBox) view).isChecked();
        String boolStr = isSelected ? trueStr : falseStr;
        int viewId = view.getId();
        switch (viewId) {
            case (R.id.cbKosher):
                kosher = boolStr;
                break;
            case (R.id.cbGlutenFree):
                glutenFree = boolStr;
                break;
            case (R.id.cbVegetarian):
                vegetarian = boolStr;
                break;
        }
    }

    private void updatePref() {
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
        updateRequest updateRequest = new updateRequest(username, kosher, glutenFree, vegetarian, responseListener);
        RequestQueue queue = Volley.newRequestQueue(MyProfileActivity.this);
        queue.add(updateRequest);
    }

    private void extractUserData() {
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
                        vegetarian = jsonResponse.getString("vegetarian");
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
