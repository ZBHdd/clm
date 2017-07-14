package com.sogou.api.client.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @author SogouAPI team
 * 
 */
public final class MD5 {

	private final static String algorithm = "MD5";

	public static String generateMD5(String seed) {
		MessageDigest md;
		try {
			md = MessageDigest.getInstance(algorithm);
			md.update(seed.getBytes());
			byte[] bytes = md.digest();
			return toHexString(bytes);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return null;
	}

	/*
	 * Converts a byte to hex digit and writes to the supplied buffer
	 */
	private static void byte2hex(byte b, StringBuffer buf) {
		char[] hexChars = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
				'a', 'b', 'c', 'd', 'e', 'f' };
		int high = ((b & 0xf0) >> 4);
		int low = (b & 0x0f);
		buf.append(hexChars[high]);
		buf.append(hexChars[low]);
	}

	/*
	 * Converts a byte array to hex string
	 */
	private static String toHexString(byte[] block) {
		StringBuffer buf = new StringBuffer();
		int len = block.length;
		for (int i = 0; i < len; i++) {
			byte2hex(block[i], buf);
		}
		return buf.toString();
	}

	public static void main(String[] args) {

	}
}
