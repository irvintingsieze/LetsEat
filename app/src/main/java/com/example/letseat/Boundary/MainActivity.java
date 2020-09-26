package com.example.letseat.Boundary;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import com.example.letseat.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONObject;
/*
UI which enables the user to register or login based on his/her choice
 */
public class MainActivity extends AppCompatActivity  {
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();

       Button mRegisterBtn = (Button)findViewById(R.id.register_btn);
       Button mLoginBtn = (Button)findViewById(R.id.login_btn);

        mRegisterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //go to register activity
                startActivity(new Intent(MainActivity.this, RegisterUI.class));
                finish();
            }
        });

        //Click on login
        mLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, LoginUI.class));
                finish();
            }
        });
    }


    @Override
    protected void onStart() {
        //check at start of app
        super.onStart();
    }


}
