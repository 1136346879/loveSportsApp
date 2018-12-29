package cmccsi.mhealth.app.sports.activity;

import java.util.ArrayList;
import java.util.List;

import cmccsi.mhealth.app.sports.adapter.CampaignListAdapter;
import cmccsi.mhealth.app.sports.basic.BaseFragment;
import cmccsi.mhealth.app.sports.bean.ActivityInfo;
import cmccsi.mhealth.app.sports.bean.ListActivity;
import cmccsi.mhealth.app.sports.common.Logger;
import cmccsi.mhealth.app.sports.common.PreferencesUtils;
import cmccsi.mhealth.app.sports.common.SharedPreferredKey;
import cmccsi.mhealth.app.sports.common.utils.ToastUtils;
import cmccsi.mhealth.app.sports.net.DataSyn;
import cmccsi.mhealth.app.sports.view.CustomDialogOK;
import cmccsi.mhealth.app.sports.view.XListView;
import cmccsi.mhealth.app.sports.view.XListView.IXListViewListener;
import cmccsi.mhealth.app.sports.R;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;

public class CampaignViewFragment_new extends BaseFragment implements IXListViewListener{

	private XListView listview;
	private List<ListActivity> listItems;
	private List<ListActivity> templistItems;
	private CampaignListAdapter listAdapter;
	private static final int SUCCESS = 88;
	private static final int FAIL = 89;
	private ActivityInfo mActivityInfo;
	private String mPhonenum;
	private String mPassword;
	private int ClubId = 1;
	private int page=2;
	private int type=0;//活动类型2：敬请期待  1：进行中  0：已结束 3：所有
	public String userNm = "";
	public String userId = "";
	public Intent intent;
	public String actividId;

	public CampaignViewFragment_new(List<ListActivity> listItems, int type) {
		super();
		this.listItems = listItems;
		this.type = type;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_campaignlist, container,
				false);
		ClubId = PreferencesUtils.getInt(getActivity(),
				SharedPreferredKey.CLUB_ID, 0);
		userNm = PreferencesUtils.getString(getActivity(),
				SharedPreferredKey.NAME, null);
		userId = PreferencesUtils.getString(getActivity(),
				SharedPreferredKey.USERID, null);
		super.onCreateView(inflater, (ViewGroup) view, savedInstanceState);
		return view;
	}
    

	private void initViews() {
		TextView tv_emptyView = findView(R.id.tv_emptyView);
		templistItems=new ArrayList<ListActivity>();
		listview = findView(R.id.lv_campaignlist);
		listview.setPullRefreshEnable(false);
		if(listItems==null||listItems.size()==0||
				listItems.size()%10>0){
			listview.setPullLoadEnable(false);
		}else{
			listview.setPullLoadEnable(true);
		}
		listAdapter = new CampaignListAdapter(listItems, getActivity());
		listview.setAdapter(listAdapter);
		listview.setXListViewListener(this);

		listview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				intent = new Intent();
				ListActivity has = listItems.get(position-1);
				String titlName = has.activityname;
				actividId = has.activityid;
				String actividType = has.activitytype;
				Logger.d("testing", "actividType==" + actividType);
				String activityslogan = has.activityslogan;
				intent.putExtra("ACTIVITYDESCRIPTION", activityslogan);
				intent.putExtra("ACTIVITYTITLE", titlName);
				intent.putExtra("ACTIVITYID", actividId);
				intent.putExtra("ACTIVITYTYPE", actividType);
				intent.putExtra("PHONENUM", mPhonenum);
				intent.putExtra("PASSWORD", mPassword);
				intent.putExtra("CLUBID", ClubId);
				
				if (has.isJoin == 0) {
					if (activityslogan == null ||activityslogan.isEmpty() ) {
						activityslogan = getResources().getString(R.string.campaigncontentactivity_noactivitydescription1);
					}
					showDialog(titlName, activityslogan);
					return;
				}
				
				if (has.isRanked==0) {
					BaseToast(getResources().getString(R.string.campaigncontentactivity_noaverage));
					return;
				}
				intent.setClass(mActivity, CampaignContentActivity_new.class);
				startActivity(intent);
				mActivity.overridePendingTransition(R.anim.slide_in_right,
						R.anim.silde_out_left);
				
			}
		});
	}

	@Override
	public void findViews() {
		try {
			initViews();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void clickListner() {
		// TODO Auto-generated method stub

	}

	@Override
	public void loadLogic() {
		// TODO Auto-generated method stub

	}

	public String getmPhonenum() {
		return mPhonenum;
	}

	public void setmPhonenum(String mPhonenum) {
		this.mPhonenum = mPhonenum;
	}

	public String getmPassword() {
		return mPassword;
	}

	public void setmPassword(String mPassword) {
		this.mPassword = mPassword;
	}

	public int getClubId() {
		return ClubId;
	}

	public void setClubId(int clubId) {
		ClubId = clubId;
	}

	public void showDialog(String title, String content) {
		final CustomDialogOK dialog = new CustomDialogOK(getActivity(),
				R.style.customdialog, R.layout.customdialog, title, content);
		dialog.show();

	}
	
	private void getmoreActivity(int page,int type){
		mActivityInfo=new ActivityInfo();
		int OrgId=PreferencesUtils.getInt(mActivity, SharedPreferredKey.CLUB_ID, 0);
		if (DataSyn.getInstance().getActivityInfo_new(type,OrgId, page , mActivityInfo) == 0) {
			mhandle.sendEmptyMessage(SUCCESS);
		}else{
			mhandle.sendEmptyMessage(FAIL);
		}
	}
	
	Handler mhandle=new Handler(){

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case SUCCESS:
				page++;
				templistItems.clear();
				//活动类型2：敬请期待  1：进行中  0：已结束 3：所有
				if(type==2){
					templistItems=mActivityInfo.activityfuture;

				}else if(type==1){
					templistItems=mActivityInfo.activitynow;

				}else if(type==0){
					templistItems=mActivityInfo.activityfinish;
					
				}

				if(templistItems==null||templistItems.size()==0||
						templistItems.size()%10>0){
					listview.setPullLoadEnable(false);
				}else{
					listview.setPullLoadEnable(true);
				}
				listItems.addAll(templistItems);
				listAdapter.notifyDataSetChanged();
				break;
			case FAIL:
				ToastUtils.showToast(mActivity, getResources().getString(R.string.campaigncontentactivity_getactivelistinformationfalse));
				break;
			default:
				break;
			}
			listview.stopLoadMore();
		}
		
	};

	@Override
	public void onRefresh() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onLoadMore() {
		listview.setPullLoadEnable(false);
		Logger.d("cjz", "加载更多活动列表");
		new Thread(new Runnable() {			
			@Override
			public void run() {
				Logger.d("cjz", "加载更多活动列表");
				getmoreActivity(page, type);
			}
		}).start();	
	}
}
