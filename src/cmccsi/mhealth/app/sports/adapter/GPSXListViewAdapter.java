package cmccsi.mhealth.app.sports.adapter;

import java.util.ArrayList;
import java.util.List;

import cmccsi.mhealth.app.sports.bean.GPSListBean;
import cmccsi.mhealth.app.sports.bean.RunType;
import cmccsi.mhealth.app.sports.common.Logger;
import cmccsi.mhealth.app.sports.R;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class GPSXListViewAdapter extends BaseAdapter {
	private static final String TAG = "GPSXListViewAdapter";
	List<GPSListBean> list = new ArrayList<GPSListBean>();
	Context context;
	
	public GPSXListViewAdapter(List<GPSListBean> list,Context context){
		this.list.clear();
		this.list = list;
		this.context = context;
	} 

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		return position;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
	ViewHolder viewHolder;
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = null;
		if(position == 7){
			Logger.e(TAG, "time = "+list.get(position).time);
		}
		if(list.get(position).time != null){
			view = View.inflate(context, R.layout.title_item, null);
			TextView textView = (TextView) view.findViewById(R.id.leftmenu_title_textview_company);
			textView.setText(list.get(position).time);
		}else{
			view = View.inflate(context, R.layout.map_list_item, null);
			viewHolder = new ViewHolder();
			adapterFindViews(view);
//			if(convertView == null){
//				adapterFindViews(view);
//			}else{
//				if(convertView.getTag() instanceof ViewHolder){
//					viewHolder = (ViewHolder) convertView.getTag();
//				}else{
//					
//				}
//				
//			}
			// if(list.get(position).listInfo.getIsUpload() == 1){
			// //未上传颜色标记
			// viewHolder.bg.setBackgroundResource(R.color.org_II);
			// }else{
			// viewHolder.bg.setBackgroundResource(R.color.blue_low);
			// }
			String date = list.get(position).listInfo.getStarttime().toString();
			String arr[] = date.split(" ");
			viewHolder.imageViewWalkType.setImageResource(RunType.ID2Image(list.get(position).listInfo.getSporttype(), list.get(position).listInfo.getIsUpload()));
			if(!date.equals("0"))
				viewHolder.textview_map_history_time.setText(arr[1].substring(
						0, 5) );

	        float tmpDistance =  list.get(position).listInfo.getDistance()/1000f;
	        if(tmpDistance>10){
	            viewHolder.textview_kilometre.setText(String.format("%.1f", list.get(position).listInfo.getDistance()/1000f));            
	        }else{
	            viewHolder.textview_kilometre.setText(String.format("%.2f", list.get(position).listInfo.getDistance()/1000f));
	        }
            float tmpCal =  list.get(position).listInfo.getCal();
            if(tmpDistance>10){
                viewHolder.textview_kaluli.setText(String.format("%.1f", tmpCal));          
            }else{
                viewHolder.textview_kaluli.setText(String.format("%.2f", tmpCal));
            }
            float tmpSpeed =  list.get(position).listInfo.getSpeed();
            if(tmpDistance>10){
                viewHolder.textview_duration.setText(String.format("%.1f", tmpSpeed));          
            }else{
                viewHolder.textview_duration.setText(String.format("%.2f", tmpSpeed));
            }
			// viewHolder.textview_runtype.setText(RunType.ID2String(list.get(position).listInfo.getSporttype()).toString());
		}
		return view;
//>>>>>>> .r2858
	}

	private void adapterFindViews(View view) {
		// viewHolder.textview_map_history_date = (TextView)
		// view.findViewById(R.id.textview_map_history_date);
		viewHolder.textview_map_history_time = (TextView) view.findViewById(R.id.textview_map_history_time);
		viewHolder.textview_kilometre = (TextView) view.findViewById(R.id.textview_kilometre);
		viewHolder.textview_kaluli = (TextView) view.findViewById(R.id.textview_kaluli);
		viewHolder.textview_duration = (TextView) view.findViewById(R.id.textview_duration);
		// viewHolder.textview_runtype = (TextView)
		// view.findViewById(R.id.textview_runtype);
		// viewHolder.rl_date = (RelativeLayout)
		// view.findViewById(R.id.rl_date);
		// viewHolder.bg = (LinearLayout)
		// view.findViewById(R.id.linearlayout_null);
		viewHolder.imageViewWalkType = (ImageView) view.findViewById(R.id.imageview_wailtype);
	}
}

class ViewHolder{
	// TextView textview_map_history_date;
	TextView textview_map_history_time;
	TextView textview_kilometre;
	TextView textview_kaluli;
	// TextView textview_runtype;
	TextView textview_duration;
	// RelativeLayout rl_date;
	// LinearLayout bg;
	ImageView imageViewWalkType;
}