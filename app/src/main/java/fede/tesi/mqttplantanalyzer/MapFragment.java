package fede.tesi.mqttplantanalyzer;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;


import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import fede.tesi.mqttplantanalyzer.databinding.ActivityMainBinding;
import fede.tesi.mqttplantanalyzer.databinding.FragmentSecondBinding;

public class MapFragment extends AppCompatActivity implements OnMapReadyCallback,LocationListener {
    private GoogleMap mMap;
    LocationManager locationManager;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

        }

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 500, this);

        setContentView(R.layout.fragment_map);

        SupportMapFragment mapFragment = (SupportMapFragment) this.getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        locationManager.requestLocationUpdates(provider, 400, 1, this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;


        //LatLng sydney = new LatLng(-34, 151);
        //mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }

    @Override
    public void onLocationChanged(Location location) {
        double lat = location.getLatitude();
        double lon = location.getLongitude();

        LatLng x = new LatLng(lat, lon);
        //Fetching address from lat and Lon---Geo Coding

        Geocoder geo = new Geocoder(MapFragment.this);
        try {
            List<Address> addresses = geo.getFromLocation(lat, lon, 10);
            String adreess = addresses.get(0).getAddressLine(0);
            String country = addresses.get(0).getCountryName();
            String permises = addresses.get(0).getPremises();
            mMap.addMarker(new MarkerOptions().position(x).title("Country:" + country + "\nAdress" + adreess + "\nPermises" + permises));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(x));
            mMap.moveCamera(CameraUpdateFactory.zoomBy(14));

        } catch (IOException e) {
            e.printStackTrace();
        }
        mMap.addMarker(new MarkerOptions().position(x).title("Marker in Whitefield"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(x));
        mMap.moveCamera(CameraUpdateFactory.zoomBy(14));
        Toast.makeText(MapFragment.this, "Location Changed", Toast.LENGTH_LONG).show();


    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        if (status == LocationProvider.TEMPORARILY_UNAVAILABLE) {
            //Suddenely satellite comm is temperory unavailable
        } else if (status == LocationProvider.OUT_OF_SERVICE) {

        } else {
            //Satellite or towers are available
        }
    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

}
