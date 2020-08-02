package com.jiek.myserver;

import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.SystemClock;

import com.jiek.aidl.ICallback;
import com.jiek.aidl.IMyAidlInterface;
import com.jiek.aidl.Person;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class JService extends BaseService {

    private static final String TAG = "JService";

    IMyAidlInterface.Stub binder;
    Person person = new Person();

    //    远程服务主动调用客户端的接口，实现 C->S, S->C 双向交互。但 Bundle 的数据不能超过1M的约束
    ICallback callback;

    @Override
    public IBinder onBind(Intent intent) {
        l("onBind");
        binder = new IMyAidlInterface.Stub() {

            @Override
            public void register(ICallback callback) throws RemoteException {
                JService.this.callback = callback;
            }

            @Override
            public void unregister(ICallback callback) throws RemoteException {
                JService.this.callback = null;
            }

            @Override
            public Person getPerson(int id) throws RemoteException {
                l("getPerson: " + person);
                person.id = id;
                int bytesLen = 1 << 18;
                byte[] bytes = person.data = (new byte[bytesLen]);
                bytes[0] = 65;
                bytes[bytesLen - 1] = 66;
                sendMsg();
                return person;
            }

            @Override
            public void setName(String name) throws RemoteException {
                person.name = name;
                l("setName: " + person);
            }

            @Override
            public void pushMsg(long time) throws RemoteException {
                l("JService: " + time);
            }

            @Override
            public void pushData(byte[] data) throws RemoteException {
                l("JService 收到的数据： " + data.length);
            }
        };
        return binder;
    }

    //默认链表阻塞队列是 Integer.MAX_VALUE 的容量，当限制此队列容量时，就得为线程池加策略，防爆仓
    LinkedBlockingQueue<Runnable> workQueue = new LinkedBlockingQueue<>(10);
    ExecutorService epoll = new ThreadPoolExecutor(2, 5, 10, TimeUnit.MILLISECONDS, workQueue, new ThreadFactory() {
        @Override
        public Thread newThread(Runnable r) {
            return new Thread(r);
        }
    },
//            new ThreadPoolExecutor.AbortPolicy()//阻塞队列满时，再添加就抛  运行时异常，直接崩溃
//            new ThreadPoolExecutor.DiscardPolicy()//放弃新的任务
            new ThreadPoolExecutor.DiscardOldestPolicy()//放弃队头老任务
//            new ThreadPoolExecutor.CallerRunsPolicy()//由调用线程去执行此溢出任务
    );

    @Override
    public boolean onUnbind(Intent intent) {
        isRunning = false;
        return super.onUnbind(intent);
    }

    boolean isRunning = false;
    int count = 0;

    private void sendMsg() {
        if (!isRunning && binder != null) {
            isRunning = true;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    while (count++ < 50 && callback != null) {
                        SystemClock.sleep(100);
                        l("while thread pool : " + workQueue.size());
                        epoll.execute(() -> {
                            try {
                                long time = SystemClock.currentThreadTimeMillis();
                                l("update 1(" + time + ") ");
                                SystemClock.sleep(2000);
                                l("update 2(" + time + ") ");
                                if (callback != null) {
                                    callback.update(time);
                                }
                            } catch (RemoteException e) {
                                e.printStackTrace();
                            }
                        });
                    }
                    l("循环结束：" + count);
                    count = 0;
                    isRunning = false;
                }
            }).start();

        }
    }

}
