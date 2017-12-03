package com.company.baike.wiki_cn;

import com.company.app.Config;
import com.company.baike.wiki_cn.domain.*;
import com.company.baike.wiki_cn.ranktext.RankText;
import com.company.baike.wiki_cn.ranktext.Term;
import com.company.utils.JsoupDao;
import com.company.utils.Log;
import com.company.utils.mysqlUtils;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * 实现中文维基百科知识森林数据集的构建
 * 将文本和图片存储到一个表格中
 * @author 郑元浩
 *
 */
public class CrawlerFragmentDAO {

	public static void main(String[] args) throws Exception {
		// 设置解析参数
		String topicName = "数据库"; // 链表  跳跃列表  数据结构与算法列表
		String topicUrl = "https://zh.wikipedia.org/wiki/" + URLEncoder.encode(topicName);
		String topicHtml = DownloaderDAO.seleniumWikiCN(topicUrl);
		Document doc = JsoupDao.parseHtmlText(topicHtml);

		// 测试解析小程序
//		List<FacetRelation> facetRelationList = getFacetRelation(doc);
//		Log.logFacetRelation(facetRelationList);

		// 解析所有内容
//		getAllContent(doc, false, false, false); // 只有summary内容
//		getAllContent(doc, true, false, false); // summary内容 + 一级标题内容
//		getAllContent(doc, true, true, false); // summary内容 + 一级和二级标题内容
		getAllContent(doc, true, true, true); // summary内容 + 一级/二级/三级标题内容
	}

	/**
	 * 获取各级分面父子对应关系
	 * @param doc
	 * @return 
	 */
	public static List<FacetRelation> getFacetRelation(Document doc){
		LinkedList<String> indexs = new LinkedList<String>();// 标题前面的下标
		LinkedList<String> facets = new LinkedList<String>();// 各级标题的名字
		List<FacetRelation> facetRelationList = new ArrayList<FacetRelation>();
		
		try {
			
			/**
			 * 获取标题
			 */
			Elements titles = doc.select("div#toc").select("li");
			Log.log(titles.size());
			if(titles.size()!=0){
				for(int i = 0; i < titles.size(); i++){
					String index = titles.get(i).child(0).child(0).text();
					String text = titles.get(i).child(0).child(1).text();
					text = Config.converter.convert(text);
					Log.log(index + " " + text);
					indexs.add(index);
					facets.add(text);
				}

				/**
				 * 将二级/三级标题全部匹配到对应的父标题
				 */
				Log.log("--------------------------------------------");
				for(int i = 0; i < indexs.size(); i++){
					String index = indexs.get(i);
					if(index.lastIndexOf(".") == 1){ // 二级分面
//						Log.log("二级标题");
						String facetSecond = facets.get(i);
						for(int j = i - 1; j >= 0; j--){
							String index2 = indexs.get(j);
							if(index2.lastIndexOf(".") == -1){
								String facetOne = facets.get(j);
								FacetRelation facetRelation = new FacetRelation(facetSecond, 2, facetOne, 1);
								facetRelationList.add(facetRelation);
								break;
							}
						}
					} 
					else if (index.lastIndexOf(".") == 3) { // 三级分面
//						Log.log("三级标题");
						String facetThird = facets.get(i);
						for(int j = i - 1; j >= 0; j--){
							String index2 = indexs.get(j);
							if(index2.lastIndexOf(".") == 1){
								String facetSecond = facets.get(j);
								FacetRelation facetRelation = new FacetRelation(facetThird, 3, facetSecond, 2);
								facetRelationList.add(facetRelation);
								break;
							}
						}
					}
				}

			} else {
				Log.log("该主题没有目录，不是目录结构，直接爬取 -->摘要<-- 信息");
			}
		} catch (Exception e) {
			Log.log("this is not a normal page...");
		}
		return facetRelationList;
	}

	/**
	 * 得到一个主题的所有分面及其分面级数
	 * 1. 数据结构为: FacetSimple
	 * @param doc
	 * @return 
	 */
	public static List<FacetSimple> getAllFacet(Document doc){
		List<FacetSimple> facetList = new ArrayList<FacetSimple>();
		List<String> firstTitle = ExtractContentDAO.getFirstTitle(doc);
		List<String> secondTitle = ExtractContentDAO.getSecondTitle(doc);
		List<String> thirdTitle = ExtractContentDAO.getThirdTitle(doc);

		// 判断条件和内容函数保持一致
		// facet中的分面与spider和assemble表格保持一致
		Elements mainContents = doc.select("div#mw-content-text").select("span.mw-headline");
		if(mainContents.size() == 0){ // 存在没有分面的情况
			String facetName = "摘要";
			int facetLayer = 1;
			FacetSimple facetSimple = new FacetSimple(facetName, facetLayer);
			facetList.add(facetSimple);
		} else {
			String facetNameZhai = "摘要";
			int facetLayerZhai = 1;
			FacetSimple facetSimpleZhai = new FacetSimple(facetNameZhai, facetLayerZhai);
			facetList.add(facetSimpleZhai);
			// 保存一级分面名及其分面级数
			for(int i = 0; i < firstTitle.size(); i++){
				String facetName = firstTitle.get(i);
				int facetLayer = 1;
				FacetSimple facetSimple = new FacetSimple(facetName, facetLayer);
				facetList.add(facetSimple);
			}
			// 保存二级分面名及其分面级数
			for(int i = 0; i < secondTitle.size(); i++){
				String facetName = secondTitle.get(i);
				int facetLayer = 2;
				FacetSimple facetSimple = new FacetSimple(facetName, facetLayer);
				facetList.add(facetSimple);
			}
			// 保存三级分面名及其分面级数
			for(int i = 0; i < thirdTitle.size(); i++){
				String facetName = thirdTitle.get(i);
				int facetLayer = 3;
				FacetSimple facetSimple = new FacetSimple(facetName, facetLayer);
				facetList.add(facetSimple);
			}
		}

		return facetList;

	}

	/**
	 * 将从"摘要"到各级标题的所有分面内容全部存到一起
	 * 1. 三级分面及其文本碎片
	 * 2. 有子分面的父分面没有文本碎片
	 * 3. 一个段落作为一个碎片
	 * @param doc 解析网页文档
	 * @param flagFirst  一级标题标志位
	 * @param flagSecond  二级标题标志位
	 * @param flagThird  三级标题标志位
	 * @return
	 */
	public static List<Assemble> getAllContent(Document doc, boolean flagFirst, boolean flagSecond, boolean flagThird){
		List<Assemble> assembleList = new ArrayList<Assemble>();

		Elements mainContents = doc.select("div#mw-content-text").select("span.mw-headline");
		if(mainContents.size() == 0){
			// 网页全部内容
			List<Assemble> specialContent = ExtractFragmentDAO.getSpecialContent(doc); // 没有目录栏的词条信息
			assembleList.addAll(specialContent);
		} else {
			// 摘要信息
			List<Assemble> summaryContent = ExtractFragmentDAO.getSummary(doc); // 摘要内容
			assembleList.addAll(summaryContent);
			// flagFirst 为 true，保留一级分面数据
			if(flagFirst){
				LinkedList<String> firstTitle = ExtractFragmentDAO.getFirstTitle(doc);
				if(firstTitle.size() != 0){
					List<Assemble> firstContent = ExtractFragmentDAO.getFirstContent(doc); // 一级分面内容
					if (firstContent != null) assembleList.addAll(firstContent);
				}
			}
			// flagSecond 为 true，保留二级分面数据
			if(flagSecond){
				LinkedList<String> secondTitle = ExtractFragmentDAO.getSecondTitle(doc);
				if(secondTitle.size() != 0){
					List<Assemble> secondContent = ExtractFragmentDAO.getSecondContent(doc); // 二级分面内容
					if (secondContent != null) assembleList.addAll(secondContent);
				}
			}
			// flagThird 为 true，保留三级分面数据
			if(flagThird){
				LinkedList<String> thirdTitle = ExtractFragmentDAO.getThirdTitle(doc);
				if(thirdTitle.size() != 0){
					List<Assemble> thirdContent = ExtractFragmentDAO.getThirdContent(doc); // 三级分面内容
					if (thirdContent != null) assembleList.addAll(thirdContent);
				}
			}
		}
		return assembleList;
	}

	/**
	 * 保存所有信息，如果某个分面含有子分面，那么这个分面下面应该没有碎片
	 * 1. 判断该文本碎片对应的分面是否包含子分面
	 * 2. 判断该文本碎片为那个不需要的文本
	 * 3. 去除长度很短且无意义的文本碎片
	 * @param domain 领域名
	 * @param topic 主题名
	 * @param doc 解析网页文档
	 * @param flagFirst 一级标题标志位
	 * @param flagSecond 二级标题标志位
	 * @param flagThird 三级标题标志位
	 * @return
	 */
	public static List<Assemble> getAllContentNew(String domain, String topic, Document doc, boolean flagFirst, boolean flagSecond, boolean flagThird){
		List<Assemble> assembleResultList = new ArrayList<Assemble>();
		List<Assemble> assembleList = getAllContent(doc, flagFirst, flagSecond, flagThird);
		for(int i = 0; i < assembleList.size(); i++){
			Assemble assemble = assembleList.get(i);
			Boolean exist = MysqlReadWriteDAO.judgeFacetRelation(assemble, domain, topic); // 判断该文本碎片对应的分面是否包含子分面
			Boolean existImg = judgeBadText(assemble); // 判断该文本碎片为那个不需要的文本
			Boolean lenBoolean = ExtractContentDAO.getContentLen(assemble.getFacetContent()) > Config.CONTENTLENGTH; // 去除长度很短且无意义的文本碎片
			if (!exist && !existImg && lenBoolean) {
				assembleResultList.add(assemble);
			}
		}
		return assembleResultList;
	}
	
	/**
	 * 判断分面内容是否包含最后一个多余的链接
	 * @return
	 */
	public static Boolean judgeBadText(Assemble assemble){
		Boolean exist = false;
		String facetContent = assemble.getFacetContent();
		String badTxt2 = "本条目没有列出任何参考或来源";
		String badTxt3 = "目标页面不存在";
		String badTxt4 = "本条目存在以下问题";
		String badTxt5 = "本条目需要扩充";
		String badTxt6 = "[隐藏] 查 论 编";
		if(facetContent.contains(badTxt2) || facetContent.contains(badTxt3)
				 || facetContent.contains(badTxt4) || facetContent.contains(badTxt5) || facetContent.contains(badTxt6)){
			exist = true;
		}
		return exist;
	}

	/**
	 * 根据领域名生成认知关系
	 * @param ClassName 领域名
	 * @return 是否产生成功
	 */
	public static boolean generateDependenceByClassName(String ClassName) {

		List<Term> termList = new ArrayList<Term>();
		/**
		 * 根据指定领域，查询主题表，获得领域下所有主题
		 */
		mysqlUtils mysql = new mysqlUtils();
		String sql = "select * from " + Config.DOMAIN_TOPIC_TABLE +" where ClassName=?";
		List<Object> params = new ArrayList<Object>();
		params.add(ClassName);
		try {
			List<Map<String, Object>> results = mysql.returnMultipleResult(sql, params);
			for (int i = 0; i < results.size(); i++) {
				Term term = new Term();
				term.setTermID(Integer.parseInt(results.get(i).get("TermID").toString()));
				term.setTermName(results.get(i).get("TermName").toString());
				termList.add(term);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			mysql.closeconnection();
		}
		/**
		 * 根据指定领域及主题，查询碎片表，获得主题的内容信息
		 */
		mysqlUtils mysqlAssemble = new mysqlUtils();
		String sqlAssemble = "select * from " + Config.ASSEMBLE_FRAGMENT_TABLE +" where TermID=? and TermName=? and ClassName=?";
		try {
			for (int i = 0; i < termList.size(); i++) {
				Term term = termList.get(i);
				List<Object> paramsAssemble = new ArrayList<Object>();
				paramsAssemble.add(term.getTermID());
				paramsAssemble.add(term.getTermName());
				paramsAssemble.add(ClassName);
				List<Map<String, Object>> results = mysqlAssemble.returnMultipleResult(sqlAssemble, paramsAssemble);
				StringBuffer termText = new StringBuffer();
				for (int j = 0; j < results.size(); j++) {
					termText.append(results.get(j).get("FragmentContent").toString());
				}
				term.setTermText(termText.toString());
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			mysqlAssemble.closeconnection();
		}

		/**
		 * 根据主题内容，调用算法得到主题认知关系
		 */
		RankText rankText = new RankText();
//		List<Dependency> dependencies = rankText.rankText(termList, ClassName, Config.DEPENDENCEMAX); // 设置认知关系的数量为固定值
		List<Dependency> dependencies = rankText.rankText(termList, ClassName, termList.size()); // 设置认知关系的数量为主题的数量
		/**
		 * 指定领域，存储主题间的认知关系
		 */
		boolean success = false;
		mysqlUtils mysqlDependency = new mysqlUtils();
		String sqlDependency = "insert into " + Config.DEPENDENCY + "(ClassName,Start,StartID,End,EndID,Confidence) values(?,?,?,?,?,?);";
		try {
			for (int i = 0; i < dependencies.size(); i++) {
				Dependency dependency = dependencies.get(i);
				List<Object> paramsDependency = new ArrayList<Object>();
				paramsDependency.add(ClassName);
				paramsDependency.add(dependency.getStart());
				paramsDependency.add(dependency.getStartID());
				paramsDependency.add(dependency.getEnd());
				paramsDependency.add(dependency.getEndID());
				paramsDependency.add(dependency.getConfidence());
				success = mysqlDependency.addDeleteModify(sqlDependency, paramsDependency);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			mysqlDependency.closeconnection();
		}
		return success;
	}
	
}

