package com.example.shivamthelordalmighty.textmessageapp;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    SmsManager sm;
    SmsMessage[] messages;
    Object[] pdus;
    TextView screen, stage;
    String income = "";
    String phoneNumber = "";
    String outcome = "";
    int currentState = 1;
    boolean confused = false;

    String[] introPhrases = {"Hi", "Hello", "How is it going?", "What's up?"};
    String[] breakupBeginningPhrases = {"We need to talk", "I've been thinking about us", "We should talk about our relationship", "I've been having some thoughts about us"};
    String[] breakupMiddlePhrases = {"We need to take a break", "I don't think we should continue this relationship", "I'm not happy", "I don't love you anymore"};
    String[] breakupEndPhrases = {"I am not attracted to you", "It's just not the same", "I hate you", "Things just aren't the same anymore"};
    String[] endPhrases = {"I never want to see you again", "Goodbye", "Don't contact me", "Leave me alone"};
    String[] confusedPhrases = {"I dont understand", "What do you mean", "What is good with you", "Are you ok in the head?"};


    @Override
    protected void onResume(){
        super.onResume();
        Log.d("TAG", "resumed");
        sm = SmsManager.getDefault();
        BroadcastReceiver br = new BroadcastReceiver() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.d("TAG", "Recieved");
                Bundle b = intent.getExtras();
                Log.d("TAG", "1");
                pdus = (Object[]) b.get("pdus");
                Log.d("TAG", "2");
                messages = new SmsMessage[pdus.length];
                Log.d("TAG", "3");
                for (int i = 0; i < pdus.length; i++) {
                    messages[i] = SmsMessage.createFromPdu((byte[]) pdus[i], b.getString("format"));
                    System.out.println(messages[i]);
                    income += messages[i].getDisplayMessageBody();
                }
                Log.d("TAG", "4");

                screen.setText(income);
                income = "";
                phoneNumber = messages[0].getDisplayOriginatingAddress();

                sm = SmsManager.getDefault();



                if(currentState != 5 && income.contains("bye")) {
                    confused = true;
                }
                else if(currentState != 1 && income.contains("hello")) {
                    confused = true;
                }

                switch(currentState) {
                    case 1:
                        outcome = introPhrases[(int)(Math.random()*introPhrases.length)];
                        stage.setText("Stage: Greeting");
                        currentState++;

                        break;
                    case 2:
                        outcome = breakupBeginningPhrases[(int)(Math.random()*breakupBeginningPhrases.length)];
                        stage.setText("Stage: Begin Breakup");
                        currentState++;
                        break;
                    case 3:
                        outcome = breakupMiddlePhrases[(int)(Math.random()*breakupMiddlePhrases.length)];
                        stage.setText("Stage: Middle of Breakup");
                        currentState++;
                        break;
                    case 4:
                        outcome = breakupEndPhrases[(int)(Math.random()*breakupEndPhrases.length)];
                        stage.setText("Stage: End of breakup");
                        currentState++;
                        break;
                    case 5:
                        outcome = endPhrases[(int)(Math.random()*endPhrases.length)];
                        stage.setText("Stage: Goodbyes");
                        currentState++;
                        break;
                }

                if(confused){
                    outcome = confusedPhrases[(int)(Math.random() * confusedPhrases.length)];
                    confused = false;
                }

                if(currentState == 6){
                    outcome = "STOP TEXTING ME. I DON'T LOVE YOU";
                }

                sm = SmsManager.getDefault();

                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Log.d("TAG", "5");
                        sm.sendTextMessage(phoneNumber, null, outcome, null, null);
                    }
                },4000);

            }
        };


        registerReceiver(br, new IntentFilter("android.provider.Telephony.SMS_RECEIVED"));





    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        screen = findViewById(R.id.screen);
        screen.setText("");
        stage= findViewById(R.id.state);
        stage.setText("");


        while (ActivityCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, 123);
        }


        while (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.RECEIVE_SMS},123);
        }

        while (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_SMS)!= PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_SMS}, 123);
        }


    }

}
