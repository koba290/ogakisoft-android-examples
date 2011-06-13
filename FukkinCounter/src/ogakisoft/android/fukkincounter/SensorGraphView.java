package ogakisoft.android.fukkincounter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.util.AttributeSet;
import android.view.View;

public class SensorGraphView extends View implements SensorEventListener {
    private Bitmap mBitmap;
    private Paint mPaint = new Paint();
    private Canvas mCanvas = new Canvas();
    private float mLastValues[] = new float[3 * 2];
    private int mColors[] = new int[3];
    private float mLastX;
    private float mScale;
    private float mYOffset;
    private float mMaxX;
    private float mSpeed = 2.0f;
    private float mWidth;
    private float mHeight;

    public SensorGraphView(Context context) {
	super(context);
	init();
    }

    public SensorGraphView(Context context, AttributeSet attrs) {
	super(context, attrs);
	init();
    }

    public SensorGraphView(Context context, AttributeSet attrs, int defStyle) {
	super(context, attrs, defStyle);
	init();
    }

    private void init() {
	mColors[0] = Color.BLUE;
	mColors[1] = Color.RED;
	mColors[2] = Color.BLACK;
	mPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
	mBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.RGB_565);
	mCanvas.setBitmap(mBitmap);
	mCanvas.drawColor(0xFFFFFFFF);
	mYOffset = h * 0.5f;
	mScale = -(h * 0.5f * (1.0f / 270.0f));
	mWidth = w;
	mHeight = h;
	if (mWidth < mHeight) {
	    mMaxX = w;
	} else {
	    mMaxX = w; // - 50;
	}
	mLastX = mMaxX;
	super.onSizeChanged(w, h, oldw, oldh);
    }

    @Override
    protected void onDraw(Canvas canvas) {
	synchronized (this) {
	    if (mBitmap != null) {
		final Paint paint = mPaint;
		if (mLastX >= mMaxX) {
		    mLastX = 0;
		    final Canvas _canvas = mCanvas;
		    final float yoffset = mYOffset;
		    final float maxx = mMaxX;
		    paint.setColor(0xFFAAAAAA);
		    _canvas.drawColor(0xFFFFFFFF);
		    _canvas.drawLine(0, yoffset, maxx, yoffset, paint);
		    paint.setColor(Color.GRAY);
		    for (int x = 0; x < maxx; x += 6) {
			_canvas.drawLine(x, yoffset * 0.5f, x + 3,
				yoffset * 0.5f, paint);
			_canvas.drawLine(x, yoffset * 1.5f, x + 3,
				yoffset * 1.5f, paint);
		    }
		}
		canvas.drawBitmap(mBitmap, 0, 0, null);
	    }
	}
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
	synchronized (this) {
	    if (mBitmap != null) {
		final Canvas canvas = mCanvas;
		final Paint paint = mPaint;
		if (event.sensor.getType() == Sensor.TYPE_ORIENTATION) {
		    // if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
		    float deltaX = mSpeed;
		    float newX = mLastX + deltaX;
		    for (int i = 0; i < 3; i++) {
			final float v = mYOffset + event.values[i] * mScale;
			paint.setColor(mColors[i]);
			canvas.drawLine(mLastX, mLastValues[i], newX, v, paint);
			mLastValues[i] = v;
		    }
		    mLastX += mSpeed;
		}
		invalidate();
	    }
	}
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }
}
