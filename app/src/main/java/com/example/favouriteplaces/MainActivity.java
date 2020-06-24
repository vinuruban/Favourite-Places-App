package com.example.favouriteplaces;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    static ArrayList<String> placeList = new ArrayList<String>();
    static ArrayList<LatLng> latLngList = new ArrayList<LatLng>(); //TO SIMULTANEOUSLY SAVE LATLNG FOR EVERY PLACELIST ITEM THAT IS ADDED!
    static ArrayAdapter<String> arrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //ALTHOUGH WE CAN DATA TO SharedPreferences IN onMapLongClick() OF MAPS ACTIVITY, WE NEED TO LOAD THOSE DATA AT ONCREATE. CODE BELOW DOES THAT.
        SharedPreferences sharedPreferences = this.getSharedPreferences("com.example.favouriteplaces", Context.MODE_PRIVATE);

        ArrayList<String> latitudes = new ArrayList<>();
        ArrayList<String> longitudes = new ArrayList<>();

        //clear objects
        placeList.clear();
        latLngList.clear();
        latitudes.clear();
        longitudes.clear();

        try {
            //ALTHOUGH placeList AND OTHERS ARE CLEARED ABOVE, DATA IS STILL STORED WITHIN SHARED PREFERENCE, SO IF I REOPEN THE APP WITH SAVED DATA, THE CODE BELOW WILL RESTORE THEM BACK INTO placeList BELOW (CHECK IF STATEMENT BELOW)
            //deserializes from String to readable ArrayList<String>!
            placeList = (ArrayList<String>) ObjectSerializer.deserialize(sharedPreferences.getString("places", ObjectSerializer.serialize(new ArrayList<String>())));
            latitudes = (ArrayList<String>) ObjectSerializer.deserialize(sharedPreferences.getString("latitudes", ObjectSerializer.serialize(new ArrayList<String>())));
            longitudes = (ArrayList<String>) ObjectSerializer.deserialize(sharedPreferences.getString("longitudes", ObjectSerializer.serialize(new ArrayList<String>())));
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (placeList.size() > 0 && latitudes.size() > 0 && longitudes.size() > 0) { //if there is data (added from the try catch above)...
            if (placeList.size() == latitudes.size() && placeList.size() == longitudes.size()) {
                for (int i = 0; i < placeList.size(); i++) {
                    double dLatitudes = Double.parseDouble(latitudes.get(i)); //convert each String into double
                    double dLongitudes = Double.parseDouble(longitudes.get(i));
                    latLngList.add(new LatLng(dLatitudes, dLongitudes));
                }
            }
        } else { //if there is no data added yet (on create)...
            placeList.add("Click here to add your favourite place");
            latLngList.add(new LatLng(0,0)); //DUMMY LATLNG ADDED FOR THE "Click here to add your favourite place" PLACELIST ITEM
        }


        arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, placeList);
        ListView listView = (ListView) findViewById(R.id.list);
        listView.setAdapter(arrayAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this, MapsActivity.class);
                intent.putExtra("placePosition", position); //THIS WILL PASS POSITION NUMBER TO THE MAP ACTIVITY TO HELP THAT ACTIVITY OBTAIN THE latLngList FROM THIS ACTIVITY!
                startActivity(intent);
            }
        });
    }

}