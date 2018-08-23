package com.cnnct.wechat.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;

import com.cnnct.wechat.busi.WechatPayUtils;


public class PropConfig {
	
	private static final String CONFIG_PATH = "/config/parameter/wechat/wechat_auth.properties";
	
	public static String readValue(String key) {  
		Properties props = new Properties();
		String value = "";
		try {   
			InputStream ips = WechatPayUtils.class.getResourceAsStream(CONFIG_PATH);  
			BufferedReader ipss = new BufferedReader(new InputStreamReader(ips));  
			props.load(ipss);  
			value = props.getProperty(key);  
		} catch (Exception e) {  
			e.printStackTrace();
		}
		return value;
	}
	
}
