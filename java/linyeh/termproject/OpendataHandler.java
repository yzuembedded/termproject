package linyeh.termproject;

import android.os.Handler;
import android.os.Message;
import android.util.JsonReader;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class OpendataHandler {
    private String url = "http://data.gov.tw/iisi/logaccess/66022?dataUrl=http://data.tycg.gov.tw/opendata/datalist/datasetMeta/download?id=5ca2bfc7-9ace-4719-88ae-4034b9a5a55c&rid=a1b4714b-3b75-4ff8-a8f2-cc377e4eaa0f&ndctype=JSON&ndcnid=28228";
    private Handler handler;
    public String data;
    public ArrayList<uBikeStationInfo> stations;

    public OpendataHandler(Handler in_handler){
        this.handler = in_handler;
        stations = new ArrayList<uBikeStationInfo>();
        Thread dataThread = new Thread(dataRunnable);
        dataThread.start();
    }

    private void dataInterpret(){
        JsonReader reader = new JsonReader(new StringReader(data));
        stations.clear();
        try {
            reader.beginObject();
            reader.nextName();
            reader.skipValue();
            reader.nextName();
            reader.beginObject();
            while(reader.hasNext()){
                reader.nextName();
                reader.beginObject();
                uBikeStationInfo tmp = new uBikeStationInfo();
                while(reader.hasNext()) {
                    String name = reader.nextName();
                    if (name.equals("sna"))
                        tmp.stationName = reader.nextString();
                    else if (name.equals("tot"))
                        tmp.totalNum = Integer.parseInt(reader.nextString());
                    else if (name.equals("sbi"))
                        tmp.usableNum = Integer.parseInt(reader.nextString());
                    else if(name.equals("bemp"))
                        tmp.returanableNum = Integer.parseInt(reader.nextString());
                    else if(name.equals("mday"))
                        tmp.updateTime = reader.nextString();
                    else if(name.equals("ar"))
                        tmp.address = reader.nextString();
                    else if(name.equals("lat"))
                        tmp.stationLatlng.setLatitude(Double.parseDouble(reader.nextString()));
                    else if(name.equals("lng"))
                        tmp.stationLatlng.setLongitude(Double.parseDouble(reader.nextString()));
                    else if(name.equals("act"))
                        tmp.isActive = reader.nextString().equals("1");
                    else if(name.equals("sno"))
                        tmp.id = reader.nextString();
                    else
                        reader.skipValue();
                }
                stations.add(tmp);
                reader.endObject();
            }
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
            Message msg = new Message();
            msg.what = 200;
            handler.sendMessage(msg);
        }
    };

    private void getDataFromUrl() {
        StringBuffer sb = new StringBuffer();
        String line = null;
        BufferedReader buffer = null;
        URL url;
        try {
            url = new URL(this.url);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            buffer = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            while((line = buffer.readLine()) != null) {
                sb.append(line);
            }
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            try {
                buffer.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        data = sb.toString();
    }
}
