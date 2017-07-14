package com.sogou.api.client.wrapper;

import javax.xml.ws.BindingProvider;

import com.sogou.api.client.core.ServiceFactory;
import com.sogou.api.client.core.SogouAdServiceFactory;
import com.sogou.api.client.exception.BusinessException;
import com.sogou.api.client.utils.ResHeaderUtils;
import com.sogou.api.sem.v1.accountdownload.AccountDownloadService;
import com.sogou.api.sem.v1.accountdownload.GetAccountFilePathRequest;
import com.sogou.api.sem.v1.accountdownload.GetAccountFilePathResponse;
import com.sogou.api.sem.v1.accountdownload.GetAccountFileRequest;
import com.sogou.api.sem.v1.accountdownload.GetAccountFileResponse;
import com.sogou.api.sem.v1.accountdownload.GetAccountFileStatusRequest;
import com.sogou.api.sem.v1.accountdownload.GetAccountFileStatusResponse;
import com.sogou.api.sem.v1.common.ResHeader;

public class AccountDownloadServiceWrapper {

	private AccountDownloadService accountDownloadService;

	public AccountDownloadServiceWrapper() {
		ServiceFactory sf = SogouAdServiceFactory.getInstance();
		accountDownloadService = sf.getWebService(AccountDownloadService.class);
	}

	public GetAccountFilePathResponse getAccountFilePath(
			GetAccountFilePathRequest parameters) {
		GetAccountFilePathResponse response = accountDownloadService
				.getAccountFilePath(parameters);

		ResHeader rheader = ResHeaderUtils
				.getResHeader((BindingProvider) accountDownloadService);
		if (rheader.getStatus() != 0) {
			throw new BusinessException(rheader, response);
		}
		return response;
	}

	public GetAccountFileResponse getAccountFile(
			GetAccountFileRequest parameters) {

		GetAccountFileResponse response = accountDownloadService
				.getAccountFile(parameters);
		ResHeader rheader = ResHeaderUtils
				.getResHeader((BindingProvider) accountDownloadService);
		if (rheader.getStatus() != 0) {
			throw new BusinessException(rheader, response);
		}
		return response;
	}

	public GetAccountFileStatusResponse getAccountFileStatus(
			GetAccountFileStatusRequest parameters) {
		GetAccountFileStatusResponse response = accountDownloadService
				.getAccountFileStatus(parameters);
		ResHeader rheader = ResHeaderUtils
				.getResHeader((BindingProvider) accountDownloadService);
		if (rheader.getStatus() != 0) {
			throw new BusinessException(rheader, response);
		}
		return response;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
