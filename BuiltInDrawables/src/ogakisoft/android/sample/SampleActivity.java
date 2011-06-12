package ogakisoft.android.sample;

import java.lang.reflect.Field;

import android.app.ListActivity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class SampleActivity extends ListActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setListAdapter(new ImageAdapter(this));
    }

    private static class ImageAdapter extends BaseAdapter {
	private LayoutInflater mInflater;
	private Field[] mFlds;
	@SuppressWarnings("rawtypes")
	private Class cls;

	public ImageAdapter(Context c) {
	    mInflater = LayoutInflater.from(c);
	    try {
		cls = Class.forName("android.R$drawable");
		mFlds = cls.getDeclaredFields();
	    } catch (ClassNotFoundException e) {
		e.printStackTrace();
	    } catch (SecurityException e) {
		e.printStackTrace();
	    } catch (IllegalArgumentException e) {
		e.printStackTrace();
	    }

	}

	public int getCount() {
	    return mFlds.length;
	}

	public Object getItem(int position) {
	    return position;
	}

	public long getItemId(int position) {
	    return position;
	}

	public View getView(final int position, View convertView,
		ViewGroup parent) {
	    ViewHolder holder;
	    if (convertView == null) {
		convertView = mInflater.inflate(R.layout.main, null);
		holder = new ViewHolder();
		holder.text = (TextView) convertView.findViewById(R.id.text);
		holder.icon = (ImageView) convertView.findViewById(R.id.image);
		convertView.setTag(holder);
	    } else {
		holder = (ViewHolder) convertView.getTag();
	    }
	    try {
		holder.icon.setImageResource(mFlds[position].getInt(cls));
		int pos = mFlds[position].toString().lastIndexOf('.');
		pos = (pos == -1) ? 0 : pos + 1;
		String str = mFlds[position].toString().substring(pos);
		
		int h = holder.icon.getDrawable().getIntrinsicHeight();
		int w = holder.icon.getDrawable().getIntrinsicWidth();
		str += "\nwidth=" + w + ",height=" + h;
		holder.text.setText(str);
	    } catch (IllegalArgumentException e) {
	    } catch (IllegalAccessException e) {
	    }
	    return convertView;
	}

	static class ViewHolder {
	    TextView text;
	    ImageView icon;
	}
    }
}
