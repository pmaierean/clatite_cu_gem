/**
 * ================================================================
 *  Copyright (c) 2017-2019 Maiereni Software and Consulting Inc
 * ================================================================
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.maiereni.synchronizer.jcr.utils;

import java.io.File;
import java.io.InputStream;
import java.net.URL;

import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.AuthCache;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.BasicAuthCache;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.jackrabbit.oak.commons.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.maiereni.synchronizer.git.service.bo.LayoutRules;
import com.maiereni.synchronizer.git.service.bo.SlingProperties;

/**
 * A bundle installer 
 * 
 * @author Petre Maierean
 *
 */
class BundleInstaller {
	private static final Logger logger = LoggerFactory.getLogger(BundleInstaller.class);
	private SlingProperties slingProperties;
	
	BundleInstaller(final SlingProperties slingProperties) throws Exception {
		this.slingProperties = slingProperties;
		if (StringUtils.isBlank(slingProperties.getUrl())) {
			throw new Exception("Invalid case. Sling URL cannot be blank");
		}
	}
	
	private HttpClientContext getContext(final HttpHost host) throws Exception {
		CredentialsProvider credsProvider = new BasicCredentialsProvider();
		credsProvider.setCredentials(
	        new AuthScope(host.getHostName(), host.getPort()),
	        new UsernamePasswordCredentials(slingProperties.getUserName(), slingProperties.getPassword()));

		AuthCache authCache = new BasicAuthCache();
		BasicScheme basicAuth = new BasicScheme();
		authCache.put(host, basicAuth);
		HttpClientContext context = HttpClientContext.create();
		context.setCredentialsProvider(credsProvider);
		context.setAuthCache(authCache);
		return context;
	}
	
	/**
	 * Install a bundle
	 * @param layoutRules
	 * @param fBundle
	 * @throws Exception
	 */
	public void installBundle(final LayoutRules layoutRules, final File fBundle) throws Exception {
		if (fBundle != null && fBundle.isFile()) {
			logger.debug("Start installing the bundle from {}", fBundle.getPath());
	        // append pseudo path after root URL to not get redirected for nothing
			String link = slingProperties.getUrl() + "/install";
	        try (CloseableHttpClient httpclient = HttpClientBuilder.create().build()) {
	        	URL u = new URL(link);
	        	String h = u.getProtocol() + "://" + u.getHost() + ":" + u.getPort();
	        	logger.debug("Connect to " + h);
	        	HttpHost host = HttpHost.create(h);
	        	HttpClientContext context = getContext(host);
	        	String path = u.getPath();
	        	HttpPost post = new HttpPost(path);
	        	post.setHeader("referer", "about:blank");

	        	MultipartEntityBuilder builder = MultipartEntityBuilder.create(); 
	        	builder = builder.addBinaryBody(fBundle.getName(), fBundle)
	        		.addTextBody("action", "install")
	        		.addTextBody("_noredir_", "_noredir_")
	        		.addTextBody("bundlestartlevel",  "" + layoutRules.getBundleStartLevel())
	        		.addTextBody("bundlestart", "start")
	        		.addTextBody("refreshPackages", "true");
	        	HttpEntity entity = builder.build();
	            post.setEntity(entity);

	            String error = null;
	            for (int i = 0; i < 3; i++) {
	            	try (CloseableHttpResponse response = httpclient.execute(host, post, context)) {
		            	int statusCode = response.getStatusLine().getStatusCode();
		            	if (statusCode == HttpStatus.SC_OK) {
						    try {
			            		String text = new BasicResponseHandler().handleResponse(response);
						    	logger.debug("Uploading result is\r\n{}", text);
						    } 
						    catch(Exception e) {
						    	logger.error("Failed to read the response", e);
						    }
						    error = null;
						    break;
		            	}
		            	else {
			            	error = response.getStatusLine().getReasonPhrase();
		            		logger.error("Retry to upload {} " + statusCode, error);
		            	}
	            	}
				}
	            if (StringUtils.isNotBlank(error)) {
	            	throw new Exception(error);
	            }
			}
		}
	}
}
