package com.comettalks.ssl.selfsigned.client;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Base64;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class HttpsURLConnectionClient {

    private static final String scheme = "https";
    private static final String hostname = "httpbin.org";
    private static final int port = 443;
    private static final String rest_api = "/hidden-basic-auth/user/passwd";
    private static final String username = "user";
    private static final String password = "passwd";

    public static void main(String[] args) throws Exception {
	// configure the SSLContext with a TrustManager
	SSLContext context = SSLContext.getInstance("SSL");// .getInstance("TLS");
	context.init(new KeyManager[0],
		new TrustManager[] { new DefaultTrustManager() },
		new SecureRandom());
	SSLContext.setDefault(context);

	URL url = new URL(scheme, hostname, port, rest_api);
	HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
	conn.setRequestMethod("GET");
	String encoded = Base64.getEncoder().encodeToString(
		(username + ":" + password).getBytes("UTF-8"));
	conn.addRequestProperty("Authorization", "Basic " + encoded);
	conn.addRequestProperty("accept", "application/json");
	conn.setHostnameVerifier(new HostnameVerifier() {

	    public boolean verify(String arg0, SSLSession arg1) {
		return true;
	    }
	});
	System.out.println("Executing request " + conn.getRequestMethod() + " " + url.toString());
	System.out.println("----------------------------------------");
	System.out.println(conn.getResponseCode());
	System.out.println("----------------------------------------");
	System.out.println(conn.getResponseMessage());
	System.out.println("----------------------------------------");

	BufferedReader in = new BufferedReader(new InputStreamReader(
		conn.getInputStream()));
	String inputLine;
	StringBuffer response = new StringBuffer();

	while ((inputLine = in.readLine()) != null) {
	    response.append(inputLine + "\n");
	}
	in.close();

	// print result
	System.out.println(response.toString());

	conn.disconnect();
    }

    private static class DefaultTrustManager implements X509TrustManager {

	public void checkClientTrusted(X509Certificate[] arg0, String arg1)
		throws CertificateException {
	}

	public void checkServerTrusted(X509Certificate[] arg0, String arg1)
		throws CertificateException {
	}

	public X509Certificate[] getAcceptedIssuers() {
	    return null;
	}
    }

}
