package com.cnnct.wechat.util;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

import net.sf.json.JSON;
import net.sf.json.JSONObject;
import net.sf.json.xml.XMLSerializer;

public class FormatConvertUtil {
	@SuppressWarnings("rawtypes")
	public static String maptoXml(Map map) {  
        Document document = DocumentHelper.createDocument();  
        Element nodeElement = document.addElement("xml");  
        for (Object obj : map.keySet()) {  
            Element keyElement = nodeElement.addElement(String.valueOf(obj));  
            keyElement.setText(String.valueOf(map.get(obj)));  
        }  
        return doc2String(document);  
    }
    public static String doc2String(Document document) {  
        String s = "";  
        try {  
            // 使用输出流来进行转化  
            ByteArrayOutputStream out = new ByteArrayOutputStream();  
            // 使用UTF-8编码  
            OutputFormat format = new OutputFormat("   ", true, "UTF-8");  
            XMLWriter writer = new XMLWriter(out, format);  
            writer.write(document);  
            s = out.toString("UTF-8");  
        } catch (Exception ex) {  
            ex.printStackTrace();  
        }  
        return s;  
    }  
    
    /**
     * xml序列化成json
     * @param inputStream
     * @return JSONObject
     * @Author guodong.zhang 2016-4-29 下午4:00:51
     */
    public static JSONObject xmlToJson(InputStream inputStream){
    	XMLSerializer xmlSerializer = new XMLSerializer();
    	JSON json = xmlSerializer.readFromStream(inputStream);
    	return (JSONObject) json;
    }
    
    
}
