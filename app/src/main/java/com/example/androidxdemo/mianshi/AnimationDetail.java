package com.example.androidxdemo.mianshi;

/**
 * 动画
 * 1. Android中有哪几种类型的动画？
 *    View动画（View Animation）/补间动画（Tween animation）：对View进行平移、缩放、旋转和透明度变化的动画，
 *                                                         不能真正的改变view的位置。应用如布局动画、Activity切换动画
 *    逐帧动画（Drawable Animation）：是View动画的一种，它会按照顺序播放一组预先定义好的图片
 *    属性动画（Property Animation）：对该类对象进行动画操作，真正改变了对象的属性
 *
 * 2. 帧动画在使用时需要注意什么？
 *    使用祯动画要注意不能使用尺寸过大的图片，否则容易造成OOM
 *
 * 3. View动画和属性动画的区别？
 *    view动画不改变实际位置, 属性动画改变实际位置
 *
 * 4. View动画为何不能真正改变View的位置？而属性动画为何可以？
 *    View动画改变的只是View的显示，而没有改变View的响应区域；而属性动画会通过反射技术来获取和执行属性的get、set方法，从而改变了对象位置的属性值。
 *
 * 5. 属性动画插值器和估值器的作用？
 *    插值器(Interpolator)：根据时间流逝的百分比计算出当前属性值改变的百分比。确定了动画效果变化的模式，
 *                         如匀速变化、加速变化等等。View动画和属性动画均可使用。常用的系统内置插值器：
 *          AccelerateDecelerateInterpolator 　 在动画开始与介绍的地方速率改变比较慢，在中间的时候加速
 *          AccelerateInterpolator  　　　　　　 在动画开始的地方速率改变比较慢，然后开始加速
 *          AnticipateInterpolator  　　　　　　 开始的时候向后然后向前甩
 *          AnticipateOvershootInterpolator   　开始的时候向后然后向前甩一定值后返回最后的值
 *          BounceInterpolator       　　　　　　动画结束的时候弹起
 *          CycleInterpolator　　　　　　　　     动画循环播放特定的次数，速率改变沿着正弦曲线
 *          DecelerateInterpolator  　　　　　　 在动画开始的地方快然后慢
 *          LinearInterpolator   　　　　　　　  以常量速率改变
 *          OvershootInterpolator   　　　　　　 向前甩一定值后再回到原来位置
 *
 *
 *
 *   类型估值器(TypeEvaluator)：根据当前属性改变的百分比计算出改变后的属性值。针对于属性动画，View动画不需要类型估值器。常用的系统内置的估值器：
 *          整形估值器(IntEvaluator)
 *          浮点型估值器(FloatEvaluator)
 *          Color属性估值器(ArgbEvaluator)
 */
public class AnimationDetail {

    //xml
    /*<alpha xmlns:android="http://schemas.android.com/apk/res/android"
       android:repeatCount="2" // 动画重复的次数
       android:repeatMode="reverse" // 动画重复模式
       android:duration="3000" // 动画持续时间
       android:fillAfter="true" // 动画结束后是否停留在结束位置
       android:fromAlpha="1.0" // 起始透明值
       android:toAlpha="0.5"> // 结束透明值
      </alpha>*/

    /*<scale xmlns:android="http://schemas.android.com/apk/res/android"
       android:duration="3000" // 动画持续时间
       android:fillBefore="true"  // 用于确定动画开始时，View的动画属性值
       android:fromXScale="0.0" // 水平方向的起始值
       android:fromYScale="0.0" // 竖直方向的起始值
       android:pivotX="50" // 缩放的x坐标
       android:pivotY="50" // 缩放的y坐标
       android:repeatCount="1" // 重复的次数
       android:repeatMode="restart" // 重复的模式
       android:toXScale="1.5" // 水平方向的结束值
       android:toYScale="1.5">  // 数值方向的结束值
      </scale>*/


    /*<translate xmlns:android="http://schemas.android.com/apk/res/android"
           android:duration="100" // 动画持续时间
           android:fillEnabled="true" // 用来控制fillBefore属性是否有效，若为true，则fillBefore生效；若为false
                                         则不管设置fillBefore为true还是false，都不起作用
           android:fromXDelta="0" // 水平方向的起始值
           android:fromYDelta="0" // 竖直方向的起始值
           android:repeatCount="1" // 重复的次数
           android:repeatMode="reverse" // 重复的模式
           android:toXDelta="-100" // 水平方向的结束值
           android:toYDelta="-100"> // 数值方向的结束值
     </translate>*/

    /*<rotate xmlns:android="http://schemas.android.com/apk/res/android"
        android:duration="2000" // 动画持续时间
        android:fillAfter="true" // 动画结束后是否停留在结束位置
        android:fromDegrees="0" // 起始角度
        android:pivotX="50%" // 缩放x坐标
        android:pivotY="50%" // 缩放y坐标
        android:toDegrees="-650"> // 旋转的角度
      </rotate>*/
}
