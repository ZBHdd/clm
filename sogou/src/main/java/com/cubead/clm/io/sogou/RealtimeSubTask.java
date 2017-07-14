package com.cubead.clm.io.sogou;

import java.math.BigDecimal;

import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.GregorianCalendar;

import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonValue;

import com.alibaba.fastjson.JSON;
import com.cubead.clm.IProcessor;
import com.cubead.clm.ITask;

import com.sogou.api.client.core.SogouAdServiceFactory;
import com.sogou.api.client.realtime.GetAccountReportRequest;
import com.sogou.api.client.realtime.RealTimeReportRequest;
import com.sogou.api.client.realtime.RealTimeReportService;
import com.sogou.api.client.wrapper.RealTimeServiceWrapper;

public class RealtimeSubTask implements ITask<String, Object, Object[]> {
	@Override
	public Collection<Object[]> perform(IProcessor<String, Object> params) {
		IProcessor<String, Object> ctx = (IProcessor<String, Object>) params.process();
		IProcessor<Object, Boolean> logger = (IProcessor<Object, Boolean>) ctx.process("log");
		IProcessor<Object, Boolean> data = (IProcessor<Object, Boolean>) ctx.process("data");
		Long time = ((BigDecimal)params.process("time")).longValue();
		Calendar c = GregorianCalendar.getInstance();
		c.setTimeInMillis(time);
		int hour = c.get(Calendar.HOUR_OF_DAY);
		JsonArray accounts = (JsonArray) params.process("accounts");
		if (accounts != null && accounts.size() > 0) for(JsonValue a : accounts) try {
			JsonObject user = (JsonObject) a;
			data.process(JSON.toJSONString(new RealTimeServiceWrapper(new SogouAdServiceFactory(user.getString("name"), user.getString("password"), 
					user.getString("token")).getWebService(RealTimeReportService.class)).getAccountReport(new GetAccountReportRequest(
							new RealTimeReportRequest(hour))).getRealTimeReportResponse()), null, 
							user.getString("tenant_id") + '|' + (user.get("client_code") == null ? "code" : user.getString("client_code")) + '|' + (user.get("utm_source") == null ? "source" : user.getString("utm_source")) + "|realTimeReport", time);
		} catch (Exception e) {
			e.printStackTrace();
			logger.process(Task.getMessage(e), 10219, ((JsonObject)a).getJsonNumber("id").toString());
		} 
		return Collections.EMPTY_LIST;
	}
}