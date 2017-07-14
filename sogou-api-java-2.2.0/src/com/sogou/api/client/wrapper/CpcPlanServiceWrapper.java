package com.sogou.api.client.wrapper;

import javax.xml.ws.BindingProvider;

import com.sogou.api.client.core.ServiceFactory;
import com.sogou.api.client.core.SogouAdServiceFactory;
import com.sogou.api.client.exception.BusinessException;
import com.sogou.api.client.utils.ResHeaderUtils;
import com.sogou.api.sem.v1.common.ResHeader;
import com.sogou.api.sem.v1.cpcplan.AddCpcPlanRequest;
import com.sogou.api.sem.v1.cpcplan.AddCpcPlanResponse;
import com.sogou.api.sem.v1.cpcplan.CpcPlanService;
import com.sogou.api.sem.v1.cpcplan.DeleteCpcPlanRequest;
import com.sogou.api.sem.v1.cpcplan.DeleteCpcPlanResponse;
import com.sogou.api.sem.v1.cpcplan.GetAllCpcPlanIdRequest;
import com.sogou.api.sem.v1.cpcplan.GetAllCpcPlanIdResponse;
import com.sogou.api.sem.v1.cpcplan.GetAllCpcPlanRequest;
import com.sogou.api.sem.v1.cpcplan.GetAllCpcPlanResponse;
import com.sogou.api.sem.v1.cpcplan.GetCpcPlanByCpcPlanIdRequest;
import com.sogou.api.sem.v1.cpcplan.GetCpcPlanByCpcPlanIdResponse;
import com.sogou.api.sem.v1.cpcplan.UpdateCpcPlanRequest;
import com.sogou.api.sem.v1.cpcplan.UpdateCpcPlanResponse;

public class CpcPlanServiceWrapper {

	private CpcPlanService cpcPlanService;

	public CpcPlanServiceWrapper() {
		ServiceFactory sf = SogouAdServiceFactory.getInstance();
		cpcPlanService = sf.getWebService(CpcPlanService.class);
	}

	public DeleteCpcPlanResponse deleteCpcPlan(DeleteCpcPlanRequest parameters) {

		DeleteCpcPlanResponse response = cpcPlanService.deleteCpcPlan(parameters);
		
		
		ResHeader rheader = ResHeaderUtils
				.getResHeader((BindingProvider) cpcPlanService);
		if (rheader.getStatus() != 0) {
			throw new BusinessException(rheader, response);
		}
		return response;


	}

	public GetAllCpcPlanResponse getAllCpcPlan() {
		GetAllCpcPlanRequest parameters = new GetAllCpcPlanRequest();
		GetAllCpcPlanResponse response = cpcPlanService.getAllCpcPlan(parameters);
		ResHeader rheader = ResHeaderUtils
				.getResHeader((BindingProvider) cpcPlanService);
		if (rheader.getStatus() != 0) {
			throw new BusinessException(rheader, response);
		}
		return response;


	}

	public GetAllCpcPlanIdResponse getAllCpcPlanId(
			GetAllCpcPlanIdRequest parameters) {
		GetAllCpcPlanIdResponse response = cpcPlanService.getAllCpcPlanId(parameters);
		ResHeader rheader = ResHeaderUtils
				.getResHeader((BindingProvider) cpcPlanService);
		if (rheader.getStatus() != 0) {
			throw new BusinessException(rheader, response);
		}
		return response;


	}

	public UpdateCpcPlanResponse updateCpcPlan(UpdateCpcPlanRequest parameters) {
		UpdateCpcPlanResponse response = cpcPlanService.updateCpcPlan(parameters);
		ResHeader rheader = ResHeaderUtils
				.getResHeader((BindingProvider) cpcPlanService);
		if (rheader.getStatus() != 0) {
			throw new BusinessException(rheader, response);
		}
		return response;


	}

	public AddCpcPlanResponse addCpcPlan(AddCpcPlanRequest parameters) {
		AddCpcPlanResponse response = cpcPlanService.addCpcPlan(parameters);
		ResHeader rheader = ResHeaderUtils
				.getResHeader((BindingProvider) cpcPlanService);
		if (rheader.getStatus() != 0) {
			throw new BusinessException(rheader, response);
		}
		return response;

	}

	public GetCpcPlanByCpcPlanIdResponse getCpcPlanByCpcPlanId(
			GetCpcPlanByCpcPlanIdRequest parameters) {
		GetCpcPlanByCpcPlanIdResponse response = cpcPlanService.getCpcPlanByCpcPlanId(parameters);
		ResHeader rheader = ResHeaderUtils
				.getResHeader((BindingProvider) cpcPlanService);
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
