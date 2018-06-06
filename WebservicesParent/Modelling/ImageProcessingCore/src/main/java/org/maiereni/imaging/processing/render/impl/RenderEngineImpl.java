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
package org.maiereni.imaging.processing.render.impl;

import java.awt.image.RenderedImage;
import java.util.ArrayList;
import java.util.List;

import javax.media.jai.JAI;

import org.apache.log4j.Logger;
import org.maiereni.imaging.common.stl.vo.Triangle;
import org.maiereni.imaging.common.stl.vo.Vertex;
import org.maiereni.imaging.processing.render.Modeler;
import org.maiereni.imaging.processing.render.ModelerFactory;
import org.maiereni.imaging.processing.render.RenderEngine;

/**
 * Specific implementation of the render engine
 * 
 * @author petre maierean
 * 
 */
public class RenderEngineImpl implements RenderEngine {
	private static final Logger logger = Logger
			.getLogger(RenderEngineImpl.class);

	private ModelerFactory factory;

	private int currentX, currentY, maxX, maxY, size;

	private Float[][] pixels;

	private List<Triangle> triangles = new ArrayList<Triangle>();

	private float height, width, kHeight, kWidth;

	public RenderEngineImpl() {
		this(1f, 1f);
	}

	public RenderEngineImpl(float height, float width) {
		this.height = height;
		this.width = width;
	}

	public ModelerFactory getFactory() {
		return factory;
	}

	public void setFactory(ModelerFactory factory) {
		this.factory = factory;
	}

	/**
	 * Verifies if there are more triangles to compute
	 * 
	 * @return true if more are available
	 */
	public boolean hasNextTriangle() {
		if (triangles.isEmpty()) {
			Triangle triangle = nextTriangle();
			if (triangle != null)
				triangles.add(0, triangle);			
		}
		return !triangles.isEmpty();
	}

	/**
	 * Computes the next triangle
	 * 
	 * @return
	 */
	public Triangle nextTriangle() {
		Triangle ret = null;
		try {
			if (triangles.isEmpty()) {
				int triangleId = 0;
				int startCurrentY = currentY;
				for (int x = currentX; (x < maxX) && (ret == null); x++) {
					for (int y = startCurrentY; (y < maxY) && (ret == null); y++) {
						if (pixels[x][y] != null) {
							Vertex vertex0 = new Vertex(null, x * kWidth, y * kHeight , pixels[x][y].floatValue());
							Vertex vertexz0 = null, vertexz1 = null, vertexz2 = null;
							float z = pixels[x][y].floatValue();
							if (pixels[x - 1][y] != null) {
								z = pixels[x - 1][y].floatValue();
							}
							vertexz0 = new Vertex(null, (x - 1) * kWidth, y * kHeight, z);
							z = pixels[x][y].floatValue();
							if (pixels[x][y - 1] != null) {
								z = pixels[x][y - 1].floatValue();
							}
							vertexz1 = new Vertex(null, x * kWidth, (y - 1) * kHeight , z);
							z = pixels[x][y].floatValue();
							if (pixels[x - 1][y - 1] != null) {
								z = pixels[x - 1][y - 1].floatValue();
							}
							vertexz2 = new Vertex(null, (x - 1) * kWidth, (y - 1) * kHeight, z);
							triangles.add(new Triangle("" + triangleId++, new Vertex[] { vertex0, vertexz0, vertexz1 }));
							triangles.add(new Triangle("" + triangleId++, new Vertex[] { vertexz0, vertexz1, vertexz2 }));
	
							if ((y + 1) < maxY) {
								currentX = x;
								currentY = y + 1;
							}
							else if ((x + 1) < maxX) {
								currentY = 1;
								currentX = x + 1;
							}
							else {
								currentY = maxY;
								currentX = maxX;
							}
							ret = triangles.remove(0);
						}
					}
					startCurrentY = 1;
				}
			} else
				ret = triangles.remove(0);
		}
		catch(Exception e) {
			logger.error("Failed to get the next triangle",  e);
		}
		return ret;
	}
	
	/**
	 * Loads the 2D image
	 * 
	 * @param image
	 * @throws Exception
	 */
	public void loadImage(RenderedImage image) throws Exception {
		Modeler modeler = factory.buildModeler(image);
		maxX = modeler.getWidth();
		maxY = modeler.getHeight();
		pixels = new Float[maxX][maxY];
		size = 0;
		for (int x = 1; x < maxX; x++) {
			for (int y = 1; y < maxY; y++) {
				if (modeler.isVisible(x, y)) {
					pixels[x][y] = new Float(modeler.calculatePosition(x, y));
					size += 2;
				}
				else {
					pixels[x][y] = null;
				}
			}
		}
		logger.debug("A number of " + size + " triangles are ready for conversion");
		currentX = 1;
		currentY = 1;
		kHeight = height / maxY;
		kWidth = width / maxX;
		logger.debug("The image file has been loaded");
	}

	/**
	 * Load an image file 
	 * @param sourceFile
	 * @throws Exception
	 */
	public void loadImage(String sourceFile) throws Exception {
		logger.debug("Load input file " + sourceFile);
		RenderedImage image = (RenderedImage) JAI.create("fileload", sourceFile);
		loadImage(image);
	}
	
	/**
	 * Estimates the number of triangles for the image
	 * @return
	 */
	public int size() {
		return size;
	}
}
