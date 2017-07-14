package com.sogou.api.client.examples;

import com.sogou.api.client.wrapper.CpcPlanServiceWrapper;
import com.sogou.api.sem.v1.cpcplan.CpcPlanType;
import com.sogou.api.sem.v1.cpcplan.GetAllCpcPlanResponse;

public class CpcPlanServiceExamples {

	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		CpcPlanServiceWrapper  cpcPlanService = new CpcPlanServiceWrapper();
		
		GetAllCpcPlanResponse response = cpcPlanService.getAllCpcPlan();
		System.out.println(response.getCpcPlanTypes().get(0).toString());
		for(CpcPlanType c:response.getCpcPlanTypes()){
			System.out.println(c);
		}

	}

}
