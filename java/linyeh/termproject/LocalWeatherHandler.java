package linyeh.termproject;

/**
 * Created by YuehJuLin on 2016/12/20.
 */
import android.icu.text.SimpleDateFormat;
import android.location.Location;
import android.location.LocationManager;
import android.os.Handler;
import android.os.Message;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import android.util.JsonReader;
import android.util.Log;
import java.io.IOException;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Date;
import java.text.ParseException;
public class LocalWeatherHandler {
    public String data;
    public int scope;
    private Handler handler;
    public ArrayList<Location> location;
    public ArrayList<LocalWeatherInfo2> LocalWeather;
    public LocalWeatherHandler(Handler in_handler)
    {
        this.handler=in_handler;
        location=new ArrayList<Location>();

        LocalWeather=new ArrayList<LocalWeatherInfo2>();
        init();
        Thread dataThread = new Thread(dataRunnable);
        dataThread.start();

    }
    public void init()
    {
        for(int i=0;i<13;i++) {
            Location temp = new Location(LocationManager.PASSIVE_PROVIDER);
            if(i==0) {
                temp.setLatitude(Double.parseDouble("24.59"));
                temp.setLongitude(Double.parseDouble("121.18"));
            }
            else if(i==1)
            {
                temp.setLatitude(Double.parseDouble("24.49"));
                temp.setLongitude(Double.parseDouble("121.21"));
            }
            else if(i==2)
            {
                temp.setLatitude(Double.parseDouble("24.57"));
                temp.setLongitude(Double.parseDouble("121.13"));
            }
            else if(i==3)
            {
                temp.setLatitude(Double.parseDouble("25.02"));
                temp.setLongitude(Double.parseDouble("121.04"));

            }
            else if(i==4)
            {
                temp.setLatitude(Double.parseDouble("24.58"));
                temp.setLongitude(Double.parseDouble("121.06"));

            }
            else if(i==5)
            {
                temp.setLatitude(Double.parseDouble("24.57"));
                temp.setLongitude(Double.parseDouble("121.12"));
            }
            else if(i==6)
            {
                temp.setLatitude(Double.parseDouble("25.03"));
                temp.setLongitude(Double.parseDouble("121.15"));

            }
            else if(i==7)
            {
                temp.setLatitude(Double.parseDouble("25.04"));
                temp.setLongitude(Double.parseDouble("121.11"));

            }
            else if(i==8)
            {
                temp.setLatitude(Double.parseDouble("24.53"));
                temp.setLongitude(Double.parseDouble("121.17"));

            }
            else if(i==9)
            {
                temp.setLatitude(Double.parseDouble("24.54"));
                temp.setLongitude(Double.parseDouble("121.08"));

            }
            else if(i==10)
            {
                temp.setLatitude(Double.parseDouble("24.52"));
                temp.setLongitude(Double.parseDouble("121.12"));

            }
            else if(i==11)
            {
                temp.setLatitude(Double.parseDouble("25.00"));
                temp.setLongitude(Double.parseDouble("121.20"));

            }
            else if(i==12)
            {
                temp.setLatitude(Double.parseDouble("24.56"));
                temp.setLongitude(Double.parseDouble("121.17"));

            }
            location.add(temp);
        }

    }
    public void Determinetime()  {
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date dt1,dt2,Nowtime,dt4,dt5;
        try {
            Nowtime = new Date();
            for(int i=0;i<LocalWeather.get(0).LocalWeatherData.size();i++)
            {
                dt1 = sdf.parse(LocalWeather.get(0).LocalWeatherData.get(i).startTime);
                dt2 = sdf.parse(LocalWeather.get(0).LocalWeatherData.get(i).endTime);
                  Log.d("dt1",Long.toString(dt1.getTime()));
                  Log.d("dt2",Long.toString(dt2.getTime()));
                if (dt1.getTime() < Nowtime.getTime() ) {
                    scope = i;
                    break;
                }
            }

            // Log.d("dt4",Long.toString(dt4.getTime()));
            // Log.d("dt5",Long.toString(dt5.getTime()));

        }catch(Exception e){
            e.printStackTrace();
        }
    }
    public void dataInterpret()
    {
        JsonReader reader = new JsonReader(new StringReader(data));

        try {
            reader.beginObject();
            reader.nextName();
            reader.skipValue();
            reader.nextName();

            reader.beginObject();
            reader.nextName();
            reader.skipValue();
            reader.nextName();

            reader.beginArray();
            while(reader.hasNext())
            {
                reader.beginObject();
                reader.nextName();
                reader.skipValue();
                reader.nextName();
                reader.skipValue();
                reader.endObject();

            }
            reader.endArray();

            reader.endObject();

           reader.nextName();
            reader.beginObject();

            reader.nextName();
            reader.skipValue();
            reader.nextName();
            reader.beginArray();
            reader.beginObject();

            reader.nextName();
            reader.skipValue();

            reader.nextName();
            reader.skipValue();

            reader.nextName();
            reader.skipValue();

            reader.nextName();
            reader.beginArray();
            for(int i=0;i<13;i++)
            {
                LocalWeatherInfo2 temp2=new LocalWeatherInfo2();
                reader.beginObject();
                reader.nextName();
                String a=reader.nextString();
                temp2.LocalName=a;
                Log.d("tag",a);
                reader.nextName();
                reader.skipValue();
                reader.nextName();
                String lat=reader.nextString();
                temp2.stationLatlng.setLatitude(Double.parseDouble(lat));
                reader.nextName();
                String lng=reader.nextString();
                temp2.stationLatlng.setLongitude(Double.parseDouble(lng));
                reader.nextName();
                reader.beginArray();
                reader.beginObject();

                reader.nextName();
                reader.skipValue();
                reader.nextName();
                reader.beginArray();

               while(reader.hasNext())
               {
                    LocalWeatherInfo temp=new LocalWeatherInfo();
                    reader.beginObject();
                    reader.nextName();
                    temp.startTime=reader.nextString();
                    reader.nextName();
                    temp.endTime=reader.nextString();
                    reader.nextName();
                    temp.elementValue=reader.nextString();
                    temp2.LocalWeatherData.add(temp);
                    reader.endObject();
                }
                LocalWeather.add(temp2);
                reader.endArray();
                reader.endObject();
                reader.endArray();
                reader.endObject();
            }

            reader.endArray();
            reader.endObject();
            reader.endArray();
            reader.endObject();
            reader.endObject();


        } catch (IOException e) {
            e.printStackTrace();
        } finally{
            try {
                reader.close();
            }catch(IOException re) {
                re.printStackTrace();
            }
        }


    }

    public Runnable dataRunnable = new Runnable() {
        @Override
        public void run() {
            getDataFromUrl();
            dataInterpret();
            Determinetime();
            Message msg = new Message();
            msg.what = 200;

            handler.sendMessage(msg);

        }
    };
    private void getDataFromUrl() {
        StringBuffer sb = new StringBuffer();
        String line = null;
        BufferedReader br = null;
        try {

            URL url = new URL("http://opendata.cwb.gov.tw/api/v1/rest/datastore/F-D0047-005?elementName=WeatherDescription&sort=time");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Authorization", "CWB-1440FB20-9E6F-4BAE-B13B-D875B8BCBC59");

            if (conn.getResponseCode() != 200) {

                throw new RuntimeException("Failed : HTTP error code : "
                        + conn.getResponseCode());
            }
            br = new BufferedReader(new InputStreamReader(
                    (conn.getInputStream())));

            while((line = br.readLine()) != null) {
                sb.append(line);
            }

            conn.disconnect();

        } catch (MalformedURLException e) {

            e.printStackTrace();

        } catch (IOException e) {

            e.printStackTrace();

        }
        data = sb.toString();

    };

    public int determineTown(Location stationLatlng)
    {
        int n=0;
        float[] d = new float[1];
        Location.distanceBetween(stationLatlng.getLatitude(), stationLatlng.getLongitude(), location.get(0).getLatitude(), location.get(0).getLongitude(), d);
        float temp=d[0];
        for (int i = 0; i < 13; ++i) {
            float[] t = new float[1];
            Location.distanceBetween(stationLatlng.getLatitude(), stationLatlng.getLongitude(), location.get(i).getLatitude(), location.get(i).getLongitude(), t);
            if(temp>t[0])
            {
                n=i;
                temp=t[0];
            }
        }

      return n;

    }




}
