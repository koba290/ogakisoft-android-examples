package ogakisoft.android;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

abstract public class AbstractSurfaceView extends SurfaceView implements
	SurfaceHolder.Callback, Runnable {
    protected Thread mThread;
    protected int SLEEP_MILLISECONDS = 1000;

    public AbstractSurfaceView(Context context, AttributeSet attrs, int defStyle) {
	super(context, attrs, defStyle);
	getHolder().addCallback(this);
	setFocusable(true);
	requestFocus();
    }

    public AbstractSurfaceView(Context context, AttributeSet attrs) {
	this(context, attrs, 0);
    }

    public AbstractSurfaceView(Context context) {
	this(context, null);
    }

    @Override
    public void run() {
	Canvas canvas = null;
	SurfaceHolder surfaceHolder = getHolder();
	while (mThread != null) {
	    try {
		canvas = surfaceHolder.lockCanvas();
		if (canvas == null)
		    return;
		synchronized (surfaceHolder) {
		    draw(canvas);
		}
		Thread.sleep(SLEEP_MILLISECONDS);
	    } catch (InterruptedException e) {
	    } finally {
		if (canvas != null)
		    surfaceHolder.unlockCanvasAndPost(canvas);
	    }
	}
    }

    @Override
    abstract public void draw(Canvas canvas);

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
	    int height) {
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
	mThread = new Thread(this);
	mThread.start();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
	if (mThread != null) {
	    mThread = null;
	}
    }

}
