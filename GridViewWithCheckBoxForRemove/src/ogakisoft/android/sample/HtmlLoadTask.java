package ogakisoft.android.sample;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.ParseException;
import org.apache.http.util.EntityUtils;
import org.xml.sax.XMLReader;

import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Handler;
import android.text.Editable;
import android.text.Html;
import android.text.Html.ImageGetter;
import android.text.Html.TagHandler;
import android.util.Log;

public class HtmlLoadTask extends AsyncTask<String, Void, Integer> {
    private final static String TAG = "HtmlLoadTask";
    public static final int STATUS_SUCCESS = 0;
    public static final int STATUS_CANCELLED = 1;
    public static final int STATUS_NOT_LOADED = 2;
    private int mStatus;
    private final HttpClient mClient;
    private List<String> urls;
    private Callback mCallback;
    private Handler mHandler;
    
    public interface Callback {
	public void post(List<String> urls);
    }
    
    public HtmlLoadTask(HttpClient client, Handler handler, Callback callback) {
	mClient = client;
	urls = new ArrayList<String>();
	mHandler = handler;
	mCallback = callback;
    }

    @Override
    protected Integer doInBackground(String... params) {
	if (isCancelled()) {
	    return STATUS_CANCELLED;
	}
	mStatus = STATUS_SUCCESS;
	int count = params.length;
	for (int i = 0; i < count; i++) {
	    Log.v(TAG, "uri=" + params[i]);
	    mClient.fetch(params[i], mHandler, new HttpClient.HttpResponseListener() {
		@Override
		public void onResponseReceived(HttpResponse response) {
		    if (response == null) {
			Log.e(TAG, "no response");
			setResult(STATUS_NOT_LOADED);
		    } else if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
			try {
			    final HttpResponse res = response;
			    String html = null;
			    try {
				html = EntityUtils.toString(res.getEntity())
					.toString();
			    } catch (ParseException e) {
				e.printStackTrace();
			    } catch (IOException e) {
				e.printStackTrace();
			    }
			    if (html == null)
				return;
			    Html.fromHtml(html, new ImageGetter() {
				@Override
				public Drawable getDrawable(String source) {
				    int start = -1;
				    if ((start = source.lastIndexOf("http://")) >= 0) {
					urls.add(source.substring(start));
					Log.v(TAG, "ImageGetter:" + source);
				    }
				    return null;
				}
			    }, new TagHandler() {
				@Override
				public void handleTag(boolean opening,
					String tag, Editable output,
					XMLReader xmlReader) {
				}
			    });
			} catch (ParseException e) {
			    Log.e(TAG, e.getMessage());
			    setResult(STATUS_NOT_LOADED);
			}

			mCallback.post(urls);

		    } else {
			Log.e(TAG, response.getStatusLine().getStatusCode()
				+ " "
				+ response.getStatusLine().getReasonPhrase());
			setResult(STATUS_NOT_LOADED);
		    }
		}
	    });
	}
	return mStatus;
    }

    private void setResult(int status) {
	mStatus = status;
    }
}
