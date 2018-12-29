package com.cmcc.ishang.lib.net.gson;

public abstract class BaseNetItem {
	public String status = "UNSET";
	public String reason = "";
	
	public abstract void setValue(BaseNetItem bni);
	public abstract boolean isValueData(BaseNetItem bni);

}
