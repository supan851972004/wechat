package com.cnnct.wechat.busi;

import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import com.alibaba.fastjson.JSONObject;
import com.cnnct.wechat.util.CommonUtils;
import com.cnnct.wechat.util.FormatConvertUtil;
import com.cnnct.wechat.util.HttpUtils;
import com.cnnct.wechat.util.PropConfig;

/**
 * 微信小程序支付工具类
 * @author supan
 *
 */
public class WechatProgramPayUtils {
	
	private static String input_charset = "UTF-8";

	/**
	 * 微信统一下单
	 * 
	 * @param json
	 * @return
	 * @throws Exception
	 */
	public static JSONObject createUnifiedorder(JSONObject json) throws Exception {
		JSONObject resultJson = new JSONObject();
		String openid = json.getString("openid");// 用户openid
		String total_fee = json.getString("total_fee");// 支付金额
		String appid = PropConfig.readValue("appid_mprog");//小程序appid
		String mch_id = PropConfig.readValue("mch_id");// 商户号
		String nonce_str = CommonUtils.generateNonceStr();// 随机字符串
		String notify_url = json.getString("notify_url");// 微信支付回调地址
		// 商户订单号
		String out_trade_no = json.getString("out_trade_no");
		String body = json.getString("body");// 商品描述
		String trade_type = json.getString("trade_type");// 交易类型
		String payKey = PropConfig.readValue("api_key");// API支付秘钥key
		String spbill_create_ip = json.getString("spbill_create_ip");// 终端IP
		// 封装参数 生成签名
		SortedMap<String, Object> signParams = new TreeMap<String, Object>();
		signParams.put("appid", appid);
		signParams.put("body", body);
		signParams.put("mch_id", mch_id);
		signParams.put("nonce_str", nonce_str);
		signParams.put("notify_url", notify_url);
		signParams.put("openid", openid);
		signParams.put("out_trade_no", out_trade_no);
		signParams.put("spbill_create_ip", spbill_create_ip);
		signParams.put("total_fee", total_fee);
		signParams.put("trade_type", trade_type);
		// 签名
		String mysign = CommonUtils.signByMD5(CommonUtils.mapToString(signParams), payKey, input_charset);
		signParams.put("sign", mysign.toUpperCase());
		// 请求参数转换为xml格式 微信下单需要将参数转为xml格式
		String paramsXml = FormatConvertUtil.maptoXml(signParams);
		paramsXml = new String(paramsXml.getBytes("ISO8859-1"),"UTF-8");
		// 微信下单url
		String url = PropConfig.readValue("createOrder_url");
		// 微信下单
		String resultStr = HttpUtils.sendPost(url, paramsXml);
		// 下单返回结果为xml格式 转为map
		Map<String, String> weixinResultMap = CommonUtils.xmltoMap(resultStr);
		SortedMap<String, Object> resultMap = new TreeMap<String, Object>();
		if (weixinResultMap.get("return_code").equals("SUCCESS")
				&& weixinResultMap.get("result_code").equals("SUCCESS")) {
			resultMap = new TreeMap<String, Object>();
			resultMap.put("appId", appid);
			resultMap.put("nonceStr", nonce_str);
			resultMap.put("package", "prepay_id=" + weixinResultMap.get("prepay_id"));
			resultMap.put("signType", "MD5");
			String time = String.valueOf(System.currentTimeMillis()).substring(0, 10);
			resultMap.put("timeStamp", time);
			// 下单成功后 将调用支付所需参数再次签名
			mysign = CommonUtils.signByMD5(CommonUtils.mapToString(resultMap), payKey, input_charset);
			resultJson.put("paySign", mysign.toUpperCase());
			resultJson.put("appid", appid);
			resultJson.put("nonceStr", nonce_str);
			resultJson.put("package", "prepay_id=" + weixinResultMap.get("prepay_id"));
			resultJson.put("signType", "MD5");
			resultJson.put("timeStamp", time);
			resultJson.put("result", 0);
		}
		return resultJson;
	}
	
}
