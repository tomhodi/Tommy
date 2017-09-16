package com.example.tommy.tommy;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import static android.R.attr.id;
//import static com.example.tommy.tommy.R.id.cbKosher;

public class MyProfileActivity extends AppCompatActivity {
    private TextView tvKnow, tvNameContent, tvDateOfBirthContent;
    private CheckBox cbGlutenFree, cbKosher;
    private String name, userName, dateOfBirth, kosher, glutenFree, password;

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

        // get variables from Home activity
        name = intent.getStringExtra("name");
        userName = intent.getStringExtra("userName");
        password = intent.getStringExtra("password");
        dateOfBirth = intent.getStringExtra("dateOfBirth");
        kosher = intent.getStringExtra("kosher");
        glutenFree = intent.getStringExtra("glutenFree");

        // set user's data to the different Views
        tvKnow.setText("Hey " + userName + ", here is what I know about you so far: ");
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

        updateRequest updateRequest = new updateRequest(userName, password, kosher, glutenFree, responseListener);
        RequestQueue queue = Volley.newRequestQueue(MyProfileActivity.this);

        queue.add(updateRequest);
    }
}
