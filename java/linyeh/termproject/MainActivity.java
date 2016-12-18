package linyeh.termproject;

import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.support.v7.app.*;
import android.os.Message;
import android.os.Bundle;
import android.util.Log;
import android.widget.*;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.view.*;
import android.os.Handler;

import com.google.android.gms.awareness.state.Weather;
import com.google.android.gms.vision.text.Text;

public class MainActivity extends AppCompatActivity {
    private TextView WX;
    private TextView IC;
    private TextView T;

    private ListView listView;
    private WeatherdataHandler weatherH;
    private String[] list = {"  租賃站地圖","  租賃站列表","  路線規劃","  使用說明"};
    Integer[] imageId = {
            R.drawable.ic_place_black_24dp,
            R.drawable.ic_directions_bike_black_24dp,
            R.drawable.ic_navigation_black_24dp,
            R.drawable.ic_event_note_black_24dp,
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        WX=(TextView)findViewById(R.id.WXtext);
        IC=(TextView)findViewById(R.id.ICtext);
        T=(TextView)findViewById(R.id.Ttext);
        weatherH=new WeatherdataHandler(net);
        CustomList adapter = new CustomList(MainActivity.this, list, imageId);
        listView=(ListView)findViewById(R.id.listView);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Toast.makeText(MainActivity.this, "You Clicked at " +list[+ position], Toast.LENGTH_SHORT).show();
                Intent action = new Intent();
                switch(position){
                    case 0:
                        action.setClass(MainActivity.this, MapsActivity.class);
                        startActivity(action);
                        break;
                    case 1:
                        action.setClass(MainActivity.this, StationListActivity.class);
                        startActivity(action);
                        break;
                }
            }
        });
    }
    private Handler net = new Handler(){

        @Override
        public void handleMessage(Message msg){
            switch(msg.what) {
                case 200:
                    Log.d("scope",Integer.toString(weatherH.scope));
                    if(weatherH.scope==1)
                    {

                        WX.setText(weatherH.weatherdata.get(0).parameterName);
                        if(weatherH.weatherdata.get(3).parameterName.equals(weatherH.weatherdata.get(4).parameterName))
                        {
                            String str= weatherH.weatherdata.get(3).parameterName;
                            T.setText(str);
                        }
                        else {
                            String str= weatherH.weatherdata.get(3).parameterName + "~" + weatherH.weatherdata.get(4).parameterName;
                            T.setText(str);
                        }
                        IC.setText(weatherH.weatherdata.get(2).parameterName);
                    }
                    else   if(weatherH.scope==2)
                    {
                        WX.setText(weatherH.weatherdata1.get(0).parameterName);
                        if(weatherH.weatherdata1.get(3).parameterName.equals(weatherH.weatherdata1.get(4).parameterName))
                        {
                            String str= weatherH.weatherdata1.get(3).parameterName;
                            T.setText(str);
                        }
                        else {
                            String str= weatherH.weatherdata1.get(3).parameterName + "~" + weatherH.weatherdata1.get(4).parameterName;
                            T.setText(str);
                        }
                        IC.setText(weatherH.weatherdata1.get(2).parameterName);

                    }
                    else
                    {
                        WX.setText(weatherH.weatherdata2.get(0).parameterName);
                        if(weatherH.weatherdata2.get(3).parameterName.equals(weatherH.weatherdata2.get(4).parameterName))
                        {
                            String str= weatherH.weatherdata2.get(3).parameterName;
                            T.setText(str);
                        }
                        else {
                            String str= weatherH.weatherdata2.get(3).parameterName + "~" + weatherH.weatherdata2.get(4).parameterName;
                            T.setText(str);
                        }
                        IC.setText(weatherH.weatherdata2.get(2).parameterName);

                    }

                    break;
            }
        }
    };

}
