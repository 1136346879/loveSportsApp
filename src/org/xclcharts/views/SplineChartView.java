package org.xclcharts.views;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.xclcharts.chart.PointD;
import org.xclcharts.chart.SplineChart;
import org.xclcharts.chart.SplineData;
import org.xclcharts.common.DensityUtil;
import org.xclcharts.common.IFormatterTextCallBack;
import org.xclcharts.event.click.PointPosition;
import org.xclcharts.renderer.XChart;
import org.xclcharts.renderer.XEnum;
import org.xclcharts.renderer.plot.PlotGrid;
import org.xclcharts.view.ChartView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

public class SplineChartView extends ChartView {

	private static final String TAG = "SplineChartView";
	private SplineChart chart = new SplineChart();
	// 分类轴标签集合
	private LinkedList<String> labels = new LinkedList<String>();
	private LinkedList<SplineData> chartData = new LinkedList<SplineData>();
	private OnItemClickListener listener;
	
	public SplineChartView(Context context) {
		super(context);
		initView();
	}

	public SplineChartView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView();
	}

	public SplineChartView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initView();
	}

	public void setTitle(String title) {
		chart.setTitle(title);
		invalidate();
	}

	public void setSubTitle(String subTitle) {
		chart.addSubtitle(subTitle);
		invalidate();
	}

	public void setLabels(List<String> lbls) {
		labels.clear();
		labels.addAll(lbls);
		invalidate();
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

	public void setDataset(String tag, List<PointD> data) {
		SplineData dataSeries = new SplineData(tag, data, Color.BLUE);
		dataSeries.getLinePaint().setStrokeWidth(2);
		chartData.clear();
		chartData.add(dataSeries);
		invalidate();
	}

	private void initView() {
		chartRender();
	}

	
	
	public void setOnItemClickListener(OnItemClickListener listener) {
		this.listener = listener;
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		// 图所占范围大小
		chart.setChartRange(w, h);
	}

	private void chartRender() {
		try {

			// 设置绘图区默认缩进px值,留置空间显示Axis,Axistitle....
			int padding = DensityUtil.dip2px(getContext(), 5);
			int paddingLeft = DensityUtil.dip2px(getContext(), 30);
			int paddingBottom = DensityUtil.dip2px(getContext(), 20);
			chart.setPadding(paddingLeft, padding, padding, paddingBottom);

			// 显示边框
			chart.showRoundBorder();

			// 数据源
			chart.setCategories(labels);
			chart.setDataSource(chartData);

			// 坐标系
			// 数据轴最大值
			chart.getDataAxis().setAxisMax(100);
			// chart.getDataAxis().setAxisMin(0);
			// 数据轴刻度间隔
			chart.getDataAxis().setAxisSteps(10);

			// 标签轴最大值
			chart.setCategoryAxisMax(100);
			// 标签轴最小值
			chart.setCategoryAxisMin(0);

			// 背景网格
			PlotGrid plot = chart.getPlotGrid();
			plot.showHorizontalLines();
			plot.getHorizontalLinePaint().setStrokeWidth(1);
			plot.getHorizontalLinePaint().setColor(
					(int) Color.rgb(247, 247, 247));

			// 把轴线设成和横向网络线一样和大小和颜色,演示下定制性，这块问得人较多
			chart.getDataAxis().getAxisPaint().setStrokeWidth(2);
			chart.getCategoryAxis().getAxisPaint().setStrokeWidth(2);

			int gray = Color.rgb(208, 208, 208);
			chart.getDataAxis().getAxisPaint().setColor(gray);
			chart.getCategoryAxis().getAxisPaint().setColor(gray);

			chart.getDataAxis().getTickMarksPaint().setColor(gray);
			chart.getCategoryAxis().getTickMarksPaint().setColor(gray);

			// 定义交叉点标签显示格式,特别备注,因曲线图的特殊性，所以返回格式为: x值,y值
			// 请自行分析定制
			chart.setDotLabelFormatter(new IFormatterTextCallBack() {

				@Override
				public String textFormatter(String value) {
					String label = "(" + value + ")";
					return label;
				}

			});

			// 激活点击监听
			chart.ActiveListenItemClick();
			// 为了让触发更灵敏，可以扩大5px的点击监听范围
			chart.extPointClickRange(DensityUtil.dip2px(getContext(), 5));
			chart.setCrurveLineStyle(XEnum.CrurveLineStyle.BEELINE);
		} catch (Exception e) {
			e.printStackTrace();
			Log.e(TAG, e.toString());
		}
	}

	@Override
	public void render(Canvas canvas) {
		try {
			if (labels.size() > 0 && chartData.size() > 0) {
				chart.render(canvas);
			}
		} catch (Exception e) {
			Log.e(TAG, e.toString());
		}
	}

	@Override
	public List<XChart> bindChart() {
		List<XChart> lst = new ArrayList<XChart>();
		lst.add(chart);
		return lst;
	}

	
	@Override
	@SuppressLint("ClickableViewAccessibility")
	public boolean onTouchEvent(MotionEvent event) {
		super.onTouchEvent(event);
		if (event.getAction() == MotionEvent.ACTION_UP) {
			triggerClick(event.getX(), event.getY());
		}
		return true;
	}

	// 触发监听
	private void triggerClick(float x, float y) {
		if(listener!=null){
			if (chart.getListenItemClickStatus()) {
				PointPosition record = chart.getPositionRecord(x, y);
				if (null == record) {
					return;
				}
				if(record.getDataID() >= chartData.size()){
					return;
				}
				SplineData lData = chartData.get(record.getDataID());
				List<PointD> linePoint = lData.getLineDataSet();
				
				int pos = record.getDataChildID();
				int i = 0;
				Iterator<PointD> it = linePoint.iterator();
				while (it.hasNext()) {
					PointD entry = it.next();
					if (pos == i) {
						listener.itemClick(entry);
						break;
					}
					i++;
				}
			} 
		}
	}
	
	public interface OnItemClickListener {
		void itemClick(PointD p);
	}
}
