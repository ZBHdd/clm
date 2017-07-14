package com.sogou.api.client.examples;

import java.io.IOException;

import com.sogou.api.client.utils.FileDownloadUtils;
import com.sogou.api.client.utils.KeywordRecommendUtils;
import com.sogou.api.sem.v1.kr.GetKRFileIdRequest;

public class KRServiceExamples {

	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		GetKRFileIdRequest getKRFileIdRequest = new GetKRFileIdRequest();
		
		String url = KeywordRecommendUtils.getKRFileURL(getKRFileIdRequest, 100);
		System.out.println(url);
		/*try {
			FileDownloadUtils.downloadFile(url, "D:/logs/kr.gzip");
		} catch (IOException e) {
			e.printStackTrace();
		}*/
	}

}
