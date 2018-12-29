package cmccsi.mhealth.app.sports.db;

import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;
import cmccsi.mhealth.app.sports.bean.MedalInfo;
import cmccsi.mhealth.app.sports.common.Logger;

public final class ActivityListDetailTableMetaData implements BaseColumns {
	public static final String TABLE_NAME = "activitydetails_info";

	public static final String MEDAL_NAME = "medalname";
	public static final String MEDAL_DETAIL = "medaldetail";
	public static final String MEDAL_TYPE = "medaltype";
	public static final String MEDAL_ACTIVITYID = "activityid";
	public static final String RANK = "rank";
	public static final String CLUBID = "clubid";
	public static final String MEDAL_SUM = "medalsnum";
	public static final String MEDAL_GAP = "medalgap";
	public static final String BEATPERCENT = "beatpercent";
	public static final String SCORE = "score";

	public static String CREATE_TABLE_SQL = "create table " 
			+ TABLE_NAME + "("
			+ _ID + " integer primary key autoincrement,"
			+ CLUBID + " int," 
			+ MEDAL_NAME + " text," 
			+ MEDAL_ACTIVITYID + " text," 
			+ MEDAL_DETAIL + " text,"
			+ MEDAL_TYPE + " text," + RANK + " text," + MEDAL_SUM + " text,"
			+ MEDAL_GAP + " text," + BEATPERCENT + " text," + SCORE
			+ " text"+
			")";
	public static String DELETE_TABLE_SQL = "drop table  IF EXISTS "
			+ TABLE_NAME;
	
	public static void CreateTable(DatabaseHelper dbHelper) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		db.execSQL(DELETE_TABLE_SQL);
		db.execSQL(CREATE_TABLE_SQL);
	}

	public static Cursor GetAllValueCursor(DatabaseHelper dbHelper,String activityid , int clubid) {
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		Cursor cursor = null;
		if (activityid != null) {
			Logger.d("outrange", activityid);
			cursor = db.query(TABLE_NAME, null, "activityid=? and clubid=?", new String[] { activityid , clubid+"" }, null, null, null);
		} else {
			cursor = db.query(TABLE_NAME, null, "clubid=?", new String[] { clubid+"" }, null, null, null);
		}
		return cursor;
	}

//	public static Cursor GetValueCursorById(DatabaseHelper dbHelper,
//			String activityid) {
//		SQLiteDatabase db = dbHelper.getReadableDatabase();
//		String sql = "select * from " + TABLE_NAME + " where " + ACTIVITY_ID
//				+ " = " + activityid;
//		Cursor cursor = db.rawQuery(sql, null);
//		return cursor;
//	}

	public static void InsertValue(DatabaseHelper dbHelper,List<MedalInfo> arr,int clubid) {
		ContentValues values = new ContentValues();
		if(arr == null)return;
		for (int j = 0; j < arr.size(); j++) {
			if (arr.get(j) == null) {
				continue;
			}
			values.put(MEDAL_NAME, arr.get(j).medalname);
			values.put(MEDAL_TYPE, arr.get(j).medaltype);
			values.put(MEDAL_DETAIL, arr.get(j).medaldetail);
			values.put(RANK, arr.get(j).rank);
			values.put(MEDAL_SUM, arr.get(j).medalsnum);
			values.put(MEDAL_ACTIVITYID, arr.get(j).activityid);
			values.put(MEDAL_GAP, arr.get(j).medalgap);
			values.put(BEATPERCENT, arr.get(j).beatpercent);
			values.put(SCORE, arr.get(j).score);
			values.put(CLUBID, clubid);
			SQLiteDatabase db = dbHelper.getWritableDatabase();
			db.insert(TABLE_NAME, null, values);
		}
	}

	public static void DeleteData(DatabaseHelper dbHelper) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		db.execSQL("DELETE FROM " + TABLE_NAME);
	}
	public static void DeleteData(DatabaseHelper dbHelper,int clubid) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		db.execSQL("DELETE FROM " + TABLE_NAME + " WHERE " + CLUBID + " = " + clubid);
	}
}
