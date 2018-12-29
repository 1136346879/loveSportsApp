package cmccsi.mhealth.app.sports.view;

import cmccsi.mhealth.app.sports.common.utils.StringUtils;
import cmccsi.mhealth.app.sports.R;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class CustomDialog extends AlertDialog {

	private Context context = null;
	private TextView mTvTitle = null;
	private LinearLayout mLlContent = null;
	private TextView mTvMessage = null;
	private LinearLayout mLlBtns = null;
	private Button mBtnLeft = null;
	private Button mBtnRight = null;
	private ImageView iv_icon = null;

	private String title = null;
	private String message = null;
	private String leftMessage = null;
	private String rightMessage = null;
	private OnClickListener onLeftListener = null;
	private OnClickListener onRightListener = null;
	private int mResId;
	private View contentView = null;

	protected CustomDialog(Context context) {
		super(context, R.style.dialog_defult);
		this.context = context;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setCanceledOnTouchOutside(false);
		this.initView();
		this.initData();
	}

	private void initView() {
		View view = LayoutInflater.from(context).inflate(R.layout.dialog_deflut, null);
		mTvTitle = (TextView) view.findViewById(R.id.tv_dialog_title);
		mLlContent = (LinearLayout) view.findViewById(R.id.ll_dialog_content);
		mTvMessage = (TextView) view.findViewById(R.id.tv_message);
		mLlBtns = (LinearLayout) view.findViewById(R.id.ll_btns);
		mBtnLeft = (Button) view.findViewById(R.id.btn_ok);
		mBtnRight = (Button) view.findViewById(R.id.btn_cancel);
		iv_icon = (ImageView) view.findViewById(R.id.iv_icon);

		// WindowManager.LayoutParams lp = this.getWindow().getAttributes();
		// lp.width = ViewUtils.pxToDp(context, 270f);
		// this.getWindow().setAttributes(lp);
		this.setContentView(view);
	}

	private void initData() {
		if (StringUtils.isNotBlank(title)) {
			mTvTitle.setText(title);
		}
		if (mResId != 0) {
			iv_icon.setImageResource(mResId);
		}
		if (contentView != null) {
			mLlContent.removeAllViews();
			mLlContent.addView(contentView);
		}
		if (message != null) {
			mTvMessage.setText(message);
		}
		if (StringUtils.isNotBlank(leftMessage)) {
			mBtnLeft.setText(leftMessage);
		}
		if (StringUtils.isNotBlank(rightMessage)) {
			mBtnRight.setText(rightMessage);
		}
		if (this.onLeftListener != null) {
			mBtnLeft.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					onLeftListener.onClick(CustomDialog.this, mBtnLeft.getId());
				}
			});
		} else {
			mBtnLeft.setVisibility(View.GONE);
		}
		if (this.onRightListener != null) {
			mBtnRight.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					CustomDialog.this.dismiss();
					onRightListener.onClick(CustomDialog.this, mBtnLeft.getId());
				}
			});
		} else {
			mBtnRight.setVisibility(View.GONE);
		}

		if (mBtnLeft.getVisibility() == View.GONE && mBtnRight.getVisibility() == View.GONE) {
			mLlBtns.setVisibility(View.GONE);
		}
	}

	private void setDialogTitle(int title) {
		this.title = context.getString(title);
	}

	private void setDialogView(View view) {
		this.contentView = view;
	}

	private void setDialogMessage(int message) {
		this.message = context.getString(message);
	}

	private void setLeftButtonText(String leftStr) {
		this.leftMessage = leftStr;
	}

	private void setLeftButtonListener(OnClickListener listener) {
		this.onLeftListener = listener;
	}

	private void setRightButtonText(String rightStr) {
		this.rightMessage = rightStr;
	}

	private void setRightButtonListener(OnClickListener listener) {
		this.onRightListener = listener;
	}

	private void setTitleIcon(int resId) {
		this.mResId = resId;
	}

	public void setTitleString(String title) {
		this.title = title;
		mTvTitle.setText(this.title);
	}

	/**
	 * 显示一个确定按钮 中间内容为视图
	 * 
	 * @param context
	 * @param title
	 * @param view
	 *            对话框内中间视图
	 * @param isCancelable
	 *            点返回是否关闭对话框，true关闭，false不关闭
	 * @param onLeftClickListener
	 *            确定按钮响应
	 * @return 当前显示对话框
	 */
	public static CustomDialog showDialog(Context context, int title, View view, boolean isCancelable, int resId,
			OnClickListener onLeftClickListener) {
		return showDialog(context, title, view, isCancelable, resId, null, onLeftClickListener, null, null);
	}

	/**
	 * 显示一个确定按钮,中间内容为文字
	 * 
	 * @param context
	 * @param title
	 * @param message
	 *            对话框内文字
	 * @param isCancelable
	 *            点返回是否关闭对话框，true关闭，false不关闭
	 * @param onLeftClickListener
	 *            确定按钮的响应
	 * @return 当前显示对话框
	 */
	public static CustomDialog showDialog(Context context, int title, int message, boolean isCancelable, int resId,
			OnClickListener onLeftClickListener) {
		return showDialog(context, title, message, isCancelable, resId, null, onLeftClickListener, null, null);
	}

	/**
	 * 显示一个确定按钮,中间内容为视图
	 * 
	 * @param context
	 * @param title
	 * @param view
	 *            对话框内中间视图
	 * @param isCancelable
	 *            点返回是否关闭对话框，true关闭，false不关闭
	 * @param leftButton
	 *            按钮显示文字
	 * @param onLeftClickListener
	 *            确定按钮响应
	 * @return 当前显示对话框
	 */
	public static CustomDialog showDialog(Context context, int title, View view, boolean isCancelable, int resId,
			String leftButton, OnClickListener onLeftClickListener) {
		return showDialog(context, title, view, isCancelable, resId, leftButton, onLeftClickListener, null, null);
	}

	/**
	 * 显示一个按钮,中间内容为文字
	 * 
	 * @param context
	 * @param title
	 * @param message
	 *            对话框内文字
	 * @param isCancelable
	 *            点返回是否关闭对话框，true关闭，false不关闭
	 * @param leftButton
	 *            按钮显示文字
	 * @param onLeftClickListener
	 *            确定按钮的响应
	 * @return 当前显示对话框
	 */
	public static CustomDialog showDialog(Context context, int title, int message, boolean isCancelable,
			String leftButton, OnClickListener onLeftClickListener) {
		return showDialog(context, title, message, isCancelable, R.drawable.ic_dialog_error, leftButton,
				onLeftClickListener, null, null);
	}

	/**
	 * 显示两个按钮，左边是确定，右边是取消,中间内容为文字
	 * 
	 * @param context
	 * @param title
	 * @param message
	 *            dialog中文字显示
	 * @param isCancelable
	 *            点返回是否关闭对话框，true关闭，false不关闭
	 * @param onLeftClickListener
	 *            左边按钮响应
	 * @param onRightClickListener
	 *            右边按钮响应
	 * @return 当前显示对话框
	 */
	public static CustomDialog showDialog(Context context, int title, int message, boolean isCancelable, int redId,
			OnClickListener onLeftClickListener, OnClickListener onRightClickListener) {
		return showDialog(context, title, message, isCancelable, redId, null, onLeftClickListener, null,
				onRightClickListener);
	}

	/**
	 * 显示两个按钮，左边是确定，右边是取消，中间内容为视图
	 * 
	 * @param context
	 * @param title
	 * @param view
	 *            dialog中视图显示
	 * @param isCancelable
	 *            点返回是否关闭对话框，true关闭，false不关闭
	 * @param onLeftClickListener
	 *            左边按钮响应
	 * @param onRightClickListener
	 *            右边按钮响应
	 * @return 当前显示对话框
	 */
	public static CustomDialog showDialog(Context context, int title, View view, boolean isCancelable, int resId,
			OnClickListener onLeftClickListener, OnClickListener onRightClickListener) {
		return showDialog(context, title, view, isCancelable, resId, null, onLeftClickListener, null,
				onRightClickListener);
	}

	/**
	 * 显示两个按钮
	 * 
	 * @param context
	 * @param title
	 *            dialog标题
	 * @param view
	 *            显示视图
	 * @param isCancelable
	 *            点返回是否关闭对话框，true关闭，false不关闭
	 * @param leftButtonText
	 *            null为确定
	 * @param onLeftClickListener
	 *            null左边按钮隐藏
	 * @param rightButtonText
	 *            null为取消
	 * @param onRightClickListener
	 *            null右边按钮隐藏
	 * @return 当前显示对话框
	 */
	public static CustomDialog showDialog(Context context, int title, View view, boolean isCancelable, int resId,
			String leftButtonText, OnClickListener onLeftClickListener, String rightButtonText,
			OnClickListener onRightClickListener) {
		CustomDialog dialog = new CustomDialog(context);
		dialog.setDialogTitle(title);
		dialog.setDialogView(view);
		dialog.setCancelable(isCancelable);
		dialog.setLeftButtonText(leftButtonText);
		dialog.setLeftButtonListener(onLeftClickListener);
		dialog.setRightButtonText(rightButtonText);
		dialog.setRightButtonListener(onRightClickListener);
		dialog.setTitleIcon(resId);
		dialog.show();
		return dialog;
	}

	/**
	 * 显示两个按钮
	 * 
	 * @param context
	 * @param title
	 *            dialog标题
	 * @param message
	 *            显示文本内容
	 * @param isCancelable
	 *            点返回是否关闭对话框，true关闭，false不关闭
	 * @param leftButtonText
	 *            null为确定
	 * @param onLeftClickListener
	 *            null左边按钮隐藏
	 * @param rightButtonText
	 *            null为取消
	 * @param onRightClickListener
	 *            null右边按钮隐藏
	 * @return 当前显示对话框
	 */
	public static CustomDialog showDialog(Context context, int title, int message, boolean isCancelable, int resId,
			String leftButtonText, OnClickListener onLeftClickListener, String rightButtonText,
			OnClickListener onRightClickListener) {
		CustomDialog dialog = new CustomDialog(context);
		dialog.setDialogTitle(title);
		dialog.setDialogMessage(message);
		dialog.setCancelable(isCancelable);
		dialog.setTitleIcon(resId);
		dialog.setLeftButtonText(leftButtonText);
		dialog.setLeftButtonListener(onLeftClickListener);
		dialog.setRightButtonText(rightButtonText);
		dialog.setRightButtonListener(onRightClickListener);
		dialog.show();
		return dialog;
	}

	// public static Dialog createLoadingDialog(Context context, String msg) {
	// View view = LayoutInflater.from(context).inflate(R.layout.loading_dialog,
	// null);
	// ImageView loading_icon = (ImageView)
	// view.findViewById(R.id.loading_icon);
	// TextView loading_text = (TextView) view.findViewById(R.id.loading_text);
	// Animation anim = AnimationUtils.loadAnimation(context,
	// R.anim.loading_animation);
	// LinearInterpolator lir = new LinearInterpolator();
	// anim.setInterpolator(lir);
	// loading_icon.startAnimation(anim);
	// loading_text.setText(msg);
	//
	// Dialog dialog = new Dialog(context, R.style.loading_dialog);
	// dialog.setContentView(view, new
	// LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
	// LinearLayout.LayoutParams.WRAP_CONTENT));
	// dialog.setCancelable(true);
	// dialog.setCanceledOnTouchOutside(false);
	// return dialog;
	//
	// }

	/**
	 * 不显示按钮
	 * 
	 * @param context
	 * @param title
	 *            标题
	 * @param view
	 *            中间视图
	 * @param isCancelable
	 *            按返回键是否可取消
	 * @param resId
	 *            左上角图标
	 * @return
	 */
	public static CustomDialog showDialog(Context context, int title, View view, boolean isCancelable, int resId) {
		CustomDialog dialog = new CustomDialog(context);
		dialog.setDialogTitle(title);
		dialog.setDialogView(view);
		dialog.setCancelable(isCancelable);
		dialog.setTitleIcon(resId);
		dialog.show();
		return dialog;
	}

}
