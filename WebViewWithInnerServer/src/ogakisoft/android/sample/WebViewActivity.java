package ogakisoft.android.sample;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.TextView;

public class WebViewActivity extends Activity {
//    private final static String TAG = "WebViewActivity";
    private TextView textView;
    private WebView webView;
    private WebServer webServer;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.main);
	textView = (TextView) findViewById(R.id.url);
	webView = (WebView) findViewById(R.id.webview);
	webServer = new WebServer();
	webServer.onCreate();
    }
    
    @Override
    protected void onDestroy() {
	super.onDestroy();
	webServer.onDestroy();
    }


    public void onClickGoButton(View v) {
	webView.loadUrl(textView.getText().toString());
//	httpTest(url.getText().toString());
    }
//
//    private final HttpClient mClient = new HttpClient();
//    private Handler mHandler = new Handler();
//
//    private void httpTest(String url) {
//	mClient.fetch(url, mHandler, new HttpClient.HttpResponseListener() {
//	    @Override
//	    public void onResponseReceived(HttpResponse response) {
//		if (response == null) {
//		    Log.e(TAG, "no response");
//		} else if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
//		    Log.d(TAG, response.toString());
//		} else {
//		    Log.e(TAG, response.getStatusLine().getStatusCode() + " "
//			    + response.getStatusLine().getReasonPhrase());
//		}
//	    }
//	});
//    }
}