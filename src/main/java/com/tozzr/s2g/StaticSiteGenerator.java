package com.tozzr.s2g;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.pegdown.PegDownProcessor;

class StaticSiteGenerator {
	public static void main(String[] args) throws IOException {
		new StaticSiteGenerator(".").generate();
	}
	
	private List<Article> articles;
	private final String baseDir;
	private final Path basePath;
	
	public StaticSiteGenerator(String baseDir) {
		this.baseDir = baseDir;
		basePath = new File(baseDir).toPath();
		articles = new ArrayList<Article>();
	}

	public void generate() throws IOException {
		generateArticles(new File(baseDir));
		updateHomePage();
		updatePageLinks();
	}

	public void generateArticles(File dir) throws IOException {
		for (File f : dir.listFiles())
			if (f.isDirectory())
				generateArticles(f);
			else if (f.getName().endsWith(".md"))
				generateArticle(f);
	}

	private void generateArticle(File f) throws IOException {
		Article a = new Article(f);
		createHtmlDocument(a);
		articles.add(a);
	}

	private void createHtmlDocument(Article a) throws IOException {
		String p = a.getPath().toString().replace(".md", ".html");
		File file = new File(p);
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
			String path = "./";
			int ac = a.getPath().getNameCount();
			int bc = basePath.getNameCount();
			for (int i = 0; i < ac - bc - 1; i++)
				path += a.getPath().getName(ac-2-i) + "/";
			links += "<li><a href=\"" + path + a.getName() + ".html\">" + a.getTitle() + "</a></li>\n";
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
		String content = FileUtils.readFileToString(new File(article.getPath().toString().replace(".md", ".html")));
		content = content.replace("{links}", getLinks(article));
		content = content.replaceAll("\\{path\\}", getPath(article));
		String htmlFilename = article.getPath().toString().replace(".md", ".html");
		FileUtils.write(new File(htmlFilename), content);
	}

	public String getLinks(Article article) {
		String links = "";
		for (Article a : articles) {
			String path = "./";
			int c = article.getPath().getNameCount();
			int ac = a.getPath().getNameCount();
			for (int i = 0; i < c - ac; i++)
				path += "../";
			for (int i = 0; i < ac - c; i++)
				path += a.getPath().getName(ac-2-i) + "/";
			links += "<li><a href=\"" + path + a.getName() + ".html\">" + a.getTitle() + "</a></li>\n";
		}
		return links;
	}

	private String getPath(Article article) {
		String path = "./";
		for (int i = 1; i < article.getPath().getNameCount() - basePath.getNameCount(); i++)
				path += "../";
		return path;
	}
}