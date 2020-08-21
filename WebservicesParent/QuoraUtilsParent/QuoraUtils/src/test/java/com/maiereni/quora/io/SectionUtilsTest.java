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

import com.maiereni.quora.io.bo.Posting;
import com.maiereni.quora.io.bo.Postings;
import com.maiereni.quora.io.bo.Section;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static org.junit.Assert.*;

/**
 * Unit test class for the SectionUtils
 *
 * @author Petre Maierean
 */
public class SectionUtilsTest {
    private static final Logger logger = LoggerFactory.getLogger(SectionUtilsTest.class);
    private SectionUtils sectionUtils = new SectionUtils();
    private FileReader reader = new FileReader();

    @Test
    public void testSearchImages() {
        try (BaseTestHelper helper = BaseTestHelper.getInstance("/sampleAnswers.html")){
            Postings postings = reader.getPostings(helper.getPath());
            assertNotNull("Not null", postings);
            assertTrue("The number of positings", postings.getPostings().size()==4);
            List<Posting> ps = sectionUtils.getPostingsWithImages(postings);
            assertTrue("Found on posting with image", ps.size() == 1);
            List<String> images = sectionUtils.getPostingImage(ps.get(0));
            assertTrue("Found one image", images.size() == 1);
            assertEquals("Expected image name", "images/qimg-78bdea20751233106c99a4bee45b49de", images.get(0));
            String title = ps.get(0).getTitle();
            logger.debug("Name: " + title);
        }
        catch(Exception e) {
            logger.error("Failed to parse", e);
            fail("Error parsing the answers file");
        }
    }

    @Test
    public void testSeachByTitle() {
        try (BaseTestHelper helper = BaseTestHelper.getInstance("/sampleAnswers.html")){
            Postings postings = reader.getPostings(helper.getPath());
            assertNotNull("Not null", postings);
            assertTrue("The number of positings", postings.getPostings().size()==4);

        }
        catch(Exception e) {
            logger.error("Failed to parse", e);
            fail("Error parsing the answers file");
        }
    }
}
