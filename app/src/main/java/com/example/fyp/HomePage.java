package com.example.fyp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.TileOverlay;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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

public class HomePage extends AppCompatActivity implements OnMapReadyCallback{

    private GoogleMap mMap;

    private LocationRequest mLocationRequest;
    ArrayList<LatLng> locations = new ArrayList();
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        firebaseAuth = FirebaseAuth.getInstance();
        if(firebaseAuth.getCurrentUser() == null){
            startActivity(new Intent(this, LoginActivity.class));
        }
        FirebaseUser user = firebaseAuth.getCurrentUser();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.MainMap);
        mapFragment.getMapAsync(this);
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.mapheading, menu);
        return true;
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
                //Toast.makeText(this, "Display all markers for fingal", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.heatmapcity:
                heatmap("dublin.json");
                return true;
            case R.id.markercity:
                marker(R.raw.dublin);
                return true;
            case R.id.logout:
                firebaseAuth.signOut();
                finish();
                startActivity(new Intent(this, LoginActivity.class));
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

        LatLng currentLocation = new LatLng(53.338743, -6.267030);
        mMap.addMarker(new MarkerOptions().position(currentLocation).title("Current Location"));
        mMap.animateCamera( CameraUpdateFactory.newLatLngZoom(currentLocation, 13.0f ));
    }
}
