# FancyFilePicker
[![](https://jitpack.io/v/fancylou/FancyFilePicker.svg)](https://jitpack.io/#fancylou/FancyFilePicker/v2.1.3)

使用Kotlin写的一个Android文件选择器，轻便，漂亮，尽量遵循Material Design

![](http://muliba.u.qiniudn.com/blog/post/filePicker1.2.0-1.jpeg?imageMogr2/auto-orient/thumbnail/720x/blur/1x0/quality/75|imageslim)

自定义标题和背景色：

![](http://muliba.u.qiniudn.com/blog/post/filePicker1.2.0-2.jpeg?imageMogr2/auto-orient/thumbnail/720x/blur/1x0/quality/75|imageslim)

单选：

![](http://muliba.u.qiniudn.com/blog/post/filePicker1.2.0-3.jpeg?imageMogr2/auto-orient/thumbnail/720x/blur/1x0/quality/75|imageslim)

分类模式：

![](http://muliba.u.qiniudn.com/blog/post/FilePicker_2.0.0.jpg)



## 使用

在gradle文件添加引用:

```groovy
dependencies {
	compile 'net.muliba.fancyfilepickerlibrary:fancyfilepickerlibrary:2.0.0'
}
```

### Kotlin中使用

```kotlin
FilePicker()
        .withActivity(this)
        .requestCode(0)
        .start()
```

接收结果:

```kotlin
override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    if (resultCode == Activity.RESULT_OK) {
        if (requestCode == 0) {
            val array = data?.getStringArrayListExtra(FilePicker.FANCY_FILE_PICKER_ARRAY_LIST_RESULT_KEY)
            ...
            return
        }
    }
    super.onActivityResult(requestCode, resultCode, data)
}
```

单选：

```kotlin
FilePicker()
	.withActivity(this)
    .requestCode(0)
    .chooseType(FilePicker.CHOOSE_TYPE_SINGLE)
    .start()
```

接收结果：

```kotlin
override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    if (resultCode == Activity.RESULT_OK) {
        if (requestCode == 0) {
           val result = data?.getStringExtra(FilePicker.FANCY_FILE_PICKER_SINGLE_RESULT_KEY)
            ...
            return
        }
    }
    super.onActivityResult(requestCode, resultCode, data)
}
```

分类模式：

```kotlin
FilePicker()
                .withActivity(this)
                .requestCode(FILE_PICKER_REQUEST_CODE)
                .mode(FilePicker.CHOOSE_MODE_CLASSIFICATION)
                .start()
```

自定义标题和颜色：

```
FilePicker()
        .withActivity(this)
        .title("自定义标题")
        .actionBarColor(ContextCompat.getColor(this, R.color.colorAccent))
        .requestCode(0)
        .start()
```

### JAVA中使用

```java
new FilePicker().withActivity(this)
                .requestCode(0)
                .chooseType(FilePicker.CHOOSE_TYPE_SINGLE())
                .start();
```

还有别忘了添加权限：

```xml
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
```









