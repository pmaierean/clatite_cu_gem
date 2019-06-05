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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.maiereni.ea.utils.DiagramProcessor;
import com.maiereni.ea.utils.EAClient;
import com.maiereni.ea.utils.EAClientFactory;
import com.maiereni.ea.utils.ElementProcessor;
import com.maiereni.ea.utils.ModelProcessor;
import com.maiereni.ea.utils.PackageProcessor;
import com.maiereni.ea.utils.bo.ConnectorDescription;
import com.maiereni.ea.utils.bo.DiagramDescription;
import com.maiereni.ea.utils.bo.ElementDescription;
import com.maiereni.ea.utils.bo.PackageDescription;
import com.maiereni.osgi.felix.utils.bo.BundleMapping;
import com.maiereni.osgi.felix.utils.bo.ComponentMapping;
import com.maiereni.osgi.felix.utils.bo.ServiceMapping;
import com.maiereni.sling.info.Bundle;
import com.maiereni.sling.info.Component;
import com.maiereni.sling.info.Components;
import com.maiereni.sling.info.ImportPackage;
import com.maiereni.sling.info.Imports;
import com.maiereni.sling.info.Model;
import com.maiereni.sling.info.Reference;
import com.maiereni.sling.info.Service;
import com.maiereni.sling.info.Services;

/**
 * Utility class that writes a model into Enterprise Architect for display purpose
 * @author Petre Maierean
 *
 */
class EASlingModelWriter extends ModelLookup {
	
	public EASlingModelWriter(Model model) {
		super(model);
	}

	private static final Logger logger = LoggerFactory.getLogger(EASlingModelWriter.class);
	public static final String MODEL_NAME = "Model";

	
	/**
	 * Write a sling model into EA
	 * @param eaFile the output file
	 * @throws Exception
	 */
	public void writetoEA(final String eaFile) throws Exception {
		logger.debug("Write model to Enterpise Architect");
		EAClientFactory clientFactory = new EAClientFactory();
		
		try(EAClient client = clientFactory.getClient(eaFile)) {
			ModelProcessor modelProcessor = null;
			if (client.isModel(MODEL_NAME)) {
				modelProcessor = client.getModel(MODEL_NAME);
			}
			else {
				modelProcessor = client.addModel(MODEL_NAME);
			}
			PackageProcessor packageArchitecture = null;
			if (modelProcessor.hasPackage("Component Model")) {
				packageArchitecture = modelProcessor.getPackage("Component Model");
				logger.debug("The main package has been found");
			}
			else {
				packageArchitecture = modelProcessor.addPackage("Component Model");
				logger.debug("Create main package");
			}
			PackageProcessor sling = addOrGet(packageArchitecture, "Sling");
			writePackages(model, sling);
		}
		logger.debug("Done writing the model to Enterpise Architect");
	}
	
	private void writePackages(final Model model, final PackageProcessor proc) throws Exception {
		PackageProcessor osgiPackages = addOrGet(proc, "osgi");
		PackageProcessor jcrPackages = addOrGet(proc, "jcr");
		PackageProcessor slingPackages = addOrGet(proc, "sling");
		PackageProcessor otherPackages = addOrGet(proc, "other");
		
		Map<String, BundleMapping> ret = new HashMap<String, BundleMapping>();

		ret.putAll(writeOthers(model, otherPackages));
		ret.putAll(writeOSGI(model, osgiPackages));
		ret.putAll(writeJCR(model, jcrPackages));
		ret.putAll(writeSling(model, slingPackages));

		addConnectors(jcrPackages, model, ret, "jcr");
		addConnectors(slingPackages, model, ret, "sling");
		addConnectors(osgiPackages, model, ret, "osgi");
		addConnectors(otherPackages, model, ret, "other");

		addDiagrams(jcrPackages, model, ret, "jcr");
		addDiagrams(slingPackages, model, ret, "sling");
		addDiagrams(osgiPackages, model, ret, "osgi");
		addDiagrams(otherPackages, model, ret, "other");
		
		DiagramProcessor diagramProcessor = addOrGet(proc, "Sling", DiagramDescription.Type.COMPONENT);
		diagramProcessor.addPackage(slingPackages.getPackageDescription());
		diagramProcessor.addPackage(osgiPackages.getPackageDescription());
		diagramProcessor.addPackage(jcrPackages.getPackageDescription());
		diagramProcessor.addPackage(otherPackages.getPackageDescription());
	}

	private void addConnectors(final PackageProcessor proc, final Model model, final Map<String, BundleMapping> refs, final String groupName) throws Exception {
		for(Bundle bundle: model.getBundles()) {
			if (bundle.getGroup().equals(groupName)) {
				PackageProcessor procBundle = proc.getPackage(bundle.getName());
				addConnectors(procBundle, bundle, refs);
			}
		}
	}
	
	private void addConnectors(final PackageProcessor proc, final Bundle bundle, final Map<String, BundleMapping> refs) throws Exception {
		logger.debug("Add the connectors for bundle: " + bundle.getName());
		Imports imports = bundle.getImportPackages();
		if (!(imports == null || imports.getPkg() == null)) {
			for(ImportPackage pkg: imports.getPkg()) {
				String ref = pkg.getBundleId();
				if (StringUtils.isNotBlank(ref)) {
					BundleMapping depMapping = refs.get(ref);
					if (depMapping == null) {
						logger.error("The reference id=" + ref + "' cannot be resolved");
						continue;
					}
					if (!proc.hasConnector(depMapping.getGuid(), ConnectorDescription.Types.DEPENDENCY)) {
						logger.debug("Add connecctor to package " + depMapping.getBundleName());
						proc.addConnector("import package", depMapping.getGuid(), ConnectorDescription.Types.DEPENDENCY);
					}
				}
			}
		}
	}
	
	private void addDiagrams(final PackageProcessor proc, final Model model, final Map<String, BundleMapping> refs, final String groupName) throws Exception {
		for(Bundle bundle: model.getBundles()) {
			if (bundle.getGroup().equals(groupName)) {
				PackageProcessor procBundle = proc.getPackage(bundle.getName());
				addDiagrams(procBundle, bundle, refs);
			}
		}
	}	
	
	private void addDiagrams(final PackageProcessor proc, final Bundle bundle, final Map<String, BundleMapping> refs) throws Exception {
		logger.debug("Add the diagram for bundle: " + bundle.getName());
		DiagramProcessor diagram = null;
		if (proc.hasDiagram(bundle.getName())) {
			diagram = proc.getDiagram(bundle.getName());
		}
		else {
			diagram = proc.addDiagram(bundle.getName(), DiagramDescription.Type.COMPONENT);
		}
		PackageDescription pkgD = proc.getPackageDescription();
		if (!diagram.hasPackage(pkgD)) {
			diagram.addPackage(pkgD);
		}
		
		addComponentsToDiagram(proc, bundle, refs, diagram);
		addImportsToDiagram(proc, bundle, refs, diagram);
	}
	
	private void addComponentsToDiagram(final PackageProcessor proc, final Bundle bundle, final Map<String, BundleMapping> refs, final DiagramProcessor diagram) throws Exception {
		logger.debug("Add components to diagram");
		if (bundle.getComponents() != null && bundle.getComponents().getComponent() != null) {
			BundleMapping mapping = refs.get(bundle.getId());
			for(Component component: bundle.getComponents().getComponent()) {
				ComponentMapping cm = getComponentMapping(mapping, component.getId());
				ElementProcessor ep = proc.getElement(cm.getName());
				diagram.addElement(ep.getDescription());
				addComponentRefrencesToDiagram(proc, refs, diagram, component, ep);
			}
		}
	}
	
	private void addComponentRefrencesToDiagram(
		final PackageProcessor proc, 
		final Map<String, BundleMapping> refs, 
		final DiagramProcessor diagram, 
		final Component component,
		final ElementProcessor ep) throws Exception {
		if (component.getReference() != null) {
			for(Reference ref: component.getReference()) {
				Component refComponent = getComponentByServiceName(ref.getAttrName());
				if (refComponent == null) {
					Bundle refBundle = getBundleByServiceName(ref.getAttrName());
					if (refBundle != null) {
						addReferencePackageToDiagram(proc, refs, diagram, refBundle, ep);
					}
					else {
						logger.error("Cannot resolve reference service " + ref.getAttrName() + " to a component");
					}
				}
				else {
					addReferenceComponentToDiagram(proc, refs, diagram, refComponent, ep);
				}
			}
		}
	}
	
	private void addReferenceComponentToDiagram(
		final PackageProcessor proc, 
		final Map<String, BundleMapping> refs,
		final DiagramProcessor diagram, 
		final Component refComponent,
		final ElementProcessor ep) throws Exception {
		Bundle refBundle = getBundleByComponentId(refComponent.getId());
		BundleMapping refBundleMapping = refs.get(refBundle.getId());
		PackageProcessor pRef = proc.findPackage(refBundleMapping.getGuid());
		if (pRef != null) {
			PackageDescription pRefDesc = pRef.getPackageDescription();
			if (!diagram.hasPackage(pRefDesc)) {
				logger.debug("Reference package " + refBundle.getName() + " to the diagram");
				diagram.addPackage(pRefDesc);
			}
			ComponentMapping refCompMapping = getComponentMapping(refBundleMapping, refComponent.getId());
			ElementProcessor refElementProc = pRef.getElement(refCompMapping.getName());
			if (refElementProc == null) {
				try {
					int elementId = Integer.parseInt(refCompMapping.getComponentId());
					refElementProc = pRef.getElementById(elementId);
				}
				catch(Exception e) {
					logger.error("Failed to parse string '" + refCompMapping.getComponentId() + "' to integer");
				}
			}
			if (refElementProc == null) {
				logger.error("Cannot find an element for component: " + refCompMapping.getName() + " in package " + pRefDesc.getName());
			}
			else {
				ElementDescription refElementDesc = refElementProc.getDescription();
				if (!ep.hasConnector(refElementDesc, ConnectorDescription.Types.ASSOCIATION)) {
					logger.debug("Add connector to component " + refComponent.getName());
					ep.addConnector(refElementDesc, "references", ConnectorDescription.Types.ASSOCIATION);
				}
				if (!diagram.hasElement(refElementDesc)) {
					logger.debug("Add component " + refComponent.getName() + " to diagram ");
					diagram.addElement(refElementDesc);
				}
			}
		}
		else {
			logger.error("Cannot find package for guid " + refBundleMapping.getGuid());							
		}
		
	}
	
	private void addReferencePackageToDiagram(
		final PackageProcessor proc, 
		final Map<String, BundleMapping> refs,
		final DiagramProcessor diagram, 
		final Bundle refBundle,
		final ElementProcessor ep) throws Exception {
		BundleMapping refBundleMapping = refs.get(refBundle.getId());
		PackageProcessor pRef = proc.findPackage(refBundleMapping.getGuid());
		if (pRef != null) {
			PackageDescription pRefDesc = pRef.getPackageDescription();
			if (!diagram.hasPackage(pRefDesc)) {
				logger.debug("Reference package " + refBundle.getName() + " to the diagram");
				diagram.addPackage(pRefDesc);
			}

			ComponentMapping refCompMapping = getComponentMappingByName(refBundleMapping, "generic");
			ElementProcessor refElementProc = pRef.getElement(refCompMapping.getName());
			if (refElementProc == null) {
				try {
					int elementId = Integer.parseInt(refCompMapping.getComponentId());
					refElementProc = pRef.getElementById(elementId);
				}
				catch(Exception e) {
					logger.error("Failed to parse string '" + refCompMapping.getComponentId() + "' to integer");
				}
			}
			if (refElementProc == null) {
				logger.error("Cannot find an element for component: " + refCompMapping.getName() + " in package " + pRefDesc.getName());
			}
			else {
				ElementDescription refElementDesc = refElementProc.getDescription();
				if (!ep.hasConnector(refElementDesc, ConnectorDescription.Types.ASSOCIATION)) {
					logger.debug("Add connector to component 'generic'");
					ep.addConnector(refElementDesc, "references", ConnectorDescription.Types.ASSOCIATION);
				}
				if (!diagram.hasElement(refElementDesc)) {
					logger.debug("Add component 'generic' to diagram ");
					diagram.addElement(refElementDesc);
				}
			}
		
		}
	}

	private ComponentMapping getComponentMapping(final BundleMapping mapping, final String componentId) {
		ComponentMapping ret = null;
		for(ComponentMapping cm : mapping.getComponents()) {
			if (cm.getComponentId().equals(componentId)) {
				ret = cm;
				break;
			}
		}
		return ret;
	}

	private ComponentMapping getComponentMappingByName(final BundleMapping mapping, final String componentName) {
		ComponentMapping ret = null;
		for(ComponentMapping cm : mapping.getComponents()) {
			if (cm.getName().equals(componentName)) {
				ret = cm;
				break;
			}
		}
		return ret;
	}

	
	private void addImportsToDiagram(final PackageProcessor proc, final Bundle bundle, final Map<String, BundleMapping> refs, final DiagramProcessor diagram) throws Exception {
		logger.debug("Add imports to diagram");
		Imports imports = bundle.getImportPackages();
		if (!(imports == null || imports.getPkg() == null)) {
			for(ImportPackage pkg: imports.getPkg()) {
				String ref = pkg.getBundleId();
				if (StringUtils.isNotBlank(ref)) {
					BundleMapping depMapping = refs.get(ref);
					if (depMapping == null) {
						logger.error("The reference id=" + ref + "' cannot be resolved");
						continue;
					}
					PackageProcessor pImports = proc.findPackage(depMapping.getGuid());
					if (pImports != null) {
						PackageDescription iPkgd = pImports.getPackageDescription();
						if (!diagram.hasPackage(iPkgd)) {
							diagram.addPackage(iPkgd);
						}
					}
					else {
						logger.error("Cannot find package for guid " + depMapping.getGuid());
					}
				}
			}
		}
	}
	
	private Map<String, BundleMapping> writeOthers(final Model model, final PackageProcessor p) throws Exception {
		return write(model, p, BundleGroupQualifier.OTHER);
	}

	private Map<String, BundleMapping> writeOSGI(final Model model, final PackageProcessor p) throws Exception {
		return write(model, p, BundleGroupQualifier.FELIX);		
	}

	private Map<String, BundleMapping> writeJCR(final Model model, final PackageProcessor p) throws Exception {
		return write(model, p, BundleGroupQualifier.JCR);	
	}

	private Map<String, BundleMapping> writeSling(final Model model, final PackageProcessor p) throws Exception {
		return write(model, p, BundleGroupQualifier.SLING);		
	}
	
	private Map<String, BundleMapping> write(final Model model, final PackageProcessor p, final String name) throws Exception {
		Map<String, BundleMapping> ret = new HashMap<String, BundleMapping>();
		for(Bundle bundle: model.getBundles()) {
			if (bundle.getGroup().equals(name)) {
				PackageProcessor pp = addOrGet(p, bundle.getName());
				pp.addNote(bundle.getDescription());
				BundleMapping bundleMapping = new BundleMapping();
				bundleMapping.setBundleName(bundle.getName());
				bundleMapping.setGuid(pp.getPackageDescription().getGuid());
				List<ComponentMapping> componentMappings = addComponents(bundle.getComponents(), pp);
				bundleMapping.setComponents(componentMappings);
				List<ServiceMapping> serviceMappings = addServices(bundle.getServices(), pp);
				ElementProcessor gEl = pp.getElement("generic");
				if (gEl != null) {
					ElementDescription desc = gEl.getDescription();
					ComponentMapping generic = new ComponentMapping();
					generic.setName("generic");
					generic.setGuid(desc.getUuid());
					generic.setComponentId(desc.getId().toString());
					componentMappings.add(generic);
				}
				bundleMapping.setServices(serviceMappings);
				ret.put(bundle.getId(), bundleMapping);
				logger.debug("The bundle '{}' has been created", bundle.getName());
			}
		}
		return ret;
	}

	private List<ComponentMapping> addComponents(final Components components, final PackageProcessor proc) throws Exception {
		List<ComponentMapping> ret = new ArrayList<ComponentMapping>();
		if (!(components == null || components.getComponent() == null)) {
			for(Component component: components.getComponent()) {
				ElementProcessor proc1 = addOrGet(proc, component.getName(), ElementDescription.TYPE_COMPONENT);
				ComponentMapping componentMapping = new ComponentMapping();
				componentMapping.setGuid(proc1.getDescription().getUuid());
				componentMapping.setName(component.getName());
				componentMapping.setComponentId(component.getId());
				List<ServiceMapping> serviceMappings = addServices(component.getServices(), proc1);
				componentMapping.setServices(serviceMappings);
				ret.add(componentMapping);
			}
		}
		return ret;
	}
	
	private List<ServiceMapping> addServices(final Services services, final ElementProcessor proc) throws Exception {
		List<ServiceMapping> ret = new ArrayList<ServiceMapping>();
		if (!( services == null || services.getService() == null)) {
			for(Service service: services.getService()) {
				if (StringUtils.isNotBlank(service.getName())) {
					ElementProcessor sproc = addOrGet(proc, service.getName(), ElementDescription.TYPE_PORT);
					if (StringUtils.isNotBlank(service.getDescription())) {
						sproc.setNote(service.getDescription());
					}
					else {
						sproc.setNote(service.getName());
					}
					ServiceMapping serviceMapping = new ServiceMapping();
					serviceMapping.setGuid(sproc.getDescription().getUuid());
					serviceMapping.setName(service.getName());
					ret.add(serviceMapping);
				}
			}
		}
		return ret;
	}

	private List<ServiceMapping> addServices(final Services services, final PackageProcessor proc) throws Exception {
		List<ServiceMapping> ret = new ArrayList<ServiceMapping>();
		if (!(services == null || services.getService() == null)) {
			ElementProcessor proc1 = addOrGet(proc, "generic", ElementDescription.TYPE_COMPONENT);
			ret.addAll(addServices(services, proc1));
		}
		return ret;
	}

	private PackageProcessor addOrGet(final PackageProcessor proc, final String name) throws Exception {
		PackageProcessor ret = null;
		if (proc.hasPackage(name))
			ret = proc.getPackage(name);
		else
			ret = proc.addPackage(name);
		return ret;
	}

	private ElementProcessor addOrGet(final PackageProcessor proc, final String name, final String type) throws Exception {
		ElementProcessor ret = null;
		if (proc.hasElement(name)) {
			ret = proc.getElement(name);
			if (!ret.getDescription().getType().equals(type)) {
				proc.deleteElement(name);
				ret = null;
			}
		}
		if (ret == null) {
			ret = proc.addElement(name, type);
		}
		
		return ret;
	}
	
	private ElementProcessor addOrGet(final ElementProcessor proc, final String name, final String type) throws Exception {
		ElementProcessor ret = null;
		if (proc.hasElement(name)) {
			ret = proc.getElement(name);
			if (!ret.getDescription().getType().equals(type)) {
				proc.deleteElement(name);
				ret = null;
			}
		}
		if (ret == null) {
			ret = proc.addElement(name, type);
		}
		
		return ret;
	}
	
	private DiagramProcessor addOrGet(final PackageProcessor proc, final String name, final DiagramDescription.Type type) throws Exception {
		DiagramProcessor ret = null;
		if (proc.hasDiagram(name)) {
			ret = proc.getDiagram(name);
			if (!ret.getDiagramDescription().getType().equals(type.toString())) {
				proc.deleteDiagram(name);
				ret = null;
			}
		}
		if (ret == null) {
			ret = proc.addDiagram(name, type);
		}
		return ret;
	}
}
