package com.zx.zx2000tvfileexploer.view;

import android.animation.FloatEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

import com.zx.zx2000tvfileexploer.R;
import com.zx.zx2000tvfileexploer.utils.Logger;

import java.text.DecimalFormat;


/**
 * Created by ShaudXiao on 2016/7/19.
 */
public class RoundProgressBar extends View {

    private Paint paint;

    private long max;
    private long progress;
    private int startAngle;
    private int ringColor;
    private int ringProgressColor;
    private float ringWidth;
    private int centreColor;
    private boolean textIsDisplayable;
    private String text;
    private float textSize;
    private int textColor;
    private int style;

    public final static int STROKE = 0;
    public final static int FILL = 1;

    private int stepnumber, stepnumbernow;
    private float pressExtraStrokeWidth;
    private int stepnumbermax = 12;// 默认最大时间

    private ValueAnimator mAnimator;
    private float mLastProgress;

    private float mSweepAnglePer;
    private float mPercent;

    private DecimalFormat fnum = new DecimalFormat("#.0");// 格式为保留小数点后一位

    public RoundProgressBar(Context context) {
        this(context, null);
    }

    public RoundProgressBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RoundProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        paint = new Paint();

        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.RoundProgressBar);

        if (ta != null) {
            max = ta.getInt(R.styleable.RoundProgressBar_max, 100);
            progress = ta.getInt(R.styleable.RoundProgressBar_progress, 20);
            startAngle = ta.getInt(R.styleable.RoundProgressBar_startAngle, -90);
            ringColor = ta.getColor(R.styleable.RoundProgressBar_ringColor, 0xFf909080);
            centreColor = ta.getColor(R.styleable.RoundProgressBar_centreColor, 0);
            ringProgressColor = ta.getColor(R.styleable.RoundProgressBar_ringProgressColor, 0xFF880000);
            ringWidth = ta.getDimensionPixelSize(R.styleable.RoundProgressBar_ringWidth, 20);
            textIsDisplayable = ta.getBoolean(R.styleable.RoundProgressBar_textIsDisplayable, false);
            textSize = ta.getDimensionPixelSize(R.styleable.RoundProgressBar_textSize, 40);
            textColor = ta.getColor(R.styleable.RoundProgressBar_textColor, 0xfff8f8f8);
            style = ta.getInt(R.styleable.RoundProgressBar_style, 0);

        }

        ta.recycle();

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);


        int centre = getWidth() / 2; //获取圆心的x坐标
        int radius = (int) (centre - ringWidth / 2); //圆环的半径

        /**
         * 画中心的颜色
         */
        if (centreColor != 0) {
            paint.setAntiAlias(true);
            paint.setColor(centreColor);
            paint.setStyle(Paint.Style.FILL);
            canvas.drawCircle(centre, centre, radius, paint);
        }

        /**
         * 画最外层的大圆环
         */
        paint.setColor(ringColor); //设置圆环的颜色
        paint.setStyle(Paint.Style.STROKE); //设置空心
        paint.setStrokeWidth(ringWidth); //设置圆环的宽度
        paint.setAntiAlias(true);  //消除锯齿
        canvas.drawCircle(centre, centre, radius, paint); //画出圆环

        /**
         * 画圆弧 ，画圆环的进度
         */
        //设置进度是实心还是空心
        paint.setStrokeWidth(ringWidth); //设置圆环的宽度
        paint.setColor(ringProgressColor);  //设置进度的颜色
        RectF oval = new RectF(centre - radius, centre - radius, centre
                + radius, centre + radius);  //用于定义的圆弧的形状和大小的界限

        switch (style) {
            case STROKE: {
                paint.setStyle(Paint.Style.STROKE);

                /*第二个参数是进度开始的角度，-90表示从12点方向开始走进度，如果是0表示从三点钟方向走进度，依次类推
                 *public void drawArc(RectF oval, float startAngle, float sweepAngle, boolean useCenter, Paint paint)
                    oval :指定圆弧的外轮廓矩形区域。
                    startAngle: 圆弧起始角度，单位为度。
                    sweepAngle: 圆弧扫过的角度，顺时针方向，单位为度。
                    useCenter: 如果为True时，在绘制圆弧时将圆心包括在内，通常用来绘制扇形。
                    paint: 绘制圆弧的画板属性，如颜色，是否填充等
                 *
                */
                canvas.drawArc(oval, startAngle, 360 * progress / max, false, paint);  //根据进度画圆弧
                break;
            }
            case FILL: {
                paint.setStyle(Paint.Style.FILL_AND_STROKE);
                if (progress != 0)
                    canvas.drawArc(oval, startAngle, 360 * progress / max, true, paint);  //根据进度画圆弧
                break;
            }
        }

        /**
         * 画进度百分比
         */
        paint.setStrokeWidth(0);
        paint.setColor(textColor);
        paint.setTextSize(textSize);
        paint.setTypeface(Typeface.DEFAULT_BOLD); //设置字体
        int percent = (int) (((float) progress / (float) max) * 100);  //中间的进度百分比，先转换成float在进行除法运算，不然都为0
        float textWidth = paint.measureText(percent + "%");   //测量字体宽度，我们需要根据字体的宽度设置在圆环中间

        if (textIsDisplayable && percent != 0 && style == STROKE) {
            canvas.drawText(percent + "%", centre - textWidth / 2, centre + textSize / 2, paint); //画出进度百分比
        }


    }

    public synchronized long getMax() {
        return max;
    }

    /**
     * 设置进度的最大值
     *
     * @param max
     */
    public synchronized void setMax(long max) {
        if (max < 0) {
            throw new IllegalArgumentException("max not less than 0");
        }
        this.max = max;
        Logger.getLogger().d("max " + max);
    }

    /**
     * 获取进度.需要同步
     *
     * @return
     */
    public synchronized float getProgress() {
        return progress;
    }

    /**
     * 设置进度，此为线程安全控件，由于考虑多线的问题，需要同步
     * 刷新界面调用postInvalidate()能在非UI线程刷新
     *
     * @param progress
     */
    public synchronized void setProgress(long progress) {
        if (progress < 0) {
            throw new IllegalArgumentException("progress not less than 0");
        }
        if (progress > max) {
            progress = max;
        }
        if (progress <= max) {
            this.progress = progress;
            postInvalidate();
        }
    }


    public int getCircleColor() {
        return ringColor;
    }

    public void setCircleColor(int CircleColor) {
        this.ringColor = CircleColor;
    }

    public int getCircleProgressColor() {
        return ringProgressColor;
    }

    public void setCircleProgressColor(int CircleProgressColor) {
        this.ringProgressColor = CircleProgressColor;
    }

    public int getTextColor() {
        return textColor;
    }

    public void setTextColor(int textColor) {
        this.textColor = textColor;
    }

    public float getTextSize() {
        return textSize;
    }

    public void setTextSize(float textSize) {
        this.textSize = textSize;
    }

    public float getringWidth() {
        return ringWidth;
    }

    public void setringWidth(float ringWidth) {
        this.ringWidth = ringWidth;
    }

    /**
     * 开始执行动画
     *
     * @param targetProgress 最终到达的进度
     */
    public void runAnimate(long targetProgress, long time) {
        // 运行之前，先取消上一次动画
        cancelAnimate();

        mLastProgress = targetProgress;

        mAnimator = ValueAnimator.ofObject(new FloatEvaluator(), 0, targetProgress);
        // 设置差值器
        mAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (float) animation.getAnimatedValue();
                setProgress((long)value);
            }
        });
//        if(targetProgress > 10000) {
//            mAnimator.setDuration((long) (targetProgress * 33000));
//        } else if(targetProgress > 1000) {
//            mAnimator.setDuration((long) (targetProgress * 3300));
//        } else {
//            mAnimator.setDuration((long) (targetProgress * 33));
//        }
//        mAnimator.setDuration((long) (targetProgress * 33));
        mAnimator.setDuration(time);
        mAnimator.start();
    }

    /**
     * 取消动画
     */
    public void cancelAnimate() {
        if (mAnimator != null && mAnimator.isRunning()) {
            mAnimator.cancel();
        }
    }


}
