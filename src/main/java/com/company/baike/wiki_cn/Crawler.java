package com.company.baike.wiki_cn;

import com.company.utils.Log;

/**  
 * 中文维基爬虫
 * 1. 爬取领域术语
 * 	1.1  读取domain表格获取所有领域名
 * 	1.2  领域术语采集，存储到domain_layer
 * 	1.3  知识主题获取，存储到domain_topic
 * 2. 爬取领域术语下的知识碎片（等第一步完成才可以第二步）
 * 	2.0  解析程序解析每个知识主题的网页，得到所有内容
 * 	2.1	分面获取，每个主题的三级分面信息，存储到facet
 * 	2.2	文本碎片采集，存储到spider_text
 *  2.3 文本碎片装配，存储到assemble_text
 * 	2.4	图片碎片采集，存储到spider_image
 *  2.5 图片碎片装配，存储到assemble_image
 *  
 * @author 郑元浩 
 * @date 2016年11月29日
 */
public class Crawler {

	public static void main(String[] args) throws Exception {
		cralwerAll();
	}
	
	/**
	 * 爬取所有课程
	 * @throws Exception
	 */
	public static void cralwerAll() throws Exception{
		Log.log("------------------------------------------begin topic crawler------------------------------------------");
		CrawlerDomainTopic.store();
		Log.log("------------------------------------------begin text crawler------------------------------------------");
		CrawlerContent.store();
	}
	
	/**
	 * 爬取一门课程
	 */
	public static void crawler() throws Exception{
		String domain = "植物生理学";
		CrawlerDomainTopic.layerExtract(domain);
		CrawlerDomainTopic.topicExtract(domain);
		CrawlerContent.pipeline(domain);
	}

}
