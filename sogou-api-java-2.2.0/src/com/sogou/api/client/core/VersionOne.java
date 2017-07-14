package com.sogou.api.client.core;

import java.util.HashMap;
import java.util.Map;

import javax.xml.namespace.QName;

import com.sogou.api.sem.v1.account.AccountService;
import com.sogou.api.sem.v1.accountdownload.AccountDownloadService;
import com.sogou.api.sem.v1.cpc.CpcService;
import com.sogou.api.sem.v1.cpcextraidea.CpcExtraIdeaService;
import com.sogou.api.sem.v1.cpcgrp.CpcGrpService;
import com.sogou.api.sem.v1.cpcidea.CpcIdeaService;
import com.sogou.api.sem.v1.cpcplan.CpcPlanService;
import com.sogou.api.sem.v1.kr.KRService;
import com.sogou.api.sem.v1.report.ReportService;

public class VersionOne implements APIVersion {

	public static final String NAMESPACE_COMMON = "http://api.sogou.com/sem/common/v1";
	public static final String NAME_SPACE = "https://api.sogou.com/sem/sms/v1";
	private static Map<String, String> urls = new HashMap<String, String>();
	private static Map<String, QName> qNames = new HashMap<String, QName>();

	static{
		registerURL(AccountService.class);
		registerURL(CpcPlanService.class);
		registerURL(CpcGrpService.class);
		registerURL(CpcService.class);
		registerURL(CpcIdeaService.class);
		registerURL(CpcExtraIdeaService.class);
		registerURL(ReportService.class);
		registerURL(AccountDownloadService.class);
		registerURL(KRService.class);
		
		registerQName(AccountService.class);
		registerQName(CpcPlanService.class);
		registerQName(CpcGrpService.class);
		registerQName(CpcService.class);
		registerQName(CpcExtraIdeaService.class);
		registerQName(ReportService.class);
		registerQName(AccountDownloadService.class);
		registerQName(KRService.class);
		registerQName(CpcIdeaService.class);
	}
	
	private static <T> void registerURL(Class<T> clz){
		urls.put(clz.getName(), "/sem/sms/v1/" + clz.getSimpleName() + "?wsdl");
	}
	
	private static <T> void registerQName(Class<T> clz){
		qNames.put(clz.getName(), new QName(NAME_SPACE, clz.getSimpleName()));
	}
	
	
	@Override
	public <T> String getServiceURL(Class<T> clz) {
		return urls.get(clz.getName());
	}

	@Override
	public <T> QName getServiceQName(Class<T> clz) {
		return qNames.get(clz.getName());
	}
	
	public static APIVersion VERSION = new VersionOne();

	private VersionOne() {
		super();
	}

	@Override
	public String getVersion() {
		
		return "v1";
	}

}
