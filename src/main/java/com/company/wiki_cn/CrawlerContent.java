package com.company.wiki_cn;

import java.util.List;

import org.jsoup.nodes.Document;

import com.company.app.Config;
import com.company.utils.JsoupDao;
import com.company.utils.Log;
import com.company.wiki_cn.bean.Assemble;
import com.company.wiki_cn.bean.AssembleImage;
import com.company.wiki_cn.bean.Domain;
import com.company.wiki_cn.bean.FacetRelation;
import com.company.wiki_cn.bean.FacetSimple;
import com.company.wiki_cn.bean.Topic;

/**  
 * 类说明   
 *  
 * @author 郑元浩 
 * @date 2016年11月29日
 */
public class CrawlerContent {
	
	public static void main(String[] args) throws Exception {
		store();
	}
	
	/**
	 * 将领域术语网页内容按照分面存储到数据库（所有课程）
	 * @throws Exception
	 */
	public static void store() throws Exception{
		/**
		 * 读取domain表格，获取所有领域名
		 */
		List<Domain> domainList = MysqlReadWriteDAO.getDomain();
		for (int i = 0; i < domainList.size(); i++) {
			Domain domainItem = domainList.get(i);
			String domain = domainItem.getClassName();
			pipeline(domain);
		}
		
	}
	
	
	/**
	 * 将领域术语网页内容按照分面存储到数据库
	 * @throws Exception
	 */
	public static void pipeline(String domain) throws Exception{
		
		/**
		 * 读取数据库表格domain_topic，得到领域术语
		 */
//		String domain = "数据结构";
		List<Topic> topicList = MysqlReadWriteDAO.getDomainTopic(domain);
		for(int i = 0; i < topicList.size(); i++){
			Topic topic = topicList.get(i);
			int topicID = topic.getTopicID();
			String topicName = topic.getTopicName();
			String topicUrl = topic.getTopicUrl();
			
			/**
			 * 判断数据是否已经存在
			 */
			Boolean existFacet = MysqlReadWriteDAO.judgeByClassAndTopic(Config.FACET_TABLE, domain, topicName);
			Boolean existFacetRelation = MysqlReadWriteDAO.judgeByClassAndTopic(Config.FACET_RELATION_TABLE, domain, topicName);
			Boolean existSpider = MysqlReadWriteDAO.judgeByClassAndTopic(Config.SPIDER_TEXT_TABLE, domain, topicName);
			Boolean existAssemble = MysqlReadWriteDAO.judgeByClassAndTopic(Config.ASSEMBLE_TEXT_TABLE, domain, topicName);
			Boolean existSpiderImage = MysqlReadWriteDAO.judgeByClassAndTopic(Config.SPIDER_IMAGE_TABLE, domain, topicName);
			Boolean existAssembleImage = MysqlReadWriteDAO.judgeByClassAndTopic(Config.ASSEMBLE_IMAGE_TABLE, domain, topicName);
			
			/**
			 * 判断该主题的信息是不是在所有表格中已经存在
			 * 只要有一个不存在就需要再次爬取（再次模拟加载浏览器）
			 */
			if(!existFacet || !existFacetRelation || !existSpider || !existAssemble 
					|| !existSpiderImage || !existAssembleImage){
				
				/**
				 * selenium解析网页
				 */
				String topicHtml = DownloaderDAO.seleniumWikiCN(topicUrl);
				Document doc = JsoupDao.parseHtmlText(topicHtml);
				
				/**
				 * 获取并存储所有分面信息Facet
				 */
				List<FacetSimple> facetSimpleList = CrawlerContentDAO.getAllFacet(doc);
				if(!existFacet){
					MysqlReadWriteDAO.storeFacet(domain, topicID, topicName, facetSimpleList);
					Log.log("domain : " + domain + ", topicName : " + topicName + " ---> store in facet...");
				} else {
					Log.log("domain : " + domain + ", topicName : " + topicName + " ---> is already existing in facet...");
				}
				
				/**
				 * 获取并存储各级分面之间的关系FacetRelation
				 */
				List<FacetRelation> facetRelationList = CrawlerContentDAO.getFacetRelation(doc);
				if(!existFacetRelation){
					MysqlReadWriteDAO.storeFacetRelation(domain, topicID, topicName, facetRelationList);
					Log.log("domain : " + domain + ", topicName : " + topicName + " ---> store in facet_relation...");
				} else {
					Log.log("domain : " + domain + ", topicName : " + topicName + " ---> is already existing in facet_relation...");
				}
				
				/**
				 * 获得网页所有内容
				 */
				boolean flagFirst = true; // 一级标题
				boolean flagSecond = true; // 二级标题
				boolean flagThird = true; // 三级标题
				String postTime = ExtractContentDAO.getPostTime(doc);
				// 获取所有分面及其文本
//				List<Assemble> assembleList = CrawlerContentDAO.getAllContent(doc, flagFirst, flagSecond, flagThird);
//				List<AssembleImage> assembleImageList = CrawlerContentDAO.getAllImage(domain, flagFirst, flagSecond, flagThird);
				// 一级分面下如果有二级分面，那么一级分面应该没有碎片文本
				List<Assemble> assembleList = CrawlerContentDAO.getAllContentNew(domain, topicName, doc, flagFirst, flagSecond, flagThird);
				// 一级分面下如果有二级分面，那么一级分面应该没有图片文本
				List<AssembleImage> assembleImageList = CrawlerContentDAO.getAllImageNew(domain, topicName, doc, flagFirst, flagSecond, flagThird);
				
				/**
				 * 存储Spider_text
				 * 存储前进行判断，已经存在的不用存储
				 */
				if(!existSpider){
					MysqlReadWriteDAO.storeSpider(domain, topicID, topicName, topicUrl, postTime, assembleList);
					Log.log("domain : " + domain + ", topicName : " + topicName + " ---> store in spider_text...");
				} else {
					Log.log("domain : " + domain + ", topicName : " + topicName + " ---> is already existing in spider_text...");
				}
				
				/**
				 * 获得Assemble_text
				 * 存储前进行判断，已经存在的不用存储
				 */
				if(!existAssemble){
					MysqlReadWriteDAO.storeAssemble(domain, topicID, topicName, topicUrl, postTime, assembleList);
					Log.log("domain : " + domain + ", topicName : " + topicName + " ---> store in assemble_text...");
				} else {
					Log.log("domain : " + domain + ", topicName : " + topicName + " ---> is already existing in assemble_text...");
				}
				
				/**
				 * 存储Spider_image
				 * 存储前进行判断，已经存在的不用存储
				 */
				if(!existSpiderImage){
					MysqlReadWriteDAO.storeSpider(domain, topicID, topicName, topicUrl, assembleImageList);
					Log.log("domain : " + domain + ", topicName : " + topicName + " ---> store in spider_image...");
				} else {
					Log.log("domain : " + domain + ", topicName : " + topicName + " ---> is already existing in spider_image...");
				}
				
				/**
				 * 获得Assemble_image
				 * 存储前进行判断，已经存在的不用存储
				 */
				if(!existAssembleImage){
					MysqlReadWriteDAO.storeAssemble(domain, topicID, topicName, topicUrl, assembleImageList);
					Log.log("domain : " + domain + ", topicName : " + topicName + " ---> store in assemble_image...");
				} else {
					Log.log("domain : " + domain + ", topicName : " + topicName + " ---> is already existing in assemble_image...");
				}
				
			} else {
				Log.log("domain : " + domain + ", topicName : " + topicName 
						+ " ---> is already existing in facet, spider_text, assemble_text, spider_image, assemble_image...");
			}
			 
		}
			
	}

}
