package cmccsi.mhealth.app.sports.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
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
import cmccsi.mhealth.app.sports.basic.BaseActivity;
import cmccsi.mhealth.app.sports.bean.RunType;
import cmccsi.mhealth.app.sports.tabhost.MapFragment;
import cmccsi.mhealth.app.sports.R;

public class MapSelectTRunType extends BaseActivity implements OnClickListener {

    private final int[] drawableRes = new int[] { R.drawable.map_type_walk_on,
            R.drawable.map_type_run_on, R.drawable.map_type_cycle_on };

    private RunType mType = null;
    private ListView lv_types;
    private List<Map<String, Object>> mMapTypes = new ArrayList<Map<String, Object>>();
    private int mTypeId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_run_type);
        mTypeId = getIntent().getIntExtra(MapFragment.KEY_TYPE_ID, -1);
        initAdapterDataSet();
        initView();
    }

    private void addTypeItem(int imgRes, String name) {
        Map<String, Object> item = new HashMap<String, Object>();
        item.put(MapTypeAdapter.KEY_IMGRES, imgRes);
        item.put(MapTypeAdapter.KEY_NAME, name);
        mMapTypes.add(item);
    }

    private void initializerRunType() {
        if (mTypeId == -1) {
            return;
        }
        mType = new RunType(drawableRes[mTypeId - 1], mMapTypes
                .get(mTypeId - 1).get(MapTypeAdapter.KEY_NAME) + "", mTypeId);
    }

    /**
     * 初始化列表中的数据
     */
    private void initAdapterDataSet() {
        addTypeItem(R.drawable.map_type_walk, "步行");
        addTypeItem(R.drawable.map_type_run, "跑步");
        addTypeItem(R.drawable.map_type_cycle, "骑行");
    }

    private void initView() {
        BaseBackKey("选择类型", null);
        findView(R.id.button_input_bg_back).setOnClickListener(this);
        lv_types = findView(R.id.lv_types);
        lv_types.setAdapter(new MapTypeAdapter(this));
        lv_types.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                    int position, long id) {
                for (int i = 0; i < parent.getCount(); i++) {
                    View itemView = parent.getChildAt(i);
                    View flag = itemView.findViewById(R.id.flag);
                    ImageView iv_type = (ImageView) itemView.findViewById(R.id.iv_type);
                    TextView tv_type = (TextView) itemView
                            .findViewById(R.id.tv_type);
                    if (position == i) {
                        flag.setSelected(true);
                        iv_type.setSelected(true);
                        tv_type.setTextColor(Color.rgb(121, 174, 58));
                    } else {
                        flag.setSelected(false);
                        iv_type.setSelected(false);
                        tv_type.setTextColor(Color.BLACK);
                    }
                }
                mTypeId = position + 1;
                initializerRunType();
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.button_input_bg_back:
            navBack();
            break;
        default:
            break;
        }
    }

    @Override
    public void onBackPressed() {
        navBack();
    }

    /**
     * 回到上一个界面 void
     * 
     * @exception
     * @since 1.0.0
     * @author Xiao
     * @addtime 2014-10-31 下午4:58:45
     */
    private void navBack() {
        Intent data = new Intent();
        Bundle extras = new Bundle();
        extras.putParcelable("type", mType);
        data.putExtras(extras);
        setResult(Activity.RESULT_OK, data);
        this.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.silde_out_right);
    }

    /**
     * 适配器
     * 
     * @author Xiao
     * 
     */
    private class MapTypeAdapter extends BaseAdapter {

        public static final String KEY_IMGRES = "imgRes";
        public static final String KEY_NAME = "name";
        private LayoutInflater mInflater;

        public MapTypeAdapter(Context context) {
            mInflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return mMapTypes.size();
        }

        @Override
        public Map<String, Object> getItem(int position) {
            return mMapTypes.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = mInflater.inflate(R.layout.list_item_map_type,
                        null);
                holder.iv_type = (ImageView) convertView
                        .findViewById(R.id.iv_type);
                holder.tv_type = (TextView) convertView
                        .findViewById(R.id.tv_type);
                holder.flag = convertView.findViewById(R.id.flag);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            Map<String, Object> item = getItem(position);
            holder.iv_type.setImageResource((Integer) item.get(KEY_IMGRES));
            holder.tv_type.setText(item.get(KEY_NAME) + "");
            if (mTypeId == (position + 1)) {
                holder.flag.setSelected(true);
                holder.iv_type.setSelected(true);
                holder.tv_type.setTextColor(Color.rgb(121, 174, 58));
            } else {
                holder.flag.setSelected(false);
                holder.iv_type.setSelected(false);
                holder.tv_type.setTextColor(Color.BLACK);
            }
            return convertView;
        }

        class ViewHolder {
            ImageView iv_type;
            TextView tv_type;
            View flag;
        }
    }
}
