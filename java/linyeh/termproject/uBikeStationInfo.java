package linyeh.termproject;

import android.location.Location;
import android.location.LocationManager;

/**
 * Created by Eric on 2016/12/15.
 */

public class uBikeStationInfo {
    public String stationName;
    public int totalNum;
    public int usableNum;
    public int returanableNum;
    public Location stationLatlng;
    public String address;
    public boolean isActive;
    public String updateTime;

    public uBikeStationInfo(){
        stationLatlng = new Location(LocationManager.PASSIVE_PROVIDER);
    }

    public String getContent(){
        StringBuffer sb = new StringBuffer();
        sb.append("總數量：");
        sb.append(totalNum);
        sb.append("\n可借數量：");
        sb.append(usableNum);
        sb.append("\n可還數量：");
        sb.append(returanableNum);
        return sb.toString();
    }
}
