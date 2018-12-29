package cmccsi.mhealth.app.sports.common.tasks;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.os.AsyncTask;
import android.util.Pair;

/**
 * task基类
 * 
 * @author xianlin.liang
 * 
 * @param <Input>
 * @param <Result>
 */
public abstract class BaseTask<Input, Result> extends AsyncTask<Input, Void, Pair<Exception, Result>> {

	protected Context mActivity = null;

	private boolean isShow = true;
	private ProgressDialog mProgressDialog;

	public void setShowDialog(boolean isShow) {
		this.isShow = isShow;
	}

	public BaseTask(Context activity, String message) {
		this.mActivity = activity;
		mProgressDialog = new ProgressDialog(activity);
		mProgressDialog.setMessage(message);
		// mProgressDialog.setCancelable(false);
		mProgressDialog.setCanceledOnTouchOutside(false);
		mProgressDialog.setOnCancelListener(new OnCancelListener() {

			@Override
			public void onCancel(DialogInterface dialog) {
				doCancelled();
			}
		});
	}

	@Override
	protected void onPreExecute() {
		if (isShow) {
			mProgressDialog.show();
		}
		super.onPreExecute();
	}

	@Override
	protected final Pair<Exception, Result> doInBackground(Input... params) {
		Result res = null;
		Exception ex = null;
		try {
			res = onExecute(params);
		} catch (Exception e) {
			e.printStackTrace();
			ex = e;
		}
		return new Pair<Exception, Result>(ex, res);
	}

	@Override
	protected final void onPostExecute(Pair<Exception, Result> result) {
		super.onPostExecute(result);
		try {
			if (result.first != null) {
				doError(result.first);
			} else {
				doStuffWithResult(result.second);
			}
		} catch (Throwable t) {

		}
		if (mProgressDialog != null && mProgressDialog.isShowing()) {
			mProgressDialog.dismiss();
		}
	}

	protected abstract Result onExecute(Input... params) throws Exception;

	protected void doStuffWithResult(Result result) {
	}

	protected void doError(Exception exception) {
	}

	/**
	 * 处理取消的方法
	 * 
	 */
	protected void doCancelled() {
	}
}
