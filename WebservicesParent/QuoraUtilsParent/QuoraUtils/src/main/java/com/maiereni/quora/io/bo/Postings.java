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
package com.maiereni.quora.io.bo;

import java.io.Serializable;
import java.util.List;

/**
 *
 * @author Petre Maierean
 */
public class Postings implements Serializable {
    private PostingsType type;
    private List<Posting> postings;

    public PostingsType getType() {
        return type;
    }

    public void setType(PostingsType type) {
        this.type = type;
    }

    public List<Posting> getPostings() {
        return postings;
    }

    public void setPostings(List<Posting> postings) {
        this.postings = postings;
    }
}
