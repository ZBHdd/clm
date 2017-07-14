package com.sogou.api.client.wrapper;

import javax.xml.ws.BindingProvider;

import com.sogou.api.client.core.ServiceFactory;
import com.sogou.api.client.core.SogouAdServiceFactory;
import com.sogou.api.client.exception.BusinessException;
import com.sogou.api.client.utils.ResHeaderUtils;
import com.sogou.api.sem.v1.common.ResHeader;
import com.sogou.api.sem.v1.cpc.AddCpcRequest;
import com.sogou.api.sem.v1.cpc.AddCpcResponse;
import com.sogou.api.sem.v1.cpc.CpcService;
import com.sogou.api.sem.v1.cpc.DeleteCpcRequest;
import com.sogou.api.sem.v1.cpc.DeleteCpcResponse;
import com.sogou.api.sem.v1.cpc.GetCpcByCpcGrpIdRequest;
import com.sogou.api.sem.v1.cpc.GetCpcByCpcGrpIdResponse;
import com.sogou.api.sem.v1.cpc.GetCpcByCpcIdRequest;
import com.sogou.api.sem.v1.cpc.GetCpcByCpcIdResponse;
import com.sogou.api.sem.v1.cpc.GetCpcIdByCpcGrpIdRequest;
import com.sogou.api.sem.v1.cpc.GetCpcIdByCpcGrpIdResponse;
import com.sogou.api.sem.v1.cpc.UpdateCpcRequest;
import com.sogou.api.sem.v1.cpc.UpdateCpcResponse;

public class CpcServiceWrapper {

	private CpcService cpcService;

	public CpcServiceWrapper() {
		ServiceFactory sf = SogouAdServiceFactory.getInstance();
		cpcService = sf.getWebService(CpcService.class);
	}

	public UpdateCpcResponse updateCpc(UpdateCpcRequest parameters) {
		UpdateCpcResponse response = cpcService.updateCpc(parameters);
		
		ResHeader rheader = ResHeaderUtils
				.getResHeader((BindingProvider) cpcService);
		if (rheader.getStatus() != 0) {
			throw new BusinessException(rheader, response);
		}
		return response;

	}

	public GetCpcByCpcGrpIdResponse getCpcByCpcGrpId(
			GetCpcByCpcGrpIdRequest parameters) {
		GetCpcByCpcGrpIdResponse response = cpcService.getCpcByCpcGrpId(parameters);
		ResHeader rheader = ResHeaderUtils
				.getResHeader((BindingProvider) cpcService);
		if (rheader.getStatus() != 0) {
			throw new BusinessException(rheader, response);
		}
		
		return response;
	}

	public DeleteCpcResponse deleteCpc(DeleteCpcRequest parameters) {
		DeleteCpcResponse response = cpcService.deleteCpc(parameters);
		ResHeader rheader = ResHeaderUtils
				.getResHeader((BindingProvider) cpcService);
		if (rheader.getStatus() != 0) {
			throw new BusinessException(rheader, response);
		}
		return response;

	}

	public GetCpcIdByCpcGrpIdResponse getCpcIdByCpcGrpId(
			GetCpcIdByCpcGrpIdRequest parameters) {
		GetCpcIdByCpcGrpIdResponse response = cpcService.getCpcIdByCpcGrpId(parameters);
		ResHeader rheader = ResHeaderUtils
				.getResHeader((BindingProvider) cpcService);
		if (rheader.getStatus() != 0) {
			throw new BusinessException(rheader, response);
		}
		return response;

	}

	public GetCpcByCpcIdResponse getCpcByCpcId(GetCpcByCpcIdRequest parameters) {
		GetCpcByCpcIdResponse response = cpcService.getCpcByCpcId(parameters);
		ResHeader rheader = ResHeaderUtils
				.getResHeader((BindingProvider) cpcService);
		if (rheader.getStatus() != 0) {
			throw new BusinessException(rheader, response);
		}
		return response;

	}

	public AddCpcResponse addCpc(AddCpcRequest parameters) {
		AddCpcResponse response = cpcService.addCpc(parameters);
		ResHeader rheader = ResHeaderUtils
				.getResHeader((BindingProvider) cpcService);
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
