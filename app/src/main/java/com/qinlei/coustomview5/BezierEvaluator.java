package com.qinlei.coustomview5;

import android.animation.TypeEvaluator;
import android.graphics.PointF;

/**
 * Created by ql
 * 2017/5/8.
 * Description:
 */

public class BezierEvaluator implements TypeEvaluator<PointF> {
    private PointF mControlPoint;//控制点的坐标

    public BezierEvaluator(PointF mControlPoint) {
        this.mControlPoint = mControlPoint;
    }

    /**
     * @param fraction 时间线的值
     * @param startValue
     * @param endValue
     * @return
     */
    @Override
    public PointF evaluate(float fraction, PointF startValue, PointF endValue) {
        return BezierUtil.CalculateBezierPointForQuadratic(fraction, startValue, mControlPoint, endValue);
    }
}
