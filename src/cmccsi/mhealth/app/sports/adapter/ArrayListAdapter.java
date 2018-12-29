package cmccsi.mhealth.app.sports.adapter;

import java.util.ArrayList;
import java.util.List;

import cmccsi.mhealth.app.sports.common.utils.ArrayUtils;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;

public abstract class ArrayListAdapter<T> extends BaseAdapter {

	protected List<T> mList;
	protected Context mContext;
	protected ListView mListView;

	public ArrayListAdapter(Context context) {
		this.mContext = context;
	}

	@Override
	public int getCount() {
		if (mList != null)
			return mList.size();
		else
			return 0;
	}

	@Override
	public T getItem(int position) {
		return mList == null ? null : mList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	abstract public View getView(int position, View convertView, ViewGroup parent);

	public void setList(List<T> list) {
		this.mList = list;
		notifyDataSetChanged();
	}
	
	public void setList(List<T> list, int flag) {
		if(flag == 0){
			this.mList = list;
		}
	}

	public List<T> getList() {
		return mList;
	}

	public void setList(T[] array) {
		if (ArrayUtils.isNotEmpty(array)) {
			ArrayList<T> list = new ArrayList<T>(array.length);
			for (T t : array) {
				list.add(t);
			}
			setList(list);
		} else {
			mList = null;
			notifyDataSetChanged();
		}
	}

	public ListView getListView() {
		return mListView;
	}

	public void setListView(ListView listView) {
		mListView = listView;
	}

	public void removeList() {
		this.mList = null;
		notifyDataSetInvalidated();
	}

	public Context getContext() {
		return mContext;
	}

}
