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
import java.util.Set;

import javax.annotation.Nonnull;
import javax.jcr.Credentials;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.Session;
import javax.jcr.query.Query;
import javax.jcr.query.QueryManager;
import javax.jcr.query.QueryResult;

import org.apache.commons.lang3.StringUtils;
import org.apache.jackrabbit.core.RepositoryImpl;
import org.apache.jackrabbit.ocm.exception.ObjectContentManagerException;
import org.apache.jackrabbit.ocm.manager.ObjectContentManager;
import org.apache.jackrabbit.ocm.manager.atomictypeconverter.impl.DefaultAtomicTypeConverterProvider;
import org.apache.jackrabbit.ocm.manager.cache.ObjectCache;
import org.apache.jackrabbit.ocm.manager.cache.impl.RequestObjectCacheImpl;
import org.apache.jackrabbit.ocm.manager.impl.ObjectContentManagerImpl;
import org.apache.jackrabbit.ocm.manager.objectconverter.ObjectConverter;
import org.apache.jackrabbit.ocm.manager.objectconverter.impl.ObjectConverterImpl;
import org.apache.jackrabbit.ocm.manager.objectconverter.impl.ProxyManagerImpl;
import org.apache.jackrabbit.ocm.mapper.impl.annotation.AnnotationMapperImpl;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.maiereni.host.web.jaxrs.service.RepositoryService;
import com.maiereni.host.web.jaxrs.service.RepositoryUserResolver;
import com.maiereni.host.web.jaxrs.service.bo.RepositoryAddNodeRequest;
import com.maiereni.host.web.jaxrs.service.bo.RepositoryDeleteRequest;
import com.maiereni.host.web.jaxrs.service.bo.RepositoryInsertRequest;
import com.maiereni.host.web.jaxrs.service.bo.RepositoryQueryRequest;
import com.maiereni.host.web.util.ExceptionHolder;

/**
 * Repository service 
 * @author Petre Maierean
 *
 */
public class RepositoryServiceImpl implements RepositoryService {
	private static final Logger logger = LoggerFactory.getLogger(RepositoryServiceImpl.class);
	private RepositoryImpl repo;
	private RepositoryUserResolver repoUserResolver;
	private AnnotationMapperImpl mapper;
	private NodePropertiesHelper nodePropertiesHelper;
	
	protected RepositoryServiceImpl(@Nonnull final RepositoryImpl repo, 
			@Nonnull final RepositoryUserResolver repoUserResolver) throws Exception {
		this.repo = repo;
		this.repoUserResolver = repoUserResolver;
		this.mapper = new AnnotationMapperImpl(scan());
		this.nodePropertiesHelper = new NodePropertiesHelper();
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
	public List<Object> getResources(@Nonnull final RepositoryQueryRequest request, @Nonnull Class<?> result) throws Exception {
		final List<Object> ret = new ArrayList<Object>();
		Credentials credentials = repoUserResolver.getCredentials(request.getRepoUser()) ;
		Session session = null;
		try {
			session = repo.login(credentials);
			ObjectContentManager ocm =  new ObjectContentManagerImpl(session, mapper); 
			QueryManager qm = session.getWorkspace().getQueryManager();
			logger.debug("Execute statement: " + request.getQueryStmt());
			Query q = qm.createQuery(request.getQueryStmt(), request.getLanguage());
			QueryResult qr = q.execute();
			NodeIterator ni = qr.getNodes();
			logger.debug("Found a number of {} results. Convert them to the expected bean type", ni.getSize());
			final ExceptionHolder contentException = new ExceptionHolder();
			ni.forEachRemaining((n) -> {
				String crtPath = null;
				try {
					Node nd = (Node)n;
					crtPath = nd.getPath();
					Object o = ocm.getObject(result, crtPath);
					ret.add(o);
				}
				catch(ObjectContentManagerException ex) {
					logger.error("Content cast exception for node at " + crtPath);
					contentException.setException(ex);
				}
				catch(Exception e) {
					logger.error("Generic exception ", e);
				}
			});
			if (contentException.isException()) {
				throw contentException.getException();
			}
		}
		finally {
			session.logout();
		}
		return ret;
	}
	
	/**
	 * Add node to the repository
	 * @param request
	 * @throws Exception
	 */
	public void addNode(@Nonnull final RepositoryAddNodeRequest request) throws Exception {
		if (StringUtils.isAnyEmpty(request.getPath(), request.getName()))
			throw new Exception("Either path or name is null");
		Credentials credentials = repoUserResolver.getCredentials(request.getRepoUser()) ;
		Session session = null;
		try {
			session = repo.login(credentials);
			Node parentNode = session.getNode(request.getPath());
			Node node = parentNode.addNode(request.getName(), request.getType());
			if (request.getProperties() != null) 
				nodePropertiesHelper.addNodeProperties(node, request.getProperties());
			session.save();
		}
		finally {
			session.logout();
		}		
	}
	
	/**
	 * Save a resource at the parent path specified in the request
	 * @param request 
	 * @param o the object to save
	 * @throws Exception
	 */
	public void addResource(@Nonnull final RepositoryInsertRequest request, @Nonnull final Object o) throws Exception {
		Credentials credentials = repoUserResolver.getCredentials(request.getRepoUser()) ;
		if (mapper.getClassDescriptorByClass(o.getClass())== null)
			throw new Exception("The object cannot be persisted because its class has not been registered for JCR");
		Session session = null;
		try {
			session = repo.login(credentials);
			ObjectConverter objectConverter = getObjectConverter(session); 
			logger.debug("Find parent: " + request.getParentPath());
			Node parentNode = session.getNode(request.getParentPath());
			objectConverter.insert(session, parentNode, request.getName(), o);
			session.save();
		}
		finally {
			session.logout();
		}
	}
	/**
	 * Delete a resource
	 * @param request
	 * @throws Exception 
	 */
	public void delete(@Nonnull final RepositoryDeleteRequest request)  throws Exception {
		Credentials credentials = repoUserResolver.getCredentials(request.getRepoUser()) ;
		Session session = null;
		try {
			session = repo.login(credentials);
			logger.debug("Delete node at: " + request.getPath());
			session.removeItem(request.getPath());
			session.save();
		}
		finally {
			session.logout();
		}	
	}
	
	@SuppressWarnings("rawtypes")
	private List<Class> scan() throws Exception {
		List<Class> ret = new ArrayList<Class>();
		Reflections reflections = new Reflections("com.maiereni.host");
		Set<Class<?>> annotated = reflections.getTypesAnnotatedWith(org.apache.jackrabbit.ocm.mapper.impl.annotation.Node.class);
		ret.addAll(annotated);
		return ret;
	}
	
	private ObjectConverter getObjectConverter(final Session session) throws Exception {
		DefaultAtomicTypeConverterProvider converterProvider = new DefaultAtomicTypeConverterProvider();
        ObjectCache requestObjectCache = new RequestObjectCacheImpl();
        return new ObjectConverterImpl(mapper, converterProvider, new ProxyManagerImpl(), requestObjectCache);
	}
}
