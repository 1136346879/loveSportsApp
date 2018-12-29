package cmccsi.mhealth.app.sports.controller;

import java.util.ArrayList;

import android.content.Context;
import cmccsi.mhealth.app.sports.bean.PedoRankBriefInfo;
import cmccsi.mhealth.app.sports.db.DatabaseHelper;
import cmccsi.mhealth.app.sports.db.PedoRankBriefTableMetaData;
import cmccsi.mhealth.app.sports.db.PedoRankDetailTableMetaData;

/**
 * 
 * @type PedoRankBriefController TODO
 * @author shaoting.chen
 * @time 2015年3月16日上午11:09:00
 */
public class PedoRankController {
	public static final String DATABASE_NAME = "pedometer_db";
	private static PedoRankController mPedoRankController = null;
	private static DatabaseHelper mdbHelper;

	private PedoRankController(Context context) {
		mdbHelper = new DatabaseHelper(context, DATABASE_NAME);
	}

	/**
	 * 获取运动数据排名控制器单例 TODO
	 * 
	 * @param context
	 *            上下文
	 * @return PedoController
	 * @time 上午11:04:17
	 */
	public static PedoRankController GetPedoRankBriefController(Context context) {
		if (mPedoRankController == null)
			mPedoRankController = new PedoRankController(context);
		return mPedoRankController;
	}

	/**
	 * 判断是否已经更新了当天的运动排名数据 TODO
	 * 
	 * @param dayCount
	 * @param type
	 * @param level
	 * @param rankGroup
	 *            排名分类 0-区域排名 1-企业排名
	 * @return
	 * @return int 0-已更新 1-未更新
	 * @author shaoting.chen
	 * @time 上午11:04:17
	 */
	public int CheckIsUpdateDate(String dayCount, String type, String rankGroup) {

		return PedoRankBriefTableMetaData.CheckIsUpdateDate(mdbHelper, dayCount, type, "1", rankGroup);
	}

	/**
	 * 获取指定的运动排名信息 TODO
	 * 
	 * @param dayCount
	 * @param type
	 * @param level
	 * @param rankGroup
	 *            排名分类 0-区域排名 1-企业排名
	 * @return
	 * @return ArrayList<PedoRankBriefInfo>
	 * @author shaoting.chen
	 * @time 上午11:36:45
	 */
	public PedoRankBriefInfo GetPedoRankBriefInfo(String dayCount, String type, String level, String rankGroup) {

		return PedoRankBriefTableMetaData.GetPedoRankBriefInfo(mdbHelper, dayCount, type, level, rankGroup);
	}

	/**
	 * 获取指定的运动排名信息 TODO
	 * 
	 * @param dayCount
	 * @param type
	 * @param rankGroup
	 *            排名分类 0-区域排名 1-企业排名
	 * @return
	 * @return ArrayList<PedoRankListInfo>
	 * @author shaoting.chen
	 * @time 上午11:36:32
	 */
	public ArrayList<PedoRankBriefInfo> GetAllPedoRankBriefInfo(String dayCount, String type, String rankGroup) {

		return PedoRankBriefTableMetaData.GetAllPedoRankBriefInfo(mdbHelper, dayCount, type, rankGroup);
	}

	/**
	 * 插入数据 TODO
	 * 
	 * @param pedoRankList
	 * @param rankGroup
	 *            排名分类 0-区域排名 1-企业排名
	 * @return void
	 * @author shaoting.chen
	 * @time 上午11:04:17
	 */
	public void InsertPedoRankBriefList(ArrayList<PedoRankBriefInfo> pedoRankList, String dayCount, String type, String rankGroup) {
		//插入前先清空旧数据
		ClearData(dayCount, type, rankGroup);
		PedoRankBriefTableMetaData.InsertPedoRankBriefList(mdbHelper, pedoRankList, dayCount, type, rankGroup);
	}

	/**
	 * 删除排名数据
	 * 
	 * @param rankGroup
	 *            排名分类 0-区域排名 1-企业排名
	 * @return void
	 * @author shaoting.chen
	 * @time 下午5:18:08
	 */
	public void ClearData(String dayCount, String type, String rankGroup) {
		// 先清空表数据（只留最近一次的处理数据）
		PedoRankBriefTableMetaData.ClearData(mdbHelper, dayCount, type, rankGroup);
		PedoRankDetailTableMetaData.ClearData(mdbHelper, dayCount, type, rankGroup);
	}

}
