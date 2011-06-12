package ogakisoft.android.sample;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ListAdapter;
import android.widget.TextView;

public class GridViewWithMassData extends Activity {
	private static final int MAX_NUM_OF_ITEMS = 200;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		final GridView gridview = (GridView) findViewById(R.id.GridView01);
		gridview.setNumColumns(8);
		gridview.setAdapter(new Adapter());
	}

	class Adapter implements ListAdapter {
		List<Integer> data = new ArrayList<Integer>(MAX_NUM_OF_ITEMS);

		public Adapter() {
			for (int i = 0; i < MAX_NUM_OF_ITEMS; i++)
				data.add(Integer.valueOf(i));
		}

		@Override
		public void registerDataSetObserver(DataSetObserver observer) {
		}

		@Override
		public void unregisterDataSetObserver(DataSetObserver observer) {
		}

		@Override
		public int getCount() {
			return MAX_NUM_OF_ITEMS;
		}

		@Override
		public Object getItem(int position) {
			return data.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public boolean hasStableIds() {
			return false;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			TextView view = new TextView(getApplicationContext());
			if (null == convertView) {
				view.setText(getItem(position).toString());
			} else {
				view = (TextView) convertView;
			}
			return view;
		}

		@Override
		public int getItemViewType(int position) {
			return android.widget.Adapter.IGNORE_ITEM_VIEW_TYPE;
		}

		@Override
		public int getViewTypeCount() {
			return 1; // MAX_NUM_OF_ITEMS;
		}

		@Override
		public boolean isEmpty() {
			return false;
		}

		@Override
		public boolean areAllItemsEnabled() {
			return false;
		}

		@Override
		public boolean isEnabled(int position) {
			return false;
		}
	}
}