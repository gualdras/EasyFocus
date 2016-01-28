package university.ssii.easyfocus;

import android.app.Service;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.StringTokenizer;

/**
 * Created by gualdras on 8/12/15.
 */
public class CheckPattern extends Service implements SensorEventListener {

    SensorManager sensorManager;
    Sensor mSensor;
    static boolean isActive = false;

    public final static StringBuffer UPDOWNPATTERN = new StringBuffer("0.392, 9.682, 1.972, -0.038, 9.117, 2.355, -0.287, 8.552, 2.154, 1.139, 9.548, 2.039, -0.076, 8.935, 2.94, 0.689, 9.184, 1.905, -0.641, 8.035, -0.545, 0.076, 4.271, -12.89, 4.022, -3.486, -5.535, 1.034, -13.254, 19.336, -2.106, -9.663, 19.336, 1.484, -6.723, 19.336, 1.302, -5.449, 19.336, 4.606, -5.344, 1.024, 1.886, 0.986, -0.861, -3.342, 4.453, -5.775, 0.536, 8.801, 2.451, 0.976, 8.81, 1.829, 1.034, 8.657, 0.134, 0.373, 8.878, 3.38, 0.038, 9.692, 1.599, 1.005, 8.715, 2.911, 0.852, 9.423, 1.915, 0.363, 9.328, 2.834");
    public final static StringBuffer LEFTPATTERN = new StringBuffer("0.134, 10.036, -0.191, -0.268, 9.921, 0.517, -1.877, 9.031, 1.599, -9.347, 0.507, -4.319, 7.862, -4.97, 3.342, 17.784, -0.229, -2.298, 19.901, 4.798, -2.758, 19.901, 1.331, 0.114, 19.901, 2.949, 3.131, 11.933, -1.953, 2.298, -2.566, -0.718, -5.037, -5.056, 3.821, -1.465, -1.561, 8.849, 0.344, 0.766, 10.534, -1.101, -0.354, 10.439, -1.551, 0.785, 9.548, -0.191, -0.038, 9.366, -0.086, -0.095, 9.854, -2.47, 0.852, 9.682, 0.181");
    public final static StringBuffer RIGHTPATTERN = new StringBuffer("0.555, 9.481, 1.474, -0.162, 9.72, 0.038, 15.38, 16.118, 9.577, 11.368, 4.577, -3.342, -7.383, -6.924, -2.825, -14.739, -6.129, -0.651, -17.708, -4.913, -6.522, -16.999, -2.423, -3.696, -10.601, -4.233, -2.317, 11.454, 7.288, 2.135, 7.91, 11.301, -2.011, 4.031, 8.849, -0.172, 0.316, 7.958, -0.067, 2.394, 10.429, -0.928, 0.383, 9.146, 1.522, 0.727, 9.433, -0.335, 1.417, 9.845, 0.067");
    //public final static StringBuffer UPDOWNPATTERN = new StringBuffer("-0.325, 9.308, 0.201, -0.028, 9.826, 2.375, 0.296, 9.356, 1.417, -0.632, 8.791, 1.245, 0.22, 9.653, 0.689, -2.001, 10.822, 0.44, 2.26, 16.491, 4.041, 3.543, 15.38, 2.672, 1.857, 6.205, 0.612, -3.342, -5.679, -2.001, -5.43, -9.586, -3.208, -1.388, 1.11, -1.053, 17.641, 19.422, 5.679, 10.803, 19.422, 14.413, 0.555, 11.339, 5.334, -1.618, -0.191, 0.0, -4.769, -9.625, -0.976, -3.294, -13.532, -4.625, 0.258, 2.097, -1.292, 8.494, 19.422, 2.48, 1.11, 19.125, 10.726, 1.465, 10.927, 3.744, -0.536, 6.263, 3.677, 0.0, 7.316, 2.978, 1.216, 8.657, -0.306, 0.565, 7.288, 2.403, -0.009, 8.245, 2.135, 0.67, 9.165, 1.177, 0.239, 9.471, 2.375, 0.201, 9.807, 1.925, ");
    //public final static StringBuffer UPDOWNPATTERN = new StringBuffer( "0.35500002, 0.15299988, 0.45000005, 0.967, 0.7279997, 3.466, 0.163, 0.4499998, 1.752, 0.056999996, 0.123999596, 0.575, 0.229, 0.05799961, 0.20100003, 0.18200001, 2.8920002, 0.12400001, 2.067, 5.354, 0.268, 0.3160001, 0.6129999, 1.704, 3.628, 11.731999, 1.1299999, 1.9160001, 6.6660013, 0.7939999, 1.5509999, 0.0, 0.612, 4.319, 6.781001, 0.40199995, 1.6370001, 6.819, 2.767, 0.18199998, 4.424, 0.957, 0.52599996, 3.074, 0.29700005, 0.039000034, 1.293, 0.21999997, 0.45, 3.495, 1.503, 1.7620001, 10.554001, 2.6239998, 5.4779997, 5.7560005, 1.5890002, 2.0879998, 0.76600075, 3.821, 4.482, 9.491, 0.747, 0.211, 3.2280002, 2.4329998, 0.574, 0.6699996, 3.8409998, 0.36299998, 1.743, 1.876, 0.27800003, 1.3889999, 1.8570001, 0.75600004, 1.283, 0.565, 1.819, 0.5270004, 0.31599998, 0.996, 1.1590004, 0.68799996");
   // public final static StringBuffer UPDOWNPATTERN = new StringBuffer( "3.5, 4.5, 6.5 ");
    public final static StringBuffer SHAKEPATTERN = new StringBuffer("0.057, 9.471, 1.522, 0.383, 9.739, 0.383, -1.57, 9.711, -2.298, 1.819, 8.59, 2.193, 4.29, 9.874, 2.566, 3.792, 9.28, 1.551, 0.708, 8.83, 0.603, 4.118, 10.582, 1.723, 3.045, 8.504, 6.838, 6.397, 10.008, -0.038, 1.656, 8.916, -1.714, 1.953, 10.343, -2.988, -1.139, 8.676, -0.641, -0.153, 9.902, -0.469, -0.584, 9.04, 0.996, -0.335, 9.146, 0.162, -0.095, 9.567, -0.162, -0.258, 9.308, 0.641");

             //"0.038999975, 0.038000107, 0.42200005, 0.34499997, 0.21999931, 0.47000003, 0.297, 0.10499954, 0.11500001, 0.20099998, 0.125, 0.12400007, 0.41199997, 0.34500027, 0.06700003, 0.15400001, 0.20100021, 0.278, 0.047999978, 0.07600021, 0.72800004, 0.04699999, 0.33500004, 0.37300003, 0.125, 0.1630001, 0.32499993, 0.048000038, 0.05799961, 0.17199993, 0.34500003, 0.1630001, 0.37400007, 0.287, 1.2639999, 0.2210002, 0.162, 4.9329996, 3.1990001, 5.784, 2.3359995, 3.5530005, 5.565, 6.214999, 5.3150005, 1.263, 4.951, 1.657, 1.426, 3.908, 2.805"

    static ArrayList<StringBuffer> activePatterns = new ArrayList<StringBuffer>();
    static ArrayList<DynamicTimeWarper> dynamicTimeWarpers = new ArrayList<DynamicTimeWarper>();


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        sensorManager.registerListener(CheckPattern.this, mSensor, SensorManager.SENSOR_DELAY_NORMAL);

        initializeList();
        getAvailablesPatterns();
        for(int i = 0; i < Constants.NPATTERNS; i++){
            if(activePatterns.get(i) != null){
                isActive = true;
            }
        }
        if(!isActive){
            stopSelf();
        }
    }

    public static void initializeList(){
        for (int i = 0; i < Constants.NPATTERNS; i++){
            dynamicTimeWarpers.add(null);
            activePatterns.add(null);
        }
    }

    @Override
    public void onDestroy() {
        sensorManager.unregisterListener(CheckPattern.this);
        isActive = false;
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        isActive = false;
        return START_STICKY;

    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        ArrayList<Float> values = new ArrayList<Float>();
        for(int i = 0; i<event.values.length; i++){
            values.add(event.values[i]);
        }
        DynamicTimeWarper dtw;
        if((dtw = dynamicTimeWarpers.get(Constants.LEFTPATTERN_ID)) != null && dtw.iterateMovement(values, 7.5f)){
            Toast.makeText(getApplicationContext(), "LEFT", Toast.LENGTH_SHORT).show();
            MainActivity.changeSwitchState(Constants.LEFTPATTERN_ID);
        }else if((dtw = dynamicTimeWarpers.get(Constants.RIGHTPATTERN_ID)) != null && dtw.iterateMovement(values, 7.5f)){
            Toast.makeText(getApplicationContext(), "RIGHT", Toast.LENGTH_SHORT).show();
            MainActivity.changeSwitchState(Constants.RIGHTPATTERN_ID);
        }else if((dtw = dynamicTimeWarpers.get(Constants.SHAKEPATTERN_ID)) != null && dtw.iterateMovement(values, 4f)){
            Toast.makeText(getApplicationContext(), "SHAKE", Toast.LENGTH_SHORT).show();
            MainActivity.changeSwitchState(Constants.SHAKEPATTERN_ID);
        }else if((dtw = dynamicTimeWarpers.get(Constants.UPDOWNPATTERN_ID)) != null && dtw.iterateMovement(values,7.4f)) {
            Toast.makeText(getApplicationContext(), "UPDOWN", Toast.LENGTH_SHORT).show();
            MainActivity.changeSwitchState(Constants.UPDOWNPATTERN_ID);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public static void addAvailablePattern(int patternID){
        DynamicTimeWarper dynamicTimeWarper;
        switch (patternID){
            case Constants.LEFTPATTERN_ID:
                activePatterns.set(Constants.LEFTPATTERN_ID, LEFTPATTERN);
                dynamicTimeWarpers.set(Constants.LEFTPATTERN_ID, new DynamicTimeWarper(preprocessPattern(LEFTPATTERN)));
                break;
            case Constants.RIGHTPATTERN_ID:
                activePatterns.set(Constants.RIGHTPATTERN_ID, RIGHTPATTERN);
                dynamicTimeWarpers.set(Constants.RIGHTPATTERN_ID, new DynamicTimeWarper(preprocessPattern(RIGHTPATTERN)));
                break;
            case Constants.UPDOWNPATTERN_ID:
                activePatterns.set(Constants.UPDOWNPATTERN_ID, UPDOWNPATTERN);
                dynamicTimeWarpers.set(Constants.UPDOWNPATTERN_ID, new DynamicTimeWarper(preprocessPattern(UPDOWNPATTERN)));
                break;
            case Constants.SHAKEPATTERN_ID:
                activePatterns.set(Constants.SHAKEPATTERN_ID, SHAKEPATTERN);
                dynamicTimeWarpers.set(Constants.SHAKEPATTERN_ID, new DynamicTimeWarper(preprocessPattern(SHAKEPATTERN)));
                break;
        }
    }

    public static void removeAvailablePatterns(int patternID){
        switch (patternID){
            case Constants.LEFTPATTERN_ID:
                activePatterns.set(Constants.LEFTPATTERN_ID, null);
                dynamicTimeWarpers.set(Constants.LEFTPATTERN_ID, null);
                break;
            case Constants.RIGHTPATTERN_ID:
                activePatterns.set(Constants.RIGHTPATTERN_ID, null);
                dynamicTimeWarpers.set(Constants.RIGHTPATTERN_ID, null);
                break;
            case Constants.UPDOWNPATTERN_ID:
                activePatterns.set(Constants.UPDOWNPATTERN_ID, null);
                dynamicTimeWarpers.set(Constants.UPDOWNPATTERN_ID, null);
                break;
            case Constants.SHAKEPATTERN_ID:
                activePatterns.set(Constants.SHAKEPATTERN_ID, null);
                dynamicTimeWarpers.set(Constants.SHAKEPATTERN_ID, null);
                break;
        }
    }

    public static ArrayList<ArrayList<Float>> preprocessPattern(StringBuffer pattern){
        ArrayList<ArrayList<Float>> matrix1 = new ArrayList<ArrayList<Float>>();
        ArrayList<Float> singleDimensionMatrix = new ArrayList<Float>();
        StringTokenizer st = new StringTokenizer (pattern.toString(), ",");
        for(int i = 0; st.hasMoreTokens(); i++){
            String token = st.nextToken();
            System.out.println(token);
            singleDimensionMatrix.add(Float.parseFloat(token));
            if(i%3 == 2){
                matrix1.add(singleDimensionMatrix);
                singleDimensionMatrix = new ArrayList<Float>();
            }
        }

        return matrix1;
    }

    private void getAvailablesPatterns() {
        BufferedReader reader = null;
        try {
            FileInputStream fis = openFileInput(Constants.FILE_NAME);
            reader = new BufferedReader(new InputStreamReader(fis));

            int activation;

            while (null !=  reader.readLine()) {
                reader.readLine();
                reader.readLine();
                activation = Integer.valueOf(reader.readLine());
                Boolean.valueOf(reader.readLine());
                Boolean.valueOf(reader.readLine());
                addAvailablePattern(activation);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (null != reader) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}

