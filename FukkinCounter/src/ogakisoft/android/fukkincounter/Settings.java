package ogakisoft.android.fukkincounter;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.widget.Toast;

public class Settings extends PreferenceActivity implements
	OnSharedPreferenceChangeListener {
    // private final static String TAG = "Settings";

    @Override
    public void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);

	addPreferencesFromResource(R.xml.preference);
	final SharedPreferences sharedPreferences = PreferenceManager
		.getDefaultSharedPreferences(Settings.this);
	onSharedPreferenceChanged(sharedPreferences, null);
    }

    @Override
    protected void onResume() {
	super.onResume();
	PreferenceManager.getDefaultSharedPreferences(this)
		.registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onStop() {
	PreferenceManager.getDefaultSharedPreferences(this)
		.unregisterOnSharedPreferenceChangeListener(this);
	super.onStop();
    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen,
	    Preference preference) {
	boolean result = super.onPreferenceTreeClick(preferenceScreen,
		preference);
	CheckBoxPreference mSoundEnabled = (CheckBoxPreference) findPreference("soundEnabled");
	EditDigitPreference mSamplingValue = (EditDigitPreference) findPreference("sampling");
	EditDigitPreference mPitchTopValue = (EditDigitPreference) findPreference("pitchTop");
	EditDigitPreference mPitchBottomValue = (EditDigitPreference) findPreference("pitchBottom");
	EditDigitPreference mRollTopValue = (EditDigitPreference) findPreference("rollTop");
	EditDigitPreference mRollBottomValue = (EditDigitPreference) findPreference("rollBottom");

	if (preference == mSoundEnabled) {
	} else if (preference == mSamplingValue) {
	    if (Math.abs(toInt(mSamplingValue.getText())) >= 1 &&
		Math.abs(toInt(mSamplingValue.getText())) <= 20) {
		mSamplingValue.setSummary(getResources().getString(
			R.string.pref_sampling_summary)
			+ getResources().getString(R.string.pref_summary_current)
			+ String.valueOf(mSamplingValue.getText()));
	    } else {
		Toast.makeText(Settings.this, R.string.err_illegal_numeric,
			Toast.LENGTH_SHORT).show();
		mSamplingValue.getEditor().clear();
		mSamplingValue.getEditor().commit();
	    }
	} else if (preference == mPitchBottomValue) {
	    if (Math.abs(toInt(mPitchBottomValue.getText())) <= 180) {
		mPitchBottomValue.setSummary(getResources().getString(
			R.string.pref_pitchBottom_summary)
			+ getResources().getString(R.string.pref_summary_current)
			+ String.valueOf(mPitchBottomValue.getText()));
	    } else {
		Toast.makeText(Settings.this, R.string.err_illegal_numeric,
			Toast.LENGTH_SHORT).show();
		mPitchBottomValue.getEditor().clear();
		mPitchBottomValue.getEditor().commit();
	    }
	} else if (preference == mPitchTopValue) {
	    if (Math.abs(toInt(mPitchTopValue.getText())) <= 180) {
		mPitchTopValue.setSummary(getResources().getString(
			R.string.pref_pitchTop_summary)
			+ getResources().getString(R.string.pref_summary_current)
			+ String.valueOf(mPitchTopValue.getText()));
	    } else {
		Toast.makeText(Settings.this, R.string.err_illegal_numeric,
			Toast.LENGTH_SHORT).show();
		mPitchTopValue.getEditor().clear();
		mPitchTopValue.getEditor().commit();
	    }
	} else if (preference == mRollBottomValue) {
	    if (Math.abs(toInt(mRollBottomValue.getText())) <= 90) {
		mRollBottomValue.setSummary(getResources().getString(
			R.string.pref_rollBottom_summary)
			+ getResources().getString(R.string.pref_summary_current)
			+ String.valueOf(mRollBottomValue.getText()));
	    } else {
		Toast.makeText(Settings.this, R.string.err_illegal_numeric,
			Toast.LENGTH_SHORT).show();
		mRollBottomValue.getEditor().clear();
		mRollBottomValue.getEditor().commit();
	    }
	} else if (preference == mRollTopValue) {
	    if (Math.abs(toInt(mRollTopValue.getText())) <= 90) {
		mRollTopValue.setSummary(getResources().getString(
			R.string.pref_rollTop_summary)
			+ getResources().getString(R.string.pref_summary_current)
			+ String.valueOf(mRollTopValue.getText()));
	    } else {
		Toast.makeText(Settings.this, R.string.err_illegal_numeric,
			Toast.LENGTH_SHORT).show();
		mRollTopValue.getEditor().clear();
		mRollTopValue.getEditor().commit();
	    }
	}
	return result;
    }

    private static int toInt(String str) {
	int val = 0;
	try {
	    val = Integer.parseInt(str);
	} catch(NumberFormatException e) {
	    e.printStackTrace();
	    val = 0;
	}
	return val;
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
	    String key) {
	CheckBoxPreference mSoundEnabled = (CheckBoxPreference) findPreference("soundEnabled");
	EditDigitPreference mSamplingValue = (EditDigitPreference) findPreference("sampling");
	EditDigitPreference mPitchBottomValue = (EditDigitPreference) findPreference("pitchBottom");
	EditDigitPreference mPitchTopValue = (EditDigitPreference) findPreference("pitchTop");
	EditDigitPreference mRollTopValue = (EditDigitPreference) findPreference("rollTop");
	EditDigitPreference mRollBottomValue = (EditDigitPreference) findPreference("rollBottom");

	if (key == null || key.equals("soundEnabled")) {
	    if (sharedPreferences.getBoolean("soundEnabled", true)) {
		mSoundEnabled.setChecked(true);
	    } else {
		mSoundEnabled.setChecked(false);
	    }
	}
	if (key == null || key.equals("sampling")) {
	    if (Math.abs(toInt(mSamplingValue.getText())) >= 1 &&
		Math.abs(toInt(mSamplingValue.getText())) <= 20) {
		mSamplingValue.setSummary(getResources().getString(
			R.string.pref_sampling_summary)
			+ getResources().getString(R.string.pref_summary_current)
			+ String.valueOf(mSamplingValue.getText()));
	    } else {
		Toast.makeText(Settings.this, R.string.err_illegal_numeric,
			Toast.LENGTH_SHORT).show();
		mSamplingValue.getEditor().putInt("sampling", 5);
		mSamplingValue.getEditor().commit();
		mSamplingValue.setSummary(getResources().getString(
			R.string.pref_sampling_summary)
			+ getResources().getString(R.string.pref_summary_current)
			+ String.valueOf(mSamplingValue.getText()));
	    }
	}
	if (key == null || key.equals("pitchTop")) {
	    if (Math.abs(toInt(mPitchTopValue.getText())) <= 180) {
		mPitchTopValue.setSummary(getResources().getString(
			R.string.pref_pitchTop_summary)
			+ getResources().getString(R.string.pref_summary_current)
			+ String.valueOf(mPitchTopValue.getText()));
	    } else {
		Toast.makeText(Settings.this, R.string.err_illegal_numeric,
			Toast.LENGTH_SHORT).show();
		mPitchTopValue.getEditor().putInt("pitchTop", 40);
		mPitchTopValue.getEditor().commit();
		mPitchTopValue.setSummary(getResources().getString(
			R.string.pref_pitchTop_summary)
			+ getResources().getString(R.string.pref_summary_current)
			+ String.valueOf(mPitchTopValue.getText()));
	    }
	}
	if (key == null || key.equals("pitchBottom")) {
	    if (Math.abs(toInt(mPitchBottomValue.getText())) <= 180) {
		mPitchBottomValue.setSummary(getResources().getString(
			R.string.pref_pitchBottom_summary)
			+ getResources().getString(R.string.pref_summary_current)
			+ String.valueOf(mPitchBottomValue.getText()));
	    } else {
		Toast.makeText(Settings.this, R.string.err_illegal_numeric,
			Toast.LENGTH_SHORT).show();
		mPitchBottomValue.getEditor().putInt("pitchBottom", 20);
		mPitchBottomValue.getEditor().commit();
		mPitchBottomValue.setSummary(getResources().getString(
			R.string.pref_pitchBottom_summary)
			+ getResources().getString(R.string.pref_summary_current)
			+ String.valueOf(mPitchBottomValue.getText()));
	    }
	}
	if (key == null || key.equals("rollTop")) {
	    if (Math.abs(toInt(mRollTopValue.getText())) <= 90) {
		mRollTopValue.setSummary(getResources().getString(
			R.string.pref_rollTop_summary)
			+ getResources().getString(R.string.pref_summary_current)
			+ String.valueOf(mRollTopValue.getText()));
	    } else {
		Toast.makeText(Settings.this, R.string.err_illegal_numeric,
			Toast.LENGTH_SHORT).show();
		mRollTopValue.getEditor().putInt("rollTop", 40);
		mRollTopValue.getEditor().commit();
		mRollTopValue.setSummary(getResources().getString(
			R.string.pref_rollTop_summary)
			+ getResources().getString(R.string.pref_summary_current)
			+ String.valueOf(mRollTopValue.getText()));
	    }
	}
	if (key == null || key.equals("rollBottom")) {
	    if (Math.abs(toInt(mRollBottomValue.getText())) <= 90) {
		mRollBottomValue.setSummary(getResources().getString(
			R.string.pref_rollBottom_summary)
			+ getResources().getString(R.string.pref_summary_current)
			+ String.valueOf(mRollBottomValue.getText()));
	    } else {
		Toast.makeText(Settings.this, R.string.err_illegal_numeric,
			Toast.LENGTH_SHORT).show();
		mRollBottomValue.getEditor().putInt("rollBottom", 20);
		mRollBottomValue.getEditor().commit();
		mRollBottomValue.setSummary(getResources().getString(
			R.string.pref_rollBottom_summary)
			+ getResources().getString(R.string.pref_summary_current)
			+ String.valueOf(mRollBottomValue.getText()));
	    }
	}
    }
}