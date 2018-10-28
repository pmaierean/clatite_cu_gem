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

import com.maiereni.host.web.jaxrs.service.bo.RepositoryAddNodeRequest;
import com.maiereni.host.web.jaxrs.service.bo.RepositoryDeleteRequest;
import com.maiereni.host.web.jaxrs.service.bo.RepositoryInsertRequest;
import com.maiereni.host.web.jaxrs.service.bo.RepositoryQueryRequest;

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
	List<? extends Object> getResources(RepositoryQueryRequest request, Class<? extends Object> resultType) throws Exception;
	/**
	 * Add a node
	 * 
	 * @param request
	 * @throws Exception
	 */
	void addNode(RepositoryAddNodeRequest request) throws Exception;
	/**
	 * Save a resource
	 * 
	 * @param request
	 * @param o
	 * @throws Exception
	 */
	void addResource(RepositoryInsertRequest request, Object o) throws Exception;
	/**
	 * Delete a resource
	 * @param request
	 * @throws Exception 
	 */
	void delete(RepositoryDeleteRequest request)  throws Exception;
}
