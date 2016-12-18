package linyeh.termproject;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

public class StationListActivity extends AppCompatActivity {

    private ListView listView;
    private OpendataHandler opendata;
    StationListActivity_ListItem adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_station_list);

        opendata = new OpendataHandler(handler);

        listView = (ListView) findViewById(R.id.stationListListView);
    }

    @Override
    protected  void onPause(){
        super.onPause();
        updater.removeCallbacks(updaterRunnable);
        Log.d("onPause", "removeCallbacks");
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
            int scrollPosition = -1;
            if(adapter != null) {
                scrollPosition = listView.getFirstVisiblePosition();
                Log.d("scroll", Integer.toString(scrollPosition));
            }
            int size = opendata.stations.size();
            int[] id = new int[size];
            for(int i=0; i<size; ++i)
                id[i] = i;
            adapter = new StationListActivity_ListItem(StationListActivity.this, opendata.stations, id);
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
