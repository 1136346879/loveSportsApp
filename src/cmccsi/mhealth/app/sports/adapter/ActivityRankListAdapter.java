package cmccsi.mhealth.app.sports.adapter;

import java.util.List;

import cmccsi.mhealth.app.sports.adapter.RankingListAdapter.ViewHolder;
import cmccsi.mhealth.app.sports.bean.GroupRankInfo;
import cmccsi.mhealth.app.sports.bean.UserRankInfo;
import cmccsi.mhealth.app.sports.common.ConstantsBitmaps;
import cmccsi.mhealth.app.sports.view.ScoreBarView;
import cmccsi.mhealth.app.sports.R;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ActivityRankListAdapter extends BaseAdapter{
	
	private LayoutInflater mInflater = null;
	private Context mContext = null;
	private ViewHolder mViewHolder = null;
	private int mMaxValue;
	private List<UserRankInfo> userRankInfo ;
	private List<GroupRankInfo> groupRankInfo ;
	private int flag  = -1;
	
	public ActivityRankListAdapter(Context context, List<UserRankInfo> list ,int flag) {
		// TODO Auto-generated constructor stub
		mContext = context;
		this.userRankInfo = list;
		this.flag = flag;
	}
	
	public ActivityRankListAdapter(Context context, List<GroupRankInfo> list) {
		// TODO Auto-generated constructor stub
		mContext = context;
		this.groupRankInfo = list;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		if(flag == 0){
			return userRankInfo.size();
		}else{
			return groupRankInfo.size();
		}
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		if(flag == 0){
			return userRankInfo.get(position);
		}else{
			return groupRankInfo.get(position);
		}
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		if (convertView == null) {
			mViewHolder = new ViewHolder();
			mInflater = LayoutInflater.from(mContext);
			convertView = mInflater.inflate(R.layout.list_item_activity_rank, null);
			mViewHolder.mTvRankSeq = (TextView) convertView.findViewById(R.id.textview_rank_seq);
			mViewHolder.mTvName = (TextView) convertView.findViewById(R.id.textview_member_name);
			mViewHolder.mTvGroupName = (TextView) convertView.findViewById(R.id.textview_group_name);
			mViewHolder.mTvStepNum = (TextView) convertView.findViewById(R.id.tv_stepNum);
			mViewHolder.mIvRankFirst = (ImageView) convertView.findViewById(R.id.imageview_rankidfirst);
			mViewHolder.mSbvLine = (ScoreBarView) convertView.findViewById(R.id.regularprogressbar);
			convertView.setTag(mViewHolder);
		} else {
			mViewHolder = (ViewHolder) convertView.getTag();
		}
		if(flag == 0){
			mViewHolder.mTvRankSeq.setText(userRankInfo.get(position).rank + "");
			mViewHolder.mTvName.setText(userRankInfo.get(position).name);
			mViewHolder.mTvGroupName.setText(userRankInfo.get(position).groupname);
			mViewHolder.mTvStepNum.setText(userRankInfo.get(position).value + userRankInfo.get(position).unit);
			if (position < 3) {
//				mViewHolder.mSbvLine.setPics(ConstantsBitmaps.mLeftPic, ConstantsBitmaps.mRunPicGreen);
				if (position == 0) {
					// mViewHolder.mIvRankFirst.setVisibility(View.VISIBLE);
					mMaxValue = userRankInfo.get(position).value;
				}
			}
			mViewHolder.mSbvLine.setMaxValue(mMaxValue);
			mViewHolder.mSbvLine.setPics(ConstantsBitmaps.mRunPicYellow);
			mViewHolder.mSbvLine.setScore(userRankInfo.get(position).value);
		}else{
			mViewHolder.mTvRankSeq.setText(groupRankInfo.get(position).rank + "");
			mViewHolder.mTvName.setText(groupRankInfo.get(position).groupname + "");
			mViewHolder.mTvStepNum.setText(groupRankInfo.get(position).value + groupRankInfo.get(position).unit);
			if (position < 3) {
//				mViewHolder.mSbvLine.setPics(ConstantsBitmaps.mLeftPic, ConstantsBitmaps.mRunPicGreen);
				if (position == 0) {
					// mViewHolder.mIvRankFirst.setVisibility(View.VISIBLE);
					mMaxValue = groupRankInfo.get(position).value;
				}
			}
			mViewHolder.mSbvLine.setMaxValue(mMaxValue);
			mViewHolder.mSbvLine.setPics(ConstantsBitmaps.mRunPicYellow);
			mViewHolder.mSbvLine.setScore(groupRankInfo.get(position).value);
		}
		return convertView;
	}
	
	class ViewHolder {
		ImageView mIvRankFirst;
		TextView mTvRankSeq;
		TextView mTvName;
		TextView mTvStepNum;
		TextView mTvGroupName;
		ScoreBarView mSbvLine;
	}

}
