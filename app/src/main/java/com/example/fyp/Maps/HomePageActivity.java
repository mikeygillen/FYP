package com.example.fyp.Maps;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.fyp.Activities.LoginActivity;
import com.example.fyp.Activities.MainActivity;
import com.example.fyp.Classes.Route;
import com.example.fyp.Classes.Run;
import com.example.fyp.Fragments.EditProfileFragment;
import com.example.fyp.Fragments.PairUsersFragment;
import com.example.fyp.Fragments.RouteFragment;
import com.example.fyp.Fragments.StatsFragment;
import com.example.fyp.Helper.Helper;
import com.example.fyp.Interface.Interface;
import com.example.fyp.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.maps.android.heatmaps.HeatmapTileProvider;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Scanner;

public class HomePageActivity extends AppCompatActivity implements Interface, StatsFragment.OnFragmentInteractionListener, RouteFragment.OnFragmentInteractionListener, PairUsersFragment.OnFragmentInteractionListener, EditProfileFragment.OnFragmentInteractionListener, BottomNavigationView.OnNavigationItemSelectedListener,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, OnMapReadyCallback, LocationListener {
    private static final String TAG = "HomePageActivity";

    private static GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationClient;
    private GoogleApiClient googleApiClient;
    private Location currentLocation;

    private static final int REQUEST_CODE = 101;
    private float mDistanceCovered;
    private long startTime, endTime;

    private static Button startRun;
    private Button endRun;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();
    private DatabaseReference newRoute = FirebaseDatabase.getInstance().getReference("Routes");
    private DatabaseReference newRun = FirebaseDatabase.getInstance().getReference("Runs");
    private String userid = mUser.getUid();
    private DatabaseReference mUserRef = FirebaseDatabase.getInstance().getReference("Users").child(userid);

    private static ArrayList<Location> locations = new ArrayList<>();
    private ArrayList<LatLng> LatLongs = new ArrayList<>();
    private static Fragment fragment = null;
    private int height, weight, age;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        firebaseAuth = FirebaseAuth.getInstance();
        if(firebaseAuth.getCurrentUser() == null){
            finish();
            startActivity(new Intent(this, LoginActivity.class));
        }

        startRun = findViewById(R.id.btn_start);
        endRun = findViewById(R.id.btn_stop);
        BottomNavigationView navigation = findViewById(R.id.bottom_navigation);
        navigation.setOnNavigationItemSelectedListener(this);

        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        mUserRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                try {
                    height = new Integer(snapshot.child("Height").getValue().toString());
                    weight = new Integer(snapshot.child("Weight").getValue().toString());
                    age = new Integer(snapshot.child("Age").getValue().toString());

                } catch (Exception e) {
                    e.printStackTrace();
                    //Toast.makeText(getActivity(), "Please update values" , Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        startRun.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                startRun.setVisibility(View.INVISIBLE);
                endRun.setVisibility(View.VISIBLE);
                startTime = 0;
                startRunTracking();
            }
        });

        endRun.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            public void onClick(View arg0) {
                endRun.setVisibility(View.INVISIBLE);
                startRun.setVisibility(View.VISIBLE);
                endRunTracking();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.mapheading, menu);
        return true;
    }


    private void startRunTracking() {
        Log.d(TAG, "Route Tracking Start");
        locations.clear();
        LatLongs.clear();

        startTime = System.currentTimeMillis();
        mDistanceCovered = 0;

            LocationRequest request = new LocationRequest();
            request.setInterval(30000);
            request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            FusedLocationProviderClient client = LocationServices.getFusedLocationProviderClient(this);

            int permission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);

            //If the app currently has access to the location permission...//
            if (permission == PackageManager.PERMISSION_GRANTED) {
                //request location updates//
                client.requestLocationUpdates(request, new LocationCallback() {
                    @Override
                    public void onLocationResult(LocationResult locationResult) {
                        Location location = locationResult.getLastLocation();
                        if (location != null) {
                            //Save the location data to the database//
                            locations.add(location);
                        }
                    }
                }, null);

            }
    }


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void endRunTracking() {
        if (locations.isEmpty()){
            Log.d(TAG, "No Route found");
        }else {
            for (int i = 0; i < locations.size(); i++) {
                LatLongs.add(new LatLng(locations.get(i).getLatitude(), locations.get(i).getLongitude()));
            }

            final double d = calculateDistance(locations.get(0), locations.get(locations.size() - 1));
            final String t = Helper.secondToHHMMSS(Helper.elapsedTime(startTime));
            final double p = Helper.calculatePace(Helper.elapsedTime(startTime), d);
            final double c = calculateCalorie(d, p);

            endTime = System.currentTimeMillis();
            SimpleDateFormat formatter = new SimpleDateFormat("E, dd MMM yyyy");
            final String strDate = formatter.format(endTime);

            Log.d(TAG, "String Date = " + strDate);

            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(HomePageActivity.this);
            alertDialogBuilder.setTitle("Workout Finished");
            alertDialogBuilder.setMessage("Are you happy with your workout?" + "\n Distance Covered = " + d + "Km" + "\n Time = " + t + "\n Pace = " + p + "min/Km" + "\n Calories Burned = " + c);
            alertDialogBuilder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String routeKey =  newRoute.push().getKey();
                    final Route route = new Route(d, LatLongs, userid, strDate);

                    try {
                        newRoute.child(routeKey).setValue(route, new DatabaseReference.CompletionListener() {
                            public void onComplete(DatabaseError error, DatabaseReference ref) {
                                Log.d("ssd", "onComplete: " + error);
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(getApplication(), "Problem creating route.", Toast.LENGTH_LONG).show();
                    }

                    String runKey = newRun.push().getKey();
                    final Run run = new Run(t, d, p, c, routeKey, userid, strDate);

                    try {
                        newRun.child(runKey).setValue(run, new DatabaseReference.CompletionListener() {
                            public void onComplete(DatabaseError error, DatabaseReference ref) {
                                Log.d("ssd", "onComplete: " + error);
                            }
                        });
                        Log.d(TAG, "Route Tracking Finished");
                        Toast.makeText(getApplication(), "Workout complete! \n Distance Covered = " + d + "Km", Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(getApplication(), "Problem creating run.", Toast.LENGTH_LONG).show();
                    }

                    try {
                        mUserRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                try {
                                    double tDis = new Double(snapshot.child("Total Distance").getValue().toString()) + d;
                                    mUserRef.child("Total Distance").setValue(tDis);

                                    int tRuns = new Integer(snapshot.child("Total Runs").getValue().toString()) + 1;
                                    mUserRef.child("Total Runs").setValue(tRuns);

                                    double tCalories = new Double(snapshot.child("Total Calories").getValue().toString() + c);
                                    mUserRef.child("Total Calories").setValue(tCalories);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                            }
                        });
                    } catch (Exception e) {
                        Log.d(TAG, "Update user totals: FAILED " + e);
                        e.printStackTrace();
                    }
                }
            });
            alertDialogBuilder.setNegativeButton("Abandon", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Toast.makeText(HomePageActivity.this, "Workout Discarded", Toast.LENGTH_SHORT).show();

                }
            });
            alertDialogBuilder.create().show();

        }
    }

    // Assume this calculates precise distance
    private float calculateDistance(Location start, Location end){
        float distanceDiff = start.distanceTo(end); // Return meter unit
        return mDistanceCovered + distanceDiff;
    }

    // Assume this calculates precise calories
    private double calculateCalorie(double d, double p){
        //Total calories burned = Duration (in minutes)*(MET*3.5*weight in kg)/200
        double calories = 0;

        if (p >= 10){
            calories = d*(3.8*3.5*weight) / 200;
        }else if (p >=8) {
            calories = d * (4.3 * 3.5 * weight) / 200;
        }else if (p >=7) {
            calories = d * (5.8 * 3.5 * weight) / 200;
        }else if (p >=6) {
            calories = d * (9.8 * 3.5 * weight) / 200;
        }else if (p >=5) {
            calories = d * (12.3 * 3.5 * weight) / 200;
        }else if (p >=4) {
            calories = d * (18 * 3.5 * weight) / 200;
        }else {
            calories = d * (23 * 3.5 * weight) / 200;
        }

        Log.d(TAG, "calculateCalorie: calories = " + calories);
        return calories;
    }

    private boolean loadFragment(Fragment fragment) {
        if (fragment != null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).commit();
            return true;
        }
        return false;
    }


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.navigation_home:
                startRun.setVisibility(View.VISIBLE);
                fragment = new Fragment();
                Objects.requireNonNull(this.getSupportActionBar()).show();
                break;

            case R.id.navigation_stats:
                startRun.setVisibility(View.INVISIBLE); endRun.setVisibility(View.INVISIBLE);
                fragment = new StatsFragment();
                break;

            case R.id.navigation_edit:
                startRun.setVisibility(View.INVISIBLE); endRun.setVisibility(View.INVISIBLE);
                fragment = new EditProfileFragment();
                break;

            case R.id.navigation_routes:
                startRun.setVisibility(View.INVISIBLE); endRun.setVisibility(View.INVISIBLE);
                fragment = new RouteFragment();
                break;

            case R.id.navigation_finder:
                startRun.setVisibility(View.INVISIBLE); endRun.setVisibility(View.INVISIBLE);
                fragment = new PairUsersFragment();
                break;
        }
        return loadFragment(fragment);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.marker:
                showNearbyLights();
                Toast.makeText(this, "Display nearby streetlight locations", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.heatmap:
                showNearbyHeatmap();
                Toast.makeText(this, "Display heat map of nearby streetlights", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.heatmapfingal:
                heatmap("fingal.json");
                return true;
            case R.id.markerfingal:
                marker("fingal.json");
                return true;
            case R.id.heatmapcity:
                heatmap("dublin.json");
                return true;
            case R.id.markercity:
                marker("dublin.json");
                return true;
            case R.id.heatmapsouth:
                heatmap("south.json");
                return true;
            case R.id.markersouth:
                marker("south.json");
                return true;
            case R.id.logout:
                firebaseAuth.signOut();
                startActivity(new Intent(this, MainActivity.class));
                finish();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void heatmap(String json){
        List<LatLng> list = null;
        try {
            list = readItems(json);
        } catch (JSONException e) {
            Toast.makeText(this, "Problem reading list of locations.", Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //mMap.clear();
        if (list != null) {
            new HeatmapTileProvider.Builder().data(list).build();
        }
    }
    public void showNearbyHeatmap(){
       // mMap.clear();
        List<LatLng> list = null;
        try {
            list = readItems("fingal.json");
            list.addAll(readItems("dublin.json"));
            list.addAll(readItems("south.json"));
        } catch (JSONException e) {
            Toast.makeText(this, "Problem reading list of locations.", Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (list != null) {
            new HeatmapTileProvider.Builder().data(list).build();
        }
    }

    public void showNearbyLights() {
        Log.d(TAG, "showNearbyLights: Beginning");
        Bitmap b = BitmapFactory.decodeResource(getResources(), R.drawable.ic_light);
        Bitmap smallMarker = Bitmap.createScaledBitmap(b, 35, 35, false);
        BitmapDescriptor smallMarkerIcon = BitmapDescriptorFactory.fromBitmap(smallMarker);

        List<LatLng> list = null;
        try {
            list = readItems("fingal.json");
            list.addAll(readItems("dublin.json"));
            list.addAll(readItems("south.json"));
        } catch (JSONException e) {
            Toast.makeText(this, "Problem reading list of locations.", Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            e.printStackTrace();
        }

        for (LatLng light : list){
            Location target = new Location("target");
            target.setLatitude(light.latitude);
            target.setLongitude(light.longitude);
            if (currentLocation.distanceTo(target) < 2000) {
                LatLng streetLight = new LatLng(light.latitude, light.longitude);
                mMap.addMarker(new MarkerOptions().position(streetLight).icon(smallMarkerIcon));
            }
        }
    }

    public void marker(String json) {
        Log.d(TAG, "show Lights For Specific Region: Beginning");
        Bitmap b = BitmapFactory.decodeResource(getResources(), R.drawable.ic_light);
        Bitmap smallMarker = Bitmap.createScaledBitmap(b, 35, 35, false);
        BitmapDescriptor smallMarkerIcon = BitmapDescriptorFactory.fromBitmap(smallMarker);

        List<LatLng> list = null;
        try {
            list = readItems(json);
        } catch (JSONException e) {
            Toast.makeText(this, "Problem reading list of locations.", Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            e.printStackTrace();
        }

        for (LatLng light : list){
                LatLng streetLight = new LatLng(light.latitude, light.longitude);
                mMap.addMarker(new MarkerOptions().position(streetLight).icon(smallMarkerIcon));
        }
    }

    private ArrayList<LatLng> readItems(String resource) throws JSONException, IOException {
        ArrayList<LatLng> list = new ArrayList<>();
        InputStream inputStream = getAssets().open(resource);
        String json = new Scanner(inputStream).useDelimiter("\\A").next();
        JSONArray array = new JSONArray(json);
        for (int i = 0; i < array.length(); i++) {
            JSONObject object = array.getJSONObject(i);
            double lat = object.getDouble("Lat");
            double lng = object.getDouble("Long");
            list.add(new LatLng(lat, lng));}
        return list;
    }

    private void getDeviceLocation() {
        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */
        try {
            Task locationResult = fusedLocationClient.getLastLocation();
            locationResult.addOnCompleteListener(this, new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if (task.isSuccessful()) {
                        // Set the map's camera position to the current location of the device.
                        currentLocation = (Location) task.getResult();
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()), 13));
                    } else {
                        Log.d(TAG, "Current location is null. Using defaults.");
                    }
                }
            });

        } catch(SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }
        Log.d(TAG, "getDeviceLocation: = " + currentLocation);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        Intent i = getIntent();
        ArrayList<LatLng> routePoints = i.getParcelableArrayListExtra("route_points");

            if (routePoints!=null){
                try {
                    mapRoute(routePoints);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }else {
                getDeviceLocation();
                mMap.setMyLocationEnabled(true);
            }
    }

    public void mapRoute(ArrayList<LatLng> routePoints) throws IOException {
        Log.d(TAG, "mapRoute beginning.... ");
        LatLng start = routePoints.get(0);
        LatLng end = routePoints.get(routePoints.size()-1);

        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(this, Locale.getDefault());
        addresses = geocoder.getFromLocation(start.latitude, start.longitude, 1);
        String startAddress = addresses.get(0).getAddressLine(0);

        addresses = geocoder.getFromLocation(end.latitude, end.longitude, 1);
        String endAddress = addresses.get(0).getAddressLine(0);

        MarkerOptions startPoint = new MarkerOptions();
        startPoint.position(routePoints.get(0)).title(startAddress).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
        mMap.addMarker(startPoint);

        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(startPoint.getPosition().latitude, startPoint.getPosition().longitude), 15.0f));

        MarkerOptions endPoint = new MarkerOptions();
        endPoint.position(routePoints.get(routePoints.size() - 1)).title(endAddress).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
        mMap.addMarker(endPoint);

        PolylineOptions poly = new PolylineOptions().clickable(true);

        int length = routePoints.size();
        for(int i = 0; i < length; i++) {
            if (routePoints.get(i) != null) {
                LatLng latLong = new LatLng(routePoints.get(i).latitude, routePoints.get(i).longitude);
                poly.add(latLong);
            }

            poly.width(15).color(Color.BLUE).geodesic(true);
            mMap.addPolyline(poly);
        }
    }

    @Override
    protected void onStart(){
        super.onStart();
        googleApiClient.connect();
    }

    @Override
    protected void onStop(){
        if(googleApiClient.isConnected()){
            googleApiClient.disconnect();
        }
        super.onStop();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult){
        Log.e("HomePageActivity", "Connection failed: " + connectionResult.getErrorCode());
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i){
        Log.e("HomePageActivity", "Connection suspended");
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void onLocationChanged(Location location) {
    }
}