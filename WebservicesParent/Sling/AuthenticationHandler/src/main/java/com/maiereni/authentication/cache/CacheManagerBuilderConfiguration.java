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

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;
/**
 * @author Petre Maierean
 *
 */
@ObjectClassDefinition(
	name = "Cache Manager Builder",
	description = "Configuration for the builder of the Cache Manager"
)
public @interface CacheManagerBuilderConfiguration {
	
    @AttributeDefinition(
		name = "The heap pool size",
		description = "The number of entries to store in the heap of a cache",
		min = "10",
		max = "1000",
		required = false, // Defaults to true
		cardinality = 0
    )
    long heap_pool_size() default 10L;
    
    @AttributeDefinition(
		name = "Time to live in seconds",
		description = "The number of seconds for entries in the heap to live in the cache",
		min = "1800",
		required = false, // Defaults to true
		cardinality = 0
    )
    long seconds_to_live_expiration() default 1800L;
    
    @AttributeDefinition(
		name = "Time to idle in seconds",
		description = "The number of seconds for entries in the heap to idle in the cache",
		min = "1800",
		required = false, // Defaults to true
		cardinality = 0
    )
    long seconds_to_idle_expiration() default 1800L;
    
    @AttributeDefinition(
		name = "Disk Storage",
		description = "The disk storage for the cache overflow",
		required = false, // Defaults to true
		cardinality = 0
    )
    String disk_storage() default "java.io.tmpDir";
}
