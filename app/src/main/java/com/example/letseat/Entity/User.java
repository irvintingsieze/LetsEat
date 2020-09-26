package com.example.letseat.Entity;

import android.app.ProgressDialog;
import android.provider.ContactsContract;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.letseat.Constants;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.google.firebase.storage.FirebaseStorage.getInstance;

/*
This class defines how the user communicates with the Firebase database.
It contains getter and setter methods for user details.
 */
public class User {

    private String name;
    private String email;
    private String password;
    private int userID;

    public void setName(final String name, final ProgressDialog pd){
        FirebaseAuth firebaseAuth;
        FirebaseUser user;
        //FirebaseDatabase firebaseDatabase;
        DatabaseReference databaseReference;
        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Users");
        Map<String,Object> result = new HashMap<>();
        result.put("name", name);

        databaseReference.child(user.getUid()).updateChildren(result)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        pd.dismiss();
                        // DO AN ALERT DIALOG!!!!
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                pd.dismiss();
                System.out.println("Lets Eat error setName: " + e);
            }
        });
    }

    public void setEmail(String email, final ProgressDialog pd){
        FirebaseAuth firebaseAuth;
        FirebaseUser user;
        DatabaseReference databaseReference;
        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Users");
        HashMap<String,Object> result = new HashMap<>();
        result.put("email", email);
        databaseReference.child(user.getUid()).updateChildren(result)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        pd.dismiss();
                        // DO AN ALERT DIALOG!!!!
                        //Toast.makeText(getActivity(), "Updated name...", Toast.LENGTH_SHORT).show();
                        //nameTV.setText("Name: " + name);
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                pd.dismiss();
                //Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    //DNU
    public void setPassword(String Password, final ProgressDialog pd){
        FirebaseAuth firebaseAuth;
        FirebaseUser user;
        //FirebaseDatabase firebaseDatabase;
        DatabaseReference databaseReference;
        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Users");
        HashMap<String,Object> result = new HashMap<>();
        result.put("email", email);
        databaseReference.child(user.getUid()).updateChildren(result)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        pd.dismiss();
                        // DO AN ALERT DIALOG!!!!
                        //Toast.makeText(getActivity(), "Updated name...", Toast.LENGTH_SHORT).show();
                        //nameTV.setText("Name: " + name);
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                pd.dismiss();
                //Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public String getName(DataSnapshot ds){
        return "" + ds.child("name").getValue();
    }

    public String getEmail(DataSnapshot ds){
        return  "" + ds.child("email").getValue();
    }

    public String getProfilePic(DataSnapshot ds){
        return  "" + ds.child("image").getValue();
    }

    public String getCoverPic(DataSnapshot ds){
        return  "" + ds.child("cover").getValue();
    }


    //DNU
    public String getPassword(DataSnapshot ds){
        String password = "" + ds.child("password").getValue();
        return password;
    }

    public void setUpAccount(String username){
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();

        //Get user email and uid from authentication
        String email = user.getEmail();
        String uid = user.getUid();

        //DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(uid);
        //Store user details using HashMap
        HashMap<String, String> hashMap = new HashMap<>();

        hashMap.put("email", email);
        hashMap.put("uid", uid);
        hashMap.put("name", username);
        hashMap.put("image", ""); //add at edit profile page
        hashMap.put("cover", ""); //add at edit profile page


        FirebaseDatabase database = FirebaseDatabase.getInstance();
        //path to store user data
        DatabaseReference ref = database.getReference("Users");
        //put data within hashmap in database
        ref.child(uid).setValue(hashMap);
        //reference.setValue(hashMap);
    }



}
