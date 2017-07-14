/*package downTest;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonWriter;

import org.junit.Test;

import com.cubead.clm.io.shenma.CsvReader;
import com.cubead.clm.io.shenma.HttpClient;

import com.cubead.clm.io.shenma.data.SemAccounts;
import com.cubead.clm.io.shenma.data.report.AccountReport;



public class test {
	@Test
	public void test(){
		SemAccounts semAccounts;
		long taskId;
		final String getTaskStateurl="https://e.sm.cn/api/task/getTaskState";
		final String downloadurl="https://e.sm.cn/api/file/download";
		String msg = "{\"header\":{\"username\":\"weichang_ydzx\",\"password\": \"by2015ydzx\",\"token\": \"340785bd-8aab-4101-a755-b4d47a704b1e\"},\"body\":{\"taskId\":1152921504618321011}}";
		HttpClient req = new HttpClient();
		JsonObject resp = req.rest("POST",getTaskStateurl,msg);
		resp.getJsonObject("body").get("taskId");
		JsonObject body = resp.getJsonObject("body");
		if(body.get("status").equals("FINISHED")){
			System.out.println("finish");
			String fileId = body.getString("fileId");
			String download = "{\"header\":{\"username\":\"weichang_ydzx\",\"password\": \"by2015ydzx\",\"token\": \"340785bd-8aab-4101-a755-b4d47a704b1e\"},\"body\":{\"fileId\":1152921504618321011}}";
			HttpClient downloadreq = new HttpClient();
			JsonObject downloadresp = req.rest("post", downloadurl,msg);
			try {
				// write to file
				File file = new File("C:\\json.txt");
				if (!file.exists()) {
					file.createNewFile();
				}
				OutputStream os = null;
				os = new FileOutputStream(file);
				JsonWriter jsonWriter = Json.createWriter(os);
				jsonWriter.writeObject(downloadresp);
				jsonWriter.close();
				System.out.println(file.getPath());
			} catch (IOException e) {
				
				e.printStackTrace();
			}
		}
		
		System.out.println(msg+"\n"+resp+"\n"+resp.get("body"));
	}
	 public String replaceShuangyinhaoToDanyinhao(String str){  
	        return str.replaceAll("\"", "\'");  
	    }  
	@Test
	public void down(){
		//System.out.println(﻿"时间".equals(﻿﻿﻿﻿﻿﻿﻿﻿"﻿时间"));﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿
		final String downloadurl="https://e.sm.cn/api/file/download";
		String download = "{\"header\":{\"username\":\"weichang_ydzx\",\"password\": \"by2015ydzx\",\"token\": \"340785bd-8aab-4101-a755-b4d47a704b1e\"},\"body\":{\"fileId\":1152921504618321011}}";
		HttpClient downloadreq = new HttpClient();
		byte[] response = downloadreq.request("POST", downloadurl,download);
		try {
			CsvReader reader = new CsvReader(new ByteArrayInputStream(response), Charset.forName("utf-8"));
			
			reader.readHeaders();
			
			
			while(reader.readRecord()) {
				 // 读一整行
                //System.out.println(reader.getRawRecord());
				
                // 读这行的某一列
                System.out.println(reader.get("﻿\"时间\""));
			}
			
			// write to file
			
		} catch (IOException e) {
			
			e.printStackTrace();
		}
		for(int i = 0; i < "campaignId".length(); i++) System.out.println("campaignId".charAt(i));
		System.out.println("--------------------------------------------");
		for(int i = 0; i < "campaignId".length(); i++) System.out.println("campaignId".charAt(i));
		System.out.println("﻿campaignId".equals("campaignId"));
	}
	@Test
	public void account(){
		final String downloadurl="https://e.sm.cn/api/file/download";
		String download = "{\"header\":{\"username\":\"weichang_ydzx\",\"password\": \"by2015ydzx\",\"token\": \"340785bd-8aab-4101-a755-b4d47a704b1e\"},\"body\":{\"fileId\":1085228359}}";
		HttpClient downloadreq = new HttpClient();
		byte[] response = downloadreq.request("POST", downloadurl,download);
		try {
			  ZipInputStream stream = null;
			  byte[] buf = new byte[8196];
		        try {
		        	stream = new ZipInputStream(new ByteArrayInputStream(response));
		        	ZipEntry entry = stream.getNextEntry();
		            while (entry != null) {
		            	if (entry.getName().indexOf(".csv") > -1) {
			              System.out.println(entry.getName());
	                      ByteArrayOutputStream out = new ByteArrayOutputStream();
	  		        	  int count;
	                      while ((count = stream.read(buf)) > -1) out.write(buf, 0, count);
	                      byte[] csv = out.toByteArray();
	                      String filename = entry.getName();
		          			CsvReader reader = new CsvReader(new ByteArrayInputStream(csv), Charset.forName("utf-8"));
		        			reader.readHeaders();
		        			List<String> result = new LinkedList<String>();
		        			while(reader.readRecord()) {
		        				AccountReport ar = new AccountReport();
		        				ar.setAccount(reader.get("adgroupName"));
		        				//System.out.println(JsonUtil.toJSONString(ar));
		        				//result.add(JsonUtil.toJSONString(ar));
		        			}
	        			
			            }
		            	entry = stream.getNextEntry();
		            }
		        } catch (IOException e) {
		        	//log.error("test : load zip error.", e);
		        }finally{
		    		if(stream!=null) {try{stream.close();} catch(Exception e){
		    			//log.error("test : load zip error.", e);
		    			}
		    		}
		    	}

			
			// write to file
			
		} catch (Exception e) {
			
			e.printStackTrace();
		}
	}
	@Test
	public void testRequest() throws InterruptedException{
//		ScheduledExecutorService executor = new ScheduledThreadPoolExecutor(Runtime.getRuntime().availableProcessors() * 2);
//		SemAccounts semAccounts = new SemAccounts();
//		semAccounts.setAccount("weichang_ydzx");
//		semAccounts.setAuthorizerAccessToken("340785bd-8aab-4101-a755-b4d47a704b1e");
//		semAccounts.setPassword("by2015ydzx");
//		Requestjob requestjob = new Requestjob() ;
//		requestjob.setStartTime("2017-06-06");
//		requestjob.setEndTime("2017-6-10");
//		requestjob.setExecutor(executor);
//		requestjob.setReportType(Type.accountReport);
//		requestjob.setSemAccounts(semAccounts);
//		executor.schedule(requestjob, 5, TimeUnit.SECONDS);
//		Thread.currentThread().sleep(6000);
		//requestjob.run();
	}
}
*/