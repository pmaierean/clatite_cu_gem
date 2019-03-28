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
package com.maiereni.ehcache2.jgroup.jaxrs2;

import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.bootstrap.BootstrapCacheLoader;
import net.sf.ehcache.config.CacheConfiguration;
import net.sf.ehcache.config.CacheConfiguration.BootstrapCacheLoaderFactoryConfiguration;
import net.sf.ehcache.config.PersistenceConfiguration;
import net.sf.ehcache.config.PersistenceConfiguration.Strategy;
import net.sf.ehcache.distribution.jgroups.JGroupsBootstrapCacheLoaderFactory;
import net.sf.ehcache.distribution.jgroups.JGroupsCacheManagerPeerProviderConfiguration;
import net.sf.ehcache.distribution.jgroups.JGroupsCacheReplicatorFactory;
import net.sf.ehcache.event.CacheEventListener;
import net.sf.ehcache.store.MemoryStoreEvictionPolicy;

/**
 * @author Petre Maierean
 *
 */
@Configuration
public class ApplicationBeansFactory {
	private static final Logger logger = LoggerFactory.getLogger(ApplicationBeansFactory.class);

	public static final String CHANNEL_NAME = "channelName";
	public static final String CLUSTERED_CACHE = "clusteredCache";
	
	public Properties getJGroupProperties() {
		String channelName = System.getProperty(CHANNEL_NAME, "sample");
		Properties ret = new Properties();
		ret.setProperty(CHANNEL_NAME, channelName);
		ret.setProperty("file", "udp.xml");
		ret.setProperty("bootstrapAsynchronously", "true");
		return ret;
	}
	
	public CacheEventListener getCacheReplicator() {
		JGroupsCacheReplicatorFactory factory = new JGroupsCacheReplicatorFactory();
		Properties replicatorProperties = new Properties();
		return factory.createCacheEventListener(replicatorProperties);
	}
	
	public BootstrapCacheLoader getJGroupsBootstrapCacheLoader() {
		JGroupsBootstrapCacheLoaderFactory factory = new JGroupsBootstrapCacheLoaderFactory();
		Properties properties = new Properties();		
		return factory.createBootstrapCacheLoader(properties);
	}
	
	@Bean("cacheManager")
	@Scope("singleton")
	public CacheManager getEhCacheManagerFactoryBean() {
		net.sf.ehcache.config.Configuration configuration = new net.sf.ehcache.config.Configuration();		
		configuration.addCacheManagerPeerProviderFactory(new JGroupsCacheManagerPeerProviderConfiguration());
		CacheManager ret = new CacheManager(configuration);		
		logger.debug("Created cache manager");
		return ret;
	}
	
	@Bean("clusteredCache")
	public Cache getClusteredEhCache(final CacheManager cacheManager) {
		Cache ret = cacheManager.getCache("clusteredCache");
		if (ret == null) {
			//Properties props = getJGroupProperties();
			BootstrapCacheLoaderFactoryConfiguration cacheLoaderConfig = new BootstrapCacheLoaderFactoryConfiguration();
			cacheLoaderConfig.className(JGroupsBootstrapCacheLoaderFactory.class.getName());
			cacheLoaderConfig.setProperties("bootstrapAsynchronously=true");
					
			CacheConfiguration cacheConfiguration = new CacheConfiguration(CLUSTERED_CACHE, 1000);
			CacheConfiguration.BootstrapCacheLoaderFactoryConfiguration bootstrapConfig = new CacheConfiguration.BootstrapCacheLoaderFactoryConfiguration();
			bootstrapConfig.setClass(JGroupsBootstrapCacheLoaderFactory.class.getName());
			
			cacheConfiguration.addBootstrapCacheLoaderFactory(bootstrapConfig);
			cacheConfiguration.memoryStoreEvictionPolicy(MemoryStoreEvictionPolicy.LFU)     
				.eternal(false)
				.timeToLiveSeconds(60)
				.timeToIdleSeconds(30)
				.diskExpiryThreadIntervalSeconds(0)
				.persistence(new PersistenceConfiguration().strategy(Strategy.LOCALTEMPSWAP))
				.addBootstrapCacheLoaderFactory(cacheLoaderConfig);
			ret = new Cache(cacheConfiguration);
			ret.getCacheEventNotificationService().registerListener(getCacheReplicator());
			cacheManager.addCache(ret);
			logger.debug("Created the clustered cache as " + ret.toString());
		}
		return ret;
	}

}
