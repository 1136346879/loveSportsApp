package cmccsi.mhealth.app.sports.bean;

public class DeviceTypeInfo {
	public String productPara; // 设备类型
	public String productName; // 设备名称
	public String productPic; // 产品图片地址
	public String productAppTag; // 适用应用
	public String productDesc; // 设备描述
	public String isBtDevice; // 0：非蓝牙设备 1：是蓝牙设备
	public String btPrefix; //蓝牙设备前缀

	public DeviceTypeInfo() {

	}

	public DeviceTypeInfo(String productPara, String productName, String productPic, String productAppTag,
			String productDesc, String isBtDevice, String btPrefix) {
		this.productName = productName;
		this.productPara = productPara;
		this.productPic = productPic;
		this.productAppTag = productAppTag;
		this.productDesc = productDesc;
		this.isBtDevice = isBtDevice;
		this.btPrefix = btPrefix;
	}

	public String getProductPara() {
		return productPara;
	}

	public void setProductPara(String productPara) {
		this.productPara = productPara;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public void setProductPic(String productPic) {
		this.productPic = productPic;
	}

	public String getProductAppTag() {
		return productAppTag;
	}

	public void setProductAppTag(String productAppTag) {
		this.productAppTag = productAppTag;
	}

	public String getProductDesc() {
		return productDesc;
	}

	public void setProductDesc(String productDesc) {
		this.productDesc = productDesc;
	}

	public String getIsBtDevice() {
		return isBtDevice;
	}

	public void setIsBtDevice(String isBtDevice) {
		this.isBtDevice = isBtDevice;
	}

	public String getProductPic() {
		return productPic;
	}

	public String getBtPrefix() {
		return btPrefix;
	}

	public void setBtPrefix(String btPrefix) {
		this.btPrefix = btPrefix;
	}

}