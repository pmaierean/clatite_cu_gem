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

import com.maiereni.osgi.felix.utils.bo.Bundle;
import com.maiereni.osgi.felix.utils.bo.Component;
import com.maiereni.osgi.felix.utils.bo.Property;
import com.maiereni.osgi.felix.utils.bo.Service;
import com.maiereni.osgi.felix.utils.bo.SlingInfo;
import com.maiereni.sling.info.BundleRef;
import com.maiereni.sling.info.BundleRefs;
import com.maiereni.sling.info.Components;
import com.maiereni.sling.info.Exports;
import com.maiereni.sling.info.Imports;
import com.maiereni.sling.info.Model;
import com.maiereni.sling.info.ObjectFactory;
import com.maiereni.sling.info.Properties;
import com.maiereni.sling.info.Services;

/**
 * @author Petre Maierean
 *
 */
public abstract class BaseModelHandler {
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
				ic.put(cp.getId(), cp);
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
				types = types.substring(1, types.length() - 2);			
			}
			String[] toks = types.split(",");
			for(String tok: toks) {
				ret.getType().add(tok);
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

	
	private com.maiereni.sling.info.Component convert(final Component component) {
		com.maiereni.sling.info.Component ret = new com.maiereni.sling.info.Component();
		ret.setId(component.getId());
		ret.setName(component.getName());
		ret.setState("" + component.getStateRaw());
		if  (component.getProps() != null) {
			for(Property p: component.getProps()) {
				String key = p.getKey();
				if (key.equals("Implementation Class")) {
					String txt = p.getValue().toString();
					ret.setImplementationClass(txt);
				}
				else {
					com.maiereni.sling.info.Property prop = convert(p);
					Properties properties = ret.getProperties();
					if (properties == null) {
						properties = factory.createProperties();
						ret.setProperties(properties);
					}
					properties.getProperty().add(prop);
				}
			}			
		}
		return ret;
	}
	
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
						imports.getPkg().addAll(arr);
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
			}
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

	private com.maiereni.sling.info.Property convert(Property p) {
		 com.maiereni.sling.info.Property ret = new  com.maiereni.sling.info.Property();
		 ret.setKey(p.getKey());
		 ret.setValue(p.getValue().toString());
		 return ret;
	}
}
