package com.tozzr.s2g;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ArticleTest {

	private List<File> files;
	
	@Before
	public void setUp() throws Exception {
		files = new ArrayList<File>();
	}
	
	@After
	public void tearDown() throws Exception {
		for (File f : files)
			f.deleteOnExit();
	}

	private File createFile(String filename) throws IOException {
		File file = new File(filename);
		FileUtils.write(file, "some content");
		files.add(file);
		return file;
	}
	
	@Test
	public void stripExtensionFromName() throws Exception {
		File f = createFile("./foo.md");
		assertThat(new Article(f).getName(), equalTo("foo"));
	}
	
	@Test
	public void removeHyphenFromTitle() throws Exception {
		File f = createFile("./foo-bar-buzz.md");
		assertThat(new Article(f).getTitle(), equalTo("foo bar buzz"));
	}
}
