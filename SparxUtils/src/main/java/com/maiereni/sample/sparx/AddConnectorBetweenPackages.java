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
		org.sparx.Package destination = getPackage(destinationPackagePath);
		Collection<Connector> connectors = source.GetConnectors();
		Connector connector = connectors.AddNew("Sample connector", connectionType);
		connector.SetClientID(source.GetPackageID());
		connector.SetSupplierID(destination.GetPackageID());
		connector.SetDirection("Source -> Destination");
		connector.Update();
		connectors.Refresh();
		repository.SaveAllDiagrams();
		System.out.println("The connector of type '" + connectionType + "' has between the packages has been added");
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
		int idDestination = destination.GetPackageID();
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
			throw new Exception("The package path must be of the format: model_name/package1/.../package");
		org.sparx.Package p = getModelPackage(toks[0]);
		StringBuffer sb = new StringBuffer();
		sb.append("/").append(toks[0]);
		for(int i=1; i<toks.length; i++) {
			sb.append("/").append(toks[i]);
			Collection<org.sparx.Package> children = p.GetPackages();
			p = null;
			for(org.sparx.Package child: children) {
				if (child.GetName().equals(toks[i])) {
					p = child;
					break;
				}
			}
			if (p == null) {
				throw new Exception("Could not find a package at '" + sb.toString() + "' in the model. ");
			}
		}
		System.out.println("Found the package at path: " + sb.toString());
		return p;
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
