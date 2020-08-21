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

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.jsoup.Connection;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;

/**
 *
 * @author Petre Maierean
 */
public class BaseTestHelper implements Closeable {
    private File f;

    /**
     * Copy a resource to a temp folder
     *
     * @param resource
     * @return
     * @throws Exception
     */
    public static BaseTestHelper getInstance(final String resource) throws Exception {
        BaseTestHelper ret = new BaseTestHelper();
        ret.f = ret.copyTemp(resource);
        return ret;
    }

    public String getPath() {
        return f != null ? f.getPath() : null;
    }

    public File getFile() {
        return f;
    }

    @Override
    public void close() throws IOException {
        if (f != null) {
            if (!f.delete())
                f.deleteOnExit();
        }
        f = null;
    }

    private File copyTemp(final String resource) throws Exception {
        File tmp = File.createTempFile("sample", "html");
        byte[] buffer = IOUtils.resourceToByteArray(resource);
        FileUtils.writeByteArrayToFile(tmp, buffer);
        return tmp;
    }
}
