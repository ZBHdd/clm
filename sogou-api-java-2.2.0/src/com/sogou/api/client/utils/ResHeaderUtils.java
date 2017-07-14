package com.sogou.api.client.utils;

import java.util.List;

import javax.xml.ws.BindingProvider;

import org.apache.cxf.binding.soap.SoapHeader;
import org.apache.cxf.headers.Header;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.sogou.api.client.exception.ClientInternalException;
import com.sogou.api.sem.v1.common.Failure;
import com.sogou.api.sem.v1.common.ResHeader;

public abstract class ResHeaderUtils {

	public static ResHeader getResHeader(BindingProvider proxy) {
		List<SoapHeader> headers = (List<SoapHeader>) proxy
				.getResponseContext().get(Header.HEADER_LIST);
		if (headers == null || headers.size() == 0)
			throw new ClientInternalException(
					"The Response header does not exist!");

		Element obj = (Element) headers.get(0).getObject();
		ResHeader resH = new ResHeader();

		resH.setStatus(Integer.parseInt(extractText(obj, "status")));
		resH.setDesc(extractText(obj, "desc"));
		if (resH.getStatus() != 3) {
			String oprs = extractText(obj, "oprs");
			resH.setOprs(null != oprs ? Integer.parseInt(oprs) : null);
			String oprtime = extractText(obj, "oprtime");
			resH.setOprtime(null != oprtime ? Integer.parseInt(oprtime) : null);
			String quota = extractText(obj, "quota");
			resH.setQuota(null != quota ? Integer.parseInt(quota) : null);
			String rquota = extractText(obj, "rquota");
			resH.setRquota(null != rquota ? Integer.parseInt(rquota) : null);
		}

		NodeList nodelist = obj.getElementsByTagNameNS("*", "failures");
		List<Failure> failures = resH.getFailures();
		if (nodelist != null && nodelist.getLength() > 0) {
			for (int i = 0; i < nodelist.getLength(); ++i) {
				Element node = (Element) nodelist.item(i);
				failures.add(parseFailures(node));
			}
		}

		return resH;
	}

	private static String extractText(Element obj, String localName) {
		NodeList nodelist = obj.getElementsByTagNameNS("*", localName);
		if (nodelist != null && nodelist.getLength() > 0) {
			Element node = (Element) nodelist.item(0);
			if (node != null) {
				String str = node.getTextContent();
				if (str != null) {
					str = str.trim();
					if (str.length() > 0)
						return str;
				}
			}
		}
		return null;
	}

	private static Failure parseFailures(Element obj) {
		Failure fail = new Failure();
		int code = Integer.parseInt(extractText(obj, "code"));
		fail.setCode(code);
		String message = extractText(obj, "message");
		fail.setMessage(message);
		String position = extractText(obj, "position");
		fail.setPosition(position);
		String content = extractText(obj, "content");
		fail.setContent(content);
		return fail;
	}

}
