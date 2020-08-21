/**
 * ================================================================
 * Copyright (c) 2017-2020 Maiereni Software and Consulting Inc
 * ================================================================
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.maiereni.quora.io;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

/**
 *
 * @author Petre Maierean
 */
public class PersistContentMain {
    private static final Logger logger = LoggerFactory.getLogger(PersistContentMain.class);
    /**
     * Extracts and persist content
     *
     * @param args data.zip dest_folder
     */
    public static void main(final String[] args) {
        try {
            if (args.length < 2) {
                throw new Exception("Expected arguments: data.zip dest_folder");
            }
            PersistContent pc = new PersistContent();
            pc.extractContent(new File(args[0]), new File(args[1]));
        }
        catch(Exception e) {
            logger.error("Failed to presist", e);
        }
    }
}
