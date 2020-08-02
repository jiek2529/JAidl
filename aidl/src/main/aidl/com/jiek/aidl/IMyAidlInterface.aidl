// IMyAidlInterface.aidl
package com.jiek.aidl;

import com.jiek.aidl.Person;
import com.jiek.aidl.ICallback;

// Declare any non-default types here with import statements

interface IMyAidlInterface {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
//    void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat,
//            double aDouble, String aString);

    void register(ICallback callback);
    void unregister(ICallback callback);

    Person getPerson(int id);
    void setName(String name);
    void pushMsg(long time);
    void pushData(in byte[] data);
}
