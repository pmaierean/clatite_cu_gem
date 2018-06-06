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

import java.io.FileReader;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.List;

import org.maiereni.imaging.processing.ColorMarkerFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Pixel filter based on its color. The class constructor may be passed a path to the file containing the
 * color patterns to use for filtering pixels based on colors. The file contains the following structure:
 * <br>
 * keep: rgb_pattern_1 rgb_pattern_2 ...
 * avoid: rgb_pattern_n rgb_pattern_n_1 ...
 * <br>
 * where rgb_pattern is a regular expression used for defining matching criterias for RGB strings 
 * (such as 127.0:127.0:127.0) 
 * 
 * @author petre maierean
 */
public class ColorFilter {
	private static final Logger logger = LoggerFactory.getLogger(ColorMarkerFilter.class);
	public static final ColorFilter AVOID_WHITE = new ColorFilter("25\\d\\x2e0:25\\d\\x2e0:25\\d\\x2e0", "\\d{1,3}\\x2e\\d:\\d{1,3}\\x2e\\d:\\d{1,3}\\x2e\\d");
	private List<String> avoidColors, keepColors; 
	private static final String KEEP = "keep:";
	private static final String AVOID = "avoid:";
	public List<String> seenColors = new ArrayList<String>();
	
	public ColorFilter(String colorSelectorFile) throws Exception {
		logger.debug("Load color filter from: " + colorSelectorFile);
		FileReader reader = null;
		try {
			LineNumberReader lnr = new LineNumberReader(reader = new FileReader(colorSelectorFile));
			String s = null;
			boolean keep = true;
			for(; (s = lnr.readLine())!=null;) {
				if (s.startsWith(ColorFilter.KEEP)) {
					keep = true;
					s = s.substring(ColorFilter.KEEP.length());
				}
				else if (s.startsWith(ColorFilter.AVOID)) {
					keep = false;
					s = s.substring(ColorFilter.AVOID.length());
				}
				s = s.trim();
				String[] arr = s.split(" ");
				for(int i=0; i<arr.length; i++) {
					if (keep) 
						addKeepColor(arr[i]);	
					else
						addAvoidColor(arr[i]);
				}
			}
			lnr.close();
		}
		finally {
			if (reader != null)
				try {
					reader.close();
				}
				catch(Exception e){}
		}
	}
	
	public ColorFilter(String avoidColor, String keepColor) {
		addAvoidColor(avoidColor);
		addKeepColor(keepColor);
	}
	
	public void addAvoidColor(float[] rgbColor) {
		String avoidColor = colorToString(rgbColor);
		addAvoidColor(avoidColor);
	}
		
	public void addAvoidColor(String avoidColor) {
		if (avoidColor != null) {
			try {
				validateColor(avoidColor);
				if (avoidColors == null)
					avoidColors = new ArrayList<String>();
				avoidColors.add(avoidColor);
				logger.debug("Add color to avoid: " + avoidColor);
			}
			catch(Exception e) {
				logger.error("Failed to add color " + avoidColor + " to list ", e);				
			}
		}		
	}

	public void addKeepColor(String keepColor) {
		if (keepColor != null) {
			try {
				validateColor(keepColor);
				if (keepColors == null)
					keepColors = new ArrayList<String>();
				keepColors.add(keepColor);
				logger.debug("Add color to keep: " + keepColor);
			}
			catch(Exception e) {
				logger.error("Failed to add color " + keepColor + " to list ", e);
			}
		}		
	}

	public void addKeepColor(float[] rgbColor) {
		String keepColor = colorToString(rgbColor);
		addKeepColor(keepColor);
	}
	
	public boolean filterPixel(float[] rgbColor) {
		String color = colorToString(rgbColor);
		if (color != null) {
			if (!seenColors.contains(color))
				seenColors.add(color);
			if (avoidColors != null)
				if (contains(avoidColors, color))
					return true;
			if (keepColors != null)
				if (contains(keepColors, color))
					return false;
		}
		return true;
	}
	
	private boolean contains(List<String> colors, String color) {
		boolean b = false;
		for(String expr: colors) {
			if (color.matches(expr)) {
				b = true;
				break;
			}
		}
		return b;
	}
	
	public String colorToString(float[] rgbColor) {
		if (rgbColor != null) {
			StringBuffer sb = new StringBuffer();
			for(int i=0; i<rgbColor.length; i++)
				sb.append(i>0? ":" : "").append(Float.toString(rgbColor[i]));
			return sb.toString();
		}
		return null;
	}
	
	private void validateColor(String s) throws Exception{
		
	}

	public List<String> getSeenColors() {
		return seenColors;
	}

	public void setSeenColors(List<String> seenColors) {
		this.seenColors = seenColors;
	}

}
