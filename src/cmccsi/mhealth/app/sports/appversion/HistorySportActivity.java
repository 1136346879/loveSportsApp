package cmccsi.mhealth.app.sports.appversion;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.SeriesSelection;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;
import org.json.JSONObject;
import org.xclcharts.chart.PointD;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.Paint.Align;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;
import cmccsi.mhealth.app.sports.R;
import cmccsi.mhealth.app.sports.basic.BaseActivity;
import cmccsi.mhealth.app.sports.common.Common;
import cmccsi.mhealth.app.sports.common.PreferencesUtils;
import cmccsi.mhealth.app.sports.common.SharedPreferredKey;
import cmccsi.mhealth.app.sports.net.NetworkTool;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;

@ContentView(R.layout.activity_historysport)
public class HistorySportActivity extends BaseActivity {
	private String TAG = HistorySportActivity.class.getSimpleName();
	@ViewInject(R.id.historysport_textview_step)
	private TextView textView_step;
	@ViewInject(R.id.historysport_textview_cal)
	private TextView textView_cal;
	@ViewInject(R.id.historysport_textview_length)
	private TextView textView_length;
	@ViewInject(R.id.historysport_dialog_textview_step)
	private TextView textView_dialog_step;
	@ViewInject(R.id.historysport_dialog_textview_date)
	private TextView textView_dialog_date;
	@ViewInject(R.id.historysport_imagebutton_left)
	private ImageButton imagebutton_left;
	@ViewInject(R.id.historysport_imagebutton_right)
	private ImageButton imagebutton_right;
	@ViewInject(R.id.loadingImageView)
	private ImageView loadingImageView;
	@ViewInject(R.id.historysport_chart)
	private LinearLayout chart;
	@ViewInject(R.id.historysport_dialog)
	private LinearLayout dialog;
	@ViewInject(R.id.historysport_RadioGroup)
	private RadioGroup radiogroup;
	@ViewInject(R.id.historysport_radiobutton_day)
	private RadioButton radiobutton_day;

	private String uid = "";// 用户userId
	private String timeType = "";
	private String endTime = "";
	private String nowday = "";
	private String nowweek = "";
	private String nowmonth = "";
	private boolean page = false;

	private XYMultipleSeriesDataset mDataset = new XYMultipleSeriesDataset();
	private XYMultipleSeriesRenderer mRenderer = new XYMultipleSeriesRenderer();
	private XYSeries mCurrentSeries;
	private GraphicalView mChartView;

	private HistorySportNetItem back;// 从网端获取的当前数据

	private GestureDetector mGestureDetector;// chart手势监听

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ViewUtils.inject(this);
		BaseBackKey("历史统计", this);
		initData();
		showProgressDialog(getResources().getString(R.string.text_wait), HistorySportActivity.this);
		getMsgFromNet();
	}

	@Override
	protected void onResume() {
		super.onResume();

	}

	/**
	 * 初始化数据
	 */
	private void initData() {
		radiobutton_day.setChecked(true);
		uid = PreferencesUtils.getString(this, SharedPreferredKey.USERUID, "");
		timeType = "day";
		endTime = "";
		nowday = getStringDateShort();
		page = false;
		imagebutton_right.setVisibility(View.INVISIBLE);
		imagebutton_right.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (0 == NetworkTool.getNetworkState(getApplicationContext())) {
					Toast.makeText(HistorySportActivity.this, getResources().getString(R.string.MESSAGE_SERVER_EXCEPTION),
							Toast.LENGTH_LONG).show();
					return;
				}
				page = true;
				if (null == back) {
					return;
				}
				if ("day".equals(timeType)) {
					endTime = getNextDay(back.endTime, "7");
				} else if ("week".equals(timeType)) {
					endTime = getNextDay(back.endTime, "49");
				} else if ("month".equals(timeType)) {
					endTime = getNextMonth(back.endTime, true) + "-01";
				}
				Log.e("  ", endTime);
				getMsgFromNet();
			}
		});
		imagebutton_left.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (0 == NetworkTool.getNetworkState(getApplicationContext())) {
					Toast.makeText(HistorySportActivity.this, getResources().getString(R.string.MESSAGE_SERVER_EXCEPTION),
							Toast.LENGTH_LONG).show();
					return;
				}
				imagebutton_right.setVisibility(View.VISIBLE);
				page = true;
				if (null == back) {
					return;
				}
				if ("day".equals(timeType)) {
					endTime = getNextDay(back.startTime, "-1");
				} else if ("week".equals(timeType)) {
					endTime = getNextDay(back.startTime, "-7");
				} else if ("month".equals(timeType)) {
					endTime = getNextMonth(back.startTime, false) + "-01";
				}
				getMsgFromNet();

			}
		});
		mGestureDetector = new GestureDetector(this, new YScrollDetector());
		mRenderer.setApplyBackgroundColor(true);
		mRenderer.setBackgroundColor(Color.WHITE);
		mRenderer.setMarginsColor(Color.WHITE);
		mRenderer.setAxisTitleTextSize(Common.dip2px(this, 13));
		mRenderer.setChartTitleTextSize(20);
		mRenderer.setLabelsTextSize(Common.dip2px(this, 11));
		mRenderer.setLegendTextSize(15);
		mRenderer.setXLabelsColor(Color.BLACK);
		mRenderer.setYLabelsColor(0, Color.BLACK);
		mRenderer.setPanEnabled(false);// 设置是否可以平移
		// top, left, bottom, right
		mRenderer.setMargins(new int[] { Common.dip2px(this, 20), Common.dip2px(this, 50), 0, Common.dip2px(this, 10) });
		mRenderer.setPointSize(8);
		mRenderer.setShowGrid(true);
		mRenderer.setAxesColor(Color.BLACK);
		mRenderer.setYLabelsAlign(Align.RIGHT);
		XYSeries series = new XYSeries("");
		mDataset.addSeries(series);
		mCurrentSeries = series;

		XYSeriesRenderer renderer = new XYSeriesRenderer();
		mRenderer.addSeriesRenderer(renderer);
		renderer.setPointStyle(PointStyle.CIRCLE);
		renderer.setFillPoints(true);
		renderer.setDisplayChartValues(false);
		renderer.setDisplayChartValuesDistance(15);
		renderer.setShowLegendItem(false);
		radiogroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				switch (checkedId) {
				case R.id.historysport_radiobutton_day:
					timeType = "day";
					endTime = "";
					chatLoading();
					getMsgFromNet();
					page = false;
					closechatLoading();
					break;
				case R.id.historysport_radiobutton_week:
					timeType = "week";
					endTime = "";
					chatLoading();
					closechatLoading();
					getMsgFromNet();
					page = false;
					break;
				case R.id.historysport_radiobutton_month:
					timeType = "month";
					endTime = "";
					chatLoading();
					closechatLoading();
					getMsgFromNet();
					page = false;
					break;

				default:
					break;
				}
			}
		});

	}

	private void bindData(HistorySportNetItem netItem) {
		closechatLoading();
		mCurrentSeries.clear();
		ArrayList<NetItem> datavalue = netItem.datavalue;
		List<String> labelsX = new ArrayList<String>();
		List<PointD> points = new ArrayList<PointD>();
		double maxY = 0;
		double maxX = 0;
		for (int i = 0; i < datavalue.size(); i++) {
			points.add(new PointD(i, Double.parseDouble(datavalue.get(i).stepNum)));
			if ("day".equals(netItem.timetype)) {
				labelsX.add(datavalue.get(i).date.substring(5, 10));
			} else if ("week".equals(netItem.timetype)) {
				labelsX.add(datavalue.get(i).date.substring(5, 10));
			} else if ("month".equals(netItem.timetype)) {
				labelsX.add(datavalue.get(i).date.substring(5, 7));
			}
		}

		for (PointD p : points) {
			mCurrentSeries.add(p.x, p.y);
			maxY = p.y > maxY ? p.y : maxY;
			maxX = p.x > maxX ? p.x : maxX;
		}
		mRenderer.setYAxisMax(maxY * 1.1);
		mRenderer.setXAxisMax(maxX + 0.5);
		mRenderer.setXAxisMin(-0.5);
		mRenderer.setYAxisMin(0);
		mRenderer.setXLabels(0);
		mRenderer.setYLabels(0);
		mRenderer.clearXTextLabels();
		mRenderer.clearYTextLabels();
		double offsetX = 0;
		for (int i = 0; i < labelsX.size(); i++) {
			String label = labelsX.get(i);
			if (label != "") {
				mRenderer.addXTextLabel(offsetX, label);
			}
			offsetX += 1;
		}

		int offsetY = 0;
		for (int i = 0; i < 6; i++) {
			String label = offsetY + "";
			// if (offsetY >= 1000 && offsetY < 10000) {
			// label = ((float) (Math.round(offsetY * 10/ 1000 )) / 10.0) + "千";
			// } else
			if (offsetY >= 10000) {
				label = ((float) (Math.round(offsetY * 10 / 10000)) / 10.0) + "万";
			}
			if (label != "") {
				mRenderer.addYTextLabel(offsetY, label);
			}
			offsetY += (int) maxY / 5;
		}

		if (mChartView == null) {
			mChartView = ChartFactory.getLineChartView(this, mDataset, mRenderer);

			// enable the chart click events
			mRenderer.setClickEnabled(true);
			int size = Common.dip2px(this, 10);
			mRenderer.setSelectableBuffer(size);
			mChartView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					SeriesSelection seriesSelection = mChartView.getCurrentSeriesAndPoint();
					if (seriesSelection != null) {
						int index = seriesSelection.getPointIndex();
						String date_dialog = back.datavalue.get(index).date;
						String step_dialog = back.datavalue.get(index).stepNum;
						textView_dialog_step.setText(step_dialog);
						textView_dialog_date.setText(date_dialog);
						dialog.setVisibility(View.VISIBLE);
						opinionHandle.removeMessages(hidedialog);
						opinionHandle.sendEmptyMessageDelayed(hidedialog, 2000);
					}
				}
			});
			chart.addView(mChartView, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
			mChartView.setOnTouchListener(new OnTouchListener() {
				
				@Override
				public boolean onTouch(View v, MotionEvent event) {
					return mGestureDetector.onTouchEvent(event);
				}
			});
		} else {
			mChartView.repaint();
		}
	}

	private GetNetMsgThread getNetMsgThread = null; // 获取网络数据的线程

	private void getMsgFromNet() {
		// 获取当前用户信息
		if (0 == NetworkTool.getNetworkState(getApplicationContext())) {
			Toast.makeText(HistorySportActivity.this, getResources().getString(R.string.MESSAGE_SERVER_EXCEPTION),
					Toast.LENGTH_LONG).show();
			return;
		}
		if (null != getNetMsgThread) {
			getNetMsgThread.stopMyThread();
			getNetMsgThread = null;
		}
		getNetMsgThread = new GetNetMsgThread();
		getNetMsgThread.start();
		opinionHandle.postDelayed(new Runnable() {
			@Override
			public void run() {
				if (null != getNetMsgThread) {
					opinionHandle.sendEmptyMessage(error1);
				}
			}
		}, 20 * 1000);
	}

	/**
	 * 获取数据的线程
	 * 
	 * @author luckchoudog
	 */
	class GetNetMsgThread extends Thread {
		private boolean isThreadRun = false;

		public GetNetMsgThread() {
		}

		public boolean isThreadRuning() {
			return isThreadRun;
		}

		public void stopMyThread() {
			if (getNetMsgThread == null) {
				return;
			}
			GetNetMsgThread tmpThread = getNetMsgThread;
			getNetMsgThread = null;
			if (tmpThread != null) {
				tmpThread.interrupt();
				isThreadRun = false;
			}
		}

		public void run() {
			if (getNetMsgThread == null) {
				return; // stopped before started.
			}
			try {
				isThreadRun = true;
				// { "uid", "timeType", "endTime"},//4.3 运动历史统计8
				HashMap<String, String> arg = new HashMap<String, String>();
				String json = "";
				if ("day".equals(timeType) && !page) {
					arg.put("uid", uid);
					json = WebServiceManage.get(arg, 8);
				} else if ("week".equals(timeType) && !page) {
					arg.put("uid", uid);
					arg.put("timeType", timeType);
					json = WebServiceManage.get(arg, 9);
				} else if ("month".equals(timeType) && !page) {
					arg.put("uid", uid);
					arg.put("timeType", timeType);
					json = WebServiceManage.get(arg, 9);
				} else {
					arg.put("uid", uid);
					arg.put("timeType", timeType);
					arg.put("endTime", endTime);
					json = WebServiceManage.get(arg, 10);
				}
				Log.e(TAG, "json = " + json);
				if (null == json || "".equals(json)) {
					opinionHandle.sendEmptyMessage(error1);// 网络错误
					return;
				}

				JSONObject jsonObject = new JSONObject(json);
				String s = jsonObject.getString("status");
				Log.e(TAG, "s = " + s);
				if (s == null || s.equals("FAILURE")) {// 返回失败
					Message message = new Message();
					message.what = error2;
					message.obj = jsonObject.getString("message");
					opinionHandle.sendMessage(message);
					return;
				} else if (s.equals("SUCCESS")) {// 成功
					HistorySportNetItem back = new HistorySportNetItem();
					back.totalStep = jsonObject.getString("totalStep");
					back.totalCal = jsonObject.getString("totalCal");
					back.totalDistance = jsonObject.getString("totalDistance");
					back.timetype = jsonObject.getString("timeType");
					back.startTime = jsonObject.getString("startTime");
					back.endTime = jsonObject.getString("endTime");
					back.datavalue = NetItem.getclazz2(jsonObject.getString("datavalue"));

					Message message = new Message();
					message.what = success;
					message.obj = back;
					opinionHandle.sendMessage(message);
				}
				Thread.yield();
				if (Thread.currentThread().isInterrupted()) {
					throw new InterruptedException("Stopped by ifInterruptedStop()");
				}
			} catch (Throwable t) {
				isThreadRun = false;
				System.out.println("-----------线程干掉------------" + t);
			}
		}
	}

	private static final int error1 = 10000;// 网络错误
	private static final int error2 = 10002;// 返回失败
	private static final int success = 10003;// 返回成功
	private static final int hidedialog = 10004;// 返回成功
	@SuppressLint("HandlerLeak")
	private Handler opinionHandle = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			dismiss();
			if (null != getNetMsgThread) {
				getNetMsgThread.stopMyThread();
				getNetMsgThread = null;
			}
			switch (msg.what) {
			case error1:// 网络错误
				// Toast.makeText(HistorySportActivity.this, "网络异常，请检查网络设置！",
				// Toast.LENGTH_LONG).show();
				break;
			case error2:// 返回失败
				Toast.makeText(HistorySportActivity.this, msg.obj + "", Toast.LENGTH_LONG).show();
				break;
			case success:// 返回成功
				Log.e(TAG, "success ");
				back = (HistorySportNetItem) msg.obj;
				if ("day".equals(back.timetype) && !page) {
					int step_int = Integer.parseInt(back.totalStep);
					String step = "";
					if (step_int >= 10000) {
						step_int = step_int / 10000;
						step = "<font color=\"#909632\" >" + step_int + "</font>" + "<font color=\"#323232\">" + " 万步"
								+ "</font>";
					} else if (step_int >= 1000) {
						step_int = step_int / 1000;
						step = "<font color=\"#909632\" >" + step_int + "</font>" + "<font color=\"#323232\">" + " 千步"
								+ "</font>";
					} else {
						step = "<font color=\"#909632\" >" + step_int + "</font>" + "<font color=\"#323232\">" + " 步" + "</font>";
					}
					textView_step.setText(Html.fromHtml(step));
					long distance_int = Long.parseLong(back.totalDistance);
					String distance = "";
					if (distance_int >= 10000000) {
						distance_int = distance_int / 10000000;
						distance = "<font color=\"#909632\" >" + distance_int + "</font>" + "<font color=\"#323232\">" + " 万千米"
								+ "</font>";
					} else if (distance_int >= 1000) {
						distance_int = distance_int / 1000;
						distance = "<font color=\"#909632\" >" + distance_int + "</font>" + "<font color=\"#323232\">" + " 千米"
								+ "</font>";
					} else {
						distance = "<font color=\"#909632\" >" + distance_int + "</font>" + "<font color=\"#323232\">" + " 米"
								+ "</font>";
					}
					textView_length.setText(Html.fromHtml(distance));

					int distance_cal = Integer.parseInt(back.totalCal);
					String cal = "";
					if (distance_cal >= 10000) {
						distance_cal = distance_cal / 10000;
						cal = "<font color=\"#909632\" >" + distance_cal + "</font>" + "<font color=\"#323232\">" + " 万千卡"
								+ "</font>";
					} else {
						cal = "<font color=\"#909632\" >" + distance_cal + "</font>" + "<font color=\"#323232\">" + " 千卡"
								+ "</font>";
					}
					textView_cal.setText(Html.fromHtml(cal));
				}
				if ("day".equals(back.timetype) && !page) {
					nowday = back.endTime;
				} else if ("week".equals(back.timetype) && !page) {
					nowweek = back.endTime;
				} else if ("month".equals(back.timetype) && !page) {
					nowmonth = back.endTime;
				}
				if (nowday.equals(back.endTime) || nowweek.equals(back.endTime) || nowmonth.equals(back.endTime)) {
					imagebutton_right.setVisibility(View.INVISIBLE);
				}

				bindData(back);
				break;
			case hidedialog:
				dialog.setVisibility(View.GONE);
				break;
			default:
				break;
			}
		}
	};

	/**
	 * 图表中loading
	 */
	private void chatLoading() {
		loadingImageView.setVisibility(View.VISIBLE);
		chart.setVisibility(View.INVISIBLE);
		Animation hyperspaceJumpAnimation = AnimationUtils.loadAnimation(this, R.anim.progess_round);
		// 使用ImageView显示动画
		loadingImageView.startAnimation(hyperspaceJumpAnimation);
	}

	/**
	 * 关闭图表中loading
	 */
	private void closechatLoading() {
		loadingImageView.setVisibility(View.GONE);
		chart.setVisibility(View.VISIBLE);
	}

	/**
	 * 得到一个时间延后或前移几天的时间,nowdate为时间,delay为前移或后延的天数，保证时间字符格式为yyyy-MM-dd
	 * 
	 * @param sj1
	 *            时间字符格式为yyyy-MM-dd
	 * @param delay
	 *            为前移或后延的天数，负值为前移
	 */
	public static String getNextDay(String nowdate, String delay) {
		try {
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
			String mdate = "";
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
			ParsePosition pos = new ParsePosition(0);
			Date d = formatter.parse(nowdate, pos);
			long myTime = (d.getTime() / 1000) + Integer.parseInt(delay) * 24 * 60 * 60;
			d.setTime(myTime * 1000);
			mdate = format.format(d);
			return mdate;
		} catch (Exception e) {
			return "";
		}
	}

	/**
	 * 获取现在短时间字符串
	 * 
	 * @return 返回短时间字符串格式：年-月-日
	 */
	public static String getStringDateShort() {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
		String dateString = formatter.format(new Date());
		return dateString;
	}

	public static String getNextMonth(String nowdate, boolean isNext) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");// 格式化对象
		Calendar calendar = Calendar.getInstance();// 日历对象
		calendar.setTime(strToDate(nowdate));// 设置当前日期
		if (isNext) {
			calendar.add(Calendar.MONTH, 7);// 月份减一
		} else {
			calendar.add(Calendar.MONTH, -1);// 月份减一
		}
		Log.e("   ", sdf.format(calendar.getTime()));// 输出格式化的日期
		return sdf.format(calendar.getTime());
	}

	/**
	 * 将短时间格式字符串转换为时间 yyyy-MM-dd
	 * 
	 * @return 短时间格式 yyyy-MM-dd
	 */
	public static Date strToDate(String strDate) {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
		ParsePosition pos = new ParsePosition(0);
		Date strtodate = formatter.parse(strDate, pos);
		return strtodate;
	}

	class YScrollDetector extends SimpleOnGestureListener {

		@Override
		public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
			/*
			 * 如果我们滚动更接近水平方向,返回true拦截；否则返回false,让子视图来处理它
			 */
			if (Math.abs(distanceY) < Math.abs(distanceX)) {
				if (distanceX > 0) {
					if (imagebutton_right.getVisibility()==View.INVISIBLE) {
						return true;
					}
					page = true;
					if ("day".equals(timeType)) {
						endTime = getNextDay(back.endTime, "7");
					} else if ("week".equals(timeType)) {
						endTime = getNextDay(back.endTime, "49");
					} else if ("month".equals(timeType)) {
						endTime = getNextMonth(back.endTime, true) + "-01";
					}
					Log.e("  ", endTime);
					getMsgFromNet();
				} else {
					imagebutton_right.setVisibility(View.VISIBLE);
					page = true;
					if ("day".equals(timeType)) {
						endTime = getNextDay(back.startTime, "-1");
					} else if ("week".equals(timeType)) {
						endTime = getNextDay(back.startTime, "-7");
					} else if ("month".equals(timeType)) {
						endTime = getNextMonth(back.startTime, false) + "-01";
					}
					getMsgFromNet();
				}
			}
			return (Math.abs(distanceY) < Math.abs(distanceX));
		}
	}
}
