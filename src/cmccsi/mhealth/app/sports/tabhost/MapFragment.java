package cmccsi.mhealth.app.sports.tabhost;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import cmccsi.mhealth.app.sports.activity.FragmentContainerActivity;
import cmccsi.mhealth.app.sports.activity.MapListGPSFragment;
import cmccsi.mhealth.app.sports.activity.MapStartRunningFragment;
import cmccsi.mhealth.app.sports.basic.BaseFragment;
import cmccsi.mhealth.app.sports.basic.BaseMapFragment;
import cmccsi.mhealth.app.sports.basic.MapApplication;
import cmccsi.mhealth.app.sports.bean.GPSListInfo;
import cmccsi.mhealth.app.sports.bean.GpsInfoDetail;
import cmccsi.mhealth.app.sports.common.Common;
import cmccsi.mhealth.app.sports.common.Constants;
import cmccsi.mhealth.app.sports.common.Logger;
import cmccsi.mhealth.app.sports.common.PreferencesUtils;
import cmccsi.mhealth.app.sports.common.SharedPreferredKey;
import cmccsi.mhealth.app.sports.common.utils.ToastUtils;
import cmccsi.mhealth.app.sports.db.MHealthProviderMetaData;
import cmccsi.mhealth.app.sports.net.NetworkTool;
import cmccsi.mhealth.app.sports.service.StepService_GPS;
import cmccsi.mhealth.app.sports.R;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationConfiguration.LocationMode;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;

public class MapFragment extends BaseMapFragment implements BDLocationListener,
        OnClickListener {
    private static final int HISTORY_DATA = 0;
    private RelativeLayout mRLayoutGetHistoryData;
    private View mBack;
    private int mTypeID = 1;
    private Dialog gpsDialog;
    public static final String KEY_TYPE_ID = "typeId";
    
    private BDLocation mCenterLocation = null;
    
    private ImageView mIvGps;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /**
         * 使用地图sdk前需先初始化BMapManager. BMapManager是全局的，可为多个MapView共用，它需要地图模块创建前创建，
         * 并在地图地图模块销毁后销毁，只要还有地图模块在使用，BMapManager就不应该销毁
         */
        locationManager = (LocationManager) getActivity()
                .getApplicationContext().getSystemService(
                        Context.LOCATION_SERVICE);
        // 重要函数，监听数据测试
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                1000, 10, new MyGPSLinster());
        bIsGPS = false;

        unSaveData();
    }

    private void showGPSDialog() {
        if (gpsDialog == null) {
            gpsDialog = new Dialog(getActivity(), R.style.Dialog_GPS);
            View view = ((LayoutInflater) getActivity().getSystemService(
                    Context.LAYOUT_INFLATER_SERVICE)).inflate(
                    R.layout.layout_gps_dialog, null);
            Button btn_openGPS = (Button) view.findViewById(R.id.btn_openGPS);
            btn_openGPS.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    // 跳转到网络设置
                    Intent intent = new Intent(
                            Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(intent);
                    gpsDialog.dismiss();
                }
            });
            Button btn_cancel = (Button) view.findViewById(R.id.btn_cancel);
            btn_cancel.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    gpsDialog.dismiss();
                }
            });
            gpsDialog.setContentView(view);
            gpsDialog.setCanceledOnTouchOutside(true);
        }
        gpsDialog.show();
    }

    private void unSaveData() {
        float cal = 0;
        float distance = 0;
        String firsttime = null;
        String endttime = null;
        String duration;
        int sec = 0;
        String starttime = PreferencesUtils.getString(getActivity(),
                SharedPreferredKey.START_TIME, null);
        if (starttime != null) {// 异常崩溃未保存的数据
            // 获取所有详细包
            final GPSListInfo gpsListInfo = new GPSListInfo();
            List<GpsInfoDetail> mListGpsDetails = MHealthProviderMetaData
                    .GetMHealthProvider(getActivity()).getGpsInfoDetails(
                            starttime);
            if (mListGpsDetails.size() < 2) {
                // 一条数据,不保存
                PreferencesUtils.removeSp(getActivity(),
                        SharedPreferredKey.START_TIME);
                return;
            }
            for (int i = 0; i < mListGpsDetails.size(); i++) {
                if (i == 0) {
                    firsttime = mListGpsDetails.get(i).getDetailtime();
                } else if (i == mListGpsDetails.size() - 1) {
                    endttime = mListGpsDetails.get(i).getDetailtime();
                }
                cal += mListGpsDetails.get(i).getCal();
                distance += mListGpsDetails.get(i).getDistance();
            }
            if (firsttime == null && endttime == null)
                return;

            SimpleDateFormat dateFormat = new SimpleDateFormat(
                    "yyyy-MM-dd HH:mm:ss");
            try {
                java.util.Date parsef = dateFormat.parse(firsttime);
                java.util.Date parsee = dateFormat.parse(endttime);
                sec = (int) ((parsee.getTime() - parsef.getTime()) / 1000);
                duration = Common.sec2Time(sec);
                gpsListInfo.setDuration(duration);
            } catch (ParseException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            gpsListInfo.setStarttime(starttime);
            gpsListInfo.setCal(cal);
            gpsListInfo.setDistance(distance);
            gpsListInfo.setSporttype(Constants.RUNTYPE);// TODO 数据库添加类型字段
            gpsListInfo.setIsUpload(1);
            gpsListInfo.setSpeed((distance / 1000 / sec) * 3600);
            MHealthProviderMetaData.GetMHealthProvider(getActivity())
                    .insertGpsListInfo(gpsListInfo);
            PreferencesUtils.removeSp(getActivity(),
                    SharedPreferredKey.START_TIME);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.map_fragment, container, false);
        view.setTag("inflated");
        super.onCreateView(inflater, (ViewGroup) view, savedInstanceState);
        return view;
    }

    @Override
    public void findViews() {
        super.findViews();
        mBack = findView(R.id.button_input_bg_back);
        mBack.setBackgroundResource(R.drawable.my_button_back);

        mBack.setVisibility(View.VISIBLE);
        mBack.setOnClickListener(new backClick(new MapListGPSFragment()));
        mImageButtonBack.setVisibility(View.VISIBLE);
        mImageButtonBack.setBackgroundResource(R.drawable.back_button_bg);
        mMapView = (MapView) findView(R.id.simpleMap);
        mRLayoutGetHistoryData = findView(R.id.imageButton_title_add);
        mRLayoutGetHistoryData.setVisibility(View.VISIBLE);
        mRLayoutGetHistoryData.findViewById(R.id.textview_title_add)
                .setBackgroundResource(R.drawable.top_menu);

        initMap(mMapView);
		mBaiduMap
		.setMyLocationConfigeration(new MyLocationConfiguration(
				LocationMode.NORMAL, true, null));
		mBaiduMap.setMyLocationEnabled(true);
        /**
		 * 是否启用旋转手势
		 */
		mUiSettings.setRotateGesturesEnabled(false);

		// 缩放手势
		mUiSettings.setZoomGesturesEnabled(false);
		// // 双击方大
		/**
		 * 是否启用平移手势
		 */
		mUiSettings.setScrollGesturesEnabled(false);
        
        ImageView imageCycle = findView(R.id.imageview_cycle_type);
        imageCycle.setOnClickListener(this);
        ImageView imageRun = findView(R.id.imageview_run_type);
        imageRun.setOnClickListener(this);
        ImageView imageWalk = findView(R.id.imageview_walk_type);
        imageWalk.setOnClickListener(this);
        
        mIvGps = findView(R.id.iv_map_start_gps);
    }

    @Override
    public void clickListner() {
        super.clickListner();
        mTextViewTitle.setText("路线");
        mRLayoutGetHistoryData.setOnClickListener(this);
    }

    @Override
    public void loadLogic() {
        super.loadLogic();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onActivityCreated(savedInstanceState);
        this.getView().setFocusable(true);
        this.getView().setFocusableInTouchMode(true);
        this.getView().requestFocus();

//        this.getView().setOnKeyListener(new OnKeyListener() {
//
//            @Override
//            public boolean onKey(View v, int keyCode, KeyEvent event) {
//                if (event.getAction() == KeyEvent.ACTION_UP
//                        && keyCode == KeyEvent.KEYCODE_BACK) {
//                    Log.d("onkey", "onkey有效");
//                    Intent i = new Intent(getActivity(), MainFragmentActivity.class);
//                    startActivity(i);
//                    getActivity().finish();
//                    return true;
//                } else
//                    return false;
//            }
//        });
    }

    class backClick implements OnClickListener {

        BaseFragment to;

        public backClick(BaseFragment to) {
            super();
            this.to = to;
        }

        @Override
        public void onClick(View v) {
            // getActivity().onBackPressed();
//            Intent i = new Intent(getActivity(), MainFragmentActivity.class);
//            startActivity(i);
            getActivity().finish();
        }

    }

    LocationManager locationManager;// =(LocationManager)this.getSystemService(Context.LOCATION_SERVICE);

    private boolean bIsGPS;
    private double mAlitude;

    private class MyGPSLinster implements LocationListener {
        StringBuilder builder = new StringBuilder();

        // 用户位置改变的时候 的回调方法
        @Override
        public void onLocationChanged(Location location) {
            if (location != null) {
                bIsGPS = true;
//				LatLng ll = new LatLng(location.getLatitude(),
//						location.getLongitude());
//				MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(ll);				
//				mBaiduMap.animateMapStatus(u);
            } else {
                bIsGPS = false;

                toast("xxx");
            }

        }

        // 状态改变
        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }

        // gps ,打开
        @Override
        public void onProviderEnabled(String provider) {
        }

        // 关闭
        @Override
        public void onProviderDisabled(String provider) {
        }
    }

    @Override
    public void onClick(View v) {
        final TabBaseFragment fca = (TabBaseFragment) getActivity();
        switch (v.getId()) {
        case R.id.button_input_bg_back:
            getActivity().finish();
            break;
        case R.id.imageButton_title_add:
        	fca.switchContent(new MapListGPSFragment());
            break;

        case R.id.imageview_run_type:
        	if (!NetworkTool.isGPSOPen(getActivity())) {
                showGPSDialog();
            } else if (bIsGPS) {
            	mTypeID = 2;
            	Constants.RUNTYPE = mTypeID;
//                MapStartRunningFragment fragment = new MapStartRunningFragment(
//                        "map");
//                fca.switchContent(fragment);
            	Intent it=new Intent();
            	it.setClass(mActivity, FragmentContainerActivity.class);

            	if(mCenterLocation != null){
            		it.putExtra("center_latitude", mCenterLocation.getLatitude());
                	it.putExtra("center_longitude", mCenterLocation.getLongitude());
            	}
            	
            	startActivity(it);
            	
            } else {
                ToastUtils.showToast(getActivity(), "GPS信号弱，请到开阔的户外再试");
            }
        	break;
        case R.id.imageview_walk_type:
        	if (!NetworkTool.isGPSOPen(getActivity())) {
                showGPSDialog();
            } else if (bIsGPS) {
            	mTypeID = 1;
            	Constants.RUNTYPE = mTypeID;
//                MapStartRunningFragment fragment = new MapStartRunningFragment(
//                        "map");
//                fca.switchContent(fragment);
            	Intent it=new Intent();
            	it.setClass(mActivity, FragmentContainerActivity.class);
            	if(mCenterLocation != null){
            		it.putExtra("center_latitude", mCenterLocation.getLatitude());
                	it.putExtra("center_longitude", mCenterLocation.getLongitude());
            	}
            	startActivity(it);
            } else {
                ToastUtils.showToast(getActivity(), "GPS信号弱，请到开阔的户外再试");
            }
        	break;
        case R.id.imageview_cycle_type:
        	if (!NetworkTool.isGPSOPen(getActivity())) {
                showGPSDialog();
            } else if (bIsGPS) {
            	mTypeID = 3;
            	Constants.RUNTYPE = mTypeID;
//                MapStartRunningFragment fragment = new MapStartRunningFragment(
//                        "map");
//                fca.switchContent(fragment);
            	Intent it=new Intent();
            	it.setClass(mActivity, FragmentContainerActivity.class);
            	if(mCenterLocation != null){
            		it.putExtra("center_latitude", mCenterLocation.getLatitude());
                	it.putExtra("center_longitude", mCenterLocation.getLongitude());
            	}
            	startActivity(it);
            } else {
                ToastUtils.showToast(getActivity(), "GPS信号弱，请到开阔的户外再试");
            }
        	break;

        default:
            break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mMapView != null) {
            mMapView.onResume();
            mMapView.setVisibility(View.VISIBLE);
        }
        if (mLocClient != null) {
            location();
            mLocClient.start();
            BDLocation location = mLocClient.getLastKnownLocation();
            if (location != null){
            	LatLng ll = new LatLng(location.getLatitude(),location.getLongitude());
				MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(ll);
				mBaiduMap.animateMapStatus(u);
            }
        }
    }

    @Override
    public void onDestroy() {
        Logger.i("Mapfragment", "onDestroy()");
        if (mMapView != null) {
        	
            try {
            	mMapView.onDestroy();
				mMapView = null;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        try {
            // TODO
            if (StepService_GPS.isRunning) {
                MapApplication app = (MapApplication) mActivity
                        .getApplication();
                app.finishReceive();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        super.onDestroy();
    }

    @Override
    public void onPause() {
        if (mMapView != null) {
            mMapView.onPause();
            mMapView.setVisibility(View.VISIBLE);
        }

        if (mLocClient != null) {
            mLocClient.stop();
            mLocClient.unRegisterLocationListener(this);
        }
        super.onPause();
    }

    private void location() {
        mLocClient.registerLocationListener(this);
    }

    @Override
    public void onReceiveLocation(BDLocation location) {
        if (location == null)
            return;
        // 过滤有效点
        if(Common.checkPoint(location)){
        	mIvGps.setBackgroundResource(R.drawable.map_gps_q);
		}else{
			mIvGps.setBackgroundResource(R.drawable.map_gps_r);
		}
        mCenterLocation = location;

        initLocationData(location);
    }

	private void initLocationData(BDLocation location) {

		// 经过测试，图层添加上去就不需要再次添加了，只需要更改数据！！
		mLocData = new MyLocationData.Builder().accuracy(location.getRadius()).direction(location.getDirection())
				.latitude(location.getLatitude()).longitude(location.getLongitude()).build();
		mBaiduMap.setMyLocationData(mLocData);
		// 新的中心点
		LatLng ll = new LatLng(location.getLatitude(), location.getLongitude());
		/**
		 * 设置地图缩放级别
		 */
		MapStatus mMapStatus = new MapStatus.Builder().target(ll).zoom(18).build();

		// 定义MapStatusUpdate对象，以便描述地图状态将要发生的变化
		MapStatusUpdate mMapStatusUpdate = MapStatusUpdateFactory.newMapStatus(mMapStatus);
		// 改变地图状态
		mBaiduMap.setMapStatus(mMapStatusUpdate);
		mBaiduMap.animateMapStatus(mMapStatusUpdate);

	}

}
