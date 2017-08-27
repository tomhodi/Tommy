package com.example.tommy.tommy;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Locale;

public class HomeActivity extends AppCompatActivity implements TextToSpeech.OnInitListener {
    private final String host = "192.168.231.1";
    private final int port = 2345;
    private TextView tvResponse;
    private EditText etUserText;
    private FloatingActionButton bMic;
    private String userText;
    private String username;
    private TextToSpeech textToSpeech = null;

    public static final int VOICE_RECOGNITION_CODE = 0;
    public static final int TEXT_TO_SPEECH_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        tvResponse = (TextView) findViewById(R.id.tvResponse);
        etUserText = (EditText) findViewById(R.id.etUserText);
        bMic = (FloatingActionButton) findViewById(R.id.bMic);

        Intent intent = getIntent();
        username = intent.getStringExtra("username");
        final String name = intent.getStringExtra("name");
        final String dateOfBirth = intent.getStringExtra("date_of_birth");

        // set main action listener
        etUserText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                userText = etUserText.getText().toString();
                hideKeyboard();
                if (actionId == EditorInfo.IME_ACTION_SEARCH && !userText.isEmpty()) {
                    sendRequest();
                    return true;
                }
                return false;
            }
        });

        bMic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());

                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(intent, VOICE_RECOGNITION_CODE);
                } else {
                    Toast.makeText(HomeActivity.this, "Your device doesn't support speech input", Toast.LENGTH_SHORT).show();
                }
            }
        });

        Intent checkTTSIntent = new Intent();
        checkTTSIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
        startActivityForResult(checkTTSIntent, TEXT_TO_SPEECH_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case VOICE_RECOGNITION_CODE:
                if (resultCode == RESULT_OK && data != null) {
                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    etUserText.setText(result.get(0));
                    sendRequest();
                }
                break;
            case TEXT_TO_SPEECH_CODE:
                if (resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS) {
                    //the user has the necessary data - create the TTS
                    textToSpeech = new TextToSpeech(this, this);
                } else {
                    //no data - install it now
                    Intent installTTSIntent = new Intent();
                    installTTSIntent.setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
                    startActivity(installTTSIntent);
                }
                break;
        }
    }

    public void hideKeyboard() {
        // etUserText.setText("");
        etUserText.clearFocus();
        InputMethodManager in = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        in.hideSoftInputFromWindow(etUserText.getWindowToken(), 0);
    }

    public void sendRequest() {
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
                            speakResponse();
                            etUserText.setText("");
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void speakResponse() {
        if (textToSpeech == null) {
            return;
        }
        String response = tvResponse.getText().toString();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            textToSpeech.speak(response, TextToSpeech.QUEUE_FLUSH, null, null);
        } else {
            textToSpeech.speak(response, TextToSpeech.QUEUE_FLUSH, null);
        }
    }

    @Override
    public void onInit(int status) {
        //check for successful instantiation
        if (status == TextToSpeech.SUCCESS) {
            if (textToSpeech.isLanguageAvailable(Locale.getDefault()) == TextToSpeech.LANG_AVAILABLE)
                textToSpeech.setLanguage(Locale.getDefault());
        } else if (status == TextToSpeech.ERROR) {
            Toast.makeText(this, "Sorry! Text To Speech failed...", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.my_menu, menu);
        return true;
    }

    public void onGroupItemClick(MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {
            case R.id.groupAboutUs:
                intent = new Intent(HomeActivity.this, AboutUsActivity.class);
                break;
            case R.id.groupMyProfile:
                intent = new Intent(HomeActivity.this, MyProfileActivity.class);
                break;
            case R.id.groupSettings:
                intent = new Intent(HomeActivity.this, SettingsActivity.class);
                break;
            case R.id.groupLogOut:
                intent = new Intent(HomeActivity.this, LoginActivity.class);
                break;
            default:
                return;
        }

        HomeActivity.this.startActivity(intent);
    }
}
