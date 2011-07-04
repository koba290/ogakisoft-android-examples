
package ogakisoft.android.example;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ZoomControls;

public class ZoomableActivity extends Activity {
    private static final String TAG = "ZoomableActivity";

    private float scale;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        final ZoomableView scrollview = (ZoomableView) findViewById(R.id.scrollView1);
        final ZoomControls zc = (ZoomControls) findViewById(R.id.zoom);
        scale = 1.0f;
        zc.setOnZoomInClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                scale += 0.1;
                Log.d(TAG, "zoomIn " + scale);
                scrollview.setScale((float) (scale));
                scrollview.invalidate();
            }
        });
        zc.setOnZoomOutClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if (scale > 1.0f) {
                    scale -= 0.1;
                    Log.d(TAG, "zoomOut " + scale);
                    scrollview.setScale(scale);
                    scrollview.invalidate();
                }
            }
        });
    }
}
