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
package com.maiereni.host.web.jaxrs.service;

import java.util.List;

import com.maiereni.host.web.jaxrs.service.bo.RepositoryRequest;

/**
 * The API of a service to access the website repository
 * @author Petre Maierean
 *
 */
public interface RepositoryService {
	/**
	 * Query the repository for a statement of a given supported language. Convert the result to a list 
	 * beans of the type specified in resultType
	 * 
	 * @param request
	 * @param resultType
	 * @return
	 * @throws Exception
	 */
	List<? extends Class<?>> getResources(RepositoryRequest request, Class<?> resultType) throws Exception;
}
