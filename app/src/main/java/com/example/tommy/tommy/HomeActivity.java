package com.example.tommy.tommy;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.speech.tts.Voice;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * Home page activity.
 * Transfers to:
 * <ul>
 * <li>About Us page, by clicking the About Us drop down menu item </li>
 * <li>My Profile page, by clicking the My Profile drop down menu item </li>
 * <li>Settings page, by clicking the Settings drop down menu item </li>
 * <li>Login page, by clicking the Log Out drop down menu item </li>
 * </ul>
 */

public class HomeActivity extends AppCompatActivity implements TextToSpeech.OnInitListener {
    private ClientThread clientThread;
    private boolean isMute = false;
    public boolean isFirstRound = true;
    private TextView tvFirstQuestion;
    private TextView tvFirstQuestionResponse;
    private TextView tvSecondQuestion;
    private TextView tvSecondQuestionResponse;
    private TextView tvThirdQuestion;
    private TextView tvThirdQuestionResponse;
    private EditText etUserText;
    private FloatingActionButton bMic;
    private String userText;
    private String username;
    private TextToSpeech textToSpeech = null;
    private Map<String, Voice> nameToVoiceMap = null;
    private Map<Voice, String> voiceToNameMap = null;
    private ArrayList<String> voices = new ArrayList<>();

    public static final String quotationFormat = "\"%s\"";

    public static final int VOICE_RECOGNITION_CODE = 0;
    public static final int TEXT_TO_SPEECH_CODE = 1;
    public static final int MY_PROFILE_CODE = 2;
    public static final int SETTINGS_CODE = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        tvFirstQuestion = (TextView) findViewById(R.id.tvFirstQuestion);
        tvFirstQuestionResponse = (TextView) findViewById(R.id.tvFirstQuestionResponse);
        tvSecondQuestion = (TextView) findViewById(R.id.tvSecondQuestion);
        tvSecondQuestionResponse = (TextView) findViewById(R.id.tvSecondQuestionResponse);
        tvThirdQuestion = (TextView) findViewById(R.id.tvThirdQuestion);
        tvThirdQuestionResponse = (TextView) findViewById(R.id.tvThirdQuestionResponse);
        etUserText = (EditText) findViewById(R.id.etUserText);
        bMic = (FloatingActionButton) findViewById(R.id.bMic);

        Intent intent = getIntent();
        username = intent.getStringExtra("username");

        // Initiate client threads
        clientThread = new ClientThread(HomeActivity.this, username);
        clientThread.start();

        // Init voice maps
        nameToVoiceMap = new HashMap<>();
        voiceToNameMap = new HashMap<>();

        // set main action listener
        etUserText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                userText = etUserText.getText().toString();
                hideKeyboard();
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    clientThread.sendRequest(userText);
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
                    userText = result.get(0);
                    clientThread.sendRequest(userText);
                }
                break;
            case TEXT_TO_SPEECH_CODE:
                if (resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS) {
                    //the user has the necessary data - create the TTS
                    textToSpeech = new TextToSpeech(this, this, "com.google.android.tts");
                } else {
                    //no data - install it now
                    Intent installTTSIntent = new Intent();
                    installTTSIntent.setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
                    startActivity(installTTSIntent);
                }
                break;
            case MY_PROFILE_CODE:
                clientThread.sendMessage("update_user_info");
                break;
            case SETTINGS_CODE:
                if (resultCode == RESULT_OK) {
                    isMute = data.getBooleanExtra("is_mute", false);
                    String selectedVoice = data.getStringExtra("selected_voice");
                    if (textToSpeech != null && nameToVoiceMap != null && nameToVoiceMap.containsKey(selectedVoice)) {
                        textToSpeech.setVoice(nameToVoiceMap.get(selectedVoice));
                    }
                }
                break;
        }
    }

    public void hideKeyboard() {
        etUserText.clearFocus();
        InputMethodManager in = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        in.hideSoftInputFromWindow(etUserText.getWindowToken(), 0);
    }

    public void processResponse(String response) {
        Message msg = new Message(response);
        clearUserText();
        switch (msg.getType()) {
            case QUERY:
                respond(response);
                break;
            case OPEN_MY_PROFILE:
                openMyProfile();
                break;
            case OPEN_SETTINGS:
                openSettings();
                break;
            case LOG_OUT:
                logOut();
                break;
        }
    }

    private String capitalizeFirstLetter(String str) {
        if (str.length() > 0) {
            return str.substring(0, 1).toUpperCase() + str.substring(1);
        }
        return str;
    }

    private String addEndOfSentenceSymbol(String str) {
        if (str.endsWith(".") || str.endsWith("?") || str.endsWith("!")) {
            return str;
        }

        return str + ".";
    }

    private String prepareQueryForDisplay(String str) {
        if (str == null) {
            return "";
        }
        return String.format(quotationFormat, capitalizeFirstLetter(str));
    }

    private String prepareAnswerForDisplay(String str) {
        if (str == null) {
            return "";
        }

        return addEndOfSentenceSymbol(capitalizeFirstLetter(str));
    }

    private void clearUserText() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                etUserText.setText(null);
            }
        });
    }

    private void updateTopTextView() {
        tvFirstQuestion.getLayoutParams().height = RelativeLayout.LayoutParams.WRAP_CONTENT;
        MarginLayoutParams mlp = (MarginLayoutParams) tvFirstQuestion.getLayoutParams();
        int topMargin = getResources().getDimensionPixelSize(R.dimen.top_margin);
        mlp.setMargins(mlp.leftMargin, topMargin, mlp.rightMargin, mlp.bottomMargin);
        tvFirstQuestion.getRootView().requestLayout();
    }

    private void rotateSession(final String response) {
        String firstQuery = tvFirstQuestion.getText().toString();
        String firstAnswer = tvFirstQuestionResponse.getText().toString();
        String secondQuery = tvSecondQuestion.getText().toString();
        String secondAnswer = tvSecondQuestionResponse.getText().toString();

        tvFirstQuestion.setText(prepareQueryForDisplay(userText));
        tvFirstQuestionResponse.setText(prepareAnswerForDisplay(response));
        if (!isFirstRound) {
            tvSecondQuestion.setText(firstQuery);
            tvSecondQuestionResponse.setText(firstAnswer);
            tvThirdQuestion.setText(secondQuery);
            tvThirdQuestionResponse.setText(secondAnswer);
        }
        isFirstRound = false;
    }

    private void respond(final String response) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                updateTopTextView();
                rotateSession(response);
                speakResponse();
            }
        });
    }

    private void speakResponse() {
        if (textToSpeech == null || isMute) {
            return;
        }
        String response = tvFirstQuestionResponse.getText().toString();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            textToSpeech.speak(response, TextToSpeech.QUEUE_FLUSH, null, null);
        } else {
            textToSpeech.speak(response, TextToSpeech.QUEUE_FLUSH, null);
        }
    }

    private void fillVoiceMap(Set<Voice> voices, String prefix) {
        Map<String, Integer> countryCount = new HashMap<>();
        if (voices != null) {
            for (Voice voice : voices) {
                if (voice.getName().startsWith(prefix)) {
                    String country = voice.getLocale().getDisplayCountry();
                    if (!countryCount.containsKey(country)) {
                        countryCount.put(country, 0);
                    }
                    countryCount.put(country, 1 + countryCount.get(country));
                    String name = country + " " + countryCount.get(country);
                    nameToVoiceMap.put(name, voice);
                    voiceToNameMap.put(voice, name);
                }
            }
        }
    }

    @Override
    public void onInit(int status) {
        //check for successful instantiation
        if (status == TextToSpeech.SUCCESS) {
            if (textToSpeech.isLanguageAvailable(Locale.getDefault()) == TextToSpeech.LANG_AVAILABLE) {
                textToSpeech.setLanguage(Locale.getDefault());
            }
            fillVoiceMap(textToSpeech.getVoices(), "en-");
            voices = new ArrayList<>(nameToVoiceMap.keySet());
            Collections.sort(voices);
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

    private void openAboutUs() {
        Intent intent = new Intent(HomeActivity.this, AboutUsActivity.class);
        HomeActivity.this.startActivity(intent);
    }

    private void openMyProfile() {
        Intent intent = new Intent(HomeActivity.this, MyProfileActivity.class);
        intent.putExtra("username", username);
        HomeActivity.this.startActivityForResult(intent, MY_PROFILE_CODE);
    }

    private void openSettings() {
        Intent intent = new Intent(HomeActivity.this, SettingsActivity.class);
        intent.putStringArrayListExtra("voices", voices);
        intent.putExtra("current_voice",
                textToSpeech != null && voiceToNameMap.containsKey(textToSpeech.getVoice()) ? voiceToNameMap.get(textToSpeech.getVoice()) : "");
        intent.putExtra("is_mute", isMute);
        HomeActivity.this.startActivityForResult(intent, SETTINGS_CODE);
    }

    public void logOut() {
        Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
        clientThread.kill();
        HomeActivity.this.startActivity(intent);
    }

    public void onGroupItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.groupAboutUs:
                openAboutUs();
                break;
            case R.id.groupMyProfile:
                openMyProfile();
                break;
            case R.id.groupSettings:
                openSettings();
                break;
            case R.id.groupLogOut:
                logOut();
                break;
        }
    }
}
