package com.qinlei.coustomview5;

import android.animation.TypeEvaluator;
import android.graphics.PointF;

/**
 * Created by ql
 * 2017/5/8.
 * Description:
 */

public class StraightLineEvaluator implements TypeEvaluator<PointF> {
    private float k;
    private float b;

    /**
     * @param fraction   时间线的值
     * @param startValue
     * @param endValue
     * @return
     */
    @Override
    public PointF evaluate(float fraction, PointF startValue, PointF endValue) {
        k = (endValue.y - startValue.y) / (endValue.x - startValue.x);
        b = endValue.y - (k * endValue.x);

        float distancex = startValue.x - endValue.x;
        float x = startValue.x - (fraction * distancex);
        float y = calculateY(x, k, b);
        return new PointF(x, y);
    }

    private float calculateY(float x, float k, float b) {
        return x * k + b;
    }

}
