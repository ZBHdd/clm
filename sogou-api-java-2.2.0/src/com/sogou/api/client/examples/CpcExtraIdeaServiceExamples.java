package com.sogou.api.client.examples;

import com.sogou.api.client.wrapper.CpcExtraIdeaServiceWrapper;
import com.sogou.api.sem.v1.cpcextraidea.GetExtraIdeaByCpcGrpIdRequest;
import com.sogou.api.sem.v1.cpcextraidea.GetExtraIdeaByCpcGrpIdResponse;

public class CpcExtraIdeaServiceExamples {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		getExIdeaByGrpId();
		
	}
	
	public static void getExIdeaByGrpId() {
		CpcExtraIdeaServiceWrapper cpcExIdeaService = new CpcExtraIdeaServiceWrapper();
		GetExtraIdeaByCpcGrpIdRequest parameters = new GetExtraIdeaByCpcGrpIdRequest();
		parameters.getCpcGrpIds().add(26620745L);
		GetExtraIdeaByCpcGrpIdResponse  response=cpcExIdeaService.getExtraIdeaByCpcGrpId(parameters);
		
		System.out.println(response);
	}
	
}
