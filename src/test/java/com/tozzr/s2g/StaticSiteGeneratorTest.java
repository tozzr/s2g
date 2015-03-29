package com.tozzr.s2g;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class StaticSiteGeneratorTest {

	private StaticSiteGenerator g;
	private final String baseDir = "./src/test/resources";
	
	@Before
	public void setUp() throws Exception {
		createFile("/foo.md", "some text");
		g = new StaticSiteGenerator(baseDir);
	}
	
	@After
	public void tearDown() throws Exception {
		FileUtils.cleanDirectory(new File(baseDir));
	}

	private void createFile(String filename, String content) throws IOException {
		FileUtils.write(new File(baseDir + filename), content);
	}

	private String getContent(String filename) throws IOException {
		String content = FileUtils.readFileToString(new File(baseDir + filename));
		return content;
	}

	@Test
	public void createHtmlDocumentForMarkdownFile() throws Exception {
		createFile("/index.html.template", "some template content");
		assertFalse(new File(baseDir + "/foo.html").isFile());
		g.generate();
		assertTrue(new File(baseDir + "/foo.html").isFile());
	}
	
	@Test
	public void indexFileShouldHaveContentOfMarkdownFile() throws Exception {
		createFile("/index.html.template", "{content}");
		g.generate();
		String content = FileUtils.readFileToString(new File(baseDir + "/foo.html"));
		String expected = "<p>some text</p>";
		assertThat(content, equalTo(expected));
	}
	
	@Test
	public void contentShouldBeProcessesFromMarkdown() throws Exception {
		createFile("/index.html.template", "{content}");
		createFile("/bar.md", "# some text");
		g.generate();
		String content = FileUtils.readFileToString(new File(baseDir + "/bar.html"));
		assertThat(content, equalTo("<h1>some text</h1>"));
	}
	
	@Test
	public void articleListOnHomePage() throws Exception {
		createFile("/index.html.template", "{links}");
		new File(baseDir + "/sub").mkdir();
		createFile("/sub/bar.md", "# bar and buzz");
		g.generate();
		String content = FileUtils.readFileToString(new File(baseDir + "/index.html"));
		assertThat(content, equalTo(
			"<li><a href=\"./sub/bar.html\">bar</a></li>\n"
		  + "<li><a href=\"./foo.html\">foo</a></li>\n"
		));
	}
	
	@Test
	public void articleListOnArticlePage() throws Exception {
		createFile("/index.html.template", "{links}");
		new File(baseDir + "/sub").mkdir();
		createFile("/sub/bar.md", "# bar and buzz");
		g.generate();
		assertThat(getContent("/foo.html"), equalTo(
			"<li><a href=\"./sub/bar.html\">bar</a></li>\n"
		  + "<li><a href=\"./foo.html\">foo</a></li>\n"
		));
	}
	
	@Test
	public void articleListOnSubPage() throws Exception {
		createFile("/index.html.template", "{links}");
		new File(baseDir + "/sub").mkdir();
		createFile("/sub/bar.md", "# bar and buzz");
		g.generate();
		assertThat(getContent("/foo.html"), equalTo(
			"<li><a href=\"./sub/bar.html\">bar</a></li>\n"
		  + "<li><a href=\"./foo.html\">foo</a></li>\n"
		));
		assertThat(getContent("/sub/bar.html"), equalTo(
				"<li><a href=\"./bar.html\">bar</a></li>\n"
			  + "<li><a href=\"./../foo.html\">foo</a></li>\n"
			));
	}

	@Test
	public void pathToAssetsOnHomePage() throws Exception {
		createFile("/index.html.template", "{path}");
		g.generate();
		String content = FileUtils.readFileToString(new File(baseDir + "/index.html"));
		assertThat(content, equalTo(""));
	}
	
	@Test
	public void pathToAssetsOnArticlePage() throws Exception {
		createFile("/index.html.template", "{path}");
		g.generate();
		String content = FileUtils.readFileToString(new File(baseDir + "/foo.html"));
		assertThat(content, equalTo("./"));
	}
	
	@Test
	public void pathToAssetsOnSubPage() throws Exception {
		createFile("/index.html.template", "{path}");
		new File(baseDir + "/sub").mkdir();
		createFile("/sub/bar.md", "{path}");
		g.generate();
		String content = FileUtils.readFileToString(new File(baseDir + "/sub/bar.html"));
		assertThat(content, equalTo("./../"));
	}
}
