# UniShare

## 功能说明

目前集成了新浪微博，QQ，微信，易信的SDK，支持分享到新浪微博，QQ，QQ空间，微信，微信朋友圈，易信和易信朋友圈。各个SDK版本如下。

- 新浪微博：3.1.2
- QQ：3.1.1精简版
- 微信：3.1.1
- 易信：2.2.2

此外，还可以支持通过短信和系统分享界面来分享。

## 使用说明

### 1. 引入sdk
导入此module，并建立app module到unishare_sdk module的依赖关系

也就是在app的build.gradle的dependencies中添加compile project(':unishare_sdk')

### 2. AndroidManifest配置
在app的AndroidManifest中添加如下权限，如果某个权限已经有了，则不用重复添加。前四项权限是各个SDK需要的权限，大部分app都会用到，所以一般来说，不需要再额外添加了。
最后一个权限是为了解决QQ分享时部分情况下存在透明Activity的问题，参见最后一节“存在的问题”

- \<uses-permission android:name="android.permission.INTERNET" />
- \<uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />
- \<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
- \<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
- \<uses-permission android:name="android.permission.GET_TASKS" />

### 3. 处理微信和易信的回调activity
微信和易信要求分享完成后的回调activity必须是当前app包名下固定名字的类，这里需要将wxapi和yxapi两个目录拷贝到应用包名所在的目标。
例如应用包名的com.me.test，则将wxapi和yxapi拷贝到/com/me/test目录中。

如果通过Android Studio拷贝，则会自动修改类的package，如果是通过系统的文件复制，则需要手动处理下wxapi和yxapi两个目录下四个类文件的package。

### 4. 替换appid
在cnx.cclink.unishare.platform下有若干个类，每个类对应一个平台的分享功能，每个类中都有一个appid的常量，需要将其替换为应用的appid。

### 5. 完成分享功能
分享的功能都封装在ShareApi类中。

isPlatformInstalled(): 判断要分享的目标平台是否已经安装

share()：分享

## 存在的问题
当前版本QQ，微信，易信的分享中都分别存在的问题。这些问题大部分都是由于对应平台的APP在实现上的bug导致的。这类问题大部分都无法在我们自身的解决，只能期待APP的版本更新后能够修复。

1. QQ分享问题
    当调用QQ分享的API后，如果QQ未登陆时会出现QQ的登陆界面，如果QQ已登陆，则会直接出现分享的目标选择界面。
    对第一种情况，如果用户在进入QQ登陆界面后，没有正常登陆，而是直接取消，这时会有一个透明的Activity覆盖在调用分享api的Activity上，导致用户无法继续操作。这个Activity不会自动结束，只有按返回键将其结束掉后，才可以正常操作。
    对第二种情况，如果用户在进入分享目标选择界面后，按下Home，返回到桌面，然后再回到我们的app中，这时分享目标选择界面会消失，同样会有一个透明的Activity覆盖在调用分享api的Activity上

    这个问题是QQ APP中的一个bug。
    在QQ SDK中有一个Activity，com.tencent.connect.common.AssistActivity。正是这个Activity覆盖在调用分享api的Activity上。
    正常情况下AssistActivity会在onNewIntent的时候调用finish结束，但是对上述两种情况onNewIntent没有被触发，从而会一直覆盖在调用api的Activity之上。

    这个问题已经在QQShareActivity中解决，但是需要增加GET_TASKS权限（用来判断AssistActivity是不是还在Activity栈中）。

2. 微信分享问题
    微信分享完成后，会有一个选项，选择是继续留在微信还是返回调用的app，如果选择留在微信，之后再通过返回键返回到调用的app，这时调用的app是收不到任何回调的，无法知道分享的结果。
    此外，微信分享时，如果停在选择用户的界面，然后按下Home键，再切回到app中，这时选择用户的界面会消失。但是同样收不到任何回调。

3. 易信分享问题
    易信分享时，如果在选择用户的界面直接返回，这时调用的app也是收不到任何回调的。
    此外，和微信一样，如果停在选择用户的界面，然后按下Home键，再切回到app中，这时选择用户的界面会消失。但是同样收不到任何回调。
