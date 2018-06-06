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
package org.maiereni.imaging.processing;

import java.io.FileOutputStream;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.maiereni.imaging.common.stl.io.BinarySTLWriter;
import org.maiereni.imaging.common.stl.io.BufferedSTLWriter;
import org.maiereni.imaging.common.stl.vo.Triangle;
import org.maiereni.imaging.processing.impl.LinearDepthModeler;
import org.maiereni.imaging.processing.impl.SemiSphereDepthModeler;
import org.maiereni.imaging.processing.render.impl.ColorFilterModelerFactory;
import org.maiereni.imaging.processing.render.impl.RenderEngineImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Converts 2D image files such as GIF of JPEG to 3D STL
 * @author petre maierean
 *
 */
public class Converter {
	private static final Logger logger = LoggerFactory.getLogger(Converter.class);
	private static final String[] INPUT_FILE_KEYS = {"-input","--i","Tells that the following argument contains the input file path. This can also be passed as the free argument"};
	private static final String[] OUTPUT_FILE_KEYS = {"-output", "--o", "The following argument contains the output file path. If not specified the program will append .stl to the input file path for that"};
	private static final String[] MODEL_HEIGHT = {"-height", "--h", "The following argument specifies the height in mm of the resulting model. It defaults to 2"};
	private static final String[] MODEL_WIDTH = {"-width", "--w", "The following argument specifies the width in mm of the resulting model. It defaults to 2"};
	private static final String[] MODEL_DEPTH = {"-depth", "--d", "The following argument specifies the depth in mm of the resulting model. It defaults to 0"};
	private static final String[] MODEL_RADIUS = {"-radius", "--r", "It indicates that the model must be rendered on a semi spheric shape. The following argument specifies the radius in mm of the resulting model "};
	private static final String[] PIXEL_DENSITY = {"-density", "--pd", "The following argument is interpreted as a comma delimited pair of integers telling the unit area in pixels of the in the input image"};
	private static final String[] COLOR_SELECTOR = {"-colors", "--c", "The following argument contains the path to a file with color filtering definitions. See comment with org.dclick.image.render.impl.ColorFilter. By default pixels of white color (25\\d.0:25\\d.0:25\\d.0) are filtered out"};
	private static final String[] SEEN_COLORS = {"-seenColors", "--sc", "The following argument contains the path to the output file that receives the list of colors selected from the input image"};
	private static final String[] ONLY_COLOR = {"-colorOnly", "-oc", "Only generate seenColors"};
	//private static final String[] DEPTH_MAP_FILE = {"-depthMap", "-dm", "The following argument is the path to the file containing the pixel to depth mapping"};
	private static final String FREE = "free";
	private String inputFile, outputFile, colorSelectorFile, seenColors;
	private float modelHeigth = 2, modelWidth=2, modelDepth = 0, modelRadius = 0;
	private int densityX, densityY;
	private boolean onlyColor;
	
	public Converter(String[] args) throws Exception {
		Map<String,String> argsMap = new HashMap<String, String>();
		String key = null;
		for(int i=0; i<args.length;i++) {
			if (args[i].startsWith("-")) {
				if (key != null)
					argsMap.put(key, "");
				key = args[i];
			}
			else if (key == null)
				if (argsMap.containsKey(Converter.FREE))
					throw new Exception("Too many free arguments");
				else
					argsMap.put(Converter.FREE, args[i]);
			else {
				argsMap.put(key, args[i]);
				key = null;
			}
		}
		inputFile = readFromMap(argsMap, Converter.INPUT_FILE_KEYS, argsMap.get(Converter.FREE));
		if (inputFile == null)
			throw new Exception("No input file defined");
		outputFile = readFromMap(argsMap, Converter.OUTPUT_FILE_KEYS, inputFile + ".stl");

		colorSelectorFile = readFromMap(argsMap, Converter.COLOR_SELECTOR, null);
		
		seenColors = readFromMap(argsMap, Converter.SEEN_COLORS, null);
		
		modelHeigth = readFromMap(argsMap, Converter.MODEL_HEIGHT, 1);
		modelWidth = readFromMap(argsMap, Converter.MODEL_WIDTH, 1);
		modelDepth = readFromMap(argsMap, Converter.MODEL_DEPTH, -1);
		modelRadius = readFromMap(argsMap, Converter.MODEL_RADIUS, Float.NaN);
				
		String pixelDensity = readFromMap(argsMap, Converter.PIXEL_DENSITY, "1,1");
		String[] sp = pixelDensity.split(",");
		densityX = Integer.parseInt(sp[0]);
		densityY = Integer.parseInt(sp[1]);
		
		String sOnlyColor = readFromMap(argsMap, Converter.ONLY_COLOR, null);
		onlyColor = sOnlyColor != null;
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			new Converter(args).processConversion();
		}
		catch(Exception e) {
			logger.error("Failed to convert", e);
		}
	}

	public void processConversion() throws Exception {
		ColorFilterModelerFactory factory = new ColorFilterModelerFactory(colorSelectorFile);
		factory.setOriginalPosition(modelDepth);
		factory.setRadius(modelRadius);
		factory.setDensityX(densityX);
		factory.setDensityY(densityY);
		
		RenderEngineImpl engine = new RenderEngineImpl(modelHeigth, modelWidth);
		engine.setFactory(factory);
		engine.loadImage(inputFile);
		BufferedSTLWriter writer = null;
		int count = 0;
		try{
			int size = engine.size();
			logger.debug("Expected number of triangles is " + size);
			writer = new BufferedSTLWriter("Simple test", outputFile, size);
			Triangle triangle = null;
			for(;engine.hasNextTriangle();) {
				triangle = engine.nextTriangle();
				writer.write(triangle);
				count++;
				if ((count % 1000) == 0)
					logger.debug("Written: " + count);
			}
			if (triangle != null)
				logger.debug("Last triangle " + count + ":\r\n" + triangle.toString());
		}
		finally{
			if (writer != null) {
				try{
					writer.close();
				}
				catch(Exception e){}
			}
		}
		logger.debug("Done. A number of " + count + " triangles have been written to the output");
	}
	
	/**
	 * Processes the file conversion
	 * @throws Exception
	 */
	public void processConvertion() throws Exception {
		DepthModeler depthModeler = null;
		if (modelRadius != Float.NaN) {
			logger.debug("Use Semi Sphere Depth Modeler");
			depthModeler = new SemiSphereDepthModeler(modelDepth, modelRadius);
		}
		else {
			logger.debug("Use Linear Depth Modeler");
			depthModeler = new LinearDepthModeler(modelDepth);
		}
		ImageFileReader reader = new ImageFileReader(inputFile, colorSelectorFile);
		reader.setDensityX(densityX);
		reader.setDensityY(densityY);
		List<Triangle> pairs = reader.buildModel(modelHeigth, modelWidth, depthModeler);
		if (!onlyColor) {
			FileOutputStream os = null;
			try{
				BinarySTLWriter writer = new BinarySTLWriter("Simple test", os = new FileOutputStream(outputFile));
	
				Iterator<Triangle> iter = pairs.iterator();
				while(iter.hasNext()) {
					Triangle triangle = iter.next();
					writer.write(triangle);
				}
	
				writer.close();
			}
			finally{
				if (os != null) {
					try{
						os.close();
					}
					catch(Exception e){}
				}
			}
		}
		if (seenColors != null) {
			FileWriter wr = null;
			try {
				wr = new FileWriter(seenColors);
				wr.write(reader.getSeenColors());
			}
			finally {
				if (wr != null)
					try {
						wr.close();
					}
					catch(Exception e){}
			}
		}
	}

	public float getModelHeigth() {
		return modelHeigth;
	}

	public void setModelHeigth(float modelHeigth) {
		this.modelHeigth = modelHeigth;
	}

	private float readFromMap(final Map<String,String> argsMap, final String[] key, float defaultValue) throws Exception {
		String val = readFromMap(argsMap, key, null);
		if (val != null)
			return Float.parseFloat(val);
		return defaultValue;
	}
	
	private String readFromMap(final Map<String,String> argsMap, final String[] key, final String defaultValue) {
		String s = argsMap.containsKey(key[0])? argsMap.get(key[0]) : 
			   (argsMap.containsKey(key[1])? argsMap.get(key[1]) :
					defaultValue);
		return s;
	}

	public String getColorSelectorFile() {
		return colorSelectorFile;
	}

	public void setColorSelectorFile(String colorSelectorFile) {
		this.colorSelectorFile = colorSelectorFile;
	}

	public String getInputFile() {
		return inputFile;
	}

	public void setInputFile(String inputFile) {
		this.inputFile = inputFile;
	}

	public String getOutputFile() {
		return outputFile;
	}

	public void setOutputFile(String outputFile) {
		this.outputFile = outputFile;
	}

	public float getModelWidth() {
		return modelWidth;
	}

	public void setModelWidth(float modelWidth) {
		this.modelWidth = modelWidth;
	}

	public float getModelDepth() {
		return modelDepth;
	}

	public void setModelDepth(float modelDepth) {
		this.modelDepth = modelDepth;
	}

	public String getSeenColors() {
		return seenColors;
	}

	public void setSeenColors(String seenColors) {
		this.seenColors = seenColors;
	}
}
