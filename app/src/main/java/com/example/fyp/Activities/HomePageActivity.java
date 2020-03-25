package com.example.fyp.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
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

import com.example.fyp.Classes.Run;
import com.example.fyp.Fragments.EditProfileFragment;
import com.example.fyp.Helper.Helper;
import com.example.fyp.Fragments.PairUsersFragment;
import com.example.fyp.Interface.Interface;
import com.example.fyp.R;
import com.example.fyp.Classes.Route;
import com.example.fyp.Fragments.RouteFragment;
import com.example.fyp.Fragments.StatsFragment;
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
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.maps.android.data.geojson.GeoJsonLayer;
import com.google.maps.android.heatmaps.HeatmapTileProvider;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;

public class HomePageActivity extends AppCompatActivity implements Interface, StatsFragment.OnFragmentInteractionListener, RouteFragment.OnFragmentInteractionListener, PairUsersFragment.OnFragmentInteractionListener, EditProfileFragment.OnFragmentInteractionListener, BottomNavigationView.OnNavigationItemSelectedListener,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, OnMapReadyCallback, LocationListener {
    private static final String TAG = "HomePageActivity";

    private static GoogleMap mMap;
    private LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            locationResult.getLastLocation();

            for (Location ignored : locationResult.getLocations()) {
                Log.e("TAG", "Location result was found");
            }
        }
    };
    private FusedLocationProviderClient fusedLocationClient;
    private GoogleApiClient googleApiClient;
    private Marker mCurrentMarker;

    private static final int REQUEST_CODE = 101;
    private float mDistanceCovered;
    private long startTime, endTime;

    private Button startRun, endRun;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();
    private DatabaseReference newRoute = FirebaseDatabase.getInstance().getReference("Routes");
    private DatabaseReference newRun = FirebaseDatabase.getInstance().getReference("Runs");
    private String userid = mUser.getUid();
    private DatabaseReference mUserRef = FirebaseDatabase.getInstance().getReference("Users").child(userid);

    private static ArrayList<Location> locations = new ArrayList<>();
    private ArrayList<LatLng> LatLongs = new ArrayList<>();

    private PolylineOptions polylineOptions;

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

        if (isLocationEnabled()){
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
        } else {
            Log.d(TAG, "location is null ...............");
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
            final String t = Helper.secondToHHMMSS(elapsedTime());
            final double p = Helper.calculatePace(elapsedTime(), calculateDistance(locations.get(0), locations.get(locations.size()-1)));

            endTime = System.currentTimeMillis();
            SimpleDateFormat formatter = new SimpleDateFormat("dd MMMM yyyy");
            String strDate = formatter.format(endTime);

            Log.d(TAG, "String Date = " + strDate);

            String routeKey =  newRoute.push().getKey();
            final Route route = new Route(d, LatLongs, userid, strDate);

            newRoute.child(routeKey).setValue(route, new DatabaseReference.CompletionListener() {
                public void onComplete(DatabaseError error, DatabaseReference ref) {
                    Log.d("ssd", "onComplete: " + error);
                }
            });

            String runKey = newRun.push().getKey();
            final Run run = new Run(t, p, userid, routeKey);

            newRun.child(runKey).setValue(run, new DatabaseReference.CompletionListener() {
                public void onComplete(DatabaseError error, DatabaseReference ref) {
                    Log.d("ssd", "onComplete: " + error);
                }
            });

            Log.d(TAG, "Route Tracking Finished");
        }
    }

    // Assume this calculates precise distance
    private float calculateDistance(Location start, Location end){
        float distanceDiff = start.distanceTo(end); // Return meter unit
            return mDistanceCovered + distanceDiff;
    }

    public long elapsedTime() {
        return (System.currentTimeMillis() - startTime) / 1000;
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
        Fragment fragment = null;

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
        mMap.clear();
        if (list != null) {
            new HeatmapTileProvider.Builder().data(list).build();
        }
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
        HeatmapTileProvider mProvider = null;
        if (list != null) {
            mProvider = new HeatmapTileProvider.Builder().data(list).build();
        }
        mMap.addTileOverlay(new TileOverlayOptions().tileProvider(mProvider));
    }

    public void marker(int marker) {
        GeoJsonLayer layer;
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

    @SuppressLint("MissingPermission")
    private void getLastLocation(){
        if (isLocationEnabled()) {
            fusedLocationClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if (location != null){
                        Log.e("TAG", "Got last known location");
                    }else{
                            Log.e("TAG", "GPS has failed");
                            requestNewLocationData();
                        }
                    }
                });
        } else {
            Toast.makeText(this, "Turn on location", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(intent);
        }
    }


    @SuppressLint("MissingPermission")
    private void requestNewLocationData(){

        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        fusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (checkPermissions()){
            getLastLocation();
            mMap.setMyLocationEnabled(true);
        }else{
            requestPermissions();
            mMap.setMyLocationEnabled(true);
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        if (mCurrentMarker != null) {
            mCurrentMarker.remove();
        }
        //Place current location marker
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("Current Position");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
        mCurrentMarker = mMap.addMarker(markerOptions);

        //move map camera
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(11));

        //stop location updates
        if (fusedLocationClient != null) {
            fusedLocationClient.removeLocationUpdates(mLocationCallback);
        }
    }

    public static void mapRoute(ArrayList<LatLng> routePoints) {
        Log.d(TAG, "mapRoute beginning.... ");

        Log.d(TAG, "mapRoute: routePoints = " + routePoints);

        int length = routePoints.size();
        // Store a data object with the polygon,
        // used here to indicate an arbitrary type.
        PolylineOptions poly = new PolylineOptions().clickable(true);

        for(int i = 0; i < length; i++) {
            if (routePoints.get(i) != null) {
                LatLng latLong = new LatLng(routePoints.get(i).latitude, routePoints.get(i).longitude);
                poly.add(latLong);
            }

            poly.width(5).color(Color.RED).geodesic(true);
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
    public void onConnectionSuspended(int i){
        Log.e("HomePageActivity", "Connection suspended");
    }

    @Override
    public void onConnected(@Nullable Bundle bundle){
        if(!checkPermissions()){
            requestPermissions();
        }else{
            getLastLocation();
        }
    }

    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
                LocationManager.NETWORK_PROVIDER
        );
    }

    private boolean checkPermissions() {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.ACCESS_BACKGROUND_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}