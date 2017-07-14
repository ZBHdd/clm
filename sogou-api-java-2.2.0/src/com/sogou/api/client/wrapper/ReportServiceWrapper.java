package com.sogou.api.client.wrapper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.ws.BindingProvider;

import com.sogou.api.client.core.ServiceFactory;
import com.sogou.api.client.core.SogouAdServiceFactory;
import com.sogou.api.client.exception.BusinessException;
import com.sogou.api.client.utils.ResHeaderUtils;
import com.sogou.api.sem.v1.common.ResHeader;
import com.sogou.api.sem.v1.report.GetReportIdRequest;
import com.sogou.api.sem.v1.report.GetReportIdResponse;
import com.sogou.api.sem.v1.report.GetReportPathRequest;
import com.sogou.api.sem.v1.report.GetReportPathResponse;
import com.sogou.api.sem.v1.report.GetReportStateRequest;
import com.sogou.api.sem.v1.report.GetReportStateResponse;
import com.sogou.api.sem.v1.report.ReportService;

public class ReportServiceWrapper {

	private ReportService reportService;

	public ReportServiceWrapper() {
		ServiceFactory sf = SogouAdServiceFactory.getInstance();
		reportService = sf.getWebService(ReportService.class);
	}

	public GetReportStateResponse getReportState(
			GetReportStateRequest parameters) {
		GetReportStateResponse response = reportService.getReportState(parameters);
		ResHeader rheader = ResHeaderUtils
				.getResHeader((BindingProvider) reportService);
		System.out.println(rheader);
		if (rheader.getStatus() != 0) {
			throw new BusinessException(rheader, response);
		}
		return response;
	}

	public GetReportPathResponse getReportPath(GetReportPathRequest parameters) {
		GetReportPathResponse response = reportService.getReportPath(parameters);
		ResHeader rheader = ResHeaderUtils
				.getResHeader((BindingProvider) reportService);
		if (rheader.getStatus() != 0) {
			throw new BusinessException(rheader, response);
		}
		return response;
	}

	public GetReportIdResponse getReportId(GetReportIdRequest parameters) {
		GetReportIdResponse response = reportService.getReportId(parameters);
		ResHeader rheader = ResHeaderUtils
				.getResHeader((BindingProvider) reportService);
		if (rheader.getStatus() != 0) {
			throw new BusinessException(rheader, response);
		}
		return response;
	}

	/**
	 * @param args
	 * @throws ParseException 
	 */
	public static void main(String[] args) throws ParseException {
		/*SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		List<String> performanceData = new ArrayList<>();
		performanceData.add("cost");
		performanceData.add("cpc");
		performanceData.add("click");
		performanceData.add("impression");
		performanceData.add("ctr");
		performanceData.add("position");*/
		
		ReportServiceWrapper reportServiceWrapper = new ReportServiceWrapper();
		GetReportPathRequest r = new GetReportPathRequest();
		r.setReportId("116c41aecb4d84244ea92ba7f1e603d5");
		System.out.println(reportServiceWrapper.getReportPath(r).getReportFilePath());
		/*GetReportIdRequest g = new GetReportIdRequest();
		ReportRequestType reportRequestType = new ReportRequestType();
		reportRequestType.setStartDate(new Date(sdf.parse("2017-05-30").getTime()));
		reportRequestType.setEndDate(new Date(sdf.parse("2017-06-21").getTime()));
		reportRequestType.setPerformanceData(performanceData);
		reportRequestType.setReportType(1);
		g.setReportRequestType(reportRequestType);
		System.out.println(reportServiceWrapper.getReportId(g).getReportId());*/
	}

}
