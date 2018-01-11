package com.example.jingyun.hdarchallenge.Activity;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jingyun.hdarchallenge.R;

public class WelcomeActivity extends AppCompatActivity {
    private static int SPLASH_TIMEOUT = 1000; //the splash welcome screen would only be on for 3 secons (3000miliseconds)


    private TextView userName;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        userName = (TextView)findViewById(R.id.welcomeUserID);

        Intent intent = getIntent();
        final String userID = intent.getStringExtra("userTeam");
        userName.setText(userID);



        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                //this method is execute once the timer is over
                //start your main activity
                Intent intent1 = new Intent(WelcomeActivity.this, MainActivity.class);
                intent1.putExtra("userID",userID);
                startActivity(intent1);


                //close this activity
                finish();
            }

        }, SPLASH_TIMEOUT);

    }
}
