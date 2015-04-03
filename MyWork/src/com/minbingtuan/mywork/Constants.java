
package com.minbingtuan.mywork;

public class Constants {

    // 固定网址
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

    // SD卡文件
    public static String USERINFOPATH = "userInfo.txt";

    public static String USERINFOISFIRSTLOGIN = "firstLogin.txt";

    // 拼接用于调用接口的网址
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
    
    //请求签到信息接口
    public static String USERSIGNINFO = "http://www.minbingtuan.cn:8088/api/result.php?";
    
    //请求签到信息接口
    public static String ISPHONE = "http://192.168.1.117/attendance_system/api/mobile.php?";

    // 短信接口请求网址
    public static String SMSURL = "http://106.ihuyi.cn/webservice/sms.php?method=Submit";

    // 服务器下载app地址
    public static final String apkUrl = "http://softfile.3g.qq.com:8080/msoft/179/24659/43549/qq_hd_mini_1.4.apk";

    // 安装包存在手机上的路径
    public static final String savePath = "/sdcard/updatedemo/";

    // 安装包的名称
    public static final String saveFileName = savePath + "minbingtuan.apk";
}
