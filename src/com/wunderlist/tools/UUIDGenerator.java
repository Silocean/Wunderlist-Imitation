package com.wunderlist.tools;

import java.util.UUID;

/**
 * UUID生成器
 * @author Silocean
 *
 */
public class UUIDGenerator {
	
	public UUIDGenerator() {
		
	}

	public static String getUUID() {
		UUID uuid = UUID.randomUUID();
		return uuid.toString();
	}

	// 获得指定数量的UUID
	public static String[] getUUID(int number) {
		if (number < 1) {
			return null;
		}
		String[] ss = new String[number];
		for (int i = 0; i < number; i++) {
			ss[i] = getUUID();
		}
		return ss;
	}

}