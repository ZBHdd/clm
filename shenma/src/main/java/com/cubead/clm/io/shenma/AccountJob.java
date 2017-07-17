package com.cubead.clm.io.shenma;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.charset.Charset;
import java.util.LinkedList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.json.JsonObject;
import javax.json.JsonValue;
import javax.json.JsonValue.ValueType;

import com.cubead.clm.IProcessor;
import com.cubead.clm.io.shenma.data.DataStorage;
import com.cubead.clm.io.shenma.data.DataStorage.UserInfo;
import com.cubead.clm.io.shenma.data.DataStorage.UserinfoType;
import com.cubead.clm.io.shenma.data.msg.Adgroups;
import com.cubead.clm.io.shenma.data.msg.AdvancedApp;
import com.cubead.clm.io.shenma.data.msg.AdvancedCssSublinks;
import com.cubead.clm.io.shenma.data.msg.AdvancedGoldens;
import com.cubead.clm.io.shenma.data.msg.AdvancedPicTexts;
import com.cubead.clm.io.shenma.data.msg.Apps;
import com.cubead.clm.io.shenma.data.msg.Campaigns;
import com.cubead.clm.io.shenma.data.msg.Creatives;
import com.cubead.clm.io.shenma.data.msg.Keywords;
import com.cubead.clm.io.shenma.data.msg.Sublinks;

public class AccountJob implements Runnable{
	private final static String getTaskStateurl="https://e.sm.cn/api/task/getTaskState";
	private final static String downloadurl="https://e.sm.cn/api/file/download";
	private IProcessor<Object, Boolean> executor;
	private IProcessor<Object, Boolean> logger;
	private final Integer interval;
	private Count count;
	private final DataStorage report;
	private IProcessor<Object, Boolean> stop;
	private JsonObject semAccounts;
	private final static String url="https://e.sm.cn/api/bulkJob/getAllObjects";

	public AccountJob(IProcessor<Object, Boolean> executor,
			IProcessor<Object, Boolean> logger, Integer interval, Count count,
			DataStorage report, IProcessor<Object, Boolean> stop,
			JsonObject semAccounts) {
		this.executor = executor;
		this.logger = logger;
		this.interval = interval;
		this.count = count;
		this.report = report;
		this.stop = stop;
		this.semAccounts = semAccounts;
	}

	@Override
	public void run() {
		if (!stop.process(semAccounts.getString("tenant_id"), "account")) try{
			String msg = "{\"header\":{\"username\":\""+semAccounts.getString("name")+"\",\"password\": \""+semAccounts.getString("password")+"\",\"token\": \""+semAccounts.getString("token")+"\"},\"body\":{}}";
			HttpClient req = new HttpClient();
			JsonObject resp = req.rest("POST", url,msg);
			if (resp.getJsonObject("header").getInt("status") == 0) 
				executor.process(new Job(resp.getJsonObject("body").getJsonNumber("taskId").longValue()), System.currentTimeMillis() + interval * 9 + SubTask.random.nextInt(interval * 2));
			else if (resp.getJsonObject("header").getJsonArray("failures").size() > 0 && resp.getJsonObject("header").getJsonArray("failures").getJsonObject(0).getInt("code") == 8502) 
				executor.process(this, System.currentTimeMillis() + interval * 9 + SubTask.random.nextInt(interval * 2));
			else {
				report.setUserinfo(report.new UserInfo(false));
				logger.process(resp, 10108, semAccounts.getString("tenant_id"));
			}
		} catch (Exception e) {
			if (count.add()) executor.process(this, System.currentTimeMillis() + interval * 9 + SubTask.random.nextInt(interval * 2));
			else  report.setUserinfo(report.new UserInfo(false));
			logger.process(Task.getMessage(e), 10107, semAccounts.getString("tenant_id"));
		} else  {
			report.finish("force quit");
			logger.process("force stop", 10106, semAccounts.getString("tenant_id"));
		}
	}
	
	protected class Job implements Runnable{
		private final Long taskId;
		
		public Job(Long taskId) {
			this.taskId = taskId;
		}

		@Override
		public void run() {
			if (!stop.process(semAccounts.getString("tenant_id"), "account")) try{
				String msg = "{\"header\":{\"username\":\""+semAccounts.getString("name")+"\",\"password\": \""+semAccounts.getString("password")+"\",\"token\": \""+semAccounts.getString("token")+"\"},\"body\":{\"taskId\":"+taskId+"}}";
				HttpClient req = new HttpClient();
				JsonObject resp = req.rest("POST", getTaskStateurl,msg);
				JsonValue body = resp.get("body");
				if (body != null && ValueType.OBJECT.equals(body.getValueType()) && "FINISHED".equals(((JsonObject)body).getString("status"))) {
					String download = "{\"header\":{\"username\":\""+semAccounts.getString("name")+"\",\"password\": \""+semAccounts.getString("password")+"\",\"token\": \""+semAccounts.getString("token")+"\"},\"body\":{\"fileId\":"+((JsonObject)body).getJsonNumber("fileId")+"}}";
					byte[] respbody = req.request("POST", downloadurl,download);
					try {
						byte[] buf = new byte[8196];
						ZipInputStream stream = new ZipInputStream(new ByteArrayInputStream(respbody));
						ZipEntry entry = stream.getNextEntry();
						entry.getSize();
						UserInfo userInfo = report.new UserInfo(true);
						while (entry != null) try {
							if (entry.getName().indexOf(".csv") > -1) {
								ByteArrayOutputStream out = new ByteArrayOutputStream();
								int count;
								while ((count = stream.read(buf)) > -1) out.write(buf, 0, count);
								CsvReader reader = new CsvReader(new ByteArrayInputStream(out.toByteArray()), Charset.forName("utf-8"));
								reader.readHeaders();
								switch(UserinfoType.valueOf(entry.getName().split("_")[0])) {
									case Adgroups : LinkedList<Adgroups> adgroupsList = new LinkedList<Adgroups>();
									while(reader.readRecord()) {
										Adgroups adgroups = new Adgroups();
										adgroups.setAdgroupId(reader.get("adgroupId")==null?null:Long.valueOf(reader.get("adgroupId")));
										adgroups.setAdgroupName(reader.get("adgroupName"));
										adgroups.setAdPlatformOS(reader.get("adPlatformOS")==null?null:Long.valueOf(reader.get("adPlatformOS")));
										adgroups.setCampaignId(reader.get("﻿campaignId")==null|| reader.get("﻿campaignId").trim().length() == 0?null:Long.valueOf(reader.get("﻿campaignId")));
										adgroups.setCampaignName(reader.get("campaignName"));
										adgroups.setExactNegativeWords(reader.get("exactNegativeWords"));
										adgroups.setMaxPrice(reader.get("maxPrice")==null?null:Float.valueOf(reader.get("maxPrice")));
										adgroups.setNegativeWords(reader.get("negativeWords"));
										adgroups.setPause("TRUE".equals(reader.get("pause")));
										adgroups.setStatus(reader.get("status")==null?null:Integer.valueOf(reader.get("status")));
										adgroupsList.add(adgroups);
									}
									userInfo.setAdgroups(adgroupsList);
									break;
									case AdvancedApps : LinkedList<AdvancedApp> advancedAppUserinfoList = new LinkedList<AdvancedApp>();
									while(reader.readRecord()) {
										AdvancedApp advancedApp = new AdvancedApp();
										advancedApp.setAdgroupId(reader.get("adgroupId")==null?null:Long.valueOf(reader.get("adgroupId")));
										advancedApp.setAdgroupName(reader.get("adgroupName"));
										advancedApp.setAdvancedAppId(reader.get("advancedAppId")==null?null:Long.valueOf(reader.get("advancedAppId")));
										advancedApp.setAndroidDownloadAddr(reader.get("androidDownloadAddr"));
										advancedApp.setAppName(reader.get("appName"));
										advancedApp.setCampaignId(reader.get("﻿campaignId")==null|| reader.get("﻿campaignId").trim().length() == 0?null:Long.valueOf(reader.get("﻿campaignId")));
										advancedApp.setCampaignName(reader.get("campaignName"));
										advancedApp.setDescription(reader.get("description"));
										advancedApp.setDestinationUrl(reader.get("destinationUrl"));
										advancedApp.setDisplayUrl(reader.get("displayUrl"));
										advancedApp.setImg(reader.get("img"));
										advancedApp.setIosDownloadAddr(reader.get("iosDownloadAddr"));
										advancedApp.setNegativeReason(reader.get("negativeReason"));
										advancedApp.setPause("TRUE".equals(reader.get("pause")));
										advancedApp.setStatus(reader.get("status")==null?null:Integer.valueOf(reader.get("status")));
										advancedApp.setTitle(reader.get("title"));
										advancedAppUserinfoList.add(advancedApp);
									}
									userInfo.setAdvancedApp(advancedAppUserinfoList);
									break;
									case AdvancedCssSublinks : LinkedList<AdvancedCssSublinks> advanceCssSublinksList = new LinkedList<AdvancedCssSublinks>();
									while(reader.readRecord()) {
										AdvancedCssSublinks advancedCss = new AdvancedCssSublinks();
										advancedCss.setAdgroupId(reader.get("adgroupId")==null?null:Long.valueOf(reader.get("adgroupId")));
										advancedCss.setAdgroupName(reader.get("adgroupName"));
										advancedCss.setAdvancedCssSublinkId(reader.get("advancedCssSublinkId")==null?null:Long.valueOf(reader.get("advancedCssSublinkId")));
										advancedCss.setCampaignId(reader.get("﻿campaignId")==null|| reader.get("﻿campaignId").trim().length() == 0?null:Long.valueOf(reader.get("﻿campaignId")));
										advancedCss.setCampaignName(reader.get("campaignName"));
										advancedCss.setDestination(reader.get("destination"));
										advancedCss.setDestinationUrl(reader.get("destinationUrl"));
										advancedCss.setDisplayUrl(reader.get("displayUrl"));
										advancedCss.setImg(reader.get("img"));
										advancedCss.setNegativeReason(reader.get("negativeReason"));
										advancedCss.setPause("TRUE".equals(reader.get("pause")));
										advancedCss.setStatus(reader.get("status")==null?null:Integer.valueOf(reader.get("status")));
										advancedCss.setSubDescription1(reader.get("subDescription1"));
										advancedCss.setSubDescription2(reader.get("subDescription2"));
										advancedCss.setSubDescriptionUrl1(reader.get("subDescriptionUrl1"));
										advancedCss.setSubDescriptionUrl2(reader.get("subDescriptionUrl2"));
										advancedCss.setTitle(reader.get("title"));
										advanceCssSublinksList.add(advancedCss);
									}
									userInfo.setAdvancedCssSublinks(advanceCssSublinksList);
									break;
									case AdvancedGoldens : LinkedList<AdvancedGoldens> advancedGoldensList = new LinkedList<AdvancedGoldens>();
									while(reader.readRecord()) {
										AdvancedGoldens advancedGoldens = new AdvancedGoldens();
										advancedGoldens.setAdgroupId(reader.get("adgroupId")==null?null:Long.valueOf(reader.get("adgroupId")));
										advancedGoldens.setAdgroupName(reader.get("adgroupName"));
										advancedGoldens.setAndroidDownloadAddr(reader.get("androidDownloadAddr"));
										advancedGoldens.setCampaignId(reader.get("﻿campaignId")==null || reader.get("﻿campaignId").trim().length() == 0 ? null : Long.valueOf(reader.get("﻿campaignId")));
										advancedGoldens.setCampaignName(reader.get("campaignName"));
										advancedGoldens.setDestinationUrl(reader.get("destinationUrl"));
										advancedGoldens.setDisplayUrl(reader.get("displayUrl"));
										advancedGoldens.setImg(reader.get("img"));
										advancedGoldens.setIosDownloadAddr(reader.get("iosDownloadAddr"));
										advancedGoldens.setNegativeReason(reader.get("negativeReason"));
										advancedGoldens.setPause("TRUE".equals(reader.get("pause")));
										advancedGoldens.setStatus(reader.get("status")==null?null:Integer.valueOf(reader.get("status")));
										advancedGoldens.setTitle(reader.get("title"));
										advancedGoldens.setAdvancedGoldenId(reader.get("advancedGoldenId")==null?null:Long.valueOf(reader.get("advancedGoldenId")));
										advancedGoldens.setDestination(reader.get("destination"));
										advancedGoldens.setLabel1(reader.get("label1"));
										advancedGoldens.setLabel2(reader.get("label2"));
										advancedGoldens.setLabel3(reader.get("label3"));
										advancedGoldens.setPhoneNumber(reader.get("phoneNumber"));
										advancedGoldens.setSubdescription11(reader.get("subdescription11"));
										advancedGoldens.setSubdescription12(reader.get("subdescription12"));
										advancedGoldens.setSubdescription13(reader.get("subdescription13"));
										advancedGoldens.setSubdescription21(reader.get("subdescription21"));
										advancedGoldens.setSubdescription22(reader.get("subdescription22"));
										advancedGoldens.setSubdescription23(reader.get("subdescription23"));
										advancedGoldens.setSubdescription31(reader.get("subdescription31"));
										advancedGoldens.setSubdescription32(reader.get("subdescription32"));
										advancedGoldens.setSubdescription33(reader.get("subdescription33"));
										advancedGoldens.setSubdestinationUrl11(reader.get("subdestinationUrl11"));
										advancedGoldens.setSubdestinationUrl12(reader.get("subdestinationUrl12"));
										advancedGoldens.setSubdestinationUrl13(reader.get("subdestinationUrl13"));
										advancedGoldens.setSubdestinationUrl21(reader.get("subdestinationUrl21"));
										advancedGoldens.setSubdestinationUrl22(reader.get("subdestinationUrl22"));
										advancedGoldens.setSubdestinationUrl23(reader.get("subdestinationUrl23"));
										advancedGoldens.setSubdestinationUrl31(reader.get("subdestinationUrl31"));
										advancedGoldens.setSubdestinationUrl32(reader.get("subdestinationUrl32"));
										advancedGoldens.setSubdestinationUrl33(reader.get("subdestinationUrl33"));
										
									}
									userInfo.setAdvancedGoldens(advancedGoldensList);;
									break;
									case Apps : LinkedList<Apps> appsList = new LinkedList<Apps>();
									while(reader.readRecord()) {
										Apps apps = new Apps();
										apps.setAdgroupId(reader.get("adgroupId")==null?null:Long.valueOf(reader.get("adgroupId")));
										apps.setAdgroupName(reader.get("adgroupName"));
										apps.setAppId(reader.get("appId")==null?null:Long.valueOf(reader.get("appId")));
										apps.setAppName(reader.get("appName"));
										apps.setCampaignId(reader.get("﻿campaignId")==null|| reader.get("﻿campaignId").trim().length() == 0?null:Long.valueOf(reader.get("﻿campaignId")));
										apps.setCampaignName(reader.get("campaignName"));
										apps.setAppLogo(reader.get("appLogo"));
										apps.setDetailAddrAndroid(reader.get("detailAddrAndroid"));
										apps.setDownloadAddrAndroid(reader.get("downloadAddrAndroid"));
										apps.setDownloadAddrIOS(reader.get("downloadAddrIOS"));
										apps.setNegativeReason(reader.get("negativeReason"));
										apps.setPause("TRUE".equals(reader.get("pause")));
										apps.setStatus(reader.get("status")==null?null:Integer.valueOf(reader.get("status")));
										appsList.add(apps);
									}
									userInfo.setApps(appsList);
									break;
									case Campaigns : LinkedList<Campaigns> campaignsList = new LinkedList<Campaigns>();
									while(reader.readRecord()) {
										Campaigns campaigns = new Campaigns();
										campaigns.setBudget(reader.get("budget")==null?null:Float.valueOf(reader.get("budget")));
										campaigns.setCampaignId(reader.get("﻿campaignId")==null|| reader.get("﻿campaignId").trim().length() == 0?null:Long.valueOf(reader.get("﻿campaignId")));
										campaigns.setCampaignName(reader.get("campaignName"));
										campaigns.setExactNegativeWords(reader.get("exactNegativeWords"));
										campaigns.setExcludeIp(reader.get("excludeIp"));
										campaigns.setPause("TRUE".equals(reader.get("pause")));
										campaigns.setStatus(reader.get("status")==null?null:Integer.valueOf(reader.get("status")));
										campaigns.setNegativeWords(reader.get("negativeWords"));
										campaigns.setRegionTarget(reader.get("regionTarget"));
										campaigns.setSchedule(reader.get("schedule"));
										campaigns.setShowProb(reader.get("showProb")==null?null:Long.valueOf(reader.get("showProb")));
										campaignsList.add(campaigns);
									}
									userInfo.setCampains(campaignsList);
									break;
									case Creatives : LinkedList<Creatives> creativesList = new LinkedList<Creatives>();
									while(reader.readRecord()) {
										Creatives creatives = new Creatives();
										creatives.setAdgroupId(reader.get("adgroupId")==null?null:Long.valueOf(reader.get("adgroupId")));
										creatives.setCampaignId(reader.get("﻿campaignId")==null|| reader.get("﻿campaignId").trim().length() == 0?null:Long.valueOf(reader.get("﻿campaignId")));
										creatives.setCampaignName(reader.get("campaignName"));
										creatives.setAdgroupName(reader.get("adgroupName"));
										creatives.setCreativeId(reader.get("creativeId")==null?null:Long.valueOf(reader.get("creativeId")));
										creatives.setDescription(reader.get("description"));
										creatives.setDestinationUrl(reader.get("destinationUrl"));
										creatives.setDisplayUrl(reader.get("displayUrl"));
										creatives.setNegativeReason(reader.get("negativeReason"));
										creatives.setPause("TRUE".equals(reader.get("pause")));
										creatives.setStatus(reader.get("status")==null?null:Integer.valueOf(reader.get("status")));
										creatives.setTitle(reader.get("title"));
										creativesList.add(creatives);
									}
									userInfo.setCreatives(creativesList);
									break;
									case Keywords : LinkedList<Keywords> keywordsList = new LinkedList<Keywords>();
									while(reader.readRecord()) {
										Keywords keywords = new Keywords();
										keywords.setPrice(reader.get("price")==null?null:Float.valueOf(reader.get("price")));
										keywords.setQuality(reader.get("quality")==null?null:Long.valueOf(reader.get("quality")));
										keywords.setCampaignId(reader.get("﻿campaignId")==null|| reader.get("﻿campaignId").trim().length() == 0?null:Long.valueOf(reader.get("﻿campaignId")));
										keywords.setCampaignName(reader.get("campaignName"));
										keywords.setNegativeReason(reader.get("negativeReason"));
										keywords.setMatchType(reader.get("matchType")==null?null:Integer.valueOf(reader.get("matchType")));
										keywords.setPause("TRUE".equals(reader.get("pause")));
										keywords.setKeywordId(reader.get("keywordId")==null?null:Long.valueOf(reader.get("keywordId")));
										keywords.setKeyword(reader.get("keyword"));
										keywords.setDestinationUrl(reader.get("destinationUrl"));
										keywords.setAdgroupName(reader.get("adgroupName"));
										keywords.setAdgroupId(reader.get("adgroupId")==null?null:Long.valueOf(reader.get("adgroupId")));
										keywords.setStatus(reader.get("status")==null?null:Integer.valueOf(reader.get("status")));
										keywordsList.add(keywords);
									}
									userInfo.setKeywords(keywordsList);
									break;
									case Sublinks : LinkedList<Sublinks> sublinksList = new LinkedList<Sublinks>();
									while(reader.readRecord()) {
										Sublinks sublinks = new Sublinks();
										sublinks.setAdgroupName(reader.get("adgroupName"));
										sublinks.setAdgroupId(reader.get("adgroupId")==null?null:Long.valueOf(reader.get("adgroupId")));
										sublinks.setCampaignId(reader.get("﻿campaignId")==null|| reader.get("﻿campaignId").trim().length() == 0?null:Long.valueOf(reader.get("﻿campaignId")));
										sublinks.setCampaignName(reader.get("campaignName"));
										sublinks.setDescription1(reader.get("description1"));
										sublinks.setDescription2(reader.get("description2"));
										sublinks.setDescription3(reader.get("description3"));
										sublinks.setDescription4(reader.get("description4"));
										sublinks.setDestinationUrl1(reader.get("destinationUrl1"));
										sublinks.setDestinationUrl2(reader.get("destinationUrl2"));
										sublinks.setDestinationUrl3(reader.get("destinationUrl3"));
										sublinks.setDestinationUrl4(reader.get("destinationUrl4"));
										sublinks.setSublinkId(reader.get("sublinkId")==null?null:Long.valueOf(reader.get("sublinkId")));
										sublinks.setPause("TRUE".equals(reader.get("pause")));
										sublinks.setNegativeReason(reader.get("negativeReason"));
										sublinks.setStatus(reader.get("status")==null?null:Integer.valueOf(reader.get("status")));
										sublinksList.add(sublinks);
									}
									userInfo.setSublinks(sublinksList);	break;
									case AdvancedPicTexts:LinkedList<AdvancedPicTexts> advancedPicTextsList = new LinkedList<AdvancedPicTexts>();
									while(reader.readRecord()) {
										AdvancedPicTexts advancedPicTexts = new AdvancedPicTexts();
										advancedPicTexts.setCampaignId(reader.get("﻿campaignId")==null|| reader.get("﻿campaignId").trim().length() == 0?null:Long.valueOf(reader.get("﻿campaignId")));
										advancedPicTexts.setCampaignName(reader.get("campaignName"));
										advancedPicTexts.setNegativeReason(reader.get("negativeReason"));
										advancedPicTexts.setPause("TRUE".equals(reader.get("pause")));
										advancedPicTexts.setAdgroupName(reader.get("adgroupName"));
										advancedPicTexts.setAdvancedCssId(reader.get("advancedCssId")==null?null:Long.valueOf(reader.get("advancedCssId")));
										advancedPicTexts.setTitle(reader.get("title"));
										advancedPicTexts.setImg(reader.get("img"));
										advancedPicTexts.setDestination1(reader.get("destination1"));
										advancedPicTexts.setDestination2(reader.get("destination2"));
										advancedPicTexts.setDestination3(reader.get("destination3"));
										advancedPicTexts.setPhoneNumber(reader.get("phoneNumber"));
										advancedPicTexts.setDisplayUrl(reader.get("displayUrl"));
										advancedPicTexts.setDestinationUrl(reader.get("destinationUrl"));
										advancedPicTexts.setNegativeReason(reader.get("negativeReason"));
										advancedPicTexts.setAdgroupId(reader.get("adgroupId")==null?null:Long.valueOf(reader.get("adgroupId")));
										advancedPicTexts.setStatus(reader.get("status")==null?null:Integer.valueOf(reader.get("status")));
										advancedPicTextsList.add(advancedPicTexts);
									}
									userInfo.setAdvancedPicTexts(advancedPicTextsList);
									break;
								}
							}
						} catch (Exception e) {
							logger.process(Task.getMessage(e), 10105, semAccounts.getString("tenant_id"));
						} finally {
							entry = stream.getNextEntry();
						}
						if (userInfo.isFull()) report.setUserinfo(userInfo);
						else report.setUserinfo(report.new UserInfo(false));
					} catch (Exception e) {
						logger.process(Task.getMessage(e), 10104, semAccounts.getString("tenant_id"));
					}
				} else executor.process(this, System.currentTimeMillis() + interval * 9 + SubTask.random.nextInt(interval * 2));
			} catch (Exception e) {
				if (count.add()) executor.process(this, System.currentTimeMillis() + interval * 9 + SubTask.random.nextInt(interval * 2));
				else  report.setUserinfo(report.new UserInfo(false));
				logger.process(Task.getMessage(e), 10103, semAccounts.getString("tenant_id"));
			} else  {
				report.finish("force quit");
				logger.process("force stop", 10102, semAccounts.getString("tenant_id"));
			}	
		}
	}
}