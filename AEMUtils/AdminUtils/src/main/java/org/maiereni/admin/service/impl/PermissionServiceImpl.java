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
package org.maiereni.admin.service.impl;

import java.util.Hashtable;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.ConfigurationPolicy;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.maiereni.aem.admin.bean.Id2Path;
import org.maiereni.aem.admin.bean.IdPathPriviledges;
import org.maiereni.aem.admin.bean.IdPrivilegePathRules;
import org.maiereni.aem.admin.bean.PrivilegePathRule;
import org.maiereni.aem.admin.bean.PrivilegePathRuleError;
import org.maiereni.aem.admin.command.GetPrivilegePathRules;
import org.maiereni.aem.admin.command.RemoveOptionalPermission;
import org.maiereni.aem.admin.command.RemovePermission;
import org.maiereni.aem.admin.command.RemovePermissionsOnAllChildNodes;
import org.maiereni.aem.admin.command.SetPermission;
import org.maiereni.aem.admin.command.ValidateIdPrivilegePathRules;
import org.maiereni.aem.admin.service.PermissionService;

/**
 * Implementation of the PermissionService
 * @author Petre Maierean
 *
 */
@Component(
        label = "Utility Permission Service",
       	immediate = true, 
        metatype = true,
        policy = ConfigurationPolicy.OPTIONAL
)
@Service
public class PermissionServiceImpl implements PermissionService {
	@Reference
	private ResourceResolverFactory resolverFactory;

	private Hashtable<String, List<PrivilegePathRule>> ruleCache = new Hashtable<String, List<PrivilegePathRule>>();
	
	/**
	 * Remove all permissions on a resource. If session is passes then use argument commit to control
	 * when the modification is actually saved <br/>
	 * If the session is null then open an administrative session to perform the operation<br/>
	 * The method throws exception if id2path is null, or it is blank (either id or path are blank)
	 * 
	 * @param resource the user session, or null (open admin session)
	 * @param id2path cannot be null
	 * @param commit
	 * @throws Exception
	 */
	public void removePermission(final Resource resource, final Id2Path id2path, final boolean commit) 
		throws Exception {
		new RemovePermission(resolverFactory, resource, commit).processAndCommit(id2path);
	}

	/**
	 * Remove permission on an optional resource. If session is passes then use argument commit to control
	 * when the modification is actually saved<br/>
	 * If the session is null then open an administrative session to perform the operation<br/>
	 * The method throws exception if id2path is null, or it is blank (either id or path are blank)
	 * 
	 * @param resource the user session, or null (open admin session)
	 * @param id2path cannot be null	 
	 * @param commit
	 * @throws Exception
	 */
	public void removePermissionOptionalPath(final Resource resource, final Id2Path id2path, final boolean commit)
			throws Exception {
		new RemoveOptionalPermission(resolverFactory, resource, commit).processAndCommit(id2path);
	}

	/**
	 * Remove permissions on all child nodes of a given node. If session is passes then use argument commit to control
	 * when the modification is actually saved <br/>
	 * If the session is null then open an administrative session to perform the operation<br/>
	 * The method throws exception if id2path is null, or it is blank (either id or path are blank)
	 * 
	 * @param resource the user session, or null (open admin session)
	 * @param id2path cannot be null
	 * @param commit
	 * @throws Exception
	 */
	public void removePermissionsOnAllChildNodes(final Resource resource, final Id2Path id2path, final boolean commit)
		throws Exception {
		new RemovePermissionsOnAllChildNodes(resolverFactory, resource, commit).processAndCommit(id2path);
	}

	/**
	 * Set permission for an authorizable identity (either user or group). If session is passes then use argument commit to control
	 * when the modification is actually saved <br/>
	 * If the session is null then open an administrative session to perform the operation<br/>
	 * The method throws exception if idPathPriviledges is null, or it is blank (either id, path, or privileges are blank)
	 * 
	 * @param resource the user session, or null (open admin session)
	 * @param idPathPriviledges cannot be null
	 * @param commit commit the change
	 * @throws Exception
	 */
	public void setPermission(final Resource resource, final IdPathPriviledges idPathPriviledges, final boolean commit)
		throws Exception {
		new SetPermission(resolverFactory, resource, commit).processAndCommit(idPathPriviledges);
	}

	/**
	 * Read the privilege rules defined at the configPath and checks them against the actual privilege of an
	 * authorizable  
	 * 
	 * @param resource
	 * @param id
	 * @param configPath the configuration path
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public List<PrivilegePathRuleError> matchPermissionRules(final Resource resource, final String id, final String configPath) throws Exception {
		if (StringUtils.isBlank(configPath))
			throw new Exception("Invalid case. The configPath to point to the node with rules cannot be null");
		if (!ruleCache.containsKey(configPath)) {
			List<PrivilegePathRule> priviledges = (List<PrivilegePathRule>)new GetPrivilegePathRules(resolverFactory, resource).processAndCommit(configPath);
			ruleCache.put(configPath, priviledges);
		}
		List<PrivilegePathRule> privileges = ruleCache.get(configPath);
		IdPrivilegePathRules id2Privileges = new IdPrivilegePathRules();
		id2Privileges.setId(id);
		id2Privileges.setRules(privileges);
		return (List<PrivilegePathRuleError>)new ValidateIdPrivilegePathRules(resolverFactory, resource).processAndCommit(id2Privileges);
	}
	
	public ResourceResolverFactory getResolverFactory() {
		return resolverFactory;
	}

	public void setResolverFactory(ResourceResolverFactory resolverFactory) {
		this.resolverFactory = resolverFactory;
	}
}
