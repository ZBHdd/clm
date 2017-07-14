package com.sogou.api.client.examples;

import com.sogou.api.client.wrapper.CpcIdeaServiceWrapper;
import com.sogou.api.sem.v1.cpcidea.CpcGrpIdeaId;
import com.sogou.api.sem.v1.cpcidea.GetCpcIdeaIdByCpcGrpIdRequest;
import com.sogou.api.sem.v1.cpcidea.GetCpcIdeaIdByCpcGrpIdResponse;

public class CpcIdeaServiceExamples {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		CpcIdeaServiceWrapper cpcIdeaService = new CpcIdeaServiceWrapper();

		GetCpcIdeaIdByCpcGrpIdRequest parameters = new GetCpcIdeaIdByCpcGrpIdRequest();
		parameters.getCpcGrpIds().add(585308324L);
		GetCpcIdeaIdByCpcGrpIdResponse reponse = cpcIdeaService
				.getCpcIdeaIdByCpcGrpId(parameters);
		System.out.println(reponse.getCpcGrpIdeaIds());
		for(CpcGrpIdeaId c:reponse.getCpcGrpIdeaIds()){
			System.out.println(c.getCpcIdeaIds());
		}
	}

}
