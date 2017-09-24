# SwipeFinishDemo
右滑返回，自动关闭activity

## 示例
![效果图](http://7xjrms.com1.z0.glb.clouddn.com/mzswipe%20finish.gif)

## 使用方法：
```
<com.orange.library.SwipeFinishLayout
    xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:swipe="http://schemas.android.com/apk/res-auto"
    android:id="@+id/activity_main"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:background="#457"
    swipe:is_full_screen="true"
    swipe:max_scroll_time="300"
    swipe:scroll_mode="BOTTOM"
   >
```

## 重要参数说明
is_full_screen：是否全屏滑动
max_scroll_time：动画时间
scroll_mode：滑动的方式（从左向右、从右向左等）
touch_size：触控范围的size
touch_scale：触控范围的比例
final_alpha：滑动时最后的透明度