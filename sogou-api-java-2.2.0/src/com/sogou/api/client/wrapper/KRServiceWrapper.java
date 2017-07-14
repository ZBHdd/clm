package com.sogou.api.client.wrapper;

import java.util.concurrent.TimeUnit;

import javax.xml.ws.BindingProvider;

import com.sogou.api.client.core.ServiceFactory;
import com.sogou.api.client.core.SogouAdServiceFactory;
import com.sogou.api.client.exception.BusinessException;
import com.sogou.api.client.utils.ResHeaderUtils;
import com.sogou.api.sem.v1.common.ResHeader;
import com.sogou.api.sem.v1.kr.GetKRBySeedWordsRequest;
import com.sogou.api.sem.v1.kr.GetKRBySeedWordsResponse;
import com.sogou.api.sem.v1.kr.GetKRCustomRequest;
import com.sogou.api.sem.v1.kr.GetKRCustomResponse;
import com.sogou.api.sem.v1.kr.GetKRFileIdRequest;
import com.sogou.api.sem.v1.kr.GetKRFileIdResponse;
import com.sogou.api.sem.v1.kr.GetKRFilePathRequest;
import com.sogou.api.sem.v1.kr.GetKRFilePathResponse;
import com.sogou.api.sem.v1.kr.GetKRFileStatusRequest;
import com.sogou.api.sem.v1.kr.GetKRFileStatusResponse;
import com.sogou.api.sem.v1.kr.KRService;

public class KRServiceWrapper {

	private KRService krService;

	public KRServiceWrapper() {
		ServiceFactory sf = SogouAdServiceFactory.getInstance();
		krService = sf.getWebService(KRService.class);
	}

	public GetKRFileStatusResponse getKRFileStatus(
			GetKRFileStatusRequest parameters) {
		GetKRFileStatusResponse response = krService.getKRFileStatus(parameters);
		ResHeader rheader = ResHeaderUtils
				.getResHeader((BindingProvider) krService);
		if (rheader.getStatus() != 0) {
			throw new BusinessException(rheader, response);
		}
		return response;

	}

	public GetKRFilePathResponse getKRFilePath(GetKRFilePathRequest parameters) {
		GetKRFilePathResponse response = krService.getKRFilePath(parameters);
		ResHeader rheader = ResHeaderUtils
				.getResHeader((BindingProvider) krService);
		if (rheader.getStatus() != 0) {
			throw new BusinessException(rheader, response);
		}
		return response;

	}

	public GetKRCustomResponse getKRCustom(GetKRCustomRequest parameters) {
		GetKRCustomResponse response = krService.getKRCustom(parameters);
		ResHeader rheader = ResHeaderUtils
				.getResHeader((BindingProvider) krService);
		if (rheader.getStatus() != 0) {
			throw new BusinessException(rheader, response);
		}
		try {
			TimeUnit.SECONDS.sleep(1);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return response;

	}

	public GetKRBySeedWordsResponse getKRBySeedWords(
			GetKRBySeedWordsRequest parameters) {
		GetKRBySeedWordsResponse response = krService.getKRBySeedWords(parameters);
		ResHeader rheader = ResHeaderUtils
				.getResHeader((BindingProvider) krService);
		if (rheader.getStatus() != 0) {
			throw new BusinessException(rheader, response);
		}
		try {
			TimeUnit.SECONDS.sleep(1);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return response;

	}

	public GetKRFileIdResponse getKRFileId(GetKRFileIdRequest parameters) {
		GetKRFileIdResponse response = krService.getKRFileId(parameters);
		ResHeader rheader = ResHeaderUtils
				.getResHeader((BindingProvider) krService);
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
