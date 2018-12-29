package cmccsi.mhealth.app.sports.tabhost;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.ObjectUtils.Null;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import cmccsi.mhealth.app.sports.basic.BaseFragment;
import cmccsi.mhealth.app.sports.bean.BackInfo;
import cmccsi.mhealth.app.sports.bean.GoalInfo;
import cmccsi.mhealth.app.sports.bean.GoalInfo.GoalType;
import cmccsi.mhealth.app.sports.common.PreferencesUtils;
import cmccsi.mhealth.app.sports.common.SharedPreferredKey;
import cmccsi.mhealth.app.sports.net.DataSyn;
import cmccsi.mhealth.app.sports.R;


public class GoalFragment extends BaseFragment{
    private ListView lv_archievementTypes ;
    private AchievementTypeAdapter mAdapter;
    private List<Map<String, Object>> mAchievementTypes = new ArrayList<Map<String,Object>>();
    private String mType;
    private Activity mActivity;
    private TextView tv_showGoal;
    
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = activity;
        mType = PreferencesUtils.getString(activity, SharedPreferredKey.GOAL_TYPE, "0");
    }
            
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_goal, container, false);
        super.onCreateView(inflater, (ViewGroup) view, savedInstanceState);
        initTitle();
        return view;
    }

    @Override
    public void findViews() {
        lv_archievementTypes = findView(R.id.lv_achievementTypes);
        tv_showGoal = findView(R.id.tv_showGoal);
        GoalInfo goalInfo = GoalInfo.getInstance(mActivity);
        tv_showGoal.setText(goalInfo.getGoalReportInpercentage(mActivity));
    }

    /**
     * 初始化标题栏
     */
    private void initTitle() {
        TextView tv_title = findView(R.id.textView_title);
        tv_title.setText(R.string.achievement_title);
        ImageView iv_backImageView = findView(R.id.button_input_bg_back);
        iv_backImageView.setVisibility(View.VISIBLE);
        iv_backImageView.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });
    }
    
    @Override
    public void clickListner() {
        
    }

    @Override
    public void onPause() {
        super.onPause();
        uploadSelectType();
    }

    /**
     * 上传成就类型
     */
    private void uploadSelectType() {
        if (!mType.equals(PreferencesUtils.getString(getActivity(), SharedPreferredKey.GOAL_TYPE, "1000"))) {
//            PreferencesUtils.putString(getActivity(), SharedPreferredKey.GOAL_TYPE, mType);
            new UploadGoalTypeTask().execute();
        }
    }
    
    /* (non-Javadoc)
     * @see cmcc.mhealth.basic.BaseFragment#loadLogic()
     */
    @Override
    public void loadLogic() {
        mAdapter = new AchievementTypeAdapter(getActivity());
        lv_archievementTypes.setAdapter(mAdapter);
        lv_archievementTypes.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                    int position, long id) {
                for (int i = 0; i < parent.getCount(); i++) {
                    View itemView = parent.getChildAt(i);
                    View flag =itemView.findViewById(R.id.flag);
                    if (position==i) {
                        flag.setSelected(true);
                    }else {
                        flag.setSelected(false);
                    }
                }
                mType = String.valueOf(position);
                GoalInfo goalInfo = GoalInfo.getInstance(mActivity, mType);
                tv_showGoal.setText(goalInfo.getGoalReportInpercentage(mActivity));
            }
        });
        for (GoalType achievementType : GoalType.values()) {
            addItem(achievementType+"", achievementType.getInfo()+" ("+(achievementType.getDistance()/1000)+"千米)", achievementType.getIconRes());
        }
    }

    private void addItem(String name, String detail, int iconRes) {
        Map<String, Object> item = new HashMap<String, Object>();
        item.put(AchievementTypeAdapter.KEY_TYPE, name);
        item.put(AchievementTypeAdapter.KEY_TYPEICON, iconRes);
        item.put(AchievementTypeAdapter.KEY_TYPEDETAIL, detail);
        mAchievementTypes.add(item);
    }
    
    private class AchievementTypeAdapter extends BaseAdapter{

        public static final String KEY_TYPE="type";
        public static final String KEY_TYPEICON="icon";
        public static final String KEY_TYPEDETAIL="detail";
        private LayoutInflater mInflater;
        
        
        public AchievementTypeAdapter(Context context) {
            mInflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return mAchievementTypes.size();
        }

        @Override
        public Map<String, Object> getItem(int position) {
            return mAchievementTypes.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView==null) {
                holder = new ViewHolder();
                convertView = mInflater.inflate(R.layout.list_item_goal_type, null);
                holder.iv_type = (ImageView) convertView.findViewById(R.id.iv_type);
                holder.tv_type=(TextView) convertView.findViewById(R.id.tv_type);
                holder.flag=convertView.findViewById(R.id.flag);
                convertView.setTag(holder);
            }else {
                holder=(ViewHolder) convertView.getTag();
            }
            Map<String, Object> item = getItem(position);
            holder.iv_type.setImageResource((Integer) item.get(KEY_TYPEICON));
            holder.tv_type.setText(item.get(KEY_TYPEDETAIL)+"");
            if (mType.equalsIgnoreCase(String.valueOf(position))) {
                holder.flag.setSelected(true);
            }else {
                holder.flag.setSelected(false);
            }
            return convertView;
        }
        
        class ViewHolder{
            ImageView iv_type;
            TextView tv_type;
            View flag;
        }
    }
    
    private class UploadGoalTypeTask extends AsyncTask<String, Null, Integer>{

        @Override
        protected Integer doInBackground(String... params) {
            return DataSyn.getInstance().uploadGoalType(mType, new BackInfo());
        }
        
        @Override
        protected void onPostExecute(Integer result) {
            super.onPostExecute(result);
            if (result !=0) {
                Toast.makeText(mActivity, R.string.MESSAGE_GOAL_UPLAOD_FAILED,Toast.LENGTH_SHORT).show();
            }
        }
    }
}
