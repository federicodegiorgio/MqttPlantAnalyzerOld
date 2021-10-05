package fede.tesi.mqttplantanalyzer;

import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.le.ScanResult;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.douglasjunior.bluetoothclassiclibrary.BluetoothClassicService;
import com.github.douglasjunior.bluetoothclassiclibrary.BluetoothConfiguration;
import com.github.douglasjunior.bluetoothclassiclibrary.BluetoothService;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.welie.blessed.BluetoothBytesParser;
import com.welie.blessed.BluetoothCentralManager;
import com.welie.blessed.BluetoothCentralManagerCallback;
import com.welie.blessed.BluetoothPeripheral;
import com.welie.blessed.BluetoothPeripheralCallback;
import com.welie.blessed.BondState;
import com.welie.blessed.ConnectionPriority;
import com.welie.blessed.GattStatus;
import com.welie.blessed.HciStatus;
import com.welie.blessed.ScanFailure;
import com.welie.blessed.WriteType;

import org.jetbrains.annotations.NotNull;

import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.Objects;
import java.util.TimeZone;
import java.util.UUID;

import fede.tesi.mqttplantanalyzer.databinding.FragmentFirstBinding;
import timber.log.Timber;

import static android.app.Activity.RESULT_OK;
import static android.bluetooth.BluetoothGattCharacteristic.PROPERTY_WRITE;
import static com.welie.blessed.BluetoothBytesParser.FORMAT_SINT16;
import static com.welie.blessed.BluetoothBytesParser.FORMAT_UINT16;
import static com.welie.blessed.BluetoothBytesParser.FORMAT_UINT8;
import static com.welie.blessed.BluetoothBytesParser.bytes2String;


public class FirstFragment extends Fragment {

    private static final UUID SERVICE_UUID=UUID.fromString("4fafc201-1fb5-459e-8fcc-c5c9c331914b");
    private static final UUID CHARACTERISTIC_UUID = UUID.fromString("beb5483e-36e1-4688-b7f5-ea07361b26a8");

    private static final int WIFI_ACTIVITY_REQUEST_CODE = 0;

    public String ssid_Text = "";
    public String pwd_Text = "";
    private FragmentFirstBinding binding;
    public LinkedList<BluetoothPeripheral> btList=new LinkedList<>();
    public BtRecyclerViewAdapter adapterr;
    public BluetoothPeripheral seldev;
    public BluetoothCentralManager central;
    public RecyclerView recyclerView;
    public View view;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = FragmentFirstBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }


    // Callback for peripherals
    private final BluetoothPeripheralCallback peripheralCallback = new BluetoothPeripheralCallback() {
        @Override
        public void onServicesDiscovered(@NotNull BluetoothPeripheral peripheral) {
            // Request a higher MTU, iOS always asks for 185
            peripheral.requestMtu(185);


            // Request a new connection priority
            peripheral.requestConnectionPriority(ConnectionPriority.HIGH);

            Snackbar.make(view,"Post time to" +peripheral.getName(), Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
            // Read manufacturer and model number from the Device Information Service
            peripheral.readCharacteristic(SERVICE_UUID, CHARACTERISTIC_UUID);

            peripheral.setNotify(SERVICE_UUID, CHARACTERISTIC_UUID, true);
            // Turn on notifications for Current Time Service and write it if possible
            BluetoothGattCharacteristic usernameCharacteristic = peripheral.getCharacteristic(SERVICE_UUID, CHARACTERISTIC_UUID);
            if (usernameCharacteristic != null) {
                peripheral.setNotify(usernameCharacteristic, true);

                // If it has the write property we write the current time
                if ((usernameCharacteristic.getProperties() & PROPERTY_WRITE) > 0) {
                        BluetoothBytesParser parser = new BluetoothBytesParser();
                        String p="$"+"prova";
                        parser.setString(p);
                        peripheral.writeCharacteristic(usernameCharacteristic, parser.getValue(), WriteType.WITH_RESPONSE);
                }
            }
            /*
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle("Nome della rete");

            // Set up the input
            final EditText input = new EditText(getContext());
// Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
            input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            builder.setView(input);

// Set up the buttons
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    ssid_Text = input.getText().toString();

                    BluetoothGattCharacteristic ssidCharacteristic = peripheral.getCharacteristic(SERVICE_UUID, CHARACTERISTIC_UUID);
                    if (ssidCharacteristic != null) {
                        peripheral.setNotify(ssidCharacteristic, true);

                        // If it has the write property we write the current time
                        if ((ssidCharacteristic.getProperties() & PROPERTY_WRITE) > 0) {
                            BluetoothBytesParser parser = new BluetoothBytesParser();
                            String p=ssid_Text;
                            parser.setString(p);
                            peripheral.writeCharacteristic(ssidCharacteristic, parser.getValue(), WriteType.WITH_RESPONSE);
                        }
                    }

                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            

            while(builder.show().isShowing()){
                ;
            }

            AlertDialog.Builder builderr = new AlertDialog.Builder(getContext());
            builderr.setTitle("Password");
            // Set up the input
            final EditText inputt = new EditText(getContext());
// Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
            input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            builderr.setView(inputt);


// Set up the buttons
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    pwd_Text = input.getText().toString();
                    BluetoothGattCharacteristic pwdCharacteristic = peripheral.getCharacteristic(SERVICE_UUID, CHARACTERISTIC_UUID);
                    if (pwdCharacteristic != null) {
                        peripheral.setNotify(pwdCharacteristic, true);

                        // If it has the write property we write the current time
                        if ((pwdCharacteristic.getProperties() & PROPERTY_WRITE) > 0) {
                            BluetoothBytesParser parser = new BluetoothBytesParser();
                            String p=ssid_Text;
                            parser.setString(p);
                            peripheral.writeCharacteristic(pwdCharacteristic, parser.getValue(), WriteType.WITH_RESPONSE);
                        }
                    }
                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });

            builderr.show();*/




        }

        @Override
        public void onNotificationStateUpdate(@NotNull BluetoothPeripheral peripheral, @NotNull BluetoothGattCharacteristic characteristic, @NotNull GattStatus status) {
            if (status == GattStatus.SUCCESS) {
                final boolean isNotifying = peripheral.isNotifying(characteristic);
                Snackbar.make(view, "SUCCESS: Notify set to"+ isNotifying+"for"+ characteristic.getUuid(), Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                Timber.i("SUCCESS: Notify set to '%s' for %s", isNotifying, characteristic.getUuid());
            }
                else {
                    Timber.e("ERROR: Changing notification state failed for %s (%s)", characteristic.getUuid(), status);
                }
        }

        @Override
        public void onCharacteristicWrite(@NotNull BluetoothPeripheral peripheral, @NotNull byte[] value, @NotNull BluetoothGattCharacteristic characteristic, @NotNull GattStatus status) {
            if (status == GattStatus.SUCCESS) {
                Snackbar.make(view, bytes2String(value), Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                Timber.i("SUCCESS: Writing <%s> to <%s>", bytes2String(value), characteristic.getUuid());
            } else {
                Timber.i("ERROR: Failed writing <%s> to <%s> (%s)", bytes2String(value), characteristic.getUuid(), status);
            }
        }

        @Override
        public void onCharacteristicUpdate(@NotNull BluetoothPeripheral peripheral, @NotNull byte[] value, @NotNull BluetoothGattCharacteristic characteristic, @NotNull GattStatus status) {
            String str = new String(value, StandardCharsets.UTF_8); // for UTF-8 encodingbytes2
            Snackbar.make(view, str, Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
            /*if (status != GattStatus.SUCCESS) return;

            UUID characteristicUUID = characteristic.getUuid();
            BluetoothBytesParser parser = new BluetoothBytesParser(value);

            if (characteristicUUID.equals(BLOOD_PRESSURE_MEASUREMENT_CHARACTERISTIC_UUID)) {
                BloodPressureMeasurement measurement = new BloodPressureMeasurement(value);
                Intent intent = new Intent(MEASUREMENT_BLOODPRESSURE);
                intent.putExtra(MEASUREMENT_BLOODPRESSURE_EXTRA, measurement);
                sendMeasurement(intent, peripheral);
                Timber.d("%s", measurement);
            } else if (characteristicUUID.equals(TEMPERATURE_MEASUREMENT_CHARACTERISTIC_UUID)) {
                TemperatureMeasurement measurement = new TemperatureMeasurement(value);
                Intent intent = new Intent(MEASUREMENT_TEMPERATURE);
                intent.putExtra(MEASUREMENT_TEMPERATURE_EXTRA, measurement);
                sendMeasurement(intent, peripheral);
                Timber.d("%s", measurement);
            } else if (characteristicUUID.equals(HEARTRATE_MEASUREMENT_CHARACTERISTIC_UUID)) {
                HeartRateMeasurement measurement = new HeartRateMeasurement(value);
                Intent intent = new Intent(MEASUREMENT_HEARTRATE);
                intent.putExtra(MEASUREMENT_HEARTRATE_EXTRA, measurement);
                sendMeasurement(intent, peripheral);
                Timber.d("%s", measurement);
            } else if (characteristicUUID.equals(PLX_CONTINUOUS_MEASUREMENT_CHAR_UUID)) {
                PulseOximeterContinuousMeasurement measurement = new PulseOximeterContinuousMeasurement(value);
                if (measurement.getSpO2() <= 100 && measurement.getPulseRate() <= 220) {
                    Intent intent = new Intent(MEASUREMENT_PULSE_OX);
                    intent.putExtra(MEASUREMENT_PULSE_OX_EXTRA_CONTINUOUS, measurement);
                    sendMeasurement(intent, peripheral);
                }
                Timber.d("%s", measurement);
            } else if (characteristicUUID.equals(PLX_SPOT_MEASUREMENT_CHAR_UUID)) {
                PulseOximeterSpotMeasurement measurement = new PulseOximeterSpotMeasurement(value);
                Intent intent = new Intent(MEASUREMENT_PULSE_OX);
                intent.putExtra(MEASUREMENT_PULSE_OX_EXTRA_SPOT, measurement);
                sendMeasurement(intent, peripheral);
                Timber.d("%s", measurement);
            } else if (characteristicUUID.equals(WSS_MEASUREMENT_CHAR_UUID)) {
                WeightMeasurement measurement = new WeightMeasurement(value);
                Intent intent = new Intent(MEASUREMENT_WEIGHT);
                intent.putExtra(MEASUREMENT_WEIGHT_EXTRA, measurement);
                sendMeasurement(intent, peripheral);
                Timber.d("%s", measurement);
            } else if (characteristicUUID.equals((GLUCOSE_MEASUREMENT_CHARACTERISTIC_UUID))) {
                GlucoseMeasurement measurement = new GlucoseMeasurement(value);
                Intent intent = new Intent(MEASUREMENT_GLUCOSE);
                intent.putExtra(MEASUREMENT_GLUCOSE_EXTRA, measurement);
                sendMeasurement(intent, peripheral);
                Timber.d("%s", measurement);
            } else if (characteristicUUID.equals(CURRENT_TIME_CHARACTERISTIC_UUID)) {
                Date currentTime = parser.getDateTime();
                Timber.i("Received device time: %s", currentTime);

                // Deal with Omron devices where we can only write currentTime under specific conditions
                if (isOmronBPM(peripheral.getName())) {
                    BluetoothGattCharacteristic bloodpressureMeasurement = peripheral.getCharacteristic(BLP_SERVICE_UUID, BLOOD_PRESSURE_MEASUREMENT_CHARACTERISTIC_UUID);
                    if (bloodpressureMeasurement == null) return;

                    boolean isNotifying = peripheral.isNotifying(bloodpressureMeasurement);
                    if (isNotifying) currentTimeCounter++;

                    // We can set device time for Omron devices only if it is the first notification and currentTime is more than 10 min from now
                    long interval = abs(Calendar.getInstance().getTimeInMillis() - currentTime.getTime());
                    if (currentTimeCounter == 1 && interval > 10 * 60 * 1000) {
                        parser.setCurrentTime(Calendar.getInstance());
                        peripheral.writeCharacteristic(characteristic, parser.getValue(), WriteType.WITH_RESPONSE);
                    }
                }
            } else if (characteristicUUID.equals(BATTERY_LEVEL_CHARACTERISTIC_UUID)) {
                int batteryLevel = parser.getIntValue(FORMAT_UINT8);
                Timber.i("Received battery level %d%%", batteryLevel);
            } else if (characteristicUUID.equals(MANUFACTURER_NAME_CHARACTERISTIC_UUID)) {
                String manufacturer = parser.getStringValue(0);
                Timber.i("Received manufacturer: %s", manufacturer);
            } else if (characteristicUUID.equals(MODEL_NUMBER_CHARACTERISTIC_UUID)) {
                String modelNumber = parser.getStringValue(0);
                Timber.i("Received modelnumber: %s", modelNumber);
            } else if (characteristicUUID.equals(PNP_ID_CHARACTERISTIC_UUID)) {
                String modelNumber = parser.getStringValue(0);
                Timber.i("Received pnp: %s", modelNumber);
            }*/
        }

        @Override
        public void onMtuChanged(@NotNull BluetoothPeripheral peripheral, int mtu, @NotNull GattStatus status) {
            Timber.i("new MTU set: %d", mtu);
        }
    };





    private final BluetoothCentralManagerCallback bluetoothCentralManagerCallback = new BluetoothCentralManagerCallback() {

        @Override
        public void onConnectedPeripheral(@NotNull BluetoothPeripheral peripheral) {
            Timber.i("connected to '%s'", peripheral.getName());
        }

        @Override
        public void onConnectionFailed(@NotNull BluetoothPeripheral peripheral, final @NotNull HciStatus status) {
            Timber.e("connection '%s' failed with status %s", peripheral.getName(), status);
        }

        @Override
        public void onDisconnectedPeripheral(@NotNull final BluetoothPeripheral peripheral, final @NotNull HciStatus status) {
            Timber.i("disconnected '%s' with status %s", peripheral.getName(), status);

        }

        @Override
        public void onDiscoveredPeripheral(@NotNull BluetoothPeripheral peripheral, @NotNull ScanResult scanResult) {
            Timber.i("Found peripheral '%s'", peripheral.getName());
            //btList.add(peripheral);
            boolean ad=true;
            if(peripheral!=null) {
                for (BluetoothPeripheral b : btList) {
                    if(b.getName()==peripheral.getName())
                        ad=false;
                }
                if(ad)
                    btList.add(peripheral);
            }
            adapterr = new BtRecyclerViewAdapter(btList);
            recyclerView.setAdapter(adapterr);
            central.stopScan();
        }

        @Override
        public void onBluetoothAdapterStateChanged(int state) {
            Timber.i("bluetooth adapter changed state to %d", state);
            Snackbar.make(view, "bluetooth adapter changed state to "+state, Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
            if (state == BluetoothAdapter.STATE_ON) {
                // Bluetooth is on now, start scanning again
                // Scan for peripherals with a certain service UUIDs
                //central.startPairingPopupHack();
                central.scanForPeripherals();
            }

        }

        @Override
        public void onScanFailed(@NotNull ScanFailure scanFailure) {
            Timber.i("scanning failed with error %s", scanFailure);
            Snackbar.make(view, "scanning failed with error"+scanFailure.name(), Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
            BluetoothAdapter.getDefaultAdapter().disable();
        }
    };

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.view=view;
        central = new BluetoothCentralManager(this.getContext(), this.bluetoothCentralManagerCallback, new Handler(Looper.getMainLooper()));
        central.scanForPeripherals();
        recyclerView = binding.myRv;
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));
        adapterr = new BtRecyclerViewAdapter(btList);
        recyclerView.setAdapter(adapterr);
        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener (this.getContext(), recyclerView ,new RecyclerItemClickListener.OnItemClickListener() {
                    @Override public void onItemClick(View view, int position) {
                        Snackbar.make(view, btList.get(position).getName(), Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                        seldev=btList.get(position);
                        central.connectPeripheral(seldev, peripheralCallback);
                        Intent i = new Intent(getActivity(), WifiConnectionActivity.class);
                        startActivityForResult(i,WIFI_ACTIVITY_REQUEST_CODE);

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
        // Required
        config.uuid = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");

        BluetoothService.init(config);

        binding.buttonFirst.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(FirstFragment.this)
                        .navigate(R.id.action_FirstFragment_to_SecondFragment);
            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // check that it is the SecondActivity with an OK result
        if (requestCode == WIFI_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {

                // get String data from Intent
                ssid_Text = data.getStringExtra("ssid");
                pwd_Text = data.getStringExtra("pwd");
                Snackbar.make(view, "ssid= "+ssid_Text+"  pwd: "+pwd_Text, Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                // send ssid e pwd
                // Turn on notifications for Current Time Service and write it if possible
                BluetoothGattCharacteristic ssidCharacteristic = seldev.getCharacteristic(SERVICE_UUID, CHARACTERISTIC_UUID);
                if (ssidCharacteristic != null) {
                    seldev.setNotify(ssidCharacteristic, true);

                    // If it has the write property we write the current time
                    if ((ssidCharacteristic.getProperties() & PROPERTY_WRITE) > 0) {
                        BluetoothBytesParser parser = new BluetoothBytesParser();
                        String p="!"+ssid_Text;
                        parser.setString(p);
                        seldev.writeCharacteristic(ssidCharacteristic, parser.getValue(), WriteType.WITH_RESPONSE);
                    }
                }

                BluetoothGattCharacteristic pwdCharacteristic = seldev.getCharacteristic(SERVICE_UUID, CHARACTERISTIC_UUID);
                if (pwdCharacteristic != null) {
                    seldev.setNotify(pwdCharacteristic, true);

                    // If it has the write property we write the current time
                    if ((pwdCharacteristic.getProperties() & PROPERTY_WRITE) > 0) {
                        BluetoothBytesParser parser = new BluetoothBytesParser();
                        String p="?"+pwd_Text;
                        parser.setString(p);
                        seldev.writeCharacteristic(pwdCharacteristic, parser.getValue(), WriteType.WITH_RESPONSE);
                    }
                }

            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}