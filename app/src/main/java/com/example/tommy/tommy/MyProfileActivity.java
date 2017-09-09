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
        tvNameContent.setText(name);
        setName();
        setDateOfBirthText();
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

    public void setName() {
        if (name.equals("")) {
            return;
        }
        String modifiedName = "";
        char first = name.charAt(0);
        first = Character.toUpperCase(first);
        int secIndex = name.indexOf(' ') + 1;
        if (secIndex >= name.length()) {
            return;
        }
        char sec = name.charAt(secIndex);
        sec = Character.toUpperCase(sec);
        modifiedName = first + name.substring(1, secIndex) + sec + name.substring(secIndex + 1, name.length());
        tvNameContent.setText(modifiedName);
    }

    public void setDateOfBirthText() {
        if (dateOfBirth.equals("")) {
            return;
        }
        String reversedDate = "";
        String[] parts = dateOfBirth.split("-");
        if (parts.length > 0) {
            for (int i = parts.length - 1; i >= 0; i--) {
                reversedDate = reversedDate.concat(parts[i] + '/');
            }
            reversedDate = reversedDate.substring(0, reversedDate.length() - 1);
        }
        tvDateOfBirthContent.setText(reversedDate);
    }
}
