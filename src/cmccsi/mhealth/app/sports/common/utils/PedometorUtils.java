package cmccsi.mhealth.app.sports.common.utils;

import cmccsi.mhealth.app.sports.common.Constants;

/**
 * 卡路里 转换 食物 算法类
 * 
 * @type PedometorUtils
 * TODO
 * @author shaoting.chen
 * @time 2015年3月11日下午12:19:06
 */
public class PedometorUtils {
	
	private static final int CALORIE_CANDY = 25; // 糖果
	private static final int CALORIE_CHOCOLATES = 75; // 巧克力
	private static final int CALORIE_CHICHEN_LEG = 390; // 鸡腿
	private static final int CALORIE_PIZZA = 832; // 披萨

	private static final int TYPE_NULL = 0;
	private static final int TYPE_CANDY = 1; // 糖果
	private static final int TYPE_CHOCOLATES = 2; // 巧克力
	private static final int TYPE_CHICHEN_LEG = 3; // 鸡腿
	private static final int TYPE_PIZZA = 4; // 披萨

	private static int toFoodType = TYPE_NULL; // 食物种类(默认没有)
	private static int toCount = 0; // 食物数量
	private static boolean toHaveHalf = false; // 是否有半个的食物

	/**
	 * 获取食物类型
	 * @param calorie
	 * @return 0-无 1-糖果 2-巧克力 3-鸡腿 4-披萨
	 */
	public static int getFoodType(int calorie) {
		calorieToFood(calorie);
		return toFoodType;
	}

	/**
	 * 获取卡路里对应食物数量
	 * @param calorie
	 * @return 返回双精度数值
	 */
	public static double getFoodCount(int calorie) {
		calorieToFood(calorie);
		double result = toCount;
		if (toHaveHalf) {
			result += 0.5;
		}
		return result;
	}
	
	/**
	 * 获取卡路里成就控件进度
	 * TODO
	 * @param calorie
	 * @return
	 * @return int
	 * @author jiazhi.cao
	 * @time 下午8:36:48
	 */
	public static int getCalpersent(int[] CoordinateX,int calorie)
	{
		//考虑不周只能写死一点了
		//对应卡路里成就节点百分数
		int[] calpersent=new int[CoordinateX.length+1];
		calpersent[0]=0;
		for (int i=0;i< CoordinateX.length;i++) {
			calpersent[i+1]=CoordinateX[i];
		}
		int foodtype=getFoodType(calorie);
		int basepersent=calpersent[foodtype];//基本食物百分比
		double foodcount=getFoodCount(calorie);//食物数量

//		if(foodtype==0)
//		{
//			return basepersent;
//		}
		
		if(foodcount>1)//超过节点
		{
			if(foodtype==4)
			{
				return 98;
			}
			else
			{
				int basecal=getBaseFoodCal(foodtype);
				int prebasecal=getBaseFoodCal(foodtype+1);
				int persentdiff=calpersent[foodtype+1]-calpersent[foodtype];
				//（实际卡-节点卡）/(卡差)*节点间长度 
				double temp=(float)(calorie-basecal)/(float)(prebasecal-basecal);
				double ddiff=temp*persentdiff;
				int diff=(int)ddiff;
				
				if(diff<0)
				{
					return basepersent+10;
					
				}
				else
				{
					return basepersent+diff;
				}
			}
		}
		else if(foodcount==1)//正好在节点上
		{
			return basepersent;
		}
		else//半个
		{
			
			
			int basecal=0;
			int prebasecal=0;
			int persentdiff=0;
			if(foodtype==0)
			{
				basecal=getBaseFoodCal(1);
				persentdiff=calpersent[1]-calpersent[0];
			}
			else
			{
				basepersent=calpersent[foodtype-1];
				basecal=getBaseFoodCal(foodtype);
				prebasecal=getBaseFoodCal(foodtype-1);
				persentdiff=calpersent[foodtype]-calpersent[foodtype-1];
			}
			double temp=(float)(calorie-prebasecal)/(float)(basecal-prebasecal);
			double ddiff=temp*persentdiff;
			int diff=(int)ddiff;
			if(foodtype==0)
			{
				return diff;
			}
			if(diff<0)
			{
				return basepersent-10;
				
			}
			else
			{
				return basepersent+diff;
			}
		}
	}

	/**
	 * 卡路里对应食物的描述
	 * 
	 */
	public static String calorieToFoodDescription(int calorie) {
		return calorieToFood(calorie);
		// System.out.println("h---"+haveHalf);
		// return getMessage(calorie, count, haveHalf, foodType);
	}

	/**
	 * 逻辑主函数
	 * 
	 * @param calorie
	 */
	private static String calorieToFood(int calorie) {
		int foodType = TYPE_NULL;
		int count = 0; // 食物数量
		boolean haveHalf = false; // 是否有半个的食物
		int calorie_tmp = calorie;
		if (calorie_tmp >= CALORIE_PIZZA) {
			foodType = TYPE_PIZZA;
			count = calorie_tmp / CALORIE_PIZZA;
			calorie_tmp %= CALORIE_PIZZA;
			if (calorie_tmp >= (CALORIE_PIZZA / 2)) {
				haveHalf = true;
			}
			return getMessage(calorie, count, haveHalf, foodType);
		} else if (calorie_tmp >= CALORIE_CHICHEN_LEG) {
			foodType = TYPE_CHICHEN_LEG;
		} else if (calorie_tmp >= CALORIE_CHOCOLATES) {
			foodType = TYPE_CHOCOLATES;
		} else if (calorie_tmp >= CALORIE_CANDY) {
			foodType = TYPE_CANDY;
		} else {
			foodType = TYPE_NULL;
			return getMessage(calorie, count, haveHalf, foodType);
		}
		return calorieToFoodMain(calorie, count, foodType, haveHalf);
	}

	/**
	 * 转换主函数
	 * 
	 * @param calorie
	 * @param foodType
	 */
	private static String calorieToFoodMain(int calorie, int count,
			int foodType, boolean haveHalf) {
		int calorie_tmp = calorie;
		if (calorie_tmp >= (getCalorieByType(foodType + 1) / 2)) {
			foodType += 1;
			count = 0;
			haveHalf = true;
			// return getMessage(count, haveHalf, foodType);
		} else if (calorie_tmp < (getCalorieByType(foodType) * 3 / 2)) {
			count = 1;
			haveHalf = false;
		} else {
			count = calorie_tmp / getCalorieByType(foodType);
			calorie_tmp %= getCalorieByType(foodType);
			if (calorie_tmp >= (getCalorieByType(foodType) / 2)) {
				haveHalf = true;
			}
		}
		return getMessage(calorie, count, haveHalf, foodType);
	}

	/**
	 * 对食物的描述
	 * 
	 * @param count
	 * @param haveHalf
	 * @param foodType
	 * @return
	 */
	private static String getMessage(int calorie, int count, boolean haveHalf,
			int foodType) {
		
		toFoodType = foodType;
		toCount = count;
		toHaveHalf = haveHalf;
		
		// TODO Auto-generated method stub
		String unit = "颗";
		if (foodType == TYPE_CHOCOLATES) {
			unit = "块";
		} else if (foodType == TYPE_CHICHEN_LEG) {
			unit = "个";
		} else if (foodType == TYPE_PIZZA) {
			unit = "份";
		}
		String fontStr = "消耗" + calorie + "千卡≈";
		String foodName = getFoodNameByType(foodType);
		if (foodType != TYPE_NULL) {
			if (haveHalf) {
				if (count > 0) {
					return fontStr + count + unit + "半" + foodName;
				}
				return fontStr + "半" + unit + foodName;
			}
			return fontStr + count + unit + foodName;
		} else {
			return "消耗" + calorie + "千卡";
		}
	}

	/**
	 * 根据食物类型获取对应的卡路里
	 * 
	 * @param foodType
	 * @return
	 */
	private static int getCalorieByType(int foodType) {
		switch (foodType) {
		case TYPE_CANDY:
			return CALORIE_CANDY;
		case TYPE_CHOCOLATES:
			return CALORIE_CHOCOLATES;
		case TYPE_CHICHEN_LEG:
			return CALORIE_CHICHEN_LEG;
		case TYPE_PIZZA:
			return CALORIE_PIZZA;
		default:
			return 0;
		}
	}

	/**
	 * 根据食物类别获取食物名称
	 * 
	 * @param foodType
	 * @return
	 */
	private static String getFoodNameByType(int foodType) {
		switch (foodType) {
		case TYPE_CANDY:
			return Constants.PEDOMEOTR_FOOD_CANDY;
		case TYPE_CHOCOLATES:
			return Constants.PEDOMEOTR_FOOD_CHOCOLATES;
		case TYPE_CHICHEN_LEG:
			return Constants.PEDOMEOTR_FOOD_CHICHEN_LEG;
		case TYPE_PIZZA:
			return Constants.PEDOMEOTR_FOOD_PIZZA;
		default:
			return null;
		}
	}

	/**
	 * 获取基本食物热量
	 * TODO
	 * @param foodtype 食物类型
	 * @return
	 * @return int
	 * @author jiazhi.cao
	 * @time 下午7:33:36
	 */
	private static int getBaseFoodCal(int foodtype)
	{
		int baseCal=0;
		switch (foodtype) {
		case 0:
			baseCal=0;
			break;
		case 1:
			baseCal=25;
			break;
		case 2:
			baseCal=75;
			break;
		case 3:
			baseCal=390;
			break;
		case 4:
			baseCal=832;
			break;
		default:
			baseCal=0;
			break;
		}
		return baseCal;
	}
}
