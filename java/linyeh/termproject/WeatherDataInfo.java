package linyeh.termproject;

/**
 * Created by Eric on 2016/12/16.
 */

public class WeatherDataInfo {
    public String elementName;
    public String parameterName;
    public String parameterValue;
    public String parameterUnit;
    public String startTime;
    public String endTime;

     public  WeatherDataInfo(){};

    public String getElementName() {
        return elementName;
    }

    public String getParameterName() {
        return parameterName;
    }

    public String getParameterValue() {
        return parameterValue;
    }

    public String getParameterUnit() {
        return parameterUnit;
    }

    public String getStartTime() {
        return startTime;
    }

    public String getEndTime() {
        return endTime;
    }
}
