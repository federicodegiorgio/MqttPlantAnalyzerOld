package fede.tesi.mqttplantanalyzer;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.utils.EntryXComparator;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import fede.tesi.mqttplantanalyzer.databinding.FragmentSecondBinding;

public class TemperatureChartFragment extends Fragment {
    private FragmentSecondBinding binding;
    LineChart lineChart;
    LineData lineData;
    List<Entry> entryList = new ArrayList<>();
    private DatabaseReference mDatabase;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        binding = FragmentSecondBinding.inflate(inflater, container, false);

        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        lineChart = view.findViewById(R.id.lineChart);
        mDatabase = FirebaseDatabase.getInstance("https://mqttplantanalyzer-default-rtdb.europe-west1.firebasedatabase.app/").getReference();
        mDatabase.child("ex").setValue("Hello");
        DatabaseReference userRef = mDatabase.child("user");
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                Log.e("Data CHANGE", dataSnapshot.toString());
                entryList.clear();
                LineDataSet lineDataSet = new LineDataSet(entryList,"Temperature");
                lineDataSet.setColors(Color.BLUE);
                lineDataSet.setFillAlpha(110);
                lineDataSet.setDrawFilled(true);
                lineDataSet.setFillColor(Color.RED);
                lineData = new LineData(lineDataSet);

                lineChart.setBackgroundColor(Color.WHITE);
                lineChart.getDescription().setEnabled(false);
                lineChart.setDrawGridBackground(false);
                lineChart.setDragEnabled(true);
                lineChart.setScaleEnabled(true);

                lineChart.setData(lineData);
                lineChart.invalidate();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    MqttValue val = snapshot.getValue(MqttValue.class);
                    Log.e("Data CHANGE", "Value is: " + val.getTemperature());
                    entryList.add(new Entry(val.getTimestamp(),val.getTemperature()));

                }
                Collections.sort(entryList, new EntryXComparator());
                lineDataSet = new LineDataSet(entryList,"Temperature");
                lineDataSet.setColors(Color.RED);
                lineDataSet.setFillAlpha(85);
                lineDataSet.setDrawFilled(true);
                lineDataSet.setFillColor(Color.RED);
                lineDataSet.setCircleRadius(5f);
                lineDataSet.setCircleColors(Color.DKGRAY);
                lineDataSet.setDrawCircleHole(false);
                lineChart.getAxisLeft().setAxisMinimum(-15f);
                lineChart.getAxisLeft().setAxisMaximum(45f);
                lineChart.getAxisRight().setAxisMinimum(-15f);
                lineChart.getAxisRight().setAxisMaximum(45f);

                lineData = new LineData(lineDataSet);
                lineChart.setBackgroundColor(Color.WHITE);
                lineChart.getDescription().setEnabled(false);
                lineChart.setDrawGridBackground(false);
                lineChart.setDragEnabled(true);
                lineChart.setScaleEnabled(true);

                lineChart.setData(lineData);
                XAxis xAxis = lineChart.getXAxis();
                xAxis.setPosition(XAxis.XAxisPosition.TOP);
                xAxis.setTextSize(10f);
                xAxis.setTextColor(Color.BLACK);
                xAxis.setDrawAxisLine(false);
                xAxis.setDrawGridLines(true);
                xAxis.setTextColor(Color.BLACK);
                xAxis.setCenterAxisLabels(true);
                xAxis.setGranularity(10f); // 10 min
                xAxis.setValueFormatter(new MyAxisFormatter());
                lineChart.setVisibleXRangeMaximum(15f);
                lineChart.setVisibleXRangeMinimum(15f);
                lineChart.setDrawGridBackground(false);
                lineChart.getAxisLeft().setDrawGridLines(false);
                lineChart.getXAxis().setDrawGridLines(false);
                lineChart.invalidate();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.e("Data ERROR", "Failed to read value.", error.toException());
            }
        });



    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
