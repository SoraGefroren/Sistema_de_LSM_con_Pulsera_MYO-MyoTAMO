package com.sora.prototype.myotamoprototype.detector;

// Librerias
import android.util.Log;
import java.util.ArrayList;
// Librerias para candado
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Clase que representa al administrador de señas
 */
public class GestureManager {
    // TAG para la Visualizacion de MSJ en Consola
    private final static String TAG = "MyoTAMO-Manager";
    // Variables de Control - Candado
    private final static Lock padlock_gestureManager = new ReentrantLock();
    private final static Lock padlock_arrayEmgDataAccumulated = new ReentrantLock();
    // Variables de Control - Boleanos
    private boolean           busyGestureManager = false;
    private boolean           busyArrayAccumulated = false;
    // Variables de apoyo en para la traducción
    private String   lastTranslation = "";     // Ultima Letra detectada
    private String   messageAccumulated = "";  // Mensaje = Concatenacion de cada deletra detectada
    private int      sizeArrayAccumulated = 0; // Numero de elementos obtenidos como muestra
    // Variables para traducción de seña
        // Muestras - Arreglo de datos EMG
    private ArrayList<EmgData_Number>  arrayEmgDataNumberAccumulated = new ArrayList<EmgData_Number>();
    // Diccionario de palabras
    private Words_Tamo wordsTamo = new Words_Tamo();
    // RNA -- -- -- ---
        //  -- -- -- -- RNA para datos EMG de 08 elementos
    private MyoTamoRNA myoTamoRNA_Average_08_AllZones;
    private MyoTamoRNA myoTamoRNA_Average_08_Pack1;
    private MyoTamoRNA myoTamoRNA_Average_08_Pack2;
    private MyoTamoRNA myoTamoRNA_Average_08_Pack3;
    private MyoTamoRNA myoTamoRNA_Average_08_Pack4;
    private MyoTamoRNA myoTamoRNA_Average_08_Pack5;
    private MyoTamoRNA myoTamoRNA_Average_08_Pack6;
    private String letterRNA_tam08 = ""; // Letra detectada por RNA de 08 elementos
        //  -- -- -- -- RNA para datos EMG de 16 elementos
    private MyoTamoRNA myoTamoRNA_Average_16_AllZones;
    private MyoTamoRNA myoTamoRNA_Average_16_Pack1;
    private MyoTamoRNA myoTamoRNA_Average_16_Pack2;
    private MyoTamoRNA myoTamoRNA_Average_16_Pack3;
    private MyoTamoRNA myoTamoRNA_Average_16_Pack4;
    private MyoTamoRNA myoTamoRNA_Average_16_Pack5;
    private MyoTamoRNA myoTamoRNA_Average_16_Pack6;
    private String letterRNA_tam16 = ""; // Letra detectada por RNA de 16 elementos
        //  -- -- -- --
    // Variable de Tamaño de muestra
    private static final int MAXSizeArray = 25; // Maximo numero de muestras
    // -------------------------------------------------------------- ------------------------------
    // Constructor -------------------------------------------------- ------------------------------
    /**
     * Metodo de contruccion para el GestureManager
     * @param dataEMGFile Datos para la contruccion de las RNA para los datos EMG de 08 y 16 elementos, y para la contruccion del diccionario de palabras para el predictor de texto
     */
    public GestureManager(GestureFileReader dataEMGFile){
        if(dataEMGFile.getStatus()){ // Si fue correcta la lectura de los archivos/Base de datos
            wordsTamo = dataEMGFile.getWords_Tamo(); // Obtener el diccionario de palabras para el predictor de texto
            // Comprobar la correcta creacion de las RNA de la App
            boolean rnaRest = createRNA(dataEMGFile.getDatRNA_Weight1_x08(), dataEMGFile.getDatRNA_Weight2_x08(), dataEMGFile.getDatRNA_Bias1_x08(), dataEMGFile.getDatRNA_Bias2_x08(),
                                        dataEMGFile.getDatRNA_Weight1_x16(), dataEMGFile.getDatRNA_Weight2_x16(), dataEMGFile.getDatRNA_Bias1_x16(), dataEMGFile.getDatRNA_Bias2_x16());
            if(!rnaRest){ // Si la creacion de la RNA falla, entonces se crean RNA vacias
                messageAccumulated = "--- ERROR al cargar la Base de datos ---";
                Log.d(TAG, messageAccumulated);
                // Crear vacias las RNA para datos EMG de 08 elementos
                myoTamoRNA_Average_08_AllZones = new MyoTamoRNA();
                myoTamoRNA_Average_08_Pack1 = new MyoTamoRNA();
                myoTamoRNA_Average_08_Pack2 = new MyoTamoRNA();
                myoTamoRNA_Average_08_Pack3 = new MyoTamoRNA();
                myoTamoRNA_Average_08_Pack4 = new MyoTamoRNA();
                myoTamoRNA_Average_08_Pack5 = new MyoTamoRNA();
                myoTamoRNA_Average_08_Pack6 = new MyoTamoRNA();
                // Crear vacias las RNA para datos EMG de 16 elementos
                myoTamoRNA_Average_16_AllZones = new MyoTamoRNA();
                myoTamoRNA_Average_16_Pack1 = new MyoTamoRNA();
                myoTamoRNA_Average_16_Pack2 = new MyoTamoRNA();
                myoTamoRNA_Average_16_Pack3 = new MyoTamoRNA();
                myoTamoRNA_Average_16_Pack4 = new MyoTamoRNA();
                myoTamoRNA_Average_16_Pack5 = new MyoTamoRNA();
                myoTamoRNA_Average_16_Pack6 = new MyoTamoRNA();
            }
        }else{ // Si no fue correcta la lectura de los archivos/Base de datos
            messageAccumulated = "--- ERROR al cargar la Base de datos ---";
            Log.d(TAG, messageAccumulated);
        }
    }
    // ---------------------------------------------------------------------------------------------
    // -------------------------------------------------------------- ------------------------------
    // Al recibir nueva informacion EMG ------------------------------------------------------------
    /**
     * Metodo por donde ingresan nuevos datos EMG emitidos por la Pulsera MYO
     * @param emgDataDetected Datos EMG
     */
    public void setNew_EmgData_Number(EmgData_Number emgDataDetected){
        busyGestureManager = true; // Indicar que el Administrador de señas esta ocupado
        GestureManager.padlock_gestureManager.lock(); // Cerrar candado
        /*  Si la EmgData es diferente de nulo y
            Si las muestran obtenidas son iguales o mayores a MAXSizeArray  */
        if(emgDataDetected != null && goHandGestureManager(emgDataDetected)){
            busyArrayAccumulated = true; // Indicar que el Arministrador esta utilizando las muestras
            GestureManager.padlock_arrayEmgDataAccumulated.lock(); // Cerrar candado para muestras
                adminTranslation(translationHandGestureDetect()); // Analizar las muestras de datos EMG
            GestureManager.padlock_arrayEmgDataAccumulated.unlock(); // Liberar candado para muestras
            busyArrayAccumulated = false; // Indicar que el Arministrador ya no esta utilizando las muestras
        }
        GestureManager.padlock_gestureManager.unlock(); // Liberar candado
        busyGestureManager = false; // Indicar que el Administrador de señas esta desocupado
    }
    // -------------------------------------------------------------- ------------------------------
    // Información ------------------------------------------------------------------------ --------
    /**
     * Metodo para devolver el mensaje acumulado (concatenaciones de las señas traducidas)
     * @return Mensaje acumulado (resultado de la concatenacion de las traducciones)
     */
    public String getMessageToTextZone(){ // Devolver el mensaje acumulado (concatenacion de las letras detectadas)
        if(messageAccumulated != null && messageAccumulated != ""){
            return messageAccumulated;
        }
        return "Traduciendo...";
    }
    /**
     * Metodo para devolver el numero de muestras
     * @return Numero de muestras
     */
    public int getMessageToGestureZone(){ // Devolver el numero de muestras obtenidas
        if(sizeArrayAccumulated > 0 ){
            return sizeArrayAccumulated;
        }
        return 0;
    }
    /**
     * Metodo para devolver resultado del analisis de las muestras de datos EMG de 08 y 16 elementos
     * @return Resultado del analsis de los datos EMG
     */
    public String getMessageToRNATextZone(){ // Devolver el resultado del analisis de los datos EMG
        return "["+letterRNA_tam08+"]vs["+letterRNA_tam16+"]";
    }
    // -------------------------------------------------------------- ------------------------------
    // Administrador de la traducción --------------------------------------------------------------
    /**
     * Metodo para administrar la traduccion de una seña detectada
     * @param newTranslation Traduccion de una seña
     */
    private void adminTranslation(String newTranslation){ // Manejar la traduccion (deteccion de una seña)
        // Si la traduccion no es nula, y no es igual a la ultima
        if (newTranslation != null && !newTranslation.equals(lastTranslation)){
            lastTranslation = newTranslation; // Marcar la nueva traduccion como la ultima traduccion
            // Manejar la traduccion mediante el predictor de palabras
            messageAccumulated = wordsTamo.fixedUltimateWord(messageAccumulated + newTranslation);
        }
    }
    // ============================================================== ==============================
    // ============================================================== ==============================
    // ============================================================== ==============================
    // ============================================================== ==============================
    // ============================================================== ==============================
    // ============================================================== ==============================
    // -------------------------------------------------------------- ------------------------------
    // Detectar y traducir alguna señal ------------------------------------------------------------
    // private boolean savedArrayFile = true; // Para saber si los datos han sido guardados en un archivo
    /**
     * Metodo para analisar los datos EMG, si detecta una seña la traduce
     * @return Traduccion resultante del analisis de los datos EMG
     */
    private String translationHandGestureDetect(){
        String returnString = null; // Variable de salida o traduccion
        // +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
        /* */
        // Analisis de los datos EMG de 08 elementos, mediante RNA
        String restZone08 = myoTamoRNA_Average_08_AllZones.getStringRNAResult_ofEmgData( EmgData_Number.getPictureAverage_ofEmgNormalized_tam08(arrayEmgDataNumberAccumulated), false );
        switch (restZone08){
            case "100000":
                restZone08 = evaluateRNAResult(myoTamoRNA_Average_08_Pack1.getStringRNAResult_ofEmgData( EmgData_Number.getPictureAverage_ofEmgNormalized_tam08(arrayEmgDataNumberAccumulated), false ));
                break;
            case "010000":
                restZone08 = evaluateRNAResult(myoTamoRNA_Average_08_Pack2.getStringRNAResult_ofEmgData( EmgData_Number.getPictureAverage_ofEmgNormalized_tam08(arrayEmgDataNumberAccumulated), false ));
                break;
            case "001000":
                restZone08 = evaluateRNAResult(myoTamoRNA_Average_08_Pack3.getStringRNAResult_ofEmgData( EmgData_Number.getPictureAverage_ofEmgNormalized_tam08(arrayEmgDataNumberAccumulated), false ));
                break;
            case "000100":
                restZone08 = evaluateRNAResult(myoTamoRNA_Average_08_Pack4.getStringRNAResult_ofEmgData( EmgData_Number.getPictureAverage_ofEmgNormalized_tam08(arrayEmgDataNumberAccumulated), false ));
                break;
            case "000010":
                restZone08 = evaluateRNAResult(myoTamoRNA_Average_08_Pack5.getStringRNAResult_ofEmgData( EmgData_Number.getPictureAverage_ofEmgNormalized_tam08(arrayEmgDataNumberAccumulated), false ));
                break;
            case "000001":
                restZone08 = evaluateRNAResult(myoTamoRNA_Average_08_Pack6.getStringRNAResult_ofEmgData( EmgData_Number.getPictureAverage_ofEmgNormalized_tam08(arrayEmgDataNumberAccumulated), false ));
                break;
            default:
                restZone08 = "";
                break;
        }
        // Analisis de los datos EMG de 16 elementos, mediante RNA
        String restZone16 = myoTamoRNA_Average_16_AllZones.getStringRNAResult_ofEmgData( EmgData_Number.getPictureAverage_ofEmgNormalized_tam16(arrayEmgDataNumberAccumulated), false );
        switch (restZone16){
            case "100000":
                restZone16 = evaluateRNAResult(myoTamoRNA_Average_16_Pack1.getStringRNAResult_ofEmgData( EmgData_Number.getPictureAverage_ofEmgNormalized_tam16(arrayEmgDataNumberAccumulated), false ));
                break;
            case "010000":
                restZone16 = evaluateRNAResult(myoTamoRNA_Average_16_Pack2.getStringRNAResult_ofEmgData( EmgData_Number.getPictureAverage_ofEmgNormalized_tam16(arrayEmgDataNumberAccumulated), false ));
                break;
            case "001000":
                restZone16 = evaluateRNAResult(myoTamoRNA_Average_16_Pack3.getStringRNAResult_ofEmgData( EmgData_Number.getPictureAverage_ofEmgNormalized_tam16(arrayEmgDataNumberAccumulated), false ));
                break;
            case "000100":
                restZone16 = evaluateRNAResult(myoTamoRNA_Average_16_Pack4.getStringRNAResult_ofEmgData( EmgData_Number.getPictureAverage_ofEmgNormalized_tam16(arrayEmgDataNumberAccumulated), false ));
                break;
            case "000010":
                restZone16 = evaluateRNAResult(myoTamoRNA_Average_16_Pack5.getStringRNAResult_ofEmgData( EmgData_Number.getPictureAverage_ofEmgNormalized_tam16(arrayEmgDataNumberAccumulated), false ));
                break;
            case "000001":
                restZone16 = evaluateRNAResult(myoTamoRNA_Average_16_Pack6.getStringRNAResult_ofEmgData( EmgData_Number.getPictureAverage_ofEmgNormalized_tam16(arrayEmgDataNumberAccumulated), false ));
                break;
            default:
                restZone16 = "";
                break;
        }
        // Si el resultado de las RNA de datos EMG de 08 y 16 elementos es igual, entonces se ha encontrado una seña
        if(restZone08.equals(restZone16)){
            returnString = restZone08; // Se pone la traduccion de la seña encontrada en la variable de salida
        }
        letterRNA_tam08 = restZone08; // Resultado de la RNA de datos EMG de 08 elementos
        letterRNA_tam16 = restZone16; // Resultado de la RNA de datos EMG de 16 elementos
        /* */
        // +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
        // +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
        /* *
        // Codigo para la obtencion de los Maximos de los datos EMG de 08 y 16 elementos
        for (EmgData_Number myoEmgData:arrayEmgDataNumberAccumulated) {
            int[] emg08 = myoEmgData.getArrayEmgData_tam08();
            for (int i = 0; i < 8; i++){
                EmgData_Number.setNum_inArrayMax_EmgData_tam08(emg08[i], i);
            }
            int[] emg16 = myoEmgData.getArrayEmgData_tam16();
            for (int i = 0; i < 16; i++){
                EmgData_Number.setNum_inArrayMax_EmgData_tam16(Math.abs(emg16[i]), i);
            }
        }
        Log.d(TAG, "----- MaxEmg08: " + EmgData_Number.getLine_ofEmgData(EmgData_Number.getArrayMax_EmgData_tam08()) );
        Log.d(TAG, "----- MaxEmg16: " + EmgData_Number.getLine_ofEmgData(EmgData_Number.getArrayMax_EmgData_tam16()) );
        /* */
        // +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
        // +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
        /* *
        // Codigo para guardar el analisis de las muestras de datos EMG de 08 y 16 elementos
        int MAXSizeFile = 10;
        if( (GestureFileReader.arrayString_x08.size() < MAXSizeFile) && (GestureFileReader.arrayString_x16.size() < MAXSizeFile)) {
            GestureFileReader.arrayString_x08.add(
                    EmgData_Number.getLine_ofEmgData(
                            EmgData_Number.getPictureAverage_ofEmgNormalized_tam08(arrayEmgDataNumberAccumulated)
                    )
            );
            GestureFileReader.arrayString_x16.add(
                    EmgData_Number.getLine_ofEmgData(
                            EmgData_Number.getPictureAverage_ofEmgNormalized_tam16(arrayEmgDataNumberAccumulated)
                    )
            );
            Log.d(TAG, "--------------- Tamañox08: " + GestureFileReader.arrayString_x08.size() + ".");
            Log.d(TAG, "--------------- Tamañox16: " + GestureFileReader.arrayString_x16.size() + ".");
        }else{
            if(savedArrayFile){
                savedArrayFile = false;
                String arcLetter = "M_Extra_1";
                GestureFileReader.savedDates_ArrayData("---_x08_Letter_"+arcLetter+".csv", "\n",GestureFileReader.arrayString_x08);
                GestureFileReader.savedDates_ArrayData("---_x16_Letter_"+arcLetter+".csv", "\n",GestureFileReader.arrayString_x16);
                Log.d(TAG, "=============== Yaaaaaaaaaaaaaaaaaaa-"+arcLetter+"!!!!!");
            }else{
                Log.d(TAG, "=============== Arreglo acumulado guardado.");
            }
        }
        /* */
        // +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
        arrayEmgDataNumberAccumulated = new ArrayList<EmgData_Number>(); // Reiniciar el almacen de muestras
        sizeArrayAccumulated = 0;                             // Indicar que se tiene 0 muestras actualmente
        return returnString; // <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
    }
    // ============================================================== ==============================
    // ============================================================== ==============================
    // ============================================================== ==============================
    // ============================================================== ==============================
    // ============================================================== ==============================
    // ============================================================== ==============================
    // -------------------------------------------------------------- ------------------------------
    // Reiniciar datas -----------------------------------------------------------------------------
    /**
     * Reiniciar las variables de apoyo a la traduccion de señas, del Administrador de señas (GestureManager)
     */
    public void reloadDataHandGestureManager(){
        lastTranslation = "";    // Cadena vacia para la ultima traduccion
        messageAccumulated = ""; // Cadena vacia para el mensaje acumulado
        letterRNA_tam08 = "";    // Cadena vacia para el resultado de analisis de los datos EMG de 08 elementos
        letterRNA_tam16 = "";    // Cadena vacia para el resultado de analisis de los datos EMG de 16 elementos
        arrayEmgDataNumberAccumulated = new ArrayList<EmgData_Number>(); // Reiniciar el almacen de datos EMG (Muestras)
        sizeArrayAccumulated = 0;     // Indicar que se tiene 0 muestras actualmente
        busyArrayAccumulated = false; // Indicar que no se esta ocupando el almacen de muestras
    }
    // -------------------------------------------------------------- ------------------------------
    // Line Emg and Num Data -----------------------------------------------------------------------
    // Guardar los datos EMG en un almacen de muestras
    /**
     * Metodo para administrar los datos EMG entrantes, acumulandolos hasta juntarse el maximo de muestras
     * @param emgDataDetected Dato EMG
     * @return Indicador de que han sido o no juntadas una determinada cantidad de muestras
     */
    private boolean goHandGestureManager(EmgData_Number emgDataDetected){
        if(!busyArrayAccumulated){ // Pregunta si estan ocupando el almacen de muestras
            // ======================= ===========================
            GestureManager.padlock_arrayEmgDataAccumulated.lock(); // Cerrar candado de almacen de muestras
            if( arrayEmgDataNumberAccumulated.size() < MAXSizeArray ){  // Si no se ah alcanzado el maximo de muestras
                arrayEmgDataNumberAccumulated.add(emgDataDetected);     // Guardar datos EMG en el almacen de muestras
                sizeArrayAccumulated = arrayEmgDataNumberAccumulated.size(); // Actualizarl el numero de muestras
            }
            GestureManager.padlock_arrayEmgDataAccumulated.unlock(); // Liberar candado de almacen de muestras
            // ======================= ==========================
            if(arrayEmgDataNumberAccumulated.size() >= MAXSizeArray){ // Si se ha alcanzdo el maximo de muestras
                return true;
            }
        }
        return false;
    }
    // ---------------------------------------------------------------------------------------------
    // Metodos de control --------------------------------------------------------------------------
    /**
     * Metodo para indicar si esta ocupado o no el Administrador de señas (GestureManager)
     * @return Indicador de que el Administrador de Señas esta ocupado
     */
    public boolean isBusy(){ // Indicar que el Administrador de señas esta ocupado
        return busyGestureManager;
    }
    // ---------------------------------------------------------------------------------------------
    // Crear RNA -----------------------------------------------------------------------------------
    /**
     * Metodo para crear o inicializar las RNA
     * @param datRNA_Weight1_x08 Pesos de Capa 1 para RNA de datos EMG de 08 elementos
     * @param datRNA_Weight2_x08 Pesos de Capa 2 para RNA de datos EMG de 08 elementos
     * @param datRNA_Weight1_x16 Pesos de Capa 1 para RNA de datos EMG de 16 elementos
     * @param datRNA_Weight2_x16 Pesos de Capa 2 para RNA de datos EMG de 16 elementos
     * @param datRNA_Bias1_x08 Bias de Capa 1 para RNA de datos EMG de 08 elementos
     * @param datRNA_Bias2_x08 Bias de Capa 2 para RNA de datos EMG de 08 elementos
     * @param datRNA_Bias1_x16 Bias de Capa 1 para RNA de datos EMG de 16 elementos
     * @param datRNA_Bias2_x16 Bias de Capa 2 para RNA de datos EMG de 16 elementos
     * @return Indicacion de si fueron o no creadas las RNA de manera correcta
     */
    private boolean createRNA(ArrayList<Double[][]> datRNA_Weight1_x08, ArrayList<Double[][]> datRNA_Weight2_x08, ArrayList<Double[]> datRNA_Bias1_x08, ArrayList<Double[]> datRNA_Bias2_x08,
                           ArrayList<Double[][]> datRNA_Weight1_x16, ArrayList<Double[][]> datRNA_Weight2_x16, ArrayList<Double[]> datRNA_Bias1_x16, ArrayList<Double[]> datRNA_Bias2_x16){
        int sizeLimit = 6; // Cade arreglo debe tener este minimo de elementos
        if( (datRNA_Weight1_x08.size() > sizeLimit) && (datRNA_Weight2_x08.size() > sizeLimit) && (datRNA_Bias1_x08.size() > sizeLimit) && (datRNA_Bias2_x08.size() > sizeLimit) &&
            (datRNA_Weight1_x16.size() > sizeLimit) && (datRNA_Weight2_x16.size() > sizeLimit) && (datRNA_Bias1_x16.size() > sizeLimit) && (datRNA_Bias2_x16.size() > sizeLimit) ){
            // -----------------------------------------------------------------------------------------
            // -----------------------------------------------------------------------------------------
            // -----------------------------------------------------------------------------------------
            // -----------------------------------------------------------------------------------------
            // Funciones por capa
            String[] nomFunctions = {
                    "logsig", // tansig  // Capa 1
                    "logsig"  // purelin // Capa 2
            };
            // -----------------------------------------------------------------------------------------
            // Pesos y Bias para RNA de datos EMG de 08 elementos
            int indexDatArrayRNA = 0; // Numero de elemento en arraglo de Pesos
            ArrayList<Double[][]> weightsRNA = new ArrayList<Double[][]>();
            weightsRNA.add(datRNA_Weight1_x08.get(indexDatArrayRNA));
            weightsRNA.add(datRNA_Weight2_x08.get(indexDatArrayRNA));
            ArrayList<Double[]> biasRNA = new ArrayList<Double[]>();
            biasRNA.add(datRNA_Bias1_x08.get(indexDatArrayRNA));
            biasRNA.add(datRNA_Bias2_x08.get(indexDatArrayRNA));
            myoTamoRNA_Average_08_AllZones = new MyoTamoRNA(weightsRNA, biasRNA, nomFunctions);
            // -----------------------------------------------------------------------------------------
            indexDatArrayRNA = 1; // Numero de elemento en arraglo de Pesos
            weightsRNA = new ArrayList<Double[][]>();
            weightsRNA.add(datRNA_Weight1_x08.get(indexDatArrayRNA));
            weightsRNA.add(datRNA_Weight2_x08.get(indexDatArrayRNA));
            biasRNA = new ArrayList<Double[]>();
            biasRNA.add(datRNA_Bias1_x08.get(indexDatArrayRNA));
            biasRNA.add(datRNA_Bias2_x08.get(indexDatArrayRNA));
            myoTamoRNA_Average_08_Pack1 = new MyoTamoRNA(weightsRNA, biasRNA, nomFunctions);
            // -----------------------------------------------------------------------------------------
            indexDatArrayRNA = 2; // Numero de elemento en arraglo de Pesos
            weightsRNA = new ArrayList<Double[][]>();
            weightsRNA.add(datRNA_Weight1_x08.get(indexDatArrayRNA));
            weightsRNA.add(datRNA_Weight2_x08.get(indexDatArrayRNA));
            biasRNA = new ArrayList<Double[]>();
            biasRNA.add(datRNA_Bias1_x08.get(indexDatArrayRNA));
            biasRNA.add(datRNA_Bias2_x08.get(indexDatArrayRNA));
            myoTamoRNA_Average_08_Pack2 = new MyoTamoRNA(weightsRNA, biasRNA, nomFunctions);
            // -----------------------------------------------------------------------------------------
            indexDatArrayRNA = 3; // Numero de elemento en arraglo de Pesos
            weightsRNA = new ArrayList<Double[][]>();
            weightsRNA.add(datRNA_Weight1_x08.get(indexDatArrayRNA));
            weightsRNA.add(datRNA_Weight2_x08.get(indexDatArrayRNA));
            biasRNA = new ArrayList<Double[]>();
            biasRNA.add(datRNA_Bias1_x08.get(indexDatArrayRNA));
            biasRNA.add(datRNA_Bias2_x08.get(indexDatArrayRNA));
            myoTamoRNA_Average_08_Pack3 = new MyoTamoRNA(weightsRNA, biasRNA, nomFunctions);
            // -----------------------------------------------------------------------------------------
            indexDatArrayRNA = 4; // Numero de elemento en arraglo de Pesos
            weightsRNA = new ArrayList<Double[][]>();
            weightsRNA.add(datRNA_Weight1_x08.get(indexDatArrayRNA));
            weightsRNA.add(datRNA_Weight2_x08.get(indexDatArrayRNA));
            biasRNA = new ArrayList<Double[]>();
            biasRNA.add(datRNA_Bias1_x08.get(indexDatArrayRNA));
            biasRNA.add(datRNA_Bias2_x08.get(indexDatArrayRNA));
            myoTamoRNA_Average_08_Pack4 = new MyoTamoRNA(weightsRNA, biasRNA, nomFunctions);
            // -----------------------------------------------------------------------------------------
            indexDatArrayRNA = 5; // Numero de elemento en arraglo de Pesos
            weightsRNA = new ArrayList<Double[][]>();
            weightsRNA.add(datRNA_Weight1_x08.get(indexDatArrayRNA));
            weightsRNA.add(datRNA_Weight2_x08.get(indexDatArrayRNA));
            biasRNA = new ArrayList<Double[]>();
            biasRNA.add(datRNA_Bias1_x08.get(indexDatArrayRNA));
            biasRNA.add(datRNA_Bias2_x08.get(indexDatArrayRNA));
            myoTamoRNA_Average_08_Pack5 = new MyoTamoRNA(weightsRNA, biasRNA, nomFunctions);
            // -----------------------------------------------------------------------------------------
            indexDatArrayRNA = 6; // Numero de elemento en arraglo de Pesos
            weightsRNA = new ArrayList<Double[][]>();
            weightsRNA.add(datRNA_Weight1_x08.get(indexDatArrayRNA));
            weightsRNA.add(datRNA_Weight2_x08.get(indexDatArrayRNA));
            biasRNA = new ArrayList<Double[]>();
            biasRNA.add(datRNA_Bias1_x08.get(indexDatArrayRNA));
            biasRNA.add(datRNA_Bias2_x08.get(indexDatArrayRNA));
            myoTamoRNA_Average_08_Pack6 = new MyoTamoRNA(weightsRNA, biasRNA, nomFunctions);
            // -----------------------------------------------------------------------------------------
            // -----------------------------------------------------------------------------------------
            // -----------------------------------------------------------------------------------------
            // -----------------------------------------------------------------------------------------
            // Pesos y Bias para RNA de datos EMG de 16 elementos
            indexDatArrayRNA = 0; // Numero de elemento en arraglo de Pesos
            weightsRNA = new ArrayList<Double[][]>();
            weightsRNA.add(datRNA_Weight1_x16.get(indexDatArrayRNA));
            weightsRNA.add(datRNA_Weight2_x16.get(indexDatArrayRNA));
            biasRNA = new ArrayList<Double[]>();
            biasRNA.add(datRNA_Bias1_x16.get(indexDatArrayRNA));
            biasRNA.add(datRNA_Bias2_x16.get(indexDatArrayRNA));
            myoTamoRNA_Average_16_AllZones = new MyoTamoRNA(weightsRNA, biasRNA, nomFunctions);
            // -----------------------------------------------------------------------------------------
            indexDatArrayRNA = 1; // Numero de elemento en arraglo de Pesos
            weightsRNA = new ArrayList<Double[][]>();
            weightsRNA.add(datRNA_Weight1_x16.get(indexDatArrayRNA));
            weightsRNA.add(datRNA_Weight2_x16.get(indexDatArrayRNA));
            biasRNA = new ArrayList<Double[]>();
            biasRNA.add(datRNA_Bias1_x16.get(indexDatArrayRNA));
            biasRNA.add(datRNA_Bias2_x16.get(indexDatArrayRNA));
            myoTamoRNA_Average_16_Pack1 = new MyoTamoRNA(weightsRNA, biasRNA, nomFunctions);
            // -----------------------------------------------------------------------------------------
            indexDatArrayRNA = 2; // Numero de elemento en arraglo de Pesos
            weightsRNA = new ArrayList<Double[][]>();
            weightsRNA.add(datRNA_Weight1_x16.get(indexDatArrayRNA));
            weightsRNA.add(datRNA_Weight2_x16.get(indexDatArrayRNA));
            biasRNA = new ArrayList<Double[]>();
            biasRNA.add(datRNA_Bias1_x16.get(indexDatArrayRNA));
            biasRNA.add(datRNA_Bias2_x16.get(indexDatArrayRNA));
            myoTamoRNA_Average_16_Pack2 = new MyoTamoRNA(weightsRNA, biasRNA, nomFunctions);
            // -----------------------------------------------------------------------------------------
            indexDatArrayRNA = 3; // Numero de elemento en arraglo de Pesos
            weightsRNA = new ArrayList<Double[][]>();
            weightsRNA.add(datRNA_Weight1_x16.get(indexDatArrayRNA));
            weightsRNA.add(datRNA_Weight2_x16.get(indexDatArrayRNA));
            biasRNA = new ArrayList<Double[]>();
            biasRNA.add(datRNA_Bias1_x16.get(indexDatArrayRNA));
            biasRNA.add(datRNA_Bias2_x16.get(indexDatArrayRNA));
            myoTamoRNA_Average_16_Pack3 = new MyoTamoRNA(weightsRNA, biasRNA, nomFunctions);
            // -----------------------------------------------------------------------------------------
            indexDatArrayRNA = 4; // Numero de elemento en arraglo de Pesos
            weightsRNA = new ArrayList<Double[][]>();
            weightsRNA.add(datRNA_Weight1_x16.get(indexDatArrayRNA));
            weightsRNA.add(datRNA_Weight2_x16.get(indexDatArrayRNA));
            biasRNA = new ArrayList<Double[]>();
            biasRNA.add(datRNA_Bias1_x16.get(indexDatArrayRNA));
            biasRNA.add(datRNA_Bias2_x16.get(indexDatArrayRNA));
            myoTamoRNA_Average_16_Pack4 = new MyoTamoRNA(weightsRNA, biasRNA, nomFunctions);
            // -----------------------------------------------------------------------------------------
            indexDatArrayRNA = 5; // Numero de elemento en arraglo de Pesos
            weightsRNA = new ArrayList<Double[][]>();
            weightsRNA.add(datRNA_Weight1_x16.get(indexDatArrayRNA));
            weightsRNA.add(datRNA_Weight2_x16.get(indexDatArrayRNA));
            biasRNA = new ArrayList<Double[]>();
            biasRNA.add(datRNA_Bias1_x16.get(indexDatArrayRNA));
            biasRNA.add(datRNA_Bias2_x16.get(indexDatArrayRNA));
            myoTamoRNA_Average_16_Pack5 = new MyoTamoRNA(weightsRNA, biasRNA, nomFunctions);
            // -----------------------------------------------------------------------------------------
            indexDatArrayRNA = 6; // Numero de elemento en arraglo de Pesos
            weightsRNA = new ArrayList<Double[][]>();
            weightsRNA.add(datRNA_Weight1_x16.get(indexDatArrayRNA));
            weightsRNA.add(datRNA_Weight2_x16.get(indexDatArrayRNA));
            biasRNA = new ArrayList<Double[]>();
            biasRNA.add(datRNA_Bias1_x16.get(indexDatArrayRNA));
            biasRNA.add(datRNA_Bias2_x16.get(indexDatArrayRNA));
            myoTamoRNA_Average_16_Pack6 = new MyoTamoRNA(weightsRNA, biasRNA, nomFunctions);
            // -----------------------------------------------------------------------------------------
            // -----------------------------------------------------------------------------------------
            // -----------------------------------------------------------------------------------------
            // -----------------------------------------------------------------------------------------
            return true;
        }
        return false;
    }
    // ---------------------------------------------------------------------------------------------
    // Obtener una traduccion con base a un patron numerico de una seña detectada

    /**
     * Devuelve una traduccion si el patron numerico entrante corresponde a una seña
     * @param resultRNA Cadena de patrones numericos resultantes de una RNA
     * @return Traduccion de un patron numerico
     */
    private String evaluateRNAResult(String resultRNA){
        String returnString = "";
        switch (resultRNA){
            case "10000000000000000":
                returnString = "a";
                break;
            case "01000000000000000":
                returnString = "b";
                break;
            case "00100000000000000":
                returnString = "c";
                break;
            case "00010000000000000":
                returnString = "d";
                break;
            case "00001000000000000":
                returnString = "e";
                break;
            case "00000100000000000":
                returnString = "g";
                break;
            case "00000010000000000":
                returnString = "h";
                break;
            case "00000001000000000":
                returnString = "i";
                break;
            case "00000000100000000":
                returnString = "l";
                break;
            case "00000000010000000":
                returnString = "m";
                break;
            case "00000000001000000":
                returnString = "n";
                break;
            case "00000000000100000":
                returnString = "o";
                break;
            case "00000000000010000":
                returnString = "p";
                break;
            case "00000000000001000":
                returnString = "r";
                break;
            case "00000000000000100":
                returnString = "s";
                break;
            case "00000000000000010":
                returnString = "t";
                break;
            case "00000000000000001":
                returnString = "u";
                break;
        }
        return returnString;
    }
}