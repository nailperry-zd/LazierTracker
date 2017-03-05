## 简介
用于Android客户端无埋点数据采集的Gradle插件。

Gradle Plugin for Codelessly Data Acquisition on Android Platform.

本插件基于开源项目[HiBeaver](https://github.com/BryanSharp/hibeaver)^_^

## 开发环境
- 语言：Groovy
- 字节码操作库：ASM5.0
- 工具：Android Studio 2.2
- Gradle：1.5+

## AOP在无埋点中的应用


### 1. Fragment生命周期

目标函数：

- onResume()V
- onPause()V
- setUserVisibleHint(Z)V
- onHiddenChanged(Z)V

具体实现：

- 对app中指定包进行扫描，筛选出所有父类为`android/app/Fragment`或`android/support/v4/app/Fragment`的类。
- 对这些Fragment子类的`onResumed`，`onPaused`，`onHiddenChanged`，`setFragmentUserVisibleHint`方法的字节码进行修改，添加数据采集代码。

### 2. 点击事件

目标函数：

- onClick(Landroid/view/View;)V
- onClick(Landroid/content/DialogInterface;I)V

具体实现：

- 对app中指定包进行扫描，分别筛选出实现了`android/view/View$OnClickListener`接口和`android/content/DialogInterface$OnClickListener`接口的类，在onClick方法中添加数据采集代码。
