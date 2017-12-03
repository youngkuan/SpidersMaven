package com.company.baike.wiki_cn;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.company.app.Config;
import com.company.baike.wiki_cn.domain.*;
import com.company.utils.Time;
import com.company.utils.mysqlUtils;

/**  
 * 读取和存储数据库操作
 * 1. 将数据从数据库表格中读取出来
 * 2. 将数据写入数据库表格中   
 * 3. 判断数据表中是否存在某个数据
 * 
 * 数据库表格如下：
 * 1. domain: 领域ID及其领域名（ClassName）
 * 2. domain_layer: 采集到的领域术语，一共有三层，每一层之间存在重复
 * 3. domain_topic: 知识主题，对领域术语进行抽取，保证每一层术语之间不重复
 * 4. spider_text: 文本知识碎片，爬取知识主题对应的中文维基页面，获取文本知识碎片
 * 5. spider_image: 图片知识碎片，爬取知识主题对应。。。
 * 6. facet: 知识主题对应的分面，一共有三级分面
 * 7. assemble_text: 文本碎片装配，将知识主题下的文本知识碎片挂载到对应的每一级分面上
 * 8. assemble_image: 图片碎片装配，将知识主题下的图片知识碎片挂载到对应的每一级分面上
 *  
 * @author 郑元浩 
 * @date 2016年11月29日
 */
public class MysqlReadWriteDAO {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	
	/**
	 * 读取domain，得到所有领域名
	 * @return 
	 */
	public static List<Domain> getDomain() throws Exception {
		List<Domain> domainList = new ArrayList<Domain>();
		mysqlUtils mysql = new mysqlUtils();
		String sql = "select * from " + Config.DOMAIN_TABLE;
		List<Object> params = new ArrayList<Object>();
		try {
			List<Map<String, Object>> results = mysql.returnMultipleResult(sql, params);
			for (int i = 0; i < results.size(); i++) {
				Map<String, Object> result = results.get(i);
				int domainID = Integer.parseInt(result.get("ClassID").toString());
				String domainName = result.get("ClassName").toString();
				Domain domain = new Domain(domainID, domainName);
				domainList.add(domain);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			mysql.closeconnection();
		}
		return domainList;
	}
	
	/**
	 * 读取domain_layer，得到所有术语（按照课程）
	 * @return 
	 */
	public static List<Term> getDomainLayer(String domain, int layer) throws Exception {
		List<Term> termList = new ArrayList<Term>();
		mysqlUtils mysql = new mysqlUtils();
		String sql = "select * from " + Config.DOMAIN_LAYER_TABLE + " where ClassName=? and TermLayer=?";
		List<Object> params = new ArrayList<Object>();
		params.add(domain);
		params.add(layer);
		try {
			List<Map<String, Object>> results = mysql.returnMultipleResult(sql, params);
			for (int i = 0; i < results.size(); i++) {
				Map<String, Object> result = results.get(i);
				String termName = result.get("TermName").toString();
				String termUrl = result.get("TermUrl").toString();
				Term termLayer = new Term(termName, termUrl);
				termList.add(termLayer);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			mysql.closeconnection();
		}
		return termList;
	}

	/**
	 * 读取domain_layer_fuzhu，得到所有术语（按照课程）
	 * @return
	 */
	public static List<Term> getDomainLayerFuzhu(String domain, int layer, int isCatalog) throws Exception {
		List<Term> termList = new ArrayList<Term>();
		mysqlUtils mysql = new mysqlUtils();
		String sql = "select * from " + Config.DOMAIN_LAYER_FUZHU_TABLE + " where ClassName=? and TermLayer=? and isCatalog=?";
		List<Object> params = new ArrayList<Object>();
		params.add(domain);
		params.add(layer);
		params.add(isCatalog);
		try {
			List<Map<String, Object>> results = mysql.returnMultipleResult(sql, params);
			for (int i = 0; i < results.size(); i++) {
				Map<String, Object> result = results.get(i);
				String termName = result.get("TermName").toString();
				String termUrl = result.get("TermUrl").toString();
				Term termLayer = new Term(termName, termUrl);
				termList.add(termLayer);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			mysql.closeconnection();
		}
		return termList;
	}

	/**
	 * 读取domain_layer_relation，得到所有术语上下位关系（按照课程）
	 * @return
	 */
	public static List<LayerRelation> getDomainLayerRelation(String domain) throws Exception {
		List<LayerRelation> layerRelationList = new ArrayList<LayerRelation>();
		mysqlUtils mysql = new mysqlUtils();
		String sql = "select * from " + Config.DOMAIN_LAYER_RELATION_TABLE + " where ClassName=?";
		List<Object> params = new ArrayList<Object>();
		params.add(domain);
		try {
			List<Map<String, Object>> results = mysql.returnMultipleResult(sql, params);
			for (int i = 0; i < results.size(); i++) {
				Map<String, Object> result = results.get(i);
				String parentName = result.get("Parent").toString();
				int parentLayer = Integer.parseInt(result.get("ParentLayer").toString());
				String childName = result.get("Child").toString();
				int childLayer = Integer.parseInt(result.get("ChildLayer").toString());
				LayerRelation layerRelation = new LayerRelation(parentName, parentLayer, childName, childLayer, domain);
				layerRelationList.add(layerRelation);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			mysql.closeconnection();
		}
		return layerRelationList;
	}
	
	/**
	 * 读取domain_topic，得到所有主题（按照课程）
	 * @return 
	 */
	public static List<Topic> getDomainTopic(String domain) throws Exception {
		List<Topic> topicList = new ArrayList<Topic>();
		mysqlUtils mysql = new mysqlUtils();
		String sql = "select * from " + Config.DOMAIN_TOPIC_TABLE + " where ClassName=?";
		List<Object> params = new ArrayList<Object>();
		params.add(domain);
		try {
			List<Map<String, Object>> results = mysql.returnMultipleResult(sql, params);
			for (int i = 0; i < results.size(); i++) {
				Map<String, Object> result = results.get(i);
				int topicID = Integer.parseInt(result.get("TermID").toString());
				String topicName = result.get("TermName").toString();
				String topicUrl = result.get("TermUrl").toString();
				Topic topic = new Topic(topicID, topicName, topicUrl);
				topicList.add(topic);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			mysql.closeconnection();
		}
		return topicList;
	}

	/**
	 * 读取facet，得到所有主题的分面（按照课程）
	 * @return 
	 */
	public static List<Facet> getFacet(String domain) throws Exception {
		List<Facet> facetList = new ArrayList<Facet>();
		mysqlUtils mysql = new mysqlUtils();
		String sql = "select * from " + Config.FACET_TABLE + " where ClassName=?";
		List<Object> params = new ArrayList<Object>();
		params.add(domain);
		try {
			List<Map<String, Object>> results = mysql.returnMultipleResult(sql, params);
			for (int i = 0; i < results.size(); i++) {
				Map<String, Object> result = results.get(i);
				int termID = Integer.parseInt(result.get("TermID").toString());
				String termName = result.get("TermName").toString();
				String facetName = result.get("FacetName").toString();
				int facetLayer = Integer.parseInt(result.get("FacetLayer").toString());
				String className = result.get("ClassName").toString();
				Facet facet = new Facet(termID, termName, facetName, facetLayer, className);
				facetList.add(facet);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			mysql.closeconnection();
		}
		return facetList;
	}

	/**
	 * 读取spider，得到所有碎片信息（按照课程）
	 * @return
	 */
	public static List<Text> getSpider(String domain) throws Exception {
		List<Text> textList = new ArrayList<Text>();
		mysqlUtils mysql = new mysqlUtils();
		String sql = "select * from " + Config.SPIDER_TEXT_TABLE + " where ClassName=?";
		List<Object> params = new ArrayList<Object>();
		params.add(domain);
		try {
			List<Map<String, Object>> results = mysql.returnMultipleResult(sql, params);
			for (int i = 0; i < results.size(); i++) {
				Map<String, Object> result = results.get(i);
//				int fragmentID = Integer.parseInt(result.get("FragmentID").toString());
				String fragmentContent = result.get("FragmentContent").toString();
				String fragmentUrl = result.get("FragmentUrl").toString();
				String fragmentPostTime = result.get("FragmentPostTime").toString();
				String fragmentScratchTime = result.get("FragmentScratchTime").toString();
				int termID = Integer.parseInt(result.get("TermID").toString());
				String termName = result.get("TermName").toString();
				String className = result.get("ClassName").toString();
				Text text = new Text(fragmentContent, fragmentUrl, fragmentPostTime, fragmentScratchTime,
						termID, termName, className);
				textList.add(text);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			mysql.closeconnection();
		}
		return textList;
	}

	/**
	 * 存储domain，保存领域名信息
	 * @param domain 领域
	 */
	public static void storeDomain(Domain domain){
		mysqlUtils mysql = new mysqlUtils();
		String sql = "insert into " + Config.DOMAIN_TABLE + " (ClassID, ClassName) VALUES(?, ?);";
		List<Object> params = new ArrayList<Object>();
		params.add(domain.getClassID());
		params.add(domain.getClassName());
		try {
			mysql.addDeleteModify(sql, params);
		} catch (Exception e) {
			e.printStackTrace();
		}
		mysql.closeconnection();
	}

	/**
	 * 存储domain，保存领域名信息
	 * @param domainList
	 */
	public static void storeDomain(List<Domain> domainList){
		mysqlUtils mysql = new mysqlUtils();
		String sql = "insert into " + Config.DOMAIN_TABLE + " (ClassID, ClassName) VALUES(?, ?);";
		for (int i = 0; i < domainList.size(); i++) {
			Domain domain = domainList.get(i);
			List<Object> params = new ArrayList<Object>();
			params.add(domain.getClassID());
			params.add(domain.getClassName());
			try {
				mysql.addDeleteModify(sql, params);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		mysql.closeconnection();
	}

	/**
	 * 存储domain_layer，存储第n层领域术语到数据库 domain_layer 表格（List）
	 * @param termList
	 * @param domain
	 * @param layer
	 */
	public static void storeDomainLayer(List<Term> termList, String domain, int layer){
		mysqlUtils mysql = new mysqlUtils();
		String sql = "insert into " + Config.DOMAIN_LAYER_TABLE + " (TermName, TermUrl, TermLayer, ClassName)"
				+ " VALUES(?, ?, ?, ?);";
		for (int i = 0; i < termList.size(); i++) {
			Term term = termList.get(i);
			List<Object> params = new ArrayList<Object>();
			params.add(term.getTermName());
			params.add(term.getTermUrl());
			params.add(layer);
			params.add(domain);
			try {
				mysql.addDeleteModify(sql, params);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		mysql.closeconnection();
	}

	/**
	 * 存储domain_layer，存储第n层领域术语到数据库 domain_layer 表格（List）
	 * @param termList
	 * @param domain
	 * @param layer
	 */
	public static void storeDomainLayerFuzhu(List<Term> termList, String domain, int layer, int isCatalog){
		mysqlUtils mysql = new mysqlUtils();
		String sql = "insert into " + Config.DOMAIN_LAYER_FUZHU_TABLE + " (TermName, TermUrl, TermLayer, isCatalog, ClassName)"
				+ " VALUES(?, ?, ?, ?, ?);";
		for (int i = 0; i < termList.size(); i++) {
			Term term = termList.get(i);
			List<Object> params = new ArrayList<Object>();
			params.add(term.getTermName());
			params.add(term.getTermUrl());
			params.add(layer);
			params.add(isCatalog);
			params.add(domain);
			try {
				mysql.addDeleteModify(sql, params);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		mysql.closeconnection();
	}

	/**
	 * 存储domain_layer_relation2，存储第n层领域术语到数据库 domain_layer 表格（List）
	 * @param termList
	 * @param domain
	 * @param layer
	 */
	public static void storeDomainLayerRelation(Set<LayerRelation> layerRelationSet){
		mysqlUtils mysql = new mysqlUtils();
		String sql = "insert into " + Config.DOMAIN_LAYER_RELATION2_TABLE + " (Parent, ParentLayer, Child, ChildLayer, ClassName)"
				+ " VALUES(?, ?, ?, ?, ?);";
		for (LayerRelation layerRelation : layerRelationSet) {
			List<Object> params = new ArrayList<Object>();
			params.add(layerRelation.getParentName());
			params.add(layerRelation.getParentLayer());
			params.add(layerRelation.getChildName());
			params.add(layerRelation.getParentLayer());
			params.add(layerRelation.getDomain());
			try {
				mysql.addDeleteModify(sql, params);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		mysql.closeconnection();
	}

	/**
	 * 存储domain_topic_relation
	 * @param termList
	 * @param domain
	 * @param layer
	 */
	public static void storeDomainTopicRelation(Set<LayerRelation> layerRelationSet){
		mysqlUtils mysql = new mysqlUtils();
		String sql = "insert into " + Config.DOMAIN_TOPIC_RELATION_TABLE + " (Parent, Child, ClassName)"
				+ " VALUES(?, ?, ?);";
		for (LayerRelation layerRelation : layerRelationSet) {
			List<Object> params = new ArrayList<Object>();
			if (layerRelation.getChildName().equals(layerRelation.getDomain())) {
				continue;
			}
			params.add(layerRelation.getParentName());
			params.add(layerRelation.getChildName());
			params.add(layerRelation.getDomain());
			try {
				mysql.addDeleteModify(sql, params);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		mysql.closeconnection();
	}

	/**
	 * 存储domain_topic，存储第n层领域术语到数据库 domain_topic 表格（Set）
	 * @param termList
	 * @param domain
	 * @param layer
	 */
	public static void storeDomainTopic(Set<Term> termList, String domain, int layer){
		mysqlUtils mysql = new mysqlUtils();
		String sql = "insert into " + Config.DOMAIN_TOPIC_TABLE + " (TermName, TermUrl, TermLayer, ClassName)"
				+ " VALUES(?, ?, ?, ?);";
		for (Term term : termList) {
			List<Object> params = new ArrayList<Object>();
			params.add(term.getTermName());
			params.add(term.getTermUrl());
			params.add(layer);
			params.add(domain);
			try {
				mysql.addDeleteModify(sql, params);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		mysql.closeconnection();
	}

	/**
	 * 存储domain_topic，存储第n层领域术语到数据库 domain_topic 表格（Set）
	 * @param termList
	 * @param domain
	 * @param layer
	 */
	public static void storeDomainTopicFuzhu(Set<Term> termList, String domain, int layer, int isCatalog){
		mysqlUtils mysql = new mysqlUtils();
		String sql = "insert into " + Config.DOMAIN_LAYER_FUZHU2_TABLE + " (TermName, TermUrl, TermLayer, isCatalog, ClassName)"
				+ " VALUES(?, ?, ?, ?, ?);";
		for (Term term : termList) {
			List<Object> params = new ArrayList<Object>();
			params.add(term.getTermName());
			params.add(term.getTermUrl());
			params.add(layer);
			params.add(isCatalog);
			params.add(domain);
			try {
				mysql.addDeleteModify(sql, params);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		mysql.closeconnection();
	}
	
	/**
	 * 存储domain_topic，存储上下位关系主题表格
	 * @param termSet
	 * @param domain
	 */
	public static void storeTopicShangXiaWei(Set<Term> termSet, String domain){
		mysqlUtils mysql = new mysqlUtils();
		String sql = "insert into " + Config.DOMAIN_TOPIC_TABLE + " (TermName, TermUrl, ClassName)"
				+ " VALUES(?, ?, ?);";
		for (Term term : termSet) {
			List<Object> params = new ArrayList<Object>();
			params.add(term.getTermName());
			params.add(term.getTermUrl());
			params.add(domain);
			try {
				mysql.addDeleteModify(sql, params);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		mysql.closeconnection();
	}

	/**
	 * 存储domain_layer_relation，存储主题间的上下位关系
	 */
	public static void storeLayerRelation(String parentTopicName, int parentTopicLayer, List<Term> childTopicList, int childLayerLayer, String domain){
		mysqlUtils mysql = new mysqlUtils();
		String sql = "insert into " + Config.DOMAIN_LAYER_RELATION_TABLE + " (Parent, ParentLayer, Child, ChildLayer, ClassName)"
				+ " VALUES(?, ?, ?, ?, ?);";
		for (Term childTopic : childTopicList) {
			List<Object> params = new ArrayList<Object>();
			if (!childTopic.getTermName().equals(domain)) {
				params.add(parentTopicName);
				params.add(parentTopicLayer);
				params.add(childTopic.getTermName());
				params.add(childLayerLayer);
				params.add(domain);
				try {
					mysql.addDeleteModify(sql, params);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		mysql.closeconnection();
	}
	
	/**
	 * 存储domain_topic_relation，存储主题间的上下位关系
	 */
	public static void storeTopicRelation(Set<Relation> relationSet, String domain){
		mysqlUtils mysql = new mysqlUtils();
		String sql = "insert into " + Config.DOMAIN_TOPIC_RELATION_TABLE + " (Parent, Child, ClassName)"
				+ " VALUES(?, ?, ?);";
		for (Relation relation : relationSet) {
			List<Object> params = new ArrayList<Object>();
			params.add(relation.getParent());
			params.add(relation.getChild());
			params.add(domain);
			try {
				mysql.addDeleteModify(sql, params);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		mysql.closeconnection();
	}
	
	/**
	 * 存储facet，按照领域进行存储
	 * @return 
	 */
	public static void storeFacet(String domain, int topicID, String topicName, List<FacetSimple> facetSimpleList) 
			throws Exception {
		
		for (int i = 0; i < facetSimpleList.size(); i++) {
			mysqlUtils mysql = new mysqlUtils();
			String sql = "insert into " + Config.FACET_TABLE + "(TermID, TermName, FacetName, FacetLayer, ClassName) "
					+ "values(?, ?, ?, ?, ?)";
			FacetSimple facetSimple = facetSimpleList.get(i);
			String facetName = facetSimple.getFacetName();
			int facetLayer = facetSimple.getFacetLayer();
			List<Object> params = new ArrayList<Object>();
			params.add(topicID);
			params.add(topicName);
			params.add(facetName);
			params.add(facetLayer);
			params.add(domain);
			try {
				mysql.addDeleteModify(sql, params);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				mysql.closeconnection();
			}
		}
		
	}
	
	/**
	 * 存储facet_Relation，按照领域进行存储
	 * @return 
	 */
	public static void storeFacetRelation(String domain, int topicID, String topicName, 
			List<FacetRelation> facetRelationList) throws Exception {
		
		for (int i = 0; i < facetRelationList.size(); i++) {
			mysqlUtils mysql = new mysqlUtils();
			String sql = "insert into " + Config.FACET_RELATION_TABLE 
					+ "(ChildFacet, ChildLayer, ParentFacet, ParentLayer, TermID, TermName, ClassName) "
					+ "values(?, ?, ?, ?, ?, ?, ?)";
			FacetRelation facetRelation = facetRelationList.get(i);
			String childFacet = facetRelation.getChildFacet();
			int childLayer = facetRelation.getChildLayer();
			String parentFacet = facetRelation.getParentFacet();
			int parentLayer = facetRelation.getParentLayer();
			List<Object> params = new ArrayList<Object>();
			params.add(childFacet);
			params.add(childLayer);
			params.add(parentFacet);
			params.add(parentLayer);
			params.add(topicID);
			params.add(topicName);
			params.add(domain);
			try {
				mysql.addDeleteModify(sql, params);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				mysql.closeconnection();
			}
		}
		
	}
	
	/**
	 * 存储spider_text
	 * @return 
	 */
	public static void storeSpider(String domain, int topicID, String topicName, String topicUrl, 
			String postTime, List<Assemble> assembleList) throws Exception {
		
		for(int j = 0; j < assembleList.size(); j++){
			Assemble assemble = assembleList.get(j);
			String content = assemble.getFacetContent().trim();
			if(!content.equals("")){ // content内容不为空进行存储
				/**
				 * 碎片采集：存储spider_text数据表
				 */
//				content = assemble.getFacetName() + "：\n" + content; //在文本内容中加上分面内容
				mysqlUtils mysql = new mysqlUtils();
				String sqlSpider = "insert into " + Config.SPIDER_TEXT_TABLE + "(FragmentContent, FragmentUrl, FragmentPostTime,"
						+ "FragmentScratchTime, TermID, TermName, ClassName) values(?, ?, ?, ?, ?, ?, ?)";
				List<Object> paramsSpider = new ArrayList<Object>();
				paramsSpider.add(content);
				paramsSpider.add(topicUrl);
				paramsSpider.add(postTime);
				paramsSpider.add(Time.getSystemTime());
				paramsSpider.add(topicID);
				paramsSpider.add(topicName);
				paramsSpider.add(domain);
				try {
					mysql.addDeleteModify(sqlSpider, paramsSpider);
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					mysql.closeconnection();
				}
			}
		}
		
	}
	
	/**
	 * 存储assemble_text
	 * @return 
	 */
	public static void storeAssemble(String domain, int topicID, String topicName, String topicUrl, 
			String postTime, List<Assemble> assembleList) throws Exception {
		
		/**
		 * 数据存储Mysql
		 */
		for(int j = 0; j < assembleList.size(); j++){
			Assemble assemble = assembleList.get(j);
			String facet = assemble.getFacetName();
			String content = assemble.getFacetContent().trim();
			int facetLayer = assemble.getFacetLayer();
			if(!content.equals("")){ // content内容不为空进行存储
				
				/**
				 * 碎片装配：存储assemble_text数据表
				 */
				mysqlUtils mysql = new mysqlUtils();
				String sqlAssemble = "insert into " + Config.ASSEMBLE_TEXT_TABLE + "(FragmentContent, FragmentUrl, FragmentPostTime,"
						+ "FragmentScratchTime, TermID, TermName, FacetName, FacetLayer, ClassName) "
						+ "values(?, ?, ?, ?, ?, ?, ?, ?, ?)";
				List<Object> paramsAssemble = new ArrayList<Object>();
				paramsAssemble.add(content);
				paramsAssemble.add(topicUrl);
				paramsAssemble.add(postTime);
				paramsAssemble.add(Time.getSystemTime());
				paramsAssemble.add(topicID);
				paramsAssemble.add(topicName);
				paramsAssemble.add(facet);
				paramsAssemble.add(facetLayer);
				paramsAssemble.add(domain);
				try {
					mysql.addDeleteModify(sqlAssemble, paramsAssemble);
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					mysql.closeconnection();
				}
			}
		}
		
	}
	
	/**
	 * 存储spider_image
	 * @return 
	 */
	public static void storeSpider(String domain, int topicID, String topicName, String topicUrl, 
			List<AssembleImage> assembleImageList) throws Exception {
		
		for(int j = 0; j < assembleImageList.size(); j++){
			AssembleImage assembleImage = assembleImageList.get(j);
			String imageUrl = assembleImage.getImageUrl();
			int imageHeight = assembleImage.getImageHeight();
			int imageWidth = assembleImage.getImageWidth();
				
			/**
			 * 碎片采集：存储spider_image数据表
			 */
			mysqlUtils mysql = new mysqlUtils();
			String sqlAssembleImage = "insert into " + Config.SPIDER_IMAGE_TABLE + "(ImageUrl, ImageWidth, ImageHeight,"
					+ "TermID, TermName, TermUrl, ClassName, ImageScratchTime) "
					+ "values(?, ?, ?, ?, ?, ?, ?, ?)";
			List<Object> paramsAssembleImage = new ArrayList<Object>();
			paramsAssembleImage.add(imageUrl);
			paramsAssembleImage.add(imageWidth);
			paramsAssembleImage.add(imageHeight);
			paramsAssembleImage.add(topicID);
			paramsAssembleImage.add(topicName);
			paramsAssembleImage.add(topicUrl);
			paramsAssembleImage.add(domain);
			paramsAssembleImage.add(Time.getSystemTime());
			try {
				mysql.addDeleteModify(sqlAssembleImage, paramsAssembleImage);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				mysql.closeconnection();
			}
			
		}
		
	}
	
	/**
	 * 存储assemble_image
	 * @return 
	 */
	public static void storeAssemble(String domain, int topicID, String topicName, String topicUrl, 
			List<AssembleImage> assembleImageList) throws Exception {
		
		/**
		 * 数据存储Mysql
		 */
		for(int j = 0; j < assembleImageList.size(); j++){
			AssembleImage assembleImage = assembleImageList.get(j);
			String imageUrl = assembleImage.getImageUrl();
			int imageHeight = assembleImage.getImageHeight();
			int imageWidth = assembleImage.getImageWidth();
			int facetLayer = assembleImage.getFacetLayer();
			String facetName = assembleImage.getFacetName();
				
			/**
			 * 碎片装配：存储assemble_image数据表
			 */
			mysqlUtils mysql = new mysqlUtils();
			String sqlAssembleImage = "insert into " + Config.ASSEMBLE_IMAGE_TABLE + "(ImageUrl, ImageWidth, ImageHeight,"
					+ "TermID, TermName, TermUrl, FacetLayer, FacetName, ClassName, ImageScratchTime) "
					+ "values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
			List<Object> paramsAssembleImage = new ArrayList<Object>();
			paramsAssembleImage.add(imageUrl);
			paramsAssembleImage.add(imageWidth);
			paramsAssembleImage.add(imageHeight);
			paramsAssembleImage.add(topicID);
			paramsAssembleImage.add(topicName);
			paramsAssembleImage.add(topicUrl);
			paramsAssembleImage.add(facetLayer);
			paramsAssembleImage.add(facetName);
			paramsAssembleImage.add(domain);
			paramsAssembleImage.add(Time.getSystemTime());
			try {
				mysql.addDeleteModify(sqlAssembleImage, paramsAssembleImage);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				mysql.closeconnection();
			}
			
		}
		
	}

	/**
	 * 存储assemble_fragment
	 * @return
	 */
	public static void storeFragment(String domain, int topicID, String topicName, String topicUrl, List<Assemble> assembleFragmentList) throws Exception {
		for(int j = 0; j < assembleFragmentList.size(); j++){
			Assemble assemble = assembleFragmentList.get(j);
			String facet = assemble.getFacetName();
			String content = assemble.getFacetContent();
			int facetLayer = assemble.getFacetLayer();
			if(!content.equals("")){ // content内容不为空进行存储
				/**
				 * 碎片装配：存储assemble_fragment数据表
				 */
				mysqlUtils mysql = new mysqlUtils();
				String sqlAssemble = "insert into " + Config.ASSEMBLE_FRAGMENT_TABLE + "(FragmentContent, "
						+ "FragmentScratchTime, TermID, TermName, FacetName, FacetLayer, ClassName) "
						+ "values(?, ?, ?, ?, ?, ?, ?)";
				List<Object> paramsAssemble = new ArrayList<Object>();
				paramsAssemble.add(content);
				paramsAssemble.add(Time.getSystemTime());
				paramsAssemble.add(topicID);
				paramsAssemble.add(topicName);
				paramsAssemble.add(facet);
				paramsAssemble.add(facetLayer);
				paramsAssemble.add(domain);
				try {
					mysql.addDeleteModify(sqlAssemble, paramsAssemble);
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					mysql.closeconnection();
				}
			}
		}

	}
	
	/**
	 * 判断表格，判断某门课程的数据是否已经在这个数据表中存在
	 * 适用表格：domain_layer，domain_topic，dependency
	 * @param table
	 * @param domainName
	 * @return true表示该领域已经爬取
	 */
	public static Boolean judgeByClass(String table, String domainName){
		Boolean exist = false;
		mysqlUtils mysql = new mysqlUtils();
		String sql = "select * from " + table + " where ClassName=?";
		List<Object> params = new ArrayList<Object>();
		params.add(domainName);
		try {
			List<Map<String, Object>> results = mysql.returnMultipleResult(sql, params);
			if (results.size()!=0) {
				exist = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			mysql.closeconnection();
		}
		return exist;
	}
	
	/**
	 * 判断表格，判断某门课程下某个主题的数据是否已经在这个数据表中存在
	 * 适用表格：facet，spider_text，assemble_text
	 * @param table
	 * @param domain
	 * @param topic
	 * @return true表示该领域已经爬取
	 */
	public static Boolean judgeByClassAndTopic(String table, String domain, String topic){
		Boolean exist = false;
		mysqlUtils mysql = new mysqlUtils();
		String sql = "select * from " + table + " where ClassName=? and TermName=?";
		List<Object> params = new ArrayList<Object>();
		params.add(domain);
		params.add(topic);
		try {
			List<Map<String, Object>> results = mysql.returnMultipleResult(sql, params);
			if (results.size()!=0) {
				exist = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			mysql.closeconnection();
		}
		return exist;
	}
	
	/**
	 * 判断表格，判断某一级分面在分面关系表的"父分面"中是否存在
	 * 适用表格：facet_relation
	 * @param assemble
	 * @param domain
	 * @param topic
	 * @return true表示该领域已经爬取
	 */
	public static Boolean judgeFacetRelation(Assemble assemble, String domain, String topic){
		Boolean exist = false;
		mysqlUtils mysql = new mysqlUtils();
		String facetName = assemble.getFacetName();
		int facetLayer = assemble.getFacetLayer();
		String sql = "select * from " + Config.FACET_RELATION_TABLE + 
				" where ClassName=? and TermName=? and ParentFacet=? and ParentLayer=?";
		List<Object> params = new ArrayList<Object>();
		params.add(domain);
		params.add(topic);
		params.add(facetName);
		params.add(facetLayer);
		try {
			List<Map<String, Object>> results = mysql.returnMultipleResult(sql, params);
			if (results.size()!=0) {
				exist = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			mysql.closeconnection();
		}
		return exist;
	}
	
	/**
	 * 判断表格，判断某一级分面在分面关系表的"父分面"中是否存在
	 * 适用表格：facet_relation
	 * @param assembleImage
	 * @param domain
	 * @param topic
	 * @return true表示该领域已经爬取
	 */
	public static Boolean judgeFacetRelation(AssembleImage assembleImage, String domain, String topic){
		Boolean exist = false;
		mysqlUtils mysql = new mysqlUtils();
		String facetName = assembleImage.getFacetName();
		int facetLayer = assembleImage.getFacetLayer();
		String sql = "select * from " + Config.FACET_RELATION_TABLE + 
				" where ClassName=? and TermName=? and ParentFacet=? and ParentLayer=?";
		List<Object> params = new ArrayList<Object>();
		params.add(domain);
		params.add(topic);
		params.add(facetName);
		params.add(facetLayer);
		try {
			List<Map<String, Object>> results = mysql.returnMultipleResult(sql, params);
			if (results.size()!=0) {
				exist = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			mysql.closeconnection();
		}
		return exist;
	}

}
