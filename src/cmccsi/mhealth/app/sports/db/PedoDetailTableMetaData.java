package cmccsi.mhealth.app.sports.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

public final class PedoDetailTableMetaData implements BaseColumns {
	public static final String TABLE_NAME = "pedo_detail_table";

	public static final String DEFAULT_SORT_ORDER = "_id DESC";

	public static final String PHONE_NUM = "phone_num";
	/**
	 * 日期记录 格式 20121122
	 */
	public static final String DATE = "sports_date";
	/**
	 * 起始时间 00,01,02,03...23
	 */
	public static final String START_TIME = "start_time";
	public static final String STEP_NUM_PER_FIVE = "step_num_per_five";
	public static final String CAL_PER_FIVE = "cal_per_five";
	public static final String STRENGTH_TWO_PER_FIVE = "strength_two_per_five";
	public static final String STRENGTH_THREE_PER_FIVE = "strength_three_per_five";
	public static final String STRENGTH_FOUR_PER_FIVE = "strength_four_per_five";
	/**
	 * 每五分钟加速度
	 */
	public static final String ACC_PER_FIVE = "acc_per_five";

	public static final String EFF_STEPNUM_PER_FIVE = "effective_stepnum_per_five";
	public static final String RES1 = "res1";
	public static final String RES2 = "res2";

	public static String CREATE_TABLE_SQL = "create table " + TABLE_NAME + "("
			+ _ID + " integer primary key autoincrement," + DATE + " text,"
			+ PHONE_NUM + " text," + START_TIME + " text," + STEP_NUM_PER_FIVE
			+ " text," + CAL_PER_FIVE + " text," + STRENGTH_TWO_PER_FIVE
			+ " text," + STRENGTH_THREE_PER_FIVE + " text,"
			+ STRENGTH_FOUR_PER_FIVE + " text," + ACC_PER_FIVE + " text, "
			+ EFF_STEPNUM_PER_FIVE + " text, " + RES1 + " text, " + RES2
			+ " text" + ")";

	public static Cursor GetValueCursor(DatabaseHelper dbHelper) {
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		Cursor cursor = db.query(TABLE_NAME, null, null, null, null, null,
				DEFAULT_SORT_ORDER);
		return cursor;
	}

	public static Cursor GetValueCursor(DatabaseHelper dbHelper, long id) {
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		Cursor cursor = db.query(TABLE_NAME, null, _ID + "=" + id, null, null,
				null, DEFAULT_SORT_ORDER);
		return cursor;
	}

	public static Cursor GetValueCursor(DatabaseHelper dbHelper,
			String search_date) {
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		Cursor cursor = db.query(TABLE_NAME, null, DATE + "='" + search_date
				+ "'", null, null, null, DEFAULT_SORT_ORDER);
		return cursor;
	}
	public static Cursor GetValueCursor(SQLiteDatabase db,String start_time) {
		String [] columns={STEP_NUM_PER_FIVE};
		Cursor cursor = db.query(TABLE_NAME, columns, START_TIME + "='" + start_time
				+ "'", null, null, null, DEFAULT_SORT_ORDER);
		return cursor;
	}
	public static Cursor GetValueCursor(SQLiteDatabase db,String date,String start_time){
		Cursor cursor=db.query(TABLE_NAME, null, START_TIME+"=? AND "+DATE+"=?", new String[]{start_time,date}, null, null, null);
		return cursor;
	}
	public static Cursor GetValueCursor(SQLiteDatabase db,String deviceId,String date,String start_time){
		Cursor cursor=db.query(TABLE_NAME, null, START_TIME+"=? AND "+DATE+"=? AND "+RES2+"=?", new String[]{start_time,date,deviceId}, null, null, null);
		return cursor;
	}
	public static Cursor GetValueCursor(SQLiteDatabase db,String deviceId,String date,String start_time,String end_time){
		Cursor cursor=db.query(TABLE_NAME, null, START_TIME+">? AND "+START_TIME+"<? AND "+DATE+"=? AND "+RES2+"=?", new String[]{start_time,end_time,date,deviceId}, null, null, null);
		return cursor;
	}
	public static void InsertValue(SQLiteDatabase db, String phone_num,
			String date, String start_time, String step_num_per_five,
			String cal_per_five, String strength_two_per_five,
			String strength_three_per_five, String strength_four_per_five,
			String acc_per_five, String effective_stepnum_per_five) {
		ContentValues values = new ContentValues();

		values.put(PHONE_NUM, phone_num);
		values.put(DATE, date);
		values.put(START_TIME, start_time);
		values.put(STEP_NUM_PER_FIVE, step_num_per_five);
		values.put(CAL_PER_FIVE, cal_per_five);
		values.put(STRENGTH_TWO_PER_FIVE, strength_two_per_five);
		values.put(STRENGTH_THREE_PER_FIVE, strength_three_per_five);
		values.put(STRENGTH_FOUR_PER_FIVE, strength_four_per_five);
		values.put(ACC_PER_FIVE, acc_per_five);
		values.put(EFF_STEPNUM_PER_FIVE, effective_stepnum_per_five);

		db.insert(TABLE_NAME, null, values);
	}
	
	public static void InsertValue(SQLiteDatabase db, String phone_num,
			String date, String start_time, String step_num_per_five,
			String cal_per_five, String strength_two_per_five,
			String strength_three_per_five, String strength_four_per_five,
			String acc_per_five, String effective_stepnum_per_five,String deviceId) {
		ContentValues values = new ContentValues();

		values.put(PHONE_NUM, phone_num);
		values.put(DATE, date);
		values.put(START_TIME, start_time);
		values.put(STEP_NUM_PER_FIVE, step_num_per_five);
		values.put(CAL_PER_FIVE, cal_per_five);
		values.put(STRENGTH_TWO_PER_FIVE, strength_two_per_five);
		values.put(STRENGTH_THREE_PER_FIVE, strength_three_per_five);
		values.put(STRENGTH_FOUR_PER_FIVE, strength_four_per_five);
		values.put(ACC_PER_FIVE, acc_per_five);
		values.put(EFF_STEPNUM_PER_FIVE, effective_stepnum_per_five);
		values.put(RES2, deviceId);
		db.insert(TABLE_NAME, null, values);
	}
	public static void UpdateValue(SQLiteDatabase db, String phone_num,
			String date, String start_time, String step_num_per_five,
			String cal_per_five, String strength_two_per_five,
			String strength_three_per_five, String strength_four_per_five,
			String acc_per_five, String effective_stepnum_per_five){
		ContentValues values = new ContentValues();
		
		values.put(PHONE_NUM, phone_num);
		values.put(DATE, date);
		values.put(START_TIME, start_time);
		values.put(STEP_NUM_PER_FIVE, step_num_per_five);
		values.put(CAL_PER_FIVE, cal_per_five);
		values.put(STRENGTH_TWO_PER_FIVE, strength_two_per_five);
		values.put(STRENGTH_THREE_PER_FIVE, strength_three_per_five);
		values.put(STRENGTH_FOUR_PER_FIVE, strength_four_per_five);
		values.put(ACC_PER_FIVE, acc_per_five);
		values.put(EFF_STEPNUM_PER_FIVE, effective_stepnum_per_five);
		
		db.update(TABLE_NAME, values, START_TIME+"=? AND "+DATE+"=?", new String[]{start_time,date});
	}
	
	public static void UpdateValue(SQLiteDatabase db, String phone_num,
			String date, String start_time, String step_num_per_five,
			String cal_per_five, String strength_two_per_five,
			String strength_three_per_five, String strength_four_per_five,
			String acc_per_five, String effective_stepnum_per_five,String deviceId){
		ContentValues values = new ContentValues();
		
		values.put(PHONE_NUM, phone_num);
		values.put(DATE, date);
		values.put(START_TIME, start_time);
		values.put(STEP_NUM_PER_FIVE, step_num_per_five);
		values.put(CAL_PER_FIVE, cal_per_five);
		values.put(STRENGTH_TWO_PER_FIVE, strength_two_per_five);
		values.put(STRENGTH_THREE_PER_FIVE, strength_three_per_five);
		values.put(STRENGTH_FOUR_PER_FIVE, strength_four_per_five);
		values.put(ACC_PER_FIVE, acc_per_five);
		values.put(EFF_STEPNUM_PER_FIVE, effective_stepnum_per_five);
		
		db.update(TABLE_NAME, values, START_TIME+"=? AND "+DATE+"=? AND"+RES2+"=?", new String[]{start_time,date,deviceId});
	}
	
	public static void UpdateValue(SQLiteDatabase db, long Id,
			String date, String start_time, String step_num_per_five,
			String cal_per_five, String strength_two_per_five,
			String strength_three_per_five, String strength_four_per_five,
			String acc_per_five, String effective_stepnum_per_five){
		ContentValues values = new ContentValues();
		
		values.put(DATE, date);
		values.put(START_TIME, start_time);
		values.put(STEP_NUM_PER_FIVE, step_num_per_five);
		values.put(CAL_PER_FIVE, cal_per_five);
		values.put(STRENGTH_TWO_PER_FIVE, strength_two_per_five);
		values.put(STRENGTH_THREE_PER_FIVE, strength_three_per_five);
		values.put(STRENGTH_FOUR_PER_FIVE, strength_four_per_five);
		values.put(ACC_PER_FIVE, acc_per_five);
		values.put(EFF_STEPNUM_PER_FIVE, effective_stepnum_per_five);
		
		db.update(TABLE_NAME, values, _ID+"="+Id,null);
	}
	
	public static void UpdateValue(SQLiteDatabase db, long Id,
			String date, String start_time, String step_num_per_five,
			String cal_per_five, String strength_two_per_five,
			String strength_three_per_five, String strength_four_per_five,
			String acc_per_five, String effective_stepnum_per_five,String deviceId){
		ContentValues values = new ContentValues();
		
		values.put(DATE, date);
		values.put(START_TIME, start_time);
		values.put(STEP_NUM_PER_FIVE, step_num_per_five);
		values.put(CAL_PER_FIVE, cal_per_five);
		values.put(STRENGTH_TWO_PER_FIVE, strength_two_per_five);
		values.put(STRENGTH_THREE_PER_FIVE, strength_three_per_five);
		values.put(STRENGTH_FOUR_PER_FIVE, strength_four_per_five);
		values.put(ACC_PER_FIVE, acc_per_five);
		values.put(EFF_STEPNUM_PER_FIVE, effective_stepnum_per_five);
		values.put(RES2, deviceId);
		db.update(TABLE_NAME, values, _ID+"="+Id,null);
	}
	
	public static boolean UpdateData(DatabaseHelper dbHelper, long id,
			String snyx) {
		ContentValues values = new ContentValues();
		values.put(EFF_STEPNUM_PER_FIVE, snyx);

		return dbHelper.getWritableDatabase().update(TABLE_NAME, values,
				_ID + "=" + id, null) > 0;
	}

	public static void DeleteData(DatabaseHelper dbHelper) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		db.execSQL("DELETE FROM " + TABLE_NAME);
	}

	public static void DeleteData(DatabaseHelper dbHelper, String date) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		db.execSQL("DELETE FROM " + TABLE_NAME + " WHERE sports_date=" + date);
	}
}
