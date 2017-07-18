package com.cubead.clm.io.sogou.data;

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
import com.cubead.clm.io.sogou.HttpClient;
import com.cubead.clm.io.sogou.Task;
import com.cubead.clm.io.sogou.data.accountMsg.CpcIdeaType;
import com.cubead.clm.io.sogou.data.accountMsg.CpcType;
import com.cubead.clm.io.sogou.data.entity.AccountReport;
import com.cubead.clm.io.sogou.data.entity.GroupReport;
import com.cubead.clm.io.sogou.data.entity.IdeaReport;
import com.cubead.clm.io.sogou.data.entity.KeyWordReport;
import com.cubead.clm.io.sogou.data.entity.KeyWordShowNoClickReport;
import com.cubead.clm.io.sogou.data.entity.PlanReport;
import com.cubead.clm.io.sogou.data.entity.SearchWordReport;
import com.sogou.api.sem.v1.account.AccountInfoType;

import com.sogou.api.sem.v1.cpcextraidea.CpcExIdeaType;
import com.sogou.api.sem.v1.cpcgrp.CpcGrpType;

import com.sogou.api.sem.v1.cpcplan.CpcPlanType;

public class DataStorage {
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
	private Result<GroupReport>[] groupReport;
	private Result<IdeaReport>[] ideaReport;
	private Result<KeyWordReport>[] keyWordReport;
	private Result<KeyWordShowNoClickReport>[] keyWordShowNoClickReport;
	private Result<PlanReport>[] planReport;
	private Result<SearchWordReport>[] searchWordReport;
	private Result<AccountReport>[] accountReport_mobile;
	private Result<GroupReport>[] groupReport_mobile;
	private Result<IdeaReport>[] ideaReport_mobile;
	private Result<KeyWordReport>[] keyWordReport_mobile;
	private Result<KeyWordShowNoClickReport>[] keyWordShowNoClickReport_mobile;
	private Result<PlanReport>[] planReport_mobile;
	private Result<SearchWordReport>[] searchWordReport_mobile;
	private UserInfo userinfo;
	private final String key;
	private Boolean full;
	
	public DataStorage(Boolean full, long update, long start, long timeout, int count, RedisTemplate<String, String> template, 
			Boolean partial, JsonObject account, IProcessor<Object, Boolean> data, IProcessor<Object, Boolean> logger,
			HashMap<Type, UUID> uuids, String updateUrl, Result<AccountReport>[] accountReport, Result<GroupReport>[] groupReport, Result<IdeaReport>[] ideaReport, 
			Result<KeyWordReport>[] keyWordReport, Result<PlanReport>[] planReport, Result<KeyWordShowNoClickReport>[] keyWordShowNoClickReport,
			Result<SearchWordReport>[] searchWordReport, Result<AccountReport>[] accountReport_mobile, Result<GroupReport>[] groupReport_mobile, Result<IdeaReport>[] ideaReport_mobile, 
			Result<KeyWordReport>[] keyWordReport_mobile, Result<PlanReport>[] planReport_mobile, Result<KeyWordShowNoClickReport>[] keyWordShowNoClickReport_mobile,
			Result<SearchWordReport>[] searchWordReport_mobile) {
		this.full=full;
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
		this.updateUrl = updateUrl;
		this.accountReport = accountReport;
		this.groupReport = groupReport;
		this.ideaReport = ideaReport;
		this.keyWordShowNoClickReport = keyWordShowNoClickReport;
		this.planReport = planReport;
		this.keyWordReport = keyWordReport;
		this.searchWordReport = searchWordReport;
		this.accountReport_mobile = accountReport_mobile;
		this.groupReport_mobile = groupReport_mobile;
		this.ideaReport_mobile = ideaReport_mobile;
		this.keyWordShowNoClickReport_mobile = keyWordShowNoClickReport_mobile;
		this.planReport_mobile = planReport_mobile;
		this.keyWordReport_mobile = keyWordReport_mobile;
		this.searchWordReport_mobile = searchWordReport_mobile;
		this.key = "cache.sogou." + account.getString("tenant_id") + '.' + UUID.randomUUID();
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
					logger.process(Task.getMessage(e), 10219, account.getString("tenant_id"));
				}
			}
			switch(type) {
				case accountReport: accountReport[team] = (Result<AccountReport>)result;
				break;
				case groupReport: groupReport[team] = (Result<GroupReport>)result;
				break;
				case ideaReport: ideaReport[team] = (Result<IdeaReport>)result;
				break;
				case keyWordShowNoClickReport: keyWordShowNoClickReport[team] = (Result<KeyWordShowNoClickReport>)result;
				break;
				case planReport: planReport[team] = (Result<PlanReport>)result;
				break;
				case keyWordReport: keyWordReport[team] = (Result<KeyWordReport>)result;
				break;
				case searchWordReport: searchWordReport[team] = (Result<SearchWordReport>)result;
				break;
				case accountReport_mobile: accountReport_mobile[team] = (Result<AccountReport>)result;
				break;
				case groupReport_mobile: groupReport_mobile[team] = (Result<GroupReport>)result;
				break;
				case ideaReport_mobile: ideaReport_mobile[team] = (Result<IdeaReport>)result;
				break;
				case keyWordShowNoClickReport_mobile: keyWordShowNoClickReport_mobile[team] = (Result<KeyWordShowNoClickReport>)result;
				break;
				case planReport_mobile: planReport_mobile[team] = (Result<PlanReport>)result;
				break;
				case keyWordReport_mobile: keyWordReport_mobile[team] = (Result<KeyWordReport>)result;
				break;
				case searchWordReport_mobile: searchWordReport_mobile[team] = (Result<SearchWordReport>)result;
			}
			checkin();
		}
	}
	
	public void setUserinfo(final UserInfo userinfo) {
		if (!finish && partial == null) {
			this.userinfo = userinfo;
			if (userinfo != null && userinfo.isSuccess) {
				final byte[] kb = key.getBytes();
				if (userinfo.account.size() >= count) {
					final UUID id = UUID.randomUUID();
					try {
						if (timeout > 0) {
							final byte[] ib = id.toString().getBytes();
							final byte[] db = JSON.toJSONString(userinfo.account).getBytes("UTF-8");
							template.executePipelined(new RedisCallback<Object>() {
							@Override
							public Object doInRedis(RedisConnection connection) throws DataAccessException {
								connection.hSet(kb, ib, db);
								connection.expire(kb, timeout);
								return null;
							}});} else template.opsForHash().put(key, id.toString(), JSON.toJSONString(userinfo.account));
						userinfo.account = null;
						userinfo.accountid = id;
					} catch (Exception e) {
						logger.process(Task.getMessage(e), 10220, account.getString("tenant_id"));
					}
				}
				if (userinfo.cpcplan.size() >= count) {
					final UUID id = UUID.randomUUID();
					try {
						if (timeout > 0) {
							final byte[] ib = id.toString().getBytes();
							final byte[] db = JSON.toJSONString(userinfo.cpcplan).getBytes("UTF-8");
							template.executePipelined(new RedisCallback<Object>() {
							@Override
							public Object doInRedis(RedisConnection connection) throws DataAccessException {
								connection.hSet(kb, ib, db);
								connection.expire(kb, timeout);
								return null;
							}});} else template.opsForHash().put(key, id.toString(), JSON.toJSONString(userinfo.cpcplan));
						userinfo.cpcplan = null;
						userinfo.cpcplanid = id;
					} catch (Exception e) {
						logger.process(Task.getMessage(e), 10221, account.getString("tenant_id"));
					}
				}
				if (userinfo.cpc.size() >= count) {
					final UUID id = UUID.randomUUID();
					try {
						if (timeout > 0) {
							final byte[] ib = id.toString().getBytes();
							final byte[] db = JSON.toJSONString(userinfo.cpc).getBytes("UTF-8");
							template.executePipelined(new RedisCallback<Object>() {
							@Override
							public Object doInRedis(RedisConnection connection) throws DataAccessException {
								connection.hSet(kb, ib, db);
								connection.expire(kb, timeout);
								return null;
							}});} else template.opsForHash().put(key, id.toString(), JSON.toJSONString(userinfo.cpc));
						userinfo.cpc = null;
						userinfo.cpcid = id;
					} catch (Exception e) {
						logger.process(Task.getMessage(e), 10222, account.getString("tenant_id"));
					}
				}
				if (userinfo.cpcgrp.size() >= count) {
					final UUID id = UUID.randomUUID();
					try {
						if (timeout > 0) {
							final byte[] ib = id.toString().getBytes();
							final byte[] db = JSON.toJSONString(userinfo.cpcgrp).getBytes("UTF-8");
							template.executePipelined(new RedisCallback<Object>() {
							@Override
							public Object doInRedis(RedisConnection connection) throws DataAccessException {
								connection.hSet(kb, ib, db);
								connection.expire(kb, timeout);
								return null;
							}});} else template.opsForHash().put(key, id.toString(), JSON.toJSONString(userinfo.cpcgrp));
						userinfo.cpcgrp = null;
						userinfo.cpcgrpid = id;
					} catch (Exception e) {
						logger.process(Task.getMessage(e), 10223, account.getString("tenant_id"));
					}
				}
				if (userinfo.cpcidea.size() >= count) {
					final UUID id = UUID.randomUUID();
					try {
						if (timeout > 0) {
							final byte[] ib = id.toString().getBytes();
							final byte[] db = JSON.toJSONString(userinfo.cpcidea).getBytes("UTF-8");
							template.executePipelined(new RedisCallback<Object>() {
							@Override
							public Object doInRedis(RedisConnection connection) throws DataAccessException {
								connection.hSet(kb, ib, db);
								connection.expire(kb, timeout);
								return null;
							}});} else template.opsForHash().put(key, id.toString(), JSON.toJSONString(userinfo.cpcidea));
						userinfo.cpcidea = null;
						userinfo.cpcideaid = id;
					} catch (Exception e) {
						logger.process(Task.getMessage(e), 10224, account.getString("tenant_id"));
					}
				}
				if (userinfo.cpcexidea.size() >= count) {
					final UUID id = UUID.randomUUID();
					try {
						if (timeout > 0) {
							final byte[] ib = id.toString().getBytes();
							final byte[] db = JSON.toJSONString(userinfo.cpcexidea).getBytes("UTF-8");
							template.executePipelined(new RedisCallback<Object>() {
							@Override
							public Object doInRedis(RedisConnection connection) throws DataAccessException {
								connection.hSet(kb, ib, db);
								connection.expire(kb, timeout);
								return null;
							}});} else template.opsForHash().put(key, id.toString(), JSON.toJSONString(userinfo.cpcexidea));
						userinfo.cpcexidea = null;
						userinfo.cpcexideaid = id;
					} catch (Exception e) {
						logger.process(Task.getMessage(e), 10225, account.getString("tenant_id"));
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
							if (update - (index + 1) * 604800000L < start){
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
					case groupReport: for (int i = groupReport.length - 1; i > index; i--) if (groupReport[i] == null) {
						allin = false;
						break outter;
					} else if (!groupReport[i].isSuccess()) {
						if (i > index) {
							index = i;
							if (update - (index + 1) * 604800000L < start){
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
					case ideaReport: for (int i = ideaReport.length - 1; i > index; i--) if (ideaReport[i] == null) {
						allin = false;
						break outter;
					} else if (!ideaReport[i].isSuccess()) {
						if (i > index) {
							index = i;
							if (update - (index + 1) * 604800000L < start){
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
					case keyWordShowNoClickReport: for (int i = keyWordShowNoClickReport.length - 1; i > index; i--) if (keyWordShowNoClickReport[i] == null) {
						allin = false;
						break outter;
					} else if (!keyWordShowNoClickReport[i].isSuccess()) {
						if (i > index) {
							index = i;
							if (update - (index + 1) * 604800000L < start){
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
					case planReport: for (int i = planReport.length - 1; i > index; i--) if (planReport[i] == null) {
						allin = false;
						break outter;
					} else if (!planReport[i].isSuccess()) {
						if (i > index) {
							index = i;
							if (update - (index + 1) * 604800000L < start){
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
							if (update - (index + 1) * 604800000L < start){
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
							if (update - (index + 1) * 604800000L < start){
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
					case accountReport_mobile: for (int i = accountReport_mobile.length - 1; i > index; i--) if (accountReport_mobile[i] == null) {
						allin = false;
						break outter;
					} else if (!accountReport_mobile[i].isSuccess()) {
						if (i > index) {
							index = i;
							if (update - (index + 1) * 604800000L < start){
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
					case groupReport_mobile: for (int i = groupReport_mobile.length - 1; i > index; i--) if (groupReport_mobile[i] == null) {
						allin = false;
						break outter;
					} else if (!groupReport_mobile[i].isSuccess()) {
						if (i > index) {
							index = i;
							if (update - (index + 1) * 604800000L < start){
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
					case ideaReport_mobile: for (int i = ideaReport_mobile.length - 1; i > index; i--) if (ideaReport_mobile[i] == null) {
						allin = false;
						break outter;
					} else if (!ideaReport_mobile[i].isSuccess()) {
						if (i > index) {
							index = i;
							if (update - (index + 1) * 604800000L < start){
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
					case keyWordShowNoClickReport_mobile: for (int i = keyWordShowNoClickReport_mobile.length - 1; i > index; i--) if (keyWordShowNoClickReport_mobile[i] == null) {
						allin = false;
						break outter;
					} else if (!keyWordShowNoClickReport_mobile[i].isSuccess()) {
						if (i > index) {
							index = i;
							if (update - (index + 1) * 604800000L < start){
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
					case planReport_mobile: for (int i = planReport_mobile.length - 1; i > index; i--) if (planReport_mobile[i] == null) {
						allin = false;
						break outter;
					} else if (!planReport_mobile[i].isSuccess()) {
						if (i > index) {
							index = i;
							if (update - (index + 1) * 604800000L < start){
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
					case keyWordReport_mobile: for (int i = keyWordReport_mobile.length - 1; i > index; i--) if (keyWordReport_mobile[i] == null) {
						allin = false;
						break outter;
					} else if (!keyWordReport_mobile[i].isSuccess()) {
						if (i > index) {
							index = i;
							if (update - (index + 1) * 604800000L < start){
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
					case searchWordReport_mobile: for (int i = searchWordReport_mobile.length - 1; i > index; i--) if (searchWordReport_mobile[i] == null) {
						allin = false;
						break outter;
					} else if (!searchWordReport_mobile[i].isSuccess()) {
						if (i > index) {
							index = i;
							if (update - (index + 1) * 604800000L < start){
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
								data.process(JSON.toJSONString(ar), entry.getValue(), account.getString("tenant_id") + '|' + (account.get("client_code") == null ? "code" : account.getString("client_code")) + '|' + (account.get("utm_source") == null ? "source" : account.getString("utm_source")) + '|' + entry.getKey(), ar.getDate().getTime());
							break;
							case groupReport: for (int i = groupReport.length - 1; i > index; i--) for (GroupReport ag : groupReport[i].getTag() == null ? 
									groupReport[i].getData() : JSON.parseArray((String)ops.get(key, groupReport[i].getTag().toString()), GroupReport.class)) 
								data.process(JSON.toJSONString(ag), entry.getValue(), account.getString("tenant_id") + '|' + (account.get("client_code") == null ? "code" : account.getString("client_code")) + '|' + (account.get("utm_source") == null ? "source" : account.getString("utm_source")) + '|' + entry.getKey(), ag.getDate().getTime());
							break;
							case ideaReport: for (int i = ideaReport.length - 1; i > index; i--) for (IdeaReport aa : ideaReport[i].getTag() == null ? 
									ideaReport[i].getData() : JSON.parseArray((String)ops.get(key, ideaReport[i].getTag().toString()), IdeaReport.class))
								data.process(JSON.toJSONString(aa), entry.getValue(), account.getString("tenant_id") + '|' + (account.get("client_code") == null ? "code" : account.getString("client_code")) + '|' + (account.get("utm_source") == null ? "source" : account.getString("utm_source")) + '|' + entry.getKey(), aa.getDate().getTime());
							break;
							case keyWordShowNoClickReport: for (int i = keyWordShowNoClickReport.length - 1; i > index; i--) for (KeyWordShowNoClickReport ag : keyWordShowNoClickReport[i].getTag() == null ? 
									keyWordShowNoClickReport[i].getData() : JSON.parseArray((String)ops.get(key, keyWordShowNoClickReport[i].getTag().toString()), KeyWordShowNoClickReport.class)) 
								data.process(JSON.toJSONString(ag), entry.getValue(), account.getString("tenant_id") + '|' + (account.get("client_code") == null ? "code" : account.getString("client_code")) + '|' + (account.get("utm_source") == null ? "source" : account.getString("utm_source")) + '|' + entry.getKey(), ag.getDate().getTime());
							break;
							case planReport: for (int i = planReport.length - 1; i > index; i--) for (PlanReport ai : planReport[i].getTag() == null ? 
									planReport[i].getData() : JSON.parseArray((String)ops.get(key, planReport[i].getTag().toString()), PlanReport.class)) 
								data.process(JSON.toJSONString(ai), entry.getValue(), account.getString("tenant_id") + '|' + (account.get("client_code") == null ? "code" : account.getString("client_code")) + '|' + (account.get("utm_source") == null ? "source" : account.getString("utm_source")) + '|' + entry.getKey(), ai.getDate().getTime());
							break;
							case keyWordReport: for (int i = keyWordReport.length - 1; i > index; i--) for (KeyWordReport kw : keyWordReport[i].getTag() == null ? 
									keyWordReport[i].getData() : JSON.parseArray((String)ops.get(key, keyWordReport[i].getTag().toString()), KeyWordReport.class)) 
								data.process(JSON.toJSONString(kw), entry.getValue(), account.getString("tenant_id") + '|' + (account.get("client_code") == null ? "code" : account.getString("client_code")) + '|' + (account.get("utm_source") == null ? "source" : account.getString("utm_source")) + '|' + entry.getKey(), kw.getDate().getTime());
							break;
							case searchWordReport: for (int i = searchWordReport.length - 1; i > index; i--) for (SearchWordReport sw : searchWordReport[i].getTag() == null ? searchWordReport[i].getData() : JSON.parseArray((String)ops.get(key, searchWordReport[i].getTag().toString()), SearchWordReport.class)) 
								data.process(JSON.toJSONString(sw), entry.getValue(), account.getString("tenant_id") + '|' + (account.get("client_code") == null ? "code" : account.getString("client_code")) + '|' + (account.get("utm_source") == null ? "source" : account.getString("utm_source")) + '|' + entry.getKey(), sw.getDate().getTime());
							break;
							case accountReport_mobile: for (int i = accountReport_mobile.length - 1; i > index; i--) for (AccountReport ar : accountReport_mobile[i].getTag() == null ? 
									accountReport_mobile[i].getData() : JSON.parseArray((String)ops.get(key, accountReport_mobile[i].getTag().toString()), AccountReport.class)) 
								data.process(JSON.toJSONString(ar), entry.getValue(), account.getString("tenant_id") + '|' + (account.get("client_code") == null ? "code" : account.getString("client_code")) + '|' + (account.get("utm_source") == null ? "source" : account.getString("utm_source")) + '|' + entry.getKey(), ar.getDate().getTime());
							break;
							case groupReport_mobile: for (int i = groupReport_mobile.length - 1; i > index; i--) for (GroupReport ag : groupReport_mobile[i].getTag() == null ? 
									groupReport_mobile[i].getData() : JSON.parseArray((String)ops.get(key, groupReport_mobile[i].getTag().toString()), GroupReport.class)) 
								data.process(JSON.toJSONString(ag), entry.getValue(), account.getString("tenant_id") + '|' + (account.get("client_code") == null ? "code" : account.getString("client_code")) + '|' + (account.get("utm_source") == null ? "source" : account.getString("utm_source")) + '|' + entry.getKey(), ag.getDate().getTime());
							break;
							case ideaReport_mobile: for (int i = ideaReport_mobile.length - 1; i > index; i--) for (IdeaReport aa : ideaReport_mobile[i].getTag() == null ? 
									ideaReport_mobile[i].getData() : JSON.parseArray((String)ops.get(key, ideaReport_mobile[i].getTag().toString()), IdeaReport.class))
								data.process(JSON.toJSONString(aa), entry.getValue(), account.getString("tenant_id") + '|' + (account.get("client_code") == null ? "code" : account.getString("client_code")) + '|' + (account.get("utm_source") == null ? "source" : account.getString("utm_source")) + '|' + entry.getKey(), aa.getDate().getTime());
							break;
							case keyWordShowNoClickReport_mobile: for (int i = keyWordShowNoClickReport_mobile.length - 1; i > index; i--) for (KeyWordShowNoClickReport ag : keyWordShowNoClickReport_mobile[i].getTag() == null ? 
									keyWordShowNoClickReport_mobile[i].getData() : JSON.parseArray((String)ops.get(key, keyWordShowNoClickReport_mobile[i].getTag().toString()), KeyWordShowNoClickReport.class)) 
								data.process(JSON.toJSONString(ag), entry.getValue(), account.getString("tenant_id") + '|' + (account.get("client_code") == null ? "code" : account.getString("client_code")) + '|' + (account.get("utm_source") == null ? "source" : account.getString("utm_source")) + '|' + entry.getKey(), ag.getDate().getTime());
							break;
							case planReport_mobile: for (int i = planReport_mobile.length - 1; i > index; i--) for (PlanReport ai : planReport_mobile[i].getTag() == null ? 
									planReport_mobile[i].getData() : JSON.parseArray((String)ops.get(key, planReport_mobile[i].getTag().toString()), PlanReport.class)) 
								data.process(JSON.toJSONString(ai), entry.getValue(), account.getString("tenant_id") + '|' + (account.get("client_code") == null ? "code" : account.getString("client_code")) + '|' + (account.get("utm_source") == null ? "source" : account.getString("utm_source")) + '|' + entry.getKey(), ai.getDate().getTime());
							break;
							case keyWordReport_mobile: for (int i = keyWordReport_mobile.length - 1; i > index; i--) for (KeyWordReport kw : keyWordReport_mobile[i].getTag() == null ? 
									keyWordReport_mobile[i].getData() : JSON.parseArray((String)ops.get(key, keyWordReport_mobile[i].getTag().toString()), KeyWordReport.class)) 
								data.process(JSON.toJSONString(kw), entry.getValue(), account.getString("tenant_id") + '|' + (account.get("client_code") == null ? "code" : account.getString("client_code")) + '|' + (account.get("utm_source") == null ? "source" : account.getString("utm_source")) + '|' + entry.getKey(), kw.getDate().getTime());
							break;
							case searchWordReport_mobile: for (int i = searchWordReport_mobile.length - 1; i > index; i--) for (SearchWordReport sw : searchWordReport_mobile[i].getTag() == null ? searchWordReport_mobile[i].getData() : JSON.parseArray((String)ops.get(key, searchWordReport_mobile[i].getTag().toString()), SearchWordReport.class)) 
								data.process(JSON.toJSONString(sw), entry.getValue(), account.getString("tenant_id") + '|' + (account.get("client_code") == null ? "code" : account.getString("client_code")) + '|' + (account.get("utm_source") == null ? "source" : account.getString("utm_source")) + '|' + entry.getKey(), sw.getDate().getTime());
						} else if (partial == null&& entry.getKey() instanceof UserinfoType) switch((UserinfoType)entry.getKey()) {
							case account : if(userinfo.account != null || userinfo.accountid != null) for (AccountInfoType acc : userinfo.accountid == null ? userinfo.account : JSON.parseArray((String)ops.get(key, userinfo.accountid.toString()), AccountInfoType.class)) data.process(JSON.toJSONString(acc), entry.getValue(), account.getString("tenant_id") + '|' + (account.get("client_code") == null ? "code" : account.getString("client_code")) + '|' + (account.get("utm_source") == null ? "source" : account.getString("utm_source")) + '|' + UserinfoType.account);
							break;
							case cpcplan : if(userinfo.cpcplan != null || userinfo.cpcplanid != null) for (CpcPlanType cpcplan : userinfo.cpcplanid == null ? userinfo.cpcplan : JSON.parseArray((String)ops.get(key, userinfo.cpcplanid.toString()), CpcPlanType.class)) data.process(JSON.toJSONString(cpcplan), entry.getValue(), account.getString("tenant_id") + '|' + (account.get("client_code") == null ? "code" : account.getString("client_code")) + '|' + (account.get("utm_source") == null ? "source" : account.getString("utm_source")) + '|' + UserinfoType.cpcplan);
							break;
							case cpc : if(userinfo.cpc != null || userinfo.cpcid != null) for (CpcType cpc : userinfo.cpcid == null ? userinfo.cpc : JSON.parseArray((String)ops.get(key, userinfo.cpcid.toString()), CpcType.class)) data.process(JSON.toJSONString(cpc), entry.getValue(), account.getString("tenant_id") + '|' + (account.get("client_code") == null ? "code" : account.getString("client_code")) + '|' + (account.get("utm_source") == null ? "source" : account.getString("utm_source")) + '|' + UserinfoType.cpc);
							break;
							case cpcgrp : if(userinfo.cpcgrp != null || userinfo.cpcgrpid != null) for (CpcGrpType cpcgrp : userinfo.cpcgrpid == null ? userinfo.cpcgrp : JSON.parseArray((String)ops.get(key, userinfo.cpcgrpid.toString()), CpcGrpType.class)) data.process(JSON.toJSONString(cpcgrp), entry.getValue(), account.getString("tenant_id") + '|' + (account.get("client_code") == null ? "code" : account.getString("client_code")) + '|' + (account.get("utm_source") == null ? "source" : account.getString("utm_source")) + '|' + UserinfoType.cpcgrp);
							break;
							case cpcidea : if(userinfo.cpcidea != null || userinfo.cpcideaid != null) for (CpcIdeaType cpcidea : userinfo.cpcideaid == null ? userinfo.cpcidea : JSON.parseArray((String)ops.get(key, userinfo.cpcideaid.toString()), CpcIdeaType.class)) data.process(JSON.toJSONString(cpcidea), entry.getValue(), account.getString("tenant_id") + '|' + (account.get("client_code") == null ? "code" : account.getString("client_code")) + '|' + (account.get("utm_source") == null ? "source" : account.getString("utm_source")) + '|' + UserinfoType.cpcidea);
							break;
							case cpcexidea : if(userinfo.cpcexidea != null || userinfo.cpcexideaid != null) for (CpcExIdeaType cpcexidea : userinfo.cpcexideaid == null ? userinfo.cpcexidea : JSON.parseArray((String)ops.get(key, userinfo.cpcexideaid.toString()), CpcExIdeaType.class)) data.process(JSON.toJSONString(cpcexidea), entry.getValue(), account.getString("tenant_id") + '|' + (account.getString("client_code") == null ? "code" : account.getString("client_code")) + '|' + (account.get("utm_source") == null ? "source" : account.getString("utm_source")) + '|' + UserinfoType.cpcexidea);
						}
					} catch (Exception e) {
						logger.process(Task.getMessage(e), 10226, account.getString("tenant_id"));
					}
					if (cache) try {
						template.delete(key);
					} catch (Exception e) {
						logger.process(Task.getMessage(e), 10227, account.getString("tenant_id"));
					}
					if (!Boolean.FALSE.equals(partial)) try {
						JsonObject result = new HttpClient().rest("GET", updateUrl + "?tenant_id=" + account.getString("tenant_id") + "&update_time=" + (update - (index + 1) * 604800000L), null);
						if ("OK".equals(result.getString("code"))) logger.process("Task finish successful", 9, account.getString("tenant_id"));
						else logger.process(result.getString("result"), 10228, account.getString("tenant_id"));
					} catch (Exception e) {
						logger.process(Task.getMessage(e), 10228, account.getString("tenant_id"));
					} else logger.process("Task finish successful without update", 9, account.getString("tenant_id"));
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
		private List<AccountInfoType> account;
		private UUID accountid;
		private List<CpcPlanType> cpcplan;
		private UUID cpcplanid;
		private List<CpcType> cpc;
		private UUID cpcid;
		private List<CpcGrpType> cpcgrp;
		private UUID cpcgrpid;
		private List<CpcIdeaType> cpcidea;
		private UUID cpcideaid;
		private List<CpcExIdeaType> cpcexidea;
		private UUID cpcexideaid;
		
		public UserInfo() {}

		public UserInfo(boolean isSuccess) {
			this.isSuccess=isSuccess;
		}

		public boolean isFull() {
			if (full == null) {
				Boolean ret = true;
outter:			for (Type type : uuids.keySet()) 
					if (type instanceof UserinfoType) switch((UserinfoType)type) {
						case account : ret = account != null;
						if (!ret) break outter;
						break;
						case cpcplan : ret = cpcplan != null;
						if (!ret) break outter;
						break;
						case cpc : ret = cpc != null;
						if (!ret) break outter;
						break;
						case cpcgrp : ret = cpcgrp != null;
						if (!ret) break outter;
						break;
						case cpcidea : ret = cpcidea != null;
						if (!ret) break outter;
						break;
						case cpcexidea : ret = cpcexidea != null;
						if (!ret) break outter;
						break;
					}
				return ret;
			} else if (full) return account != null && cpcplan != null && cpc != null && 
					cpcgrp != null && cpcidea != null && cpcexidea != null;
			else return true;
			
		}

		public boolean isSuccess() {
			return isSuccess;
		}

		public void setSuccess(boolean isSuccess) {
			this.isSuccess = isSuccess;
		}

		public List<AccountInfoType> getAccount() {
			return account;
		}

		public void setAccount(List<AccountInfoType> account) {
			this.account = account;
		}

		public List<CpcPlanType> getCpcplan() {
			return cpcplan;
		}

		public void setCpcplan(List<CpcPlanType> cpcplan) {
			this.cpcplan = cpcplan;
		}

		public List<CpcType> getCpc() {
			return cpc;
		}

		public void setCpc(List<CpcType> cpc) {
			this.cpc = cpc;
		}

		public List<CpcGrpType> getCpcgrp() {
			return cpcgrp;
		}

		public void setCpcgrp(List<CpcGrpType> cpcgrp) {
			this.cpcgrp = cpcgrp;
		}

		public List<CpcIdeaType> getCpcidea() {
			return cpcidea;
		}

		public void setCpcidea(List<CpcIdeaType> cpcidea) {
			this.cpcidea = cpcidea;
		}

		public List<CpcExIdeaType> getCpcexidea() {
			return cpcexidea;
		}

		public void setCpcexidea(List<CpcExIdeaType> cpcexidea) {
			this.cpcexidea = cpcexidea;
		}
	}
	
	public interface Type {
		public String name();
		public int ordinal();
	}
	
	public enum ReportType implements Type{
		accountReport,accountReport_mobile,planReport,planReport_mobile,groupReport,groupReport_mobile,ideaReport,ideaReport_mobile,keyWordReport,keyWordReport_mobile,searchWordReport,searchWordReport_mobile,keyWordShowNoClickReport,keyWordShowNoClickReport_mobile
	}
	
	public enum UserinfoType implements Type{
		account,cpcplan,cpc,cpcgrp,cpcidea,cpcexidea
	}
}