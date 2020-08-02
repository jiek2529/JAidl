package com.jiek.jaidl;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.jiek.aidl.ICallback;
import com.jiek.aidl.IMyAidlInterface;

public class MainActivity extends AppCompatActivity {

    IMyAidlInterface binder;
    private ICallback callback = new ICallback.Stub() {
        @Override
        public void update(long time) throws RemoteException {
            Log.e(TAG, "update: Sevice -> Activity : " + time);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    MyServiceConnection conn = new MyServiceConnection();

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(conn);
    }

    private static final String TAG = "MainActivity";

    public void bind(View view) {
        Log.e(TAG, "bind: ");
        Intent intent = new Intent();
        intent.setAction("jservice_1");
        intent.setPackage("com.jiek.myserver");
        bindService(intent, conn, BIND_AUTO_CREATE);

//        startService(intent);
    }

    public void getPerson(View view) {
        try {
//            System.out.println(binder.getPerson(12));

            int len = 1 << 19;
            byte[] bytes = new byte[len];
            bytes[0] = 67;
            bytes[len - 1] = 68;
            binder.pushData(bytes);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private class MyServiceConnection implements ServiceConnection {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.e(TAG, "onServiceConnected: ");
            binder = IMyAidlInterface.Stub.asInterface(service);
            try {
                binder.register(callback);

                binder.setName("jiek");
                binder.getPerson(123);
//                binder.pushData(new byte[1<<20]);

//                {//给绑定的 service 再绑定一个信使
//                    messenger = new Messenger(service);
//                    Message message = new Message();
//                    message.what = 1;
//
////                Activity 绑定 Service的时候给Service 发送一个消息,该消息的obj属性是一个 Messenger对象message.obj = mOutMessenger;
//
//                    try {
//                        messenger.send(message);
//                    } catch (RemoteException e) {
//                        e.printStackTrace();
//                    }
//                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.e(TAG, "onServiceDisconnected: " + name.getClassName() + " > " + name + "  " + binder);
            try {
                if (binder != null && callback != null) {
                    binder.unregister(callback);
                }
                binder = null;
                callback = null;
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    private Messenger messenger;
    //将该 Handler 发送Service
    private Messenger mOutMessenger = new Messenger(new OutgoingHandler());

    private class OutgoingHandler extends Handler {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
        }
    }
}
