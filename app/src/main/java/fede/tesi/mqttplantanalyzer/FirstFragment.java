package fede.tesi.mqttplantanalyzer;

import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.douglasjunior.bluetoothclassiclibrary.BluetoothClassicService;
import com.github.douglasjunior.bluetoothclassiclibrary.BluetoothConfiguration;
import com.github.douglasjunior.bluetoothclassiclibrary.BluetoothService;
import com.github.douglasjunior.bluetoothclassiclibrary.BluetoothStatus;
import com.google.android.material.snackbar.Snackbar;

import java.util.LinkedList;
import java.util.UUID;

import fede.tesi.mqttplantanalyzer.databinding.FragmentFirstBinding;

public class FirstFragment extends Fragment {

    private FragmentFirstBinding binding;
    public LinkedList<BtDisp> btList=new LinkedList<>();
    public BtRecyclerViewAdapter adapter;
    BluetoothService service;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = FragmentFirstBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView recyclerView = binding.myRv;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new BtRecyclerViewAdapter(btList);
        recyclerView.setAdapter(adapter);
        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener (this.getContext(), recyclerView ,new RecyclerItemClickListener.OnItemClickListener() {
                    @Override public void onItemClick(View view, int position) {
                        Snackbar.make(view, btList.get(position).name, Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                    }

                    @Override public void onLongItemClick(View view, int position) {
                        // do whatever
                    }
                })
        );

        BluetoothConfiguration config = new BluetoothConfiguration();
        config.context = getContext().getApplicationContext();
        config.bluetoothServiceClass = BluetoothClassicService.class;
        config.bufferSize = 1024;
        config.characterDelimiter = '\n';
        config.deviceName = "MqttPlantAnalyzer";
        config.callListenersInMainThread = true;
        BtDisp bt = new BtDisp("mio", "device.getAddress()", "dopo");
        btList.add(bt);
        // Required
        config.uuid = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");

        BluetoothService.init(config);
        service = BluetoothService.getDefaultInstance();
        service.setOnScanCallback(new BluetoothService.OnBluetoothScanCallback() {
            @Override
            public void onDeviceDiscovered(BluetoothDevice device, int rssi) {
                if(device.getName()!=null&&device.getAddress()!=null) {
                    BtDisp bt = new BtDisp(device.getName(), device.getAddress(), "dopo");
                    btList.add(bt);
                    recyclerView.setAdapter(adapter);

                }
            }

            @Override
            public void onStartScan() {
                BtDisp bt = new BtDisp("mio start", "device.getAddress()", "dopo");
                btList.add(bt);
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onStopScan() {
            }
        });
        service.setOnEventCallback(new BluetoothService.OnBluetoothEventCallback() {
            @Override
            public void onDataRead(byte[] buffer, int length) {

            }

            @Override
            public void onStatusChange(BluetoothStatus status) {
            }

            @Override
            public void onDeviceName(String deviceName) {
            }

            @Override
            public void onToast(String message) {
            }

            @Override
            public void onDataWrite(byte[] buffer) {
            }
        });
        service.startScan(); // See also service.stopScan();

        //service.connect(device); // See also service.disconnect();

        binding.buttonFirst.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(FirstFragment.this)
                        .navigate(R.id.action_FirstFragment_to_SecondFragment);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}