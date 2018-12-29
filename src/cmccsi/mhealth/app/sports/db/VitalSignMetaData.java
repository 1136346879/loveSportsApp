package cmccsi.mhealth.app.sports.db;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;
import cmccsi.mhealth.app.sports.bean.VitalSignInfoDataBean;
import cmccsi.mhealth.app.sports.common.Common;
import cmccsi.mhealth.app.sports.common.Logger;

public final class VitalSignMetaData implements BaseColumns {
	private static final String TAG = "VitalSignMetaData";
	// 存入数据库的不同类别的类型
	public static final int TYPE_WEIGHT = 0;
	public static final int TYPE_XUEYA = 1;
	public static final String TYPE_STR_WEIGHT = "weight";
	public static final String TYPE_STR_XUEYA = "xueya";

	public static final String TABLE_NAME = "vitalsign_infos";

	public static final String EDITTIMEBYLONGSTYLE = "edittimebylongstyle";
	public static final String EDITTIME = "edittime";
	public static final String MEASUREDATE = "measuredate";
	public static final String MEASUREDATEBYLONGSTYLE = "measuredatebylongstyle";
	public static final String VALUE = "value";
	public static final String DATETYPE = "datetype";

	public static String CREATE_TABLE_SQL = "create table " + TABLE_NAME + "(" + _ID + " integer primary key autoincrement,"
			+ EDITTIMEBYLONGSTYLE + " BIGINT,"
			+ EDITTIME + " text,"
			+ MEASUREDATE + " text,"
			+ MEASUREDATEBYLONGSTYLE + " BIGINT,"
			+ DATETYPE + " text,"
			+ VALUE + " text)";

	public static String DELETE_TABLE_SQL = "drop table  IF EXISTS " + TABLE_NAME;

	public static void CreateTable(DatabaseHelper dbHelper) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		db.execSQL(DELETE_TABLE_SQL);
		db.execSQL(CREATE_TABLE_SQL);
	}

	// 获取指定最新天数内的所有数据
	public static List<VitalSignInfoDataBean> getVitalSignInRange(DatabaseHelper dbHelper, long starttime, int datetype , String timechoice) {
		Cursor cursor = null;
		List<VitalSignInfoDataBean> mList = null;
		try {
			SQLiteDatabase db = dbHelper.getReadableDatabase();
			
			if(VitalSignMetaData.MEASUREDATEBYLONGSTYLE.equals(timechoice)){
				cursor = db.query(TABLE_NAME, null, timechoice + " > ? and " + timechoice +" < ?  and datetype = ?", new String[] { starttime + "", new Date().getTime() + "",datetype + "" }, null, null, MEASUREDATE + " asc");
			}else{
				cursor = db.query(TABLE_NAME, null, timechoice + " > ? and datetype = ?", new String[] { starttime + "", datetype + "" }, null, null, MEASUREDATE + " asc");
			}
			mList = new ArrayList<VitalSignInfoDataBean>();
			while (cursor.moveToNext()) {
				VitalSignInfoDataBean vsb = new VitalSignInfoDataBean();
				String type = cursor.getString(cursor.getColumnIndex(VitalSignMetaData.DATETYPE));
				String measuredate = cursor.getString(cursor.getColumnIndex(VitalSignMetaData.MEASUREDATE));
				String measuredatebylongstyle = cursor.getString(cursor.getColumnIndex(VitalSignMetaData.MEASUREDATEBYLONGSTYLE));
				String value = cursor.getString(cursor.getColumnIndex(VitalSignMetaData.VALUE));
				String edittime = cursor.getString(cursor.getColumnIndex(VitalSignMetaData.EDITTIME));
				String edittimebylongstyle = cursor.getString(cursor.getColumnIndex(VitalSignMetaData.EDITTIMEBYLONGSTYLE));
				vsb.setDatetype(type);
				vsb.setMeasureDate(measuredate);
				vsb.setMeasureDateByLongStyle(measuredatebylongstyle);
				vsb.setValue(value);
				vsb.setEditTime(edittime);
				vsb.setEditTimeByLongStyle(edittimebylongstyle);
				mList.add(vsb);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (cursor != null)
				cursor.close();
		}
		return mList;
	}

	// 单独加数据
	public static void InsertValue(DatabaseHelper dbHelper, VitalSignInfoDataBean vitalSignBean, int datetype) {
		ContentValues values = new ContentValues();
		SQLiteDatabase db = dbHelper.getWritableDatabase();

		long insert = 0;
		int update = 0;
		
		vitalSignBean.setEditTimeByLongStyle("" + Common.getDateFromStrFromServel(vitalSignBean.getEditTime()));
		vitalSignBean.setMeasureDateByLongStyle("" + Common.getDateFromStr(vitalSignBean.getMeasureDate()));
		
		values.put(EDITTIMEBYLONGSTYLE, vitalSignBean.getEditTimeByLongStyle());
		values.put(EDITTIME, vitalSignBean.getEditTime());
		values.put(MEASUREDATE, vitalSignBean.getMeasureDate());
		values.put(MEASUREDATEBYLONGSTYLE, vitalSignBean.getMeasureDateByLongStyle());
		values.put(VALUE, vitalSignBean.getValue());
		values.put(DATETYPE, datetype);
		
		Cursor cursor = db.rawQuery("select count(*) from " + TABLE_NAME + " where datetype = ? and measuredate = ?", new String[] { datetype + "", vitalSignBean.getMeasureDate() });
		cursor.moveToFirst();
		if(cursor.getInt(0) == 0){
			insert = db.insert(TABLE_NAME, null, values);
		}else{
			update = db.update(TABLE_NAME, values, "edittimebylongstyle < ? and datetype = ? and measuredate = ?", new String[] { vitalSignBean.getEditTimeByLongStyle(), datetype + "", vitalSignBean.getMeasureDate() });
		}
		Logger.d(TAG, "添加操作 =" + insert + "\n修改操作 = " + update);
	}

	// 修改/加数据
	public static void InsertValue(DatabaseHelper dbHelper, List<VitalSignInfoDataBean> listVsb, int datetype) {
		ContentValues values = new ContentValues();
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		db.beginTransaction();

		long insert = 0;
		int update = 0;
		
		for (VitalSignInfoDataBean vitalSignBean : listVsb) {
			vitalSignBean.setEditTimeByLongStyle("" + Common.getDateFromStrFromServel(vitalSignBean.getEditTime()));
			vitalSignBean.setMeasureDateByLongStyle("" + Common.getDateFromStr(vitalSignBean.getMeasureDate()));

			values.put(EDITTIMEBYLONGSTYLE, vitalSignBean.getEditTimeByLongStyle());
			values.put(EDITTIME, vitalSignBean.getEditTime());
			values.put(MEASUREDATE, vitalSignBean.getMeasureDate());
			values.put(MEASUREDATEBYLONGSTYLE, vitalSignBean.getMeasureDateByLongStyle());
			values.put(VALUE, vitalSignBean.getValue());
			values.put(DATETYPE, datetype);

			Cursor cursor = db.rawQuery("select count(*) from " + TABLE_NAME + " where datetype = ? and measuredate = ?", new String[] { datetype + "", vitalSignBean.getMeasureDate() });
			cursor.moveToFirst();
			if(cursor.getInt(0) == 0){
				insert = db.insert(TABLE_NAME, null, values);
			}else{
				update = db.update(TABLE_NAME, values, "edittimebylongstyle < ? and datetype = ? and measuredate = ?", new String[] {vitalSignBean.getEditTimeByLongStyle(), datetype + "", vitalSignBean.getMeasureDate() });
			}
			values.clear();
		}
		db.setTransactionSuccessful();
		db.endTransaction();
		Logger.d(TAG, "添加操作 =" + insert + "\n修改操作 = " + update);
	}

	public static void deleteVitalSignData(DatabaseHelper dbHelper) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		db.execSQL("DELETE FROM " + TABLE_NAME);
	}

}
