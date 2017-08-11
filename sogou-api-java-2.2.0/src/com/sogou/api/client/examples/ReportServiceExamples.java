package com.sogou.api.client.examples;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.sogou.api.client.utils.FileDownloadUtils;
import com.sogou.api.client.utils.ReportUtils;
import com.sogou.api.client.utils.ZipUtils;
import com.sogou.api.sem.v1.report.GetReportIdRequest;
import com.sogou.api.sem.v1.report.ReportRequestType;

public class ReportServiceExamples {

	
	/**
	 * @param args
	 * @throws ParseException 
	 */
	public static void main(String[] args) throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		GetReportIdRequest getReportIdRequest = new GetReportIdRequest();
		ReportRequestType reportRequestType = new ReportRequestType();
		reportRequestType.getPerformanceData();
		reportRequestType.setPlatform(2);
		List<String> performanceData = new ArrayList<>();
		performanceData.add("cost");
		performanceData.add("cpc");
		performanceData.add("click");
		performanceData.add("impression");
		performanceData.add("ctr");
		performanceData.add("position");
		reportRequestType.setPerformanceData(performanceData);
		reportRequestType.setReportType(5);
		reportRequestType.setStartDate(new Date(sdf.parse("2017-07-11").getTime()));
		reportRequestType.setEndDate(new Date(sdf.parse("2017-07-25").getTime()));
		getReportIdRequest.setReportRequestType(reportRequestType);
	
		String url = ReportUtils.getReportDownloadURL(getReportIdRequest, 10);
		System.out.println(url);
		/*try {
			String reportFile = "D:/logs/report.gzip";
			FileDownloadUtils.downloadFile(url, reportFile);
			// 报表使用的gzip进行的压缩
			ZipUtils.unGzipFile(reportFile, "D:/logs/");
		} catch (IOException e) {
			e.printStackTrace();
		}*/
		
	}

}
