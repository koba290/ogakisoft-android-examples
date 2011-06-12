package ogakisoft.android.shogi;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;

public class ShogiActivity extends Activity {
	public class Koma {
		int kind;
		int me;
		int status;

		public Koma(int kind, int me) {
			this.kind = kind;
			this.me = me;
			if (me == ORIENTATION_ME)
				status = STATUS_NORMAL;
			else
				status = STATUS_ROTATE;
		}

		public Bitmap getBitmap(KomaImage image) {
			return image.getBitmap(kind, status);
		}
	}

	private class KomaBoard {
		private Koma[][] board;
		private Koma[][] start_board = {
				{ Kyo11, Kei11, Gin11, Kin11, Gyoku1, Kin12, Gin12, Kei12,
						Kyo12 },
				{ None, Hi1, None, None, None, None, None, Kaku1, None },
				{ Fu11, Fu12, Fu13, Fu14, Fu15, Fu16, Fu17, Fu18, Fu19 },
				{ None, None, None, None, None, None, None, None, None },
				{ None, None, None, None, None, None, None, None, None },
				{ None, None, None, None, None, None, None, None, None },
				{ Fu01, Fu02, Fu03, Fu04, Fu05, Fu06, Fu07, Fu08, Fu09 },
				{ None, Kaku0, None, None, None, None, None, Hi0, None },
				{ Kyo01, Kei01, Gin01, Kin01, Gyoku0, Kin02, Gin02, Kei02,
						Kyo02 } };
		private List<Koma> stock;
		private List<KomaHistory> history;

		public KomaBoard() {
			board = new Koma[ROWS][COLS];
			stock = new ArrayList<Koma>();
			history = new ArrayList<KomaHistory>();

			for (int y = 0; y < ROWS; y++) {
				for (int x = 0; x < COLS; x++) {
					board[y][x] = start_board[y][x];
				}
			}
		}

		public Koma getKoma(int x, int y) {
			return board[y][x];
		}

		public void moveKoma(Koma koma, int from_x, int from_y, int to_x,
				int to_y) {
			Koma temp = board[to_y][to_x];
			if (temp.kind == KOMA_NONE
					&& isMovable(koma, from_x, from_y, to_x, to_y)) {
				// Log.d(TAG,
				// "moveKoma: fx="+from_x+",fy="+from_y+",tx="+to_x+",ty="+to_y);
				board[to_y][to_x] = koma; // .set(to_y * ROWS + to_x, koma);
				board[from_y][from_x] = None; // ].set(from_y * ROWS + from_x,
												// temp);
				history.add(new KomaHistory(koma, from_x, from_y, to_x, to_y));
			} else {
				if (koma.me != temp.me
						&& isMovable(koma, from_x, from_y, to_x, to_y)) {
					// take-away
					switch (temp.me) {
					case ORIENTATION_ME:
						temp.me = ORIENTATION_YOU;
						break;
					case ORIENTATION_YOU:
						temp.me = ORIENTATION_ME;
						break;
					}
					switch (temp.status) {
					case STATUS_NORMAL:
						temp.status = STATUS_ROTATE;
						break;
					case STATUS_REVERSE:
						temp.status = STATUS_ROTATE;
						break;
					case STATUS_ROTATE:
						temp.status = STATUS_NORMAL;
						break;
					case STATUS_ROTATE_REVERSE:
						temp.status = STATUS_NORMAL;
						break;
					}
					board[to_y][to_x] = koma; // .set(to_y * ROWS + to_x, koma);
					board[from_y][from_x] = None; // .set(from_y * ROWS +
													// from_x, None);
					stock.add(temp);
					history.add(new KomaHistory(koma, from_x, from_y, to_x,
							to_y));
				}
			}
		}

		private boolean isMovable(Koma koma, int from_x, int from_y, int to_x,
				int to_y) {
			switch (koma.kind) {
			case KOMA_FU:
				switch (koma.status) {
				case STATUS_NORMAL:
				case STATUS_ROTATE:
					if (isForwardOne(koma, from_x, from_y, to_x, to_y)) {
						return true;
					}
					return false;
				case STATUS_REVERSE:
				case STATUS_ROTATE_REVERSE:
					if (isForwardDiagonally(koma, from_x, from_y, to_x, to_y)) {
						return true;
					}
					if (isSideOrBack(koma, from_x, from_y, to_x, to_y)) {
						return true;
					}
					return false;
				}
			case KOMA_HI:
				switch (koma.status) {
				case STATUS_NORMAL:
				case STATUS_ROTATE:
					if (isVertical(koma, from_x, from_y, to_x, to_y)) {
						return true;
					}
					if (isHorizontal(koma, from_x, from_y, to_x, to_y)) {
						return true;
					}
					return false;
				case STATUS_REVERSE:
				case STATUS_ROTATE_REVERSE:
					if (isVertical(koma, from_x, from_y, to_x, to_y)) {
						return true;
					}
					if (isHorizontal(koma, from_x, from_y, to_x, to_y)) {
						return true;
					}
					if (isForwardDiagonally(koma, from_x, from_y, to_x, to_y)) {
						return true;
					}
					if (isBackwardDiagonally(koma, from_x, from_y, to_x, to_y)) {
						return true;
					}
					return false;
				}
				break;
			case KOMA_KAKU:
				switch (koma.status) {
				case STATUS_NORMAL:
				case STATUS_ROTATE:
					if (isDiagonally(koma, from_x, from_y, to_x, to_y)) {
						return true;
					}
					return false;
				case STATUS_REVERSE:
				case STATUS_ROTATE_REVERSE:
					if (isDiagonally(koma, from_x, from_y, to_x, to_y)) {
						return true;
					}
					if (isForwardDiagonally(koma, from_x, from_y, to_x, to_y)) {
						return true;
					}
					if (isSideOrBack(koma, from_x, from_y, to_x, to_y)) {
						return true;
					}
					return false;
				}
				break;
			case KOMA_KYO:
				switch (koma.status) {
				case STATUS_NORMAL:
				case STATUS_ROTATE:
					if (isForward(koma, from_x, from_y, to_x, to_y)) {
						return true;
					}
					return false;
				case STATUS_REVERSE:
				case STATUS_ROTATE_REVERSE:
					if (isForwardDiagonally(koma, from_x, from_y, to_x, to_y)) {
						return true;
					}
					if (isSideOrBack(koma, from_x, from_y, to_x, to_y)) {
						return true;
					}
					return false;
				}
				break;
			case KOMA_KEI:
				switch (koma.status) {
				case STATUS_NORMAL:
				case STATUS_ROTATE:
					if (isTwoVerticallyOneHorizontally(koma, from_x, from_y,
							to_x, to_y)) {
						return true;
					}
					return false;
				case STATUS_REVERSE:
				case STATUS_ROTATE_REVERSE:
					if (isForwardDiagonally(koma, from_x, from_y, to_x, to_y)) {
						return true;
					}
					if (isSideOrBack(koma, from_x, from_y, to_x, to_y)) {
						return true;
					}
					return false;
				}
				break;
			case KOMA_GIN:
				switch (koma.status) {
				case STATUS_NORMAL:
				case STATUS_ROTATE:
					if (isForwardDiagonally(koma, from_x, from_y, to_x, to_y)) {
						return true;
					}
					if (isBackwardDiagonally(koma, from_x, from_y, to_x, to_y)) {
						return true;
					}
					return false;
				case STATUS_REVERSE:
				case STATUS_ROTATE_REVERSE:
					if (isForwardDiagonally(koma, from_x, from_y, to_x, to_y)) {
						return true;
					}
					if (isSideOrBack(koma, from_x, from_y, to_x, to_y)) {
						return true;
					}
					return false;
				}
				break;
			case KOMA_KIN:
				switch (koma.status) {
				case STATUS_NORMAL:
				case STATUS_ROTATE:
					if (isForwardDiagonally(koma, from_x, from_y, to_x, to_y)) {
						return true;
					}
					if (isSideOrBack(koma, from_x, from_y, to_x, to_y)) {
						return true;
					}
					return false;
				case STATUS_REVERSE:
				case STATUS_ROTATE_REVERSE:
					// nothing this state
					return false;
				}
				break;
			case KOMA_GYOKU:
				if (isForwardDiagonally(koma, from_x, from_y, to_x, to_y)) {
					return true;
				}
				if (isSideOrBack(koma, from_x, from_y, to_x, to_y)) {
					return true;
				}
				if (isBackwardDiagonally(koma, from_x, from_y, to_x, to_y)) {
					return true;
				}
				return false;
			}
			return false;
		}

		private boolean isForwardOne(Koma koma, int from_x, int from_y,
				int to_x, int to_y) {
			Koma temp;
			int y;
			if (from_x == to_x) {
				temp = board[to_y][to_x];
				switch (koma.status) {
				case STATUS_NORMAL:
				case STATUS_REVERSE:
					y = from_y - to_y;
					if (y == 1) {
						if (temp.kind == KOMA_NONE || temp.me != koma.me)
							return true;
					}
					break;
				case STATUS_ROTATE:
				case STATUS_ROTATE_REVERSE:
					y = to_y - from_y;
					if (y == 1) {
						if (temp.kind == KOMA_NONE || temp.me != koma.me)
							return true;
					}
					break;
				}
			}
			return false;
		}

		private boolean isVertical(Koma koma, int from_x, int from_y, int to_x,
				int to_y) {
			boolean result = false;
			Koma temp;
			int start_y = 0;
			int step_y = 1;
			if (from_x == to_x) {
				if (from_y > to_y) {
					start_y = from_y - 1;
					step_y = -1;
				} else {
					start_y = from_y + 1;
					step_y = 1;
				}
				result = true;
				for (int y = start_y; y != to_y; y += step_y) {
					temp = board[y][from_x];
					if (temp.kind != KOMA_NONE) {
						// Log.d(TAG, "isVertical: temp.kind=" + temp.kind +
						// ",y="
						// + y + ",x=" + from_x);
						result = false;
						break;
					}
				}
			}
			return result;
		}

		private boolean isHorizontal(Koma koma, int from_x, int from_y,
				int to_x, int to_y) {
			boolean result = false;
			Koma temp;
			int start_x = 0;
			int step_x = 1;
			if (from_y == to_y) {
				if (from_x > to_x) {
					start_x = from_x - 1;
					step_x = -1;
				} else {
					start_x = from_x + 1;
					step_x = 1;
				}
				result = true;
				for (int x = start_x; x != to_x; x += step_x) {
					temp = board[from_y][x];
					if (temp.kind != KOMA_NONE) {
						result = false;
						break;
					}
				}
			}
			return result;
		}

		private boolean isForward(Koma koma, int from_x, int from_y, int to_x,
				int to_y) {
			Koma temp;
			if (from_x != to_x) {
				return false;
			}
			int start_y = 0;
			int step_y = 0;
			boolean result = false;
			switch (koma.status) {
			case STATUS_NORMAL:
			case STATUS_REVERSE:
				if (from_y > to_y) {
					start_y = from_y - 1;
					step_y = -1;
					result = true;
				}
				break;
			case STATUS_ROTATE:
			case STATUS_ROTATE_REVERSE:
				if (from_y < to_y) {
					start_y = from_y + 1;
					step_y = 1;
					result = true;
				}
				break;
			}
			if (result) {
				for (int y = start_y; y != to_y; y += step_y) {
					temp = board[y][from_x];
					if (temp.kind != KOMA_NONE) {
						result = false;
					}
				}
			}
			return result;
		}

		private boolean isForwardDiagonally(Koma koma, int from_x, int from_y,
				int to_x, int to_y) {
			boolean result = false;
			Koma temp;
			int x, y;
			switch (koma.status) {
			case STATUS_NORMAL:
			case STATUS_REVERSE:
				x = Math.abs(from_x - to_x);
				y = from_y - to_y;
				if (y == 1 && x <= 1) {
					result = true;
				}
				break;
			case STATUS_ROTATE:
			case STATUS_ROTATE_REVERSE:
				x = Math.abs(from_x - to_x);
				y = to_y - from_y;
				if (y == 1 && x <= 1) {
					result = true;
				}
				break;
			}
			if (result) {
				temp = board[to_y][to_x];
				if (temp.me == koma.me) {
					result = false;
				}
			}
			return result;
		}

		private boolean isSideOrBack(Koma koma, int from_x, int from_y,
				int to_x, int to_y) {
			boolean result = false;
			Koma temp;
			int x, y;
			temp = board[to_y][to_x];
			switch (koma.status) {
			case STATUS_NORMAL:
			case STATUS_REVERSE:
				x = Math.abs(from_x - to_x);
				y = from_y - to_y;
				if (y == 0 && x == 1 || y == -1 && x == 0) {
					result = true;
				}
				// Log.d(TAG, "***: fx=" + from_x + ",fy=" + from_y + ",tx="
				// + to_x + ",ty=" + to_y+ ",x="+x+",y="+y);
				break;
			case STATUS_ROTATE:
			case STATUS_ROTATE_REVERSE:
				x = Math.abs(from_x - to_x);
				y = from_y - to_y;
				if (y == 0 && x == 1 || y == 1 && x == 0) {
					result = true;
				}
				break;
			}
			if (result) {
				temp = board[to_y][to_x];
				if (temp.me == koma.me) {
					result = false;
				}
			}
			return result;
		}

		private boolean isDiagonally(Koma koma, int from_x, int from_y,
				int to_x, int to_y) {
			boolean result = false;
			Koma temp;
			int x, y;
			int start_y = 0;
			int start_x = 0;
			int step_y = 1;
			int step_x = 1;
			switch (koma.status) {
			case STATUS_NORMAL:
			case STATUS_REVERSE:
			case STATUS_ROTATE:
			case STATUS_ROTATE_REVERSE:
				x = Math.abs(from_x - to_x);
				y = Math.abs(from_y - to_y);
				if (y == x) {
					result = true;
				}
				break;
			}
			if (result) {
				if (from_y > to_y) {
					start_y = from_y - 1;
					step_y = -1;
				} else {
					start_y = from_y + 1;
					step_y = 1;
				}
				if (from_x > to_x) {
					start_x = from_x - 1;
					step_x = -1;
				} else {
					start_x = from_x + 1;
					step_x = 1;
				}
				for (y = start_y, x = start_x; (y != to_y && x != to_x); y += step_y, x += step_x) {
					temp = board[y][x]; // .get(y * ROWS + x);
					if (temp.kind != KOMA_NONE) {
						result = false;
						break;
					}
				}
			}
			return result;
		}

		private boolean isBackwardDiagonally(Koma koma, int from_x, int from_y,
				int to_x, int to_y) {
			boolean result = false;
			Koma temp;
			int x, y;
			switch (koma.status) {
			case STATUS_NORMAL:
			case STATUS_REVERSE:
				x = Math.abs(from_x - to_x);
				y = from_y - to_y;
				if (y == -1 && x == 1) {
					result = true;
				}
				break;
			case STATUS_ROTATE:
			case STATUS_ROTATE_REVERSE:
				x = Math.abs(from_x - to_x);
				y = from_y - to_y;
				if (y == 1 && x == 1) {
					result = true;
				}
				break;
			}
			if (result) {
				temp = board[to_y][to_x]; // .get(to_y * ROWS + to_x);
				if (temp.me == koma.me) {
					result = false;
				}
			}
			return result;
		}

		/**
		 * for KEIMA
		 * 
		 * @param koma
		 * @param from_x
		 * @param from_y
		 * @param to_x
		 * @param to_y
		 * @return
		 */
		private boolean isTwoVerticallyOneHorizontally(Koma koma, int from_x,
				int from_y, int to_x, int to_y) {
			boolean result = false;
			int x, y;
			switch (koma.status) {
			case STATUS_NORMAL:
			case STATUS_REVERSE:
				x = Math.abs(from_x - to_x);
				y = from_y - to_y;
				if (y == 2 && x == 1) {
					result = true;
				}
				break;
			case STATUS_ROTATE:
			case STATUS_ROTATE_REVERSE:
				x = Math.abs(from_x - to_x);
				y = from_y - to_y;
				if (y == -2 && x == 1) {
					result = true;
				}
				break;
			}
			return result;
		}
	}

	private class KomaHistory {
		private Koma koma;
		private int from_x;
		private int to_x;
		private int from_y;
		private int to_y;

		public KomaHistory(Koma koma, int from_x, int from_y, int to_x, int to_y) {
			this.koma = koma;
			this.from_x = from_x;
			this.from_y = from_y;
			this.to_x = to_x;
			this.to_y = to_y;
		}
	}

	public static class KomaImage {
		private final int[][] drawable = {
				{ 0, 0, 0, 0 },
				{ R.drawable.fu, R.drawable.nari_fu, R.drawable.fu_r,
						R.drawable.nari_fu_r },
				{ R.drawable.hi, R.drawable.ryu, R.drawable.hi_r,
						R.drawable.ryu_r },
				{ R.drawable.kaku, R.drawable.uma, R.drawable.kaku_r,
						R.drawable.uma_r },
				{ R.drawable.kyo, R.drawable.nari_kyo, R.drawable.kyo_r,
						R.drawable.nari_kyo_r },
				{ R.drawable.kei, R.drawable.nari_kei, R.drawable.kei_r,
						R.drawable.nari_kei_r },
				{ R.drawable.gin, R.drawable.nari_gin, R.drawable.gin_r,
						R.drawable.nari_gin_r },
				{ R.drawable.kin, R.drawable.kin, R.drawable.kin_r,
						R.drawable.kin_r },
				{ R.drawable.gyoku, R.drawable.gyoku, R.drawable.gyoku_r,
						R.drawable.gyoku_r } };
		private final Bitmap[][] images;

		public KomaImage(Resources resources, int width, int height) {
			images = new Bitmap[9][4];
			for (int i = 0; i < 9; i++) {
				for (int j = 0; j < 4; j++) {
					if (0 != drawable[i][j]) {
						images[i][j] = Bitmap.createScaledBitmap(
								((BitmapDrawable) resources
										.getDrawable(drawable[i][j]))
										.getBitmap(), width, height, true);
					}
				}
			}
		}

		public Bitmap getBitmap(int koma, int status) {
			return images[koma][status];
		}
	}

	private static class ShogiView extends View {
		private static Drawable ban;
		private Rect canvas_rect;
		private int koma_from_x;
		private int koma_from_y;
		private int koma_height;
		private Koma koma_move;
		private int koma_pos_x;
		private int koma_pos_y;
		private int koma_width;
		private int koma_x;
		private int koma_y;
		private boolean mMoving = false;
		private final Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		private int[] posX = new int[COLS];
		private int[] posY = new int[ROWS];

		public ShogiView(Context context) {
			super(context);
			final Resources r = getResources();
			ban = r.getDrawable(R.drawable.shogi_board);
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

		private void koma_moving(float x, float y) {
			koma_x = Math.min((int) x / koma_width, COLS);
			koma_pos_x = koma_x * koma_width;
			koma_y = Math.min((int) y / koma_height, ROWS);
			koma_pos_y = koma_y * koma_height;
			if (!mMoving) {
				koma_from_x = koma_x;
				koma_from_y = koma_y;
				mMoving = true;
			}
		}

		@Override
		protected void onDraw(Canvas canvas) {
			final Paint paint = mPaint;
			canvas_rect = canvas.getClipBounds();
			ban.setBounds(canvas_rect);
			ban.draw(canvas);

			// if (null == mKomaImage) {
			// Log.e(TAG, "onDraw: mKomaImage is null");
			// }
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
			Rect koma_rect;
			Koma koma;
			Bitmap bitmap;
			for (int y = 0; y < ROWS; y++) {
				for (int x = 0; x < COLS; x++) {
					koma = mKomaBoard.getKoma(x, y);
					if (koma.kind != KOMA_NONE) {
						if (mMoving && x == koma_from_x && y == koma_from_y) {
							koma_move = koma;
						} else {
							koma_rect = new Rect(posX[x], posY[y], posX[x]
									+ koma_width, posY[y] + koma_height);
							bitmap = koma.getBitmap(mKomaImage);
							if (null != bitmap) {
								canvas.drawBitmap(bitmap, null, koma_rect, null);
							}
						}
					}
				}
			}
			if (mMoving) {
				koma_rect = new Rect(koma_pos_x, koma_pos_y, koma_pos_x
						+ koma_width, koma_pos_y + koma_height);
				canvas.drawBitmap(koma_move.getBitmap(mKomaImage), null,
						koma_rect, paint);
			}
		}

		@Override
		protected void onSizeChanged(int w, int h, int oldw, int oldh) {
			super.onSizeChanged(w, h, oldw, oldh);
			koma_width = w / COLS;
			koma_height = h / ROWS;
			final Resources r = getResources();
			mKomaImage = new KomaImage(r, koma_width, koma_height);
			Log.d(TAG, "onSizeChanged: w=" + w + ",h=" + h);
		}

		private boolean processEvent(MotionEvent event) {
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				if (!mMoving) {
					koma_moving(event.getX(), event.getY());
				}
				return true;
			case MotionEvent.ACTION_MOVE:
				if (mMoving) {
					koma_moving(event.getX(), event.getY());
					invalidate();
				}
				return true;
			case MotionEvent.ACTION_UP:
				if (koma_x >= 0 && koma_y >= 0 && null != koma_move) {
					mKomaBoard.moveKoma(koma_move, koma_from_x, koma_from_y,
							koma_x, koma_y);
				}
				mMoving = false;
				koma_move = null;
				invalidate();
				return true;
			case MotionEvent.ACTION_CANCEL:
				if (mMoving)
					return true;
				break;
			}
			return false;
		}
	}

	private static final int ROWS = 9;
	private static final int COLS = 9;

	public static final int KOMA_FU = 1;
	public static final int KOMA_GIN = 6;
	public static final int KOMA_GYOKU = 8;
	public static final int KOMA_HI = 2;
	public static final int KOMA_KAKU = 3;
	public static final int KOMA_KEI = 5;
	public static final int KOMA_KIN = 7;
	public static final int KOMA_KYO = 4;
	public static final int KOMA_NONE = 0;
	private static KomaBoard mKomaBoard;
	private static KomaImage mKomaImage;
	public static final int ORIENTATION_ME = 0;
	public static final int ORIENTATION_YOU = 1;
	public static final int STATUS_NORMAL = 0;
	public static final int STATUS_REVERSE = 1;
	public static final int STATUS_ROTATE = 2;
	public static final int STATUS_ROTATE_REVERSE = 3;
	private final Koma Fu01 = new Koma(KOMA_FU, ORIENTATION_ME);
	private final Koma Fu02 = new Koma(KOMA_FU, ORIENTATION_ME);
	private final Koma Fu03 = new Koma(KOMA_FU, ORIENTATION_ME);
	private final Koma Fu04 = new Koma(KOMA_FU, ORIENTATION_ME);
	private final Koma Fu05 = new Koma(KOMA_FU, ORIENTATION_ME);
	private final Koma Fu06 = new Koma(KOMA_FU, ORIENTATION_ME);
	private final Koma Fu07 = new Koma(KOMA_FU, ORIENTATION_ME);
	private final Koma Fu08 = new Koma(KOMA_FU, ORIENTATION_ME);
	private final Koma Fu09 = new Koma(KOMA_FU, ORIENTATION_ME);
	private final Koma Fu11 = new Koma(KOMA_FU, ORIENTATION_YOU);
	private final Koma Fu12 = new Koma(KOMA_FU, ORIENTATION_YOU);
	private final Koma Fu13 = new Koma(KOMA_FU, ORIENTATION_YOU);
	private final Koma Fu14 = new Koma(KOMA_FU, ORIENTATION_YOU);
	private final Koma Fu15 = new Koma(KOMA_FU, ORIENTATION_YOU);
	private final Koma Fu16 = new Koma(KOMA_FU, ORIENTATION_YOU);
	private final Koma Fu17 = new Koma(KOMA_FU, ORIENTATION_YOU);
	private final Koma Fu18 = new Koma(KOMA_FU, ORIENTATION_YOU);
	private final Koma Fu19 = new Koma(KOMA_FU, ORIENTATION_YOU);
	private final Koma Gin01 = new Koma(KOMA_GIN, ORIENTATION_ME);
	private final Koma Gin02 = new Koma(KOMA_GIN, ORIENTATION_ME);
	private final Koma Gin11 = new Koma(KOMA_GIN, ORIENTATION_YOU);
	private final Koma Gin12 = new Koma(KOMA_GIN, ORIENTATION_YOU);
	private final Koma Gyoku0 = new Koma(KOMA_GYOKU, ORIENTATION_ME);
	private final Koma Gyoku1 = new Koma(KOMA_GYOKU, ORIENTATION_YOU);
	private final Koma Hi0 = new Koma(KOMA_HI, ORIENTATION_ME);
	private final Koma Hi1 = new Koma(KOMA_HI, ORIENTATION_YOU);
	private final Koma Kaku0 = new Koma(KOMA_KAKU, ORIENTATION_ME);
	private final Koma Kaku1 = new Koma(KOMA_KAKU, ORIENTATION_YOU);
	private final Koma Kei01 = new Koma(KOMA_KEI, ORIENTATION_ME);
	private final Koma Kei02 = new Koma(KOMA_KEI, ORIENTATION_ME);
	private final Koma Kei11 = new Koma(KOMA_KEI, ORIENTATION_YOU);
	private final Koma Kei12 = new Koma(KOMA_KEI, ORIENTATION_YOU);
	private final Koma Kin01 = new Koma(KOMA_KIN, ORIENTATION_ME);
	private final Koma Kin02 = new Koma(KOMA_KIN, ORIENTATION_ME);
	private final Koma Kin11 = new Koma(KOMA_KIN, ORIENTATION_YOU);
	private final Koma Kin12 = new Koma(KOMA_KIN, ORIENTATION_YOU);
	private final Koma Kyo01 = new Koma(KOMA_KYO, ORIENTATION_ME);
	private final Koma Kyo02 = new Koma(KOMA_KYO, ORIENTATION_ME);
	private final Koma Kyo11 = new Koma(KOMA_KYO, ORIENTATION_YOU);
	private final Koma Kyo12 = new Koma(KOMA_KYO, ORIENTATION_YOU);
	private final Koma None = new Koma(KOMA_NONE, -1);
	private static final String TAG = "Shogi";
	private final static int MENU_CAPTURED = 1;
	private final static int MENU_SETTINGS = 2;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ShogiView view = new ShogiView(this);
		setContentView(view);

		mKomaBoard = new KomaBoard();
	}

	public List<Koma> getCapturedPieces() {
		return mKomaBoard.stock;
	}

	public KomaImage getKomaImage() {
		return mKomaImage;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		menu.add(0, MENU_CAPTURED, Menu.NONE, R.string.menu_captured);
		// menu.add(0, MENU_SETTINGS, Menu.NONE, R.string.menu_settings);
		return true;
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		boolean result = super.onMenuItemSelected(featureId, item);
		switch (item.getItemId()) {
		case MENU_CAPTURED:
			startActivity(new Intent(this, CapturedPiecesActivity.class));
			break;
		case MENU_SETTINGS:
			// startActivity(new Intent(this, Settings.class));
			break;
		}
		return result;
	}
}