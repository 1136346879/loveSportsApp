package com.cmcc.bracelet.lsjx.libs;

public class LSJXMsg {
	int id;
	/**
	 * 日期 格式为20150103
	 */
	private int date;
	/**
	 * 时间 24小时制 0，1，...，22，23
	 */
	private int hour;
	/**
	 * 分钟
	 */
	private int minute;
	/**
	 * 卡路里
	 */
	private int carlories;
	/**
	 * 步数
	 */
	private int stepNum;
	/**
	 * 运动距离（范围）
	 */
	private int range;
	/**
	 * 运动时间
	 */
	private int exercise_time;
	/**
	 * 预留1
	 */
	private int other1;
	/**
	 * 预留2
	 */
	private int other2;

	public int getDate() {
		return date;
	}

	public void setDate(int date) {
		this.date = date;
	}

	public int getHour() {
		return hour;
	}

	public void setHour(int hour) {
		this.hour = hour;
	}

	public int getMinute() {
		return minute;
	}

	public void setMinute(int minute) {
		this.minute = minute;
	}

	public int getCarlories() {
		return carlories;
	}

	public void setCarlories(int carlories) {
		this.carlories = carlories;
	}

	public int getStepNum() {
		return stepNum;
	}

	public void setStepNum(int stepNum) {
		this.stepNum = stepNum;
	}

	public int getRange() {
		return range;
	}

	public void setRange(int range) {
		this.range = range;
	}

	public int getExercise_time() {
		return exercise_time;
	}

	public void setExercise_time(int exercise_time) {
		this.exercise_time = exercise_time;
	}

	public int getOther1() {
		return other1;
	}

	public void setOther1(int other1) {
		this.other1 = other1;
	}

	public int getOther2() {
		return other2;
	}

	public void setOther2(int other2) {
		this.other2 = other2;
	}

	public LSJXMsg( int date, int hour, int minute, int carlories, int stepNum, int range, int exercise_time, int other1, int other2) {
		super();
		this.date = date;
		this.hour = hour;
		this.minute = minute;
		this.carlories = carlories;
		this.stepNum = stepNum;
		this.range = range;
		this.exercise_time = exercise_time;
		this.other1 = other1;
		this.other2 = other2;
	}

	public LSJXMsg() {
		super();
	}

}
