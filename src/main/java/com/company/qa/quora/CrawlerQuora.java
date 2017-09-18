package com.company.qa.quora;

/**
 * 按照Quora主题爬取所有信息
 * @author 郑元浩
 * @date 2016年11月25号
 */

import java.io.File;
import java.util.ArrayList;

import jxl.Sheet;
import jxl.Workbook;

import org.apache.commons.io.FileUtils;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.company.app.Config;
import com.company.qa.quora.domain.TopicExcel;
import com.company.utils.JsoupDao;
import com.company.utils.Log;

public class CrawlerQuora {
	
	public static void main(String[] args) throws Exception {
		ArrayList<TopicExcel> topicList = getKeyword();
		// 深度优先
		for (int i = 0; i < topicList.size(); i++) {
			TopicExcel topic = topicList.get(i);
			String keyword = topic.getTopicName();
			String topicLink = topic.getTopicLink();
			int qnum = topic.getQuestionNum();
			crawlerTopic(keyword, topicLink, qnum); // 爬取主题页面
			crawlerKeyword(keyword); // 爬取问题页面
		}
//		// 广度优先
//		for (int i = 0; i < topicList.size(); i++) {
//			Topic topic = topicList.get(i);
//			String keyword = topic.getTopicName();
//			String topicLink = topic.getTopicLink();
//			int qnum = topic.getQuestionNum();
//			crawlerTopic(keyword, topicLink, qnum); // 爬取主题页面
//		}
//		for (int i = 0; i < topicList.size(); i++) {
//			Topic topic = topicList.get(i);
//			String keyword = topic.getTopicName();
//			crawlerKeyword(keyword); // 爬取问题页面
//		}
	}
	
	/**
	 * 得到关键词
	 * @return 
	 * @throws Exception 
	 */
	public static ArrayList<TopicExcel> getKeyword() throws Exception{
		ArrayList<TopicExcel> topicList = new ArrayList<TopicExcel>();
		String excelPath = "F:\\02-CQA网站中问题答案质量评估\\Quora_Topic_Spider.xls";
		Workbook wb = Workbook.getWorkbook(new File(excelPath));
		Sheet st = wb.getSheet("spider");
		int rows = st.getRows();
		for (int i = 1; i < rows; i++) {
			String keyword = st.getCell(1, i).getContents();
			int qnum = Integer.parseInt(st.getCell(2, i).getContents());
			String topicLink = "https://www.quora.com/topic/" + keyword + "/all_questions";
			TopicExcel topic = new TopicExcel(keyword, topicLink, qnum);
			topicList.add(topic);
		}
		return topicList;
	}
	
	/**
	 * 爬取主题页面
	 */
	public static void crawlerTopic(String keyword, String url, int qnum){
		String path =  Config.QUORA_PATH + "\\" + keyword;
		new File(path).mkdir();
		String filePath = path + "\\" + keyword + "(selenium).html";
		try {
			CrawlerQuoraDao.seleniumTopic(filePath, url, qnum);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 实现功能：用于爬取某门课程的一个主题数据，输入是主题名和课程名，输出是主题相关网页
	 */
	public static void crawlerKeyword(String keyword) throws Exception {
		long start = System.currentTimeMillis();
		crawlerQuestion(keyword);//爬取问题页面和作者页面
		storeQuestionURLs(keyword);//存储问题页面链接
		long end = System.currentTimeMillis();
		Log.log("爬取" + keyword + "的所有信息用时：" + (end - start)/1000 + "秒...");
	}

	/**
	 * 实现功能：爬取单个主题下的所有问题页面和作者页面（爬取阶段2），输入是主题词keyword
	 */
	public static void crawlerQuestion(String keyword) throws Exception {
		String catalog = Config.QUORA_PATH + "\\" + keyword + "\\question";
		new File(catalog).mkdir();
		String[] urls = getQuestionURLs(keyword);//得到问题页面链接
		String[] testResult = { "aa", "bb" };
		if (urls.equals(testResult)) {
			Log.log("不存在第一层链接！！！");
		} else {
			Log.log(keyword + "下面包含主题数目为：" + urls.length);
			for (int n = 0; n < urls.length; n++) {
				//设置问题页面保存路径
				String fileName = keyword + (n+1) + ".html";
				String filePath = catalog + "\\" + fileName;
				Log.log("\n正在爬取第 " + (n+1) + "问题：" + urls[n] + ",  ---> 保存路径为：" + filePath);
				if(new File(filePath).exists()){
					Log.log(filePath + " is existing...");
				} else {
					// 爬取问题网页
					CrawlerQuoraDao.seleniumQuestion(filePath, urls[n]);  
				}
				// 保存作者链接
				storeAuthorURLs(keyword, n);
			}
		}
	}

	/**
	 * 解析主题网页，得到主题页面中所有问题页面的链接
	 */
	public static String[] getQuestionURLs(String keyword) throws Exception {
		String path =  Config.QUORA_PATH + "\\" + keyword;
		String filePath = path + "\\" + keyword + "(selenium).html";
		File file = new File(filePath);
		if (!file.exists()) {
			String[] testResult = { "aa", "bb" };
			Log.log(filePath + "  不存在，得不到它的子页面链接！！！");
			return testResult;
		} else {
			Document doc = JsoupDao.parsePathText(filePath);
			Elements links = doc.select("a.question_link").select("a[href]");
			String urls[] = new String[links.size()];
			for (int i = 0; i < links.size(); i++) {
				Element link = links.get(i);
				urls[i] = "http://www.quora.com" + link.attr("href");
			}
			return urls;
		}
	}

	/**
	 * 解析问题页面，得到问题页面中所有作者页面的链接，keyword是主题词，n 是主题页面下的问题页面的序号
	 */
	public static ArrayList<String> getAuthorURLs(String keyword, int n){
		String pathQ = Config.QUORA_PATH + "\\" + keyword + "\\question";
		String filePath = pathQ + "\\" + keyword + (n + 1) + ".html";
		Document doc = JsoupDao.parsePathText(filePath);
		// Quora after 2016
		Elements authors = doc.select("div.pagedlist_item").select("span.feed_item_answer_user");
		ArrayList<String> url = new ArrayList<String>(); //最后有六个作者是例外（Selenium爬取存在的问题）
		for (int m = 0; m < authors.size() - 6; m++) {
			Element a = authors.get(m);
			Elements b = a.select("a.user");
			if (b.size() == 0) {
				Log.log("作者信息不存在！！！");
			} else {
				Element author = b.get(0);
				String urls = author.attr("href");
				if (urls.startsWith("http://")) {
					url.add(urls);
				} else if (urls.startsWith("/")) {
					url.add("http://www.quora.com/" + urls);
				}
			}
		}
		return url;
	}
	
	/**
	 * 实现功能：存储作者网页的链接
	 */
	public static void storeAuthorURLs(String keyword, int n) throws Exception {
		String pathAuthor =  Config.QUORA_PATH + "\\" + keyword + "\\question\\author";
		ArrayList<String> urls = getAuthorURLs(keyword, n);
		String url2txt = "";
		for(int i = 0; i < urls.size(); i++){
			String url = urls.get(i);
			url2txt = url2txt + keyword + (n+1) + "_author_" + i + ": " + url + "\n";
		}
		new File(pathAuthor).mkdir();
		String filePath = pathAuthor + "\\" + keyword + (n+1) + "_author.txt";
		File f = new File(filePath);
		FileUtils.writeStringToFile(f, url2txt);
	}
	
	/**
	 * 实现功能：存储问题网页的链接
	 */
	public static void storeQuestionURLs(String keyword) throws Exception {
		String path =  Config.QUORA_PATH + "\\" + keyword;
		String urls[] = getQuestionURLs(keyword);
		String url2txt = "";
		Log.log("问题总数为：" + urls.length);
		for(int i = 0; i < urls.length; i++){
			String url = urls[i];
			url2txt = url2txt + keyword + (i + 1) + ": " + url + "\n";
		}
		String filePath = path + "\\" + keyword + "_question.txt";
		File f = new File(filePath);
		FileUtils.writeStringToFile(f, url2txt);
	}
	
}
