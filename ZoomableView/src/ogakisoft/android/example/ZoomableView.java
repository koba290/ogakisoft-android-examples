
package ogakisoft.android.example;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;

public class ZoomableView extends ScrollView {
    private static final String TAG = "ZoomableView";
    private Rect[] defaultPos;

    public ZoomableView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ZoomableView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setScale(float scale) {

        if (defaultPos == null) {
            LinearLayout layout = (LinearLayout) findViewById(R.id.layout1);
            int childCount = layout.getChildCount();
            defaultPos = new Rect[childCount];
            for (int i = 0; i < childCount; i++) {
                View view = layout.getChildAt(i);
                defaultPos[i] = new Rect();
                view.getDrawingRect(defaultPos[i]);
            }
        }

        final LinearLayout layout = (LinearLayout) findViewById(R.id.layout1);
        final int childCount = layout.getChildCount();
        final int margin = 5;
        int maxRight = layout.getRight();
        int maxBottom = layout.getBottom();
        Rect[] pos = new Rect[childCount];
        View view;
        for (int i = 0; i < childCount; i++) {
            view = layout.getChildAt(i);
            pos[i] = new Rect();
            view.getDrawingRect(pos[i]);
            pos[i].right = (int) (defaultPos[i].right * scale);
            pos[i].bottom = (int) (defaultPos[i].bottom * scale);
            if (i > 0) {
                if (pos[i - 1].right > pos[i].left) {
                    pos[i].left = pos[i - 1].right + margin;
                    pos[i].right = pos[i].left + pos[i].right;
                }
                if (pos[i].top > 0 && pos[i-1].bottom > pos[i].top) {
                    pos[i].top = pos[i-1].bottom + margin;
                    pos[i].bottom = pos[i].top + pos[i].bottom;
                }
            }
            maxRight = Math.max(pos[i].right, maxRight);
            maxBottom = Math.max(pos[i].bottom, maxBottom);
        }
        layout.layout(layout.getLeft(), layout.getTop(), maxRight + 50, maxBottom + 50);
        Log.d(TAG, "parent, left=" + layout.getLeft() + ",top=" + layout.getTop() + ",right="
                + layout.getRight() + ",bottom=" + layout.getBottom());
        for (int i = 0; i < childCount; i++) {
            view = layout.getChildAt(i);
            view.layout(pos[i].left, pos[i].top, pos[i].right, pos[i].bottom);
            Log.d(TAG, "child=" + i + ",left=" + pos[i].left + ",top=" + pos[i].top + ",right="
                    + pos[i].right + ",bottom=" + pos[i].bottom);
        }
    }
}
