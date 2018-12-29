package cmccsi.mhealth.app.sports.phonesteplib;

/**
 * 用户信息，默认值
 * 
 * @author luckchoudog
 *
 */
public class UserInstance {
	/**
	 * 性别
	 */
	private int gender = 1;
	/**
	 * 身高
	 */
	private double height = 170;
	/**
	 * 体重
	 */
	private double weight = 65;
	/**
	 * 年龄
	 */
	private int age = 24;
	public int getGender() {
		return gender;
	}
	public void setGender(int gender) {
		this.gender = gender;
	}
	public double getHeight() {
		return height;
	}
	public void setHeight(double height) {
		this.height = height;
	}
	public double getWeight() {
		return weight;
	}
	public void setWeight(double d) {
		this.weight = d;
	}
	public int getAge() {
		return age;
	}
	public void setAge(int age) {
		this.age = age;
	}
}
