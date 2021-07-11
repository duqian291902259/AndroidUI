package site.duqian.android_ui.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.TypeEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.PointF;
import android.graphics.Rect;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import java.util.Random;

import site.duqian.android_ui.R;
import site.duqian.android_ui.utils.UIUtils;

/**
 * Description:比心、点赞类的动画管理
 *
 * @author 杜小菜, Created on 7/11/21 - 10:08 PM.
 * E-mail:duqian2010@gmail.com
 */
public class LoveAnimation {
    private static final int ANIM_DURATION = 2 * 1000;
    private Context mContext;
    private RelativeLayout mRootView;
    private View mViewFrom;
    private View mViewTo;
    private final Random mRandom = new Random();
    //图片的原始高度
    private int mViewWidth;
    private int mViewHeight;
    //移动范围的宽高
    private int mWidth;
    private int mHeight;
    private Rect mRectFrom;
    private Rect mRectTo;

    public LoveAnimation(RelativeLayout rootView, View fromView, View toView) {
        this.mRootView = rootView;
        this.mViewFrom = fromView;
        this.mViewTo = toView;
        this.mContext = rootView.getContext();
        mRectFrom = new Rect();
        mRectTo = new Rect();
        fromView.getGlobalVisibleRect(mRectFrom);
        toView.getGlobalVisibleRect(mRectTo);

        mViewWidth = fromView.getMeasuredWidth();
        mViewHeight = fromView.getMeasuredHeight();
        mWidth = Math.abs(mRectFrom.right - mRectTo.left);//UIUtils.INSTANCE.getScreenWidth(rootView.getContext());
        mHeight = Math.abs(mRectFrom.bottom - mRectTo.top);
    }


    public void startAnimation() {
        //添加心形，并开始执行动画
        final ImageView iv = new ImageView(mViewFrom.getContext());
        iv.setImageResource(R.mipmap.ic_avatar_duqian);
        //将iv添加到父容器底部、水平居中位置
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(mViewWidth, mViewWidth);
        //将iv添加到父容器底部、水平居中位置
        params.addRule(RelativeLayout.CENTER_HORIZONTAL);
        params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        params.addRule(RelativeLayout.ALIGN_PARENT_END);
        params.setMarginEnd(UIUtils.INSTANCE.dp2px(mContext, 10));
        params.bottomMargin = UIUtils.INSTANCE.dp2px(mContext, 80);
        iv.setLayoutParams(params);

        mRootView.addView(iv);
        //开始属性动画：平移、透明度渐变、缩放动画
        AnimatorSet animatorSet = getAnimator(iv);

        //监听动画执行完毕，将iv移除或者复用
        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                mRootView.removeView(iv);
                Log.d("dq-anim", "end anim");
            }
        });
        animatorSet.setDuration(ANIM_DURATION);
        animatorSet.setInterpolator(new AccelerateDecelerateInterpolator());
        animatorSet.start();
    }

    // 得到一个iv的动画集合
    private AnimatorSet getAnimator(View animView) {
        //平移、透明度渐变、缩放动画
        ObjectAnimator alpha = ObjectAnimator.ofFloat(animView, "alpha", 0.3f, 1f);
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(animView, "scaleX", 0.3f, 1f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(animView, "scaleY", 0.3f, 1f);
        AnimatorSet enter = new AnimatorSet();
        enter.setDuration(ANIM_DURATION);
        enter.playTogether(alpha, scaleX, scaleY);

        //设置平移的曲线动画---贝塞尔曲线，启动估值器
        ValueAnimator bezierAnimator = getBezierValueAnimator(animView);

        AnimatorSet set = new AnimatorSet();
        set.playSequentially(enter, bezierAnimator);
        set.setTarget(animView);
        return set;
    }

    //得到一个贝塞尔曲线动画
    private ValueAnimator getBezierValueAnimator(final View animView) {
        //根据贝塞尔公式确定四个点（起始点p0，拐点1p1，拐点2p2，终点p3）
        //PointF pointF0 = new PointF((mWidth - mViewWidth) / 2f, mHeight - mViewHeight);
        //PointF pointF3 = new PointF(mRandom.nextInt(mWidth), 0);
        PointF pointF0 = new PointF(mRectFrom.left, mRectFrom.top);
        PointF pointF3 = new PointF(mRectTo.left, mRectTo.top);
        PointF pointF1 = getPointF(1);
        PointF pointF2 = getPointF(2);
        BezierEvaluator evaluator = new BezierEvaluator(pointF1, pointF2);
        ValueAnimator animator = ValueAnimator.ofObject(evaluator, pointF0, pointF3);
        animator.addUpdateListener(animation -> {
            PointF pointF = (PointF) animation.getAnimatedValue();
            animView.setX(pointF.x);
            animView.setY(pointF.y);
            Log.d("dq-anim", "坐标是:x" + pointF.x + "  y:" + pointF.y);
            animView.setAlpha(1 - animation.getAnimatedFraction());//1~0 百分比
        });

        animator.setDuration(ANIM_DURATION);
        return animator;
    }

    private PointF getPointF(int i) {
        PointF pointF = new PointF();
        pointF.x = mRandom.nextInt(mWidth);
        if (i == 1) {
            pointF.y = mRandom.nextInt(mHeight / 2) + mHeight / 2f;
        } else {
            pointF.y = mRandom.nextInt(mHeight / 2);
        }
        return pointF;
    }

    static class BezierEvaluator implements TypeEvaluator<PointF> {

        private PointF pointF1;
        private PointF pointF2;

        public BezierEvaluator(PointF pointF1, PointF pointF2) {
            this.pointF1 = pointF1;
            this.pointF2 = pointF2;
        }

        @Override
        public PointF evaluate(float t, PointF point0, PointF point3) {
            // b(t)=p0*(1-t)*(1-t)*(1-t)+3*p1*t*(1-t)*(1-t)+3*p2*t*t*(1-t)+p3*t*t*t
            PointF point = new PointF();
            point.x = point0.x * (1 - t) * (1 - t) * (1 - t)
                    + 3 * pointF1.x * t * (1 - t) * (1 - t)
                    + 3 * pointF2.x * t * t * (1 - t)
                    + point3.x * t * t * t;
            point.y = point0.y * (1 - t) * (1 - t) * (1 - t)
                    + 3 * pointF1.y * t * (1 - t) * (1 - t)
                    + 3 * pointF2.y * t * t * (1 - t)
                    + point3.y * t * t * t;
            return point;
        }
    }
}
