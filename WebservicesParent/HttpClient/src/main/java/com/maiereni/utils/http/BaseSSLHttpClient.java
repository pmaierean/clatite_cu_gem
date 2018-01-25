/**
 * ================================================================
 *  Copyright (c) 2017-2018 Maiereni Software and Consulting Inc
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
package com.maiereni.utils.http;

import java.io.File;

import javax.net.ssl.SSLContext;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.ssl.SSLContexts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A base SSL client
 * 
 * @author Petre Maierean
 *
 */
public class BaseSSLHttpClient extends BaseHttpClient {
	private static final Logger logger = LoggerFactory.getLogger(BaseSSLHttpClient.class);
	private SSLConnectionSocketFactory sslConnectionSocketFactory;
    
	public BaseSSLHttpClient() throws Exception {
		sslConnectionSocketFactory = getSSLSocketFactory();
	}

	@Override
    protected HttpClientBuilder getHttpClientBuilder() {
		HttpClientBuilder builder = HttpClientBuilder.create();
		builder.setSSLSocketFactory(sslConnectionSocketFactory);
		return builder;
    }
	
    private SSLConnectionSocketFactory getSSLSocketFactory() throws Exception {
        String trustStorePassword = System.getProperty("javax.net.ssl.trustStorePassword");
        String trustStorePath = System.getProperty("javax.net.ssl.truesStore");
        String keyStorePassword = System.getProperty("javax.net.ssl.keyStorePassword");
        String keyPassword = System.getProperty("javax.net.ssl.keyPassword");
        String keyStorePath = System.getProperty("javax.net.ssl.keyStore");
        SSLContext sslContext = null;
        if (StringUtils.isNotEmpty(trustStorePath) && StringUtils.isNotBlank(keyStorePath)) {
        	if (StringUtils.isEmpty(keyStorePassword))
        		throw new Exception("Please specify the key store password with -Djavax.net.ssl.keyStorePassword");
        	if (StringUtils.isEmpty(trustStorePassword))
        		throw new Exception("Please specify the key store password with -Djavax.net.ssl.trustStorePassword");
            sslContext = SSLContexts.custom()
                    .loadKeyMaterial(new File(keyStorePath), keyStorePassword.toCharArray(),keyPassword!=null?keyPassword.toCharArray():"".toCharArray())
                    .loadTrustMaterial(new File(trustStorePath), trustStorePassword.toCharArray())
                    .build();        	
        }
        else
        	sslContext = SSLContexts.createDefault();
        logger.debug("The ssl connection socket factory is being created");
        return new SSLConnectionSocketFactory(sslContext);
    }
}
