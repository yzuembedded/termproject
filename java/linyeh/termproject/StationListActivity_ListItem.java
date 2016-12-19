package linyeh.termproject;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.vision.text.Text;

import java.util.ArrayList;
import java.util.List;

public class StationListActivity_ListItem extends ArrayAdapter<uBikeStationInfo> {

    private Activity context;
    private ArrayList<uBikeStationInfo> stationList;
    //private int[] id;

    public StationListActivity_ListItem(Activity context, ArrayList<uBikeStationInfo> stationList) {
        super(context, R.layout.activity_station_list_item, stationList);
        this.context = context;
        this.stationList = stationList;
    }
    @Override
    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView= inflater.inflate(R.layout.activity_station_list_item, null, true);
        TextView txtTitle = (TextView) rowView.findViewById(R.id.title);
        TextView txtContent = (TextView) rowView.findViewById(R.id.content);
        TextView txtDistance = (TextView) rowView.findViewById(R.id.distance);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.img);
        txtTitle.setText(stationList.get(position).stationName);
        txtDistance.setText("距離此處：" + Integer.toString(Math.round(stationList.get(position).distance)) + "公尺");
        txtContent.setText("可借數量：" + stationList.get(position).usableNum + "    可還數量：" + stationList.get(position).returanableNum);
        imageView.setImageResource(R.drawable.ic_place_black_24dp);
        return rowView;
    }
}