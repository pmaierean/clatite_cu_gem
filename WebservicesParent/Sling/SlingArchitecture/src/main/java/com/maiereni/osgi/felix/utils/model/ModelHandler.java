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
import java.util.List;

import com.maiereni.sling.info.Model;

/**
 * The Sling model handler
 * @author Petre Maierean
 *
 */
public class ModelHandler extends BaseModelHandler {
	private List<PostProcessor> postProcessors;
	
	public ModelHandler() throws Exception {
		super();
		this.postProcessors = new ArrayList<PostProcessor>();
		this.postProcessors.add(new BundleGroupQualifier());
	}

	/**
	 * Writes the Sling information into a file
	 * @param slingInfo
	 * @param xmlFile
	 * @throws Exception
	 */
	public void writetoXML(final Model model, final String xmlFile) throws Exception {
		SlingModelWriter modelWriter = new SlingModelWriter(getFactory(), getMarshaller());
		modelWriter.writetoXML(model, xmlFile);
	}

	/**
	 * Writes the Sling information into Enterprise Architect
	 * @param slingInfo
	 * @param eaFile
	 * @throws Exception
	 */
	public void writetoEA(final Model model, final String eaFile) throws Exception {
		EASlingModelWriter modelWriter = new EASlingModelWriter();
		modelWriter.writetoEA(model, eaFile);
	}

	/**
	 * Reads the Sling information from an xml file
	 * @param xmlFile
	 * @return
	 * @throws Exception
	 */
	public Model loadFromXml(String xmlFile) throws Exception {
		SlingModelReader modelReader = new SlingModelReader(getFactory(), getUnmarshaller());
		return modelReader.loadFromXml(xmlFile);
	}

	/**
	 * Post processes the model
	 * @param model
	 * @return
	 * @throws Exception
	 */
	public Model updataModel(final Model model) throws Exception {
		Model ret = model;
		for(PostProcessor processor: postProcessors) {
			ret = processor.updataModel(model);
		}
		return ret;
	}
}
