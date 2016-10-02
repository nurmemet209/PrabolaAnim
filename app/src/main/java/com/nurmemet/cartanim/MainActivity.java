package com.nurmemet.cartanim;

import android.animation.Animator;
import android.animation.TimeInterpolator;
import android.animation.TypeEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.PointF;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class MainActivity extends AppCompatActivity {



    private ImageView mAnimItem;
    private View startView;
    private View endView;

    ThrowAnim mThrower;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        startView = findViewById(R.id.start_point);
        endView = findViewById(R.id.end_point);




    }

    public void onThrow(View view) {
        int[] start=new int[2];
        startView.getLocationInWindow(start);
        int[] end=new int[2];
        endView.getLocationInWindow(end);
        mThrower=new ThrowAnim(this,(ViewGroup) findViewById(android.R.id.content),new PointF(end[0],end[1]),new PointF(start[0],start[1]));
        mThrower.move();
    }




    private class CustomInterpolater implements TimeInterpolator {

        @Override
        public float getInterpolation(float input) {
            return 10F;
        }
    }

    private class ThrowAnim {

        private int mDuration = 1000;
        private PointF mStart;
        private PointF mEnd;
        private ViewGroup mContentView;
        private Context mContext;
        private int mContentTop;
        /**
         * 加速度
         */
        private float g;

        public ThrowAnim(Context mContext, ViewGroup contentView, PointF mEnd, PointF mStart) {
            this.mContext = mContext;
            this.mContentView = contentView;
            this.mEnd = mEnd;
            this.mStart = mStart;
        }

        public void move() {
            g = 3600*2 * (mEnd.y - mStart.y) / (mDuration * mDuration);
            int[] contentL=new int[2];
            mContentView.getLocationInWindow(contentL);
            mContentTop=contentL[1];
            mAnimItem = new ImageView(mContext);

            mAnimItem.setImageResource(R.mipmap.ic_launcher);
            mContentView.addView(mAnimItem, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            //mAnimItem.setTranslationY(591-mContentTop);

            final ValueAnimator anim = new ValueAnimator();
            anim.setObjectValues(mStart, mEnd);
            anim.setDuration(mDuration);
            //anim.setInterpolator(new CustomInterpolater());
            anim.setRepeatCount(-1);
            final float vx = (mEnd.x - mStart.x) / mDuration;
            anim.setEvaluator(new TypeEvaluator() {
                @Override
                public Object evaluate(float fraction, Object startValue, Object endValue) {
                    PointF st = (PointF) startValue;
                    PointF ed = (PointF) endValue;
                    float t = fraction * mDuration;
                    float y = mStart.y-mContentTop+1F / 2F * t * t * g / 3600F;
                    float x = t * vx;

                    final PointF md = new PointF(x, y);

                    System.out.println("x=" + md.x + "   y=" + md.y);
                    return md;
                }
            });
            anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    PointF value = (PointF) anim.getAnimatedValue();
                    mAnimItem.setTranslationX(value.x);
                    mAnimItem.setTranslationY(value.y);
                }
            });
            anim.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    mContentView.removeView(mAnimItem);
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });
            anim.start();
        }
    }
}
