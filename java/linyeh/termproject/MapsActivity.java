package linyeh.termproject;

import android.content.Context;
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

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    public GoogleMap mMap;
    private OpendataHandler opendata;
    private LocationManager locationManager;
    private LocationListener locationListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        /*if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) { return; }
        else {
            locationManager.removeUpdates(locationListener);
        }*/
        locationManager = null;
        //locationListener = null;
    }

    @Override
    protected void onResume(){
        super.onResume();
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                if(location != null)
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 15));
                Log.d("onLocationChanged", Double.toString(location.getLatitude())+" "+Double.toString(location.getLongitude()));
                if (ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) { return; }
                else {
                    Log.d("onLocationChanged", "listenerRemove1");
                    locationManager.removeUpdates(locationListener);
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

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setInfoWindowAdapter(new MapsActivity_InfoWindowAdapter(MapsActivity.this));

        String opendataurl = "http://data.gov.tw/iisi/logaccess/66022?dataUrl=http://data.tycg.gov.tw/opendata/datalist/datasetMeta/download?id=5ca2bfc7-9ace-4719-88ae-4034b9a5a55c&rid=a1b4714b-3b75-4ff8-a8f2-cc377e4eaa0f&ndctype=JSON&ndcnid=28228";
        opendata = new OpendataHandler(opendataurl, net);

        Location initLocation = null;
        initLocation = getCurrentLocation();
        if(initLocation != null) {
            if (ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) { return; }
            else {
                Log.d("onLocationChanged", "listenerRemove2");
                locationManager.removeUpdates(locationListener);
            }
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(initLocation.getLatitude(), initLocation.getLongitude()), 15));
        }
        /*Thread waitInitLocationThread = new Thread(new Runnable() {
            @Override
            public void run() {
                Location initLocation = null;
                while((initLocation = getCurrentLocation()) == null) Log.d("gps", "searching...");
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(initLocation.getLatitude(), initLocation.getLongitude()), 15));
            }
        });
        waitInitLocationThread.start();*/

        /*if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) { return; }
        else {
            Location initLocation = null;
            while((initLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)) == null);
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(initLocation.getLatitude(), initLocation.getLongitude()), 15));
        }*/
    }

    private void drawMarker(uBikeStationInfo info){
        if(info.stationLatlng != null) {
            LatLng yzu = new LatLng(info.stationLatlng.getLatitude(), info.stationLatlng.getLongitude());
            BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.ic_place_black_24dp);
            mMap.addMarker(new MarkerOptions().position(yzu).title(info.stationName).snippet(info.getContent()).icon(icon));
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
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 5000, locationListener);
            location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        }
        else if(isNetworkEnabled){
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 5000, locationListener);
            location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        }
        return location;
    }

    private Handler net = new Handler(){
        @Override
        public void handleMessage(Message msg){
            switch(msg.what) {
                case 200:
                    for (int i = 0; i < opendata.stations.size(); ++i)
                        drawMarker(opendata.stations.get(i));
                    break;
            }
        }
    };
}
