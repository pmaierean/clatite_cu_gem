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

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jcr.Binary;
import javax.jcr.Node;
import javax.jcr.Property;
import javax.jcr.nodetype.NodeType;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.maiereni.aem.admin.bean.PrivilegePathRule;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * A command that retrieves the privilege validation rules from a 
 * configuration node
 * @author Petre Maierean
 *
 */
public class GetPrivilegePathRules extends AbstractCommand {
	public static final String QUERY_TYPE_XPATH = "xpath";
	public static final String ELEMENT_INCLUDE_NAME = "include";
	public static final String ELEMENT_INCLUDE_ATTR_PATH = "path";
	public static final String ELEMENT_RULE_NAME = "rule";
	public static final String ELEMENT_RULE_ATTR_LANG = "lang";
	public static final String ELEMENT_RULE_ATTR_STMT = "stmt";
	public static final String ELEMENT_RULE_ATTR_PERMISSIONS_REF = "permissionRef";
	public static final String ELEMENT_PERMISSIONS_NAME = "permissions";
	public static final String ELEMENT_PERMISSIONS_ATTR_ID = "id";
	
	/**
	 * @param resourceResolverFactory
	 * @param resource
	 * @throws Exception
	 */
	public GetPrivilegePathRules(
		final ResourceResolverFactory resourceResolverFactory,
		final Resource resource)
		throws Exception {
		super(resourceResolverFactory, resource);
	}

	@Override
	protected Object process(Object o) throws Exception {
		String nodePath = (String)o;
		Map<String, List<String>> permissionDefs = new HashMap<String, List<String>>();
		List<PrivilegePathRule> rules = loadConfigurationFile(nodePath, permissionDefs);
		logger.debug("Has loaded a number of {} rules from " + nodePath, rules.size());
		return rules; 
	}

	private List<PrivilegePathRule> loadConfigurationFile(final String nodePath, Map<String, List<String>> permissionDefs)  
		throws Exception {
		List<PrivilegePathRule> rules = new ArrayList<PrivilegePathRule>();
		Element element = readXMLFromNode(nodePath);
		logger.debug("Load permissions from node " + nodePath);
		loadPermissionDef(element, permissionDefs);
		NodeList list = element.getElementsByTagName(ELEMENT_INCLUDE_NAME);
		int ix = nodePath.lastIndexOf("/");
		int max = list.getLength();
		for(int i=0; i<max; i++) {
			Element elIncl = (Element)list.item(i);
			String inclPath = elIncl.getAttribute(ELEMENT_INCLUDE_ATTR_PATH);
			if (StringUtils.isBlank(inclPath))
				throw new Exception("The include rule " + i + " is missing attribute 'path'");
			if (!inclPath.startsWith("/"))  // Relative path
				inclPath = nodePath.substring(0, ix + 1) + inclPath;
			
			List<PrivilegePathRule> pr = loadConfigurationFile(inclPath, permissionDefs);
			if (!pr.isEmpty())
				rules.addAll(pr);
		}
		
		logger.debug("Load rules from node " + nodePath);
		return convert(element, permissionDefs);
	}
	
	private void loadPermissionDef(final Element element,  Map<String, List<String>> permissions)  throws Exception {
		NodeList list = element.getElementsByTagName(ELEMENT_PERMISSIONS_NAME);
		int max = list.getLength();
		for(int i=0; i<max; i++) {
			Element p = (Element)list.item(i);
			String key = p.getAttribute(ELEMENT_PERMISSIONS_ATTR_ID);
			String txt = p.getTextContent();
			if (StringUtils.isEmpty(key))
				throw new Exception("Invalid premission element " + i);
			if (permissions.containsKey(key))
				throw new Exception("Duplicate at permission " + i);
			List<String> arr = new ArrayList<String>();
			if (StringUtils.isNotBlank(txt)) {
				String[] toks = txt.split(",");
				for(String tok : toks)
					arr.add(tok);
			}
			permissions.put(key, arr);
		}
	}
	
	@Override
	protected void validateArgument(Object o) throws Exception {
		if (o == null)
			throw new Exception("The argument cannot be null");
		if (!(o instanceof String))
			throw new Exception("The argument must be a string");	
	}

	private List<PrivilegePathRule> convert(final Element element, final Map<String, List<String>> permissions) throws Exception {
		List<PrivilegePathRule> rules = new ArrayList<PrivilegePathRule>();
		NodeList list = element.getElementsByTagName(ELEMENT_RULE_NAME);
		int max = list.getLength();
		for(int i=0; i<max; i++) {
			Element el = (Element)list.item(i);
			String queryLanguage = el.getAttribute(ELEMENT_RULE_ATTR_LANG);
			if (StringUtils.isEmpty(queryLanguage))
				queryLanguage = QUERY_TYPE_XPATH;
			String queryStmt = el.getAttribute(ELEMENT_RULE_ATTR_STMT);
			if (StringUtils.isBlank(queryStmt))
				throw new Exception("Invalid rule definition found at element " + i);
			String permissionRef = el.getAttribute(ELEMENT_RULE_ATTR_PERMISSIONS_REF);
			if (!permissions.containsKey(permissionRef))
				throw new Exception("No permission definition found for key " + permissionRef);
			PrivilegePathRule rule = new PrivilegePathRule();
			rule.setLang(queryLanguage);
			rule.setQueryStmt(queryStmt);
			rule.setPriviledges(new ArrayList<String>());
			rule.getPriviledges().addAll(permissions.get(permissionRef));
			rules.add(rule);
		}
		return rules;
	} 
	
	private Element readXMLFromNode(final String nodePath) throws Exception {
        Node configNode = session.getNode(nodePath);
        if (configNode == null)
        	throw new Exception("The path '" + nodePath + "' does not point to an existing node");
        if (!configNode.isNodeType(NodeType.NT_FILE))
        	throw new Exception("The path '" + nodePath + "' does not point to a file node");
        Node content = configNode.getNode(Node.JCR_CONTENT);
        Property prop = content.getProperty(Property.JCR_DATA);
        if (prop == null)
        	throw new Exception("Tne node at path '" + content.getPath() + "' does not have a data content");
        Binary b = prop.getBinary();
        InputStream is = null;
        ByteArrayInputStream bis = null;
        try {
            is = b.getStream();
            byte[] buffer = IOUtils.toByteArray(is);
            logger.debug("Have read {} bytes from the resource ", buffer.length);
            bis = new ByteArrayInputStream(buffer);
            Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(bis);
            return document.getDocumentElement();
        }
        finally{
            if (bis != null)
                try {
                    bis.close();
                }
                catch(Exception e) {}
            if (is != null)
                try {
                    is.close();
                }
                catch(Exception e) {}
        }
	}
}
