package cmccsi.mhealth.app.sports.db;

import java.util.ArrayList;
import java.util.List;

import cmccsi.mhealth.app.sports.bean.DetailGPSData;
import cmccsi.mhealth.app.sports.bean.GpsInfoDetail;
import cmccsi.mhealth.app.sports.bean.ListGPSData;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class GpsInfoDetailMetaData {
	public static final String TABLE_NAME = "detail_gpsinfo";
	private static final String LATITUDE = "latitude";
	private static final String LONGITUDE = "longitude";
	private static final String START_TIME = "start_time";
	private static final String DETAIL_TIME = "detail_time";
	private static final String SPEED = "speed";
	private static final String ALTITUDE = "altitude";
	private static final String DISTANCE = "distance";
	private static final String CAL = "cal";
	private static final String ISPOINTSTOP = "ispointstop";

	public static final String CREATE_TABLE = "create table " + TABLE_NAME 
			+ " (" + LATITUDE + " int," 
			+ LONGITUDE + " int," 
			+ START_TIME + " text," 
			+ DETAIL_TIME + " text," 
			+ SPEED + " float," 
			+ DISTANCE + " double,"
			+ CAL + " float,"
			+ ISPOINTSTOP + " integer,"
			+ ALTITUDE + " double" 
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
	public static void DeleteDetailGPSData(DatabaseHelper dbHelper) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		db.execSQL("DELETE FROM " + TABLE_NAME);
	}
	
	public static void insertData(DatabaseHelper dbHelper, DetailGPSData gpsInfo,String starttime) {
		ContentValues values = new ContentValues();
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		db.beginTransaction();
		
		for (int i = 0; i < gpsInfo.datavalue.size(); i++) {
			values.put(LATITUDE, ((GpsInfoDetail)gpsInfo.datavalue.get(i)).getLatitude());
			values.put(LONGITUDE, ((GpsInfoDetail)gpsInfo.datavalue.get(i)).getLongtitude());
			values.put(START_TIME, starttime);
			values.put(DETAIL_TIME, ((GpsInfoDetail)gpsInfo.datavalue.get(i)).getDetailtime());
			values.put(SPEED, ((GpsInfoDetail)gpsInfo.datavalue.get(i)).getSpeed());
			values.put(ALTITUDE, ((GpsInfoDetail)gpsInfo.datavalue.get(i)).getAltitude());
			values.put(DISTANCE, ((GpsInfoDetail)gpsInfo.datavalue.get(i)).getDistance());
			values.put(CAL, ((GpsInfoDetail)gpsInfo.datavalue.get(i)).getCal());
			values.put(ISPOINTSTOP, ((GpsInfoDetail)gpsInfo.datavalue.get(i)).getIsStopPoint());
			db.insert(TABLE_NAME, null, values);
		}
		db.setTransactionSuccessful();
		db.endTransaction();
	}
	
	public static void insertDetail(DatabaseHelper dbHelper, GpsInfoDetail gpsInfo) {
		ContentValues values = new ContentValues();
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		db.beginTransaction();
		
			values.put(LATITUDE, gpsInfo.getLatitude());
			values.put(LONGITUDE, gpsInfo.getLongtitude());
			values.put(START_TIME, gpsInfo.getStarttime());
			values.put(DETAIL_TIME, gpsInfo.getDetailtime());
			values.put(SPEED, gpsInfo.getSpeed());
			values.put(ALTITUDE, gpsInfo.getAltitude());
			values.put(DISTANCE, gpsInfo.getDistance());
			values.put(ISPOINTSTOP, gpsInfo.getIsStopPoint());
			values.put(CAL, gpsInfo.getCal());
			db.insert(TABLE_NAME, null, values);
		db.setTransactionSuccessful();
		db.endTransaction();
	}

	public static List<GpsInfoDetail> getGpsInfoDetails(DatabaseHelper dbHelper,String starttime) {
		List<GpsInfoDetail> geoPoints = new ArrayList<GpsInfoDetail>();
		GpsInfoDetail gpsInfo;
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		Cursor cursor = null;
		try {
			cursor = getGeoPointCursor(db,starttime);
//			int count = getPointCount(dbHelper);
			int count = cursor.getCount();
			for (int i = 0; i < count; i++) {
				if (cursor.moveToNext()) {
					gpsInfo = new GpsInfoDetail();
					gpsInfo.setAltitude(cursor.getDouble(cursor.getColumnIndex(ALTITUDE)));
					gpsInfo.setDetailtime(cursor.getString(cursor.getColumnIndex(DETAIL_TIME)));
					gpsInfo.setSpeed(cursor.getFloat(cursor.getColumnIndex(SPEED)));
					gpsInfo.setLatitude(cursor.getDouble(cursor.getColumnIndex(LATITUDE)));
					gpsInfo.setLongtitude(cursor.getDouble(cursor.getColumnIndex(LONGITUDE)));
					gpsInfo.setStarttime(cursor.getString(cursor.getColumnIndex(START_TIME)));
					gpsInfo.setDistance(cursor.getDouble(cursor.getColumnIndex(DISTANCE)));
					gpsInfo.setCal(cursor.getFloat(cursor.getColumnIndex(CAL)));
					gpsInfo.setIsStopPoint(cursor.getInt(cursor.getColumnIndex(ISPOINTSTOP)));
					geoPoints.add(gpsInfo);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			if(cursor != null)cursor.close();
		}
		
		return geoPoints;
	}
	

	private static Cursor getGeoPointCursor(SQLiteDatabase db,String starttime) {
		String sql = "select * from " + TABLE_NAME + " where "+START_TIME+" = '" + starttime + "' ";
		Cursor cursor = db.rawQuery(sql, null);
		return cursor;
	}

	public static int getPointCount(DatabaseHelper dbHelper) {
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		Cursor cursor = null;
		try {
			cursor = db.rawQuery("select count(*)from " + TABLE_NAME + "", null);
			cursor.moveToFirst();
			int count = cursor.getInt(0);
			return count;
		} finally {
			if (cursor != null)
				cursor.close();
		}
	}
}
