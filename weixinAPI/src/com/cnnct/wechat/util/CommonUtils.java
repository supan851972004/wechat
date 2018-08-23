package com.cnnct.wechat.util;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

/**
 * @ClassName: CommonUtils 
 * @Description: 支付系统公共工具类  
 * @Author: guodong.zhang 
 * @Date: 2016-3-14 下午6:17:44
 */
public class CommonUtils {
	
	private static Logger logger = Logger.getLogger(CommonUtils.class);
	
	/**
	 * @Title: mapToString
	 * @Description: map转换为string
	 * @param parameterMap
	 * @return: String
	 * @Author: guodong.zhang 
	 * @Date: 2015-12-2 下午10:01:11
	 */
	public static String mapToString(Map<String, Object> parameterMap) {
		StringBuffer stringBuffer = new StringBuffer();
		for (String key : parameterMap.keySet()) {
			String value = parameterMap.get(key).toString();
			if (value != null && !value.trim().equals("")) {
				stringBuffer.append("&" + key + "=" + value);
			}
		}
		if (stringBuffer.length() > 0) {
			stringBuffer.deleteCharAt(0);
		}
		return stringBuffer.toString();
	}
	
	/**
	 * @Title: xmltoMap
	 * @Description: xml转换为map 
	 * @param xml
	 * @return: Map
	 * @Author: guodong.zhang 
	 * @Date: 2016-3-14 下午6:43:42
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static Map xmltoMap(String xml) {
		try {
			Map map = new HashMap();
			Document document = DocumentHelper.parseText(xml);
			Element nodeElement = document.getRootElement();
			List node = nodeElement.elements();
			for (Iterator it = node.iterator(); it.hasNext();) {
				Element elm = (Element) it.next();
				map.put(elm.getName(), elm.getText());
				elm = null;
			}
			node = null;
			nodeElement = null;
			document = null;
			return map;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static Map parseXml(HttpServletRequest request) {
		try {
			Map map = new HashMap();
			InputStream inputStream = request.getInputStream();
			 // 读取输入流
		    SAXReader reader = new SAXReader();
		    Document document = reader.read(inputStream);
			Element nodeElement = document.getRootElement();
			List node = nodeElement.elements();
			for (Iterator it = node.iterator(); it.hasNext();) {
				Element elm = (Element) it.next();
				map.put(elm.getName(), elm.getText());
				elm = null;
			}
			node = null;
			nodeElement = null;
			document = null;
			return map;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * @Title: sign
	 * @Description: MD5对指定字符串进行签名
	 * @param text
	 * @param key
	 * @param input_charset
	 * @return: String
	 * @Author: guodong.zhang 
	 * @Date: 2016-3-14 下午6:39:22
	 */
	public static String signByMD5(String text, String key, String input_charset) {
		text = text + "&key=" + key;
		logger.debug("需要签名的参数:" + text);
		return DigestUtils.md5Hex(getContentBytes(text, input_charset));
	}
	
	/**
	 * @Title: getContentBytes
	 * @Description: 根据指定的编码格式(charset)获取字符串的字节流
	 * @param content
	 * @param charset
	 * @return: byte[]
	 * @Author: guodong.zhang 
	 * @Date: 2016-3-14 下午6:37:20
	 */
	public static byte[] getContentBytes(String content, String charset) {
		if (charset == null || "".equals(charset)) {
			return content.getBytes();
		}
		try {
			return content.getBytes(charset);
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(
					"MD5 charset:" + charset);
		}
	}
	
	/**
	 * @Title: getNonceStr
	 * @Description: 生成32位随机字符串(含英文大小写字母、0~9) 
	 * @return: String
	 * @Author: guodong.zhang 
	 * @Date: 2016-3-14 下午6:20:09
	 */
	public static String generateNonceStr() {
		String achars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
		int maxPos = achars.length();
		String noceStr = "";
		for (int i = 0; i < 32; i++) {
			noceStr += achars.charAt((int) Math.floor(Math.random() * maxPos));
		}
		return noceStr;
	}
	
}
