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

import com.maiereni.quora.io.bo.Postings;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.nio.ch.IOUtil;

import java.io.File;

import static org.junit.Assert.*;

/**
 * Validates the parsing of input files
 * @author Petre Maierean
 */
public class FileReaderTest {
    private static final Logger logger = LoggerFactory.getLogger(FileReaderTest.class);
    private static final FileReader reader = new FileReader();

    @Test
    public void testParseQuestionFiles() {
        try (BaseTestHelper helper = BaseTestHelper.getInstance("/sampleQuestions.html")){
            Postings postings = reader.getPostings(helper.getPath());
            assertNotNull("Not null", postings);
            assertTrue("The expected number of postings", postings.getPostings().size() == 6);
        }
        catch(Exception e) {
            logger.error("Failed to parse", e);
        }
    }

    @Test
    public void testParseAnswerFiles() {
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
