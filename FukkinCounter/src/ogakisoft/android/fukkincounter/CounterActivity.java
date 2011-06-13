package ogakisoft.android.fukkincounter;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class CounterActivity extends Activity implements SensorEventListener,
	OnSharedPreferenceChangeListener {
    // private final static String TAG = "CounterActivity";

    private final static int STATUS_STOP = 0;
    private final static int STATUS_START = 1;

    private final static int MENU_HISTORY = 0;
    private final static int MENU_RESET = 1;
    private final static int MENU_SETTINGS = 2;

    private static final int SAMPLING_SIZE = 10;
    public final static String SAVE_DELIMITER_SYMBOL = "$";
    public final static String HISTORY_FILE_NAME = "fukkincounter_history";

    private SensorGraphView graphView;
    private TextView mCountText;
    private TextView mElapsedTime;
    private TextView mStartDate;
    private Button mStartStopButton;
    private ElapsedTimeTask mTask;
    private MediaPlayer mPlayer;
    private int mStatus = STATUS_STOP;
    private int mCount;

    private SensorManager mSensorManager;
    private ValueHolder orientX, orientY, orientZ;
    // private ValueHolder accelX,accelY,accelZ;
    private int mPitchState;
    private final static int PITCH_STATE_UP = -1;
    private final static int PITCH_STATE_DOWN = 0;
    private int mRollState;
    private final static int ROLL_STATE_BEGIN = -1;
    private final static int ROLL_STATE_END = 0;
    // private float mRollStateBeginZ;
    private boolean mCountState;

    // preferences
    private boolean mSoundEnabled = true;
    private float mPitchTop = 40.0f;
    private float mPitchBottom = 20.0f;
    private float mRollTop = 40.0f;
    private float mRollBottom = 20.0f;

    private int mCountInit = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.main);

	graphView = new SensorGraphView(this);
	LinearLayout layout = (LinearLayout) findViewById(R.id.main_container);
	layout.addView(graphView);

	mCount = 0;
	mCountText = (TextView) findViewById(R.id.label_count);
	mCountText.setText(String.valueOf(mCount));

	mElapsedTime = (TextView) findViewById(R.id.label_elapsed_time);
	mStartDate = (TextView) findViewById(R.id.label_start_date);
	mStartStopButton = (Button) findViewById(R.id.startStopButton);
	mPlayer = MediaPlayer.create(getApplicationContext(), R.raw.beep);

	mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
	if (mSensorManager.getDefaultSensor(SensorManager.SENSOR_ORIENTATION) == null) {
	    // && mSensorManager
	    // .getDefaultSensor(SensorManager.SENSOR_ACCELEROMETER) == null) {
	    Toast.makeText(CounterActivity.this, R.string.err_no_sensor,
		    Toast.LENGTH_LONG);
	}
	setFromPreferences();
    }

    @Override
    protected void onDestroy() {
	super.onDestroy();
	mPlayer.release();
	lockOrientation(false);
    }

    @Override
    public void finish() {
	super.finish();
	lockOrientation(false); // release
    }

    public void onClickStartStop(View v) throws IOException {
	if (mStatus == STATUS_STOP) {
	    mStatus = STATUS_START;
	} else {
	    mStatus = STATUS_STOP;
	}
	if (mStatus == STATUS_START) {
	    lockOrientation(true);
	    mCount = mCountInit;
	    mCountText.setEnabled(true);
	    mCountText.setText(String.valueOf(mCount));
	    mElapsedTime
		    .setText(getResources().getString(R.string.elapsedTime));
	    mStartDate.setText(getResources().getString(R.string.startDate)
		    + " "
		    + java.text.SimpleDateFormat.getDateTimeInstance().format(
			    new Date()));
	    mStartDate.invalidate();
	    mStartStopButton.setText(getResources().getString(
		    R.string.clickToStop));
	    mTask = new ElapsedTimeTask();
	    mTask.execute(null, null, null);
	} else if (mStatus == STATUS_STOP) {
	    if (mTask != null) {
		mTask.cancel(true);
		mTask = null;
	    }
	    mStartStopButton.setText(getResources().getString(
		    R.string.clickToStart));
	    mCountText.setEnabled(false);
	    lockOrientation(false);

	    // save history
	    StringBuffer sb = new StringBuffer();
	    if (mStartDate.getText() != null && mCountText.getText() != null
		    && mElapsedTime.getText() != null) {
		sb.append(mStartDate
			.getText()
			.toString()
			.substring(
				getResources().getString(R.string.startDate)
					.length()).trim());
		sb.append(SAVE_DELIMITER_SYMBOL);
		sb.append(mCountText.getText().toString());
		sb.append(SAVE_DELIMITER_SYMBOL);
		sb.append(mElapsedTime
			.getText()
			.toString()
			.substring(
				getResources().getString(R.string.elapsedTime)
					.length()).trim());
	    }
	    sb.append('\n');
	    saveHistory(sb.toString());
	}
    }

    private class ElapsedTimeTask extends AsyncTask<Void, Void, Void> {
	private boolean mCancel;
	private long startTime;
	private Runnable runnable;

	@Override
	protected void onPreExecute() {
	    super.onPreExecute();
	    startTime = SystemClock.elapsedRealtime();
	}

	@Override
	protected Void doInBackground(Void... params) {
	    runnable = new Runnable() {
		@Override
		public void run() {
		    while (!mCancel) {
			try {
			    Thread.sleep(1000);
			} catch (InterruptedException e) {
			    // e.printStackTrace();
			    return;
			}
			publishProgress(null, null, null);
		    }
		}
	    };
	    runnable.run();
	    return null;
	}

	@Override
	protected void onProgressUpdate(Void... values) {
	    super.onProgressUpdate(values);
	    long currentTime = SystemClock.elapsedRealtime();
	    currentTime -= startTime;
	    // long ms = 0;
	    long sec = 0;
	    long min = 0;
	    long hour = 0;
	    if (currentTime > 0) {
		sec = currentTime / 1000;
		// if (sec > 0)
		// ms = currentTime - (sec * 1000);
	    }
	    if (sec > 0) {
		min = sec / 60;
		if (min > 0)
		    sec = sec - (min * 60);
	    }
	    if (min > 0) {
		hour = min / 60;
		if (hour > 0)
		    min = min - (hour * 60);
	    }
	    String label = getResources().getString(R.string.elapsedTime);
	    mElapsedTime.setText(label + " " + timeformat(hour, min, sec));
	    mElapsedTime.invalidate();
	}

	@Override
	protected void onCancelled() {
	    super.onCancelled();
	    mCancel = true;
	    runnable = null;
	}
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
	// if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
	// All values are in SI units (m/s^2) and measure the acceleration
	// applied to the phone minus the force of gravity.
	// values[0]: Acceleration minus Gx on the x-axis
	// values[1]: Acceleration minus Gy on the y-axis
	// values[2]: Acceleration minus Gz on the z-axis
	// float sensorValueX = event.values[SensorManager.DATA_X];
	// float sensorValueY = event.values[SensorManager.DATA_Y];
	// float sensorValueZ = event.values[SensorManager.DATA_Z];
	// accelX.add(sensorValueX);
	// accelY.add(sensorValueY);
	// accelZ.add(sensorValueZ);
	// float valueX = sensorValueX - accelX.getMedian();
	// float valueY = sensorValueY - accelY.getMedian();
	// float valueZ = sensorValueZ - accelZ.getMedian();
	// Log.v(TAG, "accel x="+valueX+",y="+valueY+",z="+valueZ);
	if (event.sensor.getType() == Sensor.TYPE_ORIENTATION) {
	    // values[0]:
	    // Azimuth, angle between the magnetic north direction
	    // and the Y axis, around the Z axis (0 to 359).
	    // 0=North, 90=East, 180=South, 270=West
	    // values[1]:
	    // Pitch, rotation around X axis (-180 to 180),
	    // with positive values when the z-axis moves toward the y-axis.
	    // values[2]:
	    // Roll, rotation around Y axis (-90 to 90),
	    // with positive values when the x-axis moves away from the z-axis.
	    float sensorValueX = event.values[SensorManager.DATA_X]; // azimuth
	    float sensorValueY = event.values[SensorManager.DATA_Y]; // pitch
	    float sensorValueZ = event.values[SensorManager.DATA_Z]; // roll

	    orientX.add(sensorValueX);
	    orientY.add(sensorValueY);
	    orientZ.add(sensorValueZ);

	    @SuppressWarnings("unused")
	    float valueX = Math.abs(orientX.getRange());
	    float valueY = Math.abs(orientY.getRange());
	    float valueZ = Math.abs(orientZ.getRange());

	    if (mStatus == STATUS_START) {
		if (valueY > mPitchTop) {
		    mPitchState = PITCH_STATE_UP;
		} else if (valueZ > mRollTop) {
		    mRollState = ROLL_STATE_BEGIN;
		} else if (mPitchState == PITCH_STATE_UP && valueY < mPitchBottom) {
		    mCountState = true;
		} else if (mRollState == ROLL_STATE_BEGIN && valueZ < mRollBottom) {
		    mCountState = true;
		}
		if (mCountState) {
		    beep();
		    mCountState = false;
		    mRollState = ROLL_STATE_END;
		    mPitchState = PITCH_STATE_DOWN;
		    mCount++;
		    mCountText.setText(String.valueOf(mCount));
		}
	    }
	}
    }

    private void beep() {
	synchronized (mPlayer) {
	    if (mSoundEnabled) {
		mPlayer.seekTo(0);
		mPlayer.start();
	    }
	}
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    @Override
    protected void onRestart() {
	super.onRestart();
	setFromPreferences();
    }

    @Override
    protected void onResume() {
	super.onResume();
	List<Sensor> sensors = mSensorManager.getSensorList(Sensor.TYPE_ALL);
	for (Sensor s : sensors) {
	    mSensorManager.registerListener(this, s,
		    SensorManager.SENSOR_DELAY_NORMAL);
	    mSensorManager.registerListener(graphView, s,
		    SensorManager.SENSOR_DELAY_NORMAL);
	}
	orientX = new ValueHolder(SAMPLING_SIZE);
	orientY = new ValueHolder(SAMPLING_SIZE);
	orientZ = new ValueHolder(SAMPLING_SIZE);
	// accelX = new ValueHolder(SAMPLING_SIZE);
	// accelY = new ValueHolder(SAMPLING_SIZE);
	// accelZ = new ValueHolder(SAMPLING_SIZE);
	PreferenceManager.getDefaultSharedPreferences(this)
		.registerOnSharedPreferenceChangeListener(this);
	lockOrientation(true);
    }

    @Override
    protected void onStop() {
	mSensorManager.unregisterListener(this);
	mSensorManager.unregisterListener(graphView);
	PreferenceManager.getDefaultSharedPreferences(this)
		.unregisterOnSharedPreferenceChangeListener(this);
	lockOrientation(false);
	super.onStop();
    }

    private void lockOrientation(boolean fix) {
	if (fix) {
	    if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
	    } else if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
	    }
	} else {
	    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
	}
    }

    private void saveHistory(String text) throws IOException {
	BufferedWriter out = new BufferedWriter(new OutputStreamWriter(
		openFileOutput(HISTORY_FILE_NAME, Context.MODE_APPEND)));
	synchronized (out) {
	    out.write(text);
	    out.newLine();
	    out.flush();
	    out.close();
	}
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
	super.onCreateOptionsMenu(menu);
	menu.add(0, MENU_HISTORY, Menu.NONE, R.string.menu_history);
	menu.add(0, MENU_RESET, Menu.NONE, R.string.menu_reset);
	menu.add(0, MENU_SETTINGS, Menu.NONE, R.string.menu_settings);
	return true;
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
	boolean result = super.onMenuItemSelected(featureId, item);
	switch (item.getItemId()) {
	case MENU_RESET:
	    reset();
	    break;
	case MENU_HISTORY:
	    startActivity(new Intent(this, HistoryActivity.class));
	    break;
	case MENU_SETTINGS:
	    startActivity(new Intent(this, Settings.class));
	    break;
	}
	return result;
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences pref, String key) {
	if (key.equals("soundEnabled")) {
	    mSoundEnabled = pref.getBoolean(key, true);
	} else if (key.equals("pitchTop")) {
	    mPitchTop = getStringToInt("pitchTop", 40);
	} else if (key.equals("pitchBottom")) {
	    mPitchBottom = getStringToInt("pitchBottom", 20);
	} else if (key.equals("rollTop")) {
	    mRollTop = getStringToInt("rollTop", 40);
	} else if (key.equals("rollBottom")) {
	    mRollBottom = getStringToInt("rollBottom", 20);
	}
    }

    private void setFromPreferences() {
	final SharedPreferences pref = PreferenceManager
		.getDefaultSharedPreferences(this);
	mSoundEnabled = pref.getBoolean("soundEnabled", true);
	mPitchTop = getStringToInt("pitchTop", 40);
	mPitchBottom = getStringToInt("pitchBottom", 20);
	mRollTop = getStringToInt("rollTop", 40);
	mRollBottom = getStringToInt("rollBottom", 20);
    }

    private int getStringToInt(String key, int defaultValue) {
	SharedPreferences pref = PreferenceManager
		.getDefaultSharedPreferences(this);
	String value = pref.getString(key, String.valueOf(defaultValue));
	int intValue = defaultValue;
	if (isNumeric(value))
	    intValue = Integer.parseInt(value);
	return intValue;
    }

    private static boolean isNumeric(String str) {
	Pattern p = Pattern.compile("[0-9]+");
	Matcher m = p.matcher(str);
	return m.matches();
    }

    private void reset() {
	if (mTask != null) {
	    mTask.cancel(true);
	    mTask = null;
	}
	mCount = mCountInit;
	mCountText.setEnabled(false);
	mCountText.setText(String.valueOf(mCount));
	mElapsedTime.setText("");
	mStartDate.setText("");
	mStartDate.invalidate();
	mStartStopButton.setText(getResources()
		.getString(R.string.clickToStart));
	lockOrientation(false);
    }

    private String timeformat(long hour, long min, long sec) {
	StringBuffer sb = new StringBuffer();
	sb.append(hour < 10 ? "0" + hour : hour).append(':')
		.append(min < 10 ? "0" + min : min).append(':')
		.append(sec < 10 ? "0" + sec : sec);
	return sb.toString();
    }

    public void onClickCountUp(View v) {
	if (mStatus == STATUS_START) {
	    if (mCount < Integer.MAX_VALUE) {
		++mCount;
		beep();
	    }
	    mCountText.setText(String.valueOf(mCount));
	    mCountText.invalidate();
	}
    }

    public void onClickCountDown(View v) {
	if (mStatus == STATUS_START) {
	    if (mCount > 0) {
		--mCount;
		beep();
	    }
	    mCountText.setText(String.valueOf(mCount));
	    mCountText.invalidate();
	}
    }
}