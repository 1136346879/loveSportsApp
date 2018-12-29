package cmccsi.mhealth.app.sports.db;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;
import cmccsi.mhealth.app.sports.bean.OrgnizeMemberInfo;

public final class FriendMetaData implements BaseColumns {
	public static final String TABLE_NAME = "my_friend";

	public static final String MYRANKID = "myrankid";
	public static final String MEMBERSEQ = "memberseq";
	public static final String FRIENDPHONE = "friendphone";
	public static final String SEX = "sex";
	public static final String MEMBERNAME = "membername";
	public static final String GROUPNAME = "groupname";
	public static final String MEMBER7AVGSTEP = "member7avgstep";
	public static final String IMAGEURL = "imageurl";

	public static String CREATE_TABLE_SQL = "create table " + TABLE_NAME + "(" + _ID + " integer primary key autoincrement," + MEMBERSEQ + " integer,"
			+ SEX + " integer,"
			+ MEMBERNAME + " text," 
			+ FRIENDPHONE + " text," 
			+ GROUPNAME + " text,"
			+ MEMBER7AVGSTEP + " text,"
			+ IMAGEURL + " text" + ")";

	public static String DELETE_TABLE_SQL = "drop table  IF EXISTS " + TABLE_NAME;

	public static void CreateTable(DatabaseHelper dbHelper) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		db.execSQL(DELETE_TABLE_SQL);
		db.execSQL(CREATE_TABLE_SQL);
	}

	public static int getFriendCount(DatabaseHelper dbHelper) {
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		Cursor cursor = null;
		try {
			cursor = db.rawQuery("select count(*)from " + TABLE_NAME, null);
			cursor.moveToFirst();
			int count = cursor.getInt(0);
			return count;
		} finally {
			if (cursor != null)
				cursor.close();
		}
	}

	public static void MyFriendInsertValue(DatabaseHelper dbHelper, List<OrgnizeMemberInfo> friendinfos, String type) {
		ContentValues values = new ContentValues();
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		db.beginTransaction();
		for (OrgnizeMemberInfo orgnizeMemberInfo : friendinfos) {
			values.put(MEMBERSEQ, orgnizeMemberInfo.memberseq);
			values.put(MEMBERNAME, orgnizeMemberInfo.membername);
			values.put(SEX, orgnizeMemberInfo.memberinforev1);
			values.put(FRIENDPHONE, orgnizeMemberInfo.friendphone);
			values.put(GROUPNAME, orgnizeMemberInfo.groupname);
			values.put(MEMBER7AVGSTEP, orgnizeMemberInfo.member7avgstep);
			values.put(IMAGEURL, orgnizeMemberInfo.avatar);
			db.insert(TABLE_NAME, null, values);
			values.clear();
		}
		db.setTransactionSuccessful();
		db.endTransaction();
	}

	/**
	 * 获取指定类型的所有数据
	 * 
	 * @param dbHelper
	 * @param type
	 *            1 = 一天的 7 = 七天的
	 * @return
	 */
	public static List<OrgnizeMemberInfo> getMyFriends(DatabaseHelper dbHelper) {
		Cursor cursor = null;
		List<OrgnizeMemberInfo> mList = null;
		try {
			SQLiteDatabase db = dbHelper.getReadableDatabase();
			cursor = db.query(TABLE_NAME, null, null, null, null, null, MEMBERSEQ + " asc");
			mList = new ArrayList<OrgnizeMemberInfo>();
			while (cursor.moveToNext()) {
				OrgnizeMemberInfo mrb = new OrgnizeMemberInfo();
				String memberseq = cursor.getString(cursor.getColumnIndex(FriendMetaData.MEMBERSEQ));
				String membername = cursor.getString(cursor.getColumnIndex(FriendMetaData.MEMBERNAME));
				String groupname = cursor.getString(cursor.getColumnIndex(FriendMetaData.GROUPNAME));
				String member7avgstep = cursor.getString(cursor.getColumnIndex(FriendMetaData.MEMBER7AVGSTEP));
				String imageurl = cursor.getString(cursor.getColumnIndex(FriendMetaData.IMAGEURL));
				String friendphone = cursor.getString(cursor.getColumnIndex(FriendMetaData.FRIENDPHONE));
				String sex = cursor.getString(cursor.getColumnIndex(FriendMetaData.SEX));
				mrb.memberseq = memberseq;
				mrb.memberinforev1 = sex;
				mrb.membername = membername;
				mrb.friendphone = friendphone;
				mrb.groupname = groupname;
				mrb.avatar = imageurl;
				mrb.member7avgstep = member7avgstep;
				mList.add(mrb);
			}
		} finally {
			if (cursor != null)
				cursor.close();
		}
		return mList;
	}

	public static void DeleteMyFriendData(DatabaseHelper dbHelper) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		db.execSQL("DELETE FROM " + TABLE_NAME);
	}

	public static void DeleteMyFriendData(DatabaseHelper dbHelper, String friendphone) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		db.execSQL("DELETE FROM " + TABLE_NAME + " where friendphone =" + friendphone);
	}

}
