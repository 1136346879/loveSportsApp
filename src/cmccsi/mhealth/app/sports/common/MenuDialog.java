package cmccsi.mhealth.app.sports.common;

import java.util.List;



import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import cmccsi.mhealth.app.sports.bean.CommonBottomMenuItem;
import cmccsi.mhealth.app.sports.R;

/**
 * 底部弹出菜单，功能包括：<br>
 * 1.获取实例后，执行showBottomMenuDialog(Context context, List items)<br>
 * 方法可以弹出底部菜单，并点选，菜单图片以及标题通过items设置。可以设定对应BottomMenuItem的id灵活使用<br>
 * 2.通过注册监听器setOnClickedItemPosition()，可以返回所点击的按钮position<br>
 * 
 * @author zy
 * 
 */
public class MenuDialog {
	private Dialog mMenuDialog;
	private GridView mGridMenu;
	private GridAdapter mAdapter;
	private TextView mClickToClose;
	private TextView mClickToCancle;
	private onClickedItemPosition mItemPositionListener;

	public void showBottomMenuDialog(Activity context, List<CommonBottomMenuItem> items) {

		mMenuDialog = new Dialog(context, R.style.dialog_withStatusBar);
		//mMenuDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		mMenuDialog.setCanceledOnTouchOutside(true);
		View viewDialog = View.inflate(context, R.layout.alertdialog_message, null);
		mClickToClose = (TextView) viewDialog.findViewById(R.id.am_click_me_to_dismiss_search_dialog);
		mClickToCancle = (TextView) viewDialog.findViewById(R.id.am_cancle);
		mGridMenu = (GridView) viewDialog.findViewById(R.id.am_gv1);
		mClickToClose.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dismiss();
			}
		});
		mClickToCancle.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dismiss();
			}
		});
		mGridMenu.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				if (mItemPositionListener != null) {
					mItemPositionListener.onClickedPosition(position);
				}
			}
		});

		Window window = mMenuDialog.getWindow();
		window.setGravity(Gravity.BOTTOM); // 此处可以设置dialog显示00 的位置
		window.setWindowAnimations(R.style.mystylebottom); // 添加动画

		viewDialog.setBackgroundColor(Color.TRANSPARENT);
		mMenuDialog.setContentView(viewDialog);

		mAdapter = new GridAdapter(context, items);
		mGridMenu.setAdapter(mAdapter);
		mMenuDialog.show();

	}

	private class GridAdapter extends BaseAdapter {
		private LayoutInflater inflater;
		private List<CommonBottomMenuItem> items;

		public GridAdapter(Context context, List<CommonBottomMenuItem> items) {
			this.inflater = LayoutInflater.from(context);
			this.items = items;
		}

		@Override
		public int getCount() {
			return items.size();
		}

		@Override
		public Object getItem(int position) {
			return position;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			if (convertView == null) {
				holder = new ViewHolder();
				convertView = this.inflater.inflate(R.layout.main_menu_frid, null);
				holder.iv = (ImageView) convertView.findViewById(R.id.mmf_iv1);
				holder.tv = (TextView) convertView.findViewById(R.id.mmf_tv1);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			holder.iv.setImageResource(items.get(position).getMenuIcon());
			holder.tv.setText(items.get(position).getMenuName());
			return convertView;
		}

		private class ViewHolder {
			ImageView iv;
			TextView tv;
		}

	}

	public interface onClickedItemPosition {
		abstract void onClickedPosition(int position);
	}

	/**
	 * 注册菜单点选监听
	 * 
	 * @param listener
	 */
	public void setOnClickedItemPosition(onClickedItemPosition listener) {
		mItemPositionListener = listener;
	}

	/**
	 * 将菜单关闭，关闭前针对菜单是否为空以及是否显示进行了判断。
	 */
	public void dismiss() {
		if (mMenuDialog != null && mMenuDialog.isShowing()) {
			mMenuDialog.dismiss();
		}
	}
}
