package fede.tesi.mqttplantanalyzer;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class MyMapFragment extends FragmentActivity implements OnMapReadyCallback {
    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationClient;
    private DatabaseReference mDatabase;
    List<LatLangModel> entryList = new ArrayList<>();
    private FirebaseAuth auth;

    String boardId;
    SharedPreferences sharedPref;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_map);
        auth = FirebaseAuth.getInstance();
        sharedPref = this.getSharedPreferences(auth.getUid(), Context.MODE_PRIVATE);
        boardId=sharedPref.getString("CurrentBoard","");
        mDatabase = FirebaseDatabase.getInstance("https://mqttplantanalyzer-default-rtdb.europe-west1.firebasedatabase.app/").getReference();
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     *
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap) {
        //fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mMap = googleMap;

        mMap.setInfoWindowAdapter(new MyInfoWindowAdapter(this));

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            // Got last known location. In some rare situations this can be null.
                            if (location != null) {
                                LatLng myPos = new LatLng(location.getLatitude(), location.getLongitude());
                                mMap.addMarker(new MarkerOptions()
                                        .position(myPos)
                                        .title(auth.getUid())
                                        .snippet(boardId));
                                mMap.setMinZoomPreference(11f);
                                mMap.setMaxZoomPreference(19f);
                                mMap.moveCamera(CameraUpdateFactory.newLatLng(myPos));

                            }
                        }
                    });
            DatabaseReference locRef=mDatabase.child("publicLocation");
        Query recentPostsQuery = locRef
                .limitToLast(100);
        recentPostsQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                entryList.clear();
                mMap.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    LatLangModel val = snapshot.getValue(LatLangModel.class);
                    Log.e("Data CHANGE", "Value is: " + val.getLatitude());
                    entryList.add( val );
                    Marker mark=mMap.addMarker(new MarkerOptions()
                            .position(new LatLng(val.getLatitude(),val.getLongitude()))
                            .title(val.getName())
                            .snippet(val.getBoard()));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

}

