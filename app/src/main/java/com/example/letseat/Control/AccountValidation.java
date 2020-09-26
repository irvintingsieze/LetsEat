package com.example.letseat.Control;

import android.app.ProgressDialog;
import android.text.TextUtils;
import android.util.Patterns;

import com.example.letseat.Constants;
import com.example.letseat.Entity.User;
import com.google.firebase.database.DataSnapshot;

import java.util.ArrayList;

/*
This class describes how the system validates password strength and retrieves user data for UI
 */
public class AccountValidation {



    int checkUserInput(String email, String password, String cfmPassword, String username){
        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password) || TextUtils.isEmpty(cfmPassword) || TextUtils.isEmpty(username)){
            return 1;
        }
        else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            return 2;
        }
        else if (password.length()<6){
            return 3;
        }else if (!(password.equals(cfmPassword))){
            return 4;
        }else if (!Constants.PASSWORD_AUTH.matcher(password).matches()) {
            return 5;
        } else {
            return 6;
        }
    }

    static void setUpNewAccounts(String username){
        User user = new User();
        user.setUpAccount(username);
    }

    //DNU
    void validateUser(String email, String password, final ProgressDialog pd){

    }

    ArrayList<String> getAllUserDetails(DataSnapshot ds ){
        User user = new User();
        ArrayList<String> userDetails = new ArrayList<>();
        userDetails.add(user.getName(ds));
        userDetails.add(user.getEmail(ds));
        userDetails.add(user.getProfilePic(ds));
        userDetails.add(user.getCoverPic(ds));
        return userDetails;
    }

    void setUserDetailsAV(int choice, String value, ProgressDialog pd){
        /*
        Choice 0: Name
        Choice 1: Email
        Choice 2: Password
         */
        User user = new User();
        switch (choice){
            case 0:
                user.setName(value, pd);
                break;
            case 1:
                user.setEmail(value, pd);
                break;
            case 2:
                user.setPassword(value, pd);
                break;
        }
    }

}
