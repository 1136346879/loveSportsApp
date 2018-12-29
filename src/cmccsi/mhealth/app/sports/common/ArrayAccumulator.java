package cmccsi.mhealth.app.sports.common;
import java.util.Arrays;


public class ArrayAccumulator {
	private long[] array;
	private int length;
	public ArrayAccumulator(String array) {
		if(array==null){
			throw new IllegalArgumentException("array must be a none-null String");
		}
		this.array = parse(array);
		this.length = this.array.length;
	}
	
	public ArrayAccumulator(int length){
		if(length<=0){
			throw new IllegalArgumentException("length must be greate than 0");
		}
		this.length=length;
		this.array =new long[length];
		Arrays.fill(this.array, 0);
	}
	
	public void add(String array){
		long[] newArray = parse(array);
		if(newArray!=null){
			for(int i=0;i<this.length;i++) {
				if(i>=newArray.length){
					break;
				}
				this.array[i]+=newArray[i];
			}
		}
	}
	
	private static long[] parse(String strArray){
		if(strArray==null){
			return null;
		}
		String[] parts = strArray.split(",");
		int length = parts.length;
		long[] array = new long[length];
		for(int i=0;i<length;i++){
			array[i] = Long.parseLong(parts[i]);
		}
		return array;
	}
	
	public long getTotal() {
		long rst = 0;
		for(int i=0;i<this.length;i++){
			rst+=this.array[i];
		}
		return rst;
	}
	
	public String getTotalString() {
		return String.valueOf(getTotal());
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for(int i=0;i<length;i++){
			if(sb.length()>0){
				sb.append(",");
			} 
			sb.append(this.array[i]);
		}
		return sb.toString();
	}
}
