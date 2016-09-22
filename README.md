# SwipeFinishDemo
右滑返回，自动关闭activity

## 示例
[效果图]{http://7xjrms.com1.z0.glb.clouddn.com/mzswipe%20finish.gif}

## 使用方法

### Gradle
```
compile 'library.whitelife:swipefinishlib:0.0.2'
```
### Maven
```
<dependency>
  <groupId>library.whitelife</groupId>
  <artifactId>swipefinishlib</artifactId>
  <version>0.0.2</version>
  <type>pom</type>
</dependency>
```
### 添加透明背景theme
```
<item name="android:windowIsTranslucent">true</item>
<item name="android:colorBackgroundCacheHint">@null</item>
<item name="android:windowBackground">@android:color/transparent</item>
```
### 添加activity动画
```
public class AboutActivity extends BaseActivity

```
