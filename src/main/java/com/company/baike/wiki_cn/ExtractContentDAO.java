package com.company.baike.wiki_cn;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.company.app.Config;
import com.company.utils.Log;
import com.company.baike.wiki_cn.domain.Assemble;
import com.company.baike.wiki_cn.domain.AssembleImage;

/**
 * 解析中文维基百科页面
 * 1. 现在返回的Assemble的List对象集合
 * 2. 三级分面信息
 * 3. 每一级分面有多个碎片，按照一个p标签作为一个碎片文本
 * @author yuanhao
 *
 */
public class ExtractContentDAO {

	
	
	public static void main(String[] args) throws Exception {

	}
	
	/**
	 *  读取一级或者二级标题，确定 LinkedList 是有序的
	 */
	public static void reviewTitles(Document doc){
		Log.log("-------------firstTitle----------------");
		LinkedList<String> firstTitle = getFirstTitle(doc);
		Log.log(firstTitle);
		Log.log("-------------secondTitle----------------");
		LinkedList<String> secondTitle = getSecondTitle(doc);
		Log.log(secondTitle);
		Log.log("-------------thirdTitle----------------");
		LinkedList<String> thirdTitle = getThirdTitle(doc);
		Log.log(thirdTitle);
		Log.log("-------------allTitle----------------");
		LinkedList<String> allTitle = getAllTitle(doc);
		Log.log(allTitle);
		Log.log("--------------title index---------------");
		LinkedList<Element> nodes = getNodes(doc);
		LinkedList<Integer> firstTitleIndex = getTitleIndex(firstTitle, nodes);
		compareTitleIndex(firstTitle, firstTitleIndex);
		LinkedList<Integer> secondTitleIndex = getTitleIndex(secondTitle, nodes);
		compareTitleIndex(secondTitle, secondTitleIndex);
		LinkedList<Integer> thirdTitleIndex = getTitleIndex(thirdTitle, nodes);
		compareTitleIndex(thirdTitle, thirdTitleIndex);
		LinkedList<Integer> allTitleIndex = getTitleIndex(allTitle, nodes);
		compareTitleIndex(allTitle, allTitleIndex);
		Log.log("--------------title content---------------");
		getFirstContent(doc);
		getSecondContent(doc);
		getThirdContent(doc);
		getSummary(doc);
	}
	
	
	/**
	 * 网页没有一级或者二级标题，网页内容的获取
	 * @param doc
	 * @return
	 */
	public static List<Assemble> getSpecialContent(Document doc){
		List<Assemble> assembleList = new ArrayList<Assemble>();
		Log.log("------------------ 页面所有内容 ----------------------");
		Elements para = doc.select("div#mw-content-text");
		if(para.size() != 0){
			String con = para.get(0).text();
			
			con = Config.converter.convert(con);
			Assemble assemble = new Assemble("摘要", con, 1);
			assembleList.add(assemble);
		}
		return assembleList;
	}
	
	/**
	 * 获取介绍信息
	 * @param doc
	 * @return
	 */
	public static List<Assemble> getSummary(Document doc) {
		List<Assemble> assembleList = new ArrayList<Assemble>();
		Log.log("------------------ 摘要内容 ----------------------");
		LinkedList<Element> list = getNodes(doc);
		String summary = "";
		int tocId = 0; 
		
		/**
		 * 获取summary的下标
		 */
		for (int i = 0; i < list.size(); i++) {
			Element child = list.get(i);
			Elements toc = child.select("div#toc");
			if (toc.size() != 0) {
				tocId = i;
				break;
			} else {
				Elements h = child.select("span.mw-headline");
				if(h.size()!=0){
					tocId = i;
					break;
				}
			}
		}
		
		/**
		 * 获取summary内容
		 */
		for (int i = 0; i < tocId; i++) {
			Element child = list.get(i);
			summary = child.text();
			summary = Config.converter.convert(summary);
			Log.log("摘要" + "--->" + summary);
			Assemble assemble = new Assemble("摘要", summary, 1);
			assembleList.add(assemble);
		}
		return assembleList;
	}
	
	/**
	 * 获取三级标题之间的内容
	 * @param doc
	 * @return
	 */
	public static List<Assemble> getThirdContent(Document doc){
		List<Assemble> assembleList = new ArrayList<Assemble>();
		LinkedList<String> allTitle = getAllTitle(doc);
		LinkedList<String> thirdTitle = getThirdTitle(doc);
		LinkedList<Element> nodes = getNodes(doc);

		/**
		 * 寻找一级和二级标题在节点链表的下标
		 */
		LinkedList<Integer> allTitleIndex = getTitleIndex(allTitle, nodes);

		/**
		 * 比较标题链表和对应的下标链表的大小是否相同，原则上是相同的，不相同说明网页存在问题等。。。
		 */
		int len = allTitle.size();
		int indexLen = allTitleIndex.size();
		if(len > indexLen){
			len = indexLen;
		}

		if (len == 0) {
			return null;
		}

		Log.log("------------------ 三级标题内容 ----------------------");
		/**
		 * 获取每个三级标题的内容，为该标题与相邻标题下标之间的节点内容
		 */
		for(int i = 0; i < len - 1; i++){
			String title = allTitle.get(i);
			for(int j = 0; j < thirdTitle.size(); j++){
				String thiTitle = thirdTitle.get(j);
				if(title.equals(thiTitle)){ // 遍历所有标题，寻找到三级标题
					String content = "";
					int begin = allTitleIndex.get(i);
					int end = allTitleIndex.get(i+1);
					Log.log(title + " ---> " + begin + "," + end);
					for(int k = begin + 1; k < end; k++){
						Element node = nodes.get(k);
						content = node.text();
						content = Config.converter.convert(content);
						Assemble assemble = new Assemble(title, content, 3);
						assembleList.add(assemble);
						Log.log(content);
					}
				}
			}
		}

		/**
		 * 所有标题的最后一个标题是否为三级标题
		 */
		String title = allTitle.get(len - 1);
		for(int j = 0; j < thirdTitle.size(); j++){
			String thiTitle = thirdTitle.get(j);
			if(title.equals(thiTitle)){ // 遍历所有标题，寻找到三级标题
				String content = "";
				int begin = allTitleIndex.get(len - 1);
				Log.log(title + " ---> " + begin + "," + (nodes.size()-1));
				for(int k = begin + 1; k < nodes.size(); k++){
					Element node = nodes.get(k);
					content = node.text();
					content = Config.converter.convert(content);
//					String imgTxt = "<img src=";
//					if(imgTxt.contains(imgTxt)){
//						content = content.substring(0, content.indexOf(imgTxt));
//					}
					Assemble assemble = new Assemble(title, content, 3);
					assembleList.add(assemble);
					Log.log(content);
				}
			}
		}
		return assembleList;
	}

	/**
	 * 获取二级标题之间的内容
	 * @param doc
	 * @return
	 */
	public static List<Assemble> getSecondContent(Document doc){
		List<Assemble> assembleList = new ArrayList<Assemble>();
		LinkedList<String> allTitle = getAllTitle(doc);
		LinkedList<String> secondTitle = getSecondTitle(doc);
		LinkedList<Element> nodes = getNodes(doc);

		/**
		 * 寻找一级和二级标题在节点链表的下标
		 */
		LinkedList<Integer> allTitleIndex = getTitleIndex(allTitle, nodes);
		
		/**
		 * 比较标题链表和对应的下标链表的大小是否相同，原则上是相同的，不相同说明网页存在问题等。。。
		 */
		int len = allTitle.size();
		int indexLen = allTitleIndex.size();
		if(len > indexLen){
			len = indexLen;
		}

		if (len == 0) {
			return null;
		}

		Log.log("------------------ 二级标题内容 ----------------------");
		/**
		 * 获取每个二级标题的内容，为该标题与相邻标题下标之间的节点内容
		 */
		for(int i = 0; i < len - 1; i++){
			String title = allTitle.get(i);
			for(int j = 0; j < secondTitle.size(); j++){
				String secTitle = secondTitle.get(j);
				if(title.equals(secTitle)){ // 遍历所有标题，寻找到二级标题
					String content = "";
					int begin = allTitleIndex.get(i);
					int end = allTitleIndex.get(i+1);
					Log.log(title + " ---> " + begin + "," + end);
					for(int k = begin + 1; k < end; k++){
						Element node = nodes.get(k);
						content = node.text();
						content = Config.converter.convert(content);
						Assemble assemble = new Assemble(title, content, 2);
						assembleList.add(assemble);
						Log.log(content);
					}
				}
			}
		}
		
		/**
		 * 所有标题的最后一个标题是否为二级标题
		 */
		String title = allTitle.get(len - 1);
		for(int j = 0; j < secondTitle.size(); j++){
			String secTitle = secondTitle.get(j);
			if(title.equals(secTitle)){ // 遍历所有标题，寻找到二级标题
				String content = "";
				int begin = allTitleIndex.get(len - 1);
				Log.log(title + " ---> " + begin + "," + (nodes.size()-1));
				for(int k = begin + 1; k < nodes.size(); k++){
					Element node = nodes.get(k);
					content = node.text();
					content = Config.converter.convert(content);
//					String imgTxt = "<img src=";
//					if(imgTxt.contains(imgTxt)){
//						content = content.substring(0, content.indexOf(imgTxt));
//					}
					Assemble assemble = new Assemble(title, content, 2);
					assembleList.add(assemble);
					Log.log(content);
				}
			}
		}
		return assembleList;
	}
	
	/**
	 * 获取一级标题之间的内容
	 * @param doc
	 * @return
	 */
	public static List<Assemble> getFirstContent(Document doc){
		List<Assemble> assembleList = new ArrayList<Assemble>();
		LinkedList<String> firstTitle = getFirstTitle(doc);
		LinkedList<Element> nodes = getNodes(doc);
		
		/**
		 * 寻找一级标题在节点链表的下标
		 */
		LinkedList<Integer> firstTitleIndex = getTitleIndex(firstTitle, nodes);

		/**
		 *  比较标题链表和对应的下标链表的大小是否相同，原则上是相同的，不相同说明网页存在问题等。。。
		 */
		int len = firstTitle.size();
		int indexLen = firstTitleIndex.size();
		Log.log("一级标题个数和一级标题下标个数：" + len + "," + indexLen);
		if(len > indexLen){
			len = indexLen;
		}

		if (len == 0) {
			return null;
		}

		Log.log("------------------ 一级标题内容 ----------------------");
		
		/**
		 * 获取每个一级标题的内容，为该标题与相邻标题下标之间的节点内容
		 */
		for(int i = 0; i < len - 1; i++){
			String title = firstTitle.get(i);
			String content = "";
			int begin = firstTitleIndex.get(i);
			int end = firstTitleIndex.get(i + 1);
			Log.log(title + " ---> " + begin + "," + end);
			for(int j = begin + 1; j < end; j++){
				Element node = nodes.get(j);
				content = node.text();
				content = Config.converter.convert(content);
				Assemble assemble = new Assemble(title, content, 1);
				assembleList.add(assemble);
				Log.log(content);
			}
		}
		
		/**
		 * 一级标题最后一个标题为该下标到节点最后
		 */
		String title = firstTitle.get(len - 1);
		String content = "";
		int begin = firstTitleIndex.get(len - 1);
		Log.log(title + " ---> " + begin + "," + (nodes.size()-1));
		for(int j = begin + 1; j < nodes.size(); j++){
			Element node = nodes.get(j);
			content = node.text();
			content = Config.converter.convert(content);
			Assemble assemble = new Assemble(title, content, 1);
			assembleList.add(assemble);
			Log.log(content);
		}
		return assembleList;
	}
	
	/**
	 * 解析得到图片的链接
	 * @param doc
	 * @return
	 */
	public static LinkedList<String> getImages(Document doc){
		LinkedList<String> allTitle = new LinkedList<String>();
		Elements titles = doc.select("div#mw-content-text").select("a").select("img");
		if(titles.size() != 0){
			for(int i = 0; i < titles.size(); i++){
				String url = titles.get(i).attr("src");
				int width = Integer.parseInt(titles.get(i).attr("width"));
				int height = Integer.parseInt(titles.get(i).attr("height"));
				Log.log(url);
				Log.log(width);
				Log.log(height);
			}
		}
		return allTitle;
	}
	
	/**
	 * 获取介绍部分的图片
	 * @param doc
	 * @return
	 */
	public static List<AssembleImage> getSummaryImage(Document doc) {
		List<AssembleImage> assembleImageList = new ArrayList<AssembleImage>();
		Log.log("------------------ 摘要图片 ----------------------");
		LinkedList<Element> list = getNodes(doc);
		int tocId = 0; 
		
		/**
		 * 获取summary的下标
		 */
		for (int i = 0; i < list.size(); i++) {
			Element child = list.get(i);
			Elements toc = child.select("div#toc");
			if (toc.size() != 0) {
				tocId = i;
				break;
			} else {
				Elements h = child.select("span.mw-headline");
				if(h.size()!=0){
					tocId = i;
					break;
				}
			}
		}
		
		/**
		 * 获取summary内容
		 */
		for (int i = 0; i < tocId; i++) {
			Element child = list.get(i);
			Elements images = child.select("a").select("img");
			int imagesLenth = images.size();
			if (imagesLenth != 0) {
				try {
					String url = images.get(0).attr("src");
					int width = Integer.parseInt(images.get(0).attr("width"));
					int height = Integer.parseInt(images.get(0).attr("height"));
					Log.log("摘要图片链接: " +  url + "---> 高度: " + width + "宽度: " + height);
					AssembleImage assembleimage = new AssembleImage(url, width, height, 1, "摘要");
					assembleImageList.add(assembleimage);
				} catch (Exception e) {
					Log.log("this image is not a standard picture...");
				}
				
			}
		}
		return assembleImageList;
	}
	
	/**
	 * 获取一级标题的图片
	 * @param doc
	 * @return
	 */
	public static List<AssembleImage> getFirstImage(Document doc){
		List<AssembleImage> assembleImageList = new ArrayList<AssembleImage>();
		LinkedList<String> firstTitle = getFirstTitle(doc);
		LinkedList<Element> nodes = getNodes(doc);
		
		/**
		 * 寻找一级标题在节点链表的下标
		 */
		LinkedList<Integer> firstTitleIndex = getTitleIndex(firstTitle, nodes);
		
		/**
		 *  比较标题链表和对应的下标链表的大小是否相同，原则上是相同的，不相同说明网页存在问题等。。。
		 */
		int len = firstTitle.size();
		int indexLen = firstTitleIndex.size();
		if(len > indexLen){
			len = indexLen;
		}

		if (len == 0) {
			return null;
		}

		Log.log("------------------ 一级标题内图片----------------------");
		
		/**
		 * 获取每个一级标题的图片，为该标题与相邻标题下标之间的节点图片
		 */
		for(int i = 0; i < len - 1; i++){
			String title = firstTitle.get(i);
			int begin = firstTitleIndex.get(i);
			int end = firstTitleIndex.get(i + 1);
			Log.log(title + " ---> " + begin + "," + end);
			for(int j = begin + 1; j < end; j++){
				Element node = nodes.get(j);
				Elements images = node.select("a").select("img");
				int imagesLenth = images.size();
				if (imagesLenth != 0) {
					try {
						String url = images.get(0).attr("src");
						int width = Integer.parseInt(images.get(0).attr("width"));
						int height = Integer.parseInt(images.get(0).attr("height"));
						Log.log("一级标题图片链接: " +  url + "---> 高度: " + width + "宽度: " + height);
						AssembleImage assembleimage = new AssembleImage(url, width, height, 1, title);
						assembleImageList.add(assembleimage);
					} catch (Exception e) {
						Log.log("this image is not a standard picture...");
					}
				}
			}
		}
		
		/**
		 * 一级标题最后一个标题为该下标到节点最后
		 */
		String title = firstTitle.get(len - 1);
		int begin = firstTitleIndex.get(len - 1);
		Log.log(title + " ---> " + begin + "," + (nodes.size()-1));
		for(int j = begin + 1; j < nodes.size(); j++){
			Element node = nodes.get(j);
			Elements images = node.select("a").select("img");
			int imagesLenth = images.size();
			if (imagesLenth != 0) {
				try {
					String url = images.get(0).attr("src");
					int width = Integer.parseInt(images.get(0).attr("width"));
					int height = Integer.parseInt(images.get(0).attr("height"));
					Log.log("一级标题图片链接: " +  url + "---> 高度: " + width + "宽度: " + height);
					AssembleImage assembleimage = new AssembleImage(url, width, height, 1, title);
					assembleImageList.add(assembleimage);
				} catch (Exception e) {
					Log.log("this image is not a standard picture...");
				}
				
			}
		}
		return assembleImageList;
	}
	
	/**
	 * 获取二级标题的图片
	 * @param doc
	 * @return
	 */
	public static List<AssembleImage> getSecondImage(Document doc){
		List<AssembleImage> assembleImageList = new ArrayList<AssembleImage>();
		LinkedList<String> allTitle = getAllTitle(doc);
		LinkedList<String> secondTitle = getSecondTitle(doc);
		LinkedList<Element> nodes = getNodes(doc);

		/**
		 * 寻找一级和二级标题在节点链表的下标
		 */
		LinkedList<Integer> allTitleIndex = getTitleIndex(allTitle, nodes);
		
		/**
		 * 比较标题链表和对应的下标链表的大小是否相同，原则上是相同的，不相同说明网页存在问题等。。。
		 */
		int len = allTitle.size();
		int indexLen = allTitleIndex.size();
		if(len > indexLen){
			len = indexLen;
		}

		if (len == 0) {
			return null;
		}

		Log.log("------------------ 二级标题内容 ----------------------");
		/**
		 * 获取每个二级标题的内容，为该标题与相邻标题下标之间的节点内容
		 */
		for(int i = 0; i < len - 1; i++){
			String title = allTitle.get(i);
			for(int j = 0; j < secondTitle.size(); j++){
				String secTitle = secondTitle.get(j);
				if(title.equals(secTitle)){ // 遍历所有标题，寻找到二级标题
					int begin = allTitleIndex.get(i);
					int end = allTitleIndex.get(i+1);
					Log.log(title + " ---> " + begin + "," + end);
					for(int k = begin + 1; k < end; k++){
						Element node = nodes.get(k);
						Elements images = node.select("a").select("img");
						int imagesLenth = images.size();
						if (imagesLenth != 0) {
							try {
								String url = images.get(0).attr("src");
								int width = Integer.parseInt(images.get(0).attr("width"));
								int height = Integer.parseInt(images.get(0).attr("height"));
								Log.log("二级标题图片链接: " +  url + "---> 高度: " + width + "宽度: " + height);
								AssembleImage assembleimage = new AssembleImage(url, width, height, 2, secTitle);
								assembleImageList.add(assembleimage);
							} catch (Exception e) {
								Log.log("this image is not a standard picture...");
							}
							
						}
					}
				}
			}
		}
		
		/**
		 * 所有标题的最后一个标题是否为二级标题
		 */
		String title = allTitle.get(len - 1);
		for(int j = 0; j < secondTitle.size(); j++){
			String secTitle = secondTitle.get(j);
			if(title.equals(secTitle)){ // 遍历所有标题，寻找到二级标题
				int begin = allTitleIndex.get(len - 1);
				Log.log(title + " ---> " + begin + "," + (nodes.size()-1));
				for(int k = begin + 1; k < nodes.size(); k++){
					Element node = nodes.get(k);
					Elements images = node.select("a").select("img");
					int imagesLenth = images.size();
					if (imagesLenth != 0) {
						try {
							String url = images.get(0).attr("src");
							int width = Integer.parseInt(images.get(0).attr("width"));
							int height = Integer.parseInt(images.get(0).attr("height"));
							Log.log("二级标题图片链接: " +  url + "---> 高度: " + width + "宽度: " + height);
							AssembleImage assembleimage = new AssembleImage(url, width, height, 2, secTitle);
							assembleImageList.add(assembleimage);
						} catch (Exception e) {
							Log.log("this image is not a standard picture...");
						}
						
					}
				}
			}
		}
		return assembleImageList;
	}
	
	/**
	 * 获取三级标题的图片
	 * @param doc
	 * @return
	 */
	public static List<AssembleImage> getThirdImage(Document doc){
		List<AssembleImage> assembleImageList = new ArrayList<AssembleImage>();
		LinkedList<String> allTitle = getAllTitle(doc);
		LinkedList<String> thirdTitle = getThirdTitle(doc);
		LinkedList<Element> nodes = getNodes(doc);

		/**
		 * 寻找一级和二级标题在节点链表的下标
		 */
		LinkedList<Integer> allTitleIndex = getTitleIndex(allTitle, nodes);

		/**
		 * 比较标题链表和对应的下标链表的大小是否相同，原则上是相同的，不相同说明网页存在问题等。。。
		 */
		int len = allTitle.size();
		int indexLen = allTitleIndex.size();
		if(len > indexLen){
			len = indexLen;
		}

		if (len == 0) {
			return null;
		}

		Log.log("------------------ 三级标题内容 ----------------------");
		/**
		 * 获取每个三级标题的内容，为该标题与相邻标题下标之间的节点内容
		 */
		for(int i = 0; i < len - 1; i++){
			String title = allTitle.get(i);
			for(int j = 0; j < thirdTitle.size(); j++){
				String thiTitle = thirdTitle.get(j);
				if(title.equals(thiTitle)){ // 遍历所有标题，寻找到三级标题
					int begin = allTitleIndex.get(i);
					int end = allTitleIndex.get(i+1);
					Log.log(title + " ---> " + begin + "," + end);
					for(int k = begin + 1; k < end; k++){
						Element node = nodes.get(k);
						Elements images = node.select("a").select("img");
						int imagesLenth = images.size();
						if (imagesLenth != 0) {
							try {
								String url = images.get(0).attr("src");
								int width = Integer.parseInt(images.get(0).attr("width"));
								int height = Integer.parseInt(images.get(0).attr("height"));
								Log.log("三级标题图片链接: " +  url + "---> 高度: " + width + "宽度: " + height);
								AssembleImage assembleimage = new AssembleImage(url, width, height, 3, thiTitle);
								assembleImageList.add(assembleimage);
							} catch (Exception e) {
								Log.log("this image is not a standard picture...");
							}
							
						}
					}
				}
			}
		}

		/**
		 * 所有标题的最后一个标题是否为三级标题
		 */
		String title = allTitle.get(len - 1);
		for(int j = 0; j < thirdTitle.size(); j++){
			String thiTitle = thirdTitle.get(j);
			if(title.equals(thiTitle)){ // 遍历所有标题，寻找到三级标题
				int begin = allTitleIndex.get(len - 1);
				Log.log(title + " ---> " + begin + "," + (nodes.size()-1));
				for(int k = begin + 1; k < nodes.size(); k++){
					Element node = nodes.get(k);
					Elements images = node.select("a").select("img");
					int imagesLenth = images.size();
					if (imagesLenth != 0) {
						try {
							String url = images.get(0).attr("src");
							int width = Integer.parseInt(images.get(0).attr("width"));
							int height = Integer.parseInt(images.get(0).attr("height"));
							Log.log("三级标题图片链接: " +  url + "---> 高度: " + width + "宽度: " + height);
							AssembleImage assembleimage = new AssembleImage(url, width, height, 3, thiTitle);
							assembleImageList.add(assembleimage);
						} catch (Exception e) {
							Log.log("this image is not a standard picture...");
						}
						
					}
				}
			}
		}
		return assembleImageList;
	}

	/**
	 * 寻找一级标题在节点链表的下标
	 * @param titleList
	 * @param nodes
	 * @return
	 */
	public static LinkedList<Integer> getTitleIndex(LinkedList<String> titleList, LinkedList<Element> nodes){
		LinkedList<Integer> firstTitleIndex = new LinkedList<Integer>();
		// 寻找一级标题在节点链表的下标
		for(int i = 0; i < titleList.size(); i++){
			String title = titleList.get(i);
			for(int j = 0; j < nodes.size(); j++){
				Element node = nodes.get(j);
				Elements h2 = node.select("span.mw-headline");
				if(h2.size() != 0){
					String level1 = h2.get(0).text();
					level1 = Config.converter.convert(level1);
					if(title.equals(level1)){// 匹配到一级标题的下标
						firstTitleIndex.add(j);
					}
				}
			}
		}
		return firstTitleIndex;
	}

	/**
	 * 读取一下标题的下标，确认是否正确
	 * @param title
	 * @param titleIndex
	 */
	public static void compareTitleIndex(LinkedList<String> title, LinkedList<Integer> titleIndex){
		Log.log("------------------ compare title and index ------------------");
		// 读取一下标题的下标，确认是否正确
		Log.log("title size is : " + title.size());
		Log.log("titleIndex size is : " + titleIndex.size());
		// 比较标题链表和对应的下标链表的大小是否相同，原则上是相同的，不相同说明网页存在问题等。。。
		int len = title.size();
		int indexLen = titleIndex.size();
		if(len > indexLen){
			len = indexLen;
		}
		if(len != indexLen){
			Log.log("+++++++++++++++++++ title don't suit index +++++++++++++++++++");
		}
		for(int i = 0; i < len; i++){
			String tit = title.get(i);
			int index = titleIndex.get(i);
			Log.log(tit + " ---> " + index);
		}
	}
	
	
	/**
	 * 获取一级、二级和三级标题
	 * @param doc
	 * @return
	 */
	public static LinkedList<String> getAllTitle(Document doc){
		LinkedList<String> allTitle = new LinkedList<String>();
		Elements titles = doc.select("div#mw-content-text").select("span.mw-headline");
		if(titles.size() != 0){
			for(int i = 0; i < titles.size(); i++){
				String head = titles.get(i).text();
				head = Config.converter.convert(head);
				Boolean flag = delTitle(head);
				if(!flag){
					allTitle.add(head);
				}
			}
		}
		return allTitle;
	}
	
	/**
	 * 获取三级标题
	 * @param doc
	 * @return
	 */
	public static LinkedList<String> getThirdTitle(Document doc){
		LinkedList<String> thirdTitle = new LinkedList<String>();
		Elements titles = doc.select("div#mw-content-text").select("h4");
		if(titles.size() != 0){
			for(int i = 0; i < titles.size(); i++){
				String level3 = titles.get(i).select("span.mw-headline").get(0).text();
				level3 = Config.converter.convert(level3);
				Boolean flag = delTitle(level3);
				if(!flag){
					thirdTitle.add(level3);
				}
			}
		}
		return thirdTitle;
	}
	
	/**
	 * 获取二级标题
	 * @param doc
	 * @return
	 */
	public static LinkedList<String> getSecondTitle(Document doc){
		LinkedList<String> secondTitle = new LinkedList<String>();
		Elements titles = doc.select("div#mw-content-text").select("h3");
		if(titles.size() != 0){
			for(int i = 0; i < titles.size(); i++){
				String level2 = titles.get(i).select("span.mw-headline").get(0).text();
				level2 = Config.converter.convert(level2);
				Boolean flag = delTitle(level2);
				if(!flag){
					secondTitle.add(level2);
				}
			}
		}
		return secondTitle;
	}
	
	/**
	 * 获取一级标题
	 * @param doc
	 * @return
	 */
	public static LinkedList<String> getFirstTitle(Document doc){
		LinkedList<String> firstTitle = new LinkedList<String>();
		Elements titles = doc.select("div#mw-content-text").select("h2");
		if(titles.size() != 0){
			for(int i = 0; i < titles.size(); i++){
				Elements lel = titles.get(i).select("span.mw-headline");
				if(lel.size() != 0){
					String level1 = lel.get(0).text();
					level1 = Config.converter.convert(level1);
					Boolean flag = delTitle(level1);
					if(!flag){
						firstTitle.add(level1);
					}
				}
			}
		}
		return firstTitle;
	}

	/**
	 * 将html内容中的所有子节点写到链表中
	 * @param doc
	 * @return
	 */
	public static LinkedList<Element> getNodes(Document doc){
		Element mainContent = doc.select("div.mw-content-ltr").get(0).child(0);
		Elements childs = mainContent.children();
		LinkedList<Element> list = new LinkedList<Element>();
		for (Element e : childs) {
			list.offer(e);
		}
		return list;
	}

	/**
	 * 去除无用标题
	 * @param title
	 * @return
	 */
	public static Boolean delTitle(String title){
//		Boolean useless = false;
		Boolean useless = title.equals("注释与参考文献")
				|| title.equals("参考文献") || title.equals("外部链接")|| title.equals("参考资料")
				|| title.equals("外部连结") || title.equals("相关条目")
				|| title.equals("参见") || title.equals("另见")|| title.equals("参看")
				|| title.equals("参考") || title.equals("参照") || title.equals("参阅")
				|| title.equals("注释") || title.equals("延伸阅读"); // 判断标题是否为无用的
		return useless;
	}

	/**
	 * 解析发布时间
	 * @param doc
	 * @return
	 */
	public static String getPostTime(Document doc) {
		String time = "";
		Elements content = doc.select("li#footer-info-lastmod");
		if (content.size() != 0) {
			Elements timeItem = content;
			time = timeItem.get(0).text();
			try {
				time = postTimeDeal(time);
			} catch (Exception e) {
				time = "2016-01-01 00:00:00";
			}
//			Log.log("post time is : " + time);
		} else {
			Log.log("constructKGByDomainName time has some bugs ...");
		}
		return time;
	}
	
	/**
	 * 对中文维基百科的时间格式进行处理，使其可以用于数据库的插入
	 * 原始格式：" 本页面最后修订于2016年1月22日 (星期五) 11:22。"
	 * 标准格式：2016-01-22 11:22:00
	 * @param time
	 * @return
	 */
	public static String postTimeDeal(String time) {
		// String time = " 本页面最后修订于2016年1月22日 (星期五) 11:22。";
		String[] time0 = time.split("修订于");
		String[] time1 = time0[1].split("年");
		String[] time2 = time1[1].split("月");
		String[] time3 = time2[1].split("日");
		String[] time4 = time3[1].split("\\)");
		String[] time5 = time4[1].split("。");
		String year = time1[0];
		String month = time2[0];
		String day = time3[0];
		String clock = time5[0].substring(1, time5[0].length());
		time = year + "-" + month + "-" + day + " " + clock + ":00";
		return time;
	}

	/**
	 * 获取各级标题与分面的对应情况
	 * @param doc
	 * @return
	 */
	public static HashMap<String, String> getTitleRelationWiki(Document doc){
		LinkedList<String> indexs = new LinkedList<String>();// 标题前面的下标
		LinkedList<String> facets = new LinkedList<String>();// 各级标题的名字
		LinkedList<String> results = new LinkedList<String>();// 二级/三级标题对应到一级标题之后的标题
		HashMap<String, String> relation = new HashMap<String, String>();

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
				results.add(text);
			}

			/**
			 * 将二级/三级标题全部匹配到对应的一级标题
			 */
			Log.log("--------------------------------------------");
			for(int i = 0; i < indexs.size(); i++){
				String index = indexs.get(i);
				if(index.contains(".")){
					for(int j = i-1; j >= 0; j--){ // 从二级/三级标题往前搜索，遇到第一个下标不是"▪"的标题即是对应的一级标题
						String indexCom = indexs.get(j);
						if(!indexCom.contains(".")){
							String facetOne = facets.get(j);
							results.set(i, facetOne);
							break;
						}
					}
				}
			}

			/**
			 * 打印最新的标题信息，确定更新二级/三级标题成功
			 */
			Log.log("--------------------------------------------");
			for(int i = 0; i < facets.size(); i++){
				relation.put(facets.get(i), results.get(i));
				Log.log(indexs.get(i) + "-->" + facets.get(i) + "-->" + results.get(i));
			}

		} else {
			Log.log("该主题没有目录，不是目录结构，直接爬取 -->摘要<-- 信息");
		}

		return relation;
	}
	
	/**
	 * 获取字符串的长度，如果有中文，则每个中文字符计为2位
	 * @param value 指定的字符串
	 * @return 
	 * @return 字符串的长度
	 */
	public static int getContentLen(String value) {
//		String value = "hello你好";
		int valueLength = 0;
		String chinese = "[\u0391-\uFFE5]";
		/* 获取字段值的长度，如果含中文字符，则每个中文字符长度为2，否则为1 */
		for (int i = 0; i < value.length(); i++) {
			/* 获取一个字符 */
			String temp = value.substring(i, i + 1);
			/* 判断是否为中文字符 */
			if (temp.matches(chinese)) {
				/* 中文字符长度为2 */
				valueLength += 2;
			} else {
				/* 其他字符长度为1 */
				valueLength += 1;
			}
		}
		Log.log(valueLength);
		return valueLength;
	}
	
	
}

