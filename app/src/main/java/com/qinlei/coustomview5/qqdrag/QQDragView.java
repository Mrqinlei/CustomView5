package com.qinlei.coustomview5.qqdrag;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.BounceInterpolator;
import android.view.animation.LinearInterpolator;

import com.qinlei.coustomview5.R;
import com.qinlei.coustomview5.StraightLineEvaluator;

/**
 * Created by ql
 * 2017/5/8.
 * Description: 防 qq 气泡拖拽动画
 */

public class QQDragView extends View {
    private int w;
    private int h;

    private Paint mPaint;
    private Paint mPathPaint;
    private Path path;

    private boolean isBreak;//是否断开
    private boolean isBoom;//是否爆炸了

    private Bitmap bitmapBoom;//爆炸的bitmap
    private int alpha;//画笔的透明度

    private float circle1Radius = 80;//小圆的半径
    private float circle2Radius = 100;//大圆的半径

    private float circle1CenterX = 0;//小圆的圆心 x 坐标
    private float circle1CenterY = 0;//小圆的圆心 y 坐标

    private float circle2CenterX = 0;//大圆的圆心 x 坐标
    private float circle2CenterY = 0;//大圆的圆心 y 坐标

    public QQDragView(Context context) {
        this(context, null);
    }

    public QQDragView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public QQDragView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mPaint.setStrokeWidth(3);
        mPaint.setColor(Color.RED);

        path = new Path();

        bitmapBoom = BitmapFactory.decodeResource(getResources(), R.drawable.boom);

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int w = measureSize(300, widthMeasureSpec);
        int h = measureSize(300, heightMeasureSpec);
        setMeasuredDimension(w, h);
    }

    private int measureSize(int defaultSize, int measureSpec) {
        int result = 0;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);
        if (specMode == MeasureSpec.EXACTLY) {
            result = specSize;
        } else if (specMode == MeasureSpec.AT_MOST) {
            result = Math.min(defaultSize, specSize);
        } else {
            result = defaultSize;
        }
        return result;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        this.w = w;
        this.h = h;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.translate(w / 2, h / 2);
        if (isBoom) {
            mPaint.setAlpha(alpha);
            canvas.drawBitmap(bitmapBoom, circle2CenterX - circle2Radius,
                    circle2CenterY - circle2Radius, mPaint);
            return;
        } else {
            mPaint.setAlpha(255);
        }
        float distance = calculateDistance(new PointF(circle1CenterX, circle1CenterY),
                new PointF(circle2CenterX, circle2CenterY));

        circle1Radius = 80 - 60 * distance / ((80 + 100) * 2);
        if (distance > (circle1Radius + circle2Radius) * 2) {
            //断开
            isBreak = true;
        } else {
            if (!isBreak) {
                //画第一个圆
                canvas.save();
                canvas.translate(circle1CenterX, circle1CenterY);
                canvas.drawCircle(0, 0, circle1Radius, mPaint);
                canvas.restore();
            }
        }
        //画第二个圆
        canvas.save();
        canvas.translate(circle2CenterX, circle2CenterY);//y=3x;垂直的为y=-1/3x;
        canvas.drawCircle(0, 0, circle2Radius, mPaint);
        canvas.restore();
        //求直线的 k 值
        float originalK = circle2CenterY / circle2CenterX;
        float k = -1 / originalK;

        //第一个圆  第一个交点
        float circle1X1 = (int) calculateX(circle1Radius, k, true);
        float circle1Y1 = calculateY(k, circle1X1, circle1Radius, true);
        circle1X1 = circle1X1 + circle1CenterX;
        circle1Y1 = circle1Y1 + circle1CenterY;
        //第一个圆  第二个交点
        float circle1X2 = -(int) calculateX(circle1Radius, k, true);
        float circle1Y2 = calculateY(k, circle1X2, circle1Radius, false);
        circle1X2 = circle1X2 + circle1CenterX;
        circle1Y2 = circle1Y2 + circle1CenterY;
//        canvas.drawCircle(circle1X1, circle1Y1, 5, mPaint);
//        canvas.drawCircle(circle1X2, circle1Y2, 5, mPaint);

        //第二个圆  第一个交点
        float circle2X1 = (int) calculateX(circle2Radius, k, true);
        float circle2Y1 = calculateY(k, circle2X1, circle2Radius, true);
        circle2X1 = circle2X1 + circle2CenterX;
        circle2Y1 = circle2Y1 + circle2CenterY;
        //第二个圆  第二个交点
        float circle2X2 = -(int) calculateX(circle2Radius, k, true);
        float circle2Y2 = calculateY(k, circle2X2, circle2Radius, false);
        circle2X2 = circle2X2 + circle2CenterX;
        circle2Y2 = circle2Y2 + circle2CenterY;
//        canvas.drawCircle(circle2X1, circle2Y1, 5, mPaint);
//        canvas.drawCircle(circle2X2, circle2Y2, 5, mPaint);

        if (distance > (circle1Radius + circle2Radius) * 2) {
            //断开
        } else if (!isBreak) {
            //连着
            float radius = 0;
            if (distance < (circle1Radius + circle2Radius)) {//1---1/2
                radius = circle1Radius - (circle1Radius / 2 * distance / (circle1Radius + circle2Radius));
            }
            if (distance < (circle1Radius + circle2Radius) * 2 &&
                    distance >= (circle1Radius + circle2Radius)) {//1/2---0
                radius = circle1Radius / 2 - (circle1Radius / 2 * (distance - (circle1Radius + circle2Radius))
                        / (circle1Radius + circle2Radius));
            }

            canvas.drawCircle(0, 0, 10, mPaint);
            //第二个圆  第一个交点
            float circle3X1 = (int) calculateX(radius, k, true);
            float circle3Y1 = calculateY(k, circle3X1, radius, true);
            //第二个圆  第二个交点
            float circle3X2 = -circle3X1;
            float circle3Y2 = calculateY(k, circle3X2, radius, false);

            PointF pointF = calculateCentralPoint(new PointF(circle1CenterX, circle1CenterY),
                    new PointF(circle2CenterX, circle2CenterY));

            path.reset();
            path.moveTo(circle1X1, circle1Y1);
            path.lineTo(circle1X2, circle1Y2);
            path.quadTo(circle3X2 + pointF.x, circle3Y2 + pointF.y, circle2X2, circle2Y2);
            path.lineTo(circle2X1, circle2Y1);
            path.quadTo(circle3X1 + pointF.x, circle3Y1 + pointF.y, circle1X1, circle1Y1);
            canvas.drawPath(path, mPaint);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                break;
            case MotionEvent.ACTION_MOVE:
                circle2CenterX = (event.getX() - w / 2);
                circle2CenterY = (event.getY() - h / 2);
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                if (!isBreak) {
                    startBounceAnimator();
                } else {
                    float distance = calculateDistance(new PointF(circle1CenterX, circle1CenterY),
                            new PointF(circle2CenterX, circle2CenterY));
                    if (distance <= (circle1Radius + circle2Radius) * 2) {
                        StartBounceAndReset();
                    } else {
                        //爆炸
                        isBoom = true;
                        invalidate();
                        ValueAnimator valueAnimator = ValueAnimator.ofInt(255, 0);
                        valueAnimator.setInterpolator(new LinearInterpolator());
                        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                            @Override
                            public void onAnimationUpdate(ValueAnimator animation) {
                                alpha = (int) animation.getAnimatedValue();
                                invalidate();
                            }
                        });
                        valueAnimator.start();
                    }
                }
                break;
        }
        if (isBoom) {
            return false;
        } else {
            return true;
        }
    }

    //重置控件为初始状态
    private void StartBounceAndReset() {
        StraightLineEvaluator evaluator = new StraightLineEvaluator();
        ValueAnimator valueAnimator = ValueAnimator.ofObject(evaluator,
                new PointF(circle2CenterX, circle2CenterY),
                new PointF(0, 0));
        valueAnimator.setInterpolator(new BounceInterpolator());
        valueAnimator.setDuration(300);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                PointF pointF = (PointF) animation.getAnimatedValue();
                circle2CenterX = pointF.x;
                circle2CenterY = pointF.y;
                invalidate();
            }
        });
        valueAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                isBreak = false;
            }
        });
        valueAnimator.start();
    }

    //开启一个动画回弹
    private void startBounceAnimator() {
        StraightLineEvaluator evaluator = new StraightLineEvaluator();
        ValueAnimator valueAnimator = ValueAnimator.ofObject(evaluator,
                new PointF(circle2CenterX, circle2CenterY),
                new PointF(circle1CenterX, circle1CenterY));
        valueAnimator.setInterpolator(new BounceInterpolator());
        valueAnimator.setDuration(300);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                PointF pointF = (PointF) animation.getAnimatedValue();
                circle2CenterX = pointF.x;
                circle2CenterY = pointF.y;
                invalidate();
            }
        });
        valueAnimator.start();
    }

    public void reset() {
        isBoom = false;
        isBreak = false;
        circle1CenterX = 0;
        circle1CenterY = 0;
        circle2CenterX = 0;
        circle2CenterY = 0;
        invalidate();
    }

    /**
     * 圆心为0,0 直线经过0,0 点的交点的 x 坐标的值
     *
     * @param radius 圆的半径
     * @param k      y=kx 中的k
     * @param isPlus 是否取正数;
     * @return
     */
    private float calculateX(float radius, float k, boolean isPlus) {
        float x = 0;
        x = (float) Math.sqrt(radius * radius / (k * k + 1));
        if (isPlus) {
            return x;
        } else {
            return -x;
        }
    }

    /**
     * y=kx;
     *
     * @param k
     * @param x
     * @return
     */
    private float calculateY(float k, float x, float r, boolean isPlus) {
        if (Math.abs(k) > 10) {
            if (isPlus) {
                return r;
            } else {
                return -r;
            }
        } else {
            return k * x;
        }
    }

    /**
     * 求两点之间的距离
     *
     * @param point1
     * @param point2
     * @return
     */
    private float calculateDistance(PointF point1, PointF point2) {
        return (float) Math.sqrt((point2.x - point1.x) * (point2.x - point1.x) +
                (point2.y - point1.y) * (point2.y - point1.y));
    }

    /**
     * 求两点之间的中点
     *
     * @param point1
     * @param point2
     * @return
     */
    private PointF calculateCentralPoint(PointF point1, PointF point2) {
        return new PointF(point1.x + point2.x / 2,
                point1.y + point2.y / 2);
    }
}
