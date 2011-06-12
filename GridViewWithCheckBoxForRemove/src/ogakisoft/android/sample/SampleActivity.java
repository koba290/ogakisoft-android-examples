package ogakisoft.android.sample;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
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
    private static final String FETCH_URL = "http://www.google.co.jp/images"
	+"?hl=ja&source=imghp&biw=1210&bih=686"
	+"&q=android&gbv=2&aq=f&aqi=g10&aql=&oq=&gs_rfai=";
    protected static final int MAX_COUNT = 10;
    private GridView mGrid;
    private HttpClient mClient;
    private Handler mHandler;
    private ImageAdapter mAdapter;
    private HtmlLoadTask mTask1;
    private ImageLoadTask mTask2;
    private ProgressDialog mProgressDialog;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.main);
	mAdapter = new ImageAdapter();
	mClient = new HttpClient();
	mHandler = new Handler();
	mGrid = (GridView) findViewById(R.id.gridview);
	mGrid.setAdapter(mAdapter);

	mProgressDialog = new ProgressDialog(SampleActivity.this);
	mProgressDialog.setIndeterminate(true);
	mProgressDialog.setMessage(getResources()
		.getString(R.string.loading));
	mProgressDialog.show();
	
	loadHtml();
    }

    private void loadHtml() {
	if (mTask1 != null
		&& mTask1.getStatus() != HtmlLoadTask.Status.FINISHED) {
	    mTask1.cancel(true);
	}
	mTask1 = (HtmlLoadTask) new HtmlLoadTask(mClient, mHandler,
		new HtmlLoadTask.Callback() {
		    @Override
		    public void post(List<String> urls) {
			if (mTask2 != null
				&& mTask2.getStatus() != ImageLoadTask.Status.FINISHED) {
			    mTask2.cancel(true);
			}
			if (urls != null) {
			    int size = urls.size();
			    String[] array = new String[SampleActivity.MAX_COUNT];
			    for (int i = 0; i < size
				    && i < SampleActivity.MAX_COUNT; i++) {
				array[i] = urls.get(i).toString();
			    }
			    mTask2 = new ImageLoadTask(mClient, mHandler,
				    mAdapter, new ImageLoadTask.Callback() {
			        @Override
			        public void callback() {
				    if (mProgressDialog != null) {
					if (mProgressDialog.isShowing()) {
					    mProgressDialog.dismiss();
					}
				    }
			        }
			    });
			    mTask2.execute(array);
			}
		    }
		}).execute(FETCH_URL);
    }

    public void onClickRemoveButton(View v) {
	mAdapter.remove();
    }

    public class ImageAdapter extends BaseAdapter {
	private final List<Drawable> mList = Collections
		.synchronizedList(new ArrayList<Drawable>());
	private final List<Long> mCheck = Collections
		.synchronizedList(new ArrayList<Long>());

	public void addBitmap(int id, Bitmap bitmap) {
	    mList.add(id, new BitmapDrawable(bitmap));
	}

	public View getView(final int position, View convertView,
		ViewGroup parent) {
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
	    backImage = (ImageView) layout.findViewWithTag("i");
	    BitmapDrawable drawable = (BitmapDrawable) mList.get(position);
	    if (drawable != null) {
		backImage.setImageBitmap(drawable.getBitmap());
		backImage.setAdjustViewBounds(true);
	    }
	    frontImage = (ImageView) layout.findViewWithTag("c");
	    frontImage.setImageDrawable(getResources().getDrawable(
		    R.drawable.btn_check_off));
	    layout.setId(position);
	    layout.setOnClickListener(new OnClickListener() {
		public void onClick(View v) {
		    RelativeLayout layout = (RelativeLayout) v;
		    ImageView c = (ImageView) layout.findViewWithTag("c");
		    long id = (long) position;
		    if (mCheck.contains(id)) {
			c.setImageDrawable(getResources().getDrawable(
				R.drawable.btn_check_off));
			mCheck.remove(id);
		    } else {
			c.setImageDrawable(getResources().getDrawable(
				R.drawable.btn_check_on));
			mCheck.add(id);
		    }
		}
	    });
	    return layout;
	}

	public final int getCount() {
	    return mList.size();
	}

	public final Object getItem(int position) {
	    return mList.get(position);
	}

	public final long getItemId(int position) {
	    return position;
	}

	public void remove() {
	    int count = mCheck.size();
	    for (int i = 0; i < count; i++) {
		int key = mCheck.get(i).intValue();
		mList.remove(key);
		Log.d(TAG, "remove pos=" + key);
	    }
	    mCheck.clear();
	    this.notifyDataSetChanged();
	}

	public void clear() {
	    mList.clear();
	    mCheck.clear();
	    this.notifyDataSetChanged();
	}
    }

    @Override
    protected void onDestroy() {
	super.onDestroy();
	if (mTask1 != null
		&& mTask1.getStatus() != HtmlLoadTask.Status.FINISHED) {
	    mTask1.cancel(true);
	    mTask1 = null;
	}
	if (mTask2 != null
		&& mTask2.getStatus() != ImageLoadTask.Status.FINISHED) {
	    mTask2.cancel(true);
	    mTask2 = null;
	}
    }

}