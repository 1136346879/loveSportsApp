package cmccsi.mhealth.app.sports.db;

import java.sql.Date;
import java.util.ArrayList;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;
import cmccsi.mhealth.app.sports.bean.PedoRankDetailInfo;
import cmccsi.mhealth.app.sports.common.Logger;
import cmccsi.mhealth.app.sports.common.utils.DateFormatUtils;
import cmccsi.mhealth.app.sports.common.utils.DateFormatUtils.FormatType;

/**
 * 运动数据排名详细100名信息操作类
 * 
 * @type PedoRankDetailTableMetaData TODO
 * @author shaoting.chen
 * @time 2015年3月13日下午2:40:53
 */
public final class PedoRankDetailTableMetaData implements BaseColumns {
	public static final String TABLE_NAME = "pedorank_detail_table";

	public static final String DAYCOUNT = "dayCount";
	public static final String TYPE = "type";
	public static final String LEVEL = "level";

	public static final String RANK = "rank";
	public static final String NAME = "name";
	public static final String GROUP = "myGroup";
	public static final String STEP = "step";
	public static final String RANKGROUP = "rankGroup";
	public static final String DATE = "date";

	public static final String DEFAULT_SORT_ORDER = "_id DESC";
	public static final String SORT_ORDER_ASC = "_id ASC";

	public static String CREATE_TABLE_SQL = "create table " + TABLE_NAME + "(" + _ID
			+ " integer primary key autoincrement," + DAYCOUNT + " text," + TYPE + " text," + LEVEL + " integer,"
			+ RANK + " integer," + NAME + " text," + GROUP + " text," + STEP + " integer," + DATE
			+ " text," + RANKGROUP + " integer"
			+ ")";

	public static Cursor GetAllValueCursor(DatabaseHelper dbHelper) {
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		Cursor cursor = db.query(TABLE_NAME, null, null, null, null, null, DEFAULT_SORT_ORDER);
		return cursor;
	}

	/**
	 * 获取指定的运动排名信息 TODO
	 * 
	 * @param dbHelper
	 * @param dayCount
	 * @param type
	 * @param level
	 * @return
	 * @return Cursor
	 * @author shaoting.chen
	 * @time 下午2:36:06
	 */
	public static Cursor GetAllValueCursor(DatabaseHelper dbHelper, String dayCount, String type, String level,
			String rankGroup) {
		Cursor cursor = null;
		try {
			SQLiteDatabase db = dbHelper.getReadableDatabase();
			String selection = DAYCOUNT + " = ? and " + TYPE + " = ? and " + LEVEL + " = ? and " + RANKGROUP + " = ?";
			String[] selectionArg = { dayCount, type, level, rankGroup };
			cursor = db.query(TABLE_NAME, null, selection, selectionArg, null, null, SORT_ORDER_ASC);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}

		return cursor;
	}

	public static Cursor GetAllValueCursor(DatabaseHelper dbHelper, String dayCount, String type, String rankGroup) {
		Cursor cursor = null;
		try {
			SQLiteDatabase db = dbHelper.getReadableDatabase();
			String selection = DAYCOUNT + " = ? and " + TYPE + " = ? and " + RANKGROUP + " = ?";
			String[] selectionArg = { dayCount, type, rankGroup };
			cursor = db.query(TABLE_NAME, null, selection, selectionArg, null, null, SORT_ORDER_ASC);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}

		return cursor;
	}

	/**
	 * 获取指定的运动排名信息 TODO
	 * 
	 * @param dbHelper
	 * @param dayCount
	 * @param type
	 * @param level
	 * @return
	 * @return Cursor
	 * @author shaoting.chen
	 * @time 下午2:36:06
	 */
	public static ArrayList<PedoRankDetailInfo> GetAllPedoRankDetailInfo(DatabaseHelper dbHelper, String dayCount,
			String type, String level, String rankGroup) {

		Cursor cursor = GetAllValueCursor(dbHelper, dayCount, type, level, rankGroup);
		ArrayList<PedoRankDetailInfo> pedoList = new ArrayList<PedoRankDetailInfo>();
		while (cursor != null && cursor.moveToNext()) {
			PedoRankDetailInfo tempdata = GetPedoRankDetailInfoFromCursor(cursor);
			pedoList.add(tempdata);
		}
		return pedoList;
	}

	public static PedoRankDetailInfo GetPedoRankDetailInfoFromCursor(Cursor cursor) {
		PedoRankDetailInfo pedoRankDetailInfo = new PedoRankDetailInfo();

		pedoRankDetailInfo._id = cursor.getInt(cursor.getColumnIndex(_ID));
		pedoRankDetailInfo.level = cursor.getInt(cursor.getColumnIndex(LEVEL));
		pedoRankDetailInfo.dayCount = cursor.getString(cursor.getColumnIndex(DAYCOUNT));
		pedoRankDetailInfo.type = cursor.getString(cursor.getColumnIndex(TYPE));
		pedoRankDetailInfo.rank = cursor.getInt(cursor.getColumnIndex(RANK));
		pedoRankDetailInfo.name = cursor.getString(cursor.getColumnIndex(NAME));
		pedoRankDetailInfo.group = cursor.getString(cursor.getColumnIndex(GROUP));
		pedoRankDetailInfo.step = cursor.getInt(cursor.getColumnIndex(STEP));
		pedoRankDetailInfo.date = cursor.getString(cursor.getColumnIndex(DATE));
		pedoRankDetailInfo.rankGroup = cursor.getInt(cursor.getColumnIndex(RANKGROUP));
		return pedoRankDetailInfo;
	}

	public static boolean UpdateValue(DatabaseHelper dbHelper, PedoRankDetailInfo pedoRank) {
		ContentValues values = new ContentValues();

		values.put(DAYCOUNT, pedoRank.dayCount);
		values.put(TYPE, pedoRank.type);
		values.put(LEVEL, pedoRank.level);
		values.put(RANK, pedoRank.rank);
		values.put(NAME, pedoRank.name);
		values.put(GROUP, pedoRank.group);
		values.put(STEP, pedoRank.step);
		values.put(DATE, pedoRank.date);
		values.put(RANKGROUP, pedoRank.rankGroup);

		return dbHelper.getWritableDatabase().update(TABLE_NAME, values, _ID + "=" + pedoRank._id, null) > 0;
	}

	public static void InsertValue(SQLiteDatabase db, PedoRankDetailInfo pedoRank, String dayCount, String type,
			String rankGroup, int level) {
		ContentValues values = new ContentValues();

		values.put(DAYCOUNT, dayCount);
		values.put(TYPE, type);
		values.put(LEVEL, level);
		values.put(RANK, pedoRank.rank);
		values.put(NAME, pedoRank.name);
		values.put(GROUP, pedoRank.group);
		values.put(STEP, pedoRank.step);
		values.put(RANKGROUP, rankGroup);
		// 插入当天日期
		values.put(DATE, DateFormatUtils.DateToString(new Date(System.currentTimeMillis()), FormatType.DateShot));

		db.insert(TABLE_NAME, null, values);
	}

	public static void InsertPedoRankDetailList(SQLiteDatabase db, ArrayList<PedoRankDetailInfo> pedoList,
			String dayCount, String type, String rankGroup, int level) {
		if (pedoList.size() > 0) {
			for (int i = 0; i < pedoList.size(); i++) {
				InsertValue(db, pedoList.get(i), dayCount, type, rankGroup, level);
			}
		}
	}

	public static void DeleteData(DatabaseHelper dbHelper, String id) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		db.execSQL("DELETE FROM " + TABLE_NAME + " WHERE " + _ID + "=" + id);
	}

	public static void ClearData(DatabaseHelper dbHelper) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		db.execSQL("DELETE FROM " + TABLE_NAME);
	}

	public static void ClearData(DatabaseHelper dbHelper, String dayCount, String type, String rankGroup) {
		try {
			// Cursor cursor = GetAllValueCursor(dbHelper, dayCount, type,
			// rankGroup);
			// if(cursor != null && cursor.moveToNext()){
			SQLiteDatabase db = dbHelper.getWritableDatabase();
			String sql = "DELETE FROM " + TABLE_NAME + " WHERE rankGroup = " + rankGroup + " and dayCount = " + dayCount
					+ " and type = '" + type + "' and date < "
					+ DateFormatUtils.DateToString(new Date(System.currentTimeMillis()), FormatType.DateShot);
			Logger.i("----delete", sql);
			db.execSQL(sql);
			// }

		} catch (Exception e) {
			// TODO: handle exception
		}

	}
}
