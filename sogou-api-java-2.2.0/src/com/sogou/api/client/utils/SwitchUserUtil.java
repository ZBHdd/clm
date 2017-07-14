/**
 * 
 */
package com.sogou.api.client.utils;

import com.sogou.api.client.core.SogouAdServiceFactory;
import com.sogou.api.client.wrapper.AccountServiceWrapper;
import com.sogou.api.sem.v1.account.GetAccountInfoResponse;
import com.sogou.api.sem.v1.common.AuthHeader;

/**
 * 调用API的过程中如果需要切换用户，调用此类
 * 
 */
public abstract class SwitchUserUtil {

	/**
	 * if you want to change user dynamically, use this method
	 * not thread-safe, you cannnot invoke api and switchuser at the same time
	 * using multi-thread
	 * 
	 * @param agentUsername
	 * @param agentPassword
	 * @param username
	 * @param password
	 * @param token
	 */
	public static void switchUser(String agentUsername, String agentPassword,
			String username, String password, String token) {
		SogouAdServiceFactory factory = (SogouAdServiceFactory) SogouAdServiceFactory
				.getInstance();
		AuthHeader authHeader = new AuthHeader();
		authHeader.setAgentusername(agentUsername);
		if (null != agentPassword)
			authHeader.setAgentpassword(MD5.generateMD5(agentPassword));
		authHeader.setUsername(username);
		if (null != password)
			authHeader.setPassword(MD5.generateMD5(password));
		authHeader.setToken(token);
		factory.switchUser(authHeader);
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		AccountServiceWrapper accountService = new AccountServiceWrapper();
		GetAccountInfoResponse response = accountService.getAccountInfo();
		System.out.println(response);
		response = accountService.getAccountInfo();
		System.out.println(response);
		
		SwitchUserUtil.switchUser("", "", "", "", "");
		//after switch user, you should new Service again
		accountService = new AccountServiceWrapper();
		GetAccountInfoResponse response2 = accountService.getAccountInfo();
		System.out.println(response2);

	}

}
