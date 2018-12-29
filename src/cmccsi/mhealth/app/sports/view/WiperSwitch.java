package cmccsi.mhealth.app.sports.view;

import cmccsi.mhealth.app.sports.common.ImageUtil;
import cmccsi.mhealth.app.sports.R;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.media.ThumbnailUtils;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;  

public class WiperSwitch extends View implements OnTouchListener{

	private Bitmap bg_on, bg_off, slipper_btn;  
    /** 
     * 按下时的x和当前的x 
     */  
    private float downX, nowX;  
      
    /** 
     * 记录用户是否在滑动 
     */  
    private boolean onSlip = false;  
      
    /** 
     * 当前的状态 
     */  
    private boolean nowStatus = false;  
      
    /** 
     * 监听接口 
     */  
    private OnChangedListener listener;  
    /**
     * 宽
     */
    private int weight=getWidth();
    /**
     * 高（也是滑块高）
     */
    private int height=getHeight();
    
    public WiperSwitch(Context context) {  
        super(context);  
        init();  
    }  
  
    public WiperSwitch(Context context, AttributeSet attrs) {  
        super(context, attrs);  
        init();  
    }  
      
    public void init(){  
        //载入图片资源  
        bg_on = BitmapFactory.decodeResource(getResources(), R.drawable.switch_on);
        
        bg_off = BitmapFactory.decodeResource(getResources(), R.drawable.switch_off);
        
        slipper_btn = BitmapFactory.decodeResource(getResources(), R.drawable.switch_btn); 
        
        setOnTouchListener(this);  
    }  
      
    protected void onDraw(Canvas canvas) {  
        super.onDraw(canvas);  
        Paint paint = new Paint();  
        float x = 0;  
        weight=getWidth();
        height=getHeight();
        Rect rect=new Rect(0, 0, weight, height);
        //根据nowX设置背景，开或者关状态  
        if (nowX < (weight/2)){  
            canvas.drawBitmap(bg_off, null,rect, paint);//画出关闭时的背景  
        }else{  
            canvas.drawBitmap(bg_on, null,rect, paint);//画出打开时的背景   
        }  
          
        if (onSlip) {//是否是在滑动状态,    
            if(nowX >= weight)//是否划出指定范围,不能让滑块跑到外头,必须做这个判断  
                x = weight - height/2;//减去滑块1/2的长度  
            else  
                x = nowX - height/2;  
        }else {  
            if(nowStatus){//根据当前的状态设置滑块的x值  
                x = weight - height;  
            }else{  
                x = 0;  
            }  
        }  
          
        //对滑块滑动进行异常处理，不能让滑块出界  
        if (x < 0 ){  
            x = 0;  
        }  
        else if(x > (weight - height)){  
            x = weight - height;  
        }  
         Rect rectslipper=new Rect((int)x,0,(int)x+height,height);
        //画出滑块  
        //canvas.drawBitmap(slipper_btn, x , 0, paint); 
        canvas.drawBitmap(slipper_btn, null, rectslipper, paint);
    }  
  
    @Override  
    public boolean onTouch(View v, MotionEvent event) {  
        switch(event.getAction()){  
        case MotionEvent.ACTION_DOWN:{  
            setcheckstatus(!nowStatus); 
            if(listener != null){  
                listener.OnChanged(WiperSwitch.this, nowStatus);  
            } 
            break;
        }
        }
        
       /* case MotionEvent.ACTION_DOWN:{  
            if (event.getX() > weight){  
                return false;  
            }else{  
                onSlip = true;  
                downX = event.getX();  
                nowX = downX;  
            }  
            break;  
        }  
        case MotionEvent.ACTION_MOVE:{  
            nowX = event.getX();  
            break;  
        }  
        case MotionEvent.ACTION_UP:{  
            onSlip = false;  
            if(event.getX() >= (weight/2)){  
                nowStatus = true;  
                nowX = weight - height/2;  
            }else{  
                nowStatus = false;  
                nowX = 0;  
            }  
              
            if(listener != null){  
                listener.OnChanged(WiperSwitch.this, nowStatus);  
            }  
            break;  
        }  
        } */ 
        //刷新界面  
        invalidate();  
        return true;  
    }  
      
      
      
    /** 
     * 为WiperSwitch设置一个监听，供外部调用的方法 
     * @param listener 
     */  
    public void setOnChangedListener(OnChangedListener listener){  
        this.listener = listener;  
    }  
      
      
    /** 
     * 设置滑动开关的初始状态，供外部调用 
     * @param checked 
     */  
    public void setChecked(boolean checked){  
        if(checked){  
            nowX = bg_off.getWidth();  
        }else{  
            nowX = 0;  
        }  
        nowStatus = checked;  
    }  
  
      
    /** 
     * 回调接口 
     * @author len 
     * 
     */  
    public interface OnChangedListener {  
        public void OnChanged(WiperSwitch wiperSwitch, boolean checkState);  
    }  

    
    private void setcheckstatus(boolean status)
    {
    	if(status){  
            nowStatus = true;  
            nowX = weight - height/2;  
        }else{  
            nowStatus = false;  
            nowX = 0;  
        }
    }
}
