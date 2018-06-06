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
import java.io.FileWriter;

import javax.media.jai.JAI;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Generates a CSV with RGB color strings for the input image file
 * 
 * @author Petre Maierean
 *
 */
public class ColorToCSV {
	public static final Logger logger = LoggerFactory.getLogger(ColorToCSV.class);

	private static final String WHITE_FILTER = "2[3-5]\\d.0:2[3-5]\\d.0:2[3-5]\\d.0";
	/**
	 * 
	 * @param imageFile
	 * @param outFile
	 * @param colorPattern
	 * @throws Exception
	 */
	public void process(final String imageFile, final String outFile, final String colorPattern) throws Exception {
		RenderedImage image = (RenderedImage) JAI.create("fileload", imageFile);
		SampleModel model = image.getSampleModel();
		DataBuffer buffer = image.getData().getDataBuffer();
		int height = model.getHeight(); // get height in pixels
		int width = model.getWidth(); // get weigth in pixels
		logger.debug("The original size is " + height + "," + width);
		FileWriter fw = null;
		try{
			fw = new FileWriter(outFile == null ? outFile : imageFile + ".csv");
			StringBuffer sb = new StringBuffer();
			for (int y = 0; y < height; y++) {	
				for (int x = 0; x < width; x++) {			
					float[] d = model.getPixel(x, y, (float[])null, buffer);
					if (x > 0)
						sb.append(",");
					String s = colorToString(d);
					if (colorPattern != null) { 
						if (s.matches(colorPattern))
							sb.append(s);
						else
							sb.append("0");
					} else {
						if (s.matches(WHITE_FILTER))
							sb.append("0");
						else
							sb.append(s);
					} 
				}
				sb.append("\r\n");
				fw.write(sb.toString());
				sb.delete(0, sb.length());
			}
			logger.debug("Done");
		}
		finally {
			if (fw != null)
				try {
					fw.close();
				}
				catch(Exception e) {}
		}
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
	
	/**
	 * Generate a report CSV file with all the individual colors found in the image file
	 * @param args 0 is the image file path, 1 is the output file path
	 */
	public static void main(String[] args) {
		ColorToCSV util = new ColorToCSV();
		try {
			String imageFile = args.length > 0 ? args[0] : null;
			if (imageFile == null)
				throw new Exception("Missing arguments. Expected imageFile color.csv [colorPatterns]");
			String outFile = args.length > 1 ? args[1] : imageFile + ".csv";
			String colorPattern = args.length > 2 ? args[2] : null;
			util.process(imageFile, outFile, colorPattern);
		}
		catch(Exception e) {
			logger.error("Failed to get color", e);
		}
	}
}
