package com.major.yodaserver.requestprocessor.plugin;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

import org.apache.commons.text.StringEscapeUtils;

public class SimpleDirectoryExplorer implements DirectoryExplorer {
    private String serverVersion;

    @Override
    public String renderPage(File rootDir, File requestedDir) throws IOException {
        StringBuffer page = new StringBuffer();
        String requestedPath = getRequestedPath(requestedDir, rootDir);
        String parent = getParentPathForRequested(requestedDir, rootDir);
        addPageOpening(requestedPath, page);
        if (existingDirectory(requestedDir)) {
            addCurrentDirectoryHeading(requestedPath, page);
            page.append("<table>");
            addParentDirectoryItem(rootDir, requestedDir, page);
            addDirectories(requestedDir, parent, page);
            addFiles(requestedDir, parent, page);
            page.append("</table>");
        } else {
            addErrorBody(requestedDir, page);
        }
        addServerSpecification(page);
        addPageClosing(page);
        return page.toString();
    }

    private boolean existingDirectory(File requestedDir) {
        return requestedDir.exists() && requestedDir.isDirectory();
    }

    private void addCurrentDirectoryHeading(String currentDirectoryPathFromRoot, StringBuffer page) {
        page.append("<h2>Index of ").append(currentDirectoryPathFromRoot).append("</h2>");
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

    private void addDirectories(File requestedDir, String parent, StringBuffer page) {
        List<File> dirs = Arrays.stream(requestedDir.listFiles()).filter(File::isDirectory).collect(Collectors.toList());
        addResourceListing(dirs, parent, page);
    }

    private void addFiles(File requestedDir, String parent, StringBuffer page) {
        List<File> files = Arrays.stream(requestedDir.listFiles()).filter(File::isFile).collect(Collectors.toList());
        addResourceListing(files, parent, page);
    }

    private void addResourceListing(List<File> resources, String parent, StringBuffer page) {
        resources.stream()
                 .sorted(Comparator.comparing(File::getName, String.CASE_INSENSITIVE_ORDER))
                 .forEach(resource -> addResourceListingElement(parent, resource, page));
    }

    private void addResourceListingElement(String parent, File resource, StringBuffer page) {
        String resourceName = StringEscapeUtils.escapeHtml4(resource.getName());
        String directoryMarker = resource.isDirectory() ? "d" : "";
        page.append("<tr><td>").append(directoryMarker).append("</td><td>")
            .append("<a href=\"").append(parent).append("/").append(resourceName).append("\">").append(resourceName)
            .append("</a></td></tr>");
    }


    private void addErrorBody(File requestedDir, StringBuffer page) {
        if (!requestedDir.exists()) {
            page.append("<p>404 - No such file</p>");
        } else if (!requestedDir.isDirectory()) {
            page.append("<p>400 - Not a directory</p>");
        }
    }

    private void addPageOpening(String requestedPath, StringBuffer page) {
        page.append("<html><head>")
            .append("<meta charset=\"utf-8\">")
            .append("<meta name=\"viewport\" content=\"width=device-width\">")
            .append("<style>td { padding: 2px 12px 2px 12px; }</style>")
            .append("<title>Index of ")
            .append(requestedPath)
            .append("</title></head><body>");
    }

    private void addPageClosing(StringBuffer page) {
        page.append("</body></html>");
    }

    private void addServerSpecification(StringBuffer page) throws IOException {
        page.append("<p><i>Generated by Yoda.Server v").append(getServerVersion()).append("</i></p>");
    }

    private String getServerVersion() throws IOException {
        if (serverVersion == null) {
            Properties properties = new Properties();
            properties.load(this.getClass().getClassLoader().getResourceAsStream("project.properties"));
            serverVersion = properties.getProperty("server.version");
        }
        return serverVersion;
    }

    private String getRequestedPath(File requestedDir, File rootDir) throws IOException {
        String path = requestedDir.getCanonicalPath().replace(rootDir.getCanonicalPath(), "");
        if (path.length() == 0) {
            path = "/";
        }
        return path;
    }

    private String getParentPathForRequested(File requestedDir, File rootDir) throws IOException {
        return StringEscapeUtils.escapeHtml4(requestedDir.getCanonicalPath().replace(rootDir.getCanonicalPath(), ""));
    }

}
