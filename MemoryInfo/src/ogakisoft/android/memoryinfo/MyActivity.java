package ogakisoft.android.memoryinfo;

import java.util.Vector;

import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.app.ListActivity;
import android.content.Context;
import android.os.Bundle;
import android.os.Debug;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class MyActivity extends ListActivity {
	private final int REPEAT_INTERVAL = 3000;	// 3 sec

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		final MemoryInfoAdapter adapter = new MemoryInfoAdapter(this);
		setListAdapter(adapter);
		final Handler handler = new Handler();
		final Runnable runnable = new Runnable() {
			@Override
			public void run() {
				memoryInfo();
				handler.postDelayed(this, REPEAT_INTERVAL);
			}
		};
		handler.postDelayed(runnable, REPEAT_INTERVAL);
	}

	private void memoryInfo() {
		ActivityManager activityManager = (ActivityManager) getApplicationContext()
				.getSystemService(ACTIVITY_SERVICE);
		MemoryInfo memoryInfo = new MemoryInfo();
		activityManager.getMemoryInfo(memoryInfo);
		Debug.MemoryInfo debugInfo = new Debug.MemoryInfo();
		Debug.getMemoryInfo(debugInfo);
		Vector<String> info = new Vector<String>();
		info.add(String.valueOf(memoryInfo.availMem));
		info.add(String.valueOf(memoryInfo.lowMemory));
		info.add(String.valueOf(memoryInfo.threshold));
		info.add(String.valueOf(debugInfo.dalvikPrivateDirty));
		info.add(String.valueOf(debugInfo.dalvikPss));
		info.add(String.valueOf(debugInfo.dalvikSharedDirty));
		info.add(String.valueOf(debugInfo.nativePrivateDirty));
		info.add(String.valueOf(debugInfo.nativePss));
		info.add(String.valueOf(debugInfo.nativeSharedDirty));
		info.add(String.valueOf(debugInfo.otherPrivateDirty));
		info.add(String.valueOf(debugInfo.otherPss));
		info.add(String.valueOf(debugInfo.otherSharedDirty));
		info.add(String.valueOf(Debug.getGlobalAllocCount()));
		info.add(String.valueOf(Debug.getGlobalAllocSize()));
		// info.add(String.valueOf(Debug.getGlobalClassInitCount())); //api
		// level8
		// info.add(String.valueOf(Debug.getGlobalClassInitTime())); //api
		// level8
		info.add(String.valueOf(Debug.getGlobalExternalAllocCount()));
		info.add(String.valueOf(Debug.getGlobalExternalAllocSize()));
		info.add(String.valueOf(Debug.getGlobalExternalFreedCount()));
		info.add(String.valueOf(Debug.getGlobalExternalFreedSize()));
		info.add(String.valueOf(Debug.getGlobalFreedCount()));
		info.add(String.valueOf(Debug.getGlobalFreedSize()));
		info.add(String.valueOf(Debug.getGlobalGcInvocationCount()));
		info.add(String.valueOf(Debug.getLoadedClassCount()));
		info.add(String.valueOf(Debug.getNativeHeapAllocatedSize()));
		info.add(String.valueOf(Debug.getNativeHeapFreeSize()));
		info.add(String.valueOf(Debug.getNativeHeapSize()));
		info.add(String.valueOf(Debug.getThreadAllocCount()));
		info.add(String.valueOf(Debug.getThreadAllocSize()));
		info.add(String.valueOf(Debug.getThreadExternalAllocCount()));
		info.add(String.valueOf(Debug.getThreadExternalAllocSize()));
		info.add(String.valueOf(Debug.getThreadGcInvocationCount()));

		MemoryInfoAdapter adapter = (MemoryInfoAdapter) getListAdapter();
		adapter.setValues(info.toArray(new String[0]));
		adapter.notifyDataSetChanged();
	}

	private static class MemoryInfoAdapter extends BaseAdapter {
		private LayoutInflater mInflater;
		private static final String[] mTitle = {
				"memoryInfo.availMem",
				"memoryInfo.lowMemory",
				"memoryInfo.threshold",
				"debugInfo.dalvikPrivateDirty",
				"debugInfo.dalvikPss",
				"debugInfo.dalvikSharedDirty",
				"debugInfo.nativePrivateDirty",
				"debugInfo.nativePss",
				"debugInfo.nativeSharedDirty",
				"debugInfo.otherPrivateDirty",
				"debugInfo.otherPss",
				"debugInfo.otherSharedDirty",
				"debug.getGlobalAllocCount",
				"debug.getGlobalAllocSize",
				// "debug.getGlobalClassInitCount",
				// "debug.getGlobalClassInitTime",
				"debug.getGlobalExternalAllocCount",
				"debug.getGlobalExternalAllocSize",
				"debug.getGlobalExternalFreedCount",
				"debug.getGlobalExternalFreedSize",
				"debug.getGlobalFreedCount",
				"debug.getGlobalFreedSize",
				"debug.getGlobalGcInvocationCount",
				"debug.getLoadedClassCount",
				"debug.getNativeHeapAllocatedSize",
				"debug.getNativeHeapFreeSize",
				"debug.getNativeHeapSize",
				"debug.getThreadAllocCount",
				"debug.getThreadAllocSize",
				"debug.getThreadExternalAllocCount",
				"debug.getThreadExternalAllocSize",
				"debug.getThreadGcInvocationCount" };
		private String[] mValues = new String[mTitle.length];

		public MemoryInfoAdapter(Context context) {
			mInflater = LayoutInflater.from(context);
		}

		public void setValues(String[] values) {
			mValues = values;
		}

		public int getCount() {
			return mTitle.length;
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
				holder.title = (TextView) convertView.findViewById(R.id.title);
				holder.value = (TextView) convertView.findViewById(R.id.value);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			holder.title.setText(mTitle[position]);
			holder.value.setText(mValues[position]);
			return convertView;
		}

		static class ViewHolder {
			TextView title;
			TextView value;
		}
	}
}