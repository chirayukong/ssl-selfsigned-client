package com.comettalks.ssl.selfsigned.client;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.AuthCache;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.BasicAuthCache;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.util.EntityUtils;

public class ApacheHttpClient {

    private static final String scheme = "https";
    private static final String hostname = "httpbin.org";
    private static final int port = 443;
    private static final String rest_api = "/hidden-basic-auth/user/passwd";
    private static final String username = "user";
    private static final String password = "passwd";

    public static void main(String[] args) throws Exception {
	
        HttpHost target = new HttpHost(hostname, port, scheme);
        
	// Trust own CA and all self-signed certs
        SSLContextBuilder builder = new SSLContextBuilder();
	builder.loadTrustMaterial(null, new TrustSelfSignedStrategy());
	
	SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(
		builder.build(),
		SSLConnectionSocketFactory.getDefaultHostnameVerifier());
	
	CredentialsProvider credsProvider = new BasicCredentialsProvider();
        credsProvider.setCredentials(
                new AuthScope(hostname, port),
                new UsernamePasswordCredentials(username, password));
        
	CloseableHttpClient httpclient = HttpClients.custom()
		.setSSLSocketFactory(sslsf)
		.setDefaultCredentialsProvider(credsProvider)
		.build();
	try {
	    // Create AuthCache instance
            AuthCache authCache = new BasicAuthCache();
            // Generate BASIC scheme object and add it to the local
            // auth cache
            BasicScheme basicAuth = new BasicScheme();
            authCache.put(target, basicAuth);

            // Add AuthCache to the execution context
            HttpClientContext localContext = HttpClientContext.create();
            localContext.setAuthCache(authCache);
	    
            HttpGet httpget = new HttpGet(scheme + "://" + hostname + rest_api);
	    httpget.addHeader("accept", "application/json");
	    System.out.println("Executing request " + httpget.getRequestLine());
	    CloseableHttpResponse response = httpclient.execute(target, httpget, localContext);
	    try {
		HttpEntity entity = response.getEntity();

		System.out.println("----------------------------------------");
		System.out.println(response.getStatusLine());
		System.out.println("----------------------------------------");
		System.out.println(entity.getContentType().toString());
		System.out.println("----------------------------------------");
		System.out.println(EntityUtils.toString(entity, "UTF-8"));
		
		EntityUtils.consume(entity);
	    } finally {
		response.close();
	    }
	} finally {
	    httpclient.close();
	}

    }

}
