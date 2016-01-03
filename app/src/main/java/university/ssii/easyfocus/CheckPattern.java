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

    public final static StringBuffer SHAKEPATTERN = new StringBuffer("0.459, 8.619, 6.311, 1.321, 9.586, 4.185, 2.566, 9.998, 6.33, -1.13, 8.245, 2.423, -2.059, 8.188, 3.457, -3.974, 7.307, 3.562, -1.245, 8.207, 2.413, -0.335, 8.609, 4.453, 0.181, 10.008, 2.097, -2.691, 7.556, 2.231, -19.317, -3.725, 2.71, -9.05, -9.471, 2.988, 19.901, 15.246, 3.399, 19.901, 12.048, 4.453, 4.836, 0.727, -0.469, -11.626, -1.426, 3.15, -19.317, -9.433, 0.363, -4.175, -19.795, 6.416, 19.901, 18.541, -10.199, 19.901, 5.775, 3.035, -8.801, -9.347, -1.925, -17.957, -0.593, 7.489, -5.047, 7.173, -1.091, 7.106, 12.383, 5.842, -2.978, 8.581, 1.915, -0.268, 8.073, 2.844, 2.911, 9.95, 4.041, -0.21, 8.887, 2.269, -1.541, 7.814, 4.108, 0.747, 9.04, 3.342,  ");
    public final static StringBuffer LEFTPATTERN = new StringBuffer("0.172, 9.414, 2.202, 0.679, 9.72, 1.829, -0.258, 9.127, 2.346, -0.191, 9.059, 2.126, -0.986, 9.165, 1.101, -7.211, 8.312, -8.207, 6.55, 9.308, -9.663, 8.514, 12.23, 0.258, 5.784, 11.348, -2.47, 7.891, 9.337, 2.394, 4.443, 8.82, -1.331, -1.915, 10.017, -9.165, -14.308, 0.268, -18.349, 19.901, 16.386, -16.97, 9.701, 12.996, 5.248, 2.451, 8.82, 3.792, -1.034, 8.657, -1.771, 2.049, 10.142, 1.446, -2.49, 7.556, 1.58, 0.172, 9.136, 1.992, 3.083, 10.63, 3.265, -0.89, 8.485, 2.288, -0.574, 8.868, 2.193, 0.996, 9.682, 1.915, 0.641, 9.203, 2.011,  ");
    public final static StringBuffer RIGHTPATTERN = new StringBuffer("0.986, 8.925, 2.346, 0.555, 8.217, 3.84, 0.603, 9.021, 3.074, 0.814, 9.203, 3.035, 1.359, 8.657, 3.208, 1.254, 9.098, 5.65, 1.082, 8.494, 4.041, -1.12, 8.533, 2.164, -1.733, 7.977, 2.557, -6.263, 9.826, -8.552, 17.775, 14.241, -1.81, 5.813, 8.935, 4.635, 4.941, 10.199, 2.758, 3.409, 6.876, 0.201, 4.692, 9.366, -2.154, -9.538, 9.289, -8.609, -0.095, 2.681, -1.503, 19.23, 14.336, -12.909, 8.705, 12.067, 5.142, 4.932, 7.01, -0.402, -1.896, 10.295, -5.162, -0.459, 7.489, 3.84, 5.822, 9.711, 5.238, 1.762, 9.567, 2.94, -0.057, 9.059, 0.957, 0.354, 9.366, 1.034, -0.354, 8.619, 2.71, 1.015, 9.634, 1.723, 1.13, 9.663, 1.158, 1.187, 9.462, 4.175, ");
    public final static StringBuffer UPDOWNPATTERN = new StringBuffer("-0.325, 9.308, 0.201, -0.028, 9.826, 2.375, 0.296, 9.356, 1.417, -0.632, 8.791, 1.245, 0.22, 9.653, 0.689, -2.001, 10.822, 0.44, 2.26, 16.491, 4.041, 3.543, 15.38, 2.672, 1.857, 6.205, 0.612, -3.342, -5.679, -2.001, -5.43, -9.586, -3.208, -1.388, 1.11, -1.053, 17.641, 19.422, 5.679, 10.803, 19.422, 14.413, 0.555, 11.339, 5.334, -1.618, -0.191, 0.0, -4.769, -9.625, -0.976, -3.294, -13.532, -4.625, 0.258, 2.097, -1.292, 8.494, 19.422, 2.48, 1.11, 19.125, 10.726, 1.465, 10.927, 3.744, -0.536, 6.263, 3.677, 0.0, 7.316, 2.978, 1.216, 8.657, -0.306, 0.565, 7.288, 2.403, -0.009, 8.245, 2.135, 0.67, 9.165, 1.177, 0.239, 9.471, 2.375, 0.201, 9.807, 1.925, ");

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

        for (int i = 0; i < Constants.NPATTERNS; i++){
            dynamicTimeWarpers.add(null);
            activePatterns.add(null);
        }
        getAvailablesPatterns();
        for(int i = 0; i < Constants.NPATTERNS; i++){
            if(activePatterns.get(i) != null){
                isActive = true;
            }
        }
        if(!isActive){
            stopSelf();
        }
        System.out.println("Entrandoloooooo");
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
        if((dtw = dynamicTimeWarpers.get(Constants.LEFTPATTERN_ID)) != null && dtw.iterateMovement(values, 3f)){
            Toast.makeText(getApplicationContext(), "LEFT", Toast.LENGTH_SHORT).show();
            MainActivity.changeSwitchState(Constants.LEFTPATTERN_ID);
        }else if((dtw = dynamicTimeWarpers.get(Constants.RIGHTPATTERN_ID)) != null && dtw.iterateMovement(values, 3f)){
            Toast.makeText(getApplicationContext(), "RIGHT", Toast.LENGTH_SHORT).show();
            MainActivity.changeSwitchState(Constants.RIGHTPATTERN_ID);
        }else if((dtw = dynamicTimeWarpers.get(Constants.SHAKEPATTERN_ID)) != null && dtw.iterateMovement(values, 3f)){
            Toast.makeText(getApplicationContext(), "SHAKE", Toast.LENGTH_SHORT).show();
            MainActivity.changeSwitchState(Constants.SHAKEPATTERN_ID);
        }else if((dtw = dynamicTimeWarpers.get(Constants.UPDOWNPATTERN_ID)) != null && dtw.iterateMovement(values, 3f)) {
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
        for(int i = 0; i < pattern.length(); i++){
            StringTokenizer st = new StringTokenizer (pattern.toString(), ",");
            singleDimensionMatrix.add(Float.parseFloat(st.nextToken()));
            if(i%3 == 0){
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

