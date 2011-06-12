package ogakisoft.android.sample;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

public class SampleActivity extends Activity {
    private GridView mGrid;
    private List<String> mList;
    private PopupWindow mPopupWindow;
    private static final String[][] array = { { "あ", "い", "う", "え", "お" },
	    { "か", "き", "く", "け", "こ" }, { "さ", "し", "す", "せ", "そ" },
	    { "た", "ち", "つ", "て", "と" }, { "な", "に", "ぬ", "ね", "の" },
	    { "は", "ひ", "ふ", "へ", "ほ" }, { "ま", "み", "む", "め", "も" },
	    { "や", "　", "ゆ", "　", "よ" }, { "ら", "り", "る", "れ", "ろ" },
	    { "わ", "ゐ", "　", "ゑ", "を" }, { "ん", "　", "　", "　", "　" }, };
    private final int[] ids = { R.id.text_center, R.id.text_left,
	    R.id.text_top, R.id.text_right, R.id.text_bottom };

    @Override
    public void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.main);

	mList = new ArrayList<String>();
	for (int i = 0; i < array.length; i++) {
	    mList.add(array[i][0]);
	}

	LinearLayout layout = (LinearLayout) getLayoutInflater().inflate(
		R.layout.popup, null);
	mPopupWindow = new PopupWindow(this);
	mPopupWindow.setWindowLayoutMode(LayoutParams.WRAP_CONTENT,
		LayoutParams.WRAP_CONTENT);
	mPopupWindow.setContentView(layout);
	for (int i = 0; i < ids.length; i++) {
	    TextView tv = (TextView) layout.findViewById(ids[i]);
	    tv.setOnClickListener(new OnClickListener() {
		public void onClick(View v) {
		    TextView text = (TextView) v;
		    if (text != null && text.toString().trim().length() > 0) {
			Toast.makeText(SampleActivity.this, text.getText(),
				Toast.LENGTH_LONG).show();
		    }
		    mPopupWindow.dismiss();
		}
	    });
	}

	mGrid = (GridView) findViewById(R.id.myGrid);
	mGrid.setAdapter(new Adapter());
    }

    public class Adapter extends BaseAdapter {
	public Adapter() {
	}

	public View getView(int position, View convertView, ViewGroup parent) {
	    TextView view;

	    if (convertView == null) {
		view = new TextView(SampleActivity.this);
		view.setLayoutParams(new GridView.LayoutParams(50, 50));
		view.setId(position);
		view.setText(mList.get(position));
		view.setTextSize(24);
		view.setGravity(Gravity.CENTER_HORIZONTAL
			| Gravity.CENTER_VERTICAL);
	    } else {
		view = (TextView) convertView;
	    }

	    view.setOnClickListener(new OnClickListener() {
		public void onClick(View v) {
		    if (mPopupWindow.isShowing()) {
			mPopupWindow.dismiss();
		    } else {
			TextView textview = (TextView) v;
			int id = textview.getId();
			LinearLayout layout = (LinearLayout) mPopupWindow
				.getContentView();
			for (int i = 0; i < ids.length; i++) {
			    TextView tv = (TextView) layout
				    .findViewById(ids[i]);
			    tv.setText(array[id][i]);
			}
			mPopupWindow.showAsDropDown(v, -25, -25);
		    }
		}
	    });
	    return view;
	}

	public final int getCount() {
	    return mList.size();
	}

	public final Object getItem(int position) {
	    return mList.get(position);
	}

	public final long getItemId(int position) {
	    return position;
	}
    }

}
