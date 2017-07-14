package com.sogou.api.client.wrapper;

import javax.jws.WebParam;
import javax.xml.ws.BindingProvider;

import com.sogou.api.client.core.ServiceFactory;
import com.sogou.api.client.core.SogouAdServiceFactory;
import com.sogou.api.client.exception.BusinessException;
import com.sogou.api.client.utils.ResHeaderUtils;
import com.sogou.api.sem.v1.common.ResHeader;
import com.sogou.api.sem.v1.cpcextraidea.AddExtraIdeaRequest;
import com.sogou.api.sem.v1.cpcextraidea.AddExtraIdeaResponse;
import com.sogou.api.sem.v1.cpcextraidea.CpcExtraIdeaService;
import com.sogou.api.sem.v1.cpcextraidea.DeleteExtraIdeaRequest;
import com.sogou.api.sem.v1.cpcextraidea.DeleteExtraIdeaResponse;
import com.sogou.api.sem.v1.cpcextraidea.GetExtraIdeaByCpcGrpIdRequest;
import com.sogou.api.sem.v1.cpcextraidea.GetExtraIdeaByCpcGrpIdResponse;
import com.sogou.api.sem.v1.cpcextraidea.GetExtraIdeaByExIdeaIdRequest;
import com.sogou.api.sem.v1.cpcextraidea.GetExtraIdeaByExIdeaIdResponse;
import com.sogou.api.sem.v1.cpcextraidea.UpdateExtraIdeaRequest;
import com.sogou.api.sem.v1.cpcextraidea.UpdateExtraIdeaResponse;


public class CpcExtraIdeaServiceWrapper {

	private CpcExtraIdeaService cpcExIdeaService;

	public CpcExtraIdeaServiceWrapper() {
		ServiceFactory sf = SogouAdServiceFactory.getInstance();
		cpcExIdeaService = sf.getWebService(CpcExtraIdeaService.class);
	}

	 public GetExtraIdeaByExIdeaIdResponse getExtraIdeaByExIdeaId(
		        GetExtraIdeaByExIdeaIdRequest parameters) {
		GetExtraIdeaByExIdeaIdResponse response = cpcExIdeaService.getExtraIdeaByExIdeaId(parameters);
		
		ResHeader rheader = ResHeaderUtils.getResHeader((BindingProvider) cpcExIdeaService);
		if (rheader.getStatus() != 0) {
			throw new BusinessException(rheader, response);
		}
		//ResHeader [desc=failure, failures=[Failure [code=1030006, message=extra idea id not exists, position=_params.exIdeaIds[0].extraIdeaId, content=null]], oprs=2, oprtime=3, quota=0, rquota=289984, status=2]
		//"ResHeader [desc=failure, failures=[Failure [code=1000012, message=The group id does not exist, position=_params.cpcGrpIds[0], content=null]], oprs=0, oprtime=1, quota=0, rquota=289984, status=2]"
		return response;

	}

	 public UpdateExtraIdeaResponse updateExtraIdea(UpdateExtraIdeaRequest parameters) {
		UpdateExtraIdeaResponse response = cpcExIdeaService.updateExtraIdea(parameters);
		ResHeader rheader = ResHeaderUtils
				.getResHeader((BindingProvider) cpcExIdeaService);
		if (rheader.getStatus() != 0) {
			throw new BusinessException(rheader, response);
		}
		return response;

	}

	 public AddExtraIdeaResponse addExtraIdea( AddExtraIdeaRequest parameters){
		AddExtraIdeaResponse response = cpcExIdeaService.addExtraIdea(parameters);
		ResHeader rheader = ResHeaderUtils
				.getResHeader((BindingProvider) cpcExIdeaService);
		if (rheader.getStatus() != 0) {
			throw new BusinessException(rheader, response);
		}
		//ResHeader [desc=success, failures=[], oprs=2, oprtime=7, quota=2, rquota=289982, status=0]
		//ResHeader [desc=failure, failures=[Failure [code=1030016, message=The extra idea exceed max size in grp, position=_params.cpcExIdeaTypes[0].extraIdea, content=null], Failure [code=1030016, message=The extra idea exceed max size in grp, position=_params.cpcExIdeaTypes[1].extraIdea, content=null]], oprs=2, oprtime=3, quota=0, rquota=289982, status=2]
		return response;

	}


	 public GetExtraIdeaByCpcGrpIdResponse getExtraIdeaByCpcGrpId(GetExtraIdeaByCpcGrpIdRequest parameters) {
		GetExtraIdeaByCpcGrpIdResponse response = cpcExIdeaService.getExtraIdeaByCpcGrpId(parameters);
		ResHeader rheader = ResHeaderUtils
				.getResHeader((BindingProvider) cpcExIdeaService);
		if (rheader.getStatus() != 0) {
			throw new BusinessException(rheader, response);
		}
		return response;

	}

	 public DeleteExtraIdeaResponse deleteExtraIdea( DeleteExtraIdeaRequest parameters) {
		DeleteExtraIdeaResponse response = cpcExIdeaService.deleteExtraIdea(parameters);
		ResHeader rheader = ResHeaderUtils
				.getResHeader((BindingProvider) cpcExIdeaService);
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
