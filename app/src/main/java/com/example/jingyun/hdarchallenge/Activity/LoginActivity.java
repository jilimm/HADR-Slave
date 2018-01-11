package com.example.jingyun.hdarchallenge.Activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.jingyun.hdarchallenge.Adapter.SelectUserAdapter;
import com.example.jingyun.hdarchallenge.R;

import java.util.ArrayList;
import java.util.List;

public class LoginActivity extends AppCompatActivity {

    private EditText enterPassword;
    private Spinner enterUserID;
    private Button loginBttn;
    private List<String> teamList;
    private SelectUserAdapter userAdapter;
    private String userID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginBttn = (Button) findViewById(R.id.loginBttn);
        enterPassword = (EditText) findViewById(R.id.enterPassword);
        enterUserID = (Spinner) findViewById(R.id.enterUserID);

        teamList = new ArrayList<String>();
        teamList.add("Select Team");//initial dummy entry that will appear as the hint
        teamList.add("Team 1");
        teamList.add("Team 2");
        teamList.add("Team 3");

        //setting up the select user drop down box
        userAdapter = new SelectUserAdapter(this, android.R.layout.simple_spinner_dropdown_item,teamList);
        userAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        enterUserID.setAdapter(userAdapter);
        enterUserID.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Log.i("Login Page",(String) adapterView.getItemAtPosition(i));
                Toast.makeText(LoginActivity.this, "selected: "+adapterView.getItemAtPosition(i).toString(), Toast.LENGTH_SHORT).show();
                userID = adapterView.getItemAtPosition(i).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        loginBttn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (enterPassword.getText().toString().equals("password")) {
                        Toast.makeText(LoginActivity.this, "Welcome " + userID, Toast.LENGTH_SHORT).show();

                        //login successful
                        //takes to the splash screen
                        Intent goToWelcome= new Intent (LoginActivity.this, WelcomeActivity.class);
                        //TODO: use a Broadcast instead of an intent to share data (name of the team)
                        goToWelcome.putExtra("userTeam", userID);
                        startActivity(goToWelcome);

                    } else {
                        Toast.makeText(LoginActivity.this, "Invalid Password", Toast.LENGTH_SHORT).show();
                    }
                }
        });
    }




}
