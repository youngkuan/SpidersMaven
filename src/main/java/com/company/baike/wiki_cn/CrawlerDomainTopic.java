package com.company.baike.wiki_cn;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import com.company.app.Config;
import com.company.baike.wiki_cn.domain.LayerRelation;
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
	 * 根据领域名存储领域
	 * @param domainName
	 */
	public static void storeDomain(String domainName) {
		List<Domain> list = new ArrayList<Domain>();
		Domain domain = new Domain();
		domain.setClassName(domainName);
		list.add(domain);
		if (!MysqlReadWriteDAO.judgeByClass(Config.DOMAIN_TABLE, domainName)) {
			MysqlReadWriteDAO.storeDomain(list);
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
		int firstLayer = 1;
		List<Term> topicFirst = CrawlerDomainTopicDAO.topic(domain_url); // 得到第一层领域术语（不含子主题）
		MysqlReadWriteDAO.storeDomainLayer(topicFirst, domain, firstLayer); // 存储第一层领域术语（不含子主题）
		MysqlReadWriteDAO.storeDomainLayerFuzhu(topicFirst, domain, firstLayer, 0); // 存储第一层领域术语（不含子主题）

		// 构造一个主题作为没有子主题的一级主题的父主题
		List<Term> terms = new ArrayList<Term>();
		Term term = new Term(domain + "介绍", domain_url);
		terms.add(term);
		MysqlReadWriteDAO.storeLayerRelation(domain, 0, terms, 0, domain); // 第一层主题与领域名构成上下位关系
		MysqlReadWriteDAO.storeLayerRelation(term.getTermName(), 0, topicFirst, firstLayer, domain); // 第一层主题与领域名构成上下位关系
		/**
		 * 第二层
		 */
		int secondLayer = 2;
		List<Term> layerSecond = CrawlerDomainTopicDAO.layer(domain_url); // 获取第一层领域术语（含子主题）
		MysqlReadWriteDAO.storeDomainLayerFuzhu(layerSecond, domain, firstLayer, 1); // 存储第一层领域术语（含子主题）
		MysqlReadWriteDAO.storeLayerRelation(domain, 0, layerSecond, firstLayer, domain); // 第一层主题与领域名构成上下位关系
		List<Term> topicSecondAll = new ArrayList<Term>(); // 保存所有第二层的领域术语
		if(layerSecond.size() != 0){
			for(int i = 0; i < layerSecond.size(); i++) {
				Term layer = layerSecond.get(i);
				String url = layer.getTermUrl();
				List<Term> topicSecond = CrawlerDomainTopicDAO.topic(url); // 得到第二层领域术语（不含子主题）
				MysqlReadWriteDAO.storeDomainLayer(topicSecond, domain, secondLayer); // 存储第二层领域术语（不含子主题）
				MysqlReadWriteDAO.storeDomainLayerFuzhu(topicSecond, domain, secondLayer, 0); // 存储第二层领域术语（不含子主题）
				MysqlReadWriteDAO.storeLayerRelation(layer.getTermName(), firstLayer, topicSecond, secondLayer, domain); // 存储领域术语的上下位关系
				topicSecondAll.addAll(topicSecond); // 合并所有第二层领域术语

				int thirdLayer = 3;
				List<Term> layerThird = CrawlerDomainTopicDAO.layer(url); // 得到第二层领域术语（含子主题）
				MysqlReadWriteDAO.storeDomainLayerFuzhu(layerThird, domain, secondLayer, 1); // 存储第二层领域术语（含子主题）
				MysqlReadWriteDAO.storeLayerRelation(layer.getTermName(), firstLayer, layerThird, secondLayer, domain); // 存储领域术语的上下位关系
				List<Term> topicThirdAll = new ArrayList<Term>(); // 保存所有第三层的领域术语
				if (layerThird.size() != 0) {
					for(int j = 0; j < layerThird.size(); j++){
						Term layer2 = layerThird.get(j);
						String url2 = layer2.getTermUrl();
						List<Term> topicThird = CrawlerDomainTopicDAO.topic(url2); // 得到第三层领域术语（不含子主题）
						MysqlReadWriteDAO.storeDomainLayer(topicThird, domain, thirdLayer); // 存储第三层领域术语（不含子主题）
						MysqlReadWriteDAO.storeDomainLayerFuzhu(topicThird, domain, thirdLayer, 0); // 存储第三层领域术语（不含子主题）
						MysqlReadWriteDAO.storeLayerRelation(layer2.getTermName(), secondLayer, topicThird, thirdLayer, domain); // 存储领域术语的上下位关系
						topicThirdAll.addAll(topicThird); // 合并所有第三层领域术语

						List<Term> layerThird2 = CrawlerDomainTopicDAO.layer(url); // 得到第二层领域术语（含子主题）
						MysqlReadWriteDAO.storeDomainLayerFuzhu(layerThird, domain, secondLayer, 1); // 存储第二层领域术语（含子主题）
						MysqlReadWriteDAO.storeLayerRelation(layer.getTermName(), firstLayer, layerThird, secondLayer, domain); // 存储领域术语的上下位关系
					}
				} else {
					Log.log("不存在第三层领域术语源链接....");
				}
			}
		}else{
			Log.log("不存在第二层领域术语源链接...");
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

		List<Term> topicFirstFuzhu = MysqlReadWriteDAO.getDomainLayerFuzhu(domain, 1, 0);
		List<Term> topicSecondFuzhu = MysqlReadWriteDAO.getDomainLayerFuzhu(domain, 2, 0);
		List<Term> topicThirdFuzhu = MysqlReadWriteDAO.getDomainLayerFuzhu(domain, 3, 0);

		List<Term> topicFirstFuzhu2 = MysqlReadWriteDAO.getDomainLayerFuzhu(domain, 1, 1);
		List<Term> topicSecondFuzhu2 = MysqlReadWriteDAO.getDomainLayerFuzhu(domain, 2, 1);
		List<Term> topicThirdFuzhu2 = MysqlReadWriteDAO.getDomainLayerFuzhu(domain, 3, 1);

		List<LayerRelation> layerRelationList = MysqlReadWriteDAO.getDomainLayerRelation(domain);

		/**
		 * 知识主题筛选：抽取算法获取知识主题
		 * 存储到 domain_topic表格中
		 */
		// 从 domain_layer 删除重复主题(含子主题)保存到 domain_topic
		List<Set<Term>> topicList = CrawlerDomainTopicDAO.getTopic(topicFirst, topicSecond, topicThird);
		for(int i = 0; i < topicList.size(); i++){
			Set<Term> topic = topicList.get(i);
			int layer_ID = i + 1;
			MysqlReadWriteDAO.storeDomainTopic(topic, domain, layer_ID); // 存储第三层领域术语
		}
		// 从 domain_layer_fuzhu 删除重复主题(不含子主题)保存到 domain_layer_fuzhu2
		List<Set<Term>> topicListFuzhu = CrawlerDomainTopicDAO.getTopic(topicFirstFuzhu, topicSecondFuzhu, topicThirdFuzhu);
		for(int i = 0; i < topicListFuzhu.size(); i++){
			Set<Term> topic = topicListFuzhu.get(i);
			int layer_ID = i + 1;
			MysqlReadWriteDAO.storeDomainTopicFuzhu(topic, domain, layer_ID, 0);
		}
		// 从 domain_layer_fuzhu 删除重复主题(含子主题)保存到 domain_layer_fuzhu2
		List<Set<Term>> topicListFuzhu2 = CrawlerDomainTopicDAO.getTopic(topicFirstFuzhu2, topicSecondFuzhu2, topicThirdFuzhu2);
		for(int i = 0; i < topicListFuzhu2.size(); i++){
			Set<Term> topic = topicListFuzhu2.get(i);
			int layer_ID = i + 1;
			MysqlReadWriteDAO.storeDomainTopicFuzhu(topic, domain, layer_ID, 1);
		}
		// 从 domain_layer_relation 删除重复主题关系保存到 domain_topic_relation
		Set<LayerRelation> layerRelationSet = new LinkedHashSet<LayerRelation>(layerRelationList);
//		MysqlReadWriteDAO.storeDomainLayerRelation(layerRelationSet); // 存储 domain_layer_relation2
		MysqlReadWriteDAO.storeDomainTopicRelation(layerRelationSet); // 存储 domain_topic_relation
	}

}
