package com.example.letseat.Boundary;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.letseat.Constants;
import com.example.letseat.Control.RestaurantFinder;
import com.example.letseat.Control.RestaurantListViewAdapter;
import com.example.letseat.Entity.Restaurant;
import com.example.letseat.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
/*
To set up the recycler view and display all the restaurants near user location based on Google nearby places API
 */
public class RestaurantListUI extends Fragment {

    private static final int UPDATE_INTERVAL = 5000; // 5 seconds
    private FusedLocationProviderClient locationProviderClient;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;
    private Location currentLocation;
    private int LOCATION_PERMISSION = 101;
    private String JSON_URL = "";
    private Context mContext;
    private JsonObjectRequest request;
    private  RequestQueue requestQueue;
    private List<Restaurant> listRestaurant;
    private RecyclerView recyclerView;
    private RestaurantFinder rf = new RestaurantFinder(mContext);
    RestaurantListUI(Context mContext){
        this.mContext = mContext;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
        final View v = inflater.inflate(R.layout.activity_restaurant_list_ui, container, false);
        getActivity().setTitle("Restaurant List");
        displayRestaurantList(v);

        final SwipeRefreshLayout mSwipeRefreshLayout = (SwipeRefreshLayout)v.findViewById(R.id.restaurantListSRL);
        mSwipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                displayRestaurantList(v);
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });
        return v;
    }

    private void displayRestaurantList(View v){
        listRestaurant = new ArrayList<>();
        recyclerView = v.findViewById(R.id.recyclerviewid);
        locationProviderClient = LocationServices.getFusedLocationProviderClient(mContext);
        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(UPDATE_INTERVAL);
        locationCallback = new LocationCallback(){
            @Override
            public void onLocationAvailability(LocationAvailability locationAvailability) {
                super.onLocationAvailability(locationAvailability);
                if(locationAvailability.isLocationAvailable()){
                    Log.i("Lets Eat","Location is available");
                }else {
                    Log.i("Lets Eat","Location is unavailable");
                }
            }

            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                Log.i("Lets Eat","Location result is available");
            }
        };
        startGettingLocation();
        stopLocationRequests();
    }

    private void jsonRequest(String JSON_URL) {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, JSON_URL, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try{
                            //Get JSON Array
                            JSONArray array = response.getJSONArray("results");
                            for (int j = 0; j<array.length();j++){
                                Restaurant restaurant = rf.getRestaurantDetails(array,j);
                                jsonRequestPlaces(restaurant);
                            }
                        }catch (Exception e){
                            System.out.println("Lets Eat error: " + e);
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        System.out.println("Lets Eat error: " + error);

                    }
                });
        requestQueue = Volley.newRequestQueue(mContext);
        requestQueue.add(jsonObjectRequest);

    }


    private void jsonRequestPlaces(final Restaurant restaurant){
        String place_search = "https://maps.googleapis.com/maps/api/place/details/json?place_id=" +restaurant.getPlaceID() + "&fields=name,rating,formatted_phone_number,opening_hours&key=" + Constants.API_KEY;

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, place_search, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Restaurant restaurantInput = rf.getRestaurantPlacesDetails(restaurant, response);
                        listRestaurant.add(restaurantInput);
                        setUpRecyclerView(listRestaurant);
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        System.out.println("Lets Eat error: " + error);

                    }
                });
        requestQueue = Volley.newRequestQueue(mContext);
        requestQueue.add(jsonObjectRequest);

    }



    public void setUpRecyclerView(List<Restaurant> listRestaurant) {

        RestaurantListViewAdapter myAdapter = new RestaurantListViewAdapter(mContext, listRestaurant);
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        recyclerView.setAdapter(myAdapter);
        //RecyclerView.ItemDecoration divider = new DividerItemDecoration(mContext,DividerItemDecoration.VERTICAL);
        //recyclerView.addItemDecoration(divider);
    }


    public void startGettingLocation() {
        if(ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION)== PackageManager.PERMISSION_GRANTED){
            locationProviderClient.requestLocationUpdates(locationRequest,locationCallback, mContext.getMainLooper());
            locationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    currentLocation = location;
                    JSON_URL = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location="+ currentLocation.getLatitude() +"," + currentLocation.getLongitude() + "&type=restaurant&rankby=distance&key="+ Constants.API_KEY;
                    jsonRequest(JSON_URL);
                }
            });

            locationProviderClient.getLastLocation().addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.i("Lets Eat", "Exception while getting the location: "+e.getMessage());
                }
            });


        }else {
            if(ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)){
                Toast.makeText(mContext, "Permission needed", Toast.LENGTH_LONG).show();
            } else {
                ActivityCompat.requestPermissions(getActivity(),
                        new String[] { Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                        LOCATION_PERMISSION);
            }
        }
    }

    public void stopLocationRequests(){
        locationProviderClient.removeLocationUpdates(locationCallback);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        startGettingLocation();

    }


}
