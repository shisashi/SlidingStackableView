package net.shisashi.android.lib.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;

public class SlidingStackableView extends ViewGroup {
    private static final int MP = LayoutParams.MATCH_PARENT;

    /**
     * メニューを表示中かどうか
     */
    private boolean mShowingMenu = false;

    /**
     * メニュー表示時に残すメインの幅。現在は48dpで固定。
     */
    private int dp48;

    /**
     * 最下層。menuとscrollの親
     */
    private FrameLayout base;

    /**
     * 最下層の子。こいつをスクロールさせてmenuの表示を切り替える
     */
    private HorizontalScrollView scroll;

    /**
     * scrollの子の入れ物。左にpadding、右にmainHolder。
     */
    private LinearLayout linear;

    /**
     * linearの子で、mainとwrapperの親。
     */
    private FrameLayout mainHolder;

    /**
     * mainの上にかぶさる。メニューが表示されているときにclick可能で、clickするとmenuを閉じる
     */
    private View wrapper;

    /**
     * linearの子。mainの左
     */
    private View padding;

    /**
     * 追加された最初の子を保持する。メニュー。
     */
    private View menu;

    /**
     * 追加された二番目の子を保持する。本体。
     */
    private View main;

    public SlidingStackableView(Context context) {
        super(context);
        init(context);
    }

    public SlidingStackableView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
        init(context);
    }

    public SlidingStackableView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context) {
        float density = context.getResources().getDisplayMetrics().density;
        dp48 = (int) (48 * density + 0.5f);

        base = new FrameLayout(context);
        base.setLayoutParams(new ViewGroup.LayoutParams(MP, MP));

        scroll = new HorizontalScrollView(context) {
            @Override
            public boolean onTouchEvent(MotionEvent ev) {
                return false;
            }

            @Override
            protected void onLayout(boolean changed, int l, int t, int r, int b) {
                super.onLayout(changed, l, t, r, b);
                scroll.scrollTo(mShowingMenu ? 0 : r, 0);
            }
        };
        scroll.setLayoutParams(new ViewGroup.LayoutParams(MP, MP));
        scroll.setVerticalScrollBarEnabled(false);
        scroll.setHorizontalScrollBarEnabled(false);
        scroll.setHorizontalFadingEdgeEnabled(false);
        scroll.setVerticalFadingEdgeEnabled(false);

        linear = new LinearLayout(context);
        linear.setLayoutParams(new ViewGroup.LayoutParams(MP, MP));

        padding = new View(context);
        padding.setLayoutParams(new LinearLayout.LayoutParams(MP, MP));
        padding.setVisibility(View.INVISIBLE);
        linear.addView(padding);

        mainHolder = new FrameLayout(context);
        mainHolder.setLayoutParams(new LinearLayout.LayoutParams(MP, MP));

        wrapper = new View(context);
        wrapper.setLayoutParams(new ViewGroup.LayoutParams(MP, MP));
        mainHolder.addView(wrapper);

        linear.addView(mainHolder);

        scroll.addView(linear);
        base.addView(scroll);
        super.addView(base, 0, new ViewGroup.MarginLayoutParams(MP, MP));

        wrapper.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                hideMenu();
            }
        });
        wrapper.setClickable(false);

        // mainHolder をクリックしたときに、裏に隠れているmenuのクリックが動かないようにする
        mainHolder.setClickable(true);
        // mainHolder が透けないように
        mainHolder.setBackgroundColor(0xff000000);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addView(View child, int index, ViewGroup.LayoutParams params) {
        System.out.println(index);
        if (index < 0) {
            if (menu == null) {
                index = 0;
            }
            else if (main == null) {
                index = 1;
            }
            else {
                index = 2;
            }
        }

        if (index == 0) {
            menu = child;
            base.addView(child, 0, params);
        }
        else if (index == 1) {
            main = child;
            mainHolder.addView(child, 0, params);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        LayoutParams baseParams = base.getLayoutParams();
        baseParams.width = w;
        base.setLayoutParams(baseParams);

        LayoutParams mainParams = mainHolder.getLayoutParams();
        mainParams.width = w;
        mainHolder.setLayoutParams(mainParams);

        int menuWidth = w - dp48;

        LayoutParams menuParams = menu.getLayoutParams();
        menuParams.width = menuWidth;
        menu.setLayoutParams(menuParams);

        LayoutParams paddingParams = padding.getLayoutParams();
        paddingParams.width = menuWidth;
        padding.setLayoutParams(paddingParams);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        measureChild(base, widthMeasureSpec, heightMeasureSpec);

        int maxWidth = base.getMeasuredWidth();
        int maxHeight = base.getMeasuredHeight();

        setMeasuredDimension(resolveSize(maxWidth, widthMeasureSpec), resolveSize(maxHeight, heightMeasureSpec));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        base.layout(0, 0, r - l, b - t);
    }

    /**
     * show menu
     */
    public void showMenu() {
        scroll.smoothScrollTo(0, 0);
        mShowingMenu = true;
        wrapper.setClickable(mShowingMenu);
    }

    /**
     * hide menu
     */
    public void hideMenu() {
        scroll.smoothScrollTo(scroll.getWidth(), 0);
        mShowingMenu = false;
        wrapper.setClickable(mShowingMenu);
    }

    /**
     * toggle menu
     */
    public void toggleMenu() {
        if (mShowingMenu) {
            hideMenu();
        }
        else {
            showMenu();
        }
    }
}
