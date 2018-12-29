package cmcc.mhealth.ble;

import java.util.Calendar;
import java.util.Date;

public class BaseSleepSummary {
	
	public int[] result;			//每5分钟一个结果，1:熟睡，2:翻身，3:醒来, 0:非睡眠状态,不显示 
	public int resultAvaileLength;
	public int startsleepHour;
	public int startsleepMin;
	public int fallasleepHour;
	public int fallasleepMin;
	public int endsleepHour;
	public int endsleepMin;
		
	public int sleepResult;			//3：很好。2：还好。1:一般。0：较差
	public int sleepDuration;		//睡眠持续时间，分钟为单位
	public int sleepEfficiency;		//乘以1000
	public int fallasleepDuration;	//入睡持续时间。分钟为单位
	public int awakeTimes;			//夜醒次数

	Date startSleep;
	Date endSleep;
	Date fallAsleep;
	
	public BaseSleepSummary(){
		result = new int[12*24];
		resultAvaileLength = 0;
		startsleepHour = 0;
		startsleepMin = 0;
		fallasleepHour = 0;
		fallasleepMin = 0;
		endsleepHour = 0;
		endsleepMin = 0;
			
		sleepResult = 0;
		sleepDuration = 0;
		sleepEfficiency = 0;
		fallasleepDuration = 0;
		awakeTimes = 0;
	}
	
	private int TimeDuration(int endHour,int endMin, int startHour,int startMin){ 
		int timeduration=0;
		//没收到开始或结束时间的异常处理
		if((startHour==0&&startMin==0)||(endHour==0&&endMin==0)){
			timeduration=0;
		}else{
			if(endHour>startHour){
				timeduration= (endHour-startHour)*60+endMin-startMin;
			}else if(endHour<startHour){
				timeduration= (endHour+24-startHour)*60+endMin-startMin;
			}else if((endHour==startHour) && (endMin>startMin) ){
				timeduration= endMin-startMin;
			}else{
				timeduration=0;
			}
		}
		return timeduration;
	}
	private int getSleepEfficiency(int DeepSleepDuration, int sleepDuration){
		if(sleepDuration == 0)
			return 0;
		else
			return DeepSleepDuration * 1000 / sleepDuration;
	}
	private int getSleepResult(){
		if(fallasleepDuration<=30 && sleepDuration>=7*60 && sleepDuration<=9*60 && awakeTimes<=5){
			sleepResult=3;
		}else if((fallasleepDuration<=30 && sleepDuration>=7*60 && sleepDuration<=9*60 && awakeTimes>5)||
				  (fallasleepDuration>30 && sleepDuration>=7*60 && sleepDuration<=9*60 && awakeTimes<=5)||
				  (fallasleepDuration<=30 && (sleepDuration<7*60 || sleepDuration>9*60) && awakeTimes<=5)){
			sleepResult=2;
		}else if((fallasleepDuration>30 && sleepDuration>=7*60 && sleepDuration<=9*60 && awakeTimes>5)||
				 (fallasleepDuration>30 && (sleepDuration<7*60 || sleepDuration>9*60) && awakeTimes<=5)||
				 (fallasleepDuration<=30 && (sleepDuration<7*60 || sleepDuration>9*60) && awakeTimes>5)){
			sleepResult=1;
		}else{
			sleepResult=0;
		}
		return sleepResult;
	}
	
	public void sleepDownSample(Date[] tsArr, int[] phArr){
		boolean fallasleepUpdate = false;
		
		for(int i = 0; i < tsArr.length; i++){
			if(startSleep.before(tsArr[i]) && endSleep.after(tsArr[i])){
				if(phArr[i] == 1)		// restless
					result[resultAvaileLength] = 3;
				else if(phArr[i] == 3)	// deep sleep
					result[resultAvaileLength] = 1;
				else if(phArr[i] == 2)	// light sleep
					result[resultAvaileLength] = 2;
				
				resultAvaileLength++;
				
				/* calculate the tiem when user fall in asleep */
				if(i == 0 && !fallasleepUpdate){
					/* immediately deep sleep, use fallasleep time = sleep start time */
					if(phArr[i] == 3) fallasleepUpdate = true;
				}else if(!fallasleepUpdate){
					if(phArr[i] == 3){
						fallAsleep = tsArr[i];
						fallasleepUpdate = true;
					}
				}
			}
		}
		if(!fallasleepUpdate){
			fallAsleep = endSleep;
		}
	}
	
	public void sleepResult(Date startSleepTime, Date endSleepTime, int totalSleep, int awake){
		Calendar calendar = Calendar.getInstance();
		if(startSleepTime != null){
			calendar.setTime(startSleepTime);		
			startsleepHour = calendar.get(Calendar.HOUR_OF_DAY);
			startsleepMin = calendar.get(Calendar.MINUTE);
		}
		if(endSleepTime != null){
			calendar.setTime(endSleepTime);
			endsleepHour = calendar.get(Calendar.HOUR_OF_DAY);
			endsleepMin = calendar.get(Calendar.MINUTE);
		}
		startSleep = startSleepTime;
		endSleep = endSleepTime;
		fallAsleep = startSleepTime;
		
		sleepDuration = TimeDuration(endsleepHour, endsleepMin, startsleepHour, startsleepMin);
		sleepEfficiency = getSleepEfficiency(totalSleep, sleepDuration);
		fallasleepDuration = 0;
		awakeTimes = awake;
	}
	public BaseSleepSummary getSleepSession(){
		BaseSleepSummary sleepSummary = new BaseSleepSummary();
		
		if(this.resultAvaileLength == 0){
			fallAsleep = endSleep;
			sleepResult = 0;
		}
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(fallAsleep);		
		sleepSummary.fallasleepHour = calendar.get(Calendar.HOUR_OF_DAY);
		sleepSummary.fallasleepMin = calendar.get(Calendar.MINUTE);

		long diff = ((fallAsleep.getTime() - startSleep.getTime()) / 1000);  // second
		fallasleepDuration = (int)(diff / 60);

		sleepSummary.startsleepHour = this.startsleepHour;
		sleepSummary.startsleepMin = this.startsleepMin;
		sleepSummary.endsleepHour = this.endsleepHour;
		sleepSummary.endsleepMin = this.endsleepMin;
		
		sleepSummary.result = this.result;
		sleepSummary.resultAvaileLength = this.resultAvaileLength;
		
//		sleepSummary.sleepResult = this.sleepResult;
		if(startSleep == null && endSleep == null)
			sleepSummary.sleepResult = 0;
		else
			sleepSummary.sleepResult = getSleepResult();
		sleepSummary.sleepDuration = this.sleepDuration;
		sleepSummary.sleepEfficiency = this.sleepEfficiency;
		sleepSummary.fallasleepDuration = this.fallasleepDuration;
		sleepSummary.awakeTimes = this.awakeTimes;
		
		return sleepSummary;
		
	}
}
