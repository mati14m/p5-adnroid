package org.p5.myapplication;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by Mefju on 29.05.2017.
 */

public class HttpController extends AsyncTask<Context, Context, Context> {

    private HttpURLConnection c = null;
    private double latitude, longitude;
    private ListView listView;
    URL u;
    List<String> values = new ArrayList<String>();

    public HttpController(String url, double latitude, double longitude, ListView listView){
        try {
            u = new URL(url+"?latitude="+Double.toString(latitude)+"&longitude="+Double.toString(longitude));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        this.latitude = latitude;
        this.longitude = longitude;
        this.listView = listView;
    }


    private String getJSON() {

        try {
            int timeout = 10000;
            c = (HttpURLConnection) u.openConnection();
            c.setRequestMethod("GET");
            c.setUseCaches(false);
            c.setAllowUserInteraction(false);
            c.setConnectTimeout(timeout);
            c.setReadTimeout(timeout);
            c.connect();
            int status = c.getResponseCode();


            switch (status) {
                case 200:
                case 201:
                    BufferedReader br = new BufferedReader(new InputStreamReader(c.getInputStream()));
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = br.readLine()) != null) {
                        sb.append(line+"\n");
                    }
                    br.close();
                    return sb.toString();
            }

        } catch (MalformedURLException ex) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (c != null) {
                try {
                    c.disconnect();
                } catch (Exception ex) {
                    Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        return null;
    }

    @Override
    protected Context doInBackground(Context... params) {
        String data = getJSON();
        Crodis msg = new Gson().fromJson(data, Crodis.class);
        if(msg != null)
            for (Item tmp: msg.getItems()) {
                for (Map.Entry<String, Float> cond: tmp.getConditions().entrySet()) {
                    values.add(cond.getKey().toString()+" "+cond.getValue().toString());
                }
            }
        return params[0];
    }

    @Override
    protected void onPostExecute(Context params) {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(params ,
                android.R.layout.simple_list_item_1, android.R.id.text1, values);
        listView.setAdapter(adapter);
    }
}
