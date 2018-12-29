package cmccsi.mhealth.app.sports.view;

import java.util.ArrayList;
import java.util.List;

import cmccsi.mhealth.app.sports.common.utils.DensityUtil;
import cmccsi.mhealth.app.sports.R;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

/**
 * 运动页面卡路里成就控件
 * 一条进度上有小人在跑
 * @type PedoCalProcess
 * TODO
 * @author jiazhi.cao
 * @time 2015-3-12上午9:02:22
 */
public class PedoCalProcess extends View {
	private int mOverColor;//已通过的颜色
	private int mNormalColor;//未通过的颜色
	private Paint mPaint;
	
	private int mUnderLineHeight=5;//下边横线高度
	private int mLineMargin=50;// 横线下边距 要大于节点照片高度 因为节点照片要下翻
	private int mExplainTextMergin=10;//说明文字相对图片偏移
	private int mPictureWeight=35;//节点照片宽度 目前是正方形的
	private int mProcessPictureWeight=50;//进度照片宽度
	private int mProcessPictureHeight=50;//进度照片高度
//	private int mExplainPictureWeight=140;//说明照片宽度
	private int mExplainPictureHeight=50;//说明照片高度
	private int mExplainTextSize=30;//说明照片高度
	
	private List<Bitmap> mNormalMilePictures;//节点普通图片
	private List<Bitmap> mOverMilePictures;//节点已过图片
	private Bitmap mProcessPictures;//进度图片
	private Bitmap mExplainPictures;//说明图片
	
	private int mProcess=0;
	private int[] mCoordinateX={50};
	
	private String mExplainText="";
	
	

	public PedoCalProcess(Context context) {
		super(context);
		mPaint=new Paint();
	}

	public PedoCalProcess(Context context, AttributeSet attrs) {
		super(context,attrs);
		mPaint=new Paint();
		init(context,attrs);
	} 
	
	/**
	 * 设置说明文字
	 * TODO
	 * @param mExplainText
	 * @return void
	 * @author jiazhi.cao
	 * @time 下午1:59:00
	 */
	public void setExplainText(String mExplainText) {
		this.mExplainText = mExplainText;
	}
	
	/**
	 * 设置正常节点图片(一定要设置)
	 * TODO
	 * @param pictures 图片id集合
	 * @return void
	 * @author jiazhi.cao
	 * @time 上午8:56:48
	 */
	public void setNormalMilePictures(int[] pictures)
	{
		for (int drawableid : pictures) {
			mNormalMilePictures.add(BitmapFactory.decodeResource(getResources(), drawableid));
		}
	}
	
	/**
	 * 设置已经过的节点图(一定要设置)
	 * TODO
	 * @param pictures 图片Id集合
	 * @return void
	 * @author jiazhi.cao
	 * @time 上午8:57:40
	 */
	public void setOverMilePictures(int[] pictures)
	{
		for (int drawableid : pictures) {
			mOverMilePictures.add(BitmapFactory.decodeResource(getResources(), drawableid));
		}
	}
	
	/**
	 * 设置进度图片(一定要设置)
	 * TODO
	 * @param picture 图片id
	 * @return void
	 * @author jiazhi.cao
	 * @time 上午8:58:52
	 */
	public void setProcessPicture(int picture)
	{
		mProcessPictures=BitmapFactory.decodeResource(getResources(), picture);
	}
	
	/**
	 * 设置说明的图片 飘在进度图片上面
	 * TODO
	 * @param picture 图片id
	 * @return void
	 * @author jiazhi.cao
	 * @time 上午8:59:34
	 */
	public void setExplainPictures(int picture)
	{
		mExplainPictures=BitmapFactory.decodeResource(getResources(), picture);
	}
	
	/**
	 * 设置进度
	 * TODO
	 * @param process 百分数 10% 则传10
	 * @return void
	 * @author jiazhi.cao
	 * @time 上午9:00:23
	 */
	public void setProcess(int process)
	{
		mProcess=process;
		
	}
	
	/**
	 * 节点百分数
	 * TODO
	 * @param coordinateX 节点位置百分数集合{10,20，..}
	 * @return void
	 * @author jiazhi.cao
	 * @time 上午9:01:01
	 */
	public void setCoordinateX(int[] coordinateX)
	{
		mCoordinateX=coordinateX;
	}
	
	/**
	 * 获取节点百分数
	 * TODO
	 * @return
	 * @return int[]
	 * @author jiazhi.cao
	 * @time 上午10:33:26
	 */
	public int[] getCoordinateX() {
		return mCoordinateX;
	}

	/**
	 * 初始化
	 * TODO
	 * @param context 上下文
	 * @param attrs 参数
	 * @return void
	 * @author jiazhi.cao
	 * @time 上午9:01:49
	 */
	private void init(Context context, AttributeSet attrs)
	{
		TypedArray typed = context.obtainStyledAttributes(attrs, R.styleable.PictureProcessView);  
		mOverColor = typed.getColor(R.styleable.PictureProcessView_overColor, Color.GREEN);
		mNormalColor= typed.getColor(R.styleable.PictureProcessView_normalColor, Color.LTGRAY);
		mNormalMilePictures=new ArrayList<Bitmap>();
		mOverMilePictures=new ArrayList<Bitmap>();
		//改px
		mUnderLineHeight=DensityUtil.dip2px(context, mUnderLineHeight);//下边横线高度
		mLineMargin=DensityUtil.dip2px(context, mLineMargin);// 横线下边距 要大于节点照片高度 因为节点照片要下翻
		mPictureWeight=DensityUtil.dip2px(context, mPictureWeight);//节点照片宽度 目前是正方形的
		mProcessPictureWeight=DensityUtil.dip2px(context, mProcessPictureWeight);//进度照片宽度
		mProcessPictureHeight=DensityUtil.dip2px(context, mProcessPictureHeight);//进度照片高度
//		mExplainPictureWeight=DensityUtil.dip2px(context, mExplainPictureWeight);//说明照片宽度
		mExplainPictureHeight=DensityUtil.dip2px(context, mExplainPictureHeight);//说明照片高度
		
		mExplainTextMergin=DensityUtil.dip2px(context,mExplainTextMergin);//说明文字相对图片偏移
		
		mExplainTextSize=DensityUtil.px2sp(context,mExplainTextSize);
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		try
		{
			PaintFlagsDrawFilter pfd = new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);  
			canvas.setDrawFilter(pfd);
			DrawUnderLine(canvas,mProcess);
			DrawMilepost(canvas, mCoordinateX,mProcess);
			DrawMilePicture(canvas, mCoordinateX, mProcess);
			DrawProcessPicture(canvas, mCoordinateX, mProcess);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	//画横线
	/**
	 * 画进度横线
	 * TODO
	 * @param canvas 画布
	 * @param percent 进度百分比数字
	 * @return void
	 * @author jiazhi.cao
	 * @time 上午9:04:36
	 */
	private void DrawUnderLine(Canvas canvas,double overPercent)
	{
		mPaint.setColor(mOverColor);
		mPaint.setStyle(Paint.Style.FILL);
		float overX=(float)Math.ceil(getWidth()*overPercent/100);
		canvas.drawRect(1, getHeight()-mUnderLineHeight-mLineMargin, overX, getHeight()-mLineMargin, mPaint);
		mPaint.setColor(mNormalColor);
		canvas.drawRect(overX, getHeight()-mUnderLineHeight-mLineMargin, getWidth(), getHeight()-mLineMargin, mPaint);
	}
	
	//画节点
	/**
	 * 画节点小圆圈
	 * TODO
	 * @param canvas 画布
	 * @param CoordinateX 节点位置百分数集合{10,20，..}
	 * @param overPercent 进度百分比数字
	 * @return void
	 * @author jiazhi.cao
	 * @time 上午9:06:44
	 */
	private void DrawMilepost(Canvas canvas,int[] CoordinateX,double overPercent)
	{
		mPaint.setAntiAlias(true);
		for (int percent : CoordinateX) {
			float overX=(float)Math.ceil(getWidth()*percent/100);
			//超过的节点变色
			SetMilePost(!(percent>overPercent));
			canvas.drawCircle(overX, getHeight()-mLineMargin-(mUnderLineHeight*2/3), mUnderLineHeight/2, mPaint);
			//填下圆环中间 否则绿线不好看
			mPaint.setStyle(Style.FILL);
			mPaint.setColor(mNormalColor);
			canvas.drawCircle(overX, getHeight()-mLineMargin-(mUnderLineHeight*2/3), mUnderLineHeight/2-3, mPaint);
		}
		
	}
	
	//画节点上图片
	/**
	 * 画节点上方图片
	 * TODO
	 * @param canvas
	 * @param CoordinateX 节点位置百分数集合{10,20，..}
	 * @param overPercent 进度百分比数字
	 * @return void
	 * @author jiazhi.cao
	 * @time 上午9:09:39
	 */
	private void DrawMilePicture(Canvas canvas,int[] CoordinateX,double overPercent)
	{
		try
		{
			if(mNormalMilePictures==null||mNormalMilePictures.size()<1)
			{
				return;
			}
			else
			{
				//画节点上方图片
				int halfPictureWeight=(int)Math.ceil(mPictureWeight/2);
				for (int i=0;i<CoordinateX.length;i++) {
					int overX=(int)Math.ceil(getWidth()*CoordinateX[i]/100);
					if(CoordinateX[i]>overPercent)//未超过的节点
					{
						if(i>mNormalMilePictures.size())
						{
							Log.e("PictureProcessView", "NormalMilePictures 不够啊！");
							return;
						}
						Rect rect=new Rect(overX-halfPictureWeight
								, getHeight()-mLineMargin-mUnderLineHeight-mPictureWeight
								, overX+halfPictureWeight
								, getHeight()-mLineMargin-mUnderLineHeight);
						canvas.drawBitmap(mNormalMilePictures.get(i), null, rect, mPaint);
					}
					else//已越过的向下翻转
					{
						if(mOverMilePictures==null||i>mOverMilePictures.size())
						{
							Log.e("PictureProcessView", "OverMilePictures 不够啊！");
							return;
						}
						Rect rect=new Rect(overX-halfPictureWeight
								, getHeight()-mLineMargin
								, overX+halfPictureWeight
								, getHeight()-mLineMargin+mPictureWeight);
						canvas.drawBitmap(mOverMilePictures.get(i), null, rect, mPaint);
					}
				}
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	//画主要进度图片 （人和云）
	/**
	 * 画主要进度图片和说明图片 （目前是人和云）
	 * TODO
	 * @param canvas
	 * @param CoordinateX 节点位置百分数集合{10,20，..}
	 * @param overPercent 进度百分比数字
	 * @return void
	 * @author jiazhi.cao
	 * @time 上午9:10:17
	 */
	private void DrawProcessPicture(Canvas canvas,int[] CoordinateX,double overPercent)
	{
		if(mProcessPictures==null)//没有进度图片
		{
			return;
		}
		else
		{
			int halfProcessPictureWeight=(int)Math.ceil(mProcessPictureWeight/2);
			int halfMilePictureWeight=(int)Math.ceil(mPictureWeight/2);
			double overX=getWidth()*overPercent/100;
			//进度图片边框位置
			int left=0;
			int right=0;
			int top=getHeight()-mLineMargin-mUnderLineHeight-mProcessPictureHeight;//高=底空+横线高+图片高
			int bottom=getHeight()-mLineMargin-mUnderLineHeight;//底=底空+横线高
			if(overX<halfProcessPictureWeight)//图片刚开始图片贴左边
			{
				left=1;
				right=mProcessPictureWeight;
			}
			else if(overX>(getWidth()-halfProcessPictureWeight))//图片跑完图片贴右边
			{
				left=getWidth()-mProcessPictureWeight;
				right=getWidth();
			}
			else
			{
				//循环检测碰撞
				for (int i : CoordinateX) {
					if(i==overPercent)//正好在节点上不变位置
					{
						break;
					}
					//检测碰撞结果
					int temp=CheckCollision(i, overPercent);
					int tempX=(int)Math.ceil(getWidth()*i/100);
					if(temp==-1||temp==0)//左碰撞则贴节点左边
					{
						left=tempX-halfMilePictureWeight-mProcessPictureWeight-1;
						right=tempX-halfMilePictureWeight-1;
						break;
					}
				}
				if(left==0&&right==0)//没有碰撞
				{
					left=(int)overX-halfProcessPictureWeight;
					right=(int)overX+halfProcessPictureWeight;
				}
			}
			//画
			Rect rect=new Rect(left
						, top
						, right
						, bottom);
				
			canvas.drawBitmap(mProcessPictures, null, rect, mPaint);
			
			//画说明图片
			if(mExplainPictures==null||mExplainText=="")//没有说明图片
			{
				return;
			}
			mPaint.setColor(Color.GRAY);
			mPaint.setTextSize(30);
			
			Rect textrect = new Rect();
			mPaint.getTextBounds(mExplainText, 0, mExplainText.length(), textrect);
			float textweight=textrect.width();
			float textheight=textrect.height();
			
			int explainWeight=(int)textweight+2*mExplainTextMergin;
			int explainHeight=(int)(explainWeight*0.25);//(int)mExplainPictureHeight/2;//
			if(explainHeight>mExplainPictureHeight/2)
			{
				explainHeight=mExplainPictureHeight/2;
			}
			int cloudRight=right+explainWeight;			
			if(cloudRight<getWidth())
			{
				Rect rectExplain =new Rect(right
						, top-2*explainHeight//人头上
						, right+explainWeight
						, top);
				canvas.drawBitmap(mExplainPictures, null, rectExplain, mPaint);
				mPaint.setColor(0xff7aaf3b);
				
				canvas.drawText(mExplainText, right+mExplainTextMergin, top-explainHeight+(textheight/3), mPaint);
			}
			else//云撞到右界边飘到人的左边
			{
				if(left-explainWeight<0)//左边超出屏幕
				{
					Rect rectExplain =new Rect(1
							, top-2*explainHeight//人头上
							, 1+explainWeight
							, top);
					canvas.drawBitmap(convertBmp(mExplainPictures), null, rectExplain, mPaint);
					mPaint.setColor(0xff7aaf3b);
					canvas.drawText(mExplainText, 1+mExplainTextMergin, top-explainHeight+(textheight/3), mPaint);
				}
				else
				{
					Rect rectExplain =new Rect(left-explainWeight
							, top-2*explainHeight//人头上
							, left
							, top);
					canvas.drawBitmap(convertBmp(mExplainPictures), null, rectExplain, mPaint);
					mPaint.setColor(0xff7aaf3b);
					canvas.drawText(mExplainText, left-explainWeight+mExplainTextMergin, top-explainHeight+(textheight/3), mPaint);
				}
			}
		}
	}
	
	//设置节点状态 目前是颜色改变
	/**
	 * 设置节点状态 目前是颜色改变
	 * TODO
	 * @param isOver 是否已经过节点
	 * @return void
	 * @author jiazhi.cao
	 * @time 上午9:13:35
	 */
	private void SetMilePost(boolean isOver)
	{
		if(isOver)
		{
			mPaint.setStyle(Style.STROKE);
			mPaint.setStrokeWidth(mUnderLineHeight/2);
			mPaint.setColor(Color.YELLOW);
		}
		else
		{
			mPaint.setStyle(Style.STROKE);
			mPaint.setStrokeWidth(mUnderLineHeight/2);
			mPaint.setColor(Color.GRAY);
		}
	}
	

	//检测是否碰撞
	/**
	 * 检测是否碰撞
	 * TODO
	 * @param milePercent 节点位置百分数
	 * @param overPercent 已经过百分数
	 * @return
	 * @return int 前进碰撞-1 已经重叠0
	 * @author jiazhi.cao
	 * @time 上午9:14:02
	 */
	private int CheckCollision(double milePercent,double overPercent)
	{
		int isCollision=9;
		if(milePercent==overPercent)//点重叠
		{
			return 0;
		}
		else if(milePercent<overPercent)//已经过点
		{
			return 9;
		}
		
		double halfPictureWeight=mPictureWeight/2;
		double halfProcessPictureWeight=mProcessPictureWeight/2;
		//进度图片左边
		double processLeft=Math.floor(getWidth()*overPercent/100-halfProcessPictureWeight);
		//进度图片右边
		double processRight=Math.ceil(getWidth()*overPercent/100+halfProcessPictureWeight);
		//节点图片左边
		double mileLeft=Math.floor(getWidth()*milePercent/100-halfPictureWeight);
		//节点图片右边
		double mileRight=Math.ceil(getWidth()*milePercent/100+halfPictureWeight);
			
		if(mileLeft<processRight&&processRight<mileRight)//左碰撞
		{
			isCollision= -1;
		}
		else if(mProcessPictureWeight>mPictureWeight&&mileLeft>processLeft&&mileRight<processRight)//包含
		{
			return 0;
		}
		else if(mProcessPictureWeight<mPictureWeight&&mileLeft<processLeft&&mileRight>processRight)//包含
		{
			return 0;
		}
		return isCollision;
	}
	
	/**
	 * 图片水平镜像翻转
	 * TODO
	 * @param bmp
	 * @return
	 * @return Bitmap
	 * @author jiazhi.cao
	 * @time 下午1:46:55
	 */
	public Bitmap convertBmp(Bitmap bmp) {  
        int w = bmp.getWidth();  
        int h = bmp.getHeight();  
  
        Matrix matrix = new Matrix();  
        matrix.postScale(-1, 1); // 镜像水平翻转  
        Bitmap convertBmp = Bitmap.createBitmap(bmp, 0, 0, w, h, matrix, true);  
          
        return convertBmp;  
    } 
}
