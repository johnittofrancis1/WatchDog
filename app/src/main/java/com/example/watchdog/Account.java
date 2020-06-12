package com.example.watchdog;

import android.net.Uri;

public class Account {
    static String name,gender,emailId,phoneno,password;
    static int age;
    static Uri photoUrl;
    static int flag;

    public static int getFlag() {
        return flag;
    }

    public static void setFlag(int flag) {
        Account.flag = flag;
    }

    public static String getName() {
        return name;
    }

    public static void setName(String name) {
        Account.name = name;
    }

    public static String getGender() {
        return gender;
    }

    public static void setGender(String gender) {
        Account.gender = gender;
    }

    public static String getEmailId() {
        return emailId;
    }

    public static void setEmailId(String emailId) {
        Account.emailId = emailId;
    }

    public static String getPhoneno() {
        return phoneno;
    }

    public static void setPhoneno(String phoneno) {
        Account.phoneno = phoneno;
    }

    public static String getPassword() {
        return password;
    }

    public static void setPassword(String password) {
        Account.password = password;
    }

    public static int getAge() {
        return age;
    }

    public static void setAge(int age) {
        Account.age = age;
    }

    public static Uri getPhotoUrl() {
        return photoUrl;
    }

    public static void setPhotoUrl(Uri photoUrl) {
        Account.photoUrl = photoUrl;
    }

    public static String accToString()
    {
        return "Name "+name+"Gender "+gender+"\nEmailId"+emailId+"PhoneNo "+phoneno+"\nPassword "+password+"Age "+age+"\nPhotoURL "+photoUrl;
    }
}
