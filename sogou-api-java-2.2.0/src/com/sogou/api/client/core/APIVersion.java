/**
 * 
 */
package com.sogou.api.client.core;

import javax.xml.namespace.QName;

/**
 * @author zhangjuncheng
 *
 */
public interface APIVersion {

	public <T> String getServiceURL(Class<T> clz);
	
	public <T> QName getServiceQName(Class<T> clz);
	
	public String getVersion();
}
