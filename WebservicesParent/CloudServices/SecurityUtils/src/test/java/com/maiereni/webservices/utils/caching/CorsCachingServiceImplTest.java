/**
 * ================================================================
 * Copyright (c) 2017-2020 Maiereni Software and Consulting Inc
 * ================================================================
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.maiereni.webservices.utils.caching;

import static org.junit.Assert.assertTrue;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Before;
import org.junit.Test;
import org.springframework.cache.Cache;
import org.springframework.cache.jcache.JCacheCacheManager;
import org.springframework.cache.jcache.JCacheManagerFactoryBean;
import org.springframework.core.io.ClassPathResource;

import java.util.UUID;

/**
 * Unit test from the CorsCachingServiceImpl
 * @author Petre Maierean
 */
public class CorsCachingServiceImplTest {
    private static final Log logger = LogFactory.getLog(CorsCachingServiceImplTest.class);
    private CachingService cachingService;
    private static final JCacheCacheManager cacheManager = getTestFactory();

    @Before
    public void setUp() {
        Cache cache = cacheManager.getCache("corsTokens");
        cachingService = new CorsCachingServiceImpl(cache);
    }

    @Test
    public void testAddCache() {
        final String value = UUID.randomUUID().toString();
        cachingService.addValue(value);
        assertTrue(cachingService.hasValue(value));
    }

    @Test
    public void testRemoveCache() {
        final String value = UUID.randomUUID().toString();
        cachingService.addValue(value);
        cachingService.remove(value);
        assertTrue(!cachingService.hasValue(value));
    }


    private static JCacheCacheManager getTestFactory() {
        JCacheManagerFactoryBean ret = new JCacheManagerFactoryBean();
        try {
            ret.setCacheManagerUri(new ClassPathResource("/testEhcache.xml").getURI());
            ret.afterPropertiesSet();
        }
        catch(Exception e) {
            logger.error("Cannot configure the cache", e);
            throw new ExceptionInInitializerError(e);
        }
        return new JCacheCacheManager(ret.getObject());
    }
}
