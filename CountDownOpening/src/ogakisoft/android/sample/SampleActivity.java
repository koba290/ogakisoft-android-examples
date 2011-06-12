package ogakisoft.android.sample;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.widget.TextView;

public class SampleActivity extends Activity {
    private TextView text;
    private Handler handler;
    private int count = 10;

    @Override
    public void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
	setContentView(R.layout.main);
	setProgressBarVisibility(true);
	text = (TextView) findViewById(R.id.counter);
	handler = new Handler();
	new Thread(new Runnable() {
	    public void run() {
		while (count > 0) {
		    handler.post(new Runnable() {
			public void run() {
			    text.setText(String.valueOf(count));
			    count--;
			}
		    });
		    try {
			Thread.sleep(1000);
		    } catch (InterruptedException e) {
		    }
		}
		finish();
	    }
	}).start();
    }
}
