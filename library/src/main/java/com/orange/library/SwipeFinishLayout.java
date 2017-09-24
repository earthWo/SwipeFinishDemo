package com.orange.library;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Scroller;

import win.whitelife.swipefinishlibrary.R;


/**
 * Created by wuzefeng on 16/9/14.
 */

public class SwipeFinishLayout extends FrameLayout {


    /**
     * 滑动的View
     */
    private View rootView;


    /**
     * 整个view的宽
     */
    private int width;

    /**
     * 整个view的高
     */
    private int height;

    /**
     * 可以滑动的位置
     */
    private Rect firstTouchRect;

    /**
     * 是否开始滑动
     */
    private boolean startSwipe=false;

    private Scroller scroller;

    private final int DEFAULT_SCROLL_TIME=500;


    private final int DEFAULT_TOUCH_SIZE=50;

    /**
     * 滑动动画时间
     */
    private  int MAX_SCROLL_TIME=DEFAULT_SCROLL_TIME;


    /**
     * 是否包含actionbar
     */
    private boolean isFullScreen;


    /**
     * 触控范围
     */
    private int touchSize=DEFAULT_TOUCH_SIZE;

    /**
     * 触控范围比例
     */
    private float touchScale;


    /**
     * 是否结束activity
     */
    private boolean isFinish=false;

    /**
     * 按下的触点
     */
    private Point startPoint;

    /**
     * 结束时的区域范围
     */
    private float scrollEndScale=0.3f;


    /**
     * 滑到最终的透明度
     */
    private float finalAlpha=1f;

    /**
     * 滑动方式
     */
    private ScrollMode scrollMode=ScrollMode.LEFT;



    public SwipeFinishLayout(Context context) {
        this(context,null);
    }

    public SwipeFinishLayout(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public SwipeFinishLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttr(context,attrs);
        init(context);
    }



    private void initAttr(Context context, AttributeSet attrs){
        TypedArray typedArray=context.obtainStyledAttributes(attrs, R.styleable.SwipeFinishLayout);


        if(typedArray.hasValue(R.styleable.SwipeFinishLayout_final_alpha)){
            finalAlpha=typedArray.getFloat(R.styleable.SwipeFinishLayout_final_alpha,1f);
        }

        if(typedArray.hasValue(R.styleable.SwipeFinishLayout_is_full_screen)){
            isFullScreen=typedArray.getBoolean(R.styleable.SwipeFinishLayout_is_full_screen,false);
        }

        if(typedArray.hasValue(R.styleable.SwipeFinishLayout_max_scroll_time)){
            MAX_SCROLL_TIME=typedArray.getInt(R.styleable.SwipeFinishLayout_max_scroll_time,DEFAULT_SCROLL_TIME);
        }

        if(typedArray.hasValue(R.styleable.SwipeFinishLayout_scroll_mode)){
            int l=typedArray.getInt(R.styleable.SwipeFinishLayout_scroll_mode,0);
            setMode(l);
        }


        typedArray.recycle();
    }

    /**
     * 初始化
     * @param context
     */
    private void init(Context context){
        if(isFullScreen){
            rootView= (View) ((Activity)context).findViewById(android.R.id.content).getParent().getParent();
        }else{
            rootView=this;
        }
        startPoint=new Point();
    }


    private void setMode(int mode){
        switch (mode){
            case 0:
                scrollMode=ScrollMode.LEFT;
                break;
            case 1:
                scrollMode=ScrollMode.RIGHT;
                break;
            case 2:
                scrollMode=ScrollMode.TOP;
                break;
            default:
                scrollMode=ScrollMode.BOTTOM;
                break;
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        width=MeasureSpec.makeMeasureSpec(widthMeasureSpec,MeasureSpec.UNSPECIFIED);
        height=MeasureSpec.makeMeasureSpec(heightMeasureSpec,MeasureSpec.UNSPECIFIED);
        setMeasuredDimension(width,height);
        //如果包含actionbar，重新计算高度
        if(isFullScreen){
            height=getScreenHeight();
        }
        //设置可滑动区域
        setSize();
        //设置触控范围
        setFirstTouchRect();
    }


    /**
     * 获取全屏高度
     * @return
     */
    private int getScreenHeight(){
        DisplayMetrics dm = getContext().getResources().getDisplayMetrics();
        return dm.heightPixels;
    }

    /**
     * 设置滑动触控位置
     */
    private void setFirstTouchRect(){
        int left,right,top,bottom;
        switch (scrollMode){
            case LEFT:
                left=0;
                top=0;
                bottom=height;
                right=touchSize;
                break;
            case RIGHT:
                left=width-touchSize;
                top=0;
                bottom=height;
                right=width;
                break;
            case TOP:
                left=0;
                top=0;
                bottom=touchSize;
                right=width;
                break;
            default:
                left=0;
                top=height-touchSize;
                bottom=height;
                right=width;
                break;
        }
        if(firstTouchRect!=null){
            firstTouchRect.set(left,top,right,bottom);
        }else{
            firstTouchRect=new Rect(left,top,right,bottom);
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        //在触控范围内时拦截点击事件
        if(startSwipe||firstTouchRect.contains((int)ev.getX(),(int)ev.getY()))return true;
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        float x=event.getRawX();
        float y = event.getRawY();

        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                //设置初始点位置
                if(!startSwipe&&firstTouchRect.contains((int)x,(int)y)){
                    startSwipe=true;
                    startPoint.set((int)x,(int)y);
                }
                break;
            case MotionEvent.ACTION_MOVE:
                //开始滑动时，根据当前的触点位置设置view的滑动位置
                if(startSwipe) {
                    int xMove = (int) (x - startPoint.x);
                    int yMove = (int) (y - startPoint.y);
                    switch (scrollMode) {
                        case LEFT:
                            if (xMove < 0) {
                                xMove = 0;
                            }
                            rootView.layout(xMove, 0, width + xMove, height);
                            break;
                        case RIGHT:
                            if (xMove > 0) {
                                xMove = 0;
                            }
                            rootView.layout(xMove, 0, width + xMove, height);
                            break;
                        case TOP:
                            if (yMove < 0) {
                                yMove = 0;
                            }
                            rootView.layout(0, yMove, width, height + yMove);
                            break;
                        case BOTTOM:
                            if (yMove > 0) {
                                yMove = 0;
                            }
                            rootView.layout(0, yMove, width, height + yMove);
                            break;
                    }
                    //设置view的透明度
                    float a = getViewFinalAlpha(scrollMode == ScrollMode.RIGHT || scrollMode == ScrollMode.LEFT ? xMove : yMove);
                    rootView.setAlpha(a);
                }
                break;
            case MotionEvent.ACTION_UP:
                //开始滑动动画
                if(startSwipe) {
                    startScroll(canFinishActivity(event), scrollMode == ScrollMode.RIGHT || scrollMode == ScrollMode.LEFT ? rootView.getLeft() : rootView.getTop());
                    startSwipe = false;
                }
                break;
        }
        return true;
    }


    /**
     * 根据滑动位置获取透明度
     * @param scrollMove
     * @return
     */
    private float getViewFinalAlpha(int scrollMove){
        scrollMove=Math.abs(scrollMove);
        if(scrollMode==ScrollMode.RIGHT||scrollMode==ScrollMode.LEFT){
            return 1-(1-finalAlpha)*(scrollMove*1.0f/width);
        }else{
            Log.d("to透明度", scrollMove+"");
            return 1-(1-finalAlpha)*(scrollMove*1.0f/height);
        }
    }

    public float getFinalAlpha() {
        return finalAlpha;
    }

    public void setFinalAlpha(float finalAlpha) {
        this.finalAlpha = finalAlpha;
    }

    /**
     * 是否可以结束activity
     * @param event
     * @return
     */
    private boolean canFinishActivity(MotionEvent event){
        if(scrollMode==ScrollMode.RIGHT||scrollMode==ScrollMode.LEFT){
            return Math.abs(event.getRawX()-startPoint.x)>width*scrollEndScale;
        }else{
            return Math.abs(event.getRawY()-startPoint.y)>height*scrollEndScale;
        }
    }


    /**
     * 开始滑动动画
     * @param isFinish
     * @param start
     */
    private void startScroll(boolean isFinish,int start){
        scroller=new Scroller(getContext());
        this.isFinish=isFinish;
        int time=0;
        if(!isFinish){
            //根据不同的滑动方式，设置滑动时间和滑动位置
            switch (scrollMode){
                case LEFT:
                    time= (int) (start*1.0f/(width*scrollEndScale)*MAX_SCROLL_TIME);
                    scroller.startScroll(start,0,-start,0,time);
                    break;
                case RIGHT:
                    time= (int) ((width+start*1.0f)/(width*scrollEndScale)*MAX_SCROLL_TIME);
                    scroller.startScroll(start,0,-start,0,time);
                    break;
                case TOP:
                    time= (int) (start*1.0f/(height*scrollEndScale)*MAX_SCROLL_TIME);
                    scroller.startScroll(0,start,0,-start,time);
                    break;
                case BOTTOM:
                    time= (int) ((height+start*1.0f)/(height*scrollEndScale)*MAX_SCROLL_TIME);
                    scroller.startScroll(0,start,0,-start,time);
                    break;
            }

        }else{
            time=MAX_SCROLL_TIME;
            switch (scrollMode){
                case LEFT:
                    scroller.startScroll(start,0,width-start,0,time);
                    break;
                case RIGHT:
                    scroller.startScroll(start,0,-width+start,0,time);
                    break;
                case TOP:
                    scroller.startScroll(0,start,0,height-start,time);
                    break;
                case BOTTOM:
                    scroller.startScroll(0,start,0,-height+start,time);
                    break;
            }
        }
        invalidate();
    }

    public int getScrollTime() {
        return MAX_SCROLL_TIME;
    }

    public void setScrollTime(int scrollTime) {
        this.MAX_SCROLL_TIME = scrollTime;
    }

    @Override
    public void computeScroll() {
        if(scroller!=null&&scroller.computeScrollOffset()){
            int x=scroller.getCurrX();
            int y=scroller.getCurrY();
            if(rootView!=null) {
                rootView.layout(x,y,width+x,height+y);
            }
            rootView.setAlpha(getViewFinalAlpha(scrollMode==ScrollMode.RIGHT||scrollMode==ScrollMode.LEFT?x:y));
            invalidate();
        }else if(scroller!=null&&!scroller.computeScrollOffset()&&isFinish){
            scroller=null;
            if(getContext() instanceof Activity){
                ((Activity) getContext()).finish();
            }
        }else{
            scroller=null;
        }
    }


    public boolean isFullScreen() {
        return isFullScreen;
    }

    public void setFullScreen(boolean isFullScreen) {
        this.isFullScreen = isFullScreen;
        if(isFullScreen){
            height=getScreenHeight();
        }
        init(getContext());
    }


    public ScrollMode getScrollMode() {
        return scrollMode;
    }

    public void setScrollMode(ScrollMode scrollMode) {
        this.scrollMode = scrollMode;
    }

    public int getTouchSize() {
        return touchSize;
    }

    public void setTouchSize(int touchSize) {
        this.touchSize = touchSize;
        setFirstTouchRect();
    }

    public float getTouchScale() {
        return touchScale;
    }

    public void setTouchScale(float touchScale) {
        if(touchScale>0&&touchScale<1) {
            this.touchScale = touchScale;
            setSize();
            setFirstTouchRect();
        }
    }


    /**
     * 设置触碰区域
     */
    private void setSize(){
        if (width != 0 && height != 0 && touchScale>0) {
            if (scrollMode == ScrollMode.LEFT || scrollMode == ScrollMode.RIGHT)
                touchSize = (int) (width * touchScale);
            if (scrollMode == ScrollMode.TOP || scrollMode == ScrollMode.BOTTOM)
                touchSize = (int) (height * touchScale);
        }
    }

    public static enum ScrollMode{
        LEFT,//从左向右
        RIGHT,//从有向左
        TOP,//从上向下
        BOTTOM//从下向上
    }

}
