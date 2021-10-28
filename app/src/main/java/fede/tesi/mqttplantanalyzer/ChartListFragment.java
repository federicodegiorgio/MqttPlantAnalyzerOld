package fede.tesi.mqttplantanalyzer;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import fede.tesi.mqttplantanalyzer.databinding.FragmentChartListBinding;
import fede.tesi.mqttplantanalyzer.databinding.FragmentSecondBinding;

public class ChartListFragment extends Fragment {
    private FragmentChartListBinding binding;
    ListView listView;
    TextView textView;
    String[] listItem;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        binding = FragmentChartListBinding.inflate(inflater, container, false);

        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        listView=(ListView)view.findViewById(R.id.chartList);
        textView=(TextView)view.findViewById(R.id.chartTypeView);
        listItem = getResources().getStringArray(R.array.array_charts);
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this.getActivity().getBaseContext(),
                android.R.layout.simple_list_item_1, android.R.id.text1, listItem);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                // TODO Auto-generated method stub
                String value=adapter.getItem(position);
                switch (value){
                    case "Luminosity":
                        NavHostFragment.findNavController(ChartListFragment.this)
                                .navigate(R.id.action_SecondFragment_to_LuminosityFragment);
                        break;
                    case "Humidity":
                        NavHostFragment.findNavController(ChartListFragment.this)
                                .navigate(R.id.action_SecondFragment_to_HumidityFragment);
                        break;
                    case "Moisture":
                        NavHostFragment.findNavController(ChartListFragment.this)
                                .navigate(R.id.action_SecondFragment_to_MoistureFragment);
                        break;
                    case "Temperature":
                        NavHostFragment.findNavController(ChartListFragment.this)
                                .navigate(R.id.action_SecondFragment_to_TemperatureFragment);
                        break;

                    case "Image":
                        NavHostFragment.findNavController(ChartListFragment.this)
                                .navigate(R.id.action_SecondFragment_to_ImageFragment);
                        break;
                    case "Map":
                        Intent i =new Intent(getActivity().getBaseContext(),MapFragment.class);
                        getActivity().getBaseContext().startActivity(i);
                        break;
                    default:
                        break;
                }

            }
        });

        binding.BtButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(ChartListFragment.this)
                        .navigate(R.id.action_SecondFragment_to_FirstFragment);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
