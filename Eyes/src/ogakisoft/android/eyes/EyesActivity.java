package ogakisoft.android.eyes;

import android.app.Activity;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;

public class EyesActivity extends Activity {
    //private final static String TAG = "EyesActivity";

    @Override
    public void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setContentView(new EyesView(this));
    }

    static class Offset {
	static int left_x0 = 1;
	static int left_y0 = 2;
	static int left_x1 = 30; // 30-1=29
	static int left_y1 = 34; // 34-2=32
	static int right_x0 = 100 - 50; // 50
	static int right_y0 = 50 - 48; // 2
	static int right_x1 = 100 - 21; // 79-50=29
	static int right_y1 = 50 - 16; // 34-2=32
    }

    public class EyesView extends View {
	ShapeDrawable left_eye, right_eye;
	BitmapDrawable bitmap;
	int bitmap_width = 100;
	int bitmap_height = 50;
	int bitmap_x0 = 70; // 100;
	int bitmap_y0 = 150; // 150;
	int eye_radius = 8;
	int display_width = 240;
	int display_height = 160;
	float touchX = 0f, touchY = 0f;
	Eyes.Point mouse;
	Eyes eye_left, eye_right;
	int left_white_x0 = bitmap_x0 + Offset.left_x0;
	int left_white_y0 = bitmap_y0 + Offset.left_y0;
	int left_white_x1 = bitmap_x0 + Offset.left_x1;
	int left_white_y1 = bitmap_y0 + Offset.left_y1;
	int right_white_x0 = bitmap_x0 + Offset.right_x0;
	int right_white_y0 = bitmap_y0 + Offset.right_y0;
	int right_white_x1 = bitmap_x0 + Offset.right_x1;
	int right_white_y1 = bitmap_y0 + Offset.right_y1;

	private boolean mIsMoving = false;

	public EyesView(Context context) {
	    super(context);

	    left_eye = new ShapeDrawable(new OvalShape());
	    left_eye.getPaint().setColor(Color.BLUE);

	    right_eye = new ShapeDrawable(new OvalShape());
	    right_eye.getPaint().setColor(Color.BLUE);

	    bitmap = new BitmapDrawable(getResources(),
		    BitmapFactory.decodeResource(getResources(),
			    R.drawable.google));
	    bitmap_width = bitmap.getIntrinsicWidth();
	    bitmap_height = bitmap.getIntrinsicHeight();

	    eye_left = new Eyes(left_white_x1 - left_white_x0, left_white_y1
		    - left_white_y0);
	    eye_right = new Eyes(right_white_x1 - right_white_x0,
		    right_white_y1 - right_white_y0);
	}

	protected void onDraw(Canvas canvas) {
	    bitmap.setBounds(bitmap_x0, bitmap_y0, bitmap_x0 + bitmap_width,
		    bitmap_y0 + bitmap_height);
	    bitmap.draw(canvas);

	    Eyes.Point left = eye_left.computePupil(touchX, touchY);
	    Eyes.Point right = eye_right.computePupil(touchX, touchY);

	    int lx0 = left_white_x0 + left.x;
	    int ly0 = left_white_y0 + left.y;
	    int lx1 = left_white_x0 + (left.x + eye_radius);
	    int ly1 = left_white_y0 + (left.y + eye_radius);
	    int rx0 = right_white_x0 + right.x;
	    int ry0 = right_white_y0 + right.y;
	    int rx1 = right_white_x0 + (right.x + eye_radius);
	    int ry1 = right_white_y0 + (right.y + eye_radius);

	    left_eye.setBounds(lx0, ly0, lx1, ly1);
	    left_eye.draw(canvas);
	    right_eye.setBounds(rx0, ry0, rx1, ry1);
	    right_eye.draw(canvas);

	    //Log.v(TAG, "left=" + lx0 + "," + ly0 + "," + lx1 + "," + ly1);
	    //Log.v(TAG, "right=" + rx0 + "," + ry0 + "," + rx1 + "," + ry1);
	}

	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
	    super.onSizeChanged(w, h, oldw, oldh);
	    
	    display_width = w;
	    display_height = h;
	    left_white_x0 = bitmap_x0 + Offset.left_x0;
	    left_white_y0 = bitmap_y0 + Offset.left_y0;
	    left_white_x1 = bitmap_x0 + Offset.left_x1;
	    left_white_y1 = bitmap_y0 + Offset.left_y1;
	    right_white_x0 = bitmap_x0 + Offset.right_x0;
	    right_white_y0 = bitmap_y0 + Offset.right_y0;
	    right_white_x1 = bitmap_x0 + Offset.right_x1;
	    right_white_y1 = bitmap_y0 + Offset.right_y1;

	    eye_left = new Eyes(left_white_x1 - left_white_x0, left_white_y1
		    - left_white_y0);

	    eye_right = new Eyes(right_white_x1 - right_white_x0,
		    right_white_y1 - right_white_y0);
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent event) {
	    if (isEnabled()) {
		processEvent(event);
		super.dispatchTouchEvent(event);
		return true;
	    }
	    return super.dispatchTouchEvent(event);
	}

	private boolean processEvent(MotionEvent event) {
	    switch (event.getAction()) {
	    case MotionEvent.ACTION_DOWN:
		touchX = event.getX();
		touchY = event.getY();
		if (bitmap_x0 <= touchX && touchX <= (bitmap_x0 + bitmap_width)
			&& bitmap_y0 <= touchY
			&& touchY <= (bitmap_y0 + bitmap_height)) {
		    mIsMoving = true;
		    bitmap_x0 = (int) touchX;
		    bitmap_y0 = (int) touchY;
		    onSizeChanged(display_width, display_height, display_width,
			    display_height);
		}
		invalidate();
		return true;
	    case MotionEvent.ACTION_MOVE:
		touchX = event.getX();
		touchY = event.getY();
		if (mIsMoving) {
		    bitmap_x0 = (int) touchX;
		    bitmap_y0 = (int) touchY;
		    onSizeChanged(display_width, display_height, display_width,
			    display_height);
		}
		invalidate();
		return true;
	    case MotionEvent.ACTION_UP:
		if (mIsMoving) {
		    mIsMoving = false;
		    touchX = event.getX();
		    touchY = event.getY();
		    bitmap_x0 = (int) touchX;
		    bitmap_y0 = (int) touchY;
		    onSizeChanged(display_width, display_height, display_width,
			    display_height);
		    invalidate();
		    return true;
		}
		break;
	    case MotionEvent.ACTION_CANCEL:
		if (mIsMoving) {
		    return true;
		}
	    }
	    return false;
	}
    }

    public class Eyes {
	final double EYE_OFFSET = 0.1;
	final double EYE_THICK = 0.1;
	final double BALL_WIDTH = 0.8f;
	final double BALL_PAD = 0.01f;
	final double BALL_HEIGHT = BALL_WIDTH;
	final double W_MIN_X = (-1.0 + EYE_OFFSET);
	final double W_MAX_X = (1.0 + EYE_OFFSET);
	final double W_MIN_Y = (1.0 + EYE_OFFSET);
	final double W_MAX_Y = (-1.0 - EYE_OFFSET);
	double eye_width = (2.8 - (EYE_THICK + EYE_OFFSET) * 2);
	double eye_height = 2.4;
	double eye_hwidth = (eye_width / 2.0f);
	double eye_hheight = (eye_height / 2.0f);
	double ball_dist = ((eye_width - BALL_WIDTH) / 2.0 - BALL_PAD);

	class Transform {
	    double mx, bx;
	    double my, by;

	    Transform(int xx1, int xx2, int xy1, int xy2, double tx1,
		    double tx2, double ty1, double ty2) {
		this.mx = ((double) xx2 - xx1) / (tx2 - tx1);
		this.bx = ((double) xx1) - this.mx * tx1;
		this.my = ((double) xy2 - xy1) / (ty2 - ty1);
		this.by = ((double) xy1) - this.my * ty1;
	    }
	}

	Transform t;

	class Point {
	    int x;
	    int y;

	    void set(int x, int y) {
		this.x = x;
		this.y = y;
	    }
	}

	public Eyes(int w, int h) {
	    t = new Transform(0, w, h, 0, W_MIN_X, W_MAX_X, W_MIN_Y, W_MAX_Y);
	}

	public Point computePupil(double mouseX, double mouseY) {
	    double cx, cy;
	    double dist;
	    double angle;
	    double x, y;
	    double h;
	    double dx, dy;
	    double cosa, sina;
	    Point ret = new Point();

	    dx = mouseX;
	    dy = mouseY;
	    //Log.v(TAG, "dx=" + dx + ",dy=" + dy);

	    if (dx == 0 && dy == 0) {
		cx = 0;
		cy = 0;
	    } else {
		angle = Math.atan2((double) dy, (double) dx);
		cosa = Math.cos(angle);
		sina = Math.sin(angle);
		h = Math.hypot(eye_hheight * cosa, eye_hwidth * sina);
		x = (eye_hwidth * eye_hheight) * cosa / h;
		y = (eye_hwidth * eye_hheight) * sina / h;
		dist = ball_dist * Math.hypot(x, y);
		if (dist > Math.hypot((double) dx, (double) dy)) {
		    cx = dx;
		    cy = dy;
		} else {
		    cx = dist * cosa;
		    cy = dist * sina;
		}
	    }
	    //Log.v(TAG, "cx=" + cx + ",cy=" + cy);
	    ret.set(Xx(cx, cy), Xy(cx, cy));
	    //Log.v(TAG, "ret.x=" + ret.x + ",ret.y=" + ret.y);
	    return ret;
	}

	private int Xx(double x, double y) {
	    return ((int) (t.mx * (x) + t.bx));
	}

	private int Xy(double x, double y) {
	    return ((int) (t.my * (y) + t.by));
	}
    }
}
