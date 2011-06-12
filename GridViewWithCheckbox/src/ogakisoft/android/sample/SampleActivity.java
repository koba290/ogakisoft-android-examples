package ogakisoft.android.sample;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class SampleActivity extends Activity {
    private static final String TAG = "SampleActivity";
    private AppsAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	loadApps();
	setContentView(R.layout.main);
	GridView grid = (GridView) findViewById(R.id.gridview);
	adapter = new AppsAdapter();
	grid.setAdapter(adapter);
    }

    private List<ResolveInfo> mApps;

    private void loadApps() {
	Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
	mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
	mApps = getPackageManager().queryIntentActivities(mainIntent, 0);
    }

    public void onClickDoneButton(View v) {
	if (adapter != null) {
	    List<ResolveInfo> list = adapter.getCheckOn();
	    for (ResolveInfo info : list) {
		Log.i(TAG, info.activityInfo.name);
	    }
	}
    }

    public class AppsAdapter extends BaseAdapter {
	private boolean[] checks;

	public AppsAdapter() {
	    checks = new boolean[mApps.size()];
	    for (@SuppressWarnings("unused")
	    boolean c : checks) {
		c = false;
	    }
	}

	public View getView(int position, View convertView, ViewGroup parent) {
	    ImageView backImage, frontImage;
	    RelativeLayout layout;

	    if (convertView == null) {
		frontImage = new ImageView(SampleActivity.this);
		frontImage.setTag("c");
		backImage = new ImageView(SampleActivity.this);
		backImage.setTag("i");
		layout = new RelativeLayout(SampleActivity.this);
		layout.addView(backImage);
		layout.addView(frontImage);
	    } else {
		layout = (RelativeLayout) convertView;
	    }
	    ResolveInfo info = mApps.get(position);
	    backImage = (ImageView) layout.findViewWithTag("i");
	    backImage.setImageDrawable(info.activityInfo
		    .loadIcon(getPackageManager()));
	    frontImage = (ImageView) layout.findViewWithTag("c");
	    frontImage.setImageDrawable(getResources().getDrawable(
		    R.drawable.btn_check_off));
	    layout.setId(position);
	    layout.setOnClickListener(new OnClickListener() {
		public void onClick(View v) {
		    RelativeLayout layout = (RelativeLayout) v;
		    int id = layout.getId();
		    ImageView c = (ImageView) layout.findViewWithTag("c");
		    if (checks[id]) {
			c.setImageDrawable(getResources().getDrawable(
				R.drawable.btn_check_off));
			checks[id] = false;
		    } else {
			c.setImageDrawable(getResources().getDrawable(
				R.drawable.btn_check_on));
			checks[id] = true;
		    }
		}
	    });
	    return layout;
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

	public List<ResolveInfo> getCheckOn() {
	    ArrayList<ResolveInfo> list = new ArrayList<ResolveInfo>(0);
	    int count = mApps.size();
	    for (int i = 0; i < count; i++) {
		if (checks[i] == true)
		    list.add(mApps.get(i));
	    }
	    return list;
	}
    }
}