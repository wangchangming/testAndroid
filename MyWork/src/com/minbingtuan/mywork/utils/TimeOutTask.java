
package com.minbingtuan.mywork.utils;

import com.minbingtuan.mywork.R;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Button;

/**
 * 用于点击获取验证码按钮的更新按钮的UI界面， 同时还判断获取验证码的有效时间
 *
 * @author
 */
public class TimeOutTask extends AsyncTask<Integer, Integer, String> {

    private Context context;

    private Button button;

    private int timeout;

    // 此方法用于返回当前时间剩余数
    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    /**
     * @param context
     * @param button 需要更新的button
     * @param timeout 获取的验证码剩余时间
     */
    public TimeOutTask(Context context, Button button, int timeout) {
        this.context = context;
        this.button = button;
        this.timeout = timeout;
    }

    @Override
    protected void onPostExecute(String result) {
        button.setBackgroundResource(R.color.loginbackground1);
        button.setText(result);
        button.setEnabled(true);
    }

    @Override
    protected void onPreExecute() {
        button.setBackgroundResource(R.color.gray);
        button.setEnabled(false);
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        button.setText(String.valueOf(values[0]) + context.getString(R.string.time_re_get));
    }

    @Override
    protected String doInBackground(Integer... params) {
        for (int i = timeout; i > 0; i--) {
            timeout = i;
            this.publishProgress(i);
            try {
                Thread.sleep(params[0]);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        timeout = 0;
        return context.getString(R.string.getsmscode);
    }

}
