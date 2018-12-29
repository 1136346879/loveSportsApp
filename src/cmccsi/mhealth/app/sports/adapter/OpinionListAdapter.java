package cmccsi.mhealth.app.sports.adapter;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import cmccsi.mhealth.app.sports.appversion.OpinionInstance;
import cmccsi.mhealth.app.sports.R;

public class OpinionListAdapter extends BaseAdapter {
	// 此处的Object最好是Map<String, Object>,可以方便记清楚数据
	private ArrayList<OpinionInstance> opinionList;
	private Context context;
	private LayoutInflater mInflater;
	private OpinionInstance itemOpinion;

	public OpinionListAdapter(Context context, ArrayList<OpinionInstance> list) {
		super();
		this.context = context;
		this.mInflater = LayoutInflater.from(context);
		this.opinionList = list;
	}

	@SuppressWarnings("unchecked")
	public void setDeviceList(ArrayList<OpinionInstance> list) {
		if (list != null) {
			opinionList = (ArrayList<OpinionInstance>) list.clone();
			notifyDataSetChanged();
		}
	}

	public void clearDeviceList() {
		if (opinionList != null) {
			opinionList.clear();
		}
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return null == opinionList ? 0 : opinionList.size();
	}

	@Override
	public Object getItem(int position) {
		return null == opinionList ? null : opinionList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@SuppressLint("InflateParams")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		HoldView hold;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.adapter_opinion_list_item, null);
			hold = new HoldView();
			hold.initView(convertView);
			convertView.setTag(hold);
		} else {
			hold = (HoldView) convertView.getTag();
		}
		itemOpinion = opinionList.get(position);
		// 对UI内的组件进行操作控制
		hold.time.setText(itemOpinion.getCreateTime());
		// 0：未回复 1：已回复
		if (1 == itemOpinion.getReplyMark()) {
			hold.isReply.setVisibility(View.VISIBLE);
		} else {
			hold.isReply.setVisibility(View.GONE);
		}
		hold.title.setText("> " + itemOpinion.getFeedbackTitle());
		return convertView;
	}

	// 内部类，操作要填充的view部件内容
	static class HoldView {
		TextView title;
		TextView time;
		TextView isReply;

		public void initView(View convertView) {
			title = (TextView) convertView.findViewById(R.id.adapter_opinion_title);
			time = (TextView) convertView.findViewById(R.id.adapter_opinion_time);
			isReply = (TextView) convertView.findViewById(R.id.adapter_opinion_isreply);
		}
	}
}
