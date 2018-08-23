package com.cnnct.wechat.util;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.Properties;

import org.apache.log4j.Logger;



/**
 * @ClassName: HttpClientUtil 
 * @Description: HttpClient工具类
 * @Author: guodong.zhang 
 * @Date: 2016-3-14 下午6:51:04
 */
public class HttpUtils {

	private static Logger logger = Logger.getLogger(HttpUtils.class);
	
	/**
	 * @Title: sendPost
	 * @Description: 发送post请求
	 * @param url
	 * @param param
	 * @return: String
	 * @Author: guodong.zhang 
	 * @Date: 2016-3-14 下午6:52:02
	 */
	public static String sendPost(String url, String param) {
		PrintWriter out = null;
		BufferedReader in = null;
		String result = "";
		try {
			URL realUrl = new URL(url);
			// 打开和URL之间的连接
			URLConnection conn = realUrl.openConnection();
			// 设置通用的请求属性
			conn.setRequestProperty("accept", "*/*");
			conn.setRequestProperty("accept-charset", "UTF-8");
			conn.setRequestProperty("Content-Type", "text/plain; charset=utf-8");
			conn.setRequestProperty("connection", "Keep-Alive");
			// 发送POST请求必须设置如下两行
			conn.setDoOutput(true);
			conn.setDoInput(true);
			// 获取URLConnection对象对应的输出流
			out = new PrintWriter(conn.getOutputStream());
			// 发送请求参数
			out.print(param);
			// flush输出流的缓冲
			out.flush();
			// 定义BufferedReader输入流来读取URL的响应
			in = new BufferedReader(new InputStreamReader(
					conn.getInputStream(), "UTF-8"));
			String line;
			while ((line = in.readLine()) != null) {
				result += line;
			}
		} catch (Exception e) {
			logger.error("send_post_error",e);
		}
		// 使用finally块来关闭输出流、输入流
		finally {
			try {
				if (out != null) {
					out.close();
				}
				if (in != null) {
					in.close();
				}
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		return result;
	}
	
	public  static Properties getProperties(String fileName) throws IOException {// fileName,
		// 类路径下面的配置文件名称

		String path = new HttpUtils().getClass()
				.getResource("/"+fileName).getFile();
        logger.debug("path ===== " + path);
		InputStream path1 = HttpUtils.class.
				getResourceAsStream("/"+fileName);
				;
		InputStream in = new BufferedInputStream(path1);
		Properties pro = new Properties();
		
		pro.load(in);

		return pro;
	}
	
}
