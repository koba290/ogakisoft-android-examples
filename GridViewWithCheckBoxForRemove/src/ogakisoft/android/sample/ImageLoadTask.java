package ogakisoft.android.sample;

import java.io.IOException;
import java.io.InputStream;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.ParseException;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;

public class ImageLoadTask extends AsyncTask<String, Bitmap, Integer> {
    private final static String TAG = "ImageLoadTask";
    public static final int STATUS_FINISH = 0;
    public static final int STATUS_CANCELLED = 1;
    public static final int STATUS_NOT_LOADED = 2;
    public static final int STATUS_LOADING = 3;
    private static final int BITMAP_HEIGHT = 60;
    private static final int BITMAP_WIDTH = 50;
    private final SampleActivity.ImageAdapter mAdapter;
    private final HttpClient mClient;
    private Handler mHandler;
    private int mBitmapCount = -1;
    private int mFetchCount = 0;
    private int mResCount = 0;
    private int mErrCount = 0;
    private int mStatus;
    private Callback mCallback;

    public interface Callback {
	public void callback();
    }

    public ImageLoadTask(HttpClient client, Handler handler,
	    SampleActivity.ImageAdapter adapter, Callback callback) {
	mClient = client;
	mHandler = handler;
	mAdapter = adapter;
	mCallback = callback;
    }

    @Override
    protected Integer doInBackground(String... params) {
	if (isCancelled()) {
	    return STATUS_CANCELLED;
	}
	mStatus = STATUS_LOADING;
	mFetchCount = params.length;
	for (int i = 0; i < mFetchCount; i++) {
	    Log.v(TAG, "uri=" + params[i]);
	    mClient.fetch(params[i], mHandler, new HttpClient.HttpResponseListener() {
		@Override
		public void onResponseReceived(HttpResponse response) {
		    if (response == null) {
			Log.e(TAG, "no response");
			setErrResult();
		    } else if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
			try {
			    InputStream in = response.getEntity().getContent();
			    Bitmap bitmap = BitmapFactory.decodeStream(in);
			    publishProgress(bitmap);
			} catch (IllegalStateException e) {
			    Log.e(TAG, e.getMessage());
			    setErrResult();
			} catch (IOException e) {
			    Log.e(TAG, e.getMessage());
			    setErrResult();
			} catch (ParseException e) {
			    Log.e(TAG, e.getMessage());
			    setErrResult();
			}
		    } else {
			Log.e(TAG, response.getStatusLine().getStatusCode()
				+ " "
				+ response.getStatusLine().getReasonPhrase());
			setErrResult();
		    }
		}
	    });
	}
	return mStatus;
    }

    @Override
    protected void onProgressUpdate(Bitmap... values) {
	super.onProgressUpdate(values);
	if (values[0] != null) {
	    mBitmapCount++;
	    Bitmap b = Bitmap.createScaledBitmap(values[0], BITMAP_WIDTH,
		    BITMAP_HEIGHT, false);
	    mAdapter.addBitmap(mBitmapCount, b);
	    mAdapter.notifyDataSetChanged();
	}
	mResCount++;
	if ((mResCount + mErrCount) >= mFetchCount) {
	    Log.d(TAG, "onProgressUpdate; finish");
	    mStatus = STATUS_FINISH;
	    mCallback.callback();
	}
    }

    private void setErrResult() {
	mStatus = STATUS_NOT_LOADED;
	mErrCount++;
    }
}
