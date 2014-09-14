/*
 * 创建日期 2004-6-24
 *
 * 更改所生成文件模板为
 * 窗口 > 首选项 > Java > 代码生成 > 代码和注释
 */
package com.poweruniverse.nim.esb.utils;
import java.security.Key;
import java.security.Security;

import javax.crypto.Cipher;

/**
 * @author Administrator
 *
 * 更改所生成类型注释的模板为
 * 窗口 > 首选项 > Java > 代码生成 > 代码和注释
 */
public class Encrypt {
	public static String encrypt( String name,String passwd)
	{
		if (name.length() < 3 ||  passwd.length() < 3){
		  return "-1";
		}

		if (name.length() > 30 || passwd.length() > 30){
		  return "-2";
		}

		String l_answer,as_name,as_passwd;
		String l_char;
		char[] charArray;
		int sigleCharIntValue;
		int l_shift,l_position,l_offset,l_root;
		//处理name
		as_name = name.toUpperCase();
		charArray = as_name.toCharArray();
		for (int i = 0;i<charArray.length;i++){
				sigleCharIntValue =(int)charArray[i];
				if (sigleCharIntValue < 65 || sigleCharIntValue > 90){
					sigleCharIntValue = 65 + (sigleCharIntValue - (sigleCharIntValue /24) * 24);
				}
			charArray[i] = (char)sigleCharIntValue;
		}
		as_name = String.valueOf(charArray);
		//处理password
		as_passwd = passwd.toUpperCase();
		charArray = as_passwd.toCharArray();
		for (int i = 0;i<charArray.length;i++){
				sigleCharIntValue =(int)charArray[i];
				if (sigleCharIntValue < 65 || sigleCharIntValue > 90){
					sigleCharIntValue = 65 + (sigleCharIntValue - (sigleCharIntValue /24) * 24);
				}
			charArray[i] = (char)sigleCharIntValue;
		}
		as_passwd = String.valueOf(charArray);
		l_offset = (int)as_name.charAt(1);
		l_root   = (int)as_passwd.charAt(as_passwd.length()-1);
		l_shift  = (int)as_name.charAt(as_name.length()-1);
		l_shift  = (l_shift - (l_shift /13) * 13);
		l_answer = as_name + as_passwd;

		l_position = 1;

		for (int i = 1;i<=30;i++){
		  if (l_answer.length() >= 30){
			  break;
		  }

		  l_shift = l_shift + l_offset + i;
		  if ( l_shift > 90){
			  l_shift = (l_shift - (l_shift /24) * 24);
			  l_shift = l_shift + 65;
		  }

		  l_char = String.valueOf((char)l_shift);

		  if (l_position == 1){
			  l_answer = l_answer + l_char ;
			  l_position = 0;
		  }else{
			  l_answer = l_char + l_answer;
			  l_position = 1;
		  }
		}
		charArray = l_answer.toCharArray();
		for (int i = 0;i<30;i++){
				sigleCharIntValue =(int)charArray[i];
				sigleCharIntValue = sigleCharIntValue + l_root + i+1;
				if (sigleCharIntValue > 90){
					sigleCharIntValue = 65 + (sigleCharIntValue - (sigleCharIntValue /24) * 24);
				}
			charArray[i] = (char)sigleCharIntValue;
		}
		l_answer = String.valueOf(charArray);

//		System.out.println("	encrypted:"+l_answer);
		return l_answer;
	}



	  
	  /** 字符串默认键值     */
//	  private static String strDefaultKey = "national";
//
//	  /** 加密工具     */
//	  private Cipher encryptCipher = null;
//
//	  /** 解密工具     */
//	  private Cipher decryptCipher = null;

	  /**  
	   * 将byte数组转换为表示16进制值的字符串， 如：byte[]{8,18}转换为：0813， 和public static byte[]  
	   * hexStr2ByteArr(String strIn) 互为可逆的转换过程  
	   *   
	   * @param arrB  
	   *            需要转换的byte数组  
	   * @return 转换后的字符串  
	   * @throws Exception  
	   *             本方法不处理任何异常，所有异常全部抛出  
	   */
	  public static String byteArr2HexStr(byte[] arrB) throws Exception {
	    int iLen = arrB.length;
	    // 每个byte用两个字符才能表示，所以字符串的长度是数组长度的两倍   
	    StringBuffer sb = new StringBuffer(iLen * 2);
	    for (int i = 0; i < iLen; i++) {
	      int intTmp = arrB[i];
	      // 把负数转换为正数   
	      while (intTmp < 0) {
	        intTmp = intTmp + 256;
	      }
	      // 小于0F的数需要在前面补0   
	      if (intTmp < 16) {
	        sb.append("0");
	      }
	      sb.append(Integer.toString(intTmp, 16));
	    }
	    return sb.toString();
	  }

	  /**  
	   * 将表示16进制值的字符串转换为byte数组， 和public static String byteArr2HexStr(byte[] arrB)  
	   * 互为可逆的转换过程  
	   *   
	   * @param strIn  
	   *            需要转换的字符串  
	   * @return 转换后的byte数组  
	   * @throws Exception  
	   *             本方法不处理任何异常，所有异常全部抛出  
	   * @author <a href="mailto:leo841001@163.com">LiGuoQing</a>  
	   */
	  public static byte[] hexStr2ByteArr(String strIn) throws Exception {
	    byte[] arrB = strIn.getBytes();
	    int iLen = arrB.length;

	    // 两个字符表示一个字节，所以字节数组长度是字符串长度除以2   
	    byte[] arrOut = new byte[iLen / 2];
	    for (int i = 0; i < iLen; i = i + 2) {
	      String strTmp = new String(arrB, i, 2);
	      arrOut[i / 2] = (byte) Integer.parseInt(strTmp, 16);
	    }
	    return arrOut;
	  }


	  /**  
	   * 指定密钥构造方法  
	   *   
	   * @param strKey  
	   *            指定的密钥  
	   * @throws Exception  
	   */
//	  public DesUtils(String strKey) throws Exception {
//	    Security.addProvider(new com.sun.crypto.provider.SunJCE());
//	    Key key = getKey(strKey.getBytes());
//
//	    encryptCipher = Cipher.getInstance("DES");
//	    encryptCipher.init(Cipher.ENCRYPT_MODE, key);
//
//	    decryptCipher = Cipher.getInstance("DES");
//	    decryptCipher.init(Cipher.DECRYPT_MODE, key);
//	  }

	  /**  
	   * 加密字节数组  
	   *   
	   * @param arrB  
	   *            需加密的字节数组  
	   * @return 加密后的字节数组  
	   * @throws Exception  
	   */
	  @SuppressWarnings("restriction")
	  public static byte[] desEncrypt(byte[] arrB,String strKey) throws Exception {
		  
		Security.addProvider(new com.sun.crypto.provider.SunJCE());
		Key key = getKey(strKey.getBytes());

		Cipher encryptCipher = Cipher.getInstance("DES");
		encryptCipher.init(Cipher.ENCRYPT_MODE, key);
		    
	    return encryptCipher.doFinal(arrB);
	  }

	  /**  
	   * 加密字符串  
	   *   
	   * @param strIn  
	   *            需加密的字符串  
	   * @return 加密后的字符串  
	   * @throws Exception  
	   */
	  public static String desEncrypt(String strIn,String key) throws Exception {
	    return byteArr2HexStr(desEncrypt(strIn.getBytes(),key));
	  }

	  /**  
	   * 解密字节数组  
	   *   
	   * @param arrB  
	   *            需解密的字节数组  
	   * @return 解密后的字节数组  
	   * @throws Exception  
	   */
	  @SuppressWarnings("restriction")
	public static byte[] desDecrypt(byte[] arrB,String strKey) throws Exception {
		Security.addProvider(new com.sun.crypto.provider.SunJCE());
		Key key = getKey(strKey.getBytes());

		Cipher decryptCipher = Cipher.getInstance("DES");
		decryptCipher.init(Cipher.DECRYPT_MODE, key);
	    return decryptCipher.doFinal(arrB);
	  }

	  /**  
	   * 解密字符串  
	   *   
	   * @param strIn  
	   *            需解密的字符串  
	   * @return 解密后的字符串  
	   * @throws Exception  
	   */
	  public static String desDecrypt(String strIn,String strKey) throws Exception {
		  return new String(desDecrypt(hexStr2ByteArr(strIn),strKey));
	  }

	  /**  
	   * 从指定字符串生成密钥，密钥所需的字节数组长度为8位 不足8位时后面补0，超出8位只取前8位  
	   *   
	   * @param arrBTmp  
	   *            构成该字符串的字节数组  
	   * @return 生成的密钥  
	   * @throws java.lang.Exception  
	   */
	  private static Key getKey(byte[] arrBTmp) throws Exception {
	    // 创建一个空的8位字节数组（默认值为0）   
	    byte[] arrB = new byte[8];
	    // 将原始字节数组转换为8位   
	    for (int i = 0; i < arrBTmp.length && i < arrB.length; i++) {
	      arrB[i] = arrBTmp[i];
	    }
	    // 生成密钥   
	    Key key = new javax.crypto.spec.SecretKeySpec(arrB, "DES");

	    return key;
	  }

	  /**
	   * main方法  。
	   * @author 刘尧兴
	   * @param args
	   */
	  public static void main(String[] args) {
	    try {
	      String test = "123456789";
	      System.out.println("加密前的字符：" + test);
	      System.out.println("加密后的字符：" + Encrypt.desEncrypt(test,"aa"));
	      System.out.println("解密后的字符：" + Encrypt.desDecrypt(Encrypt.desEncrypt(test,"aa"),"aa"));
	      
	    }
	    catch (Exception e) {
	      e.printStackTrace();
	    }
	  }


}
