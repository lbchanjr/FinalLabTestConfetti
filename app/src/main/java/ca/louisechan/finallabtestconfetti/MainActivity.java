package ca.louisechan.finallabtestconfetti;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.graphics.Point;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private final int PITCH_BLACK_LUX_VALUE = 5; // mean of pitch-black threshold in lux

    // Sensor variables
    private SensorManager sensorManager;
    private Sensor lightSensor, magnetSensor, accelSensor;
    private SensorEventListener lightSensorListener;
    private SensorEventListener orientationSensorListener;

    float[] accelerometerData = new float[3];
    float[] magnetometerData = new float[3];

    double azimuthDegrees;

    // Drawing variables
    GameDrawingSurface gameView;
    private boolean screenIsLocked = false;
    LinearLayout llParent;

//    ArrayList<Confetti> confettis;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        llParent = (LinearLayout) findViewById(R.id.linearLayoutParent);

        // Get size of the screen
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        // programmatically showing the GameDrawingSurface
        // - no need to use an xml file
        gameView = new GameDrawingSurface(this, size.x, size.y);

        llParent.addView(gameView);

        // Configure sensor manager
        this.sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        // try to access the device's light, accelerometer and magnetometer sensor
        this.lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        this.magnetSensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        this.accelSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        // if sensor is avaiable then:
        // -- attach a "listener" to the sensor
        if (this.lightSensor != null) {
            Log.d(TAG, "onCreate: Able to access the light sensor");

            // configure the listener
            this.lightSensorListener = this.configureLightListener();
            sensorManager.registerListener(this.lightSensorListener, lightSensor, SensorManager.SENSOR_DELAY_NORMAL);


        } else {
            Log.d(TAG, "onCreate: Light sensor not available or inaccessible");
            return;

        }

        if (this.magnetSensor != null && this.accelSensor != null) {

            this.orientationSensorListener = configureOrientationListener();

            sensorManager.registerListener(this.orientationSensorListener, magnetSensor,
                    SensorManager.SENSOR_DELAY_NORMAL);

            sensorManager.registerListener(this.orientationSensorListener, accelSensor,
                    SensorManager.SENSOR_DELAY_NORMAL);
        }

        gameView.startGame();
    }

    // This function gets run when user switches from the game to some other app on the phone
    @Override
    protected void onPause() {
        super.onPause();

        // Pause the game
        gameView.pauseGame();
    }

    // This function gets run when user comes back to the game
    @Override
    protected void onResume() {
        super.onResume();

        // Start the game
        gameView.startGame();
    }

    private SensorEventListener configureOrientationListener() {
        return new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {

                // check which sensor the data is coming from
                int sensorType = event.sensor.getType();

                if (sensorType == Sensor.TYPE_ACCELEROMETER) {
                    // data is coming from accelerometer
                    // - clone() creates a copy of teh data
                    // - we could do: accelerometerData = event.values
                    // But the sensor tends to update data faster than you can save and process it
                    // Thus, its better to make a copy than to just use whatever was pulled from the sensor.
                    accelerometerData = event.values.clone();
                }
                else if (sensorType == Sensor.TYPE_MAGNETIC_FIELD) {
                    magnetometerData = event.values.clone();
                }


                // figure out the orientation of the phone
                float[] rotationMatrix = new float[9];
                boolean rotationOK = SensorManager.getRotationMatrix(rotationMatrix, null, accelerometerData, magnetometerData);


                float orientationValues[] = new float[3];
                if (rotationOK == true) {
                    // returns the "angle" or "tilt" of the device
                    SensorManager.getOrientation(rotationMatrix, orientationValues);

                    // azimuth
                    float azimuth = orientationValues[0];
                    // pitch
                    float pitch = orientationValues[1];
                    // roll
                    float roll = orientationValues[2];

                    // Get the heading direction in degrees
                    azimuthDegrees = azimuth*(180/Math.PI);
                    //Log.d(TAG, "onSensorChanged: Heading: " + azimuthDegrees);

                }

            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {

            }
        };
    }


    // configure the light sensor listerner

    public SensorEventListener configureLightListener() {
        return new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                // - inside the "listener", wait for the light sensor to send you data
                // - do something with that data
                float lightLevel = event.values[0];
                //Log.d(TAG, "The light level in lux is: " + lightLevel);

                if (lightLevel <= PITCH_BLACK_LUX_VALUE /*&& customView != null*/) {
                    // Erase canvas.
                    gameView.erase();
                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {
                // rarely used
                // often seen with location services
                // for example, when person switches from wifi to gps
            }
        };


    }
    public void lockScreenPressed(View view) {
        Button btnLockScr = (Button) findViewById(R.id.btnLockScreen);
        if (screenIsLocked == false) {
            screenIsLocked = true;
            btnLockScr.setText("Unlock Screen");
        } else {
            screenIsLocked = false;
            btnLockScr.setText("Lock Screen");
        }

        // Tell surfaceview about lock status
        //customView.lockScreen(screenIsLocked);
        gameView.lockScreen(screenIsLocked);
    }

    public void throwConfettiPressed(View view) {

        double heading = azimuthDegrees;
        boolean headNorth = false;
        Log.d(TAG, "throwConfettiPressed: Heading is " + heading + " degrees");
        if ( (heading >= -90 && heading <= 90)) {
            headNorth = true;
        }

        // set throw direction
        gameView.throwConfettis(headNorth);
    }

    public void sweepIntoPilePressed(View view) {
        gameView.sweepConfettis();
    }
}
