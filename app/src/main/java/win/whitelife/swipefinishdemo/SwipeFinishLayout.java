package win.whitelife.swipefinishdemo;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PointF;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Scroller;

/**
 * Created by wuzefeng on 16/9/14.
 */

public class SwipeFinishLayout extends ViewGroup {

    private FrameLayout rootView;

    private int width;

    private int height;

    private PointF lastPoint;


    private Rect firstTouchRect;

    private boolean startSwipe=false;


    private Scroller scroller;


    private final int MAX_SCROLL_TIME=500;


    private boolean isTransparent=false;


    public SwipeFinishLayout(Context context) {
        this(context,null);
    }

    public SwipeFinishLayout(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public SwipeFinishLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs,defStyleAttr);
    }

    private void init( AttributeSet attrs, int defStyleAttr){
        lastPoint=new PointF();
        rootView=new FrameLayout(getContext(),attrs,defStyleAttr);
        LayoutParams layoutParams=new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT);
        rootView.setLayoutParams(layoutParams);

        this.addView(rootView);
        this.setBackgroundColor(Color.TRANSPARENT);
    }


    @Override
    public void addView(View child, int index, LayoutParams params) {
        if(child==rootView) {
            super.addView(child, index, params);
        }else{
            rootView.addView(child,index,params);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        measureChildren(widthMeasureSpec,heightMeasureSpec);
        width=getMeasuredWidth();
        height=getMeasuredHeight();
        firstTouchRect=new Rect(0,0,width/7,height);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if(rootView!=null){
            rootView.layout(l,t,r,b);
        }
    }




    @Override
    public boolean onTouchEvent(MotionEvent event) {

        float x=event.getX();
        float y=event.getY();

        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                if(!startSwipe&&firstTouchRect.contains((int)x,(int)y)){
                    startSwipe=true;
                    lastPoint.x=x;
                    lastPoint.y=y;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if(startSwipe) {
                    if (this.getScrollX() + lastPoint.x - x <= 0) {
                        scrollBy((int) (lastPoint.x - x), 0);
                    } else {
                        scrollTo(0, 0);
                    }
                    lastPoint.x = x;
                    lastPoint.y = y;
                    if(rootView!=null&&isTransparent){
                        rootView.setAlpha(1-Math.abs(this.getScrollX()*1.0f/width)/5);
                    }
                    invalidate();
                }
                break;
            case MotionEvent.ACTION_UP:
                if(-getScrollX()>width/3){
                    startScroll(false,-getScrollX());
                }else{
                    startScroll(true,-getScrollX());
                }
                lastPoint.x=0;
                lastPoint.y=0;
                startSwipe=false;
                break;
        }


        return true;
    }

    private boolean isFinish=false;

    private void startScroll(boolean isClose,int startX){
        scroller=new Scroller(getContext());
        if(isClose){
            isFinish=false;
            int time= (int) (startX*1.0f/(width/3)*MAX_SCROLL_TIME);
            scroller.startScroll(-startX,0,startX,0,time);
        }else{
            isFinish=true;
            int time= (int) ((width-startX)*1.0f/(2*width/3)*MAX_SCROLL_TIME);
            scroller.startScroll(-startX,0,startX-width,0,time);
        }
        invalidate();
    }

    public void setSwipeTransparent(boolean isTransparent){
        this.isTransparent=isTransparent;
    }



    @Override
    public void computeScroll() {
        if(scroller!=null&&scroller.computeScrollOffset()){
            int x=scroller.getCurrX();
            int y=scroller.getCurrY();
            if(rootView!=null) scrollTo(x,y);
            if(rootView!=null&&isTransparent){
                rootView.setAlpha(1-Math.abs(this.getScrollX()*1.0f/width)/5);
            }
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

}
