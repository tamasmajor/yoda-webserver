package com.major.yodaserver.requestprocessor.plugin;

import java.io.File;
import java.io.IOException;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class SimpleDirectoryExplorerTest {

    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();

    private final SimpleDirectoryExplorer directoryExplorer = new SimpleDirectoryExplorer();


    @Test
    public void renderPage_addsCurrentDirectoryHeading() throws IOException {
        // given
        File anEmptyDir = tempFolder.newFolder("anEmptyDir");
        // when
        String page = directoryExplorer.renderPage(tempFolder.getRoot(), anEmptyDir);
        // then
        assertBodyEquals("<h2>Index of /anEmptyDir</h2><table><tr><td></td><td><a href=\"/\">..</a></td></tr></table>", page);
    }

    @Test
    public void renderPage_requestedDirectoryIsUnderRoot_addsParentAsRoot() throws IOException {
        // given
        File anEmptyDir = tempFolder.newFolder("anEmptyDir");
        // when
        String page = directoryExplorer.renderPage(tempFolder.getRoot(), anEmptyDir);
        // then
        assertBodyContains("<table><tr><td></td><td><a href=\"/\">..</a></td></tr></table>", page);
    }

    @Test
    public void renderPage_requestedDirectoryIsNotStraightUnderRoot_addsParent() throws IOException {
        // given
        File subDirectory = tempFolder.newFolder("topLevel", "subDirectory");
        // when
        String page = directoryExplorer.renderPage(tempFolder.getRoot(), subDirectory);
        // then
        assertBodyContains("<table><tr><td></td><td><a href=\"/topLevel\">..</a></td></tr></table>", page);
    }

    @Test
    public void renderPage_requestedDirectoryIsRoot_doesNotAddParentEntry() throws IOException {
        // given
        // when
        String page = directoryExplorer.renderPage(tempFolder.getRoot(), tempFolder.getRoot());
        // then
        assertBodyContains("<table></table>", page);
    }

    @Test
    public void renderPage_providedDirectoryDoesNotExists_rendersErrorPage() throws IOException {
        // given
        // when
        String page = directoryExplorer.renderPage(tempFolder.getRoot(),
                                                   new File(tempFolder.getRoot().getPath() + "/doesNotExists"));
        // then
        assertBodyContains("<p>404 - No such file</p>", page);
    }

    @Test
    public void renderPage_providedFileIsNotADirectory_rendersErrorPage() throws IOException {
        // given
        File aFile = tempFolder.newFile("aFile");
        // when
        String page = directoryExplorer.renderPage(tempFolder.getRoot(), aFile);
        // then
        assertBodyContains("<p>400 - Not a directory</p>", page);
    }

    @Test
    public void renderPage_containsDirectories_addDirectoriesInAlphanumericOrder() throws IOException {
        // given
        File topLevel = tempFolder.newFolder("topLevel");
        tempFolder.newFolder("topLevel", "WuffWuff");
        tempFolder.newFolder("topLevel", "01");
        tempFolder.newFolder("topLevel", ".wuffWuff");
        // when
        String page = directoryExplorer.renderPage(tempFolder.getRoot(), topLevel);
        // then
        assertBodyContains("<table>" +
                "<tr><td></td><td><a href=\"/\">..</a></td></tr>" +
                "<tr><td>d</td><td><a href=\"/topLevel/.wuffWuff\">.wuffWuff</a></td></tr>" +
                "<tr><td>d</td><td><a href=\"/topLevel/01\">01</a></td></tr>" +
                "<tr><td>d</td><td><a href=\"/topLevel/WuffWuff\">WuffWuff</a></td></tr>" +
                "</table>", page);
    }

    @Test
    public void renderPage_containsFiles_addFilesInAlphanumericOrderAfterDirectories() throws IOException {
        // given
        File topLevel = tempFolder.newFolder("topLevel");
        tempFolder.newFolder("topLevel", "WuffWuff");
        new File(topLevel + "/fileC.txt").createNewFile();
        new File(topLevel + "/01.pdf").createNewFile();
        new File(topLevel + "/fileA.txt").createNewFile();
        // when
        String page = directoryExplorer.renderPage(tempFolder.getRoot(), topLevel);
        // then
        assertBodyContains("<table>" +
                "<tr><td></td><td><a href=\"/\">..</a></td></tr>" +
                "<tr><td>d</td><td><a href=\"/topLevel/WuffWuff\">WuffWuff</a></td></tr>" +
                "<tr><td></td><td><a href=\"/topLevel/01.pdf\">01.pdf</a></td></tr>" +
                "<tr><td></td><td><a href=\"/topLevel/fileA.txt\">fileA.txt</a></td></tr>" +
                "<tr><td></td><td><a href=\"/topLevel/fileC.txt\">fileC.txt</a></td></tr>" +
                "</table>", page);
    }

    private void assertBodyContains(String expectedBody, String actualPage) {
        String actualBody = extractBodyFromPage(actualPage);
        assertTrue(actualBody.contains(expectedBody));
    }

    private void assertBodyEquals(String expectedBody, String actualPage) {
        String actualBody = extractBodyFromPage(actualPage);
        assertEquals(expectedBody, actualBody);
    }

    private String extractBodyFromPage(String actualPage) {
        int startIndex = 6 + actualPage.indexOf("<body>");
        int endIndex = actualPage.indexOf("</body>");
        return actualPage.substring(startIndex, endIndex);
    }

}