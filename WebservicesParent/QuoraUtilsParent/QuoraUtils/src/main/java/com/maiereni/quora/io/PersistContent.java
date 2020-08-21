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

import com.maiereni.quora.index.bo.ContentIndex;
import com.maiereni.quora.index.bo.ContentItem;
import com.maiereni.quora.io.bo.Posting;
import com.maiereni.quora.io.bo.Postings;
import com.maiereni.quora.io.bo.PostingsType;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Given the content of a ZIP file from quora.com, this class converts all the postings into JSON and persists them
 * in a local folder
 *
 * @author Petre Maierean
 */
public class PersistContent {
    private static final Logger logger = LoggerFactory.getLogger(PersistContent.class);
    private SectionUtils utils = new SectionUtils();
    private ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Extract content
     * @param zipFile
     * @param destDir
     * @throws Exception
     */
    public void extractContent(final File zipFile, final File destDir) throws Exception {
        if (destDir == null || destDir.isFile()) {
            throw new Exception("Cannot use a file");
        }
        logger.debug("Read content from " + zipFile.getPath());
        Postings postings = new FileReader() {
            @Override
            protected Status parseNext(File f, Element e, Status status) throws Exception {
                Status ret = super.parseNext(f,e, status);
                if (ret.p != null) {
                    List<String> images = utils.getPostingImage(ret.p);
                    if (images.size() > 0) {
                        for (String image : images) {
                            copyImageFile(image, f.getParentFile(), destDir);
                        }
                    }
                }
                return ret;
            }
        }.getPostings(zipFile);
        if (postings.getType().equals(PostingsType.ANSWERS)) {
            ContentIndex ci = new ContentIndex();
            ci.setLinks(new ArrayList<>());
            for(Posting p : postings.getPostings()) {
                ContentItem item = savePosting(p, destDir);
                ci.getLinks().add(item);
            };
            File fIndex = new File(destDir, "index.csv");
            FileUtils.writeStringToFile(fIndex, ci.toString(), "UTF-8");
            logger.debug("Extracted a number of {} responses", ci.getLinks().size());
        }
    }

    protected ContentItem savePosting(final Posting p, final File destDir) throws Exception {
        ContentItem ret = new ContentItem();
        String title = p.getTitle();
        ret.setName(title);
        ret.setCreationDate(p.getPublishDate());
        if (title.length() > 126) {
            title = title.substring(0, 126);
        }
        String html = p.getHtml();
        p.setHtml(null);
        File fOut = new File(destDir, title + ".json");
        objectMapper.writeValue(fOut, p);
        logger.debug("File has been created at " + fOut.getPath());
        String relative = destDir.toURI().relativize(fOut.toURI()).getPath();
        ret.setLink(fOut.getPath());
        if (StringUtils.isNotBlank(html)){
            File fOut1 = new File(destDir, title + ".html");
            FileUtils.writeStringToFile(fOut1, html, "UTF-8");
            ret.setSize(html.length());
        }
        ret.setImages(utils.hasPostingImage(p));
        return ret;
    }

    private void copyImageFile(final String name, final File sourceDir, final File destDir) throws Exception {
        File fImage = new File(destDir, name);
        if (!fImage.exists()) {
            File fIn = new File(sourceDir, name);
            if (fIn.exists()) {
                FileUtils.copyFile(fIn, fImage);
                logger.debug("The image file has been copied to " + fImage.getPath());
            }
        }
    }

}
