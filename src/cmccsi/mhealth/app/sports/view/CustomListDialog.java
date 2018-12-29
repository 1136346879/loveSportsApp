package cmccsi.mhealth.app.sports.view;

import java.util.ArrayList;
import java.util.List;

import cmccsi.mhealth.app.sports.R;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

public class CustomListDialog extends Dialog{

	private Context mContext;
	private String mTitle;
	private List<String> mContents;
	
	public CustomListDialog(Context context) {
		super(context);
		this.mContext=context;
	}

	public CustomListDialog(Context context,int theme,String title,List<String> contents) {
		super(context,theme);
		this.mContext=context;
		this.mTitle=title;
		this.mContents=contents;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.customlistdialog);
		TextView tv_title = (TextView) findViewById(R.id.tv_customlistdialog_title);
		ListView lv=(ListView)findViewById(R.id.lv_content);

		lv.setAdapter(new ArrayAdapter<String>(mContext, R.layout.list_item_simpletext, mContents));

		Button btn = (Button) findViewById(R.id.btn_ok);
		tv_title.setText(mTitle);

		btn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				CustomListDialog.this.dismiss();
			}
		});
	}
}
