package com.sogou.api.client.wrapper;

import javax.xml.ws.BindingProvider;

import com.sogou.api.client.core.ServiceFactory;
import com.sogou.api.client.core.SogouAdServiceFactory;
import com.sogou.api.client.exception.BusinessException;
import com.sogou.api.client.utils.ResHeaderUtils;
import com.sogou.api.sem.v1.common.ResHeader;
import com.sogou.api.sem.v1.cpcgrp.AddCpcGrpRequest;
import com.sogou.api.sem.v1.cpcgrp.AddCpcGrpResponse;
import com.sogou.api.sem.v1.cpcgrp.CpcGrpService;
import com.sogou.api.sem.v1.cpcgrp.DeleteCpcGrpRequest;
import com.sogou.api.sem.v1.cpcgrp.DeleteCpcGrpResponse;
import com.sogou.api.sem.v1.cpcgrp.GetAllCpcGrpIdRequest;
import com.sogou.api.sem.v1.cpcgrp.GetAllCpcGrpIdResponse;
import com.sogou.api.sem.v1.cpcgrp.GetCpcGrpByCpcGrpIdRequest;
import com.sogou.api.sem.v1.cpcgrp.GetCpcGrpByCpcGrpIdResponse;
import com.sogou.api.sem.v1.cpcgrp.GetCpcGrpByCpcPlanIdRequest;
import com.sogou.api.sem.v1.cpcgrp.GetCpcGrpByCpcPlanIdResponse;
import com.sogou.api.sem.v1.cpcgrp.GetCpcGrpIdByCpcPlanIdRequest;
import com.sogou.api.sem.v1.cpcgrp.GetCpcGrpIdByCpcPlanIdResponse;
import com.sogou.api.sem.v1.cpcgrp.UpdateCpcGrpRequest;
import com.sogou.api.sem.v1.cpcgrp.UpdateCpcGrpResponse;

public class CpcGrpServiceWrapper {

	private CpcGrpService cpcGrpService;

	public CpcGrpServiceWrapper() {
		ServiceFactory sf = SogouAdServiceFactory.getInstance();
		cpcGrpService = sf.getWebService(CpcGrpService.class);
	}

	public GetCpcGrpByCpcPlanIdResponse getCpcGrpByCpcPlanId(
			GetCpcGrpByCpcPlanIdRequest parameters) {
		GetCpcGrpByCpcPlanIdResponse response = cpcGrpService
				.getCpcGrpByCpcPlanId(parameters);

		ResHeader rheader = ResHeaderUtils
				.getResHeader((BindingProvider) cpcGrpService);
		if (rheader.getStatus() != 0) {
			throw new BusinessException(rheader, response);
		}
		return response;
	}

	public AddCpcGrpResponse addCpcGrp(AddCpcGrpRequest parameters) {
		AddCpcGrpResponse response = cpcGrpService.addCpcGrp(parameters);
		ResHeader rheader = ResHeaderUtils
				.getResHeader((BindingProvider) cpcGrpService);
		if (rheader.getStatus() != 0) {
			throw new BusinessException(rheader, response);
		}
		return response;

	}

	public UpdateCpcGrpResponse updateCpcGrp(UpdateCpcGrpRequest parameters) {
		UpdateCpcGrpResponse response = cpcGrpService.updateCpcGrp(parameters);
		ResHeader rheader = ResHeaderUtils
				.getResHeader((BindingProvider) cpcGrpService);
		if (rheader.getStatus() != 0) {
			throw new BusinessException(rheader, response);
		}
		return response;

	}

	public GetAllCpcGrpIdResponse getAllCpcGrpId() {
		GetAllCpcGrpIdRequest parameters = new GetAllCpcGrpIdRequest();
		GetAllCpcGrpIdResponse response = cpcGrpService
				.getAllCpcGrpId(parameters);
		ResHeader rheader = ResHeaderUtils
				.getResHeader((BindingProvider) cpcGrpService);
		if (rheader.getStatus() != 0) {
			throw new BusinessException(rheader, response);
		}
		return response;

	}

	public GetCpcGrpByCpcGrpIdResponse getCpcGrpByCpcGrpId(
			GetCpcGrpByCpcGrpIdRequest parameters) {
		GetCpcGrpByCpcGrpIdResponse response = cpcGrpService
				.getCpcGrpByCpcGrpId(parameters);
		ResHeader rheader = ResHeaderUtils
				.getResHeader((BindingProvider) cpcGrpService);
		if (rheader.getStatus() != 0) {
			throw new BusinessException(rheader, response);
		}
		return response;
	}

	public DeleteCpcGrpResponse deleteCpcGrp(DeleteCpcGrpRequest parameters) {
		ResHeader rheader = ResHeaderUtils
				.getResHeader((BindingProvider) cpcGrpService);
		DeleteCpcGrpResponse response = cpcGrpService.deleteCpcGrp(parameters);
		if (rheader.getStatus() != 0) {
			throw new BusinessException(rheader, response);
		}
		return response;

	}

	public GetCpcGrpIdByCpcPlanIdResponse getCpcGrpIdByCpcPlanId(
			GetCpcGrpIdByCpcPlanIdRequest parameters) {
		GetCpcGrpIdByCpcPlanIdResponse response = cpcGrpService
				.getCpcGrpIdByCpcPlanId(parameters);
		ResHeader rheader = ResHeaderUtils
				.getResHeader((BindingProvider) cpcGrpService);
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
