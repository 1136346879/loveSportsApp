package cmccsi.mhealth.app.sports.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;
import cmccsi.mhealth.app.sports.bean.DataECG;
import cmccsi.mhealth.app.sports.bean.DataPedometor;

public final class ECGTableMetaData implements BaseColumns {
	public static final String TABLE_NAME = "ECG_table";

	public static final String DATE = "date";
	public static final String POWER = "power";
	public static final String HR = "hr";
	public static final String RAWDATA = "rawData";
	public static final String HRV = "hrv";
	public static final String RR_INTERVAL = "rr_interval";
	public static final String MOOD = "mood";
	public static final String TRAINING_ZONE = "trainingzone";

	public static final String MOOD_RECORD = "mood_record";
	public static final String MOOD_LEVEL = "mood_level";
	public static final String SPORTS_TYPE = "sports_type";
	
	public static final String DEFAULT_SORT_ORDER = "_id DESC";
	public static final String SORT_ORDER_ASC = "_id ASC";
	public static final String SORT_ORDER_DATE_ASC = "date ASC";

	public static String CREATE_TABLE_SQL = "create table " + TABLE_NAME + "("
			+ _ID + " integer primary key autoincrement," + DATE + " text,"+POWER+" text,"
			+ HR + " text," + RAWDATA + " text," + HRV + " text," + RR_INTERVAL
			+ " text," + MOOD + " text," + TRAINING_ZONE
			+ " text" + ")";

	public static Cursor GetAllValueCursor(DatabaseHelper dbHelper) {
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		Cursor cursor = db.query(TABLE_NAME, null, null, null, null, null,
				DEFAULT_SORT_ORDER);
		return cursor;
	}
	/**
	 * 检索给定时间段之内的数据
	 * @author Lianxw
	 * @param dbHelper
	 * @param startTime 开始时间
	 * @param endTime 结束时间
	 * @return 
	 */
	public static Cursor GetAllValueCursor(DatabaseHelper dbHelper,String startTime,String endTime) {
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		String selection = " "+ DATE +" between ? and ? ";
		String[] selectionArg = { startTime, endTime };
		Cursor cursor = db.query(TABLE_NAME, null, selection, selectionArg, null, null,
				SORT_ORDER_DATE_ASC);
		return cursor;
	}

	public static Cursor GetAllValueCursor(DatabaseHelper dbHelper,
			String searchDate) {
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		Cursor cursor = db.query(TABLE_NAME, null, DATE + " like '"
				+ searchDate + "%'", null, null, null, DEFAULT_SORT_ORDER);
		return cursor;
	}

	public static Cursor GetALLValueCursorByASC(DatabaseHelper dbHelper) {
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		Cursor cursor = db.query(TABLE_NAME, null, null, null, null, null,
				SORT_ORDER_ASC);
		return cursor;
	}

	public static Cursor GetValueCursor(DatabaseHelper dbHelper, long id) {
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		Cursor cursor = db.query(TABLE_NAME, null, _ID + "=" + id, null, null,
				null, DEFAULT_SORT_ORDER);
		return cursor;
	}

	public static DataECG GetECGFromCursor(Cursor cursor) {
		DataECG ECGInfo = new DataECG();

		ECGInfo.createtime = cursor.getString(cursor.getColumnIndex(DATE));
		ECGInfo.data.date = cursor.getString(cursor.getColumnIndex(DATE));
		ECGInfo.data.power = cursor.getString(cursor.getColumnIndex(POWER));
		ECGInfo.data.hr = cursor.getString(cursor.getColumnIndex(HR));
		ECGInfo.data.rawdata = cursor.getString(cursor
				.getColumnIndex(RAWDATA));
		ECGInfo.data.hrv = cursor.getString(cursor
				.getColumnIndex(HRV));
		ECGInfo.data.rr_interval = cursor.getString(cursor
				.getColumnIndex(RR_INTERVAL));
		ECGInfo.data.trainingzone = cursor.getString(cursor
				.getColumnIndex(TRAINING_ZONE));
		ECGInfo.data.mood=cursor.getString(cursor.getColumnIndex(MOOD));
		return ECGInfo;
	}

	public static boolean UpdateData(DatabaseHelper dbHelper, long id,
			int mood_level, String text, int type) {
		ContentValues args = new ContentValues();
		args.put(MOOD_LEVEL, mood_level);
		args.put(MOOD_RECORD, text);
		args.put(SPORTS_TYPE, type);
		return dbHelper.getWritableDatabase().update(TABLE_NAME, args,
				_ID + "=" + id, null) > 0;
	}

	public static boolean UpdateValue(DatabaseHelper dbHelper, long id,
			String date, String power,String hr, String rawData, String hrv,
			String rr_interval,String mood, String trainingzone) {
		ContentValues values = new ContentValues();

		values.put(DATE, date);
		values.put(POWER, power);
		values.put(HR, hr);
		values.put(RAWDATA, rawData);
		values.put(HRV, hrv);
		values.put(RR_INTERVAL, rr_interval);
		values.put(TRAINING_ZONE, trainingzone);
		values.put(MOOD, mood);

		return dbHelper.getWritableDatabase().update(TABLE_NAME, values,
				_ID + "=" + id, null) > 0;
	}

	public static void InsertValue(SQLiteDatabase db, String date,
			String power,String hr, String rawData, String hrv,
			String rr_interval,String mood, String trainingzone){
		ContentValues values = new ContentValues();

		values.put(DATE, date);
		values.put(POWER, power);
		values.put(HR, hr);
		values.put(RAWDATA, rawData);
		values.put(HRV, hrv);
		values.put(RR_INTERVAL, rr_interval);
		values.put(TRAINING_ZONE, trainingzone);
		values.put(MOOD, mood);

		db.insert(TABLE_NAME, null, values);
	}

	public static void DeleteData(DatabaseHelper dbHelper, String id) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		db.execSQL("DELETE FROM " + TABLE_NAME + " WHERE _id=" + id);
	}

	public static void DeleteData(DatabaseHelper dbHelper) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		db.execSQL("DELETE FROM " + TABLE_NAME);
	}
}
