package com.cubead.clm.io.shenma.data;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.UUID;

import javax.json.JsonObject;

import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;

import com.alibaba.fastjson.JSON;
import com.cubead.clm.IProcessor;
import com.cubead.clm.io.shenma.HttpClient;
import com.cubead.clm.io.shenma.Task;
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


public class DataStorage {
	private final SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
	private volatile int index = -1;
	private volatile boolean finish = false;
	private volatile boolean cache = false;
	private String updateUrl;
	private long update;
	private long start;
	private long timeout;
	private int count;
	private Boolean partial;
	private RedisTemplate<String, String> template;
	private JsonObject account;
	private IProcessor<Object, Boolean> data;
	private IProcessor<Object, Boolean> logger;
	private HashMap<Type, UUID> uuids;
	private Result<AccountReport>[] accountReport;
	private Result<AdgroupReport>[] adgroupReport;
	private Result<AdvanceAppReport>[] advanceAppReport;
	private Result<AdvanceGoldensReport>[] advanceGoldReport;
	private Result<AdvanceImgTextCreativeReport>[] advanceImgTextCreativeReport;
	private Result<AdvanceImgTextSublinkReport>[] advanceImgTextSublinkReport;
	private Result<AppReport>[] appReport;
	private Result<AreaReport>[] areaReport;
	private Result<CampaignReport>[] campaignReport;
	private Result<CreativeReport>[] creativeReport;
	private Result<InvalidClickReport>[] invalidClickReport;
	private Result<KeyWordReport>[] keyWordReport;
	private Result<PathReport>[] pathReport;
	private Result<PhoneReport>[] phoneReport;
	private Result<SearchWordReport>[] searchWordReport;
	private Result<AdgroupReport>[] rtadgroupReport;
	private Result<AdvanceAppReport>[] rtadvanceAppReport;
	private Result<AdvanceGoldensReport>[] rtadvanceGoldReport;
	private Result<AdvanceImgTextCreativeReport>[] rtadvanceImgTextCreativeReport;
	private Result<AdvanceImgTextSublinkReport>[] rtadvanceImgTextSublinkReport;
	private Result<AppReport>[] rtappReport;
	private Result<AreaReport>[] rtareaReport;
	private Result<CampaignReport>[] rtcampaignReport;
	private Result<CreativeReport>[] rtcreativeReport;
	private Result<InvalidClickReport>[] rtinvalidClickReport;
	private Result<KeyWordReport>[] rtkeyWordReport;
	private Result<PathReport>[] rtpathReport;
	private Result<PhoneReport>[] rtphoneReport;
	private Result<SearchWordReport>[] rtsearchWordReport;
	private Result<RealTimeAccountReport>[] realTimeAccountReport;
	private UserInfo userinfo;
	private Boolean full;
	private final String key;
	
	public DataStorage( Boolean full, long update, long start, long timeout, int count, RedisTemplate<String, String> template, 
			Boolean partial, JsonObject account, IProcessor<Object, Boolean> data, IProcessor<Object, Boolean> logger,
			HashMap<Type, UUID> uuids, String updateUrl, Result<AccountReport>[] accountReport, Result<AdgroupReport>[] adgroupReport,
			Result<AdvanceAppReport>[] advanceAppReport, Result<AdvanceGoldensReport>[] advanceGoldReport,
			Result<AdvanceImgTextCreativeReport>[] advanceImgTextCreativeReport,
			Result<AdvanceImgTextSublinkReport>[] advanceImgTextSublinkReport, Result<AppReport>[] appReport,
			Result<AreaReport>[] areaReport, Result<CampaignReport>[] campaignReport,
			Result<CreativeReport>[] creativeReport, Result<InvalidClickReport>[] invalidClickReport,
			Result<KeyWordReport>[] keyWordReport, Result<PathReport>[] pathReport, Result<PhoneReport>[] phoneReport,
			Result<SearchWordReport>[] searchWordReport, Result<AdgroupReport>[] rtadgroupReport,
			Result<AdvanceAppReport>[] rtadvanceAppReport, Result<AdvanceGoldensReport>[] rtadvanceGoldReport,
			Result<AdvanceImgTextCreativeReport>[] rtadvanceImgTextCreativeReport,
			Result<AdvanceImgTextSublinkReport>[] rtadvanceImgTextSublinkReport, Result<AppReport>[] rtappReport,
			Result<AreaReport>[] rtareaReport, Result<CampaignReport>[] rtcampaignReport,
			Result<CreativeReport>[] rtcreativeReport, Result<InvalidClickReport>[] rtinvalidClickReport,
			Result<KeyWordReport>[] rtkeyWordReport, Result<PathReport>[] rtpathReport, Result<PhoneReport>[] rtphoneReport,
			Result<SearchWordReport>[] rtsearchWordReport,Result<RealTimeAccountReport>[] realTimeAccountReport) {
		this.update = update;
		this.account = account;
		this.data = data;
		this.start = start;
		this.count = count;
		this.timeout = timeout;
		this.template = template;
		this.partial = partial;
		this.logger = logger;
		this.uuids = uuids;
		this.updateUrl=updateUrl;
		this.full = full;
		this.accountReport = accountReport;
		this.adgroupReport = adgroupReport;
		this.advanceAppReport = advanceAppReport;
		this.advanceGoldReport = advanceGoldReport;
		this.advanceImgTextCreativeReport = advanceImgTextCreativeReport;
		this.advanceImgTextSublinkReport = advanceImgTextSublinkReport;
		this.appReport = appReport;
		this.areaReport = areaReport;
		this.campaignReport = campaignReport;
		this.creativeReport = creativeReport;
		this.invalidClickReport = invalidClickReport;
		this.keyWordReport = keyWordReport;
		this.pathReport = pathReport;
		this.phoneReport = phoneReport;
		this.searchWordReport = searchWordReport;
		this.rtadgroupReport = rtadgroupReport;
		this.rtadvanceAppReport = rtadvanceAppReport;
		this.rtadvanceGoldReport = rtadvanceGoldReport;
		this.rtadvanceImgTextCreativeReport = rtadvanceImgTextCreativeReport;
		this.rtadvanceImgTextSublinkReport = rtadvanceImgTextSublinkReport;
		this.rtappReport = rtappReport;
		this.rtareaReport = rtareaReport;
		this.rtcampaignReport = rtcampaignReport;
		this.rtcreativeReport = rtcreativeReport;
		this.rtinvalidClickReport = rtinvalidClickReport;
		this.rtkeyWordReport = rtkeyWordReport;
		this.rtpathReport = rtpathReport;
		this.rtphoneReport = rtphoneReport;
		this.rtsearchWordReport = rtsearchWordReport;
		this.realTimeAccountReport= realTimeAccountReport;
		this.key = "cache.shenma." + account.getString("tenant_id") + '.' + UUID.randomUUID();
	}
	
	public void setResult(ReportType type, Integer team, Result result) {
		if (!finish) {
			if (result.isSuccess() && result.getData().size() >= count) {
				final List data = result.getData();
				final UUID id = UUID.randomUUID();
				try {
					if (timeout > 0) {
						final byte[] kb = key.getBytes();
						final byte[] ib = id.toString().getBytes();
						final byte[] db = JSON.toJSONString(data).getBytes("UTF-8");
						template.executePipelined(new RedisCallback<Object>() {
						@Override
						public Object doInRedis(RedisConnection connection) throws DataAccessException {
							connection.hSet(kb, ib, db);
							connection.expire(kb, timeout);
							return null;
						}});} else template.opsForHash().put(key, id.toString(), JSON.toJSONString(data));
					result.setTag(id);
					result.setData(null);
					cache = true;
				} catch (Exception e) {
					logger.process(Task.getMessage(e), 10115, account.getString("tenant_id"));
				}
			}
			switch(type) {
				case accountReport: accountReport[team] = (Result<AccountReport>)result;
				break;
				case adgroupReport: adgroupReport[team] = (Result<AdgroupReport>)result;
				break;
				case advanceAppReport: advanceAppReport[team] = (Result<AdvanceAppReport>)result;
				break;
				case advanceGoldReport: advanceGoldReport[team] = (Result<AdvanceGoldensReport>)result;
				break;
				case advanceImgTextCreativeReport: advanceImgTextCreativeReport[team] = (Result<AdvanceImgTextCreativeReport>)result;
				break;
				case advanceImgTextSublinkReport: advanceImgTextSublinkReport[team] = (Result<AdvanceImgTextSublinkReport>)result;
				break;
				case appReport: appReport[team] = (Result<AppReport>)result;
				break;
				case areaReport: areaReport[team] = (Result<AreaReport>)result;
				break;
				case campaignReport: campaignReport[team] = (Result<CampaignReport>)result;
				break;
				case creativeReport: creativeReport[team] = (Result<CreativeReport>)result;
				break;
				case invalidClickReport: invalidClickReport[team] = (Result<InvalidClickReport>)result;
				break;
				case keyWordReport: keyWordReport[team] = (Result<KeyWordReport>)result;
				break;
				case pathReport: pathReport[team] = (Result<PathReport>)result;
				break;
				case phoneReport: phoneReport[team] = (Result<PhoneReport>)result;
				break;
				case searchWordReport: searchWordReport[team] = (Result<SearchWordReport>)result;
				break;
				case realTimeAccountReport: realTimeAccountReport[team] = (Result<RealTimeAccountReport>)result;
				break;
				case realTimeAdgroupReport: rtadgroupReport[team] = (Result<AdgroupReport>)result;
				break;
				case realTimeAdvanceAppReport: rtadvanceAppReport[team] = (Result<AdvanceAppReport>)result;
				break;
				case realTimeAdvanceGoldReport: rtadvanceGoldReport[team] = (Result<AdvanceGoldensReport>)result;
				break;
				case realTimeAdvanceImgTextCreativeReport: rtadvanceImgTextCreativeReport[team] = (Result<AdvanceImgTextCreativeReport>)result;
				break;
				case realTimeAdvanceImgTextSublinkReport: rtadvanceImgTextSublinkReport[team] = (Result<AdvanceImgTextSublinkReport>)result;
				break;
				case realTimeAppReport: rtappReport[team] = (Result<AppReport>)result;
				break;
				case realTimeAreaReport: rtareaReport[team] = (Result<AreaReport>)result;
				break;
				case realTimeCampaignReport: rtcampaignReport[team] = (Result<CampaignReport>)result;
				break;
				case realTimeCreativeReport: rtcreativeReport[team] = (Result<CreativeReport>)result;
				break;
				case realTimeInvalidClickReport: rtinvalidClickReport[team] = (Result<InvalidClickReport>)result;
				break;
				case realTimeKeyWordReport: rtkeyWordReport[team] = (Result<KeyWordReport>)result;
				break;
				case realTimePathReport: rtpathReport[team] = (Result<PathReport>)result;
				break;
				case realTimePhoneReport: rtphoneReport[team] = (Result<PhoneReport>)result;
				break;
				case realTimeSearchWordReport: rtsearchWordReport[team] = (Result<SearchWordReport>)result;
				break;
			}
			checkin();
		}
	}
	
	public void setUserinfo(final UserInfo userinfo) {
		if (!finish && partial == null) {
			this.userinfo = userinfo;
			if (userinfo != null && userinfo.isSuccess) {
				final byte[] kb = key.getBytes();
				if (userinfo.adgroups != null && userinfo.adgroups.size() >= count) {
					final UUID id = UUID.randomUUID();
					try {
						if (timeout > 0) {
							final byte[] ib = id.toString().getBytes();
							final byte[] db = JSON.toJSONString(userinfo.adgroups).getBytes("UTF-8");
							template.executePipelined(new RedisCallback<Object>() {
							@Override
							public Object doInRedis(RedisConnection connection) throws DataAccessException {
								connection.hSet(kb, ib, db);
								connection.expire(kb, timeout);
								return null;
							}});} else template.opsForHash().put(key, id.toString(), JSON.toJSONString(userinfo.adgroups));
						userinfo.adgroups = null;
						userinfo.adgroupsid = id;
					} catch (Exception e) {
						logger.process(Task.getMessage(e), 10116, account.getString("tenant_id"));
					}
				}
				if (userinfo.advancedApp != null && userinfo.advancedApp.size() >= count) {
					final UUID id = UUID.randomUUID();
					try {
						if (timeout > 0) {
							final byte[] ib = id.toString().getBytes();
							final byte[] db = JSON.toJSONString(userinfo.advancedApp).getBytes("UTF-8");
							template.executePipelined(new RedisCallback<Object>() {
							@Override
							public Object doInRedis(RedisConnection connection) throws DataAccessException {
								connection.hSet(kb, ib, db);
								connection.expire(kb, timeout);
								return null;
							}});} else template.opsForHash().put(key, id.toString(), JSON.toJSONString(userinfo.advancedApp));
						userinfo.advancedApp = null;
						userinfo.advancedAppid = id;
					} catch (Exception e) {
						logger.process(Task.getMessage(e), 10117, account.getString("tenant_id"));
					}
				}
				if (userinfo.advancedCssSublinks != null && userinfo.advancedCssSublinks.size() >= count) {
					final UUID id = UUID.randomUUID();
					try {
						if (timeout > 0) {
							final byte[] ib = id.toString().getBytes();
							final byte[] db = JSON.toJSONString(userinfo.advancedCssSublinks).getBytes("UTF-8");
							template.executePipelined(new RedisCallback<Object>() {
							@Override
							public Object doInRedis(RedisConnection connection) throws DataAccessException {
								connection.hSet(kb, ib, db);
								connection.expire(kb, timeout);
								return null;
							}});} else template.opsForHash().put(key, id.toString(), JSON.toJSONString(userinfo.advancedCssSublinks));
						userinfo.advancedCssSublinks = null;
						userinfo.advancedCssSublinksid = id;
					} catch (Exception e) {
						logger.process(Task.getMessage(e), 10118, account.getString("tenant_id"));
					}
				}
				if (userinfo.advancedGoldens != null && userinfo.advancedGoldens.size() >= count) {
					final UUID id = UUID.randomUUID();
					try {
						if (timeout > 0) {
							final byte[] ib = id.toString().getBytes();
							final byte[] db = JSON.toJSONString(userinfo.advancedGoldens).getBytes("UTF-8");
							template.executePipelined(new RedisCallback<Object>() {
							@Override
							public Object doInRedis(RedisConnection connection) throws DataAccessException {
								connection.hSet(kb, ib, db);
								connection.expire(kb, timeout);
								return null;
							}});} else template.opsForHash().put(key, id.toString(), JSON.toJSONString(userinfo.advancedGoldens));
						userinfo.advancedGoldens = null;
						userinfo.advancedGoldensid = id;
					} catch (Exception e) {
						logger.process(Task.getMessage(e), 10119, account.getString("tenant_id"));
					}
				}
				if (userinfo.apps != null && userinfo.apps.size() >= count) {
					final UUID id = UUID.randomUUID();
					try {
						if (timeout > 0) {
							final byte[] ib = id.toString().getBytes();
							final byte[] db = JSON.toJSONString(userinfo.apps).getBytes("UTF-8");
							template.executePipelined(new RedisCallback<Object>() {
							@Override
							public Object doInRedis(RedisConnection connection) throws DataAccessException {
								connection.hSet(kb, ib, db);
								connection.expire(kb, timeout);
								return null;
							}});} else template.opsForHash().put(key, id.toString(), JSON.toJSONString(userinfo.apps));
						userinfo.apps = null;
						userinfo.appsid = id;
					} catch (Exception e) {
						logger.process(Task.getMessage(e), 10120, account.getString("tenant_id"));
					}
				}
				if (userinfo.campains != null && userinfo.campains.size() >= count) {
					final UUID id = UUID.randomUUID();
					try {
						if (timeout > 0) {
							final byte[] ib = id.toString().getBytes();
							final byte[] db = JSON.toJSONString(userinfo.campains).getBytes("UTF-8");
							template.executePipelined(new RedisCallback<Object>() {
							@Override
							public Object doInRedis(RedisConnection connection) throws DataAccessException {
								connection.hSet(kb, ib, db);
								connection.expire(kb, timeout);
								return null;
							}});} else template.opsForHash().put(key, id.toString(), JSON.toJSONString(userinfo.campains));
						userinfo.campains = null;
						userinfo.campainsid = id;
					} catch (Exception e) {
						logger.process(Task.getMessage(e), 10121, account.getString("tenant_id"));
					}
				}
				if (userinfo.creatives != null && userinfo.creatives.size() >= count) {
					final UUID id = UUID.randomUUID();
					try {
						if (timeout > 0) {
							final byte[] ib = id.toString().getBytes();
							final byte[] db = JSON.toJSONString(userinfo.creatives).getBytes("UTF-8");
							template.executePipelined(new RedisCallback<Object>() {
							@Override
							public Object doInRedis(RedisConnection connection) throws DataAccessException {
								connection.hSet(kb, ib, db);
								connection.expire(kb, timeout);
								return null;
							}});} else template.opsForHash().put(key, id.toString(), JSON.toJSONString(userinfo.creatives));
						userinfo.creatives = null;
						userinfo.creativesid = id;
					} catch (Exception e) {
						logger.process(Task.getMessage(e), 10122, account.getString("tenant_id"));
					}
				}
				if (userinfo.keywords != null && userinfo.keywords.size() >= count) {
					final UUID id = UUID.randomUUID();
					try {
						if (timeout > 0) {
							final byte[] ib = id.toString().getBytes();
							final byte[] db = JSON.toJSONString(userinfo.keywords).getBytes("UTF-8");
							template.executePipelined(new RedisCallback<Object>() {
							@Override
							public Object doInRedis(RedisConnection connection) throws DataAccessException {
								connection.hSet(kb, ib, db);
								connection.expire(kb, timeout);
								return null;
							}});} else template.opsForHash().put(key, id.toString(), JSON.toJSONString(userinfo.keywords));
						userinfo.keywords = null;
						userinfo.keywordsid = id;
					} catch (Exception e) {
						logger.process(Task.getMessage(e), 10123, account.getString("tenant_id"));
					}
				}
				if (userinfo.sublinks != null && userinfo.sublinks.size() >= count) {
					final UUID id = UUID.randomUUID();
					try {
						if (timeout > 0) {
							final byte[] ib = id.toString().getBytes();
							final byte[] db = JSON.toJSONString(userinfo.sublinks).getBytes("UTF-8");
							template.executePipelined(new RedisCallback<Object>() {
							@Override
							public Object doInRedis(RedisConnection connection) throws DataAccessException {
								connection.hSet(kb, ib, db);
								connection.expire(kb, timeout);
								return null;
							}});} else template.opsForHash().put(key, id.toString(), JSON.toJSONString(userinfo.sublinks));
						userinfo.sublinks = null;
						userinfo.sublinksid = id;
					} catch (Exception e) {
						logger.process(Task.getMessage(e), 10124, account.getString("tenant_id"));
					}
				}
				if (userinfo.advancedPicTexts != null && userinfo.advancedPicTexts.size() >= count) {
					final UUID id = UUID.randomUUID();
					try {
						if (timeout > 0) {
							final byte[] ib = id.toString().getBytes();
							final byte[] db = JSON.toJSONString(userinfo.advancedPicTexts).getBytes("UTF-8");
							template.executePipelined(new RedisCallback<Object>() {
							@Override
							public Object doInRedis(RedisConnection connection) throws DataAccessException {
								connection.hSet(kb, ib, db);
								connection.expire(kb, timeout);
								return null;
							}});} else template.opsForHash().put(key, id.toString(), JSON.toJSONString(userinfo.advancedPicTexts));
						userinfo.advancedPicTexts = null;
						userinfo.advancedPicTextsid = id;
					} catch (Exception e) {
						logger.process(Task.getMessage(e), 10124, account.getString("tenant_id"));
					}
				}
			}
			checkin();
		}
	}
	
	public void finish(String msg) {
		if (!finish) synchronized(this) {
			if (!finish) try {
				if (cache) template.delete(key);	
			} finally {
				this.finish = true;
				logger.process(msg, 9, account.getString("tenant_id"));
			}
		}	
	}
 
	protected void checkin() {
		if (partial != null || userinfo != null) if (partial != null || userinfo.isSuccess) {
			boolean allin = true;
outter:		for (Type type : uuids.keySet()) if (type instanceof ReportType) 
				switch((ReportType)type) {
					case accountReport: for (int i = accountReport.length - 1; i > index; i--) if (accountReport[i] == null) {
						allin = false;
						break outter;
					} else if (!accountReport[i].isSuccess()) {
						if (i > index) {
							index = i;
							if (update - (index + 1) * 432000000L < start){
								if (!finish) synchronized(this){
									if (!finish) {
										finish = true;
										logger.process("Task finish fail", 9, account.getString("tenant_id"));
										if (cache) template.delete(key);
									}
								}
								allin = false;
								break outter;
							}
						}
						break;
					}
					break;
					case adgroupReport: for (int i = adgroupReport.length - 1; i > index; i--) if (adgroupReport[i] == null) {
						allin = false;
						break outter;
					} else if (!adgroupReport[i].isSuccess()) {
						if (i > index) {
							index = i;
							if (update - (index + 1) * 432000000L < start){
								if (!finish) synchronized(this){
									if (!finish) {
										finish = true;
										logger.process("Task finish fail", 9, account.getString("tenant_id"));
										if (cache) template.delete(key);
									}
								}
								allin = false;
								break outter;
							}
						}
						break;
					}
					break;
					case advanceAppReport: for (int i = advanceAppReport.length - 1; i > index; i--) if (advanceAppReport[i] == null) {
						allin = false;
						break outter;
					} else if (!advanceAppReport[i].isSuccess()) {
						if (i > index) {
							index = i;
							if (update - (index + 1) * 432000000L < start){
								if (!finish) synchronized(this){
									if (!finish) {
										finish = true;
										logger.process("Task finish fail", 9, account.getString("tenant_id"));
										if (cache) template.delete(key);
									}
								}
								allin = false;
								break outter;
							}
						}
						break;
					}
					break;
					case advanceGoldReport: for (int i = advanceGoldReport.length - 1; i > index; i--) if (advanceGoldReport[i] == null) {
						allin = false;
						break outter;
					} else if (!advanceGoldReport[i].isSuccess()) {
						if (i > index) {
							index = i;
							if (update - (index + 1) * 432000000L < start){
								if (!finish) synchronized(this){
									if (!finish) {
										finish = true;
										logger.process("Task finish fail", 9, account.getString("tenant_id"));
										if (cache) template.delete(key);
									}
								}
								allin = false;
								break outter;
							}
						}
						break;
					}
					break;
					case advanceImgTextCreativeReport: for (int i = advanceImgTextCreativeReport.length - 1; i > index; i--) if (advanceImgTextCreativeReport[i] == null) {
						allin = false;
						break outter;
					} else if (!advanceImgTextCreativeReport[i].isSuccess()) {
						if (i > index) {
							index = i;
							if (update - (index + 1) * 432000000L < start){
								if (!finish) synchronized(this){
									if (!finish) {
										finish = true;
										logger.process("Task finish fail", 9, account.getString("tenant_id"));
										if (cache) template.delete(key);
									}
								}
								allin = false;
								break outter;
							}
						}
						break;
					}
					break;
					case advanceImgTextSublinkReport: for (int i = advanceImgTextSublinkReport.length - 1; i > index; i--) if (advanceImgTextSublinkReport[i] == null) {
						allin = false;
						break outter;
					} else if (!advanceImgTextSublinkReport[i].isSuccess()) {
						if (i > index) {
							index = i;
							if (update - (index + 1) * 432000000L < start){
								if (!finish) synchronized(this){
									if (!finish) {
										finish = true;
										logger.process("Task finish fail", 9, account.getString("tenant_id"));
										if (cache) template.delete(key);
									}
								}
								allin = false;
								break outter;
							}
						}
						break;
					}
					break;
					case appReport: for (int i = appReport.length - 1; i > index; i--) if (appReport[i] == null) {
						allin = false;
						break outter;
					} else if (!appReport[i].isSuccess()) {
						if (i > index) {
							index = i;
							if (update - (index + 1) * 432000000L < start){
								
								if (!finish) synchronized(this){
									if (!finish) {
										finish = true;
										logger.process("Task finish fail", 9, account.getString("tenant_id"));
										if (cache) template.delete(key);
									}
								}
								allin = false;
								break outter;
							}
						}
						break;
					}
					break;
					case areaReport: for (int i = areaReport.length - 1; i > index; i--) if (areaReport[i] == null) {
						allin = false;
						break outter;
					} else if (!areaReport[i].isSuccess()) {
						if (i > index) {
							index = i;
							if (update - (index + 1) * 432000000L < start){
								if (!finish) synchronized(this){
									if (!finish) {
										finish = true;
										logger.process("Task finish fail", 9, account.getString("tenant_id"));
										if (cache) template.delete(key);
									}
								}
								allin = false;
								break outter;
							}
						}
						break;
					}
					break;
					case campaignReport: for (int i = campaignReport.length - 1; i > index; i--) if (campaignReport[i] == null) {
						allin = false;
						break outter;
					} else if (!campaignReport[i].isSuccess()) {
						if (i > index) {
							index = i;
							if (update - (index + 1) * 432000000L < start){
								if (!finish) synchronized(this){
									if (!finish) {
										finish = true;
										logger.process("Task finish fail", 9, account.getString("tenant_id"));
										if (cache) template.delete(key);
									}
								}
								allin = false;
								break outter;
							}
						}
						break;
					}
					break;
					case creativeReport: for (int i = creativeReport.length - 1; i > index; i--) if (creativeReport[i] == null) {
						allin = false;
						break outter;
					} else if (!creativeReport[i].isSuccess()) {
						if (i > index) {
							index = i;
							if (update - (index + 1) * 432000000L < start){
								if (!finish) synchronized(this){
									if (!finish) {
										finish = true;
										logger.process("Task finish fail", 9, account.getString("tenant_id"));
										if (cache) template.delete(key);
									}
								}
								allin = false;
								break outter;
							}
						}
						break;
					}
					break;
					case invalidClickReport: for (int i = invalidClickReport.length - 1; i > index; i--) if (invalidClickReport[i] == null) {
						allin = false;
						break outter;
					} else if (!invalidClickReport[i].isSuccess()) {
						if (i > index) {
							index = i;
							if (update - (index + 1) * 432000000L < start){
								if (!finish) synchronized(this){
									if (!finish) {
										finish = true;
										logger.process("Task finish fail", 9, account.getString("tenant_id"));
										if (cache) template.delete(key);
									}
								}
								allin = false;
								break outter;
							}
						}
						break;
					}
					break;
					case keyWordReport: for (int i = keyWordReport.length - 1; i > index; i--) if (keyWordReport[i] == null) {
						allin = false;
						break outter;
					} else if (!keyWordReport[i].isSuccess()) {
						if (i > index) {
							index = i;
							if (update - (index + 1) * 432000000L < start){
								if (!finish) synchronized(this){
									if (!finish) {
										finish = true;
										logger.process("Task finish fail", 9, account.getString("tenant_id"));
										if (cache) template.delete(key);
									}
								}
								allin = false;
								break outter;
							}
						}
						break;
					}
					break;
					case pathReport: for (int i = pathReport.length - 1; i > index; i--) if (pathReport[i] == null) {
						allin = false;
						break outter;
					} else if (!pathReport[i].isSuccess()) {
						if (i > index) {
							index = i;
							if (update - (index + 1) * 432000000L < start){
								if (!finish) synchronized(this){
									if (!finish) {
										finish = true;
										logger.process("Task finish fail", 9, account.getString("tenant_id"));
										if (cache) template.delete(key);
									}
								}
								allin = false;
								break outter;
							}
						}
						break;
					}
					break;
					case phoneReport: for (int i = phoneReport.length - 1; i > index; i--) if (phoneReport[i] == null) {
						allin = false;
						break outter;
					} else if (!phoneReport[i].isSuccess()) {
						if (i > index) {
							index = i;
							if (update - (index + 1) * 432000000L < start){
								if (!finish) synchronized(this){
									if (!finish) {
										finish = true;
										logger.process("Task finish fail", 9, account.getString("tenant_id"));
										if (cache) template.delete(key);
									}
								}
								allin = false;
								break outter;
							}
						}
						break;
					}
					break;
					case searchWordReport: for (int i = searchWordReport.length - 1; i > index; i--) if (searchWordReport[i] == null) {
						allin = false;
						break outter;
					} else if (!searchWordReport[i].isSuccess()) {
						if (i > index) {
							index = i;
							if (update - (index + 1) * 432000000L < start){
								if (!finish) synchronized(this){
									if (!finish) {
										finish = true;
										logger.process("Task finish fail", 9, account.getString("tenant_id"));
										if (cache) template.delete(key);
									}
								}
								allin = false;
								break outter;
							}
						}
						break;
					}
					case realTimeAdgroupReport: for (int i = rtadgroupReport.length - 1; i > index; i--) if (rtadgroupReport[i] == null) {
						allin = false;
						break outter;
					} else if (!rtadgroupReport[i].isSuccess()) {
						if (i > index) {
							index = i;
							if (update - (index + 1) * 432000000L < start){
								if (!finish) synchronized(this){
									if (!finish) {
										finish = true;
										logger.process("Task finish fail", 9, account.getString("tenant_id"));
										if (cache) template.delete(key);
									}
								}
								allin = false;
								break outter;
							}
						}
						break;
					}
					break;
					case realTimeAdvanceAppReport: for (int i = rtadvanceAppReport.length - 1; i > index; i--) if (rtadvanceAppReport[i] == null) {
						allin = false;
						break outter;
					} else if (!rtadvanceAppReport[i].isSuccess()) {
						if (i > index) {
							index = i;
							if (update - (index + 1) * 432000000L < start){
								if (!finish) synchronized(this){
									if (!finish) {
										finish = true;
										logger.process("Task finish fail", 9, account.getString("tenant_id"));
										if (cache) template.delete(key);
									}
								}
								allin = false;
								break outter;
							}
						}
						break;
					}
					break;
					case realTimeAdvanceGoldReport: for (int i = rtadvanceGoldReport.length - 1; i > index; i--) if (rtadvanceGoldReport[i] == null) {
						allin = false;
						break outter;
					} else if (!rtadvanceGoldReport[i].isSuccess()) {
						if (i > index) {
							index = i;
							if (update - (index + 1) * 432000000L < start){
								if (!finish) synchronized(this){
									if (!finish) {
										finish = true;
										logger.process("Task finish fail", 9, account.getString("tenant_id"));
										if (cache) template.delete(key);
									}
								}
								allin = false;
								break outter;
							}
						}
						break;
					}
					break;
					case realTimeAdvanceImgTextCreativeReport: for (int i = rtadvanceImgTextCreativeReport.length - 1; i > index; i--) if (rtadvanceImgTextCreativeReport[i] == null) {
						allin = false;
						break outter;
					} else if (!rtadvanceImgTextCreativeReport[i].isSuccess()) {
						if (i > index) {
							index = i;
							if (update - (index + 1) * 432000000L < start){
								if (!finish) synchronized(this){
									if (!finish) {
										finish = true;
										logger.process("Task finish fail", 9, account.getString("tenant_id"));
										if (cache) template.delete(key);
									}
								}
								allin = false;
								break outter;
							}
						}
						break;
					}
					break;
					case realTimeAdvanceImgTextSublinkReport: for (int i = rtadvanceImgTextSublinkReport.length - 1; i > index; i--) if (rtadvanceImgTextSublinkReport[i] == null) {
						allin = false;
						break outter;
					} else if (!rtadvanceImgTextSublinkReport[i].isSuccess()) {
						if (i > index) {
							index = i;
							if (update - (index + 1) * 432000000L < start){
								if (!finish) synchronized(this){
									if (!finish) {
										finish = true;
										logger.process("Task finish fail", 9, account.getString("tenant_id"));
										if (cache) template.delete(key);
									}
								}
								allin = false;
								break outter;
							}
						}
						break;
					}
					break;
					case realTimeAppReport: for (int i = rtappReport.length - 1; i > index; i--) if (rtappReport[i] == null) {
						allin = false;
						break outter;
					} else if (!rtappReport[i].isSuccess()) {
						if (i > index) {
							index = i;
							if (update - (index + 1) * 432000000L < start){
								
								if (!finish) synchronized(this){
									if (!finish) {
										finish = true;
										logger.process("Task finish fail", 9, account.getString("tenant_id"));
										if (cache) template.delete(key);
									}
								}
								allin = false;
								break outter;
							}
						}
						break;
					}
					break;
					case realTimeAreaReport: for (int i = rtareaReport.length - 1; i > index; i--) if (rtareaReport[i] == null) {
						allin = false;
						break outter;
					} else if (!rtareaReport[i].isSuccess()) {
						if (i > index) {
							index = i;
							if (update - (index + 1) * 432000000L < start){
								if (!finish) synchronized(this){
									if (!finish) {
										finish = true;
										logger.process("Task finish fail", 9, account.getString("tenant_id"));
										if (cache) template.delete(key);
									}
								}
								allin = false;
								break outter;
							}
						}
						break;
					}
					break;
					case realTimeCampaignReport: for (int i = rtcampaignReport.length - 1; i > index; i--) if (rtcampaignReport[i] == null) {
						allin = false;
						break outter;
					} else if (!rtcampaignReport[i].isSuccess()) {
						if (i > index) {
							index = i;
							if (update - (index + 1) * 432000000L < start){
								if (!finish) synchronized(this){
									if (!finish) {
										finish = true;
										logger.process("Task finish fail", 9, account.getString("tenant_id"));
										if (cache) template.delete(key);
									}
								}
								allin = false;
								break outter;
							}
						}
						break;
					}
					break;
					case realTimeCreativeReport: for (int i = rtcreativeReport.length - 1; i > index; i--) if (rtcreativeReport[i] == null) {
						allin = false;
						break outter;
					} else if (!rtcreativeReport[i].isSuccess()) {
						if (i > index) {
							index = i;
							if (update - (index + 1) * 432000000L < start){
								if (!finish) synchronized(this){
									if (!finish) {
										finish = true;
										logger.process("Task finish fail", 9, account.getString("tenant_id"));
										if (cache) template.delete(key);
									}
								}
								allin = false;
								break outter;
							}
						}
						break;
					}
					break;
					case realTimeInvalidClickReport: for (int i = rtinvalidClickReport.length - 1; i > index; i--) if (rtinvalidClickReport[i] == null) {
						allin = false;
						break outter;
					} else if (!rtinvalidClickReport[i].isSuccess()) {
						if (i > index) {
							index = i;
							if (update - (index + 1) * 432000000L < start){
								if (!finish) synchronized(this){
									if (!finish) {
										finish = true;
										logger.process("Task finish fail", 9, account.getString("tenant_id"));
										if (cache) template.delete(key);
									}
								}
								allin = false;
								break outter;
							}
						}
						break;
					}
					break;
					case realTimeKeyWordReport: for (int i = rtkeyWordReport.length - 1; i > index; i--) if (rtkeyWordReport[i] == null) {
						allin = false;
						break outter;
					} else if (!rtkeyWordReport[i].isSuccess()) {
						if (i > index) {
							index = i;
							if (update - (index + 1) * 432000000L < start){
								if (!finish) synchronized(this){
									if (!finish) {
										finish = true;
										logger.process("Task finish fail", 9, account.getString("tenant_id"));
										if (cache) template.delete(key);
									}
								}
								allin = false;
								break outter;
							}
						}
						break;
					}
					break;
					case realTimePathReport: for (int i = rtpathReport.length - 1; i > index; i--) if (rtpathReport[i] == null) {
						allin = false;
						break outter;
					} else if (!rtpathReport[i].isSuccess()) {
						if (i > index) {
							index = i;
							if (update - (index + 1) * 432000000L < start){
								if (!finish) synchronized(this){
									if (!finish) {
										finish = true;
										logger.process("Task finish fail", 9, account.getString("tenant_id"));
										if (cache) template.delete(key);
									}
								}
								allin = false;
								break outter;
							}
						}
						break;
					}
					break;
					case realTimePhoneReport: for (int i = rtphoneReport.length - 1; i > index; i--) if (rtphoneReport[i] == null) {
						allin = false;
						break outter;
					} else if (!rtphoneReport[i].isSuccess()) {
						if (i > index) {
							index = i;
							if (update - (index + 1) * 432000000L < start){
								if (!finish) synchronized(this){
									if (!finish) {
										finish = true;
										logger.process("Task finish fail", 9, account.getString("tenant_id"));
										if (cache) template.delete(key);
									}
								}
								allin = false;
								break outter;
							}
						}
						break;
					}
					break;
					case realTimeSearchWordReport: for (int i = rtsearchWordReport.length - 1; i > index; i--) if (rtsearchWordReport[i] == null) {
						allin = false;
						break outter;
					} else if (!rtsearchWordReport[i].isSuccess()) {
						if (i > index) {
							index = i;
							if (update - (index + 1) * 432000000L < start){
								if (!finish) synchronized(this){
									if (!finish) {
										finish = true;
										logger.process("Task finish fail", 9, account.getString("tenant_id"));
										if (cache) template.delete(key);
									}
								}
								allin = false;
								break outter;
							}
						}
						break;
					}
					case realTimeAccountReport: for (int i = realTimeAccountReport.length - 1; i > index; i--) if (realTimeAccountReport[i] == null) {
						allin = false;
						break outter;
					} else if (!realTimeAccountReport[i].isSuccess()) {
						if (i > index) {
							index = i;
							if (update - (index + 1) * 432000000L < start){
								if (!finish) synchronized(this){
									if (!finish) {
										finish = true;
										logger.process("Task finish fail", 9, account.getString("tenant_id"));
										if (cache) template.delete(key);
									}
								}
								allin = false;
								break outter;
							}
						}
						break;
					}
				}
			if (allin && !finish) synchronized(this){
				if (!finish) {
					finish = true;
					if (cache) {
						final byte[] kb = key.getBytes();
						List<Object> ls = template.executePipelined(new RedisCallback<Object>() {
							@Override
							public Object doInRedis(RedisConnection connection) throws DataAccessException {
								connection.exists(kb);
								connection.persist(kb);
								return null;
							}});
						if (Boolean.FALSE.equals(ls.get(0))) {
							logger.process("Data in cache has lost.", 9, account.getString("tenant_id"));
							return;
						}
					}
				    HashOperations<String, Object, Object> ops = template.opsForHash();
					for (Entry<Type, UUID> entry : uuids.entrySet()) try {
						if (entry.getKey() instanceof ReportType) switch((ReportType)entry.getKey()) {
							case accountReport: for (int i = accountReport.length - 1; i > index; i--) for (AccountReport ar : accountReport[i].getTag() == null ? 
									accountReport[i].getData() : JSON.parseArray((String)ops.get(key, accountReport[i].getTag().toString()), AccountReport.class)) 
								data.process(JSON.toJSONString(ar), entry.getValue(), account.getString("tenant_id") + '|' + (account.get("client_code") == null ? "code" : account.getString("client_code")) + '|' + (account.get("utm_source") == null ? "source" : account.getString("utm_source")) + '|' + ReportType.accountReport, formatter.parse(ar.getTime()).getTime());
							break;
							case adgroupReport: for (int i = adgroupReport.length - 1; i > index; i--) for (AdgroupReport ag : adgroupReport[i].getTag() == null ? 
									adgroupReport[i].getData() : JSON.parseArray((String)ops.get(key, accountReport[i].getTag().toString()), AdgroupReport.class)) 
								data.process(JSON.toJSONString(ag), entry.getValue(), account.getString("tenant_id") + '|' + (account.get("client_code") == null ? "code" : account.getString("client_code")) + '|' + (account.get("utm_source") == null ? "source" : account.getString("utm_source")) + '|' + ReportType.adgroupReport, formatter.parse(ag.getTime()).getTime());
							break;
							case advanceAppReport: for (int i = advanceAppReport.length - 1; i > index; i--) for (AdvanceAppReport aa : advanceAppReport[i].getTag() == null ? 
									advanceAppReport[i].getData() : JSON.parseArray((String)ops.get(key, accountReport[i].getTag().toString()), AdvanceAppReport.class))
								data.process(JSON.toJSONString(aa), entry.getValue(), account.getString("tenant_id")+ '|' + (account.get("client_code") == null ? "code" : account.getString("client_code")) + '|' + (account.get("utm_source") == null ? "source" : account.getString("utm_source")) + '|' + ReportType.advanceAppReport, formatter.parse(aa.getTime()).getTime());
							break;
							case advanceGoldReport: for (int i = advanceGoldReport.length - 1; i > index; i--) for (AdvanceGoldensReport ag : advanceGoldReport[i].getTag() == null ? 
									advanceGoldReport[i].getData() : JSON.parseArray((String)ops.get(key, advanceGoldReport[i].getTag().toString()), AdvanceGoldensReport.class)) 
								data.process(JSON.toJSONString(ag), entry.getValue(), account.getString("tenant_id") + '|' + (account.get("client_code") == null ? "code" : account.getString("client_code")) + '|' + (account.get("utm_source") == null ? "source" : account.getString("utm_source")) + '|' + ReportType.advanceGoldReport, formatter.parse(ag.getTime()).getTime());
							break;
							case advanceImgTextCreativeReport: for (int i = advanceImgTextCreativeReport.length - 1; i > index; i--) for (AdvanceImgTextCreativeReport ai : advanceImgTextCreativeReport[i].getTag() == null ? 
									advanceImgTextCreativeReport[i].getData() : JSON.parseArray((String)ops.get(key, advanceImgTextCreativeReport[i].getTag().toString()), AdvanceImgTextCreativeReport.class)) 
								data.process(JSON.toJSONString(ai), entry.getValue(), account.getString("tenant_id") + '|' + (account.get("client_code") == null ? "code" : account.getString("client_code")) + '|' + (account.get("utm_source") == null ? "source" : account.getString("utm_source")) + '|' + ReportType.advanceImgTextCreativeReport, formatter.parse(ai.getTime()).getTime());
							break;
							case advanceImgTextSublinkReport: for (int i = advanceImgTextSublinkReport.length - 1; i > index; i--) for (AdvanceImgTextSublinkReport at : advanceImgTextSublinkReport[i].getTag() == null ? 
									advanceImgTextSublinkReport[i].getData() : JSON.parseArray((String)ops.get(key, advanceImgTextSublinkReport[i].getTag().toString()), AdvanceImgTextSublinkReport.class)) 
								data.process(JSON.toJSONString(at), entry.getValue(), account.getString("tenant_id") + '|' + (account.get("client_code") == null ? "code" : account.getString("client_code")) + '|' + (account.get("utm_source") == null ? "source" : account.getString("utm_source")) + '|' + ReportType.advanceImgTextSublinkReport, formatter.parse(at.getTime()).getTime());
							break;
							case appReport: for (int i = appReport.length - 1; i > index; i--) for (AppReport a : appReport[i].getTag() == null ? appReport[i].getData() : JSON.parseArray((String)ops.get(key, appReport[i].getTag().toString()), AppReport.class)) 
								data.process(JSON.toJSONString(a), entry.getValue(), account.getString("tenant_id") + '|' + (account.get("client_code") == null ? "code" : account.getString("client_code")) + '|' + (account.get("utm_source") == null ? "source" : account.getString("utm_source")) + '|' + ReportType.appReport, formatter.parse(a.getTime()).getTime());
							break;
							case areaReport: for (int i = areaReport.length - 1; i > index; i--) for (AreaReport ar : areaReport[i].getTag() == null ? areaReport[i].getData() : JSON.parseArray((String)ops.get(key, areaReport[i].getTag().toString()), AreaReport.class)) 
								data.process(JSON.toJSONString(ar), entry.getValue(), account.getString("tenant_id") + '|' + (account.get("client_code") == null ? "code" : account.getString("client_code")) + '|' + (account.get("utm_source") == null ? "source" : account.getString("utm_source")) + '|' + ReportType.areaReport, formatter.parse(ar.getTime()).getTime());
							break;
							case campaignReport: for (int i = campaignReport.length - 1; i > index; i--) for (CampaignReport ca : campaignReport[i].getTag() == null ? campaignReport[i].getData() : JSON.parseArray((String)ops.get(key, campaignReport[i].getTag().toString()), CampaignReport.class)) 
								data.process(JSON.toJSONString(ca), entry.getValue(), account.getString("tenant_id") + '|' + (account.get("client_code") == null ? "code" : account.getString("client_code")) + '|' + (account.get("utm_source") == null ? "source" : account.getString("utm_source")) + '|' + ReportType.campaignReport, formatter.parse(ca.getTime()).getTime());
							break;
							case creativeReport: for (int i = creativeReport.length - 1; i > index; i--) for (CreativeReport cr : creativeReport[i].getTag() == null ? creativeReport[i].getData() : JSON.parseArray((String)ops.get(key, creativeReport[i].getTag().toString()), CreativeReport.class)) 
								data.process(JSON.toJSONString(cr), entry.getValue(), account.getString("tenant_id") + '|' + (account.get("client_code") == null ? "code" : account.getString("client_code")) + '|' + (account.get("utm_source") == null ? "source" : account.getString("utm_source")) + '|' + ReportType.creativeReport, formatter.parse(cr.getTime()).getTime());
							break;
							case invalidClickReport: for (int i = invalidClickReport.length - 1; i > index; i--) for (InvalidClickReport ic : invalidClickReport[i].getTag() == null ? 
									invalidClickReport[i].getData() : JSON.parseArray((String)ops.get(key, invalidClickReport[i].getTag().toString()), InvalidClickReport.class)) 
								data.process(JSON.toJSONString(ic), entry.getValue(), account.getString("tenant_id") + '|' + (account.get("client_code") == null ? "code" : account.getString("client_code")) + '|' + (account.get("utm_source") == null ? "source" : account.getString("utm_source")) + '|' + ReportType.invalidClickReport, formatter.parse(ic.getTime()).getTime());
							break;
							case keyWordReport: for (int i = keyWordReport.length - 1; i > index; i--) for (KeyWordReport kw : keyWordReport[i].getTag() == null ? 
									keyWordReport[i].getData() : JSON.parseArray((String)ops.get(key, keyWordReport[i].getTag().toString()), KeyWordReport.class)) 
								data.process(JSON.toJSONString(kw), entry.getValue(), account.getString("tenant_id") + '|' + (account.get("client_code") == null ? "code" : account.getString("client_code")) + '|' + (account.get("utm_source") == null ? "source" : account.getString("utm_source")) + '|' + ReportType.keyWordReport, formatter.parse(kw.getTime()).getTime());
							break;
							case pathReport: for (int i = pathReport.length - 1; i > index; i--) for (PathReport p : pathReport[i].getTag() == null ? pathReport[i].getData() : JSON.parseArray((String)ops.get(key, pathReport[i].getTag().toString()), PathReport.class)) 
								data.process(JSON.toJSONString(p), entry.getValue(), account.getString("tenant_id") + '|' + (account.get("client_code") == null ? "code" : account.getString("client_code")) + '|' + (account.get("utm_source") == null ? "source" : account.getString("utm_source")) + '|' + ReportType.pathReport, formatter.parse(p.getTime()).getTime());
							break;
							case phoneReport: for (int i = phoneReport.length - 1; i > index; i--) for (PhoneReport ph : phoneReport[i].getTag() == null ? phoneReport[i].getData() : JSON.parseArray((String)ops.get(key, phoneReport[i].getTag().toString()), PhoneReport.class)) 
								data.process(JSON.toJSONString(ph), entry.getValue(), account.getString("tenant_id") + '|' + (account.get("client_code") == null ? "code" : account.getString("client_code")) + '|' + (account.get("utm_source") == null ? "source" : account.getString("utm_source")) + '|' + ReportType.phoneReport, formatter.parse(ph.getTime()).getTime());
							break;
							case searchWordReport: for (int i = searchWordReport.length - 1; i > index; i--) for (SearchWordReport sw : searchWordReport[i].getTag() == null ? searchWordReport[i].getData() : JSON.parseArray((String)ops.get(key, searchWordReport[i].getTag().toString()), SearchWordReport.class)) 
								data.process(JSON.toJSONString(sw), entry.getValue(), account.getString("tenant_id") + '|' + (account.get("client_code") == null ? "code" : account.getString("client_code")) + '|' + (account.get("utm_source") == null ? "source" : account.getString("utm_source")) + '|' + ReportType.searchWordReport, formatter.parse(sw.getTime()).getTime());
							break;
							case realTimeAdgroupReport: for (int i = rtadgroupReport.length - 1; i > index; i--) for (AdgroupReport ag : rtadgroupReport[i].getTag() == null ? 
									rtadgroupReport[i].getData() : JSON.parseArray((String)ops.get(key, rtadgroupReport[i].getTag().toString()), AdgroupReport.class)) 
								data.process(JSON.toJSONString(ag), entry.getValue(), account.getString("tenant_id") + '|' + (account.get("client_code") == null ? "code" : account.getString("client_code")) + '|' + (account.get("utm_source") == null ? "source" : account.getString("utm_source")) + '|' + ReportType.realTimeAdgroupReport, paseDate(ag.getTime()));
							break;
							case realTimeAdvanceAppReport: for (int i = rtadvanceAppReport.length - 1; i > index; i--) for (AdvanceAppReport aa : rtadvanceAppReport[i].getTag() == null ? 
									rtadvanceAppReport[i].getData() : JSON.parseArray((String)ops.get(key, rtadvanceAppReport[i].getTag().toString()), AdvanceAppReport.class))
								data.process(JSON.toJSONString(aa), entry.getValue(), account.getString("tenant_id") + '|' + (account.get("client_code") == null ? "code" : account.getString("client_code")) + '|' + (account.get("utm_source") == null ? "source" : account.getString("utm_source")) + '|' + ReportType.realTimeAdvanceAppReport, paseDate(aa.getTime()));
							break;
							case realTimeAdvanceGoldReport: for (int i = rtadvanceGoldReport.length - 1; i > index; i--) for (AdvanceGoldensReport ag : rtadvanceGoldReport[i].getTag() == null ? 
									rtadvanceGoldReport[i].getData() : JSON.parseArray((String)ops.get(key, rtadvanceGoldReport[i].getTag().toString()), AdvanceGoldensReport.class)) 
								data.process(JSON.toJSONString(ag), entry.getValue(), account.getString("tenant_id") + '|' + (account.get("client_code") == null ? "code" : account.getString("client_code")) + '|' + (account.get("utm_source") == null ? "source" : account.getString("utm_source")) + '|' + ReportType.realTimeAdvanceGoldReport, paseDate(ag.getTime()));
							break;
							case realTimeAdvanceImgTextCreativeReport: for (int i = rtadvanceImgTextCreativeReport.length - 1; i > index; i--) for (AdvanceImgTextCreativeReport ai : rtadvanceImgTextCreativeReport[i].getTag() == null ? 
									rtadvanceImgTextCreativeReport[i].getData() : JSON.parseArray((String)ops.get(key, rtadvanceImgTextCreativeReport[i].getTag().toString()), AdvanceImgTextCreativeReport.class)) 
								data.process(JSON.toJSONString(ai), entry.getValue(), account.getString("tenant_id") + '|' + (account.get("client_code") == null ? "code" : account.getString("client_code")) + '|' + (account.get("utm_source") == null ? "source" : account.getString("utm_source")) + '|' + ReportType.realTimeAdvanceImgTextCreativeReport, paseDate(ai.getTime()));
							break;
							case realTimeAdvanceImgTextSublinkReport: for (int i = rtadvanceImgTextSublinkReport.length - 1; i > index; i--) for (AdvanceImgTextSublinkReport at : rtadvanceImgTextSublinkReport[i].getTag() == null ? 
									rtadvanceImgTextSublinkReport[i].getData() : JSON.parseArray((String)ops.get(key, rtadvanceImgTextSublinkReport[i].getTag().toString()), AdvanceImgTextSublinkReport.class)) 
								data.process(JSON.toJSONString(at), entry.getValue(), account.getString("tenant_id") + '|' + (account.get("client_code") == null ? "code" : account.getString("client_code")) + '|' + (account.get("utm_source") == null ? "source" : account.getString("utm_source")) + '|' + ReportType.realTimeAdvanceImgTextSublinkReport, paseDate(at.getTime()));
							break;
							case realTimeAppReport: for (int i = rtappReport.length - 1; i > index; i--) for (AppReport a : rtappReport[i].getTag() == null ? rtappReport[i].getData() : JSON.parseArray((String)ops.get(key, rtappReport[i].getTag().toString()), AppReport.class)) 
								data.process(JSON.toJSONString(a), entry.getValue(), account.getString("tenant_id") + '|' + (account.get("client_code") == null ? "code" : account.getString("client_code")) + '|' + (account.get("utm_source") == null ? "source" : account.getString("utm_source")) + '|' + ReportType.realTimeAppReport, paseDate(a.getTime()));
							break;
							case realTimeAreaReport: for (int i = rtareaReport.length - 1; i > index; i--) for (AreaReport ar : rtareaReport[i].getTag() == null ? rtareaReport[i].getData() : JSON.parseArray((String)ops.get(key, rtareaReport[i].getTag().toString()), AreaReport.class)) 
								data.process(JSON.toJSONString(ar), entry.getValue(), account.getString("tenant_id") + '|' + (account.get("client_code") == null ? "code" : account.getString("client_code")) + '|' + (account.get("utm_source") == null ? "source" : account.getString("utm_source")) + '|' + ReportType.realTimeAreaReport, paseDate(ar.getTime()));
							break;
							case realTimeCampaignReport: for (int i = rtcampaignReport.length - 1; i > index; i--) for (CampaignReport ca : rtcampaignReport[i].getTag() == null ? rtcampaignReport[i].getData() : JSON.parseArray((String)ops.get(key, rtcampaignReport[i].getTag().toString()), CampaignReport.class)) 
								data.process(JSON.toJSONString(ca), entry.getValue(), account.getString("tenant_id") + '|' + (account.get("client_code") == null ? "code" : account.getString("client_code")) + '|' + (account.get("utm_source") == null ? "source" : account.getString("utm_source")) + '|' + ReportType.realTimeCampaignReport, paseDate(ca.getTime()));
							break;
							case realTimeCreativeReport: for (int i = rtcreativeReport.length - 1; i > index; i--) for (CreativeReport cr : rtcreativeReport[i].getTag() == null ? rtcreativeReport[i].getData() : JSON.parseArray((String)ops.get(key, rtcreativeReport[i].getTag().toString()), CreativeReport.class)) 
								data.process(JSON.toJSONString(cr), entry.getValue(), account.getString("tenant_id") + '|' + (account.get("client_code") == null ? "code" : account.getString("client_code")) + '|' + (account.get("utm_source") == null ? "source" : account.getString("utm_source")) + '|' + ReportType.realTimeCreativeReport, paseDate(cr.getTime()));
							break;
							case realTimeInvalidClickReport: for (int i = rtinvalidClickReport.length - 1; i > index; i--) for (InvalidClickReport ic : rtinvalidClickReport[i].getTag() == null ? 
									rtinvalidClickReport[i].getData() : JSON.parseArray((String)ops.get(key, rtinvalidClickReport[i].getTag().toString()), InvalidClickReport.class)) 
								data.process(JSON.toJSONString(ic), entry.getValue(), account.getString("tenant_id") + '|' + (account.get("client_code") == null ? "code" : account.getString("client_code")) + '|' + (account.get("utm_source") == null ? "source" : account.getString("utm_source")) + '|' + ReportType.realTimeInvalidClickReport, paseDate(ic.getTime()));
							break;
							case realTimeKeyWordReport: for (int i = rtkeyWordReport.length - 1; i > index; i--) for (KeyWordReport kw : rtkeyWordReport[i].getTag() == null ? 
									rtkeyWordReport[i].getData() : JSON.parseArray((String)ops.get(key, rtkeyWordReport[i].getTag().toString()), KeyWordReport.class)) 
								data.process(JSON.toJSONString(kw), entry.getValue(), account.getString("tenant_id") + '|' + (account.get("client_code") == null ? "code" : account.getString("client_code")) + '|' + (account.get("utm_source") == null ? "source" : account.getString("utm_source")) + '|' + ReportType.realTimeKeyWordReport, paseDate(kw.getTime()));
							break;
							case realTimePathReport: for (int i = rtpathReport.length - 1; i > index; i--) for (PathReport p : rtpathReport[i].getTag() == null ? rtpathReport[i].getData() : JSON.parseArray((String)ops.get(key, rtpathReport[i].getTag().toString()), PathReport.class)) 
								data.process(JSON.toJSONString(p), entry.getValue(), account.getString("tenant_id") + '|' + (account.get("client_code") == null ? "code" : account.getString("client_code")) + '|' + (account.get("utm_source") == null ? "source" : account.getString("utm_source")) + '|' + ReportType.realTimePathReport, paseDate(p.getTime()));
							break;
							case realTimePhoneReport: for (int i = rtphoneReport.length - 1; i > index; i--) for (PhoneReport ph : rtphoneReport[i].getTag() == null ? rtphoneReport[i].getData() : JSON.parseArray((String)ops.get(key, rtphoneReport[i].getTag().toString()), PhoneReport.class)) 
								data.process(JSON.toJSONString(ph), entry.getValue(), account.getString("tenant_id") + '|' + (account.get("client_code") == null ? "code" : account.getString("client_code")) + '|' + (account.get("utm_source") == null ? "source" : account.getString("utm_source")) + '|' + ReportType.realTimePhoneReport, paseDate(ph.getTime()));
							break;
							case realTimeSearchWordReport: for (int i = rtsearchWordReport.length - 1; i > index; i--) for (SearchWordReport sw : rtsearchWordReport[i].getTag() == null ? rtsearchWordReport[i].getData() : JSON.parseArray((String)ops.get(key, rtsearchWordReport[i].getTag().toString()), SearchWordReport.class)) 
								data.process(JSON.toJSONString(sw), entry.getValue(), account.getString("tenant_id") + '|' + (account.get("client_code") == null ? "code" : account.getString("client_code")) + '|' + (account.get("utm_source") == null ? "source" : account.getString("utm_source")) + '|' + ReportType.realTimeSearchWordReport, paseDate(sw.getTime()));
							break;
							case realTimeAccountReport: for (int i = realTimeAccountReport.length - 1; i > index; i--) for (RealTimeAccountReport ra : realTimeAccountReport[i].getTag() == null ? realTimeAccountReport[i].getData() : JSON.parseArray((String)ops.get(key, realTimeAccountReport[i].getTag().toString()), RealTimeAccountReport.class)) 
								data.process(JSON.toJSONString(ra), entry.getValue(), account.getString("tenant_id") + '|' + (account.get("client_code") == null ? "code" : account.getString("client_code")) + '|' + (account.get("utm_source") == null ? "source" : account.getString("utm_source")) + '|' + ReportType.realTimeAccountReport, paseDate(ra.getTime()));
							break;
						} else if (partial == null && entry.getKey() instanceof UserinfoType) switch((UserinfoType)entry.getKey()) {
							case Adgroups : if(userinfo.adgroups != null || userinfo.adgroupsid != null) for (Adgroups adgroups : userinfo.adgroupsid == null ? userinfo.adgroups : JSON.parseArray((String)ops.get(key, userinfo.adgroupsid.toString()), Adgroups.class)) data.process(JSON.toJSONString(adgroups), entry.getValue(), account.getString("tenant_id") + '|' + (account.get("client_code") == null ? "code" : account.getString("client_code")) + '|' + (account.get("utm_source") == null ? "source" : account.getString("utm_source")) + '|' + UserinfoType.Adgroups);
							break;
							case AdvancedApps : if(userinfo.advancedApp != null || userinfo.advancedAppid != null) for (AdvancedApp advancedApps : userinfo.advancedAppid == null ? userinfo.advancedApp : JSON.parseArray((String)ops.get(key, userinfo.advancedAppid.toString()), AdvancedApp.class)) data.process(JSON.toJSONString(advancedApps), entry.getValue(), account.getString("tenant_id") + '|' + (account.get("client_code") == null ? "code" : account.getString("client_code")) + '|' + (account.get("utm_source") == null ? "source" : account.getString("utm_source")) + '|' + UserinfoType.AdvancedApps);
							break;
							case AdvancedCssSublinks : if(userinfo.advancedCssSublinks != null || userinfo.advancedCssSublinksid != null) for (AdvancedCssSublinks advancedCssSublinks : userinfo.advancedCssSublinksid == null ? userinfo.advancedCssSublinks : JSON.parseArray((String)ops.get(key, userinfo.advancedCssSublinksid.toString()), AdvancedCssSublinks.class)) data.process(JSON.toJSONString(advancedCssSublinks), entry.getValue(), account.getString("tenant_id") + '|' + (account.get("client_code") == null ? "code" : account.getString("client_code")) + '|' + (account.get("utm_source") == null ? "source" : account.getString("utm_source")) + '|' + UserinfoType.AdvancedCssSublinks);
							break;
							case AdvancedGoldens : if(userinfo.advancedGoldens != null || userinfo.advancedGoldensid != null) for (AdvancedGoldens advancedGoldens : userinfo.advancedGoldensid == null ? userinfo.advancedGoldens : JSON.parseArray((String)ops.get(key, userinfo.advancedGoldensid.toString()), AdvancedGoldens.class)) data.process(JSON.toJSONString(advancedGoldens), entry.getValue(), account.getString("tenant_id") + '|' + (account.get("client_code") == null ? "code" : account.getString("client_code")) + '|' + (account.get("utm_source") == null ? "source" : account.getString("utm_source")) + '|' + UserinfoType.AdvancedGoldens);
							break;
							case Apps : if(userinfo.apps != null || userinfo.appsid != null) for (Apps apps : userinfo.appsid == null ? userinfo.apps : JSON.parseArray((String)ops.get(key, userinfo.appsid.toString()), Apps.class)) data.process(JSON.toJSONString(apps), entry.getValue(), account.getString("tenant_id") + '|' + (account.get("client_code") == null ? "code" : account.getString("client_code")) + '|' + (account.get("utm_source") == null ? "source" : account.getString("utm_source")) + '|' + UserinfoType.Apps);
							break;
							case Campaigns : if(userinfo.campains != null || userinfo.campainsid != null) for (Campaigns campaigns : userinfo.campainsid == null ? userinfo.campains : JSON.parseArray((String)ops.get(key, userinfo.campainsid.toString()), Campaigns.class)) data.process(JSON.toJSONString(campaigns), entry.getValue(), account.getString("tenant_id") + '|' + (account.get("client_code") == null ? "code" : account.getString("client_code")) + '|' + (account.get("utm_source") == null ? "source" : account.getString("utm_source")) + '|' + UserinfoType.Campaigns);
							break;
							case Creatives : if(userinfo.creatives != null || userinfo.creativesid != null) for (Creatives creatives : userinfo.creativesid == null ? userinfo.creatives : JSON.parseArray((String)ops.get(key, userinfo.creativesid.toString()), Creatives.class)) data.process(JSON.toJSONString(creatives), entry.getValue(), account.getString("tenant_id") + '|' + (account.get("client_code") == null ? "code" : account.getString("client_code")) + '|' + (account.get("utm_source") == null ? "source" : account.getString("utm_source")) + '|' + UserinfoType.Creatives);
							break;
							case Keywords : if(userinfo.keywords != null || userinfo.keywordsid != null) for (Keywords keywords : userinfo.keywordsid == null ? userinfo.keywords : JSON.parseArray((String)ops.get(key, userinfo.keywordsid.toString()), Keywords.class)) data.process(JSON.toJSONString(keywords), entry.getValue(), account.getString("tenant_id") + '|' + (account.get("client_code") == null ? "code" : account.getString("client_code")) + '|' + (account.get("utm_source") == null ? "source" : account.getString("utm_source")) + '|' + UserinfoType.Keywords);
							break;
							case Sublinks : if(userinfo.sublinks != null || userinfo.sublinksid != null) for (Sublinks sublinks : userinfo.sublinksid == null ? userinfo.sublinks : JSON.parseArray((String)ops.get(key, userinfo.sublinksid.toString()), Sublinks.class)) data.process(JSON.toJSONString(sublinks), entry.getValue(), account.getString("tenant_id") + '|' + (account.get("client_code") == null ? "code" : account.getString("client_code")) + '|' + (account.get("utm_source") == null ? "source" : account.getString("utm_source")) + '|' + UserinfoType.Sublinks);
							break;
							case AdvancedPicTexts : if(userinfo.advancedPicTexts != null || userinfo.advancedPicTextsid != null) for (AdvancedPicTexts advancedPicTexts : userinfo.advancedPicTextsid == null ? userinfo.advancedPicTexts : JSON.parseArray((String)ops.get(key, userinfo.advancedPicTextsid.toString()), AdvancedPicTexts.class)) data.process(JSON.toJSONString(advancedPicTexts), entry.getValue(), account.getString("tenant_id") + '|' + (account.get("client_code") == null ? "code" : account.getString("client_code")) + '|' + (account.get("utm_source") == null ? "source" : account.getString("utm_source")) + '|' + UserinfoType.AdvancedPicTexts);
						}
					} catch (Exception e) {
						logger.process(Task.getMessage(e), 10125, account.getString("tenant_id"));
					}
					if (cache) try {
						template.delete(key);
					} catch (Exception e) {
						logger.process(Task.getMessage(e), 10126, account.getString("tenant_id"));
					}
					if (!Boolean.FALSE.equals(partial)) {
						try {
							JsonObject result = new HttpClient().rest("GET", updateUrl + "?tenant_id=" + account.getString("tenant_id") + "&update_time=" + (update - (index + 1) * 604800000L), null);
							if ("OK".equals(result.getString("code"))) logger.process("Task finish successful", 9, account.getString("tenant_id"));
							else logger.process(result.getString("result"), 10228, account.getString("tenant_id"));
						} catch (Exception e) {
							logger.process(Task.getMessage(e), 10127, account.getString("tenant_id"));
						} 
					}
				}
			}
		} else {
			finish = true;
			logger.process("Task finish fail", 9, account.getString("tenant_id"));
			if (cache) template.delete(key);
		}
	}
	
	public class UserInfo {
		private boolean isSuccess;
		private List<Adgroups> adgroups;
		private UUID adgroupsid;
		private List<AdvancedApp> advancedApp;
		private UUID advancedAppid;
		private List<AdvancedCssSublinks> advancedCssSublinks;
		private UUID advancedCssSublinksid;
		private List<AdvancedGoldens> advancedGoldens;
		private UUID advancedGoldensid;
		private List<Apps> apps;
		private UUID appsid;
		private List<Campaigns> campains;
		private UUID campainsid;
		private List<Creatives> creatives;
		private UUID creativesid;
		private List<Keywords> keywords;
		private UUID keywordsid;
		private List<Sublinks> sublinks;
		private UUID sublinksid;
		private List<AdvancedPicTexts> advancedPicTexts;
		private UUID advancedPicTextsid;
		
		public UserInfo() {}

		public UserInfo(boolean isSuccess) {
			this.isSuccess=isSuccess;
		}
		
		public UserInfo(List<Adgroups> adgroups, List<AdvancedApp> advancedApp,
				List<AdvancedCssSublinks> advancedCssSublinks, List<AdvancedGoldens> advancedGoldens, List<Apps> apps,
				List<Campaigns> campains, List<Creatives> creatives, List<Keywords> keywords, List<Sublinks> sublinks,List<AdvancedPicTexts> advancedPicTexts) {
			this.adgroups = adgroups;
			this.advancedApp = advancedApp;
			this.advancedCssSublinks = advancedCssSublinks;
			this.advancedGoldens = advancedGoldens;
			this.apps = apps;
			this.campains = campains;
			this.creatives = creatives;
			this.keywords = keywords;
			this.sublinks = sublinks;
			this.advancedPicTexts = advancedPicTexts;
		}

		public boolean isFull() {
			if (full == null) {
				Boolean ret = true;
outter:			for (Type type : uuids.keySet()) 
					if (type instanceof UserinfoType) switch((UserinfoType)type) {
						case Adgroups : ret = adgroups != null;
						if (!ret) break outter;
						break;
						case AdvancedApps : ret = advancedApp != null;
						if (!ret) break outter;
						break;
						case AdvancedCssSublinks : ret = advancedCssSublinks != null;
						if (!ret) break outter;
						break;
						case AdvancedGoldens : ret = advancedGoldens != null;
						if (!ret) break outter;
						break;
						case Apps : ret = apps != null;
						if (!ret) break outter;
						break;
						case Campaigns : ret = campains != null;
						if (!ret) break outter;
						break;
						case Creatives : ret = creatives != null;
						if (!ret) break outter;
						break;
						case Keywords : ret = keywords != null;
						if (!ret) break outter;
						break;
						case Sublinks : ret = sublinks != null;
						if (!ret) break outter;
						break;
						case AdvancedPicTexts : ret = advancedPicTexts != null;
						if (!ret) break outter;
					}
				return ret;
			} else if (full) return adgroups != null && advancedApp != null && advancedCssSublinks != null && 
					advancedGoldens != null && apps != null && campains != null &&
					creatives != null && keywords != null && sublinks != null && advancedPicTexts!=null;
			else return true;
		}
		
		public List<AdvancedPicTexts> getAdvancedPicTexts() {
			return advancedPicTexts;
		}

		public void setAdvancedPicTexts(List<AdvancedPicTexts> advancedPicTexts) {
			this.advancedPicTexts = advancedPicTexts;
		}

		public boolean isSuccess() {
			return isSuccess;
		}

		public void setSuccess(boolean isSuccess) {
			this.isSuccess = isSuccess;
		}

		public List<Adgroups> getAdgroups() {
			return adgroups;
		}

		public List<AdvancedApp> getAdvancedApp() {
			return advancedApp;
		}

		public List<AdvancedCssSublinks> getAdvancedCssSublinks() {
			return advancedCssSublinks;
		}

		public List<AdvancedGoldens> getAdvancedGoldens() {
			return advancedGoldens;
		}

		public List<Apps> getApps() {
			return apps;
		}

		public List<Campaigns> getCampains() {
			return campains;
		}

		public List<Creatives> getCreatives() {
			return creatives;
		}

		public List<Keywords> getKeywords() {
			return keywords;
		}

		public List<Sublinks> getSublinks() {
			return sublinks;
		}

		public void setAdgroups(List<Adgroups> adgroups) {
			this.adgroups = adgroups;
		}

		public void setAdvancedApp(List<AdvancedApp> advancedApp) {
			this.advancedApp = advancedApp;
		}

		public void setAdvancedCssSublinks(List<AdvancedCssSublinks> advancedCssSublinks) {
			this.advancedCssSublinks = advancedCssSublinks;
		}

		public void setAdvancedGoldens(List<AdvancedGoldens> advancedGoldens) {
			this.advancedGoldens = advancedGoldens;
		}

		public void setApps(List<Apps> apps) {
			this.apps = apps;
		}

		public void setCampains(List<Campaigns> campains) {
			this.campains = campains;
		}

		public void setCreatives(List<Creatives> creatives) {
			this.creatives = creatives;
		}

		public void setKeywords(List<Keywords> keywords) {
			this.keywords = keywords;
		}

		public void setSublinks(List<Sublinks> sublinks) {
			this.sublinks = sublinks;
		}
	}
	public  Long paseDate(String date) throws ParseException{
		return formatter.parse(date.substring(0,10)).getTime()+Long.parseUnsignedLong(date.charAt(date.indexOf("时")-2)+""+date.charAt(date.indexOf("时")-1))*3600*1000;
	}
	public interface Type {
		public String name();
		public int ordinal();
	}
	
	public enum ReportType implements Type{
		accountReport,adgroupReport,advanceAppReport,advanceGoldReport,advanceImgTextCreativeReport,advanceImgTextSublinkReport,appReport,areaReport,campaignReport,creativeReport,invalidClickReport,keyWordReport,pathReport,phoneReport,searchWordReport,
		realTimeAccountReport,realTimeAdgroupReport,realTimeAdvanceAppReport,realTimeAdvanceGoldReport,realTimeAdvanceImgTextCreativeReport,realTimeAdvanceImgTextSublinkReport,realTimeAppReport,realTimeAreaReport,realTimeCampaignReport,realTimeCreativeReport,realTimeInvalidClickReport,realTimeKeyWordReport,realTimePathReport,realTimePhoneReport,realTimeSearchWordReport
	}
	
	public enum UserinfoType implements Type{
		Adgroups,AdvancedApps,AdvancedCssSublinks,AdvancedGoldens,Apps,Campaigns,Creatives,Keywords,Sublinks,AdvancedPicTexts
	}
}