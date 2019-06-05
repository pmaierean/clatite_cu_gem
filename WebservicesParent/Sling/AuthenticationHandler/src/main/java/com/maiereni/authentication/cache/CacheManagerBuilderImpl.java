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
package com.maiereni.authentication.cache;

import java.io.File;
import java.time.Duration;

import org.ehcache.Cache;
import org.ehcache.CacheManager;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.CacheManagerBuilder;
import org.ehcache.config.builders.ExpiryPolicyBuilder;
import org.ehcache.config.builders.PooledExecutionServiceConfigurationBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.ehcache.expiry.ExpiryPolicy;
import org.ehcache.impl.config.executor.PooledExecutionServiceConfiguration;
import org.ehcache.impl.config.persistence.CacheManagerPersistenceConfiguration;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A provider of cache 
 * 
 * @author Petre Maierean
 *
 */
@Component(
	service = { CacheManagerProvider.class },
	immediate = true,
	configurationPolicy = ConfigurationPolicy.REQUIRE,
	property = {
		Constants.SERVICE_VENDOR + "=Maiereni Software and Consulting Inc",
		Constants.SERVICE_DESCRIPTION + "=Activates the CacheManager and provides instances of cache"
	}
)
@Designate( ocd=CacheManagerBuilderConfiguration.class, factory=true )
@CacheManagerBuilderConfiguration
public class CacheManagerBuilderImpl implements CacheManagerProvider {
	public static final String TMP_DIR = "java.io.tmpdir";
	private final Logger log = LoggerFactory.getLogger(CacheManagerBuilderImpl.class);
	private CacheManager cacheManager;
	private long heapPoolSize, secondsToLive, secondsToIdle;
	
	
	/**
	 * Creates and configures an instance of ehcache manager
	 * 
	 * @param componentContext
	 */
	@Activate
	protected void activate(final CacheManagerBuilderConfiguration configuration) {
		heapPoolSize = configuration.heap_pool_size();
		secondsToLive = configuration.seconds_to_live_expiration();
		secondsToIdle = configuration.seconds_to_idle_expiration();
		
		PooledExecutionServiceConfiguration pooledExecutionServiceConfiguration  = PooledExecutionServiceConfigurationBuilder.newPooledExecutionServiceConfigurationBuilder() 
        	.defaultPool("dflt", 0, 10)
        	.pool("defaultDiskPool", 1, 3)
        	.pool("cache2Pool", 2, 2)
        .build();
		
		String overflowDir = configuration.disk_storage();
		if (overflowDir.equals(TMP_DIR)) {
			overflowDir = System.getProperty(TMP_DIR);
		}
		CacheManagerPersistenceConfiguration persistenceConfiguration = new CacheManagerPersistenceConfiguration(new File(overflowDir, "cache"));
		
		cacheManager = CacheManagerBuilder.newCacheManagerBuilder()
			.using(pooledExecutionServiceConfiguration)
			.using(persistenceConfiguration)
			.build(); 
		cacheManager.init(); 
		log.debug("The cache manager has been created");
	}

	/**
	 * Creates a cache by the specified cacheName. Configure it to hold entries of the clazzName type. Always the String as a key
	 * 
	 * @param cacheName the name of the cache
	 * @param clazzName the type of entity to store
	 */
	@Override
	public <T> Cache<String, T> getCache(final String cacheName, Class<T> clazzName) throws Exception {
		Cache<String, T> ret = cacheManager.getCache(cacheName, String.class, clazzName);
		if (ret == null) {
			log.debug("Creates a cache for alias " + cacheName);
			ResourcePoolsBuilder resourcePoolsBuilder = ResourcePoolsBuilder.heap(heapPoolSize);
			ExpiryPolicy<Object, Object> timeToLiveexpiryPolicy = ExpiryPolicyBuilder.timeToLiveExpiration(Duration.ofSeconds(secondsToLive));
			ExpiryPolicy<Object, Object> timeToIdlePolicy = ExpiryPolicyBuilder.timeToIdleExpiration(Duration.ofSeconds(secondsToIdle));
			
			CacheConfigurationBuilder<String, T> configBuilder = 
				CacheConfigurationBuilder.newCacheConfigurationBuilder(String.class, clazzName, resourcePoolsBuilder)
					.withExpiry(timeToLiveexpiryPolicy)
					.withExpiry(timeToIdlePolicy);
			ret = cacheManager.createCache(cacheName, configBuilder);
		}
		return ret;
	}
}
