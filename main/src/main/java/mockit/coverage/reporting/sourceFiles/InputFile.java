/*
 * Copyright (c) 2006 JMockit developers
 * This file is subject to the terms of the MIT license (see LICENSE.txt).
 */
package mockit.coverage.reporting.sourceFiles;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class InputFile {
    @Nonnull
    final String filePath;
    @Nonnull
    private final File sourceFile;
    @Nonnull
    private final BufferedReader input;

    @Nullable
    public static InputFile createIfFileExists(@Nonnull List<File> sourceDirs, @Nonnull String filePath)
            throws FileNotFoundException, IOException {
        File sourceFile = findSourceFile(sourceDirs, filePath);
        return sourceFile == null ? null : new InputFile(filePath, sourceFile);
    }

    @Nullable
    private static File findSourceFile(@Nonnull List<File> sourceDirs, @Nonnull String filePath) {
        int p = filePath.indexOf('/');
        String topLevelPackage = p < 0 ? "" : filePath.substring(0, p);
        int n = sourceDirs.size();

        for (int i = 0; i < n; i++) {
            File sourceDir = sourceDirs.get(i);
            File sourceFile = getSourceFile(sourceDir, topLevelPackage, filePath);

            if (sourceFile != null) {
                giveCurrentSourceDirHighestPriority(sourceDirs, i);
                addRootSourceDirIfNew(sourceDirs, filePath, sourceFile);
                return sourceFile;
            }
        }

        return null;
    }

    @Nullable
    private static File getSourceFile(@Nonnull File sourceDir, @Nonnull final String topLevelPackage,
            @Nonnull String filePath) {
        File file = new File(sourceDir, filePath);

        if (file.exists()) {
            return file;
        }

        File[] subDirs = sourceDir.listFiles((FileFilter) subDir -> subDir.isDirectory() && !subDir.isHidden()
                && !subDir.getName().equals(topLevelPackage));

        if (subDirs != null && subDirs.length > 0) {
            for (File subDir : subDirs) {
                File sourceFile = getSourceFile(subDir, topLevelPackage, filePath);

                if (sourceFile != null) {
                    return sourceFile;
                }
            }
        }

        return null;
    }

    private static void giveCurrentSourceDirHighestPriority(@Nonnull List<File> sourceDirs,
            @Nonnegative int currentSourceDirIndex) {
        if (currentSourceDirIndex > 0) {
            File firstSourceDir = sourceDirs.get(0);
            File currentSourceDir = sourceDirs.get(currentSourceDirIndex);

            if (!firstSourceDir.getPath().startsWith(currentSourceDir.getPath())) {
                sourceDirs.set(currentSourceDirIndex, firstSourceDir);
                sourceDirs.set(0, currentSourceDir);
            }
        }
    }

    private static void addRootSourceDirIfNew(@Nonnull List<File> sourceDirs, @Nonnull String filePath,
            @Nonnull File sourceFile) {
        String sourceFilePath = sourceFile.getPath();
        String sourceRootDir = sourceFilePath.substring(0, sourceFilePath.length() - filePath.length());
        File newSourceDir = new File(sourceRootDir);

        if (!sourceDirs.contains(newSourceDir)) {
            sourceDirs.add(0, newSourceDir);
        }
    }

    private InputFile(@Nonnull String filePath, @Nonnull File sourceFile) throws FileNotFoundException, IOException {
        this.filePath = filePath;
        this.sourceFile = sourceFile;
        input = new BufferedReader(new FileReader(sourceFile, StandardCharsets.UTF_8));
    }

    @Nonnull
    String getSourceFileName() {
        return sourceFile.getName();
    }

    @Nonnull
    String getSourceFilePath() {
        String path = sourceFile.getPath();
        return path.startsWith("..") ? path.substring(3) : path;
    }

    @Nullable
    String nextLine() throws IOException {
        return input.readLine();
    }

    void close() throws IOException {
        input.close();
    }
}
