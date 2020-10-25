package com.sora.prototype.myotamoprototype;

// Librerias para soporte de Bluetooth
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
// Librerias para Activity Android
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.ParcelUuid;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
// Otras librerias
import java.util.ArrayList;

// Clase para conectar la App con un dispositivo MYO, a traves de Bluetooth
public class ListBluetoothActivity extends ActionBarActivity implements BluetoothAdapter.LeScanCallback {
    // TAG para la Visualizacion de MSJs en Consola
    public static String TAG = "MyoTAMO-ListBlue";
    /** Intervalo de tiempo para escaner dispositivo (ms) */
    private static final long SCAN_PERIOD = 5000;
    /** Intent code for requesting Bluetooth enable */
    private static final int  REQUEST_ENABLE_BT = 1;
    /** ******************* ******************* */
    private Handler mHandler;
    private BluetoothAdapter mBluetoothAdapter;
    private ArrayList<String> deviceNames = new ArrayList<>();
    private ArrayAdapter<String> adapter;
    /** ******************* ******************* */
    /** ******************* ******************* */
    // Metodos Heredados
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_bluetooth);
        BluetoothManager mBluetoothManager = (BluetoothManager) getSystemService(BLUETOOTH_SERVICE);
        mBluetoothAdapter = mBluetoothManager.getAdapter();
        mHandler = new Handler();
        ListView lv = (ListView) findViewById(R.id.listView_bluetooth);
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_expandable_list_item_1, deviceNames);
        lv.setAdapter(adapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ListView listView = (ListView) parent;
                String item = (String) listView.getItemAtPosition(position);
                // Emitir mensaje en pantalla
                Toast.makeText(getApplicationContext(),
                        "Pulsera MYO seleccionada, por favor regrese a la pantalla anterior",
                        Toast.LENGTH_SHORT).show();
                MainActivity.deviceName = item;   // Seleccionar el dispositivo con el cual se iniciara la conexión Bluetooth
                MainActivity.isConnected = false; // Indicar que no existe alguna conexión Bluetooth para que esta se inicie
            }
        });
    }
    // ***************************************
    // ***************************************
    // **** Metodos para escaneo *************
    // ***************************************
    // ***************************************
    @Override
    public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
        // Device Log
        ParcelUuid[] uuids = device.getUuids();
        String uuid = "";
        if (uuids != null) {
            for (ParcelUuid puuid : uuids) {
                uuid += puuid.toString() + " ";
            }
        }
        String msg = "name=" + device.getName() + ", bondStatus="
                + device.getBondState() + ", address="
                + device.getAddress() + ", type" + device.getType()
                + ", uuids=" + uuid;
        Log.d(TAG + " BLEActivity", msg);
        if (device.getName() != null && !deviceNames.contains(device.getName())) {
            deviceNames.add(device.getName());
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ENABLE_BT && resultCode == RESULT_OK){
            scanDevice();
        }
    }
    // ********************************
    // ********************************
    // **** Otros Metodos *************
    // ********************************
    // ********************************
    // Click eb Boton Buscar
    public void onClickScan(View v) {
        scanDevice();
    }
    // Metodo para buscar dispositivos Bluetooth
    public void scanDevice() {
        // Ensures Bluetooth is available on the device and it is enabled. If not,
        // displays a dialog requesting user permission to enable Bluetooth.
        if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        } else {
            deviceNames.clear();
            // Scanning Time out by Handler.
            // The device scanning needs high energy.
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mBluetoothAdapter.stopLeScan(ListBluetoothActivity.this);
                    adapter.notifyDataSetChanged();
                    // Emitir mensaje en pantalla
                    Toast.makeText(getApplicationContext(), "Se ha terminado de buscar dispositivos bluetooth", Toast.LENGTH_SHORT).show();
                }
            }, SCAN_PERIOD);
            mBluetoothAdapter.startLeScan(ListBluetoothActivity.this);
        }
    }
}

