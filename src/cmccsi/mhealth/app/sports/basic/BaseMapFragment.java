package cmccsi.mhealth.app.sports.basic;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import cmccsi.mhealth.app.sports.R;

import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.UiSettings;
import com.baidu.mapapi.model.LatLng;


public class BaseMapFragment extends BaseFragment {

	public MapView mMapView;

	public MyLocationData mLocData;
	public LocationClient mLocClient;
	
    public BaiduMap mBaiduMap;
    
    public UiSettings mUiSettings;
    public BaseMapFragment(){}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		return container;
	}
	@Override
	public void onDestroy() {
		super.onDestroy();
		if(mLocClient!=null)
			mLocClient.stop();
	}
	
	protected  void initMap(MapView mMapView){
		mBaiduMap = mMapView.getMap();
		mUiSettings=mBaiduMap.getUiSettings();
		//普通地图  
		mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);  
		/**
		 * 设置地图是否响应点击事件 .
		 */
		mMapView.setClickable(true);
		/**
		 * 显示内置缩放控件
		 */
		mMapView.showZoomControls(false);
		
		/**
		 * 设定地图中心点
		 */
		// 开启定位图层  
		mBaiduMap.setMyLocationEnabled(true);
		
		// 定位初始化
		mLocClient = new LocationClient(mActivity);
		
		//设定中心点坐标 (默认)
        LatLng cenpt = new LatLng(116.403875, 39.915168); 
        //定义地图状态
        MapStatus mMapStatus = new MapStatus.Builder().target(cenpt).zoom(18).build();
		// 定义MapStatusUpdate对象，以便描述地图状态将要发生的变化
        MapStatusUpdate mMapStatusUpdate = MapStatusUpdateFactory.newMapStatus(mMapStatus);
		// 改变地图状态
		mBaiduMap.setMapStatus(mMapStatusUpdate);
		//以动画方式更新地图状态（移动）
		mBaiduMap.animateMapStatus(mMapStatusUpdate);

		setLocationOption();
	}
	
	protected void setLocationOption() {
		LocationClientOption option = new LocationClientOption();
		
		option.setOpenGps(true);
		option.setCoorType("bd09ll");
		option.setProdName("com.baidu.location.service_v2.9");
		//设置定位精度Hight_Accuracy（高精度）、Battery_Saving（低功耗）、Device_Sensors（仅设备(Gps)模式）
		option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
		option.setAddrType("all");
		option.setScanSpan(10000);
		option.setNeedDeviceDirect(false);

		mLocClient.setLocOption(option);
	}
	@Override
	public void findViews() {
		
	}
	@Override
	public void clickListner() {
		
	}
	@Override
	public void loadLogic() {
		
	}

}
