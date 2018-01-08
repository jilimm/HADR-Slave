package com.example.jingyun.hdarchallenge.Activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.jingyun.hdarchallenge.R;

public class LoginActivity extends AppCompatActivity {

    private EditText enterPassword;
    private EditText enterUserID;
    private Button loginBttn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginBttn = (Button) findViewById(R.id.loginBttn);
        enterPassword = (EditText) findViewById(R.id.enterPassword);
        enterUserID = (EditText) findViewById(R.id.enterUserID);



        loginBttn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final String userID = enterUserID.getText().toString();
                if (enterUserID.getText().toString().equals("")){

                    Toast.makeText(LoginActivity.this, "Please enter valid User ID", Toast.LENGTH_SHORT).show();
                }

                else {
                    if (enterPassword.getText().toString().equals("password")) {
                        Toast.makeText(LoginActivity.this, "Welcome " + enterUserID.getText(), Toast.LENGTH_SHORT).show();

                        //login successful
                        //takes to the splash screen
                        Intent goToWelcome= new Intent (LoginActivity.this, WelcomeActivity.class);
                        goToWelcome.putExtra("userTeam", userID);
                        startActivity(goToWelcome);


                    } else {
                        Toast.makeText(LoginActivity.this, "Invalid Password", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }




}
