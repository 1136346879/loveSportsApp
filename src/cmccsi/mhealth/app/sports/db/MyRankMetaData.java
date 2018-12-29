package cmccsi.mhealth.app.sports.db;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;
import cmccsi.mhealth.app.sports.bean.GroupInfo;
import cmccsi.mhealth.app.sports.bean.GroupMemberInfo;
import cmccsi.mhealth.app.sports.bean.OrgnizeMemberInfo;
import cmccsi.mhealth.app.sports.bean.RankUserBean;

public final class MyRankMetaData implements BaseColumns {
	public static final String TABLE_NAME = "my_rank";

	public static final String MYRANKID = "myrankid";
	public static final String MEMBERSEQ = "memberseq";
	public static final String MEMBERNAME = "membername";
	public static final String GROUPNAME = "groupname";
	public static final String MEMBER7AVGSTEP = "member7avgstep";
	public static final String CLUBID = "clubid";
	public static final String TYPE = "type";
	public static final String IMAGEURL = "imageurl";

	public static String CREATE_TABLE_SQL = "create table " + TABLE_NAME + "(" + _ID + " integer primary key autoincrement,"
			+ MYRANKID + " text,"
			+ MEMBERSEQ + " integer," 
			+ MEMBERNAME + " text,"
			+ GROUPNAME + " text,"
			+ CLUBID + " text,"
			+ MEMBER7AVGSTEP + " text,"
			+ TYPE + " text,"
			+ IMAGEURL + " text"		
			+ ")";

	public static String DELETE_TABLE_SQL = "drop table  IF EXISTS " + TABLE_NAME;

	public static void CreateTable(DatabaseHelper dbHelper) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		db.execSQL(DELETE_TABLE_SQL);
		db.execSQL(CREATE_TABLE_SQL);
	}
	
	public static void MyRankInsertValue(DatabaseHelper dbHelper, String memberseq, String membername,
			String groupname, String member7avgstep, String type, String imageurl, int clubid) {
		ContentValues values = new ContentValues();

		values.put(MYRANKID, "");// UUID.randomUUID().toString()
		values.put(MEMBERSEQ, memberseq);
		values.put(MEMBERNAME, membername);
		values.put(GROUPNAME, groupname);
		values.put(MEMBER7AVGSTEP, member7avgstep);
		values.put(CLUBID, clubid);
		values.put(TYPE, type);
		values.put(IMAGEURL, imageurl);
		
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		db.insert(TABLE_NAME, null, values);
	}
	public static void MyRankInsertValue(DatabaseHelper dbHelper, List<OrgnizeMemberInfo> orgnizemember ,String type , int clubid) {
		ContentValues values = new ContentValues();
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		db.beginTransaction();
		for (OrgnizeMemberInfo orgnizeMemberInfo : orgnizemember) {
			values.put(MYRANKID, "");// UUID.randomUUID().toString()
			values.put(MEMBERSEQ, orgnizeMemberInfo.memberseq);
			values.put(MEMBERNAME, orgnizeMemberInfo.membername);
			values.put(GROUPNAME, orgnizeMemberInfo.groupname);
			values.put(MEMBER7AVGSTEP, orgnizeMemberInfo.member7avgstep);
			values.put(CLUBID, clubid);
			values.put(TYPE, type);
			values.put(IMAGEURL, orgnizeMemberInfo.avatar);
			db.insert(TABLE_NAME, null, values);
			values.clear();
		}
		db.setTransactionSuccessful();
		db.endTransaction(); 
	}
	public static void MyRankInsertValueGP(DatabaseHelper dbHelper, List<GroupMemberInfo> groupmember ,String type , int clubid) {
		ContentValues values = new ContentValues();
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		db.beginTransaction();
		for (GroupMemberInfo orgnizeMemberInfo : groupmember) {
			values.put(MYRANKID, "");// UUID.randomUUID().toString()
			values.put(MEMBERSEQ, orgnizeMemberInfo.memberseq);
			values.put(MEMBERNAME, orgnizeMemberInfo.membername);
			values.put(GROUPNAME, orgnizeMemberInfo.membername);
			values.put(MEMBER7AVGSTEP, orgnizeMemberInfo.member7avgstep);
			values.put(CLUBID, clubid);
			values.put(TYPE, type);
			values.put(IMAGEURL, orgnizeMemberInfo.avatar);
			db.insert(TABLE_NAME, null, values);
			values.clear();
		}
		db.setTransactionSuccessful();
		db.endTransaction(); 
	}
	public static void MyRankInsertValueGroup(DatabaseHelper dbHelper, List<GroupInfo> grouppkdata,String type , int clubid) {
		ContentValues values = new ContentValues();
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		db.beginTransaction();
		for (GroupInfo groupInfo : grouppkdata) {
			values.put(MYRANKID, "");// UUID.randomUUID().toString()
			values.put(MEMBERSEQ, groupInfo.groupseq);
			values.put(MEMBERNAME, groupInfo.groupname);
			values.put(GROUPNAME, groupInfo.groupname);
			values.put(MEMBER7AVGSTEP, groupInfo.group7avgstep);
			values.put(CLUBID, clubid);
			values.put(TYPE, type);
			values.put(IMAGEURL, "");
			db.insert(TABLE_NAME, null, values);
			values.clear();
		}
		db.setTransactionSuccessful();
		db.endTransaction(); 
	}
	
	/**
	 * 获取指定类型的所有数据
	 * @param dbHelper
	 * @param type  1 = 一天的           7 = 七天的
	 * @return
	 */
	public static List<RankUserBean> getMyRankByType(DatabaseHelper dbHelper,int type , int clubid){
		Cursor cursor = null;
		List<RankUserBean> mList = null;
		try {
			SQLiteDatabase db = dbHelper.getReadableDatabase();
			cursor = db.query(TABLE_NAME, null, "TYPE = ? and clubid = ?", new String[] { type + "", clubid + "" }, null, null, MEMBERSEQ + " asc");
			mList = new ArrayList<RankUserBean>();
			while (cursor.moveToNext()) {
				RankUserBean mrb = new RankUserBean();
				String myrankid = cursor.getString(cursor.getColumnIndex(MyRankMetaData.MYRANKID));
				String memberseq = cursor.getString(cursor.getColumnIndex(MyRankMetaData.MEMBERSEQ));
				String membername = cursor.getString(cursor.getColumnIndex(MyRankMetaData.MEMBERNAME));
				String groupname = cursor.getString(cursor.getColumnIndex(MyRankMetaData.GROUPNAME));
				String member7avgstep = cursor.getString(cursor.getColumnIndex(MyRankMetaData.MEMBER7AVGSTEP));
				String type1 = cursor.getString(cursor.getColumnIndex(MyRankMetaData.TYPE));
				String imageurl = cursor.getString(cursor.getColumnIndex(MyRankMetaData.IMAGEURL));
				mrb.setMyrankid(myrankid);
				mrb.setMemberseq(memberseq);
				mrb.setMembername(membername);
				mrb.setGroupname(groupname);
				mrb.setMember7avgstep(member7avgstep);
				mrb.setType(type1);
				mrb.setImageurl(imageurl);
				mList.add(mrb);
			}
		} finally {
			if (cursor != null)
				cursor.close();
		}
		return mList;
	}

	public static void deleteMyRankData(DatabaseHelper dbHelper , int clubid) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		db.execSQL("DELETE FROM " + TABLE_NAME + " WHERE " + CLUBID + " = " + clubid);
		 
	}
	public static void deleteMyRankData(DatabaseHelper dbHelper) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		db.execSQL("DELETE FROM " + TABLE_NAME);
		
	}

}
