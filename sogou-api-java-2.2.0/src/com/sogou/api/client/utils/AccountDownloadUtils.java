package com.sogou.api.client.utils;

import java.util.concurrent.TimeUnit;

import com.sogou.api.client.constants.APIClientConstants;
import com.sogou.api.client.exception.ClientInternalException;
import com.sogou.api.client.exception.SystemException;
import com.sogou.api.client.wrapper.AccountDownloadServiceWrapper;
import com.sogou.api.sem.v1.accountdownload.GetAccountFilePathRequest;
import com.sogou.api.sem.v1.accountdownload.GetAccountFilePathResponse;
import com.sogou.api.sem.v1.accountdownload.GetAccountFileRequest;
import com.sogou.api.sem.v1.accountdownload.GetAccountFileResponse;
import com.sogou.api.sem.v1.accountdownload.GetAccountFileStatusRequest;
import com.sogou.api.sem.v1.accountdownload.GetAccountFileStatusResponse;

public abstract class AccountDownloadUtils {

	public static String getAccountDownloadURL(
			GetAccountFileRequest getAccountFileRequest, int retryNum) {
		if (null == getAccountFileRequest || retryNum < 1) {
			throw new IllegalArgumentException(
					"illegal get account file parametes, parameters="
							+ getAccountFileRequest + ", retryNum=");
		}

		AccountDownloadServiceWrapper accountDownService = new AccountDownloadServiceWrapper();

		GetAccountFileResponse getAccountFileResponse = accountDownService
				.getAccountFile(getAccountFileRequest);
		if (null == getAccountFileResponse
				|| null == getAccountFileResponse.getAccountFileId()) {
			throw new SystemException("API return null account file id");
		}

		GetAccountFileStatusRequest getAccountFileStatusRequest = new GetAccountFileStatusRequest();
		getAccountFileStatusRequest.setAccountFileId(getAccountFileResponse
				.getAccountFileId());
		GetAccountFileStatusResponse getAccountFileStatusResponse = null;
		int retry = retryNum;
		boolean status = false;
		while (retry-- > 0) {
			getAccountFileStatusResponse = accountDownService
					.getAccountFileStatus(getAccountFileStatusRequest);
			if (null == getAccountFileStatusResponse
					|| null == getAccountFileStatusResponse.getIsGenerated()) {
				throw new SystemException(
						"API return null account download state");
			}
			if (getAccountFileStatusResponse.getIsGenerated() == 0) {
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
			GetAccountFilePathRequest getAccountFilePathRequest = new GetAccountFilePathRequest();
			getAccountFilePathRequest.setAccountFileId(getAccountFileResponse
					.getAccountFileId());

			GetAccountFilePathResponse getAccountFilePathResponse = accountDownService
					.getAccountFilePath(getAccountFilePathRequest);
			if (null == getAccountFilePathResponse
					|| null == getAccountFilePathResponse.getAccountFilePath()) {
				throw new SystemException(
						"API return null account download path");
			}
			return getAccountFilePathResponse.getAccountFilePath();
		} else {
			throw new ClientInternalException("We tried to get account file for "
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
