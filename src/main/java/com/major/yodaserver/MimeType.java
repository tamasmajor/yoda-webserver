package com.major.yodaserver;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public enum MimeType {
    CSS("css", "text/css");

    private static final Map<String, String> typesByExtension;
    private final String extension;
    private final String mimeType;

    static {
        typesByExtension = Arrays.stream(values()).collect(Collectors.toMap(v -> v.extension, v -> v.mimeType));
    }

    MimeType(String extension, String mimeType) {
        this.extension = extension;
        this.mimeType = mimeType;
    }

    public String getMimeType() {
        return mimeType;
    }

    public static String typeForExtension(String fileName) {
        String mimeType = null;
        int extensionStartIdx = fileName.lastIndexOf(".") + 1;
        if (extensionStartIdx >= 0) {
            String extension = fileName.substring(extensionStartIdx);
            mimeType = typesByExtension.get(extension);
        }
        return mimeType;
    }
}
