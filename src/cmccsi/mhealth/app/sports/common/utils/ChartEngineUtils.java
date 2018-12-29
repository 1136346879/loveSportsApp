package cmccsi.mhealth.app.sports.common.utils;


import org.achartengine.chart.PointStyle;
import org.achartengine.renderer.BasicStroke;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint.Align;
import cmccsi.mhealth.app.sports.common.Common;
import cmccsi.mhealth.app.sports.R;

/**
 * AChartEngine 基本设置
 * @author Administrator
 *
 */
public class ChartEngineUtils {
	private Context mContext;
	public XYMultipleSeriesRenderer renderer;
	
	public ChartEngineUtils(Context context){
		mContext=context;
		renderer=new XYMultipleSeriesRenderer();
		initRenderer();
	}

	/**
	 * 初始化图标基本信息
	 */
	private void initRenderer() {
		renderer.setAxisTitleTextSize(Common.dip2px(mContext, 8));//设置轴标题文本大小 
		renderer.setChartTitleTextSize(Common.dip2px(mContext, 8));//设置图表标题文字的大小  
		renderer.setLabelsTextSize(Common.dip2px(mContext, 12));//设置标签的文字大小  
		renderer.setClickEnabled(false);
		renderer.setPointSize(5f);//设置点的大小 
		renderer.setLegendTextSize(28);
        
        XYSeriesRenderer rData = new XYSeriesRenderer();//数据(类似于一条线对象)  
        rData.setColor(Color.rgb(255, 128, 64));//设置颜色  
        rData.setPointStyle(PointStyle.POINT);//设置点的样式  
        rData.setStroke(BasicStroke.DASHED);
        
        renderer.addSeriesRenderer(rData);
        
        renderer.setZoomEnabled(false, false);//缩放
        renderer.setExternalZoomEnabled(false);//设置是否可以缩放
        renderer.setPanEnabled(false, false);//表盘移动
        renderer.setInScroll(true);

        renderer.setApplyBackgroundColor(true);// 允许自定义背景
		renderer.setBackgroundColor(mContext.getResources().getColor(
                R.color.lucensy));// 内部表格颜色
		renderer.setMargins(new int[] { Common.dip2px(mContext, 25), Common.dip2px(mContext, 25),
				Common.dip2px(mContext, 15), Common.dip2px(mContext, 5) });
		renderer.setMarginsColor(mContext.getResources().getColor(R.color.lucensy));// 表格外边颜色
		renderer.setYLabels(5);// 设置y轴显示10个点,根据setChartSettings的最大值和最小值自动计算点的间隔
		renderer.setShowGrid(true);// 是否显示网格
		renderer.setXLabelsAlign(Align.CENTER);// 刻度线与刻度标注之间的相对位置关系
		renderer.setYLabelsAlign(Align.RIGHT);// 刻度线与刻度标注之间的相对位置关系
		renderer.setZoomButtonsVisible(false);// 是否显示放大缩小按钮
		renderer.setAxesColor(Color.BLACK); // xy轴坐标线颜色
		renderer.setLabelsColor(mContext.getResources().getColor(R.color.org_IIII)); // 标签颜色
		renderer.setXLabelsColor(Color.GRAY); // x轴坐标颜色
		renderer.setYLabelsColor(0, Color.GRAY);
	}
	
	/**
	 * 设置图表XY轴信息
	 * @param title 图表标题
	 * @param yTitle y轴标题
	 * @param xTitle x轴标题
	 * @param xMax x轴最大值
	 * @param yMax Y轴最大值
	 */
	public void setChartData(String title,String yTitle
			,String xTitle,double xMax, double yMax){
		renderer.setChartTitle(title);
        renderer.setYTitle(yTitle);// 设置Y轴名称
		renderer.setXTitle(xTitle);
		
		renderer.setXAxisMin(0);
		renderer.setXAxisMax(xMax);
		renderer.setYAxisMin(0);
		renderer.setYAxisMax(yMax);
	
	}
	
	/**
	 * 设置图表XY轴信息
	 * @param title 图表标题
	 * @param yTitle y轴标题
	 * @param xTitle x轴标题
	 * @param xMax x轴最大值
	 * @param yMax Y轴最大值
	 */
	public void setChartData(String title,String yTitle
			,String xTitle, double yMax,String[] xLabels){
		setChartData(title,yTitle,xTitle,(double)xLabels.length,yMax);
		
		renderer.setXLabels(0);
		int i=0;
		for (String xlabel : xLabels) {
			 renderer.addXTextLabel(i, xlabel);
			 i++;
		}
	}
	
	
}
