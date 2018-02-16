package com.example.ursul.rdvgeo;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    LocationManager locationManager;
    LocationListener locationListener;
    String coordRDV, adr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        adr = getIntent().getStringExtra("adr");

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                mMap.addMarker(new MarkerOptions().position(new LatLng(location.getLatitude(),location.getLongitude())).title("My position"));
                mMap.animateCamera(CameraUpdateFactory.newLatLng(new LatLng(location.getLatitude(),location.getLongitude())));
                coordRDV = " "+parseLatitude(location.getLatitude())+" "+parseLongitude(location.getLongitude());
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {}

            @Override
            public void onProviderEnabled(String provider) {}
            @Override
            public void onProviderDisabled(String provider) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        };

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void onSearchMyPosition(){ //TODO mettre ceci dans un thread à part
        if (ActivityCompat.checkSelfPermission(getBaseContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getBaseContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.INTERNET}, 10);
            return;
        }
        locationManager.requestSingleUpdate(LocationManager.NETWORK_PROVIDER, locationListener, null);
    }

    public void onSearch(String adr) throws IOException { //TODO mettre ceci dans un thread à part
        String location = adr;
        List<Address> addressList = null;
        if(location == null || location.equals("")){
            Toast.makeText(this, "Vous devez indiquer une adresse", Toast.LENGTH_SHORT).show();
        } else {
            Geocoder geocoder = new Geocoder(this);
            if(geocoder.getFromLocationName(location, 1).isEmpty()){
                Toast.makeText(this, "Vous devez indiquez une adresse juste", Toast.LENGTH_SHORT).show();
            } else {
                try{
                    addressList = geocoder.getFromLocationName(location, 1);
                }catch (IOException e){
                    e.printStackTrace();
                }
            }
            Address address = addressList.get(0);
            mMap.addMarker(new MarkerOptions().position(new LatLng(address.getLatitude(),address.getLongitude())).title("Address"));
            mMap.animateCamera(CameraUpdateFactory.newLatLng(new LatLng(address.getLatitude(),address.getLongitude())));
            coordRDV = " "+parseLatitude(address.getLatitude())+" "+parseLongitude(address.getLongitude());
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if(adr.equals("") || adr == null){
            onSearchMyPosition();
        } else {
            try {
                onSearch(adr);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void recupCoord(View view){
        Intent returnIntent = new Intent();
        returnIntent.putExtra("result", coordRDV);
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
    }

    public String parseLatitude(double lat) {
        if(lat < 0)
            return ""+(-lat)+"S";
        return ""+lat+"N";
    }

    public String parseLongitude(double longi) {
        if(longi < 0)
            return ""+(-longi)+"O";
        return ""+longi+"E";
    }
}
