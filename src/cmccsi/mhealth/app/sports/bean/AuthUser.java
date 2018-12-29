package cmccsi.mhealth.app.sports.bean;
import java.util.Date;

public class AuthUser extends BaseNetItem{
	/**
	 * 用户ID
	 */
	private Integer userId;

	/**
	 * 用户姓名
	 */
	private String userName;

	/**
	 * 用户登录UID
	 */
	private String userUid;

	/**
	 * 用户密码
	 */
	private String userPassword;

	/**
	 * 用户昵称
	 */
	private String userNickName;

	/**
	 * 用户身份证号码
	 */
	private String userIdCardNumber;

	/**
	 * 用户性别
	 */
	private String userGender;

	/**
	 * 用户性别代码
	 */
	private String userGenderDict;

	/**
	 * 用户出生日期
	 */
	private Date userBirthday;

	/**
	 * 用户民族
	 */
	private String userNation;

	/**
	 * 用户民族代码
	 */
	private String userNationDict;

	/**
	 * 用户国籍
	 */
	private String userNationality;

	/**
	 * 用户国籍代码
	 */
	private String userNationalityDict;

	/**
	 * 用户婚姻状态
	 */
	private String userWedlock;

	/**
	 * 用户婚姻状态代码
	 */
	private String userWedlockDict;

	/**
	 * 用户政治面貌
	 */
	private String userReligion;

	/**
	 * 用户政治面貌代码
	 */
	private String userReligionDict;

	/**
	 * 用户学历
	 */
	private String userEducation;

	/**
	 * 用户学历代码
	 */
	private String userEducationDict;

	/**
	 * 用户邮件地址
	 */
	private String userEmail;

	/**
	 * 用户办公电话
	 */
	private String userTelephoneNumber;

	/**
	 * 用户移动电话
	 */
	private String userPreferredMobile;

	/**
	 * 用户通讯地址
	 */
	private String userPostalAddress;

	/**
	 * 用户通讯邮政编码
	 */
	private String userPostalCode;

	/**
	 * 用户传真号码
	 */
	private String userFaxNumber;

	/**
	 * 用户QQ号码
	 */
	private String userQq;

	/**
	 * 用户所在省份
	 */
	private String userLocationProvince;

	/**
	 * 用户所在地市
	 */
	private String userLocationCity;

	/**
	 * 用户所在区县
	 */
	private String userLocationArea;

	/**
	 * 用户所在街道
	 */
	private String userLocationStreet;

	/**
	 * 用户所在公司
	 */
	private String userCompany;

	/**
	 * 用户所在部门
	 */
	private String userDepart;

	/**
	 * 备注
	 */
	private String userRemark;

	/**
	 * 用户标签
	 */
	private String userTag;

	/**
	 * 用户类型
	 */
	private String userUsertype;

	/**
	 * 用户类型代码
	 */
	private String userUsertypeDict;

	/**
	 * 用户头像
	 */
	private String userPic;

	/**
	 * 用户签名
	 */
	private String userSignature;

	/**
	 * 用户描述
	 */
	private String userDescription;

	/**
	 * 用户帐号注册时间
	 */
	private Date userAccountRegistertime;

	/**
	 * 用户帐号结束时间
	 */
	private Date userAccountEndtime;

	/**
	 * 用户帐号状态
	 */
	private String userStatus;

	/**
	 * 用户帐号状态代码
	 */
	private String userStatusDict;

	/**
	 * 用户最后密码修改时间
	 */
	private Date userPwdModifiedDate;

	/**
	 * 用户最后一次登录时间
	 */
	private Date lastLoginTime;

	/**
	 * 创建人员ID
	 */
	private Integer createUserId;

	/**
	 * 创建时间
	 */
	private Date createTime;

	/**
	 * 最后更新人员ID
	 */
	private Integer updateUserId;

	/**
	 * 最后更新时间
	 */
	private Date updateTime;

	/**
	 * 密码重置类型
	 */
	private String passwordModifyFlag;

	/**
	 * 数据操作状态
	 */
	private String operateState;

	/**
	 * 扩展字符串
	 */
	private String userExtensions;

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getUserUid() {
		return userUid;
	}

	public void setUserUid(String userUid) {
		this.userUid = userUid;
	}

	public String getUserPassword() {
		return userPassword;
	}

	public void setUserPassword(String userPassword) {
		this.userPassword = userPassword;
	}

	public String getUserNickName() {
		return userNickName;
	}

	public void setUserNickName(String userNickName) {
		this.userNickName = userNickName;
	}

	public String getUserIdCardNumber() {
		return userIdCardNumber;
	}

	public void setUserIdCardNumber(String userIdCardNumber) {
		this.userIdCardNumber = userIdCardNumber;
	}

	public String getUserGender() {
		return userGender;
	}

	public void setUserGender(String userGender) {
		this.userGender = userGender;
	}

	public String getUserGenderDict() {
		return userGenderDict;
	}

	public void setUserGenderDict(String userGenderDict) {
		this.userGenderDict = userGenderDict;
	}

	public Date getUserBirthday() {
		return userBirthday;
	}

	public void setUserBirthday(Date userBirthday) {
		this.userBirthday = userBirthday;
	}

	public String getUserNation() {
		return userNation;
	}

	public void setUserNation(String userNation) {
		this.userNation = userNation;
	}

	public String getUserNationDict() {
		return userNationDict;
	}

	public void setUserNationDict(String userNationDict) {
		this.userNationDict = userNationDict;
	}

	public String getUserNationality() {
		return userNationality;
	}

	public void setUserNationality(String userNationality) {
		this.userNationality = userNationality;
	}

	public String getUserNationalityDict() {
		return userNationalityDict;
	}

	public void setUserNationalityDict(String userNationalityDict) {
		this.userNationalityDict = userNationalityDict;
	}

	public String getUserWedlock() {
		return userWedlock;
	}

	public void setUserWedlock(String userWedlock) {
		this.userWedlock = userWedlock;
	}

	public String getUserWedlockDict() {
		return userWedlockDict;
	}

	public void setUserWedlockDict(String userWedlockDict) {
		this.userWedlockDict = userWedlockDict;
	}

	public String getUserReligion() {
		return userReligion;
	}

	public void setUserReligion(String userReligion) {
		this.userReligion = userReligion;
	}

	public String getUserReligionDict() {
		return userReligionDict;
	}

	public void setUserReligionDict(String userReligionDict) {
		this.userReligionDict = userReligionDict;
	}

	public String getUserEducation() {
		return userEducation;
	}

	public void setUserEducation(String userEducation) {
		this.userEducation = userEducation;
	}

	public String getUserEducationDict() {
		return userEducationDict;
	}

	public void setUserEducationDict(String userEducationDict) {
		this.userEducationDict = userEducationDict;
	}

	public String getUserEmail() {
		return userEmail;
	}

	public void setUserEmail(String userEmail) {
		this.userEmail = userEmail;
	}

	public String getUserTelephoneNumber() {
		return userTelephoneNumber;
	}

	public void setUserTelephoneNumber(String userTelephoneNumber) {
		this.userTelephoneNumber = userTelephoneNumber;
	}

	public String getUserPreferredMobile() {
		return userPreferredMobile;
	}

	public void setUserPreferredMobile(String userPreferredMobile) {
		this.userPreferredMobile = userPreferredMobile;
	}

	public String getUserPostalAddress() {
		return userPostalAddress;
	}

	public void setUserPostalAddress(String userPostalAddress) {
		this.userPostalAddress = userPostalAddress;
	}

	public String getUserPostalCode() {
		return userPostalCode;
	}

	public void setUserPostalCode(String userPostalCode) {
		this.userPostalCode = userPostalCode;
	}

	public String getUserFaxNumber() {
		return userFaxNumber;
	}

	public void setUserFaxNumber(String userFaxNumber) {
		this.userFaxNumber = userFaxNumber;
	}

	public String getUserQq() {
		return userQq;
	}

	public void setUserQq(String userQq) {
		this.userQq = userQq;
	}

	public String getUserLocationProvince() {
		return userLocationProvince;
	}

	public void setUserLocationProvince(String userLocationProvince) {
		this.userLocationProvince = userLocationProvince;
	}

	public String getUserLocationCity() {
		return userLocationCity;
	}

	public void setUserLocationCity(String userLocationCity) {
		this.userLocationCity = userLocationCity;
	}

	public String getUserLocationArea() {
		return userLocationArea;
	}

	public void setUserLocationArea(String userLocationArea) {
		this.userLocationArea = userLocationArea;
	}

	public String getUserLocationStreet() {
		return userLocationStreet;
	}

	public void setUserLocationStreet(String userLocationStreet) {
		this.userLocationStreet = userLocationStreet;
	}

	public String getUserCompany() {
		return userCompany;
	}

	public void setUserCompany(String userCompany) {
		this.userCompany = userCompany;
	}

	public String getUserDepart() {
		return userDepart;
	}

	public void setUserDepart(String userDepart) {
		this.userDepart = userDepart;
	}

	public String getUserRemark() {
		return userRemark;
	}

	public void setUserRemark(String userRemark) {
		this.userRemark = userRemark;
	}

	public String getUserTag() {
		return userTag;
	}

	public void setUserTag(String userTag) {
		this.userTag = userTag;
	}

	public String getUserUsertype() {
		return userUsertype;
	}

	public void setUserUsertype(String userUsertype) {
		this.userUsertype = userUsertype;
	}

	public String getUserUsertypeDict() {
		return userUsertypeDict;
	}

	public void setUserUsertypeDict(String userUsertypeDict) {
		this.userUsertypeDict = userUsertypeDict;
	}

	public String getUserPic() {
		return userPic;
	}

	public void setUserPic(String userPic) {
		this.userPic = userPic;
	}

	public String getUserSignature() {
		return userSignature;
	}

	public void setUserSignature(String userSignature) {
		this.userSignature = userSignature;
	}

	public String getUserDescription() {
		return userDescription;
	}

	public void setUserDescription(String userDescription) {
		this.userDescription = userDescription;
	}

	public Date getUserAccountRegistertime() {
		return userAccountRegistertime;
	}

	public void setUserAccountRegistertime(Date userAccountRegistertime) {
		this.userAccountRegistertime = userAccountRegistertime;
	}

	public Date getUserAccountEndtime() {
		return userAccountEndtime;
	}

	public void setUserAccountEndtime(Date userAccountEndtime) {
		this.userAccountEndtime = userAccountEndtime;
	}

	public String getUserStatus() {
		return userStatus;
	}

	public void setUserStatus(String userStatus) {
		this.userStatus = userStatus;
	}

	public String getUserStatusDict() {
		return userStatusDict;
	}

	public void setUserStatusDict(String userStatusDict) {
		this.userStatusDict = userStatusDict;
	}

	public Date getUserPwdModifiedDate() {
		return userPwdModifiedDate;
	}

	public void setUserPwdModifiedDate(Date userPwdModifiedDate) {
		this.userPwdModifiedDate = userPwdModifiedDate;
	}

	public Date getLastLoginTime() {
		return lastLoginTime;
	}

	public void setLastLoginTime(Date lastLoginTime) {
		this.lastLoginTime = lastLoginTime;
	}

	public Integer getCreateUserId() {
		return createUserId;
	}

	public void setCreateUserId(Integer createUserId) {
		this.createUserId = createUserId;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Integer getUpdateUserId() {
		return updateUserId;
	}

	public void setUpdateUserId(Integer updateUserId) {
		this.updateUserId = updateUserId;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	public String getPasswordModifyFlag() {
		return passwordModifyFlag;
	}

	public void setPasswordModifyFlag(String passwordModifyFlag) {
		this.passwordModifyFlag = passwordModifyFlag;
	}

	public String getOperateState() {
		return operateState;
	}

	public void setOperateState(String operateState) {
		this.operateState = operateState;
	}

	public String getUserExtensions() {
		return userExtensions;
	}

	public void setUserExtensions(String userExtensions) {
		this.userExtensions = userExtensions;
	}

	@Override
	public void setValue(BaseNetItem bni) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isValueData(BaseNetItem bni) {
		// TODO Auto-generated method stub
		return false;
	}
}
