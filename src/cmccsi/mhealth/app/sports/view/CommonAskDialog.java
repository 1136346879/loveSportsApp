package cmccsi.mhealth.app.sports.view;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import cmccsi.mhealth.app.sports.R;

public class CommonAskDialog extends DialogFragment implements OnClickListener {

	public static final int BUTTON_OK = 1;
	public static final int BUTTON_CANCEL = 2;
	public static final int BUTTON_NEUTRAL = 3;

	private static final String KEY_MSG = "key_msg";
	private static final String KEY_NEUTRAL = "key_neutral";
	private static final String KEY_CANCEL = "key_cancel";
	private static final String KEY_BUTTON = "key_button";

	@Deprecated
	private OnButtonClickListener onButtonClickListener;
	private OnDialogCloseListener onDialogCloseListener;

	private Button buttonOK;
	private Button buttonCancel;
	private Button buttonNeutral;
	private String[] defaultButtonText = { "确定","","取消"  } ;
	
	private ImageView mIvAlert;
	private int IvAlertResource;

	public static CommonAskDialog create(String msg, String[] buttonText,
			boolean showNeutral, boolean showCancel) {
		CommonAskDialog dialog = new CommonAskDialog();
		Bundle args = new Bundle();
		args.putString(KEY_MSG, msg);
		args.putBoolean(KEY_NEUTRAL, showNeutral);
		args.putBoolean(KEY_CANCEL, showCancel);
		args.putStringArray(KEY_BUTTON, buttonText);
		dialog.setArguments(args);
		return dialog;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.dialog_common_ask, container,
				false);
		buttonOK = (Button) view.findViewById(R.id.button1);
		buttonCancel = (Button) view.findViewById(R.id.button2);
		buttonNeutral = (Button) view.findViewById(R.id.button3);
		buttonOK.setOnClickListener(this);
		buttonCancel.setOnClickListener(this);
		buttonNeutral.setOnClickListener(this);
		getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
		getDialog().setCanceledOnTouchOutside(false);
		mIvAlert=(ImageView)view.findViewById(R.id.iv_alert_icon);
		if(IvAlertResource==-1)
		{
			mIvAlert.setVisibility(View.GONE);
		}
		else{
			mIvAlert.setVisibility(View.VISIBLE);
			mIvAlert.setBackgroundResource(IvAlertResource);
		}
		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		boolean showNeutral = getArguments().getBoolean(KEY_NEUTRAL);
		boolean showCancel = getArguments().getBoolean(KEY_CANCEL);
		String msg = getArguments().getString(KEY_MSG);
		buttonNeutral.setVisibility(showNeutral ? View.VISIBLE : View.GONE);
		buttonCancel.setVisibility(showCancel ? View.VISIBLE : View.GONE);
		TextView text = (TextView) getView().findViewById(R.id.textMessage);
		text.setText(msg);
		String[] buttonText = getArguments().getStringArray(KEY_BUTTON);
		if(buttonText!=null) {
			for(int i=0;i<buttonText.length;i++) {
				defaultButtonText[i]=buttonText[i];
			}
		}
		buttonOK.setText(defaultButtonText[0]);
		buttonNeutral.setText(defaultButtonText[1]);
		buttonCancel.setText(defaultButtonText[2]);
	}
	
	/**
	 * 设置图标
	 * TODO
	 * @param drawable 图片资源 -1则隐藏图标
	 * @return void
	 * @author jiazhi.cao
	 * @time 下午2:58:13
	 */
	public void setAlertIconVisible(int drawable)
	{
		IvAlertResource=drawable;
		
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		if (id == R.id.button1) {
			if (onButtonClickListener != null) {
				onButtonClickListener.onClick(true);
			}
			if (onDialogCloseListener != null) {
				onDialogCloseListener.onClick(BUTTON_OK);
			}
		} else if (id == R.id.button2) {
			if (onButtonClickListener != null) {
				onButtonClickListener.onClick(false);
			}
			if (onDialogCloseListener != null) {
				onDialogCloseListener.onClick(BUTTON_CANCEL);
			}
		} else {
			if (onDialogCloseListener != null) {
				onDialogCloseListener.onClick(BUTTON_NEUTRAL);
			}
		}
		dismiss();
	}
	
	public interface OnButtonClickListener {
		void onClick(boolean isok);
	}

	public interface OnDialogCloseListener {
		void onClick(int which);
	}

	@Deprecated
	public void setOnButtonClickListener(
			OnButtonClickListener onButtonClickListener) {
		this.onButtonClickListener = onButtonClickListener;
	}

	public void setOnDialogCloseListener(
			OnDialogCloseListener onDialogCloseListener) {
		this.onDialogCloseListener = onDialogCloseListener;
	}

}
