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
package com.maiereni.quora.io;

import com.maiereni.quora.AbstractPayloadParser;
import com.maiereni.quora.index.ContentIndexReader;
import com.maiereni.quora.index.bo.ContentBody;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

/**
 * Extracts the content from the raw HTML file of a posting from Quora. Convert that content into a Posting object
 *
 * @author Petre Maierean
 */
public class HTMLContentExtractor extends AbstractPayloadParser<ContentBody> {
    private static final Logger logger = LoggerFactory.getLogger(HTMLContentExtractor.class);
    private ContentIndexReader reader = new ContentIndexReader();
    private static final String CONTENT_BEGIN = "\"{\\\"data\\\":{\\\"answer\\\":{";
    private static final String CONTENT_END = "}\";";

    @Override
    public ContentBody parse(int lineNumber, String s, ContentBody status) throws Exception {
        ContentBody cb = status;
        if (cb == null) {
            cb = new ContentBody();
        }
        if (!cb.isOpen()) {
            int ix = s.indexOf(CONTENT_BEGIN);
            if (ix > 0) {
               int iy = s.indexOf(CONTENT_END, ix);
               String content = "";
               if (iy > ix) {
                    content = s.substring(ix, iy + CONTENT_END.length());
               }
               else {
                    content = s.substring(iy);
                    cb.setOpen(true);
               }
               if (cb.getBody() != null) {
                    content = cb.getBody() + "\r\n" + content;
               }
               cb.setBody(content);
            }
        }
        else {
            int ix = s.indexOf(CONTENT_END + CONTENT_END.length());
            String content = "";
            if (ix > 0) {
                content = s.substring(0, ix);
                cb.setOpen(false);
            }
            else {
                content = s;
            }
            if (cb.getBody() != null) {
                content = cb.getBody() + "\r\n" + content;
            }
            cb.setBody(content);
        }
        return cb;
    }

    /**
     * Extract the content of a raw HTML file
     *
     * @param fin
     * @param fout
     * @throws Exception
     */
    public void extractContent(final File fin, final File fout) throws Exception {
        if (fin != null && fout != null && fin.exists()) {
            try {
                logger.debug("Parse " + fin.getPath());
                ContentBody body = parse(fin);
                if (StringUtils.isNotBlank(body.getBody())) {
                    String rep = processBody(body.getBody());
                    FileUtils.writeStringToFile(fout, rep, "UTF-8");
                }
                else {
                    logger.error("No content found");
                }
            }
            catch(Exception e) {
                logger.error("Failed to extract content", e);
            }
        }
    }

    private String processBody(final String body) throws Exception {
        String ret = null;
        ret = body.replaceAll("\\x5c\\x22", "\"") ;
        ret = ret.replaceAll("\\\\\"", "\\\"");
        ret = ret.substring(1);
        ret = ret.substring(0, ret.length() - 1);
        JSONObject obj = new JSONObject(ret);
        JSONObject data = obj.getJSONObject("data");
        JSONObject answer = data.getJSONObject("answer");
        String s = answer.getString("content");
        return s.replaceAll("\\x5c\\x22", "\"");
    }

    /**
     * Extract the content of a raw HTML file downloaded from Quora.com
     * @param args
     */
    public static void main(final String[] args) {
        try {
            if (args.length > 2) {
                new HTMLContentExtractor().extractContent(new File(args[0]), new File(args[1]));
            }
        }
        catch(Exception e) {
            logger.error("Failed to parse", e);
        }
    }

}
