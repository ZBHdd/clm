package com.cubead.clm.io.shenma;

import java.io.ByteArrayInputStream;
import java.nio.charset.Charset;
import java.util.LinkedList;

import javax.json.JsonObject;
import javax.json.JsonValue;
import javax.json.JsonValue.ValueType;

import com.cubead.clm.IProcessor;

import com.cubead.clm.io.shenma.data.DataStorage;
import com.cubead.clm.io.shenma.data.Result;
import com.cubead.clm.io.shenma.data.DataStorage.ReportType;
import com.cubead.clm.io.shenma.data.realTime.RealTimeAccountReport;
import com.cubead.clm.io.shenma.data.report.AccountReport;
import com.cubead.clm.io.shenma.data.report.AdgroupReport;
import com.cubead.clm.io.shenma.data.report.AdvanceAppReport;
import com.cubead.clm.io.shenma.data.report.AdvanceGoldensReport;
import com.cubead.clm.io.shenma.data.report.AdvanceImgTextCreativeReport;
import com.cubead.clm.io.shenma.data.report.AdvanceImgTextSublinkReport;
import com.cubead.clm.io.shenma.data.report.AppReport;
import com.cubead.clm.io.shenma.data.report.AreaReport;
import com.cubead.clm.io.shenma.data.report.CampaignReport;
import com.cubead.clm.io.shenma.data.report.CreativeReport;
import com.cubead.clm.io.shenma.data.report.InvalidClickReport;
import com.cubead.clm.io.shenma.data.report.KeyWordReport;
import com.cubead.clm.io.shenma.data.report.PathReport;
import com.cubead.clm.io.shenma.data.report.PhoneReport;
import com.cubead.clm.io.shenma.data.report.SearchWordReport;


public class ReportJob implements Runnable{
	private final static String getTaskStateurl="https://e.sm.cn/api/task/getTaskState";
	private final static String downloadurl="https://e.sm.cn/api/file/download";
	private IProcessor<Object, Boolean> executor;
	private String startTime;
	private String endTime;
	private IProcessor<Object, Boolean> logger;
	private final Integer interval;
	private final ReportType reportType;
	private Count count;
	private final DataStorage report;
	private final int team;
	private IProcessor<Object, Boolean> stop;
	private JsonObject semAccounts;
	private final static String url="https://e.sm.cn/api/report/getReport";

	public ReportJob(IProcessor<Object, Boolean> executor, String startTime,
			String endTime, IProcessor<Object, Boolean> logger, Integer interval,
			ReportType reportType, Count count, DataStorage report, int team,
			IProcessor<Object, Boolean> stop, JsonObject semAccounts) {
		this.executor = executor;
		this.startTime = startTime;
		this.endTime = endTime;
		this.logger = logger;
		this.interval = interval;
		this.reportType = reportType;
		this.count = count;
		this.report = report;
		this.team = team;
		this.stop = stop;
		this.semAccounts = semAccounts;
	}

	@Override
	public void run() {
		if (!stop.process(semAccounts.getString("tenant_id"), reportType, team)) try{
			String msg = null;
			
			if(reportType.ordinal()>14){
				msg = "{\"header\":{\"username\":\""+semAccounts.getString("name")+"\",\"password\": \""+semAccounts.getString("password")+"\",\"token\": \""+semAccounts.getString("token")+"\"},\"body\":{\"startDate\": \""+startTime+"\",\"endDate\": \""+endTime+"\",\"reportType\":"+type(reportType)+",\"unitOfTime\":7}}";
			}else{
				msg = "{\"header\":{\"username\":\""+semAccounts.getString("name")+"\",\"password\": \""+semAccounts.getString("password")+"\",\"token\": \""+semAccounts.getString("token")+"\"},\"body\":{\"startDate\": \""+startTime+"\",\"endDate\": \""+endTime+"\",\"reportType\":"+type(reportType)+"}}";
			}
			HttpClient req = new HttpClient();
			JsonObject resp = req.rest("POST", url,msg);
			if (resp.getJsonObject("header").getInt("status") == 0) 
				executor.process(new Job(resp.getJsonObject("body").getJsonNumber("taskId").longValue()), System.currentTimeMillis() + interval * 9 + SubTask.random.nextInt(interval * 2));
			else if (resp.getJsonObject("header").getJsonArray("failures").size() > 0 && resp.getJsonObject("header").getJsonArray("failures").getJsonObject(0).getInt("code") == 8502) 
				executor.process(this, System.currentTimeMillis() + interval * 9 + SubTask.random.nextInt(interval * 2));
			else {
				report.setResult(reportType, team, new Result(false, null));
				logger.process(resp, 10111, semAccounts.getString("tenant_id"));
			}
		} catch (Exception e) {
			if (count.add()) executor.process(this, System.currentTimeMillis() + interval * 9 + SubTask.random.nextInt(interval * 2));
			else report.setResult(reportType, team, new Result(false, null));
			logger.process(Task.getMessage(e), 10110, semAccounts.getString("tenant_id"));
		} else  {
			report.finish("force quit");
			logger.process("force stop", 10109, semAccounts.getString("tenant_id"));
		}
	}

	protected int type(ReportType type){
		switch (type) {
			case accountReport : case realTimeAccountReport: return 2;
			case advanceAppReport : case realTimeAdvanceAppReport : return 26;
			case adgroupReport : case realTimeAdgroupReport : return 11;
			case advanceGoldReport : case realTimeAdvanceGoldReport :  return 27;
			case advanceImgTextCreativeReport : case realTimeAdvanceImgTextCreativeReport : return 25;
			case advanceImgTextSublinkReport : case realTimeAdvanceImgTextSublinkReport : return 28;
			case appReport : case realTimeAppReport : return 23;
			case areaReport : case realTimeAreaReport : return 3;
			case creativeReport : case realTimeCreativeReport : return 12;
			case invalidClickReport : case realTimeInvalidClickReport : return 24;
			case keyWordReport : case realTimeKeyWordReport : return 14;
			case pathReport : case realTimePathReport : return 11;
			case phoneReport : case realTimePhoneReport : return 22;
			case searchWordReport : case realTimeSearchWordReport : return 6;
			case campaignReport : case realTimeCampaignReport : return 10;
		}
		return 0;
	}

	protected class Job implements Runnable{
		private final Long taskId;

		public Job(Long taskId) {
			this.taskId = taskId;
		}

		@Override
		public void run() {
			if (!stop.process(semAccounts.getString("tenant_id"), reportType, team)) try{
				String msg = "{\"header\":{\"username\":\""+semAccounts.getString("name")+"\",\"password\": \""+semAccounts.getString("password")+"\",\"token\": \""+semAccounts.getString("token")+"\"},\"body\":{\"taskId\":"+taskId+"}}";
				HttpClient req = new HttpClient();
				JsonObject resp = req.rest("POST", getTaskStateurl,msg);
				JsonValue body = resp.get("body");
				if (body != null && ValueType.OBJECT.equals(body.getValueType()) && "FINISHED".equals(((JsonObject)body).getString("status"))) {
					String download = "{\"header\":{\"username\":\""+semAccounts.getString("name")+"\",\"password\": \""+semAccounts.getString("password")+"\",\"token\": \""+semAccounts.getString("token")+"\"},\"body\":{\"fileId\":"+((JsonObject)body).getJsonNumber("fileId")+"}}";
					byte[] respbody = req.request("POST", downloadurl,download);
					try {
						CsvReader reader = new CsvReader(new ByteArrayInputStream(respbody), Charset.forName("utf-8"));
						reader.readHeaders();
						switch(reportType) {
							case accountReport: LinkedList<AccountReport> accountresult = new LinkedList<AccountReport>();
							while(reader.readRecord()) {
								AccountReport accountReport = new AccountReport();
								accountReport.setTime(reader.get("﻿\"时间\""));
								accountReport.setAccountID(reader.get("账户ID")==null?null:Long.valueOf(reader.get("账户ID")));
								accountReport.setAccount(reader.get("账户"));
								accountReport.setShowNum(reader.get("展现量")==null?null:Long.valueOf(reader.get("展现量")));
								accountReport.setClickNum(reader.get("点击量")==null?null:Long.valueOf(reader.get("点击量")));
								accountReport.setCost(reader.get("消费")==null?null:Float.parseFloat(reader.get("消费")));
								accountReport.setClickRate(reader.get("点击率"));
								accountReport.setAvgClickPrice(reader.get("平均点击价格")==null?null:Float.parseFloat(reader.get("平均点击价格")));
								accountresult.add(accountReport);
							}
							report.setResult(reportType, team, new Result<AccountReport>(true, accountresult));break;
							case adgroupReport:	case realTimeAdgroupReport: LinkedList<AdgroupReport> adgroupresult = new LinkedList<AdgroupReport>();
							while(reader.readRecord()) {
								AdgroupReport adgroupReport = new AdgroupReport();
								adgroupReport.setTime(reader.get("﻿\"时间\""));
								adgroupReport.setAccountId(reader.get("账户ID")==null?null:Long.valueOf(reader.get("账户ID")));
								adgroupReport.setAccountName(reader.get("账户"));
								adgroupReport.setCampaignId(reader.get("推广计划ID")==null?null:Long.parseLong(reader.get("推广计划ID")));
								adgroupReport.setShowNum(reader.get("展现量")==null?null:Long.valueOf(reader.get("展现量")));
								adgroupReport.setClickNum(reader.get("点击量")==null?null:Long.valueOf(reader.get("点击量")));
								adgroupReport.setCost(reader.get("消费")==null?null:Float.parseFloat(reader.get("消费")));
								adgroupReport.setClickRate(reader.get("点击率"));
								adgroupReport.setAvgClickPrice(reader.get("平均点击价格")==null?null:Float.parseFloat(reader.get("平均点击价格")));
								adgroupReport.setCampaignName(reader.get("推广计划"));
								adgroupReport.setAdgroupId(reader.get("推广单元ID")==null?null:Long.parseLong(reader.get("推广单元ID")));
								adgroupReport.setAdgroupName(reader.get("推广单元"));
								adgroupresult.add(adgroupReport);
							}
							report.setResult(reportType, team, new Result<AdgroupReport>(true, adgroupresult));break;
							case advanceAppReport: case realTimeAdvanceAppReport: LinkedList<AdvanceAppReport> advanceAppresult = new LinkedList<AdvanceAppReport>();
							while(reader.readRecord()) {
								AdvanceAppReport advanceAppReport=new AdvanceAppReport();
								advanceAppReport.setTime(reader.get("﻿\"时间\""));
								advanceAppReport.setAccountId(reader.get("账户ID")==null?null:Long.valueOf(reader.get("账户ID")));
								advanceAppReport.setAccountName(reader.get("账户"));
								advanceAppReport.setCampaignId(reader.get("推广计划ID")==null?null:Long.parseLong(reader.get("推广计划ID")));
								advanceAppReport.setCampaignName(reader.get("推广计划"));
								advanceAppReport.setAdgroupId(reader.get("推广单元ID")==null?null:Long.parseLong(reader.get("推广单元ID")));
								advanceAppReport.setAdgroupName(reader.get("推广单元"));
								advanceAppReport.setShowNum(reader.get("展现量")==null?null:Long.valueOf(reader.get("展现量")));
								advanceAppReport.setClickNum(reader.get("点击量")==null?null:Long.valueOf(reader.get("点击量")));
								advanceAppReport.setCost(reader.get("消费")==null?null:Float.parseFloat(reader.get("消费")));
								advanceAppReport.setClickRate(reader.get("点击率"));
								advanceAppReport.setAvgClickPrice(reader.get("平均点击价格")==null?null:Float.parseFloat(reader.get("平均点击价格")));
								advanceAppReport.setCreativeId(reader.get("创意ID")==null?null:Long.parseLong(reader.get("创意ID")));
								advanceAppReport.setCreativeName(reader.get("创意"));
								advanceAppresult.add(advanceAppReport);
							}
							report.setResult(reportType, team, new Result<AdvanceAppReport>(true, advanceAppresult));break;
							case advanceGoldReport: case realTimeAdvanceGoldReport: LinkedList<AdvanceGoldensReport> advanceGoldresult = new LinkedList<AdvanceGoldensReport>();
							while(reader.readRecord()){
								AdvanceGoldensReport advanceGoldensReport = new AdvanceGoldensReport();
								advanceGoldensReport.setTime(reader.get("﻿\"时间\""));
								advanceGoldensReport.setAccountId(reader.get("账户ID")==null?null:Long.valueOf(reader.get("账户ID")));
								advanceGoldensReport.setAccountName(reader.get("账户"));
								advanceGoldensReport.setCampaignId(reader.get("推广计划ID")==null?null:Long.parseLong(reader.get("推广计划ID")));
								advanceGoldensReport.setCampaignName(reader.get("推广计划"));
								advanceGoldensReport.setAdgroupId(reader.get("推广单元ID")==null?null:Long.parseLong(reader.get("推广单元ID")));
								advanceGoldensReport.setAdgroupName(reader.get("推广单元"));
								advanceGoldensReport.setShowNum(reader.get("展现量")==null?null:Long.valueOf(reader.get("展现量")));
								advanceGoldensReport.setClickNum(reader.get("点击量")==null?null:Long.valueOf(reader.get("点击量")));
								advanceGoldensReport.setCost(reader.get("消费")==null?null:Float.parseFloat(reader.get("消费")));
								advanceGoldensReport.setClickRate(reader.get("点击率"));
								advanceGoldensReport.setAvgClickPrice(reader.get("平均点击价格")==null?null:Float.parseFloat(reader.get("平均点击价格")));
								advanceGoldensReport.setCreativeId(reader.get("创意ID")==null?null:Long.parseLong(reader.get("创意ID")));
								advanceGoldensReport.setCreativeName(reader.get("创意"));
								advanceGoldresult.add(advanceGoldensReport);
							}
							report.setResult(reportType, team, new Result<AdvanceGoldensReport>(true, advanceGoldresult));break;
							case advanceImgTextCreativeReport: case realTimeAdvanceImgTextCreativeReport: LinkedList<AdvanceImgTextCreativeReport> advanceImgTextCreativeresult = new LinkedList<AdvanceImgTextCreativeReport>();
							while(reader.readRecord()){
								AdvanceImgTextCreativeReport advanceImgTextCreativeReport =  new AdvanceImgTextCreativeReport();
								advanceImgTextCreativeReport.setTime(reader.get("﻿\"时间\""));
								advanceImgTextCreativeReport.setAccountId(reader.get("账户ID")==null?null:Long.valueOf(reader.get("账户ID")));
								advanceImgTextCreativeReport.setAccountName(reader.get("账户"));
								advanceImgTextCreativeReport.setCampaignId(reader.get("推广计划ID")==null?null:Long.parseLong(reader.get("推广计划ID")));
								advanceImgTextCreativeReport.setCampaignName(reader.get("推广计划"));
								advanceImgTextCreativeReport.setAdgroupId(reader.get("推广单元ID")==null?null:Long.parseLong(reader.get("推广单元ID")));
								advanceImgTextCreativeReport.setAdgroupName(reader.get("推广单元"));
								advanceImgTextCreativeReport.setShowNum(reader.get("展现量")==null?null:Long.valueOf(reader.get("展现量")));
								advanceImgTextCreativeReport.setClickNum(reader.get("点击量")==null?null:Long.valueOf(reader.get("点击量")));
								advanceImgTextCreativeReport.setCost(reader.get("消费")==null?null:Float.parseFloat(reader.get("消费")));
								advanceImgTextCreativeReport.setClickRate(reader.get("点击率"));
								advanceImgTextCreativeReport.setAvgClickPrice(reader.get("平均点击价格")==null?null:Float.parseFloat(reader.get("平均点击价格")));
								advanceImgTextCreativeReport.setCreativeId(reader.get("账户ID")==null?null:Long.parseLong(reader.get("创意ID")));
								advanceImgTextCreativeReport.setCreativeName(reader.get("创意"));
								advanceImgTextCreativeresult.add(advanceImgTextCreativeReport);	
							}
							report.setResult(reportType, team, new Result<AdvanceImgTextCreativeReport>(true, advanceImgTextCreativeresult));break;
							case advanceImgTextSublinkReport: case realTimeAdvanceImgTextSublinkReport: LinkedList<AdvanceImgTextSublinkReport> advanceImgTextSublinkReportresult = new LinkedList<AdvanceImgTextSublinkReport>();
							while(reader.readRecord()){
								AdvanceImgTextSublinkReport advanceImgTextSublinkReport=  new AdvanceImgTextSublinkReport();
								advanceImgTextSublinkReport.setTime(reader.get("﻿\"时间\""));
								advanceImgTextSublinkReport.setAccountId(reader.get("账户ID")==null?null:Long.valueOf(reader.get("账户ID")));
								advanceImgTextSublinkReport.setAccountName(reader.get("账户"));
								advanceImgTextSublinkReport.setCampaignId(reader.get("推广计划ID")==null?null:Long.parseLong(reader.get("推广计划ID")));
								advanceImgTextSublinkReport.setCampaignName(reader.get("推广计划"));
								advanceImgTextSublinkReport.setAdgroupId(reader.get("推广单元ID")==null?null:Long.parseLong(reader.get("推广单元ID")));
								advanceImgTextSublinkReport.setAdgroupName(reader.get("推广单元"));
								advanceImgTextSublinkReport.setShowNum(reader.get("展现量")==null?null:Long.valueOf(reader.get("展现量")));
								advanceImgTextSublinkReport.setClickNum(reader.get("点击量")==null?null:Long.valueOf(reader.get("点击量")));
								advanceImgTextSublinkReport.setCost(reader.get("消费")==null?null:Float.parseFloat(reader.get("消费")));
								advanceImgTextSublinkReport.setClickRate(reader.get("点击率"));
								advanceImgTextSublinkReport.setAvgClickPrice(reader.get("平均点击价格")==null?null:Float.parseFloat(reader.get("平均点击价格")));
								advanceImgTextSublinkReport.setCreativeId(reader.get("创意ID")==null?null:Long.parseLong(reader.get("创意ID")));
								advanceImgTextSublinkReport.setCreativeName(reader.get("创意"));
								advanceImgTextSublinkReportresult.add(advanceImgTextSublinkReport);
							}
							report.setResult(reportType, team, new Result<AdvanceImgTextSublinkReport>(true, advanceImgTextSublinkReportresult));break;
							case appReport: case realTimeAppReport: LinkedList<AppReport> appReportresult = new LinkedList<AppReport>();
							while(reader.readRecord()){
								AppReport appReport =  new AppReport();
								appReport.setTime(reader.get("﻿\"时间\""));
								appReport.setAccountId(reader.get("账户ID")==null?null:Long.valueOf(reader.get("账户ID")));
								appReport.setAccountName(reader.get("账户"));
								appReport.setCampaignId(reader.get("推广计划ID")==null?null:Long.parseLong(reader.get("推广计划ID")));
								appReport.setCampaignName(reader.get("推广计划"));
								appReport.setAdgroupId(reader.get("推广单元ID")==null?null:Long.parseLong(reader.get("推广单元ID")));
								appReport.setAdgroupName(reader.get("推广单元"));
								appReport.setShowNum(reader.get("展现量")==null?null:Long.valueOf(reader.get("展现量")));
								appReport.setClickNum(reader.get("点击量")==null?null:Long.valueOf(reader.get("点击量")));
								appReport.setCost(reader.get("消费")==null?null:Float.parseFloat(reader.get("消费")));
								appReport.setClickRate(reader.get("点击率"));
								appReport.setAvgClickPrice(reader.get("平均点击价格")==null?null:Float.parseFloat(reader.get("平均点击价格")));
								appReport.setAppId(reader.get("推广APP ID")==null?null:Long.parseLong(reader.get("推广APP ID")));
								appReport.setAppName(reader.get("推广APP"));	
								appReportresult.add(appReport);
							}
							report.setResult(reportType, team, new Result<AppReport>(true, appReportresult));break;
							case areaReport: case realTimeAreaReport: LinkedList<AreaReport> areaReportresult = new LinkedList<AreaReport>();
							while(reader.readRecord()){
								AreaReport areaReport= new AreaReport();
								areaReport.setTime(reader.get("﻿\"时间\""));
								areaReport.setAccountId(reader.get("账户ID")==null?null:Long.valueOf(reader.get("账户ID")));
								areaReport.setAccountName(reader.get("账户"));
								areaReport.setShowNum(reader.get("展现量")==null?null:Long.valueOf(reader.get("展现量")));
								areaReport.setClickNum(reader.get("点击量")==null?null:Long.valueOf(reader.get("点击量")));
								areaReport.setCost(reader.get("消费")==null?null:Float.parseFloat(reader.get("消费")));
								areaReport.setClickRate(reader.get("点击率"));
								areaReport.setAvgClickPrice(reader.get("平均点击价格")==null?null:Float.parseFloat(reader.get("平均点击价格")));
								areaReport.setAreaId(reader.get("地域ID")==null?null:Long.parseLong(reader.get("地域ID")));
								areaReport.setAreaName(reader.get("省级地域"));
								areaReportresult.add(areaReport);
							}
							report.setResult(reportType, team, new Result<AreaReport>(true, areaReportresult));break;
							case campaignReport: case realTimeCampaignReport: LinkedList<CampaignReport> campaignReportresult = new LinkedList<CampaignReport>();
							while(reader.readRecord()){
								CampaignReport campaignReport = new CampaignReport();
								campaignReport.setTime(reader.get("﻿\"时间\""));
								campaignReport.setAccountId(reader.get("账户ID")==null?null:Long.valueOf(reader.get("账户ID")));
								campaignReport.setAccountName(reader.get("账户"));
								campaignReport.setCampaignId(reader.get("推广计划ID")==null?null:Long.parseLong(reader.get("推广计划ID")));
								campaignReport.setCampaignName(reader.get("推广计划"));
								campaignReport.setShowNum(reader.get("展现量")==null?null:Long.valueOf(reader.get("展现量")));
								campaignReport.setClickNum(reader.get("点击量")==null?null:Long.valueOf(reader.get("点击量")));
								campaignReport.setCost(reader.get("消费")==null?null:Float.parseFloat(reader.get("消费")));
								campaignReport.setClickRate(reader.get("点击率"));
								campaignReport.setAvgClickPrice(reader.get("平均点击价格")==null?null:Float.parseFloat(reader.get("平均点击价格")));
								campaignReportresult.add(campaignReport);
							}
							report.setResult(reportType, team, new Result<CampaignReport>(true, campaignReportresult));break;
							case creativeReport: case realTimeCreativeReport: LinkedList<CreativeReport> creativeReportresult = new LinkedList<CreativeReport>();
							while(reader.readRecord()){
								CreativeReport creativeReport = new CreativeReport();
								creativeReport.setTime(reader.get("﻿\"时间\""));
								creativeReport.setAccountId(reader.get("账户ID")==null?null:Long.valueOf(reader.get("账户ID")));
								creativeReport.setAccountName(reader.get("账户"));
								creativeReport.setCampaignId(reader.get("推广计划ID")==null?null:Long.parseLong(reader.get("推广计划ID")));
								creativeReport.setCampaignName(reader.get("推广计划"));
								creativeReport.setAdgroupId(reader.get("推广单元ID")==null?null:Long.parseLong(reader.get("推广单元ID")));
								creativeReport.setAdgroupName(reader.get("推广单元"));
								creativeReport.setCreativeId(reader.get("创意ID")==null?null:Long.parseLong(reader.get("创意ID")));
								creativeReport.setCreativeName(reader.get("创意"));
								creativeReport.setShowNum(reader.get("展现量")==null?null:Long.valueOf(reader.get("展现量")));
								creativeReport.setClickNum(reader.get("点击量")==null?null:Long.valueOf(reader.get("点击量")));
								creativeReport.setCost(reader.get("消费")==null?null:Float.parseFloat(reader.get("消费")));
								creativeReport.setClickRate(reader.get("点击率"));
								creativeReport.setAvgClickPrice(reader.get("平均点击价格")==null?null:Float.parseFloat(reader.get("平均点击价格")));
								creativeReportresult.add(creativeReport);
							}
							report.setResult(reportType, team, new Result<CreativeReport>(true, creativeReportresult));break;
							case invalidClickReport: case realTimeInvalidClickReport: LinkedList<InvalidClickReport> invalidClickReportresult = new LinkedList<InvalidClickReport>();
							while(reader.readRecord()){
								InvalidClickReport invalidClickReport =  new InvalidClickReport();
								invalidClickReport.setTime(reader.get("﻿\"时间\""));
								invalidClickReport.setBeforeFilterClickNum(reader.get("过滤前点击量")==null?null:Long.parseLong(reader.get("过滤前点击量")));
								invalidClickReport.setAfterFilterClickNum(reader.get("过滤点击量")==null?null:Long.parseLong(reader.get("过滤点击量")));
								invalidClickReport.setFilterMoney(reader.get("过滤金额")==null?null:Float.parseFloat(reader.get("过滤金额")));
								invalidClickReportresult.add(invalidClickReport);
							}
							report.setResult(reportType, team, new Result<InvalidClickReport>(true, invalidClickReportresult));break;
							case keyWordReport: case realTimeKeyWordReport: LinkedList<KeyWordReport> keyWordReportresult = new LinkedList<KeyWordReport>();
							while(reader.readRecord()){
								KeyWordReport keyWordReport = new KeyWordReport();
								keyWordReport.setTime(reader.get("﻿\"时间\""));
								keyWordReport.setAccountId(reader.get("账户ID")==null?null:Long.valueOf(reader.get("账户ID")));
								keyWordReport.setAccountName(reader.get("账户"));
								keyWordReport.setCampaignId(reader.get("推广计划ID")==null?null:Long.parseLong(reader.get("推广计划ID")));
								keyWordReport.setCampaignName(reader.get("推广计划"));
								keyWordReport.setAdgroupId(reader.get("推广单元ID")==null?null:Long.parseLong(reader.get("推广单元ID")));
								keyWordReport.setAdgroupName(reader.get("推广单元"));
								keyWordReport.setKeyWordId(reader.get("关键词ID")==null?null:Long.parseLong(reader.get("关键词ID")));
								keyWordReport.setKeyWordName(reader.get("关键词"));
								keyWordReport.setShowNum(reader.get("展现量")==null?null:Long.valueOf(reader.get("展现量")));
								keyWordReport.setClickNum(reader.get("点击量")==null?null:Long.valueOf(reader.get("点击量")));
								keyWordReport.setCost(reader.get("消费")==null?null:Float.parseFloat(reader.get("消费")));
								keyWordReport.setClickRate(reader.get("点击率"));
								keyWordReport.setAvgClickPrice(reader.get("平均点击价格")==null?null:Float.parseFloat(reader.get("平均点击价格")));
								keyWordReportresult.add(keyWordReport);
							}
							report.setResult(reportType, team, new Result<KeyWordReport>(true, keyWordReportresult));break;
							case pathReport: case realTimePathReport:LinkedList<PathReport> pathReportresult = new LinkedList<PathReport>();
							while(reader.readRecord()){
								PathReport pathReport = new PathReport();
								pathReport.setTime(reader.get("﻿\"时间\""));
								pathReport.setAccountId(reader.get("账户ID")==null?null:Long.valueOf(reader.get("账户ID")));
								pathReport.setAccountName(reader.get("账户"));
								pathReport.setCampaignId(reader.get("推广计划ID")==null?null:Long.parseLong(reader.get("推广计划ID")));
								pathReport.setCampaignName(reader.get("推广计划"));
								pathReport.setAdgroupId(reader.get("推广单元ID")==null?null:Long.parseLong(reader.get("推广单元ID")));
								pathReport.setAdgroupName(reader.get("推广单元"));
								pathReport.setSubLinkId(reader.get("推广子链ID")==null?null:Long.parseLong(reader.get("推广子链ID")));
								pathReport.setSubLinkName(reader.get("推广子链"));
								pathReport.setShowNum(reader.get("展现量")==null?null:Long.valueOf(reader.get("展现量")));
								pathReport.setClickNum(reader.get("点击量")==null?null:Long.valueOf(reader.get("点击量")));
								pathReport.setCost(reader.get("消费")==null?null:Float.parseFloat(reader.get("消费")));
								pathReport.setClickRate(reader.get("点击率"));
								pathReport.setAvgClickPrice(reader.get("平均点击价格")==null?null:Float.parseFloat(reader.get("平均点击价格")));
								pathReportresult.add(pathReport);
							}
							report.setResult(reportType, team, new Result<PathReport>(true, pathReportresult));break;
							case phoneReport: case realTimePhoneReport:LinkedList<PhoneReport> phoneReportresult = new LinkedList<PhoneReport>();
							while(reader.readRecord()){
								PhoneReport phoneReport =  new PhoneReport();
								phoneReport.setTime(reader.get("﻿\"时间\""));
								phoneReport.setAccountId(reader.get("账户ID")==null?null:Long.valueOf(reader.get("账户ID")));
								phoneReport.setAccountName(reader.get("账户"));
								phoneReport.setCampaignId(reader.get("推广计划ID")==null?null:Long.parseLong(reader.get("推广计划ID")));
								phoneReport.setCampaignName(reader.get("推广计划"));
								phoneReport.setAdgroupId(reader.get("推广单元ID")==null?null:Long.parseLong(reader.get("推广单元ID")));
								phoneReport.setAdgroupName(reader.get("推广单元"));
								phoneReport.setPhoneId(reader.get("推广电话ID")==null?null:Long.parseLong(reader.get("推广电话ID")));
								phoneReport.setPhoneName(reader.get("推广电话"));
								phoneReport.setShowNum(reader.get("展现量")==null?null:Long.valueOf(reader.get("展现量")));
								phoneReport.setClickNum(reader.get("点击量")==null?null:Long.valueOf(reader.get("点击量")));
								phoneReport.setCost(reader.get("消费")==null?null:Float.parseFloat(reader.get("消费")));
								phoneReport.setClickRate(reader.get("点击率"));
								phoneReport.setAvgClickPrice(reader.get("平均点击价格")==null?null:Float.parseFloat(reader.get("平均点击价格")));
								phoneReportresult.add(phoneReport);
							}
							report.setResult(reportType, team, new Result<PhoneReport>(true, phoneReportresult));break;
							case searchWordReport:case realTimeSearchWordReport:LinkedList<SearchWordReport> searchWordReportresult = new LinkedList<SearchWordReport>();
							while(reader.readRecord()){
								SearchWordReport searchWordReport = new SearchWordReport();
								searchWordReport.setTime(reader.get("﻿\"时间\""));
								searchWordReport.setAccountId(reader.get("账户ID")==null?null:Long.valueOf(reader.get("账户ID")));
								searchWordReport.setAccountName(reader.get("账户"));
								searchWordReport.setCampaignId(reader.get("推广计划ID")==null?null:Long.parseLong(reader.get("推广计划ID")));
								searchWordReport.setCampaignName(reader.get("推广计划"));
								searchWordReport.setAdgroupId(reader.get("推广单元ID")==null?null:Long.parseLong(reader.get("推广单元ID")));
								searchWordReport.setAdgroupName(reader.get("推广单元"));
								searchWordReport.setCreativeId(reader.get("创意ID")==null?null:Long.parseLong(reader.get("创意ID")));
								searchWordReport.setTitle(reader.get("标题"));
								searchWordReport.setDesc(reader.get("描述"));
								searchWordReport.setSearchWord(reader.get("搜索词"));
								searchWordReport.setKeyWord(reader.get("关键词"));
								searchWordReport.setKeyWordState(reader.get("关键词状态"));
								searchWordReport.setShowNum(reader.get("展现量")==null?null:Long.valueOf(reader.get("展现量")));
								searchWordReport.setClickNum(reader.get("点击量")==null?null:Long.valueOf(reader.get("点击量")));
								searchWordReport.setCost(reader.get("消费")==null?null:Float.parseFloat(reader.get("消费")));
								searchWordReportresult.add(searchWordReport);
							}
							report.setResult(reportType, team, new Result<SearchWordReport>(true, searchWordReportresult));break;
							case realTimeAccountReport:LinkedList<RealTimeAccountReport> realTimeAccountReportList = new LinkedList<RealTimeAccountReport>();
							while(reader.readRecord()){
								RealTimeAccountReport realTimeAccountReport = new RealTimeAccountReport();
								realTimeAccountReport.setTime(reader.get("﻿\"时间\""));
								realTimeAccountReport.setAccountID(reader.get("账户ID")==null?null:Long.valueOf(reader.get("账户ID")));
								realTimeAccountReport.setAccount(reader.get("账户"));
								realTimeAccountReport.setShowNum(reader.get("展现量")==null?null:Long.valueOf(reader.get("展现量")));
								realTimeAccountReport.setClickNum(reader.get("点击量")==null?null:Long.valueOf(reader.get("点击量")));
								realTimeAccountReport.setCost(reader.get("消费")==null?null:Float.parseFloat(reader.get("消费")));
								realTimeAccountReport.setClickRate(reader.get("点击率"));
								realTimeAccountReport.setAvgClickPrice(reader.get("平均点击价格")==null?null:Float.parseFloat(reader.get("平均点击价格")));
								realTimeAccountReportList.add(realTimeAccountReport);
							}
							report.setResult(reportType, team, new Result<RealTimeAccountReport>(true, realTimeAccountReportList));break;
						} 
					} catch (Exception e) {
						report.setResult(reportType, team, new Result(false, null));
						logger.process(Task.getMessage(e), 10114, semAccounts.getString("tenant_id"));
					}
				} else executor.process(this, System.currentTimeMillis() + interval * 9 + SubTask.random.nextInt(interval * 2));
			} catch (Exception e) {
				if (count.add()) executor.process(this, System.currentTimeMillis() + interval * 9 + SubTask.random.nextInt(interval * 2));
				else report.setResult(reportType, team, new Result(false, null));
				logger.process(Task.getMessage(e), 10113, semAccounts.getString("tenant_id"));
			} else  {
				report.finish("force quit");
				logger.process("force quit", 10112, semAccounts.getString("tenant_id"));
			}
	
		}
	}
}