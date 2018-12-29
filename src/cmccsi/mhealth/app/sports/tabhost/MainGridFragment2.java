package cmccsi.mhealth.app.sports.tabhost;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.R.bool;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.Toast;
import cmccsi.mhealth.app.sports.common.Constants;
import cmccsi.mhealth.app.sports.common.Logger;
import cmccsi.mhealth.app.sports.common.PreferencesUtils;
import cmccsi.mhealth.app.sports.common.SharedPreferredKey;
import cmccsi.mhealth.app.sports.common.utils.ToastUtils;
import cmccsi.mhealth.app.sports.pedo.PedometerActivity;
import cmccsi.mhealth.app.sports.pedo.PedometerActivityTest;
import cmccsi.mhealth.app.sports.view.CustomDialog;
import cmccsi.mhealth.app.sports.R;

public class MainGridFragment2 extends Fragment implements OnClickListener, 

OnItemClickListener {
	
	public static final int TAB_HEALTH = 0;
	public static final int TAB_CORPORATION = 1;
	public static final int TAB_PLAY = 2;
	
	private int tabId;
	
	View view;
	private GridView gd_main;
	private SimpleAdapter gridViewAdapter;
	private List<Map<String, Object>> gridItemView;
	private	Intent intent;
	private ImageView selectedIcon;
	private boolean hasECG=false;//是否包含心境模块
	private Map<String, Object> EcgItem;
	
	public MainGridFragment2(){
		this.tabId = 0;
	}
	public MainGridFragment2(int tabId){
		this.tabId = tabId;
	} 
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		view = inflater.inflate(R.layout.fragment_main_grid,
				container, false);
		Logger.d("cjz", "---MainGridFragment2----onCreateView-----");
		hasECG=PreferencesUtils.getBoolean(getActivity(), SharedPreferredKey.HAVE_BRACELET_DEVICE, false);
		
		initView();
		intent = new Intent();
		return view;

	}
	@Override
	public void onResume() {
        super.onResume();
		boolean temphasECG = PreferencesUtils.getBoolean(getActivity(),
				SharedPreferredKey.HAVE_BRACELET_DEVICE, false);
		Logger.d("cjz", "原来是否有心境" + hasECG + "现在是否有心境" + temphasECG);
		if(hasECG!=temphasECG){
			hasECG=temphasECG;
			if (temphasECG) {
				EcgItem = new HashMap<String, Object>();
				EcgItem.put("playimg", R.drawable.bg_grid_ecg);
				EcgItem.put("play", "心境");
				EcgItem.put("intent", 14);
				gridItemView.add(EcgItem);
			} else {
				gridItemView.remove(EcgItem);
			}
			gridViewAdapter.notifyDataSetChanged();
		}
		if (selectedIcon != null) {
			selectedIcon.setSelected(false);
		}
	}
	private void initView() {
        gridItemView = new ArrayList<Map<String, Object>>();
        
        initGridView();
    }
	/**
     * 初始化gridview
     */
    private void initGridView() {
    	gd_main = (GridView) view.findViewById(R.id.gd_main);
    	gd_main.setSelector(new ColorDrawable(Color.TRANSPARENT));
        
        loadGridViewData(tabId);
        initAdapter();
        gd_main.setAdapter(gridViewAdapter);
        gd_main.setOnItemClickListener(this);
        
    }
    /**
     * 添加item
     * 
     * @param iconRes
     * @param itemDetails
     * @param launchId
     */
    private void addItem(int iconRes, String itemDetails, int launchId) {
        Map<String, Object> item = new HashMap<String, Object>();
        item.put("playimg", iconRes);
        item.put("play", itemDetails);
        item.put("intent", launchId);
        gridItemView.add(item);
    }
    /**
     * 初始化适配器
     */
    private void initAdapter() {
        String[] gridContent = new String[] { "playimg", "play" };
        int[] gridItemId = new int[] { R.id.tab_main_gridview_img,
                R.id.tab_main_gridview_text };
        gridViewAdapter = new SimpleAdapter(getActivity(), gridItemView,
                R.layout.tab_main_gridview_layout, gridContent, gridItemId);
    }

    /**
     * 加载gridview中的数据
     * 
     * @param tag 	
     */
    private void loadGridViewData(int tag) {
        switch (tag) {
        case TAB_HEALTH:
            loadDataPage1();
            break;
        case TAB_CORPORATION:
            laodDataPage2();
            break;
        case TAB_PLAY:
            loadDataPage3();
            break;
        default:
            loadDataPage1();
            break;
        }
    }

    private void loadDataPage3() {
        addItem(R.drawable.bg_grid_daren, "每日达人", 6);
        addItem(R.drawable.bg_grid_haoyou, "伙伴", 7);
        addItem(R.drawable.bg_grid_jingsai, "比赛", 8);
        addItem(R.drawable.bg_grid_message, "消息", 9);
        //暂时注释掉，以后有可能改动
//        addItem(R.drawable.bg_grid_zhishi, "知识", 10);
    }

    private void laodDataPage2() {
//        if (isClubInfoAvailable()) {
            addItem(R.drawable.grid_qiye, "企业", 4);
            addItem(R.drawable.grid_quyu, "区域", 15);
//        }else {
//            addItem(R.drawable.grid_qiye_selected, "企业", 4);
//            addItem(R.drawable.grid_quyu_selected, "区域", 15);
//        }
    }

    private void loadDataPage1() {
        addItem(R.drawable.bg_grid_health, "运动", 0);
        addItem(R.drawable.bg_grid_gui, "路线", 1);
        addItem(R.drawable.grid_qiye, "企业", 4);
        addItem(R.drawable.grid_quyu, "区域", 15);
        addItem(R.drawable.bg_grid_daren, "每日达人", 6);
//        addItem(R.drawable.bg_grid_lishi, "运动测试", 16);
//        addItem(R.drawable.bg_grid_wight, "体重", 3);
        
        //隐藏掉 成就模块（在运动界面显示成就）
//        addItem(R.drawable.bg_grid_goal, "成就", 13);
        
        //判断是否绑定有手环设备，有则显示 心境 功能，无则不显示
        if(hasECG){
//        	addItem(R.drawable.bg_grid_ecg, "心境", 14);
        	EcgItem = new HashMap<String, Object>();
        	EcgItem.put("playimg", R.drawable.bg_grid_ecg);
        	EcgItem.put("play", "心境");
        	EcgItem.put("intent", 14);
            gridItemView.add(EcgItem);
        }
//        addItem(R.drawable.bg_grid_daren, "好友", 7);
    }
    /**
     * club信息是否正常获取
     * @return
     */
    private boolean isClubInfoAvailable(){
        return PreferencesUtils.getInt(getActivity(), SharedPreferredKey.CLUB_ID, Constants.DEFAULT_CLUBID) != Constants.DEFAULT_CLUBID;
    }
    
    /**
     * club信息是否正常获取
     * @return
     */
    private boolean isCityInfoAvailable(){
        return PreferencesUtils.getInt(getActivity(), SharedPreferredKey.COUNTY_ID, 0) != 0;
    }
    
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
        if (v.getId() == R.id.iv_showGoal) {
        	String mDeviceId = PreferencesUtils.getString(getActivity(), SharedPreferredKey.DEVICE_ID, null);
			if(mDeviceId != null){
				//intent.setClass(getActivity(), PedometorActivity.class);
	        	intent.setClass(getActivity(), PedometerActivity.class);
		        startActivity(intent);
			}else{
				ToastUtils.showToast(getActivity(), "请先绑定运动设备！");
			}
			
		}
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub
		Map<String, Object> gridItem = gridItemView.get(arg2);
	    int i = (Integer) gridItem.get("intent");
        if (i == 4) {
            if (!isClubInfoAvailable()) {
            	ToastUtils.showToast(getActivity(), "您不是企业用户，无企业相关信息");
                return;
            }
        }
        else if(i==15)
        {
        	if (!isCityInfoAvailable()) {
//        		ToastUtils.showToast(getActivity(), "请您到设置页完善区域组织信息");
        		isShowAreaInfo();
                return;
            }
        }
        //心境功能需要Android 4.3
        if(i==14) {
        	if(Build.VERSION.SDK_INT<18) {
        		Toast.makeText(getActivity(), "您的Android系统版本过低，暂不支持心境功能", Toast.LENGTH_SHORT).show();
        		return;
        	}
        }
        selectedIcon = (ImageView) arg1
                .findViewById(R.id.tab_main_gridview_img);
        selectedIcon.setSelected(true);
        intent.putExtra("intent", i);
        if (!handleItemClick(i)) {
            intent.setClass(getActivity(), TabBaseFragment.class);
            startActivity(intent);
        }
	}
	/**
	 * TODO 展示区域信息对话框
	 * 
	 * @return void
	 * @author zhangfengjuan
	 * @time 下午5:02:20
	 */
	private void isShowAreaInfo() {

		// 从接口获取"省市县"的信息，未设置，弹出对话框，提示前往设置。
		CustomDialog.showDialog(getActivity(), R.string.dialog_area_title, R.string.dialog_area_message, true,
				R.drawable.ic_dialog_error, getString(R.string.dialog_area_yes), new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// // 进入设置页面
						Intent intent = new Intent(getActivity(), TabBaseFragment.class);
						intent.putExtra("intent", 11);
						startActivity(intent);
//						getActivity().finish();
						dialog.dismiss();
					}
				}, getString(R.string.dialog_area_no), new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}

				});

	}
	
	/**
     * 处理item点击事件，过滤特殊item
     * @return
     */
    private boolean handleItemClick(int launchId) {
        switch (launchId) {
        case 0:
//        	int deviceType= PreferencesUtils.getInt(getActivity(), SharedPreferredKey.DEVICE_TYPE, 0);
//        	if(deviceType==2)
//        	{
//        		intent.setClass(getActivity(), PedometorActivity.class);
//        	}
//        	else
//        	{
//        		intent.setClass(getActivity(), PedometerActivity.class);
////        	}
//            startActivity(intent);
//            
        	String mDeviceId = PreferencesUtils.getString(getActivity(), SharedPreferredKey.DEVICE_ID, null);
        	if(mDeviceId != null){
	        	intent.setClass(getActivity(), PedometerActivity.class);
		        startActivity(intent);
		        getActivity().overridePendingTransition(R.anim.slide_in_right,R.anim.silde_out_left);
			}else{
				ToastUtils.showToast(getActivity(), "请先到设置页面激活运动设备！");
				selectedIcon.setSelected(false);
			}
    		return true;
        case 16:
        	intent.setClass(getActivity(), PedometerActivityTest.class);
	        startActivity(intent);
	        getActivity().overridePendingTransition(R.anim.slide_in_right,R.anim.silde_out_left);
	        return true;
        default:
            break;
        }
		return false;
    }

}
