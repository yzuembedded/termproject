package linyeh.termproject;

import android.os.Handler;
import android.os.Message;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class OpendataHandler {
    private String url;
    public String data;
    private Handler handler;
    public OpendataHandler(String in_url, Handler in_handler){
        this.url = in_url;
        this.handler = in_handler;
        Thread networkThread = new Thread(networkRunnable);
        networkThread.start();
    }
    public Runnable networkRunnable = new Runnable() {
        @Override
        public void run() {
            getDataFromUrl();
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
