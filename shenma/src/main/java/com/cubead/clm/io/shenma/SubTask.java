package com.cubead.clm.io.shenma;

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
import com.cubead.clm.io.shenma.data.Report;
import com.cubead.clm.io.shenma.data.Result;
import com.cubead.clm.io.shenma.data.Report.ReportType;
import com.cubead.clm.io.shenma.data.Report.Type;
import com.cubead.clm.io.shenma.data.Report.UserinfoType;

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
			o = params.process("adgroupReport");
			if (o != null) uuids.put(ReportType.adgroupReport, UUID.fromString(o.toString()));
			o = params.process("advanceAppReport");
			if (o != null) uuids.put(ReportType.advanceAppReport, UUID.fromString(o.toString()));
			o = params.process("advanceGoldReport");
			if (o != null) uuids.put(ReportType.advanceGoldReport, UUID.fromString(o.toString()));
			o = params.process("advanceImgTextCreativeReport");
			if (o != null) uuids.put(ReportType.advanceImgTextCreativeReport, UUID.fromString(o.toString()));
			o = params.process("advanceImgTextSublinkReport");
			if (o != null) uuids.put(ReportType.advanceImgTextSublinkReport, UUID.fromString(o.toString()));
			o = params.process("appReport");
			if (o != null) uuids.put(ReportType.appReport, UUID.fromString(o.toString()));
			o = params.process("CampaignReport");
			if (o != null) uuids.put(ReportType.campaignReport, UUID.fromString(o.toString()));
			o = params.process("creativeReport");
			if (o != null) uuids.put(ReportType.creativeReport, UUID.fromString(o.toString()));
			o = params.process("invalidClickReport");
			if (o != null) uuids.put(ReportType.invalidClickReport, UUID.fromString(o.toString()));
			o = params.process("keyWordReport");
			if (o != null) uuids.put(ReportType.keyWordReport, UUID.fromString(o.toString()));
			o = params.process("areaReport");
			if (o != null) uuids.put(ReportType.areaReport, UUID.fromString(o.toString()));
			o = params.process("pathReport");
			if (o != null) uuids.put(ReportType.pathReport, UUID.fromString(o.toString()));
			o = params.process("phoneReport");
			if (o != null) uuids.put(ReportType.phoneReport, UUID.fromString(o.toString()));
			o = params.process("searchWordReport");
			if (o != null) uuids.put(ReportType.searchWordReport, UUID.fromString(o.toString()));
			o = params.process("realTimeAccountReport");
			if (o != null) uuids.put(ReportType.realTimeAccountReport, UUID.fromString(o.toString()));
			o = params.process("realTimeAdgroupReport");
			if (o != null) uuids.put(ReportType.realTimeAdgroupReport, UUID.fromString(o.toString()));
			o = params.process("realTimeAdvanceAppReport");
			if (o != null) uuids.put(ReportType.realTimeAdvanceAppReport, UUID.fromString(o.toString()));
			o = params.process("realTimeAdvanceGoldReport");
			if (o != null) uuids.put(ReportType.realTimeAdvanceGoldReport, UUID.fromString(o.toString()));
			o = params.process("realTimeAdvanceImgTextCreativeReport");
			if (o != null) uuids.put(ReportType.realTimeAdvanceImgTextCreativeReport, UUID.fromString(o.toString()));
			o = params.process("realTimeAdvanceImgTextSublinkReport");
			if (o != null) uuids.put(ReportType.realTimeAdvanceImgTextSublinkReport, UUID.fromString(o.toString()));
			o = params.process("realTimeAppReport");
			if (o != null) uuids.put(ReportType.realTimeAppReport, UUID.fromString(o.toString()));
			o = params.process("realTimeCampaignReport");
			if (o != null) uuids.put(ReportType.realTimeCampaignReport, UUID.fromString(o.toString()));
			o = params.process("realTimeCreativeReport");
			if (o != null) uuids.put(ReportType.realTimeCreativeReport, UUID.fromString(o.toString()));
			o = params.process("realTimeInvalidClickReport");
			if (o != null) uuids.put(ReportType.realTimeInvalidClickReport, UUID.fromString(o.toString()));
			o = params.process("realTimeKeyWordReport");
			if (o != null) uuids.put(ReportType.realTimeKeyWordReport, UUID.fromString(o.toString()));
			o = params.process("realTimeAreaReport");
			if (o != null) uuids.put(ReportType.realTimeAreaReport, UUID.fromString(o.toString()));
			o = params.process("realTimePathReport");
			if (o != null) uuids.put(ReportType.realTimePathReport, UUID.fromString(o.toString()));
			o = params.process("realTimePhoneReport");
			if (o != null) uuids.put(ReportType.realTimePhoneReport, UUID.fromString(o.toString()));
			o = params.process("realTimeSearchWordReport");
			if (o != null) uuids.put(ReportType.realTimeSearchWordReport, UUID.fromString(o.toString()));
			o = params.process("adgroupsUserinfo");
			if (o != null) uuids.put(UserinfoType.Adgroups, UUID.fromString(o.toString()));
			o = params.process("advanceCssSublinksUserinfo");
			if (o != null) uuids.put(UserinfoType.AdvancedCssSublinks, UUID.fromString(o.toString()));
			o = params.process("advancedAppUserinfo");
			if (o != null) uuids.put(UserinfoType.AdvancedApps, UUID.fromString(o.toString()));
			o = params.process("advanceGoldensUserinfo");
			if (o != null) uuids.put(UserinfoType.AdvancedGoldens, UUID.fromString(o.toString()));
			o = params.process("appUserinfo");
			if (o != null) uuids.put(UserinfoType.Apps, UUID.fromString(o.toString()));
			o = params.process("campaignsUserinfo");
			if (o != null) uuids.put(UserinfoType.Campaigns, UUID.fromString(o.toString()));
			o = params.process("creativesUserinfo");
			if (o != null) uuids.put(UserinfoType.Creatives, UUID.fromString(o.toString()));
			o = params.process("keywordsUserinfo");
			if (o != null) uuids.put(UserinfoType.Keywords, UUID.fromString(o.toString()));
			o = params.process("advancePicTextUserinfo");
			if (o != null) uuids.put(UserinfoType.AdvancedPicTexts, UUID.fromString(o.toString()));
			o = params.process("sublinksUserinfo");
			if (o != null) uuids.put(UserinfoType.Sublinks, UUID.fromString(o.toString()));
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
			Boolean full = (Boolean)params.process("full");
			Boolean account = (Boolean)params.process("partial");
			o = params.process("start");
			Long start = o == null ? null : formatter.parse(o.toString()).getTime();
			o = params.process("end");
			Long end = o == null ? null : formatter.parse(o.toString()).getTime();
			o = params.process("updateUrl");
			String updateUrl = o == null ? "http://danei.new.clm.cubead.com/update_time_by_tenant_id" : o.toString();
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
					int team = day % 5 == 0 ? day / 5 : day / 5 + 1;
					Report report = new Report( full, today - 172799999L, update, timeout, count, template, account, ss, 
							data, logger, uuids,updateUrl, new Result[team], new Result[team], new Result[team], new Result[team],	
							new Result[team], new Result[team], new Result[team], new Result[day > 90 ? 18 : team], 
							new Result[team], new Result[team], new Result[team],	new Result[team], new Result[team], 
							new Result[team], new Result[day > 30 ? 6 : team],
							new Result[day>15?3:team], new Result[day>15?3:team], new Result[day>15?3:team], new Result[day>15?3:team], new Result[day>15?3:team],
							new Result[day>15?3:team], new Result[day>15?3:team], new Result[day>15?3:team], new Result[day>15?3:team], new Result[day>15?3:team], 
							new Result[day>15?3:team], new Result[day>15?3:team], new Result[day>15?3:team], new Result[day>15?3:team], new Result[day>15?3:team]);
					today -= 432000000L;
					Long now = System.currentTimeMillis();
					for(int i=0; i < team; i++, today -= 432000000L){
						for(ReportType type : ReportType.values()) if (uuids.containsKey(type)) {
							if (type.ordinal()>14&&i>2||type.equals(ReportType.searchWordReport) && i > 5 || type.equals(ReportType.areaReport) && i > 17) continue;
							else exec.process(new ReportJob(exec, formatter.format(new Date(today < update ? update : today)), 
								formatter.format(new Date(today + 345600000L)), logger, interval, type, new Count(retry), report, i, stop, ss), now + SubTask.random.nextInt(interval));
						}
					}
					if (account == null) exec.process(new AccountJob(exec, logger, interval, new Count(retry), report, stop, ss), now + SubTask.random.nextInt(interval));
				}else logger.process("没有数据可采");
			}
			exec.start();
		} catch (Exception e) {
			logger.process(Task.getMessage(e), 10101);
		} 
		return Collections.EMPTY_LIST;
	}
}