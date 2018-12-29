package cmccsi.mhealth.app.sports.db;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;
import cmccsi.mhealth.app.sports.bean.RequestData;

public final class HistroyMessageMetaData implements BaseColumns {
	public static final String TABLE_NAME = "old_msgs";

	public static final String REQUESTTYPE = "requesttype";
	public static final String PHONENUM = "phonenum";
	public static final String NAME = "name";
	public static final String MSG = "msg";
	public static final String RACEID = "raceid";
	public static final String RACETYPE = "racetype";
	public static final String SAVEDTIME = "savedtime";
	public static final String AVATAR = "avatar";

	public static String CREATE_TABLE_SQL = "create table " + TABLE_NAME + "(" + _ID + " integer primary key autoincrement," 
			+ REQUESTTYPE + " text,"
			+ PHONENUM + " text,"
			+ NAME + " text," 
			+ MSG + " text," 
			+ RACEID + " text,"
			+ AVATAR + " text,"
			+ SAVEDTIME + " bigint,"
			+ RACETYPE + " text" + ")";

	public static String DELETE_TABLE_SQL = "drop table  IF EXISTS " + TABLE_NAME;

	public static void CreateTable(DatabaseHelper dbHelper) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		db.execSQL(DELETE_TABLE_SQL);
		db.execSQL(CREATE_TABLE_SQL);
	}

	public static void OldMsgsInsertValue(DatabaseHelper dbHelper, List<RequestData> oldmsgs) {
		ContentValues values = new ContentValues();
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		db.beginTransaction();
		for (RequestData rd : oldmsgs) {
			values.put(REQUESTTYPE, rd.requesttype);
			values.put(PHONENUM, rd.phonenum);
			values.put(NAME, rd.name);
			values.put(MSG, rd.msg);
			values.put(RACEID, rd.raceid);
			values.put(RACETYPE, rd.racetype);
			values.put(AVATAR, rd.avatar);
			values.put(SAVEDTIME, new Date().getTime() + "");
			db.insert(TABLE_NAME, null, values);
			values.clear();
		}
		db.setTransactionSuccessful();
		db.endTransaction();
	}

	public static void OldMsgsInsertValue(DatabaseHelper dbHelper, RequestData rd) {
		ContentValues values = new ContentValues();
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		values.put(REQUESTTYPE, rd.requesttype);
		values.put(PHONENUM, rd.phonenum);
		values.put(NAME, rd.name);
		values.put(MSG, rd.msg);
		values.put(RACEID, rd.raceid);
		values.put(RACETYPE, rd.racetype);
		values.put(AVATAR, rd.avatar);
		values.put(SAVEDTIME, new Date().getTime() + "");
		db.insert(TABLE_NAME, null, values);
	}

	public static List<RequestData> getMyOldMsgs(DatabaseHelper dbHelper) {
		Cursor cursor = null;
		List<RequestData> mList = null;
		try {
			SQLiteDatabase db = dbHelper.getReadableDatabase();
			cursor = db.query(TABLE_NAME, null, null, null, null, null, SAVEDTIME + " desc");
			mList = new ArrayList<RequestData>();
			while (cursor.moveToNext()) {
				RequestData rd = new RequestData();
				String requesttype = cursor.getString(cursor.getColumnIndex(HistroyMessageMetaData.REQUESTTYPE));
				String phonenum = cursor.getString(cursor.getColumnIndex(HistroyMessageMetaData.PHONENUM));
				String name = cursor.getString(cursor.getColumnIndex(HistroyMessageMetaData.NAME));
				String msg = cursor.getString(cursor.getColumnIndex(HistroyMessageMetaData.MSG));
				String raceid = cursor.getString(cursor.getColumnIndex(HistroyMessageMetaData.RACEID));
				String racetype = cursor.getString(cursor.getColumnIndex(HistroyMessageMetaData.RACETYPE));
				String avatar = cursor.getString(cursor.getColumnIndex(HistroyMessageMetaData.AVATAR));
				rd.requesttype = requesttype;
				rd.phonenum = phonenum;
				rd.name = name;
				rd.msg = msg;
				rd.raceid = raceid;
				rd.racetype = racetype;
				rd.avatar = avatar;
				rd.isoldmsgs = true;
				mList.add(rd);
			}
		} finally {
			if (cursor != null)
				cursor.close();
		}
		return mList;
	}

	public static void DeleteMyOldMsgs(DatabaseHelper dbHelper) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		db.execSQL("DELETE FROM " + TABLE_NAME);
	}

}
