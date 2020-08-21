/**
 * ================================================================
 * Copyright (c) 2017-2018 Maiereni Software and Consulting Inc
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
package com.maiereni.quora.index;

import com.maiereni.quora.index.bo.ContentIndex;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

/**
 *
 * @author Petre Maierean
 */
public class ContentIndexUpdater extends ContentIndexReader {
    private static final Logger logger = LoggerFactory.getLogger(ContentIndexUpdater.class);

    /**
     * Download the content for all files
     *
     * @param inputFile
     * @param destFolder
     * @throws Exception
     */
    public void downloadFiles(final File inputFile, final File destFolder) throws Exception {
        final ContentIndex ci = parse(inputFile);
        ci.getLinks().forEach(contentItem -> {
            if (contentItem.isActive()) {
                File fout = new File(destFolder, contentItem.getName() + ".txt");
                if (fout.exists()) {
                    contentItem.setActive(false);
                }
            }
        });
        String s = ci.toString();
        FileUtils.writeStringToFile(inputFile, s, "UTF8");
    }


    public static void main(final String[] args) {
        try {
            if (args.length > 1) {

                new ContentIndexUpdater().downloadFiles(new File(args[0]), new File(args[1]));
            }
        }
        catch(Exception e) {
            logger.error("Failed to parse", e);
        }
    }
}
