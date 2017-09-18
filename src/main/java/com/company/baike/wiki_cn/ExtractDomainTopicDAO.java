package com.company.baike.wiki_cn;

import java.util.ArrayList;
import java.util.List;

import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import com.company.utils.JsoupDao;
import com.company.utils.Log;
import com.company.baike.wiki_cn.domain.Term;
import com.spreada.utils.chinese.ZHConverter;

/**  
 * 解析中文维基
 * 1. 获得每一分类的子分类
 * 2. 获得每一分类的子页面
 *  
 * @author 郑元浩 
 * @date 2016年11月26日
 */
public class ExtractDomainTopicDAO {
	
	private static ZHConverter converter = ZHConverter.getInstance(ZHConverter.SIMPLIFIED);// 转化为简体中文

	public static void main(String[] args) throws Exception {
		String url = "https://zh.wikipedia.org/wiki/Category:%E6%95%B0%E6%8D%AE%E7%BB%93%E6%9E%84";
		String html = DownloaderDAO.seleniumWikiCN(url);
		Document doc = JsoupDao.parseHtmlText(html);
		getTopic(doc);
//		getLayer(doc);
	}
	
	/**
	 * 解析得到Category中的页面术语
	 * @param doc
	 * @return 
	 */
	public static List<Term> getTopic(Document doc){
		List<Term> termList = new ArrayList<Term>();
		Elements mwPages = doc.select("#mw-pages").select("li");
		int len = mwPages.size();
		Log.log(len);
		for (int i = 0; i < mwPages.size(); i++) {
			String url = "https://zh.wikipedia.org" + mwPages.get(i).select("a").attr("href");
			String topic = mwPages.get(i).text();
			topic = converter.convert(topic);
//			Log.log("topic is : " + topic + "  url is : " + url);
			Term term = new Term(topic, url);
			termList.add(term);
		}
		return termList;
	}
	
	/**
	 * 解析得到Category中的二级子分类页面
	 * @param doc
	 * @return 
	 */
	public static List<Term> getLayer(Document doc){
		List<Term> termList = new ArrayList<Term>();
		if(doc.select("#mw-subcategories").size()==0){
			Log.log("没有下一层子分类...");
		} else {
			Elements mwPages = doc.select("#mw-subcategories").select("li");
			int len = mwPages.size();
			Log.log(len);
			for (int i = 0; i < mwPages.size(); i++) {
				String url = "https://zh.wikipedia.org" + mwPages.get(i).select("a").attr("href");
				String layer = mwPages.get(i).select("a").text();
				layer = converter.convert(layer);
//				Log.log("Layer is : " + layer + "  url is : " + url);
				Term term = new Term(layer, url);
				termList.add(term);
			}
		}
		return termList;
	}

}
