package cmccsi.mhealth.app.sports.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;
import cmccsi.mhealth.app.sports.common.Constants;

public final class GroupPkInfoTableMetaData implements BaseColumns {
	public static final String TABLE_NAME = "group_pk_info_table";

	public static final String GROUP_NAME = "groupname";
	public static final String GROUP_ID = "groupid";
	public static final String GROUP_SEQ = "groupseq";
	public static final String GROUP7AVGDIST = "group7avgdist";
	public static final String GROUP7AVGSTEP = "group7avgstep";
	public static final String GROUP_SCORE = "groupscore";
	
	public static final String CLUBID = "clubid";

	public static final String RES1 = "res1";
	public static final String RES2 = "res2";
	public static final String RES3 = "res3";

	public static final String DEFAULT_SORT_ORDER = GROUP_SEQ + " ASC";

	public static String CREATE_TABLE_SQL = "create table " + TABLE_NAME
			+ "(" + _ID + " integer primary key autoincrement,"
			+ GROUP_NAME + " text,"
			+ CLUBID + " int,"
			+ GROUP_SEQ + " int,"
			+ GROUP_ID + " int,"
			+ GROUP7AVGDIST + " text," + GROUP7AVGSTEP + " text,"
			+ GROUP_SCORE + " text," + RES1 + " text," + RES2 + " text,"
			+ RES3 + " text" + ")";

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

	public static Cursor GetAllValueCursor(DatabaseHelper dbHelper) {
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		Cursor cursor = db.query(TABLE_NAME, null, null, null, null, null,
				DEFAULT_SORT_ORDER);
		return cursor;
	}

	public static Cursor GetValueCursorToday(DatabaseHelper dbHelper , int clubid) {
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		Cursor cursor = db.query(TABLE_NAME, null, RES2 + "="
				+ Constants.GROUP_7DAY  + " and " + CLUBID + "=" + clubid, null, null, null,
				DEFAULT_SORT_ORDER);
		return cursor;
	}
	
	/**
	 * 
	* GetGroupIdFromName(根据组name查询对应的id)  
	* @return Cursor
	* @Exception 异常对象   
	* @创建人：Qiujunjie - 邱俊杰
	* @创建时间：2013-9-12 下午5:44:46   
	* @修改人：Qiujunjie - 邱俊杰
	* @修改时间：2013-9-12 下午5:44:46
	 */
	public static Cursor GetGroupIdFromName(DatabaseHelper dbHelper,String name,int clubid) {
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		String sql = "select distinct(groupid) from "+TABLE_NAME+" where clubid = " + clubid +" and groupname = '"+name+"'";
		Cursor cursor = db.rawQuery(sql, null);
//		Cursor cursor = db.query(TABLE_NAME, null,sql, null, null, null,
//				DEFAULT_SORT_ORDER);
		return cursor;
	}

	public static Cursor GetValueCursorYesterday(DatabaseHelper dbHelper, int clubid) {
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		Cursor cursor = db.query(TABLE_NAME, null, RES2 + "="
				+ Constants.GROUP_YESTERDAY + " and " + CLUBID + "=" + clubid, null, null, null,
				DEFAULT_SORT_ORDER);
		return cursor;
	}

	public static void InsertValue(SQLiteDatabase db,
			String groupName, String groupid, String groupSeq,
			String group7avgdist, String group7avgstep, String groupScore,
			String groupType, int clubid) {
		ContentValues values = new ContentValues();

		values.put(GROUP_NAME, groupName);
		values.put(GROUP_ID, groupid);
		values.put(GROUP_SEQ, groupSeq);
		values.put(GROUP7AVGDIST, group7avgdist);
		values.put(GROUP7AVGSTEP, group7avgstep);
		values.put(GROUP_SCORE, groupScore);
		
		values.put(CLUBID, clubid);

		values.put(RES1, "");
		values.put(RES2, groupType); // 昨日 今日
		values.put(RES3, "");

		db.insert(TABLE_NAME, null, values);
	}

	public static void DeleteData(DatabaseHelper dbHelper, String id) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		db.execSQL("DELETE FROM " + TABLE_NAME + " WHERE _id=" + id);
	}
	public static void DeleteData(DatabaseHelper dbHelper, int clubid) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		db.execSQL("DELETE FROM " + TABLE_NAME + " WHERE "+ CLUBID +" = " + clubid);
	}

	public static void DeleteData(DatabaseHelper dbHelper) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		db.execSQL("DELETE FROM " + TABLE_NAME);
	}
}
