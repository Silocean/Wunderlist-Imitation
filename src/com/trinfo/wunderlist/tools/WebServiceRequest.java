package com.trinfo.wunderlist.tools;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import com.trinfo.wunderlist.entity.Common;

/**
 * 处理webservice请求
 * @author Silocean
 *
 */
public class WebServiceRequest {
	
	/**
	 * 向webservice发送xml请求数据
	 * @param path webservice路径
	 * @param inputStream 本地xml文件读取流
	 * @param data 要发送的数据
	 * @param tagStr 返回数据中供识别的tag字符
	 * @return json格式数据
	 * @throws Exception
	 */
	public static String SendPost (InputStream inputStream, byte[] data, String tagStr) throws Exception{
		HttpURLConnection conn = (HttpURLConnection)new URL(Common.WEBSERVICEPATH).openConnection();
		conn.setConnectTimeout(5000);
		conn.setRequestMethod("POST");
		conn.setRequestProperty("Content-Type", "text/xml; charset=UTF-8");
		conn.setRequestProperty("Content-Length", String.valueOf(data.length));
		conn.setDoOutput(true);
		conn.getOutputStream().write(data);
		if(conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
			return parseResponse(conn.getInputStream(), tagStr);
		} else {
			return null;
		}
	}
	/**
	 * 解析返回的xml数据
	 * @param inputStream 从conn连接中取得的读取流
	 * @param tagStr 返回数据中供识别的tag字符
	 * @return json格式数据
	 * @throws Exception
	 */
	public static String parseResponse(InputStream inputStream, String tagStr) throws Exception {
		XmlPullParser parser = XmlPullParserFactory.newInstance().newPullParser();
		parser.setInput(inputStream, "UTF-8");
		int event = parser.getEventType();
		while(event!=XmlPullParser.END_DOCUMENT) {
			switch(event){
			case XmlPullParser.START_TAG: {
				if(parser.getName().equals(tagStr)){
					return parser.nextText();
				}
				break;
			}
			default:
				break;
			}
			event = parser.next();
		}
		return null;
	}

}