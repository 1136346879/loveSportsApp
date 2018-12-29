package cmccsi.mhealth.app.sports.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

public final class ListActivityTableMetaData implements BaseColumns {
	public static final String TABLE_NAME = "activity_info";

	public static final String ACTIVITYID = "activityid";
	public static final String ACTIVITYNAME = "activityname";
	public static final String ACTIVITYTYPE = "activitytype";
	public static final String ACTIVITYSLOGAN = "activityslogan";
	public static final String ACTIVITYSTART = "activitystart";
	public static final String ACTIVITYEND = "activityend";
	public static final String COMPANYNAME = "companyname";
	public static final String CLUBID = "clubid";
	public static final String AIMSTEP = "aimstep";
	public static final String PERSONNUM = "personnum";
	public static final String PERSONSEQ = "personseq";
	public static final String GROUPNUM = "groupnum";
	public static final String GROUPSEQ = "groupseq";
	// public static final String ACTIVITY_NOW_NUM = "activitynownum";
	// public static final String ACTIVITY_OLD_NUM = "activityoldnum";
	// public static final String ACTIVITY_FUTURE_NUM = "groupseq";

	public static String CREATE_TABLE_SQL = "create table " + TABLE_NAME
			+ "(" + _ID + " integer primary key autoincrement,"
			+ ACTIVITYID + " text," + ACTIVITYTYPE + " text," + ACTIVITYNAME + " text,"
			+ ACTIVITYSLOGAN + " text,"
			+ CLUBID + " int,"
			+ ACTIVITYSTART + " text,"
			+ ACTIVITYEND + " text," + COMPANYNAME + " text," + AIMSTEP
			+ " text," + PERSONNUM + " text," + PERSONSEQ + " text,"
			+ GROUPNUM + " text," + GROUPSEQ + " text" + ")";

	public static String DELETE_TABLE_SQL = "drop table  IF EXISTS "
			+ TABLE_NAME;
	

	public static void CreateTable(DatabaseHelper dbHelper) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		db.execSQL(CREATE_TABLE_SQL);
	}
	
	public static Cursor GetAllValueCursor(DatabaseHelper dbHelper, int clubid) {
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		Cursor cursor = db.query(TABLE_NAME, null, " clubid=? ", new String[] { clubid + "" }, null, null, null);
		return cursor;
	}

	public static void InsertValue(DatabaseHelper dbHelper,
			String activityid, String activityname, String activityslogan,
			String activitystart,String activitytype, String activityend, String company_name,
			String aimstep, String personnum, String personseq,
			String groupnum, String groupseq , int clubid) {
		ContentValues values = new ContentValues();

		values.put(ACTIVITYID, activityid);
		values.put(ACTIVITYNAME, activityname);
		values.put(ACTIVITYSLOGAN, activityslogan);
		values.put(ACTIVITYSTART, activitystart);
		values.put(ACTIVITYTYPE, activitytype);
		values.put(ACTIVITYEND, activityend);
		values.put(COMPANYNAME, company_name);
		values.put(AIMSTEP, aimstep);
		values.put(PERSONNUM, personnum);
		values.put(PERSONSEQ, personseq);
		values.put(GROUPNUM, groupnum);
		values.put(GROUPSEQ, groupseq);
		values.put(CLUBID, clubid);

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

