package com.example.favouriteplaces;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMapLongClickListener {

    private GoogleMap mMap;

    LocationManager locationManager;
    LocationListener locationListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setOnMapLongClickListener(this); //ADDING THIS REQUIRED US TO ENSURE THIS CLASS (MapsActivity) implements GoogleMap.OnMapLongClickListener AND TO OVERRIDE onMapLongClick() METHOD

        Intent intent = getIntent();
        if (intent.getIntExtra("placePosition", 0) == 0) { //IF CLICKED ON "Click here to add your favourite place", BELOW IS EXECUTED (WHICH INCLUDES A LOCATION PERMISSION REQUEST)
            locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
            locationListener = new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    setLocation(location, "You're here!");
                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {

                }

                @Override
                public void onProviderEnabled(String provider) {

                }

                @Override
                public void onProviderDisabled(String provider) {

                }
            };
            //CODE BELOW IS THE POP UP WHICH ASKS FOR THE LOCATION PERMISSION WHEN THE APP STARTS, USERS CAN CHOOSE TO ACCEPT/DENY REQUEST
            if (Build.VERSION.SDK_INT < 23) { //IF API < 23, PROVIDE LOCATION and we won't need to manually ask for permission
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
            } else {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) { //IF PERMISSION WASNT GRANTED, ASK FOR PERMISSION
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1); //THIS WILL EXECUTE onRequestPermissionsResult() METHOD TO DECIDE WHAT TO DO IF PERMISSION IS GRANTED!
                } else { //IF PERMISSION IS GRANTED, PROVIDE LOCATION
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
                    //BELOW GETS LAST KNOWN LOCATION AT APP START
                    Location lastKnownLocation =  locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    //CODE BELOW IS SIMILAR TO THE ONES IN onLocationChanged()
                    setLocation(lastKnownLocation, "Your last known location");
                }
            }
            // ^ ^ LOCATION PERMISSION IS ONLY NEEDED HERE (WHEN CLICKED ON "Click here to add your favourite place").
            // WHEN CLICKED ON SAVED LOCATIONS, GPS ISNT REQUIRED, THUS NO NEED FOR LOCATION PERMISSION!
        } else {
            Location placeLocation = new Location(LocationManager.GPS_PROVIDER); //creates NEW EMPTY location
            placeLocation.setLatitude(MainActivity.latLngList.get(intent.getIntExtra("placePosition",0)).latitude); //adds latitude to thus empty location from the latLngList in Main Activity!
            placeLocation.setLongitude(MainActivity.latLngList.get(intent.getIntExtra("placePosition",0)).longitude); //adds longitude to thus empty location from the latLngList in Main Activity!
            setLocation(placeLocation, MainActivity.placeList.get(intent.getIntExtra("placePosition",0)));
        }

    }


    //WHEN THE USERS ACCEPT/DENY LOCATION REQUEST, THE CODE BELOW IS EXECUTED
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) { //IF PERMISSION WAS GRANTED
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 4, 0, locationListener);
                    Location lastKnownLocation =  locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    setLocation(lastKnownLocation, "Your last known location");
                }
            }
        }
    }


    public void setLocation(Location location, String title) {
        if (location != null) {
            // Clears previous map
            mMap.clear();

            // Change map type
            mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);

            // Add a marker at USER'S LOCATION and move the camera to it!
            LatLng userLocation = new LatLng(location.getLatitude(), location.getLongitude());

            // icon(... added to change the colour of the marker
            mMap.addMarker(new MarkerOptions().position(userLocation).title(title).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));

            // newLatLngZoom to zoom into map - from 1 to 20, where 1 is totally zoomed out and 20 is totally zoomed in
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 18));
        }
    }


    public String generateAddress(LatLng latLng) {
        double latitude = latLng.latitude;
        double longitude = latLng.longitude;

        String address = "";

        //CODE BELOW CONVERTS THE LAT AND LONG TO ACTUAL ADDRESS
        Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault()); //Locale.getDefault() gets address from the specific country the phone is in
        try {
            List<Address> addressList = geocoder.getFromLocation(latitude, longitude, 1);

            //Address[addressLines=[0:"398 Alexandra Ave, Rayners Lane, Harrow HA2 9UF, UK"],feature=398,admin=England,sub-admin=Greater London,locality=null,thoroughfare=Alexandra Avenue,postalCode=HA2 9UF,countryCode=GB,countryName=United Kingdom,hasLatitude=true,latitude=51.572310699999996,hasLongitude=true,longitude=-0.3708033,phone=null,url=null,extras=null]

            // ^ ^ code below will retrieve data from here. We will use getAddressLine(0) since that will simply get the full address
            // ^ ^ e.g. we can do getFeatureName(), getAdminArea(), getThoroughFare(), etc. instead of getAddressLine(0) to get the specifics

            if (addressList != null && addressList.size() > 0) {
                if (addressList.get(0).getAddressLine(0) != null) {
                    address = addressList.get(0).getAddressLine(0);
                    // icon(... added to change the colour of the marker
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return address;
    }


    @Override
    public void onMapLongClick(LatLng latLng) {
        mMap.addMarker(new MarkerOptions()
                .position(latLng)
                .title("Saved  to favourites!")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)));

        Toast.makeText(getApplicationContext(), "Saved to favourites!", Toast.LENGTH_SHORT).show();

        MainActivity.placeList.add(generateAddress(latLng));
        MainActivity.latLngList.add(latLng);
        MainActivity.arrayAdapter.notifyDataSetChanged(); //THIS WILL UPDATE THE LISTVIEW (IN THE ADAPTER)
    }


}