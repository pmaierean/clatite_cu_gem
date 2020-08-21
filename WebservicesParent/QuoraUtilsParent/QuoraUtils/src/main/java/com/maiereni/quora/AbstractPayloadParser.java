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
package com.maiereni.quora;

import org.apache.commons.lang3.StringUtils;

import java.io.*;

/**
 * Parse an HTTP content
 * @author Petre Maierean
 */
public abstract class AbstractPayloadParser<T> {

    public abstract T parse(final int lineNumber, final String s, final T status) throws Exception;

    /**
     * Parse the content of a string
     * @param s
     * @return
     * @throws Exception
     */
    public T parse(final String s) throws Exception {
        if (StringUtils.isBlank(s)) {
            throw new Exception("The argument is either null or blank");
        }
        return parse(new StringReader(s));
    }

    /**
     * Parse the content of a file
     * @param f
     * @return
     * @throws Exception
     */
    public T parse(final File f) throws Exception {
        if (f == null)
            throw new Exception("The argument is null");
        if (!f.exists())
            throw new Exception("The file cannot be found");
        try (FileReader fr = new FileReader(f)) {
            return parse(fr);
        }
    }

    protected T parse(final Reader reader) throws Exception {
        T ret = null;
        try(LineNumberReader lnr = new LineNumberReader(reader)) {
            String s = null;
            int line = -1;
            while ((s = lnr.readLine()) != null) {
                ret = parse(line++, s, ret);
            }
        }
        return ret;
    }

}
