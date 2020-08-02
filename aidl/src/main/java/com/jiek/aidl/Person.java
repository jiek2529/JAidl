package com.jiek.aidl;

import android.os.Parcel;
import android.os.Parcelable;

public class Person implements Parcelable {
    public int id;
    public String name;

    public byte[] data;
    public int age;

    public Person() {
    }


    protected Person(Parcel in) {
        id = in.readInt();
        name = in.readString();
        data = in.createByteArray();
        age = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeByteArray(data);
        dest.writeInt(age);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Person> CREATOR = new Creator<Person>() {
        @Override
        public Person createFromParcel(Parcel in) {
            return new Person(in);
        }

        @Override
        public Person[] newArray(int size) {
            return new Person[size];
        }
    };

    @Override
    public String toString() {
        return "Person{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", age=" + age +
                '}';
    }
}
