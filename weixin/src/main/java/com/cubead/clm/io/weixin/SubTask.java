package com.cubead.clm.io.weixin;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonNumber;
import javax.json.JsonObject;
import javax.json.JsonValue;

import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.entity.StringEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import weixin.popular.api.API;
import weixin.popular.api.BaseAPI;
import weixin.popular.api.ClearQuotaAPI;
import weixin.popular.api.ComponentAPI;
import weixin.popular.bean.BaseResult;
import weixin.popular.bean.component.ApiGetAuthorizerInfoResult;
import weixin.popular.bean.datacube.article.Articlesummary;
import weixin.popular.bean.datacube.article.Articletotal;
import weixin.popular.bean.datacube.article.Details;
import weixin.popular.bean.datacube.user.Usercumulate;
import weixin.popular.client.LocalHttpClient;
import weixin.popular.util.JsonUtil;

import com.cubead.clm.IProcessor;
import com.cubead.clm.ITask;
import com.cubead.clm.PlatformException;
import com.cubead.clm.PlatformException.Error;
import com.cubead.clm.io.weixin.dao.IDao;
import com.cubead.clm.io.weixin.data.ArticleDaily;
import com.cubead.clm.io.weixin.data.ArticleTotal;
import com.cubead.clm.io.weixin.data.CumulateUser;
import com.cubead.clm.io.weixin.data.SemAccounts;
import com.cubead.clm.io.weixin.data.UserInfo;

public class SubTask extends BaseAPI implements ITask<String, Object, Object[]> {
	private static final Logger log = LoggerFactory.getLogger(Task.class);
	
	protected static String cat(String url) {
		String result = request(url);
		JsonObject o = Json.createReader(new StringReader(result)).readObject();
		for (int i = 0; "ERROR".equals(o.getString("status")) && i < 3; i++) try {
			Thread.sleep(10000);
			result = request(url);
		} catch (Exception e) {}
		if ("ERROR".equals(o.getString("status"))) throw new PlatformException(PlatformException.Error.INTERVAL_SERVER_FAIL);
		else return o.getJsonObject("result_data").getString("component_access_token");
	}
	
	@Override
	public Collection<Object[]> perform(IProcessor<String, Object> params) {
		IProcessor<String, Object> ctx = (IProcessor<String, Object>) params.process();
		IProcessor<Object, Boolean> logger = (IProcessor<Object, Boolean>) ctx.process("log");
		IProcessor<Object, Boolean> data = (IProcessor<Object, Boolean>) ctx.process("data");
		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(new String[]{"config/springContext.xml"}, false);
		try {
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
			context.setClassLoader(getClass().getClassLoader());
			context.getEnvironment().getPropertySources().addFirst(new ParamPropertySource(params));
			context.refresh();
			IDao dao = (IDao) context.getBean("dao");
			Object o = params.process("userinfo");
			UUID userinfo = o == null ? null : UUID.fromString(o.toString());
			o = params.process("daily");
			UUID daily = o == null ? null : UUID.fromString(o.toString());
			o = params.process("total");
			UUID total = o == null ? null : UUID.fromString(o.toString());
			o = params.process("cumulate");
			UUID cumulate = o == null ? null : UUID.fromString(o.toString());
			o = params.process("component");
			String component = o == null ? "wx322ffe108e22a589" : o.toString();
			o = params.process("authorizer");
			String authorizer = o == null ? "wxd4e98e07ee9b6d83" : o.toString();
			o = params.process("tokenurl");
			String url = o == null ? "http://omms.cubead.com/wx/component/getComponentAccessToken" : o.toString();
			JsonArray accounts = (JsonArray) params.process("accounts");
			Calendar c = new GregorianCalendar();
		    c.set(Calendar.HOUR_OF_DAY, 0); 
		    c.set(Calendar.MINUTE, 0);
		    c.set(Calendar.SECOND, 0);
		    c.set(Calendar.MILLISECOND, 0);
			Long time = c.getTimeInMillis();
			for(JsonValue a : accounts) try {
				int count = 0;
				PlatformException ex = null;
				UserInfo ui = new UserInfo();
				SemAccounts account = dao.<SemAccounts, Long>findById(SemAccounts.class, ((JsonNumber)a).longValue());
				long t = account.getUpdate() == null ? formatter.parse("2014-12-01").getTime() : account.getUpdate().getTime() + 86400000L;
				LinkedList<String> ds = new LinkedList<String>();
				for (;t < time; t += 86400000L) ds.add(formatter.format(new Date(t)));
				String[] dates = ds.toArray(new String[ds.size()]);
				Collection<CumulateUser>[] cumulates = new Collection[dates.length];
				Collection<ArticleDaily>[] dailys = new Collection[dates.length];
				Collection<ArticleTotal>[] totals = new Collection[dates.length];
				int tindex = 0, cindex = 0, dindex = 0;
				List<UserInfo> ls = dao.<UserInfo>findByHQL("from UserInfo as u where u.appid = '" + account.getAuthorizerAppid() + "'", new HashMap<String, Object>());
				if (ls.size() < 1) for (; count < 3; count ++) {
					ApiGetAuthorizerInfoResult aaiResult = ComponentAPI.api_get_authorizer_info(cat(url), component, authorizer);
					if (aaiResult == null || !aaiResult.isSuccess()) ex = new PlatformException(new PlatformException.DefaultError(account.getAuthorizerAppid(), 10001, aaiResult == null ? null : aaiResult.getErrmsg()));
					else try {
						ui.setAlias(aaiResult.getAuthorizer_info().getAlias());
						ui.setAppid(aaiResult.getAuthorization_info().getAuthorizer_appid());
						ui.setBusinessInfo(JsonUtil.toJSONString(aaiResult.getAuthorizer_info().getBusiness_info()));
						ui.setFuncInfo(JsonUtil.toJSONString(aaiResult.getAuthorization_info().getFunc_info()));
						ui.setHeadImg(aaiResult.getAuthorizer_info().getHead_img());
						ui.setNickName(aaiResult.getAuthorizer_info().getNick_name());
						ui.setPrincipalName(aaiResult.getAuthorizer_info().getPrincipal_name());
						ui.setQrcodeUrl(aaiResult.getAuthorizer_info().getQrcode_url());
						ui.setServiceTypeInfo(aaiResult.getAuthorizer_info().getService_type_info().getId());
						ui.setUserName(aaiResult.getAuthorizer_info().getUser_name());
						ui.setVerifyTypeInfo(aaiResult.getAuthorizer_info().getVerify_type_info().getId());
						break;
					} catch (Exception exception) {
						logger.process(Task.getMessage(exception), 10003, account.getTenantId());
					}
				} else ui = null; 
				if (count > 2) throw ex;
				else count = 0;
				while (cindex < dates.length || tindex < dates.length || dindex < dates.length) {
cumulate:			for (; cindex < dates.length; cindex++) if (count > 2) break; 
					else for (; count < 3; count ++) {
						UsercumulateResult u = getusercumulate(account.getAuthorizerAccessToken(), dates[cindex], dates[cindex]);
						if (u == null || !u.isSuccess()) {
							if (u != null && "45009".equals(u.getErrcode())) break cumulate;
							ex = new PlatformException(new PlatformException.DefaultError(account.getAuthorizerAppid(), 10001, u == null ? null : u.getErrmsg()));
							try {
								Thread.sleep(10000);
								account = dao.<SemAccounts, Long>findById(SemAccounts.class, ((JsonNumber)a).longValue());
							} catch (Exception exception) {}
						} else {
							if (u.getList() != null) {
								LinkedList<CumulateUser> ll = new LinkedList<CumulateUser>();
								for (Usercumulate uc : u.getList()) try {
									CumulateUser cu = new CumulateUser();
									cu.setAppid(account.getAuthorizerAppid());
									cu.setCumulateUser(uc.getCumulate_user());
									cu.setRefDate(formatter.parse(uc.getRef_date()));
									ll.add(cu);
								} catch (Exception exception) {
									logger.process(Task.getMessage(exception), 10004, account.getTenantId());
								}
								if (ll.size() > 0) cumulates[cindex] = ll;
							}
							break;
						}
					} 
					if (count > 2) throw ex;
					else count = 0;
daily:				for (; dindex < dates.length; dindex++) if (count > 2) break; 
					else for (; count < 3; count ++) {
						ArticlesummaryResult s = getarticlesummary(account.getAuthorizerAccessToken(), dates[dindex], dates[dindex]);
						if (s == null || !s.isSuccess()) {
							if (s != null && "45009".equals(s.getErrcode())) break daily;
							ex = new PlatformException(new PlatformException.DefaultError(account.getAuthorizerAppid(), 10001, s == null ? null : s.getErrmsg()));
							try {
								Thread.sleep(10000);
								account = dao.<SemAccounts, Long>findById(SemAccounts.class, ((JsonNumber)a).longValue());
							} catch (Exception exception) {}
						} else {
							if (s.getList() != null) {
								LinkedList<ArticleDaily> ll = new LinkedList<ArticleDaily>();
								for (Articlesummary as : s.getList()) try {
									ArticleDaily ad = new ArticleDaily();
									ad.setAppid(account.getAuthorizerAppid());
									ad.setAddToFavCount(as.getAdd_to_fav_count());
									ad.setAddToFavUser(as.getAdd_to_fav_user());
									ad.setIntPageReadCount(as.getInt_page_read_count());
									ad.setIntPageReadUser(as.getInt_page_read_user());
									ad.setMsgid(as.getMsgid());
									ad.setOriPageReadCount(as.getOri_page_read_count());
									ad.setOriPageReadUser(as.getOri_page_read_user());
									ad.setRefDate(formatter.parse(as.getRef_date()));
									ad.setShareCount(as.getShare_count());
									ad.setShareUser(as.getShare_user());
									ad.setTitle(as.getTitle());
									ll.add(ad);
								} catch (Exception exception) {
									logger.process(Task.getMessage(exception), 10005, account.getTenantId());
								}
								if (ll.size() > 0) dailys[dindex] = ll;
							}
							break;
						}
					} 
					if (count > 2) throw ex;
					else count = 0;
total:				for (; tindex < dates.length; tindex++) if (count > 2) break; 
					else for (; count < 3; count ++) {
						ArticletotalResult l = getarticletotal(account.getAuthorizerAccessToken(), dates[tindex], dates[tindex]);
						if (l == null || !l.isSuccess()) {
							if (l != null && "45009".equals(l.getErrcode())) break total;
							ex = new PlatformException(new PlatformException.DefaultError(account.getAuthorizerAppid(), 10001, l == null ? null : l.getErrmsg()));
							try {
								Thread.sleep(10000);
								account = dao.<SemAccounts, Long>findById(SemAccounts.class, ((JsonNumber)a).longValue());
							} catch (Exception exception) {}
						} else {
							if (l.getList() != null) {
								LinkedList<ArticleTotal> ll = new LinkedList<ArticleTotal>();
								for (Articletotal al : l.getList()) try {
									ArticleTotal at = new ArticleTotal();
									at.setAppid(account.getAuthorizerAppid());
									at.setDetails(JsonUtil.toJSONString(al.getDetails()));
									for (Details detail : al.getDetails()) if (al.getRef_date().equals(detail.getStat_date())) {
										at.setFirstDayStatDate(detail.getTarget_user());
										break;
									}
									at.setMsgid(al.getMsgid());
									at.setRefDate(formatter.parse(al.getRef_date()));
									at.setTitle(al.getTitle());
									ll.add(at);
								} catch (Exception exception) {
									logger.process(Task.getMessage(exception), 10006, account.getTenantId());
								}
								if (ll.size() > 0) totals[tindex] = ll;
							}
							break;
						}
					} 
					if (count > 2) throw ex;
					else count = 0;
					if (cindex < dates.length || tindex < dates.length || dindex < dates.length) for (; count < 3; count ++) {
						BaseResult r = ClearQuotaAPI.clear_quota(account.getAuthorizerAccessToken(), account.getAuthorizerAppid());
						if (r == null || !r.isSuccess()) {
							ex = new PlatformException(new PlatformException.DefaultError(account.getAuthorizerAppid(), 10001, r == null ? null : r.getErrmsg()));
							try {
								Thread.sleep(10000);
								account = dao.<SemAccounts, Long>findById(SemAccounts.class, ((JsonNumber)a).longValue());
							} catch (Exception exception) {}
						} else {
							count = 0;
							break;
						}
					}
					if (count > 2) throw ex;
				}
				LinkedList<Object> all = new LinkedList<Object>();
				if (dindex < cindex) cindex = dindex;
				if (tindex < cindex) cindex = tindex;
				if (ui != null) {
					all.add(ui);
					if (userinfo != null) data.process(JsonUtil.toJSONString(ui), userinfo, account.getTenantId().toString() + '|' + account.getClientCode() + '|' + account.getUtmSource() + "|userinfo");
				}
				for (int i = 0; i < cindex; i++) if (cumulates[i] != null && cumulates[i].size() > 0) {
					all.addAll(cumulates[i]);
					if (cumulate != null) for (CumulateUser cu : cumulates[i]) data.process(JsonUtil.toJSONString(cu), cumulate, account.getTenantId().toString() + '|' + account.getClientCode() + '|' + account.getUtmSource() + "|cumulate", cu.getRefDate().getTime());
				}
				for (int i = 0; i < cindex; i++) if (dailys[i] != null && dailys[i].size() > 0) {
					all.addAll(dailys[i]);
					if (daily != null) for (ArticleDaily ad : dailys[i]) data.process(JsonUtil.toJSONString(ad), daily, account.getTenantId().toString() + '|' + account.getClientCode() + '|' + account.getUtmSource() + "|daily", ad.getRefDate().getTime());
				}
				for (int i = 0; i < cindex; i++) if (totals[i] != null && totals[i].size() > 0) {
					all.addAll(totals[i]);
					if (total != null) for (ArticleTotal at : totals[i]) data.process(JsonUtil.toJSONString(at), total, account.getTenantId().toString() + '|' + account.getClientCode() + '|' + account.getUtmSource() + "|total", at.getRefDate().getTime());
				}
				account.setUpdate(formatter.parse(dates[cindex - 1]));
				all.add(account);
				dao.batchSaveOrUpdate(all);
			} catch (Exception e) {
				log.error("Weixin SubTask:", e);
				if (e instanceof PlatformException) logger.process(((PlatformException) e).getType().getMessage(), ((PlatformException) e).getType().getCode(), ((PlatformException) e).getType().getCate());
				else logger.process(Task.getMessage(e), 10002);
			}
		} catch (Exception e) {
			log.error("Weixin SubTask:", e);
			logger.process(Task.getMessage(e), 10001);
		} finally {
			context.close();
		}
		return Collections.EMPTY_LIST;
	}
	
	private static String request(String url) {
		int nRead;
		byte[] data = new byte[256];
		InputStream input = null;
		HttpURLConnection conn = null; 
		try{
			conn = (HttpURLConnection)new URL(url).openConnection();
			conn.setDoOutput(true);
			conn.setDoInput(true);
			conn.setRequestMethod("GET");
			Integer code = conn.getResponseCode();
			try{
				input = conn.getInputStream();
				ByteArrayOutputStream buf = new ByteArrayOutputStream();
				while ((nRead = input.read(data, 0, data.length)) != -1) buf.write(data, 0, nRead);
				buf.flush();
				byte[] resp = buf.toByteArray();
				if (code.equals(401)) throw new PlatformException(PlatformException.Error.INTERVAL_SERVER_FAIL);
				else if (resp.length > 0) return new String(resp);
				else if (code < 300) return null;
				throw new PlatformException(Error.INTERVAL_SERVER_FAIL);
			}finally{
				if (input != null) input.close();
			}
		} catch (PlatformException e) {
			throw e;
		} catch (Exception e) {
			if (conn != null) {
				try {
					input = conn.getInputStream();
					if (input != null) try {
						while ((input.read(data, 0, data.length)) != -1);
					} finally{
						input.close();
					}
				} catch (Exception e1) {}
				try {
					input = conn.getErrorStream();
					if (input != null) try {
						while ((input.read(data, 0, data.length)) != -1);
					} finally{
						input.close();
					}
				} catch (Exception e1) {}
			}
			throw new PlatformException(Error.CONNECTION_FAIL, e);
		}
	}
	
	public static ArticlesummaryResult getarticlesummary(String access_token, String begin_date,String end_date) {
		String requestJson = String.format("{\"begin_date\":\"%s\",\"end_date\":\"%s\"}", begin_date,end_date);
		HttpUriRequest httpUriRequest = RequestBuilder.post()
				.setHeader(jsonHeader)
				.setUri(BASE_URI+"/datacube/getarticlesummary")
				.addParameter(PARAM_ACCESS_TOKEN, API.accessToken(access_token))
				.setEntity(new StringEntity(requestJson,Charset.forName("utf-8")))
				.build();
		return LocalHttpClient.executeJsonResult(httpUriRequest,ArticlesummaryResult.class);
	}
	
	public static UsercumulateResult getusercumulate(String access_token, String begin_date,String end_date) {
		String requestJson = String.format("{\"begin_date\":\"%s\",\"end_date\":\"%s\"}", begin_date,end_date);
		HttpUriRequest httpUriRequest = RequestBuilder.post()
				.setHeader(jsonHeader)
				.setUri(BASE_URI+"/datacube/getusercumulate")
				.addParameter(PARAM_ACCESS_TOKEN, API.accessToken(access_token))
				.setEntity(new StringEntity(requestJson,Charset.forName("utf-8")))
				.build();
		return LocalHttpClient.executeJsonResult(httpUriRequest,UsercumulateResult.class);
	}
	
	public static ArticletotalResult getarticletotal(String access_token, String begin_date,String end_date) {
		String requestJson = String.format("{\"begin_date\":\"%s\",\"end_date\":\"%s\"}", begin_date,end_date);
		HttpUriRequest httpUriRequest = RequestBuilder.post()
				.setHeader(jsonHeader)
				.setUri(BASE_URI+"/datacube/getarticletotal")
				.addParameter(PARAM_ACCESS_TOKEN, API.accessToken(access_token))
				.setEntity(new StringEntity(requestJson,Charset.forName("utf-8")))
				.build();
		return LocalHttpClient.executeJsonResult(httpUriRequest,ArticletotalResult.class);
	}
}

class UsercumulateResult extends BaseResult{
	private List<Usercumulate> list;

	public List<Usercumulate> getList() {
		return list;
	}

	public void setList(List<Usercumulate> list) {
		this.list = list;
	}

}

class ArticlesummaryResult extends BaseResult {
	private List<Articlesummary> list;

	public List<Articlesummary> getList() {
		return list;
	}

	public void setList(List<Articlesummary> list) {
		this.list = list;
	}
}

class ArticletotalResult extends BaseResult {
	private List<Articletotal> list;

	public List<Articletotal> getList() {
		return list;
	}

	public void setList(List<Articletotal> list) {
		this.list = list;
	}
}