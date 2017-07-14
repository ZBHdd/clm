package com.cubead.clm.io.weixin;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map.Entry;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonValue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.cubead.clm.IProcessor;
import com.cubead.clm.ITask;
import com.cubead.clm.io.weixin.dao.IDao;
import com.cubead.clm.io.weixin.data.SemAccounts;

public class Task implements ITask<String, Object, String> {	
	private static final Logger log = LoggerFactory.getLogger(Task.class);
	public final static String getMessage(Throwable e) {
		StringWriter errors = new StringWriter();
		e.printStackTrace(new PrintWriter(errors));
		return errors.toString();
	}
	
	@Override
	public Collection<String> perform(IProcessor<String, Object> params) {
		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(new String[]{"config/springContext.xml"}, false);
		IProcessor<String, Object> ctx = (IProcessor<String, Object>) params.process();
		IProcessor<Object, Boolean> logger = (IProcessor<Object, Boolean>) ctx.process("log");
		try {
			context.setClassLoader(getClass().getClassLoader());
			context.getEnvironment().getPropertySources().addFirst(new ParamPropertySource(params));
			context.refresh();
			IProcessor<JsonObject, Boolean> jobber = (IProcessor<JsonObject, Boolean>) ctx.process("job");
			IDao dao = (IDao) context.getBean("dao");
			Object o = params.process("sql");
			StringBuilder sb = new StringBuilder("from SemAccounts as a where a.utmSource = 'weixin' and a.isActive = true and a.authorizerAppid is not null and a.authorizerAccessToken is not null");
			if (o != null) sb.append(" and ").append(o);
			Iterator<SemAccounts> itr = dao.<SemAccounts>findByHQL(sb.toString(), new HashMap<String, Object>()).iterator();
			o = params.process("batch");
			int batch = o instanceof BigDecimal && ((BigDecimal) o).intValue() > 0 ? ((BigDecimal) o).intValue() : 0;
			LinkedList<JsonObject> jobs = new LinkedList<JsonObject>();
			while (itr.hasNext()) {
				JsonObjectBuilder jb = Json.createObjectBuilder();
				if (params.process(new String[]{null}) instanceof JsonObject) for (Entry<String, JsonValue> entry : ((JsonObject)params.process(new String[]{null})).entrySet()) jb.add(entry.getKey(), entry.getValue());
				if (params.process(null) instanceof JsonObject) for (Entry<String, JsonValue> entry : ((JsonObject)params.process(null)).entrySet()) jb.add(entry.getKey(), entry.getValue());
				JsonArrayBuilder ja = Json.createArrayBuilder();
				for (int i = 0; i < ( batch > 0 ? batch : Integer.MAX_VALUE ); i++) {
					ja.add(itr.next().getTenantId());
					if (!itr.hasNext()) break;
				}
				jb.add("accounts", ja.build());
				JsonObjectBuilder job = Json.createObjectBuilder();
				job.add("class", "com.cubead.clm.io.weixin.SubTask");
				job.add("param", jb.build().toString());
				jobs.add(job.build());
			}
			if (jobs.size() > 0) jobber.process(jobs.toArray(new JsonObject[jobs.size()]));
		} catch (Exception e) {
			log.error("Weixin Task:", e);
			logger.process(getMessage(e), 10000);
		} finally {
			context.close();
		}
		return Collections.EMPTY_LIST;
	}

}
