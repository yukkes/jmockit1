/*
 * Copyright (c) 2006 JMockit developers
 * This file is subject to the terms of the MIT license (see LICENSE.txt).
 */
package mockit.coverage;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import mockit.coverage.data.CoverageData;

final class DataFileMerging {
    @Nonnull
    private final List<File> inputFiles;

    DataFileMerging(@Nonnull String[] inputPaths) {
        inputFiles = new ArrayList<>(inputPaths.length);

        for (String path : inputPaths) {
            addInputFileToList(path.trim());
        }
    }

    private void addInputFileToList(@Nonnull String path) {
        if (!path.isEmpty()) {
            File inputFile = new File(path);

            if (inputFile.isDirectory()) {
                inputFile = new File(inputFile, "coverage.ser");
            }

            inputFiles.add(inputFile);
        }
    }

    @Nonnull
    CoverageData merge() throws IOException {
        CoverageData mergedData = null;

        for (File inputFile : inputFiles) {
            if (inputFile.exists()) {
                CoverageData existingData = CoverageData.readDataFromFile(inputFile);

                if (mergedData == null) {
                    mergedData = existingData;
                } else {
                    mergedData.merge(existingData);
                }
            }
        }

        if (mergedData == null) {
            throw new IllegalArgumentException("No input \"coverage.ser\" files found");
        }

        return mergedData;
    }
}
