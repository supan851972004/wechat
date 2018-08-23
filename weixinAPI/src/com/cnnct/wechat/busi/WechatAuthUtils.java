package com.cnnct.wechat.busi;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;
import java.security.MessageDigest;
import java.util.UUID;

import com.alibaba.fastjson.JSONObject;
import com.cnnct.wechat.util.PropConfig;

/**
 * 微信授权工具类
 * @author Administrator
 *
 */
public class WechatAuthUtils {

	private static String proxyHost = "";
	private static Integer proxyPort = null;
	private static String charset = "utf-8";

	public static String comm_token = "";
	public static Long comm_expires = 0l;
	public static String apiticket = "";
	public static Long apiticket_expires = 0l;

	/**
	 * 发送get请求方法
	 * 
	 * @param url
	 * @return
	 * @throws Exception
	 */
	public static String doGet(String url) throws Exception {

		URL localURL = new URL(url);

		URLConnection connection = openConnection(localURL);
		HttpURLConnection httpURLConnection = (HttpURLConnection) connection;

		httpURLConnection.setRequestProperty("Accept-Charset", charset);
		httpURLConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

		InputStream inputStream = null;
		InputStreamReader inputStreamReader = null;
		BufferedReader reader = null;
		StringBuffer resultBuffer = new StringBuffer();
		String tempLine = null;

		if (httpURLConnection.getResponseCode() >= 300) {
			throw new Exception("HTTP Request is not success, Response code is " + httpURLConnection.getResponseCode());
		}

		try {
			inputStream = httpURLConnection.getInputStream();
			inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
			reader = new BufferedReader(inputStreamReader);

			while ((tempLine = reader.readLine()) != null) {
				resultBuffer.append(tempLine);
			}

		} finally {

			if (reader != null) {
				reader.close();
			}

			if (inputStreamReader != null) {
				inputStreamReader.close();
			}

			if (inputStream != null) {
				inputStream.close();
			}

		}
		return resultBuffer.toString();
	}

	private static URLConnection openConnection(URL localURL) throws IOException {
		URLConnection connection;
		if (proxyHost != null && proxyPort != null) {
			Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyHost, proxyPort));
			connection = localURL.openConnection(proxy);
		} else {
			connection = localURL.openConnection();
		}
		return connection;
	}

	/**
	 * 根据code获取微信用户基本信息
	 * 
	 * @param code
	 * @return
	 * @throws Exception
	 */
	public static JSONObject getWxInfo(String code) throws Exception {
		JSONObject data = new JSONObject();
		// 通过code换取网页授权access_token
		data = getAccessToken(code);
		data = refreshAccessToken(data.getString("refresh_token"));
		String openid = data.getString("openid");
		String access_token = data.getString("access_token");
		// 获取用户信息
		JSONObject userInfo = getUserInfo(access_token, openid);
		return userInfo;
	}

	/**
	 * 根据code获取网页授权access_token(有效期7200s)
	 * 
	 * @param code
	 * @return
	 * @throws Exception
	 */
	private static JSONObject getAccessToken(String code) throws Exception {
		StringBuilder url = new StringBuilder();
		JSONObject json = new JSONObject();
		try {
			url.append(PropConfig.readValue("access_token_url"));
			url.append("?appid=").append(PropConfig.readValue("appid"));
			url.append("&secret=").append(PropConfig.readValue("app_secret"));
			url.append("&code=").append(code);
			url.append("&grant_type=authorization_code");
			json = JSONObject.parseObject(doGet(url.toString()));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return json;
	}

	/**
	 * 刷新access_token获取更长的有效期(30天)
	 * 
	 * @param refresh_token
	 * @return
	 * @throws Exception
	 */
	private static JSONObject refreshAccessToken(String refresh_token) throws Exception {
		StringBuilder url = new StringBuilder();
		JSONObject json = new JSONObject();
		try {
			url.append(PropConfig.readValue("refresh_token_url"));
			url.append("?appid=").append(PropConfig.readValue("appid"));
			url.append("&grant_type=refresh_token");
			url.append("&refresh_token=" + refresh_token);
			json = JSONObject.parseObject(doGet(url.toString()));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return json;
	}

	/**
	 * 根据access_token和openid 获取用户基本信息
	 * 
	 * @param access_token
	 * @param openid
	 * @return
	 * @throws Exception
	 */
	private static JSONObject getUserInfo(String access_token, String openid) throws Exception {
		StringBuilder url = new StringBuilder();
		try {
			url.append(PropConfig.readValue("scope_url"));
			url.append("?access_token=").append(access_token);
			url.append("&openid=").append(openid);
			url.append("&lang=zh_CN");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return JSONObject.parseObject(doGet(url.toString()));
	}

	/**
	 * 判断用户是否已关注该公众号 subscribe 0:未关注, 1:已关注
	 * 
	 * @throws Exception
	 */
	public static JSONObject isSubscribe(String openid) throws Exception {
		JSONObject jsonObject = new JSONObject();
		StringBuilder url = new StringBuilder();
		try {
			// 获取token
			url.append(PropConfig.readValue("token_url"));
			url.append("?grant_type=").append("client_credential");
			url.append("&appid=").append(PropConfig.readValue("appid"));
			url.append("&secret=").append(PropConfig.readValue("app_secret"));
			JSONObject json = JSONObject.parseObject(doGet(url.toString()));
			String access_token = json.getString("access_token");
			// 获取包含关注信息的用户基本信息
			StringBuilder oauth_url = new StringBuilder();
			oauth_url.append(PropConfig.readValue("sub_info_url"));
			oauth_url.append("?access_token=").append(access_token);
			oauth_url.append("&openid=").append(openid);
			oauth_url.append("&lang=zh_CN");
			JSONObject userJson = JSONObject.parseObject(doGet(oauth_url.toString()));
			// subscribe 0:未关注, 1:已关注
			String subscribe = userJson.getString("subscribe");
			jsonObject.put("result", "0");
			jsonObject.put("subscribe", subscribe);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return jsonObject;
	}

	/**
	 * 获取微信签名,用于使用微信JS_SDK
	 * 
	 * @param signurl
	 * @return
	 * @throws Exception
	 */
	public static JSONObject getWxSign(String signurl) throws Exception {
		JSONObject jsonObject = new JSONObject();
		StringBuilder url = new StringBuilder();
		try {
			// 获取token
			url.append(PropConfig.readValue("token_url"));
			url.append("?grant_type=").append("client_credential");
			url.append("&appid=").append(PropConfig.readValue("appid"));
			url.append("&secret=").append(PropConfig.readValue("app_secret"));
			JSONObject json = JSONObject.parseObject(doGet(url.toString()));
			comm_token = json.getString("access_token");
			// 获取ticket
			String ticket = "";
			Long curtime = System.currentTimeMillis();
			if (apiticket == "" || curtime - apiticket_expires > 0) {
				StringBuffer url1 = new StringBuffer("https://api.weixin.qq.com/cgi-bin/ticket/getticket?access_token="
						+ comm_token + "&type=jsapi");
				String jsonStr = doGet(url1.toString());
				JSONObject ticketJson = JSONObject.parseObject(jsonStr);
				ticket = (String) ticketJson.get("ticket");
				apiticket = ticket;
				apiticket_expires = ticketJson.getLong("expires_in") * 1000 + curtime;
			} else {
				ticket = (String) apiticket;
			}
			jsonObject = sign(ticket, signurl);
			jsonObject.put("result", "0");
		} catch (Exception e) {
			jsonObject.put("result", "1");
			jsonObject.put("msg", e.getMessage());
			e.printStackTrace();
		}
		return jsonObject;
	}

	private static JSONObject sign(String jsapi_ticket, String url) {
		JSONObject ret = new JSONObject();
		String nonce_str = UUID.randomUUID().toString();
		String timestamp = Long.toString(System.currentTimeMillis() / 1000);
		String string1;
		String signature = "";
		// 注意这里参数名必须全部小写，且必须有序
		string1 = "jsapi_ticket=" + jsapi_ticket + "&noncestr=" + nonce_str + "&timestamp=" + timestamp + "&url=" + url;
		try {
			MessageDigest crypt = MessageDigest.getInstance("SHA-1");
			crypt.reset();
			crypt.update(string1.getBytes("UTF-8"));
			signature = byteToHex(crypt.digest());
		} catch (Exception e) {
			e.printStackTrace();
		}
		ret.put("url", url);
		ret.put("jsapi_ticket", jsapi_ticket);
		ret.put("nonceStr", nonce_str);
		ret.put("timestamp", timestamp);
		ret.put("signature", signature);
		ret.put("appid", PropConfig.readValue("appid"));
		return ret;
	}

	private static String byteToHex(byte[] digest) {
		StringBuffer hexstr = new StringBuffer();
		String shaHex = "";
		for (int i = 0; i < digest.length; i++) {
			shaHex = Integer.toHexString(digest[i] & 0xFF);
			if (shaHex.length() < 2) {
				hexstr.append(0);
			}
			hexstr.append(shaHex);
		}
		return hexstr.toString();
	}
	
	
	
}
