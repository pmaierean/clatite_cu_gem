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

import com.maiereni.quora.io.bo.Modifier;
import com.maiereni.quora.io.bo.ModifierType;
import com.maiereni.quora.io.bo.Posting;
import com.maiereni.quora.io.bo.Postings;
import javafx.geometry.Pos;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Contains utility methods about sections
 * @author Petre Maierean
 */
public class SectionUtils {
    private static final Logger logger = LoggerFactory.getLogger(SectionUtils.class);

    /**
     * Get all postings that have images
     *
     * @param postings
     * @return
     */
    public final List<Posting> getPostingsWithImages(final Postings postings) {
        logger.debug("list all the postings that contain images");
        final List<Posting> ret = new ArrayList<>();
        if (!(postings == null || postings.getPostings() == null)){
            postings.getPostings().forEach( p -> {
                if (!getPostingImage(p).isEmpty()) {
                    ret.add(p);
                }
            });
        }
        logger.debug("Found {} postings with images", ret.size());
        return ret;
    }

    /**
     * Find the images of a posting
     *
     * @param posting
     * @return
     */
    public List<String> getPostingImage(final Posting posting) {
        final List<String> ret = new ArrayList<>();
        if (!(posting == null || posting.getSections() == null)) {
            posting.getSections().forEach( s -> {
                if (s.getSpans() != null) {
                    s.getSpans().forEach( sp -> {
                        Modifier m = sp.getModifier();
                        if (m != null && m.getType().equals(ModifierType.IMAGE)) {
                            if (!ret.contains(m.getUrl())) {
                                ret.add(m.getUrl());
                            }
                        }
                    });
                }
            });
        }
        return ret;
    }

    /**
     * Checks if the posting has images
     * @param posting
     * @return
     */
    public boolean hasPostingImage(final Posting posting) {
        return getPostingImage(posting).size() > 0;
    }

    /**
     * Find postings by the pattern in the title
     *
     * @param postings
     * @param titlePattern
     * @return
     */
    public List<Posting> findPostingByTitle(final Postings postings, final String titlePattern) {
        logger.debug("list all the postings that match the title");
        final List<Posting> ret = new ArrayList<>();
        if (StringUtils.isNotBlank(titlePattern)) {
            Pattern pattern = Pattern.compile(titlePattern);
            if ((postings == null || postings.getPostings() == null)) {
                postings.getPostings().forEach(p -> {
                    if (p.getTitle() != null && pattern.matcher(p.getTitle()).matches()) {
                        ret.add(p);
                    }
                });
            }
        }
        logger.debug("Found {} postings with images", ret.size());
        return ret;
    }

}
