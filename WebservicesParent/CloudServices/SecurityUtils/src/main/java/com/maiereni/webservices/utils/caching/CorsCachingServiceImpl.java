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

/**
 * An implementation of the Caching service that is based on Ehcaching
 *
 * @author Petre Maierean
 */
public class CorsCachingServiceImpl implements CachingService {
    private Cache cache;

    CorsCachingServiceImpl(Cache cache) {
        this.cache = cache;
    }

    @Override
    public boolean hasValue(String value) {
        return cache.get(value, Boolean.class) != null;
    }

    @Override
    public void addValue(String value) {
        cache.evictIfPresent(value);
        cache.put(value, Boolean.TRUE);
    }

    @Override
    public void remove(String value) {
        cache.evictIfPresent(value);
    }
}
