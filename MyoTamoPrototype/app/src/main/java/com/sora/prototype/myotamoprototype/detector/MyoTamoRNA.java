package com.sora.prototype.myotamoprototype.detector;

// Libreria
import android.util.Log;
import java.util.ArrayList;

/**
 * Implementacion del comportamiento de una RNA sin retroalimentacion
 */
public class MyoTamoRNA {
    // Variable de Pesos para esta RNA
    private ArrayList<Double[][]> weightsRNA = new ArrayList<Double[][]>();
    // Variable de Bias para esta RNA
    private ArrayList<Double[]> biasRNA = new ArrayList<Double[]>();;
    // Variable de Funciones para esta RNA
    private String[] nameFuctionsForLayer = {""};
    // Variable de status (Estado) de esta RNA
    private boolean statusRNA = false;
    // TAG para la Visualizacion de MSJ en Consola
    private static final String TAG = "MyoTAMO-RNA";
    // ---------------------------------------------- ----------------------------------------------
    // Contruccion  --------------------------------------------------------------------------------
    /**
     * Constructor de la implementacion de la RNA
     */
    public MyoTamoRNA(ArrayList<Double[][]> weights, ArrayList<Double[]> bias, String[] nomFuctionsForLayer){
        weightsRNA = weights; // Establecer los Pesos
        biasRNA = bias;       // Establecer las Bias
        nameFuctionsForLayer = nomFuctionsForLayer; // Establecer las funciones por capa
        if((weightsRNA.size() == nameFuctionsForLayer.length) && (weightsRNA.size() == biasRNA.size())){
            statusRNA = true; // Correcta contruccion de la RNA
        }
    }
    /**
     * Constructor vacio de la implemementacion de la RNA
     */
    public MyoTamoRNA(){ } // Incorrecta contruccion de la RNA
    // ---------------------------------------------- ----------------------------------------------
    // Calculo RNA ---------------------------------------------------------------------------------
    /**
     * En base a un patron de una muestra de datos EMG, devuelve un patron numerico de 0 y 1, el cual podria corresponder con una seña
     * @param averageEmgData Patron numerico de muestras de datos EMG
     * @param seeEval Indicacion para ver o no mensajes en consola
     * @return Patron numerico de 0 y 1 que podria coincidir con el de una seña
     */
    public String getStringRNAResult_ofEmgData(Double[] averageEmgData, boolean seeEval){
        String rnaResult = ""; // Resultado del analisis por la RNA
        if(statusRNA){ // Comprobar el estado de la RNA
            Double[] inputs = averageEmgData; // Entradas
            // Ciclo para recorrer cada capa de la RNA
            for(int numLayer = 0; numLayer < weightsRNA.size(); numLayer++){
                Double[][] weights = weightsRNA.get(numLayer); // Pesos de la presente capa
                Double[] bias = biasRNA.get(numLayer);         // Bias de la presente capa
                int numNeurons = weights.length; // Numero de Neuronas en las presente capa
                int numInputs = inputs.length;   // Numero de entradas para la presente capa
                Double[] resultOnNeurons = new Double[numNeurons]; // Resultado de la presente capa
                if(numNeurons == bias.length){
                    // Operacion por cada Neurona
                    for (int nNeuron = 0; nNeuron < numNeurons; nNeuron++){
                        resultOnNeurons[nNeuron] = 0.0; // Resultado por defecto de la Neurona
                        if(numInputs == weights[nNeuron].length){
                            // Operacion por cada Entrada
                            for(int nInput = 0; nInput < numInputs; nInput++){
                                // Resultado de la Neurona
                                resultOnNeurons[nNeuron] += (inputs[nInput] * weights[nNeuron][nInput]);
                            }
                            resultOnNeurons[nNeuron] += bias[nNeuron]; // Sumar Bias al resultado de la Neurona
                            // Aplicar funcion especifica al resultado de la Neurona
                            resultOnNeurons[nNeuron] = getResult_activationFunction(nameFuctionsForLayer[numLayer], resultOnNeurons[nNeuron]);
                        }else{
                            if(seeEval){
                                Log.d(TAG, "------ N°Inputs:[" + numInputs + "], N°wInpt: [" + weights[nNeuron].length + "]");
                            }
                        }
                    }
                }else{
                    if(seeEval){
                        Log.d(TAG, "------ N°Neurons:[" + numNeurons + "], N°Bias: [" + bias.length + "]");
                    }
                }
                // Actualizar la entrada con el ultimo resultado de las Neuronas
                inputs = resultOnNeurons;
            }
            // Convertir el resultado de la RNA a un patron numerico
            for(int i = 0; i < inputs.length; i++){
                if(inputs[i] > 0.9){
                    rnaResult += "1";
                }else{
                    rnaResult += "0";
                }
                if(seeEval){
                    Log.d(TAG, "------ Nr°:"+i+"...[" + inputs[i] + "]["+rnaResult+"]");
                }
            }
        }else{
            if(seeEval){
                Log.d(TAG, ">>>>>> myoTamoRNA Uninitialized <<<<<<");
            }
        }
        return rnaResult;
    }
    // Aplicar una funcion especifica a un valor especifico
    /**
     * Devuelve la aplicacion de una funcion especifica sobre un valor especifico
     * @param nomFunction Nombre de la funcion a ser aplicada
     * @param value Valor sobre el cual se aplicara una funcion
     * @return Resultado de la aplicacion una funcion sobre un valor
     */
    public Double getResult_activationFunction(String nomFunction, Double value){
        Double result; // Resultado de la funcion
        switch (nomFunction){
            case "logsig": // Aplicar funcion logaritmica sigmoide
                result = (1 / (1 + Math.exp(-value)));
                break;
            case "tansig": // Aplicar funcion de tangente sigmoide
                result = (2 / (1+Math.exp(-2 * value))) -1;
                break;
            case "purelin": // Funcion de tranferencia linear
                result = value;
                break;
            default:
                result = value;
        }
        return result;
    }
}
