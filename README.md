# FancyFilePicker
[![](https://jitpack.io/v/fancylou/FancyFilePicker.svg)](https://jitpack.io/#fancylou/FancyFilePicker/v4.0.0)

使用Kotlin写的一个Android文件选择器，轻便，漂亮，尽量遵循Material Design



| 分类选择 | 本地目录 | 单选 | 图片选择器 |
|:--------:|:--------:|:--------:|:--------:|
|![](https://raw.githubusercontent.com/fancylou/FancyFilePicker/master/screenshots/FancyFilePicker-classification-3.0.0.jpeg)| ![](https://raw.githubusercontent.com/fancylou/FancyFilePicker/master/screenshots/FancyFilePicker-local-3.0.0.jpeg) | ![](https://raw.githubusercontent.com/fancylou/FancyFilePicker/master/screenshots/FancyFilePicker-single-3.0.0.jpeg)| ![](https://raw.githubusercontent.com/fancylou/FancyFilePicker/master/screenshots/FancyFilePicker-picture-m-3.0.0.jpeg) |

 



## 使用

在gradle文件添加引用:

```groovy
dependencies {
	compile 'net.muliba.fancyfilepickerlibrary:fancyfilepickerlibrary:4.0.0'
}
```

### Kotlin中使用

多选：

```kotlin
FilePicker()
    .withActivity(this)
    .forResult { filePaths ->
                 ...
               }
```

 单选：

```kotlin
FilePicker()
    .withActivity(this)
		.chooseType(FilePicker.CHOOSE_TYPE_SINGLE)
    .forResult { filePaths ->
                 ...
               }
```



### JAVA中使用

```java
new FilePicker()
  	.withActivity(this)
    .chooseType(FilePicker.CHOOSE_TYPE_SINGLE())
    .forResult(new Function1<List<String>, Unit>() {
      @Override
      public Unit invoke(List<String> strings) {
        ...
          return Unit.INSTANCE;
      }
    });
```

还有别忘了添加权限：

```xml
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
```









