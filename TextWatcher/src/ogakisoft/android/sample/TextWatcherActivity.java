package ogakisoft.android.sample;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

public class TextWatcherActivity extends Activity {
    private EditText mPicker;
    private PopupWindow mPopupWindow;

    public class DigitInputFilter implements InputFilter {
        @Override
        public CharSequence filter(CharSequence source, int start, int end,
                Spanned dest, int dstart, int dend) {
            String str = null; // null means 'keep original input string'
            int pos = -1;
            final String allow = "1234567890";
            char[] buf = new char[end - start];
            TextUtils.getChars(source, start, end, buf, 0);
            final int count = buf.length;
            for (int i = 0; i < count; i++) {
                if (allow.indexOf(buf[i]) < 0) {
                    pos = i;
                    break;
                }
            }
            if (pos > 0) {
                buf = new char[pos - start];
                TextUtils.getChars(source, start, pos, buf, 0);
                str = new String(buf);
            } else if (0 == pos) {
                str = "";
            }
            return str;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.main);

	InputFilter filters[] = { new DigitInputFilter() };
	mPicker = (EditText) findViewById(R.id.picker_value);
	mPicker.setFilters(filters);
	mPicker.setText(String.valueOf(10));

	LinearLayout layout = (LinearLayout) getLayoutInflater().inflate(
		R.layout.popup, null);
	mPopupWindow = new PopupWindow(this);
	mPopupWindow.setWindowLayoutMode(LayoutParams.WRAP_CONTENT,
		LayoutParams.WRAP_CONTENT);
	mPopupWindow.setContentView(layout);
	TextView text = (TextView) layout.findViewById(R.id.text);
	text.setText("TOO LONG!");

	final EditText edit = (EditText) findViewById(R.id.edit);

	edit.addTextChangedListener(new TextWatcher() {
	    @Override
	    public void beforeTextChanged(CharSequence s, int start, int count,
		    int after) {
		int val = Integer.parseInt(mPicker.getText().toString());
		if (val < after) {
		    showMessage(edit);
		}
	    }
	    @Override
	    public void onTextChanged(CharSequence s, int start, int before,
		    int count) {
	    }
	    @Override
	    public void afterTextChanged(Editable s) {
		int val = Integer.parseInt(mPicker.getText().toString());
		if (val < s.length()) {
		    showMessage(edit);
		}
	    }
	});
    }

    public void showMessage(View v) {
	mPopupWindow.showAsDropDown(v);
	final Handler handler = new Handler();
	new Thread(new Runnable() {
	    public void run() {
		try {
		    Thread.sleep(3000);
		} catch (InterruptedException e) {
		}
		handler.post(new Runnable() {
		    public void run() {
			mPopupWindow.dismiss();
		    }
		});
	    }
	}).start();
    }

    public void onClickPickerIncrement(View v) {
	int val = Integer.parseInt(mPicker.getText().toString());
	mPicker.setText(String.valueOf(++val));
    }

    public void onClickPickerDecrement(View v) {
	int val = Integer.parseInt(mPicker.getText().toString());
	mPicker.setText(String.valueOf(--val));
    }
}