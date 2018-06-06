/** ====================================================================
   Licensed to the Apache Software Foundation (ASF) under one or more
   contributor license agreements.  See the NOTICE file distributed with
   this work for additional information regarding copyright ownership.
   The ASF licenses this file to You under the Apache License, Version 2.0
   (the "License"); you may not use this file except in compliance with
   the License.  You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
==================================================================== */
package org.maiereni.imaging.common.stl.io;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.maiereni.imaging.common.stl.vo.Model;
import org.maiereni.imaging.common.stl.vo.Triangle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A simple utility that loads/saves models to or from a file or stream
 * @author Petre Maierean
 *
 */
public class ModelLoader {
	private static final Logger logger = LoggerFactory.getLogger(ModelLoader.class);
	/**
	 * Reads an STL model from a file
	 * @param fileName the name of the fole
	 * @return the model
	 * @throws Exception
	 */
	public static Model loadModel(final String fileName) throws Exception {
		return loadModel(new FileInputStream(fileName));
	}
	
	/**
	 * Reads an STL model from a stream of bytes
	 * @param is the input stream
	 * @return the model
	 * @throws Exception
	 */
	public static Model loadModel(final InputStream is) throws Exception {
		if (is == null)
			throw new Exception("The input stream is blank");
		try (BinarySTLReader reader = new BinarySTLReader(is)) {
			Model model = new Model();
			model.setName(reader.getName());
			long size = reader.getSize();
			List<Triangle> triangles = new ArrayList<Triangle>();
			
			for(long i=0; i<size;i++)
				triangles.add(reader.nextTriangle());
			model.setTriangles(triangles);
			logger.debug("Model has been loaded");
			return model;
		}
	}
	
	/**
	 * Save the model to a file
	 * @param model the model to save
	 * @param fileName
	 * @throws Exception
	 */
	public static void saveModel(Model model, String fileName) throws Exception {
		if (model == null)
			throw new Exception("The model is null");
		if (model.getTriangles() == null)
			throw new Exception("The model is blank");
		FileOutputStream os = null;
		try {
			saveModel(model, new FileOutputStream(fileName));
		}
		finally {
			if (os != null)
				try {
					os.close();
				}
				catch(Exception e){}
		}		
	}
	/**
	 * Writes the model to an output stream
	 * @param model the model to save
	 * @param os the output stream
	 * @throws Exception
	 */
	public static void saveModel(Model model, OutputStream os) throws Exception {
		if (model == null)
			throw new Exception("The model is null");
		if (model.getTriangles() == null)
			throw new Exception("The model is blank");
		if (os == null)
			throw new Exception("The output is null");
		BinarySTLWriter writer = new BinarySTLWriter(model.getName(), os);
		writer.triangles = model.getTriangles();
		writer.close();
		logger.debug("Model has been saved");
	}
	/**
	 * Writes the model to an output stream
	 * @param model the model to save
	 * @param os the output stream
	 * @throws Exception
	 */
	public static void saveModelAsText(Model model, OutputStream os) throws Exception {
		if (model == null)
			throw new Exception("The model is null");
		if (model.getTriangles() == null)
			throw new Exception("The model is blank");
		if (os == null)
			throw new Exception("The output is null");
		BufferedSTLWriter writer = new BufferedSTLWriter(model.getName(), os, 4);
		for (Triangle triangle: model.getTriangles())
			writer.write(triangle);
		writer.close();
		logger.debug("Model has been saved");
	}
}
