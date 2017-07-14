package com.sogou.api.client.wrapper;

import javax.xml.ws.BindingProvider;

import com.sogou.api.client.core.ServiceFactory;
import com.sogou.api.client.core.SogouAdServiceFactory;
import com.sogou.api.client.exception.BusinessException;
import com.sogou.api.client.utils.ResHeaderUtils;
import com.sogou.api.sem.v1.common.ResHeader;
import com.sogou.api.sem.v1.cpcidea.AddCpcIdeaRequest;
import com.sogou.api.sem.v1.cpcidea.AddCpcIdeaResponse;
import com.sogou.api.sem.v1.cpcidea.CpcIdeaService;
import com.sogou.api.sem.v1.cpcidea.DeleteCpcIdeaRequest;
import com.sogou.api.sem.v1.cpcidea.DeleteCpcIdeaResponse;
import com.sogou.api.sem.v1.cpcidea.GetCpcIdeaByCpcGrpIdRequest;
import com.sogou.api.sem.v1.cpcidea.GetCpcIdeaByCpcGrpIdResponse;
import com.sogou.api.sem.v1.cpcidea.GetCpcIdeaByCpcIdeaIdRequest;
import com.sogou.api.sem.v1.cpcidea.GetCpcIdeaByCpcIdeaIdResponse;
import com.sogou.api.sem.v1.cpcidea.GetCpcIdeaIdByCpcGrpIdRequest;
import com.sogou.api.sem.v1.cpcidea.GetCpcIdeaIdByCpcGrpIdResponse;
import com.sogou.api.sem.v1.cpcidea.UpdateCpcIdeaRequest;
import com.sogou.api.sem.v1.cpcidea.UpdateCpcIdeaResponse;

public class CpcIdeaServiceWrapper {

	private CpcIdeaService cpcIdeaService;

	public CpcIdeaServiceWrapper() {
		ServiceFactory sf = SogouAdServiceFactory.getInstance();
		cpcIdeaService = sf.getWebService(CpcIdeaService.class);
	}

	public GetCpcIdeaByCpcIdeaIdResponse getCpcIdeaByCpcIdeaId(
			GetCpcIdeaByCpcIdeaIdRequest parameters) {
		GetCpcIdeaByCpcIdeaIdResponse response = cpcIdeaService.getCpcIdeaByCpcIdeaId(parameters);
		
		ResHeader rheader = ResHeaderUtils
				.getResHeader((BindingProvider) cpcIdeaService);
		if (rheader.getStatus() != 0) {
			throw new BusinessException(rheader, response);
		}
		return response;

	}

	public UpdateCpcIdeaResponse updateCpcIdea(UpdateCpcIdeaRequest parameters) {
		UpdateCpcIdeaResponse response = cpcIdeaService.updateCpcIdea(parameters);
		ResHeader rheader = ResHeaderUtils
				.getResHeader((BindingProvider) cpcIdeaService);
		if (rheader.getStatus() != 0) {
			throw new BusinessException(rheader, response);
		}
		return response;

	}

	public AddCpcIdeaResponse addCpcIdea(AddCpcIdeaRequest parameters) {
		AddCpcIdeaResponse response = cpcIdeaService.addCpcIdea(parameters);
		ResHeader rheader = ResHeaderUtils
				.getResHeader((BindingProvider) cpcIdeaService);
		if (rheader.getStatus() != 0) {
			throw new BusinessException(rheader, response);
		}
		return response;

	}

	public GetCpcIdeaByCpcGrpIdResponse getCpcIdeaByCpcGrpId(
			GetCpcIdeaByCpcGrpIdRequest parameters) {
		GetCpcIdeaByCpcGrpIdResponse response = cpcIdeaService.getCpcIdeaByCpcGrpId(parameters);
		ResHeader rheader = ResHeaderUtils
				.getResHeader((BindingProvider) cpcIdeaService);
		if (rheader.getStatus() != 0) {
			throw new BusinessException(rheader, response);
		}
		return response;

	}

	public GetCpcIdeaIdByCpcGrpIdResponse getCpcIdeaIdByCpcGrpId(
			GetCpcIdeaIdByCpcGrpIdRequest parameters) {
		GetCpcIdeaIdByCpcGrpIdResponse response = cpcIdeaService.getCpcIdeaIdByCpcGrpId(parameters);
		ResHeader rheader = ResHeaderUtils
				.getResHeader((BindingProvider) cpcIdeaService);
		if (rheader.getStatus() != 0) {
			throw new BusinessException(rheader, response);
		}
		return response;

	}

	public DeleteCpcIdeaResponse deleteCpcIdea(DeleteCpcIdeaRequest parameters) {
		DeleteCpcIdeaResponse response = cpcIdeaService.deleteCpcIdea(parameters);
		ResHeader rheader = ResHeaderUtils
				.getResHeader((BindingProvider) cpcIdeaService);
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
