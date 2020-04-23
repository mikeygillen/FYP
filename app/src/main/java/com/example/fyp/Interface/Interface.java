package com.example.fyp.Interface;

import com.example.fyp.Classes.Route;
import com.example.fyp.Classes.Run;
import com.example.fyp.Classes.User;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

public interface Interface {
    User currentUser = new User();

    ArrayList<User> userList = new ArrayList<>();
    ArrayList<Route> routeList = new ArrayList<>();
    ArrayList<Run> runList = new ArrayList<>();

    ArrayList<LatLng> routePoints = new ArrayList<>();
}
