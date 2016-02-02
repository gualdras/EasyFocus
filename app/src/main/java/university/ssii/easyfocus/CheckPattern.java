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
        }else if((dtw = dynamicTimeWarpers.get(Constants.UPDOWNPATTERN_ID)) != null && dtw.iterateMovement(values,7.4f)) {
            Toast.makeText(getApplicationContext(), "UPDOWN", Toast.LENGTH_SHORT).show();
            MainActivity.changeSwitchState(Constants.UPDOWNPATTERN_ID);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public static void addAvailablePattern(int patternID){
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

