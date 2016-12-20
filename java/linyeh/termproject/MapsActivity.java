package linyeh.termproject;

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

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {
    private LocalWeatherHandler localweather;
    public GoogleMap mMap;
    private OpendataHandler opendata;
    private LocationManager locationManager;
    private LocationListener locationListener;
    private Handler markerUpdater = new Handler();
    private TextView updateTime;
    private TextView Tvweather;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        Tvweather=(TextView)findViewById(R.id.TV_weather);

        updateTime = (TextView) findViewById(R.id.updateTime);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        markerUpdater.removeCallbacks(markerUpdaterRunnable);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) { return; }
        else {
            locationManager.removeUpdates(locationListener);
        }
        locationManager = null;
        locationListener = null;
    }

    @Override
    protected void onResume(){
        super.onResume();
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                if(location != null) {
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 15));
                    Log.d("onLocationChanged", Double.toString(location.getLatitude()) + " " + Double.toString(location.getLongitude()));
                    if (ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                            && ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    } else {
                        Log.d("onLocationChanged", "listenerRemove1");
                        locationManager.removeUpdates(locationListener);
                    }
                }
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {

            }
        };
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
            localweather = new LocalWeatherHandler(localnet);
            CameraPosition c = mMap.getCameraPosition();
            mapCameraLocation.setLatitude(c.target.latitude);
            mapCameraLocation.setLongitude(c.target.longitude);
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

        Location initLocation = null;

        Intent in_intent = getIntent();
        Bundle bundle = in_intent.getExtras();
        if(bundle != null && bundle.containsKey("Lat") && bundle.containsKey("Lng")){
            initLocation = new Location(LocationManager.PASSIVE_PROVIDER);
            initLocation.setLatitude(bundle.getDouble("Lat"));
            initLocation.setLongitude(bundle.getDouble("Lng"));
            marker_isClicking = mMap.addMarker(new MarkerOptions().title(bundle.getString("stationName")).position(new LatLng(bundle.getDouble("Lat"), bundle.getDouble("Lng"))));
        }
        else {
            initLocation = getCurrentLocation();
            if (ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            } else {
                Log.d("onLocationChanged", "listenerRemove2");
                //locationManager.removeUpdates(locationListener);
            }
        }
        if (initLocation != null) {
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(initLocation.getLatitude(), initLocation.getLongitude()), 15));
        }
        else{
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(24.9699,121.266), 15));
        }
    }

    private Runnable markerUpdaterRunnable = new Runnable() {
        @Override
        public void run() {
            opendata = new OpendataHandler(net);
        }
    };

    private void drawMarker(uBikeStationInfo info, boolean needShowInfo){
        if(info.stationLatlng != null) {
            LatLng latLng = new LatLng(info.stationLatlng.getLatitude(), info.stationLatlng.getLongitude());
            BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.ic_place_black_24dp);
            Marker m = mMap.addMarker(new MarkerOptions().position(latLng).title(info.stationName).snippet(info.getContent()).icon(icon));
            if(needShowInfo) {
                marker_isClicking = m;
                m.showInfoWindow();
            }
            //mMap.animateCamera(CameraUpdateFactory.zoomTo(15));
            //mMap.moveCamera(CameraUpdateFactory.newLatLng(yzu));
            //mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(yzu, 15));
        }
    }

    private Location getCurrentLocation() {
        boolean isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER),
                isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        Location location = null;
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return null;
        }
        if (isGPSEnabled) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 5000, locationListener);
            location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        }
        else if(isNetworkEnabled){
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 5000, locationListener);
            location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        }
        return location;
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
                        if (opendata.stations.get(i).isActive) {
                            drawMarker(opendata.stations.get(i), marker_isClicking != null && opendata.stations.get(i).stationName.equals(title_isClicking));
                        }
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
                                                    //determineTown(Location) 放在0的位置
                    break;
            }
        }
    };
}
