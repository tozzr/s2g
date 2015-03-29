package com.tozzr.s2g;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

class Article {
	private String name;
	private String title;
	private String content;
	private String date;
	
	public Article(File f) throws IOException {
		name = getName(f);
		title = getTitle(name);
		content = FileUtils.readFileToString(f);
	}
	
	private String getName(File f) {
		return f.getName().replace(".md", "");
	}
	
	private String getTitle(String name) {
		return name.replaceAll("-", " ");
	}

	public String getName() {
		return name;
	}
	
	public String getTitle() {
		return title;
	}
	
	public String getDate() {
		return date;
	}

	public String getContent() {
		return content;
	}
}