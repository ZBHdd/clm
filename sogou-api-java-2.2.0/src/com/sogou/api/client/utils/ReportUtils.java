package com.sogou.api.client.utils;

import java.util.concurrent.TimeUnit;

import com.sogou.api.client.constants.APIClientConstants;
import com.sogou.api.client.exception.ClientInternalException;
import com.sogou.api.client.exception.SystemException;
import com.sogou.api.client.wrapper.ReportServiceWrapper;
import com.sogou.api.sem.v1.report.GetReportIdRequest;
import com.sogou.api.sem.v1.report.GetReportIdResponse;
import com.sogou.api.sem.v1.report.GetReportPathRequest;
import com.sogou.api.sem.v1.report.GetReportPathResponse;
import com.sogou.api.sem.v1.report.GetReportStateRequest;
import com.sogou.api.sem.v1.report.GetReportStateResponse;

public abstract class ReportUtils {

	public static String getReportDownloadURL(
			GetReportIdRequest getReportIdRequest, int retryNum) {
		if (null == getReportIdRequest || retryNum < 1) {
			throw new IllegalArgumentException(
					"illegal get report id parametes, parameters="
							+ getReportIdRequest + ", retryNum=" + retryNum);
		}

		ReportServiceWrapper reportService = new ReportServiceWrapper();

		GetReportIdResponse getReportIdResponse = reportService
				.getReportId(getReportIdRequest);
		if (null == getReportIdResponse
				|| getReportIdResponse.getReportId() == null) {
			throw new SystemException("API return null report id");
		}

		GetReportStateRequest getReportStateRequest = new GetReportStateRequest();
		getReportStateRequest.setReportId(getReportIdResponse.getReportId());
		int retry = retryNum;
		boolean status = false;
		while (retry-- > 0) {

			GetReportStateResponse getReportStateResponse = reportService
					.getReportState(getReportStateRequest);
			if (null == getReportStateResponse
					|| null == getReportStateResponse.getIsGenerated()) {
				throw new SystemException("API return null report state");
			}
			if (getReportStateResponse.getIsGenerated() == 0) {
				try {
					TimeUnit.SECONDS.sleep(APIClientConstants.QUERY_FILE_STATUS_INTERVAL_SECONDS);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			} else {
				status = true;
				break;
			}
		}
		if (status) {
			GetReportPathRequest getReportPathRequest = new GetReportPathRequest();
			getReportPathRequest.setReportId(getReportIdResponse.getReportId());

			GetReportPathResponse getReportPathResponse = reportService
					.getReportPath(getReportPathRequest);
			if (null == getReportPathResponse
					|| null == getReportPathResponse.getReportFilePath()) {
				throw new SystemException("API return null report path");
			}

			return getReportPathResponse.getReportFilePath();
		} else {
			throw new ClientInternalException("We tried to get report file for "
					+ retryNum * APIClientConstants.QUERY_FILE_STATUS_INTERVAL_SECONDS + " seconds, but file still not ready!");
		}

	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
