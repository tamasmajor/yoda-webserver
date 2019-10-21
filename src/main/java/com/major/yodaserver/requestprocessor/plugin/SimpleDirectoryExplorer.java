package com.major.yodaserver.requestprocessor.plugin;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

public class SimpleDirectoryExplorer implements DirectoryExplorer {

    @Override
    public String renderPage(File rootDir, File requestedDir) throws IOException {
        StringBuffer page = new StringBuffer();
        addPageOpening(page);
        if (existingDirectory(requestedDir)) {
            addCurrentDirectoryHeading(rootDir, requestedDir, page);
            page.append("<table>");
            addParentDirectoryItem(rootDir, requestedDir, page);
            addDirectories(rootDir, requestedDir, page);
            addFiles(rootDir, requestedDir, page);
            page.append("</table>");
        } else {
            addErrorBody(requestedDir, page);
        }
        addPageClosing(page);
        return page.toString();
    }

    private boolean existingDirectory(File requestedDir) {
        return requestedDir.exists() && requestedDir.isDirectory();
    }

    private void addCurrentDirectoryHeading(File rootDir, File requestedDir, StringBuffer page) throws  IOException {
        String currentDir = requestedDir.getCanonicalPath().replace(rootDir.getCanonicalPath(), "");
        page.append("<h2>Index of ").append(currentDir).append("</h2>");
    }

    private void addParentDirectoryItem(File rootDir, File requestedDir, StringBuffer page) throws IOException {
        if (requestedBelowRoot(rootDir, requestedDir)) {
            page.append("<tr><td></td><td>");
            page.append("<a href=\"");
            page.append(determineParent(rootDir, requestedDir));
            page.append("\">..</a>");
            page.append("</td></tr>");
        }
    }

    private boolean requestedBelowRoot(File rootDir, File requestedDir) throws IOException {
        String rootPath = rootDir.getCanonicalPath();
        String requestedPath = requestedDir.getCanonicalPath();
        return requestedPath.startsWith(rootPath) && !requestedPath.equals(rootPath);
    }

    private String determineParent(File rootDir, File requestedDir) throws IOException {
        String rootPath = rootDir.getCanonicalPath();
        String requestedParentPath = requestedDir.getParentFile().getCanonicalPath();
        String path;
        if (rootPath.equals(requestedParentPath)) {
            path = "/";
        } else {
            path = requestedParentPath.replace(rootPath, "");
        }
        return path;
    }

    private void addDirectories(File rootDir, File requestedDir, StringBuffer page) throws IOException {
        String parent = requestedDir.getCanonicalPath().replace(rootDir.getCanonicalPath(), "");
        Arrays.stream(requestedDir.listFiles())
              .filter(File::isDirectory)
              .map(File::getName)
              .sorted()
              .forEach(dirName ->
                  page.append("<tr><td>d</td><td>")
                      .append("<a href=\"").append(parent).append("/").append(dirName).append("\">").append(dirName)
                      .append("</a></td></tr>"));
    }

    private void addFiles(File rootDir, File requestedDir, StringBuffer page) throws IOException {
        String parent = requestedDir.getCanonicalPath().replace(rootDir.getCanonicalPath(), "");
        Arrays.stream(requestedDir.listFiles())
              .filter(File::isFile)
              .map(File::getName)
              .sorted()
              .forEach(dirName ->
                  page.append("<tr><td></td><td>")
                      .append("<a href=\"").append(parent).append("/").append(dirName).append("\">").append(dirName)
                      .append("</a></td></tr>"));
    }

    private void addErrorBody(File requestedDir, StringBuffer page) {
        if (!requestedDir.exists()) {
            page.append("<p>404 - No such file</p>");
        } else if (!requestedDir.isDirectory()) {
            page.append("<p>400 - Not a directory</p>");
        }
    }

    private void addPageOpening(StringBuffer page) {
        page.append("<html><head></head><body>");
    }

    private void addPageClosing(StringBuffer page) {
        page.append("</body></html>");
    }

}
