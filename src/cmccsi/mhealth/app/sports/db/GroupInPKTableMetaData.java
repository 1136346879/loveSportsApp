package cmccsi.mhealth.app.sports.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

public final class GroupInPKTableMetaData implements BaseColumns {
	public static final String TABLE_NAME = "group_inpk_table";

	public static final String MEMBER_SEQ = "memberseq";
	public static final String GROUP_ID = "groupid";
	public static final String MEMBER_NAME = "membername";
	public static final String GROUP_NAME = "groupname";
	public static final String MEMBER7AVGSTEP = "member7avgstep";
	public static final String CLUBID = "clubid";

	public static final String MEMBER7AVGDIST = "member7avgdist";
	public static final String MEMBER_SCORE = "memberscore";
	public static final String MEMBER_FORVE1 = "memberforve1";
	public static final String MEMBER_FORVE2 = "memberforve2";
	public static final String AVATER = "avater";

	public static String CREATE_TABLE_SQL = "create table " + TABLE_NAME + "("
			+ _ID + " integer primary key autoincrement,"
			+ MEMBER_NAME + " text,"
			+ GROUP_NAME + " text,"
			+ GROUP_ID + " int,"
			+ CLUBID + " int,"
			+ MEMBER_SEQ + " int,"
			+ MEMBER7AVGDIST + " text,"
			+ MEMBER7AVGSTEP + " text,"
			+ MEMBER_SCORE + " text,"
			+ MEMBER_FORVE1 + " text,"
			+ AVATER + " text,"
			+ MEMBER_FORVE2 + " text" + ")";

	public static String DELETE_TABLE_SQL = "drop table  IF EXISTS "
			+ TABLE_NAME;

	public static final String DEFAULT_SORT_ORDER = MEMBER_SEQ + " ASC"; // 升序

	/**
	 * 创建数据库
	 * 
	 * @param dbHelper
	 *            qjj 2013-1-30
	 */
	public static void CreateTable(DatabaseHelper dbHelper) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		db.execSQL(DELETE_TABLE_SQL);
		db.execSQL(CREATE_TABLE_SQL);
	}

	/**
	 * 
	 * @param dbHelper
	 * @return qjj 2013-1-30
	 */
	public static Cursor GetAllValueCursor(DatabaseHelper dbHelper) {
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		Cursor cursor = db.query(TABLE_NAME, null, null, null, null, null,
				DEFAULT_SORT_ORDER);
		return cursor;
	}

	/**
	 * 查询不同组的id
	 * 
	 * @param dbHelper
	 * @param id
	 * @return qjj 2013-1-30
	 */
	public static Cursor GetValueCursor(DatabaseHelper dbHelper, int groupid) {
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		Cursor cursor = db.query(TABLE_NAME, null, GROUP_ID + "=" + groupid,null, null, null, DEFAULT_SORT_ORDER);
		return cursor;
	}

	/**
	 * 查询不同组的id
	 * 
	 * @param dbHelper
	 * @param id
	 * @return qjj 2013-1-30
	 */
	public static Cursor GetYestodayCursor(DatabaseHelper dbHelper,
			int groupid, String yes,int clubid) {
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		// String sql = "select * from " + TABLE_NAME + " where "
		// + ACTIVITY_ID + " = " + activityid;
		String sql = "select * from " + TABLE_NAME + " where " + GROUP_ID
				+ " = " + groupid + " and " + MEMBER_FORVE2 + " = " + yes + " and " + CLUBID + " = " + clubid
				+" order by "+GroupInPKTableMetaData.MEMBER_SEQ;
		Cursor cursor = db.rawQuery(sql, null);
		
		return cursor;
	}

	/**
	 * 
	 * @param dbHelper
	 * @param memberSeq
	 *            组成员id
	 * @param memberName
	 *            组成员名称
	 * @param groupname
	 *            组名称
	 * @param groupid
	 *            组ID
	 * @param member7avgdist
	 *            7天移动的距离
	 * @param member7avgstep
	 *            7天平局步数
	 * @param memberScore
	 *            7天总步数 qjj 2013-1-31
	 */
	public static void InsertValue(SQLiteDatabase db, String memberSeq,
			String memberName, String groupName, String groupid,
			String member7avgstep, String member7avgdist, String memberScore,
			String memberforve1, String memberforve2,String avater , int clubid) {
		ContentValues values = new ContentValues();

		values.put(MEMBER_NAME, memberName);
		values.put(MEMBER_SEQ, memberSeq);
		values.put(MEMBER7AVGSTEP, member7avgstep);
		values.put(GROUP_NAME, groupName);
		values.put(GROUP_ID, groupid);
		
		values.put(CLUBID, clubid);

		values.put(MEMBER7AVGDIST, member7avgdist);
		values.put(MEMBER_SCORE, memberScore);
		values.put(MEMBER_FORVE1, memberforve1);
		values.put(MEMBER_FORVE2, memberforve2);
		values.put(AVATER, avater);

		db.insert(TABLE_NAME, null, values);
	}

	/**
	 * 
	 * @param dbHelper
	 * @param groupid
	 *            qjj 2013-1-31
	 */
	public static void DeleteData(DatabaseHelper dbHelper, String groupid) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		db.execSQL("DELETE FROM " + TABLE_NAME + " WHERE "
				+ GroupInPKTableMetaData.GROUP_ID + "=" + groupid);
	}
	public static void DeleteData(DatabaseHelper dbHelper, String groupid, int clubid) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		db.execSQL("DELETE FROM " + TABLE_NAME + " WHERE " + GroupInPKTableMetaData.GROUP_ID + "=" + groupid + " and " 
		+ CLUBID + "=" + clubid);
	}

	/**
	 * 
	 * @param dbHelper
	 *            qjj 2013-1-31
	 */
	public static void DeleteData(DatabaseHelper dbHelper) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		db.execSQL("DELETE FROM " + TABLE_NAME);
	}
}
