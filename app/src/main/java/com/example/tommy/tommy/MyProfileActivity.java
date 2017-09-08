package com.example.tommy.tommy;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import static android.R.attr.id;

public class MyProfileActivity extends AppCompatActivity {
    private TextView tvKnow, tvNameContent, tvDateOfBirthContent;
    private String name, userName, dateOfBirth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);
        tvKnow = (TextView) findViewById(R.id.tvKnow);
        tvNameContent = (TextView) findViewById(R.id.tvNameContent);
        tvDateOfBirthContent = (TextView) findViewById(R.id.tvDateOfBirthContent);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Intent intent = getIntent();
        name = intent.getStringExtra("name");
        userName = intent.getStringExtra("userName");
        dateOfBirth = intent.getStringExtra("dateOfBirth");
        tvKnow.setText("Hey " + userName + ", this is what I know about you so far : ");
        Log.d("aaaaaaaaa", name);
        Log.d("aaaaaaaaa", dateOfBirth);
        tvNameContent.setText(name);
        tvDateOfBirthContent.setText(dateOfBirth);
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

    public void selectItem(View view) {
        boolean isSelected = ((CheckBox) view).isChecked();
        int viewId = view.getId();
        switch (viewId) {
            case (R.id.cbGlutenFree):
                Log.d("GLUTEN", String.valueOf(isSelected));
                break;
            case (R.id.cbKosher):
                Log.d("Kosher", String.valueOf(isSelected));
                break;
        }
    }
}
