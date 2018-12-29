package cmccsi.mhealth.app.sports.pedo;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import android.text.format.DateFormat;
import cmccsi.mhealth.app.sports.bean.DataDetailPedo;
import cmccsi.mhealth.app.sports.bean.PedoDetailInfo;
import cmccsi.mhealth.app.sports.bean.PedometorDataInfo;
import cmccsi.mhealth.app.sports.bean.PedometorListInfo;
import cmccsi.mhealth.app.sports.common.utils.DateFormatUtils;
import cmccsi.mhealth.app.sports.common.utils.DateFormatUtils.FormatType;
import cmccsi.mhealth.app.sports.db.DatabaseHelper;
import cmccsi.mhealth.app.sports.db.PedoDetailTableMetaData;
import cmccsi.mhealth.app.sports.db.PedometerTableMetaData;

public class PedoController {
	public static final String DATABASE_NAME = "pedometer_db";
	private static PedoController mPedoController = null;
	private static DatabaseHelper mdbHelper;
	
	private PedoController(Context context) {
		mdbHelper = new DatabaseHelper(context, DATABASE_NAME);
	}
	
	/**
	 * 获取运动数据控制器单例
	 * TODO
	 * @param context 上下文
	 * @return PedoController
	 * @author jiazhi.cao
	 * @time 上午10:31:00
	 */
	public static PedoController GetPedoController(Context context) {
		if (mPedoController == null)
			mPedoController = new PedoController(context);
		return mPedoController;
	}
	
	/**
	 * 获取对应设备最晚数据
	 * TODO
	 * @param deviceId 设备id
	 * @return
	 * @return DataPedometor
	 * @author jiazhi.cao
	 * @time 上午10:59:49
	 */
	public PedometorDataInfo getLatestPedometer(String deviceId) {
		if(TextUtils.isEmpty(deviceId))
		{
			return null;
		}
		Cursor cursor = selectLastCursor(deviceId);
		PedometorDataInfo pedoInfo = null;
		try {
			if (cursor!=null&&cursor.moveToFirst()) {
				pedoInfo = PedometerTableMetaData
						.GetPedometerDataInfoFromCursor(cursor);
			}
		} finally {
			cursor.close();
		}
		return pedoInfo;
	}
	
	public PedometorDataInfo getLatestPedometerOfAllByDay(Date searchDate) {
		Calendar cr=Calendar.getInstance();
		cr.setTime(searchDate);
		cr.add(Calendar.DAY_OF_YEAR, 1);
		String tempStart=DateFormatUtils.DateToString(searchDate, FormatType.DateWithUnderline);
		String tempend=DateFormatUtils.DateToString(cr.getTime(), FormatType.DateWithUnderline);
		Cursor cur= PedometerTableMetaData.GetAllValueCursor(mdbHelper, tempStart, tempend);
		if(cur!=null&&cur.moveToFirst())
		{
			return PedometerTableMetaData.GetPedometerDataInfoFromCursor(cur);
		}
		else
		{
			return null;
		}
	}
	
	/**
	 * 获取对应设备对应日期的数据
	 * TODO
	 * @param deviceId 设备id
	 * @param Date 查询日期
	 * @return null
	 * @return DataPedometor 
	 * @author jiazhi.cao
	 * @time 上午11:16:37
	 */
	public PedometorDataInfo getPedometerByDay(String deviceId,Date searchDate) {
		if(TextUtils.isEmpty(deviceId))
		{
			return null;
		}
		Cursor cursor=selectPedometerByDay(deviceId,searchDate);
		PedometorDataInfo pedoInfo = null;
		try {
			if (cursor!=null&&cursor.moveToNext()) {
				pedoInfo = PedometerTableMetaData.GetPedometerDataInfoFromCursor(cursor);
			}
		} finally {
			cursor.close();
		}
		return pedoInfo;
	}
	
	/**
	 * 获取某设备一段时间运动简要数据
	 * TODO
	 * @param deviceId 设备id
	 * @param startDay 开始日期 
	 * @param endDay 结束日期 
	 * @return
	 * @return ArrayList<DataPedometor>
	 * @author jiazhi.cao
	 * @time 上午10:44:56
	 */
	public ArrayList<PedometorDataInfo> getPeriodPedometer(String deviceId,Date startDay,Date endDay)
	{
		Cursor cursor = selectPeriodPedometer(deviceId, startDay, endDay);
		ArrayList<PedometorDataInfo> pedoList = new ArrayList<PedometorDataInfo>();
		while (cursor!=null&&cursor.moveToNext()) {
			PedometorDataInfo tempdata=PedometerTableMetaData.GetPedometerDataInfoFromCursor(cursor);
			pedoList.add(tempdata);
		}
		return pedoList;
	}
	
	/**
	 * 获取某设备一段时间运动简要数据，多设备取最后一条
	 * TODO
	 * @param deviceId 设备id
	 * @param startDay 开始日期 
	 * @param endDay 结束日期 
	 * @return
	 * @return ArrayList<DataPedometor>
	 * @author jiazhi.cao
	 * @time 上午10:44:56
	 */
	public ArrayList<PedometorDataInfo> getAllPeriodPedometer(Date startDay)
	{
		if(startDay==null)
		{
			return null;
		}
		String tempStart=DateFormatUtils.DateToString(startDay, FormatType.DateWithUnderline);
		SQLiteDatabase db = mdbHelper.getWritableDatabase();
		Cursor cursor = db.rawQuery("select * from "+PedometerTableMetaData.TABLE_NAME
				+" where "+PedometerTableMetaData.DATE+" >'"+tempStart
				+"' group by substr("+PedometerTableMetaData.DATE+",1,10) order by "+PedometerTableMetaData.DATE
				+" desc",null);
		ArrayList<PedometorDataInfo> pedoList = new ArrayList<PedometorDataInfo>();
		while (cursor!=null&&cursor.moveToNext()) {
			PedometorDataInfo tempdata=PedometerTableMetaData.GetPedometerDataInfoFromCursor(cursor);
			pedoList.add(tempdata);
		}
		return pedoList;
	}
	
	/**
	 * 获取某设备一段时间运动简要数据
	 * TODO
	 * @param deviceId 设备id
	 * @param startDay 开始日期 
	 * @param diff 日期间隔
	 * @return
	 * @return ArrayList<DataPedometor>
	 * @author jiazhi.cao
	 * @time 上午10:55:47
	 */
	public ArrayList<PedometorDataInfo> getPeriodPedometer(String deviceId,Date startDay,int diff)
	{
		if(TextUtils.isEmpty(deviceId))
		{
			return null;
		}
		Calendar cr=Calendar.getInstance();
		cr.setTime(startDay);
		if(diff==0)
		{
			cr.add(Calendar.DAY_OF_YEAR, 1);
		}
		else
		{
			cr.add(Calendar.DAY_OF_YEAR, diff);
		}
		Date endDay=cr.getTime();
		if(diff<0)
		{
			return getPeriodPedometer(deviceId,endDay,startDay);
		}
		else
		{
			return getPeriodPedometer(deviceId,startDay,endDay);
		}
	}
	
	/**
	 * 插入或更新运动数据
	 * TODO
	 * @param data 运动数据
	 * @param checkStep 是否比较步数大小  网络数据大于本地数据更新
	 * @return void
	 * @author jiazhi.cao
	 * @time 上午10:06:47
	 */
	public void insertOrUpdatePedometer(PedometorDataInfo data,boolean checkStep)
	{
		if(data==null){
			return;
		}
		SQLiteDatabase db = mdbHelper.getWritableDatabase();
		Cursor cur =selectPedometerByDay(data.deviceId,DateFormatUtils.StringToDate(data.createtime, FormatType.DateLong));
		try
		{
			if(cur!=null&&cur.moveToFirst())//存在数据更新
			{
				Date pedodate= DateFormatUtils.StringToDate(data.createtime, FormatType.DateLong);
				if(checkStep&&DateFormatUtils.isToday(pedodate))
				{
					//网络数据步数比本地数据步数大则更新
					int webstep=Integer.parseInt(data.stepNum);
					int localstep=Integer.parseInt(cur.getString(cur.getColumnIndex(PedometerTableMetaData.STEP_NUM)));
					if(webstep>localstep)
					{
					long id=	cur.getLong(cur.getColumnIndex("_id"));
					PedometerTableMetaData.UpdateValue(db,id,data.createtime, data.power, data.weight, data.step, data.cal, data.stepNum, data.distance,
							data.strength1, data.strength2, data.strength3, data.strength4, data.transType , data.yxbssum,data.deviceId,data.deviceType);
					}
				}
				else
				{
					long id=	cur.getLong(cur.getColumnIndex("_id"));
					PedometerTableMetaData.UpdateValue(db,id,data.createtime, data.power, data.weight, data.step, data.cal, data.stepNum, data.distance,
							data.strength1, data.strength2, data.strength3, data.strength4, data.transType , data.yxbssum,data.deviceId,data.deviceType);
				}
			}
			else//不存在数据插入
			{
				PedometerTableMetaData.InsertValue(db, data.createtime, data.power, data.weight, data.step, data.cal, data.stepNum, data.distance,
						data.strength1, data.strength2, data.strength3, data.strength4, data.transType , data.yxbssum,data.deviceId,data.deviceType);
			}
		}
		
		catch(NumberFormatException e)
		{
			e.printStackTrace();
		}
		finally{
			if(cur!=null){
				cur.close();
			}
		}
	}
	
	/**
	 * 插入或更新运动数据
	 * TODO
	 * @param data
	 * @return void
	 * @author jiazhi.cao
	 * @time 上午10:15:43
	 */
	public void insertOrUpdatePedometer(PedometorListInfo data)
	{
		if(data==null||data.datavalue==null)
		{
			return;
		}
		for (PedometorDataInfo tempPedo : data.datavalue) {
			insertOrUpdatePedometer(tempPedo,true);
		}
	}
	
	/**
	 * 插入或更新详细包
	 * TODO
	 * @param deviceId
	 * @param pedoDetailInfo
	 * @return void
	 * @author jiazhi.cao
	 * @time 下午4:43:04
	 */
	public void insertOrUpdatePedoDetail(String deviceId,PedoDetailInfo pedoDetailInfo)
	{
		for (DataDetailPedo detail : pedoDetailInfo.datavalue) {
			if(detail!=null)
			{
				Cursor cursor=SelectPedoDetailByHour(deviceId,pedoDetailInfo.date,detail.start_time);
				if(cursor!=null&&cursor.moveToFirst())//存在数据更新
				{
					//从数据库里取出来的步数
					String old_step = null;
					String old_CAL = null;
					String old_level2 = null;
					String old_level3 = null;
					String final_step = "";
					String final_CAL="";
					String final_level2="";
					String final_level3="";
					
					old_step = cursor
							.getString(cursor
									.getColumnIndex(PedoDetailTableMetaData.STEP_NUM_PER_FIVE));
					old_CAL = cursor
							.getString(cursor
									.getColumnIndex(PedoDetailTableMetaData.CAL_PER_FIVE));
					old_level2 = cursor
							.getString(cursor
									.getColumnIndex(PedoDetailTableMetaData.STRENGTH_TWO_PER_FIVE));
					old_level3=cursor
							.getString(cursor
									.getColumnIndex(PedoDetailTableMetaData.STRENGTH_THREE_PER_FIVE));
					
					final_step=Cumulative(old_step,detail.snp5);
					final_CAL=Cumulative(old_CAL,detail.knp5);
					final_level2=Cumulative_minute(old_level2,detail.level2p5);
					final_level3=Cumulative_minute(old_level3,detail.level3p5);
					//如果数据库有数据就修改
					PedoDetailTableMetaData.UpdateValue(mdbHelper.getWritableDatabase(), cursor.getLong(cursor.getColumnIndex("_id"))
							, pedoDetailInfo.date,detail.start_time, final_step, final_CAL,
							final_level2, final_level3,
							detail.level4p5, detail.yuanp5, detail.snyxp5,deviceId);
					cursor.close();
				}
				else
				{
					PedoDetailTableMetaData.InsertValue(mdbHelper.getWritableDatabase(), "", pedoDetailInfo.date,
							detail.start_time, detail.snp5, detail.knp5,
							detail.level2p5, detail.level3p5,
							detail.level4p5, detail.yuanp5, detail.snyxp5,deviceId);
				}
			}
		}
	}
	
	/**]
	 * 查询详细包
	 * TODO
	 * @param deviceId 设备ID
	 * @param searchDate 日期
	 * @param hour 小时
	 * @return
	 * @return Cursor
	 * @author jiazhi.cao
	 * @time 下午3:47:56
	 */
	private Cursor SelectPedoDetailByHour(String deviceId,String searchDate,String hour)
	{
		Cursor result=PedoDetailTableMetaData.GetValueCursor(mdbHelper.getWritableDatabase(), deviceId,searchDate, hour );
		return result;
	}
	
	/**
	 * 当前日期的一段时间数据
	 * @param search_date 
	 * @param starttime
	 * @return
	 */
	public PedoDetailInfo getPedoDetailByDay(Date search_date,String deviceId,int starttime,int endtime) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
		
		Cursor cursor = PedoDetailTableMetaData.GetValueCursor(mdbHelper.getWritableDatabase(),deviceId,sdf.format(search_date)
				,String.valueOf(starttime),String.valueOf(endtime));
		PedoDetailInfo pedoInfo = new PedoDetailInfo();
		pedoInfo.datavalue = new ArrayList<DataDetailPedo>();
		try {
			while (cursor.moveToNext()) {
				DataDetailPedo dataDetail = new DataDetailPedo();

				dataDetail.start_time = cursor.getString(cursor.getColumnIndex(PedoDetailTableMetaData.START_TIME));
				dataDetail.knp5 = cursor.getString(cursor.getColumnIndex(PedoDetailTableMetaData.CAL_PER_FIVE));
				dataDetail.snp5 = cursor.getString(cursor.getColumnIndex(PedoDetailTableMetaData.STEP_NUM_PER_FIVE));
				dataDetail.level2p5 = cursor.getString(cursor.getColumnIndex(PedoDetailTableMetaData.STRENGTH_TWO_PER_FIVE));
				dataDetail.level3p5 = cursor.getString(cursor.getColumnIndex(PedoDetailTableMetaData.STRENGTH_THREE_PER_FIVE));
				dataDetail.level4p5 = cursor.getString(cursor.getColumnIndex(PedoDetailTableMetaData.STRENGTH_FOUR_PER_FIVE));
				dataDetail.yuanp5 = cursor.getString(cursor.getColumnIndex(PedoDetailTableMetaData.ACC_PER_FIVE));
				dataDetail.snyxp5 = cursor.getString(cursor.getColumnIndex(PedoDetailTableMetaData.EFF_STEPNUM_PER_FIVE));
				pedoInfo.datavalue.add(dataDetail);
			}
		} finally {
			cursor.close();
		}

		pedoInfo.date = sdf.format(search_date);
		return pedoInfo;
	}
	
	/**
	 * 刪除給定日期之前的数据
	 * TODO
	 * @param date 日期 
	 * @return void
	 * @author jiazhi.cao
	 * @time 下午3:56:40
	 */
	public void deletePedoBeforeDay(Date date)
	{
		try
		{
			DateFormatUtils.DateToString(date, FormatType.DateWithUnderline);
			String sql="DELETE FROM "+PedometerTableMetaData.TABLE_NAME+" WHERE "+PedometerTableMetaData.DATE+" <'"+date+"'";
			SQLiteDatabase db = mdbHelper.getWritableDatabase();
			db.execSQL(sql);
		}catch(SQLException e)
		{
			e.printStackTrace();
		}
	}
	
	/**
	 * 获取运动表最后一条数据
	 * TODO
	 * @param deviceId 设备ID
	 * @return
	 * @return Cursor
	 * @author jiazhi.cao
	 * @time 上午10:55:31
	 */
	private Cursor selectLastCursor(String deviceId)
	{
		if(TextUtils.isEmpty(deviceId))
		{
			return null;
		}
		SQLiteDatabase db = mdbHelper.getWritableDatabase();
		Cursor result= db.rawQuery("select * from "+PedometerTableMetaData.TABLE_NAME
				+" where "+PedometerTableMetaData.RES2+" = '"+deviceId+"' order by _id desc limit 0,1",null);
		return result;
	}

	/**
	 * 查询一段时间的数据
	 * TODO
	 * @param deviceId 设备Id
	 * @param startday 开始日期 格式"yyyy-mm-dd"
	 * @param endday 结束日期 格式"yyyy-mm-dd"
	 * @return
	 * @return Cursor
	 * @author jiazhi.cao
	 * @time 上午11:08:39
	 */
	private Cursor selectPeriodPedometer(String deviceId, Date startday,Date endday)
	{
		if(TextUtils.isEmpty(deviceId)||startday==null||endday==null)
		{
			return null;
		}
		String tempStart=DateFormatUtils.DateToString(startday, FormatType.DateWithUnderline);
		String tempend=DateFormatUtils.DateToString(endday, FormatType.DateWithUnderline);
		return PedometerTableMetaData.GetAllValueCursor(mdbHelper, tempStart, tempend,deviceId);
	}
	
	/**
	 * 查询某设备某天运动数据
	 * TODO
	 * @param deviceId 设备ID
	 * @param searchDate 
	 * @return
	 * @return Cursor
	 * @author jiazhi.cao
	 * @time 上午9:52:43
	 */
	private Cursor selectPedometerByDay(String deviceId, Date searchDate)
	{
		if(TextUtils.isEmpty(deviceId)||searchDate==null)
		{
			return null;
		}
		Calendar cr=Calendar.getInstance();
		cr.setTime(searchDate);
		cr.add(Calendar.DAY_OF_YEAR, 1);
		Cursor cursor = selectPeriodPedometer(deviceId, searchDate, cr.getTime());
		return cursor;
	}
	
	private String Cumulative_minute(String old_data, String new_data) {
		// TODO Auto-generated method stub
		String[] old_datas = null;
		String[] new_datas=null;
		String final_data="";
		old_datas = old_data.split(",");
		new_datas=new_data.split(",");
		for (int x = 0; x < old_datas.length; x++) {
			int old_temp = Integer.parseInt(old_datas[x]);
			int new_temp = Integer.parseInt(new_datas[x]);
			//累加，here
			final_data = final_data
					+ Integer.toString((old_temp + new_temp)*60);
			if (x != old_datas.length - 1) {
				final_data = final_data + ",";
			}
		}
		return final_data;
	}
	
	private String Cumulative(String old_data, String new_data) {
		// TODO Auto-generated method stub
		String[] old_datas = null;
		String[] new_datas=null;
		String final_data="";
		old_datas = old_data.split(",");
		new_datas=new_data.split(",");
		for (int x = 0; x < old_datas.length; x++) {
			int old_temp = Integer.parseInt(old_datas[x]);
			int new_temp = Integer.parseInt(new_datas[x]);
			//累加，here
			final_data = final_data
					+ Integer.toString(old_temp + new_temp);
			if (x != old_datas.length - 1) {
				final_data = final_data + ",";
			}
		}
		return final_data;
	}
}
