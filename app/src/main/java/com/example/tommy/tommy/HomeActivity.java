package com.example.tommy.tommy;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;

import static android.R.attr.host;
import static android.R.attr.name;
import static android.R.attr.port;
import static com.example.tommy.tommy.R.id.etUserText;

public class HomeActivity extends AppCompatActivity {
    final String host = "192.168.231.1";
    final int port = 2345;
    TextView tvResponse;
    EditText etUserText;
    String userText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        tvResponse = (TextView) findViewById(R.id.tvResponse);
        etUserText = (EditText) findViewById(R.id.etUserText);

        Intent intent = getIntent();
        final String username = intent.getStringExtra("username");
        final String name = intent.getStringExtra("name");
        final String dateOfBirth = intent.getStringExtra("date_of_birth");

        etUserText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                userText = etUserText.getText().toString();
                clearUserText();
                if (actionId == EditorInfo.IME_ACTION_SEARCH && !userText.isEmpty()) {
                    sendRequest(username);
                    return true;
                }
                return false;
            }
        });
    }

    public void clearUserText() {
        etUserText.setText("");
        etUserText.clearFocus();
        InputMethodManager in = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        in.hideSoftInputFromWindow(etUserText.getWindowToken(), 0);
    }

    public void sendRequest(final String username) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Socket clientSocket = new Socket(host, port);
                    OutputStream outToServer = clientSocket.getOutputStream();
                    DataOutputStream out = new DataOutputStream(outToServer);
                    out.writeUTF("Query[" + username + "]: " + userText);
                    InputStream inFromServer = clientSocket.getInputStream();
                    DataInputStream in = new DataInputStream(inFromServer);
                    final String response = in.readUTF();
                    clientSocket.close();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            tvResponse.setText(response);
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
