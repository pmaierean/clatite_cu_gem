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

import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 *
 * @author Petre Maierean
 */
@Configuration
@EnableCaching
public class JCachingServiceFactory {
    private static final String CORS_TOKENS_CACHE = "corsTokens";

    @Bean("corsTokenCachingService")
    public CorsCachingServiceImpl getCorsTokenCachingService(final CacheManager cacheManager) throws Exception {
        Cache cache = cacheManager.getCache(CORS_TOKENS_CACHE);
        return new CorsCachingServiceImpl(cache);
    }
}
