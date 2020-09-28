package org.zhx.common.guideview;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import androidx.annotation.ColorInt;
import androidx.annotation.IdRes;

/**
 * @ProjectName: guideView
 * @Package: org.zhx.common.guideview
 * @ClassName: GuidanceLayout
 * @Description:java
 * @Author: zhouxue
 * @CreateDate: 2020/9/27 15:17
 * @UpdateUser:
 * @UpdateDate: 2020/9/27 15:17
 * @UpdateRemark:
 * @Version:1.0
 */
public class GuidanceLayout extends RelativeLayout  {

    private final String TAG = "GuideView";

    private boolean hasMeasure = false;
    private boolean hasAddGuidanceView = false;
    private boolean isShowing = false;
    private int mTargetPaddingBottom;
    private int mTargetMargin;
    private int mTargetMarginLeft;
    private int mTargetMarginRight;
    private int mTargetMarginTop;
    private int mTargetMarginBottom;
    private int mGuidanceViewMargin;  //params.mGuidanceViewSpace和它意义相同，但mGuidanceViewMargin会覆盖其他的margin类型
    private int mGuidanceViewMarginLeft;
    private int mGuidanceViewMarginRight;
    private int mGuidanceViewMarginTop;
    private int mGuidanceViewMarginBottom;
    private int[] mTargetViewLocation = new int[2];
    private int mTargetViewWidth;
    private int mTargetViewHeight;
    private int mGuidanceViewDirection;

    private int mScreenWidth;
    private int mScreenHeight;
    private
    @ColorInt
    int MASK_LAYER_COLOR = 0xd9000000;  //遮罩层默认颜色

    private LayoutParams mHintLayoutParams;
    private int mOffsetX;
    private int mFormType;

    private Paint mBackgroundPaint;  //遮罩层画笔
    private Paint mTargetPaint;  //透明椭圆画笔

    private FrameLayout mDecorView;
    private int mTargetViewLocationX;
    private Bitmap mShowBitmap;
    private Builder.GuideViewParams mParams;

    public GuidanceLayout(Context context) {
        this(context, null);
    }

    public GuidanceLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public GuidanceLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        mScreenWidth = displayMetrics.widthPixels;
        mScreenHeight = displayMetrics.heightPixels;
        mDecorView = (FrameLayout) ((Activity) getContext()).getWindow().getDecorView();
        mBackgroundPaint = new Paint();
        mTargetPaint = new Paint();
        mBackgroundPaint.setColor(MASK_LAYER_COLOR);
    }

    public void initParams(Builder.GuideViewParams params) {
        Log.i(TAG, "initParams");
        this.mParams=params;
        mTargetPaddingBottom = params.mTargetPaddingBottom;
        mTargetMargin = params.mTargetMargin;
        mTargetMarginLeft = params.mTargetMarginLeft;
        mTargetMarginRight = params.mTargetMarginRight;
        mTargetMarginTop = params.mTargetMarginTop;
        mTargetMarginBottom = params.mTargetMarginBottom;
        mGuidanceViewMargin = params.mGuidanceViewMargin;  //params.mGuidanceViewSpace和它意义相同，但mGuidanceViewMargin会覆盖其他的margin类型
        mGuidanceViewMarginLeft = params.mGuidanceViewMarginLeft;
        mGuidanceViewMarginRight = params.mGuidanceViewMarginRight;
        mGuidanceViewMarginTop = params.mGuidanceViewMarginTop;
        mGuidanceViewMarginBottom = params.mGuidanceViewMarginBottom;
        mGuidanceViewDirection = params.mDirection;
        MASK_LAYER_COLOR = params.MASK_LAYER_COLOR;  //遮罩层默认颜色
        mHintLayoutParams = params.mHintLayoutParams;
        mFormType = params.mForm;
        mOffsetX = params.mOffsetX;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        Log.i(TAG, " --- onMeasure");
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        Log.i(TAG, " --- onLayout");
        super.onLayout(changed, l, t, r, b);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        drawBackgroud(canvas.getWidth(), canvas.getHeight());
        //绘制到GuideView的画布上
        canvas.drawBitmap(mShowBitmap, 0, 0, mBackgroundPaint);
    }

    public void drawBackgroud(int width, int height) {
        //先绘制遮罩层
        mShowBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas mTemp = new Canvas(mShowBitmap);
        mTemp.drawRect(0, 0, width, height, mBackgroundPaint);
        PorterDuffXfermode mDrawMode = new PorterDuffXfermode(PorterDuff.Mode.SRC_OUT);
        mTargetPaint.setXfermode(mDrawMode);
        mTargetPaint.setAntiAlias(true);

        /**
         * 透明区域Margin设置
         */
        if (mTargetMargin != 0) {
            mTargetMarginLeft = mTargetMargin;
            mTargetMarginRight = mTargetMargin;
            mTargetMarginTop = mTargetMargin;
            mTargetMarginBottom = mTargetMargin;
        }
        /**
         * GuidanceView margin设置
         */
        if (mGuidanceViewMargin != 0) {
            mGuidanceViewMarginLeft = mGuidanceViewMargin;
            mGuidanceViewMarginRight = mGuidanceViewMargin;
            mGuidanceViewMarginTop = mGuidanceViewMargin;
            mGuidanceViewMarginBottom = mGuidanceViewMargin;
        }

        if (!"0".equals(mOffsetX)) {
            mTargetViewLocationX = mTargetViewLocation[0] + mOffsetX;
        } else {
            mTargetViewLocationX = mTargetViewLocation[0];
        }

        RectF rectF = new RectF(mTargetViewLocationX - mParams.mTargetPadding + mTargetMarginLeft,
                mTargetViewLocation[1] - mParams.mTargetPaddingTop + mTargetMarginTop,
                mTargetViewLocationX + mTargetViewWidth + mParams.mTargetPaddingRight - mTargetMarginRight,
                mTargetViewLocation[1] + mTargetViewHeight + mTargetPaddingBottom - mTargetMarginBottom);

        switch (mFormType) {
            case Form.CIRCLE:
                mTemp.drawCircle(mTargetViewLocationX + mTargetViewWidth / 2, mTargetViewLocation[1] + mTargetViewWidth / 2, mTargetViewWidth / 2, mTargetPaint);
                break;
            case Form.ELLIPSE:
                mTemp.drawOval(rectF, mTargetPaint);
                break;
            case Form.REACTANGLE:
                mTemp.drawRect(mTargetViewLocationX, mTargetViewLocation[1], mTargetViewLocationX + mTargetViewWidth, mTargetViewLocation[1] + mTargetViewHeight, mTargetPaint);
                break;
            case Form.CIRCLE_LONG:
                int longRadius = Math.max(mTargetViewWidth, mTargetViewHeight);
                mTemp.drawCircle(mTargetViewLocation[0] + mTargetViewWidth / 2, mTargetViewLocation[1] + mTargetViewWidth / 2, longRadius / 2, mTargetPaint);
                break;
            case Form.CIRCLE_SHORT:
                int shortRadius = Math.min(mTargetViewWidth, mTargetViewHeight);
                mTemp.drawCircle(mTargetViewLocation[0] + mTargetViewWidth / 2, mTargetViewLocation[1] + mTargetViewWidth / 2, shortRadius / 2, mTargetPaint);
                break;
        }

    }


    /**
     * 添加GuidanceView
     */
    private void addGuidanceView() {
        if (hasAddGuidanceView) {
            return;
        }
        if (mParams.mGuidanceView != null) {

            LayoutParams layoutParams;
            if (mHintLayoutParams != null) {
                layoutParams = mHintLayoutParams;
            } else {
                layoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT,
                        LayoutParams.WRAP_CONTENT);
            }

            switch (mGuidanceViewDirection) {
                //左边相关
                case Direction.LEFT:
                    this.setGravity(Gravity.RIGHT);
                    layoutParams.setMargins(0, mTargetViewLocation[1],
                            mScreenWidth - mTargetViewLocation[0] + mParams.mGuidanceViewSpace + mGuidanceViewMarginRight, 0);
                    break;
                case Direction.LEFT_BOTTOM:
                    this.setGravity(Gravity.RIGHT | Gravity.TOP);
                    layoutParams.setMargins(0, mTargetViewLocation[1] + mTargetViewHeight + mParams.mGuidanceViewSpace + mGuidanceViewMarginTop,
                            mScreenWidth - mTargetViewLocation[0] + mParams.mGuidanceViewSpace + mGuidanceViewMarginRight, 0);
                    break;
                case Direction.LEFT_ABOVE:
                    this.setGravity(Gravity.RIGHT | Gravity.BOTTOM);
                    layoutParams.setMargins(0, 0, mScreenWidth - mTargetViewLocation[0] + mParams.mGuidanceViewSpace + mGuidanceViewMarginRight,
                            mScreenHeight - mTargetViewLocation[1] + mParams.mGuidanceViewSpace + mGuidanceViewMarginBottom);
                    break;
                case Direction.LEFT_ALIGN_BOTTOM:
                    this.setGravity(Gravity.RIGHT | Gravity.BOTTOM);
                    layoutParams.setMargins(0, mTargetViewLocation[1],
                            mScreenWidth - mTargetViewLocation[0] + mParams.mGuidanceViewSpace + mGuidanceViewMarginRight,
                            mScreenHeight - mTargetViewLocation[1] - mTargetViewHeight);
                    break;

                //右边相关
                case Direction.RIGHT:
                    this.setGravity(Gravity.LEFT);
                    layoutParams.setMargins(mTargetViewLocation[0] + mTargetViewWidth + mParams.mGuidanceViewSpace + mGuidanceViewMarginLeft,
                            mTargetViewLocation[1], 0, 0);
                    break;
                case Direction.RIGHT_ABOVE:
                    this.setGravity(Gravity.LEFT | Gravity.BOTTOM);
                    layoutParams.setMargins(mTargetViewWidth + mTargetViewLocation[0] + mParams.mGuidanceViewSpace + mGuidanceViewMarginLeft, 0,
                            0, mScreenHeight - mTargetViewLocation[1] + mParams.mGuidanceViewSpace + mGuidanceViewMarginBottom);
                    break;
                case Direction.RIGHT_BOTTOM:
                    this.setGravity(Gravity.LEFT | Gravity.TOP);
                    layoutParams.setMargins(mTargetViewLocation[0] + mTargetViewWidth + mParams.mGuidanceViewSpace + mGuidanceViewMarginLeft,
                            mTargetViewLocation[1] + mTargetViewHeight + mParams.mGuidanceViewSpace + mGuidanceViewMarginTop, 0, 0);
                    break;
                case Direction.RIGHT_ALIGN_BOTTOM:
                    this.setGravity(Gravity.LEFT | Gravity.BOTTOM);
                    layoutParams.setMargins(mTargetViewLocation[0] + mTargetViewWidth + mParams.mGuidanceViewSpace + mGuidanceViewMarginLeft,
                            0, 0, mScreenHeight - mTargetViewLocation[1] - mTargetViewHeight + mGuidanceViewMarginBottom);
                    break;

                //上方相关
                case Direction.ABOVE:
                    this.setGravity(Gravity.BOTTOM);
                    layoutParams.setMargins(0, 0,
                            0, mScreenHeight - mTargetViewLocation[1] + mParams.mGuidanceViewSpace + mGuidanceViewMarginBottom);
                    break;
                case Direction.ABOVE_ALIGN_LEFT:
                    this.setGravity(Gravity.BOTTOM | Gravity.LEFT);
                    layoutParams.setMargins(mTargetViewLocation[0] + mGuidanceViewMarginLeft, 0, 0,
                            mScreenHeight - mTargetViewLocation[1] + mParams.mGuidanceViewSpace + mGuidanceViewMarginBottom);
                    break;
                case Direction.ABOVE_ALIGN_RIGHT:
                    this.setGravity(Gravity.BOTTOM | Gravity.RIGHT);
                    layoutParams.setMargins(0, 0, mScreenWidth - mTargetViewLocation[0] - mTargetViewWidth + mGuidanceViewMarginRight,
                            mScreenHeight - mTargetViewLocation[1] + mParams.mGuidanceViewSpace + mGuidanceViewMarginBottom);
                    break;

                //下方相关
                case Direction.BOTTOM:
                    this.setGravity(Gravity.TOP);
                    layoutParams.setMargins(0, mTargetViewLocation[1] + mTargetViewHeight + mGuidanceViewMarginTop, 0, 0);
                    break;
                case Direction.BOTTOM_ALIGN_LEFT:
                    this.setGravity(Gravity.TOP | Gravity.LEFT);
                    layoutParams.setMargins(mTargetViewLocation[0] + mGuidanceViewMarginLeft,
                            mTargetViewLocation[1] + mTargetViewHeight + mParams.mGuidanceViewSpace + mGuidanceViewMarginTop, 0, 0);
                    break;
                case Direction.BOTTOM_ALIGN_RIGHT:
                    this.setGravity(Gravity.TOP | Gravity.RIGHT);
                    layoutParams.setMargins(0, mTargetViewLocation[1] + mTargetViewHeight + mParams.mGuidanceViewSpace + mGuidanceViewMarginTop,
                            mScreenWidth - mTargetViewLocation[0] - mTargetViewWidth + mGuidanceViewMarginRight, 0);
            }
            this.addView(mParams.mGuidanceView, layoutParams);
            hasAddGuidanceView = true;
        }
    }

    public void hide() {
        this.removeAllViews();
        mDecorView.removeView(this);
    }

    public void show() {
        if (isShowing || !hasMeasure) {
            return;
        }
        this.setBackgroundColor(Color.TRANSPARENT);
        addGuidanceView();
        mDecorView.addView(this);
        isShowing = true;
    }

    public boolean isShowing() {
        return isShowing;
    }

    public void getTargetViewPosition() {
        if (mParams.mTargetView.getWidth() > 0 && mParams.mTargetView.getHeight() > 0) {
            mParams.mTargetView.getLocationInWindow(mTargetViewLocation);
            if (mTargetViewWidth == 0 || mTargetViewHeight == 0) {
                mTargetViewWidth = mParams.mTargetView.getWidth();
                mTargetViewHeight = mParams.mTargetView.getHeight();
            }
            if (mTargetViewLocation[0] >= 0 && mTargetViewLocation[1] > 0) {
                hasMeasure = true;
            }
        } else {
            hasMeasure = false;
        }
    }


    /**
     * 通过Builder构建
     */
    public static class Builder {
        private static class GuideViewParams {
            View mTargetView;
            View mGuidanceView;
            int mDirection;
            int mGuidanceViewSpace = 20; //GuidanceView和TargetView间距,默认20px
            int mTargetPadding;
            int mTargetPaddingLeft;
            int mTargetPaddingTop;
            int mTargetPaddingRight;
            int mTargetPaddingBottom;
            int mTargetMargin;
            int mTargetMarginLeft;
            int mTargetMarginRight;
            int mTargetMarginTop;
            int mTargetMarginBottom;
            int mGuidanceViewMargin;  //params.mGuidanceViewSpace和它意义相同
            int mGuidanceViewMarginLeft;
            int mGuidanceViewMarginRight;
            int mGuidanceViewMarginTop;
            int mGuidanceViewMarginBottom;
            int mForm;
            int mOffsetX;


            @ColorInt
            int MASK_LAYER_COLOR = 0xcc1D1C1C;  //遮罩层默认颜色
            LayoutParams mHintLayoutParams;
            OnClickListener mClickListener;
        }

        private GuideViewParams mParams;
        private Activity activity;

        public Builder(Activity ctx) {
            mParams = new GuideViewParams();
            activity = ctx;
        }

        /**
         * 设置位移
         *
         * @param i
         * @return
         */
        public Builder setTranslateX(int i) {
            mParams.mOffsetX = i;
            return this;
        }

        public Builder anchorView(View targetView) {
            mParams.mTargetView = targetView;
            return this;
        }

        public Builder setForm(int mForm) {
            mParams.mForm = mForm;
            return this;
        }

        public Builder anchorView(@IdRes int resId) {
            mParams.mTargetView = ((Activity) activity).findViewById(resId);
            return this;
        }

        public Builder setGuidanceView(View GuidanceView) {
            mParams.mGuidanceView = GuidanceView;
            return this;
        }

        public Builder setGuidanceViewDirection(int direction) {
            mParams.mDirection = direction;
            return this;
        }

        public Builder setTargetOvalPadding(int px) {
            mParams.mTargetPadding = px;
            return this;
        }

        public Builder setTargetOvalPaddingLeft(int px) {
            mParams.mTargetPaddingLeft = px;
            return this;
        }

        public Builder setTargetOvalPaddingRight(int px) {
            mParams.mTargetPaddingRight = px;
            return this;
        }

        public Builder setTargetOvalPaddingTop(int px) {
            mParams.mTargetPaddingTop = px;
            return this;
        }

        public Builder setTargetOvalPaddingBottom(int px) {
            mParams.mTargetPaddingBottom = px;
            return this;
        }

        public Builder setTargetMargin(int px) {
            mParams.mTargetMargin = px;
            return this;
        }

        public Builder setTargetMarginLeft(int mTargetMarginLeft) {
            mParams.mTargetMarginLeft = mTargetMarginLeft;
            return this;
        }

        public Builder setTargetMarginRight(int mTargetMarginRight) {
            mParams.mTargetMarginRight = mTargetMarginRight;
            return this;
        }

        public Builder setTargetMarginTop(int mTargetMarginTop) {
            mParams.mTargetMarginTop = mTargetMarginTop;
            return this;
        }

        public Builder setTargetMarginBottom(int mTargetMarginBottom) {
            mParams.mTargetMarginBottom = mTargetMarginBottom;
            return this;
        }

        public Builder setGuidanceViewMargin(int px) {
            mParams.mGuidanceViewMargin = px;
            return this;
        }

        public Builder setGuidanceViewMarginLeft(int px) {
            mParams.mGuidanceViewMarginLeft = px;
            return this;
        }

        public Builder setGuidanceViewMarginRight(int px) {
            mParams.mGuidanceViewMarginRight = px;
            return this;
        }

        public Builder setGuidanceViewMarginTop(int px) {
            mParams.mGuidanceViewMarginTop = px;
            return this;
        }

        public Builder setGuidanceViewMarginBottom(int px) {
            mParams.mGuidanceViewMarginBottom = px;
            return this;
        }

        public Builder setGuidanceViewSpace(int px) {
            mParams.mGuidanceViewSpace = px;
            return this;
        }

        public Builder setBackgroundColor(@ColorInt int color) {
            mParams.MASK_LAYER_COLOR = color;
            return this;
        }

        public Builder setHintLayoutParams(LayoutParams mHintLayoutParams) {
            mParams.mHintLayoutParams = mHintLayoutParams;
            return this;
        }

        public Builder setOnClickListener(OnClickListener listener) {
            mParams.mClickListener = listener;
            return this;
        }

        public GuidanceLayout Build() {
            if (mParams.mTargetView == null) {
                throw new RuntimeException("please set a targetView");
            }
            GuidanceLayout guideView = new GuidanceLayout(activity);
            guideView.initParams(mParams);
            guideView.setOnClickListener(mParams.mClickListener);
            guideView.getTargetViewPosition(); //获取TargetView的位置
            return guideView;
        }

        public void show() {
            Build().show();
        }
    }

    public interface Direction {

        int LEFT = 10; //左边，默认上边对齐
        int LEFT_ABOVE = 11;  //左上方
        int LEFT_BOTTOM = 12; //左下方
        int LEFT_ALIGN_BOTTOM = 13; //左方并且下边对齐

        int RIGHT = 20; //右边
        int RIGHT_ABOVE = 21; //右上方
        int RIGHT_BOTTOM = 22; //右下方
        int RIGHT_ALIGN_BOTTOM = 23; //右方并且下边对齐

        int ABOVE = 30; //正上方
        int ABOVE_ALIGN_LEFT = 31; //上方并且左对齐
        int ABOVE_ALIGN_RIGHT = 32; //上方并且右对齐

        int BOTTOM = 40; //正下方
        int BOTTOM_ALIGN_LEFT = 41; //下方并且左对齐
        int BOTTOM_ALIGN_RIGHT = 42; //下方并且右对齐
    }

    public interface Form {
        int CIRCLE = 0; //园
        int CIRCLE_LONG = 11;  //长边圆
        int CIRCLE_SHORT = 12; //短边圆
        int ELLIPSE = 1; //椭圆
        int REACTANGLE = 2; //矩形
    }
}