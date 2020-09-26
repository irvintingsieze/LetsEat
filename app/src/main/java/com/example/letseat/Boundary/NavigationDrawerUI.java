package com.example.letseat.Boundary;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.letseat.Control.SystemControl;
import com.example.letseat.R;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import static com.google.firebase.storage.FirebaseStorage.getInstance;
/*
Required for navigation drawers. Enable the user to navigate through different fragments
 */
public class NavigationDrawerUI extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation_drawer_ui);

        boolean isFave;
        try {
            isFave = getIntent().getExtras().getBoolean("isFave");
        }catch (Exception e){
            isFave = false;
        }

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = firebaseAuth.getCurrentUser();
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference("Users");

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        drawer = findViewById(R.id.drawer_layout);

        NavigationView navigationView = findViewById(R.id.nav_view);
        View mView =  navigationView.getHeaderView(0);
        final TextView nameTV = (TextView)mView.findViewById(R.id.nameTV_nav);
        final ImageView avatarIV = (ImageView)mView.findViewById(R.id.avatarIV_nav);
        Query query = databaseReference.orderByChild("email").equalTo(user.getEmail());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()){
                    SystemControl sc = new SystemControl();
                    ArrayList<String> userDetails = sc.getUserDetails(ds);
                    String name = "" + userDetails.get(0);
                    String image = "" + userDetails.get(2);
                    nameTV.setText(name);
                    RequestOptions requestOptions = new RequestOptions().centerCrop().placeholder(R.drawable.editprofile).error(R.drawable.editprofile);
                    Glide.with(NavigationDrawerUI.this).load(image).apply(requestOptions).into(avatarIV);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        if (savedInstanceState==null) {
            if (isFave){
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new FavouritesUI(this)).commit();
                navigationView.setCheckedItem(R.id.favouritesActivity);
            }else {
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new RestaurantListUI(this)).commit();
                navigationView.setCheckedItem(R.id.foodPlaceActivity);
            }
        }
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.favouritesActivity:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new FavouritesUI(this)).commit();
                break;
            case R.id.editProfileActivity:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new ProfileUI()).commit();
                break;
            case R.id.foodPlaceActivity:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new RestaurantListUI(this)).commit();
                break;
            case R.id.logout:
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(NavigationDrawerUI.this, MainActivity.class));
                finish();
                break;
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)){
            drawer.closeDrawer(GravityCompat.START);
        }else{
            super.onBackPressed();
            startActivity(new Intent(NavigationDrawerUI.this, MainActivity.class));
            finish();
        }
    }
}