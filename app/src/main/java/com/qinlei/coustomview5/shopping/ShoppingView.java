package com.qinlei.coustomview5.shopping;

import android.animation.IntEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.qinlei.coustomview5.BezierEvaluator;

/**
 * Created by ql
 * 2017/5/8.
 * Description: 购物车动画
 */

public class ShoppingView extends View {
    private Paint paint;

    private int w;
    private int h;
    private float moveX = 50;
    private float moveY = 50;
    private ValueAnimator valueAnimator;
    private Path path;

    public ShoppingView(Context context) {
        this(context, null);
    }

    public ShoppingView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ShoppingView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.BLACK);
        paint.setStrokeWidth(5);

        path = new Path();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        // 300 可根据内容的测量得出一个最小值来作为默认值
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

        initAnimator();//其中的 w , h 需要在 onSizeChanged 获取不能在 init() 初始化动画
    }

    /**
     * 不要在onDraw() 方法中 new Paint 和 Path
     */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawCircle(moveX, moveY, 10, paint);

        path.moveTo(50, 50);
        path.quadTo(w - 50, 50, w - 50, h - 50);
        canvas.drawPath(path, paint);

        canvas.drawCircle(50, 50, 10, paint);
        canvas.drawCircle(w - 50, h - 50, 10, paint);
    }

    private void initAnimator() {
        BezierEvaluator evaluator = new BezierEvaluator(new PointF(w - 50, 50));
        valueAnimator = ValueAnimator.ofObject(evaluator,
                new PointF(50, 50),
                new PointF(w - 50, h - 50));
        valueAnimator.setDuration(1000);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                PointF pointF = (PointF) animation.getAnimatedValue();
                moveX = pointF.x;
                moveY = pointF.y;
                invalidate();
            }
        });
    }

    public void start() {
        valueAnimator.start();
    }

}
