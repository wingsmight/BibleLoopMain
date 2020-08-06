package com.wingsmight.bibleloop;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.OvershootInterpolator;
import android.view.animation.Transformation;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.lang.reflect.Field;


public class ExpandableTextView extends androidx.appcompat.widget.AppCompatTextView
{
    // copy off TextView.LINES
    private static final int MAXMODE_LINES = 2;

    private OnExpandListener onExpandListener;
    private TimeInterpolator expandInterpolator;
    private TimeInterpolator collapseInterpolator;

    private final int maxLines;
    private long animationDuration;
    private boolean animating;
    private boolean expanded;
    private int originalHeight;

    public static int minHeight;

    public ExpandableTextView(final Context context)
    {
        this(context, null);
    }

    public ExpandableTextView(final Context context, final AttributeSet attrs)
    {
        this(context, attrs, 0);
    }

    public ExpandableTextView(final Context context, final AttributeSet attrs, final int defStyle)
    {
        super(context, attrs, defStyle);

        // read attributes
        final TypedArray attributes = context.obtainStyledAttributes(attrs, R.styleable.ExpandableTextView, defStyle, 0);
        int DEFAULT_ANIMATION_DURATION = 50;
        this.animationDuration = attributes.getInt(R.styleable.ExpandableTextView_animation_duration, DEFAULT_ANIMATION_DURATION);
        attributes.recycle();

        // keep the original value of maxLines
        this.maxLines = this.getMaxLines();

        // create default interpolators
        this.expandInterpolator = new OvershootInterpolator();
        this.collapseInterpolator = new OvershootInterpolator();

        final ExpandableTextView expandableTextView = this;
        //final ImageView narrow = ((Activity)MainActivity.GetContext()).findViewById(R.id.narrow);

        // set animation duration via code, but preferable in your layout files by using the animation_duration attribute
        this.setAnimationDuration(100);
        // set interpolators for both expanding and collapsing animations
        this.setInterpolator(new OvershootInterpolator());

        this.measure
                (
                        MeasureSpec.makeMeasureSpec(this.getMeasuredWidth(), MeasureSpec.EXACTLY),
                        MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED)
                );

        minHeight = this.getMeasuredHeight();
    }

    @Override
    public int getMaxLines()
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
        {
            return super.getMaxLines();
        }

        try
        {
            final Field mMaxMode = TextView.class.getField("mMaxMode");
            mMaxMode.setAccessible(true);
            final Field mMaximum = TextView.class.getField("mMaximum");
            mMaximum.setAccessible(true);

            final int mMaxModeValue = (int) mMaxMode.get(this);
            final int mMaximumValue = (int) mMaximum.get(this);

            return mMaxModeValue == MAXMODE_LINES ? mMaximumValue : -1;
        } catch (final Exception e)
        {
            return -1;
        }
    }

    public boolean toggle()
    {
        if (this.expanded)
        {
            return this.collapse();
        }

        return this.expand();
    }

    public boolean expand()
    {
        if (!this.expanded && !this.animating && this.maxLines >= 0)
        {
            this.animating = true;

            // notify listener
            if (this.onExpandListener != null)
            {
                this.onExpandListener.onExpand(this);
            }

            // get original height
            this.measure
                    (
                            MeasureSpec.makeMeasureSpec(this.getMeasuredWidth(), MeasureSpec.EXACTLY),
                            MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED)
                    );

            this.originalHeight = this.getMeasuredHeight();

            // set maxLines to MAX Integer
            this.setMaxLines(Integer.MAX_VALUE);

            // get new height
            this.measure
                    (
                            MeasureSpec.makeMeasureSpec(this.getMeasuredWidth(), MeasureSpec.EXACTLY),
                            MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED)
                    );

            final int fullHeight = this.getMeasuredHeight();

            final ValueAnimator valueAnimator = ValueAnimator.ofInt(this.originalHeight, fullHeight);
            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener()
            {
                @Override
                public void onAnimationUpdate(final ValueAnimator animation)
                {
                    final ViewGroup.LayoutParams layoutParams = ExpandableTextView.this.getLayoutParams();
                    layoutParams.height = (int) animation.getAnimatedValue();
                    ExpandableTextView.this.setLayoutParams(layoutParams);
                }
            });
            valueAnimator.addListener(new AnimatorListenerAdapter()
            {
                @Override
                public void onAnimationEnd(final Animator animation)
                {
                    ExpandableTextView.this.expanded = true;
                    ExpandableTextView.this.animating = false;
                }
            });

            // set interpolator
            valueAnimator.setInterpolator(this.expandInterpolator);

            // start the animation
            valueAnimator
                    .setDuration(this.animationDuration)
                    .start();

            return true;
        }

        return false;
    }

    public boolean collapse()
    {
        if (this.expanded && !this.animating && this.maxLines >= 0)
        {
            this.animating = true;

            // notify listener
            if (this.onExpandListener != null)
            {
                this.onExpandListener.onCollapse(this);
            }

            // get new height
            final int fullHeight = this.getMeasuredHeight();

            final ValueAnimator valueAnimator = ValueAnimator.ofInt(fullHeight, this.originalHeight);
            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener()
            {
                @Override
                public void onAnimationUpdate(final ValueAnimator animation)
                {
                    final ViewGroup.LayoutParams layoutParams = ExpandableTextView.this.getLayoutParams();
                    layoutParams.height = (int) animation.getAnimatedValue();
                    ExpandableTextView.this.setLayoutParams(layoutParams);
                }
            });
            valueAnimator.addListener(new AnimatorListenerAdapter()
            {
                @Override
                public void onAnimationEnd(final Animator animation)
                {
                    // set maxLines to original value
                    ExpandableTextView.this.setMaxLines(ExpandableTextView.this.maxLines);

                    ExpandableTextView.this.expanded = false;
                    ExpandableTextView.this.animating = false;
                }
            });

            // set interpolator
            valueAnimator.setInterpolator(this.collapseInterpolator);

            // start the animation
            valueAnimator
                    .setDuration(this.animationDuration)
                    .start();

            return true;
        }

        return false;
    }

    public void setAnimationDuration(final long animationDuration)
    {
        this.animationDuration = animationDuration;
    }

    public void setOnExpandListener(final OnExpandListener onExpandListener)
    {
        this.onExpandListener = onExpandListener;
    }

    public OnExpandListener getOnExpandListener()
    {
        return onExpandListener;
    }

    public void setInterpolator(final TimeInterpolator interpolator)
    {
        this.expandInterpolator = interpolator;
        this.collapseInterpolator = interpolator;
    }

    public void setExpandInterpolator(final TimeInterpolator expandInterpolator)
    {
        this.expandInterpolator = expandInterpolator;
    }

    public TimeInterpolator getExpandInterpolator()
    {
        return this.expandInterpolator;
    }

    public void setCollapseInterpolator(final TimeInterpolator collapseInterpolator)
    {
        this.collapseInterpolator = collapseInterpolator;
    }

    public TimeInterpolator getCollapseInterpolator()
    {
        return this.collapseInterpolator;
    }

    public boolean isExpanded()
    {
        return this.expanded;
    }

    public interface OnExpandListener
    {
        void onExpand(ExpandableTextView view);
        void onCollapse(ExpandableTextView view);
    }

    public static void expand(final View v) {
        v.measure(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        final int targetHeight = v.getMeasuredHeight();

        // Older versions of android (pre API 21) cancel animations for views with a height of 0.
        v.getLayoutParams().height = 1;
        v.setVisibility(View.VISIBLE);
        Animation a = new Animation()
        {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                v.getLayoutParams().height = interpolatedTime == 1
                        ? LinearLayout.LayoutParams.WRAP_CONTENT
                        : (int)(targetHeight * interpolatedTime);
                v.requestLayout();
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        // 1dp/ms
        a.setDuration((int)(targetHeight / v.getContext().getResources().getDisplayMetrics().density));
        v.startAnimation(a);
    }

    public static void collapse(final View v) {
        final int initialHeight = v.getMeasuredHeight();

        Animation a = new Animation()
        {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                if(interpolatedTime == 1){
                    v.setVisibility(View.GONE);
                }else{
                    v.getLayoutParams().height = initialHeight - (int)(initialHeight * interpolatedTime);
                    v.requestLayout();
                }
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        // 1dp/ms
        a.setDuration((int)(initialHeight / v.getContext().getResources().getDisplayMetrics().density));
        v.startAnimation(a);
    }
}