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
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;

/**
 * Reads the content from the CSV file that contains the index of the files
 *
 * @author Petre Maierean
 */
public class ContentIndexReader extends AbstractPayloadParser<ContentIndex> {

    @Override
    public ContentIndex parse(int lineNumber, String s, ContentIndex status) throws Exception {
        ContentIndex ret = new ContentIndex();
        if (status != null) {
            ret.setLinks(status.getLinks());
            status.setLinks(null);
        }
        else {
            ret.setLinks(new ArrayList<>());
        }
        if (StringUtils.isNotBlank(s)) {
            String[] toks = s.split(",");
            if (toks.length > 1) {
                ContentItem item = new ContentItem();
                item.setName(toks[0]);
                item.setLink(toks[1]);
                if (toks.length == 6) {
                    item.setCreationDate(toks[2]);
                    item.setSize(Integer.parseInt(toks[3]));
                    item.setImages("1".equals(toks[4]));
                    item.setActive("1".equals(toks[5]));
                }
                ret.getLinks().add(item);
            }
        }
        return ret;
    }
}
