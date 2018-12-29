package org.xclcharts.views;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.xclcharts.chart.PointD;
import org.xclcharts.chart.SplineChart;
import org.xclcharts.chart.SplineData;
import org.xclcharts.common.DensityUtil;
import org.xclcharts.renderer.XChart;
import org.xclcharts.renderer.XEnum;
import org.xclcharts.view.ChartView;

import cmccsi.mhealth.app.sports.common.Common;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

public class StaticSplineChartView extends ChartView {

	private static String TAG = "StaticSplineChartView";
	private SplineChart chart = new SplineChart();
	// 分类轴标签集合
	private LinkedList<String> labels = new LinkedList<String>();
	private LinkedList<SplineData> chartData = new LinkedList<SplineData>();
	private boolean hasData = false;
	public StaticSplineChartView(Context context) {
		super(context);
		initView();
	}

	public StaticSplineChartView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView();
	}

	public StaticSplineChartView(Context context, AttributeSet attrs,int defStyle) {
		super(context, attrs, defStyle);
		initView();
	}

	public void setDataAxisMax(double max) {
		chart.getDataAxis().setAxisMax(max);
		invalidate();
	}

	public void setDataAxisStep(double step) {
		chart.getDataAxis().setAxisSteps(step);
		invalidate();
	}

	public void setLabelAxisMax(double max) {
		chart.setCategoryAxisMax(max);
		invalidate();
	}
	
	public void setDataset(List<PointD> data) {
		hasData = data.size()>0;
		SplineData dataSeries = new SplineData(null, data, Color.BLUE);
		dataSeries.getLinePaint().setStrokeWidth(2);
		dataSeries.setDotRadius(Common.dip2px(getContext(), 2));
		chartData.clear();
		chartData.add(dataSeries);
		invalidate();
	}
	
	public void setLabel(List<String> labels){
		this.labels.clear();
		this.labels.addAll(labels);
		invalidate();
	}

	private void initView() {
		chartRender();
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		chart.setChartRange(w, h);
	}

	private void chartRender() {
		try {
			int padding = DensityUtil.dip2px(getContext(), 5);
			chart.setPadding(padding, padding, padding, padding);
//			chart.setPadding(0 , 0, 0, 5);
			// 数据源
			chart.setCategories(labels);
			chart.setDataSource(chartData);

			// 坐标系
			// 数据轴最大值
			chart.getDataAxis().setAxisMax(100);
			// 数据轴刻度间隔
			chart.getDataAxis().setAxisSteps(10);

			// 标签轴最大值
			chart.setCategoryAxisMax(100);
			// 标签轴最小值
			chart.setCategoryAxisMin(-5);
			
			chart.getDataAxis().hideTickMarks();
			chart.getCategoryAxis().hide();
			chart.getDataAxis().hide();
			// 调整轴显示位置
			chart.setCrurveLineStyle(XEnum.CrurveLineStyle.BEELINE);
			chart.getCategoryAxis().hideAxisLabels();
			chart.disablePanMode();

		} catch (Exception e) {
			Log.d(TAG, e.toString());
		}
	}
	
	public boolean hasData() {
		return hasData;
	}

	@Override
	@SuppressLint("ClickableViewAccessibility")
	public boolean onTouchEvent(MotionEvent event) {
		super.onTouchEvent(event);
		if(event.getAction() == MotionEvent.ACTION_UP) 
		{			
			performClick();
		}
		return true; 
	}

	@Override
	public void render(Canvas canvas) {
		try {
			if(labels.size()>0 && chartData.size()>0){
				chart.render(canvas);
			}
		} catch (Exception e) {
			Log.d(TAG, e.toString());
		}
	}

	@Override
	public List<XChart> bindChart() {
		List<XChart> lst = new ArrayList<XChart>();
		lst.add(chart);
		return lst;
	}
}
