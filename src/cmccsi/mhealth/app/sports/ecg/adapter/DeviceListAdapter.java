package cmccsi.mhealth.app.sports.ecg.adapter;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import cmccsi.mhealth.app.sports.activity.FirmwareDownloadProgressActivity;
import cmccsi.mhealth.app.sports.bean.DeviceListInfo;
import cmccsi.mhealth.app.sports.common.Common;
import cmccsi.mhealth.app.sports.common.Config;
import cmccsi.mhealth.app.sports.common.utils.ToastUtils;
import cmccsi.mhealth.app.sports.device.DeviceConstants;
import cmccsi.mhealth.app.sports.ecg.activity.DeviceSettingActivityTest;
import cmccsi.mhealth.app.sports.R;

/**
 * 设备列表adapter
 * @type DeviceListAdapter
 * TODO
 * @author shaoting.chen
 * @time 2015年4月11日上午10:34:48
 */
public class DeviceListAdapter extends BaseAdapter {
	private int firstSelectPosition = -1;
	private LayoutInflater inflater;
	private List<String> data = null;
	private Context mContext;
	private DeviceListInfo mDeviceListInfo = null;
	DeviceSettingActivityTest mDeviceSettingActivityTest;

	public DeviceListAdapter(Context context, List<String> data) {
		inflater = LayoutInflater.from(context);
		this.data = data;
		this.mContext = context;
		this.mDeviceSettingActivityTest = (DeviceSettingActivityTest) context;
	}
	
	public void setDeviceListInfo(DeviceListInfo deviceListInfo, List<String> data) {
		this.mDeviceListInfo = deviceListInfo;
		this.data = data;
	}

	public void clear() {
		if(mDeviceListInfo != null){
			mDeviceListInfo.datavalue.clear();
			mDeviceListInfo = null;
		}
		if(data != null){
			data.clear();
			data = null;
		}
	}
	@Override
	public int getCount() {
		return data.size();
	}

	@Override
	public String getItem(int position) {
		return data.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View view, ViewGroup parent) {
		ViewHolder holder;
		
		if (view == null) {
			view = inflater.inflate(R.layout.list_item_device, parent, false);
			holder=new ViewHolder();
			holder.device= (TextView) view.findViewById(R.id.tv_device);
			holder.device_image= (ImageView) view.findViewById(R.id.iv_image_device);
			holder.rl_update=(RelativeLayout) view.findViewById(R.id.rl_device_setting_update);
			holder.btn_update=(Button)view.findViewById(R.id.btn_device_setting_update);
			view.setTag(holder);

		}else{
			holder=(ViewHolder)view.getTag();
		}

		holder.device.setText(getItem(position));

		int deviceType = Common.getDeviceType(mDeviceListInfo.datavalue.get(position).deviceSerial, mDeviceListInfo.datavalue.get(position).productPara);
		if (deviceType == DeviceConstants.DEVICE_PEDOMETER) {
			String pedoType = mDeviceListInfo.datavalue.get(position).deviceSerial.substring(0, 3);
			if(pedoType.equals("728")){
				holder.device_image.setBackground(mContext.getResources().getDrawable(R.drawable.device_pedo_728));
			}else if(pedoType.equalsIgnoreCase("801")){
				if(mDeviceListInfo.datavalue.get(position).deviceSerial.substring(0, 4).equalsIgnoreCase("801d")){
					holder.device_image.setBackground(mContext.getResources().getDrawable(R.drawable.device_pedo_801d));
				}else{
					holder.device_image.setBackground(mContext.getResources().getDrawable(R.drawable.device_pedo_801a));
				}				
			}else if(pedoType.equals("901")){
				holder.device_image.setBackground(mContext.getResources().getDrawable(R.drawable.device_pedo_901));
			}else{
				holder.device_image.setBackground(mContext.getResources().getDrawable(R.drawable.device_pedo_728));
			}
		} else if (deviceType == DeviceConstants.DEVICE_BRACLETE_BEATBAND) {
			holder.device_image.setBackground(mContext.getResources().getDrawable(R.drawable.device_bracelet));
		} else if (deviceType == DeviceConstants.DEVICE_BRACLETE_JW) {
			holder.device_image.setBackground(mContext.getResources().getDrawable(R.drawable.tb_4_720));
		} else if (deviceType == DeviceConstants.DEVICE_BRACLETE_JW201) {
			holder.device_image.setBackground(mContext.getResources().getDrawable(R.drawable.tb_5_720));
		} else {
			holder.device_image.setBackground(mContext.getResources().getDrawable(R.drawable.device_mobile));
		}

		// View flag = view.findViewById(R.id.flag);
		if (firstSelectPosition == position) {
			// flag.setSelected(true);
			holder.device.setTextColor(mContext.getResources().getColor(R.color.bracelet_selected));
		} else {
			// flag.setSelected(false);
			holder.device.setTextColor(mContext.getResources().getColor(R.color.bracelet_normal));
		}
		//独立版固件升级
		if(Config.ISALONE){

			if(mDeviceListInfo.datavalue.get(position).productName.equals("BeatBand手环") || mDeviceListInfo.datavalue.get(position).productPara.equals("SMARTPHONE_BT")){
				holder.rl_update.setVisibility(View.VISIBLE);
				int strokeWidth = 3; // 3dp 边框宽度
			    int roundRadius = 25; // 8dp 圆角半径
			    int strokeColor = Color.parseColor("#fe0202");//边框颜色
			    int fillColor = Color.parseColor("#00000000");//内部填充颜色
			    GradientDrawable gd = new GradientDrawable();//创建drawable
			    gd.setColor(fillColor);
			    gd.setCornerRadius(roundRadius);
			    if(mDeviceListInfo.datavalue.get(position).updateMark.equals("1")){
			    	strokeColor = Color.parseColor("#fe0202");//边框颜色
			    	gd.setStroke(strokeWidth, strokeColor);
			    	holder.btn_update.setBackground(gd);
			    	holder.btn_update.setTextColor(mContext.getResources().getColor(R.color.red));
					holder.btn_update.setText("立即更新");
					
					final int index = position;
					holder.btn_update.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							mDeviceSettingActivityTest.downloadFirmware(mDeviceListInfo.datavalue.get(index).updateUrl, mDeviceListInfo.datavalue.get(index).currentVersion, mDeviceListInfo.datavalue.get(index).deviceSerial);
						}
					});
				} else {
			    	strokeColor = Color.parseColor("#836FFF");//边框颜色
			    	gd.setStroke(strokeWidth, strokeColor);
			    	holder.btn_update.setBackground(gd);
			    	holder.btn_update.setTextColor(mContext.getResources().getColor(R.color.blue));
			    	holder.btn_update.setText("已是最新");
					final int index = position;
					holder.btn_update.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {

						}
					});
			    }		    
			}else{
				holder.rl_update.setVisibility(View.GONE);
			}
			
		}
		return view;
	}

	public void setFirstSelectPosition(int firstSelectPosition) {
		this.firstSelectPosition = firstSelectPosition;

	}
	
	class ViewHolder {
		TextView device;
        ImageView device_image;
        RelativeLayout rl_update;
        Button btn_update;
	}
}