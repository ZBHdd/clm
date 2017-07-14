package com.cubead.clm.io.sogou;

import java.io.ByteArrayInputStream;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.zip.GZIPInputStream;

import javax.json.JsonObject;

import com.cubead.clm.IProcessor;

import com.cubead.clm.io.sogou.data.DataStorage;
import com.cubead.clm.io.sogou.data.Result;
import com.cubead.clm.io.sogou.data.DataStorage.ReportType;
import com.cubead.clm.io.sogou.data.entity.AccountReport;
import com.cubead.clm.io.sogou.data.entity.GroupReport;
import com.cubead.clm.io.sogou.data.entity.IdeaReport;
import com.cubead.clm.io.sogou.data.entity.KeyWordReport;
import com.cubead.clm.io.sogou.data.entity.KeyWordShowNoClickReport;
import com.cubead.clm.io.sogou.data.entity.PlanReport;
import com.cubead.clm.io.sogou.data.entity.SearchWordReport;
import com.sogou.api.client.core.SogouAdServiceFactory;
import com.sogou.api.client.exception.BusinessException;
import com.sogou.api.client.wrapper.ReportServiceWrapper;
import com.sogou.api.sem.v1.report.GetReportIdRequest;
import com.sogou.api.sem.v1.report.GetReportPathRequest;
import com.sogou.api.sem.v1.report.GetReportStateRequest;
import com.sogou.api.sem.v1.report.ReportRequestType;
import com.sogou.api.sem.v1.report.ReportService;

public class ReportJob implements Runnable{
	private IProcessor<Object, Boolean> executor;
	private Date startTime;
	private Date endTime;
	private IProcessor<Object, Boolean> logger;
	private final Integer interval;
	private final ReportType reportType;
	private Count count;
	private final DataStorage report;
	private final int team;
	private IProcessor<Object, Boolean> stop;
	private JsonObject semAccounts;
	private ReportServiceWrapper service;

	public ReportJob(IProcessor<Object, Boolean> executor, Date startTime,
			Date endTime, IProcessor<Object, Boolean> logger, Integer interval,
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
		this.service = new ReportServiceWrapper(new SogouAdServiceFactory(semAccounts.getString("name"), semAccounts.getString("password"), 
				semAccounts.getString("token")).getWebService(ReportService.class));
	}

	@Override
	public void run() {
		if (!stop.process(semAccounts.getString("tenant_id"), reportType, team)) try{
			GetReportIdRequest request = new GetReportIdRequest();
			ReportRequestType type = new ReportRequestType();
			type.setStartDate(startTime);
			type.setEndDate(endTime);
			ArrayList<String> performanceData = new ArrayList<String>();
			performanceData.add("cost");
			performanceData.add("cpc");
			performanceData.add("click");
			if (!ReportType.searchWordReport.equals(reportType)) {
				performanceData.add("impression");
				performanceData.add("ctr");
				performanceData.add("position");
			}
			type.setPerformanceData(performanceData);
			type.setReportType(reportType.ordinal() / 2 + 1);
			type.setPlatform(reportType.ordinal() % 2 + 1);
			request.setReportRequestType(type);						
			executor.process(new Job(service.getReportId(request).getReportId()), System.currentTimeMillis() + interval * 9 + SubTask.random.nextInt(interval * 2));
		}  catch (BusinessException e) {
			if (e.getResHeader().getFailures() != null && e.getResHeader().getFailures().size() > 0 && e.getResHeader().getFailures().get(0).getCode() == 1025009) report.setResult(reportType, team, new Result<Object>(true, Collections.EMPTY_LIST));
			else report.setResult(reportType, team, new Result(false, null));
			logger.process(Task.getMessage(e), 10202, semAccounts.getString("tenant_id"));
		} catch (Exception e) {
			if (count.add()) executor.process(this, System.currentTimeMillis() + interval * 9 + SubTask.random.nextInt(interval * 2));
			else report.setResult(reportType, team, new Result(false, null));
			logger.process(Task.getMessage(e), 10203, semAccounts.getString("tenant_id"));
		} else  {
			report.finish("force quit");
			logger.process("force stop", 10204, semAccounts.getString("tenant_id"));
		}
	}

	protected class Job implements Runnable{
		private final String taskId;

		public Job(String taskId) {
			this.taskId = taskId;
		}

		@Override
		public void run() {
			if (!stop.process(semAccounts.getString("tenant_id"), reportType, team)) try{
				GetReportStateRequest request = new GetReportStateRequest();
				request.setReportId(taskId);
				if (service.getReportState(request).getIsGenerated() == 1) {
					GetReportPathRequest r = new GetReportPathRequest();
					r.setReportId(taskId);
					HttpClient client = new HttpClient();
					client.setReadMode(2);
					byte[] respbody = client.request("GET", service.getReportPath(r).getReportFilePath(),null);
					try {
						SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd");
						SimpleDateFormat formatter0 = new SimpleDateFormat("yyyy-MM-dd");
						CsvReader reader = new CsvReader(new GZIPInputStream(new ByteArrayInputStream(respbody)), Charset.forName("GBK"));
						reader.readHeaders();
						reader.readRecord();
						switch(reportType) {
						case accountReport: case accountReport_mobile: LinkedList<AccountReport> accountresult = new LinkedList<AccountReport>();
						while(reader.readRecord()) {
							AccountReport accountReport = new AccountReport();
							accountReport.setDate(reader.get("日期").indexOf('/') >= 0 ? formatter.parse(reader.get("日期")) : formatter0.parse(reader.get("日期")));
							accountReport.setAccount(reader.get("账户")==null || reader.get("账户").trim().length() == 0 || "--".equals(reader.get("账户"))|| "-".equals(reader.get("账户"))?null:reader.get("账户"));
							accountReport.setAvgClickPrice(reader.get("点击均价")==null || reader.get("点击均价").trim().length() == 0 || "--".equals(reader.get("点击均价"))|| "-".equals(reader.get("点击均价"))?null:Double.valueOf(reader.get("点击均价")));
							accountReport.setClickNum(reader.get("点击数")==null || reader.get("点击数").trim().length() == 0 || "--".equals(reader.get("点击数"))|| "-".equals(reader.get("点击数"))?null:Long.valueOf(reader.get("点击数")));
							accountReport.setClickRate(reader.get("点击率"));
							accountReport.setCost(reader.get("消耗")==null || reader.get("消耗").trim().length() == 0 || "--".equals(reader.get("消耗"))|| "-".equals(reader.get("消耗"))?null:Double.valueOf(reader.get("消耗")));
							accountReport.setKeyworddAvgSort(reader.get("关键词平均排名"));
							accountReport.setShowNum(reader.get("展示数")==null || reader.get("展示数").trim().length() == 0 || "--".equals(reader.get("展示数"))|| "-".equals(reader.get("展示数"))?null:Long.valueOf(reader.get("展示数")));
							accountresult.add(accountReport);
						}
						report.setResult(reportType, team, new Result<AccountReport>(true, accountresult));break;
						case groupReport: case groupReport_mobile: LinkedList<GroupReport> groupReportResult = new LinkedList<GroupReport>();
						while(reader.readRecord()) {
							GroupReport groupReport = new GroupReport();
							groupReport.setAccount(reader.get("账户")==null || reader.get("账户").trim().length() == 0 || "--".equals(reader.get("账户"))|| "-".equals(reader.get("账户"))?null:reader.get("账户"));
							groupReport.setAvgClickPrice(reader.get("点击均价")==null || reader.get("点击均价").trim().length() == 0 || "--".equals(reader.get("消耗"))|| "-".equals(reader.get("消耗"))?null:Double.valueOf(reader.get("点击均价")));
							groupReport.setClickNum(reader.get("点击数")==null || reader.get("点击数").trim().length() == 0 || "--".equals(reader.get("点击数"))|| "-".equals(reader.get("点击数"))?null:Long.valueOf(reader.get("点击数")));
							groupReport.setClickRate(reader.get("点击率"));
							groupReport.setCost(reader.get("消耗")==null || reader.get("消耗").trim().length() == 0 || "--".equals(reader.get("消耗"))|| "-".equals(reader.get("消耗"))?null:Double.valueOf(reader.get("消耗")));
							groupReport.setDate(reader.get("日期").indexOf('/') >= 0 ? formatter.parse(reader.get("日期")) : formatter0.parse(reader.get("日期")));
							groupReport.setGroup(reader.get("推广组"));
							groupReport.setGroupId(reader.get("推广组ID")==null || reader.get("推广组ID").trim().length() == 0 || "--".equals(reader.get("推广组ID"))|| "-".equals(reader.get("推广组ID"))?null:Long.valueOf(reader.get("推广组ID")));
							groupReport.setKeyworddAvgSort(reader.get("关键词平均排名"));
							groupReport.setPlan(reader.get("推广计划"));
							groupReport.setPlanId(reader.get("推广计划ID")==null || reader.get("推广计划ID").trim().length() == 0 || "--".equals(reader.get("推广计划ID"))|| "-".equals(reader.get("推广计划ID"))?null:Long.valueOf(reader.get("推广计划ID")));
							groupReport.setShowNum(reader.get("展示数")==null || reader.get("展示数").trim().length() == 0 || "--".equals(reader.get("展示数"))|| "-".equals(reader.get("展示数"))?null:Long.valueOf(reader.get("展示数")));
							groupReportResult.add(groupReport);
						}
						report.setResult(reportType, team, new Result<GroupReport>(true, groupReportResult));break;
						case ideaReport: case ideaReport_mobile: LinkedList<IdeaReport> ideaReportResult = new LinkedList<IdeaReport>();
						while(reader.readRecord()) {
							IdeaReport ideaReport = new IdeaReport();
							ideaReport.setAccount(reader.get("账户")==null || reader.get("账户").trim().length() == 0 || "--".equals(reader.get("账户")) || "-".equals(reader.get("账户"))?null:reader.get("账户"));
							ideaReport.setAvgClickPrice(reader.get("点击均价")==null || reader.get("点击均价").trim().length() == 0 || "--".equals(reader.get("点击均价")) || "-".equals(reader.get("点击均价"))?null:Double.valueOf(reader.get("点击均价")));
							ideaReport.setClickNum(reader.get("点击数")==null || reader.get("点击数").trim().length() == 0 || "--".equals(reader.get("点击数"))|| "-".equals(reader.get("点击数"))?null:Long.valueOf(reader.get("点击数")));
							ideaReport.setClickRate(reader.get("点击率"));
							ideaReport.setCost(reader.get("消耗")==null || reader.get("消耗").trim().length() == 0 || "--".equals(reader.get("消耗"))|| "-".equals(reader.get("消耗"))?null:Double.valueOf(reader.get("消耗")));
							ideaReport.setDate(reader.get("日期").indexOf('/') >= 0 ? formatter.parse(reader.get("日期")) : formatter0.parse(reader.get("日期")));
							ideaReport.setGroup(reader.get("推广组"));
							ideaReport.setGroupId(reader.get("推广组ID")==null || reader.get("推广组ID").trim().length() == 0 || "--".equals(reader.get("推广组ID"))|| "-".equals(reader.get("推广组ID"))?null:Long.valueOf(reader.get("推广组ID")));
							ideaReport.setIdea(reader.get("创意标题"));
							ideaReport.setIdeaAccessUrl(reader.get("创意访问URL"));
							ideaReport.setIdeaDesc1(reader.get("创意描述1"));
							ideaReport.setIdeaDesc2(reader.get("创意描述2"));
							ideaReport.setIdeaId(reader.get("创意id")==null || reader.get("创意id").trim().length() == 0 || "--".equals(reader.get("创意id"))|| "-".equals(reader.get("创意id"))?null:Long.valueOf(reader.get("创意id")));
							ideaReport.setIdeaMobilAccessUrl(reader.get("创意移动访问URL"));
							ideaReport.setIdeaMobilShowUrl(reader.get("创意移动展示URL"));
							ideaReport.setIdeaShowUrl(reader.get("创意展示URL"));
							ideaReport.setKeyworddAvgSort(reader.get("关键词平均排名"));
							ideaReport.setPlan(reader.get("推广计划"));
							ideaReport.setPlanId(reader.get("推广计划ID")==null || reader.get("推广计划ID").trim().length() == 0 || "--".equals(reader.get("推广计划ID"))|| "-".equals(reader.get("推广计划ID"))?null:Long.valueOf(reader.get("推广计划ID")));
							ideaReport.setShowNum(reader.get("展示数")==null || reader.get("展示数").trim().length() == 0 || "--".equals(reader.get("展示数"))|| "-".equals(reader.get("展示数"))?null:Long.valueOf(reader.get("展示数")));
							ideaReportResult.add(ideaReport);
						}
						report.setResult(reportType, team, new Result<IdeaReport>(true, ideaReportResult));break;
						case keyWordReport: case keyWordReport_mobile: LinkedList<KeyWordReport> keyWordReportResult = new LinkedList<KeyWordReport>();
						while(reader.readRecord()) {
							KeyWordReport keyWordReport = new KeyWordReport();
							keyWordReport.setAccount(reader.get("账户")==null || reader.get("账户").trim().length() == 0|| "--".equals(reader.get("账户"))|| "-".equals(reader.get("账户"))?null:reader.get("账户"));
							keyWordReport.setAvgClickPrice(reader.get("点击均价")==null || reader.get("点击均价").trim().length() == 0|| "--".equals(reader.get("点击均价"))|| "-".equals(reader.get("点击均价"))?null:Double.valueOf(reader.get("点击均价")));
							keyWordReport.setClickNum(reader.get("点击数")==null || reader.get("点击数").trim().length() == 0|| "--".equals(reader.get("点击数"))|| "-".equals(reader.get("点击数"))?null:Long.valueOf(reader.get("点击数")));
							keyWordReport.setClickRate(reader.get("点击率"));
							keyWordReport.setCost(reader.get("消耗")==null || reader.get("消耗").trim().length() == 0|| "--".equals(reader.get("消耗"))|| "-".equals(reader.get("消耗"))?null:Double.valueOf(reader.get("消耗")));
							keyWordReport.setDate(reader.get("日期").indexOf('/') >= 0 ? formatter.parse(reader.get("日期")) : formatter0.parse(reader.get("日期")));
							keyWordReport.setGroup(reader.get("推广组"));
							keyWordReport.setGroupId(reader.get("推广组ID")==null || reader.get("推广组ID").trim().length() == 0|| "--".equals(reader.get("推广组ID"))|| "-".equals(reader.get("推广组ID"))?null:Long.valueOf(reader.get("推广组ID")));
							keyWordReport.setKeyWord(reader.get("关键词"));
							keyWordReport.setKeyWordId(reader.get("关键词id")==null || reader.get("关键词id").trim().length() == 0|| "--".equals(reader.get("关键词id"))|| "-".equals(reader.get("关键词id"))?null:Long.valueOf(reader.get("关键词id")));
							keyWordReport.setKeyworddAvgSort("--".equals(reader.get("关键词id"))?null:reader.get("关键词平均排名"));
							keyWordReport.setPlan(reader.get("推广计划"));
							keyWordReport.setPlanId(reader.get("推广计划ID")==null || reader.get("推广计划ID").trim().length() == 0|| "--".equals(reader.get("推广计划ID"))|| "-".equals(reader.get("推广计划ID"))?null:Long.valueOf(reader.get("推广计划ID")));
							keyWordReport.setShowNum(reader.get("展示数")==null || reader.get("展示数").trim().length() == 0|| "--".equals(reader.get("展示数"))|| "-".equals(reader.get("展示数"))?null:Long.valueOf(reader.get("展示数")));
							keyWordReportResult.add(keyWordReport);
						}
						report.setResult(reportType, team, new Result<KeyWordReport>(true, keyWordReportResult));break;
						case keyWordShowNoClickReport: case keyWordShowNoClickReport_mobile: LinkedList<KeyWordShowNoClickReport> keyWordShowNoClickReportResult = new LinkedList<KeyWordShowNoClickReport>();
						while(reader.readRecord()) {
							KeyWordShowNoClickReport keyWordShowNoClickReport = new KeyWordShowNoClickReport();
							keyWordShowNoClickReport.setAccount(reader.get("账户")==null || reader.get("账户").trim().length() == 0|| "--".equals(reader.get("账户"))|| "-".equals(reader.get("账户"))?null:reader.get("账户"));
							keyWordShowNoClickReport.setAvgClickPrice(reader.get("点击均价")==null || reader.get("点击均价").trim().length() == 0|| "--".equals(reader.get("点击均价"))|| "-".equals(reader.get("点击均价"))?null:Double.valueOf(reader.get("点击均价")));
							keyWordShowNoClickReport.setClickNum(reader.get("点击数")==null || reader.get("点击数").trim().length() == 0|| "--".equals(reader.get("点击数"))|| "-".equals(reader.get("点击数"))?null:Long.valueOf(reader.get("点击数")));
							keyWordShowNoClickReport.setCost(reader.get("消耗")==null || reader.get("消耗").trim().length() == 0|| "--".equals(reader.get("消耗"))|| "-".equals(reader.get("消耗"))?null:Double.valueOf(reader.get("消耗")));
							keyWordShowNoClickReport.setDate(reader.get("日期").indexOf('/') >= 0 ? formatter.parse(reader.get("日期")) : formatter0.parse(reader.get("日期")));
							keyWordShowNoClickReport.setGroup(reader.get("推广组"));
							keyWordShowNoClickReport.setGroupId(reader.get("推广组ID")==null || reader.get("推广组ID").trim().length() == 0|| "--".equals(reader.get("推广组ID"))|| "-".equals(reader.get("推广组ID"))?null:Long.valueOf(reader.get("推广组ID")));
							keyWordShowNoClickReport.setKeyWord(reader.get("关键词"));
							keyWordShowNoClickReport.setKeyWordId(reader.get("关键词id")==null || reader.get("关键词id").trim().length() == 0|| "--".equals(reader.get("关键词id"))|| "-".equals(reader.get("关键词id"))?null:Long.valueOf(reader.get("关键词id")));
							keyWordShowNoClickReport.setPlan(reader.get("推广计划"));
							keyWordShowNoClickReport.setPlanId(reader.get("推广计划ID")==null || reader.get("推广计划ID").trim().length() == 0|| "--".equals(reader.get("推广计划ID"))|| "-".equals(reader.get("推广计划ID"))?null:Long.valueOf(reader.get("推广计划ID")));
							keyWordShowNoClickReportResult.add(keyWordShowNoClickReport);
						}
						report.setResult(reportType, team, new Result<KeyWordShowNoClickReport>(true, keyWordShowNoClickReportResult));break;
						case planReport: case planReport_mobile: LinkedList<PlanReport> planReportResult = new LinkedList<PlanReport>();
						while(reader.readRecord()) {
							PlanReport planReport = new PlanReport();
							planReport.setAccount(reader.get("账户")==null || reader.get("账户").trim().length() == 0|| "--".equals(reader.get("账户"))|| "-".equals(reader.get("账户"))?null:reader.get("账户"));
							planReport.setAvgClickPrice(reader.get("点击均价")==null || reader.get("点击均价").trim().length() == 0|| "--".equals(reader.get("点击均价"))|| "-".equals(reader.get("点击均价"))?null:Double.valueOf(reader.get("点击均价")));
							planReport.setClickNum(reader.get("点击数")==null || reader.get("点击数").trim().length() == 0|| "--".equals(reader.get("点击数"))|| "-".equals(reader.get("点击数"))?null:Long.valueOf(reader.get("点击数")));
							planReport.setCost(reader.get("消耗")==null || reader.get("消耗").trim().length() == 0|| "--".equals(reader.get("消耗"))|| "-".equals(reader.get("消耗"))?null:Double.valueOf(reader.get("消耗")));
							planReport.setDate(reader.get("日期").indexOf('/') >= 0 ? formatter.parse(reader.get("日期")) : formatter0.parse(reader.get("日期")));
							planReport.setClickRate(reader.get("点击率"));
							planReport.setKeyworddAvgSort(reader.get("关键词平均排名"));
							planReport.setPlan(reader.get("推广计划"));
							planReport.setPlanId(reader.get("推广计划ID")==null || reader.get("推广计划ID").trim().length() == 0|| "--".equals(reader.get("推广计划ID"))|| "-".equals(reader.get("推广计划ID"))?null:Long.valueOf(reader.get("推广计划ID")));
							planReport.setShowNum(reader.get("展示数")==null || reader.get("展示数").trim().length() == 0|| "--".equals(reader.get("展示数"))|| "-".equals(reader.get("展示数"))?null:Long.valueOf(reader.get("展示数")));
							planReportResult.add(planReport);
						}
						report.setResult(reportType, team, new Result<PlanReport>(true, planReportResult));break;
						case searchWordReport: case searchWordReport_mobile:  LinkedList<SearchWordReport> searchWordReportResult = new LinkedList<SearchWordReport>();
						while(reader.readRecord()) {
							SearchWordReport searchWordReport = new SearchWordReport();
							searchWordReport.setAccount(reader.get("账户")==null || reader.get("账户").trim().length() == 0|| "--".equals(reader.get("账户"))|| "-".equals(reader.get("账户"))?null:reader.get("账户"));
							searchWordReport.setAvgClickPrice(reader.get("点击均价")==null || reader.get("点击均价").trim().length() == 0|| "--".equals(reader.get("点击均价"))|| "-".equals(reader.get("点击均价"))?null:Double.valueOf(reader.get("点击均价")));
							searchWordReport.setClickNum(reader.get("点击数")==null || reader.get("点击数").trim().length() == 0|| "--".equals(reader.get("点击数"))|| "-".equals(reader.get("点击数"))?null:Long.valueOf(reader.get("点击数")));
							searchWordReport.setCost(reader.get("消耗")==null || reader.get("消耗").trim().length() == 0|| "--".equals(reader.get("消耗"))|| "-".equals(reader.get("消耗"))?null:Double.valueOf(reader.get("消耗")));
							searchWordReport.setDate(reader.get("日期").indexOf('/') >= 0 ? formatter.parse(reader.get("日期")) : formatter0.parse(reader.get("日期")));
							searchWordReport.setGroup(reader.get("推广组"));
							searchWordReport.setGroupId(reader.get("推广组ID")==null || reader.get("推广组ID").trim().length() == 0|| "--".equals(reader.get("推广组ID"))|| "-".equals(reader.get("推广组ID"))?null:Long.valueOf(reader.get("推广组ID")));
							searchWordReport.setIdea(reader.get("创意标题"));
							searchWordReport.setIdeaAccessUrl(reader.get("创意访问URL"));
							searchWordReport.setIdeaDesc1(reader.get("创意描述1"));
							searchWordReport.setIdeaDesc2(reader.get("创意描述2"));
							searchWordReport.setIdeaId(reader.get("创意id")==null || reader.get("创意id").trim().length() == 0|| "--".equals(reader.get("创意id"))|| "-".equals(reader.get("创意id"))?null:Long.valueOf(reader.get("创意id")));
							searchWordReport.setIdeaMobilAccessUrl(reader.get("创意移动访问URL"));
							searchWordReport.setIdeaMobilShowUrl(reader.get("创意移动展示URL"));
							searchWordReport.setKeyWord(reader.get("关键词"));
							searchWordReport.setSearchWord(reader.get("搜索词"));
							searchWordReport.setPlan(reader.get("推广计划"));
							searchWordReport.setPlanId(reader.get("推广计划ID")==null || reader.get("推广计划ID").trim().length() == 0|| "--".equals(reader.get("推广计划ID"))|| "-".equals(reader.get("推广计划ID"))?null:Long.valueOf(reader.get("推广计划ID")));
							searchWordReport.setShowNum(reader.get("展示数")==null || reader.get("展示数").trim().length() == 0|| "--".equals(reader.get("展示数"))|| "-".equals(reader.get("展示数"))?null:Long.valueOf(reader.get("展示数")));
							searchWordReportResult.add(searchWordReport);
						}
						report.setResult(reportType, team, new Result<SearchWordReport>(true, searchWordReportResult));break;
						} 
					} catch (Exception e) {
						report.setResult(reportType, team, new Result(false, null));
						logger.process(Task.getMessage(e), 10205, semAccounts.getString("tenant_id"));
					}
				} else executor.process(this, System.currentTimeMillis() + interval * 9 + SubTask.random.nextInt(interval * 2));
			}  catch (BusinessException e) {
				if (e.getResHeader().getFailures() != null && e.getResHeader().getFailures().size() > 0 && e.getResHeader().getFailures().get(0).getCode() == 1025009) report.setResult(reportType, team, new Result<Object>(true, Collections.EMPTY_LIST));
				else report.setResult(reportType, team, new Result(false, null));
				logger.process(Task.getMessage(e), 10206, semAccounts.getString("tenant_id"));
			} catch (Exception e) {
				if (count.add()) executor.process(this, System.currentTimeMillis() + interval * 9 + SubTask.random.nextInt(interval * 2));
				else report.setResult(reportType, team, new Result(false, null));
				logger.process(Task.getMessage(e), 10207, semAccounts.getString("tenant_id"));
			} else  {
				report.finish("force quit");
				logger.process("force quit", 10208, semAccounts.getString("tenant_id"));
			}
		}
	}
}