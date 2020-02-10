package com.example.fyp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.TileOverlay;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.maps.android.data.geojson.GeoJsonLayer;
import com.google.maps.android.heatmaps.HeatmapTileProvider;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class HomePage extends AppCompatActivity implements Routes.OnFragmentInteractionListener, Leaderboard.OnFragmentInteractionListener, EditProfile.OnFragmentInteractionListener, BottomNavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback{
    private static final String TAG = "HomePageActivity";

    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationClient;
    private LocationRequest mLocationRequest;
    private LocationCallback locationCallback;
    private static final int REQUEST_CODE = 101;
    public double cLat;
    public double cLong;

    private LocationProvider mLocationProvider;
    private boolean isUserWalking;
    private Location mCurrentLocation;
    private Location mPreviousLocation;
    private boolean isBroadcastAllow;
    private float mDistanceCovered;
    private long startTime;

    private boolean requestingLocationUpdates;

    private Button startRun, endRun;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference mRef, newRoute;

    ArrayList<LatLng> latLngs = new ArrayList();
    ArrayList<Location> locations = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        isUserWalking = false;

        startRun = (Button) findViewById(R.id.btn_start);
        endRun = (Button) findViewById(R.id.btn_stop);
        BottomNavigationView navigation = findViewById(R.id.bottom_navigation);
        navigation.setOnNavigationItemSelectedListener(this);

        firebaseAuth = FirebaseAuth.getInstance();
        if(firebaseAuth.getCurrentUser() == null){
            startActivity(new Intent(this, LoginActivity.class));
        }

        FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();
        String userid = mUser.getUid();
        mRef = FirebaseDatabase.getInstance().getReference("Users").child(userid);
        newRoute = FirebaseDatabase.getInstance().getReference("Routes");

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        getLastLocation();

        startRun.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                startRun.setVisibility(View.INVISIBLE);
                endRun.setVisibility(View.VISIBLE);
                startTime = 0;
                startRunTracking();
            }
        });

        endRun.setOnClickListener(new View.OnClickListener() {
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
        locations.clear();

        Log.d(TAG, "Route Tracking Start");
        //if (mCurrentLocation != null) {
        if (isLocationEnabled()){

                //Initiate the request to track the device's location//
                LocationRequest request = new LocationRequest();
                //Specify how often your app should request the device’s location//
                request.setInterval(5000);
                //Get the most accurate location data available//
                request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                FusedLocationProviderClient client = LocationServices.getFusedLocationProviderClient(this);

            // TODO:
            startTime = System.currentTimeMillis();
            mPreviousLocation = mCurrentLocation;
            mDistanceCovered = 0;

                //final String path = newRoute.toString().substring(Integer.parseInt(newRoute.toString()));
                int permission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);

                //If the app currently has access to the location permission...//
            if (permission == PackageManager.PERMISSION_GRANTED) {
                //request location updates//
                client.requestLocationUpdates(request, new LocationCallback() {
                    @Override
                    public void onLocationResult(LocationResult locationResult) {
                        //Get a reference to the database//
                        //newRoute = FirebaseDatabase.getInstance().getReference();
                        //newRoute.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(route);
                        Location location = locationResult.getLastLocation();
                        if (location != null) {
                            //Save the location data to the database//
                            cLat = location.getLatitude();
                            cLong = location.getLongitude();
                            locations.add(location);
                        }
                    }
                }, null);
            }
        } else {
            Log.d(TAG, "location is null ...............");
        }
    }

    private void endRunTracking() {
        calculateDistance();

        final String d = String.valueOf(distanceCovered());
        final String t = String.valueOf(elapsedTime());
        final String p = Helper.calculatePace(elapsedTime(), distanceCovered());

        Log.d(TAG, "Route Tracking Finished with " + t + " time and " + d + " distance");

        Route route = new Route(locations);

        newRoute.child(FirebaseAuth.getInstance().getCurrentUser().getEmail()).setValue(route).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                newRoute.child("Distance").setValue(d);
            }
        });
    }

    // Assume this algorithm calculates precise distance
    private void calculateDistance(){
            float distanceDiff = mPreviousLocation.distanceTo(mCurrentLocation); // Return meter unit
            mDistanceCovered = mDistanceCovered + distanceDiff;
            mPreviousLocation = mCurrentLocation;
    }

    public long elapsedTime() {
        return (System.currentTimeMillis() - startTime) / 1000;
    }

    public float distanceCovered() {
        return mDistanceCovered;
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (requestingLocationUpdates) {
            startLocationUpdates();
        }
    }
    private void startLocationUpdates() {
        fusedLocationClient.requestLocationUpdates(mLocationRequest, locationCallback, Looper.getMainLooper());
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopLocationUpdates();
    }
    private void stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback);
    }

    private boolean loadFragment(Fragment fragment) {
        //switching fragment
        if (fragment != null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).commit();
            return true;
        }
        return false;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Fragment fragment = null;

        switch (item.getItemId()) {
            case R.id.navigation_home:
                startRun.setVisibility(View.VISIBLE);
                fragment = new Fragment();
                break;

            /*case R.id.navigation_stats:
                startRun.setVisibility(View.INVISIBLE); endRun.setVisibility(View.INVISIBLE);
                fragment = new StatsFragment();
                break;*/

            case R.id.navigation_edit:
                startRun.setVisibility(View.INVISIBLE); endRun.setVisibility(View.INVISIBLE);
                fragment = new EditProfile();
                break;

            case R.id.navigation_routes:
                startRun.setVisibility(View.INVISIBLE); endRun.setVisibility(View.INVISIBLE);
                fragment = new Routes();
                break;

            case R.id.navigation_leaderboard:
                startRun.setVisibility(View.INVISIBLE); endRun.setVisibility(View.INVISIBLE);
                fragment = new Leaderboard();
                break;
        }

        return loadFragment(fragment);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.marker:
                Toast.makeText(this, "Display all Marker selected", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.heatmap:
                heatmapAll();
                Toast.makeText(this, "Display all Heatmaps selected", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.heatmapfingal:
                heatmap("fingal.json");
                return true;
            case R.id.markerfingal:
                marker(R.raw.fingal);
                return true;
            case R.id.heatmapcity:
                heatmap("dublin.json");
                return true;
            case R.id.markercity:
                marker(R.raw.dublin);
                return true;
            case R.id.heatmapsouth:
                heatmap("south.json");
                return true;
            case R.id.markersouth:
                marker(R.raw.south);
                return true;
            case R.id.logout:
                finish();
                firebaseAuth.signOut();
                startActivity(new Intent(this, MainActivity.class));
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
        mMap.clear();
        HeatmapTileProvider mProvider = new HeatmapTileProvider.Builder().data(list).build();
        TileOverlay mOverlay = mMap.addTileOverlay(new TileOverlayOptions().tileProvider(mProvider));
    }
    public void heatmapAll(){
        mMap.clear();
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
        HeatmapTileProvider mProvider = new HeatmapTileProvider.Builder().data(list).build();
        mMap.addTileOverlay(new TileOverlayOptions().tileProvider(mProvider));
    }

    public void marker(int marker) {
        GeoJsonLayer layer = null;
        layer.removeLayerFromMap();
        try {
            layer = new GeoJsonLayer(mMap, marker, getApplicationContext());
            layer.addLayerToMap();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private ArrayList<LatLng> readItems(String resource) throws JSONException, IOException {
        ArrayList<LatLng> list = new ArrayList<LatLng>();
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


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setZoomControlsEnabled(true);

        LatLng currentLocation = new LatLng(53.338444, -6.267202);
        //atLng currentLocation = new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
        //LatLng currentLocation = new LatLng(cLat, cLong);
        mMap.addMarker(new MarkerOptions().position(currentLocation).title("Current Location"));
        mMap.animateCamera( CameraUpdateFactory.newLatLngZoom(currentLocation, 13.0f ));
    }

    @SuppressLint("MissingPermission")
    private void getLastLocation(){
        if (checkPermissions()) {
            if (isLocationEnabled()) {
                fusedLocationClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        Location location = task.getResult();
                        // Got last known location. In some rare situations this can be null
                        if (location == null) {
                            Log.e("TAG", "GPS has failed");
                            requestNewLocationData();
                        }else{
                            Log.e("TAG", "Got last known location");
                            mCurrentLocation = location;
                        }
                    }
                });
            } else {
                Toast.makeText(this, "Turn on location", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        } else {
            requestPermissions();
        }
    }

    @SuppressLint("MissingPermission")
    private void requestNewLocationData(){

        mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        fusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());

    }

    private LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            mPreviousLocation = locationResult.getLastLocation();

            for (Location location : locationResult.getLocations()) {
                // Update UI with location data
                Log.e("TAG", "Location result was found");
                mCurrentLocation = location;
                locations.add(location);
                //cLat = location.getLatitude();
                //cLong = location.getLongitude();
            }
            if (locationResult == null) {
                Log.e("TAG", "Location result = null");
                return;
            }
        }
    };

    private boolean checkPermissions() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        return false;
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(this, new String[] {
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION}, REQUEST_CODE);
    }

    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
                LocationManager.NETWORK_PROVIDER
        );
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}