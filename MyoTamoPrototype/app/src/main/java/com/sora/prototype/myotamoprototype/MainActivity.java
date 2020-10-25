package com.sora.prototype.myotamoprototype;

// Librerias para soporte de Bluetooth
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothManager;
// Librerias para Activity Android
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.os.Handler;
import android.widget.TextView;
import android.widget.Toast;
// Librerias para la Graficación de elementos
/* Soporta los graficos para visualizar el comportamiento
   de los datos EMG en tiempo real */
import com.echo.holographlibrary.LineGraph;
// Librerias MyoTamoPrototype
import com.sora.prototype.myotamoprototype.detector.GestureFileReader;
import com.sora.prototype.myotamoprototype.detector.GestureManager;
// Librerias BLE_MYO (material de Terceros)
import naoki.ble_myo.MyoCommandList;
import naoki.ble_myo.MyoGattCallback;

// Clase principal de la APP
public class MainActivity extends AppCompatActivity implements BluetoothAdapter.LeScanCallback {
    // ********* ***********************************************************************************
    // ********* ***********************************************************************************
    // VARIABLES ***********************************************************************************
    // ********* ***********************************************************************************
    // Variables del detector de señas en tiempo real
    public static GestureManager gestureManager = null;
    // Variables Globales -----------------------
    /* Para saber si existe o no una conexión bluetooth con una Pulsera MYO */
    public static Boolean    isConnected = false;
    /* Mantiene el nombre del dispositivo bluetooth con quien la APP
       ha establecido una conexión */
    public static String     deviceName = null;
    // ------------------ -----------------------
    // Variables de control
    private Boolean          threadInfinite = true; // Control de Hilo para conexión
    /* Apoya el intercambio de texto entre "Iniciar" y "Detener" de un botón*/ 
    private Boolean          doDetecting = false;   
    // Elementos del Layout
    private TextView         txt_textZone;    // Zona de Texto
    private TextView         txt_gestureZone; // Contador de Muestras
    private TextView         txt_rnaTextZone; // Resultado del analisis EMG
    private Button           btn_detectEMG;   // Para Iniciar o Detener la detección de señas
    private Button           btn_changeEmgDataGraph; // Cambia los Graficos entre EMG08 y EMG16
    private Button           btn_changeTypeGraph;    // Cambia el tipo de datos EMG // Normal o Normalizados
    /* Graficos para ver el comportamiento de los sensores en tiempo real */
    private LineGraph[]      linesGraph = new LineGraph[8];         // Una grafica por sensor de la Pulser MYO
    private TextView[]       txt_sensorsEmgData = new TextView[16]; // Zona para poner los datos EMG de cada Grafica
    // Variables para Menu desplegable (material de Terceros)
    public static final int  MENU_CONECT = 0;
    public static final int  BTN_DESCONECT = 1;
    public static final int  BTN_TEST_CONECT = 2;
    // ------------------------------------ (material de Terceros)
    private static final int    REQUEST_ENABLE_BT = 1;
    private static final long   SCAN_PERIOD = 5000;
    // Vairables para BLUETOOTH 
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothGatt    mBluetoothGatt;
    // Variables de control para atrapar información EMG (material de Terceros)
    private Handler          mHandler;
    private MyoGattCallback  mMyoCallback;
    private MyoCommandList   commandList = new MyoCommandList();
    // TAG para la Visualizacion de MSJ en Consola
    private final static String TAG = "MyoTAMO-MainActivity";
    // ********* ***********************************************************************************
    // ********* ***********************************************************************************
    // METODOS HEREDADOS ***************************************************************************
    // ********* ***********************************************************************************
    @Override // Al inicializar la Actividad
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initializeInterface();
        gestureManager = new GestureManager(new GestureFileReader());
    }
    @Override // Al crear el Menu de opciones (Menu desplegable)
    // Este menu se ubcica en la parte superior derecha de la App
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, MENU_CONECT,     0,  "Conectar Pulsera MYO");
        menu.add(0, BTN_DESCONECT,   0,  "Desconectar Pulsera MYO");
        menu.add(0, BTN_TEST_CONECT, 0,  "Probar conexión");
        return true;
    }
    @Override // Cada vez que se escoga una opción del menu de opciones
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case MENU_CONECT: // Cargar Actividad para conexión Bluetooth
                Intent intentListBluetoothActivity = new Intent(this,ListBluetoothActivity.class);
                startActivity(intentListBluetoothActivity);
                return true;
            case BTN_DESCONECT: // Terminar conexión Bluetooth
                // Emitir mensaje en pantalla
                Toast.makeText(getApplicationContext(), "Pulsera MYO desconectada", Toast.LENGTH_SHORT).show();
                closeBLEGatt();
                return true;
            case BTN_TEST_CONECT: // Probar conexión con Pulsera MYO (Hacer vibrar a la Pulsera)
                if (mBluetoothGatt == null || !mMyoCallback.setMyoControlCommand(commandList.sendVibration3())) {
                    // Emitir mensaje en pantalla
                    Toast.makeText(getApplicationContext(), "No hay conexión con la Pulsera MYO", Toast.LENGTH_SHORT).show();
                }
                return true;
        }
        return false;
    }
    @Override // Al detener la Actividad
    public void onStop(){
        super.onStop();
        this.closeBLEGatt();
    }
    // ----------------------------------------------------------- ---------------------------------
    // ----------------------------------------------------------- ---------------------------------
    // Para la Busqueda de dispositivos Bluetooth (La Pulsera MYO)
    // ----------------------------------------------------------- ---------------------------------
    @Override // Cuando se busca la conexion Bluetooth con una Pulsera
    public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
        if (deviceName.equals(device.getName())) {
            mBluetoothAdapter.stopLeScan(this);
            mMyoCallback = new MyoGattCallback(mHandler, txt_textZone, txt_gestureZone, txt_rnaTextZone, linesGraph, txt_sensorsEmgData);
            mBluetoothGatt = device.connectGatt(this, false, mMyoCallback);
            mMyoCallback.setBluetoothGatt(mBluetoothGatt);
        }
    }
    @Override // Cada vez que ocurre una llamada tracera (Callback)
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ENABLE_BT && resultCode == RESULT_OK){
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mBluetoothAdapter.stopLeScan(MainActivity.this);
                }
            }, SCAN_PERIOD);
            mBluetoothAdapter.startLeScan(this);
        }
    }
    // ********* ***********************************************************************************
    // ********* ***********************************************************************************
    // INICIALIZAR INTERFAZ ************************************************************************
    // ********* ***********************************************************************************
    // Inicializar interfaz
    /**
     * Para inicializar los elementos o contenido de la interfaz correspondiente a la pantalla principal
     */
    private void initializeInterface(){
        txt_textZone = (TextView) findViewById(R.id.txt_textZone);      // Zona de Texto
        txt_gestureZone = (TextView)findViewById(R.id.txt_gestureZone); // Zona de Gesto (Muestras)
        txt_rnaTextZone = (TextView)findViewById(R.id.txt_rnaTextZone); // Zona RNA (Resultado del analisis EMG)
        btn_detectEMG = (Button)findViewById(R.id.btn_startOrStop);     // Botón para Iniciar/Detener detección EMG
        btn_changeEmgDataGraph = (Button)findViewById(R.id.btn_changeEmgDataGraph); // Botón para cambiar los Datoa EMG del grafico (EMG08 o EMG16)
        btn_changeTypeGraph = (Button)findViewById(R.id.btn_changeTypeGraph);       // Botón para cambiar el tipo de grafico (Normal o Normalizar)
        // Lineas de grafica por sensor de la Pulsera
        linesGraph[0] = (LineGraph) findViewById(R.id.holoGraphView_1);
        linesGraph[1] = (LineGraph) findViewById(R.id.holoGraphView_2);
        linesGraph[2] = (LineGraph) findViewById(R.id.holoGraphView_3);
        linesGraph[3] = (LineGraph) findViewById(R.id.holoGraphView_4);
        linesGraph[4] = (LineGraph) findViewById(R.id.holoGraphView_5);
        linesGraph[5] = (LineGraph) findViewById(R.id.holoGraphView_6);
        linesGraph[6] = (LineGraph) findViewById(R.id.holoGraphView_7);
        linesGraph[7] = (LineGraph) findViewById(R.id.holoGraphView_8);
        // TextView Para los datos EMG08
        txt_sensorsEmgData[0] = (TextView) findViewById(R.id.txt_sensor1_emgData_1);
        txt_sensorsEmgData[1] = (TextView) findViewById(R.id.txt_sensor2_emgData_1);
        txt_sensorsEmgData[2] = (TextView) findViewById(R.id.txt_sensor3_emgData_1);
        txt_sensorsEmgData[3] = (TextView) findViewById(R.id.txt_sensor4_emgData_1);
        txt_sensorsEmgData[4] = (TextView) findViewById(R.id.txt_sensor5_emgData_1);
        txt_sensorsEmgData[5] = (TextView) findViewById(R.id.txt_sensor6_emgData_1);
        txt_sensorsEmgData[6] = (TextView) findViewById(R.id.txt_sensor7_emgData_1);
        txt_sensorsEmgData[7] = (TextView) findViewById(R.id.txt_sensor8_emgData_1);
        // Anexo de TextView Para los datos EMG16
        txt_sensorsEmgData[8] = (TextView) findViewById(R.id.txt_sensor1_emgData_2);
        txt_sensorsEmgData[9] = (TextView) findViewById(R.id.txt_sensor2_emgData_2);
        txt_sensorsEmgData[10] = (TextView) findViewById(R.id.txt_sensor3_emgData_2);
        txt_sensorsEmgData[11] = (TextView) findViewById(R.id.txt_sensor4_emgData_2);
        txt_sensorsEmgData[12] = (TextView) findViewById(R.id.txt_sensor5_emgData_2);
        txt_sensorsEmgData[13] = (TextView) findViewById(R.id.txt_sensor6_emgData_2);
        txt_sensorsEmgData[14] = (TextView) findViewById(R.id.txt_sensor7_emgData_2);
        txt_sensorsEmgData[15] = (TextView) findViewById(R.id.txt_sensor8_emgData_2);
        initializeBluetoothProperties();
        // Iniciar Hilo infinito // Para controlar la conexion a dispositivos bluetooth
        new Thread(){
            public void run() {
                while(threadInfinite){
                    try{
                        conectMYODevice();
                        if(!isConnected){
                            Thread.sleep(100);
                        }else{
                            Thread.sleep(2500);
                        }
                    }catch (Exception e){
                    }
                }
            }
        }.start();
    }
    // Inicializar propiedades MYO (material de terceros)
    /**
     * Para inicializar las propiedades BLUETOOTH con la Pulsera MYO
     */
    public void initializeBluetoothProperties(){
        mHandler = new Handler();
        BluetoothManager mBluetoothManager = (BluetoothManager) getSystemService(BLUETOOTH_SERVICE);
        mBluetoothAdapter = mBluetoothManager.getAdapter();
    }
    // ********* ***********************************************************************************
    // ********* ***********************************************************************************
    // METODOS MODIFICADOS (Material de terceros) **************************************************
    // ********* ***********************************************************************************
    // Conectar Dispositivo MYO (Iniciar conexión bluetooth)
    /**
     * Para conectar la Pulsera MYO
     */
    public void conectMYODevice(){
        if (deviceName != null && !isConnected) {
            isConnected = true;
            // Ensures Bluetooth is available on the device and it is enabled. If not,
            // displays a dialog requesting user permission to enable Bluetooth.
            if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            } else {
                // Scanning Time out by Handler.
                // The device scanning needs high energy.
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mBluetoothAdapter.stopLeScan(MainActivity.this);
                    }
                }, SCAN_PERIOD);
                mBluetoothAdapter.startLeScan(this);
            }
        }
    }
    // Click en Botón, para Iniciar/Detener el proceso de detección de información EMG
    /**
     * Metodo para el boton de "INICIAR" o "DETENER" la deteccion de datos EMG
     */
    public void onClickEMGDetect(View v) {
        if(!doDetecting){ // Si no esta detectando datos EMG, entonces se Inicia esta detección
            if (mBluetoothGatt == null || !mMyoCallback.setMyoControlCommand(commandList.sendEmgOnly())) {
                Log.d(TAG, "No se puede iniciar la detección de señas");
            }
            // Emitir mensaje en pantalla
            Toast.makeText(getApplicationContext(), "Detección de señas iniciada ;D", Toast.LENGTH_SHORT).show();
            btn_detectEMG.setText("Detener");
            doDetecting = true;
            cleanTextZone();
        }else{ // Si esta detectando datos EMG, entonces se detiene esta detección
            if (mBluetoothGatt == null
                    || !mMyoCallback.setMyoControlCommand(commandList.sendUnsetData())
                    || !mMyoCallback.setMyoControlCommand(commandList.sendNormalSleep())) {
                Log.d(TAG, "No se puede detener la detección de señas");
            }
            // Emitir mensaje en pantalla
            Toast.makeText(getApplicationContext(), "Detección de señas detenida :|", Toast.LENGTH_SHORT).show();
            btn_detectEMG.setText("Iniciar");
            doDetecting = false;
        }
    }
    // ********* ***********************************************************************************
    // ********* ***********************************************************************************
    // MIS METODOS *********************************************************************************
    // ********* ***********************************************************************************
    // Click en Botón para Limpiar Zona de Texto
    /**
     * Metodo para limpiar la Zona de Texto
     */
    public void onClickCleanTextZone(View v) {
        cleanTextZone();
    }
    // Limpiar Zona de Texto
    /**
     * Metodo para limpiar la Zona de Texto, reiniciar el Contador de Muestras y reiniicar la Zona para el resultdo del analisis de los datos EMG
     */
    public void cleanTextZone(){
        /* El reinicio de los valores del Manejador de señas no implica reiniciar la RNA o Diccionario,
           solo implica el reinicio de variables de apoyo a su estado inicial */
        gestureManager.reloadDataHandGestureManager(); // Reiniciar las variables del manejador de señas
        txt_textZone.setText("");          // Zona de texto
        txt_gestureZone.setText("00");     // Muestras
        txt_rnaTextZone.setText("[]vs[]"); // Resultado del analisis EMG
    }
    // ********* ***********************************************************************************
    // ********* ***********************************************************************************
    // METODOS POR DEFECTO *************************************************************************
    // ********* ***********************************************************************************
    public void closeBLEGatt() {
        if (mBluetoothGatt == null) {
            return;
        }
        mMyoCallback.stopCallback();
        mBluetoothGatt.close();
        mBluetoothGatt = null;
    }
    // ********************************* ********************************* *************************
    // ********************************* ********************************* *************************
    // PARA MANEJAR LOS GRAFICOS DE CADA SENSOR ************************** *************************
    // ********************************* ********************************* *************************
    /* Para controlar los cambios entre las EMG08 y la EMG16, 
       por defecto en FALSO para poder visualizar la EMG08 */
    public static Boolean bol_viewEmgData16 = false;
    /**
     * Metodo cambiar el tipo de datos EMG vistos en la Grafica, sean de 08 o 16 elementos
     */
    public void onClickChangeEmgDataGraph(View v) {
        if(bol_viewEmgData16){ // Si estoy viendo la EMG16, cambiar por la EMG08
            bol_viewEmgData16 = false;
            btn_changeEmgDataGraph.setText("Emg16");
        }else{ // Si no estoy viendo la EMG16, cambiar entonces a la EMG16
            bol_viewEmgData16 = true;
            btn_changeEmgDataGraph.setText("Emg08");
        }
    }
    /* Para el control del tipo de datos EMG a ver en el grafico, 
       los datos EMG Normales o Normalizados */
    public static Boolean bol_normalTypeGraph = true;
    /**
     * Metodo cambiar el comportamiento de los datos EMG de la Grafica, sean Normalizados o no
     */
    public void onClick_changeGraphType(View v) {
        if(bol_normalTypeGraph){ // Si estoy bien los datos EMG Normales, entonces cambiar a datos EMG Normalizados
            bol_normalTypeGraph = false;
            btn_changeTypeGraph.setText("Normal");
        }else{ // Si no estoy bien los datos EMG Normales, entonces cambiar a datos EMG Normales
            bol_normalTypeGraph = true;
            btn_changeTypeGraph.setText("Normalizar");
        }
    }
}