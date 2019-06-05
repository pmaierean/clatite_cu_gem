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
package com.maiereni.osgi.felix.utils.sling;

import java.util.List;

import javax.servlet.http.Cookie;

import org.apache.http.client.RedirectStrategy;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultRedirectStrategy;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.cookie.BasicClientCookie;

/**
 * @author Petre Maierean
 *
 */
public abstract class BaseValidatorClient  {

	protected CloseableHttpClient getHttpClient(final List<Cookie> cookies) {
        HttpClientBuilder builder = getHttpClientBuilder();
        if (!(cookies == null || cookies.isEmpty())) {
            BasicCookieStore cookieStore = new BasicCookieStore();
            for(Cookie cookie: cookies) {
                BasicClientCookie bc = new BasicClientCookie(cookie.getName(), cookie.getValue());
                bc.setDomain(cookie.getDomain());
                bc.setPath(cookie.getPath());
                cookieStore.addCookie(bc);
            }
            builder.setDefaultCookieStore(cookieStore);
        }
        builder.setRedirectStrategy(getRedirectStrategy());
        return builder.build();
    }
	
    protected HttpClientBuilder getHttpClientBuilder() {
    	return HttpClientBuilder.create();
    }
    
    protected RedirectStrategy getRedirectStrategy() {
    	return new RedirectorStrategy();
    }
    
    private static final String[] REDIRECT_METHODS_N = new String[] {
            HttpGet.METHOD_NAME,
            HttpPost.METHOD_NAME,
            HttpHead.METHOD_NAME
    };
           
    private class RedirectorStrategy extends DefaultRedirectStrategy {
        @Override
        protected boolean isRedirectable(final String method) {
            for (final String m: REDIRECT_METHODS_N) {
                if (m.equalsIgnoreCase(method)) {
                    return true;
                }
            }
            return false;
        }
    }
}
