package com.example.letseat.Boundary;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.letseat.Control.SystemControl;
import com.example.letseat.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


/*
UI to enable user to login to app using firebase
 */
public class LoginUI extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_ui);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Login");

        final EditText mEmailEt = findViewById(R.id.emailET);
        final EditText mPasswordEt = findViewById(R.id.passwordET);
        TextView notHaveAccountTV = findViewById(R.id.notHaveAccountTV);
        TextView mResetPW = findViewById(R.id.forgotPW_TV);
        Button mLoginBtn =  findViewById(R.id.loginBtn);

        final FirebaseAuth mAuth = FirebaseAuth.getInstance();
        final ProgressDialog pd = new ProgressDialog(this);

        //No account TV
        notHaveAccountTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginUI.this, RegisterUI.class));
                finish();
            }
        });

        //Reset PW on click
        mResetPW.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showRecoverPasswordDialog(pd, mAuth);
            }
        });

        //Login with email and password
        mLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = mEmailEt.getText().toString();
                String password = mPasswordEt.getText().toString().trim();
                if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)){
                    Toast.makeText(LoginUI.this, "Please fill in all of the fields", Toast.LENGTH_SHORT).show();
                }else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                    //invalid email
                    mEmailEt.setError("Invalid Email");
                    mEmailEt.setFocusable(true);
                }else {
                    loginUser(email,password, pd, mAuth);
                }
            }
        });

    }

    private void loginUser(final String email, final String password, final ProgressDialog pd, final FirebaseAuth mAuth){
        pd.setMessage("Logging In...");
        pd.show();
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            if(mAuth.getCurrentUser().isEmailVerified()) {
                                pd.dismiss();
                                // Sign in success, update UI with the signed-in user's information
                                //Log.d("Sign in!", "signInWithEmail:success");
                                FirebaseUser user = mAuth.getCurrentUser();

                                if (task.getResult().getAdditionalUserInfo().isNewUser()) {
                                    SystemControl sc = new SystemControl();
                                    sc.setUpNewAccount("");
                                    sc.verifyUser(email, password, pd);
                                }
                                startActivity(new Intent(LoginUI.this, NavigationDrawerUI.class));
                                finish();
                            }
                            else{
                                pd.dismiss();
                                showValidateEmailDialog();
                            }
                        } else {
                            pd.dismiss();
                            // If sign in fails, display a message to the user.
                            //Log.w("Sign in Fails!", "signInWithEmail:failure", task.getException());
                            Toast.makeText(LoginUI.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                //show error msg
                pd.dismiss();
                Toast.makeText(LoginUI.this, "Invalid email and password combination!", Toast.LENGTH_SHORT).show();
                //Toast.makeText(LoginActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showRecoverPasswordDialog(final ProgressDialog pd, final FirebaseAuth mAuth){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        LinearLayout linearLayout = new LinearLayout(this);
        builder.setTitle("Recover Password");
        final EditText emailET = new EditText(this);
        emailET.setHint("Email");
        emailET.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        emailET.setMinEms(18);

        linearLayout.addView(emailET);
        linearLayout.setPadding(20,10,10,10);
        builder.setView(linearLayout);

        builder.setPositiveButton("Recover", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //key in email
                String email = emailET.getText().toString().trim();
                pwRecovery(email, pd, mAuth);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.create().show();
    }

    private void showValidateEmailDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LinearLayout linearLayout = new LinearLayout(this);
        builder.setTitle("Email Validation");
        final TextView text = new TextView(this);
        text.setText(R.string.verifyemailnotice);
        text.setPadding(30,10,10,10);
        linearLayout.addView(text);
        linearLayout.setPadding(20,10,10,10);
        builder.setView(linearLayout);

        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.create().show();
    }

    private void pwRecovery(String email, final ProgressDialog pd, FirebaseAuth mAuth){
        pd.setMessage("Sending email...");
        pd.show();
        mAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                pd.dismiss();
                if (task.isSuccessful()){
                    Toast.makeText(LoginUI.this, "Email sent!", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(LoginUI.this, "Failed to send email", Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                pd.dismiss();
                Toast.makeText(LoginUI.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Make the action bar back button to go to main activity
    @Override
    public boolean onSupportNavigateUp () {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    public void onBackPressed()
    {
        super.onBackPressed();
        startActivity(new Intent(LoginUI.this, MainActivity.class));
        finish();

    }
}
