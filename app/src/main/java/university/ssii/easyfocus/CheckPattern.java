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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.StringTokenizer;

/**
 * Created by gualdras on 8/12/15.
 */
public class CheckPattern extends Service implements SensorEventListener {

    SensorManager sensorManager;
    Sensor mSensor;

    StringBuffer pattern = new StringBuffer("0.344, 7.7, 4.951, -0.459, 7.68, 6.253, -0.44, 7.987, 5.918, -0.718, 7.977, 6.694, -0.411, 7.661, 4.118, 0.21, 7.106, 1.407, 0.335, 5.918, 1.877, 0.373, 4.434, 1.513, 0.833, 2.097, 11.76, 0.172, 0.766, 13.436, -0.181, 1.197, 19.336, -0.737, 2.624, 12.833, 0.172, 3.85, 10.343, 0.028, 4.041, 6.818, 0.047, 6.186, 1.455, 0.766, 7.134, -2.087, 0.603, 6.905, -2.49, 0.612, 6.723, -2.461, 0.019, 5.631, 1.656, 0.976, 3.964, 6.138, 1.436, 2.451, 12.325, 0.986, 2.384, 14.672, 1.857, 2.767, 17.363, -0.268, 3.706, 17.985, -0.478, 3.821, 8.006, 1.934, 4.28, 6.579, 0.785, 5.909, 4.654, -0.469, 6.215, 3.562, 0.804, 6.742, 2.921,");
    DynamicTimeWarper dtw;
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

        dtw = new DynamicTimeWarper(preprocessPattern());
        System.out.println("Entrandoloooooo");
    }

    @Override
    public void onDestroy() {
        sensorManager.unregisterListener(CheckPattern.this);
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        return START_STICKY;

    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        ArrayList<Float> values = new ArrayList<Float>();
        for(int i = 0; i<event.values.length; i++){
            values.add(event.values[i]);
        }

        if(dtw.iterateMovement(values, 2.8f)){
            Toast.makeText(getApplicationContext(), "Reconocido", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public ArrayList<ArrayList<Float>> preprocessPattern(){
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
}

