# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in D:\Android\sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

-repackageclasses
#忽略警告
-ignorewarnings

#指定压缩级别
-optimizationpasses 5
#包名不混合大小写
-dontusemixedcaseclassname
#不去忽略非公共的库类
-dontskipnonpubliclibraryclasses
#不优化输入的类文件
-dontoptimize
#预校验
-dontpreverify
#混淆时是否记录日志
-verbose
#混淆前后的映射
-printmapping proguardMapping.txt
#不跳过非公共的库的类成员
-dontskipnonpubliclibraryclassmembers

#混淆时采用的算法
-optimizations !code/simplification/arithmetic,!code/simplification/cast,!field/*,!class/merging/*
#保护注解
-keepattributes *Annotation*,InnerClasses
#把混淆类中的方法名也混淆了
-useuniqueclassmembernames

#优化时允许访问并修改有修饰符的类和类的成员
-allowaccessmodification

#将文件来源重命名为“SourceFile”字符串
-renamesourcefileattribute SourceFile
#保留行号
-keepattributes SourceFile,LineNumberTable
#保持泛型
-keepattributes Signature

##记录生成的日志数据,gradle build时在本项目根目录输出##
#apk 包内所有 class 的内部结构
-dump class_files.txt
#未混淆的类和成员-printseeds seeds.txt
#列出从 apk 中删除的代码
-printusage unused.txt

#需要保留的东西
-keep class android.support.annotation.Keep
-keep @android.support.annotation.Keep class * {*;}
-keepclasseswithmembers class * {
    @android.support.annotation.Keep <methods>;
}
-keepclasseswithmembers class * {
    @android.support.annotation.Keep <fields>;
}
-keepclasseswithmembers class * {
    @android.support.annotation.Keep <init>(...);
}

#保持所有实现 Serializable 接口的类成员
-keepclassmembers class * implements java.io.Serializable {
      static final long serialVersionUID;
      private static final java.io.ObjectStreamField[] serialPersistentFields;
      !static !transient <fields>;
      !private <fields>;
      !private <methods>;
      private void writeObject(java.io.ObjectOutputStream);
      private void readObject(java.io.ObjectInputStream);
      java.lang.Object writeReplace();
      java.lang.Object readResolve();
}

# 保持测试相关的代码
-dontnote junit.framework.**
-dontnote junit.runner.**
-dontwarn android.test.**
-dontwarn android.support.test.**
-dontwarn org.junit.**

-dontwarn android.support.**
-dontwarn android.os.**
-dontwarn com.android.internal.app.LocalePicker
-dontwarn android.app.*
-dontwarn android.support.v4.**
-keep class android.support.v4.** { *; }
-keep interface android.support.v4.app.** { *; }
-keep public class * extends android.support.v4.**

#Fragment不需要在AndroidManifest.xml中注册，需要额外保护下
-keep public class * extends android.support.v4.app.Fragment
-keep public class * extends android.app.Fragment

#保持哪些类不被混淆
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.preference.Preference
-keep public class com.android.vending.licensing.ILicensingService
-keep public class * extends android.view.View
-keep public class * extends java.lang.Throwable {*;}
-keep public class * extends java.lang.Exception {*;}
-keep class android.support.** {*;}

#保持所有拥有本地native方法的类名及本地方法名
-keepclasseswithmembernames class * {
    native <methods>;
}

#保持Activity中View及其子类入参的方法
-keepclassmembers class * extends android.app.Activity{
    public void *(android.view.View);
}

#枚举
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

#保持自定义View的get和set相关方法
-keep public class * extends android.view.View{
    *** get*();
    void set*(***);
    public <init>(android.content.Context);
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
}

-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
}

#保持Parcelable不被混淆
-keep class * implements android.os.Parcelable {
    public static final android.os.Parcelable$Creator *;
}

#不混淆资源类
-keep class **.R$* {
 *;
}

-keepclassmembers class * {
    void *(**On*Event);
}

#JS接口类不混淆，否则执行不了
-keepclassmembers class * {
    public <init>(org.json.JSONObject);
}

-keepclassmembers class * {
    public void *ButtonClicked(android.view.View);
}

-keep class com.desay_svautomotive.tspaccount.MyApplication.TSPAccountApplication {*;}
-dontwarn  com.desay_svautomotive.tspaccount.MyApplication.TSPAccountApplication

-keep class com.desaysv.vehicle.theft.** {*;}
-dontwarn  com.desaysv.vehicle.theft.**

-keep class com.desaysv.vehicle.TspBigDataPointService.** {*;}
-dontwarn  com.desaysv.vehicle.TspBigDataPointService.**

-keep class com.desay_svautomotive.tspaccount.receiver.** {*;}
-dontwarn  com.desay_svautomotive.tspaccount.receiver.**

#butterknife 8.4.0
-keep class butterknife.** { *; }
-dontwarn butterknife.internal.**
#-dontwarn  butterknife.**
-keep class **$$ViewBinder { *; }

-keepclasseswithmembernames class * {
    @butterknife.* <fields>;
}

-keepclasseswithmembernames class * {
    @butterknife.* <methods>;
}

-keep class com.bumptech.glide.** {*;}
-dontwarn  com.bumptech.glide.**
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public enum com.bumptech.glide.load.resource.bitmap.ImageHeaderParser$** {
    **[] $VALUES;
    public *;
}

-keep class com.google.zxing.** {*;}
-dontwarn  com.google.zxing.**

#okhttp
-dontwarn okhttp3.**
-keep class okhttp3.**{*;}

#okio
-dontwarn okio.**
-keep class okio.**{*;}

-keep class com.lzy.okgo.** {*;}
-dontwarn  com.lzy.okgo.**

#io.reactivex:rxandroid:1.2.1
-keep class rx.android.** { *; }
-keep interface rx.android.** { *; }

-keep class rx.android.plugins.** { *; }
-keep interface rx.android.plugins.** { *; }

-keep class rx.android.schedulers.** { *; }
-keep interface rx.android.schedulers.** { *; }

-keep class com.tencent.android.tpush.** {* ;}
-keep class com.tencent.mid.** {* ;}
-keep class com.qq.taf.jce.** {*;}