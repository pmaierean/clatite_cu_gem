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
package com.maiereni.host.web.jaxrs.service.cache;

import java.net.URI;

import javax.annotation.Nonnull;
import javax.cache.Cache;
import javax.cache.CacheManager;
import javax.cache.Caching;
import javax.cache.spi.CachingProvider;
import javax.jcr.SimpleCredentials;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.maiereni.host.web.util.BaseBeanFactory;

/**
 * @author Petre Maierean
 *
 */
@Configuration
public class CacheBeanFactory extends BaseBeanFactory {
	public static final String CACHE_CONFIG_KEY = "cache.configuration";
	public CacheBeanFactory() throws Exception {
		super();
	}

	@Bean(name="cacheManager")
	public CacheManager getCacheManager() throws Exception {
		CachingProvider cachingProvider = Caching.getCachingProvider();
		String cacheCfgFile = getProperty(CACHE_CONFIG_KEY, "/ehcache.xml");
		URI config = getClass().getResource(cacheCfgFile).toURI();
		return cachingProvider.getCacheManager(config, getClass().getClassLoader());
	}

	@Bean(name="sessionTokenResolver")
	public SessionTokenResolverImpl getSessionTokenResolver(@Nonnull final CacheManager cacheManager) throws Exception {
		Cache<String, SimpleCredentials> cache = cacheManager.getCache("clientCache", String.class, SimpleCredentials.class);
		if (cache == null) {
			throw new Exception("Cannot find clientCache");
		}
		return new SessionTokenResolverImpl(cache);
	}
}
