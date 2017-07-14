/**
 * 
 */
package com.sogou.api.client.core;

/**
 * @author zhangjuncheng
 *
 */
public interface ServiceFactory {

	
	public <T> T  getWebService(Class<T> clz);
	
	
}
