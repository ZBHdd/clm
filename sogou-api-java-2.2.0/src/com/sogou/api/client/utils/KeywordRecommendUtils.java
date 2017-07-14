package com.sogou.api.client.utils;

import java.util.concurrent.TimeUnit;

import com.sogou.api.client.constants.APIClientConstants;
import com.sogou.api.client.exception.ClientInternalException;
import com.sogou.api.client.exception.SystemException;
import com.sogou.api.client.wrapper.KRServiceWrapper;
import com.sogou.api.sem.v1.kr.GetKRFileIdRequest;
import com.sogou.api.sem.v1.kr.GetKRFileIdResponse;
import com.sogou.api.sem.v1.kr.GetKRFilePathRequest;
import com.sogou.api.sem.v1.kr.GetKRFilePathResponse;
import com.sogou.api.sem.v1.kr.GetKRFileStatusRequest;
import com.sogou.api.sem.v1.kr.GetKRFileStatusResponse;

public abstract class KeywordRecommendUtils {

	public static String getKRFileURL(GetKRFileIdRequest getKRFileIdRequest,
			int retryNum) {
		if (null == getKRFileIdRequest || retryNum < 1) {
			throw new IllegalArgumentException(
					"illegal get kr file url parametes, parameters="
							+ getKRFileIdRequest + ", retryNum=" + retryNum);
		}

		KRServiceWrapper krService = new KRServiceWrapper();

		GetKRFileIdResponse getKRFileIdResponse = krService
				.getKRFileId(getKRFileIdRequest);
		if (null == getKRFileIdResponse
				|| null == getKRFileIdResponse.getKrFileId()) {
			throw new SystemException("API return null kr file id");
		}

		GetKRFileStatusRequest getKRFileStatusRequest = new GetKRFileStatusRequest();
		getKRFileStatusRequest.setKrFileId(getKRFileIdResponse.getKrFileId());
		int retry = retryNum;
		boolean status = false;
		while (retry-- > 0) {
			GetKRFileStatusResponse getKRFileStatusResponse = krService
					.getKRFileStatus(getKRFileStatusRequest);
			if (null == getKRFileStatusResponse
					|| null == getKRFileStatusResponse.getIsGenerated()) {
				throw new SystemException("API return null kr file status");
			}
			if (getKRFileStatusResponse.getIsGenerated() == 0) {
				try {
					TimeUnit.SECONDS.sleep(APIClientConstants.QUERY_FILE_STATUS_INTERVAL_SECONDS);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			} else {
				status = true;
				break;
			}
		}
		if (status) {
			GetKRFilePathRequest getKRFilePathRequest = new GetKRFilePathRequest();
			getKRFilePathRequest.setKrFileId(getKRFileIdResponse.getKrFileId());

			GetKRFilePathResponse getKRFilePathResponse = krService
					.getKRFilePath(getKRFilePathRequest);
			if (null == getKRFilePathResponse
					|| null == getKRFilePathResponse.getKrFilePath()) {
				throw new SystemException("API return null kr file path");
			}
			return getKRFilePathResponse.getKrFilePath();
		} else {
			throw new ClientInternalException("We tried to get kr file for "
					+ retryNum * APIClientConstants.QUERY_FILE_STATUS_INTERVAL_SECONDS + " seconds, but file still not ready!");
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
