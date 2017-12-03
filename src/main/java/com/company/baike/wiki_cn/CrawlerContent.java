package com.company.baike.wiki_cn;

import java.util.List;

import org.jsoup.nodes.Document;

import com.company.app.Config;
import com.company.utils.JsoupDao;
import com.company.utils.Log;
import com.company.baike.wiki_cn.domain.Assemble;
import com.company.baike.wiki_cn.domain.AssembleImage;
import com.company.baike.wiki_cn.domain.Domain;
import com.company.baike.wiki_cn.domain.FacetRelation;
import com.company.baike.wiki_cn.domain.FacetSimple;
import com.company.baike.wiki_cn.domain.Topic;

/**  
 * 构建领域知识森林数据
 *  
 * @author 郑元浩 
 * @date 2016年11月29日
 */
public class CrawlerContent {
	
	public static void main(String[] args) throws Exception {
//		store();
		String topicUrl = "https://zh.wikipedia.org/wiki/%E7%96%8F%E8%8A%B1%E4%BB%99%E8%8C%85";
		String topicHtml = DownloaderDAO.seleniumWikiCN(topicUrl);
		Document doc = JsoupDao.parseHtmlText(topicHtml);
		List<Assemble> assembleList = CrawlerContentDAO.getAllContentNew("植物生理学", "疏花仙茅", doc, true, true, true);
	}
	
	/**
	 * 将领域术语网页内容按照分面存储到数据库
	 * @throws Exception
	 */
	public static void storeKGByDomainName(String domain) throws Exception{
		
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
			Boolean existAssembleFragment = MysqlReadWriteDAO.judgeByClassAndTopic(Config.ASSEMBLE_FRAGMENT_TABLE, domain, topicName);

			/**
			 * 判断该主题的信息是不是在所有表格中已经存在
			 * 只要有一个不存在就需要再次爬取（再次模拟加载浏览器）
			 */
			if(!existFacet || !existFacetRelation || !existSpider || !existAssemble 
					|| !existSpiderImage || !existAssembleImage || !existAssembleFragment){
				
				/**
				 * selenium解析网页
				 */
				String topicHtml = DownloaderDAO.seleniumWikiCN(topicUrl);
				Document doc = JsoupDao.parseHtmlText(topicHtml);
				boolean flagFirst = true; // 一级标题
				boolean flagSecond = true; // 二级标题
				boolean flagThird = true; // 三级标题
				String postTime = ExtractContentDAO.getPostTime(doc);
				// 一级分面下如果有二级分面，那么一级分面应该没有碎片文本
				List<Assemble> assembleList = CrawlerContentDAO.getAllContentNew(domain, topicName, doc, flagFirst, flagSecond, flagThird);
				List<AssembleImage> assembleImageList = CrawlerContentDAO.getAllImageNew(domain, topicName, doc, flagFirst, flagSecond, flagThird);
				List<Assemble> assembleFragmentList = CrawlerFragmentDAO.getAllContentNew(domain, topicName, doc, flagFirst, flagSecond, flagThird);

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
				
				// 获取并存储各级分面之间的关系FacetRelation
				List<FacetRelation> facetRelationList = CrawlerContentDAO.getFacetRelation(doc);
				if(!existFacetRelation){
					MysqlReadWriteDAO.storeFacetRelation(domain, topicID, topicName, facetRelationList);
					Log.log("domain : " + domain + ", topicName : " + topicName + " ---> store in facet_relation...");
				} else {
					Log.log("domain : " + domain + ", topicName : " + topicName + " ---> is already existing in facet_relation...");
				}
				
				// 存储Spider_text
				if(!existSpider){
					MysqlReadWriteDAO.storeSpider(domain, topicID, topicName, topicUrl, postTime, assembleList);
					Log.log("domain : " + domain + ", topicName : " + topicName + " ---> store in spider_text...");
				} else {
					Log.log("domain : " + domain + ", topicName : " + topicName + " ---> is already existing in spider_text...");
				}
				
				// 获得Assemble_text
				if(!existAssemble){
					MysqlReadWriteDAO.storeAssemble(domain, topicID, topicName, topicUrl, postTime, assembleList);
					Log.log("domain : " + domain + ", topicName : " + topicName + " ---> store in assemble_text...");
				} else {
					Log.log("domain : " + domain + ", topicName : " + topicName + " ---> is already existing in assemble_text...");
				}
				
				// 存储Spider_image
				if(!existSpiderImage){
					MysqlReadWriteDAO.storeSpider(domain, topicID, topicName, topicUrl, assembleImageList);
					Log.log("domain : " + domain + ", topicName : " + topicName + " ---> store in spider_image...");
				} else {
					Log.log("domain : " + domain + ", topicName : " + topicName + " ---> is already existing in spider_image...");
				}
				
				// 获得Assemble_image
				if(!existAssembleImage){
					MysqlReadWriteDAO.storeAssemble(domain, topicID, topicName, topicUrl, assembleImageList);
					Log.log("domain : " + domain + ", topicName : " + topicName + " ---> store in assemble_image...");
				} else {
					Log.log("domain : " + domain + ", topicName : " + topicName + " ---> is already existing in assemble_image...");
				}

				// 获得Assemble_fragment
				if(!existAssembleFragment){
					MysqlReadWriteDAO.storeFragment(domain, topicID, topicName, topicUrl, assembleFragmentList);
					Log.log("domain : " + domain + ", topicName : " + topicName + " ---> store in assemble_fragment...");
				} else {
					Log.log("domain : " + domain + ", topicName : " + topicName + " ---> is already existing in assemble_fragment...");
				}
			} else {
				Log.log("domain : " + domain + ", topicName : " + topicName
						+ " ---> is already existing in facet, spider_text, assemble_text, spider_image, assemble_image, assemble_fragment...");
			}
		}

		/**
		 * 保存主题间的上下位关系
		 */
		CrawlerFragmentDAO.generateDependenceByClassName(domain);
	}


	/**
	 * 爬取所有课程
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
			storeKGByDomainName(domain);
		}

	}

}
