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
package com.maiereni.sample.sparx;

import java.io.File;

import org.sparx.Collection;
import org.sparx.Connector;
import org.sparx.Diagram;
import org.sparx.DiagramObject;
import org.sparx.Element;
import org.sparx.Repository;

/**
 * Add connector between two packages of an existing SPARX Enterprise Architect model
 * @author Petre Maierean
 *
 */
public class AddConnectorBetweenPackages extends BaseClient {

	public AddConnectorBetweenPackages(String eapFilePath) throws Exception {
		super(new File(eapFilePath));
	}

	
	/**
	 * Add connection between to packages in the model file
	 * @param sourcePackagePath
	 * @param destinationPackagePath
	 * @param connectionType one of the supported packages
	 * @throws Exception
	 */
	public void addConnection(final String sourcePackagePath, final String destinationPackagePath, final String connectionType) throws Exception {
		org.sparx.Package source = getPackage(sourcePackagePath);
		Element elSource = source.GetElement();
		int iSource = elSource.GetElementID();
		org.sparx.Package destination = getPackage(destinationPackagePath);
		Element elDestination = destination.GetElement();
		int iDest = elDestination.GetElementID();
		Collection<Connector> connectors = source.GetConnectors();
		boolean b = false;
		for(Connector connector : connectors) {
			if (connector.GetClientID() == iSource &&
				connector.GetSupplierID() == iDest) {
				b = true;
				break;
			}
		}
		if (!b) {
			Connector connector = connectors.AddNew("Sample connector", connectionType);
			connector.SetClientID(elSource.GetElementID());
			connector.SetSupplierID(elDestination.GetElementID());
			connector.SetDirection("Source -> Destination");
			connector.Update();
			connectors.Refresh();
			System.out.println("A connector has been added");
		}
		Diagram sourceDiagram = getMainDiagram(source, source.GetName());
		addPackage(sourceDiagram, source);
		addPackage(sourceDiagram, destination);
		repository.SaveAllDiagrams();
		System.out.println("The connector of type '" + connectionType + "' has between the packages has been added");
	}
	
	private Diagram getMainDiagram(final org.sparx.Package source, final String name) throws Exception {
		Diagram ret = null;
		Collection<Diagram> diagrams = source.GetDiagrams();
		for(Diagram diagram: diagrams) {
			if (diagram.GetName().equals(name)) {
				ret = diagram;
				break;
			}
		}
		if (ret == null) {
			ret = diagrams.AddNew(name, "Component");
			ret.Update();
			diagrams.Refresh();
		}
		return ret;
	}
	
	private void addPackage(final Diagram diagram, final org.sparx.Package pkg) throws Exception {
		Collection<DiagramObject> diagramObjects = diagram.GetDiagramObjects();
		boolean b = false;
		int id = pkg.GetElement().GetElementID();
		for(DiagramObject obj: diagramObjects) {
			if (obj.GetElementID() == id) {
				b = true;
				break;
			}
		}
		if (!b) {
			DiagramObject diagramObject = diagramObjects.AddNew(pkg.GetName(), "");
			diagramObject.SetElementID(id);
			diagramObject.Update();
			diagram.Update();
			diagramObjects.Refresh();
			System.out.println("The package '" + pkg.GetName() + "' has been added to the diagram");
		}
	}
	

	
	/**
	 * Verifies it a connection between to packages in the model file exists
	 * @param sourcePackagePath
	 * @param destinationPackagePath
	 * @param connectionType one of the supported packages
	 * @throws Exception
	 */
	public boolean hasConnection(final String sourcePackagePath, final String destinationPackagePath, final String connectionType) throws Exception {
		boolean ret = false;
		org.sparx.Package source = getPackage(sourcePackagePath);
		org.sparx.Package destination = getPackage(destinationPackagePath);
		int idDestination = destination.GetElement().GetElementID();
		Collection<Connector> connectors = source.GetConnectors();
		for(Connector connector: connectors) {
			if (connector.GetSupplierID() == idDestination) {
				ret = true;
				break;
			}
		}
		return ret;
	}
	
	protected org.sparx.Package getPackage(final String packagePath) throws Exception {
		String[] toks = packagePath.split("/");
		if (toks.length < 2)
			throw new Exception("The package path must be of the format: model_name/package1/.../package but it is " + packagePath);
		org.sparx.Package p = getModelPackage(toks[0]);
		String[] names = getLessOne(toks);
		org.sparx.Package ret = getNestedPackage(p, names);
		if (ret == null) 
			throw new Exception("Cannot resolve '" + packagePath + "' in the model");
		System.out.println("Found the package at path: " + packagePath);
		return ret;
	}
	
	private org.sparx.Package getNestedPackage(org.sparx.Package p, String[] names) throws Exception {
		org.sparx.Package ret = null;
		Collection<org.sparx.Package> children = p.GetPackages();
		
		for(org.sparx.Package child: children) {
			if (child.GetName().equals(names[0])) {
				if (names.length == 1) {
					ret = child;
					break;
				}
				else {
					String[] nnames = getLessOne(names);
					ret = getNestedPackage(child, nnames);
					if (ret != null) {
						break;
					}
				}
			}			
		}
		return ret;
	} 
	
	/**
	 * Get a model from the EAP file by name
	 * @param modleName
	 * @return
	 * @throws Exception
	 */
	public org.sparx.Package getModelPackage(final String modleName) throws Exception {
		Repository repository = getRepository();
		Collection<org.sparx.Package> models = repository.GetModels();
		org.sparx.Package ret = null;
		for(org.sparx.Package model: models) {
			if (model.GetName().equals(modleName)) {
				ret = model;
				break;
			}
		}
		return ret;
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		if (args.length < 4) {
			System.out.println("Expected arguments: modelFile sourcePackagePath destinationPackagePath connectionType");
			System.exit(-1);
		}
		else {
			try (AddConnectorBetweenPackages processor = new AddConnectorBetweenPackages(args[0])){
				processor.addConnection(args[1], args[2], args[3]);
				if (processor.hasConnection(args[1], args[2], args[2])) {
					System.out.println("The connection has been found");
				}
				else 
					throw new Exception("The connection has not been added");
			}
			catch(Exception e) {
				e.printStackTrace();
			}
		}
	}

}
