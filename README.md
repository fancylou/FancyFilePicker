# FancyFilePicker

Material Design File Picker library for Android , code with Kotlin.

![](http://muliba.u.qiniudn.com/blog/post/FilePicker1.jpg)



![](http://muliba.u.qiniudn.com/blog/post/FilePicker2.jpg)



![](http://muliba.u.qiniudn.com/blog/post/FilePicker3.jpg)



## using

dependency in application module gradle file:

```groovy
dependencies {
	compile 'net.muliba.fancyfilepickerlibrary:fancyfilepickerlibrary:1.1.1'
}
```



open FancyFilePicker with:

```kotlin
FilePicker()
        .withActivity(this)
        .requestCode(FILE_PICKER_REQUEST_CODE)
        .start()
```

and receive the result:

```kotlin
override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    if (resultCode == Activity.RESULT_OK) {
        if (requestCode == FILE_PICKER_REQUEST_CODE) {
            val array = data?.getStringArrayListExtra(FilePicker.FANCY_FILE_PICKER_ARRAY_LIST_RESULT_KEY)
            ...
            return
        }
    }
    super.onActivityResult(requestCode, resultCode, data)
}
```

of course, do not forget add permission

```xml
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
```





