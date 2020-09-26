package com.example.letseat.Boundary;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.letseat.Constants;
import com.example.letseat.Control.SystemControl;
import com.example.letseat.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

/*
UI for user to register for new account
 */
public class RegisterUI extends AppCompatActivity {

    ProgressDialog pd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_ui);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Register");

        final EditText mUsername = findViewById(R.id.usernameET);
        final EditText mEmailEt = findViewById(R.id.emailET);
        final EditText mPasswordEt = findViewById(R.id.passwordET);
        Button mRegisterBtn = (Button)findViewById(R.id.registerBtn);
        final EditText mCfmPasswordEt = findViewById(R.id.cfmPasswordET);
        TextView mHaveAccountTV = findViewById(R.id.haveAccountTV);

        pd = new ProgressDialog(this);
        pd.setMessage("Registering User...");
        final FirebaseAuth mAuth = FirebaseAuth.getInstance();

        mHaveAccountTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegisterUI.this, LoginUI.class));
            }
        });

        mRegisterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = mUsername.getText().toString();
                String email = mEmailEt.getText().toString().trim();
                String password = mPasswordEt.getText().toString().trim();
                String cfmPassword = mCfmPasswordEt.getText().toString().trim();
                /*
                If return 1, at least 1 field is empty.
                If return 2, email is in an incorrect format
                If return 3, password is less than 6 characters
                If return 4, password fields do not match
                If return 5, password does not fulfil requirements: alphanumeric and special characters, digits, upper and lower cases
                If return 6, input fulfil requirements
                 */
                switch(SystemControl.checkInputRequirement(email, password,cfmPassword,username)){
                    case 1:
                        Toast.makeText(RegisterUI.this, "Please fill in all of the fields", Toast.LENGTH_SHORT).show();
                        break;
                    case 2:
                        mEmailEt.setError("Invalid Email");
                        mEmailEt.setFocusable(true);
                        break;
                    case 3:
                        mPasswordEt.setError("Password must be at least 6 characters");
                        mPasswordEt.setFocusable(true);
                        break;
                    case 4:
                        mCfmPasswordEt.setError("Password must match");
                        mCfmPasswordEt.setFocusable(true);
                        break;
                    case 5:
                        mPasswordEt.setError("Password is too weak");
                        mPasswordEt.setFocusable(true);
                        break;
                    case 6:
                        registerUser(username,email, password, mAuth);
                }
            }
        });


    }

    public void registerUser(final String username, String email, String password, final FirebaseAuth mAuth){
        pd.show();
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            mAuth.getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    // Sign in success, update UI with the signed-in user's information
                                    pd.dismiss();
                                    //Log.d(TAG, "createUserWithEmail:success"); //Check logs
                                    SystemControl sc = new SystemControl();
                                    sc.setUpNewAccount(username);
                                    showValidateEmailDialog();
                                }
                            });
                        } else {
                            // If sign in fails, display a message to the user.
                            // Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            pd.dismiss();
                            Toast.makeText(RegisterUI.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                pd.dismiss();
                Toast.makeText(RegisterUI.this, "" + e.getMessage(),Toast.LENGTH_SHORT ).show();
            }
        });

    }

    private void showValidateEmailDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LinearLayout linearLayout = new LinearLayout(this);
        builder.setTitle("Email Validation");
        final TextView text = new TextView(this);
        text.setText(R.string.verifyEmailNotice);
        text.setPadding(20,10,10,10);
        linearLayout.addView(text);
        linearLayout.setPadding(20,10,10,10);
        builder.setView(linearLayout);
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startActivity(new Intent(RegisterUI.this, LoginUI.class));
                finish();
            }
        });
        builder.create().show();
    }




    // Make the action bar back button to go to main activity
    @Override
    public boolean onSupportNavigateUp () {
        onBackPressed();
        return super.onSupportNavigateUp();
    }
    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        startActivity(new Intent(RegisterUI.this, MainActivity.class));
        finish();

    }
}