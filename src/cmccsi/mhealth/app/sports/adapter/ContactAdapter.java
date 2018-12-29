package cmccsi.mhealth.app.sports.adapter;

import java.util.Arrays;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.RelativeLayout;
import android.widget.TextView;
import cmccsi.mhealth.app.sports.bean.ContactInfo;
import cmccsi.mhealth.app.sports.common.utils.StringUtils;
import cmccsi.mhealth.app.sports.R;

/**
 * 好友联系人
 * @type ContactAdapter
 * TODO
 * @author shaoting.chen
 * @time 2015年6月2日下午3:35:20
 */
public class ContactAdapter extends BaseAdapter {
	private LayoutInflater inflater;
	private Context mContext;
	private List<ContactInfo> mContactList = null;

	public ContactAdapter(Context context, List<ContactInfo> data) {
		inflater = LayoutInflater.from(context);
		this.mContactList = data;
		this.mContext = context;
		for(int i = 0; i < data.size(); i++){
			data.get(i).setChecked(true);
		}
	}

	public void clear() {
		if(mContactList != null){
			mContactList.clear();
			mContactList = null;
		}
	}
	public List<ContactInfo> getList(){
		return this.mContactList;
	}
	/**
	 * 批量添加好友，未全部成功
	 * TODO
	 * @param friendNumbers 已成为好友的手机号集合
	 * @param sendNumbers 已发送过好友申请的手机号集合
	 * @param mIndexs mContactList中批量添加好友的index集合
	 * @return void
	 * @author shaoting.chen
	 * @time 下午3:15:47
	 */
	public void noAddAllSuccess(String friendNumbers, String sendNumbers, List<Integer> mIndexs){
		String[] friend_added = null;
		if(StringUtils.isNotBlank(friendNumbers)){
			friend_added = friendNumbers.split(",");	
		}
		for(int i=0; i<mIndexs.size(); i++){
			if(friend_added != null && Arrays.asList(friend_added).contains(mContactList.get(mIndexs.get(i)).getPhonenumber())){
				mContactList.get(mIndexs.get(i)).setIsFriend("1");
			}else if(StringUtils.isNotBlank(sendNumbers)){
				mContactList.get(mIndexs.get(i)).setIsFriend("2");
			}
		}
	}
	@Override
	public int getCount() {
		return mContactList.size();
	}

	@Override
	public ContactInfo getItem(int position) {
		return mContactList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View view, ViewGroup parent) {
		View view_temp = null;
		ViewHolder holder = null; 
		if (view == null || (ViewHolder) view.getTag() == null) {
			holder = new ViewHolder();
			view_temp = inflater.inflate(R.layout.activity_match_contact_items, parent, false);
			holder.tv_name = (TextView) view_temp.findViewById(R.id.tv_friend_name);
			holder.tv_mobile = (TextView) view_temp.findViewById(R.id.tv_friend_mobile);
			holder.tv_added = (TextView) view_temp.findViewById(R.id.tv_friend_is_added);
			holder.cb_contact = (CheckBox) view_temp.findViewById(R.id.cb_friend_add);
//			holder.rl_match_contact_item = (RelativeLayout) view_temp.findViewById(R.id.rl_match_contact_item);
		}else{
			view_temp = view;
			holder = (ViewHolder) view_temp.getTag();
		}
		holder.tv_name.setText(mContactList.get(position).getPhonename());
		holder.tv_mobile.setText(mContactList.get(position).getPhonenumber());
		
		if(mContactList.get(position).isChecked()){
			holder.cb_contact.setChecked(true);
		}
		final int index = position;
		holder.cb_contact.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				// TODO Auto-generated method stub
				mContactList.get(index).setChecked(isChecked);
			}
		});

		if(mContactList.get(position).getIsFriend().equals("1")){
			holder.tv_added.setVisibility(View.VISIBLE);
			holder.cb_contact.setVisibility(View.GONE);
		}else if(mContactList.get(position).getIsFriend().equals("2")){
			holder.tv_added.setVisibility(View.VISIBLE);
			holder.cb_contact.setVisibility(View.GONE);
			holder.tv_added.setText("已发送");
		}
		return view_temp;
	}

	class ViewHolder{
		public TextView tv_name;
		public TextView tv_mobile;
		public TextView tv_added;
		public CheckBox cb_contact;
//		public RelativeLayout rl_match_contact_item;
	}
}