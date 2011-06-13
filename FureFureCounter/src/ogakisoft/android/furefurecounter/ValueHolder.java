package ogakisoft.android.furefurecounter;

import java.util.Arrays;

public class ValueHolder {
    private static final float BLANK = -999;
    private int size;
    private float[] values;
    private int position = 0;

    public ValueHolder(int size) {
	this.size = size;
	values = new float[size];
	clear();
    }

    private void clear() {
	Arrays.fill(values, BLANK);
    }

    public boolean add(float value) {
	values[position] = value;
	if (size - 1 == position) {
	    position = 0;
	    return true;
	}
	position++;
	return false;
    }

    public float getMedian() {
	float[] tmp = values.clone();
	Arrays.sort(tmp);
	int len = tmp.length;
	int first = 0;
	for (int i = 0; i < len; i++) {
	    first = i;
	    if (tmp[i] != BLANK)
		break;
	}
	return tmp[(len - first) / 2 + first];
    }
}