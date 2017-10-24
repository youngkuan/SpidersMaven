package com.company.baike.wiki_cn;

import java.util.ArrayList;
import java.util.List;

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
//		1、操作系统
//		2、计算机组成原理（没有）
//		3、网络原理（没有）
//		4、C语言
//		5、Java
//		6、数据挖掘
//		7、数据库
//		8、计算机图形学
//		9、汇编语言（比较少）
//		10、软件工程

		// 测试课程  计算机科学史 最优化 农业史  软件工程   汇编语言   计算机图形学    数据库   数据挖掘   Java
//		String domain = "计算机科学史";
//		constructKGByDomainName(domain);

		// 爬取多门课程
		List<String> domainList = new ArrayList<String>();
//		domainList.add("C语言");
//		domainList.add("数学最佳化");
//		domainList.add("神经网络");
//		domainList.add("最优化");
		domainList.add("人机互动");
		domainList.add("信息论");
		domainList.add("电脑安全");
		domainList.add("计算语言学");
		domainList.add("计算机科学基础理论");
		domainList.add("计算机编程");
		domainList.add("算法");
		domainList.add("软件工程");
		domainList.add("人工智能");

//		domainList.add("Java");
		domainList.add("数据挖掘");
		domainList.add("数据库");
		domainList.add("计算机图形学");
		domainList.add("汇编语言");
		domainList.add("软件工程");
		domainList.add("C语言");

		for (int i = 0; i < domainList.size(); i++) {
			constructKGByDomainName(domainList.get(i));
		}
	}
	
	/**
	 * 爬取一门课程
	 */
	public static void constructKGByDomainName(String domainName) throws Exception {
		// 存储主题
		CrawlerDomainTopic.storeDomain(domainName);
		CrawlerDomainTopic.layerExtract(domainName);
		CrawlerDomainTopic.topicExtract(domainName);
		// 存储分面和碎片
		CrawlerContent.storeKGByDomainName(domainName);
	}

}
