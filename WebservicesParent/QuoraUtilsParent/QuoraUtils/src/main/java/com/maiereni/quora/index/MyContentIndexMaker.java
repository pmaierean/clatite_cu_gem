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

import com.maiereni.quora.AbstractPayloadParser;
import com.maiereni.quora.index.bo.ContentIndex;
import com.maiereni.quora.index.bo.ContentItem;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;

/**
 *
 * @author Petre Maierean
 */
public class MyContentIndexMaker extends AbstractPayloadParser<ContentIndex> {
    private static final Logger logger = LoggerFactory.getLogger(MyContentIndexMaker.class);
    private static final String TOKEN = "href=\"";
    private static final String URL_PREFIX = "https://www.quora.com";
    private static final String PC = "/answer/Peter-Clings";
    /**
     * Parse and create new content
     * @param line
     * @param s
     * @param status
     * @return
     * @throws Exception
     */
    @Override
    public ContentIndex parse(final int line, final String s, final ContentIndex status) throws Exception {
        ContentIndex ret = new ContentIndex();
        if (status != null) {
            ret.setLinks(status.getLinks());
            status.setLinks(null);
        }
        else {
            ret.setLinks(new ArrayList<>());
        }
        int ix = 0;
        while(ix >= 0) {
            int iy = s.indexOf(TOKEN, ix);
            if (iy > 0) {
                iy += TOKEN.length();
                int iz = s.indexOf("\"", iy);
                if (iz < 0) {
                    throw new Exception("Error at line " + line);
                }
                String ref = s.substring(iy, iz);
                iy = iz;
                iz = ref.indexOf(PC);
                if (iz > 0) {
                    ContentItem item = new ContentItem();
                    String name = ref.substring(1, iz);
                    if (name.length() > 255) {
                        name.substring(0, 255);
                    }
                    item.setName(name);
                    item.setLink(URL_PREFIX + ref);
                    ret.getLinks().add(item);
                }
            }
            ix = iy;
        }
        logger.debug("Up to line {} found " + ret.getLinks().size(), line);
        return ret;
    }

    /**
     *
     * @param inputFile
     * @param outputFile
     * @throws Exception
     */
    public void parseAndSave(final String inputFile, final String outputFile) throws Exception {
       if (StringUtils.isNoneBlank(inputFile)) {
           File f = new File(inputFile);
           logger.debug("Parse " + inputFile);
           ContentIndex ci = parse(f);
           if (StringUtils.isNotBlank(outputFile)) {
               FileUtils.writeStringToFile(new File(outputFile), ci.toString(), "UTF-8");
               logger.debug("The result has been saved at " + outputFile);
           }
           else {
               logger.debug("Obtained: " + ci);
           }
       }
    }

    public static void main(final String[] args) {
        String inputFile = "C:\\Users\\pmaie\\Documents\\quora.txt";
        String outputFile = "C:\\Users\\pmaie\\Documents\\index.csv";
        try {
            if (args.length > 0) {
                inputFile = args[0];
                if (args.length > 1) {
                    outputFile = args[1];
                }
            }
            new MyContentIndexMaker().parseAndSave(inputFile, outputFile);
        }
        catch(Exception e) {
            logger.error("Failed to parse", e);
        }
    }

}

