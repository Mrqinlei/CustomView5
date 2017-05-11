# 贝塞尔曲线学习  (  加入购物车动画 ,  qq气泡拖拽动画  )
本篇将使用贝塞尔曲线, 绘制一个轨迹动画,和一个图形变换的动画
效果如下:
<img src="https://images-cdn.shimo.im/AJsa9KtZfjsDDtQR/shopping.gif!thumbnail" style="zoom:60%" align=left/><img src="https://images-cdn.shimo.im/ZvblczbHDN4q3x8t/qqDrag.gif!thumbnail" style="zoom:60%" align=left/>
> qq气泡拖拽目前还有些瑕疵没有处理 ( 就是水平拉动时会,连接处可能会抖动 )

<!-- more -->
## 轨迹动画

### 原理 
其实就是属性动画的应用
```
valueAnimator = ValueAnimator.ofObject(evaluator,new PointF(x1, y1),new PointF(x2, y2));
```
evaluator 这里使用我们自定义的 TypeEvaluator, 他可以根据 new PointF(x1, y1), new PointF(x2, y2),计算返回这个曲线从 new PointF(x1, y1) 到 new PointF(x2, y2) 上曲线的点.我们在根据这些点的坐标不停的重绘就好了

### TypeEvaluator
#### 详细请看 : 
[http://www.jianshu.com/p/b239d14060a8](http://www.jianshu.com/p/b239d14060a8)
[http://blog.csdn.net/silentweek/article/details/71216571](http://blog.csdn.net/silentweek/article/details/71216571)  

#### 首先先来看看 android 为我们提供的一个简单的 TypeEvaluator
```
public class IntEvaluator implements TypeEvaluator<Integer> {
    public Integer evaluate(float fraction, Integer startValue, Integer endValue) {
        int startInt = startValue;
        return (int)(startInt + fraction * (endValue - startInt));
    }
}
```
这里看他的返回值 (int)(startInt + fraction * (endValue - startInt));
这里的 fraction 是一个从 0 到 1 的 float 类型的数据设置不同的插值器可以改变它变化的轨迹
所以可以很显然的看出这是一个从 startInt 慢慢增加到 endValue 的一个过程 (使用什么插值器我们暂不谈论 感兴趣的可以看看 [http://www.jcodecraeer.com/a/anzhuokaifa/androidkaifa/2015/0110/2292.html](http://www.jcodecraeer.com/a/anzhuokaifa/androidkaifa/2015/0110/2292.html))

#### 下面是一个自定义 Bezier 曲线的 TypeEvaluator
```
public class BezierEvaluator implements TypeEvaluator<PointF> {
    private PointF mControlPoint;//控制点的坐标

    public BezierEvaluator(PointF mControlPoint) {
        this.mControlPoint = mControlPoint;
    }

    @Override
    public PointF evaluate(float fraction, PointF startValue, PointF endValue) {
        return BezierUtil.CalculateBezierPointForQuadratic(fraction, startValue, mControlPoint, endValue);
    }
}
```
### 贝塞尔曲线求点的算法
```
    /**
     * B(t) = (1 - t)^2 * P0 + 2t * (1 - t) * P1 + t^2 * P2, t ∈ [0,1]
     * 二阶贝塞尔曲线求点算法 
     * @param t  曲线长度比例
     * @param p0 起始点
     * @param p1 控制点
     * @param p2 终止点
     * @return t对应的点
     */
public static PointF CalculateBezierPointForQuadratic(float t, PointF p0, PointF p1, PointF p2) {
    PointF point = new PointF();
    float temp = 1 - t;
    point.x = temp * temp * p0.x + 2 * t * temp * p1.x + t * t * p2.x;
    point.y = temp * temp * p0.y + 2 * t * temp * p1.y + t * t * p2.y;
    return point;
}
```
```
    /**
     * B(t) = P0 * (1-t)^3 + 3 * P1 * t * (1-t)^2 + 3 * P2 * t^2 * (1-t) + P3 * t^3, t ∈ [0,1]
     * 三阶贝塞尔曲线求点算法 
     * @param t  曲线长度比例
     * @param p0 起始点
     * @param p1 控制点1
     * @param p2 控制点2
     * @param p3 终止点
     * @return t对应的点
     */
public static PointF CalculateBezierPointForCubic(float t, PointF p0, PointF p1, PointF p2, PointF p3) {
    PointF point = new PointF();
    float temp = 1 - t;
    point.x = p0.x * temp * temp * temp + 3 * p1.x * t * temp * temp + 3 * p2.x * t * t * temp + p3.x * t * t * t;
    point.y = p0.y * temp * temp * temp + 3 * p1.y * t * temp * temp + 3 * p2.y * t * t * temp + p3.y * t * t * t;
    return point;
}
```
### 使用 
```
BezierEvaluator evaluator = new BezierEvaluator(new PointF(w - 50, 50));
valueAnimator = ValueAnimator.ofObject(evaluator,
        new PointF(50, 50),
        new PointF(w - 50, h - 50));
valueAnimator.setDuration(1000);
valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
    @Override
    public void onAnimationUpdate(ValueAnimator animation) {
        PointF pointF = (PointF) animation.getAnimatedValue();
    }
});
```
根据动画返回的 pointF 不断的重绘,这样就能模拟一个二阶贝塞尔曲线的坐标的变化 , 三阶段的同理

## 图形变换的动画
### 原理
控件主要的静态形态主要分为四种状态:
1. 正常显示状态 (还没有触摸控件的状态)
![](https://images-cdn.shimo.im/FMXxUde2z30NWjz8/snipaste_20170511_102355.png!thumbnail)
2. 连接状态 (控件的两个圆处于连接的状态)
![](https://images-cdn.shimo.im/IGQo8Fr8T5MJQNay/snipaste_20170511_102414.png!thumbnail)
3. 断开状态 (控件两圆断开,只剩下拖拽的那个圆)
![](https://images-cdn.shimo.im/oSKYEwbQvH0bdrj7/snipaste_20170511_102428.png!thumbnail)
4. 爆炸状态 (控件消失的状态) 
> 不好截图看开始的 gif 吧

#### 连接状态
这些状态之中就连接状态比较难点 , 我们就具体分析这个状态 , 看图
![](https://images-cdn.shimo.im/8oeovf9q5o0DxRjv/snipaste_20170511_104229.png!thumbnail)

图形主要分为三部分 圆A 圆B 及它们的连接图形(连接图形主要利用二阶贝塞尔曲线绘制)
两个圆都比较好画,关键就是画曲线,这个曲线如果知道它的起始点、终止点、控制点也可以轻松的画出来

##### 思路
> 这里我的求点的思路还有点问题 ,就是两圆水平时,求到的点有问题

我看别的博客关于这个的文章大都是通过三角函数来求这些点的，我则是通过求直线与圆的交点求出这些点
1. 先求出过两者圆心的直线的 k 值 ,来算出与其垂直的 k 值( y = kx + b )
2. 由于知道圆心就可以求出 A1-A2 的直线方程,同理 B1-B2 一样
3. 为了简单我这里是先让圆心在 (0,0) 点然后平移画布来绘制,在 (0,0) 点这样计算就比较简单点
4. C1-C2 我也是假定它们在一个圆上的
5. 然后就是通过两者圆心距来作为引擎, 改变 圆A 的大小,和 圆C 的大小

### 正常显示状态
绘制两个重叠的圆
画圆A
```
canvas.save();
canvas.translate(circle1CenterX, circle1CenterY);
canvas.drawCircle(0, 0, circle1Radius, mPaint);
canvas.restore();
```
画圆B
```
canvas.save();
canvas.translate(circle2CenterX, circle2CenterY);//y=3x;垂直的为y=-1/3x;
canvas.drawCircle(0, 0, circle2Radius, mPaint);
canvas.restore();
```
这其中圆A的圆心不会变一直都是 (0,0) 半径则根据圆A和圆B的圆心距来改变
圆B 的圆心通过 onTouch() 事件不断改变, 半径不变

### 连接状态
这里就是求出上面连接状态图的点然后绘制的过程,圆的绘制如上
曲线部分
```
path.reset();
path.moveTo(circle1X1, circle1Y1);//移动到A1
path.lineTo(circle1X2, circle1Y2);//连接到A2
path.quadTo(circle3X2 + pointF.x, circle3Y2 + pointF.y, circle2X2, circle2Y2);//连接 A2 到 B2 的曲线
path.lineTo(circle2X1, circle2Y1);//连接到B1
path.quadTo(circle3X1 + pointF.x, circle3Y1 + pointF.y, circle1X1, circle1Y1);//连接 B1 到 A1 的曲线
canvas.drawPath(path, mPaint); //绘制 path
```
### 断开状态
这个就是判断距离大于一定的值后就只是绘制那个触摸的圆其他的不绘制,通过 isBreak 来判断
绘制圆A
```
if (distance > (circle1Radius + circle2Radius) * 2) {
            //断开
            isBreak = true;
        } else {
            if (!isBreak) {
                //画圆A
                canvas.save();
                canvas.translate(circle1CenterX, circle1CenterY);
                canvas.drawCircle(0, 0, circle1Radius, mPaint);
                canvas.restore();
            }
        }
```
绘制连接处
```
if (distance > (circle1Radius + circle2Radius) * 2) {
            //断开
        } else if (!isBreak) {
            ....
            path.reset();
            path.moveTo(circle1X1, circle1Y1);
            path.lineTo(circle1X2, circle1Y2);
            path.quadTo(circle3X2 + pointF.x, circle3Y2 + pointF.y, circle2X2, circle2Y2);
            path.lineTo(circle2X1, circle2Y1);
            path.quadTo(circle3X1 + pointF.x, circle3Y1 + pointF.y, circle1X1, circle1Y1);
            canvas.drawPath(path, mPaint);
        }
```
### 回弹状态
当手指抬起时,如果距离小于一定的值,触控的圆就要回弹回去,也就是控制圆心B 移动到(0,0) 点的过程
```
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
```
### 爆炸状态
当手指抬起时,如果距离大于一定的值,就会绘制一个爆炸的图片在原来圆B的位置上,并加上一个透明的动画
```
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
```
onDraw方法中,绘制 bitmapBoom 
```
if (isBoom) {
    mPaint.setAlpha(alpha);
    canvas.drawBitmap(bitmapBoom, circle2CenterX - circle2Radius,
            circle2CenterY - circle2Radius, mPaint);
    return;
} else {
    mPaint.setAlpha(255);
}
```
### 对外的方法
重置到初始状态的方法,就是重置参数,并重绘
```
public void reset() {
    isBoom = false;
    isBreak = false;
    circle1CenterX = 0;
    circle1CenterY = 0;
    circle2CenterX = 0;
    circle2CenterY = 0;
    invalidate();
}
```