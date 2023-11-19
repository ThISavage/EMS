package ru.ems.enums;

import java.io.File;

public enum MainFile {
    MERGE(new File("src" + File.separator + "main" + File.separator + "resources" + File.separator + "merge.txt")),
    BLOCKS(new File("src" + File.separator + "main" + File.separator + "resources" + File.separator + "blocks.txt")),
    SORTED(new File("src" + File.separator + "main" + File.separator + "resources" + File.separator + "sorted.txt")),
    UNSORTED(new File("src" + File.separator + "main" + File.separator + "resources" + File.separator + "unsorted.txt"));
    private final File file;

    MainFile(File file) {
        this.file = file;
    }

    public File getFile() {
        return file;
    }
}
