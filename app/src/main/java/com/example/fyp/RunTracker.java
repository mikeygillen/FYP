package com.example.fyp;

import java.util.List;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesClient.OnConnectionFailedListener;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.LocationSource.OnLocationChangedListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import android.location.Location;
import android.location.LocationListener;
import android.os.Build;
import android.os.Bundle;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.widget.Toast;

public class RunTracker extends Activity implements GooglePlayServicesClient.ConnectionCallbacks, OnConnectionFailedListener, LocationListener{

    private OnLocationChangedListener mListener;
    private GoogleMap mMap;
    private GoogleApiClient mLocationClient;
    private Location mCurrentLocation;
    private LocationRequest mLocationRequest;
    List<LatLng>routePoints;

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);


        mMap =((MapFragment)getFragmentManager().findFragmentById(R.id.map)).getMap();
        mMap.setMyLocationEnabled(true);
        mLocationClient = new LocationClient(this,this, this);
        mLocationRequest = LocationRequest.create();

    }

    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }*/

    @Override
    public void onConnected(Bundle dataBundle) {

        Toast.makeText(this, "Connected", Toast.LENGTH_SHORT).show();
        Location location = mLocationClient.getLastLocation();
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        routePoints.add(latLng);
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 17);
        mMap.animateCamera(cameraUpdate);

    }

    @Override
    public void onDisconnected() {

        Toast.makeText(this, "Disconnected. Please re-connect.",
                Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    public void onStart(){
        super.onStart();
        mLocationClient.connect();
    }

    public void onStop(){
        mLocationClient.disconnect();
        super.onStop();
    }

    public void activate(OnLocationChangedListener listener){
        mListener = listener;
    }

    public void deactivate(){
        mListener = null;
    }

    @Override
    public void onLocationChanged(Location location) {

        if(mListener != null){
            mListener.onLocationChanged(location);
        }

        LatLng mapPoint = new LatLng(location.getLatitude(), location.getLongitude());
        routePoints.add(mapPoint);
        Polyline route = mMap.addPolyline(new PolylineOptions());
        route.setPoints(routePoints);


    }
    @Override
    public void onProviderDisabled(String arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onProviderEnabled(String arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
        // TODO Auto-generated method stub

    }
}
