package com.tozzr.s2g;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

class StaticSiteGenerator {
	public static void main(String[] args) throws IOException {
		new StaticSiteGenerator().generate();
	}
	
	private FilenameFilter markdownFileFilter;
	
	public StaticSiteGenerator() {
		markdownFileFilter = new FilenameFilter() {				
			public boolean accept(File dir, String name) {
				return name.endsWith(".md");
			}
		};
	}

	public void generate() throws IOException {
		File dir = new File(".");
		for (File f : dir.listFiles(markdownFileFilter))
			generateArticle(f);
	}

	private void generateArticle(File f) throws IOException {
		Article a = new Article(f);
		createDirectory(a);
		createIndexFile(a);
		updateHomePage(a);
	}

	private void createDirectory(Article a) {
		new File("./" + a.getName()).mkdir();
	}

	private void createIndexFile(Article a) throws IOException {
		File file = new File("./" + a.getName() + "/index.html");
		FileUtils.write(file, mergeContentIntoTemplate(a));
	}

	private String mergeContentIntoTemplate(Article a) throws IOException {
		File template = new File("./index.html.template");
		return FileUtils.readFileToString(template).replace("{content}", a.getContent());
	}
	
	private void updateHomePage(Article a) throws IOException {
		String content = mergeContentIntoTemplate(a);
		content = content.replace("{links}", "<a href=\"./" + a.getName() + "\">" + a.getName() + "</a>");
		FileUtils.write(new File("./index.html"), content);
	}

	class Article {
		private String name;
		private String content;
		
		public Article(File f) throws IOException {
			this.name = f.getName().replace(".md", "");
			content = FileUtils.readFileToString(f);
		}

		public String getName() {
			return name;
		}

		public String getContent() {
			return content;
		}
	}
}