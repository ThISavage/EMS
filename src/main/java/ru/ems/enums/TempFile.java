package ru.ems.enums;

public enum TempFile {
    BLOCK_TEMP_FILE("block", ".txt");
    private final String prefix;
    private final String suffix;

    TempFile(String prefix, String suffix) {
        this.prefix = prefix;
        this.suffix = suffix;
    }

    public String getPrefix() {
        return prefix;
    }

    public String getSuffix() {
        return suffix;
    }
}
