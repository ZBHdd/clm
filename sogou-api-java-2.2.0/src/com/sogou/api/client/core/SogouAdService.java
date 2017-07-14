package com.sogou.api.client.core;

import java.net.URL;

import javax.xml.namespace.QName;
import javax.xml.ws.Service;

public class SogouAdService extends Service {

	public SogouAdService(URL wsdlDocumentLocation, QName serviceName) {
		super(wsdlDocumentLocation, serviceName);
	}

}
