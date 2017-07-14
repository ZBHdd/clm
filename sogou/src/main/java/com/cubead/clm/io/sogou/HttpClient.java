package com.cubead.clm.io.sogou;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.KeyStore;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ScheduledExecutorService;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cubead.clm.PlatformException;
import com.cubead.clm.PlatformException.Error;

public class HttpClient {
	protected static final Logger log = LoggerFactory.getLogger(HttpClient.class);
	protected String url;
	protected Boolean compress = false;
	protected Integer readMode = 0;
	protected Map<String, String> properties;
	protected Integer buffer = 8196;
	protected HostnameVerifier verifier;
	protected String protocol = "TLS";
	protected String keystore = "JKS";
	protected String algorithm = "SunX509";
	protected String trustAlgorithm = "SunX509";
	protected File keyfile;
	protected String keyword;
	protected String cookie;
	protected SSLSocketFactory sslContextFactory;
	protected String auth;
	protected Long wait = 300L;
	protected ScheduledExecutorService executor;
	
	public void setBuffer(Integer buffer) {
		if(buffer != null && buffer > 0) this.buffer = buffer;
	}

	public void setSslContext(SSLContext sslContext) {
		if(sslContext != null) this.sslContextFactory = sslContext.getSocketFactory();
	}

	public void setWait(Long wait) {
		if(wait != null && wait > 0) this.wait = wait;
	}

	public void setExecutor(ScheduledExecutorService executor) {
		this.executor = executor;
	}

	public void setKeystore(String keystore) {
		if(keystore != null) this.keystore = keystore;
	}

	public void setAlgorithm(String algorithm) {
		if(algorithm != null) this.algorithm = algorithm;
	}

	public void setTrustAlgorithm(String trustAlgorithm) {
		if(trustAlgorithm != null) this.trustAlgorithm = trustAlgorithm;
	}

	public void setKeyfile(File keyfile) {
		this.keyfile = keyfile;
	}

	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}

	public void setVerifier(HostnameVerifier verifier) {
		if(verifier != null) this.verifier = verifier;
	}
	
	public void setReadMode(Integer readMode) {
		if(readMode != null) this.readMode = readMode;
	}
	
	public void setCompress(Boolean compress) {
		if(compress != null) this.compress = compress;
	}

	public void setProperties(Map<String, String> properties) {
		this.properties = properties;
	}

	public HttpClient() {
		CookieHandler.setDefault(new CookieManager(null, CookiePolicy.ACCEPT_ALL));
	}
	
	protected void initialize(){
		if (sslContextFactory == null && keyfile != null) try{
			SSLContext sslContext = SSLContext.getInstance (protocol);
		    KeyStore ks = KeyStore.getInstance (keystore);
		    char[] keyword = null;
		    if(this.keyword != null) keyword = this.keyword.toCharArray();
		    InputStream is = null;
            try {
            	is = new FileInputStream(keyfile);
                ks.load(is, keyword);
            } finally {
                if (is != null) is.close();
            }
		    KeyManagerFactory kmf = KeyManagerFactory.getInstance(algorithm);
		    kmf.init(ks, keyword);
		    TrustManagerFactory tmf = TrustManagerFactory.getInstance(trustAlgorithm);
		    tmf.init(ks);
		    sslContext.init(kmf.getKeyManagers (), tmf.getTrustManagers (), null);
		    sslContextFactory = sslContext.getSocketFactory();
		}catch(Exception e) {
			log.error("HttpProtocol: init ssl context fail.", e);
		}
	}
	
	protected InputStream decompressStream(InputStream input) throws IOException{
		if (!input.markSupported()) input = new BufferedInputStream(input);
		input.mark(2);
	    int magic = input.read() & 0xff | ((input.read() << 8) & 0xff00);
	    input.reset();
	    if(magic == GZIPInputStream.GZIP_MAGIC) return new GZIPInputStream(input);
	    else return input;
	}
	
	public JsonObject rest(String method, String url, Object msg) {
		byte[] resp = request(method, url, msg);
		try {
			JsonReader reader = Json.createReader(new StringReader(new String(resp, "UTF-8")));
			return (JsonObject) reader.read();
		} catch (PlatformException e) {
			throw e;
		} catch (Exception e) {
			throw new PlatformException(Error.OTHER_FAIL);
		}
	}
	
	public byte[] request(String method, String url, Object msg) {
		if (sslContextFactory == null && keyfile != null) initialize();
		int nRead;
		byte[] data = new byte[buffer];
		InputStream input = null;
		HttpURLConnection conn = null; 
		try{
			conn = (HttpURLConnection)new URL(url).openConnection();
			if(conn instanceof HttpsURLConnection){
				if(verifier != null) ((HttpsURLConnection)conn).setHostnameVerifier(verifier);
				if(sslContextFactory != null) ((HttpsURLConnection)conn).setSSLSocketFactory(sslContextFactory);
			}
			conn.setDoOutput(true);
			conn.setDoInput(true);
			conn.setRequestMethod(method);
			if(readMode == 0 || readMode == 1) conn.setRequestProperty("Accept-Encoding", "gzip,deflate");
			conn.setRequestProperty("Content-Type", "application/json; charset=utf-8");
			conn.setRequestProperty("Accept", "application/json");
			if(auth != null) conn.setRequestProperty("Authorization", auth);
			if(properties != null) for(Entry<String, String> entry : properties.entrySet()) conn.setRequestProperty(entry.getKey(), entry.getValue());
			if (msg != null) {
				if(compress) conn.setRequestProperty("Content-Encoding", "gzip");
				OutputStream out;
				if(compress) out = new GZIPOutputStream(conn.getOutputStream());
				else out = conn.getOutputStream();
				try{
					out.write(msg.toString().getBytes("UTF-8"));
					out.flush();
				}finally{
					out.close();
				}
			}
			Integer code = conn.getResponseCode();
	    	switch(readMode){
		    	case 0: input = decompressStream(code < 300 ? conn.getInputStream() : conn.getErrorStream());
		    	break;
		    	case 1: input = new GZIPInputStream(code < 300 ? conn.getInputStream() : conn.getErrorStream());
		    	break;
		    	default: input = conn.getInputStream();
		    }
			try{
				ByteArrayOutputStream buf = new ByteArrayOutputStream();
				while ((nRead = input.read(data, 0, data.length)) != -1) buf.write(data, 0, nRead);
				buf.flush();
				byte[] resp = buf.toByteArray();
				if (code.equals(401)) throw new PlatformException(PlatformException.Error.AUTH_FAIL);
				else if (resp.length > 0) return resp;
				else if (code < 300) return null;
				throw new PlatformException(Error.INTERVAL_SERVER_FAIL);
			}finally{
				input.close();
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
}