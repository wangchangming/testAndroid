
package com.minbingtuan.mywork.activity;

import java.util.HashMap;

import org.json.JSONObject;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.minbingtuan.mywork.MyApplication;
import com.minbingtuan.mywork.R;
import com.minbingtuan.mywork.service.UpdateManager;
import com.minbingtuan.mywork.service.UploadService;
import com.minbingtuan.mywork.utils.LogHelper;
import com.minbingtuan.mywork.utils.SDCardUtil;
import com.minbingtuan.mywork.utils.StringUtils;
import com.minbingtuan.mywork.utils.Tools;
import com.minbingtuan.mywork.utils.VolleyErrorHelper;
import com.minbingtuan.mywork.view.CustomProgress;
import com.minbingtuan.mywork.view.NetDialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

public class MyLoginActivity extends Activity implements OnClickListener {

    private Button buttonLogin = null;

    private Button buttonRegister = null;

    private CheckBox checkBox = null;

    private EditText mEditTextUserName = null;

    private EditText mEditTextUserPassWord = null;

    private String mUserName;

    private String mPassWord;

    private MyApplication myApp;

    private CustomProgress dialog;

    SharedPreferences shared;

    boolean isFirstLogin;
    
    private BroadcastReceiver receiver = new BroadcastReceiver(){

        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction()== "com.minbingtuan.mywork.uploadApp"){
                LogHelper.trace("广播处理....");
                Tools.installApk(context);
            }
        }};

    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_userlogin);

        myApp = (MyApplication)getApplication();
        registerBroast(this);//注册广播
        
        
//        this.startService(new Intent(this,UploadService.class));

        // 判断手机是否连接网络
        if (!myApp.isConnect()) {// 如果没有连接网络
            Dialog dialog = new NetDialog(this, R.style.MyDialog);
            dialog.show();
        }

        // 打印手机串号
        LogHelper.trace(SDCardUtil.getMIEI(getWindow().getContext()));

        // 这里判断用户的登录的状态
        if (MyApplication.getLoginStatus()) {
            Intent intent = new Intent();
            intent.setClass(MyLoginActivity.this, MyWorkActivity.class);
            startActivity(intent);
            finish();
        }

        // 这里判断用户是否首次登录，根据情况作出相应的处理
        init();
    }

    /**
     * 注册广播
     * @param context
     */
    private void registerBroast(Context context){
        IntentFilter filter = new IntentFilter();
        filter.addAction("com.minbingtuan.mywork.uploadApp");
        context.registerReceiver(receiver, filter);
    }
    
    @Override
    protected void onDestroy() {
        unregisterReceiver(receiver);
        this.stopService(new Intent(this,UploadService.class));
        super.onDestroy();
    }

    // 关于自动登录的问题
    public void init() {
        // 取出存储器
        shared = getSharedPreferences("userInfo", Activity.MODE_WORLD_WRITEABLE);

        // 获取布局信息
        mEditTextUserName = (EditText)findViewById(R.id.EditTextUserName);
        mEditTextUserPassWord = (EditText)findViewById(R.id.EditTextUserPassWord);
        buttonLogin = (Button)findViewById(R.id.buttonLogin);
        buttonRegister = (Button)findViewById(R.id.buttonRegister);
        checkBox = (CheckBox)findViewById(R.id.check);
        buttonLogin.setOnClickListener(this);
        buttonRegister.setOnClickListener(this);
        findViewById(R.id.buttonforget).setOnClickListener(this);

        isFirstLogin = shared.getBoolean("isFirstLogin", false);
        if (isFirstLogin) {// 如果是非首次进入应用
            // 取出上次登录用户的个人信息
            String uName = shared.getString("uName", "");
            String uPassword = shared.getString("uPwd", "");
            Boolean namePwd = shared.getBoolean("namePwd", false);
            if (!"".equals(uName) && !"".equals(uPassword) && namePwd) {// 如果用户名和密码存在
                // 直接跳转到主界面
                myApp.startGPSService();
                myApp.setLoginStatus(true);
                startActivity(new Intent(this, MyWorkActivity.class));
                finish();
            } else if (!"".equals(uName) && !"".equals(uPassword) && !namePwd) {
                mEditTextUserName.setText(uName);
                mEditTextUserPassWord.setText(uPassword);
            } else {// 如果上次登录时点击了退出程序
                Toast.makeText(this, getString(R.string.input_name_pwd), Toast.LENGTH_SHORT).show();
            }
        } else {// 如果是首次进入应用
            Toast.makeText(this, getString(R.string.welcome), Toast.LENGTH_LONG).show();
            Editor edit = shared.edit();
            edit.putBoolean("isFirstLogin", true);// 表示非首次登录
            edit.commit();
        }
    }

    public void HttpGetRequestLogin(String mUserName, String mPassWord) {
        RequestQueue queue = Volley.newRequestQueue(this);
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("userName", mUserName);
        params.put("userPwd", mPassWord);
        String url = MyApplication.LocalLoginUrl;
        url += StringUtils.encodeUrl(params);

        // 调试输出
        LogHelper.trace(url);

        JsonObjectRequest jsObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        int status = response.optInt("status");
                        if (status == 0) {
                            JSONObject jobj = response.optJSONObject("userInfo");
                            int id = jobj.optInt("id");
                            int groupId = jobj.optInt("groupId");
                            String userName = jobj.optString("userName");
                            String realName = jobj.optString("realName");
                            String pwd = jobj.optString("userPwd");
                            String mobile = jobj.optString("mobile");
                            String email = jobj.optString("email");
                            String birthday = jobj.optString("birthday");
                            String groupName = jobj.optString("groupName");
                            
                            myApp.setUserInfo(id, userName, realName, mobile, email, groupId, pwd,
                                    birthday, groupName);
                            myApp.startGPSService();
                            myApp.setLoginStatus(true);
                            Editor edit = shared.edit();
                            edit.putString("uName", userName);
                            
                            SDCardUtil.saveFile(userName);

                            if (checkBox.isChecked()) {
                                // 如果选择了自动登录，则把个人信息保存在本地
                                edit.putInt("uId", id);
                                edit.putInt("uGroupId", groupId);
                                edit.putString("uRealName", realName);
                                edit.putString("uPwd", pwd);
                                edit.putString("uMobile", mobile);
                                edit.putString("uEmail", email);
                                edit.putString("uBirthday", birthday);
                                edit.putString("uGroupName", groupName);
                                edit.putBoolean("autoLogin", true);
                                edit.putBoolean("namePwd", true);

                                StringUtils.userName = userName;
                                StringUtils.password = pwd;

                            }
                            edit.commit();

                            Toast.makeText(getApplicationContext(),
                                    getString(R.string.Login_successful), Toast.LENGTH_SHORT)
                                    .show();
                            Intent intent = new Intent();
                            intent.setClass(MyLoginActivity.this, MyWorkActivity.class);
                            startActivity(intent);
                            finish();
                        }
                        if (status == -1) {
                            Toast.makeText(getApplicationContext(),
                                    getString(R.string.PassWordError), Toast.LENGTH_SHORT).show();
                        }
                        if (status == -2) {
                            Toast.makeText(getApplicationContext(),
                                    getString(R.string.The_user_is_not_registered),
                                    Toast.LENGTH_SHORT).show();
                        }

                        // 关闭对话框
                        if (dialog != null) {
                            dialog.dismiss();
                        }
                    }

                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {

                        Toast.makeText(getApplicationContext(),
                                VolleyErrorHelper.handleServerError(error, getApplication()),
                                Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }

                });
        queue.add(jsObjectRequest);
    }

    /**
     * @author wching
     */
    static class Utils {
        public static long lastClickTime;

        public static boolean isFastDoubleClick() {
            long time = System.currentTimeMillis();
            if (time - lastClickTime < 2000) {
                return true;
            }
            lastClickTime = time;
            return false;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.buttonLogin:// 如果点击登录按钮

                // 判断手机是否连接网络
                if (!myApp.isConnect()) {// 如果没有连接网络
                    Dialog dialog = new NetDialog(this, R.style.MyDialog);
                    dialog.show();
                } else {// 在有网络的情况下点击提交
                    mUserName = mEditTextUserName.getText().toString();
                    mPassWord = mEditTextUserPassWord.getText().toString();
                    if (TextUtils.isEmpty(mUserName) || TextUtils.isEmpty(mPassWord)) {
                        Toast.makeText(getApplicationContext(),
                                getString(R.string.Please_enter_the_UserName_to_log_on),
                                Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (Utils.isFastDoubleClick()) {
                        return;
                    }
                    if (!myApp.isConnect()) {
                        Toast.makeText(getApplicationContext(), getString(R.string.NetError),
                                Toast.LENGTH_SHORT).show();
                        return;
                    }
//                    //判断用户是否是手机当前手机绑定的用户
//                    String name = shared.getString("uName", "");
//                    if(!TextUtils.isEmpty(name)&&!name.equals(mUserName)){
//                    	LogHelper.toast(this, getString(R.string.is_me));
//                    	return;
//                    }
                    
                    if(SDCardUtil.getFile()!=null&&!mUserName.equals(SDCardUtil.getFile())){
                    	LogHelper.toast(this, getString(R.string.is_me));
                    	return;
                    }

                    HttpGetRequestLogin(mUserName, mPassWord);
                    dialog = CustomProgress.show(MyLoginActivity.this,
                            getString(R.string.is_logining), true, null);
                }
                break;

            case R.id.buttonRegister:// 如果点击注册按钮
                Intent intent = new Intent();
                intent.setClass(MyLoginActivity.this, MyRegisterActivity.class);
                startActivity(intent);
                finish();
                break;
            case R.id.buttonforget:
                startActivity(new Intent(this, BackPwdActivity.class));
                break;

            default:
                break;
        }
    }
    
}
