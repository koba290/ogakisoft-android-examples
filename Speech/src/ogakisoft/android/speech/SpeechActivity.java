package ogakisoft.android.speech;

import java.util.Locale;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

public class SpeechActivity extends Activity implements OnInitListener {
    private static final String TAG = "SpeechActivity";
    private static final int TTS_CHECK_REQUEST = 9999;
    private TextToSpeech mTts;
    private EditText text_kana;
    private TextView text_romaji;
    private SeekBar seek_pitch;
    private SeekBar seek_rate;
    private float pitch = 1.0f;
    private float rate = 1.0f;

    @Override
    public void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.main);
	text_kana = (EditText) findViewById(R.id.speechText);
	text_romaji = (TextView) findViewById(R.id.romajiText);
	seek_pitch = (SeekBar) findViewById(R.id.speechPitch);
	seek_pitch.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

	    @Override
	    public void onProgressChanged(SeekBar seekBar, int progress,
		    boolean fromUser) {
		pitch = progress / 5.0f;
	    }

	    @Override
	    public void onStartTrackingTouch(SeekBar seekBar) {
	    }

	    @Override
	    public void onStopTrackingTouch(SeekBar seekBar) {
	    }

	});
	pitch = seek_pitch.getProgress() / 5.0f;
	seek_rate = (SeekBar) findViewById(R.id.speechRate);
	seek_rate.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

	    @Override
	    public void onProgressChanged(SeekBar seekBar, int progress,
		    boolean fromUser) {
		rate = progress / 5.0f;
	    }

	    @Override
	    public void onStartTrackingTouch(SeekBar seekBar) {
	    }

	    @Override
	    public void onStopTrackingTouch(SeekBar seekBar) {
	    }

	});
	rate = seek_rate.getProgress() / 5.0f;
	
	Intent checkIntent = new Intent();
	checkIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
	startActivityForResult(checkIntent, TTS_CHECK_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	if (requestCode == TTS_CHECK_REQUEST) {
	    if (resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS) {
		// success, create the TTS instance
		mTts = new TextToSpeech(this, this);
		mTts.setLanguage(Locale.US);
	    } else {
		// missing data, install it
		Intent installIntent = new Intent();
		installIntent
			.setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
		startActivity(installIntent);
	    }
	}
    }

    @Override
    public void onInit(int status) {
    }

    public void onClickSpeech(View v) {
	if (mTts != null) {
	    Log.v(TAG, "pitch=" + pitch);
	    Log.v(TAG, "rate=" + rate);
	    if (mTts.setPitch(pitch) == TextToSpeech.ERROR) {
		Log.e(TAG, "tts setPitch error");
	    }
	    if (mTts.setSpeechRate(rate) == TextToSpeech.ERROR) {
		Log.e(TAG, "tts setSpeechRate error");
	    }
	    String romaji = KanaToRomaji.toRomaji(text_kana.getText().toString());
	    text_romaji.setText(romaji);
	    mTts.speak(romaji, TextToSpeech.QUEUE_ADD, null);
	}
    }

    @Override
    protected void onDestroy() {
	if (mTts != null) {
	    mTts.shutdown();
	}
	super.onDestroy();
    }
}
