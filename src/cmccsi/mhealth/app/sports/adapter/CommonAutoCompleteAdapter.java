package cmccsi.mhealth.app.sports.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import cmccsi.mhealth.app.sports.bean.CommonUserSearchInfos;
import cmccsi.mhealth.app.sports.bean.ContectGroupData;
import cmccsi.mhealth.app.sports.R;

public class CommonAutoCompleteAdapter extends BaseAdapter implements Filterable {
	private Context context;
	private ArrayFilter mFilter;
	private List<CommonUserSearchInfos> mOriginalValues;// 所有的Item
	private List<CommonUserSearchInfos> mObjects;// 过滤后的item
	private List<ContectGroupData> mOriginalGroupValues;// 所有的groupItem
	private List<ContectGroupData> mObjectsGroup;// 过滤后的groupitem
	private int mCurrentIndex;// 个人0 还是 组1
	private final Object mLock = new Object();
	private int maxMatch = 0;// 最多显示多少个选项,负数表示全部

	public CommonAutoCompleteAdapter(Context context, List<CommonUserSearchInfos> mOriginalValues,List<ContectGroupData> mOriginalGroupValues, int maxMatch , int mCurrentIndex) {
		this.context = context;
		this.mOriginalValues = mOriginalValues;
		this.mOriginalGroupValues = mOriginalGroupValues;
		this.maxMatch = maxMatch;
		this.mCurrentIndex = mCurrentIndex;
	}

	@Override
	public Filter getFilter() {
		// TODO Auto-generated method stub
		if (mFilter == null) {
			mFilter = new ArrayFilter();
		}
		return mFilter;
	}
	

	private class ArrayFilter extends Filter {

		@Override
		protected FilterResults performFiltering(CharSequence prefix) {
			// TODO Auto-generated method stub
			FilterResults results = new FilterResults();

			if (prefix == null || prefix.length() == 0) {
				synchronized (mLock) {
					switch (mCurrentIndex) {
					case 0:
						ArrayList<CommonUserSearchInfos> list1 = new ArrayList<CommonUserSearchInfos>(mOriginalValues);
						results.values = list1;
						results.count = list1.size();
						return results;
					case 1:
						ArrayList<ContectGroupData> list2 = new ArrayList<ContectGroupData>(mOriginalGroupValues);
						results.values = list2;
						results.count = list2.size();
						return results;
					}
				}
			} else {
				String prefixString = prefix.toString().toLowerCase();
				ArrayList<CommonUserSearchInfos> newValues1 =null;
				ArrayList<ContectGroupData> newValues2 =null;
				switch (mCurrentIndex) {
				case 0:
					newValues1 = singlefilter(prefixString);
					results.values = newValues1;
					results.count = newValues1.size();
					break;
				case 1:
					newValues2 = groupfilter(prefixString);
					results.values = newValues2;
					results.count = newValues2.size();
					break;
				}
			}

			return results;
		}

		private ArrayList<CommonUserSearchInfos> singlefilter(String prefixString) {
			prefixString = prefixString.trim();
			int count = mOriginalValues.size();
			ArrayList<CommonUserSearchInfos> newValues = new ArrayList<CommonUserSearchInfos>(count);
			outer:for (int i = 0; i < count; i++) {
				CommonUserSearchInfos value = mOriginalValues.get(i);
				String valueTextName = value.getName();
				String[] valueTextPinyin = value.getPinyin().split(",");
				String[] vtqp = value.getQuanpin().split(",");
				String valueTextPhone = value.getPhone();
//				String valueTextEmail = value.getEmail();

				if (valueTextName.contains(prefixString)) { 
					newValues.add(value);
					continue;
				}
				for (int j = 0; j < valueTextPinyin.length; j++) {
					if (valueTextPinyin[j].contains(prefixString)) { 
						newValues.add(value);
						continue outer;
					}
				}
				for (int j = 0; j < vtqp.length; j++) {
					if (vtqp[j].contains(prefixString)) { 
						newValues.add(value);
						continue outer;
					}
				}
				if (valueTextPhone.contains(prefixString)) { 
					newValues.add(value);
					continue;
				}
				if (maxMatch > 0) {// 有数量限制
					if (newValues.size() > maxMatch - 1) {// 不要太多
						break;
					}
				}
			}
			return newValues;
		}
		private ArrayList<ContectGroupData> groupfilter(String prefixString) {
			prefixString = prefixString.trim();
			int count = mOriginalGroupValues.size();
			ArrayList<ContectGroupData> newValues = new ArrayList<ContectGroupData>(count);
			outer:for (int i = 0; i < count; i++) {
				ContectGroupData value = mOriginalGroupValues.get(i);
				String valueTextPinyin[] = value.getPinyin().split(",");
				String valueTextQuanpin[] = value.getQuanpin().split(",");
				String valueTextGroupname = value.getGroupname();
				
				for (int j = 0; j < valueTextPinyin.length; j++) {
					if (valueTextPinyin[j].contains(prefixString)) {
						newValues.add(value);
						continue outer;
					}
				}
				for (int j = 0; j < valueTextPinyin.length; j++) {
					if (valueTextQuanpin[j].contains(prefixString)) {
						newValues.add(value);
						continue outer;
					}
				}
				if (valueTextGroupname.contains(prefixString)) { 
					newValues.add(value);
					continue;
				}
				if (maxMatch > 0) {// 有数量限制
					if (newValues.size() > maxMatch - 1) {// 不要太多
						break;
					}
				}
			}
			return newValues;
		}

		@SuppressWarnings("unchecked")
		@Override
		protected void publishResults(CharSequence constraint, FilterResults results) {
			switch (mCurrentIndex) {
			case 0:
				mObjects = (List<CommonUserSearchInfos>) results.values;
				break;
			case 1:
				mObjectsGroup = (List<ContectGroupData>) results.values;
				break;
			}
			if (results.count > 0) {
				notifyDataSetChanged();
			} else {
				notifyDataSetInvalidated();
			}
		}

	}

	@Override
	public int getCount() {
		switch (mCurrentIndex) {
		case 0:
			return mObjects.size();
		case 1:
			return mObjectsGroup.size();
		}
		return 0;
	}

	@Override
	public Object getItem(int position) {
		switch (mCurrentIndex) {
		case 0:
			return mObjects.get(position).getName(); 
		case 1:
			return mObjectsGroup.get(position).getGroupname(); 
		}
		return null;
	}

	//用来传递电话号码。。。或者组id
	@Override
	public long getItemId(int position) {
		switch (mCurrentIndex) {
		case 0:
			if (mObjects.get(position).getPhone() != null && !"".equals(mObjects.get(position).getPhone()))
				return Long.parseLong(getNumber(mObjects.get(position).getPhone()));
			break;
		case 1:
			if (mObjectsGroup.get(position).getGroupid() != null && !"".equals(mObjectsGroup.get(position).getGroupid()))
				return Long.parseLong(getNumber(mObjectsGroup.get(position).getGroupid()));
			break;
		}
		return 0;
	}
	
	public String getNumber(String str) {
		str = str.trim();
		StringBuilder sb = new StringBuilder();
		if (str != null && !"".equals(str)) {
			for (int i = 0; i < str.length(); i++) {
				if (str.charAt(i) >= 48 && str.charAt(i) <= 57) {
					sb.append(str.charAt(i));
				}
			}
		}
		return sb.toString();
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder holder = null;
		if (convertView == null) {
			holder = new ViewHolder();
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.item_searchlist, null);
			holder.tv = (TextView) convertView.findViewById(R.id.is_tv_searchitem);
			holder.tv2 = (TextView) convertView.findViewById(R.id.is_tv_searchitem2);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		switch (mCurrentIndex) {
		case 0:
			holder.tv.setText(mObjects.get(position).getName());
			holder.tv2.setVisibility(View.VISIBLE);
			holder.tv2.setText(mObjects.get(position).getEmail().split("@")[0]);
			break;
		case 1:
			holder.tv.setText(mObjectsGroup.get(position).getGroupname());
			holder.tv2.setVisibility(View.GONE);
			break;
		}
		return convertView;
	}

	class ViewHolder {
		TextView tv;
		TextView tv2;
	}
}
