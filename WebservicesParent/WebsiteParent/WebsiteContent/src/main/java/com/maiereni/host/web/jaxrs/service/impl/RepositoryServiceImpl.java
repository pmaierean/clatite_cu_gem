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

import java.util.List;

import javax.annotation.Nonnull;
import javax.jcr.Credentials;
import javax.jcr.NodeIterator;
import javax.jcr.Session;
import javax.jcr.query.Query;
import javax.jcr.query.QueryManager;
import javax.jcr.query.QueryResult;

import org.apache.jackrabbit.core.RepositoryImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.maiereni.host.web.jaxrs.service.RepositoryConsumer;
import com.maiereni.host.web.jaxrs.service.RepositoryConsumerFactory;
import com.maiereni.host.web.jaxrs.service.RepositoryService;
import com.maiereni.host.web.jaxrs.service.RepositoryUserResolver;
import com.maiereni.host.web.jaxrs.service.bo.RepositoryRequest;

/**
 * Repository service 
 * @author Petre Maierean
 *
 */
public class RepositoryServiceImpl implements RepositoryService {
	private static final Logger logger = LoggerFactory.getLogger(RepositoryServiceImpl.class);
	private RepositoryImpl repo;
	private RepositoryUserResolver repoUserResolver;
	private RepositoryConsumerFactory repositoryConsumerFactory;
	
	protected RepositoryServiceImpl(@Nonnull final RepositoryImpl repo, 
			@Nonnull final RepositoryUserResolver repoUserResolver,
			@Nonnull final RepositoryConsumerFactory repositoryConsumerFactory) {
		this.repo = repo;
		this.repoUserResolver = repoUserResolver;
		this.repositoryConsumerFactory = repositoryConsumerFactory;
	}
	
	/**
	 * Query the repository for a statement of a given supported language and convert the result to a list 
	 * beans of the type specified in resultType
	 * 
	 * @param request
	 * @param resultType
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<? extends Class<?>> getResources(@Nonnull final RepositoryRequest request, @Nonnull Class<?> result) throws Exception {
		Credentials credentials = repoUserResolver.getCredentials(request.getRepoUser()) ;
		Session session = repo.login(credentials);
		RepositoryConsumer repositoryConsumer = repositoryConsumerFactory.getConsumer(result);
		try {
			QueryManager qm = session.getWorkspace().getQueryManager();
			logger.debug("Execute statement: " + request.getQueryStmt());
			Query q = qm.createQuery(request.getQueryStmt(), request.getLanguage());
			QueryResult qr = q.execute();
			NodeIterator ni = qr.getNodes();
			logger.debug("Found a number of {} results. Convert them to the expected bean type", ni.getSize());
			ni.forEachRemaining(repositoryConsumer);
		}
		finally {
			session.logout();
		}
		return repositoryConsumer.getResults();
	}

}
