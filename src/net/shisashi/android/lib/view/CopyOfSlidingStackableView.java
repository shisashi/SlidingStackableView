package net.shisashi.android.lib.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewDebug;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;

public class CopyOfSlidingStackableView extends ViewGroup {
    private boolean mShowingMenu = true;
    private HorizontalScrollView scroll;

    @ViewDebug.ExportedProperty
    boolean mMeasureAllChildren = false;

    public CopyOfSlidingStackableView(Context context) {
        super(context);
        init(context);
    }

    public CopyOfSlidingStackableView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
        init(context);
    }

    public CopyOfSlidingStackableView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context) {
        scroll = new HorizontalScrollView(context) {
            @Override
            public boolean onTouchEvent(MotionEvent ev) {
                return false;
            }
        };
        scroll.setHorizontalFadingEdgeEnabled(false);
        scroll.setVerticalFadingEdgeEnabled(false);
    }

    @Override
    public void addView(View child, int index, LayoutParams params) {
        super.addView(child, index, params);
        System.out.println(index);
    }
    
    @Override
    protected void attachViewToParent(View child, int index, LayoutParams params) {
        super.attachViewToParent(child, index, params);
        System.out.println(index);
    }
    
    /**
     * Returns a set of layout parameters with a width of
     * {@link android.view.ViewGroup.LayoutParams#MATCH_PARENT}, and a height of
     * {@link android.view.ViewGroup.LayoutParams#MATCH_PARENT}.
     */
    @Override
    protected LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final int count = getChildCount();

        int maxHeight = 0;
        int maxWidth = 0;

        // Find rightmost and bottommost child
        for (int i = 0; i < count; i++) {
            final View child = getChildAt(i);
            if (mMeasureAllChildren || child.getVisibility() != GONE) {
                measureChild(child, widthMeasureSpec, heightMeasureSpec);
                maxWidth = Math.max(maxWidth, child.getMeasuredWidth());
                maxHeight = Math.max(maxHeight, child.getMeasuredHeight());
            }
        }

        // Check against our minimum height and width
        maxHeight = Math.max(maxHeight, getSuggestedMinimumHeight());
        maxWidth = Math.max(maxWidth, getSuggestedMinimumWidth());

        setMeasuredDimension(resolveSize(maxWidth, widthMeasureSpec), resolveSize(maxHeight, heightMeasureSpec));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        final int count = getChildCount();

        if (count < 2) {
            return;
        }

        int menuButtonWidth = 80;
        int menuWidth = getMeasuredWidth() - menuButtonWidth;
        
        View menu = getChildAt(0);
        View main = getChildAt(1);
        
        if (mShowingMenu) {
            if (menu.getVisibility() != GONE) {
                final int width = menuWidth;
                final int height = menu.getMeasuredHeight();

                menu.layout(left, top, left + width, top + height);
            }
        }

        if (main.getVisibility() != GONE) {
            final int width = main.getMeasuredWidth();
            final int height = main.getMeasuredHeight();

            int myLeft = left + (mShowingMenu ? menuWidth : 0);

            main.layout(myLeft, top, myLeft + width, top + height);
        }
    }

    /**
     * Determines whether to measure all children or just those in the VISIBLE
     * or INVISIBLE state when measuring. Defaults to false.
     * 
     * @param measureAll
     *            true to consider children marked GONE, false otherwise.
     *            Default value is false.
     * 
     * @attr ref android.R.styleable#FrameLayout_measureAllChildren
     */
    public void setMeasureAllChildren(boolean measureAll) {
        mMeasureAllChildren = measureAll;
    }

    /**
     * Determines whether to measure all children or just those in the VISIBLE
     * or INVISIBLE state when measuring.
     */
    public boolean getConsiderGoneChildrenWhenMeasuring() {
        return mMeasureAllChildren;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean checkLayoutParams(ViewGroup.LayoutParams p) {
        return p != null;
    }
}
