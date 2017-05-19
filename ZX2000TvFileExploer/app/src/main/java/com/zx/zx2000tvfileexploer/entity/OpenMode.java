package com.zx.zx2000tvfileexploer.entity;


public enum OpenMode {

    UNKNOWN,
    FILE,

    /**
     * Custom file types like apk/images/downloads (which don't have a defined path)
     */
    CUSTOM,

    OTG;

    /**
     * Get open mode based on the id assigned.
     * Generally used to retrieve this type after config change or to send enum as argument
     * @param ordinal the position of enum starting from 0 for first element
     * @return
     */
    public static OpenMode getOpenMode(int ordinal) {
        return OpenMode.values()[ordinal];
    }
}
