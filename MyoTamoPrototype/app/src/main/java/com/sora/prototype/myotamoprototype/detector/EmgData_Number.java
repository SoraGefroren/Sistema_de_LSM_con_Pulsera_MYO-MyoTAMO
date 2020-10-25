package com.sora.prototype.myotamoprototype.detector;

// Librerias
import java.util.ArrayList;
// Libreria de terceros
import naoki.ble_myo.ByteReader;

/**
 * Clase que representa los datos EMG en la App
 */
public class EmgData_Number {
    // Variabless ----------------------------------------------------------------------------------
        // Almacen para los datos EMG de 16 elementos
    private int[] emgData_tam16 = { 0, 0, 0, 0, 0, 0, 0, 0,
                                    0, 0, 0, 0, 0, 0, 0, 0 };
        // Almacen para los datos EMG de 08 elementos
    private int[] emgData_tam08 = {0, 0, 0, 0, 0, 0, 0, 0};
    // Valores Maximos ------------------------------------------------------------------------------
        // Maximos para los datos EMG de 16 elementos
    private static int[] emgData_Max_tam16 = { 128,128,128,128,128,128,99,105,128,128,128,128,128,128,127,76 };
        // Maximos para los datos EMG de 08 elementos
    private static int[] emgData_Max_tam08 = { 128,128,128,128,128,128,127,105 };
    // TAG para la Visualizacion de MSJ en Consola
    private final static String TAG = "MyoTAMO-EmgDat_Num";
    // ----------- ---------------------------------------------------------------------------------
    // ----------- ---------------------------------------------------------------------------------
    // ----------- ---------------------------------------------------------------------------------
    // ----------- ---------------------------------------------------------------------------------
    // ----------- ---------------------------------------------------------------------------------
    // ----------- ---------------------------------------------------------------------------------
    // ----------- ---------------------------------------------------------------------------------
    // Constructor v1 ------------------------------------------------------------------------------
    /**
     * Constructor para la EmgData
     * @param byteData Bytes recibidos de la Pulsera MYO
     */
    public EmgData_Number (byte[] byteData){
        // Implementacion de terceros para manejar los Bytes de los cuales obtengo los datos EMG
        ByteReader emgData = new ByteReader();
        emgData.setByteData(byteData);
        // Formar los datos EMG de 16 elementos
        for (int i_emg16 = 0; i_emg16 < 16; i_emg16++) {
            emgData_tam16[i_emg16] = emgData.getByte();
        }
        // Formar los datos EMG de 08 elementos
        for (int i_emg8 = 0; i_emg8 < 8; i_emg8++) {
            if (Math.abs(emgData_tam16[i_emg8]) < Math.abs(emgData_tam16[i_emg8 + 8])){
                emgData_tam08[i_emg8] = Math.abs(emgData_tam16[i_emg8 + 8]);
            } else {
                emgData_tam08[i_emg8] = Math.abs(emgData_tam16[i_emg8]);
            }
        }
    }
    // ----------- ---------------------------------------------------------------------------------
    // Metodos Get ---------------------------------------------------------------------------------
    /**
     * Devuelve los datos EMG de 08 elementos
     * @return Datos EMG de 08 elementos
     */
    public int[] getArrayEmgData_tam08(){
        return emgData_tam08; // Devolver los datos EMG de 08 elementos
    }
    /**
     * Devuelve los datos EMG de 16 elementos
     * @return Datos EMG de 16 elementos
     */
    public int[] getArrayEmgData_tam16(){
        return emgData_tam16; // Devolver los datos EMG de 16 elementos
    }
    // Metodos Get and Set of EmgData Max ----------------------------------------------------------
    /**
     * Devuelve los maximos datos EMG de 08 elementos
     * @return Maximos datos EMG de 08 elementos
     */
    public static int[] getArrayMax_EmgData_tam08(){
        return emgData_Max_tam08; // Devolver los maximos datos EMG de 08 elementos
    }
    /**
     * Devuelve los maximos datos EMG de 16 elementos
     * @return Maximos datos EMG de 16 elementos
     */
    public static int[] getArrayMax_EmgData_tam16(){
        return emgData_Max_tam16; // Devolver los maximos datos EMG de 16 elementos
    }
    // Para actualizar los Maximos de los datos EMG de 08 elementos
    /**
     * Metodo para actualizar los valores maximos de los datos EMG de 08 elementos
     * @param num Dato EMG
     * @param pos Posicion dentro del arreglo de maximos datos EMG de 08 elementos
     */
    public static void setNum_inArrayMax_EmgData_tam08(int num, int pos){
        if(pos < 8){
            if(emgData_Max_tam08[pos] < num){
                emgData_Max_tam08[pos] = num;
            }
        }
    }
    // Para actualizar los Maximos de los datos EMG de 16 elementos
    /**
     * Metodo para actualizar los valores maximos de los datos EMG de 16 elementos
     * @param num Dato EMG
     * @param pos Posicion dentro del arreglo de maximos datos EMG de 16 elementos
     */
    public static void setNum_inArrayMax_EmgData_tam16(int num, int pos){
        if(pos < 16){
            if(emgData_Max_tam16[pos] < num){
                emgData_Max_tam16[pos] = num;
            }
        }
    }
    // ----------- ---------------------------------------------------------------------------------
    // Obtener una linea de la EMG Data de 08 o 16 elementos (Cadena de caracteres) ----------------
    /**
     * Metodo para convertir un arreglo de datos EMG enterros a una cadena de texto separada por comas
     * @param myoData Arreglo de datos EMG
     * @return Cadena con los datos EMG separados por coma
     */
    public static String getLine_ofEmgData(int[] myoData){
        String returnString = "";
        for (int i = 0; i < myoData.length ; i++){
            if(i < (myoData.length-1)){
                returnString += myoData[i]+",";
            }else{
                returnString += myoData[i];
            }
        }
        return returnString;
    }
    /**
     * Metodo para convertir un arreglo de datos EMG dobles a una cadena de texto separada por comas
     * @param myoData Arreglo de datos EMG
     * @return Cadena con los datos EMG separados por coma
     */
    public static String getLine_ofEmgData(Double[] myoData){
        String returnString = "";
        for (int i = 0; i < myoData.length ; i++){
            if(i < (myoData.length-1)){
                returnString += myoData[i]+",";
            }else{
                returnString += myoData[i];
            }
        }
        return returnString;
    }
    // ----------- ---------------------------------------------------------------------------------
    // ----------- ---------------------------------------------------------------------------------
    // ----------- ---------------------------------------------------------------------------------
    // ----------- ---------------------------------------------------------------------------------
    // ----------- ---------------------------------------------------------------------------------
    // ----------- ---------------------------------------------------------------------------------
    // ----------- ---------------------------------------------------------------------------------
    // ----------- ---------------------------------------------------------------------------------
    // EmgData Normalizada Int16 de -10 a 10 --------------------------------------------------------
    /* Se Normalizan los datos EMG de 16 elementos y ponen en un rango de -10 a 10 para su visualizacion */
    /**
     * Metodo para normalizar los datos EMG de 16 elementos y ponerlos en un rango de -10 a 10
     * @return Arreglo de 16 elementos de datos EMG normalizados y puestos en un rango de -10 a 10
     */
    public int[] getNormalize_fromZeroToHundred_Int_EmgData_tam16(){
        int[] return_emgData_tam16 = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
        for(int i = 0; i < 16; i++){
            if(emgData_tam16[i] != 0 && emgData_Max_tam16[i] != 0){
                Double valueRen =  ((double) emgData_tam16[i] / emgData_Max_tam16[i]) * 10;
                return_emgData_tam16[i] = valueRen.intValue();
            }else{
                return_emgData_tam16[i] = 0;
            }
        }
        return return_emgData_tam16;
    }
    // EmgData Normalizada Ints08 de 0 a 10 -------------------------------------------------------
    /* Se Normalizan los datos EMG de 08 elementos y ponen en un rango de 0 a 10 para su visualizacion */
    /**
     * Metodo para normalizar los datos EMG de 08 elementos y ponerlos en un rango de 0 a 10
     * @return Arreglo de 08 elementos de datos EMG normalizados y puestos en un rango de 0 a 10
     */
    public int[] getNormalize_fromZeroToHundred_Int_EmgData_tam08(){
        int[] return_emgData_tam08 = {0, 0, 0, 0, 0, 0, 0, 0};
        for(int i = 0; i < 8; i++){
            if(emgData_tam08[i] != 0 && emgData_Max_tam08[i] != 0){
                Double valueRen =  ((double) emgData_tam08[i] / emgData_Max_tam08[i]) * 10;
                return_emgData_tam08[i] = valueRen.intValue();
            }else{
                return_emgData_tam08[i] = 0;
            }
        }
        return return_emgData_tam08;
    }
    // ----------- ---------------------------------------------------------------------------------
    // ----------- ---------------------------------------------------------------------------------
    // ----------- ---------------------------------------------------------------------------------
    // ----------- ---------------------------------------------------------------------------------
    // ----------- ---------------------------------------------------------------------------------
    // ----------- ---------------------------------------------------------------------------------
    // ----------- ---------------------------------------------------------------------------------
    // ----------- ---------------------------------------------------------------------------------
    // ---------------------------------------------------------------------------------------------
    // EmgData Normalizada Ints08 de 0 a 1 ---------------------------------------------------------
    /* Se Normalizan los datos EMG de 08 elementos sin algun otro tratamiento */
    /**
     * Metodo para normalizar los datos EMG de 08 elementos
     * @return Arreglo de 08 elementos de datos EMG normalizados
     */
    public Double[] getNormalize_fromZeroToOne_EmgData_tam08(){
        Double[] return_emgData_tam08 = {0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0};
        for(int i = 0; i < 8; i++){
            if(emgData_tam08[i] != 0 && emgData_Max_tam08[i] != 0){
                return_emgData_tam08[i] = (double) emgData_tam08[i]/emgData_Max_tam08[i];
            }else{
                return_emgData_tam08[i] = 0.0;
            }
        }
        return return_emgData_tam08;
    }
    // EmgData Normalizada Ints16 de 0 a 1 ---------------------------------------------------------
    /* Se Normalizan los datos EMG de 16 elementos sin algun otro tratamiento */
    /**
     * Metodo para normalizar los datos EMG de 16 elementos
     * @return Arreglo de 16 elementos de datos EMG normalizados
     */
    public Double[] getNormalize_fromZeroToOne_EmgData_tam16(){
        Double[] return_emgData_tam16 = {0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0,
                                         0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0};
        for(int i = 0; i < 16; i++){
            if(emgData_tam16[i] != 0 && emgData_Max_tam16[i] != 0){
                return_emgData_tam16[i] = (double) emgData_tam16[i]/emgData_Max_tam16[i];
            }else{
                return_emgData_tam16[i] = 0.0;
            }
        }
        return return_emgData_tam16;
    }
    // ---------------------------------------------------------------------------------------------
    // ---------------------------------------------------------------------------------------------
    // ---------------------------------------------------------------------------------------------
    // ---------------------------------------------------------------------------------------------
    // EmgData Normalized obtener dibujo de Promedios 08 -------------------------------------------
    /**
     * Metodo para obtener el promedio de un conjunto de datos EMG de 08 elementos (un paquete), utilizando los datos normalizados sin algun otro tratamiento
     * @param arrayAccumulated Paquete o conjunto de datos EMG (Muestras)
     * @return Arreglo de 08 elementos de datos EMG
     */
    public static Double[] getPictureAverage_ofEmgNormalized_tam08(ArrayList<EmgData_Number> arrayAccumulated){
        Double[] return_pictureInts08 = {0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0};
        for(EmgData_Number emgDataNum: arrayAccumulated){
            Double[] myoEmgData = emgDataNum.getNormalize_fromZeroToOne_EmgData_tam08();
            for(int i = 0; i < 8; i++){
                return_pictureInts08[i] += myoEmgData[i];
            }
        }
        for(int i = 0; i < 8; i++){
            return_pictureInts08[i] /= arrayAccumulated.size();
        }
        return return_pictureInts08;
    }
    // EmgData Normalized obtener dibujo de Promedios 16 -------------------------------------------
    /**
     * Metodo para obtener el promedio de un conjunto de datos EMG de 16 elementos (un paquete), utilizando los datos normalizados sin algun otro tratamiento
     * @param arrayAccumulated Paquete o conjunto de datos EMG (Muestras)
     * @return Arreglo de 16 elementos de datos EMG
     */
    public static Double[] getPictureAverage_ofEmgNormalized_tam16(ArrayList<EmgData_Number> arrayAccumulated){
        Double[] return_pictureInts16 = {0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0,
                                         0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0};
        for(EmgData_Number emgDataNum: arrayAccumulated){
            Double[] myoEmgData = emgDataNum.getNormalize_fromZeroToOne_EmgData_tam16();
            for(int i = 0; i < 16; i++){
                return_pictureInts16[i] += Math.abs(myoEmgData[i]);
            }
        }
        for(int i = 0; i < 16; i++){
            return_pictureInts16[i] /= arrayAccumulated.size();
        }
        return return_pictureInts16;
    }
    // ---------------------------------------------------------------------------------------------
    // ---------------------------------------------------------------------------------------------
}
