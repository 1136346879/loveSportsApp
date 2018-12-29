package cmccsi.mhealth.app.sports.bean;

public class DeviceInfo {
    public String isUsed; 
    public String productName; 
    public String productPara; 
    public String deviceSerial;
    public String deviceNumber;
    public String deviceVersion;
    public String deviceToken;
    public String updateMark;
    public String updateUrl;
    public String currentVersion;
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((deviceSerial == null) ? 0 : deviceSerial.hashCode());
        result = prime * result + ((isUsed == null) ? 0 : isUsed.hashCode());
        result = prime * result
                + ((productName == null) ? 0 : productName.hashCode());
        result = prime * result
                + ((productPara == null) ? 0 : productPara.hashCode());
        result = prime * result + ((deviceNumber == null) ? 0 : deviceNumber.hashCode());
        result = prime * result + ((deviceVersion == null) ? 0 : deviceVersion.hashCode());
        result = prime * result + ((deviceToken == null) ? 0 : deviceToken.hashCode());
        return result;
    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        DeviceInfo other = (DeviceInfo) obj;
        if (deviceSerial == null) {
            if (other.deviceSerial != null)
                return false;
        } else if (!deviceSerial.equals(other.deviceSerial))
            return false;
        if (isUsed == null) {
            if (other.isUsed != null)
                return false;
        } else if (!isUsed.equals(other.isUsed))
            return false;
        if (productName == null) {
            if (other.productName != null)
                return false;
        } else if (!productName.equals(other.productName))
            return false;
        if (productPara == null) {
            if (other.productPara != null)
                return false;
        } else if (!productPara.equals(other.productPara))
            return false;
        if(deviceNumber == null){
        	if(other.deviceNumber != null)
        		return false;
        }else if(!deviceNumber.equals(other.deviceNumber))
        		return false;
        if(deviceVersion == null){
        	if(other.deviceVersion != null)
        		return false;
        }else if(!deviceVersion.equals(other.deviceVersion))
        		return false;
        if(deviceToken == null){
        	if(other.deviceToken != null)
        		return false;
        }else if(!deviceToken.equals(other.deviceToken))
        		return false;
        return true;
    }
}
