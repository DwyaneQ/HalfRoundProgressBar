package com.dwq.arcprogressbar;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;


/**
 * Created by DWQ on 2018/5/14.
 * E-Mail:lomapa@163.com
 */
public class ArcProgressBar extends View {

    private Paint mTextPaint;
    private Paint mDottedLinePaint;
    private Paint mRoundRectPaint;
    private Paint mProgressPaint;
    private RectF mArcRect;
    /**
     * 虚线默认颜色
     */
    private int mDottedDefaultColor = 0xFF8D99A1;
    /**
     * 虚线变动颜色
     */
    private int mDottedRunColor = 0xFFf0724f;
    /**
     * 底部默认文字
     */
    private String mBottomText = "底部文字";
    /**
     * 线条数
     */
    private int mDottedLineCount = 100;
    /**
     * 线条宽度
     */
    private int mDottedLineWidth = 45;
    /**
     * 线条高度
     */
    private int mDottedLineHeight = 12;
    /**
     * 进度条最大值
     */
    private int mProgressMax = 100;
    /**
     * 进度文字大小
     */
    private int mProgressTextSize = 45;
    /**
     * 进度文字颜色
     */
    private int progressTextColor = 0xFFEE2550;
    /**
     * 进度描述
     */
    private String mProgressDesc;

    private int mProgress;
    private float mExternalDottedLineRadius;
    private float mInsideDottedLineRadius;
    private int mArcCenterX;
    private int mArcRadius; // 圆弧半径
    private boolean isRestart = false;
    private int mRealProgress;

    public ArcProgressBar(Context context) {
        this(context, null, 0);
    }

    public ArcProgressBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ArcProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        intiAttributes(context, attrs);
        initPaint();
    }

    private void intiAttributes(Context context, AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ArcProgressBar);
        mDottedDefaultColor = a.getColor(R.styleable.ArcProgressBar_dottedDefaultColor, mDottedDefaultColor);
        mDottedRunColor = a.getColor(R.styleable.ArcProgressBar_dottedRunColor, mDottedRunColor);
        mDottedLineCount = a.getInteger(R.styleable.ArcProgressBar_dottedLineCount, mDottedLineCount);
        mDottedLineWidth = a.getInteger(R.styleable.ArcProgressBar_dottedLineWidth, mDottedLineWidth);
        mDottedLineHeight = a.getInteger(R.styleable.ArcProgressBar_dottedLineHeight, mDottedLineHeight);
        mProgressMax = a.getInteger(R.styleable.ArcProgressBar_progressMax, mProgressMax);
        mProgressTextSize = a.getInteger(R.styleable.ArcProgressBar_progressTextSize, mProgressTextSize);
        mProgressDesc = a.getString(R.styleable.ArcProgressBar_progressDesc);
        mBottomText = a.getString(R.styleable.ArcProgressBar_arcText);
        if (TextUtils.isEmpty(mBottomText)) {
            mBottomText = "底部文字";
        }
        a.recycle();
    }

    private void initPaint() {
        //底部文字Paint
        mTextPaint = new Paint();
        mTextPaint.setAntiAlias(true);
        mTextPaint.setTextSize(dp2px(getResources(), 15));
        mTextPaint.setColor(progressTextColor);
        // Default虚线Paint
        mDottedLinePaint = new Paint();
        mDottedLinePaint.setAntiAlias(true);
        mDottedLinePaint.setStrokeWidth(mDottedLineHeight);
        mDottedLinePaint.setStrokeCap(Paint.Cap.ROUND);
        mDottedLinePaint.setColor(mDottedDefaultColor);
        // 选中效果虚线Paint
        mRoundRectPaint = new Paint();
        mRoundRectPaint.setAntiAlias(true);
        mRoundRectPaint.setColor(mDottedRunColor);
        mRoundRectPaint.setStyle(Paint.Style.FILL);
        // 中间进度文字Paint
        mProgressPaint = new Paint();
        mProgressPaint.setAntiAlias(true);
        mProgressPaint.setColor(progressTextColor);
        mProgressPaint.setTextSize(dp2px(getResources(), mProgressTextSize));
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(getMeasuredWidth(), getMeasuredWidth());
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mArcCenterX = (int) (w / 2.0f);

        mArcRect = new RectF();
        mArcRect.top = 0;
        mArcRect.left = 0;
        mArcRect.right = w * 0.9f;// 等于w时，虚线会缺失
        mArcRect.bottom = h * 0.9f;

        mArcRadius = (int) (mArcRect.width() / 2);

        // 虚线的外半径
        mExternalDottedLineRadius = mArcRadius;
        // 虚线的内半径
        mInsideDottedLineRadius = mExternalDottedLineRadius - mDottedLineWidth;

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawDottedLineArc(canvas);
        drawRunDottedLineArc(canvas);
        drawRunText(canvas);
        if (isRestart) {
            drawDottedLineArc(canvas);
        }
    }


    private void drawRunText(Canvas canvas) {
        String progressStr = "0";
        if (!TextUtils.isEmpty(mProgressDesc)) {
            progressStr = mProgressDesc;
        }
        // 绘制进度文字
        canvas.drawText(progressStr, mArcCenterX - mProgressPaint.measureText(progressStr) / 2,
                mArcCenterX - (mProgressPaint.descent() + mProgressPaint.ascent()) / 2, mProgressPaint);

        canvas.drawText(mBottomText, getWidth() / 2 - mTextPaint.measureText(mBottomText) / 2,
                mArcCenterX + mArcRadius - (mTextPaint.descent() + mTextPaint.ascent()) - dp2px(getResources(), 25), mTextPaint);

    }

    /**
     * 重置
     */
    public void restart() {
        isRestart = true;
        this.mRealProgress = 0;
        this.mProgressDesc = "";
        invalidate();
    }

    /**
     * 设置中间进度描述
     *
     * @param desc
     */
    public void setProgressDesc(String desc) {
        this.mProgressDesc = desc;
        postInvalidate();
    }

    /**
     * 设置最大进度
     *
     * @param max
     */
    public void setMaxProgress(int max) {
        this.mProgressMax = max;
    }

    /**
     * 设置当前进度
     *
     * @param progress
     */
    public void setProgress(int progress) {
        // 进度100% = 控件的5/6
        this.mRealProgress = progress;
        isRestart = false;
        if (progress != 100) {
            this.mProgress = ((mDottedLineCount * 5 / 6) * progress) / mProgressMax;
        } else {
            this.mProgress = 51;
        }
        postInvalidate();
    }

    private void drawRunDottedLineArc(Canvas canvas) {
        mDottedLinePaint.setColor(mDottedRunColor);
        float everyDegrees = (float) (2.0f * Math.PI / mDottedLineCount);

        float startDegrees = (float) (210 * Math.PI / 180);

        for (int i = 0; i < mProgress; i++) {
            float degrees = i * everyDegrees + startDegrees;

            float startX = mArcCenterX + (float) Math.sin(degrees) * mInsideDottedLineRadius;
            float startY = mArcCenterX - (float) Math.cos(degrees) * mInsideDottedLineRadius;

            float stopX = mArcCenterX + (float) Math.sin(degrees) * mExternalDottedLineRadius;
            float stopY = mArcCenterX - (float) Math.cos(degrees) * mExternalDottedLineRadius;

            canvas.drawLine(startX, startY, stopX, stopY, mDottedLinePaint);
        }
    }

    private void drawDottedLineArc(Canvas canvas) {
        mDottedLinePaint.setColor(mDottedDefaultColor);
        // 360 * Math.PI / 180
        float everyDegrees = (float) (2.0f * Math.PI / mDottedLineCount);

        float startDegrees = (float) (150 * Math.PI / 180);
        float endDegrees = (float) (210 * Math.PI / 180);

        for (int i = 0; i < mDottedLineCount; i++) {
            float degrees = i * everyDegrees;
            // 过滤底部60度的弧长
            if (degrees > startDegrees && degrees < endDegrees) {
                continue;
            }

            float startX = mArcCenterX + (float) Math.sin(degrees) * mInsideDottedLineRadius;
            float startY = mArcCenterX - (float) Math.cos(degrees) * mInsideDottedLineRadius;

            float stopX = mArcCenterX + (float) Math.sin(degrees) * mExternalDottedLineRadius;
            float stopY = mArcCenterX - (float) Math.cos(degrees) * mExternalDottedLineRadius;


            canvas.drawLine(startX, startY, stopX, stopY, mDottedLinePaint);
        }
    }

    private float dp2px(Resources resources, float dp) {
        final float scale = resources.getDisplayMetrics().density;
        return dp * scale + 0.5f;
    }
}
