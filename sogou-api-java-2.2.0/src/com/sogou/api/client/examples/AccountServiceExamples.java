package com.sogou.api.client.examples;

import com.sogou.api.client.wrapper.AccountServiceWrapper;
import com.sogou.api.sem.v1.account.GetAccountInfoResponse;

public class AccountServiceExamples {

	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		AccountServiceWrapper accountService = new AccountServiceWrapper();
		GetAccountInfoResponse response = accountService.getAccountInfo();
		System.out.println(response.getAccountInfoType().getTotalCost());

	}

}
