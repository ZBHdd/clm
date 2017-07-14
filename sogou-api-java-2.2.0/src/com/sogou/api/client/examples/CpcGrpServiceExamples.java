package com.sogou.api.client.examples;

import com.sogou.api.client.wrapper.CpcGrpServiceWrapper;
import com.sogou.api.sem.v1.cpcgrp.CpcPlanGrpId;
import com.sogou.api.sem.v1.cpcgrp.GetAllCpcGrpIdResponse;

public class CpcGrpServiceExamples {

	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		CpcGrpServiceWrapper cpcGrpService = new CpcGrpServiceWrapper();
		GetAllCpcGrpIdResponse response = cpcGrpService.getAllCpcGrpId();
		System.out.println(response.getCpcPlanGrpIds());
		for(CpcPlanGrpId c:response.getCpcPlanGrpIds()){
			System.out.println(c.getCpcGrpIds());
		}
	}

}
