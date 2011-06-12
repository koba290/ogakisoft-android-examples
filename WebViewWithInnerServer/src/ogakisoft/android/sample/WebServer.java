package ogakisoft.android.sample;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class WebServer extends Service implements Runnable {
    private final static int PORT = 8080;
    private final static String TAG = "WebServer";
    private final int SLEEP_MILLISECONDS = 1000;
    private Thread mThread;
    private ServerSocket mSocket;

    @Override
    public IBinder onBind(Intent intent) {
	return null;
    }

    @Override
    public void onCreate() {
	super.onCreate();
	try {
	    mSocket = new ServerSocket(PORT);
	    Log.d(TAG, "ServerSocket:" + mSocket.toString());
	} catch (Exception e) {
	    e.printStackTrace();
	}
	mThread = new Thread(this);
	mThread.start();
    }

    @Override
    public void onDestroy() {
	super.onDestroy();
	try {
	    mSocket.close();
	} catch (IOException e) {
	    e.printStackTrace();
	}
	if (mThread != null) {
	    mThread = null;
	}
    }

    @Override
    public void run() {
	while (mThread != null) {
	    try {
		Socket client = mSocket.accept();
		Log.d(TAG, "Socket:" + client.toString());
		synchronized (client) {
		    for (;;) {
			BufferedReader in = new BufferedReader(
				new InputStreamReader(client.getInputStream()));
			PrintWriter out = new PrintWriter(
				client.getOutputStream());
			out.print("HTTP/1.1 200 \r\n");
			out.print("Content-Type: text/plain\r\n");
			out.print("Connection: close\r\n");
			out.print("\r\n");

			String line;
			while ((line = in.readLine()) != null) {
			    if (line.length() == 0)
				break;
			    out.print(line + "\r\n");
			}
			out.close();
			in.close();
			client.close();
			Thread.sleep(SLEEP_MILLISECONDS);
		    }
		}
	    } catch (Exception e) {
	    } finally {
	    }
	}
    }
}
