package fede.tesi.mqttplantanalyzer;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.github.mikephil.charting.data.Entry;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;

import java.util.Collections;
import java.util.concurrent.TimeUnit;

public class MyInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {

    private LayoutInflater inflater;
    private Context context;
    private DatabaseReference mDatabase;
    View v;
    MqttValue val;
    FirebaseStorage storage = FirebaseStorage.getInstance();
    private StorageReference storageRef;
    ImageView image;
    byte[] imgbytes;


    public MyInfoWindowAdapter(Context context){
        inflater = (LayoutInflater) context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
        this.context = context;
    }

    @Nullable
    @Override
    public View getInfoWindow(@NonNull Marker marker) {
        // Getting view from the layout file
        Log.e("MARKER",marker.getTitle()+marker.getSnippet());
        storageRef = storage.getReference();
        //v = inflater.inflate(R.layout.info_window_layout, null);
        image= (ImageView) v.findViewById(R.id.markerImage);

        StorageReference pathReference = storageRef.child(marker.getTitle()+"/"+marker.getSnippet());
        pathReference.list(1)
                .addOnSuccessListener(new OnSuccessListener<ListResult>() {
                    @Override
                    public void onSuccess(ListResult listResult) {
                        for (StorageReference prefix : listResult.getPrefixes()) {
                            // All the prefixes under listRef.
                            // You may call listAll() recursively on them.
                        }

                        for (StorageReference item : listResult.getItems()) {
                            StorageReference pathReference=storageRef.child(marker.getTitle()+"/"+marker.getSnippet()+"/"+item.getName());
                            Log.e("PATH",pathReference.toString());
                            final long ONE_MEGABYTE = 1024 * 1024;
                            pathReference.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                                @Override
                                public void onSuccess(byte[] bytes) {
                                    // Data for "images/island.jpg" is returns, use this as needed
                                    imgbytes=bytes;
                                    image.setImageBitmap(BitmapFactory.decodeByteArray(bytes, 0, bytes.length));
                                    Log.e("Succes immage","");
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception exception) {
                                    // Handle any errors
                                    Log.e("ERR",exception.toString());
                                }
                            });
                        }
                    }
                });

        mDatabase = FirebaseDatabase.getInstance("https://mqttplantanalyzer-default-rtdb.europe-west1.firebasedatabase.app/").getReference();
        DatabaseReference markerRef=mDatabase.child(marker.getTitle()).child(marker.getSnippet());
        Query lastchield =
                        markerRef.orderByKey().limitToLast(1);
        lastchield.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot ds) {
                for (DataSnapshot snapshot : ds.getChildren()) {
                    val = snapshot.getValue(MqttValue.class);

                }
                v = inflater.inflate(R.layout.info_window_layout, null);

                TextView title = (TextView) v.findViewById(R.id.title);
                title.setText("Last detection");

                TextView address = (TextView) v.findViewById(R.id.distance);
                address.setText("Luminosity is : "+val.getLux()+ " lux\n"+
                        "Moisture is : " + val.getMoisture()+" %\n"+
                        "Temperature is : "+val.getTemperature()+" C\n"+
                        "Humidity is : "+val.getHumidity()+" %\n");
                getInfoWindow(marker);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
        return v;

    }

    @Nullable
    @Override
    public View getInfoContents(@NonNull Marker marker) {
        image= (ImageView) v.findViewById(R.id.markerImage);
        image.setImageBitmap(BitmapFactory.decodeByteArray(imgbytes, 0, imgbytes.length));
        return v;
    }
}
