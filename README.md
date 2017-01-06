# Unishare

## 功能说明

目前支持分享到新浪微博，QQ，QQ空间，微信和微信朋友圈，易信和易信朋友圈

## 使用说明

1. 引入sdk
    导入此module，并建立app module到unishare_sdk module的依赖关系
	也就是在app的build.gradle中添加
	dependencies {
		compile project(':unishare_sdk')
	}

2. AndroidManifest配置
	在app的AndroidManifest中添加如下权限，如果某个权限已经有了，则不用重复添加。这些权限大部分app都会用到，所以一般来说，不需要再额外添加了。
	<uses-permission android:name="android.permission.INTERNET" />
	<uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />
	<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
	<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />

3. 处理微信和易信的回调activity
	微信和易信要求分享完成后的回调activity必须是当前app包名下固定名字的类，这里需要将wxapi和yxapi两个目录拷贝到应用包名所在的目标。
	例如应用包名的com.me.test，则将wxapi和yxapi拷贝到/com/me/test目录中。
	如果通过Android Studio拷贝，则会自动修改类的package，如果是通过系统的文件复制，则需要手动处理下wxapi和yxapi两个目录下四个类文件的package。

4. 替换appid
	

5. 完成分享功能
	分享的功能都封装在ShareApi类中。
	isPlatformInstalled(): 判断要分享的目标平台是否已经安装
	share()：分享


 