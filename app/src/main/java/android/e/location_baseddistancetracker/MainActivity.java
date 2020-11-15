package android.e.location_baseddistancetracker;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements LocationListener {

    private TextView start_long;
    private TextView start_lat;
    private TextView end_long;
    private TextView end_lat;
    private TextView distance;
    private Button grab;
    private static final int REQUEST_LOCATION = 1;
    LocationManager locationManager;
    double startingLat;
    double startingLong;
    double currLat;
    double currLong;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        createUI();
    }

    private void createUI() {
        start_long = findViewById(R.id.start_long);
        start_lat = findViewById(R.id.start_lat);
        end_long = findViewById(R.id.end_long);
        end_lat = findViewById(R.id.end_lat);
        distance = findViewById(R.id.distance);
        grab = findViewById(R.id.grab);
        grab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getLocation();
            }

        });
    }

    private void getLocation(){
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
        }
        if (grab.getText().toString().equalsIgnoreCase("START")){
            end_lat.setText("N/A");
            end_long.setText("N/A");
            startingLat = currLat;
            startingLong = currLong;
            start_long.setText(String.valueOf(startingLong));
            start_lat.setText(String.valueOf(startingLat));
            distance.setText("N/A");
            grab.setText("STOP");
        } else {
            end_lat.setText(String.valueOf(currLat));
            end_long.setText(String.valueOf(currLong));
            distance.setText(String.valueOf(getDistanceFromCoordinates(startingLong,startingLat,currLong,currLat)));
            grab.setText("START");
        }
    }

    private double getDistanceFromCoordinates(double startlong, double startlat, double endlong, double endlat){
        final double radius = 20902230.971;
        double dLat = deg2rad(endlat - startlat);
        double dLon = deg2rad(endlong - startlong);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) + Math.cos(deg2rad(startlat)) * Math.cos(deg2rad(endlat)) * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double d = radius * c;
        return d;
    }

    private double deg2rad(double deg)
    {
        return deg * (Math.PI / 180);
    }

    private void init(){
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
        }
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            OnGPS();
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
    }

    private void OnGPS() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Enable GPS").setCancelable(false).setPositiveButton("Yes", new  DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
            }
        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        final AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    public void onLocationChanged(Location location) {
        currLat = location.getLatitude();
        currLong = location.getLongitude();
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
}