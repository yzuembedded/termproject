package linyeh.termproject;

import android.location.Location;
import android.location.LocationManager;

import java.util.ArrayList;

/**
 * Created by YuehJuLin on 2016/12/20.
 */

public class LocalWeatherInfo2 {
    public String LocalName;
    public ArrayList<LocalWeatherInfo> LocalWeatherData;
    public Location stationLatlng;
    public LocalWeatherInfo2(){
        LocalWeatherData=new ArrayList<LocalWeatherInfo>();
        stationLatlng = new Location(LocationManager.PASSIVE_PROVIDER);
    }


}
