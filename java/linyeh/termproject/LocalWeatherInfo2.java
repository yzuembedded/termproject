package linyeh.termproject;

import java.util.ArrayList;

/**
 * Created by YuehJuLin on 2016/12/20.
 */

public class LocalWeatherInfo2 {
    public String LocalName;
    public ArrayList<LocalWeatherInfo> LocalWeatherData;
    public String lat;
    public String lon;
    public LocalWeatherInfo2(){
        LocalWeatherData=new ArrayList<LocalWeatherInfo>();
    }


}
