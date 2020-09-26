package com.example.letseat.Control;

import android.content.Context;

import com.android.volley.RequestQueue;
import com.example.letseat.Constants;
import com.example.letseat.Entity.Restaurant;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/*
This class retrieves restaurant details from Google Places API based on current location
 */
public class RestaurantFinder {
    private Context mContext;
    private RequestQueue requestQueue;

    public RestaurantFinder(Context mContext) {
        this.mContext = mContext;
    }


    public Restaurant getRestaurantDetails (JSONArray array, int j){
        Restaurant restaurant = new Restaurant();
        try {
            double latitude = 0.0, ratings = 0.0, longitude = 0.0;
            String name = "Name not found", photoURL = "", vicinity = "No address found";
            JSONObject js = array.getJSONObject(j);
            try{
                JSONObject directions = js.getJSONObject("geometry");
                JSONObject locations = directions.getJSONObject("location");
                latitude = locations.getDouble("lat");
                longitude = locations.getDouble("lng");
            }catch(Exception e){
                System.out.println("Exception error: " + e);
            }
            if (!js.isNull("geometry")){
                name = js.getString("name");
            }
            if (!js.isNull("rating")){
                ratings = js.getDouble("rating");
            }

            try{
                JSONArray photosArray = js.getJSONArray("photos");
                String photoReference = photosArray.getJSONObject(0).getString("photo_reference");
                photoURL = "https://maps.googleapis.com/maps/api/place/photo?maxwidth=9999&photoreference="
                        + photoReference + "&key=" + Constants.API_KEY;
            }catch (Exception e){
                System.out.println("Error: " + e);
            }
            if (!js.isNull("vicinity")){
                vicinity = js.getString("vicinity");
            }
            String place_id = js.getString("place_id");
            restaurant.setName(name);
            restaurant.setRating(ratings);
            restaurant.setImage(photoURL);
            restaurant.setVicinity(vicinity);
            restaurant.setPlaceID(place_id);
            restaurant.setLatitude(latitude);
            restaurant.setLongitude(longitude);
            restaurant.setIsFavourite(false);
            restaurant.setFirebaseID("None");
        }catch (Exception e){
            System.out.println("Lets Eat error: " + e);
        }
        return restaurant;
    }


    public Restaurant getRestaurantPlacesDetails (Restaurant restaurant, JSONObject response){
        try {
            ArrayList<String> openingHrStringArray = new ArrayList<>();
            JSONObject js = response.getJSONObject("result");
            String phone_num = "No phone number found";
            if (!js.isNull("formatted_phone_number")){
                phone_num = js.getString("formatted_phone_number");
            }
            try {
                JSONObject c = js.getJSONObject("opening_hours");
                JSONArray openingHrArray = c.getJSONArray("weekday_text");
                for (int i = 0; i < openingHrArray.length(); i++) {
                    openingHrStringArray.add(openingHrArray.get(i).toString());
                }
            } catch (Exception e) {
                openingHrStringArray.add("Opening hours not available");
            }
            restaurant.setPhoneNumber(phone_num);
            restaurant.setOpeningHours(openingHrStringArray);
        }catch (Exception e ){
            System.out.println("Lets Eat error: " + e);
        }
        return restaurant;
    }
}
