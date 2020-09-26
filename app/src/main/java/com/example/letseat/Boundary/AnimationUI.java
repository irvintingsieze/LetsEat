package com.example.letseat.Boundary;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.os.Bundle;

import com.airbnb.lottie.LottieAnimationView;
import com.example.letseat.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/*
This defines the splash screen at start of application.
Also checks if user is already logged in to redirect them accordingly
 */

public class AnimationUI extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_animation_ui);
        getSupportActionBar().hide();

        LottieAnimationView lottieAnimationView = findViewById(R.id.foodAnimation);
        lottieAnimationView.addAnimatorListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                checkUserStatus();
            }
        });
    }
    private void checkUserStatus(){
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user!=null){
            startActivity(new Intent(AnimationUI.this, NavigationDrawerUI.class));
        }else{
            startActivity( new Intent(AnimationUI.this, MainActivity.class));
        }
    }
}
