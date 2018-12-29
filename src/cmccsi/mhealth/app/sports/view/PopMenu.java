package cmccsi.mhealth.app.sports.view;

import java.util.List;

import cmccsi.mhealth.app.sports.bean.CommonBottomMenuItem;
import cmccsi.mhealth.app.sports.R;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

public class PopMenu {  
    private List<CommonBottomMenuItem> itemList;  
    private Context context;  
    private PopupWindow popupWindow ;  
    private ListView listView;  
    //private OnItemClickListener listener;  
      
  
    public PopMenu(Context context,List<CommonBottomMenuItem> list) {  
        // TODO Auto-generated constructor stub  
        this.context = context;  
  
        itemList = list;  
          
        View view = LayoutInflater.from(context).inflate(R.layout.popmenu, null);  
          
        //设置 listview  
        listView = (ListView)view.findViewById(R.id.lv_popmenu);  
        listView.setAdapter(new PopAdapter(context,list));  
        WindowManager wm=(WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
        popupWindow = new PopupWindow(view, wm.getDefaultDisplay().getWidth()/2, LayoutParams.WRAP_CONTENT);  
//        popupWindow = new PopupWindow(view,   
//        		LayoutParams.WRAP_CONTENT,   
//                LayoutParams.WRAP_CONTENT);  
          
        // 这个是为了点击“返回Back”也能使其消失，并且并不会影响你的背景（很神奇的）  
        popupWindow.setBackgroundDrawable(new BitmapDrawable());  
    }  
  
    //设置菜单项点击监听器  
    public void setOnItemClickListener(OnItemClickListener listener) {  
        //this.listener = listener;  
        listView.setOnItemClickListener(listener);  
    }  
   
  
    //单个添加菜单项  
    public void addItem(CommonBottomMenuItem item) {  
        itemList.add(item); 
        
    }  
  
    //下拉式 弹出 pop菜单 parent 右下角  
    public void showAsDropDown(View parent) {  
        popupWindow.showAsDropDown(parent, -20,20);  
        // 使其聚集  
        popupWindow.setFocusable(true);  
        // 设置允许在外点击消失  
        popupWindow.setOutsideTouchable(true);  
        //刷新状态  
        popupWindow.update();  
    }  
      
    //隐藏菜单  
    public void dismiss() {  
        popupWindow.dismiss();  
    }  
  
    // 适配器  
    private final class PopAdapter extends BaseAdapter {  
  
    	private LayoutInflater inflater;
		private List<CommonBottomMenuItem> items;

		public PopAdapter(Context context, List<CommonBottomMenuItem> items) {
			this.inflater = LayoutInflater.from(context);
			this.items = items;
		}
    	
        @Override  
        public int getCount() {  
            // TODO Auto-generated method stub  
            return items.size();  
        }  
  
        @Override  
        public Object getItem(int position) {  
            // TODO Auto-generated method stub  
            return items.get(position);  
        }  
  
        @Override  
        public long getItemId(int position) {  
            // TODO Auto-generated method stub  
            return position;  
        }  
  
        @Override  
        public View getView(int position, View convertView, ViewGroup parent) {  
            // TODO Auto-generated method stub  
            ViewHolder holder;  
            if (convertView == null) {  
                convertView = LayoutInflater.from(context).inflate(R.layout.simple_imagetext_listitem, null);  
                holder = new ViewHolder();  
  
                convertView.setTag(holder);  
                holder.groupImage=(ImageView) convertView.findViewById(R.id.imageView1); 
                holder.groupItem = (TextView) convertView.findViewById(R.id.textView1);  
  
            } else {  
                holder = (ViewHolder) convertView.getTag();  
            }  
  
            holder.groupImage.setBackgroundResource(items.get(position).getMenuIcon());
            holder.groupItem.setText(items.get(position).getMenuName());  
  
            return convertView;  
        }  
  
        private final class ViewHolder {  
            TextView groupItem;  
            ImageView groupImage;
        }  
    }  
}  
