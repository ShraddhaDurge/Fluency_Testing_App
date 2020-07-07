package com.example.fluencystage;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import java.util.ArrayList;
import java.util.List;


//CHAT BUBBLE// http://www.devexchanges.info/2016/03/design-chat-bubble-ui-in-android.html
//9PATCH// https://romannurik.github.io/AndroidAssetStudio/nine-patches.html#source.type=image&sourceDensity=480&name=chat
//GIF// https://android.developreference.com/article/22656818/I+want+to+play+gif+animation+on+touch+event

public class MainActivity extends AppCompatActivity implements RecognitionListener {

    private ListView listView;
    private TextView clk;
    private List<ChatMessage> chatMessages;
    private ArrayAdapter<ChatMessage> adapter;
    private SpeechRecognizer speech;
    private static final int REQUEST_RECORD_PERMISSION = 100;
    private Intent recognizerIntent;
    boolean listening = false;
    private String LOG_TAG = "VoiceRecognitionActivity";
    private boolean stopSpeech = false;
    private ArrayList<String> words;
    private ArrayList<String> allWords;
    private ImageView processing;
    private ImageButton speakBtn;
    private TextView buttonText;
    char letter;
    int correctWords;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();

        askPermission();

        chatMessages = new ArrayList<>();
        clk = findViewById(R.id.clock);
        words = new ArrayList<>();
        allWords = new ArrayList<>();
        listView =  findViewById(R.id.list_msg);
        processing = findViewById(R.id.processing);
        speakBtn = findViewById(R.id.micbtn);
        buttonText = findViewById(R.id.micText);
        setSpeechRecognition();

        //set ListView as adapter first
        adapter = new TextAdapter(this, R.layout.leftcloud, chatMessages);
        listView.setAdapter(adapter);

        ChatMessage text1 = new ChatMessage(selectLetter(),false);
        ChatMessage text2 = new ChatMessage(getResources().getString(R.string.blueCloudText2),false);
        chatMessages.add(text1);
        chatMessages.add(text2);
        adapter.notifyDataSetChanged();

        setTimer(); //60sec timer

        final boolean[] pressed = {false};

        //event for button SPEAK button
        speakBtn.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if(!stopSpeech) {
                    switch (motionEvent.getAction()) {
                        case MotionEvent.ACTION_UP:
                            speech.stopListening();
                            pressed[0] = false;
                            if(words.isEmpty()){
                                buttonText.setText(getResources().getString(R.string.buttonText1));
                                onStop();
                            }
                            words = new ArrayList<>();
                            break;

                        case MotionEvent.ACTION_DOWN:
                            //if(!listening) {
                                listening = true;
                                speech.startListening(recognizerIntent);
                           // }
                                buttonText.setText(getResources().getString(R.string.buttonText2));
                                pressed[0] = true;
                            break;
                    }
                } else {
                    Toast.makeText(MainActivity.this, "Time is over...", Toast
                            .LENGTH_SHORT).show();
                }

                return false;
            }


        });


    }

    private String selectLetter(){
       String s = "";
       int i = (int)((Math.random())*100) % 3;

       switch (i) {
           case 0 : s = getResources().getString(R.string.blueCloudText1A);
           letter = 'A';
            break;

           case 1:  s = getResources().getString(R.string.blueCloudText1F);
               letter = 'F';
               break;

           case 2:  s = getResources().getString(R.string.blueCloudText1S);
               letter = 'S';
               break;
       }
       return s;
    }

    void setSpeechRecognition(){
        recognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE,
                "en");
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);

        recognizerIntent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 2);

        speech = SpeechRecognizer.createSpeechRecognizer(this);
        Log.i(LOG_TAG, "isRecognitionAvailable: " + SpeechRecognizer.isRecognitionAvailable(this));
        speech.setRecognitionListener(this);

    }


    //wil set the words spoken by a user in the cloud boxes
    void setWords(){
        for(int i=0; i<words.size() ; i++) {
            ChatMessage word = new ChatMessage(words.get(i),true);
            chatMessages.add(word);
        }
        adapter.notifyDataSetChanged();
    }


    @Override
    public void onResults(Bundle results) {

        Log.i(LOG_TAG, "onResults");

        //getting all the matches
        ArrayList<String> matches = results
                .getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);

        String[] s = matches.get(0).trim().split(" ");

        /*ChatMessage word = new ChatMessage(matches.get(0),true);
        chatMessages.add(word);
        adapter.notifyDataSetChanged();*/ // to display all words in one cloud

        listening = false;
        buttonText.setText(getResources().getString(R.string.buttonText1));

        for(int i=0; i<s.length; i++){
            if(s[i] != " ") {
                words.add(s[i]);
                s[i].toUpperCase();
                if(s[i].charAt(0) ==  letter){
                    if(!allWords.contains(s[i])) {
                        correctWords++;
                    }
                }
                allWords.add(s[i]);
            }
        }

        setWords();
        changeToImage();
    }

    private void changeToGif(){
        /*from raw folder*/
        Glide.with(this).load(R.raw.listening).into(processing);
    }
    private void changeToImage() {
        processing.setImageDrawable(getResources().getDrawable(R.drawable.typing));
    }
    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public void onReadyForSpeech(Bundle bundle) {
        Log.i(LOG_TAG, "onReadyForSpeech");
    }

    @Override
    public void onBeginningOfSpeech() {
        Log.i(LOG_TAG, "onBeginningOfSpeech");
        changeToGif();
        //Toast.makeText(MainActivity.this, "onBeginningOfSpeech", Toast
          //         .LENGTH_SHORT).show();
    }

    @Override
    public void onRmsChanged(float rmsdB) {
        Log.i(LOG_TAG, "onRmsChanged: " + rmsdB);

    }

    @Override
    public void onBufferReceived(byte[] bytes) {
        Log.i(LOG_TAG, "onBufferReceived: " + bytes);
    }

    @Override
    public void onEndOfSpeech() {
        Log.i(LOG_TAG, "onEndOfSpeech");

    }

    @Override
    public void onError(int errorCode) {
        String errorMessage = getErrorText(errorCode);
        Log.d(LOG_TAG, "FAILED " + errorMessage);
        switch (errorMessage){
            case "No match" : listening = false;
                changeToImage();
                buttonText.setText(getResources().getString(R.string.buttonText1));
                break;
        }
    }

    @Override
    public void onPartialResults(Bundle results) {
        Log.i(LOG_TAG, "onPartialResults");
        ArrayList<String> matches = results
                .getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);

        Log.i(LOG_TAG, "onPartialResults="+matches.get(0));

    }

    @Override
    public void onEvent(int i, Bundle bundle) {
        Log.i(LOG_TAG, "onEvent");

    }

    public String getErrorText(int errorCode) {
        String message;
        switch (errorCode) {
            case SpeechRecognizer.ERROR_AUDIO:
                message = "Audio recording error";
                break;
            case SpeechRecognizer.ERROR_CLIENT:
                message = "Client side error";
                break;
            case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS:
                message = "Insufficient permissions";
                break;
            case SpeechRecognizer.ERROR_NETWORK:
                message = "Network error";
                break;
            case SpeechRecognizer.ERROR_NETWORK_TIMEOUT:
                message = "Network timeout";
                break;
            case SpeechRecognizer.ERROR_NO_MATCH:
                message = "No match";
                break;
            case SpeechRecognizer.ERROR_RECOGNIZER_BUSY:
                message = "RecognitionService busy";
                break;
            case SpeechRecognizer.ERROR_SERVER:
                message = "error from server";
                break;
            case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
                message = "No speech input";
                break;
            default:
                message = "Didn't understand, please try again.";
                break;
        }
        return message;
    }

    //to check/ask recording permission
    void askPermission(){
        ActivityCompat.requestPermissions
                (MainActivity.this,
                        new String[]{Manifest.permission.RECORD_AUDIO},
                        REQUEST_RECORD_PERMISSION);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_RECORD_PERMISSION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //Toast.makeText(MainActivity.this, "Permission Granted!", Toast
                         //   .LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "Permission Denied!", Toast
                            .LENGTH_SHORT).show();
                }
        }
    }

    void setTimer(){
        new CountDownTimer(59000, 1000) {

            public void onTick(long millisUntilFinished) {
                clk.setText("00:" + millisUntilFinished / 1000);
            }

            public void onFinish() {
                clk.setTextSize(30);
                clk.setText("Total correct words : " + Integer.toString(correctWords));

                onStop();
                speech.destroy();
                stopSpeech = true;

                changeToImage();
                buttonText.setText(getResources().getString(R.string.buttonText1));

            }
        }.start();
    }
}
