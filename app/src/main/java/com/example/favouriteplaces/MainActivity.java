package com.example.favouriteplaces;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
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

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    static ArrayList<String> placeList = new ArrayList<String>();
    static ArrayList<LatLng> latLngList = new ArrayList<LatLng>(); //TO SIMULTANEOUSLY SAVE LATLNG FOR EVERY PLACELIST ITEM THAT IS ADDED!
    static ArrayAdapter<String> arrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        placeList.add("Click here to add your favourite place");
        latLngList.add(new LatLng(0,0)); //DUMMY LATLNG ADDED FOR THE "Click here to add your favourite place" PLACELIST ITEM

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