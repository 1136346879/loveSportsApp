package cmccsi.mhealth.app.sports.adapter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.zip.Inflater;

import cmccsi.mhealth.app.sports.bean.ListActivity;
import cmccsi.mhealth.app.sports.db.ListActivityTableMetaData;
import cmccsi.mhealth.app.sports.R;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class CampaignListAdapter extends BaseAdapter {
	
    private final List<ListActivity> listItems;
    private final Context context;
    
    private final SimpleDateFormat df_yyyyMMdd = new SimpleDateFormat("yyyyMMdd");
	private static SimpleDateFormat df_M_d = new SimpleDateFormat("M月d日");
    
	public CampaignListAdapter(List<ListActivity> listItems, Context context) {
		super();
		this.listItems = listItems;
		this.context = context;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return listItems.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return listItems.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}

	@Override
	public View getView(int arg0, View convertView, ViewGroup parent) {
		  View view = convertView;
	      ViewHolder holder ;
	      if (view == null) {
	    	  view = LayoutInflater.from(context)
	    			  .inflate(R.layout.racelist_expandablelist,
	    			  parent, false);
	    			  holder = new ViewHolder();
	    			  holder.textview_racelist_nowTitle = (TextView) view.findViewById(R.id.textview_racelist_nowTitle);
	    			  holder.textview_racelist_nowdate = (TextView) view.findViewById(R.id.textview_racelist_nowdate);
	    			  holder.textview_racelist_nowperson = (TextView) view.findViewById(R.id.textview_racelist_nowperson);
	    			  view.setTag(holder);
	      }
	      else
	      {
	          holder = (ViewHolder)view.getTag();
	      }
	      holder.textview_racelist_nowTitle.setText(listItems.get(arg0).activityname);
	      holder.textview_racelist_nowperson.setText(listItems.get(arg0).personnum);
	      
			try {
				String start = listItems.get(arg0).activitystart;
				String time = start.substring(4, start.length());
				String end = listItems.get(arg0).activityend;
				String endtime = end.substring(4, end.length());
				holder.textview_racelist_nowdate.setText(time + "-" + endtime);
				Date currentDate = df_yyyyMMdd.parse(start);
				String strStarDate = df_M_d.format(currentDate);

				currentDate = df_yyyyMMdd.parse(end);
				String strEndDate = df_M_d.format(currentDate);
				holder.textview_racelist_nowdate.setText(strStarDate + "-" + strEndDate);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}catch (NullPointerException e) {
				// TODO: handle exception
				e.printStackTrace();
			}
	      
	      return view;
	}

	class ViewHolder
    {
        TextView textview_racelist_nowTitle,textview_racelist_nowdate,textview_racelist_nowperson;
        
    }
}
