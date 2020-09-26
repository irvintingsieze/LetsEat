package com.example.letseat.Control;
import android.content.Intent;
import android.net.Uri;

/*
Uses implicit intents to define how the user can share restaurant details, find restaurant location and call restaurant through its contact number
 */
public class ShareFindCall {


    public Intent call(String phone_number){
        if (phone_number.equals("No phone number found")){
            phone_number = "";
        }
        return  new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phone_number, null));
    }

    public  Intent share (String name, String vicinity, String googleMapsURL){
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, "Let's Eat together here! \n\n"
                +"Restaurant Name: " + name + "\n"
                +"Restaurant Location: " + vicinity + "\n\n"
                + "Head there now: \n"
                +googleMapsURL
        );
        sendIntent.setType("text/plain");
        return Intent.createChooser(sendIntent, null);
    }

    public Intent find (String googleMapsURL){
        Uri intentUri = Uri.parse(googleMapsURL);
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, intentUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        return mapIntent;
    }

}
