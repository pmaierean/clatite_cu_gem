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

import java.awt.image.RenderedImage;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.media.jai.JAI;

import org.maiereni.imaging.common.stl.vo.Triangle;
import org.maiereni.imaging.common.stl.vo.Vertex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Image file wrapper
 * 
 * @author Petre Maierean
 *
 */
public class ImageFileReader {
	private static final Logger logger = LoggerFactory.getLogger(ImageFileReader.class);
	private RenderedImage image;
	private ColorMarkerFilter filter;
	private int densityX, densityY;
	
	public int getDensityX() {
		return densityX;
	}

	public void setDensityX(int densityX) {
		this.densityX = densityX;
		
	}

	public int getDensityY() {
		return densityY;
	}

	public void setDensityY(int densityY) {
		this.densityY = densityY;
	}

	/**
	 * Constructor
	 * @param fileName Image file to read
	 * @param colorSelector file containing the color pannel to use for marker
	 * @throws Exception
	 */
	public ImageFileReader(final String fileName, final String colorSelectorFile) throws Exception {
		logger.debug("Load input file " + fileName);
		image = (RenderedImage)JAI.create("fileload", fileName);
		logger.debug("The image has been read");
		if (colorSelectorFile != null) {
			this.filter = new ColorMarkerFilter(colorSelectorFile);
		}
		else {
			logger.debug("Use white filter");
			this.filter = ColorMarkerFilter.AVOID_WHITE;
		}
	}
	
	/**
	 * Identify markers in the image
	 * 
	 * @throws Exception
	 */
	public List<Triangle> buildModel(float modelHeigth, float modelWidth, DepthModeler depthModeler) throws Exception {
		logger.debug("Model Height: " + modelHeigth + " Model Weigth: " + modelWidth);
		List<Triangle> triangles = new ArrayList<Triangle>();
		ImageReducer reducer = new ImageReducer(image, filter, densityX, densityY);
		int height = reducer.getHeight(); // get height in pixels
		int width = reducer.getWidth(); // get weigth in pixels
		depthModeler.setImageHeight(height);
		depthModeler.setImageWidth(width);
		float kHeight = modelHeigth / height;
		float kWidth = modelWidth / width;
		logger.debug("Height: " + height + " Weigth: " + width);
		Vertex[] pre = null, last = null;
		int trId = 0;;
		for (int x = 0; x < width; x++) {
			Vertex[] oldVertex = null;
			boolean gap = true;
			for (int y = 0; y < height; y++) {
				if (reducer.isPoint(x, y)) {
					float zo = depthModeler.calculateOrigin(x, y);
					float zd = depthModeler.calculateDepth(x, y);
					Vertex[] vertex = new Vertex[2];
					vertex[0] = new Vertex(null, x * kWidth, y * kHeight, zo);
					vertex[1] = new Vertex(null, x * kWidth, y * kHeight, zd);
					if (oldVertex != null) {
						if (!gap) {
							triangles.add(new Triangle("" + trId++, new Vertex[] {oldVertex[1], oldVertex[0], vertex[0]}));
							triangles.add(new Triangle("" + trId++, new Vertex[] {oldVertex[1], vertex[0], vertex[1]}));
						}
					} 
					else {
						if (pre != null) {
							triangles.add(new Triangle("" + trId++, new Vertex[] {pre[1], pre[0], vertex[0]}));
							triangles.add(new Triangle("" + trId++, new Vertex[] {pre[1], vertex[0], vertex[1]}));							
						}
						pre = vertex;
					}
					oldVertex = vertex;
					gap = false;
				} 
				else {
					gap = true;
				}
			}
			if (oldVertex != null) {
				if (last != null) {
					triangles.add(new Triangle("" + trId++, new Vertex[] {last[1], last[0], oldVertex[0]}));
					triangles.add(new Triangle("" + trId++, new Vertex[] {last[1], oldVertex[0], oldVertex[1]}));												
				}
				oldVertex = last;
			}
		}
		
		logger.debug("Built a number of " + triangles.size() + " triangles in the image");
		return triangles;
	}
	
	public String getSeenColors() {
		List<String> seenColors = filter.getSeenColors();
		StringBuffer sb = new StringBuffer();
		Iterator<String> iter = seenColors.iterator();
		while(iter.hasNext()) {
			sb.append(iter.next()).append(" ");
		}
		return sb.toString();
	}
	
	class Pair {
		int x, y;
		Pair(int x, int y) {
			this.x = x;
			this.y = y;
		}
	}
}