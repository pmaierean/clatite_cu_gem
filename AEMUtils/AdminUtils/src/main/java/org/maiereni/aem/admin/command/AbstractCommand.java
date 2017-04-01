/**
 * Copyright 2017 Maiereni Software and Consulting Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at 
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS, 
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
 * See the License for the specific language governing permissions and 
 * limitations under the License.
 */
package org.maiereni.aem.admin.command;

import java.security.Principal;
import java.util.NoSuchElementException;

import javax.jcr.Session;
import javax.jcr.security.AccessControlEntry;
import javax.jcr.security.AccessControlList;
import javax.jcr.security.AccessControlManager;
import javax.jcr.security.Privilege;

import org.apache.jackrabbit.api.JackrabbitSession;
import org.apache.jackrabbit.api.security.user.UserManager;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Petre Maierean
 *
 */
abstract class AbstractCommand {
	protected Logger logger = LoggerFactory.getLogger(getClass());
	protected JackrabbitSession session;
	private static final String[] PRIVILEDGE_READ_ONLY = new String[] { Privilege.JCR_READ };
	protected boolean askToCommit, toClose;

	public AbstractCommand(
		final ResourceResolverFactory resourceResolverFactory) 
		throws Exception {
		this(resourceResolverFactory, null, false);
	}
	
	public AbstractCommand(
		final ResourceResolverFactory resourceResolverFactory,
		final Resource resource) 
		throws Exception {
		this(resourceResolverFactory, resource, false);
	}
	
	public AbstractCommand(
		final ResourceResolverFactory resourceResolverFactory, 
		final Resource resource,
		boolean askToCommit) 
		throws Exception {
		if (resourceResolverFactory == null)
			throw new Exception("This resource resolver factory cannot be null");
		this.askToCommit = askToCommit;
		initSession(resourceResolverFactory, resource);
	}
	
	protected abstract Object process(final Object o) throws Exception;
	protected abstract void validateArgument(final Object o) throws Exception;
	
	/**
	 * Validate the call argument, process the call, commit and close the admin session if
	 * that has been open
	 * @param o
	 * @return
	 * @throws Exception
	 */
	public Object processAndCommit(final Object o) throws Exception {
		Object ret = null;
		validateArgument(o);
		try {
			ret = process(o);
			if (askToCommit) {
				session.save();
				session.refresh(false);
			}
		}
		finally {
			if (toClose) {
				session.logout();
				logger.debug("The administrative session as been closed");
			}
		}
		return ret;
	}

	protected boolean isAuthorizable(final String id) {
		try {
			return session.getUserManager().getAuthorizable(id) != null;
		} catch (Exception e) {
		}
		return false;
	}	
	
	protected boolean isAuthorizable(final UserManager manager, final String id) {
		try {
			return manager.getAuthorizable(id) != null;
		} catch (Exception e) {
		}
		return false;
	}
	
	protected void setPermission(final AccessControlManager aMgr,
			final Principal principal, final String path,
			final String[] privileges) throws Exception {
		setPermission(aMgr, principal, path, privileges, true);
	}
	
	protected void setPermission(final AccessControlManager aMgr,
			final Principal principal, final String path,
			final String[] privileges, boolean isAllow) throws Exception {
		// create a privilege set with jcr:all
		Privilege[] p = new Privilege[privileges.length];
		for (int i = 0; i < privileges.length; i++) {
			p[i] = aMgr.privilegeFromName(privileges[i]);
		}
		AccessControlList acl;
		try {
			acl = (AccessControlList) aMgr.getApplicablePolicies(path)
					.nextAccessControlPolicy();
		} catch (NoSuchElementException e) {
			acl = (AccessControlList) aMgr.getPolicies(path)[0];
		}
		// Remove all entries belonging to the current principal
		for (AccessControlEntry e : acl.getAccessControlEntries()) {
			if (e.getPrincipal().equals(principal))
				acl.removeAccessControlEntry(e);
		}
		if (!isAllow) {
			if (acl instanceof org.apache.jackrabbit.api.security.JackrabbitAccessControlList)
				if (!((org.apache.jackrabbit.api.security.JackrabbitAccessControlList) acl)
						.addEntry(principal, p, false))
					logger.warn("Could not deny privilege on path " + path);
		} else {
			if (!acl.addAccessControlEntry(principal, p))
				logger.warn("Could not allow privilege on path " + path);
		}
		// the policy must be re-set
		aMgr.setPolicy(path, acl);
	}

	protected void removePermission(final AccessControlManager aMgr,
			final Principal principal, final String path) throws Exception {
		setPermission(aMgr, principal, path, PRIVILEDGE_READ_ONLY, false);
	}

	protected void initSession(
		final ResourceResolverFactory resourceResolverFactory,
		final Resource resource) 
		throws Exception {
		ResourceResolver resourceResolver = null;
		if (resource != null)
			resourceResolver = resource.getResourceResolver();
		else {
			resourceResolver = resourceResolverFactory.getAdministrativeResourceResolver(null);
			toClose = true;
		}
		initSession(resourceResolver);
	}
	
	protected void initSession(final ResourceResolver resourceResolver) 
		throws Exception{
		session = (JackrabbitSession)resourceResolver.adaptTo(Session.class);		
	}
}
