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
package com.maiereni.utils.archiving.zip;

import com.maiereni.utils.archiving.ArchivingUtility;
import com.maiereni.utils.archiving.BaseArchivingUtility;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 *
 * @author Petre Maierean
 */
public class ZipArchivingUtil extends BaseArchivingUtility {
    private Compressor compressor = new Compressor();
    private Decopressor decopressor = new Decopressor();

    /**
     * Decompress that archive file to a temporary directory
     * @param archiveFile
     * @return
     * @throws Exception
     */
    @Override
    public File decompress(File archiveFile) throws Exception {
        if (archiveFile == null || !archiveFile.isFile()) {
            throw new Exception("The archive is null");
        }
        if (!isArchive(archiveFile)) {
            throw new Exception("The file is not an archive");
        }
        File dest = getTempDir();
        try {
            decopressor.decompress(archiveFile, dest);
            return dest;
        }
        catch(Exception ex) {
            FileUtils.deleteDirectory(dest);
            throw ex;
        }
    }

    /**
     * Compress an input
     * @param input
     * @param name the name of the compressed file
     * @return
     * @throws Exception
     */
    @Override
    public File compress(final File input, final String name) throws Exception {
        if (input == null) {
            throw new Exception("The input is null");
        }
        compressor.compress(input.getPath(), name);
        return new File(name);
    }

    public boolean isArchive(File f) {
        boolean ret = false;
        int fileSignature = 0;
        try (RandomAccessFile raf = new RandomAccessFile(f, "r")) {
            fileSignature = raf.readInt();
            ret = fileSignature == 0x504B0304 || fileSignature == 0x504B0506 || fileSignature == 0x504B0708;
        } catch (IOException e) {
            // handle if you like
        }
        return ret;
    }
}
