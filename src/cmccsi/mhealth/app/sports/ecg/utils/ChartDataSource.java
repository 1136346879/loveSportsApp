package cmccsi.mhealth.app.sports.ecg.utils;

import java.util.List;
import java.util.Map;

import org.xclcharts.chart.PointD;

public class ChartDataSource {
	private double maxX;
	private double maxY;
	private List<PointD> points;
	private Map<Double,String> dateMap;
	private List<String> lables;
	private List<String> dates;
	
	
	
	public List<String> getDateList() {
		return dates;
	}
	public double getMaxX() {
		return maxX;
	}
	public void setMaxX(double maxX) {
		this.maxX = maxX;
	}
	public double getMaxY() {
		return maxY;
	}
	public void setMaxY(double maxY) {
		this.maxY = maxY;
	}
	public List<PointD> getPoints() {
		return points;
	}
	public void setPoints(List<PointD> points) {
		this.points = points;
	}
	public Map<Double, String> getDateMap() {
		return dateMap;
	}
	public void setDateMap(Map<Double, String> dateMap) {
		this.dateMap = dateMap;
	}
	
	public void setDateList(List<String> dates) {
		this.dates = dates;
	}
	public List<String> getLables() {
		return lables;
	}
	public void setLables(List<String> lables) {
		this.lables = lables;
	}
}
