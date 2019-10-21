package com.major.yodaserver.requestprocessor.plugin;

import java.io.File;
import java.io.IOException;

public interface DirectoryExplorer {
    String renderPage(File root, File requestedResource) throws IOException;
}
