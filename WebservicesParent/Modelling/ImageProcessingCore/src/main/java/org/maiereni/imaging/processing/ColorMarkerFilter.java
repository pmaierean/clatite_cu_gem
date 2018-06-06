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

import java.io.FileReader;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;

/**
 * Pixel filter based on its color
 * 
 * @author Petre Maierean
 *
 */
public class ColorMarkerFilter {
	private static final Logger logger = Logger.getLogger(ColorMarkerFilter.class);
	public static final ColorMarkerFilter AVOID_WHITE = new ColorMarkerFilter(new float[] {255, 255, 255});
	private List<String> avoidColors, keepColors; 
	private static final String KEEP = "keep:";
	private static final String AVOID = "avoid:";
	
	public List<String> seenColors = new ArrayList<String>();
	
	public ColorMarkerFilter(final String colorSelectorFile) throws Exception {
		try(FileReader reader = new FileReader(colorSelectorFile)) {
			LineNumberReader lnr = new LineNumberReader(reader);
			String s = null;
			boolean keep = true;
			for(int line = 1; (s = lnr.readLine())!=null; line++) {
				if (s.startsWith(ColorMarkerFilter.KEEP)) {
					keep = true;
					s = s.substring(ColorMarkerFilter.KEEP.length());
				}
				else if (s.startsWith(ColorMarkerFilter.AVOID)) {
					keep = false;
					s = s.substring(ColorMarkerFilter.AVOID.length());
				}
				else {
					logger.warn("Unknown command at line: " + line);
					continue;
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
	}
	
	public ColorMarkerFilter(float[] rgbColor) {
		addAvoidColor(rgbColor);
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
				return contains(avoidColors, color);
			else if (keepColors != null)
				return !contains(keepColors, color);
		}
		return true;
	}
	
	private boolean contains(List<String> colors, String color) {
		Iterator<String> iter = colors.iterator();
		while(iter.hasNext()) {
			String expr = iter.next();
			if (color.matches(expr))
				return true;
		}
		return false;
	}
	
	private String colorToString(float[] rgbColor) {
		if (rgbColor != null && rgbColor.length == 3) {
			StringBuffer sb = new StringBuffer();
			for(int i=0; i<3; i++)
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
