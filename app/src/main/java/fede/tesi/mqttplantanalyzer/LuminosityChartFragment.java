package fede.tesi.mqttplantanalyzer;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.utils.EntryXComparator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import fede.tesi.mqttplantanalyzer.databinding.FragmentSecondBinding;

public class LuminosityChartFragment extends Fragment {

    //private LuminosityChartFragmentBinding binding;
    private FragmentSecondBinding binding;
    LineChart lineChart;
    LineData lineData;
    List<Entry> entryList = new ArrayList<>();
    private DatabaseReference mDatabase;
    private FirebaseAuth auth;

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
        auth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance("https://mqttplantanalyzer-default-rtdb.europe-west1.firebasedatabase.app/").getReference();
        DatabaseReference userRef = mDatabase.child(auth.getUid()).child("246f28969298");
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                Log.e("Data CHANGE", dataSnapshot.toString());
                entryList.clear();
                LineDataSet lineDataSet = new LineDataSet(entryList,"Luminosity");
                lineDataSet.setColors(Color.BLUE);
                lineDataSet.setFillAlpha(110);
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
                    Log.e("Data CHANGE", "Value is: " + val.getLux());
                    long time= TimeUnit.MILLISECONDS.toMinutes(val.getTimestamp());
                    entryList.add(new Entry(time,val.getLux()));

                }
                Collections.sort(entryList, new EntryXComparator().reversed());
                lineDataSet = new LineDataSet(entryList,"Luminosity");
                lineDataSet.setColors(Color.YELLOW);
                lineDataSet.setFillAlpha(85);
                lineDataSet.setDrawFilled(true);
                lineDataSet.setFillColor(Color.YELLOW);
                lineDataSet.setCircleRadius(5f);
                lineDataSet.setCircleColors(Color.DKGRAY);
                lineDataSet.setDrawCircleHole(false);

                lineData = new LineData(lineDataSet);
                lineChart.setBackgroundColor(Color.WHITE);
                lineChart.getDescription().setEnabled(false);
                lineChart.setDrawGridBackground(false);
                lineChart.setDragEnabled(true);
                lineChart.setScaleEnabled(true);
                lineChart.getAxisRight().setAxisMinimum(0f);
                lineChart.getAxisLeft().setAxisMinimum(0f);

                lineChart.setData(lineData);
                XAxis xAxis = lineChart.getXAxis();
                xAxis.setPosition(XAxis.XAxisPosition.TOP);
                xAxis.setTextSize(10f);
                xAxis.setTextColor(Color.BLACK);
                xAxis.setDrawAxisLine(false);
                xAxis.setDrawGridLines(true);
                xAxis.setTextColor(Color.BLACK);
                xAxis.setCenterAxisLabels(true);
                xAxis.setGranularity(30f); // half hour
                xAxis.setValueFormatter(new MyAxisFormatter());


                lineChart.setVisibleXRangeMaximum(60f);
                lineChart.setVisibleXRangeMinimum(30f);
                lineChart.setVisibleYRangeMaximum(360f,lineChart.getAxisLeft().getAxisDependency());
                lineChart.setVisibleYRangeMinimum(60f,lineChart.getAxisLeft().getAxisDependency());
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



        binding.buttonSecond.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(LuminosityChartFragment.this)
                        .navigate(R.id.action_LuminosityFragment_to_SecondFragment);
            }
        });

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}