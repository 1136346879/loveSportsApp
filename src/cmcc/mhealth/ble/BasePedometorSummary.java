package cmcc.mhealth.ble;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import android.util.Log;

public class BasePedometorSummary {
	List<BasePedometorSummary> pedListM = new ArrayList<BasePedometorSummary>();
	List<BasePedometorSummary> pedListH = new ArrayList<BasePedometorSummary>();
	List<BasePedometorSummary> pedListD = new ArrayList<BasePedometorSummary>();
	List<PedometorSubData> subList = new ArrayList<PedometorSubData>();
	
	// BasePedometorSummary
	public String power;		//电源电量
	public String cal; 			//卡路里消耗	 (单位:卡) Active Carlories
	public String stepNum; 		//当日累计步数(单位:步)	// step_num
	public String distance;		//当日累计距离(单位:米)
	public String strength1;	//*分级运动时间(单位:秒)-轻微-1级别
	public String strength2;  	//*分级运动时间(单位:秒)-中等强度-2级
	public String strength3;	//*分级运动时间(单位:秒)-高强度-3级
	public String strength4;	//*分级运动时间(单位:秒)-超强度-4级
	public String transType; 	//*同步类型:	1:自动 2:手动
	public String yxbssum;		//*效能(有氧，或有效)运动步数(单位:步)	
	public String yxbs;			//*效能(有氧，或有效)运动时长(有氧，或有效)
	public String date;			//*开始时间
	public String typeScale;	//合计粒度(单位:秒)[86400 日，3600 小时， 最小粒度是x min时，使用60*x]
	
	private static String startSyncTime;
	private static int hour;
	private static int day;
	Calendar calendar = GregorianCalendar.getInstance();

	public BasePedometorSummary(){
		power = "60";
		cal = "0.0"; 
		stepNum = "0"; 
		distance = "0";	
		strength1 = "0";	//walking total time
		strength2 = "0";  	//running total time
		strength3 = "0";	
		strength4 = "0";	
		transType = "1"; 
		yxbssum = "0";
		yxbs = "0";
		date = "2012-11-21 20:00:00";
		typeScale = "86400";
	}
	public void initBasePedometorSummary(){
		startSyncTime = null;
		hour = 0;
		day = 0;
	}
	
	public void setBasePedometorSummary(int powerLevel, int actCalories, int step, int distance, int transType, Date startTime, int walkTime, int runTime){
		BasePedometorSummary pedData = new BasePedometorSummary();
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		
		if(startSyncTime == null) startSyncTime = df.format(startTime);
		// Minutes Package
		pedData.power = Integer.toString(powerLevel);
		pedData.cal = Integer.toString(actCalories);
		pedData.stepNum = Integer.toString(step);
		pedData.distance = Integer.toString(distance);				
		pedData.transType = Integer.toString(transType);	
		pedData.date = df.format(startTime);
		pedData.typeScale = Integer.toString(60);		
		pedData.strength1 = Integer.toString(walkTime);
		pedData.strength2 = Integer.toString(runTime);
		pedListM.add(pedData);	
		
		// Hourly Pacakge
		calendar.setTime(startTime);
		if(pedListH.size() == 0 || hour !=  calendar.get(calendar.HOUR_OF_DAY)){
			BasePedometorSummary pedDataH = new BasePedometorSummary();
			hour = calendar.get(calendar.HOUR_OF_DAY);		
			pedDataH.power = Integer.toString(powerLevel);
			pedDataH.cal = Integer.toString(actCalories);
			pedDataH.stepNum = Integer.toString(step);
			pedDataH.distance = Integer.toString(distance);				
			pedDataH.transType = Integer.toString(transType);	
			pedDataH.date = df.format(startTime);
			pedDataH.typeScale = Integer.toString(60);		
			pedDataH.strength1 = Integer.toString(walkTime);
			pedDataH.strength2 = Integer.toString(runTime);
			pedListH.add(pedDataH);
		}else{
			BasePedometorSummary tempPedData = pedListH.get(pedListH.size()-1);
			tempPedData.cal = Integer.toString(Integer.parseInt(tempPedData.cal)+actCalories);
			tempPedData.stepNum = Integer.toString(Integer.parseInt(tempPedData.stepNum)+step);
			tempPedData.distance = Integer.toString(Integer.parseInt(tempPedData.distance)+distance);
			tempPedData.strength1 = Integer.toString(Integer.parseInt(tempPedData.strength1)+walkTime);
			tempPedData.strength2 = Integer.toString(Integer.parseInt(tempPedData.strength2)+runTime);
			tempPedData.typeScale = Integer.toString(3600);
			pedListH.set(pedListH.size()-1, tempPedData);
		}		
	}
	public void setDailyPedometorSummary(int powerLevel, int actCalories, int step, int distance, int transType, Date startTime, int walkTime, int runTime){

		calendar.setTime(startTime);
		if(pedListD.size() == 0 || day != calendar.get(calendar.DATE)){
			BasePedometorSummary pedData = new BasePedometorSummary();
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			day = calendar.get(calendar.DATE);
		
			pedData.power = Integer.toString(powerLevel);
			pedData.cal = Integer.toString(actCalories);
			pedData.stepNum = Integer.toString(step);
			pedData.distance = Integer.toString(distance);	
			pedData.strength1 = Integer.toString(walkTime);
			pedData.strength2 = Integer.toString(runTime);
			pedData.transType = Integer.toString(transType);	
			pedData.date = df.format(startTime);
			pedData.typeScale = Integer.toString(86400);
			pedListD.add(pedData);
		}else{
			BasePedometorSummary tempPedData = pedListD.get(pedListD.size()-1);
			tempPedData.cal = Integer.toString(actCalories);
			tempPedData.stepNum = Integer.toString(step);
			tempPedData.distance = Integer.toString(distance);
			tempPedData.strength1 = Integer.toString(Integer.parseInt(tempPedData.strength1)+walkTime);
			tempPedData.strength2 = Integer.toString(Integer.parseInt(tempPedData.strength2)+runTime);
			pedListD.set(pedListD.size()-1, tempPedData);
		}
	}
	
	public class PedometorSubData{
		public String createTime;
		public List<BasePedometorSummary>data;
		public String flagtypeScale;
	}
	public void setPedometorSubData(int actCalories, int step, int distance, int walkTime, int runTime){
		PedometorSubData subDataM = new PedometorSubData();
		PedometorSubData subDataH = new PedometorSubData();
		PedometorSubData subDataD = new PedometorSubData();
	
		if(pedListD.size() > 0){
			BasePedometorSummary tempPedData = pedListD.get(pedListD.size()-1);
			tempPedData.cal = Integer.toString(actCalories);
			tempPedData.stepNum = Integer.toString(step);
			tempPedData.distance = Integer.toString(distance);
			tempPedData.strength1 = Integer.toString(walkTime);
			tempPedData.strength2 = Integer.toString(runTime);
			pedListD.set(pedListD.size()-1, tempPedData);
		}
		subDataM.createTime = startSyncTime;
		subDataM.data = pedListM;
		subDataM.flagtypeScale = "M";
		subList.add(subDataM);
		
		subDataH.createTime = startSyncTime;
		subDataH.data = pedListH;
		subDataH.flagtypeScale = "H";
		subList.add(subDataH);
		
		subDataD.createTime = startSyncTime;
		subDataD.data = pedListD;
		subDataD.flagtypeScale = "D";
		subList.add(subDataD);
	}
	public class PedometorSession{
		public static final String TAG = "PedometorSession";
		public List<PedometorSubData> pedSession;
		public String date;
		public String deviceID;
	}
	public PedometorSession getPedometorSession(String mfgID){
		PedometorSession sessionData = new PedometorSession();
				
		sessionData.pedSession = subList;
		sessionData.date = startSyncTime;
		sessionData.deviceID = mfgID;
		if(subList.size() > 0){
			return sessionData;
		}else{
			return null;
		}
	}
}
