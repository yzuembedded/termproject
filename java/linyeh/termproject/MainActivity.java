package linyeh.termproject;

import android.content.Intent;
import android.support.v7.app.*;

import android.os.Bundle;
import android.widget.*;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.view.*;

public class MainActivity extends AppCompatActivity {

    private ListView listView;
    private String[] list = {"  租賃地圖","  租賃站列表","  體能計算","  使用說明"};
    private ArrayAdapter<String> listAdapter;
    Integer[] imageId = {
            R.drawable.ic_place_black_24dp,
            R.drawable.ic_directions_bike_black_24dp,
            R.drawable.ic_timer_black_48dp,
            R.drawable.ic_event_note_black_24dp,
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        CustomList adapter = new
                CustomList(MainActivity.this, list, imageId);
        listView=(ListView)findViewById(R.id.listView);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Toast.makeText(MainActivity.this, "You Clicked at " +list[+ position], Toast.LENGTH_SHORT).show();
                switch(position){
                    case 0:
                        Intent action = new Intent();
                        action.setClass(MainActivity.this, MapsActivity.class);
                        startActivity(action);
                        finish();
                        break;
                }
            }
        });

    }

}
