package ogakisoft.android;

import android.app.Activity;
import android.os.Bundle;

public class AnalogClockActivity extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(new AnalogClockView(this));
    }
}