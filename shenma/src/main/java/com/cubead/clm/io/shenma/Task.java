package com.cubead.clm.io.shenma;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map.Entry;
import java.util.zip.GZIPInputStream;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonValue;

import com.cubead.clm.IProcessor;
import com.cubead.clm.ITask;

public class Task implements ITask<String, Object, String> {
	public final static String getMessage(Throwable e) {
		StringWriter errors = new StringWriter();
		e.printStackTrace(new PrintWriter(errors));
		return errors.toString();
		
	}
	
	@Override
	public Collection<String> perform(IProcessor<String, Object> params) {
		IProcessor<String, Object> ctx = (IProcessor<String, Object>) params.process();
		IProcessor<Object, Boolean> logger = (IProcessor<Object, Boolean>) ctx.process("log");
		IProcessor<JsonObject, Boolean> jobber = (IProcessor<JsonObject, Boolean>) ctx.process("job");
		try {
			Object o = params.process("getUrl");
			String url = o instanceof String  ? (String)o : "http://danei.new.clm.cubead.com/get_account_info_by_channel";
			o = params.process("condition");
			url = o == null ? url + "?channel=shenma" : url + "?channel=shenma&condition=" + o.toString();
			JsonObject result = new HttpClient().rest("GET", url, null);
			if ("OK".equals(result.getString("code"))) {
				o = params.process("batch");
				int batch = o instanceof BigDecimal && ((BigDecimal) o).intValue() > 0 ? ((BigDecimal) o).intValue() : 0;
				LinkedList<JsonObject> jobs = new LinkedList<JsonObject>();
				Iterator<JsonValue> itr = result.getJsonArray("result").iterator();
				while (itr.hasNext()) {
					JsonObjectBuilder jb = Json.createObjectBuilder();
					if (params.process(new String[]{null}) instanceof JsonObject) for (Entry<String, JsonValue> entry : ((JsonObject)params.process(new String[]{null})).entrySet()) jb.add(entry.getKey(), entry.getValue());
					if (params.process(null) instanceof JsonObject) for (Entry<String, JsonValue> entry : ((JsonObject)params.process(null)).entrySet()) jb.add(entry.getKey(), entry.getValue());
					JsonArrayBuilder ja = Json.createArrayBuilder();
					for (int i = 0; i < ( batch > 0 ? batch : Integer.MAX_VALUE ); i++) {
						ja.add(itr.next());
						if (!itr.hasNext()) break;
					}
					jb.add("accounts", ja.build());
					JsonObjectBuilder job = Json.createObjectBuilder();
					job.add("class", "com.cubead.clm.io.shenma.SubTask");
					job.add("param", jb.build().toString());
					jobs.add(job.build());
				}
				if (jobs.size() > 0) jobber.process(jobs.toArray(new JsonObject[jobs.size()]));
			
			} else logger.process(result.get("result"), 10100);
		} catch (Exception e) {
			logger.process(getMessage(e), 10100);
		} 
		return Collections.EMPTY_LIST;
	}
}