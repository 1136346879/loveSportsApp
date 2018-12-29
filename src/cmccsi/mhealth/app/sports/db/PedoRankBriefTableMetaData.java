package cmccsi.mhealth.app.sports.db;

import java.sql.Date;
import java.util.ArrayList;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;
import cmccsi.mhealth.app.sports.bean.PedoRankBriefInfo;
import cmccsi.mhealth.app.sports.bean.PedoRankDetailInfo;
import cmccsi.mhealth.app.sports.common.Logger;
import cmccsi.mhealth.app.sports.common.utils.DateFormatUtils;
import cmccsi.mhealth.app.sports.common.utils.DateFormatUtils.FormatType;

/**
 * 运动数据排名简要信息表操作类
 * 
 * @type PedoRankBriefTableMetaData TODO
 * @author shaoting.chen
 * @time 2015年3月13日下午2:40:07
 */
public final class PedoRankBriefTableMetaData implements BaseColumns {
	public static final String TABLE_NAME = "pedorank_brief_table";

	public static final String DAYCOUNT = "dayCount";
	public static final String TYPE = "type";
	public static final String LEVEL = "level";

	public static final String AREANAME = "areaName";
	public static final String MEMBERNAME = "memberName";
	public static final String MEMBERRANK = "memberRank";
	public static final String MEMBERSTEP = "memberStep";

	public static final String DEFAULT_SORT_ORDER = "_id DESC";
	public static final String SORT_ORDER_ASC = "_id ASC";
	public static final String DATE = "date";
	public static final String RANKGROUP = "rankGroup";

	public static String CREATE_TABLE_SQL = "create table " + TABLE_NAME + "(" + _ID
			+ " integer primary key autoincrement," + DAYCOUNT + " text," + TYPE + " text," + LEVEL + " integer,"
			+ AREANAME + " text," + MEMBERNAME + " text," + MEMBERRANK + " integer," + MEMBERSTEP + " integer," + DATE
			+ " text," + RANKGROUP + " integer" + ")";

	public static Cursor GetAllValueCursor(DatabaseHelper dbHelper) {
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		Cursor cursor = db.query(TABLE_NAME, null, null, null, null, null, SORT_ORDER_ASC);
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
	 * @time 下午2:36:50
	 */
	public static Cursor GetValueCursor(DatabaseHelper dbHelper, String dayCount, String type, String level,
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
			cursor = db.query(TABLE_NAME, null, selection, selectionArg, null, null, LEVEL + " asc");
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}

		return cursor;
	}

	/**
	 * 获取指定的运动排名信息
	 * 
	 * @param dbHelper
	 * @param dayCount
	 * @param type
	 * @param level
	 * @return
	 * @return PedoRankBriefInfo
	 * @author shaoting.chen
	 * @time 下午12:18:12
	 */
	public static PedoRankBriefInfo GetPedoRankBriefInfo(DatabaseHelper dbHelper, String dayCount, String type,
			String level, String rankGroup) {
		PedoRankBriefInfo tempdata = new PedoRankBriefInfo();
		Cursor cursor;
		try {
			cursor = GetValueCursor(dbHelper, dayCount, type, level, rankGroup);
			if (cursor != null && cursor.moveToNext()) {
				tempdata = GetPedoRankBriefInfoFromCursor(cursor);
				ArrayList<PedoRankDetailInfo> pedoDetailList = PedoRankDetailTableMetaData.GetAllPedoRankDetailInfo(
						dbHelper, tempdata.dayCount, tempdata.type, String.valueOf(tempdata.level), rankGroup);
				tempdata.setRankList(pedoDetailList);
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}

		return tempdata;
	}

	/**
	 * 获取指定运动数据排名信息
	 * 
	 * @param dbHelper
	 * @param dayCount
	 * @param type
	 * @return
	 * @return ArrayList<PedoRankBriefInfo>
	 * @author shaoting.chen
	 * @time 下午12:23:30
	 */
	public static ArrayList<PedoRankBriefInfo> GetAllPedoRankBriefInfo(DatabaseHelper dbHelper, String dayCount,
			String type, String rankGroup) {
		ArrayList<PedoRankBriefInfo> pedoRankList = new ArrayList<PedoRankBriefInfo>();
		try {
			Cursor cursor = GetAllValueCursor(dbHelper, dayCount, type, rankGroup);
			while (cursor != null && cursor.moveToNext()) {
				PedoRankBriefInfo tempdata = GetPedoRankBriefInfoFromCursor(cursor);
				ArrayList<PedoRankDetailInfo> pedoDetailList = PedoRankDetailTableMetaData.GetAllPedoRankDetailInfo(
						dbHelper, tempdata.dayCount, tempdata.type, String.valueOf(tempdata.level), rankGroup);
				tempdata.setRankList(pedoDetailList);

				pedoRankList.add(tempdata);
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}

		return pedoRankList;
	}

	public static PedoRankBriefInfo GetPedoRankBriefInfoFromCursor(Cursor cursor) {
		PedoRankBriefInfo pedoRankBriefInfo = new PedoRankBriefInfo();

		pedoRankBriefInfo._id = cursor.getInt(cursor.getColumnIndex(_ID));
		pedoRankBriefInfo.level = cursor.getInt(cursor.getColumnIndex(LEVEL));
		pedoRankBriefInfo.dayCount = cursor.getString(cursor.getColumnIndex(DAYCOUNT));
		pedoRankBriefInfo.type = cursor.getString(cursor.getColumnIndex(TYPE));
		pedoRankBriefInfo.areaName = cursor.getString(cursor.getColumnIndex(AREANAME));
		pedoRankBriefInfo.membername = cursor.getString(cursor.getColumnIndex(MEMBERNAME));
		pedoRankBriefInfo.memberrank = cursor.getInt(cursor.getColumnIndex(MEMBERRANK));
		pedoRankBriefInfo.memberstep = cursor.getInt(cursor.getColumnIndex(MEMBERSTEP));
		pedoRankBriefInfo.date = cursor.getString(cursor.getColumnIndex(DATE));
		pedoRankBriefInfo.rankGroup = cursor.getInt(cursor.getColumnIndex(RANKGROUP));
		return pedoRankBriefInfo;
	}

	/**
	 * 更新数据 TODO
	 * 
	 * @param dbHelper
	 * @param pedoRank
	 * @return
	 * @return boolean（the number of rows affected ）
	 * @author shaoting.chen
	 * @time 下午12:43:07
	 */
	public static boolean UpdateValue(DatabaseHelper dbHelper, PedoRankBriefInfo pedoRank) {
		ContentValues values = new ContentValues();

		values.put(DAYCOUNT, pedoRank.dayCount);
		values.put(TYPE, pedoRank.type);
		values.put(LEVEL, pedoRank.level);
		values.put(AREANAME, pedoRank.areaName);
		values.put(MEMBERNAME, pedoRank.membername);
		values.put(MEMBERRANK, pedoRank.memberrank);
		values.put(MEMBERSTEP, pedoRank.memberstep);
		values.put(DATE, pedoRank.date);
		values.put(RANKGROUP, pedoRank.rankGroup);

		return dbHelper.getWritableDatabase().update(TABLE_NAME, values, _ID + "=" + pedoRank._id, null) > 0;
	}

	public static void InsertValue(SQLiteDatabase db, PedoRankBriefInfo pedoRank, String dayCount, String type,
			String rankGroup) {
		Logger.i("insert----", pedoRank.membername + pedoRank.memberstep);
		ContentValues values = new ContentValues();

		values.put(DAYCOUNT, dayCount);
		values.put(TYPE, type);
		values.put(LEVEL, pedoRank.level);
		values.put(AREANAME, pedoRank.areaName);
		values.put(MEMBERNAME, pedoRank.membername);
		values.put(MEMBERRANK, pedoRank.memberrank);
		values.put(MEMBERSTEP, pedoRank.memberstep);
		values.put(RANKGROUP, rankGroup);
		// 插入当天日期
		values.put(DATE, DateFormatUtils.DateToString(new Date(System.currentTimeMillis()), FormatType.DateShot));

		db.insert(TABLE_NAME, null, values);
	}

	public static void InsertPedoRankBriefList(DatabaseHelper dbHelper, ArrayList<PedoRankBriefInfo> pedoRankList,
			String dayCount, String type, String rankGroup) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		if (pedoRankList.size() > 0) {
			for (int i = 0; i < pedoRankList.size(); i++) {
				InsertValue(db, pedoRankList.get(i), dayCount, type, rankGroup);
				if (pedoRankList.get(i).rankList.size() > 0) {
					PedoRankDetailTableMetaData.InsertPedoRankDetailList(db, pedoRankList.get(i).rankList, dayCount,
							type, rankGroup, pedoRankList.get(i).level);
				}
			}
		}
	}

	/**
	 * 判断是否已经更新了当天的运动排名数据 TODO
	 * 
	 * @param dbHelper
	 * @param dayCount
	 * @param type
	 * @param level
	 * @return
	 * @return int 0-已更新 1-未更新
	 * @author shaoting.chen
	 * @time 下午4:59:13
	 */
	public static int CheckIsUpdateDate(DatabaseHelper dbHelper, String dayCount, String type, String level,
			String rankGroup) {

		int flag = 1;
		Cursor cursor = GetValueCursor(dbHelper, dayCount, type, level, rankGroup);
		if (cursor != null && cursor.moveToNext()) {
			PedoRankBriefInfo pedoBrief = GetPedoRankBriefInfoFromCursor(cursor);
			String nowDate = DateFormatUtils.DateToString(new Date(System.currentTimeMillis()), FormatType.DateShot);
			if (pedoBrief.date.equals(nowDate)) {
				flag = 0;
			}
		}

		return flag;
	}

	public static void DeleteData(DatabaseHelper dbHelper, String id) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		db.execSQL("DELETE FROM " + TABLE_NAME + " WHERE _id=" + id);
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
			e.printStackTrace();
		}

	}
}
