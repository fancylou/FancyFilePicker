# FancyFilePicker
[![](https://jitpack.io/v/fancylou/FancyFilePicker.svg)](https://jitpack.io/#fancylou/FancyFilePicker/v3.0.0)

使用Kotlin写的一个Android文件选择器，轻便，漂亮，尽量遵循Material Design

分类：

![](https://raw.githubusercontent.com/fancylou/FancyFilePicker/master/screenshots/FancyFilePicker-classification-3.0.0.jpeg)

本地目录：

![](https://raw.githubusercontent.com/fancylou/FancyFilePicker/master/screenshots/FancyFilePicker-local-3.0.0.jpeg)

单选：

![](https://raw.githubusercontent.com/fancylou/FancyFilePicker/master/screenshots/FancyFilePicker-single-3.0.0.jpeg)

图片选择器：

![](https://raw.githubusercontent.com/fancylou/FancyFilePicker/master/screenshots/FancyFilePicker-picture-m-3.0.0.jpeg)



## 使用

在gradle文件添加引用:

```groovy
dependencies {
	compile 'net.muliba.fancyfilepickerlibrary:fancyfilepickerlibrary:3.0.0'
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









