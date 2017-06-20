package org.p5.myapplication;

import android.Manifest;
import android.content.Context;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    Context context;
    GPSTracker gps;
    Button refresh;
    ListView listView;
    String url = "http://:4001/api/weather";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        refresh = (Button) findViewById(R.id.refreshButton);
        listView = (ListView) findViewById(R.id.listView);

        int TAG_CODE_PERMISSION_LOCATION = 0;
        ActivityCompat.requestPermissions(this, new String[] {
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION },
                TAG_CODE_PERMISSION_LOCATION);

        refresh.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                gps = new GPSTracker(MainActivity.this);

                if(gps.canGetLocation()){

                    double latitude = gps.getLatitude();
                    double longitude = gps.getLongitude();
                    new HttpController(url, latitude, longitude, listView).execute(MainActivity.this);
                    Toast.makeText(getApplicationContext(), "Your Location is - \nLat: " + latitude + "\nLong: " + longitude, Toast.LENGTH_LONG).show();
                }else{
                    gps.showSettingsAlert();
                }
            }
        });
    }


}
