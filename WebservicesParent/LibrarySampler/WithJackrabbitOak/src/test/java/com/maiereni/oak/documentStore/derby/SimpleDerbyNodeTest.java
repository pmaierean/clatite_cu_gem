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
package com.maiereni.oak.documentStore.derby;

import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.util.Calendar;

import javax.jcr.Node;
import javax.jcr.PathNotFoundException;
import javax.jcr.PropertyType;
import javax.jcr.Repository;
import javax.jcr.Session;
import javax.jcr.SimpleCredentials;
import javax.jcr.Value;
import javax.jcr.ValueFactory;
import javax.jcr.nodetype.NodeType;
import javax.jcr.security.Privilege;

import org.apache.jackrabbit.commons.JcrUtils;
import org.apache.jackrabbit.commons.jackrabbit.authorization.AccessControlUtils;
import org.apache.jackrabbit.oak.jcr.Jcr;
import org.apache.jackrabbit.oak.spi.security.ConfigurationParameters;
import org.apache.jackrabbit.oak.spi.security.principal.EveryonePrincipal;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Petre Maierean
 *
 */
public class SimpleDerbyNodeTest extends BaseDocumentStoreTest {
	private Repository repo;
	private SimpleCredentials su;
	
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		logger.debug("Setup ");
		Jcr jcr = getBean(Jcr.class);
		repo = jcr.createRepository();
		su = getBean("adminUser", SimpleCredentials.class);
	}

	@Test
	public void testAddingANode() {
		Session session = null;
		try {
			session = repo.login(su);
			Node root = session.getRootNode();

	        Node data = getOrAddNode(root, "testdata");
	        addPropertyTestData(getOrAddNode(data, "property"));
	        addQueryTestData(getOrAddNode(data, "query"));
	        addNodeTestData(getOrAddNode(data, "node"));
	        if (session.getRepository().getDescriptorValue(Repository.OPTION_LIFECYCLE_SUPPORTED).getBoolean()) {
	            addLifecycleTestData(getOrAddNode(data, "lifecycle"));
	        }
	        addExportTestData(getOrAddNode(data, "docViewTest"));

/*	        if (session.getRepository().getDescriptorValue(Repository.OPTION_RETENTION_SUPPORTED).getBoolean()) {
	            Node conf = getOrAddNode(session.getRootNode(), "testconf");
	            addRetentionTestData(getOrAddNode(conf, "retentionTest"));
	        }
*/
	        AccessControlUtils.addAccessControlEntry(session, "/", EveryonePrincipal.getInstance(), new String[]{Privilege.JCR_READ}, true);
	        AccessControlUtils.addAccessControlEntry(session, "/jcr:system", EveryonePrincipal.getInstance(), new String[]{Privilege.JCR_READ}, false);
	        session.save();
	        logger.debug("Tested adding nodes have been successful");
		}
		catch(Exception e) {
			logger.error("Failed to login", e);
			fail("Cannot add nodes");
		}
		finally {
			if (session != null)
				try {
					session.logout();
				}
				catch(Exception e) {
					
				}
		}
	}
    
    protected ConfigurationParameters getSecurityConfigParameters() {
        return ConfigurationParameters.EMPTY;
    }
	
    private void addExportTestData(Node node) throws Exception {
        getOrAddNode(node, "invalidXmlName").setProperty("propName", "some text");

        // three nodes which should be serialized as xml text in docView export
        // separated with spaces
        getOrAddNode(node, "jcr:xmltext").setProperty("jcr:xmlcharacters", "A text without any special character.");
        getOrAddNode(node, "some-element");
        getOrAddNode(node, "jcr:xmltext").setProperty("jcr:xmlcharacters",
                " The entity reference characters: <, ', ,&, >,  \" should" + " be escaped in xml export. ");
        getOrAddNode(node, "some-element");
        getOrAddNode(node, "jcr:xmltext").setProperty("jcr:xmlcharacters", "A text without any special character.");

        Node big = getOrAddNode(node, "bigNode");
        big.setProperty("propName0", "SGVsbG8gd8O2cmxkLg==;SGVsbG8gd8O2cmxkLg==".split(";"), PropertyType.BINARY);
        big.setProperty("propName1", "text 1");
        big.setProperty("propName2", "multival text 1;multival text 2;multival text 3".split(";"));
        big.setProperty("propName3", "text 1");

        addExportValues(node, "propName");
        addExportValues(node, "Prop<>prop");
    }	

    private void addExportValues(Node node, String name) throws Exception {
        String prefix = "valid";
        if (name.indexOf('<') != -1) {
            prefix = "invalid";
        }
        node = getOrAddNode(node, prefix + "Names");

        String[] texts = new String[] { "multival text 1", "multival text 2", "multival text 3" };
        getOrAddNode(node, prefix + "MultiNoBin").setProperty(name, texts);

        Node resource = getOrAddNode(node, prefix + "MultiBin");
        resource.setProperty("jcr:encoding", ENCODING);
        resource.setProperty("jcr:mimeType", "text/plain");
        String[] values = new String[] { "SGVsbG8gd8O2cmxkLg==", "SGVsbG8gd8O2cmxkLg==" };
        resource.setProperty(name, values, PropertyType.BINARY);
        resource.setProperty("jcr:lastModified", Calendar.getInstance());

        getOrAddNode(node, prefix + "NoBin").setProperty(name, "text 1");

        resource = getOrAddNode(node, "invalidBin");
        resource.setProperty("jcr:encoding", ENCODING);
        resource.setProperty("jcr:mimeType", "text/plain");
        resource.setProperty(name, "Hello w\u00F6rld.", PropertyType.BINARY);
        resource.setProperty("jcr:lastModified", Calendar.getInstance());
    } 
    
    private void addLifecycleTestData(Node node) throws Exception {
        Node policy = getOrAddNode(node, "policy");
        policy.addMixin(NodeType.MIX_REFERENCEABLE);
        Node transitions = getOrAddNode(policy, "transitions");
        Node transition = getOrAddNode(transitions, "identity");
        transition.setProperty("from", "identity");
        transition.setProperty("to", "identity");
    }
	
    private void addNodeTestData(Node node) throws Exception {
        if (node.hasNode("multiReference")) {
            node.getNode("multiReference").remove();
        }
        if (node.hasNode("resReference")) {
            node.getNode("resReference").remove();
        }
        if (node.hasNode("myResource")) {
            node.getNode("myResource").remove();
        }

        Node resource = node.addNode("myResource", "nt:resource");
        // nt:resource not longer referenceable since JCR 2.0
        resource.addMixin("mix:referenceable");
        resource.setProperty("jcr:encoding", ENCODING);
        resource.setProperty("jcr:mimeType", "text/plain");
        resource.setProperty("jcr:data", "Hello w\u00F6rld.", PropertyType.BINARY);
        resource.setProperty("jcr:lastModified", Calendar.getInstance());

        Node resReference = getOrAddNode(node, "reference");
        resReference.setProperty("ref", resource);
        // make this node itself referenceable
        resReference.addMixin("mix:referenceable");

        Node multiReference = node.addNode("multiReference");
        ValueFactory factory = node.getSession().getValueFactory();
        multiReference.setProperty("ref", new Value[] {
            factory.createValue(resource),
            factory.createValue(resReference)
        });

        // NodeDefTest requires a test node with a mandatory child node
        JcrUtils.putFile(node, "testFile", "text/plain", new ByteArrayInputStream("Hello, World!".getBytes("UTF-8")));
    }
	
	
    private void addPropertyTestData(Node node) throws Exception {
        node.setProperty("boolean", true);
        node.setProperty("double", Math.PI);
        node.setProperty("long", 90834953485278298L);
        Calendar c = Calendar.getInstance();
        c.set(2005, 6, 18, 17, 30);
        node.setProperty("calendar", c);
        ValueFactory factory = node.getSession().getValueFactory();
        node.setProperty("path", factory.createValue("/", PropertyType.PATH));
        node.setProperty("multi", new String[] { "one", "two", "three" });
    }
	
    private void addQueryTestData(Node node) throws Exception {
        while (node.hasNode("node1")) {
            node.getNode("node1").remove();
        }
        getOrAddNode(node, "node1").setProperty("prop1", "You can have it good, cheap, or fast. Any two.");
        getOrAddNode(node, "node1").setProperty("prop1", "foo bar");
        getOrAddNode(node, "node1").setProperty("prop1", "Hello world!");
        getOrAddNode(node, "node2").setProperty("prop1", "Apache Jackrabbit");
    }

	
    private Node getOrAddNode(Node node, String name) throws Exception {
        try {
            return node.getNode(name);
        } catch (PathNotFoundException e) {
            return node.addNode(name);
        }
    }
}
