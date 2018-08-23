package com.cnnct.wechat.busi;

import com.alibaba.fastjson.JSONObject;
import com.cnnct.wechat.util.PropConfig;

/**
 * 小程序工具类
 * @author Administrator
 *
 */
public class WechatProgramUtils {
	
	/**
	 * 小程序根据code获取openid等信息
	 * @param code
	 * @return
	 * @throws Exception
	 */
	public static JSONObject getWxInfo(String code) throws Exception {
		StringBuilder url = new StringBuilder();
		JSONObject json = new JSONObject();
		try {
			url.append("https://api.weixin.qq.com/sns/jscode2session");
			url.append("?appid=").append(PropConfig.readValue("appid_mprog"));
			url.append("&secret=").append(PropConfig.readValue("app_secret_mprog"));
			url.append("&js_code=").append(code);
			url.append("&grant_type=authorization_code");
			json = JSONObject.parseObject(WechatAuthUtils.doGet(url.toString()));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return json;
	}
	
}
