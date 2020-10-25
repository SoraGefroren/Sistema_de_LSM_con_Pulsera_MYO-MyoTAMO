package com.sora.prototype.myotamoprototype.detector;

// Librerias
import android.util.Log;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Diccionario de palabras para la prediccion de texto
 */
public class Words_Tamo{
    // Variables para Datos del Gesto/Señal (Palabras para el Diccionario y la Prediccion de texto)
    private ArrayList<String>[] arrayOfWords = new ArrayList[] {
            new ArrayList<String>(), // A // 01
            new ArrayList<String>(), // B // 02
            new ArrayList<String>(), // C // 03
            new ArrayList<String>(), // D // 04
            new ArrayList<String>(), // E // 05
            new ArrayList<String>(), // G // 06
            new ArrayList<String>(), // H // 07
            new ArrayList<String>(), // I // 08
            new ArrayList<String>(), // L // 09
            new ArrayList<String>(), // M // 10
            new ArrayList<String>(), // N // 11
            new ArrayList<String>(), // O // 12
            new ArrayList<String>(), // P // 13
            new ArrayList<String>(), // R // 14
            new ArrayList<String>(), // S // 15
            new ArrayList<String>(), // T // 16
            new ArrayList<String>()  // U // 17
    };
    // TAG para la Visualizacion de MSJ en Consola
    public final static String TAG = "MyoTAMO-Words";
    // ----------- ---------------------------------------------------------------------------------
    // Constructor ---------------------------------------------------------------------------------
    /**
     * Constructor vacio del diccionario
     */
    public Words_Tamo(){
    }
    // ---------------------------------------------- ----------------------------------------------
    // Agregar una nueva palabra -------------------- ----------------------------------------------
    /**
     * Agrega una nueva palabra a un diccionaro de palabra en base a su primer letra
     * @param word Palabra
     * @param position Posicion donde sera agregada la palabra
     */
    public void setNewWord_In(String word, int position){
        if(!arrayOfWords[position].contains(word)){
            arrayOfWords[position].add(word);
        }
    }
    // -------------------------------------------- ------------------------------------------------
    // Componer la frase con las palabras conocidas ------------------------------------------------
    /**
     * Este metodo analisa la cadena de caracteres, buscando la ultima palabra la cual analizara para intentar predecir completamente en base con un diccionario de palabras
     * @param message Mensaje o Palabra que sera analizada
     * @return Mensaje con la ultima palabra compuesta (es decir su prediccion)
     */
    public String fixedUltimateWord(String message){
        String returnPhrase = ""; // Resultado o frase
        if(message != null && message != ""){ // Comprobar el tipo de mensaje de entrada
            // Si solo se tiene 1 palabra, devolver el tratamiento de esta
            if( message.split(" ").length < 1 ){
                returnPhrase = fixedWord(message);
            }else{ // Si se tiene mas de 1 palabra, devolver el tratamiento de la ultima palabra
                String[] wordsInMessage = message.split(" "); // Separar el texto por espacios
                // Respaldar todas las palabras excepto la ultima
                for(int i = 0; i < wordsInMessage.length -1; i++){
                    returnPhrase += wordsInMessage[i] + " ";
                }
                // Concatenar al resultado el tratamiento de la ultima palabra
                returnPhrase += fixedWord(wordsInMessage[wordsInMessage.length - 1]);
            }
        }else{
            returnPhrase = message; // Devolver el mismo mensaje de entrada
        }
        return returnPhrase;
    }
    // Tratar una plabra en particular
    /**
     * Analisa una palabra en particular para intentar completarla con base al diccionario de palabras
     * @param wordString Palabra de entrada que sera analisada (esta secuencia de caracteres podria no ser alguna palabra en especifico)
     * @return La palabra de entrada completada en base al analisis con el diccionario de palabras
     */
    private int minMatch = 2;              // Minimo numero de caracteres para considerar al texto como palabra
    private int minMatchSize = minMatch*2; // Minimo numero de caracteres para comenzar a tratar una palabra
    public String fixedWord(String wordString){
        String newWord = wordString;                     // Resultado del tratamiento de la palabra, por defecto el mismo texto de entrada
        if(wordString.toCharArray().length >= minMatch){ // Verificar si se puede conciderar palabra
            boolean breakFixed = false; // Variable de control para romper comparacion con el diccionario en caso de encontrar coincidencia
            for(ArrayList<String> arrayWords :arrayOfWords){ // Recorrer los conjunto de palabras del diccionario
                // Comparar cada palabra del diccionario con la palabra de entrada
                for(String wordOfBD: arrayWords){ // Recorrer cada palabra de un conjunto de palabras del diccionario
                    Log.d(TAG, "__________________."+wordOfBD+".vs."+wordString+".__________________");
                    String myMiniumWord = "";
                    // Limpiar la Palabra del diccionario de impuresas como espacios
                    if(wordOfBD.toCharArray().length >= minMatchSize){
                        myMiniumWord = wordOfBD.substring(0, minMatchSize);
                    }else{
                        myMiniumWord = wordOfBD;
                    }
                    // Si la palabra del diccionario es diferente de nulo y cadena vacia
                    if(myMiniumWord != null && myMiniumWord != ""){
                        // Se obtiene un patron de caracteres de las palabras del diccionario (Con base en consonantes)
                        String patronConsonante = myMiniumWord.replaceAll("(?![aeiouAEIOU])[a-zA-Z]","[bcdefghijklmnopqrstuvwxyzBCDEFGHIJKLMNOPQRSTUVWXYZ]+");
                        // Se obtiene un patron de caracteres de las palabras del diccionario (Con base en vocales)
                        String patronVocal = myMiniumWord.replaceAll("(?![bcdefghijklmnopqrstuvwxyzBCDEFGHIJKLMNOPQRSTUVWXYZ])[a-zA-Z]", "[aeiouAEIOU]+");
                        Log.d(TAG, "Patron.Constante:_"+patronConsonante+"______");
                        Log.d(TAG, "Patron.Vocal:_____"+patronVocal+"______");
                        Matcher matCon = Pattern.compile(patronConsonante).matcher(wordString); // Comparar la palabra entrante contra el patron de la palabra del diccionario (con base en consonantes)
                        Matcher matVoc = Pattern.compile(patronVocal).matcher(wordString);      // Comparar la palabra entrante contra el patron de la palabra del diccionario (con base en vocales)
                        // Si algun patron coincide entonces se toma a la palabra del diccionario, como la palabra que se esta escribiendo
                        if (matCon.matches() || matVoc.matches()) {
                            newWord = wordOfBD + " "; // Establecer la nueva palabra
                            breakFixed = true;        // Indicar la salida del ciclo tras predecir la palabra
                            break;
                        }
                    }
                }
                if(breakFixed){
                    break;
                }
            }
        }
        return newWord;
    }
    // ---------------------------------------------- ----------------------------------------------
    // ---------------------------------------------- ----------------------------------------------
}