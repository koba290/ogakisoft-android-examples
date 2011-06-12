package ogakisoft.android.sample;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.os.Handler;

public class HttpClient {
    // private static final String TAG = "HttpClient";

    public interface HttpResponseListener {
	public void onResponseReceived(HttpResponse response);
    }

    public void fetch(String uri, final Handler handler,
	    final HttpResponseListener listener) {
	final HttpGet request = new HttpGet(uri);
	final DefaultHttpClient httpClient = new DefaultHttpClient();
	new Thread() {
	    public void run() {
		try {
		    final HttpResponse response;
		    if (request != null) {
			synchronized (httpClient) {
			    response = httpClient.execute(request);
			}
			handler.post(new Thread() {
			    public void run() {
				listener.onResponseReceived(response);
			    }
			});
		    }
		} catch (ClientProtocolException e) {
		    e.printStackTrace();
		} catch (IOException e) {
		    e.printStackTrace();
		}
	    }
	}.start();
    }
}
