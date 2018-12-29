package cmccsi.mhealth.app.sports.tabhost;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import cmccsi.mhealth.app.sports.activity.RankingActivity;
import cmccsi.mhealth.app.sports.adapter.CampaignRankListAdapter;
import cmccsi.mhealth.app.sports.bean.CampaignRankListInfo;
import cmccsi.mhealth.app.sports.bean.RankInfo;
import cmccsi.mhealth.app.sports.bean.RankListInfo_new;
import cmccsi.mhealth.app.sports.common.utils.ToastUtils;
import cmccsi.mhealth.app.sports.net.DataSyn;
import cmccsi.mhealth.app.sports.view.XListView;
import cmccsi.mhealth.app.sports.view.XListView.IXListViewListener;
import cmccsi.mhealth.app.sports.R;

/**
 * 活动排名fragment
 * @type CampaignRankFragment
 * TODO
 * @author shaoting.chen
 * @time 2015年7月30日下午4:54:20
 */
public class CampaignRankFragment extends Fragment implements IXListViewListener {

	private XListView mLvRankList;
	private CampaignRankListAdapter mRanklistAdapter;
	private RankListInfo_new mRankListInfo_new;
	private TextView mTvRankEmpty;
	private List<RankInfo> mDetailInfos;
	private String teamOrPerSon = "user";
	private int level = 0; //
	private String activityId;
	private int page = 2;
	private RankListInfo_new datavalue = new RankListInfo_new();
	
	private RankListInfo_new tempDatavalue = new RankListInfo_new();

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.fragment_campaign_rank,
				container, false);
		
		mLvRankList = (XListView) rootView.findViewById(R.id.lv_campaign_rank_list);
		mTvRankEmpty = (TextView) rootView.findViewById(R.id.tv_campaign_rank_empty);
		if (datavalue != null&&datavalue.rankList.size() % 100 == 0) {
			mLvRankList.setPullLoadEnable(true);
		} else {
			mLvRankList.setPullLoadEnable(false);
		}
		mLvRankList.setPullRefreshEnable(false);
		mLvRankList.setXListViewListener(this);

		mRanklistAdapter = new CampaignRankListAdapter(getActivity());
		mRankListInfo_new =datavalue;
		mDetailInfos = datavalue.rankList;

		showdata(mRankListInfo_new);

		return rootView;
	}

	public CampaignRankFragment(RankListInfo_new datavalue,String teamOrPerSon, int level, String activityId) {
		// TODO Auto-generated constructor stub
		this.datavalue = datavalue;
		this.teamOrPerSon = teamOrPerSon;
		this.level = level;
		this.activityId = activityId;
	}

	/**
	 * 
	 * TODO
	 * @param teamOrPerSon
	 * @param level
	 * @param activityId
	 * @param flag
	 * @return void
	 * @author jiazhi.cao
	 * @time 下午2:01:32
	 */
	public void setParams(String teamOrPerSon, int level, String activityId, int flag) {
		this.teamOrPerSon = teamOrPerSon;
		this.level = level;
		this.activityId = activityId;
		if(flag == 1){
			page = 2;
		}
		if (mLvRankList != null) {
			mLvRankList.setPullLoadEnable(false);
		} else {
			mLvRankList.setPullLoadEnable(true);
		}
	}

	public void transportData(RankListInfo_new datavalue) {
		this.datavalue = datavalue;
		if (mLvRankList != null&&datavalue.rankList.size() % 100 == 0) {
			mLvRankList.setPullLoadEnable(true);
		} else {
			mLvRankList.setPullLoadEnable(false);
		}
	}

	public void showdata(RankListInfo_new mBriefInfo) {
		if (mBriefInfo != null) {
			mRankListInfo_new = mBriefInfo;

			if (mRankListInfo_new.memberstep == -1) {
				mTvRankEmpty.setVisibility(View.VISIBLE);
				mTvRankEmpty.setText("活动暂无排名");
			} else {
				mTvRankEmpty.setVisibility(View.GONE);
			}
			mDetailInfos = mRankListInfo_new.rankList;
			mRanklistAdapter.setList(mDetailInfos);
			mLvRankList.setAdapter(mRanklistAdapter);
		} else {
//			Toast.makeText(getActivity(), "null====", Toast.LENGTH_LONG).show();
		}
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public void onRefresh() {
		// TODO Auto-generated method stub
	}

	@Override
	public void onLoadMore() {
		// TODO Auto-generated method stub
		mLvRankList.setPullLoadEnable(false);
		new Thread() {
			public void run() {

				CampaignRankListInfo mCampaignRankListInfo = new CampaignRankListInfo();
				int res = DataSyn.getInstance().getActivityUserRankByLevel(activityId, page + "", level + "", mCampaignRankListInfo);

				if (res == 0) {
					tempDatavalue = mCampaignRankListInfo.userRank.get(0);
					System.out.println("-----------" + tempDatavalue.rankList.size());

					if (tempDatavalue.rankList.size() > 0) {

						getActivity().runOnUiThread(new Runnable() {
							
							@Override
							public void run() {
								if (datavalue.level.equals(tempDatavalue.level)) {
									datavalue.rankList.addAll(tempDatavalue.rankList);
								}
								page = datavalue.rankList.size() / 100 + 1;
								if (datavalue.rankList.size() % 100 == 0) {
									mLvRankList.setPullLoadEnable(true);
								} else {
									mLvRankList.setPullLoadEnable(false);
								}
								mRanklistAdapter.setList(datavalue.rankList, 0);
								mRanklistAdapter.notifyDataSetChanged();
								mLvRankList.stopLoadMore();
							}
						});
					}
				}
			};
		}.start();
	}

}
