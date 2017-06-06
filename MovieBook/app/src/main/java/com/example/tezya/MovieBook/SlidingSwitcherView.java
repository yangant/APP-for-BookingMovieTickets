package com.example.tezya.MovieBook;

import android.content.Context;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

/**
 * Created by Tezya on 2017/6/5.
 */

public class SlidingSwitcherView extends RelativeLayout implements View.OnTouchListener {
    /** 
          * 让菜单滚动，手指滑动需要达到的速度。 
          */
    public static final int SNAP_VELOCITY = 200;/** 
          * SlidingSwitcherView的宽度。 
          */
    private int switcherViewWidth;
    /** 
          * 当前显示的元素的下标。 
          */
    private int currentItemIndex;
    /** 
          * 菜单中包含的元素总数。 
          */
    private int itemsCount;
    /** 
          * 各个元素的偏移边界值。 
          */
    private int[] borders;
    /** 
          * 最多可以滑动到的左边缘。值由菜单中包含的元素总数来定，marginLeft到达此值之后，不能再减少。 
          *  
          */
    private int leftEdge = 0;
    /** 
          * 最多可以滑动到的右边缘。值恒为0，marginLeft到达此值之后，不能再增加。 
          */
    private int rightEdge = 0;
    /** 
          * 记录手指按下时的横坐标。 
          */
    private float xDown;
    /** 
          * 记录手指移动时的横坐标。 
          */
    private float xMove;
    /** 
          * 记录手机抬起时的横坐标。 
          */
    private float xUp;

    private LinearLayout itemsLayout;
    private LinearLayout dotsLayout;
    private View firstItem;
    private MarginLayoutParams firstItemParams;
    private VelocityTracker mVelocityTracker;
    public SlidingSwitcherView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    public void scrollToNext() {
        new ScrollTask().execute(-20);
    }
    public void scrollToPrevious() {
        new ScrollTask().execute(20);
    }
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if (changed) {
            initializeItems();
            initializeDots();
        }
    }
    private void initializeItems() {
        switcherViewWidth = getWidth();
        itemsLayout = (LinearLayout) getChildAt(0);
        itemsCount = itemsLayout.getChildCount();
        borders = new int[itemsCount];
        for (int i = 0; i < itemsCount; i++) {
            borders[i] = -i * switcherViewWidth;
            View item = itemsLayout.getChildAt(i);
            MarginLayoutParams params = (MarginLayoutParams) item.getLayoutParams();
            params.width = switcherViewWidth;
            item.setLayoutParams(params);
            item.setOnTouchListener(this);
        }
        leftEdge = borders[itemsCount - 1];
        firstItem = itemsLayout.getChildAt(0);
        firstItemParams = (MarginLayoutParams) firstItem.getLayoutParams();
    }
    private void initializeDots() {
        dotsLayout = (LinearLayout) getChildAt(1);
        refreshDotsLayout();
    }
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        createVelocityTracker(event);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                xDown = event.getRawX();
                break;
            case MotionEvent.ACTION_MOVE:
                xMove = event.getRawX();
                int distanceX = (int) (xMove - xDown) - (currentItemIndex * switcherViewWidth);
                firstItemParams.leftMargin = distanceX;
                if (beAbleToScroll()) {
                    firstItem.setLayoutParams(firstItemParams);
                }
                break;
            case MotionEvent.ACTION_UP:
                xUp = event.getRawX();
                if (beAbleToScroll()) {
                    if (wantScrollToPrevious()) {
                        if (shouldScrollToPrevious()) {
                            currentItemIndex--;
                            scrollToPrevious();
                            refreshDotsLayout();
                        } else {
                            scrollToNext();
                        }
                    } else if (wantScrollToNext()) {
                        if (shouldScrollToNext()) {
                            currentItemIndex++;
                            scrollToNext();
                            refreshDotsLayout();
                        } else {
                            scrollToPrevious();
                        }
                    }
                }
                recycleVelocityTracker();
                break;
        }
        return false;
    }
    private boolean beAbleToScroll() {
        return firstItemParams.leftMargin < rightEdge && firstItemParams.leftMargin > leftEdge;
    }
    private boolean wantScrollToPrevious() {
        return xUp - xDown > 0;
    }
    private boolean wantScrollToNext() {
        return xUp - xDown < 0;
    }
    private boolean shouldScrollToNext() {
        return xDown - xUp > switcherViewWidth / 2 || getScrollVelocity() > SNAP_VELOCITY;
    }
    private boolean shouldScrollToPrevious() {
        return xUp - xDown > switcherViewWidth / 2 || getScrollVelocity() > SNAP_VELOCITY;
    }
    private void refreshDotsLayout() {
        dotsLayout.removeAllViews();
        for (int i = 0; i < itemsCount; i++) {
            LinearLayout.LayoutParams linearParams = new LinearLayout.LayoutParams(0, LayoutParams.FILL_PARENT);
            linearParams.weight = 1;
            RelativeLayout relativeLayout = new RelativeLayout(getContext());
            ImageView image = new ImageView(getContext());
            if (i == currentItemIndex) {
                image.setBackgroundResource(R.mipmap.logoround1);
            } else {
                image.setBackgroundResource(R.mipmap.logo);
            }
            RelativeLayout.LayoutParams relativeParams = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            relativeParams.addRule(RelativeLayout.CENTER_IN_PARENT);
            relativeLayout.addView(image, relativeParams);
            dotsLayout.addView(relativeLayout, linearParams);
        }
    }
    private void createVelocityTracker(MotionEvent event) {
        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        }
        mVelocityTracker.addMovement(event);
    }
    private int getScrollVelocity() {
        mVelocityTracker.computeCurrentVelocity(1000);
        int velocity = (int) mVelocityTracker.getXVelocity();
        return Math.abs(velocity);
    }
    private void recycleVelocityTracker() {
        mVelocityTracker.recycle();
        mVelocityTracker = null;
    }
    private boolean isCrossBorder(int leftMargin, int speed) {
        for (int border : borders) {
            if (speed > 0) {
                if (leftMargin >= border && leftMargin - speed < border) {
                    return true;
                }
            } else {
                if (leftMargin <= border && leftMargin - speed > border) {
                    return true;
                }
            }
        }
        return false;
    }
    private int findClosestBorder(int leftMargin) {
        int absLeftMargin = Math.abs(leftMargin);
        int closestBorder = borders[0];
        int closestMargin = Math.abs(Math.abs(closestBorder) - absLeftMargin);
        for (int border : borders)  {
            int margin = Math.abs(Math.abs(border) - absLeftMargin);
            if (margin < closestMargin){
                closestBorder = border;
                closestMargin = margin;
            }
        }
        return closestBorder;
    }
    class ScrollTask extends AsyncTask<Integer, Integer, Integer> {
        @Override
        protected Integer doInBackground(Integer... speed) {
            int leftMargin = firstItemParams.leftMargin;
            while (true) {
                leftMargin = leftMargin + speed[0];
                if (isCrossBorder(leftMargin, speed[0])) {
                    leftMargin = findClosestBorder(leftMargin);
                    break;
                }
                publishProgress(leftMargin);
                sleep(10);
            }
            return leftMargin;
        }
        @Override
        protected void onProgressUpdate(Integer... leftMargin) {
            firstItemParams.leftMargin = leftMargin[0];
            firstItem.setLayoutParams(firstItemParams);
        }
        @Override
        protected void onPostExecute(Integer leftMargin) {
            firstItemParams.leftMargin = leftMargin;
            firstItem.setLayoutParams(firstItemParams);
        }
    }
    private void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
