package com.wunderlist.tools;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

/**
 *  从输入流中获取字节数据
 * @author Silocean
 *
 */
public class StreamTool {

	/**
	 * 从输入流中获取字节数组数据
	 * @param inputStream
	 * @return
	 * @throws Exception
	 */
	public static byte[] read(InputStream inputStream) throws Exception {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		byte[] buf = new byte[1024];
		int len = 0;
		while((len=inputStream.read(buf)) != -1) {
			baos.write(buf, 0, len);
		}
		return baos.toByteArray();
	}

}
