/**
 * 历史运动轨迹
 */

package cmccsi.mhealth.app.sports.activity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import cmccsi.mhealth.app.sports.basic.BaseFragment;
import cmccsi.mhealth.app.sports.basic.BaseMapFragment;
import cmccsi.mhealth.app.sports.bean.DetailGPSData;
import cmccsi.mhealth.app.sports.bean.GPSListInfo;
import cmccsi.mhealth.app.sports.bean.GpsInfoDetail;
import cmccsi.mhealth.app.sports.common.Common;
import cmccsi.mhealth.app.sports.common.Config;
import cmccsi.mhealth.app.sports.common.ImageUtil;
import cmccsi.mhealth.app.sports.common.Logger;
import cmccsi.mhealth.app.sports.common.ShowProgressDialog;
import cmccsi.mhealth.app.sports.common.UploadUtil;
import cmccsi.mhealth.app.sports.common.utils.DateFormatUtils;
import cmccsi.mhealth.app.sports.common.utils.DateFormatUtils.FormatType;
import cmccsi.mhealth.app.sports.db.MHealthProviderMetaData;
import cmccsi.mhealth.app.sports.net.DataSyn;
import cmccsi.mhealth.app.sports.pedo.ShareWeiXin;
import cmccsi.mhealth.app.sports.view.CommonAskDialog;
import cmccsi.mhealth.app.sports.view.CustomProgressDialog;
import cmccsi.mhealth.app.sports.view.CommonAskDialog.OnDialogCloseListener;
import cmccsi.mhealth.app.sports.R;

import com.baidu.mapapi.map.BaiduMap.SnapshotReadyCallback;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.model.LatLng;

public class MapViewOrbitFragment extends BaseMapFragment implements OnClickListener {

	private static final int handl_showData=501;
	private MapView mMapView;
	private List<LatLng> mAllPoints;

	private View mBack;

	private TextView mTextViewSpeed;
	private TextView mTextViewCal;
	private final GPSListInfo mGpsListInfo;
	private LinearLayout mLayoutAll;
	private CustomProgressDialog mCustomDialog;

	private BitmapDescriptor mOverlayEndItem;
	private BitmapDescriptor mOverlayStartItem;
	PolylineOptions lineGeometry = new PolylineOptions();

	public MapViewOrbitFragment(GPSListInfo listInfo) {
		this.mGpsListInfo = listInfo;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_map_orbit, container, false);
		super.onCreateView(inflater, (ViewGroup) view, savedInstanceState);
		return view;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	@Override
	public void loadLogic() {
		super.loadLogic();
		
		String starttime = mGpsListInfo.getStarttime();
		mAllPoints = new ArrayList<LatLng>();
		ShowProgressDialog.showProgressDialog(getResources().getString(R.string.text_wait), getActivity(), false);
		mGpsInfoArr = MHealthProviderMetaData.GetMHealthProvider(mActivity).getGpsInfoDetails(starttime);
		if(mGpsInfoArr!=null&&mGpsInfoArr.size()>0){
			handler.sendEmptyMessage(handl_showData);
		}else{	
			new Thread(new DownloadDetailRunable()).start();
		}
		if(mGpsListInfo.getIsUpload() == 1){
        	showAskDialog();
        }

	}
	
	/**
	 * 画地图点
	 * @param mGpsInfoArr
	 */
	private void drawGpsDetail(List<GpsInfoDetail> mGpsInfoArr){
		if (mGpsInfoArr != null && mGpsInfoArr.size() > 1) {

			for (int i = 1; i < mGpsInfoArr.size(); i++) {
				mAllPoints.add(new LatLng(mGpsInfoArr.get(i).getLatitude() , mGpsInfoArr.get(i).getLongtitude() ));
				//标注起点
				if(i == mGpsInfoArr.size() - 1){
					LatLng fristPoint = new LatLng(mGpsInfoArr.get(mGpsInfoArr.size() - 1).getLatitude(), mGpsInfoArr.get(mGpsInfoArr.size() - 1).getLongtitude());
					
					mOverlayStartItem = BitmapDescriptorFactory.fromResource(R.drawable.img_map_start);
					OverlayOptions ooA = new MarkerOptions().position(fristPoint).icon(mOverlayStartItem);
					mBaiduMap.addOverlay(ooA);
				}
				//标注终点
				if (i == 1) {
					LatLng lastPoint = new LatLng(mGpsInfoArr.get(0).getLatitude(), mGpsInfoArr.get(0).getLongtitude());
					mOverlayEndItem = BitmapDescriptorFactory.fromResource(R.drawable.img_map_end);
					OverlayOptions ooA = new MarkerOptions().position(lastPoint).icon(mOverlayEndItem);
					mBaiduMap.addOverlay(ooA);
				}
				drawLine(mGpsInfoArr.get(i), mGpsInfoArr.get(i - 1));
			}
			//定位中心点，并进行合理缩放
			Common.fitPoints(mAllPoints, mBaiduMap);

		}
		showDetailData(mGpsListInfo);
		
	}
	
	/**
	 * 显示轨迹数据
	 * @param mGpsListInfo
	 */
	private void showDetailData(GPSListInfo mGpsListInfo){
		float tmpCal = mGpsListInfo.getDistance()/1000f;
        if(tmpCal>10){
            mTextViewCal.setText(String.format("%.2f", tmpCal));            
        }else{
            mTextViewCal.setText(String.format("%.2f", tmpCal));
        }
        float tmpSpeed = mGpsListInfo.getSpeed();
        if(tmpCal>10){
            mTextViewSpeed.setText(String.format("%.2f", tmpSpeed));         
        }else{
            mTextViewSpeed.setText(String.format("%.2f", tmpSpeed));
        }
        ShowProgressDialog.dismiss();
	}
	int lineColor ;
	public void drawLine(GpsInfoDetail mNowPoints,GpsInfoDetail mLastPoints) {
		LatLng[] linePoints = new LatLng[2];// 数组不能存null！！
		LatLng geoPoint1 = null ;
		LatLng geoPoint2 = null;
		geoPoint1 = new LatLng((mNowPoints.getLatitude()), (mNowPoints.getLongtitude()));
		geoPoint2 = new LatLng(mLastPoints.getLatitude(), mLastPoints.getLongtitude());
		if (mNowPoints.getIsStopPoint() == 1) {
			lineColor = Color.argb(255, 195, 195, 195);
			System.out.println("------11111111111111111---------");
		} else {
			lineColor = Color.argb(255, 143, 195, 32);
			System.out.println("------22222222222222222---------");
		}
		linePoints[0] = geoPoint2;
		linePoints[1] = geoPoint1;
		// setOnMarkClickListener();
		OverlayOptions ooPolyline = lineGeometry.width(10).color(lineColor).points(Arrays.asList(linePoints));
		mBaiduMap.addOverlay(ooPolyline);
	}
	private void drawLine(List<LatLng> mAllPoints) {
		LatLng[] linePoints = new LatLng[mAllPoints.size()];// 数组不能存null！！
		Log.e("size", "mPoints 的 大小 = " + mAllPoints.size());

		for (int i = 0; i < mAllPoints.size(); i++) {
			linePoints[i] = mAllPoints.get(i);

			if(i==0)
			{
				// 开启定位图层
				mBaiduMap.setMyLocationEnabled(true);
				// 定义地图状态
				MapStatus _mMapStatus = new MapStatus.Builder().target(linePoints[i]).zoom(18).build();
				// 定义MapStatusUpdate对象，以便描述地图状态将要发生的变化
				MapStatusUpdate mMapStatusUpdate = MapStatusUpdateFactory.newMapStatus(_mMapStatus);
				// 改变地图状态
				mBaiduMap.setMapStatus(mMapStatusUpdate);
				mBaiduMap.animateMapStatus(mMapStatusUpdate);
				
				mOverlayStartItem = BitmapDescriptorFactory.fromResource(R.drawable.img_map_start);
				OverlayOptions ooA = new MarkerOptions().position(linePoints[i]).icon(mOverlayStartItem);
				mBaiduMap.addOverlay(ooA);
			}
			
			if (i == mAllPoints.size() - 1) {

				mOverlayEndItem = BitmapDescriptorFactory.fromResource(R.drawable.img_map_end);
				OverlayOptions ooA = new MarkerOptions().position(linePoints[i]).icon(mOverlayStartItem);
				mBaiduMap.addOverlay(ooA);

			}
		}
		
		// mLineGeometry.setPolyLine(linePoints);
		OverlayOptions ooPolyline = lineGeometry.width(10).color(Color.argb(255, 143, 195, 32)).points(Arrays.asList(linePoints));
		mBaiduMap.addOverlay(ooPolyline);
	}

	@Override
	public void findViews() {
		super.findViews();
		
		mBack = findView(R.id.button_input_bg_back);
		mBack.setBackgroundResource(R.drawable.my_button_back);
		mBack.setVisibility(View.VISIBLE);
		mBack.setOnClickListener(new backClick(new MapListGPSFragment()));

		mLayoutAll = findView(R.id.content_details);

		ImageButton mImageButtonUpdate = findView(R.id.imageButton_title);
		mImageButtonUpdate.setBackgroundResource(R.drawable.bg_gps_share);
		mImageButtonUpdate.setVisibility(View.VISIBLE);
		mImageButtonUpdate.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mBaiduMap.snapshot(callback);
				if (mCustomDialog != null && mCustomDialog.isShowing()) {
					mCustomDialog.dismiss();
				}
				mCustomDialog = CustomProgressDialog.createDialog(mActivity, false);
				mCustomDialog.setMessage(getResources().getString(R.string.text_wait));
				mCustomDialog.show();
			}
		});

		mTextViewSpeed = findView(R.id.textview_speed);
		mTextViewCal = findView(R.id.textview_cal);

		// mButtonOpenMap.setOnClickListener(this);
		mTextViewTitle.setText("轨迹");		

	}

	class backClick implements OnClickListener {

		BaseFragment to;

		public backClick(BaseFragment to) {
			super();
			this.to = to;
		}

		@Override
		public void onClick(View v) {
			getActivity().onBackPressed();
		}

	}

	private void initMap() {
		mMapView = (MapView) findView(R.id.map_start);
		/**
		 * 获取地图控制器
		 */
		// mMapController = mMapView.getController();
		mBaiduMap = mMapView.getMap();
		mUiSettings = mBaiduMap.getUiSettings();
		/**
		 * 设置地图是否响应点击事件 .
		 */
		// mMapController.enableClick(true);
		mMapView.setClickable(true);
		/**
		 * 显示内置缩放控件
		 */
		// mMapView.setBuiltInZoomControls(false);
		mMapView.showZoomControls(false);
		/**
		 * 是否启用旋转手势
		 */
		// mMapController.setZoom(15);
		// mMapController.setRotationGesturesEnabled(true);
		mUiSettings.setRotateGesturesEnabled(true);
		// 缩放手势
		// mMapController.setZoomGesturesEnabled(true);
		mUiSettings.setZoomGesturesEnabled(true);
		// // 双击方大
		// mMapView.setDoubleClickZooming(true);
		mMapView.showScaleControl(true);
		/**
		 * 是否启用平移手势
		 */
		// mMapController.setScrollGesturesEnabled(true);
//		mUiSettings.setScrollGesturesEnabled(true);
//
//		mBaiduMap.setMyLocationEnabled(true);
//		LatLng p = new LatLng(39.933859, 116.400191);
//		// 定义地图状态
//		MapStatus _mMapStatus = new MapStatus.Builder().target(p).zoom(18).build();
//		// 定义MapStatusUpdate对象，以便描述地图状态将要发生的变化
//		MapStatusUpdate mMapStatusUpdate = MapStatusUpdateFactory.newMapStatus(_mMapStatus);
//		// 改变地图状态
//		mBaiduMap.setMapStatus(mMapStatusUpdate);
//		mBaiduMap.animateMapStatus(mMapStatusUpdate);
//		mOverlayStartItem = BitmapDescriptorFactory.fromResource(R.drawable.img_map_start);
//		OverlayOptions ooA = new MarkerOptions().position(p).icon(mOverlayStartItem);
//		mBaiduMap.addOverlay(ooA);
//		// 当不需要定位图层时关闭定位图层
//		mBaiduMap.setMyLocationEnabled(false);
	}

	@Override
	public void onResume() {
		initMap();
		if (mMapView != null) {
			mMapView.onResume();
		}

        handler.sendEmptyMessageDelayed(500, 5000);

		super.onResume();
	}

	@Override
	public void onPause() {
		if (mMapView != null) {
			mMapView.onPause();
		}
		super.onPause();
	}

	@Override
	public void onDestroy() {
		if(mOverlayStartItem!=null)
		{
			mOverlayStartItem.recycle();
		}
		if(mOverlayEndItem!=null)
		{
			mOverlayEndItem.recycle();
		}
		if (mMapView != null) {
			mMapView.onDestroy();
			mMapView = null;
		}
		super.onDestroy();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		// case R.id.button_open_map:
		// openOrCloseMap();
		// break;
		case R.id.button_get_location:
			Common.fitPoints(mAllPoints, mBaiduMap);
			// mMapView.refresh();
			break;

		default:
			break;
		}
	}

	Handler handler = new Handler() {
		@Override
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case R.string.MESSAGE_UPLOAD_GPS_SUCCESS:
				BaseToast(getResources().getString(R.string.mapvieworbitfragment_messageuploadgpssuccess));
				ShowProgressDialog.dismiss();
				break;
			case R.string.MESSAGE_GPS_NODATA:
				BaseToast(getResources().getString(R.string.mapvieworbitfragment_messagegpsnodata));
				ShowProgressDialog.dismiss();
				break;
			case R.string.MESSAGE_SYNCHRO_GPS_SUCCESS:
				Logger.i("MapViewOrbitFragment", "---数据同步成功！");
				BaseToast(getResources().getString(R.string.mapvieworbitfragment_messagesynchrogpssuccess));
				ShowProgressDialog.dismiss();
				break;
			case 500:
				Logger.i("REF", " ref -----------");
//				Common.fitPoints(mAllPoints, mBaiduMap);
				// mMapView.refresh();
				ShowProgressDialog.dismiss();
				break;
			case handl_showData:
				try {
					drawGpsDetail(mGpsInfoArr);		
				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
				}
					
				break;
			default:
				BaseToast("上传失败");
				ShowProgressDialog.dismiss();
				break;
			}
		};
	};
	private List<GpsInfoDetail> mGpsInfoArr;

	SnapshotReadyCallback callback = new SnapshotReadyCallback() {
		/**
		 * 地图截屏回调接口
		 * 
		 * @param snapshot
		 *            截屏返回的 bitmap 数据
		 */
		@Override
		public void onSnapshotReady(Bitmap snapshot) {
			mCustomDialog.dismiss();
//			try{
//				ImageUtil.getInstance().saveFile(snapshot, Config.ERRORLOG_URL, System.currentTimeMillis()+".jpg");
//			}catch(IOException e){
//				e.printStackTrace();
//			}
			
			String shareDate = DateFormatUtils.DateToString(new Date(), FormatType.DateWithDiagonalNoYear);
			ShareWeiXin share = new ShareWeiXin(getMyActivity(), shareDate, snapshot, mLayoutAll);
			share.setShareWithQR(true);
			share.Shared();
		}

	};
	
	private void showAskDialog(){
		String[] buttons = { "确定", "", "取消" };
		CommonAskDialog mAskDialog = CommonAskDialog.create(getResources().getString(R.string.mapvieworbitfragment_dailogmsg), buttons, false, true);
		mAskDialog.setAlertIconVisible(-1);
		mAskDialog.setOnDialogCloseListener(new OnDialogCloseListener() {
			@Override
			public void onClick(int which) {
				if (which == CommonAskDialog.BUTTON_OK) {
					ShowProgressDialog.showProgressDialog("正在上传", mActivity);
					new Thread() {
						public void run() {
							// 上传数据
							List<GPSListInfo> infos = new ArrayList<GPSListInfo>();
							infos.add(mGpsListInfo);
							Message msg = new Message();
							msg.what = UploadUtil.upload(mActivity, infos);
							handler.sendMessage(msg);
						};
					}.start();
				}
			}
		});
		mAskDialog.show(mActivity.getSupportFragmentManager(), "CommonAskDialog");
	}
	
	private class DownloadDetailRunable implements Runnable{

		@Override
		public void run() {
			DetailGPSData gpsInfoDetail = new DetailGPSData();
			// 获取详细包
			int result = DataSyn.getInstance().getDetailsGpsData(gpsInfoDetail,
					mGpsListInfo.getStarttime(), mActivity);
			if (result == 0) {
				// 插入详细包
				MHealthProviderMetaData.GetMHealthProvider(mActivity).insertAllPoints(gpsInfoDetail,
						mGpsListInfo.getStarttime());
				for(int i=0;i<gpsInfoDetail.datavalue.size();i++){
					GpsInfoDetail data=new GpsInfoDetail();
					data.setAltitude(gpsInfoDetail.datavalue.get(i).getAltitude());
					data.setDetailtime(gpsInfoDetail.datavalue.get(i).getDetailtime());
					data.setSpeed(gpsInfoDetail.datavalue.get(i).getSpeed());
					data.setLatitude(gpsInfoDetail.datavalue.get(i).getLatitude());
					data.setLongtitude(gpsInfoDetail.datavalue.get(i).getLongtitude());
					data.setStarttime(mGpsListInfo.getStarttime());
					data.setDistance(gpsInfoDetail.datavalue.get(i).getDistance());
					data.setCal(gpsInfoDetail.datavalue.get(i).getCal());
					data.setIsStopPoint(gpsInfoDetail.datavalue.get(i).getIsStopPoint());
					mGpsInfoArr.add(data);
				}
				handler.sendEmptyMessage(handl_showData);
			}
		}
		
	}
}
