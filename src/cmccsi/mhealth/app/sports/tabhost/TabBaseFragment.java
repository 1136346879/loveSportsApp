package cmccsi.mhealth.app.sports.tabhost;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Toast;
import cmccsi.mhealth.app.sports.activity.MapStartRunningFragment;
import cmccsi.mhealth.app.sports.appversion.SettingFragmentApp;
import cmccsi.mhealth.app.sports.basic.BaseActivity;
import cmccsi.mhealth.app.sports.basic.BaseFragment;
import cmccsi.mhealth.app.sports.basic.BaseFragment.OnFragmentDestroyListener;
import cmccsi.mhealth.app.sports.common.Config;
import cmccsi.mhealth.app.sports.common.ConstantsBitmaps;
import cmccsi.mhealth.app.sports.common.SharedPreferredKey;
import cmccsi.mhealth.app.sports.common.ShowProgressDialog;
import cmccsi.mhealth.app.sports.net.DataSyn;
import cmccsi.mhealth.app.sports.R;

public class TabBaseFragment extends BaseActivity implements  BackHandledInterface{
    Fragment fragment;
    private SharedPreferences mSharedInfo;
    private String mPhoneNum;
    private String mPassword;
    private int clubId;
    Fragment mContent;
    // FragmentActivity mActivity;
    private SharedPreferences info;
    private MapStartRunningFragment mfragment;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.frament_activity);
        info = getSharedPreferences(SharedPreferredKey.SHARED_NAME, 0);
        mSharedInfo = getSharedPreferences(SharedPreferredKey.SHARED_NAME, 0);
        clubId = mSharedInfo.getInt(SharedPreferredKey.CLUB_ID, -1);
        int i = getIntent().getIntExtra("intent", 12);
        Log.d("iii", i + "  f");
        intentActivity(i);

    }
    
//    @Override
//    public void onBackPressed() {
//    	ShowProgressDialog.dismiss();
//    	super.onBackPressed();
//    }

    @Override
    protected void onResume() {
        super.onResume();
        ConstantsBitmaps.initRunPics(this);
    }

    public void mgetActivity() {

    }

    public void back() {
        onKeyDown(KeyEvent.KEYCODE_BACK, null);
        // finish();

    }

    private void intentActivity(int tag) {
        switch (tag) {
        case 0:
//        	fragment =new PedometorFragment(Constants.PedoBriefActivity);
//            //fragment = new PedomViewPaperFragment();
//            getSupportFragmentManager().beginTransaction()
//                    .replace(R.id.frament_activity, fragment).commit();
            break;
        case 1:
            fragment = new MapFragment();
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.frament_activity, fragment).commit();
            break;
        case 2:
            fragment = new HistoryListFragment();
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.frament_activity, fragment).commit();
            break;
        case 3:
//            fragment = new WeightFragment();
//            getSupportFragmentManager().beginTransaction()
//                    .replace(R.id.frament_activity, fragment).commit();
            break;
        case 4:
            //fragment = new RankFragment(clubId);
        	fragment=new RankCompanyMenuFragment();
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.frament_activity, fragment).commit();
            break;
        case 5:
//            fragment = new CampaignFragment1();
            fragment = new CampaignFragment_new();
        	//fragment=new RankAreaMenuFragment();
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.frament_activity, fragment).commit();
            break;
        case 6:
            fragment = new WebViewFragment(DataSyn.strAccountHttpURL
                    + "rank", false);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.frament_activity, fragment).commit();
            break;
        case 7:
            fragment = new FriendFragment();
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.frament_activity, fragment).commit();
            break;
        case 8:
//            fragment = new RaceFragment();
//            getSupportFragmentManager().beginTransaction()
//                    .replace(R.id.frament_activity, fragment).commit();
            break;
        case 9:
            fragment = new MessageFragment();
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.frament_activity, fragment).commit();
            break;
        case 10:
            fragment = new WebViewFragment("http://"
                    + info.getString(SharedPreferredKey.SERVER_NAME, "")
                    // + "10.111.111.113:8080/data_new"
                    + "/account.do?action=knowledge", false);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.frament_activity, fragment).commit();
            break;
        case 11:
        	if(Config.ISALONE){
        		fragment = new SettingFragmentApp();
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.frament_activity, fragment).commit();
        	}else{
        		fragment = new SettingFragment();
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.frament_activity, fragment).commit();
        	}
            
            break;
        case 12:
            mfragment = new MapStartRunningFragment("tab");
            if (mfragment.isAdded()) {
                getSupportFragmentManager().beginTransaction().hide(fragment)
                        .add(0, mfragment).commit();
            } else {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.frament_activity, mfragment).commit();
            }

            break;
        case 13:
            fragment = new GoalFragment();
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.frament_activity, fragment).commit();
            break;
            
        case 14://心境
            fragment = new ECGFragment();
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.frament_activity, fragment).commit();
            break;
        case 15:
        	fragment=new RankAreaMenuFragment();
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.frament_activity, fragment).commit();
            break;
        default:
            break;
        }
    }

    public void switchContent(BaseFragment newContent) {
        FragmentManager sfm = getSupportFragmentManager();
        FragmentTransaction transaction = sfm.beginTransaction();
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        transaction.replace(R.id.frament_activity, newContent);
        transaction.addToBackStack(null);
        transaction.commitAllowingStateLoss();
    }

    private BackHandledFragment mBackHandedFragment;
	@Override
	public void setSelectedFragment(BackHandledFragment selectedFragment) {
		this.mBackHandedFragment = selectedFragment;
	}
	
	@Override
	public void onBackPressed() {
		ShowProgressDialog.dismiss();
		if(mBackHandedFragment == null || !mBackHandedFragment.onBackPressed()){
			if(getSupportFragmentManager().getBackStackEntryCount() == 0){
				super.onBackPressed();
			}else{
				super.getSupportFragmentManager().popBackStack();
			}
		}
	}
}
