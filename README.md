# 行情 Android App

自包含：APK 里自带界面，直接用手机网络从 Yahoo Finance 取行情，不需要自己的服务器。

## 获取 APK（不用装 Android Studio）
1. 在 GitHub 新建一个仓库，把本目录所有文件（包含隐藏的 .github 文件夹）上传到仓库根目录。
2. 打开仓库的 Actions 页面，等 “Build APK” 跑完（约 3-5 分钟），或点 Run workflow 手动触发。
3. 点进完成的运行记录，在 Artifacts 里下载 quotes-apk，解压得到 app-debug.apk。
4. 传到手机，允许“安装未知来源应用”后安装。

## 本地编译
装好 Android Studio 打开本目录，或在配好 JDK 17 与 Android SDK 的机器上运行 `gradle assembleDebug`。
