package com.sogou.api.client.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.zip.GZIPInputStream;

import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipFile;

public abstract class ZipUtils {
	private final static int BUFFER_SIZE = 2028;

	public static void unzipFile(String srcZipFile, String destFilePath)
			throws IOException {
		if (srcZipFile == null || destFilePath == null)
			throw new IllegalArgumentException(
					"illegal unzip file parametes, parameters=" + srcZipFile
							+ ", destFilePath=" + destFilePath);

		File file = new File(srcZipFile);
		if (!file.exists()) {
			throw new IllegalArgumentException("cannot find srcZipFile="
					+ srcZipFile);
		}
		File outputDir = new File(destFilePath);
		if (!file.exists()) {
			file.mkdirs();
		}

		ZipFile zipFile = null;
		try {
			zipFile = new ZipFile(file);
			createDirectory(destFilePath, null); // 创建输出目录

			Enumeration<?> enums = zipFile.getEntries();
			while (enums.hasMoreElements()) {
				ZipEntry entry = (ZipEntry) enums.nextElement();
				if (entry.isDirectory()) { // 处理目录
					createDirectory(destFilePath, entry.getName());// 创建空目录
				} else { // 处理文件
					File tmpFile = new File(outputDir + "/" + entry.getName());
					createDirectory(tmpFile.getParent() + "/", null);
					InputStream in = null;
					OutputStream out = null;
					try {
						in = zipFile.getInputStream(entry);
						out = new FileOutputStream(tmpFile);
						int length = 0;
						byte[] buffer = new byte[BUFFER_SIZE];
						while ((length = in.read(buffer)) != -1) {
							out.write(buffer, 0, length);
						}
						out.flush();
					} catch (IOException e) {
						throw e;
					} finally {
						if (in != null)
							in.close();
						if (out != null)
							out.close();
					}
				}
			}
		} catch (IOException e) {
			throw new IOException("解压缩文件出现异常", e);
		} finally {
			try {
				if (zipFile != null) {
					zipFile.close();
				}
			} catch (IOException e) {
				throw new IOException("关闭zipFile出现异常", e);
			}
		}

	}

	private static void createDirectory(String outputDir, String subDir) {

		if (outputDir == null)
			return;
		File file = new File(outputDir);
		if (!(subDir == null || subDir.trim().equals(""))) { // 子目录不为空
			file = new File(outputDir + "/" + subDir);
		}
		if (!file.exists()) {
			file.mkdirs();
		}
	}

	public static void unGzipFile(String srcGzipFile, String destFilePath)
			throws IOException {
		if (srcGzipFile == null || destFilePath == null)
			throw new IllegalArgumentException(
					"illegal ungzip file parametes, parameters=" + srcGzipFile
							+ ", destFilePath=" + destFilePath);

		File file = new File(srcGzipFile);
		if (!file.exists()) {
			throw new IllegalArgumentException("cannot find srcZipFile="
					+ srcGzipFile);
		}

		GZIPInputStream zin = new GZIPInputStream(new FileInputStream(file));
		OutputStream out = new FileOutputStream(new File(destFilePath));
		try {
			byte[] data = new byte[BUFFER_SIZE];
			int len;
			while ((len = zin.read(data)) != -1) {
				out.write(data, 0, len);
			}
		} finally {
			if (null != zin)
				zin.close();
			if (null != out)
				out.close();
		}

	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {

	}

}
