package com.sogou.api.client.examples;

import com.sogou.api.client.wrapper.CpcServiceWrapper;
import com.sogou.api.sem.v1.cpc.CpcGrpCpc;
import com.sogou.api.sem.v1.cpc.GetCpcByCpcGrpIdRequest;
import com.sogou.api.sem.v1.cpc.GetCpcByCpcGrpIdResponse;
import com.sogou.api.sem.v1.cpc.GetCpcByCpcIdRequest;
import com.sogou.api.sem.v1.cpc.GetCpcByCpcIdResponse;

public class CpcServiceExamples {

	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		CpcServiceWrapper cpcService = new CpcServiceWrapper();
		GetCpcByCpcGrpIdRequest parameters = new GetCpcByCpcGrpIdRequest();
		parameters.getCpcGrpIds().add(582679916L);
		GetCpcByCpcGrpIdResponse response = cpcService.getCpcByCpcGrpId(parameters);
		System.out.println(response.getCpcGrpCpcs());
		for(CpcGrpCpc c:response.getCpcGrpCpcs()){
			System.out.println(c.getCpcTypes());
		}
		/*GetCpcByCpcIdRequest parameters2 = new GetCpcByCpcIdRequest();
		parameters2.getCpcIds().add(20779109L);
		GetCpcByCpcIdResponse response2 = cpcService.getCpcByCpcId(parameters2);
		
		System.out.println(response2);*/
	}

}
