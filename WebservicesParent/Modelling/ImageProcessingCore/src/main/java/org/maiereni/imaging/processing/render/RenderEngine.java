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

import org.maiereni.imaging.common.stl.vo.Triangle;

/**
 * Defines a utility for rendering an image to 
 * vertexes
 * 
 * @author petre maierean
 *
 */
public interface RenderEngine {
	/**
	 * Loads the 2D image
	 * @param image
	 * @throws Exception
	 */
	void loadImage(String imageFile) throws Exception;
	/**
	 * Tests if there are more triangles 
	 * @return
	 */
	boolean hasNextTriangle();
	/**
	 * Get the next triangle
	 * @return
	 */
	Triangle nextTriangle();
	/**
	 * Estimates the number of triangles for the image
	 * @return
	 */
	int size();
}
