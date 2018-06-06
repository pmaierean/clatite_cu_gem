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
package org.maiereni.imaging.processing.render;

/**
 * Defines an engine to compute the 3D depth of a pixel
 * @author Petre Maierean
 *
 */
public interface Modeler {
	/**
	 * Calculates the origin for the pixel in the model
	 * @param x
	 * @param y
	 * @return
	 */
	float calculatePosition(int x, int y);
	/**
	 * Get the maximum number of pixels on the Y coordonate
	 * @return
	 */
	public int getHeight();
	/**
	 * Get the maximum number of pixels on the X coordonate
	 * @return
	 */
	public int getWidth();
	/**
	 * Verify if the point defined by x and y should be visible on the 
	 * output
	 * @param x
	 * @param y
	 * @return
	 */
	public boolean isVisible(int x, int y);
}
