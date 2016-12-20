package linyeh.termproject;

/**
 * Created by YuehJuLin on 2016/12/20.
 */
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
public class LocalWeatherHandler {
    public String data;
    private Handler handler;
    public ArrayList<LocalWeatherInfo2> LocalWeather;
    public LocalWeatherHandler(Handler in_handler)
    {
        this.handler=in_handler;
        LocalWeather=new ArrayList<LocalWeatherInfo2>();
        Thread dataThread = new Thread(dataRunnable);
        dataThread.start();

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
                reader.nextName();
                reader.skipValue();
                reader.nextName();
                temp2.lat=reader.nextString();
                reader.nextName();
                temp2.lon=reader.nextString();
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
            //Determinetime();
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





}