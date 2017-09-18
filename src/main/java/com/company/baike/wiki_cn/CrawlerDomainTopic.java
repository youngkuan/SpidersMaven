package com.company.baike.wiki_cn;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.company.app.Config;
import com.company.utils.Log;
import com.company.baike.wiki_cn.domain.Domain;
import com.company.baike.wiki_cn.domain.Term;


/**  
 * 爬取中文维基的领域术语
 * @author 郑元浩 
 * @date 2016年11月26日
 */
public class CrawlerDomainTopic {
	
	public static void main(String[] args) throws Exception {
		store();
	}
	
	/**
	 * 获取三层领域术语（所有课程）
	 * @throws Exception
	 */
	public static void store() throws Exception{
		/**
		 * 领域术语采集：爬虫爬取
		 * 读取domain表格，获取所有领域名
		 * 将所有领域术语存储到damain_layer表格中
		 */
		List<Domain> domainList = MysqlReadWriteDAO.getDomain();
		for (int i = 0; i < domainList.size(); i++) {
			Domain domainItem = domainList.get(i);
			String domain = domainItem.getClassName();
			
			/**
			 * 判断该领域是否已经爬取
			 */
			Boolean existLayer = MysqlReadWriteDAO.judgeByClass(Config.DOMAIN_LAYER_TABLE, domain);
			if (!existLayer) {
				layerExtract(domain);
			} else {
				Log.log("domain : " + domain + " ---> is already existing in domain_layer...");
			}
			
			/**
			 * 判断该领域是否已经爬取
			 */
			Boolean existTopic = MysqlReadWriteDAO.judgeByClass(Config.DOMAIN_TOPIC_TABLE, domain);
			if (!existTopic) {
				topicExtract(domain);
			} else {
				Log.log("domain : " + domain + " ---> is already existing in domain_topic...");
			}
		}
		
	}
	
	/**
	 * 获取三层领域术语（某门所有课程）
	 * @throws Exception
	 */
	public static void layerExtract(String domain) throws Exception{
		
		/**
		 * 领域术语采集：单门课程
		 */
		/**
		 * 第一层
		 */
//		String domain = "数据结构";
		String domain_url = "https://zh.wikipedia.org/wiki/Category:" + URLEncoder.encode(domain ,"UTF-8");
		int layerID = 1;
		List<Term> topicFirst = CrawlerDomainTopicDAO.topic(domain_url); // 得到第一层领域术语
		MysqlReadWriteDAO.storeDomainLayer(topicFirst, domain, layerID); // 存储第一层领域术语
		/**
		 * 第二层
		 */
		layerID = 2;
		List<Term> layerSecond = CrawlerDomainTopicDAO.layer(domain_url); // 获取第二层领域术语的源链接
		List<Term> topicSecondAll = new ArrayList<Term>(); // 保存所有第二层的领域术语
		if(layerSecond.size() != 0){
			for(int i = 0; i < layerSecond.size(); i++){
				Term layer = layerSecond.get(i);
				String url = layer.getTermUrl();
				List<Term> topicSecond = CrawlerDomainTopicDAO.topic(url); // 得到第二层领域术语
				MysqlReadWriteDAO.storeDomainLayer(topicSecond, domain, layerID); // 存储第二层领域术语
				topicSecondAll.addAll(topicSecond); // 合并所有第二层领域术语
			}
		}else{
			Log.log("存在第二层领域术语源链接...");
		}
		/**
		 * 第三层
		 */
		layerID = 3;
		List<Term> layerThird = CrawlerDomainTopicDAO.getLayerThird(domain, layerSecond); // 获取第三层领域术语的源链接
		List<Term> topicThirdAll = new ArrayList<Term>(); // 保存所有第三层的领域术语
		if (layerThird.size() != 0) {
			for(int i = 0; i < layerThird.size(); i++){
				Term layer = layerThird.get(i);
				String url = layer.getTermUrl();
				List<Term> topicThird = CrawlerDomainTopicDAO.topic(url); // 得到第三层领域术语
				MysqlReadWriteDAO.storeDomainLayer(topicThird, domain, layerID); // 存储第三层领域术语
				topicThirdAll.addAll(topicThird); // 合并所有第三层领域术语
			}
		} else {
			Log.log("不存在第三层领域术语源链接....");
		}
		
	}
	
	/**
	 * 获取三层知识主题（某门所有课程）
	 * @throws Exception
	 */
	public static void topicExtract(String domain) throws Exception{
		
		List<Term> topicFirst = MysqlReadWriteDAO.getDomainLayer(domain, 1);
		List<Term> topicSecond = MysqlReadWriteDAO.getDomainLayer(domain, 2);
		List<Term> topicThird = MysqlReadWriteDAO.getDomainLayer(domain, 3);
		
		/**
		 * 知识主题筛选：抽取算法获取知识主题
		 * 存储到 domain_topic表格中
		 */
		List<Set<Term>> topicList = CrawlerDomainTopicDAO.getTopic(topicFirst, topicSecond, topicThird);
		for(int i = 0; i < topicList.size(); i++){
			Set<Term> topic = topicList.get(i);
			int layer_ID = i + 1;
			MysqlReadWriteDAO.storeDomainTopic(topic, domain, layer_ID); // 存储第三层领域术语
		}
		
	}
	
	
}
