package com.sora.prototype.myotamoprototype.detector;

// Librerias
import android.util.Log;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

/**
 * Clase contenedora de los mecanimos necesarios para iniciar la App
 */
public class GestureFileReader {
    // Variable para el directorio/dispositivo masivo de almacenamiento
    private final static File     BASE_DIR = new File("sdcard");
    // Variable del directorio donde se encuentran los datos para el funcionamiento de la APP
    private final static String   DirName = "MyoTamoPrototype";
    // Variable de status del Lector de archivos para señas
    private boolean status = false;
    // TAG para la Visualizacion de MSJ en Consola
    public final static String TAG = "KUMATO-FILE";
    // Variables para las palabras (diccionario de palabras) para el predictor de texto
    private Words_Tamo wordsTamo_ofFile;
    // Variables de Pesos y Bias para las RNA
        // --- Contenido para las RNA que trabajan con datos EMG de 08 elemento
    private ArrayList<Double[][]> datRNA_Weight1_x08;
    private ArrayList<Double[][]> datRNA_Weight2_x08;
    private ArrayList<Double[]> datRNA_Bias1_x08;
    private ArrayList<Double[]> datRNA_Bias2_x08;
        // --- Contenido para las RNA que trabajan con datos EMG de 16 elemento
    private ArrayList<Double[][]> datRNA_Weight1_x16;
    private ArrayList<Double[][]> datRNA_Weight2_x16;
    private ArrayList<Double[]> datRNA_Bias1_x16;
    private ArrayList<Double[]> datRNA_Bias2_x16;
    // -------------------------------------------------------------- ------------------------------
    // -------------------------------------------------------------- ------------------------------
    // -------------------------------------------------------------- ------------------------------
    // -------------------------------------------------------------- ------------------------------
    // Constructor -------------------------------------------------- ------------------------------
    public GestureFileReader(){
        status = true;
        loadData_WordsFiles(new String[]{ // Nombre de archivos de palabras
                "Dictionary_A_Words.csv", // 01
                "Dictionary_B_Words.csv", // 02
                "Dictionary_C_Words.csv", // 03
                "Dictionary_D_Words.csv", // 04
                "Dictionary_E_Words.csv", // 05
                "Dictionary_G_Words.csv", // 06
                "Dictionary_H_Words.csv", // 07
                "Dictionary_I_Words.csv", // 08
                "Dictionary_L_Words.csv", // 09
                "Dictionary_M_Words.csv", // 10
                "Dictionary_N_Words.csv", // 11
                "Dictionary_O_Words.csv", // 12
                "Dictionary_P_Words.csv", // 13
                "Dictionary_R_Words.csv", // 14
                "Dictionary_S_Words.csv", // 15
                "Dictionary_T_Words.csv", // 16
                "Dictionary_U_Words.csv", // 17
        });
        loadData_RNAFiles(
                    new String[] { // Nombre de archivos para RNA de datos EMG de 08 elementos
                        "x08_PesosAllZones.rnadat",
                        "x08_PesosPack1.rnadat",
                        "x08_PesosPack2.rnadat",
                        "x08_PesosPack3.rnadat",
                        "x08_PesosPack4.rnadat",
                        "x08_PesosPack5.rnadat",
                        "x08_PesosPack6.rnadat"
                },  new String[]{ // Nombre de archivos para RNA de datos EMG de 16 elementos
                        "x16_PesosAllZones.rnadat",
                        "x16_PesosPack1.rnadat",
                        "x16_PesosPack2.rnadat",
                        "x16_PesosPack3.rnadat",
                        "x16_PesosPack4.rnadat",
                        "x16_PesosPack5.rnadat",
                        "x16_PesosPack6.rnadat"
                }
        );
    }
    // -------------------------------------------------------------- ------------------------------
    // Cargar el archivo de palabras para el predictor de texto --------------------------------- --
    /**
     * Metodo para cargar los datos necesarios para el diccionario de palabras que utiliza el predictor de texto
     * @param nomsFile Nombre de los archivos que contiene las palabras
     */
    private void loadData_WordsFiles(String[] nomsFile){
        //--------------------------------------------
        // Verificar existencia del directorio Kumato
        File directory = new File(BASE_DIR, DirName);
        if(!directory.exists()){
            directory.mkdir();
        }
        wordsTamo_ofFile = new Words_Tamo();
        //------------------------------------------------------------------------------------------
        for (int iFile = 0; iFile < nomsFile.length; iFile++) {
            // Abrir y verificar existencia del archivo .dat
            File targetFile = new File(directory, nomsFile[iFile]);
            if(!targetFile.exists()){
                try{
                    targetFile.createNewFile();
                }catch (Exception e){
                    status = false;
                }
            }else{
                // Leer archivo
                FileReader fr = null;
                try{
                    // Archivo
                    fr = new FileReader(targetFile);
                    BufferedReader br = new BufferedReader(fr);
                    // Para leer lineas del archivo
                    String line;
                    // Leer archivo linea por linea
                    while((line = br.readLine()) != null){
                        line = line.trim(); // Leer linea
                        if(line != null && line != "" && line != "\n"){
                            switch (line.split(",").length){
                                case 0:
                                    wordsTamo_ofFile.setNewWord_In(line,iFile);
                                    break;
                                case 1:
                                    String nLin = line.split(",")[0];
                                    if(nLin != null && nLin != "" && nLin != "\n"){
                                        wordsTamo_ofFile.setNewWord_In(nLin,iFile);
                                    }
                                    break;
                                default:
                                    String[] nLins = line.split(",");
                                    for (String lin: nLins) {
                                        if(lin != null && lin != "" && lin != "\n"){
                                            wordsTamo_ofFile.setNewWord_In(lin,iFile);
                                        }
                                    }
                                    break;
                            }
                        }
                    }
                    br.close();
                }catch(Exception e){
                    if(fr != null){
                        try {
                            fr.close();
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }
                    }
                    status = false;
                }finally {
                    if(fr != null){
                        try {
                            fr.close();
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }
                    }
                }
            }
        }
        //------------------------------------------------------------------------------------------
    }
    // -------------------------------------------------------------- ------------------------------
    // Cargar el archivo de pesos y bias para las RNA --------------------------------- ------------
    /**
     * Metodo para cargar los Pesos y Bias necesarios para construir las RNA
     * @param nomsFile_x08 Nombre de los archivos con Pesos y Bias, para datos EMG de 08 elementos
     * @param nomsFile_x16 Nombre de los archivos con Pesos y Bias, para datos EMG de 16 elementos
     */
    private void loadData_RNAFiles(String[] nomsFile_x08, String[] nomsFile_x16){
        //--------------------------------------------
        // Verificar existencia del directorio Kumato
        File directory = new File(BASE_DIR, DirName);
        if(!directory.exists()){
            directory.mkdir();
        }
        //------------------------------------------------------------------------------------------
        datRNA_Weight1_x08 = new ArrayList<Double[][]>();
        datRNA_Weight2_x08 = new ArrayList<Double[][]>();
        datRNA_Bias1_x08 = new ArrayList<Double[]>();
        datRNA_Bias2_x08 = new ArrayList<Double[]>();
        //------------------------------------------------------------------------------------------
        for (String nomFile: nomsFile_x08) {
            // Abrir y verificar existencia del archivo
            File targetFile = new File(directory, nomFile);
            if(!targetFile.exists()){
                try{
                    targetFile.createNewFile();
                }catch (Exception e){
                    status = false;
                }
            }else{
                // Leer archivo
                FileReader fr = null;
                try{
                    // Archivo
                    fr = new FileReader(targetFile);
                    BufferedReader br = new BufferedReader(fr);
                    // Para leer lineas del archivo
                    String line;
                    int numLin = 0;
                    // Leer archivo linea por linea
                    while((line = br.readLine()) != null){
                        line = line.trim();
                        switch (numLin){
                            case 0:
                                datRNA_Weight1_x08.add(lineToMatrizDouble(line));
                                break;
                            case 1:
                                datRNA_Weight2_x08.add(lineToMatrizDouble(line));
                                break;
                            case 2:
                                datRNA_Bias1_x08.add(lineToArrayDouble(line));
                                break;
                            case 3:
                                datRNA_Bias2_x08.add(lineToArrayDouble(line));
                                break;
                        }
                        numLin++;
                    }
                    br.close();
                }catch(Exception e){
                    if(fr != null){
                        try {
                            fr.close();
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }
                    }
                    status = false;
                }finally {
                    if(fr != null){
                        try {
                            fr.close();
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }
                    }
                }
            }
        }
        //------------------------------------------------------------------------------------------
        datRNA_Weight1_x16 = new ArrayList<Double[][]>();
        datRNA_Weight2_x16 = new ArrayList<Double[][]>();
        datRNA_Bias1_x16 = new ArrayList<Double[]>();
        datRNA_Bias2_x16 = new ArrayList<Double[]>();
        //------------------------------------------------------------------------------------------
        for (String nomFile: nomsFile_x16) {
            // Abrir y verificar existencia del archivo
            File targetFile = new File(directory, nomFile);
            if(!targetFile.exists()){
                try{
                    targetFile.createNewFile();
                }catch (Exception e){
                    status = false;
                }
            }else{
                // Leer archivo
                FileReader fr = null;
                try{
                    // Archivo
                    fr = new FileReader(targetFile);
                    BufferedReader br = new BufferedReader(fr);
                    // Leer lineas del archivo
                    String line;
                    int numLin = 0;
                    while((line = br.readLine()) != null){
                        line = line.trim();
                        switch (numLin){
                            case 0:
                                datRNA_Weight1_x16.add(lineToMatrizDouble(line));
                                break;
                            case 1:
                                datRNA_Weight2_x16.add(lineToMatrizDouble(line));
                                break;
                            case 2:
                                datRNA_Bias1_x16.add(lineToArrayDouble(line));
                                break;
                            case 3:
                                datRNA_Bias2_x16.add(lineToArrayDouble(line));
                                break;
                        }
                        numLin++;
                    }
                    br.close();
                }catch(Exception e){
                    if(fr != null){
                        try {
                            fr.close();
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }
                    }
                    status = false;
                }finally {
                    if(fr != null){
                        try {
                            fr.close();
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }
                    }
                }
            }
        }
        //------------------------------------------------------------------------------------------
    }
    // -------------------------------------------------------------- ------------------------------
    // Line to MatrizDouble Methods ----------------------------------------------------------------
    /* Convierte una linea del archivo de Pesos a una matriz */
    /**
     * Metodo para convierte una linea (cadena de caracteres) a una matriz
     * @param line Cadena de caracteres que representan a una matriz
     * @return Matriz de numeros
     */
    private Double[][] lineToMatrizDouble(String line){
        int numFilas = 0;
        int numColums = 0;
        String[] segmString = line.split("\\|");
        ArrayList<String[]> valuesArrString = new ArrayList<String[]>();
        for (String segmento: segmString) {
            String[] valuesString = segmento.split(",");
            valuesArrString.add(valuesString);
        }
        if(valuesArrString.size() > 0){
            numFilas = segmString.length;
            numColums = valuesArrString.get(0).length;
            Double[][] returnMatriz = new Double[numFilas][numColums];
            for (int iv = 0; iv < numFilas; iv++){
                String[] valsString = valuesArrString.get(iv);
                for (int jv = 0; jv < numColums; jv++){
                    try{
                        returnMatriz[iv][jv] = Double.parseDouble(valsString[jv]);
                    }catch (Exception e){
                        returnMatriz[iv][jv] = 0.0;
                        Log.d(TAG, "xxxxxxxxxxxxxx Error en el ParserDouble de la MatrizDouble");
                    }
                }
            }
            return returnMatriz;
        }
        return null;
    }
    // Line to ArrayDouble Methods -----------------------------------------------------------------
    /* Convierte una linea del archivo de Bias a un arreglo */
    /**
     * Metodo para convierte una linea (cadena de caracteres) a un arreglo
     * @param line Cadena de caracteres que representan a un arreglo
     * @return Arreglo de numeros
     */
    private Double[] lineToArrayDouble(String line){
        String[] valuesString = line.split(",");
        Double[] returnArray = new Double[valuesString.length];
        for (int iv = 0; iv < valuesString.length; iv++){
            try{
                returnArray[iv] = Double.parseDouble(valuesString[iv]);
            }catch (Exception e){
                returnArray[iv] = 0.0;
                Log.d(TAG, "xxxxxxxxxxxxxx Error en el ParserDouble del ArrayDouble");
            }
        }
        return returnArray;
    }
    // -------------------------------------------------------------- ------------------------------
    // GetMethods ----------------------------------------------------------------------------------
        // --- Devolver Diccionario de palabras Words
    /**
     * Metodo para devolver el diccionario de palabras
     * @return Diccionario de palabras
     */
    public Words_Tamo getWords_Tamo(){
        return wordsTamo_ofFile;
    }
        // --- Devolver Pesos para RNA de datos EMG de 08 elementos
    /**
     * Metodo para devolver los Pesos para RNA de datos EMG de 08 elementos
     * @return Arreglo de pesos para RNA
     */
    public ArrayList<Double[][]> getDatRNA_Weight1_x08(){
        return datRNA_Weight1_x08;
    }
    /**
     * Metodo para devolver los Pesos para RNA de datos EMG de 08 elementos
     * @return Arreglo de pesos para RNA
     */
    public ArrayList<Double[][]> getDatRNA_Weight2_x08(){
        return datRNA_Weight2_x08;
    }
        // --- Devolver Bias para RNA de datos EMG de 08 elementos
    /**
     * Metodo para devolver los Bias para RNA de datos EMG de 08 elementos
     * @return Arreglo de bias para RNA
     */
    public ArrayList<Double[]> getDatRNA_Bias1_x08(){
        return datRNA_Bias1_x08;
    }
    /**
     * Metodo para devolver los Bias para RNA de datos EMG de 08 elementos
     * @return Arreglo de bias para RNA
     */
    public ArrayList<Double[]> getDatRNA_Bias2_x08(){
        return datRNA_Bias2_x08;
    }
        // --- Devolver Pesos para RNA de datos EMG de 16 elementos
    /**
     * Metodo para devolver los Pesos para RNA de datos EMG de 16 elementos
     * @return Arreglo de pesos para RNA
     */
    public ArrayList<Double[][]> getDatRNA_Weight1_x16(){
        return datRNA_Weight1_x16;
    }
    /**
     * Metodo para devolver los Pesos para RNA de datos EMG de 16 elementos
     * @return Arreglo de pesos para RNA
     */
    public ArrayList<Double[][]> getDatRNA_Weight2_x16(){
        return datRNA_Weight2_x16;
    }
        // --- Devolver Pesos para RNA de datos EMG de 16 elementos
    /**
     * Metodo para devolver los Bias para RNA de datos EMG de 16 elementos
     * @return Arreglo de bias para RNA
     */
    public ArrayList<Double[]> getDatRNA_Bias1_x16(){
        return datRNA_Bias1_x16;
    }
    /**
     * Metodo para devolver los Bias para RNA de datos EMG de 16 elementos
     * @return Arreglo de bias para RNA
     */
    public ArrayList<Double[]> getDatRNA_Bias2_x16(){
        return datRNA_Bias2_x16;
    }
        // --- Status
    /**
     * Metodo para devolver el estado de la lectura de los elementos necesarios para la App
     * @return Estado de la RNA
     */
    public boolean getStatus(){
        return status;
    }
    // -------------------------------------------------------------- ------------------------------
    // -------------------------------------------------------------- ------------------------------
    // -------------------------------------------------------------- ------------------------------
    // -------------------------------------------------------------- ------------------------------
    // -------------------------------------------------------------- ------------------------------
    // -------------------------------------------------------------- ------------------------------
    // -------------------------------------------------------------- ------------------------------
    // -------------------------------------------------------------- ------------------------------
    // -------------------------------------------------------------- ------------------------------
    // -------------------------------------------------------------- ------------------------------
    // -------------------------------------------------------------- ------------------------------
    // -------------------------------------------------------------- ------------------------------
    public static ArrayList<String> arrayString_x08 = new ArrayList<String>(); // Almacen de lineas de datos EMG de 08 elementos
    public static ArrayList<String> arrayString_x16 = new ArrayList<String>(); // Almacen de lineas de datos EMG de 16 elementos
    // Para guardar un Archivo dado el nomre del archivo, algun fin de linea como "\n", y el arreglo de cadenas ha almacenar
    /**
     * Metodo guardar un archivo
     * @param nameFileAcAux Nombre del archivo
     * @param finLine Fin de linea
     * @param arrayString Arreglo de Lineas de caracteres ha guardar en el archivo
     */
    public static void savedDates_ArrayData(String nameFileAcAux, String finLine, ArrayList<String> arrayString){
        File targetFile = new File(new File(BASE_DIR, DirName), nameFileAcAux);
        targetFile.getParentFile().mkdirs();
        if(!targetFile.exists()) {
            try {
                targetFile.createNewFile();
            } catch (Exception e) {}
        }
        PrintWriter writer = null;
        try {
            writer = new PrintWriter(targetFile);
            String textToFile = "";
            for(String myLine: arrayString){
                textToFile += myLine + finLine;
            }
            writer.print(textToFile);
            Log.d(TAG, "--------------- Archivo guardado ---------");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(writer != null){
                writer.close();
            }
        }
    }
    // ---------------------------------------------------------------------------------------------
}
