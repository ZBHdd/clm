package com.sogou.api.client.wrapper;

import javax.xml.ws.BindingProvider;

import com.sogou.api.client.core.ServiceFactory;
import com.sogou.api.client.core.SogouAdServiceFactory;
import com.sogou.api.client.exception.BusinessException;
import com.sogou.api.client.utils.ResHeaderUtils;
import com.sogou.api.sem.v1.account.AccountService;
import com.sogou.api.sem.v1.account.GetAccountInfoRequest;
import com.sogou.api.sem.v1.account.GetAccountInfoResponse;
import com.sogou.api.sem.v1.account.GetChangedCpcGrpIdRequest;
import com.sogou.api.sem.v1.account.GetChangedCpcGrpIdResponse;
import com.sogou.api.sem.v1.account.GetChangedCpcMaterialIdRequest;
import com.sogou.api.sem.v1.account.GetChangedCpcMaterialIdResponse;
import com.sogou.api.sem.v1.account.GetChangedCpcPlanIdRequest;
import com.sogou.api.sem.v1.account.GetChangedCpcPlanIdResponse;
import com.sogou.api.sem.v1.account.GetChangedIdRequest;
import com.sogou.api.sem.v1.account.GetChangedIdResponse;
import com.sogou.api.sem.v1.account.UpdateAccountInfoRequest;
import com.sogou.api.sem.v1.account.UpdateAccountInfoResponse;
import com.sogou.api.sem.v1.common.ResHeader;

public class AccountServiceWrapper {

	private AccountService accountService;

	public AccountServiceWrapper() {
		ServiceFactory sf = SogouAdServiceFactory.getInstance();
		accountService = sf.getWebService(AccountService.class);
	}

	public GetAccountInfoResponse getAccountInfo() {
		GetAccountInfoRequest parameters = new GetAccountInfoRequest();
		GetAccountInfoResponse response = accountService
				.getAccountInfo(parameters);
		ResHeader rheader = ResHeaderUtils
				.getResHeader((BindingProvider) accountService);
		if (rheader.getStatus() != 0) {
			throw new BusinessException(rheader, response);
		}
		System.err.println(rheader);
		return response;

	}

	public GetChangedCpcPlanIdResponse getChangedCpcPlanId(
			GetChangedCpcPlanIdRequest parameters) {
		GetChangedCpcPlanIdResponse response = accountService
				.getChangedCpcPlanId(parameters);
		ResHeader rheader = ResHeaderUtils
				.getResHeader((BindingProvider) accountService);
		if (rheader.getStatus() != 0) {
			throw new BusinessException(rheader, response);
		}
		return response;

	}

	public GetChangedCpcGrpIdResponse getChangedCpcGrpId(
			GetChangedCpcGrpIdRequest parameters) {
		GetChangedCpcGrpIdResponse response = accountService
				.getChangedCpcGrpId(parameters);
		ResHeader rheader = ResHeaderUtils
				.getResHeader((BindingProvider) accountService);
		if (rheader.getStatus() != 0) {
			throw new BusinessException(rheader, response);
		}
		return response;

	}

	public GetChangedIdResponse getChangedId(GetChangedIdRequest parameters) {
		GetChangedIdResponse response = accountService.getChangedId(parameters);
		ResHeader rheader = ResHeaderUtils
				.getResHeader((BindingProvider) accountService);
		if (rheader.getStatus() != 0) {
			throw new BusinessException(rheader, response);
		}
		return response;

	}

	public GetChangedCpcMaterialIdResponse getChangedCpcMaterialId(
			GetChangedCpcMaterialIdRequest parameters) {
		GetChangedCpcMaterialIdResponse response = accountService
				.getChangedCpcMaterialId(parameters);
		ResHeader rheader = ResHeaderUtils
				.getResHeader((BindingProvider) accountService);
		if (rheader.getStatus() != 0) {
			throw new BusinessException(rheader, response);
		}
		return response;

	}

	public UpdateAccountInfoResponse updateAccountInfo(
			UpdateAccountInfoRequest parameters) {
		UpdateAccountInfoResponse response = accountService
				.updateAccountInfo(parameters);
		ResHeader rheader = ResHeaderUtils
				.getResHeader((BindingProvider) accountService);
		if (rheader.getStatus() != 0) {
			throw new BusinessException(rheader, response);
		}
		return response;

	}

	public static void main(String args[]) {
		AccountServiceWrapper wrapper = new AccountServiceWrapper();
		GetAccountInfoResponse response = wrapper.getAccountInfo();
		System.err.println(response);
	}

}
