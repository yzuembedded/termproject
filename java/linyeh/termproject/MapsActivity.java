package linyeh.termproject;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private LocalWeatherHandler localweather;
    public GoogleMap mMap;
    private OpendataHandler opendata;
    GoogleApiClient mGoogleApiClient;
    private Handler markerUpdater = new Handler();
    private TextView updateTime;
    private TextView Tvweather;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        mGoogleApiClient = new GoogleApiClient.Builder(this).addConnectionCallbacks(this).addOnConnectionFailedListener(this).addApi(LocationServices.API).build();
        Tvweather = (TextView) findViewById(R.id.TV_weather);
        updateTime = (TextView) findViewById(R.id.updateTime);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    private com.google.android.gms.location.LocationListener locationListener = new com.google.android.gms.location.LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            Log.d("calllllllllllllllllllll", "callllllllllllllllllllllllllllllllll");
            if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            Log.d("calllllllllllllllllllll", "callllllllllllllllllllllllllllllllll");
            Location initLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            if(initLocation != null) {
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(initLocation.getLatitude(), initLocation.getLongitude()), 15));
                Log.d("getlocation", "success!");
            }
            else{
                Log.d("onConnectedgetlocation", "fail");
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(24.969820,121.266557), 15));
            }
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            mGoogleApiClient.disconnect();
        }
    };

    @Override
    public void onConnected(Bundle connectionHint) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        Log.d("onConnected", "onConnected");
        //LocationRequest locationRequest = LocationRequest.create().setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY).setInterval(10000).setFastestInterval(2000);
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY).setInterval(10000).setFastestInterval(2000).setExpirationDuration(5000);
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, locationRequest, locationListener);
    }

    @Override
    public void onConnectionSuspended(int s){
        Log.d("suspend", "suspend");
    }

    @Override
    public void onConnectionFailed(ConnectionResult r){
        Log.d("getlocation", "fail");
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(24.969820,121.266557), 15));
        mGoogleApiClient.disconnect();
    }

    @Override
    protected void onPause() {
        super.onPause();
        markerUpdater.removeCallbacks(markerUpdaterRunnable);
    }

    @Override
    protected void onResume(){
        super.onResume();
    }

    private Marker marker_isClicking = null;
    private GoogleMap.OnMarkerClickListener markerClickListener = new GoogleMap.OnMarkerClickListener() {
        @Override
        public boolean onMarkerClick(Marker marker) {
            marker_isClicking = marker;
            return false;
        }
    };
    private GoogleMap.OnMapClickListener onMapClickListener = new GoogleMap.OnMapClickListener() {
        @Override
        public void onMapClick(LatLng latLng) {
            marker_isClicking = null;
        }
    };

    private Location mapCameraLocation = new Location(LocationManager.PASSIVE_PROVIDER);

    private Handler localweahterHandler = new Handler();
    private Runnable localweatherRunnable = new Runnable() {
        @Override
        public void run() {
            CameraPosition c = mMap.getCameraPosition();
            mapCameraLocation.setLatitude(c.target.latitude);
            mapCameraLocation.setLongitude(c.target.longitude);
            localweather = new LocalWeatherHandler(localnet);
        }
    };

    @Override
    public void onMapReady(GoogleMap googleMap) { // YZU      24.9699      121.266
        mMap = googleMap;
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(23.973875, 120.982024)));
        mMap.setInfoWindowAdapter(new MapsActivity_InfoWindowAdapter(MapsActivity.this));
        mMap.setOnMarkerClickListener(markerClickListener);
        mMap.setOnMapClickListener(onMapClickListener);
        mMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {
                localweahterHandler.postDelayed(localweatherRunnable, 1500);
            }
        });
        mMap.setOnCameraMoveStartedListener(new GoogleMap.OnCameraMoveStartedListener() {
            @Override
            public void onCameraMoveStarted(int i) {
                localweahterHandler.removeCallbacks(localweatherRunnable);
            }
        });
        opendata = new OpendataHandler(net);

        Intent in_intent = getIntent();
        Bundle bundle = in_intent.getExtras();
        if(bundle != null && bundle.containsKey("Lat") && bundle.containsKey("Lng") && bundle.containsKey("stationName")){
            LatLng p = new LatLng(bundle.getDouble("Lat"), bundle.getDouble("Lng"));
            marker_isClicking = mMap.addMarker(new MarkerOptions().title(bundle.getString("stationName")).position(p));
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(p, 15));
        }
        else{
            mGoogleApiClient.connect();
        }
        icon_black = BitmapDescriptorFactory.fromResource(R.drawable.ic_place_black_24dp);
        icon_red = BitmapDescriptorFactory.fromResource(R.drawable.ic_place_black_24dp_red);
        icon_green = BitmapDescriptorFactory.fromResource(R.drawable.ic_place_black_24dp_green);
        icon_orange = BitmapDescriptorFactory.fromResource(R.drawable.ic_place_black_24dp_orange);
        Log.d("onMapReady", "called");
    }

    private Runnable markerUpdaterRunnable = new Runnable() {
        @Override
        public void run() {
            opendata = new OpendataHandler(net);
        }
    };

    BitmapDescriptor icon_black;
    BitmapDescriptor icon_red;
    BitmapDescriptor icon_green;
    BitmapDescriptor icon_orange;

    private void drawMarker(uBikeStationInfo info, boolean needShowInfo){
        if(info.stationLatlng != null) {
            LatLng latLng = new LatLng(info.stationLatlng.getLatitude(), info.stationLatlng.getLongitude());
            MarkerOptions t = new MarkerOptions();
            t.position(latLng).title(info.stationName).snippet(info.getContent());
            if(!info.isActive)
                t.icon(icon_black);
            else if(info.usableNum == 0)
                t.icon(icon_orange);
            else if(info.returanableNum == 0)
                t.icon(icon_red);
            else
                t.icon(icon_green);
            Marker m = mMap.addMarker(t);
            if(needShowInfo) {
                marker_isClicking = m;
                m.showInfoWindow();
            }
        }
    }

    private int updateTimeSec = 5;
    private Handler updateTimeHandler = new Handler();
    private Runnable updateTimeRunnable = new Runnable() {
        @Override
        public void run() {
            if(updateTimeSec >= 0) {
                updateTime.setText("將於" + Integer.toString(updateTimeSec) + "後更新");
                updateTimeSec = updateTimeSec - 1;
                updateTimeHandler.postDelayed(updateTimeRunnable, 1000);
            }
            else{
                updateTimeSec = 5;
            }
        }
    };

    private Handler net = new Handler(){
        @Override
        public void handleMessage(Message msg){
            switch(msg.what) {
                case 200:
                    updateTimeHandler.removeCallbacks(updateTimeRunnable);
                    updateTime.setText("資料更新中...");
                    String title_isClicking = null;
                    if(marker_isClicking != null)
                        title_isClicking = marker_isClicking.getTitle();
                    mMap.clear();
                    for (int i = 0; i < opendata.stations.size(); ++i) {
                        drawMarker(opendata.stations.get(i), marker_isClicking != null && opendata.stations.get(i).stationName.equals(title_isClicking));
                    }
                    updateTimeHandler.post(updateTimeRunnable);
                    markerUpdater.postDelayed(markerUpdaterRunnable, 5000);
                    break;
            }
        }
    };
    private Handler localnet = new Handler(){

        @Override
        public void handleMessage(Message msg){
            switch(msg.what) {
                case 200:
                    Tvweather.setText(localweather.LocalWeather.get(localweather.determineTown(mapCameraLocation)).LocalName+localweather.LocalWeather.get(localweather.determineTown(mapCameraLocation)).LocalWeatherData.get(localweather.scope).elementValue);
                    break;
            }
        }
    };
}
