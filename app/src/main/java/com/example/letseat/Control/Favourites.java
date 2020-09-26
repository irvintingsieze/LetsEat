package com.example.letseat.Control;

import com.example.letseat.Entity.Restaurant;
import com.google.firebase.database.DataSnapshot;
/*
This class helps retrieve favourite restaurant details and returns it to UI for display
 */
public class Favourites {


    public Restaurant findFavouriteRestaurant(DataSnapshot favouriteSnapshot){
        Restaurant restaurant;
        restaurant = favouriteSnapshot.getValue(Restaurant.class);
        System.out.println("Lets Eat restaurant details: " + restaurant.getName() + restaurant.getRating());
        restaurant.setIsFavourite(true);
        return restaurant;
    }

}
