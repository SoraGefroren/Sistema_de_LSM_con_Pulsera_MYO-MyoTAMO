package naoki.ble_myo;

// Librerias para Bluetooth
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
// Otras Librerias
import android.os.Handler;
import android.util.Log;
import android.widget.TextView;
import java.util.LinkedList;
import java.util.Queue;
import java.util.UUID;
// Librerias para Graficos
import android.graphics.Color;
import com.echo.holographlibrary.Line;
import com.echo.holographlibrary.LineGraph;
import com.echo.holographlibrary.LinePoint;
// Librerias MyoTamoPrototype
import com.sora.prototype.myotamoprototype.MainActivity;
import com.sora.prototype.myotamoprototype.detector.EmgData_Number;
// Librerias de candados
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class MyoGattCallback extends BluetoothGattCallback {
    /* ******************************************* */
    /** Service ID */
    private static final String MYO_CONTROL_ID  = "d5060001-a904-deb9-4748-2c7f4a124842";
    private static final String MYO_EMG_DATA_ID = "d5060005-a904-deb9-4748-2c7f4a124842";
    /** Characteristics ID */
    private static final String MYO_INFO_ID = "d5060101-a904-deb9-4748-2c7f4a124842";
    private static final String FIRMWARE_ID = "d5060201-a904-deb9-4748-2c7f4a124842";
    private static final String COMMAND_ID  = "d5060401-a904-deb9-4748-2c7f4a124842";
    private static final String EMG_0_ID    = "d5060105-a904-deb9-4748-2c7f4a124842";
    /** android Characteristic ID (from Android Samples/BluetoothLeGatt/SampleGattAttributes.CLIENT_CHARACTERISTIC_CONFIG) */
    private static final String CLIENT_CHARACTERISTIC_CONFIG = "00002902-0000-1000-8000-00805f9b34fb";
    // Variables para Bluetooth
    private Queue<BluetoothGattDescriptor> descriptorWriteQueue = new LinkedList<BluetoothGattDescriptor>();
    private Queue<BluetoothGattCharacteristic> readCharacteristicQueue = new LinkedList<BluetoothGattCharacteristic>();
    private BluetoothGatt mBluetoothGatt;
    private BluetoothGattCharacteristic mCharacteristic_command;
    private BluetoothGattCharacteristic mCharacteristic_emg0;
    // Variables para de la Interfaz o GUI
    private TextView txt_textZone;         // Zona de Texto
    private TextView txt_gestureZone;      // Contador de muestras
    private TextView txt_rnaTextZone;      // Resultado del analisis de los datos EMG
    private LineGraph[] linesGraph;        // Graficos por sensor en tiempo real
    private TextView[] txt_emgDataSensors; // Para ver los valores de los datos EMG
    // Variables de Graficos (Implementacion de terceros)
    private final int numberDataList = 50; // Numero de lineas en grafico
    private final int addNumberDataList = 100;
    private int[][] dataList_a = new int[8][50]; // Para guardar primeros 8 datos EMG de 16 elementos
    private int[][] dataList_b = new int[8][50]; // Para guardar ultimos 8 datos EMG de 16 elementos
    private int[][] dataList_x = new int[8][50]; // Para guardar los datos EMG de 08 elementos
    // Others Vars
    private MyoCommandList commandList = new MyoCommandList();
    private Handler mHandler;
    private String callback_msg;
    // Other Vars
    private String TAG = "MyoTAMO-MyoGatt";                   // TAG para la Visualizacion de MSJ en Consola
    private EmgData_Number emgData_TamoNumber = null; // Implementacion para datos EMG
    private final static Lock padlock_MyoGatt = new ReentrantLock(); // Candado de control para el Administrador de señas
    // -----------------------------------------------------
    // -----------------------------------------------------
    // ----- Constructor -----------------------------------
    // -----------------------------------------------------
    // -----------------------------------------------------
    /**
     * Constructor para el MyoGattCallback
     * @param handler Para llevar el control de los datos EMG (Hilo)
     * @param textView Zona de Texto
     * @param gestureView Contador de muestras
     * @param rnaView Zona para ver el resultado del analisis de los datos EMG
     * @param linesView Lineas o Graficos por sensor
     * @param emgDatasView Para ver los valores numericos por sensor
     */
    public MyoGattCallback(Handler handler, TextView textView, TextView gestureView, TextView rnaView, LineGraph[] linesView, TextView[] emgDatasView) {
        mHandler = handler;                  // Para Hilo/Tarea
        txt_textZone = textView;             // Zona de texto
        txt_gestureZone = gestureView;       // Contador de Muestras
        txt_rnaTextZone = rnaView;           // Resultado del analisis de los datos EMG
        linesGraph = linesView;              // Graficos por sensor
        txt_emgDataSensors = emgDatasView;   // Para ver los datos EMG de forma numerica
    }
    // -----------------------------------------------------
    // -----------------------------------------------------
    // ----- Metodos Heredados -----------------------------
    // -----------------------------------------------------
    // -----------------------------------------------------
    @Override
    public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
        super.onConnectionStateChange(gatt, status, newState);
        Log.d(TAG, "La conexión cambio de estado: " + status + " -> " + newState);
        if (newState == BluetoothProfile.STATE_CONNECTED) {
            // GATT Connected // Searching GATT Service
            gatt.discoverServices();
        } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
            // GATT Disconnected
            stopCallback();
            Log.d(TAG,"Bluetooth Desconectado");
        }
    }
    @Override
    public void onServicesDiscovered(BluetoothGatt gatt, int status) {
        super.onServicesDiscovered(gatt, status);
        Log.d(TAG, "onServicesDiscovered received: " + status);
        if (status == BluetoothGatt.GATT_SUCCESS) {
            // Find GATT Service
            BluetoothGattService service_emg = gatt.getService(UUID.fromString(MYO_EMG_DATA_ID));
            if (service_emg == null) {
                Log.d(TAG,"No Myo EMG-Data Service !!");
            } else {
                Log.d(TAG, "Find Myo EMG-Data Service !!");
                // Getting CommandCharacteristic
                mCharacteristic_emg0 = service_emg.getCharacteristic(UUID.fromString(EMG_0_ID));
                if (mCharacteristic_emg0 == null) {
                    callback_msg = "No se encontro EMG-Data";
                } else {
                    // Setting the notification
                    boolean registered_0 = gatt.setCharacteristicNotification(mCharacteristic_emg0, true);
                    if (!registered_0) {
                        Log.d(TAG,"EMG-Data Notification FALSE !!");
                    } else {
                        Log.d(TAG,"EMG-Data Notification TRUE !!");
                        // Turn ON the Characteristic Notification
                        BluetoothGattDescriptor descriptor_0 = mCharacteristic_emg0.getDescriptor(UUID.fromString(CLIENT_CHARACTERISTIC_CONFIG));
                        if (descriptor_0 != null ){
                            descriptor_0.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                            writeGattDescriptor(descriptor_0);
                            Log.d(TAG,"Set descriptor");
                        } else {
                            Log.d(TAG,"No descriptor");
                        }
                    }
                }
            }
            BluetoothGattService service = gatt.getService(UUID.fromString(MYO_CONTROL_ID));
            if (service == null) {
                Log.d(TAG,"No Myo Control Service !!");
            } else {
                Log.d(TAG, "Find Myo Control Service !!");
                // Get the MyoInfoCharacteristic
                BluetoothGattCharacteristic characteristic =
                        service.getCharacteristic(UUID.fromString(MYO_INFO_ID));
                if (characteristic == null) {
                } else {
                    Log.d(TAG, "Find read Characteristic !!");
                    //put the characteristic into the read queue
                    readCharacteristicQueue.add(characteristic);
                    //if there is only 1 item in the queue, then read it.  If more than 1, we handle asynchronously in the callback above
                    //GIVE PRECEDENCE to descriptor writes.  They must all finish first.
                    if((readCharacteristicQueue.size() == 1) && (descriptorWriteQueue.size() == 0)) {
                        mBluetoothGatt.readCharacteristic(characteristic);
                    }
                }
                // Get CommandCharacteristic
                mCharacteristic_command = service.getCharacteristic(UUID.fromString(COMMAND_ID));
                if (mCharacteristic_command == null) {
                } else {
                    Log.d(TAG, "Find command Characteristic !!");
                }
            }
        }
    }
    @Override
    public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
        if (status == BluetoothGatt.GATT_SUCCESS) {
            Log.d(TAG, "Callback: Wrote GATT Descriptor successfully.");
        }
        else{
            Log.d(TAG, "Callback: Error writing GATT Descriptor: "+ status);
        }
        descriptorWriteQueue.remove();  //pop the item that we just finishing writing
        //if there is more to write, do it!
        if(descriptorWriteQueue.size() > 0)
            mBluetoothGatt.writeDescriptor(descriptorWriteQueue.element());
        else if(readCharacteristicQueue.size() > 0)
            mBluetoothGatt.readCharacteristic(readCharacteristicQueue.element());
    }
    @Override
    public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
        if (status == BluetoothGatt.GATT_SUCCESS) {
            Log.d(TAG, "onCharacteristicWrite success");
        } else {
            Log.d(TAG, "onCharacteristicWrite error: " + status);
        }
    }
    // *************************************************************************************************
    // *************************************************************************************************
    // ----- Se ejecuta al cambiar la informacion que envia la Pulsera MYO -----------------------------
    // *************************************************************************************************
    // *************************************************************************************************
    long last_send_never_sleep_time_ms = System.currentTimeMillis();
    final static long NEVER_SLEEP_SEND_TIME = 10000;  // Milli Second
    /**
     * Este metodo controla el flujo de datos EMG de la Pulsera MYO al equipo movil
     *
     */
    @Override
    public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
        if (EMG_0_ID.equals(characteristic.getUuid().toString())) {
            long systemTime_ms = System.currentTimeMillis();
            // --------------- ------------------------------------------ --------------------------
            // --------------- ------------------------------------------ --------------------------
            // --------------- ------------------------------------------ --------------------------
            // Informacion EMG ------------------------------------------ --------------------------
            // --------------- ------------------------------------------ --------------------------
            // --------------- ------------------------------------------ --------------------------
            // --------------- ------------------------------------------ --------------------------
            emgData_TamoNumber = new EmgData_Number(characteristic.getValue()); // Obtener datos EMG
            mHandler.post(new Runnable() { // Hilo/Tarea para el manejo de datos EMG
                @Override
                public void run() {
                    // +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
                    // +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
                    try{
                        /* Si no esta ocupado el Manejador de Señas, entonces se iniciara un hilo para
                           administrar los datos EMG */
                        if(!MainActivity.gestureManager.isBusy()){ 
                            new Thread(){
                                public void run() {
                                    // Mandar al administrador de señas los datos EMG obtenidos
                                    /* Esta es la parte de interes al prototipo */
                                    MainActivity.gestureManager.setNew_EmgData_Number(emgData_TamoNumber);
                                }
                            }.start();
                        }
                        // Se obtiene la ultima actualizacion del mensaje generado por el analisis de los datos EMG
                        txt_textZone.setText(MainActivity.gestureManager.getMessageToTextZone());
                        // Se obtiene el numero de muestra en que se encuentra
                        txt_gestureZone.setText(MainActivity.gestureManager.getMessageToGestureZone()+"");
                        // Nos muestra el resultado del analisis de datos EMG
                        txt_rnaTextZone.setText(MainActivity.gestureManager.getMessageToRNATextZone());
                    }catch (Exception e){
                        Log.d(TAG, ">>>> [ Error general en el MainActivity.gestureManager ] <<<<");
                    }
                    // +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
                    // +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
                    // +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
                    // +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
                    // +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
                    // +++++ GRAFICACION +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
                    // +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
                    // +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
                        if(MainActivity.bol_viewEmgData16){ // Ver datos EMG16
                            // ---------------------- --------------------------------------------------
                            int[] emgData16; // Par guardar los 16 elementos de datos EMG
                            if(MainActivity.bol_normalTypeGraph){ // Verificar si se solicita datos EMG son normales
                                // Obtener datos EMG normales
                                emgData16 = emgData_TamoNumber.getArrayEmgData_tam16();
                            }else{ // Verificar si no se solicita datos EMG son normales
                                // Obtener datos EMG normalizados
                                emgData16 = emgData_TamoNumber.getNormalize_fromZeroToHundred_Int_EmgData_tam16();
                            }
                            // Se actualiza los TextView para la visualizacion de los datos EMG en formato numerico
                            for(int inputIndex = 0;inputIndex<8;inputIndex++) {
                                MyoGattCallback.padlock_MyoGatt.lock();
                                txt_emgDataSensors[0+inputIndex].setText(""+emgData16[0+inputIndex]);
                                txt_emgDataSensors[8+inputIndex].setText(""+emgData16[8+inputIndex]);
                                MyoGattCallback.padlock_MyoGatt.unlock();
                                dataList_a[inputIndex][0] = emgData16[0+inputIndex];
                                dataList_b[inputIndex][0] = emgData16[8+inputIndex]; //7+inputIndex
                            }
                            try {
                                // SENSOR_LineGraphINDEX_
                                int number = numberDataList;
                                int addNumber = addNumberDataList;
                                // Crear las Lineas Graficas por sensor
                                Line[] lines = {new Line(),new Line(),new Line(),new Line(),new Line(),new Line(),new Line(),new Line()};
                                // Actulizar las lineas graficas por sensor
                                while (0 < number) {
                                    number--;
                                    addNumber--;
                                    if(number != 0){ // Mover a los primeros 8 datos EMG hacia adelante en la Grafica
                                        for(int setDatalistIndex = 0;setDatalistIndex < 8;setDatalistIndex++){
                                            dataList_a[setDatalistIndex][number] = dataList_a[setDatalistIndex][number - 1];
                                        }
                                    } 
                                    /* Crear puntos sobre las Lineas Graficas con base a los primeros 8 datos EMG */
                                    LinePoint linesPointA[] = {new LinePoint(),new LinePoint(),new LinePoint(),new LinePoint(),new LinePoint(),new LinePoint(),new LinePoint(),new LinePoint()};
                                    for(int iLinePoint = 0; iLinePoint < 8; iLinePoint++){
                                        linesPointA[iLinePoint].setY(dataList_a[iLinePoint][number]);
                                        linesPointA[iLinePoint].setX(addNumber);
                                    }
                                    addNumber--;
                                    if(number != 0){ // Mover a los ultimos 8 datos EMG hacia adelante en la Grafica
                                        for(int setDatalistIndex = 0;setDatalistIndex < 8;setDatalistIndex++) {
                                            dataList_b[setDatalistIndex][number] = dataList_b[setDatalistIndex][number - 1];
                                        }
                                    }
                                    /* Crear puntos sobre las Lineas Graficas con base a los ultimos 8 datos EMG */
                                    LinePoint linesPointB[] = {new LinePoint(),new LinePoint(),new LinePoint(),new LinePoint(),new LinePoint(),new LinePoint(),new LinePoint(),new LinePoint()};
                                    for(int iLinePoint = 0; iLinePoint < 8; iLinePoint++) {
                                        linesPointB[iLinePoint].setY(dataList_b[iLinePoint][number]);
                                        linesPointB[iLinePoint].setX(addNumber);
                                    }
                                    // Añadir los puntos en cada Linea para ser Graficados
                                    for(int iLine = 0; iLine < 8; iLine++){
                                        lines[iLine].addPoint(linesPointA[iLine]);
                                        lines[iLine].addPoint(linesPointB[iLine]);
                                    }
                                }
                                // Establecer el color de cada Linea para la Grafica
                                lines[0].setColor(Color.argb(0x66, 0xff, 0,    0xff));
                                lines[1].setColor(Color.argb(0x66, 0xff, 0x00, 0x00));
                                lines[2].setColor(Color.argb(0x66, 0x66, 0x33, 0xff));
                                lines[3].setColor(Color.argb(0x66, 0xff, 0x66, 0x33));
                                lines[4].setColor(Color.argb(0x66, 0xff, 0x33, 0x66));
                                lines[5].setColor(Color.argb(0x66, 0x00, 0x33, 0xff));
                                lines[6].setColor(Color.argb(0x66, 0x00, 0x33, 0x33));
                                lines[7].setColor(Color.argb(0x66, 0x66, 0xcc, 0x66));
                                // Actualizar las lineas del grafico
                                for(int iLine = 0; iLine < 8; iLine++) {
                                    lines[iLine].setShowingPoints(false);
                                    linesGraph[iLine].removeAllLines();
                                    linesGraph[iLine].addLine(lines[iLine]);
                                    // Si es data normal ira de -128 a 128
                                    if(MainActivity.bol_normalTypeGraph){
                                        linesGraph[iLine].setRangeY(-128, 128);
                                    }else{ // Si no es data normal ira de -10 a 10
                                        linesGraph[iLine].setRangeY(-10, 10);
                                    }
                                }
                            }catch (Exception e){
                                Log.d(TAG, ">>>> [ Error al actualizar la Grafica de un sensor ] <<<<");
                            }
                        }else{
                            // ---------------------- ------------------------------------------------------
                            int[] emgData8; // Para guardar los datos EMG de 8 elementos
                            if(MainActivity.bol_normalTypeGraph){ // Verificar si se solicita datos EMG son normales
                                // Obtener datos EMG normales
                                emgData8 = emgData_TamoNumber.getArrayEmgData_tam08();
                            }else{ // Verificar si no se solicita datos EMG son normales
                                // Obtener datos EMG normalizados
                                emgData8 = emgData_TamoNumber.getNormalize_fromZeroToHundred_Int_EmgData_tam08();
                            }
                            // Se actualiza los TextView para la visualizacion de los datos EMG en formato numerico
                            for(int inputIndex = 0;inputIndex<8;inputIndex++) {
                                MyoGattCallback.padlock_MyoGatt.lock();
                                txt_emgDataSensors[inputIndex].setText(""+emgData8[inputIndex]);
                                // Este valor se queda en ceros porque es para para EMG16
                                txt_emgDataSensors[8+inputIndex].setText("-000"); 
                                MyoGattCallback.padlock_MyoGatt.unlock();
                                dataList_x[inputIndex][0] = emgData8[inputIndex];
                            }
                            try {
                                // SENSOR_LineGraphINDEX_
                                int number = numberDataList;
                                int addNumber = addNumberDataList;
                                // Crear las Lineas Graficas por sensor
                                Line[] lines = {new Line(),new Line(),new Line(),new Line(),new Line(),new Line(),new Line(),new Line()};
                                while (0 < number) {
                                    number--;
                                    addNumber--;
                                    // Mover/Actulizar los valores de las lineas del grafico
                                    if(number != 0){
                                        for(int setDatalistIndex = 0;setDatalistIndex < 8;setDatalistIndex++){
                                            dataList_x[setDatalistIndex][number] = dataList_x[setDatalistIndex][number - 1];
                                        }
                                    }
                                    // Crear actualizacion de lineas para el Grafico
                                    LinePoint linesPointA[] = {new LinePoint(),new LinePoint(),new LinePoint(),new LinePoint(),new LinePoint(),new LinePoint(),new LinePoint(),new LinePoint()};
                                    for(int iLinePoint = 0; iLinePoint < 8; iLinePoint++){
                                        linesPointA[iLinePoint].setY(dataList_x[iLinePoint][number]);
                                        linesPointA[iLinePoint].setX(addNumber);
                                        lines[iLinePoint].addPoint(linesPointA[iLinePoint]);
                                    }
                                }
                                // Establecer color de las lineas del grafico
                                lines[0].setColor(Color.argb(0x66, 0xff, 0,    0xff));
                                lines[1].setColor(Color.argb(0x66, 0xff, 0x00, 0x00));
                                lines[2].setColor(Color.argb(0x66, 0x66, 0x33, 0xff));
                                lines[3].setColor(Color.argb(0x66, 0xff, 0x66, 0x33));
                                lines[4].setColor(Color.argb(0x66, 0xff, 0x33, 0x66));
                                lines[5].setColor(Color.argb(0x66, 0x00, 0x33, 0xff));
                                lines[6].setColor(Color.argb(0x66, 0x00, 0x33, 0x33));
                                lines[7].setColor(Color.argb(0x66, 0x66, 0xcc, 0x66));
                                // Actualizar los Graficos
                                for(int iLine = 0; iLine < 8; iLine++) {
                                    lines[iLine].setShowingPoints(false);
                                    linesGraph[iLine].removeAllLines();
                                    linesGraph[iLine].addLine(lines[iLine]);
                                    // Si es data EMG norma su valor ira de 0 a 128
                                    if(MainActivity.bol_normalTypeGraph){
                                        linesGraph[iLine].setRangeY(0, 128);
                                    }else{ // Si no es data EMG norma su valor ira de 0 a 10
                                        linesGraph[iLine].setRangeY(0, 10);
                                    }
                                }
                            }catch (Exception e){
                                Log.d(TAG, ">>>> [ Error al actualizar la Grafica de un sensor ] <<<<");
                            }
                        }
                    // +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
                    // +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
                }
            });
            // --------------- ------------------------------------------ --------------------------
            // --------------- ------------------------------------------ --------------------------
            // --------------- ------------------------------------------ --------------------------
            // --------------- ------------------------------------------ --------------------------
            // --------------- ------------------------------------------ --------------------------
            // --------------- ------------------------------------------ --------------------------
            // --------------- ------------------------------------------ --------------------------
            if (systemTime_ms > last_send_never_sleep_time_ms + NEVER_SLEEP_SEND_TIME) {
                // set Myo [Never Sleep Mode]
                setMyoControlCommand(commandList.sendUnSleep());
                last_send_never_sleep_time_ms = systemTime_ms;
            }
        }
    }
    // -------------------------------------------------
    // -------------------------------------------------
    // ----- Otros Metodos -----------------------------
    // -------------------------------------------------
    // -------------------------------------------------
    public void writeGattDescriptor(BluetoothGattDescriptor d){
        //put the descriptor into the write queue
        descriptorWriteQueue.add(d);
        //if there is only 1 item in the queue, then write it.  If more than 1, we handle asynchronously in the callback above
        if(descriptorWriteQueue.size() == 1){
            mBluetoothGatt.writeDescriptor(d);
        }
    }
    // *************************************************************************
    // *************************************************************************
    // ----- Se ejecuta al conectar la Pulsera MYO -----------------------------
    // *************************************************************************
    // *************************************************************************
    /**
     * Este metodo indica cuando se ha establecido conexion con la Pulsera MYO
     */
    public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
        readCharacteristicQueue.remove();
        if (status == BluetoothGatt.GATT_SUCCESS) {
            if (UUID.fromString(FIRMWARE_ID).equals(characteristic.getUuid())) {
                // Myo Firmware Infomation
                final byte[] data = characteristic.getValue();
                if (data != null && data.length > 0) {
                    ByteReader byteReader = new ByteReader();
                    byteReader.setByteData(data);
                    Log.d(TAG, String.format("This Version is %d.%d.%d - %d",
                            byteReader.getShort(), byteReader.getShort(),
                            byteReader.getShort(), byteReader.getShort()));
                }
                if (data == null) { Log.d(TAG,"Characteristic String is " + characteristic.toString()); }
            } else if (UUID.fromString(MYO_INFO_ID).equals(characteristic.getUuid())) {
                // Myo Device Information
                final byte[] data = characteristic.getValue();
                if (data != null && data.length > 0) {
                    // -----------------------------------------------------------------------------
                    // -----------------------------------------------------------------------------
                    /* Para indicar que ya se puede iniciar la deteccion de señas,
                       Nos dice que se ha establecido conexion con una Pulsera MYO */
                    callback_msg = "Ya puedes iniciar la detección de señas.";
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            txt_textZone.setText(callback_msg);
                        }
                    });
                    // -----------------------------------------------------------------------------
                    // -----------------------------------------------------------------------------
                }
            }
        } else { Log.d(TAG, "onCharacteristicRead error: " + status); }
        if(readCharacteristicQueue.size() > 0){ mBluetoothGatt.readCharacteristic(readCharacteristicQueue.element()); }
    }
    // -------------------------------------------------------------------------
    public void setBluetoothGatt(BluetoothGatt gatt) {
        mBluetoothGatt = gatt;
    }
    // -------------------------------------------------------------------------
    public boolean setMyoControlCommand(byte[] command) {
        if ( mCharacteristic_command != null) {
            mCharacteristic_command.setValue(command);
            int i_prop = mCharacteristic_command.getProperties();
            if (i_prop == BluetoothGattCharacteristic.PROPERTY_WRITE) {
                if (mBluetoothGatt.writeCharacteristic(mCharacteristic_command)) {
                    return true;
                }
            }
        }
        return false;
    }
    // -----------------------------------------------------------------------------------------
    // -----------------------------------------------------------------------------------------
    // ----- Se ejecuta al terminar la conexión con la Pulsera MYO -----------------------------
    // -----------------------------------------------------------------------------------------
    // -----------------------------------------------------------------------------------------
    public void stopCallback() {
        // Before the closing GATT, set Myo [Normal Sleep Mode].
        setMyoControlCommand(commandList.sendNormalSleep());
        descriptorWriteQueue = new LinkedList<BluetoothGattDescriptor>();
        readCharacteristicQueue = new LinkedList<BluetoothGattCharacteristic>();
        if (mCharacteristic_command != null) {
            mCharacteristic_command = null;
        }
        if (mCharacteristic_emg0 != null) {
            mCharacteristic_emg0 = null;
        }
        if (mBluetoothGatt != null) {
            mBluetoothGatt = null;
        }
    }
}
