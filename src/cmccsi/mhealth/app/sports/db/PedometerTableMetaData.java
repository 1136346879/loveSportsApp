package cmccsi.mhealth.app.sports.db;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;
import cmccsi.mhealth.app.sports.bean.DataPedometor;
import cmccsi.mhealth.app.sports.bean.PedometorDataInfo;

public final class PedometerTableMetaData implements BaseColumns {
	public static final String TABLE_NAME = "pedometer_table";

	public static final String DATE = "date";
	public static final String POWER = "power";
	public static final String WEIGHT = "weight";
	public static final String STEP = "step";
	public static final String ENERGY_CONSUMPTION = "energy_consumption";
	public static final String STEP_NUM = "step_num";
	public static final String DISTANCE = "distance";
	public static final String STRENGTH_ONE = "strength_one";
	public static final String STRENGTH_TWO = "strength_two";
	public static final String STRENGTH_THREE = "strength_three";
	public static final String STRENGTH_FOUR = "strength_four";
	public static final String TRANS_TYPE = "trans_type";

	public static final String MOOD_RECORD = "mood_record";
	public static final String MOOD_LEVEL = "mood_level";
	public static final String SPORTS_TYPE = "sports_type";

	public static final String RES1 = "res1";
	public static final String RES2 = "res2";
	public static final String RES3 = "res3";

	public static final String DEFAULT_SORT_ORDER = "_id DESC";
	public static final String SORT_ORDER_ASC = "_id ASC";

	public static String CREATE_TABLE_SQL = "create table " + TABLE_NAME + "("
			+ _ID + " integer primary key autoincrement," + DATE + " text,"
			+ POWER + " text," + WEIGHT + " text," + STEP + " text," + STEP_NUM
			+ " text," + ENERGY_CONSUMPTION + " text," + STRENGTH_ONE
			+ " text," + STRENGTH_TWO + " text," + STRENGTH_THREE + " text,"
			+ STRENGTH_FOUR + " text," + TRANS_TYPE + " text," + DISTANCE
			+ " text," + MOOD_RECORD + " text," + MOOD_LEVEL + " text,"
			+ SPORTS_TYPE + " text," + RES1 + " text," + RES2 + " text," + RES3
			+ " text" + ")";

	public static Cursor GetAllValueCursor(DatabaseHelper dbHelper) {
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		Cursor cursor = db.query(TABLE_NAME, null, null, null, null, null,
				DEFAULT_SORT_ORDER);
		return cursor;
	}

	public static Cursor GetAllValueCursor(DatabaseHelper dbHelper,
			String searchDate) {
		String tomorrow;
		try
		{
			SimpleDateFormat sf=new SimpleDateFormat("yyyy-MM-dd");
			Date date=sf.parse(searchDate);
			Calendar cr=Calendar.getInstance();
			cr.setTime(date);
			cr.add(Calendar.DAY_OF_YEAR, 1);
			tomorrow=sf.format(cr.getTime());
		}
		catch(ParseException e)
		{
			e.printStackTrace();
			return null;
		}
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		Cursor cursor= db.rawQuery("select * from "+PedometerTableMetaData.TABLE_NAME+" where "
				+PedometerTableMetaData.DATE+">'"+searchDate+"' and "+PedometerTableMetaData.DATE+"<'"+tomorrow+"'",null);
		return cursor;
	}
	
	public static Cursor GetAllValueCursor(DatabaseHelper dbHelper,
			String startDate,String endDate) {
		try
		{
			SimpleDateFormat sf=new SimpleDateFormat("yyyy-MM-dd");
			Date tempstartdate=sf.parse(startDate);
			startDate=sf.format(tempstartdate);
			Date tempenddate=sf.parse(endDate);
			endDate=sf.format(tempenddate);
		}
		catch(IllegalArgumentException ille)
		{
			ille.printStackTrace();
			return null;
		} catch (ParseException e) {
			e.printStackTrace();
			return null;
		}
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		Cursor cursor= db.rawQuery("select * from "+PedometerTableMetaData.TABLE_NAME+" where "
				+PedometerTableMetaData.DATE+">='"+startDate+"' and "+PedometerTableMetaData.DATE+"<'"+endDate+"' order by "
				+PedometerTableMetaData.DATE+" desc",null);
		return cursor;
	}
	
	public static Cursor GetAllValueCursor(DatabaseHelper dbHelper,
			String startDate,String endDate,String deviceId) {
		try
		{
			SimpleDateFormat sf=new SimpleDateFormat("yyyy-MM-dd");
			Date tempstartdate=sf.parse(startDate);
			startDate=sf.format(tempstartdate);
			Date tempenddate=sf.parse(endDate);
			endDate=sf.format(tempenddate);
		}
		catch(IllegalArgumentException ille)
		{
			ille.printStackTrace();
			return null;
		} catch (ParseException e) {
			e.printStackTrace();
			return null;
		}
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		Cursor cursor= db.rawQuery("select * from "+PedometerTableMetaData.TABLE_NAME+" where "+PedometerTableMetaData.RES2+"='"
				+deviceId+"' and "
				+PedometerTableMetaData.DATE+">'"+startDate+"' and "+PedometerTableMetaData.DATE+"<'"+endDate+"' order by "
				+PedometerTableMetaData.DATE+" desc",null);
		return cursor;
	}

	public static Cursor GetALLValueCursorByASC(DatabaseHelper dbHelper) {
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		Cursor cursor = db.query(TABLE_NAME, null, null, null, null, null,
				SORT_ORDER_ASC);
		return cursor;
	}
	/**
	 * ��ȡ�� �����ݵ�����
	 * @param dbHelper
	 * @return
	 */
	public static Cursor GetTheFirstValue(DatabaseHelper dbHelper) {
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		Cursor cursor = db.query(TABLE_NAME, new String[]{"min(" + _ID +")",DATE}, null, null, null, null,
				DEFAULT_SORT_ORDER);
		return cursor;
		
	}


	public static Cursor GetValueCursor(DatabaseHelper dbHelper, long id) {
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		Cursor cursor = db.query(TABLE_NAME, null, _ID + "=" + id, null, null,
				null, DEFAULT_SORT_ORDER);
		return cursor;
	}

	public static DataPedometor GetPedometerFromCursor(Cursor cursor) {
		DataPedometor pedoInfo = new DataPedometor();

		pedoInfo.createtime = cursor.getString(cursor.getColumnIndex(DATE));
		pedoInfo.data.date=pedoInfo.createtime;
		pedoInfo.data.step = cursor.getString(cursor.getColumnIndex(STEP));
		pedoInfo.data.weight = cursor.getString(cursor.getColumnIndex(WEIGHT));
		pedoInfo.data.distance = cursor.getString(cursor
				.getColumnIndex(DISTANCE));
		pedoInfo.data.cal = cursor.getString(cursor
				.getColumnIndex(ENERGY_CONSUMPTION));
		pedoInfo.data.stepNum = cursor.getString(cursor
				.getColumnIndex(STEP_NUM));
		pedoInfo.data.strength1 = cursor.getString(cursor
				.getColumnIndex(STRENGTH_ONE));
		pedoInfo.data.strength2 = cursor.getString(cursor
				.getColumnIndex(STRENGTH_TWO));
		pedoInfo.data.strength3 = cursor.getString(cursor
				.getColumnIndex(STRENGTH_THREE));
		pedoInfo.data.strength4 = cursor.getString(cursor
				.getColumnIndex(STRENGTH_FOUR));
		pedoInfo.data.yxbssum = cursor.getString(cursor.getColumnIndex(RES1));

		return pedoInfo;
	}
	
	public static PedometorDataInfo GetPedometerDataInfoFromCursor(Cursor cursor) {
		PedometorDataInfo pedoInfo = new PedometorDataInfo();

		pedoInfo.createtime = cursor.getString(cursor.getColumnIndex(DATE));
		pedoInfo.date=pedoInfo.createtime;
		pedoInfo.step = cursor.getString(cursor.getColumnIndex(STEP));
		pedoInfo.weight = cursor.getString(cursor.getColumnIndex(WEIGHT));
		pedoInfo.distance = cursor.getString(cursor
				.getColumnIndex(DISTANCE));
		pedoInfo.cal = cursor.getString(cursor
				.getColumnIndex(ENERGY_CONSUMPTION));
		pedoInfo.stepNum = cursor.getString(cursor
				.getColumnIndex(STEP_NUM));
		pedoInfo.strength1 = cursor.getString(cursor
				.getColumnIndex(STRENGTH_ONE));
		pedoInfo.strength2 = cursor.getString(cursor
				.getColumnIndex(STRENGTH_TWO));
		pedoInfo.strength3 = cursor.getString(cursor
				.getColumnIndex(STRENGTH_THREE));
		pedoInfo.strength4 = cursor.getString(cursor
				.getColumnIndex(STRENGTH_FOUR));
		pedoInfo.yxbssum = cursor.getString(cursor.getColumnIndex(RES1));
		pedoInfo.deviceId = cursor.getString(cursor.getColumnIndex(RES2));
		pedoInfo.deviceType = cursor.getString(cursor.getColumnIndex(RES3));
		return pedoInfo;
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
			String date, String power, String weight, String step,
			String energy_consumption, String step_num, String distance,
			String strength_one, String strength_two, String strength_three,
			String strength_four, String transType, String yxbssum) {
		ContentValues values = new ContentValues();

		values.put(DATE, date);
		values.put(POWER, power);
		values.put(WEIGHT, weight);
		values.put(STEP, step);
		values.put(ENERGY_CONSUMPTION, energy_consumption);
		values.put(STEP_NUM, step_num);
		values.put(DISTANCE, distance);
		values.put(STRENGTH_ONE, strength_one);
		values.put(STRENGTH_TWO, strength_two);
		values.put(STRENGTH_THREE, strength_three);
		values.put(STRENGTH_FOUR, strength_four);
		values.put(TRANS_TYPE, transType);
		values.put(RES1, yxbssum);

		return dbHelper.getWritableDatabase().update(TABLE_NAME, values,
				_ID + "=" + id, null) > 0;
	}

	public static boolean UpdateValue(SQLiteDatabase db, long id,
			String date, String power, String weight, String step,
			String energy_consumption, String step_num, String distance,
			String strength_one, String strength_two, String strength_three,
			String strength_four, String transType, String yxbssum,String deviceId,String devicePara) {
		ContentValues values = new ContentValues();

		values.put(DATE, date);
		values.put(POWER, power);
		values.put(WEIGHT, weight);
		values.put(STEP, step);
		values.put(ENERGY_CONSUMPTION, energy_consumption);
		values.put(STEP_NUM, step_num);
		values.put(DISTANCE, distance);
		values.put(STRENGTH_ONE, strength_one);
		values.put(STRENGTH_TWO, strength_two);
		values.put(STRENGTH_THREE, strength_three);
		values.put(STRENGTH_FOUR, strength_four);
		values.put(TRANS_TYPE, transType);
		values.put(RES1, yxbssum);
		values.put(RES2, deviceId);
		values.put(RES3, devicePara);
		return db.update(TABLE_NAME, values,
				_ID + "=" + id, null) > 0;
	}
	
	public static void InsertValue(SQLiteDatabase db, String date,
			String power, String weight, String step,
			String energy_consumption, String step_num, String distance,
			String strength_one, String strength_two, String strength_three,
			String strength_four, String transType, String yxbssum) {
		ContentValues values = new ContentValues();

		values.put(DATE, date);
		values.put(POWER, power);
		values.put(WEIGHT, weight);
		values.put(STEP, step);
		values.put(ENERGY_CONSUMPTION, energy_consumption);
		values.put(STEP_NUM, step_num);
		values.put(DISTANCE, distance);
		values.put(STRENGTH_ONE, strength_one);
		values.put(STRENGTH_TWO, strength_two);
		values.put(STRENGTH_THREE, strength_three);
		values.put(STRENGTH_FOUR, strength_four);
		values.put(TRANS_TYPE, transType);
		values.put(RES1, yxbssum);
		values.put(RES2, "");
		values.put(RES3, "");

		db.insert(TABLE_NAME, null, values);
	}

	public static void InsertValue(SQLiteDatabase db, String date,
			String power, String weight, String step,
			String energy_consumption, String step_num, String distance,
			String strength_one, String strength_two, String strength_three,
			String strength_four, String transType, String yxbssum,String deviceId,String devicePara) {
		ContentValues values = new ContentValues();

		values.put(DATE, date);
		values.put(POWER, power);
		values.put(WEIGHT, weight);
		values.put(STEP, step);
		values.put(ENERGY_CONSUMPTION, energy_consumption);
		values.put(STEP_NUM, step_num);
		values.put(DISTANCE, distance);
		values.put(STRENGTH_ONE, strength_one);
		values.put(STRENGTH_TWO, strength_two);
		values.put(STRENGTH_THREE, strength_three);
		values.put(STRENGTH_FOUR, strength_four);
		values.put(TRANS_TYPE, transType);
		values.put(RES1, yxbssum);
		values.put(RES2, deviceId);
		values.put(RES3, devicePara);

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
