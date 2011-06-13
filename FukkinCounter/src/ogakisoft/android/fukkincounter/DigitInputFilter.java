package ogakisoft.android.fukkincounter;

import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;

public class DigitInputFilter implements InputFilter {

    @Override
    public CharSequence filter(CharSequence source, int start, int end,
	    Spanned dest, int dstart, int dend) {

	int pos = -1;
	for (int i = start; i < end; i++) {
	    if (!Character.isDigit(source.charAt(i))) {
		pos = i;
		break;
	    }
	}
	String s = null; // null means 'keep original input string'
	if (pos > 0) {
	    char[] v = new char[pos - start];
	    TextUtils.getChars(source, start, pos, v, 0);
	    s = new String(v);
	} else if (pos == 0) {
	    s = "";
	}
	return s;
    }

}
