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
public class ListAllPackages extends BaseClient {

	public ListAllPackages(final String eapFilePath) throws Exception {
		super(new File(eapFilePath));
	}
	
	public void listAllPackages() throws Exception {
		Collection<org.sparx.Package> models =  getRepository().GetModels();
		for(org.sparx.Package p: models) {
			listPackage("", p);
		}
	}
	
	public void listPackage(final String tab, final org.sparx.Package pkg) throws Exception {
		System.out.println(tab + "(" + pkg.GetPackageGUID() + " " + pkg.GetName()); 
		Collection<org.sparx.Package> pkgs =  pkg.GetPackages();
		for(org.sparx.Package p: pkgs) {
			listPackage(tab + "\t", p);
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		if (args.length == 0) {
			System.out.println("Expected argument: eapFilePath ");
			System.exit(-1);
		}
		else {
			try (ListAllPackages processor = new ListAllPackages(args[0])) {
				processor.listAllPackages();
			}
			catch(Exception e) {
				e.printStackTrace();
			}
		}
	}

}
