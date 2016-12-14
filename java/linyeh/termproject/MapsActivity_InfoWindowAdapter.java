package linyeh.termproject;

import android.app.Activity;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

public class MapsActivity_InfoWindowAdapter implements GoogleMap.InfoWindowAdapter {
    private Activity context;
    public MapsActivity_InfoWindowAdapter(Activity in_context){
        context = in_context;
    }
    @Override
    public View getInfoWindow(Marker marker){
        return null;
    }
    @Override
    public View getInfoContents(Marker marker){
        View view = context.getLayoutInflater().inflate(R.layout.activity_maps_infowindow, null);
        TextView title = (TextView) view.findViewById(R.id.title);
        TextView snippet = (TextView) view.findViewById(R.id.snippet);
        title.setText(marker.getTitle());
        snippet.setText(marker.getSnippet());
        return view;
    }
}
