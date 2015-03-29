package com.tozzr.s2g;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.pegdown.PegDownProcessor;

class StaticSiteGenerator {
	public static void main(String[] args) throws IOException {
		new StaticSiteGenerator(".").generate();
	}
	
	private FilenameFilter markdownFileFilter;
	private List<Article> articles;
	private final String baseDir;
	
	public StaticSiteGenerator(String baseDir) {
		this.baseDir = baseDir;
		markdownFileFilter = new FilenameFilter() {				
			public boolean accept(File dir, String name) {
				return name.endsWith(".md");
			}
		};
		articles = new ArrayList<Article>();
	}

	public void generate() throws IOException {
		File dir = new File(baseDir);
		for (File f : dir.listFiles(markdownFileFilter))
			generateArticle(f);
		updateHomePage();
		updatePageLinks();
	}

	private void generateArticle(File f) throws IOException {
		Article a = new Article(f);
		createHtmlDocument(a);
		articles.add(a);
	}

	private void createHtmlDocument(Article a) throws IOException {
		File file = new File(baseDir + "/" + a.getName() + ".html");
		FileUtils.write(file, mergeContentIntoTemplate(a));
	}

	private String mergeContentIntoTemplate(Article a) throws IOException {
		File template = new File(baseDir + "/index.html.template");
		String content = a.getContent();
		content = new PegDownProcessor().markdownToHtml(content);
		return FileUtils.readFileToString(template).replace("{content}", content);
	}
	
	private void updateHomePage() throws IOException {
		String links = "";
		String content = "";
		for (Article a : articles) {
			content = mergeContentIntoTemplate(a);
			links += "<li><a href=\"." + "/" + a.getName() + ".html\">" + a.getTitle() + "</a></li>\n";
		}
		content = content.replace("{links}", links);
		content = content.replaceAll("\\{path\\}", "");
		FileUtils.write(new File(baseDir + "/index.html"), content);
	}
	
	private void updatePageLinks() throws IOException {
		for (Article a : articles) 
			updateSinglePageLinks(a);
	}

	public void updateSinglePageLinks(Article article) throws IOException {
		String content = FileUtils.readFileToString(new File(baseDir + "/" + article.getName() + ".html"));
		String links = "";
		for (Article a : articles) 
			links += "<li><a href=\"./" + a.getName() + ".html\">" + a.getName() + "</a></li>\n";
		content = content.replace("{links}", links);
		content = content.replaceAll("\\{path\\}", "./");
		FileUtils.write(new File(baseDir + "/" + article.getName() + ".html"), content);
	}
}