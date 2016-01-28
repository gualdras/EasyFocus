package university.ssii.easyfocus;

/**
 * Created by Leonardo on 13/12/2015.
 */

import java.lang.reflect.Array;
import java.util.ArrayList;

public class DynamicTimeWarper {

    private ArrayList<ArrayList<Float>> matrix1;
    private ArrayList<ArrayList<Float>> matrix2;
    private ArrayList<Float> lastValues;

    int matrix1Distance;
    int matrix2Distance;
    static int NUMBER_OF_LAST_SAMPLES = 3;
    static int MAX_PER_SAMPLE = 3;
    int K;
    int[][] warpingPath;
    float warpingDistance;
    long initTime, lastTime;
    boolean timerLock;


    public DynamicTimeWarper(ArrayList<ArrayList<Float>> matrix1){
        this.matrix1 = matrix1;
        this.matrix2 = new ArrayList<ArrayList<Float>>();
        matrix1Distance = matrix1.size();
        matrix2Distance = matrix2.size();
        K = 1;
        initTime = System.currentTimeMillis();
        lastTime = initTime;
        timerLock = false;


    }

    //Itera en 1 nuevo delta
    public boolean iterateMovement(ArrayList<Float> newValues, float umbral) {
        if (lastTime - initTime > 500) {
            if (lastValues == null) {
                lastValues = newValues;
                return false;
            } else {

                ArrayList<Float> deltaValues = newValues;
                ArrayList<Float> realdDeltaValues = substractArrayList(newValues,lastValues);
                lastValues = newValues;


         //       System.out.println("X:" + deltaValues.get(0) + " Y:" + deltaValues.get(1) + " Z:" + deltaValues.get(2));
           //     System.out.println( deltaValues.get(0) + ", " + deltaValues.get(1) + ", " + deltaValues.get(2) +", ");

                matrix2.add(newValues);
                matrix2Distance = matrix2.size();
                if (restartSequence(realdDeltaValues)) {
                    this.matrix2.clear();
                    //System.out.println("Cleared");
                    return false;
                } else {
                    double value = resolve();
                      System.out.println("Is :" + value + "<" + umbral);
                    boolean recognized = value < umbral;
                    if (recognized) {
                        this.matrix2.clear();
                        System.out.println("RECOGNIZED PATRON");
                        initTime = System.currentTimeMillis();
                    }
                    return recognized;
                }
            }
        } else {
            lastTime = System.currentTimeMillis();
            return false;
        }
    }

    private ArrayList<Float> substractArrayList(ArrayList<Float> m1, ArrayList<Float> m2){
        ArrayList<Float> result = new ArrayList<Float>();
        for (int i = 0; i < m1.size() ; i++){
            result.add(Math.abs(m1.get(i) - m2.get(i)));
        }
        return result;
    }

    //Detecta si debe empezar a esperar nuevos valores
//    public boolean restartSequence(){
//
//        float total = 0;
//
//        for (int i = matrix2.size()-1; matrix2.size() >= NUMBER_OF_LAST_SAMPLES && i > matrix2.size()-1-NUMBER_OF_LAST_SAMPLES; i--) {
//            total += addValues(matrix2.get(i));
//        }
//        System.out.println("asdfasdfasdfasdfasdfasdfasdfasdfasdfasdf" + total);
//        return total < MAX_PER_SAMPLE * NUMBER_OF_LAST_SAMPLES && !(matrix2.size()<= NUMBER_OF_LAST_SAMPLES);
//    }

    public boolean restartSequence(ArrayList<Float> smite){

        float total = 0;

        total += addValues(smite);

       // System.out.println("Valor delta total: "+ total);
        return total < MAX_PER_SAMPLE;
    }

    private float addValues(ArrayList<Float> matrix){
        float sum = 0;
        for (int i = 0; i < matrix.size(); i++) {
            sum += Math.abs(matrix.get(i));
        }
        return sum;
    }

    public float resolve(){
        warpingPath = new int[matrix1Distance + matrix2Distance][2];        // max(n, m) <= K < n + m
        warpingDistance = 0.0f;
        K=1;
        float accumulatedDistance = 0.0f;

       // System.out.println(matrix1.toString());

        float[][] localDistance = new float[matrix1Distance][matrix2Distance];        // local distances
        float[][] globalDistance = new float[matrix1Distance][matrix2Distance];        // global distances

        for (int i = 0; i < matrix1Distance; i++) {
            for (int j = 0; j < matrix2Distance; j++) {
                localDistance[i][j] = distanceBetween(matrix1.get(i), matrix2.get(j));
            }
        }

        globalDistance[0][0] = localDistance[0][0];

        for (int i = 1; i < matrix1Distance; i++) {
            globalDistance[i][0] = localDistance[i][0] + globalDistance[i - 1][0];
        }
        for (int i = 1; i < matrix2Distance; i++) {
            globalDistance[0][i] = localDistance[0][i] + globalDistance[0][i - 1];
        }

        for (int i = 1; i < matrix1Distance; i++) {
            for (int j = 1; j < matrix2Distance; j++) {
                accumulatedDistance = Math.min(Math.min(globalDistance[i-1][j], globalDistance[i-1][j-1]), globalDistance[i][j-1]);
                accumulatedDistance += localDistance[i][j];
                globalDistance[i][j] = accumulatedDistance;
            }
        }
        accumulatedDistance = globalDistance[matrix1Distance - 1][matrix2Distance - 1];

        int i = matrix1Distance - 1;
        int j = matrix2Distance - 1;
        int minIndex = 1;

        warpingPath[K - 1][0] = i;
        warpingPath[K - 1][1] = j;

        while ((i + j) != 0) {
            if (i == 0) {
                j -= 1;
            } else if (j == 0) {
                i -= 1;
            } else {        // i != 0 && j != 0
                float[] array = { globalDistance[i - 1][j], globalDistance[i][j - 1], globalDistance[i - 1][j - 1] };
                minIndex = this.getIndexOfMinimum(array);

                if (minIndex == 0) {
                    i -= 1;
                } else if (minIndex == 1) {
                    j -= 1;
                } else if (minIndex == 2) {
                    i -= 1;
                    j -= 1;
                }
            } // end else
            K++;
            warpingPath[K - 1][0] = i;
            warpingPath[K - 1][1] = j;
        } // end while
        return warpingDistance = accumulatedDistance / K;

    }

    private int getIndexOfMinimum(float[] array) {
        int index = 0;
        float val = array[0];

        for (int i = 1; i < array.length; i++) {
            if (array[i] < val) {
                val = array[i];
                index = i;
            }
        }
        return index;
    }

    private float distanceBetween(ArrayList<Float> arrayList, ArrayList<Float> arrayList2) {
        float distance = 0;
        for (int i = 0; i < arrayList.size(); i++) {
            float minus = (arrayList.get(i) - arrayList2.get(i));
            distance += minus*minus;
        }
        distance = (float) Math.sqrt(distance);
        return distance;
    }



}