package com.sogou.api.client.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import com.sogou.api.client.exception.ClientInternalException;

public class FileDownloadUtils {
	
	private static final int BUFFER_SIZE = 1024 * 16;
	private static final int CONNECT_TIMEOUT = 6000;
	private static final int READ_TIMEOUT = 6000;

	public static void downloadFile(String downloadUrl, String destFile)
			throws IOException {
		downloadFile(downloadUrl, destFile, CONNECT_TIMEOUT, READ_TIMEOUT);
	}
	
	public static void downloadFile(String downloadUrl, String destFile, int connectTimeout, int readTimeout)
			throws IOException {

		File file = new File(destFile);// 创建新文件
		if (file != null && !file.exists()) {
			file.createNewFile();
		}
		OutputStream outstream = new FileOutputStream(file);
		URL url = new URL(downloadUrl);
		HttpURLConnection uc = (HttpURLConnection) url.openConnection();
		uc.setConnectTimeout(connectTimeout);
		uc.setReadTimeout(readTimeout);
		
		uc.connect();
		if (uc instanceof HttpURLConnection) {
			HttpURLConnection httpCon = (HttpURLConnection) uc;
			if (httpCon.getResponseCode() > 399) {
				String msg = "Failed to download file " + downloadUrl
						+ " server response "
						+ httpCon.getResponseMessage();
				throw new ClientInternalException(msg);
			}
		}
		InputStream inputstream = uc.getInputStream();
		byte[] buffer = new byte[BUFFER_SIZE];
		int byteRead = -1;
		while ((byteRead = (inputstream.read(buffer))) != -1) {
			outstream.write(buffer, 0, byteRead);
		}
		outstream.flush();
		if (null != inputstream)
			inputstream.close();
		if (null != outstream)
			outstream.close();

	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
