package com.example.letseat.Boundary;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import android.view.Window;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.letseat.Control.Favourites;
import com.example.letseat.Control.ShareFindCall;
import com.example.letseat.Control.SystemControl;
import com.example.letseat.Entity.Restaurant;
import com.example.letseat.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
/*
Display specific details about the restaurants based on output from Google places API
 */

public class RestaurantsDetailsUI extends AppCompatActivity {

    //TextView
    TextView tv_name,tv_rating,location,tv_phoneNumber,tv_OpeningHours;
    ImageView img_res;
    ImageButton share, call, directions, favourite;
    SystemControl sc= new SystemControl();
    ShareFindCall sfc = new ShareFindCall();

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurants_details_ui);
        //Firebase init
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = firebaseAuth.getCurrentUser();
        final FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        final DatabaseReference databaseReference = firebaseDatabase.getReference("Users/"+user.getUid());

        //Receive data
        final String name = getIntent().getExtras().getString("restaurant_name");
        final String res_img = getIntent().getExtras().getString("restaurant_image");
        final Double ratings = getIntent().getExtras().getDouble("restaurant_rating");
        final String vicinity = getIntent().getExtras().getString("vicinity");
        final String place_id = getIntent().getExtras().getString("place_id");
        final String phone_number = getIntent().getExtras().getString("phone_number");
        final ArrayList<String> opening_hours = getIntent().getExtras().getStringArrayList("opening_hour");
        final Double latitude = getIntent().getExtras().getDouble("latitude");
        final Double longitude = getIntent().getExtras().getDouble("longitude");
        final String fireBaseID = getIntent().getExtras().getString("fireBaseID");

        final Restaurant restaurant = new Restaurant();
        restaurant.setRating(ratings);
        restaurant.setName(name);
        restaurant.setImage(res_img);
        restaurant.setVicinity(vicinity);
        restaurant.setPlaceID(place_id);
        restaurant.setPhoneNumber(phone_number);
        restaurant.setOpeningHours(opening_hours);
        restaurant.setLatitude(latitude);
        restaurant.setLongitude(longitude);


        String locationName = name.trim().replaceAll(" ", "+").replaceAll(",","%2C").replaceAll("|", "%7C");
        final String googleMapsURL = "https://www.google.com/maps/search/?api=1&query=" + locationName + "&query_place_id=" + place_id;

        //UI design
        Toolbar toolbar = (Toolbar) findViewById(R.id.user_toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
        setSupportActionBar(toolbar);
        Window window = this.getWindow();
        // clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        // finally change the color
        window.setStatusBarColor(ContextCompat.getColor(this,R.color.colorPrimaryDark));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(name);
        getSupportActionBar().setDisplayShowTitleEnabled(true);

        CollapsingToolbarLayout collapsingToolbarLayout = findViewById(R.id.collapsingtooldbar_id);
        collapsingToolbarLayout.setTitleEnabled(false);
        toolbar.setTitleTextColor(getResources().getColor(R.color.colorWhite));

        //init views
        tv_name = findViewById(R.id.aa_res_name);
        tv_rating = findViewById(R.id.aa_rating);
        location = findViewById(R.id.description);
        img_res = findViewById(R.id.aa_thumbnail);
        tv_phoneNumber = findViewById(R.id.phoneNumber);
        tv_OpeningHours = findViewById(R.id.operatingHours);
        share = findViewById(R.id.share);
        call = findViewById(R.id.call);
        directions = findViewById(R.id.directions);
        favourite = findViewById(R.id.favourite);
        setDetailsUI(name, ratings,phone_number,vicinity,opening_hours,res_img);


        //Button clicks
        call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(sc.call(phone_number));
            }
        });

        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(sc.share(name, vicinity,googleMapsURL));
            }
        });

        directions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mapIntent = sc.find(googleMapsURL);
                if (mapIntent.resolveActivity(getPackageManager()) != null) {
                    startActivity(mapIntent);
                }
            }
        });

        favourite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               setDetails(restaurant);
            }
        });
    }



    public void setDetails(final Restaurant restaurantClass){
        FirebaseAuth fbaseAuth = FirebaseAuth.getInstance();
        FirebaseUser fbUser = fbaseAuth.getCurrentUser();
        FirebaseDatabase fbDatabase = FirebaseDatabase.getInstance();
        final DatabaseReference dbReference = fbDatabase.getReference("Users/"+fbUser.getUid()+"/Favourites");

        System.out.println("Lets Eat restaurant details On diff class insert removed!! start" );

        dbReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot favouriteSnapshot : dataSnapshot.getChildren()){
                    Restaurant restaurant ;
                    restaurant = favouriteSnapshot.getValue(Restaurant.class);
                    if (restaurant.getPlaceID().equals(restaurantClass.getPlaceID())  ){
                        dbReference.child(restaurant.getFirebaseID()).removeValue();
                        Toast.makeText(RestaurantsDetailsUI.this, "Favourite removed: " + restaurantClass.getName(), Toast.LENGTH_SHORT).show();
                        //tv_rating.setCompoundDrawablesRelativeWithIntrinsicBounds(getApplicationContext().getResources().getDrawable( R.drawable.ic_favourite_black ),null,null,null);
                        favourite.setImageResource(R.drawable.ic_favourite_black);
                        return;
                    }
                }
                String id = dbReference.push().getKey();
                restaurantClass.setFirebaseID(id);
                dbReference.child(id).setValue(restaurantClass).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        //tv_rating.setCompoundDrawablesRelativeWithIntrinsicBounds(getApplicationContext().getResources().getDrawable( R.drawable.ic_favourite_red ),null,null,null);

                        favourite.setImageResource(R.drawable.ic_favourite_red);
                        Toast.makeText(RestaurantsDetailsUI.this, "Favourited " + restaurantClass.getName(), Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        System.out.println("Lets Eat error adding favourites: " + e);
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                System.out.println("Lets Eat FavouritesUI error: " + databaseError);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        final String place_id = getIntent().getExtras().getString("place_id");
        FirebaseAuth fbaseAuth = FirebaseAuth.getInstance();
        FirebaseUser fbUser = fbaseAuth.getCurrentUser();
        FirebaseDatabase fbDatabase = FirebaseDatabase.getInstance();
        final DatabaseReference dbReference = fbDatabase.getReference("Users/"+fbUser.getUid()+"/Favourites");
        dbReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot favouriteSnapshot : dataSnapshot.getChildren()){
                    Restaurant restaurant ;
                    restaurant = favouriteSnapshot.getValue(Restaurant.class);
                    if (restaurant.getPlaceID().equals(place_id)  ){
                        //tv_rating.setCompoundDrawablesRelativeWithIntrinsicBounds(getApplicationContext().getResources().getDrawable( R.drawable.ic_favourite_black ),null,null,null);
                        favourite.setImageResource(R.drawable.ic_favourite_red);
                        return;
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                System.out.println("Lets Eat FavouritesUI error: " + databaseError);
            }
        });
    }

    private void setDetailsUI(String name, double rating, String phoneNumber, String vicinity, ArrayList<String> openingHours, String res_img){
        tv_name.setText(name);
        tv_rating.setText(rating + "");
        tv_phoneNumber.setText( phoneNumber);
        location.setText(vicinity);

        RequestOptions requestOptions = new RequestOptions().centerCrop().placeholder(R.drawable.restaurant_place).error(R.drawable.restaurant_place);
        Glide.with(this).load(res_img).apply(requestOptions).into(img_res);


        String openingHoursText = "";
        for (int i = 0; i<openingHours.size();i++){
            openingHoursText = openingHoursText + openingHours.get(i) + "\n";
        }
        openingHoursText = openingHoursText.replace("Monday:","Mon    :\t\t\t");
        openingHoursText = openingHoursText.replace("Tuesday:","Tues   :\t\t\t");
        openingHoursText = openingHoursText.replace("Wednesday:","Wed    :\t\t\t");
        openingHoursText = openingHoursText.replace("Thursday:","Thurs  :\t\t\t");
        openingHoursText = openingHoursText.replace("Friday:","Fri       :\t\t\t");
        openingHoursText = openingHoursText.replace("Saturday:","Sat      :\t\t\t");
        openingHoursText = openingHoursText.replace("Sunday:","Sun     :\t\t\t");
        tv_OpeningHours.setText(openingHoursText);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent i = new Intent(RestaurantsDetailsUI.this, NavigationDrawerUI.class);
        boolean isFave = false;
        isFave = getIntent().getExtras().getBoolean("isFave");
        i.putExtra("isFave", isFave);
        startActivity(i);
        finish();
    }
}