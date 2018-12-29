package com.cmcc.bracelet.lsjx.libs;

/**
 * 用户信息类，佩戴者的的一些信息
 */
public class UserInfo {
	/**
	 * 设置用户性别 true为女 false为男
	 */
	private boolean female; // 设置用户性别 true为女 false为男
	/**
	 * 设置用户身高（cm）
	 */
	private int height; // 设置用户身高（cm）
	/**
	 * 设置用户体重（kg）
	 */
	private int weight; // 设置用户体重（kg）
	/**
	 * 设置佩戴位置 true为右 false为左
	 */
	private boolean bandOnRight; // 设置佩戴位置 true为右 false为左
	/**
	 * 设置走路步长
	 */
	private int walkingStepLength; // 设置走路步长
	/**
	 * 设置跑步步长
	 */
	private int runningStepLength; // 设置跑步步长
	/**
	 * 设置用户出生日期 年
	 */
	private int birthYear; // 设置用户生日
	/**
	 * 设置用户出生日期 月
	 */
	private int birthMonth;
	/**
	 * 设置用户出生日期 日
	 */
	private int birthDay;
	/**
	 * 设置目标步数
	 */
	private int goalSteps; // 设置目标步数
	/**
	 * 设置运动闹钟提醒时间 小时(24小时制)
	 */
	private int goalDurationHour; // 设置运动闹钟提醒时间
	/**
	 * 设置运动闹钟提醒时间 分钟
	 */
	private int goalDurationMinute;
	/**
	 * 设置睡眠闹钟重复日期：8bit 的int参数日六五四三二一設定
	 */
	private int alarmRepeat;
	/**
	 * 设置睡眠闹钟提醒时间 小时
	 */
	private int alarmHour; // 设置睡眠闹钟提醒时间
	/**
	 * 设置睡眠闹钟重复日期 分钟
	 */
	private int alarmMinute; // 设置睡眠闹钟重复日期

	public boolean isFemale() {
		return female;
	}

	public void setFemale(boolean female) {
		this.female = female;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public int getWeight() {
		return weight;
	}

	public void setWeight(int weight) {
		this.weight = weight;
	}

	public boolean isBandOnRight() {
		return bandOnRight;
	}

	public void setBandOnRight(boolean bandOnRight) {
		this.bandOnRight = bandOnRight;
	}

	public int getWalkingStepLength() {
		return walkingStepLength;
	}

	public void setWalkingStepLength(int walkingStepLength) {
		this.walkingStepLength = walkingStepLength;
	}

	public int getRunningStepLength() {
		return runningStepLength;
	}

	public void setRunningStepLength(int runningStepLength) {
		this.runningStepLength = runningStepLength;
	}

	public int getBirthYear() {
		return birthYear;
	}

	public void setBirthYear(int birthYear) {
		this.birthYear = birthYear;
	}

	public int getBirthMonth() {
		return birthMonth;
	}

	public void setBirthMonth(int birthMonth) {
		this.birthMonth = birthMonth;
	}

	public int getBirthDay() {
		return birthDay;
	}

	public void setBirthDay(int birthDay) {
		this.birthDay = birthDay;
	}

	public int getGoalSteps() {
		return goalSteps;
	}

	public void setGoalSteps(int goalSteps) {
		this.goalSteps = goalSteps;
	}

	public int getGoalDurationHour() {
		return goalDurationHour;
	}

	public void setGoalDurationHour(int goalDurationHour) {
		this.goalDurationHour = goalDurationHour;
	}

	public int getGoalDurationMinute() {
		return goalDurationMinute;
	}

	public void setGoalDurationMinute(int goalDurationMinute) {
		this.goalDurationMinute = goalDurationMinute;
	}

	public int getAlarmRepeat() {
		return alarmRepeat;
	}

	public void setAlarmRepeat(int alarmRepeat) {
		this.alarmRepeat = alarmRepeat;
	}

	public int getAlarmHour() {
		return alarmHour;
	}

	public void setAlarmHour(int alarmHour) {
		this.alarmHour = alarmHour;
	}

	public int getAlarmMinute() {
		return alarmMinute;
	}

	public void setAlarmMinute(int alarmMinute) {
		this.alarmMinute = alarmMinute;
	}
}
