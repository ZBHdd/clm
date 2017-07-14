package com.cubead.clm.io.sogou;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.charset.Charset;
import java.util.LinkedList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.json.JsonObject;

import com.cubead.clm.IProcessor;
import com.cubead.clm.io.sogou.HttpClient;
import com.cubead.clm.io.sogou.data.DataStorage;
import com.cubead.clm.io.sogou.data.accountMsg.CpcIdeaType;
import com.cubead.clm.io.sogou.data.accountMsg.CpcType;
import com.cubead.clm.io.sogou.data.DataStorage.UserInfo;
import com.cubead.clm.io.sogou.data.DataStorage.UserinfoType;
import com.sogou.api.client.core.SogouAdServiceFactory;
import com.sogou.api.client.exception.BusinessException;
import com.sogou.api.client.wrapper.AccountDownloadServiceWrapper;
import com.sogou.api.sem.v1.account.AccountInfoType;
import com.sogou.api.sem.v1.accountdownload.AccountDownloadService;
import com.sogou.api.sem.v1.accountdownload.AccountFileType;
import com.sogou.api.sem.v1.accountdownload.GetAccountFilePathRequest;
import com.sogou.api.sem.v1.accountdownload.GetAccountFileRequest;
import com.sogou.api.sem.v1.accountdownload.GetAccountFileStatusRequest;

import com.sogou.api.sem.v1.cpcextraidea.CpcExIdeaType;
import com.sogou.api.sem.v1.cpcextraidea.ExIdeaPicType;
import com.sogou.api.sem.v1.cpcgrp.CpcGrpType;

import com.sogou.api.sem.v1.cpcplan.CpcPlanType;

public class AccountJob implements Runnable{
	private IProcessor<Object, Boolean> executor;
	private IProcessor<Object, Boolean> logger;
	private final Integer interval;
	private Count count;
	private final DataStorage report;
	private IProcessor<Object, Boolean> stop;
	private JsonObject semAccounts;
	private AccountDownloadServiceWrapper service;
	
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
		this.service = new AccountDownloadServiceWrapper(new SogouAdServiceFactory(semAccounts.getString("name"), semAccounts.getString("password"), 
				semAccounts.getString("token")).getWebService(AccountDownloadService.class));
	}

	@Override
	public void run() {
		if (!stop.process(semAccounts.getString("tenant_id"), "account")) try{
			GetAccountFileRequest request = new GetAccountFileRequest();
			AccountFileType type = new AccountFileType();
			request.setAccoutFileRequest(type);
			executor.process(new Job(service.getAccountFile(request).getAccountFileId()), System.currentTimeMillis() + interval * 9 + SubTask.random.nextInt(interval * 2));
		}  catch (BusinessException e) {
			report.setUserinfo(report.new UserInfo(false));
			logger.process(Task.getMessage(e), 10209, semAccounts.getString("tenant_id"));
		} catch (Exception e) {
			if (count.add()) executor.process(this, System.currentTimeMillis() + interval * 9 + SubTask.random.nextInt(interval * 2));
			else  report.setUserinfo(report.new UserInfo(false));
			logger.process(Task.getMessage(e), 10210, semAccounts.getString("tenant_id"));
		} else  {
			report.finish("force quit");
			logger.process("force stop", 10211, semAccounts.getString("tenant_id"));
		}
	}
	
	protected class Job implements Runnable{
		private final String taskId;
		
		public Job(String taskId) {
			this.taskId = taskId;
		}

		@Override
		public void run() {
			if (!stop.process(semAccounts.getString("tenant_id"), "account")) try{
				GetAccountFileStatusRequest request = new GetAccountFileStatusRequest();
				request.setAccountFileId(taskId);
				if (service.getAccountFileStatus(request).getIsGenerated() == 1) {
					GetAccountFilePathRequest r = new GetAccountFilePathRequest();
					r.setAccountFileId(taskId);
					byte[] respbody = new HttpClient().request("GET", service.getAccountFilePath(r).getAccountFilePath(),null);
					try {
						byte[] buf = new byte[8196];
						ZipInputStream stream = new ZipInputStream(new ByteArrayInputStream(respbody));
						ZipEntry entry = stream.getNextEntry();
						UserInfo userInfo = report.new UserInfo(true);
						while (entry != null) try {
							UserinfoType type = UserinfoType.valueOf(entry.getName().split("_")[1]);
							ByteArrayOutputStream out = new ByteArrayOutputStream();
							int count;
							while ((count = stream.read(buf)) > -1) out.write(buf, 0, count);
							CsvReader reader = new CsvReader(new ByteArrayInputStream(out.toByteArray()), Charset.forName("GBK"));
							reader.readHeaders();
							switch(type) {
								case account : LinkedList<AccountInfoType> accountList = new LinkedList<AccountInfoType>();
								while(reader.readRecord()) {
									AccountInfoType account = new AccountInfoType();
									account.setAccountid(reader.get("账户ID")==null || reader.get("账户ID").trim().length() == 0|| "--".equals(reader.get("账户ID"))|| "-".equals(reader.get("账户ID"))?null:Long.valueOf(reader.get("账户ID")));
									account.setBalance(reader.get("账户余额")==null || reader.get("账户余额").trim().length() == 0|| "--".equals(reader.get("账户余额"))|| "-".equals(reader.get("账户余额"))?null:Double.valueOf(reader.get("账户余额")));
									account.setBudget(reader.get("账户预算")==null || reader.get("账户预算").trim().length() == 0|| "--".equals(reader.get("账户预算"))|| "-".equals(reader.get("账户预算"))?null:Double.valueOf(reader.get("账户预算")));
									account.setTotalCost(reader.get("账户累积消费")==null || reader.get("账户累积消费").trim().length() == 0|| "--".equals(reader.get("账户累积消费"))|| "-".equals(reader.get("账户累积消费"))?null:Double.valueOf(reader.get("账户累积消费")));
									account.setTotalPay(reader.get("账户投资")==null || reader.get("账户投资").trim().length() == 0|| "--".equals(reader.get("账户投资"))|| "-".equals(reader.get("账户投资"))?null:Double.valueOf(reader.get("账户投资")));
									accountList.add(account);
								}
								userInfo.setAccount(accountList);
								break;
								case cpcplan: LinkedList<CpcPlanType> cpcPlanTypeList = new LinkedList<CpcPlanType>();
								while(reader.readRecord()) {
									CpcPlanType cpcPlanType = new CpcPlanType();
									cpcPlanType.setBudget(reader.get("推广计划每日预算")==null || reader.get("推广计划每日预算").trim().length() == 0|| "--".equals(reader.get("推广计划每日预算"))|| "-".equals(reader.get("推广计划每日预算"))?null:Double.valueOf(reader.get("推广计划每日预算")));
									cpcPlanType.setCpcPlanId(reader.get("推广计划ID")==null || reader.get("推广计划ID").trim().length() == 0|| "--".equals(reader.get("推广计划ID"))|| "-".equals(reader.get("推广计划ID"))?null:Long.valueOf(reader.get("推广计划ID")));
									cpcPlanType.setCpcPlanName(reader.get("推广计划名称"));
									cpcPlanType.setJoinUnion(Boolean.parseBoolean(reader.get("是否内容关联")));
									cpcPlanType.setMobilePriceRate(reader.get("移动出价比例")==null || reader.get("移动出价比例").trim().length() == 0|| "--".equals(reader.get("移动出价比例"))|| "-".equals(reader.get("移动出价比例"))?null:Double.valueOf(reader.get("移动出价比例")));
									cpcPlanType.setPause(reader.get("暂停/启用推广计划")==null || reader.get("暂停/启用推广计划").trim().length() == 0|| "--".equals(reader.get("暂停/启用推广计划"))|| "-".equals(reader.get("暂停/启用推广计划"))?null:Boolean.valueOf(reader.get("暂停/启用推广计划")));
									cpcPlanType.setShowProb(reader.get("创意展现方式")==null || reader.get("创意展现方式").trim().length() == 0|| "--".equals(reader.get("创意展现方式"))|| "-".equals(reader.get("创意展现方式"))?null:Integer.valueOf(reader.get("创意展现方式")));
									cpcPlanType.setStatus(reader.get("推广计划状态")==null || reader.get("推广计划状态").trim().length() == 0|| "--".equals(reader.get("推广计划状态"))|| "-".equals(reader.get("推广计划状态"))?null:Integer.valueOf(reader.get("推广计划状态")));
									cpcPlanType.setUnionPrice(reader.get("内容关联出价")==null || reader.get("内容关联出价").trim().length() == 0|| "--".equals(reader.get("内容关联出价"))|| "-".equals(reader.get("内容关联出价"))?null:Double.valueOf(reader.get("内容关联出价")));
									cpcPlanTypeList.add(cpcPlanType);
								}
								userInfo.setCpcplan(cpcPlanTypeList);
								break;
								case cpc:  LinkedList<CpcType> cpcTypeList = new LinkedList<CpcType>();
								while(reader.readRecord()) {
									CpcType cpcType = new CpcType();
									cpcType.setCpcMobileQuality(reader.get("关键词移动质量度"));
									cpcType.setIsChangedDisabledMateriel(reader.get("是否为修改未生效物料")==null || reader.get("是否为修改未生效物料").trim().length() == 0|| "--".equals(reader.get("是否为修改未生效物料"))|| "-".equals(reader.get("是否为修改未生效物料"))?null:Integer.valueOf(reader.get("是否为修改未生效物料")));
									cpcType.setCampaignId(reader.get("推广计划ID")==null || reader.get("推广计划ID").trim().length() == 0|| "--".equals(reader.get("推广计划ID"))|| "-".equals(reader.get("推广计划ID"))?null:Long.valueOf(reader.get("推广计划ID")));
									cpcType.setCpc(reader.get("关键词"));
									cpcType.setNonApprovalReason(reader.get("审核不通过原因"));
									cpcType.setNoShowReason(reader.get("关键词不能展示原因"));
									cpcType.setIsGroupPrice(reader.get("是否使用组出价")==null || reader.get("是否使用组出价").trim().length() == 0|| "--".equals(reader.get("是否使用组出价"))|| "-".equals(reader.get("是否使用组出价"))?null:Integer.valueOf(reader.get("是否使用组出价")));
									cpcType.setCpcGrpId(reader.get("推广组ID")==null || reader.get("推广组ID").trim().length() == 0|| "--".equals(reader.get("推广组ID"))|| "-".equals(reader.get("推广组ID"))?null:Long.valueOf(reader.get("推广组ID")));
									cpcType.setCpcId(reader.get("关键词ID")==null || reader.get("关键词ID").trim().length() == 0|| "--".equals(reader.get("推广计划ID"))|| "-".equals(reader.get("推广计划ID"))?null:Long.valueOf(reader.get("关键词ID")));
									cpcType.setCpcQuality(reader.get("关键词质量度")==null || reader.get("关键词质量度").trim().length() == 0|| "-".equals(reader.get("关键词质量度"))|| "--".equals(reader.get("关键词质量度"))?null:Double.valueOf(reader.get("关键词质量度")));
									cpcType.setIsShow(reader.get("能否展现")==null || reader.get("能否展现").trim().length() == 0|| "--".equals(reader.get("能否展现"))|| "-".equals(reader.get("能否展现"))?null:Integer.valueOf(reader.get("能否展现")));
									cpcType.setMatchType(reader.get("匹配方式")==null || reader.get("匹配方式").trim().length() == 0|| "--".equals(reader.get("匹配方式"))|| "-".equals(reader.get("匹配方式"))?null:Integer.valueOf(reader.get("匹配方式")));
									cpcType.setMobileVisitUrl(reader.get("关键词移动访问URL"));
									cpcType.setPause(reader.get("暂停/取消暂停关键词")==null || reader.get("暂停/取消暂停关键词").trim().length() == 0|| "--".equals(reader.get("暂停/取消暂停关键词"))|| "-".equals(reader.get("暂停/取消暂停关键词"))?null:Boolean.valueOf(reader.get("暂停/取消暂停关键词")));
									cpcType.setPrice(reader.get("关键词出价")==null || reader.get("关键词出价").trim().length() == 0|| "--".equals(reader.get("关键词出价"))|| "-".equals(reader.get("关键词出价"))?null:Double.valueOf(reader.get("关键词出价")));
									cpcType.setStatus(reader.get("关键词状态")==null || reader.get("关键词状态").trim().length() == 0|| "--".equals(reader.get("关键词状态"))|| "-".equals(reader.get("关键词状态"))?null:Integer.valueOf(reader.get("关键词状态")));
									cpcType.setVisitUrl(reader.get("访问URL"));
									cpcTypeList.add(cpcType);
								}
								userInfo.setCpc(cpcTypeList);
								break;
								case cpcgrp:  LinkedList<CpcGrpType> cpcGrpTypeList = new LinkedList<CpcGrpType>();
								while(reader.readRecord()) {
									CpcGrpType cpcGrpType = new CpcGrpType();
									cpcGrpType.setCpcGrpId(reader.get("推广组ID")==null || reader.get("推广组ID").trim().length() == 0|| "--".equals(reader.get("推广组ID"))|| "-".equals(reader.get("推广组ID"))?null:Long.valueOf(reader.get("推广组ID")));
									cpcGrpType.setCpcGrpName(reader.get("推广组"));
									cpcGrpType.setCpcPlanId(reader.get("推广计划ID")==null || reader.get("推广计划ID").trim().length() == 0|| "--".equals(reader.get("推广计划ID"))|| "-".equals(reader.get("推广计划ID"))?null:Long.valueOf(reader.get("推广计划ID")));
									cpcGrpType.setMaxPrice(reader.get("关键词出价")==null || reader.get("关键词状态").trim().length() == 0|| "--".equals(reader.get("关键词出价"))|| "-".equals(reader.get("关键词出价"))?null:Double.valueOf(reader.get("关键词出价")));
									cpcGrpType.setPause(reader.get("暂停/取消暂停关键词")==null || reader.get("关键词状态").trim().length() == 0|| "--".equals(reader.get("暂停/取消暂停关键词"))|| "-".equals(reader.get("暂停/取消暂停关键词"))?null:Boolean.valueOf(reader.get("暂停/取消暂停关键词")));
									cpcGrpType.setStatus(reader.get("关键词状态")==null || reader.get("关键词状态").trim().length() == 0|| "--".equals(reader.get("关键词状态"))|| "-".equals(reader.get("关键词状态"))?null:Integer.valueOf(reader.get("关键词状态")));
									cpcGrpTypeList.add(cpcGrpType);
								}
								userInfo.setCpcgrp(cpcGrpTypeList);
								break;
								case cpcidea:  LinkedList<CpcIdeaType> cpcIdeaTypeList = new LinkedList<CpcIdeaType>();
								while(reader.readRecord()) {
									CpcIdeaType cpcIdeaType = new CpcIdeaType();
									cpcIdeaType.setMobileVisitUrlNonApprovalReason(reader.get("移动访问URL审核不通过原因"));
									cpcIdeaType.setIdeaNonApprovalReason(reader.get("创意审核不通过原因"));
									cpcIdeaType.setIsChangedDisabledMateriel(reader.get("是否为修改未生效物料")==null || reader.get("是否为修改未生效物料").trim().length() == 0|| "--".equals(reader.get("是否为修改未生效物料"))|| "-".equals(reader.get("是否为修改未生效物料"))?null:Integer.valueOf(reader.get("是否为修改未生效物料")));
									cpcIdeaType.setCpcIdeaEncryptId(reader.get("加密创意ID")==null || reader.get("加密创意ID").trim().length() == 0|| "--".equals(reader.get("加密创意ID"))|| "-".equals(reader.get("加密创意ID"))?null:reader.get("加密创意ID"));
									cpcIdeaType.setCampaignId(reader.get("推广计划ID")==null || reader.get("推广计划ID").trim().length() == 0|| "--".equals(reader.get("推广计划ID"))|| "-".equals(reader.get("推广计划ID"))?null:Long.valueOf(reader.get("推广计划ID")));
									cpcIdeaType.setCpcGrpId(reader.get("推广组ID")==null || reader.get("推广组ID").trim().length() == 0|| "--".equals(reader.get("推广组ID"))|| "-".equals(reader.get("推广组ID"))?null:Long.valueOf(reader.get("推广组ID")));
									cpcIdeaType.setCpcIdeaId(reader.get("创意ID")==null || reader.get("创意ID").trim().length() == 0|| "--".equals(reader.get("创意ID"))|| "-".equals(reader.get("创意ID"))?null:Long.valueOf(reader.get("创意ID")));
									cpcIdeaType.setDescription1(reader.get("创意描述第一行"));
									cpcIdeaType.setDescription2(reader.get("创意描述第二行"));
									cpcIdeaType.setMobileShowUrl(reader.get("创意移动显示URL"));
									cpcIdeaType.setMobileVisitUrl(reader.get("创意移动访问URL"));
									cpcIdeaType.setPause(reader.get("暂停/取消暂停创意")==null || reader.get("暂停/取消暂停创意").trim().length() == 0|| "--".equals(reader.get("暂停/取消暂停创意"))|| "-".equals(reader.get("暂停/取消暂停创意"))?null:Boolean.valueOf(reader.get("暂停/取消暂停创意")));
									cpcIdeaType.setShowUrl(reader.get("显示URL"));
									cpcIdeaType.setTitle(reader.get("创意标题"));
									cpcIdeaType.setVisitUrl(reader.get("访问URL"));
									cpcIdeaType.setStatus(reader.get("创意状态")==null || reader.get("创意状态").trim().length() == 0|| "--".equals(reader.get("创意状态"))|| "-".equals(reader.get("创意状态"))?null:Integer.valueOf(reader.get("创意状态")));
									cpcIdeaTypeList.add(cpcIdeaType);
								}
								userInfo.setCpcidea(cpcIdeaTypeList);
								break;
								case cpcexidea:  LinkedList<CpcExIdeaType> cpcExIdeaTypeList = new LinkedList<CpcExIdeaType>();
								while(reader.readRecord()) {
									CpcExIdeaType cpcExIdeaType = new CpcExIdeaType();
									cpcExIdeaType.setCpcExtraIdeaDeviceType(reader.get("创意状态")==null || reader.get("创意状态").trim().length() == 0|| "--".equals(reader.get("创意状态"))|| "-".equals(reader.get("创意状态"))?null:Integer.valueOf(reader.get("创意状态")));
									cpcExIdeaType.setCpcExtraIdeaId(reader.get("附加创意ID")==null || reader.get("附加创意ID").trim().length() == 0|| "--".equals(reader.get("附加创意ID"))|| "-".equals(reader.get("附加创意ID"))?null:Long.valueOf(reader.get("附加创意ID")));
									cpcExIdeaType.setCpcGrpId(reader.get("推广组ID")==null || reader.get("推广组ID").trim().length() == 0|| "--".equals(reader.get("推广组ID"))|| "-".equals(reader.get("推广组ID"))?null:Long.valueOf(reader.get("推广组ID")));
									ExIdeaPicType exIdeaPicType = new ExIdeaPicType();
									exIdeaPicType.setCpcExtraIdeaId(reader.get("附加创意ID")==null || reader.get("附加创意ID").trim().length() == 0|| "--".equals(reader.get("附加创意ID"))|| "-".equals(reader.get("附加创意ID"))?null:Long.valueOf(reader.get("附加创意ID")));
									exIdeaPicType.setExIdeaPicId(reader.get("图片ID")==null || reader.get("图片ID").trim().length() == 0|| "--".equals(reader.get("图片ID"))|| "-".equals(reader.get("图片ID"))?null:Long.valueOf(reader.get("图片ID")));
									exIdeaPicType.setPicUploadUrl(reader.get("图片"));
									exIdeaPicType.setPicVisitUrl(reader.get("图片访问URL"));
									cpcExIdeaType.setExIdeaPic(exIdeaPicType);
									cpcExIdeaType.setPause(reader.get("取消暂停/暂停")==null || reader.get("取消暂停/暂停").trim().length() == 0|| "--".equals(reader.get("取消暂停/暂停"))|| "-".equals(reader.get("取消暂停/暂停"))?null:Boolean.valueOf(reader.get("取消暂停/暂停")));
									cpcExIdeaType.setPicCheckStatus(reader.get("附加创意图片状态")==null || reader.get("附加创意图片状态").trim().length() == 0|| "--".equals(reader.get("附加创意图片状态"))|| "-".equals(reader.get("附加创意图片状态"))?null:Integer.valueOf(reader.get("附加创意图片状态")));
									cpcExIdeaType.setPicModiCheckStatus(reader.get("图片审核不通过原因")==null || reader.get("图片审核不通过原因").trim().length() == 0|| "--".equals(reader.get("图片审核不通过原因"))|| "-".equals(reader.get("图片审核不通过原因"))?null:Integer.valueOf(reader.get("图片审核不通过原因")));
									cpcExIdeaType.setSubChainCheckStatus(reader.get("附加创意子链状态")==null || reader.get("附加创意子链状态").trim().length() == 0|| "--".equals(reader.get("附加创意子链状态"))|| "-".equals(reader.get("附加创意子链状态"))?null:Integer.valueOf(reader.get("附加创意子链状态")));
									cpcExIdeaType.setSubChainModiCheckStatus(reader.get("子链审核不通过原因")==null || reader.get("子链审核不通过原因").trim().length() == 0|| "--".equals(reader.get("子链审核不通过原因"))|| "-".equals(reader.get("子链审核不通过原因"))?null:Integer.valueOf(reader.get("子链审核不通过原因")));
									cpcExIdeaTypeList.add(cpcExIdeaType);
								}
								userInfo.setCpcexidea(cpcExIdeaTypeList);
								break;
							}					
						} catch (IllegalArgumentException e){} catch (ArrayIndexOutOfBoundsException e){}
						catch (Exception e) {
							logger.process(Task.getMessage(e), 10212, semAccounts.getString("tenant_id"));
						} finally {
							entry = stream.getNextEntry();
						}
						if (userInfo.isFull()) report.setUserinfo(userInfo);
						else report.setUserinfo(report.new UserInfo(false));
					} catch (Exception e) {
						logger.process(Task.getMessage(e), 10213, semAccounts.getString("tenant_id"));
					}
				} else executor.process(this, System.currentTimeMillis() + interval * 9 + SubTask.random.nextInt(interval * 2));
			} catch (BusinessException e) {
				report.setUserinfo(report.new UserInfo(false));
				logger.process(Task.getMessage(e), 10214, semAccounts.getString("tenant_id"));
			} catch (Exception e) {
				if (count.add()) executor.process(this, System.currentTimeMillis() + interval * 9 + SubTask.random.nextInt(interval * 2));
				else  report.setUserinfo(report.new UserInfo(false));
				logger.process(Task.getMessage(e), 10215, semAccounts.getString("tenant_id"));
			} else  {
				report.finish("force quit");
				logger.process("force stop", 10216, semAccounts.getString("tenant_id"));
			}	
		}
	}
}