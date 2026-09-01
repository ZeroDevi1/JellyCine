# 更新说明

面向用户的 GitHub Release 正文。发版时 CI 读取与 `appVersionName` 对应的 `## x.y.z` 章节。

写法对齐已发布的 `Vela v1.0.0`：一句简介、若干要点、结尾提示选架构。不要写 commit / PR changelog。

## 1.1.0

手机端媒体库海报更清晰；页面进入与返回转场更完整。

- 媒体库海报、横图与 Banner 按更高分辨率请求，减少发糊
- 进入详情等页面时从右侧滑入；返回时整页滑出
- 小米/红米 HyperOS 侧滑返回时也会播放完整退出动画，不再几乎无动画

请根据设备架构选择对应 APK。

## 1.0.3

播放设置增加杜比开关；从播放器返回演员或详情页时不再整页刷新。

- 设置「画面」增加杜比亮度增强（默认开，仅作用于杜比视界）与 DV Profile 7 转 8.1（默认关，改完需重开当前片）
- 设备不支持杜比视界时回退 HDR10 / SDR，避免黑屏
- 从播放器返回演职员或影片详情时保留已加载内容，不再闪成加载态

请根据设备架构选择对应 APK。

## 1.0.1

关于页可检查 GitHub Release，按机型与架构下载更新。

- 关于页支持检查最新 GitHub Release 并安装对应 APK
- 按 Phone / TV 与 CPU 架构推荐安装包
- 支持 gh-proxy 等 CDN 加速下载

请根据设备架构选择对应 APK。

## 1.0.0

Vela 首个正式版本。

- 提供 Android Phone 与 Android TV 客户端
- 支持 arm64-v8a、armeabi-v7a、x86 与 x86_64
- 接入 Vela 独立 Discord Social SDK 1.10.18687 与 Rich Presence
- 使用 Vela 独立 Discord Application ID 和移动端 OAuth 回调

请根据设备架构选择对应 APK。
