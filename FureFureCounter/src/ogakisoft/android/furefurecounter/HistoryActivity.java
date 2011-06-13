package ogakisoft.android.furefurecounter;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

import android.app.ListActivity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

public class HistoryActivity extends ListActivity {
    private final static String TAG = "HistoryActivity";
    // private final static boolean DEBUG = false;
    private HistoryAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.history);
	try {
	    adapter = new HistoryAdapter(getApplicationContext());
	} catch (IOException e) {
	    // e.printStackTrace();
	    Log.e(TAG, e.getMessage());
	}
	setListAdapter(adapter);
	setTitle(R.string.title_history);
    }

    private List<String[]> load() throws IOException {
	ArrayList<String[]> list = new ArrayList<String[]>();
	BufferedReader in = new BufferedReader(new InputStreamReader(
		openFileInput(CounterActivity.HISTORY_FILE_NAME)));
	String line;
	while ((line = in.readLine()) != null) {
	    String[] str = line.split("\\"
		    + CounterActivity.SAVE_DELIMITER_SYMBOL);
	    // if (DEBUG) {
	    // StringBuffer sb = new StringBuffer();
	    // for (int i = 0; i < str.length; i++) {
	    // sb.append(i).append("=").append(str[i]).append("||");
	    // }
	    // Log.d(TAG, "load: " + sb.toString());
	    // }
	    if (str != null && str.length == 3) {
		list.add(str);
	    }
	}
	in.close();
	return list;
    }

    private class HistoryAdapter extends BaseAdapter {
	private LayoutInflater mInflater;
	private List<String[]> list;

	public HistoryAdapter(Context context) throws IOException {
	    mInflater = (LayoutInflater) context
		    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	    list = load();
	}

	@Override
	public int getCount() {
	    if (list == null || list.size() == 0)
		return 0;
	    return list.size() + 1; // column title
	}

	@Override
	public Object getItem(int position) {
	    if (list == null)
		return null;
	    if (position == 0) { // column title
		String[] item = new String[3];
		item[0] = getResources().getString(R.string.history_item1);
		item[1] = getResources().getString(R.string.history_item2);
		item[2] = getResources().getString(R.string.history_item3);
		return item;
	    } else {
		return list.get(position - 1);
	    }
	}

	@Override
	public long getItemId(int position) {
	    return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
	    if (convertView == null) {
		convertView = mInflater.inflate(R.layout.history_item, parent,
			false);
	    }
	    LinearLayout view = (LinearLayout) convertView;
	    String[] item = (String[]) getItem(position);
	    if (item != null) {
		TextView v1 = (TextView) view.findViewById(R.id.text1);
		v1.setText(item[0]);
		TextView v2 = (TextView) view.findViewById(R.id.text2);
		v2.setText(item[1]);
		TextView v3 = (TextView) view.findViewById(R.id.text3);
		v3.setText(item[2]);
	    }
	    return convertView;
	}

	public void clear() {
	    list = null;
	    notifyDataSetChanged();
	}
    }

    public void onClickButtonClear(View v) throws IOException {
	BufferedWriter out = new BufferedWriter(new OutputStreamWriter(
		openFileOutput(CounterActivity.HISTORY_FILE_NAME,
			Context.MODE_PRIVATE)));
	synchronized (out) {
	    out.newLine();
	    out.flush();
	    out.close();
	}
	synchronized (adapter) {
	    adapter.clear();
	}
	this.finish();
    }

    public void onClickButtonCancel(View v) {
	this.finish();
    }
}
