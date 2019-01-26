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

/**
 * @author Petre Maierean
 *
 */
public class AddPackages extends BaseClient {

	public AddPackages(String eapFile) throws Exception {
		super(new File(eapFile));
	}

	public void createPackages(final String templatePackagePath, int count) throws Exception {
		String[] toks = templatePackagePath.split("/");
		if (toks.length < 2) {
			throw new Exception("Expect minimum 2 tokens in the templatePackagePath");
		}
		Collection<org.sparx.Package> models =  getRepository().GetModels();
		org.sparx.Package model = null;
		for(org.sparx.Package p: models) {
			if (p.GetName().equals(toks[0])) {
				model = p;
				break;
			}
		}
		if (model == null) {
			model = models.AddNew(toks[0], "");
			model.Update();
			models.Refresh();
		}
		String[] names = getLessOne(toks);
		addPackage(model, names, count);
	}
	
	private void addPackage(final org.sparx.Package p, final String[] names, int count) throws Exception {
		Collection<org.sparx.Package> pkgs = p.GetPackages();
		if (names.length > 1) {
			org.sparx.Package pn = null;
			for (org.sparx.Package pkg: pkgs) {
				if (pkg.GetName().equals(names[0])) {
					pn = pkg;
					break;
				}
			}
			if (pn == null) {
				pn = pkgs.AddNew(names[0], null);
				pn.Update();
				pkgs.Refresh();
				System.out.println("The package '" + names[0] + "' has been created in " + p.GetXMLPath());
			}
			String[] nname = getLessOne(names);
			addPackage(pn, nname, count);
		}
		else {
			addPackage(p, names[0], count);
		}
	}
	
	private void addPackage(final org.sparx.Package p, final String templateName, int count) throws Exception {
		Collection<org.sparx.Package> pkgs = p.GetPackages();
		for(int i=0; i< count; i++) {
			String name = templateName + i;
			org.sparx.Package pn = null;
			for (org.sparx.Package pkg: pkgs) {
				if (pkg.GetName().equals(name)) {
					pn = pkg;
					break;
				}
			}
			if (pn == null) {
				pn = pkgs.AddNew(name, "");
				pn.Update();
				pkgs.Refresh();
				System.out.println("Package " + name + " has been create in " + p.GetXMLPath());
				pkgs = p.GetPackages();
			}
		}
	}
	
	
	/**
	 * Add a number of sub-packages to an existing package. Expected package_template should be of the format /model_name/package1/.../parent_package/new_name 
	 * The program creates any parent package from package1 to parent_package first
	 *  
	 * @param args eapFilePath package_path_template count
	 * 
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		if (args.length < 3) {
			System.out.println("Expected argument: eapFilePath template_package_path count");
			System.exit(-1);
		}
		else {
			try (AddPackages processor = new AddPackages(args[0])) {
				int count = Integer.parseInt(args[2]);
				processor.createPackages(args[1], count);
			}
			catch(Exception e) {
				e.printStackTrace();
			}
		}		
	}
}
