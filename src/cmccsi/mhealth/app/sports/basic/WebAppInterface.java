package cmccsi.mhealth.app.sports.basic;
/**
 * Java 和 JavaScript之间互调的接口
 * @author flb
 */
public interface WebAppInterface {
	public abstract void jsCallJava();
	public abstract void jsCallJava(String data);
	
	public abstract void javaCallJS();
	public abstract void javaCallJS(String data);
}
