package com.cubead.clm.io.sogou;

import java.math.BigDecimal;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ScheduledExecutorService;

import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonValue;

import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.data.redis.core.RedisTemplate;

import com.cubead.clm.IProcessor;
import com.cubead.clm.ITask;

import com.cubead.clm.io.sogou.data.DataStorage;
import com.cubead.clm.io.sogou.data.Result;
import com.cubead.clm.io.sogou.data.DataStorage.ReportType;
import com.cubead.clm.io.sogou.data.DataStorage.Type;
import com.cubead.clm.io.sogou.data.DataStorage.UserinfoType;

public class SubTask implements ITask<String, Object, Object[]> {
	protected static Random random = new Random(System.nanoTime());
	
	@Override
	public Collection<Object[]> perform(IProcessor<String, Object> params) {
		IProcessor<String, Object> ctx = (IProcessor<String, Object>) params.process();
		IProcessor<Object, Boolean> logger = (IProcessor<Object, Boolean>) ctx.process("log");
		IProcessor<Object, Boolean> data = (IProcessor<Object, Boolean>) ctx.process("data");
		IProcessor<Object, Boolean> stop = (IProcessor<Object, Boolean>) ctx.process("stop");
		ScheduledExecutorService executor = ((IProcessor<Object, ScheduledExecutorService>) ctx.process("resource")).process("executor");
		final ClassPathXmlApplicationContext redisContext = new ClassPathXmlApplicationContext(new String[]{"config/redisContext.xml"}, false);
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		try {
			ParamPropertySource pps = new ParamPropertySource(params);
			redisContext.setClassLoader(getClass().getClassLoader());
			redisContext.getEnvironment().getPropertySources().addFirst(pps);
			redisContext.refresh();
			RedisTemplate<String, String> template = (RedisTemplate<String, String>) redisContext.getBean("redis");
			HashMap<Type, UUID> uuids = new HashMap<Type, UUID>();
			Object o = params.process("accountReport");
			if (o != null) uuids.put(ReportType.accountReport, UUID.fromString(o.toString()));
			o = params.process("groupReport");
			if (o != null) uuids.put(ReportType.groupReport, UUID.fromString(o.toString()));
			o = params.process("ideaReport");
			if (o != null) uuids.put(ReportType.ideaReport, UUID.fromString(o.toString()));
			o = params.process("keyWordReport");
			if (o != null) uuids.put(ReportType.keyWordReport, UUID.fromString(o.toString()));
			o = params.process("keyWordShowNoClickReport");
			if (o != null) uuids.put(ReportType.keyWordShowNoClickReport, UUID.fromString(o.toString()));
			o = params.process("planReport");
			if (o != null) uuids.put(ReportType.planReport, UUID.fromString(o.toString()));
			o = params.process("searchWordReport");
			if (o != null) uuids.put(ReportType.searchWordReport, UUID.fromString(o.toString()));
			o = params.process("accountReport_mobile");
			if (o != null) uuids.put(ReportType.accountReport_mobile, UUID.fromString(o.toString()));
			o = params.process("groupReport_mobile");
			if (o != null) uuids.put(ReportType.groupReport_mobile, UUID.fromString(o.toString()));
			o = params.process("ideaReport_mobile");
			if (o != null) uuids.put(ReportType.ideaReport_mobile, UUID.fromString(o.toString()));
			o = params.process("keyWordReport_mobile");
			if (o != null) uuids.put(ReportType.keyWordReport_mobile, UUID.fromString(o.toString()));
			o = params.process("keyWordShowNoClickReport_mobile");
			if (o != null) uuids.put(ReportType.keyWordShowNoClickReport_mobile, UUID.fromString(o.toString()));
			o = params.process("planReport_mobile");
			if (o != null) uuids.put(ReportType.planReport_mobile, UUID.fromString(o.toString()));
			o = params.process("searchWordReport_mobile");
			if (o != null) uuids.put(ReportType.searchWordReport_mobile, UUID.fromString(o.toString()));
			o = params.process("account");
			if (o != null) uuids.put(UserinfoType.account, UUID.fromString(o.toString()));
			o = params.process("cpcplan");
			if (o != null) uuids.put(UserinfoType.cpcplan, UUID.fromString(o.toString()));
			o = params.process("cpc");
			if (o != null) uuids.put(UserinfoType.cpc, UUID.fromString(o.toString()));
			o = params.process("cpcgrp");
			if (o != null) uuids.put(UserinfoType.cpcgrp, UUID.fromString(o.toString()));
			o = params.process("cpcidea");
			if (o != null) uuids.put(UserinfoType.cpcidea, UUID.fromString(o.toString()));
			o = params.process("cpcexidea");
			if (o != null) uuids.put(UserinfoType.cpcexidea, UUID.fromString(o.toString()));
			o = params.process("interval");
			Integer interval = o == null||((BigDecimal)o).intValue() < 10 ? 3000 : ((BigDecimal)o).intValue() * 100;
			o = params.process("thread");
			Integer thread = o == null||((BigDecimal)o).intValue() < 1 ? Runtime.getRuntime().availableProcessors() : ((BigDecimal)o).intValue();
			o = params.process("retry");
			Integer retry = o == null || ((BigDecimal)o).intValue()<0 ? -1 : ((BigDecimal)o).intValue();
			o = params.process("count");
			Integer count = o == null || ((BigDecimal)o).intValue()<0 ? 100 : ((BigDecimal)o).intValue();
			o = params.process("timeout");
			Long timeout = o == null || ((BigDecimal)o).longValue()<0 ? 7200 : ((BigDecimal)o).longValue();
			Boolean account = (Boolean)params.process("partial");
			o = params.process("start");
			Long start = o == null ? null : formatter.parse(o.toString()).getTime();
			o = params.process("end");
			Long end = o == null ? null : formatter.parse(o.toString()).getTime();
			o = params.process("updateUrl");
			String updateUrl = o == null ? "http://danei.new.clm.cubead.com/update_time_by_tenant_id" : o.toString();
			Boolean full = (Boolean)params.process("full");
			JsonArray accounts = (JsonArray) params.process("accounts");
			Calendar c = new GregorianCalendar();
		    c.set(Calendar.HOUR_OF_DAY, 0); 
		    c.set(Calendar.MINUTE, 0);
		    c.set(Calendar.SECOND, 0);
		    c.set(Calendar.MILLISECOND, 0);
			o = params.process("before");
			if (o != null) end = c.getTimeInMillis() - ((BigDecimal)o).longValue() * 86400000;
			o = params.process("after");
			if (o != null) start = c.getTimeInMillis() - ((BigDecimal)o).longValue() * 86400000;
			Long today = end == null ? c.getTimeInMillis() + 86399999L : end + 86399999L;
			Executor exec = new Executor(executor, thread);
			exec.setClose(new IProcessor<Object, Object>(){
				@Override
				public Object process(Object... parameter) {
					redisContext.close();
					return null;
				}	
			});
			for(JsonValue a : accounts){
				JsonObject ss = (JsonObject) a;
				logger.process("start task", 8, ss.getString("tenant_id"));
				long update = start == null ? ss.get("update_time") == null || ss.getJsonNumber("update_time").longValue() == 0 ? today - 31449600000L : ss.getJsonNumber("update_time").longValue() + 86400000L : start;
				int day = (int) ((today - update) / 86400000);
				if (day > 0) {
					int team = day % 7 == 0 ? day / 7 : day / 7 + 1;
					DataStorage report = new DataStorage(full, today - 172799999L, update, timeout, count, template, account, ss, 
							data, logger, uuids, updateUrl, new Result[team], new Result[team], new Result[team], new Result[team],	
							new Result[team], new Result[day > 7 ? 1 : team], new Result[day > 28 ? 4 : team],new Result[team], new Result[team], new Result[team], new Result[team],	
							new Result[team], new Result[day > 7 ? 1 : team], new Result[day > 28 ? 4 : team]);
					today -= 604800000L;
					Long now = System.currentTimeMillis();
					for(int i=0; i < team; i++, today -= 604800000){
						for(ReportType type : ReportType.values()) if (uuids.containsKey(type)) {
							if (type.equals(ReportType.searchWordReport) && i > 3 || type.equals(ReportType.keyWordShowNoClickReport) && i > 0 ||
									type.equals(ReportType.searchWordReport_mobile) && i > 3 || type.equals(ReportType.keyWordShowNoClickReport_mobile) && i > 0) continue;
							else exec.process(new ReportJob(exec, new Date(today < update ? update : today), 
								new Date(today + 518400000L), logger, interval, type, new Count(retry), report, i, stop, ss), now + SubTask.random.nextInt(interval));
						}
					}
					if (account == null) exec.process(new AccountJob(exec, logger, interval, new Count(retry), report, stop, ss), now + SubTask.random.nextInt(interval));
				}
			}
			exec.start();
		} catch (Exception e) {
			logger.process(Task.getMessage(e), 10201);
		} 
		return Collections.EMPTY_LIST;
	}
}