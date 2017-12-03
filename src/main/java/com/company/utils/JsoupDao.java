package com.company.utils;

/**
 * zhengyuanhao  2015/6/30
 * 实现功能：Jsoup获取HTML解析对象的几种方法：
 *        1.从一个URL加载一个Document对象；
 *        2.根据一个文件加载Document对象；
 *        3.解析一个html字符串。
 * 
 */

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.File;
import java.io.IOException;

public class JsoupDao {

	/**
	 * 实现功能：根据一个文件加载Document对象， 解析指定路径的html文件
	 * @param path
	 */
	public static Document parsePathText(String path) {
		Document doc = null;
		try {
			File input = new File(path);
			doc = Jsoup.parse(input, "UTF-8");
		} catch (IOException e) {
			e.printStackTrace();
		}
		return doc;
	}
	
	/**
	 * 实现功能：从一个URL加载一个Document对象
	 * @param URL
	 */
	public static Document parseURLText(String URL) {
		Document doc = null;
		try {
			// get方法
			doc = Jsoup.connect(URL).get();
			// post方法
//			doc = Jsoup.connect(URL)
//					  .data("query", "Java")
//					  .userAgent("Mozilla")
//					  .cookie("auth", "token")
//					  .timeout(3000)
//					  .post();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return doc;
	}
	
	/**
	 * 实现功能：解析一个html字符串
	 * @param html
	 */
	public static Document parseHtmlText(String html) {
//		String html = "<html><head><title>First parse</title></head>"
//				  + "<body><p>Parsed HTML into a doc.</p></body></html>";
		Document doc = Jsoup.parse(html);
		return doc;
	}
	
}
