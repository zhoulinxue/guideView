/*
 * Copyright (c) 2015-2020 Founder Ltd. All Rights Reserved.
 *
 *zhx for  org
 *
 *
 */

package org.zhx.common.guideview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;


/**
 * 希望有一天可以开源出来  org.zhx
 *
 * @author zhx
 * @version 1.0, 2015-11-15 下午7:11:49
 */
public class GuideView extends View {
    private Paint mAreaPaint;
    private Rect mCenterRect = null;
    private Context mContext;

    public GuideView(Context context) {
        this(context,null);
    }

    public GuideView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initPaint();
        mContext = context;
        Point p = DisplayUtil.getScreenMetrics(mContext);
        width = p.x;
        height = p.y;
    }

    private void initPaint() {
        // 绘制四周阴影区域
        mAreaPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mAreaPaint.setColor(Color.GRAY);
        mAreaPaint.setStyle(Style.FILL);
        mAreaPaint.setAlpha(100);
    }


    public void setCenterRect(Rect r) {
        this.mCenterRect = r;
        postInvalidate();
    }

    int width, height;

    @Override
    protected void onDraw(Canvas canvas) {
        // TODO Auto-generated method stub
        if (mCenterRect == null)
            return;
        // 绘制四周阴影区域
        //顶部
        canvas.drawRect(0, 0, width, mCenterRect.top, mAreaPaint);
        //左侧
        canvas.drawRect(0, mCenterRect.top, mCenterRect.left, height, mAreaPaint);
        //底部
        canvas.drawRect(mCenterRect.left, mCenterRect.bottom, mCenterRect.right,
                height, mAreaPaint);
        //右侧
        canvas.drawRect(mCenterRect.right, mCenterRect.top, width, height, mAreaPaint);
        super.onDraw(canvas);
    }

    public Rect getCenterRect() {
        return mCenterRect;
    }


}

