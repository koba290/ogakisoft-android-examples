package ogakisoft.android.sample;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

public class SampleActivity extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.main);
	
	SeekBar seek1 = (SeekBar) findViewById(R.id.seek1);
	SeekBar seek2 = (SeekBar) findViewById(R.id.seek2);
	SeekBar seek3 = (SeekBar) findViewById(R.id.seek3);
	
	seek1.setMax(100);
	seek2.setMax(100);
	seek3.setMax(100);
	seek1.setProgress(50);
	seek2.setProgress(50);
	seek3.setProgress(50);
	
	seek2.setThumb(getResources().getDrawable(R.drawable.btn_circle));
	seek3.setThumb(getResources().getDrawable(R.drawable.stone));
    }
}