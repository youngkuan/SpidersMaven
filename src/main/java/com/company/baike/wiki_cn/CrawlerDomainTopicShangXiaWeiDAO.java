package com.company.baike.wiki_cn;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import com.company.app.Config;
import com.company.utils.JsoupDao;
import com.company.utils.Log;
import com.company.baike.wiki_cn.domain.Relation;
import com.company.baike.wiki_cn.domain.Term;

import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;


/**
 * 获取主题及其上下位关系，将其写到数据库中
 * 
 * @author 郑元浩
 * @date 2016年12月19日
 */
public class CrawlerDomainTopicShangXiaWeiDAO {

	public static void main(String[] args) {
		/**
		 * 处理生成所有主题及其上下位关系，excel表格操作，只用一次执行
		 */
//		getData();
		/**
		 * 存储所有主题及其上下位关系
		 */
//		storeTopicFromExcel();
		storeRelationFromExcel();
	}
	
	/**
	 * 得到所有主题及其上下位关系，读取本地excel进行处理
	 */
	public static void getData(){
		String path = "file\\topic.txt";
		List<String> topicList = getTopics(path);
		String excelPath = "file\\treeEdges.xls";
		List<Relation> relationList = getRelation(excelPath);
		
		/**
		 * 翻译英文主题到中文保存到本地excel
		 */
		String topicCnPath = "file\\topic_cn.xls";
		String topicRelationCnPath = "file\\topic_relation_cn.xls";
		Map<String, String> topicResultMap = getTopicCn(topicList);
		storeTopic(topicResultMap, topicCnPath);
		storeTopicRelationCn(relationList, topicResultMap, topicRelationCnPath);
	}
	
	/**
	 * 爬取指定文件目录下的所有主题信息（英文）
	 * @param path
	 * @return
	 */
	public static List<String> getTopics(String path) {
		List<String> topicList = new ArrayList<String>();
		try {
			String encoding = "utf-8";
			File file = new File(path);
			if (file.isFile() && file.exists()) {
				InputStreamReader read = new InputStreamReader(new FileInputStream(file), encoding);
				BufferedReader bufferedReader = new BufferedReader(read);
				String lineTxt = null;
				while ((lineTxt = bufferedReader.readLine()) != null) {
					topicList.add(lineTxt);
					Log.log(lineTxt);
				}
				read.close();
			} else {
				System.out.println("找不到指定的文件");
			}
		} catch (Exception e) {
			System.out.println("读取文件内容出错");
			e.printStackTrace();
		}
		return topicList;
	}
	
	/**
	 * 得到一个excel表格中的所有关系边
	 * @param excelPath
	 * @return
	 */
	public static List<Relation> getRelation(String excelPath){
		List<Relation> relationList = new ArrayList<Relation>();
		try {
			Workbook wb = Workbook.getWorkbook(new File(excelPath));
			Sheet st = wb.getSheet(0);
			int rows = st.getRows();
			for (int i = 0; i < rows; i++) {
				String parent = st.getCell(0, i).getContents();
				String child = st.getCell(1, i).getContents();
				Relation relation = new Relation(parent, child);
				relationList.add(relation);
			}
		} catch (BiffException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return relationList;
	}
	
	/**
	 * 得到英文维基百科词及其中文维基百科词的对应关系
	 * @param topicList
	 * @return
	 */
	public static Map<String, String> getTopicCn(List<String> topicList){
		Map<String, String> topicResultMap = new HashMap<String, String>();
		for (int i = 0; i < topicList.size(); i++) {
			String topic = topicList.get(i);
			String urlWiki = "https://en.wikipedia.org/wiki/" + topic;
			// 解析是否存在中文维基页面
			try {
				Document doc = JsoupDao.parseURLText(urlWiki);
				Elements ch = doc.select("#p-lang").select("li").select("li[class$=interwiki-zh]");
				if (ch.size() != 0) {
					String title = ch.select("a").attr("title").replace(" – Chinese", "");
					title = Config.converter.convert(title);
					String url = ch.select("a").attr("href");
					Log.log(title + "--->" + url);
					topicResultMap.put(topic, title);
				} else {
					Log.log(topic + " doesn't have chinese wiki...");
				}
			} catch (Exception e) {
				Log.log(topic + " connect error...");
			}
		}
		return topicResultMap;
	}
	
	/**
	 * 保存关系到excel表格中
	 * @param topicList
	 * @param excelPath
	 */
	public static void storeTopic(Map<String, String> topicResultMap, String excelPath){
		try {
			WritableWorkbook wb = Workbook.createWorkbook(new File(excelPath));
			WritableSheet st = wb.createSheet("topic", 0);
			int i = 0;
			for(String key : topicResultMap.keySet()){
				String topicChinese = topicResultMap.get(key);
				st.addCell(new Label(0, i, key));
				st.addCell(new Label(1, i, topicChinese));
				i++;
			}
			wb.write();
			wb.close();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (RowsExceededException e) {
			e.printStackTrace();
		} catch (WriteException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 上下位关系中文处理
	 * @param relationList
	 * @param topicResultMap
	 * @param excelPath
	 */
	public static void storeTopicRelationCn(List<Relation> relationList, Map<String, String> topicResultMap, String excelPath){
		List<Relation> relationListNew = new ArrayList<Relation>();
		for (int i = 0; i < relationList.size(); i++) {
			Relation relation = relationList.get(i);
			String parent = relation.getParent();
			String child = relation.getChild();
			if (topicResultMap.keySet().contains(parent) && topicResultMap.keySet().contains(child)) {
				String parentCn = topicResultMap.get(parent);
				String childCn = topicResultMap.get(child);
				relationListNew.add(new Relation(parentCn, childCn));
			}
		}
		Log.log(relationListNew.size());
		
		try {
			WritableWorkbook wb = Workbook.createWorkbook(new File(excelPath));
			WritableSheet st = wb.createSheet("relation", 0);
			for (int i = 0; i < relationListNew.size(); i++) {
				Relation relation = relationListNew.get(i);
				st.addCell(new Label(0, i, relation.getParent()));
				st.addCell(new Label(1, i, relation.getChild()));
			}
			wb.write();
			wb.close();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (RowsExceededException e) {
			e.printStackTrace();
		} catch (WriteException e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * 以上程序都是在处理excel表格获取最新的数据
	 * 以下程序开始读取file文件夹下的两个xls表格，将主题及主题间的上下位关系存储到数据库中
	 */
	
	/**
	 * 存储所有主题，读取excel中的数据
	 */
	public static void storeTopicFromExcel(){
		Set<Term> termList = new HashSet<Term>();
		try {
			/**
			 * 读取excel表格，获取所有主题及其链接
			 */
			Workbook wb = Workbook.getWorkbook(new File("file\\topic_cn.xls"));
			Sheet st = wb.getSheet(0);
			int rows = st.getRows();
			for (int i = 0; i < rows; i++) {
				String topic = st.getCell(1, i).getContents();
				String url = "https://zh.wikipedia.org/wiki/" + URLEncoder.encode(topic, "UTF-8");
				Term term = new Term(topic, url);
				termList.add(term);
			}
			/**
			 * 将所有主题信息存储到数据库中
			 */
			String domain = "数据结构";
			MysqlReadWriteDAO.storeTopicShangXiaWei(termList, domain);
		} catch (BiffException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	public static void storeRelationFromExcel(){
		Set<Relation> relationSet = new HashSet<Relation>();
		try {
			/**
			 * 读取excel表格，获取所有主题及其链接
			 */
			Workbook wb = Workbook.getWorkbook(new File("file\\topic_relation_cn.xls"));
			Sheet st = wb.getSheet(0);
			int rows = st.getRows();
			for (int i = 0; i < rows; i++) {
				String parent = st.getCell(0, i).getContents();
				String child = st.getCell(1, i).getContents();
				Relation relation = new Relation(parent, child);
				relationSet.add(relation);
			}
			/**
			 * 将所有主题信息存储到数据库中
			 */
			String domain = "数据结构";
			MysqlReadWriteDAO.storeTopicRelation(relationSet, domain);
		} catch (BiffException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	

}
