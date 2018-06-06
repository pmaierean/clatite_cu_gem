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

import java.awt.image.DataBuffer;
import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;

import org.apache.log4j.Logger;

/**
 * Reduces the density of readings
 * 
 * @author Petre Maierean
 */
public class ImageReducer {
	private static final Logger logger = Logger.getLogger(ImageReducer.class);
	private int densityX, densityY;
	private ColorMarkerFilter filter;
	private boolean[][] map;
	private int lx, ly;
	
	public ImageReducer(RenderedImage image, ColorMarkerFilter filter, int densityX, int densityY) throws Exception {
		this.densityX = densityX;
		this.densityY = densityY;
		SampleModel model = image.getSampleModel();
		DataBuffer buffer = image.getData().getDataBuffer();
		int height = model.getHeight(); // get height in pixels
		int width = model.getWidth(); // get weigth in pixels
		logger.debug("The original size is " + height + "," + width);
		ly = height / densityY;
		lx = width / densityX;
		logger.debug("The reduced size is " + ly + "," + lx);
		map = new boolean[lx][ly];
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {				
				float[] d = model.getPixel(x, y, (float[])null, buffer);
				if (!filter.filterPixel(d)) {
					int xh = x / densityX;
					int yh = y / densityY;
					if (!map[xh][yh])
						map[xh][yh] = true;
				}
			}
		}
	}
	
	public int getHeight() {
		return ly;
	}
	
	public int getWidth() {
		return lx;
	}
	
	public boolean isPoint(int x, int y) {
		if (x >=0 && x < lx)
			if (y >=0 && y < ly)
				return map[x][y];
		return false;
	}

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

	public ColorMarkerFilter getFilter() {
		return filter;
	}

	public void setFilter(ColorMarkerFilter filter) {
		this.filter = filter;
	}

	public boolean[][] getMap() {
		return map;
	}

	public void setMap(boolean[][] map) {
		this.map = map;
	}

	public int getLx() {
		return lx;
	}

	public void setLx(int lx) {
		this.lx = lx;
	}

	public int getLy() {
		return ly;
	}

	public void setLy(int ly) {
		this.ly = ly;
	}
}
