package com.example.letseat;


import java.util.regex.Pattern;
/*
Contains all the constants used in the application.
 */
public class Constants {

    public static final Pattern PASSWORD_AUTH =
            Pattern.compile("^" +
            "(?=.*[0-9])" +         //at least 1 digit
            "(?=.*[a-z])" +         //at least 1 lower case letter
            "(?=.*[A-Z])" +         //at least 1 upper case letter
            "(?=.*[a-zA-Z])" +        //any letter
            "(?=.*[@#$%^&+=!*()])" +  //at least 1 special character
            ".{6,}" +                 //at least 6 characters
            "$");


    public static final int CAMERA_REQ_CODE =100;
    public static final int STORAGE_REQ_CODE =200;
    public static final int IMAGE_PICK_GALLERY_CODE =300;
    public static final int IMAGE_PICK_CAMERA_CODE =400;
    public static final String API_KEY = "ENTER_YOUR_PLACES_API_KEY_HERE";


}
