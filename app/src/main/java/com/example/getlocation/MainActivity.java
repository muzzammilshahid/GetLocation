package com.example.getlocation;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private EditText latitude;
    private EditText longitude;
    private Button getLocation;
    private TextView address;

    String lat;
    String lon;

    //LocationManager is the main class through which our application can access the location services in android
    public LocationManager locationManager;

    //LocationListener is use for receiving the notification from LocationManager when the location has changed
    //Here we are creating the custom LocationListener class
    public LocationListener locationListener = new MyLocationListener();


    // flag for GPS Status
    private boolean gps_enable = false;
    // flag for network status
    private boolean network_enable = false;


    //to generate address and other information
    Geocoder geocoder;
    List<Address> myAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        latitude = findViewById(R.id.latitude);
        latitude.setEnabled(false);
        longitude = findViewById(R.id.longitude);
        longitude.setEnabled(false);
        getLocation = findViewById(R.id.button_location);
        address = findViewById(R.id.address);

        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        getLocation.setOnClickListener(v -> getMyLocation());

        checkLocationPermission();
    }


    //custom LocationListener class
    class MyLocationListener implements LocationListener {

        @Override
        public void onLocationChanged(@NonNull Location location) {
            if (location != null) {
                locationManager.removeUpdates(locationListener);
                lat = "" + location.getLatitude();
                lon = "" + location.getLongitude();

                latitude.setText(lat);
                longitude.setText(lon);

                geocoder = new Geocoder(MainActivity.this, Locale.getDefault());

                try {
                    myAddress = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                } catch (IOException e) {
                    e.printStackTrace();
                }


                // the information that can be taken by using geocoder
                System.out.println("This  "+myAddress.get(0).getAdminArea()+
                        "    "+myAddress.get(0).getCountryCode()+
                        "   "+ myAddress.get(0).getFeatureName()+
                        "   "+myAddress.get(0).getPhone()+
                        "   "+myAddress.get(0).getLocality()+
                        "   "+myAddress.get(0).getPostalCode()+
                        "   "+myAddress.get(0).getPremises());
                String address1 = myAddress.get(0).getAddressLine(0);
                address.setText(address1);
            }
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(@NonNull String provider) {

        }

        @Override
        public void onProviderDisabled(@NonNull String provider) {

        }
    }

    private void getMyLocation() {

        try {
            // This will check that the GPS is enable or not
            gps_enable = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        } catch (Exception e) {
            System.out.println("This is first catch");
            e.printStackTrace();
        }

        try {
            // This will check that the network is enable or not
            network_enable = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        } catch (Exception e) {
            System.out.println("This is last catch");
            e.printStackTrace();
        }

        //if gps and network is not available then show the dialog box
        if (!gps_enable && !network_enable) {
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle("Attention");
            builder.setMessage("Sorry, location is not available please enable the location service...");

            //on pressing Positive button go to settings to enable location
            builder.setPositiveButton("Enable Location", (dialog, which) -> {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            });

            //On pressing cancel button
            builder.setNegativeButton("cancel", (dialog, which) -> dialog.cancel());
            builder.create().show();
        }


        //check if the gps is enable
        if (gps_enable) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
        }

        //check if the network is available
        if (network_enable){
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
        }

    }

    private boolean checkLocationPermission() {
        int location = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        int location2 = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);

        List<String> listPermission = new ArrayList<>();

        if (location!= PackageManager.PERMISSION_GRANTED){
            listPermission.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }

        if (location2 != PackageManager.PERMISSION_GRANTED){
            listPermission.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        }

        if (!listPermission.isEmpty()){
            ActivityCompat.requestPermissions(this,listPermission.toArray(new String[listPermission.size()]),
                    1);
        }
        else {
            getMyLocation();
        }


        return true;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted.
                    getMyLocation();

                } else {

                    // permission denied
                    Toast.makeText(this, "The permission denied", Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
    }

}