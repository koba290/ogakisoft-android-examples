package ogakisoft.android.sample;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.Toast;

public class SampleActivity extends Activity {
    private SensorGraphView mGraphView;
    private SensorManager mSensorManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	mGraphView = new SensorGraphView(this);
	setContentView(mGraphView);
	mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
	if (mSensorManager.getDefaultSensor(SensorManager.SENSOR_ORIENTATION) == null) {
	    Toast.makeText(SampleActivity.this, "no orientation sensor", Toast.LENGTH_LONG);
	}
	if (mSensorManager.getDefaultSensor(SensorManager.SENSOR_ACCELEROMETER) == null) {
	    Toast.makeText(SampleActivity.this, "no accelerometer sensor", Toast.LENGTH_LONG);
	}
    }

    @Override
    protected void onDestroy() {
	super.onDestroy();
	lockOrientation(false);
    }

    @Override
    public void finish() {
	super.finish();
	lockOrientation(false); // release
    }

    @Override
    protected void onResume() {
	super.onResume();
	List<Sensor> sensors = mSensorManager.getSensorList(Sensor.TYPE_ALL);
	for (Sensor s : sensors) {
	    mSensorManager.registerListener(mGraphView, s,
		    SensorManager.SENSOR_DELAY_NORMAL);
	}
	lockOrientation(true);
    }

    @Override
    protected void onStop() {
	mSensorManager.unregisterListener(mGraphView);
	lockOrientation(false);
	super.onStop();
    }

    private void lockOrientation(boolean fix) {
	if (fix) {
	    if (getResources().getConfiguration().orientation
		    == Configuration.ORIENTATION_LANDSCAPE) {
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
	    } else if (getResources().getConfiguration().orientation
		    == Configuration.ORIENTATION_PORTRAIT) {
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
	    }
	} else {
	    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
	}
    }

}