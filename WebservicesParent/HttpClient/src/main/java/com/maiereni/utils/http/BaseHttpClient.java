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

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.annotation.Nonnull;
import javax.servlet.http.Cookie;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.CookieStore;
import org.apache.http.client.RedirectStrategy;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultRedirectStrategy;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpCoreContext;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.maiereni.utils.http.bo.ResponseBean;

/**
 * A base HTTP client 
 * 
 * @author Petre Maierean
 *
 */
public class BaseHttpClient {
    private static final Logger logger = LoggerFactory.getLogger(BaseHttpClient.class);
    public static final Map<String, String> NO_HEADER = new Hashtable<String, String>();
    public static final Map<String, String> EMPTY_PARAMS = new Hashtable<String, String>();
    public static final List<Cookie> NO_COOKIES = new Vector<Cookie>();

    public static final String GET = "get";
    public static final String POST = "post";
    private EntityProcessor defaultEntityProcessor = new DefaultEntityProcessor();
    private EntityProcessor fileDownloadingProcessor = new FileDownloadingProcessor();
    /**
     * Make a GET request call without headers nor cookies
     * @param url
     * @param params the parameters of the call
     * @return the marshalled result of the call
     * @throws Exception
     */
    public ResponseBean get(
        @Nonnull final String url,
        @Nonnull final Map<String, String> params)
        throws Exception {
        return get(url, params, NO_HEADER, NO_COOKIES);
    }
   
    /**
     * Make a GET request
     * @param url
     * @param params the query parameters
     * @param headers
     * @param cookies
     * @param valueType the class type to marshall the result to
     * @return the marshalled result of the call
     * @throws Exception
     */
    public ResponseBean get(
        @Nonnull final String url,
        @Nonnull final Map<String, String> params,
        @Nonnull final Map<String, String> headers)
        throws Exception {
        return _get(url, params, headers, NO_COOKIES);
    }
   
    /**
     * Make a GET request
     * @param url
     * @param params the query parameters
     * @param headers
     * @param cookies
     * @param valueType the class type to marshall the result to
     * @return the marshalled result of the call
     * @throws Exception
     */
    public ResponseBean get(
        @Nonnull final String url,
        @Nonnull final Map<String, String> params,
        @Nonnull final Map<String, String> headers,
        @Nonnull final List<Cookie> cookies)
        throws Exception {
        return _get(url, params, headers, cookies);
    }

    /**
     * Make a POST request
     * @param url
     * @param params
     * @return the marshaled result of the call
     * @throws Exception
     */
    public ResponseBean post(
        @Nonnull final String url,
        @Nonnull final Map<String, String> params)
        throws Exception {
        return post(url, params, NO_HEADER, NO_COOKIES);
    }
   
    /**
     * Make a POST request
     * @param url
     * @param params
     * @param headers
     * @param cookies
     * @return the marshaled result of the call
     * @throws Exception
     */
    public ResponseBean post(
        @Nonnull final String url,
        @Nonnull final Map<String, String> params,
        @Nonnull final Map<String, String> headers)
        throws Exception {
        return post(url, params, headers, NO_COOKIES);       
    }
   
    /**
     * Make a POST request
     * @param url
     * @param params
     * @param headers
     * @param cookies
     * @return the marshaled result of the call
     * @throws Exception
     */
    public ResponseBean post(
        @Nonnull final String url,
        @Nonnull final Map<String, String> params,
        @Nonnull final Map<String, String> headers,
        @Nonnull final List<Cookie> cookies)
        throws Exception {
        return _post(url, params, headers, cookies);
    }

    
    /**
     * Downloads a file from a given URL
     * @param url
     * @return
     * @throws Exception
     */
    public ResponseBean downloadFile(@Nonnull final String url) throws Exception {
    	return downloadFile(url, GET, NO_HEADER, NO_COOKIES);
    }
    
    /**
     * Downloads a file from a given URL
     * @param url
     * @param method
     * @return
     * @throws Exception
     */
    public ResponseBean downloadFile(@Nonnull final String url, final String method) throws Exception {
    	return downloadFile(url, method, NO_HEADER, NO_COOKIES);
    }
    
    /**
     * Download a file from the give URLs
     * @param url the URL to download from
     * @param method the method (GET, POST). If null then GET is being used
     * @param headers
     * @param cookies
     * @return
     * @throws Exception
     */
    public ResponseBean downloadFile(@Nonnull final String url, final String method, final Map<String, String> headers, final List<Cookie> cookies) throws Exception {
    	ResponseBean responseBean = null;
    	if (StringUtils.isBlank(method) || method.equalsIgnoreCase(GET)) {
    		responseBean = _get(url, EMPTY_PARAMS, headers, cookies, fileDownloadingProcessor);
    	}
    	else if (method.equalsIgnoreCase(POST)) {
    		responseBean = _post(url, EMPTY_PARAMS, headers, cookies, fileDownloadingProcessor);    		
    	}
    	else
    		throw new Exception("Unsupported method");
    	return responseBean;
    }
    
    protected ResponseBean execute(@Nonnull final HttpRequestBase request, final List<Cookie> cookies, final Map<String, String> headers) 
    	throws Exception {
    	return execute(request, cookies, headers, defaultEntityProcessor);
    }

    private ResponseBean execute(@Nonnull final HttpRequestBase request, final List<Cookie> cookies, final Map<String, String> headers, final EntityProcessor entityProcessor) 
    	throws Exception {
    	setHeaders(request, headers);
    	try (CloseableHttpClient httpclient = getHttpClient(cookies)) {
	        HttpClientContext context = HttpClientContext.create();
	        CloseableHttpResponse response = httpclient.execute(request, context);
	        checkError(response);
	        return entityProcessor.processResponse(context, response);
    	}
    }
    
    protected void setHeaders(@Nonnull final HttpRequestBase requeste, final Map<String, String> headers) {
        if (!(headers == null || headers.isEmpty()))
            for(String key: headers.keySet()) {
            	requeste.setHeader(key, headers.get(key));
            }
    }
    
    protected HttpClientBuilder getHttpClientBuilder() {
    	return HttpClientBuilder.create();
    }
    
    protected RedirectStrategy getRedirectStrategy() {
    	return new RedirectorStrategy();
    }
    
    private CloseableHttpClient getHttpClient(final List<Cookie> cookies) {
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

    private ResponseBean _get(final String url, final Map<String, String> params, final Map<String, String> headers, final List<Cookie> cookies)
        throws Exception {
    	return _get(url, params, headers, cookies, defaultEntityProcessor);
     }
    
    private ResponseBean _get(final String url, final Map<String, String> params, final Map<String, String> headers, final List<Cookie> cookies, final EntityProcessor entityProcessor)
        throws Exception {
        StringBuffer sb = new StringBuffer();
        for(String key: params.keySet()) {
            String value = params.get(key);
            if (sb.length() > 0)
                sb.append("&");
            sb.append(key).append("=");
            if (StringUtils.isNoneEmpty(value))
                sb.append(URLEncoder.encode(value, "UTF-8"));
        }
        if (sb.length()>0)
            sb.insert(0, "?");
        sb.insert(0, url);
        // logger.debug("Send GET: " + sb.toString());
           
        HttpGet httpGet = new HttpGet(sb.toString());
        return execute(httpGet, cookies, headers, entityProcessor);
    }

    
    private ResponseBean _post(final String url, final Map<String, String> params, final Map<String, String> headers, final List<Cookie> cookies)
        throws Exception {
    	return _post(url, params, headers, cookies, defaultEntityProcessor);
    }
    
    private ResponseBean _post(final String url, final Map<String, String> params, final Map<String, String> headers, final List<Cookie> cookies, final EntityProcessor entityProcessor)
        throws Exception {
        List <NameValuePair> nameValuePairs = new ArrayList <NameValuePair>();
       
        for(String key: params.keySet())    {
            String value = params.get(key);               
            nameValuePairs.add(new BasicNameValuePair(key, value));
        }

        logger.debug("Send POST: " + url);
        HttpPost httpPost = new HttpPost(url);
        httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs, "UTF-8"));
        return execute(httpPost, cookies, headers, entityProcessor);
    }
    
       
    private String getCurrentURL(final HttpContext context) {
        HttpUriRequest currentReq = (HttpUriRequest) context.getAttribute(HttpCoreContext.HTTP_REQUEST);
        HttpHost currentHost = (HttpHost)  context.getAttribute(HttpCoreContext.HTTP_TARGET_HOST);
        return (currentReq.getURI().isAbsolute()) ? currentReq.getURI().toString() : (currentHost.toURI() + currentReq.getURI());
    }
   
    private List<Cookie> getCookies(final HttpClientContext context) {
        List<Cookie> ret = new ArrayList<Cookie>();
        CookieStore cookieStore = context.getCookieStore();
        for (org.apache.http.cookie.Cookie c: cookieStore.getCookies()) {
        	Cookie r = new Cookie(c.getName(), c.getValue());
            r.setComment(c.getComment());
            r.setDomain(c.getDomain());
            r.setPath(c.getPath());
            r.setVersion(c.getVersion());
            if (c.getExpiryDate() != null) {
                long age = c.getExpiryDate().getTime() - System.currentTimeMillis()/1000;
                int maxAge = BigDecimal.valueOf(age/1000).intValue();
                r.setMaxAge(maxAge);
            }
            // logger.debug("Add cookie: " + c.getName() + "=" + c.getValue() + sAge);
            ret.add(r);
        }
        return ret;
    }
   
    private void checkError(final HttpResponse response) throws HttpClientException {
        if (response.getStatusLine().getStatusCode() != 200) {
            throw new HttpClientException(response);   
        }
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

    private class FileDownloadingProcessor implements EntityProcessor {
        public ResponseBean processResponse(final HttpClientContext context, final HttpResponse response) throws Exception {
            ResponseBean ret = new ResponseBean();
            ret.setCookies(getCookies(context));
            HttpEntity entity = response.getEntity();
            File fRet = File.createTempFile("download", ".file");
            try (BufferedInputStream bis = new BufferedInputStream(entity.getContent());
           		BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(fRet));) {
            	byte[] buffer = new byte[2048];
            	for(int i=0; (i = bis.read(buffer)) > 0 ;) {
            		bos.write(buffer, 0, i);
            	}
            }
            catch(Exception e) {
            	if (!fRet.delete())
            		fRet.deleteOnExit();
            }
            ret.setBody(fRet.getPath());
            ret.setPageUrl(getCurrentURL(context));
            return ret;    	
        }    	
    }
    
    private class DefaultEntityProcessor implements EntityProcessor {
        public ResponseBean processResponse(final HttpClientContext context, final HttpResponse response) throws Exception {
            ResponseBean ret = new ResponseBean();
            ret.setCookies(getCookies(context));
            HttpEntity entity = response.getEntity();
            ret.setBody(EntityUtils.toString(entity, "UTF-8"));
            ret.setPageUrl(getCurrentURL(context));
            return ret;    	
        }    	
    }
    private interface EntityProcessor {
    	ResponseBean processResponse(HttpClientContext context, HttpResponse response) throws Exception;
    }
}
