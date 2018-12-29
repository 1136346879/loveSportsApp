package cmccsi.mhealth.app.sports.tabhost;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import cmccsi.mhealth.app.sports.activity.RankingActivity;
import cmccsi.mhealth.app.sports.basic.BaseFragment;
import cmccsi.mhealth.app.sports.R;

public class RankCompanyMenuFragment extends BaseFragment implements OnClickListener{
	
	private ImageButton mBack;
	private LinearLayout mll_company_rank;
	private LinearLayout mll_rank_activite;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		
		 
		 View view = inflater.inflate(R.layout.activity_rank_companymenu, container,
					false);
		 super.onCreateView(inflater, (ViewGroup) view, savedInstanceState);
		 return view;
	}

	@Override
	public void findViews() {
		//返回键
		ImageButton mImageButtonBack = (ImageButton)findView(R.id.button_input_bg_back);
		mImageButtonBack.setVisibility(View.VISIBLE);
		mImageButtonBack.setBackgroundResource(R.drawable.my_button_back);
		mImageButtonBack.setOnClickListener(this);
		
		mTextViewTitle = (TextView) findView(R.id.textView_title);
		mTextViewTitle.setText("企业");
		
		mll_company_rank=findView(R.id.ll_company_rank);
		mll_rank_activite=findView(R.id.ll_rank_activite);
		mll_company_rank.setOnClickListener(this);
		mll_rank_activite.setOnClickListener(this);
	}

	@Override
	public void clickListner() {

	}

	@Override
	public void loadLogic() {

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.button_input_bg_back:
			getActivity().finish();
			break;
		case R.id.ll_company_rank:
			Intent it=new Intent();
			it.putExtra(RankingActivity.ISAREARANK, RankingActivity.RANK_GROUP);
			it.setClass(getActivity(), RankingActivity.class);
	        startActivity(it);
			break;
		case R.id.ll_rank_activite:
			Intent itent=new Intent();
			itent.putExtra("intent", 5);
			itent.setClass(getActivity(), TabBaseFragment.class);
	        startActivity(itent);
			break;
		default:
			break;
		}
	}

}
