package cmccsi.mhealth.app.sports.view;

import cmccsi.mhealth.app.sports.R;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class CustomDialogOK extends Dialog {
	int layoutRes;// 布局文件
	Context context;
	String title;
	String content;

	public CustomDialogOK(Context context) {
		super(context);
		this.context = context;
	}

	/**
	 * 自定义布局的构造方法
	 * 
	 * @param context
	 * @param resLayout
	 */
	public CustomDialogOK(Context context, int resLayout) {
		super(context);
		this.context = context;
		this.layoutRes = resLayout;
	}

	/**
	 * 自定义主题及布局的构造方法
	 * 
	 * @param context
	 * @param theme
	 * @param resLayout
	 */
	public CustomDialogOK(Context context, int theme, int resLayout, String title, String content) {
		super(context, theme);
		this.context = context;
		this.layoutRes = resLayout;
		this.title = title;
		this.content = content;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.customdialog);
		TextView tv_title = (TextView) findViewById(R.id.title);
		TextView tv_content = (TextView) findViewById(R.id.content);
		Button btn = (Button) findViewById(R.id.button);
		tv_title.setText(title);
		tv_content.setText(content);
		btn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				CustomDialogOK.this.dismiss();
			}
		});
	}
}