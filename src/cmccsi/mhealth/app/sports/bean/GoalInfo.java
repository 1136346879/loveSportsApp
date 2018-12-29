package cmccsi.mhealth.app.sports.bean;

import android.content.Context;
import cmccsi.mhealth.app.sports.common.PreferencesUtils;
import cmccsi.mhealth.app.sports.common.SharedPreferredKey;
import cmccsi.mhealth.app.sports.R;

public class GoalInfo {
    private static final int ESTIMATERATE = 60;
    private static final long KILOMETER = 1000L;
    private static final int HOUR_PASS_LINE = 100;//
    private static final int HOURS_PER_DAY = 10; // 每天运动时间
    private double latestRate = 0;// 米/分钟
    private double currentDistance = 0;// 米
    public GoalType type;
    private long remainDays = 0;
    private long circleNum = 1;
    private long remainHours = 0;
    private long percentage;
    
    /**
     * 获得一个实例
     * 
     * @param netInfo
     * @return
     */
    public static GoalInfo getInstanse(GoalNetInfo netInfo) {
        for (GoalType achievementType : GoalType.values()) {
            String typeStr = achievementType.toString();
            if (netInfo.goalinfo.goal.equals(typeStr.substring(
                    typeStr.length() - 1, typeStr.length()))) {
                GoalInfo goalInfo = new GoalInfo();
                goalInfo.type = achievementType;
                goalInfo.latestRate = Double
                        .parseDouble(netInfo.goalinfo.rate);
                goalInfo.currentDistance = Double
                        .parseDouble(netInfo.goalinfo.distance);
                return goalInfo;
            }
        }
        return null;
    }
    
    /**
     * 通过Context获取实例
     * 
     * @param context
     * @return
     */
    public static GoalInfo getInstance(Context context) {
        return getInstance(context, PreferencesUtils.getString(context,
                SharedPreferredKey.GOAL_TYPE, "1"));
    }

    /**
     * 通过Context 和 typeId获取实例 
     * @param context
     * @param typeId 取值范围 0、1、2、3、4、5
     * @return
     */
    public static GoalInfo getInstance(Context context,String typeId){
        GoalNetInfo goalNetInfo = new GoalNetInfo();
        goalNetInfo.goalinfo.goal = typeId;
        goalNetInfo.goalinfo.rate =PreferencesUtils.getString(context, SharedPreferredKey.LATEST_RATE, "0");
        goalNetInfo.goalinfo.distance=PreferencesUtils.getString(context, SharedPreferredKey.CURRENT_DISTANCE  , "0");
        GoalInfo info = GoalInfo.getInstanse(goalNetInfo);
        info.calcRemainDays();
        return info;
    }
    
    private GoalInfo(){
    }
    
    public long getRemainDays() {
        return remainDays;
    }
    
    public double getLatestRate() {
        return latestRate;
    }

    public double getCurrentDistance() {
        return currentDistance;
    }

    public long getCircleNum() {
        return circleNum;
    }

    private void calcRemainDays(){
        if(latestRate ==0){
            latestRate = ESTIMATERATE;
        }
        
        circleNum = (long) (currentDistance / type.getDistance()) + 1;
        remainHours = (long) Math.ceil((type.getDistance() - currentDistance
                % type.getDistance())
                / latestRate / 60);
        remainDays = (long) Math.ceil((type.getDistance() - currentDistance
                % type.getDistance())
                / latestRate / 60 / HOURS_PER_DAY);
        
        percentage = (long) Math.ceil(currentDistance/type.distance*100.0);
    }
    
    public String getGoalReportInTime(Context context) {
        calcRemainDays();
        String report = null;
        if (this.type == GoalType.type4) {
            if (circleNum == 1) {
                if (remainHours < HOUR_PASS_LINE) {
                    report = context.getString(R.string.text_show_goal_First_inhours_huanghe,String.valueOf(remainHours));
                }else {
                    report = context.getString(R.string.text_show_goal_First_indays_huanghe,String.valueOf(remainDays));
                }
            } else {
                if (remainHours < HOUR_PASS_LINE) {
                    report = context.getString(R.string.text_show_goal_inhours_huanghe, String.valueOf(circleNum),
                        String.valueOf(remainHours));
                } else {
                    report = context.getString(R.string.text_show_goal_indays_huanghe, String.valueOf(circleNum),
                            String.valueOf(remainDays));
                }
            }
            return report;
        }
        if (circleNum == 1) {
            if (remainHours < HOUR_PASS_LINE) {
                report = context.getString(R.string.text_show_goal_First_inhours,
                        type.getName(), String.valueOf(remainHours));
            }else {
                report = context.getString(R.string.text_show_goal_First_indays,
                        type.getName(), String.valueOf(remainDays));
            }
        } else {
            if (remainHours < HOUR_PASS_LINE) {
                report = context.getString(R.string.text_show_goal_inhours,
                        type.getName(), String.valueOf(circleNum),
                        String.valueOf(remainHours));
            } else {
                report = context.getString(R.string.text_show_goal_indays,
                        type.getName(), String.valueOf(circleNum),
                        String.valueOf(remainDays));
            }
        }
        if (this.type == GoalType.type0) {
           report = report.replace("绕", "完成");
           report = report.replace("圈", "次");
        } 
        return report;
    }
    
    /**
     * 以半分比的形式展现成就
     * @param context
     * @return
     */
    public String getGoalReportInpercentage(Context context){
        String report = context.getString(R.string.text_show_goal_percentage, percentage+"%");
        return report;
    }

    public enum GoalType {
        type0("马拉松", 50 * KILOMETER, R.drawable.icon_achievement_marathon,
                R.drawable.img_achievement_marathon,"完成一次马拉松"), type1("北京",
                99 * KILOMETER, R.drawable.icon_achievement_beijing,
                R.drawable.img_achievement_beijing,"绕北京一圈"), type2("青海湖",
                540 * KILOMETER, R.drawable.icon_achievement_qinghailake,
                R.drawable.img_achievement_qinghailake,"绕青海湖一圈"), type3("台湾",
                913 * KILOMETER, R.drawable.icon_achievement_taiwan,
                R.drawable.img_achievement_taiwan,"绕台湾一圈"), type4("黄河",
                5464 * KILOMETER, R.drawable.icon_achievement_huanghe,
                R.drawable.img_achievement_huanghe,"黄河徒步行一次"), type5("地球",
                40000 * KILOMETER, R.drawable.icon_achievement_earth,
                R.drawable.img_achievement_earth,"绕地球一圈");

        GoalType(String name, long distance,int iconRes,int imgRes,String info) {
            setName(name);
            setDistance(distance);
            setIconRes(iconRes);
            setImgRes(imgRes);
            setInfo(info);
        }

        private String name;
        private long distance;
        private int imgRes;
        private int iconRes;
        private String info;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public long getDistance() {
            return distance;
        }

        /**
         * @return the info
         */
        public String getInfo() {
            return info;
        }

        /**
         * @param info the info to set
         */
        public void setInfo(String info) {
            this.info = info;
        }

        public void setDistance(long distance) {
            this.distance = distance;
        }

        public int getIconRes() {
            return iconRes;
        }

        public void setIconRes(int iconRes) {
            this.iconRes = iconRes;
        }

        public int getImgRes() {
            return imgRes;
        }

        public void setImgRes(int imgRes) {
            this.imgRes = imgRes;
        }
    }
}
