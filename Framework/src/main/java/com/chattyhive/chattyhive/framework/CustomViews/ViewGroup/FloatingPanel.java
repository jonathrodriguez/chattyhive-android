package com.chattyhive.chattyhive.framework.CustomViews.ViewGroup;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
//import android.util.Log;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Scroller;

import com.chattyhive.chattyhive.framework.R;

import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * Created by Jonathan on 14/06/2014.
 */
public class FloatingPanel extends ViewGroup {

    private static float defaultCenterMainPanelVisibleWidthDP = 40;
    private static float defaultCenterActionBarVisibleWidthDP = 48;

    private static float defaultCenterMainPanelVisibleWidth = 0;
    private static float defaultCenterActionBarVisibleWidth = 0;

    private static boolean defaultFixLeftPanel = false;
    private static float defaultFixedLeftPanelWidthDP = 300;
    private static float defaultFixedLeftPanelWidth = 0;

    private static float defaultMaxLeftPanelWidthDP = 320;
    private static float defaultMaxLeftPanelWidth = 0;

    private static float defaultMaxRightPanelWidthDP = 320;
    private static float defaultMaxRightPanelWidth = 0;

    private static boolean defaultAllowSwipeToMovePanels = true;

    private static DisplayMetrics displayMetrics = null;

    private TreeMap<String,View> actionBars = new TreeMap<String, View>();
    private TreeMap<String,View> mainPanels = new TreeMap<String, View>();

    private float centerMainPanelLeftVisibleWidth;
    private float centerActionBarLeftVisibleWidth;

    private float centerMainPanelRightVisibleWidth;
    private float centerActionBarRightVisibleWidth;

    private float maxLeftPanelWidth;
    private float maxRightPanelWidth;

    private boolean fixLeftPanel;
    private float fixedLeftPanelWidth;

    private boolean allowSwipeToMovePanels;

    private TreeMap<String,Integer> actionBarsHeight = new TreeMap<String, Integer>();
    private TreeMap<String,Integer> actionBarsWidth = new TreeMap<String, Integer>();

    private TreeMap<String,Integer> mainPanelsHeight = new TreeMap<String, Integer>();
    private TreeMap<String,Integer> mainPanelsWidth = new TreeMap<String, Integer>();

    /*************************/
    /* Panel movement fields */
    /*************************/
    private static float defaultActionMoveThresholdDP = 10;
    private static float defaultActionMoveThreshold = 0;

    private static int defaultMaxAnimationDuration = 250;
    private static int defaultButtonPressedAnimationDuration = 250;

    private static int defaultFlingSpeedThreshold = 800;

    private float actionMoveThreshold;

    private int maxAnimationDuration;
    private int buttonPressedAnimationDuration;

    private int flingSpeedThreshold;

    private float actualPosition = 0; //0 is for normal state with center panel visible.

    private Boolean restored = false;
    private int actualState; //Center: 0; Left: 1; Right: 2

    private float StartEventX = -1;
    private float StartEventY = -1;
    private float LastEventX = -1;

    private Boolean moving = false;
    private VelocityTracker velocityTracker;
    private Scroller scroller;
    private Boolean scrolling = false;

    public FloatingPanel(Context context) {
        this(context, null);
    }

    public FloatingPanel(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.floatingPanelStyle);
    }

    public FloatingPanel(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        setWillNotDraw(false);

        this.actionBars.clear();
        this.mainPanels.clear();

        TypedArray a = context.obtainStyledAttributes(attrs,R.styleable.FloatingPanel, defStyle, 0);

        if (displayMetrics == null) {
            displayMetrics = new DisplayMetrics();
            ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getMetrics(displayMetrics);

            defaultCenterActionBarVisibleWidth = defaultCenterActionBarVisibleWidthDP * displayMetrics.scaledDensity;
            defaultCenterMainPanelVisibleWidth = defaultCenterMainPanelVisibleWidthDP * displayMetrics.scaledDensity;

            defaultFixedLeftPanelWidth = defaultFixedLeftPanelWidthDP * displayMetrics.scaledDensity;

            defaultActionMoveThreshold = defaultActionMoveThresholdDP * displayMetrics.scaledDensity;

            defaultMaxLeftPanelWidth = defaultMaxLeftPanelWidthDP * displayMetrics.scaledDensity;
            defaultMaxRightPanelWidth = defaultMaxRightPanelWidthDP * displayMetrics.scaledDensity;
        }

        this.centerMainPanelLeftVisibleWidth = a.getDimension(R.styleable.FloatingPanel_centerMainPanelLeftVisibleWidth, defaultCenterMainPanelVisibleWidth);
        this.centerActionBarLeftVisibleWidth = a.getDimension(R.styleable.FloatingPanel_centerActionBarLeftVisibleWidth, defaultCenterActionBarVisibleWidth);

        this.centerMainPanelRightVisibleWidth = a.getDimension(R.styleable.FloatingPanel_centerMainPanelRightVisibleWidth, defaultCenterMainPanelVisibleWidth);
        this.centerActionBarRightVisibleWidth = a.getDimension(R.styleable.FloatingPanel_centerActionBarRightVisibleWidth, defaultCenterActionBarVisibleWidth);

        this.maxLeftPanelWidth = a.getDimension(R.styleable.FloatingPanel_maxLeftPanelWidth,defaultMaxLeftPanelWidth);
        this.maxRightPanelWidth = a.getDimension(R.styleable.FloatingPanel_maxRightPanelWidth,defaultMaxRightPanelWidth);

        this.fixLeftPanel = a.getBoolean(R.styleable.FloatingPanel_fixLeftPanel,defaultFixLeftPanel);
        this.fixedLeftPanelWidth = a.getDimension(R.styleable.FloatingPanel_fixedLeftPanelWidth,defaultFixedLeftPanelWidth);

        this.allowSwipeToMovePanels = a.getBoolean(R.styleable.FloatingPanel_allowSwipeToMovePanels,defaultAllowSwipeToMovePanels);

        this.actionMoveThreshold = a.getDimension(R.styleable.FloatingPanel_touchActionMoveDistanceThreshold,defaultActionMoveThreshold);

        this.maxAnimationDuration = a.getInteger(R.styleable.FloatingPanel_maxAnimationDuration,defaultMaxAnimationDuration);
        this.buttonPressedAnimationDuration = a.getInteger(R.styleable.FloatingPanel_buttonPressedAnimationDuration,defaultButtonPressedAnimationDuration);

        this.flingSpeedThreshold = a.getInteger(R.styleable.FloatingPanel_flingSpeedThreshold,defaultFlingSpeedThreshold);

        a.recycle();

        setFocusable(true);
        setFocusableInTouchMode(true);

        velocityTracker = VelocityTracker.obtain();
        scroller = new Scroller(context);
    }

    public void openLeft() {
        if (this.fixLeftPanel) return;
        openLeft(this.buttonPressedAnimationDuration);
    }

    public void openLeft(int animationDuration) {
        if (this.fixLeftPanel) return;
        float leftWidth = mainPanels.get("left").getMeasuredWidth();
        int leftMargin = ((LayoutParams)mainPanels.get("left").getLayoutParams()).leftMargin;
        int rightMargin = ((LayoutParams)mainPanels.get("left").getLayoutParams()).rightMargin;
        int distance = Math.round(leftWidth + leftMargin + rightMargin - actualPosition);
        movePanels(distance, animationDuration);
    }

    public void openRight() {
        openRight(this.buttonPressedAnimationDuration);
    }

    public void openRight(int animationDuration) {
        float rightWidth = mainPanels.get("right").getMeasuredWidth();
        int leftMargin = ((LayoutParams)mainPanels.get("right").getLayoutParams()).leftMargin;
        int rightMargin = ((LayoutParams)mainPanels.get("right").getLayoutParams()).rightMargin;
        int distance = Math.round(- actualPosition - rightWidth - leftMargin - rightMargin);
        movePanels(distance,animationDuration);
    }

    public void close() {
        close(this.buttonPressedAnimationDuration);
    }

    public void close(int animationDuration) {
        movePanels(-actualPosition,animationDuration);
    }

    public Boolean isOpen() {
        return (actualPosition != 0);
    }

    @Override
    public void addView(View child, int index, android.view.ViewGroup.LayoutParams params) {
        if (isInEditMode()) return;

        if (!(params instanceof LayoutParams)) {
            throw new IllegalArgumentException("The parameter params must a instance of com.chattyhive.chattyhive.slidingpanels.FloatingPanel$LayoutParams");
        }

/*        if (null == params) { // Skip the view without LayoutParams
            return;
        }*/

        LayoutParams layoutParams = (LayoutParams) params;

        String position = layoutParams.getPosition().name();

        switch (layoutParams.getType()) {
            case actionBar:
                if (this.actionBars.containsKey(position))
                    removeView(this.actionBars.get(position));

                this.actionBars.put(position,child);
                break;
            case mainPanel:
                if (this.mainPanels.containsKey(position))
                    removeView(this.mainPanels.get(position));

                this.mainPanels.put(position,child);
                break;
            default:
                return;
        }

        if ((position.equalsIgnoreCase("right")) && (actualPosition >= 0)) {
            child.setVisibility(GONE);
        }

        super.addView(child, index, params);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (KeyEvent.ACTION_UP == event.getAction()) {
            final boolean isOpen = isOpen();
            switch (event.getKeyCode()) {
                case KeyEvent.KEYCODE_BACK:
                    if (isOpen) {
                        close();
                        return true;
                    }
                    break;
                case KeyEvent.KEYCODE_DPAD_LEFT:
                    if (actualPosition > 0) {
                        close();
                        return true;
                    } else if ((!isOpen) && (this.allowSwipeToMovePanels)) {
                        openLeft();
                        return true;
                    }
                    break;
                case KeyEvent.KEYCODE_DPAD_RIGHT:
                    if (actualPosition < 0) {
                        close();
                        return true;
                    } else if ((!isOpen) && (this.allowSwipeToMovePanels)) {
                        openRight();
                        return true;
                    }
                    break;
            }
        }
        return super.dispatchKeyEvent(event);
    }


    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        Log.w("onInterceptTouchEvent_1",String.format("ev.x: %f\tev.y: %f\tev.action: %d\tmoving: %b\tallowSwipeToMovePanels: %b",ev.getX(),ev.getY(),ev.getAction(),moving,allowSwipeToMovePanels));
        Log.w("onInterceptTouchEvent_2",String.format("StartX: %f\tStartY: %f\tLastX: %f\tactualPosition: %f\tactionMoveThreshold: %f",StartEventX,StartEventY,LastEventX,actualPosition,actionMoveThreshold));
        Log.w("onInterceptTouchEvent_3",String.format("LeftMainWidth: %d\tLeftMain_leftMargin: %d\tLeftMain_rightMargin: %d",mainPanelsWidth.get("left"),((LayoutParams) mainPanels.get("left").getLayoutParams()).leftMargin,((LayoutParams) mainPanels.get("left").getLayoutParams()).rightMargin));
        Log.w("onInterceptTouchEvent_4",String.format("CenterMainWidth: %d\tCenterMain_leftMargin: %d\tCenterMain_rightMargin: %d",mainPanelsWidth.get("center"),((LayoutParams) mainPanels.get("center").getLayoutParams()).leftMargin,((LayoutParams) mainPanels.get("center").getLayoutParams()).rightMargin));
        Log.w("onInterceptTouchEvent_5",String.format("RightMainWidth: %d\tRightMain_leftMargin: %d\tRightMain_rightMargin: %d",mainPanelsWidth.get("right"),((LayoutParams) mainPanels.get("right").getLayoutParams()).leftMargin,((LayoutParams) mainPanels.get("right").getLayoutParams()).rightMargin));

        if (!this.allowSwipeToMovePanels) return false;

        final float x = ev.getX();
        final float y = ev.getY();

        Boolean result = false;

        if (moving) {
            result=true;
        } else {
            switch (ev.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    if (actualPosition == 0) {
                        this.StartEventX = this.LastEventX = x;
                        this.StartEventY = y;
                    } else if (actualPosition > 0) {
                        if (x >= (mainPanelsWidth.get("left") + ((LayoutParams) mainPanels.get("left").getLayoutParams()).leftMargin + ((LayoutParams) mainPanels.get("left").getLayoutParams()).rightMargin)) {
                            this.StartEventX = this.LastEventX = x;
                            this.StartEventY = y;
                        }

                    } else if (actualPosition < 0) {
                        if (x <= (mainPanelsWidth.get("center") + ((LayoutParams) mainPanels.get("center").getLayoutParams()).leftMargin + ((LayoutParams) mainPanels.get("center").getLayoutParams()).rightMargin - (mainPanelsWidth.get("right") + ((LayoutParams) mainPanels.get("right").getLayoutParams()).leftMargin + ((LayoutParams) mainPanels.get("right").getLayoutParams()).rightMargin))) {
                            this.StartEventX = this.LastEventX = x;
                            this.StartEventY = y;
                        }
                    }
                    break;
                case MotionEvent.ACTION_MOVE:
                    if ((this.StartEventX < 0) || (this.StartEventY < 0) || (this.LastEventX < 0)) {
                        result = false;
                    } else {
                        float deltaX = x - this.StartEventX;
                        float deltaY = y - this.StartEventY;

                        if (actualPosition == 0) {
                            result = true;
                        } else if (actualPosition > 0) {
                            result = (this.StartEventX >= (mainPanels.get("left").getMeasuredWidth() + ((LayoutParams) mainPanels.get("left").getLayoutParams()).leftMargin + ((LayoutParams) mainPanels.get("left").getLayoutParams()).rightMargin));
                        } else if (actualPosition < 0) {
                            result = (this.StartEventX <= (mainPanels.get("center").getMeasuredWidth() + ((LayoutParams) mainPanels.get("center").getLayoutParams()).leftMargin + ((LayoutParams) mainPanels.get("center").getLayoutParams()).rightMargin - (mainPanels.get("right").getMeasuredWidth() + ((LayoutParams) mainPanels.get("right").getLayoutParams()).leftMargin + ((LayoutParams) mainPanels.get("right").getLayoutParams()).rightMargin)));
                        }
                        if ((Math.abs(deltaX) < Math.abs(deltaY)) || (Math.abs(deltaX) < this.actionMoveThreshold))
                            result = false;
                    }
                    break;
            }
        }

        Log.w("onInterceptTouchEvent_6",String.format("Result: %b",result));

        return result;
    }


    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        Log.w("onTouchEvent_1",String.format("ev.x: %f\tev.y: %f\tev.action: %d\tmoving: %b\tallowSwipeToMovePanels: %b",ev.getX(),ev.getY(),ev.getAction(),moving,allowSwipeToMovePanels));
        Log.w("onTouchEvent_2",String.format("StartX: %f\tStartY: %f\tLastX: %f\tactualPosition: %f\tactionMoveThreshold: %f",StartEventX,StartEventY,LastEventX,actualPosition,actionMoveThreshold));
        Log.w("onTouchEvent_3",String.format("LeftMainWidth: %d\tLeftMain_leftMargin: %d\tLeftMain_rightMargin: %d",mainPanelsWidth.get("left"),((LayoutParams) mainPanels.get("left").getLayoutParams()).leftMargin,((LayoutParams) mainPanels.get("left").getLayoutParams()).rightMargin));
        Log.w("onTouchEvent_4",String.format("CenterMainWidth: %d\tCenterMain_leftMargin: %d\tCenterMain_rightMargin: %d",mainPanelsWidth.get("center"),((LayoutParams) mainPanels.get("center").getLayoutParams()).leftMargin,((LayoutParams) mainPanels.get("center").getLayoutParams()).rightMargin));
        Log.w("onTouchEvent_5",String.format("RightMainWidth: %d\tRightMain_leftMargin: %d\tRightMain_rightMargin: %d",mainPanelsWidth.get("right"),((LayoutParams) mainPanels.get("right").getLayoutParams()).leftMargin,((LayoutParams) mainPanels.get("right").getLayoutParams()).rightMargin));

        if (!this.allowSwipeToMovePanels) return false;

        final float x = ev.getX();
        final float y = ev.getY();

        if ((this.StartEventX < 0) || (this.StartEventY < 0) || (this.LastEventX < 0)) return false;

        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                Boolean result = false;
                if (actualPosition == 0) {
                    result = true;
                } else if (actualPosition > 0) {
                    result = (x >= (mainPanels.get("left").getMeasuredWidth()+((LayoutParams)mainPanels.get("left").getLayoutParams()).leftMargin+((LayoutParams)mainPanels.get("left").getLayoutParams()).rightMargin));
                } else if (actualPosition < 0) {
                    result = (x <= (mainPanels.get("center").getMeasuredWidth()+((LayoutParams)mainPanels.get("center").getLayoutParams()).leftMargin+((LayoutParams)mainPanels.get("center").getLayoutParams()).rightMargin-(mainPanels.get("right").getMeasuredWidth()+((LayoutParams)mainPanels.get("right").getLayoutParams()).leftMargin+((LayoutParams)mainPanels.get("right").getLayoutParams()).rightMargin)));
                }
                if (!result) {
                    velocityTracker.clear();
                    scroller.abortAnimation();
                    moving = false;
                    this.StartEventX = -1;
                    this.StartEventY = -1;
                    this.LastEventX = -1;
                } else {
                    velocityTracker.addMovement(ev);
                }
                return result;
            case MotionEvent.ACTION_MOVE:
                if (!moving) { // test if we have to move
                    float deltaX = x-this.StartEventX;
                    float deltaY = y-this.StartEventY;
                    if ((Math.abs(deltaX) > Math.abs(deltaY)) && (Math.abs(deltaX) > this.actionMoveThreshold)) {
                        moving = true;
                        getParent().requestDisallowInterceptTouchEvent(true);
                    } else {
                        velocityTracker.addMovement(ev);
                        return false;
                    }
                }
                velocityTracker.addMovement(ev);

                movePanels(x-this.LastEventX);
                this.LastEventX = x;
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_OUTSIDE:
                if (!moving) { // test if we have to move
                    float deltaX = x-this.StartEventX;
                    float deltaY = y-this.StartEventY;
                    if ((Math.abs(deltaX) > Math.abs(deltaY)) && (Math.abs(deltaX) > this.actionMoveThreshold)) {
                        getParent().requestDisallowInterceptTouchEvent(true);
                    } else {
                        velocityTracker.clear();
                        scroller.abortAnimation();
                        moving = false;
                        this.StartEventX = -1;
                        this.StartEventY = -1;
                        this.LastEventX = -1;
                        return false;
                    }
                }
                velocityTracker.addMovement(ev);

                velocityTracker.computeCurrentVelocity(1000);

                float destination = 0;

                int finalPosition; //center = 0, left = 1, right = 2;

                Log.w("onTouchEvent_6",String.format("XVelocity: %f\tYVelocity: %f\tflingSpeedThreshold: %d",velocityTracker.getXVelocity(),velocityTracker.getYVelocity(),this.flingSpeedThreshold));

                if ((Math.abs(velocityTracker.getXVelocity()) > Math.abs(velocityTracker.getYVelocity())) && (Math.abs(velocityTracker.getXVelocity()) > this.flingSpeedThreshold)) {
                    //It's a valid fling
                    float velocity = velocityTracker.getXVelocity();
                    if ((velocity > 0) && (actualPosition > 0)) { //show left
                        finalPosition = 1;
                    } else if ((velocity < 0) && (actualPosition < 0)) { //show right
                        finalPosition = 2;
                    } else { //show center
                        finalPosition = 0;
                    }
                } else if (!moving) { //Is not a valid fling and ev must be passed up
                    velocityTracker.clear();
                    scroller.abortAnimation();
                    moving = false;
                    this.StartEventX = -1;
                    this.StartEventY = -1;
                    this.LastEventX = -1;
                    return false;
                } else if (actualPosition > Math.round((mainPanels.get("left").getMeasuredWidth()+((LayoutParams)mainPanels.get("left").getLayoutParams()).leftMargin+((LayoutParams)mainPanels.get("left").getLayoutParams()).rightMargin) / 2)) { //Go to left
                    finalPosition = 1;
                } else if (Math.abs(actualPosition) > Math.round((mainPanels.get("right").getMeasuredWidth()+((LayoutParams)mainPanels.get("right").getLayoutParams()).leftMargin+((LayoutParams)mainPanels.get("right").getLayoutParams()).rightMargin) / 2)) { // Go to right
                    finalPosition = 2;
                } else { //Go center
                    finalPosition = 0;
                }

                int animationDuration = Math.round(3 * Math.round(1000 * Math.abs((destination-actualPosition) / velocityTracker.getXVelocity())));
                Log.w("onTouchEvent_7",String.format("finalPosition: %d\tanimationDuration: %d",finalPosition,animationDuration));
                switch (finalPosition) {
                    case 0: //center
                        close(animationDuration);
                        break;
                    case 1: //left
                        openLeft(animationDuration);
                        break;
                    case 2: //right
                        openRight(animationDuration);
                        break;
                }

                velocityTracker.clear();
                moving = false;
                this.StartEventX = -1;
                this.StartEventY = -1;
                this.LastEventX = -1;
                getParent().requestDisallowInterceptTouchEvent(false);
                break;
        }
        return true;
    }

    protected void computePosition() {
        float leftBound = mainPanels.get("left").getMeasuredWidth() + ((LayoutParams)mainPanels.get("left").getLayoutParams()).leftMargin + ((LayoutParams)mainPanels.get("left").getLayoutParams()).rightMargin;
        float rightBound = -(mainPanels.get("right").getMeasuredWidth() + ((LayoutParams)mainPanels.get("right").getLayoutParams()).leftMargin + ((LayoutParams)mainPanels.get("right").getLayoutParams()).rightMargin);

        if (actualPosition > (leftBound/2)) { //left
            actualState = 1;
        } else if (actualPosition < (rightBound/2)) { //right
            actualState = 2;
        } else { //center
            actualState = 0;
        }

        if (actualPosition < 0) {
            mainPanels.get("right").setVisibility(VISIBLE);
            actionBars.get("right").setVisibility(VISIBLE);
        } else {
            mainPanels.get("right").setVisibility(GONE);
            actionBars.get("right").setVisibility(GONE);
        }

    }

    protected void correctPosition() {
        if (!restored) return;
        restored = false;
        if (actualState == 1) { //Go to left
            openLeft(0);
        } else if (actualState == 2) { // Go to right
            openRight(0);
        } else { //Go center
            close(0);
        }
    }

    @Override
    public void computeScroll() {
        if (scrolling) {
            if (scroller.computeScrollOffset()) {
                setCurrentPosition(scroller.getCurrX());
            } else {
                scrolling = false;
            }
        }
    }

    protected void setCurrentPosition(float newPosition) {
        this.movePanels(newPosition-actualPosition);
    }

    protected void movePanels (float distance) {
        actualPosition = saturateNewPosition(actualPosition+distance);
        computePosition();
        invalidate();
        requestLayout();
    }

    protected float saturateNewPosition(float newPosition) {
        float leftBound = mainPanels.get("left").getMeasuredWidth() + ((LayoutParams)mainPanels.get("left").getLayoutParams()).leftMargin + ((LayoutParams)mainPanels.get("left").getLayoutParams()).rightMargin;
        float rightBound = -(mainPanels.get("right").getMeasuredWidth() + ((LayoutParams)mainPanels.get("right").getLayoutParams()).leftMargin + ((LayoutParams)mainPanels.get("right").getLayoutParams()).rightMargin);
        if (this.fixLeftPanel)
            leftBound = 0;
        return Math.max(Math.min(newPosition,leftBound),rightBound);
    }

    protected void movePanels (float distance, int duration) {
        int animationDuration = Math.min(duration,this.maxAnimationDuration);
        float finalDistance = saturateNewPosition(actualPosition+distance) - actualPosition;
        scroller.abortAnimation();
        scrolling = true;
        scroller.startScroll(Math.round(actualPosition), 0, Math.round(finalDistance), 0, animationDuration);
        invalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        this.actionBarsHeight.clear();
        this.actionBarsWidth.clear();

        this.mainPanelsHeight.clear();
        this.mainPanelsWidth.clear();

        int actionBarHeightMeasureSpec = MeasureSpec.makeMeasureSpec(Math.round(MeasureSpec.getSize(heightMeasureSpec) / 2),MeasureSpec.AT_MOST);

        int leftPageMaxWidth = (this.fixLeftPanel)?Math.round(this.fixedLeftPanelWidth):(MeasureSpec.getSize(widthMeasureSpec) - Math.round(this.centerActionBarLeftVisibleWidth));
        int rightPageMaxWidth = MeasureSpec.getSize(widthMeasureSpec) - Math.round(this.centerActionBarRightVisibleWidth);
        int centerPageMaxWidth = MeasureSpec.getSize(widthMeasureSpec) - ((this.fixLeftPanel)?Math.round(this.fixedLeftPanelWidth):0);

        if ((!this.fixLeftPanel) && (leftPageMaxWidth > Math.round(this.maxLeftPanelWidth)))
            leftPageMaxWidth = Math.round(this.maxLeftPanelWidth);

        if (rightPageMaxWidth > Math.round(this.maxRightPanelWidth))
            rightPageMaxWidth = Math.round(this.maxRightPanelWidth);

        int leftPageWidthMeasureSpec;
        int rightPageWidthMeasureSpec;

        View child;

        for (Map.Entry<String,View> actionBar : this.actionBars.entrySet()) {
            child = actionBar.getValue();

            if (actionBar.getKey().equalsIgnoreCase("center"))
                measureChild(child,MeasureSpec.makeMeasureSpec(centerPageMaxWidth,MeasureSpec.getMode(widthMeasureSpec)),actionBarHeightMeasureSpec);
            else if (actionBar.getKey().equalsIgnoreCase("left")) {
                if (child.getLayoutParams().width == LayoutParams.WRAP_CONTENT)
                    leftPageWidthMeasureSpec = MeasureSpec.makeMeasureSpec(leftPageMaxWidth,MeasureSpec.AT_MOST);
                else if ((child.getLayoutParams().width == LayoutParams.MATCH_PARENT) || (child.getLayoutParams().width == 0))
                    leftPageWidthMeasureSpec = MeasureSpec.makeMeasureSpec(leftPageMaxWidth,MeasureSpec.EXACTLY);
                else
                    leftPageWidthMeasureSpec = MeasureSpec.makeMeasureSpec(child.getLayoutParams().width,MeasureSpec.EXACTLY);

                measureChild(child, leftPageWidthMeasureSpec, actionBarHeightMeasureSpec);
            }
            else {
                if (child.getLayoutParams().width == LayoutParams.WRAP_CONTENT)
                    rightPageWidthMeasureSpec = MeasureSpec.makeMeasureSpec(rightPageMaxWidth,MeasureSpec.AT_MOST);
                else if ((child.getLayoutParams().width == LayoutParams.MATCH_PARENT) || (child.getLayoutParams().width == 0))
                    rightPageWidthMeasureSpec = MeasureSpec.makeMeasureSpec(rightPageMaxWidth,MeasureSpec.EXACTLY);
                else
                    rightPageWidthMeasureSpec = MeasureSpec.makeMeasureSpec(child.getLayoutParams().width,MeasureSpec.EXACTLY);

                measureChild(child, rightPageWidthMeasureSpec, actionBarHeightMeasureSpec);
            }

            actionBarsHeight.put(actionBar.getKey(),child.getMeasuredHeight());
            actionBarsWidth.put(actionBar.getKey(),child.getMeasuredWidth());
        }

        int panelHeightMeasureSpec;

        leftPageMaxWidth = (this.fixLeftPanel)?Math.round(this.fixedLeftPanelWidth):(MeasureSpec.getSize(widthMeasureSpec) - Math.round(this.centerMainPanelLeftVisibleWidth));
        rightPageMaxWidth = MeasureSpec.getSize(widthMeasureSpec) - Math.round(this.centerMainPanelRightVisibleWidth);

        if ((!this.fixLeftPanel) && (leftPageMaxWidth > Math.round(this.maxLeftPanelWidth)))
            leftPageMaxWidth = Math.round(this.maxLeftPanelWidth);

        if (rightPageMaxWidth > Math.round(this.maxRightPanelWidth))
            rightPageMaxWidth = Math.round(this.maxRightPanelWidth);

        for (Map.Entry<String,View> mainPanel : this.mainPanels.entrySet()) {
            child = mainPanel.getValue();


            int heightSpec = MeasureSpec.getSize(heightMeasureSpec);

            if (this.actionBars.containsKey(mainPanel.getKey()))
                heightSpec -= actionBarsHeight.get(mainPanel.getKey());


            if (child.getLayoutParams().height == LayoutParams.WRAP_CONTENT) {
                panelHeightMeasureSpec = MeasureSpec.makeMeasureSpec(heightSpec,MeasureSpec.AT_MOST);
            } else if ((child.getLayoutParams().height == LayoutParams.MATCH_PARENT) || (child.getLayoutParams().height == 0)) {
                panelHeightMeasureSpec = MeasureSpec.makeMeasureSpec(heightSpec,MeasureSpec.EXACTLY);
            } else {
                if (child.getLayoutParams().height < heightSpec)
                    panelHeightMeasureSpec = MeasureSpec.makeMeasureSpec(child.getLayoutParams().height,MeasureSpec.EXACTLY);
                else
                    panelHeightMeasureSpec = MeasureSpec.makeMeasureSpec(heightSpec,MeasureSpec.AT_MOST);
            }

            if (mainPanel.getKey().equalsIgnoreCase("center"))
                measureChild(child, MeasureSpec.makeMeasureSpec(centerPageMaxWidth,MeasureSpec.getMode(widthMeasureSpec)), panelHeightMeasureSpec);
            else if (mainPanel.getKey().equalsIgnoreCase("left")) {
                if (child.getLayoutParams().width == LayoutParams.WRAP_CONTENT)
                    leftPageWidthMeasureSpec = MeasureSpec.makeMeasureSpec(leftPageMaxWidth, MeasureSpec.AT_MOST);
                else if ((child.getLayoutParams().width == LayoutParams.MATCH_PARENT) || (child.getLayoutParams().width == 0))
                    leftPageWidthMeasureSpec = MeasureSpec.makeMeasureSpec(leftPageMaxWidth, MeasureSpec.EXACTLY);
                else
                    leftPageWidthMeasureSpec = MeasureSpec.makeMeasureSpec(child.getLayoutParams().width, MeasureSpec.EXACTLY);

                measureChild(child, leftPageWidthMeasureSpec, panelHeightMeasureSpec);
            } else {
                if (child.getLayoutParams().width == LayoutParams.WRAP_CONTENT)
                    rightPageWidthMeasureSpec = MeasureSpec.makeMeasureSpec(rightPageMaxWidth,MeasureSpec.AT_MOST);
                else if ((child.getLayoutParams().width == LayoutParams.MATCH_PARENT) || (child.getLayoutParams().width == 0))
                    rightPageWidthMeasureSpec = MeasureSpec.makeMeasureSpec(rightPageMaxWidth,MeasureSpec.EXACTLY);
                else
                    rightPageWidthMeasureSpec = MeasureSpec.makeMeasureSpec(child.getLayoutParams().width,MeasureSpec.EXACTLY);

                measureChild(child, rightPageWidthMeasureSpec, panelHeightMeasureSpec);
            }

            mainPanelsHeight.put(mainPanel.getKey(),child.getMeasuredHeight());
            mainPanelsWidth.put(mainPanel.getKey(),child.getMeasuredWidth());
        }

        int maxChildWidth = 0, maxChildHeight = 0, pageHeight;

        for (String POSITION : LayoutParams.POSITIONS) {
            if ((actionBarsWidth.containsKey(POSITION)) && (actionBarsWidth.get(POSITION) > maxChildWidth))
                maxChildWidth = actionBarsWidth.get(POSITION);

            if ((mainPanelsWidth.containsKey(POSITION)) && (mainPanelsWidth.get(POSITION) > maxChildWidth))
                maxChildWidth = mainPanelsWidth.get(POSITION);

            pageHeight = 0;

            if (actionBarsHeight.containsKey(POSITION))
                pageHeight = actionBarsHeight.get(POSITION);

            if (mainPanelsHeight.containsKey(POSITION))
                pageHeight += mainPanelsHeight.get(POSITION);

            if (pageHeight > maxChildHeight)
                maxChildHeight = pageHeight;
        }

        if ((this.fixLeftPanel) && (actionBarsWidth.containsKey("left")) && (actionBarsWidth.containsKey("center")) && ((actionBarsWidth.get("left") + actionBarsWidth.get("center") > maxChildWidth)))
            maxChildWidth = actionBarsWidth.get("left") + actionBarsWidth.get("center");

        if ((this.fixLeftPanel) && (mainPanelsWidth.containsKey("left")) && (mainPanelsWidth.containsKey("center")) && ((mainPanelsWidth.get("left") + mainPanelsWidth.get("center") > maxChildWidth)))
            maxChildWidth = mainPanelsWidth.get("left") + mainPanelsWidth.get("center");

        maxChildWidth += getPaddingLeft() + getPaddingRight();
        maxChildHeight += getPaddingTop() + getPaddingBottom();

        setMeasuredDimension(resolveSize(maxChildWidth,widthMeasureSpec),resolveSize(maxChildHeight, heightMeasureSpec));

        correctPosition();
    }

    /**
     * {@inheritDoc}
     *
     * @param changed
     * @param l
     * @param t
     * @param r
     * @param b
     */
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        final int count = getChildCount();
        final int paddingLeft = getPaddingLeft();
        final int paddingRight = getPaddingRight();
        final int paddingTop = getPaddingTop();
        final int paddingBottom = getPaddingBottom();

        for (int index = 0; index < count; index++) {
            View child = getChildAt(index);
            int measureWidth = child.getMeasuredWidth();
            ViewGroup.LayoutParams vLayoutParams = child.getLayoutParams();
            if (!(vLayoutParams instanceof LayoutParams)) continue;
            LayoutParams layoutParams = (LayoutParams) child.getLayoutParams();
            final int childMarginLeft = layoutParams.leftMargin;
            final int childMarginRight = layoutParams.rightMargin;
            final int childMarginTop = layoutParams.topMargin;
            final int childMarginBottom = layoutParams.bottomMargin;

            int childLeft = l+childMarginLeft + paddingLeft;
            int childTop = t+childMarginTop + paddingTop;
            int childRight = r-childMarginRight - paddingRight;
            int childBottom = b-childMarginBottom - paddingBottom;

            switch(layoutParams.getPosition()) {
                case center:
                    switch (layoutParams.getType()) {
                        case actionBar:
                            if ((actualPosition > 0) && (actualPosition >= (this.centerActionBarLeftVisibleWidth-this.centerMainPanelLeftVisibleWidth))) {
                                childLeft += (actualPosition - this.centerActionBarLeftVisibleWidth + this.centerMainPanelLeftVisibleWidth);
                                childRight += (actualPosition - this.centerActionBarLeftVisibleWidth + this.centerMainPanelLeftVisibleWidth);
                            } else if ((actualPosition <  0) && (actualPosition <= (this.centerMainPanelRightVisibleWidth-this.centerActionBarRightVisibleWidth))) {
                                childLeft += (actualPosition + this.centerActionBarRightVisibleWidth - this.centerMainPanelRightVisibleWidth);
                                childRight += (actualPosition + this.centerActionBarRightVisibleWidth - this.centerMainPanelRightVisibleWidth);
                            }
                            if (this.fixLeftPanel)
                                childLeft += this.fixedLeftPanelWidth;

                            childBottom -= mainPanels.get("center").getMeasuredHeight() + ((LayoutParams)mainPanels.get("center").getLayoutParams()).topMargin + ((LayoutParams)mainPanels.get("center").getLayoutParams()).bottomMargin;
                            break;
                        case mainPanel:
                            childLeft += actualPosition;
                            childRight += actualPosition;

                            if (this.fixLeftPanel)
                                childLeft += this.fixedLeftPanelWidth;

                            childTop += actionBars.get("center").getMeasuredHeight() + ((LayoutParams)actionBars.get("center").getLayoutParams()).topMargin + ((LayoutParams)actionBars.get("center").getLayoutParams()).bottomMargin;
                            break;
                        default:
                            continue;
                    }
                    break;
                case left:
                    childLeft -= (measureWidth-actualPosition);
                    switch (layoutParams.getType()) {
                        case actionBar:
                            childLeft -= (this.centerActionBarLeftVisibleWidth-this.centerMainPanelLeftVisibleWidth);
                            if (((actualPosition >= 0) && (actualPosition >= (this.centerActionBarLeftVisibleWidth-this.centerMainPanelLeftVisibleWidth))) ||
                                    ((actualPosition <  0) && (actualPosition <= (this.centerMainPanelRightVisibleWidth-this.centerActionBarRightVisibleWidth)))) {
                                childRight -= (measureWidth+(2*this.centerActionBarLeftVisibleWidth)-this.centerMainPanelLeftVisibleWidth-actualPosition);
                            } else {
                                childRight -= (measureWidth+(2*this.centerActionBarLeftVisibleWidth)-this.centerMainPanelLeftVisibleWidth);
                            }
                            if (this.fixLeftPanel) {
                                childLeft = Math.min(Math.round(this.actualPosition),0);
                                childRight += measureWidth-(2*this.centerActionBarLeftVisibleWidth)+this.centerMainPanelLeftVisibleWidth;
                            }

                            childBottom -= mainPanels.get("left").getMeasuredHeight() + ((LayoutParams)mainPanels.get("left").getLayoutParams()).topMargin + ((LayoutParams)mainPanels.get("left").getLayoutParams()).bottomMargin;
                            break;
                        case mainPanel:
                            childTop += actionBars.get("left").getMeasuredHeight() + ((LayoutParams)actionBars.get("left").getLayoutParams()).topMargin + ((LayoutParams)actionBars.get("left").getLayoutParams()).bottomMargin;
                            childRight -= (measureWidth-actualPosition+this.centerMainPanelLeftVisibleWidth);
                            if (this.fixLeftPanel) {
                                childLeft = Math.min(Math.round(this.actualPosition),0);
                                childRight += measureWidth-this.centerMainPanelLeftVisibleWidth;
                            }
                            break;
                        default:
                            continue;
                    }
                    break;
                case right:
                    switch (layoutParams.getType()) {
                        case actionBar:
                            childLeft = childRight - actionBars.get("right").getMeasuredWidth();
                            childBottom -= mainPanels.get("right").getMeasuredHeight() + ((LayoutParams)mainPanels.get("right").getLayoutParams()).topMargin + ((LayoutParams)mainPanels.get("right").getLayoutParams()).bottomMargin;
                            break;
                        case mainPanel:
                            childLeft = childRight - mainPanels.get("right").getMeasuredWidth();
                            childTop += actionBars.get("right").getMeasuredHeight() + ((LayoutParams)actionBars.get("right").getLayoutParams()).topMargin + ((LayoutParams)actionBars.get("right").getLayoutParams()).bottomMargin;
                            break;
                        default:
                            continue;
                    }
                    break;
                default:
                    continue;
            }

            child.layout(childLeft,childTop,childRight,childBottom);
        }

        this.actionBars.get("left").bringToFront();
        this.mainPanels.get("left").bringToFront();
        this.actionBars.get("center").bringToFront();
        this.mainPanels.get("center").bringToFront();

    }


    @Override
    public android.view.ViewGroup.LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new LayoutParams(getContext(), attrs);
    }

    public static class LayoutParams extends ViewGroup.MarginLayoutParams {
        public static final TreeSet<String> POSITIONS = new TreeSet<String>(String.CASE_INSENSITIVE_ORDER){{
            add("center");
            add("left");
            add("right");
        }};
        public static enum LAYOUT_TYPE { actionBar, mainPanel }
        public static enum LAYOUT_POSITION { center, left, right }

        private LAYOUT_POSITION position;
        public void setPosition (String position) {
            if (POSITIONS.contains(position))
                this.position = LAYOUT_POSITION.valueOf(POSITIONS.tailSet(position).first());
            else
                throw new IllegalArgumentException("Specified position is not valid.");
        }
        public LAYOUT_POSITION getPosition() {
            return this.position;
        }

        private LAYOUT_TYPE type;
        public void setType (LAYOUT_TYPE type) {
            this.type = type;
        }
        public LAYOUT_TYPE getType() {
            return this.type;
        }

        public LayoutParams(Context context, AttributeSet attrs) {
            super(context, attrs);

            TypedArray a = context.obtainStyledAttributes(attrs,R.styleable.FloatingPanel_Child, 0, 0);
            int pos = -1;
            int typ = -1;

            final int indexCount = a.getIndexCount();
            for (int index = 0; index < indexCount; index++) {
                if (a.getIndex(index) == R.styleable.FloatingPanel_Child_layout_position) {
                    pos = a.getInt(R.styleable.FloatingPanel_Child_layout_position,-1);
                } else if (a.getIndex(index) == R.styleable.FloatingPanel_Child_layout_type) {
                    typ = a.getInt(R.styleable.FloatingPanel_Child_layout_type,-1);
                }
            }

            switch (pos) {
                case 0: //center
                    this.position = LAYOUT_POSITION.center;
                    break;
                case 1: //left
                    this.position = LAYOUT_POSITION.left;
                    break;
                case 2: //right
                    this.position = LAYOUT_POSITION.right;
                    break;
                default:
                    throw new IllegalArgumentException("You must specify a layout_position for this view.");
            }

            switch(typ) {
                case 0:
                    this.type = LAYOUT_TYPE.actionBar;
                    break;
                case 1:
                    this.type = LAYOUT_TYPE.mainPanel;
                    break;
                default:
                    throw new IllegalArgumentException("You must specify a layout_type for this view.");
            }

            a.recycle();
        }
        public LayoutParams(int width, int height) {
            super(width, height);
        }

        public LayoutParams(int width, int height, String position, LAYOUT_TYPE type) {
            super(width, height);

            this.setPosition(position);
            this.setType(type);
        }

        public LayoutParams(android.view.ViewGroup.LayoutParams layoutParams) {
            super(layoutParams);

            if (layoutParams instanceof LayoutParams) {
                this.setPosition(((LayoutParams) layoutParams).getPosition().name());
                this.setType(((LayoutParams) layoutParams).getType());
            }
        }
    }

    @Override
    public boolean shouldDelayChildPressedState() {
        return false;
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        SavedState savedState = new SavedState(super.onSaveInstanceState());

        savedState.actualState = actualState;

        return savedState;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        SavedState savedState = (SavedState) state;
        super.onRestoreInstanceState(savedState.getSuperState());

        actualState = savedState.actualState;
        restored = true;

        requestLayout();
        invalidate();
    }

    public static class SavedState extends BaseSavedState {
        private int actualState;

        SavedState(Parcelable superState) {
            super(superState);
        }

        private SavedState(Parcel in) {
            super(in);

            actualState = in.readInt();
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);

            out.writeInt(actualState);
        }

        public static final Parcelable.Creator<SavedState> CREATOR = new Parcelable.Creator<SavedState>() {
            @Override
            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in);
            }

            @Override
            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };
    }
}
