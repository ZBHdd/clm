/**
 * 
 */
package com.sogou.api.client.core;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import javax.xml.bind.JAXBException;
import javax.xml.namespace.QName;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.Service;

import org.apache.cxf.endpoint.Client;
import org.apache.cxf.frontend.ClientProxy;
import org.apache.cxf.headers.Header;
import org.apache.cxf.interceptor.LoggingInInterceptor;
import org.apache.cxf.interceptor.LoggingOutInterceptor;
import org.apache.cxf.jaxb.JAXBDataBinding;
import org.apache.cxf.transport.http.HTTPConduit;
import org.apache.cxf.transports.http.configuration.HTTPClientPolicy;

import com.sogou.api.client.exception.ClientInternalException;
import com.sogou.api.client.utils.MD5;
import com.sogou.api.sem.v1.common.AuthHeader;

/**
 * @author zhangjuncheng
 * 
 */
public class SogouAdServiceFactory implements ServiceFactory {

	private static ServiceFactory INSTANCE = new SogouAdServiceFactory();

	protected String serviceURL = "";
	protected String agentUsername = "";
	protected String agentPassword = "";
	protected String username = "";
	protected String password = "";
	protected String token = "";
	protected AuthHeader authHeader = null;
	protected boolean enableInLog = false;
	protected boolean enableOutLog = false;

	protected Integer connectTimeoutMills = 10000;
	protected Integer readTimeoutMills = 1800000;

	private List<Header> headers = null;

	private SogouAdServiceFactory() {
		super();
		ResourceBundle rb = ResourceBundle.getBundle("sogou-api");
		serviceURL = rb.getString("serviceURL");
//		agentUsername = rb.getString("agentUsername");
//		agentPassword = rb.getString("agentPassword");
		username = rb.getString("username");
		password = rb.getString("password");
		token = rb.getString("token");
		if (isEmpty(serviceURL) || isEmpty(username) || isEmpty(token)) {
			throw new IllegalArgumentException(
					"client user information is illegal" + ", agentUsername="
							+ agentUsername + ", agentPassword="
							+ agentPassword + ", username=" + username
							+ ", password=" + password + ", token=" + token);
		}
		String logIn = rb.getString("enableInLog");
		String logOut = rb.getString("enableOutLog");
		enableInLog = Boolean.valueOf(logIn);
		enableOutLog = Boolean.valueOf(logOut);
		connectTimeoutMills = Integer.valueOf(rb.getString("connectTimeoutMills"));
		readTimeoutMills  = Integer.valueOf(rb.getString("readTimeoutMills"));
		generateHeader();
	}

	
	private boolean isEmpty(String str) {
		return null == str || str.trim().isEmpty();
	}

	public static ServiceFactory getInstance() {
		return INSTANCE;
	}

	@Override
	public <T> T getWebService(Class<T> clz) {

		T proxy = getWebServiceInner(clz);

		Map<String, Object> reqCtxt = ((BindingProvider) proxy)
				.getRequestContext();
		reqCtxt.put(Header.HEADER_LIST, headers);
		reqCtxt.put("com.sun.xml.internal.ws.connect.timeout",
				connectTimeoutMills);
		reqCtxt.put("com.sun.xml.internal.ws.request.timeout", readTimeoutMills);

		Client client = ClientProxy.getClient(proxy);
		HTTPConduit http = (HTTPConduit) client.getConduit();
		HTTPClientPolicy httpClientPolicy = new HTTPClientPolicy();
		httpClientPolicy.setConnectionTimeout(connectTimeoutMills);
		httpClientPolicy.setAllowChunking(false);
		httpClientPolicy.setReceiveTimeout(readTimeoutMills);

		if (enableInLog) {
			client.getInInterceptors().add(new LoggingInInterceptor());
		}
		if (enableOutLog) {
			client.getOutInterceptors().add(new LoggingOutInterceptor());
		}
		http.setClient(httpClientPolicy);

		return proxy;

	}

	private <T> T getWebServiceInner(Class<T> clz) {
		if (clz == null)
			throw new IllegalArgumentException("clz is null!");
		Service service;
		try {
			String addr = VersionOne.VERSION.getServiceURL(clz);
			QName qName = VersionOne.VERSION.getServiceQName(clz);
			if (addr == null || qName == null)
				throw new ClientInternalException("Service ["
						+ clz.getSimpleName() + "] not supported by version - "
						+ VersionOne.VERSION.getVersion());
			service = new SogouAdService(new URL(serviceURL + addr), qName);
			return service.getPort(clz);
		} catch (MalformedURLException e) {
			throw new ClientInternalException(e);
		}

	}

	private void generateHeader() {
		try {
			authHeader = new AuthHeader();
//			authHeader.setAgentusername(agentUsername);
//			authHeader.setAgentpassword(MD5.generateMD5(agentPassword));
			authHeader.setUsername(username);
			authHeader.setPassword(MD5.generateMD5(password));
			authHeader.setToken(token);

			Header header = new Header(new QName(VersionOne.NAMESPACE_COMMON,
					"AuthHeader"), authHeader, new JAXBDataBinding(
					AuthHeader.class));
			headers = new ArrayList<Header>();
			headers.add(header);
		} catch (JAXBException e) {
			throw new ClientInternalException("Failed to genarate AuthHeader!");
		}
	}
	
	/**
	 * 代理商在代管用户的过程中可能要切换代管用户，do it here
	 * @param authHeader
	 */
	public void switchUser(AuthHeader authHeader) {
		Header header;
		try {
			header = new Header(new QName(VersionOne.NAMESPACE_COMMON,
					"AuthHeader"), authHeader, new JAXBDataBinding(
					AuthHeader.class));
			headers = new ArrayList<Header>();
			headers.add(header);
		} catch (JAXBException e) {
			throw new ClientInternalException("Failed to genarate AuthHeader!");
		}
	}
}
