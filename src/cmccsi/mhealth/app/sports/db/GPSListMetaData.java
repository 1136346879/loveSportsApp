package cmccsi.mhealth.app.sports.db;

import java.util.ArrayList;
import java.util.List;

import cmccsi.mhealth.app.sports.bean.GPSListInfo;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

public class GPSListMetaData implements BaseColumns{
	public static final String TABLE_NAME = "list_gpsinfo";
	
	private static final String START_TIME = "starttime";
	private static final String DURATION = "duration";
	private static final String SPEED = "speed";
	private static final String DISTANCE = "distance";
	private static final String CAL = "cal";
	private static final String STEPNUM = "stepNum";
	private static final String DURATIONPERKM = "durationperkm";
	private static final String SPEEDMAX = "speedmax";
	private static final String SPEEDMIN = "speedmin";
	private static final String CLIMBSUM = "climbsum";
	private static final String SPORTTYPE = "sporttype";
	private static final String ISUPLOAD = "isupload";

	public static final String CREATE_TABLE = "create table " + TABLE_NAME +
			" (" 
			+ _ID + " integer primary key autoincrement," 
			+ START_TIME + " text,"
			+ DURATION + " text,"
			+ SPEED + " float,"
			+ DISTANCE + " float,"
			+ CAL + " float,"
			+ STEPNUM + " text,"
			+ DURATIONPERKM + " text,"
			+ SPEEDMAX + " text,"
			+ SPEEDMIN + " text,"
			+ CLIMBSUM + " text,"
			+ ISUPLOAD + " int,"
			+ SPORTTYPE + " int"
			+")";
	
	public static String deleteSql(String starttime){
		return "DELETE FROM "+TABLE_NAME+" WHERE "+START_TIME+" = '"+starttime+"' ";
	}
	
	public static boolean deleteData(DatabaseHelper dbHelper,String sql){
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		try {
			db.execSQL(sql);
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			
		}
		return true;
	}
	
	public static void DeleteListGPSData(DatabaseHelper dbHelper) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		db.execSQL("DELETE FROM " + TABLE_NAME);
	}

	public static void AllPointInsert(DatabaseHelper dbHelper, GPSListInfo gpsInfo) {
		ContentValues values = new ContentValues();
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		db.beginTransaction();
		
//		for (int i = 0; i < arrGeoPoint.size(); i++) {
			values.put(START_TIME, gpsInfo.getStarttime());
			values.put(SPEED, gpsInfo.getSpeed());
			values.put(DISTANCE, gpsInfo.getDistance());
			values.put(CAL, gpsInfo.getCal());
			values.put(DURATION, gpsInfo.getDuration());
			values.put(STEPNUM, gpsInfo.getStepNum());
			values.put(DURATIONPERKM, gpsInfo.getDurationperkm());
			values.put(SPEEDMAX, gpsInfo.getSpeedmax());
			values.put(SPEEDMIN, gpsInfo.getSpeedmin());
			values.put(CLIMBSUM, gpsInfo.getClimbsum());
			values.put(SPORTTYPE, gpsInfo.getSporttype());
			values.put(ISUPLOAD, gpsInfo.getIsUpload());
			db.insert(TABLE_NAME, null, values);
//		}
		db.setTransactionSuccessful();
		db.endTransaction();
	}

//	public static List<String> getHistoryStartTimeArr(DatabaseHelper dbHelper) {
//		List<String> date = new ArrayList<String>();
//		String sql = "select "+START_TIME+" from " + TABLE_NAME;
//		Cursor cursor = null;
//		try {
//			SQLiteDatabase db = dbHelper.getReadableDatabase();
//			db.beginTransaction();
//			cursor = db.rawQuery(sql, null);
//			while (cursor.moveToNext()) {
//				date.add(cursor.getString(cursor.getColumnIndex(START_TIME)));
//			}
//			db.setTransactionSuccessful();
//			db.endTransaction();
//		} catch (Exception e) {
//			e.printStackTrace();
//		} finally {
//			if (cursor != null)
//				cursor.close();
//		}
//		return date;
//	}

	public static List<GPSListInfo> getListGPSData(DatabaseHelper dbHelper) {
		List<GPSListInfo> date = new ArrayList<GPSListInfo>();
		String sql = "select * from " + TABLE_NAME + " ORDER BY " + START_TIME +" DESC ";
		Cursor cursor = null;
		SQLiteDatabase db = null;
		try {
			db = dbHelper.getReadableDatabase();
			cursor = db.rawQuery(sql, null);
			while (cursor.moveToNext()) {
				GPSListInfo gpsInfo = new GPSListInfo();
				gpsInfo.setStarttime(cursor.getString(cursor.getColumnIndex(START_TIME)));
				gpsInfo.setSpeed(cursor.getFloat(cursor.getColumnIndex(SPEED)));
				gpsInfo.setDistance(cursor.getFloat(cursor.getColumnIndex(DISTANCE)));
				gpsInfo.setCal(cursor.getFloat(cursor.getColumnIndex(CAL)));
				gpsInfo.setDuration(cursor.getString(cursor.getColumnIndex(DURATION)));
				gpsInfo.setStepNum(cursor.getString(cursor.getColumnIndex(STEPNUM)));
				gpsInfo.setDurationperkm(cursor.getString(cursor.getColumnIndex(DURATIONPERKM)));
				gpsInfo.setSpeedmax(cursor.getString(cursor.getColumnIndex(SPEEDMAX)));
				gpsInfo.setSpeedmin(cursor.getString(cursor.getColumnIndex(SPEEDMIN)));
				gpsInfo.setClimbsum(cursor.getString(cursor.getColumnIndex(CLIMBSUM)));
				gpsInfo.setSporttype(cursor.getInt(cursor.getColumnIndex(SPORTTYPE)));
				gpsInfo.setIsUpload(cursor.getInt(cursor.getColumnIndex(ISUPLOAD)));
				date.add(gpsInfo);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (cursor != null)
				cursor.close();
		}
		return date;
	}
	
	public static List<GPSListInfo> getListGPSDataByIsload(DatabaseHelper dbHelper,String isload) {
		List<GPSListInfo> date = new ArrayList<GPSListInfo>();
		String sql = "select * from " + TABLE_NAME +" where "+ISUPLOAD+" = '"+isload+"'  ORDER BY " + START_TIME +" DESC ";
		Cursor cursor = null;
		SQLiteDatabase db = null;
		try {
			db = dbHelper.getReadableDatabase();
			cursor = db.rawQuery(sql, null);
			while (cursor.moveToNext()) {
				GPSListInfo gpsInfo = new GPSListInfo();
				gpsInfo.setStarttime(cursor.getString(cursor.getColumnIndex(START_TIME)));
				gpsInfo.setSpeed(cursor.getFloat(cursor.getColumnIndex(SPEED)));
				gpsInfo.setDistance(cursor.getFloat(cursor.getColumnIndex(DISTANCE)));
				gpsInfo.setCal(cursor.getFloat(cursor.getColumnIndex(CAL)));
				gpsInfo.setDuration(cursor.getString(cursor.getColumnIndex(DURATION)));
				gpsInfo.setStepNum(cursor.getString(cursor.getColumnIndex(STEPNUM)));
				gpsInfo.setDurationperkm(cursor.getString(cursor.getColumnIndex(DURATIONPERKM)));
				gpsInfo.setSpeedmax(cursor.getString(cursor.getColumnIndex(SPEEDMAX)));
				gpsInfo.setSpeedmin(cursor.getString(cursor.getColumnIndex(SPEEDMIN)));
				gpsInfo.setClimbsum(cursor.getString(cursor.getColumnIndex(CLIMBSUM)));
				gpsInfo.setSporttype(cursor.getInt(cursor.getColumnIndex(SPORTTYPE)));
				gpsInfo.setIsUpload(cursor.getInt(cursor.getColumnIndex(ISUPLOAD)));
				date.add(gpsInfo);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (cursor != null)
				cursor.close();
		}
		return date;
	}

	public static String getDataByStartTime(DatabaseHelper dbHelper,String starttime) {
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		Cursor cursor = null;
		String sql = "select "+START_TIME+" from "+TABLE_NAME+" where "+START_TIME+" = '"+starttime+"'";
		try {
			cursor = db.rawQuery(sql, null);
			if(cursor.moveToNext())
				return cursor.getString(cursor.getColumnIndex(START_TIME));
		} catch (Exception e) {
			e.printStackTrace();	
		} finally {
			if (cursor != null)
				cursor.close();
		}
		return null;
	}
	
	public static boolean updateIsLoadData(DatabaseHelper dbHelper,String isload,String starttime){
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		db.beginTransaction();
		String sql = "update "+TABLE_NAME+" set "+ISUPLOAD+" = '"+isload+"' where "+START_TIME+"= '"+starttime+"' ";
		try {
			db.execSQL(sql);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}finally{
			
		}
		db.setTransactionSuccessful();
		db.endTransaction();
		return true;
	}

//	

//	private static Cursor getGeoPointCursor(SQLiteDatabase db,String starttime) {
//		String sql = "select * from " + TABLE_NAME + " where "+START_TIME+" = '" + starttime + "' ";
//		Cursor cursor = db.rawQuery(sql, null);
//		return cursor;
//	}

//	public static int getPointCount(DatabaseHelper dbHelper) {
//		SQLiteDatabase db = dbHelper.getReadableDatabase();
//		Cursor cursor = null;
//		try {
//			cursor = db.rawQuery("select count(*)from " + TABLE_NAME + "", null);
//			cursor.moveToFirst();
//			int count = cursor.getInt(0);
//			return count;
//		} finally {
//			if (cursor != null)
//				cursor.close();
//		}
//	}
}
