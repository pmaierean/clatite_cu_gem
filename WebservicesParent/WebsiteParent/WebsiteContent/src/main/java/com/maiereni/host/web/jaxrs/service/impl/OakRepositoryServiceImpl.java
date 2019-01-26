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
package com.maiereni.host.web.jaxrs.service.impl;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import org.apache.jackrabbit.oak.api.ContentSession;
import org.apache.jackrabbit.oak.api.QueryEngine;
import org.apache.jackrabbit.oak.api.Result;
import org.apache.jackrabbit.oak.ocm.NodeConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.maiereni.host.web.jaxrs.service.RepositoryService;
import com.maiereni.host.web.jaxrs.service.bo.RepositoryAddNodeRequest;
import com.maiereni.host.web.jaxrs.service.bo.RepositoryDeleteRequest;
import com.maiereni.host.web.jaxrs.service.bo.RepositoryInsertRequest;
import com.maiereni.host.web.jaxrs.service.bo.RepositoryQueryRequest;

/**
 * An implementation of the Repository Service based on Apache OAK
 * @author Petre Maierean
 *
 */
@Component
public class OakRepositoryServiceImpl extends BaseService implements RepositoryService {
	private static final Logger logger = LoggerFactory.getLogger(OakRepositoryServiceImpl.class);
	
	@Autowired
	private NodeConverter nodeConverter;
	
	/**
	 * Query the repository for a statement of a given supported language. Convert the result to a list 
	 * beans of the type specified in resultType
	 * 
	 * @param request
	 * @param resultType
	 * @return
	 * @throws Exception
	 */
	@Override
	public <T> List<T> getResources(@Nonnull final RepositoryQueryRequest request, @Nonnull final Class<T> clazz)
		throws Exception {
		List<T> ret = null;	
		try (ContentSession session = getContentSession(request)) {
			ret = new ArrayList<T>();
			logger.debug("Get resources for " + request.getQueryStmt());
			QueryEngine queryEngine = getQueryEngine(session);
			Result result = queryEngine.executeQuery(request.getQueryStmt(), request.getLanguage(), QueryEngine.NO_BINDINGS, QueryEngine.NO_MAPPINGS);
			ret = nodeConverter.convert(result, clazz);
			logger.debug("Found " + ret.size());
		}
		return ret;
	}

	/* (non-Javadoc)
	 * @see com.maiereni.host.web.jaxrs.service.RepositoryService#addNode(com.maiereni.host.web.jaxrs.service.bo.RepositoryAddNodeRequest)
	 */
	@Override
	public void addNode(RepositoryAddNodeRequest request) throws Exception {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see com.maiereni.host.web.jaxrs.service.RepositoryService#addResource(com.maiereni.host.web.jaxrs.service.bo.RepositoryInsertRequest, java.lang.Object)
	 */
	@Override
	public void addResource(RepositoryInsertRequest request, Object o) throws Exception {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see com.maiereni.host.web.jaxrs.service.RepositoryService#delete(com.maiereni.host.web.jaxrs.service.bo.RepositoryDeleteRequest)
	 */
	@Override
	public void delete(RepositoryDeleteRequest request) throws Exception {
		// TODO Auto-generated method stub

	}

}
