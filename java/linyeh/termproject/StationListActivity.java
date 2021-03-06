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
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.util.Collections;
import java.util.Comparator;

public class StationListActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private ListView listView;
    private OpendataHandler opendata;
    GoogleApiClient mGoogleApiClient;
    private StationListActivity_ListItem adapter;
    private Location here = null;
    private TextView updateTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_station_list);

        mGoogleApiClient = new GoogleApiClient.Builder(this).addConnectionCallbacks(this).addOnConnectionFailedListener(this).addApi(LocationServices.API).build();

        updateTime = (TextView) findViewById(R.id.updateTime);

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

    private com.google.android.gms.location.LocationListener locationListener = new com.google.android.gms.location.LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            Log.d("calllllllllllllllllllll", "callllllllllllllllllllllllllllllllll");
            if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            Log.d("calllllllllllllllllllll", "callllllllllllllllllllllllllllllllll");
            Location initLocation =  LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            if(initLocation != null) {
                here = initLocation;
                Log.d("getlocation", "success!");
            }
            else{
                here = null;
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
        here = null;
        mGoogleApiClient.disconnect();
    }

    @Override
    protected void onPause() {
        super.onPause();
        updater.removeCallbacks(updaterRunnable);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mGoogleApiClient.connect();
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
            updateTimeHandler.removeCallbacks(updateTimeRunnable);
            updateTime.setText("資料更新中...");
            int size = opendata.stations.size();
            if(here == null){
                here = new Location(LocationManager.PASSIVE_PROVIDER);
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
            /*for(int i=size-1; i>=0; --i){
                if(!opendata.stations.get(i).isActive){
                    adapter.remove(opendata.stations.get(i));
                }
            }*/
            listView.setAdapter(adapter);
            if(scrollPosition != -1) {
                Log.d("smooth", Integer.toString(scrollPosition));
                listView.setSelection(scrollPosition);
            }
            updateTimeHandler.post(updateTimeRunnable);
            updater.postDelayed(updaterRunnable, 5000);
        }
    };

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
}
