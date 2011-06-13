package ogakisoft.android.furefurecounter;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Date;
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
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class CounterActivity extends Activity implements SensorEventListener,
	OnSharedPreferenceChangeListener {

    // private final static String TAG = "CounterActivity";
    // private final static boolean DEBUG = false;

    private final static int STATUS_STOP = 0;
    private final static int STATUS_START = 1;

    private final static int MENU_HISTORY = 0;
    private final static int MENU_RESET = 1;
    private final static int MENU_SETTINGS = 2;
    private final static int MENU_COUNT_PLUS_1 = 3;
    private final static int MENU_COUNT_MINUS_1 = 4;

    private static final int SAMPLING_SIZE = 30;
    public final static String SAVE_DELIMITER_SYMBOL = "$";
    public final static String HISTORY_FILE_NAME = "furefurecounter_history";

    private TextView mCountText;
    private TextView mGuideText;
    private TextView mElapsedTime;
    private TextView mStartDate;
    private Button mStartStopButton;
    private TapListener mTapListener;
    private ElapsedTimeTask mTask;
    private MediaPlayer mPlayer;
    private int mStatus = STATUS_STOP;
    private int mCount;
    private int mShakeStatus = 0;

    private SensorManager mSensorManager;
    private ValueHolder x;
    // private ValueHolder y;
    // private ValueHolder z;

    // preferences
    private boolean mSoundEnabled = true;
    private boolean mTapEnabled = true;
    private boolean mShakeEnabled = true;
    private boolean mIncrement = true;
    private int mCountInit = 0;
    private int mPitchBottom = 10;
    private int mPitchTop = 60;

    @Override
    public void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.main);

	mCountText = (TextView) findViewById(R.id.label_count);
	mCount = 0;
	mCountText.setText(String.valueOf(mCount));
	mCountText.setClickable(true);
	mTapListener = new TapListener();
	mCountText.setOnClickListener(mTapListener);

	mGuideText = (TextView) findViewById(R.id.label_guide);
	mElapsedTime = (TextView) findViewById(R.id.label_elapsed_time);
	mStartDate = (TextView) findViewById(R.id.label_start_date);
	mStartStopButton = (Button) findViewById(R.id.startStopButton);
	mPlayer = MediaPlayer.create(getApplicationContext(), R.raw.beep);

	mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
	if (mSensorManager.getDefaultSensor(SensorManager.SENSOR_ORIENTATION) == null) {
	    Toast.makeText(CounterActivity.this, R.string.err_no_sensor,
		    Toast.LENGTH_LONG);
	    mShakeEnabled = false;
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
	    mGuideText.setText(getResources().getString(R.string.guideText));
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
	    mGuideText.setText("");
	    mCountText.setEnabled(false);
	    lockOrientation(false);

	    // save history
	    StringBuffer sb = new StringBuffer();
	    if (mStartDate.getText() != null 
		    && mCountText.getText() != null
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
	    sb.append("\n");
	    saveHistory(sb.toString());
	}
    }

    private class TapListener implements OnClickListener {
	@Override
	public void onClick(View v) {
	    if (mStatus == STATUS_STOP)
		return;
	    if (mTapEnabled == false && v != null) {
		Toast.makeText(getApplicationContext(),
			R.string.err_tap_disabled, Toast.LENGTH_SHORT).show();
		return;
	    }
	    synchronized (this) {
		if (mIncrement)
		    mCount++;
		else
		    mCount--;
		if (Integer.valueOf(mCount) == Integer.MAX_VALUE)
		    mCount = 0;
		mCountText.setText(String.valueOf(mCount));
	    }
	    if (mSoundEnabled) {
		mPlayer.seekTo(0);
		mPlayer.start();
	    }
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
	protected void onPostExecute(Void result) {
	    super.onPostExecute(result);
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
	    // if (DEBUG) {
	    // Log.d(TAG,
	    // "orientation azimuth="
	    // + String.valueOf(event.values[0]) + ", pitch="
	    // + String.valueOf(event.values[1]) + ", roll="
	    // + String.valueOf(event.values[2]));
	    // }
	    // float sensorValueZ = event.values[0]; //azimuth
	    float sensorValueX = event.values[1]; // pitch
	    // float sensorValueY = event.values[2]; //roll
	    x.add(sensorValueX);
	    // y.add(sensorValueY);
	    // z.add(sensorValueZ);
	    float valueX = sensorValueX - x.getMedian();
	    // float valueY = sensorValueY - y.getMedian();
	    // float valueZ = sensorValueZ - z.getMedian();
	    // int value = (int)(Math.abs(valueX) + Math.abs(valueY) +
	    // Math.abs(valueZ));
	    if (valueX > 0 && valueX < mPitchBottom) {
		// incline to left or right@0...10
		mShakeStatus = 1;
	    } else if (mShakeStatus == 1) {
		// return 60...70
		if (valueX > mPitchTop) {
		    mShakeStatus = 0;
		    if (mShakeEnabled) {
			mTapListener.onClick(null);
		    } else {
			Toast.makeText(getApplicationContext(),
				R.string.err_shake_disabled, Toast.LENGTH_SHORT)
				.show();
		    }
		}
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
	mSensorManager.registerListener(this,
		mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION),
		SensorManager.SENSOR_DELAY_FASTEST);
	x = new ValueHolder(SAMPLING_SIZE);
	// y = new ValueHolder(SAMPLING_SIZE);
	// z = new ValueHolder(SAMPLING_SIZE);
	PreferenceManager.getDefaultSharedPreferences(this)
		.registerOnSharedPreferenceChangeListener(this);
	lockOrientation(true);
    }

    @Override
    protected void onStop() {
	mSensorManager.unregisterListener(this);
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
	menu.add(0, MENU_COUNT_PLUS_1, Menu.NONE, R.string.menu_count_plus_1);
	menu.add(0, MENU_COUNT_MINUS_1, Menu.NONE, R.string.menu_count_minus_1);
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
	case MENU_COUNT_PLUS_1:
	    if (mStatus == STATUS_START) {
		synchronized (this) {
		    mCount++;
		    mCountText.setText(String.valueOf(mCount));
		    mCountText.invalidate();
		}
	    } else if (mStatus == STATUS_STOP) {
		Toast.makeText(this, R.string.err_adjust_status_stop,
			Toast.LENGTH_LONG).show();
	    }
	    break;
	case MENU_COUNT_MINUS_1:
	    if (mStatus == STATUS_START) {
		synchronized (this) {
		    mCount--;
		    mCountText.setText(String.valueOf(mCount));
		    mCountText.invalidate();
		}
	    } else if (mStatus == STATUS_STOP) {
		Toast.makeText(this, R.string.err_adjust_status_stop,
			Toast.LENGTH_LONG).show();
	    }
	    break;
	}
	return result;
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences pref, String key) {
	if (key.equals("soundEnabled")) {
	    mSoundEnabled = pref.getBoolean(key, true);
	} else if (key.equals("tapEnabled")) {
	    mTapEnabled = pref.getBoolean(key, true);
	} else if (key.equals("shakeEnabled")) {
	    mShakeEnabled = pref.getBoolean(key, true);
	} else if (key.equals("countIncDec")) {
	    if (pref.getString(key, "INC").toString().equals("INC")) {
		mIncrement = true;
	    } else {
		mIncrement = false;
	    }
	} else if (key.equals("countInitValue")) {
	    mCountInit = getStringToInt("countInitValue", 0);
	} else if (key.equals("pitch_bottom")) {
	    mPitchBottom = getStringToInt("pitchBottom", 10);
	} else if (key.equals("pitch_top")) {
	    mPitchTop = getStringToInt("pitchTop", 60);
	}
    }

    private void setFromPreferences() {
	final SharedPreferences pref = PreferenceManager
		.getDefaultSharedPreferences(this);
	mSoundEnabled = pref.getBoolean("soundEnabled", true);
	mTapEnabled = pref.getBoolean("tapEnabled", true);
	mShakeEnabled = pref.getBoolean("shakeEnabled", true);
	if (pref.getString("countIncDec", "INC").toString().equals("INC")) {
	    mIncrement = true;
	} else {
	    mIncrement = false;
	}
	mCountInit = getStringToInt("countInitValue", 0);
	mPitchBottom = getStringToInt("pitchBottom", 10);
	mPitchTop = getStringToInt("pitchTop", 60);
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
	mGuideText.setText("");
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
}