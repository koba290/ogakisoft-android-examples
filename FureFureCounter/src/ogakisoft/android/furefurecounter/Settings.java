package ogakisoft.android.furefurecounter;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.widget.Toast;

public class Settings extends PreferenceActivity implements OnSharedPreferenceChangeListener {
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
	CheckBoxPreference mTapEnabled = (CheckBoxPreference) findPreference("tapEnabled");
	CheckBoxPreference mShakeEnabled = (CheckBoxPreference) findPreference("shakeEnabled");
	ListPreference mCountMode = (ListPreference) findPreference("countIncDec");
	EditDigitPreference mInitValue = (EditDigitPreference) findPreference("countInitValue");
	EditDigitPreference mPitchBottomValue = (EditDigitPreference) findPreference("pitchBottom");
	EditDigitPreference mPitchTopValue = (EditDigitPreference) findPreference("pitchTop");

	if (preference == mSoundEnabled) {
	    // mSoundEnabled
	    // .setSummary(Boolean.toString(mSoundEnabled.isChecked()));
	} else if (preference == mTapEnabled) {
	    if (mTapEnabled.isChecked() == false
		    && mShakeEnabled.isChecked() == false) {
		mTapEnabled.setChecked(true);
	    }
	} else if (preference == mShakeEnabled) {
	    if (mTapEnabled.isChecked() == false
		    && mShakeEnabled.isChecked() == false) {
		mShakeEnabled.setChecked(true);
	    }
	} else if (preference == mCountMode) {
	    String value = "";
	    if (mCountMode.getValue().equals("INC")) {
		value = getResources().getString(
			R.string.pref_countIncDec_IncValue);
	    } else {
		value = getResources().getString(
			R.string.pref_countIncDec_DecValue);
	    }
	    mCountMode.setSummary(getResources().getString(
		    R.string.pref_countInitValue_summary)
		    + value);
	} else if (preference == mInitValue) {
	    if (isNumeric(mInitValue.getText())) {
		mInitValue.setSummary(getResources().getString(
			R.string.pref_countInitValue_summary)
			+ String.valueOf(mInitValue.getText()));
	    } else {
		Toast.makeText(Settings.this, R.string.err_illegal_numeric,
			Toast.LENGTH_SHORT).show();
		mInitValue.getEditor().clear();
		mInitValue.getEditor().commit();
	    }
	} else if (preference == mPitchBottomValue) {
	    if (isNumeric(mPitchBottomValue.getText())) {
		mPitchBottomValue.setSummary(getResources().getString(
			R.string.pref_pitchBottom_summary)
			+ String.valueOf(mPitchBottomValue.getText()));
	    } else {
		Toast.makeText(Settings.this, R.string.err_illegal_numeric,
			Toast.LENGTH_SHORT).show();
		mPitchBottomValue.getEditor().clear();
		mPitchBottomValue.getEditor().commit();
	    }
	} else if (preference == mPitchTopValue) {
	    if (isNumeric(mPitchTopValue.getText())) {
		mPitchTopValue.setSummary(getResources().getString(
			R.string.pref_pitchTop_summary)
			+ String.valueOf(mPitchTopValue.getText()));
	    } else {
		Toast.makeText(Settings.this, R.string.err_illegal_numeric,
			Toast.LENGTH_SHORT).show();
		mPitchTopValue.getEditor().clear();
		mPitchTopValue.getEditor().commit();
	    }
	}
	return result;
    }

    private static boolean isNumeric(String str) {
	Pattern p = Pattern.compile("[0-9]+");
	Matcher m = p.matcher(str);
	return m.matches();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
	    String key) {
	CheckBoxPreference mSoundEnabled = (CheckBoxPreference) findPreference("soundEnabled");
	CheckBoxPreference mTapEnabled = (CheckBoxPreference) findPreference("tapEnabled");
	CheckBoxPreference mShakeEnabled = (CheckBoxPreference) findPreference("shakeEnabled");
	ListPreference mCountMode = (ListPreference) findPreference("countIncDec");
	EditDigitPreference mInitValue = (EditDigitPreference) findPreference("countInitValue");
	EditDigitPreference mPitchBottomValue = (EditDigitPreference) findPreference("pitchBottom");
	EditDigitPreference mPitchTopValue = (EditDigitPreference) findPreference("pitchTop");

	// Log.i(TAG, "onSharedPreferenceChanged: "+key);

	if (key == null || key.equals("soundEnabled")) {
	    if (sharedPreferences.getBoolean("soundEnabled", true)) {
		mSoundEnabled.setChecked(true);
	    } else {
		mSoundEnabled.setChecked(false);
	    }
	}
	if (key == null || key.equals("tapEnabled")) {
	    if (sharedPreferences.getBoolean("tapEnabled", true)) {
		mTapEnabled.setChecked(true);
	    } else {
		mTapEnabled.setChecked(false);
	    }
	}
	if (key == null || key.equals("shakeEnabled")) {
	    if (sharedPreferences.getBoolean("shakeEnabled", true)) {
		mShakeEnabled.setChecked(true);
	    } else {
		mShakeEnabled.setChecked(false);
	    }
	}
	if (key == null || key.equals("countIncDec")) {
	    String value = "";
	    if (mCountMode.getValue().equals("INC")) {
		value = getResources().getString(
			R.string.pref_countIncDec_IncValue);
	    } else {
		value = getResources().getString(
			R.string.pref_countIncDec_DecValue);
	    }
	    mCountMode.setSummary(getResources().getString(
		    R.string.pref_countInitValue_summary)
		    + value);
	}
	if (key == null || key.equals("countInitValue")) {
	    if (isNumeric(mInitValue.getText())) {
		mInitValue.setSummary(getResources().getString(
			R.string.pref_countInitValue_summary)
			+ String.valueOf(mInitValue.getText()));
	    } else {
		Toast.makeText(Settings.this, R.string.err_illegal_numeric,
			Toast.LENGTH_SHORT).show();
		mInitValue.getEditor().putInt("countInitValue", 0);
		mInitValue.getEditor().commit();
		mInitValue.setSummary(getResources().getString(
			R.string.pref_countInitValue_summary)
			+ String.valueOf(mInitValue.getText()));
	    }
	}
	if (key == null || key.equals("pitchBottom")) {
	    if (isNumeric(mPitchBottomValue.getText())) {
		mPitchBottomValue.setSummary(getResources().getString(
			R.string.pref_pitchBottom_summary)
			+ String.valueOf(mPitchBottomValue.getText()));
	    } else {
		Toast.makeText(Settings.this, R.string.err_illegal_numeric,
			Toast.LENGTH_SHORT).show();
		mPitchBottomValue.getEditor().putInt("pitchBottom", 10);
		mPitchBottomValue.getEditor().commit();
		mPitchBottomValue.setSummary(getResources().getString(
			R.string.pref_pitchBottom_summary)
			+ String.valueOf(mPitchBottomValue.getText()));
	    }
	}
	if (key == null || key.equals("pitchTop")) {
	    if (isNumeric(mPitchTopValue.getText())) {
		mPitchTopValue.setSummary(getResources().getString(
			R.string.pref_pitchTop_summary)
			+ String.valueOf(mPitchTopValue.getText()));
	    } else {
		Toast.makeText(Settings.this, R.string.err_illegal_numeric,
			Toast.LENGTH_SHORT).show();
		mPitchTopValue.getEditor().putInt("pitchTop", 60);
		mPitchTopValue.getEditor().commit();
		mPitchTopValue.setSummary(getResources().getString(
			R.string.pref_pitchTop_summary)
			+ String.valueOf(mPitchTopValue.getText()));
	    }
	}
    }
}