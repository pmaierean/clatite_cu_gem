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
import java.util.ArrayList;
import java.util.List;

import javax.jcr.Node;
import javax.jcr.Workspace;
import javax.jcr.query.Query;
import javax.jcr.query.QueryManager;
import javax.jcr.query.QueryResult;
import javax.jcr.security.AccessControlEntry;
import javax.jcr.security.AccessControlList;
import javax.jcr.security.AccessControlManager;
import javax.jcr.security.AccessControlPolicy;
import javax.jcr.security.Privilege;

import org.apache.commons.lang3.StringUtils;
import org.apache.jackrabbit.api.security.principal.PrincipalManager;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.maiereni.aem.admin.bean.IdPrivilegePathRules;
import org.maiereni.aem.admin.bean.PrivilegePathRule;
import org.maiereni.aem.admin.bean.PrivilegePathRuleError;

/**
 * @author Petre Maierean
 *
 */
public class ValidateIdPrivilegePathRules extends AbstractCommand {
	public static final String ROOT_XPATH_STMT = "/jcr:root";
	/**
	 * @param resourceResolverFactory
	 * @throws Exception
	 */
	public ValidateIdPrivilegePathRules(
		final ResourceResolverFactory resourceResolverFactory,
		final Resource resource) throws Exception {
		super(resourceResolverFactory, resource);
	}

	@Override
	protected Object process(Object o) throws Exception {
		List<PrivilegePathRuleError> issues = new ArrayList<PrivilegePathRuleError>();
		IdPrivilegePathRules arg = (IdPrivilegePathRules)o;
		PrincipalManager pm = session.getPrincipalManager();
		
		Principal principal = pm.getPrincipal(arg.getId());
		if (principal == null)
			throw new Exception("Could not resolve a principal for: '" + arg.getId() + "'");
	
		AccessControlManager aMgr = session.getAccessControlManager();
		Workspace ws =  session.getWorkspace();
		QueryManager queryManager = ws.getQueryManager();
		for(PrivilegePathRule rule : arg.getRules()) {
			String stmt = rule.getQueryStmt();
			if (!stmt.startsWith(ROOT_XPATH_STMT)) 
				stmt = ROOT_XPATH_STMT + stmt;
			
			Query query = queryManager.createQuery(stmt, rule.getLang());
			QueryResult result = query.execute();
			javax.jcr.NodeIterator nodeIter = result.getNodes();
			while ( nodeIter.hasNext() ) {
			    Node node = nodeIter.nextNode();
			    String path = node.getPath();
			    PrivilegePathRuleError error = validate(aMgr, path, principal, rule);
		    	issues.add(error);
			}
		}
		return issues;
	}

	PrivilegePathRuleError validate(final AccessControlManager aMgr, final String path, final Principal principal, final PrivilegePathRule rule) 
		throws Exception {
		PrivilegePathRuleError error = new PrivilegePathRuleError();
		error.setPath(path);
		error.setExpected(rule.getPriviledges());
		error.setEffective(new ArrayList<String>());
		error.setMatch(true);
		AccessControlPolicy[] policies = aMgr.getEffectivePolicies(path);
		if (policies.length == 0 && error.getExpected().size() > 0) {
			error.setMatch(false);
		} else {
			for(AccessControlPolicy policy: policies){
				if (policy instanceof AccessControlList) {
					AccessControlList acl = (AccessControlList)policy;
					for (AccessControlEntry e : acl.getAccessControlEntries()) {
						if (e.getPrincipal().equals(principal)) {
							Privilege[] privileges = e.getPrivileges();
							for(Privilege p : privileges) {
								String privilegeName = p.getName();
								if (!error.getEffective().contains(privilegeName))
									error.getEffective().add(privilegeName);
								if (!rule.getPriviledges().contains(privilegeName)) { // No match
									error.setMatch(false);
								}
							}
						}
					}
				}
			}
		}
		return error;
	}
	
	@Override
	protected void validateArgument(Object o) throws Exception {
		if (o == null)
			throw new Exception("The argument cannot be null");
		if (!(o instanceof IdPrivilegePathRules))
			throw new Exception("The argument is expected as IdPrivilegePathRules");
		IdPrivilegePathRules rules = (IdPrivilegePathRules)o;
		if (StringUtils.isBlank(rules.getId()))
			throw new Exception("The argument is expected to contain a not null id");
		if (rules.getRules() == null)
			throw new Exception("Wrong case. The argument is expected to contain a non null list of rules");
	}

}
