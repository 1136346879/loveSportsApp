package cmccsi.mhealth.app.sports.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

/**
 * 使用时务必将bitmap传递进来，view内部不会decode bitmap
 * 
 * @author zy
 * 
 */
public class CenterRollingBall extends View {
    private int[]   colorArray = new int[] { Color.rgb(203, 203, 203),
            Color.rgb(168, 211, 59), Color.rgb(253, 230, 34),
            Color.rgb(244, 153, 17),

                               // Color.rgb(255, 200, 14),
                               // Color.rgb(200, 85, 80),
                               };
    private int     colorIndex;

    private Bitmap  mBallPic;
    private Bitmap  mYelPoint;
    // 分数
    private int     score      = 0;
    private int     maxScore   = 0;

    private float   scorePercent;
    private Paint   paint;
    private RectF   arcRectF;
    private float   centerOffest;
    private float   angleOffest;
    private boolean showFrontArc;
    private boolean showEndBall;


    public void showEndBall(Boolean showEndBall) {
        this.showEndBall = showEndBall;
    }

    public void showFrontArc(boolean showFrontArc) {
        this.showFrontArc = showFrontArc;
    }

    /**
     * 设置分数
     * 
     * @param score
     */
    public void setScore(int score) {
        this.score = score;
        initRatio();
    }

    public void setMaxScore(int maxScore) {
        this.maxScore = maxScore;
        initRatio();
    }

    public void setCenterOffest(float offest) {
        this.centerOffest =dip2px(offest);
    }

    public void setPics(Bitmap ball, Bitmap point) {
        this.mYelPoint = point;
        this.mBallPic = ball;
    }

    /**
     * 设置弧线的开始角度，默认为时钟的3点钟方向。当angleOffest为正数时，顺时针偏移开始角度；当angleOffest为负数时，逆时针偏移角度
     * 。
     * 
     * @param angleOffest
     */
    public void setAngelOffest(float angleOffest) {
        this.angleOffest = angleOffest;
    }

    /**
     * 重绘
     */
    public void reDraw() {
        postInvalidate();
    }

    public CenterRollingBall(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public CenterRollingBall(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * 普通构造函数，需要手动设置setPics 以及分数setScore，然后执行reDraw
     * 
     * @param context
     */
    public CenterRollingBall(Context context) {
        super(context);
        init();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec),
                MeasureSpec.getSize(heightMeasureSpec));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mBallPic == null || mYelPoint == null) {
            return;
        }
        float viewWidth = getWidth();
        float viewHeight = getHeight();
        if (getWidth() > getHeight()) {
            initArcRectF((viewWidth - viewHeight) / 2, 0, (viewWidth + viewHeight) / 2, viewHeight);
        } else {
            initArcRectF(0, (viewHeight - viewWidth) / 2, viewWidth, (viewHeight + viewWidth) / 2);
        }
        
        drawArcs(canvas);
        drawCenterCircle(canvas);
        if (showEndBall) {
          drawEndBall(canvas);
        }
    }
    
    /**
     * 初始化分数比例弧线的外切矩形
     * @param left
     * @param top
     * @param right
     * @param bottom
     */
    private void initArcRectF(float left,float top,float right,float bottom){
        arcRectF.left=left;
        arcRectF.right=right;
        arcRectF.top=top;
        arcRectF.bottom=bottom;
    }

    /** 画弧线结尾的圆
     * @param canvas
     */
    private void drawEndBall(Canvas canvas) {
        float endBallCX = (float) ((arcRectF.left + arcRectF.right) / 2 + (arcRectF.right-arcRectF.left - centerOffest)
                / 2
                * Math.cos(Math.PI * 2 * (scorePercent + angleOffest / 360)));
        float endBallCY = (float) ((arcRectF.top + arcRectF.bottom )/ 2 + (arcRectF.right-arcRectF.left - centerOffest)
                / 2
                * Math.sin(Math.PI * 2 * (scorePercent + angleOffest / 360)));
        paint.setColor(Color.WHITE);
        canvas.drawCircle(endBallCX, endBallCY, centerOffest / 2, paint);
        paint.setColor(Color.rgb(242, 151, 0));
        canvas.drawCircle(endBallCX, endBallCY, centerOffest / 2 - 4, paint);
    }

    /**画中间的圆
     * @param canvas
     * @param unit
     * @param centerCX
     * @param centerCY
     */
    private void drawCenterCircle(Canvas canvas) {
        if (centerOffest > (arcRectF.right-arcRectF.left) / 2) {
            centerOffest = 0;
        }
        paint.setColor(Color.WHITE);
        canvas.drawCircle((arcRectF.left+arcRectF.right)/2, (arcRectF.top+arcRectF.bottom)/2, (arcRectF.right-arcRectF.left) / 2 - centerOffest, paint);
    }

    /**画分数比例的弧线
     * @param canvas
     */
    private void drawArcs(Canvas canvas) {
        float sweepAngle = 360 * scorePercent;
        float frontArcAngle = 0;
        if (sweepAngle > 2 && showFrontArc) {
            frontArcAngle = 1;
            paint.setColor(Color.WHITE);
            canvas.drawArc(arcRectF, 0 + angleOffest, frontArcAngle, true,
                    paint);
        }
        paint.setColor(colorArray[colorIndex + 1]);
        canvas.drawArc(arcRectF, 0 + angleOffest + frontArcAngle, sweepAngle,
                true, paint);
        paint.setColor(colorArray[colorIndex]);
        canvas.drawArc(arcRectF, 360 * scorePercent + angleOffest,
                360 - 360 * scorePercent, true, paint);
    }

    /**
     * 初始化画笔
     */
    private void init() {
        initRatio();
        paint = new Paint();
        paint.setAntiAlias(true);
        arcRectF = new RectF();
    }

    /**
     * 初始化分数比例和颜色
     */
    private void initRatio() {
        if (maxScore == 0) {
            maxScore = 10000;
        }
        colorIndex = score / maxScore;
        scorePercent = score / (float) maxScore;
        scorePercent = colorIndex > 2 ? 1 : scorePercent - colorIndex;
        colorIndex = colorIndex > 2 ? 2 : colorIndex;
    }
    
    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public  float dip2px(float dpValue) {
        final float scale = getContext().getResources().getDisplayMetrics().density;
        return dpValue * scale + 0.5f;
    }
}
