package com.major.yodaserver;

import org.junit.Test;

import com.major.yodaserver.common.MimeType;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class MimeTypeTest {

    @Test
    public void typeForExtension_fileNameWithKnownExtension_returnCorrespondingMimeType() {
        // given
        String fileName = "the.Style.css";
        // when
        String mimeType = MimeType.typeForExtension(fileName);
        // then
        assertEquals(mimeType, MimeType.CSS.getMimeType());
    }

    @Test
    public void typeForExtension_fileNameWithUnknownExtension_returnsNull() {
        // given
        String fileName = "theStyle.unknown";
        // when
        String mimeType = MimeType.typeForExtension(fileName);
        // then
        assertNull(mimeType);
    }

    @Test
    public void typeForExtension_fileNameWithoutExtension_returnsNull() {
        // given
        String fileName = "theStyle";
        // when
        String mimeType = MimeType.typeForExtension(fileName);
        // then
        assertNull(mimeType);
    }

    @Test
    public void typeForExtension_fileNameWithoutExtensionWithTrailingDot_returnsNull() {
        // given
        String fileName = "theStyle.";
        // when
        String mimeType = MimeType.typeForExtension(fileName);
        // then
        assertNull(mimeType);
    }

}