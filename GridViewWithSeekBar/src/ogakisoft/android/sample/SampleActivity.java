package ogakisoft.android.sample;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

public class SampleActivity extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);

	loadApps();
	setContentView(R.layout.main);

	final GridView grid = (GridView) findViewById(R.id.gridview);
	grid.setAdapter(new AppsAdapter());

	SeekBar seek = (SeekBar) findViewById(R.id.seekBar);
	seek.setMax(10);
	seek.setProgress(3);
	grid.setNumColumns(3);

	seek.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

	    @Override
	    public void onProgressChanged(SeekBar seekBar, int progress,
		    boolean fromUser) {
		grid.setNumColumns(progress);

		TextView textView = (TextView) findViewById(R.id.progress);
		textView.setText(String.valueOf(progress));
	    }

	    @Override
	    public void onStartTrackingTouch(SeekBar seekBar) {
	    }

	    @Override
	    public void onStopTrackingTouch(SeekBar seekBar) {
	    }
	});
    }

    private List<ResolveInfo> mApps;

    private void loadApps() {
	Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
	mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
	mApps = getPackageManager().queryIntentActivities(mainIntent, 0);
    }

    public class AppsAdapter extends BaseAdapter {
	public AppsAdapter() {
	}

	public View getView(int position, View convertView, ViewGroup parent) {
	    ImageView imageView;

	    if (convertView == null) {
		imageView = new ImageView(SampleActivity.this);
		imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
		// imageView.setLayoutParams(new GridView.LayoutParams(50, 50));
	    } else {
		imageView = (ImageView) convertView;
	    }
	    ResolveInfo info = mApps.get(position);
	    imageView.setImageDrawable(info.activityInfo
		    .loadIcon(getPackageManager()));

	    return imageView;
	}

	public final int getCount() {
	    return mApps.size();
	}

	public final Object getItem(int position) {
	    return mApps.get(position);
	}

	public final long getItemId(int position) {
	    return position;
	}
    }
}