package cmccsi.mhealth.app.sports.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

public final class OrgnizeInfoTableMetaData implements BaseColumns {
	public static final String TABLE_NAME = "orgnize_member_info_table";

	public static final String MEMBER_NAME = "membername";
	public static final String GROUP_NAME = "groupname";

	public static final String MEMBER_SEQ = "memberseq";
	public static final String MEMBER7AVGDIST = "member7avgdist";
	public static final String MEMBER7AVGSTEP = "member7avgstep";
	public static final String MEMBER_SCORE = "memberscore";
	
	public static final String CLUBID = "clubid";

	public static final String MEMBER_FORVE1 = "memberforve1";//
	public static final String MEMBER_FORVE2 = "memberforve2";

	public static final String DEFAULT_SORT_ORDER = MEMBER_SEQ + " ASC";
	public static final String AVATER = "avater";
	

	public static String CREATE_TABLE_SQL = "create table " + TABLE_NAME
			+ "(" + _ID + " integer primary key autoincrement,"
			+ MEMBER_NAME + " text,"
			+ MEMBER_SEQ + " int,"
			+ CLUBID + " int,"
			+ MEMBER7AVGDIST + " text," + MEMBER7AVGSTEP + " text,"
			+ MEMBER_SCORE + " text," + MEMBER_FORVE1 + " text,"
			+ MEMBER_FORVE2 + " text," + GROUP_NAME + " text," 
			+ AVATER+" text"+
			")";

	public static String DELETE_TABLE_SQL = "drop table  IF EXISTS "
			+ TABLE_NAME;

	/**
	 * 重新创建数据库
	 * 
	 * @param dbHelper
	 */
	public static void CreateTable(DatabaseHelper dbHelper) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		db.execSQL(DELETE_TABLE_SQL);
		db.execSQL(CREATE_TABLE_SQL);
	}

	public static int GetAllDataSum(DatabaseHelper dbHelper, int timeType , int clubid) {
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		Cursor cursor = db.rawQuery("SELECT COUNT(*) AS CNT FROM " + TABLE_NAME +
				" WHERE " + MEMBER_FORVE2 + " = " + timeType + " and "
				 	      + CLUBID + " = " + clubid, null);
		cursor.moveToNext();
		return cursor.getInt(cursor.getColumnIndex("CNT"));
	}

	public static Cursor GetAllValueCursor(DatabaseHelper dbHelper) {
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		Cursor cursor = db.query(TABLE_NAME, null, null, null, null, null,
				DEFAULT_SORT_ORDER);
		return cursor;
	}

	public static Cursor GetValueCursor(DatabaseHelper dbHelper, long id) {
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		Cursor cursor = db.query(TABLE_NAME, null, _ID + "=" + id, null,
				null, null, DEFAULT_SORT_ORDER);
		return cursor;
	}

	public static Cursor GetValueCursor(DatabaseHelper dbHelper,
			int timeType , int clubid) {
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		Cursor cursor = db.query(TABLE_NAME, null, MEMBER_FORVE2 + "="
				+ timeType + " and " + CLUBID + "=" + clubid, null, null,null, DEFAULT_SORT_ORDER);
		return cursor;
	}

	public static void InsertValue(SQLiteDatabase db,
			String memberName, String groupName, String memberSeq,
			String member7avgdist, String member7avgstep,
			String memberScore, String memberforve1, String memberforve2,String avater , int clubid){
		ContentValues values = new ContentValues();

		values.put(MEMBER_NAME, memberName);
		values.put(GROUP_NAME, groupName);
		values.put(MEMBER_SEQ, memberSeq);
		values.put(MEMBER7AVGDIST, member7avgdist);
		values.put(MEMBER7AVGSTEP, member7avgstep);
		values.put(MEMBER_SCORE, memberScore);
		
		values.put(CLUBID, clubid);

		values.put(MEMBER_FORVE1, memberforve1);
		values.put(MEMBER_FORVE2, memberforve2);
		values.put(AVATER, avater);

		db.insert(TABLE_NAME, null, values);
	}

	public static void DeleteData(DatabaseHelper dbHelper, String id) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		db.execSQL("DELETE FROM " + TABLE_NAME + " WHERE _id=" + id);
	}

	public static void DeleteData(DatabaseHelper dbHelper, int clubid) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		db.execSQL("DELETE FROM " + TABLE_NAME + " WHERE " + CLUBID
				+ " = " + clubid);
	}

	public static void DeleteData(DatabaseHelper dbHelper) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		db.execSQL("DELETE FROM " + TABLE_NAME);
	}
}

