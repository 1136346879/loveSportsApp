package cmccsi.mhealth.app.sports.tabhost;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import cmccsi.mhealth.app.sports.activity.RankingActivity;
import cmccsi.mhealth.app.sports.adapter.ActivityRankListAdapter;
import cmccsi.mhealth.app.sports.adapter.RankingListAdapter;
import cmccsi.mhealth.app.sports.bean.ActivityMedalInfo;
import cmccsi.mhealth.app.sports.bean.PedoRankBriefInfo;
import cmccsi.mhealth.app.sports.bean.PedoRankDetailInfo;
import cmccsi.mhealth.app.sports.bean.PedoRankListInfo;
import cmccsi.mhealth.app.sports.common.ImageUtil;
import cmccsi.mhealth.app.sports.common.Logger;
import cmccsi.mhealth.app.sports.common.PreferencesUtils;
import cmccsi.mhealth.app.sports.common.SharedPreferredKey;
import cmccsi.mhealth.app.sports.common.utils.ToastUtils;
import cmccsi.mhealth.app.sports.controller.PedoRankController;
import cmccsi.mhealth.app.sports.net.DataSyn;
import cmccsi.mhealth.app.sports.view.XListView;
import cmccsi.mhealth.app.sports.view.XListView.IXListViewListener;
import cmccsi.mhealth.app.sports.R;

/**
 * @author hjn
 * 
 */
public class RankinglistFragment extends Fragment implements IXListViewListener {

	private TextView mTvUserOrg;
	private TextView mTvStepNum;
	private TextView mTvRankNo;
	private XListView mLvRankList;
	private RankingListAdapter mRanklistAdapter;
	private PedoRankBriefInfo mPedoRankBriefInfo;
	private TextView mTvRanknull;
	private List<PedoRankDetailInfo> mDetailInfos;
	private ImageView mIvUserPhoto;
	private String oneOrSeven = "1";
	private String teamOrPerSon = "user";
	private String groupOrArea = "0";
	private String userId;
	private int OrgId;
	private int index = 0;
	private int pageForOne = 2;
	private int pageForSeven = 2;
	private ArrayList<PedoRankBriefInfo> datavalue = new ArrayList<PedoRankBriefInfo>();
	private PedoRankController mPedoRankController;
	private LinearLayout ll_self;
	
	private ArrayList<PedoRankBriefInfo> tempDatavalue;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.fragment_rankinglist,
				container, false);
		mTvUserOrg = (TextView) rootView.findViewById(R.id.tv_user_org);
		mTvStepNum = (TextView) rootView.findViewById(R.id.tv_userstepnum);
		mTvRankNo = (TextView) rootView.findViewById(R.id.tv_userrankno);
		mLvRankList = (XListView) rootView.findViewById(R.id.lv_rankinglist);
		mTvRanknull = (TextView) rootView.findViewById(R.id.tv_ranknull);
		ll_self=(LinearLayout)rootView.findViewById(R.id.self);
		mLvRankList.setPullLoadEnable(true);
		mLvRankList.setPullRefreshEnable(false);
		mLvRankList.setXListViewListener(this);
		mPedoRankController = PedoRankController
				.GetPedoRankBriefController(getActivity());
		getAvatar();

		mRanklistAdapter = new RankingListAdapter(getActivity());
		mPedoRankBriefInfo = new PedoRankBriefInfo();
		mDetailInfos = new ArrayList<PedoRankDetailInfo>();

		Bundle bundle = getArguments();
		if (bundle != null) {
			mPedoRankBriefInfo = (PedoRankBriefInfo) bundle
					.getSerializable(RankingActivity.ARGUMENTS_RANK);
			showdata(mPedoRankBriefInfo);
		} else {
//			ToastUtils.showToast(getActivity(), "null");
		}

		return rootView;
	}

	public RankinglistFragment(ArrayList<PedoRankBriefInfo> datavalue, String groupOrArea) {
		// TODO Auto-generated constructor stub
		this.datavalue = datavalue;
		this.groupOrArea = groupOrArea;
	}

	public void setParams(String oneOrSeven, String teamOrPerSon,
			String groupOrArea, int index, int flag) {
		this.oneOrSeven = oneOrSeven;
		this.teamOrPerSon = teamOrPerSon;
		this.groupOrArea = groupOrArea;
		this.index = index;
		if(flag==1){
			pageForSeven = 2;
			pageForOne = 2;
		}
		if (!teamOrPerSon.equals("user") && mLvRankList != null) {
			mLvRankList.setPullLoadEnable(false);
		} else {
			mLvRankList.setPullLoadEnable(true);
		}
	}

	public void transportData(ArrayList<PedoRankBriefInfo> datavalue) {
		this.datavalue = datavalue;
		if (datavalue.get(index).getRankList().size() % 100 == 0
				&&datavalue.get(index).getRankList().size()>0
				&& mLvRankList != null) {
			mLvRankList.setPullLoadEnable(true);
		} else {
			mLvRankList.setPullLoadEnable(false);
		}
	}

	public void showdata(PedoRankBriefInfo mBriefInfo) {
		if (mBriefInfo != null) {
			mPedoRankBriefInfo = mBriefInfo;

			Logger.i("---getMemberstep",
					"" + mPedoRankBriefInfo.getMemberstep());
			if (mPedoRankBriefInfo.getMemberstep() == -1) {
				
				if(groupOrArea.equals("0")){
//					mTvRanknull.setText("加入区域组织后，次日方可查看您的排名");
					mTvRanknull.setVisibility(View.GONE);
					mTvRanknull.setText("加入区域组织后，次日方可查看您的排名");
				}else{
					mTvRanknull.setVisibility(View.VISIBLE);
					mTvRanknull.setText("加入企业组织后，次日方可查看您的排名");
				}
				ll_self.setVisibility(View.GONE);
				mTvStepNum.setText("");
				mTvRankNo.setText("");
				mTvUserOrg.setText(mPedoRankBriefInfo.getMembername());
			} else {
				mTvRanknull.setVisibility(View.GONE);
				ll_self.setVisibility(View.VISIBLE);
				mTvStepNum.setText(mPedoRankBriefInfo.getMemberstep() + "步");
				mTvRankNo.setText("第" + mPedoRankBriefInfo.getMemberrank()
						+ "名");
				mTvUserOrg.setText(mPedoRankBriefInfo.getMembername());
			}
			mDetailInfos = mPedoRankBriefInfo.getRankList();
			mRanklistAdapter.setList(mDetailInfos);
			mLvRankList.setAdapter(mRanklistAdapter);
			if(mDetailInfos!=null&&mDetailInfos.size()>0&&mDetailInfos.size()%100==0){
				mLvRankList.setPullLoadEnable(true);
			} else {
				mLvRankList.setPullLoadEnable(false);
			}
		} else {
//			Toast.makeText(getActivity(), "null====", Toast.LENGTH_LONG).show();
		}
	}

	/**
	 * 展示用户头像图片
	 * 
	 * @return void
	 * @author zhangfengjuan
	 * @time 上午10:32:12
	 */
	private void getAvatar() {
		String mAvatarName = getActivity().getSharedPreferences(
				SharedPreferredKey.SHARED_NAME, 0).getString(
				SharedPreferredKey.AVATAR, null);
		if (null != mAvatarName) {
			getImageAsync(mIvUserPhoto, mAvatarName);
		}
	}

	private Drawable getImageAsync(ImageView holder, String url) {
		return getImageAsync(holder, url, null, 0);
	}

	private Drawable getImageAsync(ImageView holder, String url, String tag,
			int mode) {
		return ImageUtil.getInstance().loadBitmap(holder, url, tag, mode);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}

	private Handler handler = new Handler() {
		public void dispatchMessage(Message msg) {
			switch (msg.what) {
			case 0:
				showdata(datavalue.get(index));
				break;
			case 2:
				if(oneOrSeven.equals("1")){
					pageForOne = datavalue.get(index).getRankList().size() / 100 + 1;
				}else{
					pageForSeven = datavalue.get(index).getRankList().size() / 100 + 1;
				}
				if (datavalue.get(index).getRankList().size()>0&&
						datavalue.get(index).getRankList().size() % 100 == 0) {
					mLvRankList.setPullLoadEnable(true);
				} else {
					mLvRankList.setPullLoadEnable(false);
				}
				mRanklistAdapter.setList(datavalue.get(index).getRankList(), 0);
				mRanklistAdapter.notifyDataSetChanged();
				mLvRankList.stopLoadMore();
				break;
			}
		};
	};

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
				userId = PreferencesUtils.getString(getActivity(),
						SharedPreferredKey.USERUID, "");
				if (groupOrArea.equals("0")) {
					OrgId = PreferencesUtils.getInt(getActivity(),
							SharedPreferredKey.COUNTY_ID, 0);
				} else {
					OrgId = PreferencesUtils.getInt(getActivity(),
							SharedPreferredKey.CLUB_ID, 0);
				}

				PedoRankListInfo mPedoRankListInfo = new PedoRankListInfo();
				int res;
				if(oneOrSeven.equals("1")){
					res = DataSyn.getInstance().getPedoRankListInfo(userId,
							OrgId + "", oneOrSeven, teamOrPerSon,
							Integer.parseInt(groupOrArea), mPedoRankListInfo,
							pageForOne + "");
				}else{
					res = DataSyn.getInstance().getPedoRankListInfo(userId,
							OrgId + "", oneOrSeven, teamOrPerSon,
							Integer.parseInt(groupOrArea), mPedoRankListInfo,
							pageForSeven + "");
				}
				if (res == 0) {
					tempDatavalue = mPedoRankListInfo.datavalue;
					System.out.println("-----------" + tempDatavalue.size());

//					mPedoRankController.InsertPedoRankBriefList(tempDatavalue,
//							oneOrSeven, teamOrPerSon, groupOrArea);
					if (!tempDatavalue.isEmpty()) {

//						handler.sendEmptyMessage(2);
						
						getActivity().runOnUiThread(new Runnable() {
							
							@Override
							public void run() {
//								// TODO Auto-generated method stub
								for (int i = 0; i < tempDatavalue.size(); i++) {
									if (datavalue.get(index).level == tempDatavalue
											.get(i).level) {
										datavalue.get(index).rankList
												.addAll(tempDatavalue.get(i).rankList);
									}
								}
								if(oneOrSeven.equals("1")){
									pageForOne = datavalue.get(index).getRankList().size() / 100 + 1;
								}else{
									pageForSeven = datavalue.get(index).getRankList().size() / 100 + 1;
								}
								if (datavalue.get(index).getRankList().size() % 100 == 0) {
									mLvRankList.setPullLoadEnable(true);
								} else {
									mLvRankList.setPullLoadEnable(false);
								}
								mRanklistAdapter.setList(datavalue.get(index).getRankList(), 0);
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
