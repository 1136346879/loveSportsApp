package cmccsi.mhealth.app.sports.ecg.activity;

import java.util.List;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.SeriesSelection;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;
import org.xclcharts.chart.PointD;
import org.xclcharts.views.SplineChartView.OnItemClickListener;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.Paint.Align;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import cmccsi.mhealth.app.sports.basic.BaseActivity;
import cmccsi.mhealth.app.sports.bean.DataECG;
import cmccsi.mhealth.app.sports.common.Common;
import cmccsi.mhealth.app.sports.db.MHealthProviderMetaData;
import cmccsi.mhealth.app.sports.ecg.utils.ChartDataSource;
import cmccsi.mhealth.app.sports.ecg.utils.DataSourceBuilder;
import cmccsi.mhealth.app.sports.ecg.utils.ECGDataFilter;
import cmccsi.mhealth.app.sports.ecg.utils.Range;
import cmccsi.mhealth.app.sports.ecg.utils.RangeUtil;
import cmccsi.mhealth.app.sports.R;

public class ECGDetailChartActivity extends BaseActivity implements
		OnItemClickListener, OnClickListener {

	private XYMultipleSeriesDataset mDataset = new XYMultipleSeriesDataset();
	private XYMultipleSeriesRenderer mRenderer = new XYMultipleSeriesRenderer();
	private XYSeries mCurrentSeries;
	private GraphicalView mChartView;

	public static final int TYPE_HR = 1;
	public static final int TYPE_HRV = 2;
	public static final int TYPE_MOOD = 3;

	public static final String EXTRA_RANGE = "range";
	public static final String EXTRA_TYPE = "extra_type";

	private int chartType = TYPE_HR;
	private int rangeType = RangeUtil.RANGE_TYPE_TODAY;
	private Dialog dialog;
	private LinearLayout dialogRoot;
	private TextView textValue;
	private TextView textDatatime;
//	private TextView textComment;
	private Range range;
	private ChartDataSource datasource;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_ecg_chart);

		mRenderer.setApplyBackgroundColor(true);
		mRenderer.setBackgroundColor(Color.WHITE);
		mRenderer.setMarginsColor(Color.WHITE);
		mRenderer.setAxisTitleTextSize(Common.dip2px(this, 13));
		mRenderer.setChartTitleTextSize(20);
		mRenderer.setLabelsTextSize(Common.dip2px(this, 13));
		mRenderer.setLegendTextSize(15);
		mRenderer.setXLabelsColor(Color.BLACK);
		mRenderer.setYLabelsColor(0, Color.BLACK);

		// top, left, bottom, right
		mRenderer.setMargins(new int[] { Common.dip2px(this, 20),
				Common.dip2px(this, 30), 0, Common.dip2px(this, 10) });
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

		chartType = getIntent().getIntExtra(EXTRA_TYPE, TYPE_HR);
		TextView textTitle = (TextView) findViewById(R.id.textView_title);
		String title = chartType == 1 ? "心率趋势" : (chartType == 2 ? "心率变异率趋势"
				: "情绪状态趋势");
		textTitle.setText(title);

		rangeType = getIntent().getIntExtra(EXTRA_RANGE,
				RangeUtil.RANGE_TYPE_TODAY);
		range = RangeUtil.getRange(rangeType);
		MHealthProviderMetaData provider = MHealthProviderMetaData
				.GetMHealthProvider(this);
		List<DataECG> ecgDataList = provider.getEcgDataByTime(
				range.getStartTime(), range.getEndTime());
		if (ecgDataList != null && ecgDataList.size()>0) {
			List<DataECG> ecgDataFilteredList = new ECGDataFilter().filter(
					rangeType, ecgDataList);
			bindData(ecgDataFilteredList);
		} else {
			this.finish();
		}

		findViewById(R.id.button_input_bg_back).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						finish();
					}
				});
		initDialog();
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (mChartView == null) {
			LinearLayout layout = (LinearLayout) findViewById(R.id.chart);
			mChartView = ChartFactory.getLineChartView(this, mDataset,
					mRenderer);

			// enable the chart click events
			mRenderer.setClickEnabled(true);
			int size = Common.dip2px(this, 10);
			mRenderer.setSelectableBuffer(size);
			mChartView.setOnClickListener(this);
			layout.addView(mChartView, new LayoutParams(
					LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));

		} else {
			mChartView.repaint();
		}
	}
	
	@Override
	public void onBackPressed() {
		if (dialog.isShowing()) {
			dialog.dismiss();
		}
		super.onBackPressed();
	}

	private void initDialog() {
		dialog = new Dialog(this, R.style.dialog_fullscreen);
		dialog.setContentView(R.layout.ecg_dialog);
		Window dialogWindow = dialog.getWindow();
		WindowManager.LayoutParams lp = dialogWindow.getAttributes();
		dialogWindow.setGravity(Gravity.TOP);
		lp.width = LayoutParams.MATCH_PARENT;
		lp.height = LayoutParams.WRAP_CONTENT;
		dialogWindow.setAttributes(lp);

		dialogRoot = (LinearLayout) dialog.findViewById(R.id.layout_dialog);
		textValue = (TextView) dialog.findViewById(R.id.textview_hr_dialog);
		textDatatime = (TextView) dialog
				.findViewById(R.id.textView_datatime_dialog);
//		textComment = (TextView) dialog
//				.findViewById(R.id.textView_comment_dialog);
		View v = dialog.findViewById(R.id.layout_container);
		v.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
	}

	private void bindData(List<DataECG> ecgDataList) {
		DataSourceBuilder builder = new DataSourceBuilder(range, chartType,
				rangeType, ecgDataList);
		datasource = builder.build();
		List<PointD> points = datasource.getPoints();
		for (PointD p : points) {
			mCurrentSeries.add(p.x, p.y);
		}
		mRenderer.setYAxisMax(datasource.getMaxY() + 20);
		mRenderer.setXAxisMax(datasource.getMaxX());
		mRenderer.setXAxisMin(0);
		mRenderer.setYAxisMin(0);
		List<String> labels = datasource.getLables();
		mRenderer.setXLabels(0);
		mRenderer.clearXTextLabels();
		double offset = 0;
		for (int i = 0; i < labels.size(); i++) {
			String label = labels.get(i);
			if (label != "") {
				mRenderer.addXTextLabel(offset, label);
			}
			offset += 10;
		}
	}

	private void showHr(double val, String date) {
		if (dialog.isShowing()) {
			dialog.dismiss();
		}
		textValue.setText(" " + (int) val);
		textDatatime.setText("测量于 " + date);
		dialogRoot.setBackground(getResources().getDrawable(
				R.drawable.hr_normal));
//		if (val < 105 && val > 55) {
//			dialogRoot.setBackground(getResources().getDrawable(
//					R.drawable.hr_normal));
//			textComment.setText("心率处于健康范围，心脏健康状态良好");
//		} else {
//			dialogRoot.setBackground(getResources().getDrawable(
//					R.drawable.hr_abnormal));
//			if (val > 105) {
//				textComment.setText("存在心动过速风险");
//			} else {
//				textComment.setText("存在心动过缓风险");
//			}
//		}
		dialog.show();
	}

	private void showHrv(double val, String date) {
		if (dialog.isShowing()) {
			dialog.dismiss();
		}
		textValue.setText(" " + (int) val);
		textDatatime.setText("测量于 " + date);
		dialogRoot.setBackground(getResources().getDrawable(
				R.drawable.hrv_normal));
//		textComment.setText("心率变异率反映一段时间内的心脏健康指数，平均值越高心脏越健康");
		dialog.show();
	}

	private void showMood(double val, String date) {
		if (dialog.isShowing()) {
			dialog.dismiss();
		}
		textValue.setText(" " + (int) val);
		textDatatime.setText("测量于 " + date);
		dialogRoot.setBackground(getResources().getDrawable(
				R.drawable.stress_normal));
//		if (val < 50) {
//			dialogRoot.setBackground(getResources().getDrawable(
//					R.drawable.stress_normal));
//			textComment.setText("近期压力水平正常，情绪较平静放松");
//		} else {
//			dialogRoot.setBackground(getResources().getDrawable(
//					R.drawable.stress_abnormal));
//
//			textComment.setText("近期压力较高，情绪较紧张或激动");
//		}
		dialog.show();
	}

	@Override
	public void itemClick(PointD p) {
		String date = datasource.getDateMap().get(p.x);
		switch (chartType) {
		case TYPE_HR:
			showHr(p.y, date);
			break;
		case TYPE_HRV:
			showHrv(p.y, date);
			break;
		case TYPE_MOOD:
			showMood(p.y, date);
			break;
		}
	}

	@Override
	public void onClick(View v) {

		SeriesSelection seriesSelection = mChartView.getCurrentSeriesAndPoint();
		if (seriesSelection != null) {
			int index = seriesSelection.getPointIndex();
			List<String> dates = datasource.getDateList();
			if (index >= 0 && index < dates.size()) {
				String date = dates.get(index);
				switch (chartType) {
				case TYPE_HR:
					showHr(seriesSelection.getValue(), date);
					break;
				case TYPE_HRV:
					showHrv(seriesSelection.getValue(), date);
					break;
				case TYPE_MOOD:
					showMood(seriesSelection.getValue(), date);
					break;
				}
			}
		}
	}
}
