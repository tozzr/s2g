package com.tozzr.s2g;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/*
 * x find md files in root dir
 * x create dir from name and create index.html in this dir from md
 * index the file
 * add it to the top of the list
 * update index.html in root with new links
 */

public class StaticSiteGeneratorTest {

	private List<File> files;
	
	@Before
	public void setUp() throws Exception {
		files = new ArrayList<File>();
	}
	
	@After
	public void tearDown() throws Exception {
		for (File f : files)
			f.deleteOnExit();
		FileUtils.deleteDirectory(new File("./foo"));
	}

	private void createFile(String filename, String content) throws IOException {
		File file = new File(filename);
		FileUtils.write(file, content);
		files.add(file);
	}

	@Test
	public void createDirectory() throws Exception {
		createFile("./foo.md", "# this is a test");
		StaticSiteGenerator g = new StaticSiteGenerator();
		assertFalse(new File("./foo").isDirectory());
		g.generate();
		assertTrue(new File("./foo").isDirectory());
	}
	
	@Test
	public void createIndexFile() throws Exception {
		createFile("./foo.md", "# this is a test");
		StaticSiteGenerator g = new StaticSiteGenerator();
		assertFalse(new File("./foo/index.html").isFile());
		g.generate();
		assertTrue(new File("./foo/index.html").isFile());
	}
	
	@Test
	public void indexFileShouldHaveContentOfMarkdownFile() throws Exception {
		createFile("./index.html.template", "<body>{content}</body>");
		createFile("./foo.md", "# this is a test");
		StaticSiteGenerator g = new StaticSiteGenerator();
		g.generate();
		String content = FileUtils.readFileToString(new File("./foo/index.html"));
		assertThat(content, equalTo("<body># this is a test</body>"));
	}
	
	@Test
	public void fileShouldBeLinkedOnHomePage() throws Exception {
		createFile("./index.html.template", "<body>{links}{content}</body>");
		createFile("./foo.md", "# this is a test");
		StaticSiteGenerator g = new StaticSiteGenerator();
		g.generate();
		String content = FileUtils.readFileToString(new File("./index.html"));
		assertThat(content, equalTo("<body><a href=\"./foo\">foo</a># this is a test</body>"));
	}
}
