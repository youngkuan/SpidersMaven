package com.company.baike.wiki_cn;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import com.company.app.Config;
import com.company.utils.JsoupDao;
import com.company.utils.Log;
import com.company.baike.wiki_cn.domain.Assemble;
import com.company.baike.wiki_cn.domain.AssembleImage;
import com.company.baike.wiki_cn.domain.FacetRelation;
import com.company.baike.wiki_cn.domain.FacetSimple;

/**
 * 实现中文维基百科知识森林数据集的构建
 * 
 * @author 郑元浩
 *
 */
public class CrawlerContentDAO {



	public static void main(String[] args) throws Exception {
		test();
	}

	/**
	 *  单个中文维基页面解析测试程序
	 * @throws Exception
	 */
	public static void test() throws Exception{

		/**
		 * 设置解析参数
		 */
		String topicUrl = "https://zh.wikipedia.org/wiki/User_talk:Liangent-test/CXTest";
//		String topicUrl = "https://zh.wikipedia.org/wiki/%E6%95%B0%E6%8D%AE%E5%BA%93";
//		String topicUrl = "https://zh.wikipedia.org/wiki/%E9%93%BE%E8%A1%A8";
//		String topicUrl = "https://zh.wikipedia.org/wiki/%E8%B7%B3%E8%B7%83%E5%88%97%E8%A1%A8";
//		String topicUrl = "https://zh.wikipedia.org/wiki/%E6%95%B0%E6%8D%AE%E7%BB%93%E6%9E%84%E4%B8%8E%E7%AE%97%E6%B3%95%E5%88%97%E8%A1%A8";
		String topicHtml = DownloaderDAO.seleniumWikiCN(topicUrl);
		Document doc = JsoupDao.parseHtmlText(topicHtml);

		/**
		 * 测试解析小程序
		 */
//		List<FacetRelation> facetRelationList = getFacetRelation(doc);
//		Log.logFacetRelation(facetRelationList);

		/**
		 * 解析所有内容
		 */
//		getAllContent(doc, false, false, false); // 只有summary内容
//		getAllContent(doc, true, false, false); // summary内容 + 一级标题内容
//		getAllContent(doc, true, true, false); // summary内容 + 一级和二级标题内容
//		getAllContent(doc, true, true, true); // summary内容 + 一级/二级/三级标题内容
		
		/**
		 * 解析图片内容
		 */
		getAllImage(doc, true, true, true);
	}

	/**
	 * 获取各级分面父子对应关系
	 * @param doc
	 * @return 
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
						Log.log("二级标题");
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
						Log.log("三级标题");
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

		/**
		 * 判断条件和内容函数保持一致
		 * facet中的分面与spider和assemble表格保持一致
		 */
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
			/**
			 * 保存一级分面名及其分面级数
			 */
			for(int i = 0; i < firstTitle.size(); i++){
				String facetName = firstTitle.get(i);
				int facetLayer = 1;
				FacetSimple facetSimple = new FacetSimple(facetName, facetLayer);
				facetList.add(facetSimple);
			}
			/**
			 * 保存二级分面名及其分面级数
			 */
			for(int i = 0; i < secondTitle.size(); i++){
				String facetName = secondTitle.get(i);
				int facetLayer = 2;
				FacetSimple facetSimple = new FacetSimple(facetName, facetLayer);
				facetList.add(facetSimple);
			}
			/**
			 * 保存三级分面名及其分面级数
			 */
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
	 * @param doc
	 * @param flagFirst  一级标题标志位
	 * @param flagSecond  二级标题标志位
	 * @param flagThird  三级标题标志位
	 * @return
	 */
	public static List<Assemble> getAllContent(Document doc, 
			boolean flagFirst, boolean flagSecond, boolean flagThird){

		List<Assemble> assembleList = new ArrayList<Assemble>();

		Elements mainContents = doc.select("div#mw-content-text").select("span.mw-headline");
		if(mainContents.size() == 0){
			/**
			 * 网页全部内容
			 */
			List<Assemble> specialContent = ExtractContentDAO.getSpecialContent(doc); // 没有目录栏的词条信息
			assembleList.addAll(specialContent);
		} else {
			/**
			 * 摘要信息
			 */
			List<Assemble> summaryContent = ExtractContentDAO.getSummary(doc); // 摘要内容
			assembleList.addAll(summaryContent);
			/**
			 * flagFirst 为 true，保留一级分面数据
			 */
			if(flagFirst){
				LinkedList<String> firstTitle = ExtractContentDAO.getFirstTitle(doc);
				if(firstTitle.size() != 0){
					List<Assemble> firstContent = ExtractContentDAO.getFirstContent(doc); // 一级分面内容
					if (firstContent != null) {
						assembleList.addAll(firstContent);
					}
				}
			}
			/**
			 * flagSecond 为 true，保留二级分面数据
			 */
			if(flagSecond){
				LinkedList<String> secondTitle = ExtractContentDAO.getSecondTitle(doc);
				if(secondTitle.size() != 0){
					List<Assemble> secondContent = ExtractContentDAO.getSecondContent(doc); // 二级分面内容
					if (secondContent != null) {
						assembleList.addAll(secondContent);
					}
				}
			}
			/**
			 * flagThird 为 true，保留三级分面数据
			 */
			if(flagThird){ 
				LinkedList<String> thirdTitle = ExtractContentDAO.getThirdTitle(doc);
				if(thirdTitle.size() != 0){
					List<Assemble> thirdContent = ExtractContentDAO.getThirdContent(doc); // 三级分面内容
					if (thirdContent != null) {
						assembleList.addAll(thirdContent);
					}
				}
			}
		}
		return assembleList;
	}

	/**
	 * 保存所有信息，如果某个分面含有子分面，那么这个分面下面应该没有碎片
	 * 1. 三级分面及其文本碎片
	 * 2. 有子分面的父分面没有文本碎片
	 * 3. 一个段落作为一个碎片
	 * @param domain
	 * @param topic
	 * @param doc
	 * @param flagFirst
	 * @param flagSecond
	 * @param flagThird
	 * @return
	 */
	public static List<Assemble> getAllContentNew(String domain, String topic, Document doc,
			boolean flagFirst, boolean flagSecond, boolean flagThird){
		List<Assemble> assembleResultList = new ArrayList<Assemble>();
		List<Assemble> assembleList = getAllContent(doc, flagFirst, flagSecond, flagThird);
		for(int i = 0; i < assembleList.size(); i++){
			Assemble assemble = assembleList.get(i);
			/**
			 * 判断该文本碎片对应的分面是否包含子分面
			 * 判断该文本碎片为那个不需要的文本
			 * 去除长度很短且无意义的文本碎片
			 */
			Boolean exist = MysqlReadWriteDAO.judgeFacetRelation(assemble, domain, topic);
			Boolean existImg = judgeBadText(assemble);
			Boolean lenBoolean = ExtractContentDAO.getContentLen(assemble.getFacetContent()) > Config.CONTENTLENGTH;
			/**
			 * 保存满足条件的图片链接
			 */
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
		String badTxt1 = "<img src=";
		String badTxt2 = "本条目没有列出任何参考或来源";
		String badTxt3 = "目标页面不存在";
		String badTxt4 = "本条目存在以下问题";
		String badTxt5 = "本条目需要扩充";
		String badTxt6 = "[隐藏] 查 论 编";
		if(facetContent.contains(badTxt1) || facetContent.contains(badTxt2) || facetContent.contains(badTxt3)
				 || facetContent.contains(badTxt4) || facetContent.contains(badTxt5) || facetContent.contains(badTxt6)){
			exist = true;
		}
		return exist;
	}
	
	/**
	 * 将从"摘要"到各级标题的所有分面图片内容全部存到一起
	 * 1. 三级分面及其图片碎片
	 * 2. 每一级分面都有图片碎片
	 * 3. 图片数量较少，一般可能为空
	 * @param doc
	 * @param flagFirst  一级标题标志位
	 * @param flagSecond  二级标题标志位
	 * @param flagThird  三级标题标志位
	 * @return
	 */
	public static List<AssembleImage> getAllImage(Document doc, 
			boolean flagFirst, boolean flagSecond, boolean flagThird){

		List<AssembleImage> assembleImageList = new ArrayList<AssembleImage>();

		Elements images = doc.select("div#mw-content-text").select("a").select("img");
		if(images.size() == 0){
			Log.log("this page doesn't have any images...");
		} else {
			
			/**
			 * 摘要信息
			 */
			List<AssembleImage> summaryImage = ExtractContentDAO.getSummaryImage(doc); // 摘要图片
			assembleImageList.addAll(summaryImage);
			
			/**
			 * flagFirst 为 true，保留一级分面数据
			 */
			if(flagFirst){
				LinkedList<String> firstTitle = ExtractContentDAO.getFirstTitle(doc);
				if(firstTitle.size() != 0){
					List<AssembleImage> firstContent = ExtractContentDAO.getFirstImage(doc); // 一级分面图片
					if (firstContent != null) {
						assembleImageList.addAll(firstContent);
					}
				}
			}
			
			/**
			 * flagSecond 为 true，保留二级分面数据
			 */
			if(flagSecond){
				LinkedList<String> secondTitle = ExtractContentDAO.getSecondTitle(doc);
				if(secondTitle.size() != 0){
					List<AssembleImage> secondContent = ExtractContentDAO.getSecondImage(doc); // 二级分面图片
					if (secondContent != null) {
						assembleImageList.addAll(secondContent);
					}
				}
			}
			
			/**
			 * flagThird 为 true，保留三级分面数据
			 */
			if(flagThird){ 
				LinkedList<String> thirdTitle = ExtractContentDAO.getThirdTitle(doc);
				if(thirdTitle.size() != 0){
					List<AssembleImage> thirdContent = ExtractContentDAO.getThirdImage(doc); // 三级分面图片
					if (thirdContent != null) {
						assembleImageList.addAll(thirdContent);
					}
				}
			}
			
		}
		return assembleImageList;
	}
	
	/**
	 * 保存所有图片信息，如果某个分面含有子分面，那么这个分面下面应该没有图片碎片（目前只考虑一级分面和二级分面）
	 * 1. 三级分面及其图片碎片
	 * 2. 有子分面的父分面没有图片碎片
	 * 3. 图片数量较少，一般可能为空
	 * @param domain
	 * @param topic
	 * @param doc
	 * @param flagFirst
	 * @param flagSecond
	 * @param flagThird
	 * @return
	 */
	public static List<AssembleImage> getAllImageNew(String domain, String topic, Document doc, 
			boolean flagFirst, boolean flagSecond, boolean flagThird){
		List<AssembleImage> assembleResultList = new ArrayList<AssembleImage>();
		List<AssembleImage> assembleImageList = getAllImage(doc, flagFirst, flagSecond, flagThird);
		for(int i = 0; i < assembleImageList.size(); i++){
			AssembleImage assembleImage = assembleImageList.get(i);
			/**
			 * 判断该图片对应的分面是否包含子分面
			 * 判断图片是否为图标图片（不需要）
			 */
			Boolean exist = MysqlReadWriteDAO.judgeFacetRelation(assembleImage, domain, topic);
			Boolean existUselessImg = judgeBadImage(assembleImage);
			/**
			 * 保存满足条件的图片链接
			 */
			if (!exist && !existUselessImg) {
				assembleResultList.add(assembleImage);
			}
		}
		return assembleResultList;
	}
	
	

	/**
	 * 判断是否为不需要的图片链接
	 * @return
	 */
	public static Boolean judgeBadImage(AssembleImage assembleImage){
		Boolean exist = false;
		String imgUrl = assembleImage.getImageUrl();
		String badImgUrl1 = "//upload.wikimedia.org/wikipedia/commons/thumb/2/2d/Stub_W.svg/40px-Stub_W.svg.png";
		String badImgUrl2 = "//upload.wikimedia.org/wikipedia/commons/thumb/4/4e/Tango-nosources.svg/45px-Tango-nosources.svg.png";
		String badImgUrl3 = "//upload.wikimedia.org/wikipedia/commons/thumb/1/1b/Ambox_question.svg/40px-Ambox_question.svg.png";
		String badImgUrl4 = "//upload.wikimedia.org/wikipedia/commons/thumb/b/be/PC_template.svg/25px-PC_template.svg.png";
		String badImgUrl5 = "//upload.wikimedia.org/wikipedia/commons/thumb/a/aa/Merge-arrow.svg/50px-Merge-arrow.svg.png";
		String badImgUrl6 = "//upload.wikimedia.org/wikipedia/commons/thumb/c/c9/Portal.svg/32px-Portal.svg.png";
		String badImgUrl7 = "//upload.wikimedia.org/wikipedia/commons/thumb/2/2a/Antistub.svg/44px-Antistub.svg.png";
		String badImgUrl8 = "//upload.wikimedia.org/wikipedia/commons/thumb/d/d7/Ambox_spelling.svg/48px-Ambox_spelling.svg.png";
		String badImgUrl9 = "//upload.wikimedia.org/wikipedia/commons/thumb/e/e1/Ambox_wikify.svg/40px-Ambox_wikify.svg.png";
		String badImgUrl10 = "//upload.wikimedia.org/wikipedia/commons/thumb/1/17/Formal_logic_template.svg/23px-Formal_logic_template.svg.png";
		String badImgUrl11 = "//upload.wikimedia.org/wikipedia/commons/thumb/0/0f/Mergefrom.svg/50px-Mergefrom.svg.png";
		String badImgUrl12 = "//upload.wikimedia.org/wikipedia/commons/thumb/b/be/Gamepad.svg/32px-Gamepad.svg.png";
		String badImgUrl13 = "//upload.wikimedia.org/wikipedia/commons/thumb/a/a4/Text_document_with_red_question_mark.svg/40px-Text_document_with_red_question_mark.svg.png";
		if(imgUrl.equals(badImgUrl1) || imgUrl.equals(badImgUrl2) || imgUrl.equals(badImgUrl3) ||
				imgUrl.equals(badImgUrl4) || imgUrl.equals(badImgUrl5) || imgUrl.equals(badImgUrl6) ||
				imgUrl.equals(badImgUrl7) || imgUrl.equals(badImgUrl8) || imgUrl.equals(badImgUrl9) ||
				imgUrl.equals(badImgUrl10) || imgUrl.equals(badImgUrl11) || imgUrl.equals(badImgUrl12) ||
				imgUrl.equals(badImgUrl13)){
			exist = true;
		}
		return exist;
	}



//	/**
//	 * 二级/三级标题转化为一级标题
//	 * @param title
//	 * @param relation
//	 * @return
//	 */
//	public static String titleToFacet(String title, HashMap<String, String> relation){
//		String facetName = title;
//		for(Entry<String, String> entry : relation.entrySet()){
//			String tit = entry.getKey();
//			String facet = entry.getValue();
//			if(title.equals(tit)){
//				facetName = facet;
//				break;
//			}
//		}
//		return facetName;
//	}



}

