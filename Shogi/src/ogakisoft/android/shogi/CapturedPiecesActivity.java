package ogakisoft.android.shogi;

import java.util.List;

import ogakisoft.android.shogi.ShogiActivity.Koma;
import ogakisoft.android.shogi.ShogiActivity.KomaImage;
import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;

public class CapturedPiecesActivity extends Activity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		MochigomaView view = new MochigomaView(this);
		setContentView(view);
	}

	public class MochigomaView extends View {
		private static final int COLS = 5;
		private static final int ROWS = 8;
		private int koma_width;
		private int koma_height;
		private Paint mPaint;
		private Rect canvas_rect;
		private Drawable ban;
		private int[] posX = new int[COLS];
		private int[] posY = new int[ROWS];

		public MochigomaView(Context context) {
			super(context);
			final Resources r = getResources();
			ban = r.getDrawable(R.drawable.shogi_board);
		}

		@Override
		protected void onSizeChanged(int w, int h, int oldw, int oldh) {
			super.onSizeChanged(w, h, oldw, oldh);
			koma_width = w / COLS;
			koma_height = h / ROWS;
		}

		@Override
		protected void onDraw(Canvas canvas) {
			final Paint paint = mPaint;
			canvas_rect = canvas.getClipBounds();
			ban.setBounds(canvas_rect);
			ban.draw(canvas);

			int count = 0;
			final int right_end = koma_width * COLS;
			for (int x = canvas_rect.left; x < right_end; x += koma_width) {
				canvas.drawLine(x, canvas_rect.top, x, canvas_rect.bottom,
						paint);
				if (count < COLS) {
					posX[count] = x;
				}
				count++;
			}
			count = 0;
			final int bottom_end = koma_height * ROWS;
			for (int y = canvas_rect.top; y < bottom_end; y += koma_height) {
				canvas.drawLine(canvas_rect.left, y, canvas_rect.right, y,
						paint);
				if (count < ROWS) {
					posY[count] = y;
				}
				count++;
			}
			Rect koma_rect = null;
			Koma koma;
			Bitmap bitmap;
			List<Koma> list = ((ShogiActivity) getParent()).getCapturedPieces();
			KomaImage image = ((ShogiActivity) getParent()).getKomaImage();
			int you = 0;
			int me = 0;
			int x = 0;
			int y = 0;
			count = list.size();
			for (int i = 0; i < count; i++) {
				koma = list.get(i);
				if (koma.me == ShogiActivity.ORIENTATION_YOU) {
					x = you / COLS;
					y = you / ROWS;
					you++;
				} else {
					x = COLS - (me / COLS);
					y = ROWS - (me / ROWS);
					me++;
				}
				koma_rect = new Rect(posX[x], posY[y],
						posX[x] + koma_width, posY[y] + koma_height);
				bitmap = koma.getBitmap(image);
				if (null != bitmap) {
					canvas.drawBitmap(bitmap, null, koma_rect, null);
				}
			}
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
			case MotionEvent.ACTION_MOVE:
			case MotionEvent.ACTION_UP:
			case MotionEvent.ACTION_CANCEL:
				break;
			}
			return false;
		}
	}
}