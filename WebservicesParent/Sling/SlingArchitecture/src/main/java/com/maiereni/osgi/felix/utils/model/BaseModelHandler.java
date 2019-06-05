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
package com.maiereni.osgi.felix.utils.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.maiereni.osgi.felix.utils.bo.Bundle;
import com.maiereni.osgi.felix.utils.bo.Component;
import com.maiereni.osgi.felix.utils.bo.Property;
import com.maiereni.osgi.felix.utils.bo.Service;
import com.maiereni.osgi.felix.utils.bo.SlingInfo;
import com.maiereni.sling.info.BundleRef;
import com.maiereni.sling.info.BundleRefs;
import com.maiereni.sling.info.Components;
import com.maiereni.sling.info.Exports;
import com.maiereni.sling.info.ImportPackage;
import com.maiereni.sling.info.Imports;
import com.maiereni.sling.info.Model;
import com.maiereni.sling.info.ObjectFactory;
import com.maiereni.sling.info.Properties;
import com.maiereni.sling.info.Reference;
import com.maiereni.sling.info.Services;

/**
 * @author Petre Maierean
 *
 */
public abstract class BaseModelHandler {
	private static final Logger logger = LoggerFactory.getLogger(BaseModelHandler.class);
	private Marshaller marshaller;
	private Unmarshaller unmarshaller;
	private ObjectFactory factory;
	
	public BaseModelHandler() throws Exception {
		JAXBContext jaxbCtx = javax.xml.bind.JAXBContext.newInstance("com.maiereni.sling.info");
		marshaller = jaxbCtx.createMarshaller();
		unmarshaller = jaxbCtx.createUnmarshaller();
		factory = new ObjectFactory();
		//mapper = new ObjectMapper();
	}
	
	public Model convert(final SlingInfo slingInfo) throws Exception {
		Model m = factory.createModel();
		if (!(slingInfo == null || slingInfo.getBundles() == null)) {
			Map<String, com.maiereni.sling.info.Bundle> ib = new HashMap<String, com.maiereni.sling.info.Bundle> ();
			for(Bundle b: slingInfo.getBundles()) {
				com.maiereni.sling.info.Bundle bundle = convert(b);
				m.getBundles().add(bundle);
				ib.put(bundle.getId(), bundle);
			}			
			Map<String, com.maiereni.sling.info.Component> ic = new HashMap<String, com.maiereni.sling.info.Component>();
			for(Component c: slingInfo.getComponent()) {
				Integer bundleId = c.getBundleId();
				com.maiereni.sling.info.Component cp = convert(c);
				if (ic.containsKey(cp.getId())) {
					if (cp.getServices() != null && cp.getServices().getService() != null) {
						ic.get(cp.getId()).setServices(cp.getServices());
					}
					continue;
				}
				ic.put(cp.getId(), cp);
				com.maiereni.sling.info.Bundle bundle = ib.get(bundleId.toString());
				if (bundle == null) { 
					throw new Exception("The bundleId cannot found: " + bundleId);
				}
				Components components = bundle.getComponents();
				if (components == null) {
					components = factory.createComponents();
					bundle.setComponents(components);
				}
				components.getComponent().add(cp);
			}
			for(Service s: slingInfo.getServices()) {
				com.maiereni.sling.info.Service service = convert(s);
				String cid = getComponentId(s);
				if (cid != null) {
					com.maiereni.sling.info.Component comp = ic.get(cid);
					if (comp == null) {
						throw new Exception("The component cannot be found for id " + cid);
					}
					Services services = comp.getServices();
					if (services == null) {
						services = factory.createServices();
						comp.setServices(services);;
					}
					services.getService().add(service);
				}
				else {
					String bid = getBundleId(s);
					com.maiereni.sling.info.Bundle bundle = ib.get(bid);
					if (bundle == null) { 
						throw new Exception("The bundleId cannot found: " + bid);
					}
					Services services = bundle.getServices();
					if (services == null) {
						services = factory.createServices();
						bundle.setServices(services);
					}
					services.getService().add(service);
				}
			}
		}
		Collections.sort(m.getBundles(), new Comparator<com.maiereni.sling.info.Bundle>() {
				@Override
				public int compare(com.maiereni.sling.info.Bundle o1, com.maiereni.sling.info.Bundle o2) {
					int ret = -1;
					if (Integer.parseInt(o1.getId()) > Integer.parseInt(o2.getId())) {
						ret = 1;
					}
					return ret;
				}
			}
		);
 		return m;
	}
	
	public SlingInfo convert(final Model model) {
		SlingInfo info = new SlingInfo();
		
		return info;
	}
	
	public Marshaller getMarshaller() {
		return marshaller;
	}
	
	public Unmarshaller getUnmarshaller() {
		return unmarshaller;
	}

	public ObjectFactory getFactory() {
		return factory;
	}

	private com.maiereni.sling.info.Service convert(final Service service) {
		com.maiereni.sling.info.Service ret = new com.maiereni.sling.info.Service();
		ret.setId(service.getId());
		if (service.getProps() != null) {
			for(Property p: service.getProps()) {
				String key = p.getKey();
				if (key.equals("Service Description")) {
					String description = (String)p.getValue();
					ret.setDescription(description);
				}
				else if (key.equals("Service PID")) {
					String name = (String)p.getValue();
					ret.setName(name);
				}
				else if (key.equals("service.scope")) {
					String s = (String)p.getValue();
					ret.setScope(s);
				}
				else if (key.equals("service.bundleid")) {
					
				}
				else {
					Properties props = ret.getProperties();
					if (props == null) {
						props = factory.createProperties();
						ret.setProperties(props);
					}
					com.maiereni.sling.info.Property prop = factory.createProperty();
					prop.setKey(key);
					String value = p.getValue().toString();
					prop.setValue(value);
					props.getProperty().add(prop);
				}
			}
		}
		String types = service.getTypes();
		if (StringUtils.isNotBlank(types)) {
			if (types.startsWith("[") && types.endsWith("]")) {
				types = types.substring(1, types.length() - 1);			
			}
			String[] toks = types.split(",");
			for(String tok: toks) {
				ret.getType().add(tok.trim());
			}			
		}
		return ret;
	}
	
	private String getComponentId(final Service service) {
		String ret = null;
		if (service.getProps() != null) {
			for(Property p: service.getProps()) {
				String key = p.getKey();
				if (key.equals("component.id")) {
					Object o = p.getValue();
					ret = o.toString();
					break;
				}
				
			}
		}
		return ret;
	}

	private String getBundleId(final Service service) {
		String ret = null;
		if (service.getProps() != null) {
			for(Property p: service.getProps()) {
				String key = p.getKey();
				if (key.equals("service.bundleid")) {
					Object o = p.getValue();
					ret = o.toString();
					break;
				}		
			}
		}
		return ret;
	}

	private static final String REFERENCE = "Reference ";
	private static final String IMPLEMENTATION_CLASS = "Implementation Class";
	private static final String PROPERTIES = "Properties";
	@SuppressWarnings("unchecked")
	private com.maiereni.sling.info.Component convert(final Component component) {
		com.maiereni.sling.info.Component ret = new com.maiereni.sling.info.Component();
		ret.setId(component.getId());
		ret.setName(component.getName());
		ret.setState("" + component.getStateRaw());
		if  (component.getProps() != null) {
			for(Property p: component.getProps()) {
				String key = p.getKey();
				if (key.equals(IMPLEMENTATION_CLASS)) {
					String txt = p.getValue().toString();
					ret.setImplementationClass(txt);
				}
				else if (key.startsWith(REFERENCE)) {
					Reference ref = convertReference(p);
					ret.getReference().add(ref);
				}
				else if (key.startsWith(PROPERTIES)) {
					Object val = p.getValue();
					Properties properties = ret.getProperties();
					if (properties == null) {
						properties = factory.createProperties();
						ret.setProperties(properties);
					}
					if (val instanceof ArrayList) {
						List<Object> vals = (List<Object>)val;
						for(Object o : vals) {
							if (o instanceof String) {
								String s = (String)o;
								String[] toks = s.split("=");
								com.maiereni.sling.info.Property prop = new com.maiereni.sling.info.Property();
								prop.setKey(toks[0].trim());
								prop.setValue(toks[1].trim());
								properties.getProperty().add(prop);
							}
						}
					}
					else if (val instanceof String) {
						String s = (String)val;
						String[] toks = s.split("=");
						com.maiereni.sling.info.Property prop = new com.maiereni.sling.info.Property();
						prop.setKey(toks[0]);
						prop.setValue(toks[1]);
						properties.getProperty().add(prop);						
					}
					else {
						logger.error("Unknown property type " + val.getClass().getName() + " for " + val);
					}
				}
				else {
					com.maiereni.sling.info.Attribute attr = new com.maiereni.sling.info.Attribute();
					attr.setKey(key);
					attr.setValue(p.getValue().toString());
					ret.getAttribute().add(attr);
				}
			}			
		}
		return ret;
	}
	
	private static final String SERVICE_NAME = "Service Name: ";
	private static final String BOUND_SERVICE = "Bound Service ID ";
	private static final String SATISFIED = "Satisfied";
	private static final String TARGET_FILTER = "Target Filter: ";
	private static final String CARDINALITY = "Cardinality: ";
	private static final String POLICY = "Policy: ";
	private static final String POLICY_OPTION = "Policy Option: ";
	private static final String UNSATISFIED = "Unsatisfied";
	private static final String NO_SERVICE = "No Services bound";
	private Reference convertReference(final Property p) {
		Reference ret = new Reference();
		String refName = p.getKey().substring(REFERENCE.length());
		ret.setName(refName);
		Object val = p.getValue();
		if (val instanceof ArrayList) {
			@SuppressWarnings("unchecked")
			List<Object> os = (List<Object>) val;
			for(Object o: os) {
				String tok = o.toString();
				if (tok.startsWith(SERVICE_NAME)) {
					ret.setAttrName(tok.substring(SERVICE_NAME.length()).trim());
				}
				else if (tok.startsWith(BOUND_SERVICE)) {
					String s = tok.substring(BOUND_SERVICE.length()).trim();
					String[] sp = s.split(" ");
					if (sp.length > 1) {
						ret.setClassName(sp[1]);
					}
					ret.setServiceId(sp[0]);
				}
				else if (tok.startsWith(SATISFIED) || tok.startsWith(UNSATISFIED) || tok.startsWith(NO_SERVICE)) {
					ret.setStatus(tok);;
				}
				else if (tok.startsWith(TARGET_FILTER)) {
					ret.getTargetFilter().add(tok.substring(TARGET_FILTER.length()).trim());
				}
				else if (tok.startsWith(CARDINALITY)) {
					ret.setCardinality(tok.substring(CARDINALITY.length()).trim());
				}
				else if (tok.startsWith(POLICY)) {
					ret.setPolicy(tok.substring(POLICY.length()).trim());
				}
				else if (tok.startsWith(POLICY_OPTION)) {
					ret.setPolicyOption(tok.substring(POLICY_OPTION.length()).trim());
				}
				else {
					logger.error("Unknown token in a reference: " + tok);
				}
			}
		}
		return ret;
	}
	
	private static final String BUNDLE_VERSION = "Bundle-Version: ";
	
	@SuppressWarnings("unchecked")
	private com.maiereni.sling.info.Bundle convert(final Bundle bundle) {
		com.maiereni.sling.info.Bundle ret = new com.maiereni.sling.info.Bundle();
		ret.setId(bundle.getId());
		ret.setName(bundle.getSymbolicName());
		if (bundle.getProps() != null) {
			for(Property p: bundle.getProps()) {
				String key = p.getKey();
				if (key.equals("Description")) {
					String txt = p.getValue().toString();
					ret.setDescription(txt);
				}
				else if (key.equals("Symbolic Name")) {
					String txt = p.getValue().toString();
					ret.setName(txt);
				}
				else if (key.equals("Start Level")) {
					Integer i = (Integer)p.getValue();
					ret.setStartLevel(i);
				}
				else if (key.equals("Exported Packages")) {
					List<String> arr = null;
					Object o = p.getValue();
					if (o instanceof List)
						arr = (List<String>)p.getValue();
					else if (o instanceof String) {
						String s1 = (String) o;
						arr = new ArrayList<String>();
						arr.add(s1);
					}
					if (arr != null) {
						Exports exports = ret.getExportPackages();
						if (exports == null) {
							exports = factory.createExports();
							ret.setExportPackages(exports);
						}
						exports.getPkg().addAll(arr);
					}
				}
				else if (key.equals("Imported Packages")) {
					List<String> arr = (List<String>)p.getValue();
					if (arr != null) {
						Imports imports = ret.getImportPackages();
						if (imports == null) {
							imports = factory.createImports();
							ret.setImportPackages(imports);
						}
						for(String ar: arr) {
							ImportPackage importPackage = parse(ar);
							imports.getPkg().add(importPackage);
						}
						//imports.getPkg().addAll(arr);
					}
				}
				else if (key.equals("Importing Bundles")) {
					List<String> arr = (List<String>)p.getValue();
					if (arr != null) {
						for(String s: arr) {
							BundleRef ref = getBundleRef(s);
							BundleRefs bundleRefs = ret.getImports();
							if (bundleRefs == null) {
								bundleRefs = factory.createBundleRefs();
								ret.setImports(bundleRefs);
							}
							bundleRefs.getBundleRef().add(ref);
						}
					}
				}
				else if (key.equals("Manifest Headers")) {
					List<String> arr = (List<String>)p.getValue();
					if (arr != null) {
						for(String s: arr) {
							if (s.startsWith(BUNDLE_VERSION)) {
								String bv = s.substring(BUNDLE_VERSION.length());
								ret.setVersion(bv);
							}
						}
					}
				}
			}
		}
		return ret;
	}
	
	private static final String FROM = " from ";
	private static final String BUNDLE = "<a href='/system/console/bundles/";
	private ImportPackage parse(final String s) {
		ImportPackage ret = new ImportPackage();
		int ix = s.indexOf(FROM);
		if (ix > 0) {
			ret.setValue(s.substring(0,  ix).trim());
			ix = s.indexOf(BUNDLE);
			if (ix > 0) {
				ix = ix + BUNDLE.length();
				int iy = s.indexOf("'>", ix);
				String bundleId = s.substring(ix, iy);
				ret.setBundleId(bundleId);
				iy += 2;
				ix = s.indexOf("(" + bundleId + ")", iy);
				if (ix > iy) {
					String name = s.substring(iy, ix).trim();
					ret.setName(name);
				}
			}
		}
		else {
			ret.setValue(s);
		}
		
		return ret;
	}
	
	private BundleRef getBundleRef(final String s) {
		BundleRef ret = new BundleRef();
		int i = s.indexOf(">");
		if (i > 0) {
			int j = s.indexOf("<", i+1);
			String s1 = s.substring(i+1, j);
			String[] toks = s1.split(" ");
			ret.setName(toks[0]);
			ret.setId(toks[1].substring(1, toks[1].length() - 1));
		}
		return ret;
	}
}
