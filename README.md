#AIDL示例

跨进程通讯使用 binder机制的aidl
进程间传大数据方案：
    1. 进程内： 拆块断点续传、文件、FileProvider、sp、静态共享数据 等方式
    2. 跨进程： 拆块断点续传等方式。

## 运行方式
1. 安装 apk/myserver-debug.apk
2. 安装 apk/app-debug.apk
3. 点击安装上的 JAidl应用
4. 点击主界面『bind_service』，绑定跨进程服务，并查看logcat 日志
5. 点击主界面『GET PERSON』，实现跨进程获取数据回调

注： 如果传输的数据超过1M （1<<20 bytes）时，就会报：TransactionTooLargeException. 可在 MainActivity 的"getPerson"方法中体验值的影响

## 基本思路

1. 使用组件化开发方式，将 C 端 S 端及 AIDL，分成三个组件
2. 实现 C端 -> AIDL -> S 端接口
3. C 端向 S 端注册 Callback，实现 S端 -> AIDL -> C 端接口
4. 数据通讯时，非基本类型数据 ，需实现 Parcelable 接口，以实现进程问数据序列化与反序列化传递
5. 因 Bundle 限制进程间数据量1M 的约束，可采用多次数据交互实现（因 Binder 缓冲区大小限制1M）
6. 进程间共享对象，因放入AIDL关联模块中， 并也需要定义其aidl，标记其实现 Parcelable 接口

## 原理


