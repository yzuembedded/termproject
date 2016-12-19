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
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.Collections;
import java.util.Comparator;

public class StationListActivity extends AppCompatActivity {

    private ListView listView;
    private OpendataHandler opendata;
    private StationListActivity_ListItem adapter;
    //private int[] id;
    private LocationManager locationManager;
    private LocationListener locationListener;
    private Location here = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_station_list);

        opendata = new OpendataHandler(handler);

        /*here = new Location(LocationManager.PASSIVE_PROVIDER);
        here.setLatitude(24.9699);
        here.setLongitude(121.266);*/

        listView = (ListView) findViewById(R.id.stationListListView);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long R_id) {
                Intent action = new Intent();
                action.setClass(StationListActivity.this, MapsActivity.class);
                Bundle bundle = new Bundle();
                bundle.putDouble("Lat", opendata.stations.get(position).stationLatlng.getLatitude());
                bundle.putDouble("Lng", opendata.stations.get(position).stationLatlng.getLongitude());
                bundle.putString("stationName", opendata.stations.get(position).stationName);
                action.putExtras(bundle);
                startActivity(action);
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        updater.removeCallbacks(updaterRunnable);
        Log.d("onPause", "removeCallbacks");
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) { return; }
        else {
            locationManager.removeUpdates(locationListener);
        }
        locationManager = null;
        locationListener = null;
    }

    @Override
    protected void onResume() {
        super.onResume();

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                if (location != null) {
                    here = location;
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
        Location initLocation = null;
        initLocation = getCurrentLocation();
        if (initLocation != null) {
            here = initLocation;
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

    private Handler updater = new Handler();

    private Runnable updaterRunnable = new Runnable() {
        @Override
        public void run() {
            opendata = new OpendataHandler(handler);
        }
    };
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg){
            int size = opendata.stations.size();

            if(here == null){
                here.setLatitude(24.9699);
                here.setLongitude(121.266);
            }
            for (int i = 0; i < size; ++i) {
                float[] d = new float[1];
                Location.distanceBetween(here.getLatitude(), here.getLongitude(), opendata.stations.get(i).stationLatlng.getLatitude(), opendata.stations.get(i).stationLatlng.getLongitude(), d);
                opendata.stations.get(i).distance = d[0];
            }
            Collections.sort(opendata.stations, new Comparator<uBikeStationInfo>() {
                @Override
                public int compare(uBikeStationInfo o1, uBikeStationInfo o2) {
                    return Float.compare(o1.distance, o2.distance);
                }
            });

            int scrollPosition = -1;
            if(adapter != null) {
                scrollPosition = listView.getFirstVisiblePosition();
                Log.d("scroll", Integer.toString(scrollPosition));
            }

            /*id = new int[size];
            for(int i=0; i<size; ++i)
                id[i] = i;*/
            adapter = new StationListActivity_ListItem(StationListActivity.this, opendata.stations);
            for(int i=size-1; i>=0; --i){
                if(!opendata.stations.get(i).isActive){
                    adapter.remove(opendata.stations.get(i));
                }
            }
            listView.setAdapter(adapter);
            if(scrollPosition != -1) {
                Log.d("smooth", Integer.toString(scrollPosition));
                listView.setSelection(scrollPosition);
            }
            updater.postDelayed(updaterRunnable, 5000);
        }
    };
}
