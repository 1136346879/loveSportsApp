package cmccsi.mhealth.app.sports.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

public final class ActivityMyDetailTableMetaData implements BaseColumns {
	public static final String TABLE_NAME = "activity_mydetails_info";

	public static final String MYNAME = "myname";
	public static final String MYGROUP = "mygroup";
	public static final String AVGSTEP = "avgstep";
	public static final String RATESCORE = "ratescore";
	public static final String HITDURATION = "hitduration";
	public static final String GROUPAVGSTEP = "groupavgstep";
	public static final String GROUP_RATESCORE = "groupratescore";
	public static final String ACTIVITY_ID = "activityId";
	public static final String CLUBID = "clubid";
	public static final String MYUSERRANK = "myuserrank";
	public static final String MYGROUPRANK = "mygrouprank";

	//接口改了，加了两个列
	public static String CREATE_TABLE_SQL = "create table " + TABLE_NAME + "("
			+ _ID + " integer primary key autoincrement," + MYNAME + " text,"
			+ MYGROUP + " text,"
			+ MYUSERRANK + " text,"
			+ MYGROUPRANK + " text,"
			+ CLUBID + " int,"
			+ AVGSTEP + " text," + ACTIVITY_ID + " text,"
			+ RATESCORE + " text," + HITDURATION + " text," + GROUPAVGSTEP
			+ " text," + GROUP_RATESCORE + " text"+
			")";
	public static String DELETE_TABLE_SQL = "drop table  IF EXISTS "
			+ TABLE_NAME;

	public static void CreateTable(DatabaseHelper dbHelper) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		db.execSQL(DELETE_TABLE_SQL);
		db.execSQL(CREATE_TABLE_SQL);
	}

	public static Cursor GetAllValueCursor(DatabaseHelper dbHelper, int clubid) {
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		Cursor cursor = db.query(TABLE_NAME, null, " clubid = ? ", new String[] { clubid + "" }, null, null, null);
		return cursor;
	}

	public static Cursor GetValueCursorById(DatabaseHelper dbHelper,String activityid, int clubid) {
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		String sql = "select * from " + TABLE_NAME + " where " + ACTIVITY_ID
				+ " = " + activityid + " and " + CLUBID + " = " + clubid;
		Cursor cursor = db.rawQuery(sql, null);
		return cursor;
	}

	public static void InsertValue(DatabaseHelper dbHelper, String myname,
			String mygroup, String avgstep, String ratescore,
			String activityid, String hitduration, String groupavgstep,
			String groupratescore,int clubid, String myuserrank, String mygrouprank) {
		ContentValues values = new ContentValues();
		
		values.put(MYNAME, myname);
		values.put(MYGROUP, mygroup);
		values.put(AVGSTEP, avgstep);
		values.put(ACTIVITY_ID, activityid);
		values.put(RATESCORE, ratescore);
		values.put(HITDURATION, hitduration);
		values.put(GROUPAVGSTEP, groupavgstep);
		values.put(GROUP_RATESCORE, groupratescore);
		values.put(CLUBID, clubid);
		//接口改了，加了两个列，不知道以后会不会用到；
		values.put(MYUSERRANK, myuserrank);
		values.put(MYGROUPRANK, mygrouprank);

		SQLiteDatabase db = dbHelper.getWritableDatabase();
		db.insert(TABLE_NAME, null, values);
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
