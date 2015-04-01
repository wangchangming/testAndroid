
package com.minbingtuan.mywork;

import java.util.Timer;
import java.util.TimerTask;

import com.minbingtuan.mywork.utils.Setting;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class MyApplication extends Application {

    public static String LOCALHOST = "http://www.minbingtuan.cn:8088/api/";

    public static String LOGIN = "function.php?action=login&";

    public static String REGISTER = "function.php?action=register&";

    public static String GROUP = "function.php?action=group";

    public static String ADD_POSITION = "position.php?action=add&";

    public static String GET_POSITION = "position.php?action=get&type=1&";

    public static String SETTING_NAME = "manager.php?action=name&";// name=whui&managerId=

    public static String SETTING_MOBILE = "manager.php?action=mobile&";// mobile=1432

    public static String SETTING_BIRTHDAY = "manager.php?action=birthday&";// birthday=1980-01-19

    public static String SETTING_EMAIL = "manager.php?action=email&";// email=222@163.com

    public static String SETTING_PWD = "manager.php?action=pwd&";// pwd=addd

    public static String SEARCHRECORD = "position.php?action=search&";

    public static String LocalLoginUrl = LOCALHOST + LOGIN;

    public static String LocalRegisterUrl = LOCALHOST + REGISTER;

    public static String LocalReportUrl = LOCALHOST + ADD_POSITION;

    public static String LocalGroupUrl = LOCALHOST + GROUP;

    public static String LocalTIMEUrl = LOCALHOST + ADD_POSITION;

    public static String LocalGETTIMEUrl = LOCALHOST + GET_POSITION;

    public static String localSETTINGNAME = LOCALHOST + SETTING_NAME;

    public static String localSETTINGMOBILE = LOCALHOST + SETTING_MOBILE;

    public static String localSETTINGBIRTHDAY = LOCALHOST + SETTING_BIRTHDAY;

    public static String localSETTINGEMAIL = LOCALHOST + SETTING_EMAIL;

    public static String localSETTINGPWD = LOCALHOST + SETTING_PWD;

    public static String localSEARCHREAORD = LOCALHOST + SEARCHRECORD;

    private userInfo myUserInfo = new userInfo();

    private static boolean isLogin = false;

    private static boolean isPosition = false;

    public void onCreate() {
        super.onCreate();
    }

    /**
     * 判断网络是否连接
     * 
     * @return boolean
     */
    public boolean isConnect() {
        ConnectivityManager connectivity = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo info = connectivity.getActiveNetworkInfo();
            if (info != null && info.isConnected()) {
                if (info.getState() == NetworkInfo.State.CONNECTED) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 启动GPS服务
     */
    public void startGPSService() {
        Intent gpsIntent = new Intent();
        gpsIntent.setClassName(MyApplication.this,
                "com.minbingtuan.mywork.service.MyAMapGpsService");
        startService(gpsIntent);
    }

    /**
     * 关闭GPS服务
     */
    public void stopGPSService() {
        Intent gpsIntent = new Intent();
        gpsIntent.setClassName(MyApplication.this,
                "com.minbingtuan.mywork.service.MyAMapGpsService");
        stopService(gpsIntent);
    }

    /**
     * 启动位置服务
     */
    public void startPositonService() {
        if (isPosition) {
            return;
        }
        isPosition = true;
        Intent posIntent = new Intent();
        posIntent.setClassName(MyApplication.this,
                "com.minbingtuan.mywork.service.MyPositonService");
        startService(posIntent);
    }

    /**
     * 关闭位置服务
     */
    public void stopPositonService() {
        if (!isPosition) {
            return;
        }
        isPosition = false;
        Intent posIntent = new Intent();
        posIntent.setClassName(MyApplication.this,
                "com.minbingtuan.mywork.service.MyPositonService");
        stopService(posIntent);
    }

    /**
     * 设置用户信息
     * 
     * @param id
     * @param userName
     * @param realName
     * @param mobile
     * @param email
     * @param groupId
     * @param password
     * @param birthday
     * @param groupName
     */
    public void setUserInfo(int id, String userName, String realName, String mobile, String email,
            int groupId, String password, String birthday, String groupName) {
        myUserInfo.id = id;
        myUserInfo.userName = userName;
        myUserInfo.realName = realName;
        myUserInfo.password = password;
        myUserInfo.mobile = mobile;
        myUserInfo.email = email;
        myUserInfo.groupId = groupId;
        myUserInfo.birthday = birthday;
        myUserInfo.groupName = groupName;

    }

    public int getUserId() {
        return myUserInfo.id;
    }

    public String getUserName() {
        return myUserInfo.userName;
    }

    public String getRealName() {
        return myUserInfo.realName;
    }

    public void setRealName(String realName) {
        myUserInfo.realName = realName;
    }

    public String getUserMobile() {
        return myUserInfo.mobile;
    }

    public void setUserMobile(String mobile) {
        myUserInfo.mobile = mobile;
    }

    public String getUserBirthday() {
        return myUserInfo.birthday;
    }

    public void setUserBirthday(String birthday) {
        myUserInfo.birthday = birthday;
    }

    public String getUserEmail() {
        return myUserInfo.email;
    }

    public void setUserEmail(String email) {
        myUserInfo.email = email;
    }

    public String getUserGroupName() {
        return myUserInfo.groupName;
    }

    public void setUserGroupName(String groupName) {
        myUserInfo.groupName = groupName;
    }

    public String getUserPassWord() {
        return myUserInfo.password;
    }

    public void setUserPassWord(String password) {
        myUserInfo.password = password;
    }

    public void setLoginStatus(boolean status) {
        isLogin = status;
    }

    public static boolean getLoginStatus() {
        return isLogin;
    }

    class userInfo {
        int id;

        String userName;

        String realName;

        String password;

        String mobile;

        String email;

        String pwd;

        String birthday;

        int groupId;

        String groupName;

        String sex;

        String showCustomMobile;

        String showMemberMobile;

        String idNumber;

        String status;
    }

    private gpsInfo myGpsInfo = new gpsInfo();

    /**
     * 设置地理位置信息
     * 
     * @param addr 地址信息
     * @param lon 经度
     * @param lat 纬度
     */
    public void setGpsInfo(String addr, double lon, double lat) {
        myGpsInfo.address = addr;
        myGpsInfo.longitude = lon;// 经度
        myGpsInfo.latitude = lat;// 纬度
    }

    public String getGpsAddr() {
        return myGpsInfo.address;
    }

    public double getGpsLongitude() {
        return myGpsInfo.longitude;
    }

    public double getGpsLatitude() {
        return myGpsInfo.latitude;
    }

    class gpsInfo {
        String address;

        double longitude;

        double latitude;
    }

    public static void exitMe() {
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                exit();
            }
        }, 1000);
    }

    private static void exit() {
        // 杀进程
        int pid = android.os.Process.myPid();
        android.os.Process.killProcess(pid);
        System.exit(0);
    }

}
