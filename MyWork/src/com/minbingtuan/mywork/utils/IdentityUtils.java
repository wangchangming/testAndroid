package com.minbingtuan.mywork.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Hashtable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.net.ParseException;

public class IdentityUtils {
	//���֤������֤��start
	 
	 /**  
	     * ���ܣ����֤����Ч��֤  
	     * @param IDStr ���֤��  
	     * @return ��Ч������"" ��Ч������String��Ϣ  
	     * @throws ParseException  
	 * @throws java.text.ParseException 
	 * @throws NumberFormatException 
	     */  
	    public  String IDCardValidate(String IDStr) throws ParseException {   
	        String errorInfo = "";// ��¼������Ϣ   
	        String[] ValCodeArr = { "1", "0", "X", "9", "8", "7", "6", "5", "4",   
	                "3", "2" };   
	        String[] Wi = { "7", "9", "10", "5", "8", "4", "2", "1", "6", "3", "7",   
	                "9", "10", "5", "8", "4", "2" };   
	        String Ai = "";   
	        // ================ ����ĳ��� 15λ��18λ ================   
	        if (IDStr.length() != 15 && IDStr.length() != 18) {   
	            errorInfo = "���֤���볤��Ӧ��Ϊ15λ��18λ��";   
	            return errorInfo;   
	        }   
	        // =======================(end)========================   
	  
	        // ================ ���� �������Ϊ��Ϊ���� ================   
	        if (IDStr.length() == 18) {   
	            Ai = IDStr.substring(0, 17);   
	        } else if (IDStr.length() == 15) {   
	            Ai = IDStr.substring(0, 6) + "19" + IDStr.substring(6, 15);   
	        }   
	        if (isNumeric(Ai) == false) {   
	            errorInfo = "���֤15λ���붼ӦΪ���� ; 18λ��������һλ�⣬��ӦΪ���֡�";   
	            return errorInfo;   
	        }   
	        // =======================(end)========================   
	  
	        // ================ ���������Ƿ���Ч ================   
	        String strYear = Ai.substring(6, 10);// ���   
	        String strMonth = Ai.substring(10, 12);// �·�   
	        String strDay = Ai.substring(12, 14);// �·�   
	        if (isDataFormat(strYear + "-" + strMonth + "-" + strDay) == false) {   
	            errorInfo = "���֤������Ч��";   
	            return errorInfo;   
	        }   
	        GregorianCalendar gc = new GregorianCalendar();   
	        SimpleDateFormat s = new SimpleDateFormat("yyyy-MM-dd");   
	        try {
				if ((gc.get(Calendar.YEAR) - Integer.parseInt(strYear)) > 150  
				        || (gc.getTime().getTime() - s.parse( strYear + "-" + strMonth + "-" + strDay).getTime()) < 0) {   
				    errorInfo = "���֤���ղ�����Ч��Χ��";   
				    return errorInfo;   
				}
			} catch (NumberFormatException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (java.text.ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}   
	        if (Integer.parseInt(strMonth) > 12 || Integer.parseInt(strMonth) == 0) {   
	            errorInfo = "���֤�·���Ч";   
	            return errorInfo;   
	        }   
	        if (Integer.parseInt(strDay) > 31 || Integer.parseInt(strDay) == 0) {   
	            errorInfo = "���֤������Ч";   
	            return errorInfo;   
	        }   
	        // =====================(end)=====================   
	  
	        // ================ ������ʱ����Ч ================   
	        Hashtable h = GetAreaCode();   
	        if (h.get(Ai.substring(0, 2)) == null) {   
	            errorInfo = "���֤�����������";   
	            return errorInfo;   
	        }   
	        // ==============================================   
	  
	        // ================ �ж����һλ��ֵ ================   
	        int TotalmulAiWi = 0;   
	        for (int i = 0; i < 17; i++) {   
	            TotalmulAiWi = TotalmulAiWi   
	                    + Integer.parseInt(String.valueOf(Ai.charAt(i)))   
	                    * Integer.parseInt(Wi[i]);   
	        }   
	        int modValue = TotalmulAiWi % 11;   
	        String strVerifyCode = ValCodeArr[modValue];   
	        Ai = Ai + strVerifyCode;   
	  
	        if (IDStr.length() == 18) {   
	             if (Ai.equals(IDStr) == false) {   
	                 errorInfo = "���֤��Ч�����ǺϷ������֤����";   
	                 return errorInfo;   
	             }   
	         } else {   
	             return "";   
	         }   
	         // =====================(end)=====================   
	         return "";   
	     }
	    	     
	     /**  
	      * ���ܣ����õ�������  
	      * @return Hashtable ����  
	      */  
	     private static Hashtable GetAreaCode() {   
	         Hashtable hashtable = new Hashtable();   
	         hashtable.put("11", "����");   
	         hashtable.put("12", "���");   
	         hashtable.put("13", "�ӱ�");   
	         hashtable.put("14", "ɽ��");   
	         hashtable.put("15", "���ɹ�");   
	         hashtable.put("21", "����");   
	         hashtable.put("22", "����");   
	         hashtable.put("23", "������");   
	         hashtable.put("31", "�Ϻ�");   
	         hashtable.put("32", "����");   
	         hashtable.put("33", "�㽭");   
	         hashtable.put("34", "����");   
	         hashtable.put("35", "����");   
	         hashtable.put("36", "����");   
	         hashtable.put("37", "ɽ��");   
	         hashtable.put("41", "����");   
	         hashtable.put("42", "����");   
	         hashtable.put("43", "����");   
	         hashtable.put("44", "�㶫");   
	         hashtable.put("45", "����");   
	         hashtable.put("46", "����");   
	         hashtable.put("50", "����");   
	         hashtable.put("51", "�Ĵ�");   
	         hashtable.put("52", "����");   
	         hashtable.put("53", "����");   
	         hashtable.put("54", "����");   
	         hashtable.put("61", "����");   
	         hashtable.put("62", "����");   
	         hashtable.put("63", "�ຣ");   
	         hashtable.put("64", "����");   
	         hashtable.put("65", "�½�");   
	         hashtable.put("71", "̨��");   
	         hashtable.put("81", "���");   
	         hashtable.put("82", "����");   
	         hashtable.put("91", "����");   
	         return hashtable;   
	     }   

	/**��֤�����ַ����Ƿ���YYYY-MM-DD��ʽ
	  * @param str
	  * @return
	  */
	 public boolean isDataFormat(String str){
	  boolean flag=false;
	  //String regxStr="[1-9][0-9]{3}-[0-1][0-2]-((0[1-9])|([12][0-9])|(3[01]))";
	  String regxStr="^((\\d{2}(([02468][048])|([13579][26]))[\\-\\/\\s]?((((0?[13578])|(1[02]))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])))))|(\\d{2}(([02468][1235679])|([13579][01345789]))[\\-\\/\\s]?((((0?[13578])|(1[02]))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\-\\/\\s]?((0?[1-9])|(1[0-9])|(2[0-8]))))))(\\s(((0?[0-9])|([1-2][0-3]))\\:([0-5]?[0-9])((\\s)|(\\:([0-5]?[0-9])))))?$";
	  Pattern pattern1=Pattern.compile(regxStr);
	  Matcher isNo=pattern1.matcher(str);
	  if(isNo.matches()){
	   flag=true;
	  }
	  return flag;
	 }

	 /**  
	      * ���ܣ��ж��ַ����Ƿ�Ϊ����  
	      * @param str  
	      * @return  
	      */  
	     private static boolean isNumeric(String str) {   
	         Pattern pattern = Pattern.compile("[0-9]*");   
	         Matcher isNum = pattern.matcher(str);   
	         if (isNum.matches()) {   
	             return true;   
	         } else {   
	             return false;   
	         }   
	     }
	 //���֤������֤��end

	     /**
	      * ��֤�ֻ���Ϣ
	      * @param mobiles
	      * @return
	      */
	     public static boolean isMobileNumber(String mobiles){
	    		Pattern p = Pattern.compile("^((13[0-9])|(15[^4,\\D])|(18[0-9]))\\d{8}$");
	    		Matcher m = p.matcher(mobiles);
	    		return m.matches();
	    		}
	     
}
