package linyeh.termproject;
import android.icu.text.SimpleDateFormat;
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


/**
 * Created by Eric on 2016/12/16.
 */

public class WeatherdataHandler {
    public int scope;
    public ArrayList<WeatherDataInfo> weatherdata;
    public ArrayList<WeatherDataInfo> weatherdata1;
    public ArrayList<WeatherDataInfo> weatherdata2;
    public String data;
    private Handler handler;
    public  WeatherdataHandler(Handler in_handler)
    {
        this.handler=in_handler;
        weatherdata=new ArrayList<WeatherDataInfo>();
        weatherdata1=new ArrayList<WeatherDataInfo>();
        weatherdata2=new ArrayList<WeatherDataInfo>();
        Thread dataThread = new Thread(dataRunnable);
        dataThread.start();

    }
    public void Determinetime()  {
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date dt1,dt2,Nowtime,dt4,dt5;
        try {
            dt1 = sdf.parse(weatherdata1.get(0).startTime);
            dt2 = sdf.parse(weatherdata1.get(0).endTime);
          //  Log.d("dt1",Long.toString(dt1.getTime()));
          //  Log.d("dt2",Long.toString(dt2.getTime()));
            Nowtime = new Date();

            dt4 = sdf.parse(weatherdata2.get(0).startTime);
            dt5 = sdf.parse(weatherdata2.get(0).endTime);
           // Log.d("dt4",Long.toString(dt4.getTime()));
           // Log.d("dt5",Long.toString(dt5.getTime()));

            if (dt1.getTime() <= Nowtime.getTime() && Nowtime.getTime() < dt2.getTime()) {
                scope = 2;
            } else if (dt4.getTime() <= Nowtime.getTime() && Nowtime.getTime() < dt5.getTime()) {
                scope = 3;
            } else {
                scope = 1;
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    public void dataInterpret()
    {
        JsonReader reader = new JsonReader(new StringReader(data));
        weatherdata.clear();
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
            reader.beginArray();

            while(reader.hasNext())
            {
                reader.beginObject();

                reader.nextName();
                String element=reader.nextString();
                reader.nextName();

                reader.beginArray();
                for(int i=0;i<3;i++) {
                    WeatherDataInfo temp=new WeatherDataInfo();
                    temp.elementName=element;
                    reader.beginObject();
                    reader.nextName();
                    temp.startTime = reader.nextString();
                    reader.nextName();
                    temp.endTime = reader.nextString();
                    reader.nextName();
                    reader.beginObject();
                    while (reader.hasNext()) {
                        String name = reader.nextName();
                        if (name.equals("paramterName")) {
                            temp.parameterName = reader.nextString();

                        } else if (name.equals("parameterValue")) {
                            temp.parameterValue = reader.nextString();
                        } else if (name.equals("parameterUnit")) {
                            temp.parameterUnit = reader.nextString();
                        }
                    }
                    if(i==0)
                    {

                        weatherdata.add(temp);
                    }
                    else if(i==1)
                    {

                        weatherdata1.add(temp);
                    }
                    else if(i==2)
                    {

                        weatherdata2.add(temp);
                    }

                    reader.endObject();
                    reader.endObject();
                }
                reader.endArray();
                reader.endObject();
            }

            reader.endArray();

            reader.endObject();
            reader.endArray();
            reader.endObject();
            reader.endObject();


        }catch(IOException e){
            e.printStackTrace();
        }finally{
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

            URL url = new URL("http://opendata.cwb.gov.tw/api/v1/rest/datastore/F-C0032-001?locationName=桃園市&sort=time&format=json");
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
}
