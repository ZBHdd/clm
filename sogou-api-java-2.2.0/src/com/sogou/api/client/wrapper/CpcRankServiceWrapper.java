package com.sogou.api.client.wrapper;

import javax.xml.ws.BindingProvider;
import com.sogou.api.client.core.ServiceFactory;
import com.sogou.api.client.core.SogouAdServiceFactory;
import com.sogou.api.client.exception.BusinessException;
import com.sogou.api.client.utils.ResHeaderUtils;
import com.sogou.api.sem.v1.common.ResHeader;
import com.sogou.api.sem.v1.cpcrank.CpcRankService;
import com.sogou.api.sem.v1.cpcrank.GetCpcRankIdRequest;
import com.sogou.api.sem.v1.cpcrank.GetCpcRankIdResponse;
import com.sogou.api.sem.v1.cpcrank.GetCpcRankPathRequest;
import com.sogou.api.sem.v1.cpcrank.GetCpcRankPathResponse;
import com.sogou.api.sem.v1.cpcrank.GetCpcRankStatusRequest;
import com.sogou.api.sem.v1.cpcrank.GetCpcRankStatusResponse;


public class CpcRankServiceWrapper {
	private CpcRankService cpcRankService;

	public CpcRankServiceWrapper() {
		ServiceFactory sf = SogouAdServiceFactory.getInstance();
		cpcRankService = sf.getWebService(CpcRankService.class);
	}

	public GetCpcRankStatusResponse getReportState(
			GetCpcRankStatusRequest parameters) {
		GetCpcRankStatusResponse response = cpcRankService.getCpcRankStatus(parameters);
		ResHeader rheader = ResHeaderUtils
				.getResHeader((BindingProvider) cpcRankService);
		if (rheader.getStatus() != 0) {
			throw new BusinessException(rheader, response);
		}
		return response;
	}

	public GetCpcRankPathResponse getReportPath(GetCpcRankPathRequest parameters) {
		GetCpcRankPathResponse response = cpcRankService.getCpcRankPath(parameters);
		ResHeader rheader = ResHeaderUtils
				.getResHeader((BindingProvider) cpcRankService);
		if (rheader.getStatus() != 0) {
			throw new BusinessException(rheader, response);
		}
		return response;
	}

	public GetCpcRankIdResponse getReportId(GetCpcRankIdRequest parameters) {
		GetCpcRankIdResponse response = cpcRankService.getCpcRankId(parameters);
		ResHeader rheader = ResHeaderUtils
				.getResHeader((BindingProvider) cpcRankService);
		if (rheader.getStatus() != 0) {
			throw new BusinessException(rheader, response);
		}
		return response;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		CpcRankServiceWrapper cpcRankServiceWrapper = new CpcRankServiceWrapper();
		GetCpcRankIdRequest getCpcRankIdRequest = new GetCpcRankIdRequest();
		GetCpcRankIdResponse getCpcRankIdResponse =cpcRankServiceWrapper.getReportId(getCpcRankIdRequest);
		System.out.println(getCpcRankIdResponse);
	}
}
