package com.minbingtuan.mywork.utils;

import java.io.IOException;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.PostMethod;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.minbingtuan.httputil.StringUtil;
import com.minbingtuan.mywork.Constants;

import android.os.Environment;
/**
 * 
 * 
 */
public class Tools {
	/**
	 * 检查是否存在SDCard
	 * @return
	 */
	public static boolean hasSdcard(){
		String state = Environment.getExternalStorageState();
		if(state.equals(Environment.MEDIA_MOUNTED)){
			return true;
		}else{
			return false;
		}

	}
	
	/**
	 * 发送http请求，申请短信验证
	 * @param str 手机号码
	 * @param mobile_code 短信验证码
	 * @return code 返回码 ：2表示成功
	 */
	public static String sendSms(String str,String mobile_code){
		
		String code = "";
		
		//创建http请求
		HttpClient client = new HttpClient();
		//使用post方法
		PostMethod post = new PostMethod(Constants.SMSURL);
		
		//设置请求编码为utf-8
		client.getParams().setContentCharset("UTF-8");
		//设置请求头信息
		post.setRequestHeader("ContentType","application/x-www-form-urlencoded;charset=UTF-8");
		
		//返回手机端的内容
		String content = new String("您的验证码是：" + mobile_code + "。请不要把验证码泄露给其他人。");
		
		//创建数组
		NameValuePair[] data = {//提交短信
			    new NameValuePair("account", "cf_wching"), 
			    //new NameValuePair("password", "密码"), //密码可以使用明文密码或使用32位MD5加密
			    new NameValuePair("password", StringUtil.MD5Encode("w821649975")),
			    new NameValuePair("mobile", str), 
			    new NameValuePair("content", content),
		};
		
		//设置请求内容
		post.setRequestBody(data);
		
		try {
			//执行http请求
			client.executeMethod(post);
			
			//获取返回值
			String SubmitResult =post.getResponseBodyAsString();
			
			//解析返回值
			Document doc = DocumentHelper.parseText(SubmitResult); 
			Element root = doc.getRootElement();
			
			code = root.elementText("code");
			
		} catch (HttpException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch(DocumentException e){
			e.printStackTrace();
		}
		
		return code;
	}
}
