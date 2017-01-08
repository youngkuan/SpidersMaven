package com.company.zhihu;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;

import com.company.utils.mysqlUtils;
import com.company.zhihu.bean.Fragment;
import com.company.zhihu.bean.LdaInput;

/**  
 * 类说明   
 *  
 * @author 郑元浩 
 * @date 2016年12月23日
 */
public class GenerateLdaData {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
//		storeText();
		
//		List<LdaInput> fragmentList = getContent();
//		storeTxt(fragmentList);
		
		String filePath= "D:\\Workspace\\eclipse\\model\\data\\lda\\model-final-mooc.theta";
		List<Double> doubleList = getTS(filePath);
		storeTxtTs(doubleList);
	}
	
	
	/**
	 * 得到表格所有碎片
	 * @return
	 */
	public static List<Fragment> getText(String table){
		List<Fragment> fragmentList = new ArrayList<Fragment>();
		mysqlUtils mysql = new mysqlUtils();
		String sql = "select * from " + table;
		List<Object> params = new ArrayList<Object>();
		try {
			List<Map<String, Object>> list = mysql.returnMultipleResult(sql, params);
			for (int i = 0; i < list.size(); i++) {
				Map<String, Object> map = list.get(i);
				String tagID = (String) map.get("tagID");
				String content = (String) map.get("content");
				int interest = Integer.parseInt(map.get("interest").toString());
				int answer = Integer.parseInt(map.get("answer").toString());
				int good = Integer.parseInt(map.get("good").toString());
				int comment = Integer.parseInt(map.get("comment").toString());
				int view = Integer.parseInt(map.get("view").toString());
				int submit = Integer.parseInt(map.get("submit").toString());
				int fans = Integer.parseInt(map.get("fans").toString());
				int idol = Integer.parseInt(map.get("idol").toString());
				int topicId = Integer.parseInt(map.get("topicId").toString());
				String subject = map.get("subject").toString();
				String source = map.get("source").toString();
				Fragment fragment = new Fragment(tagID, content, interest, answer, good, comment, view, submit, fans, idol, topicId, subject, source);
				fragmentList.add(fragment);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return fragmentList;
	}
	
	/**
	 * 存储所有碎片信息
	 * @param fragmentList
	 */
	public static void storeInfo(List<Fragment> fragmentList){
		mysqlUtils mysql = new mysqlUtils();
		try {
			for(int i = 0; i < fragmentList.size(); i++){
				Fragment fragment = fragmentList.get(i);
				String sql = "replace into fragment_all (tagID, content, interest, answer, good, comment, view, submit, fans, idol, "
						+ "topicId, subject, source) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";
				List<Object> params = new ArrayList<Object>();
				params.add(fragment.getTagID());
				params.add(fragment.getContent());
				params.add(fragment.getInterest());
				params.add(fragment.getAnswer());
				params.add(fragment.getGood());
				params.add(fragment.getComment());
				params.add(fragment.getView());
				params.add(fragment.getSubmit());
				params.add(fragment.getFans());
				params.add(fragment.getIdol());
				params.add(fragment.getTopicID());
				params.add(fragment.getSubject());
				params.add(fragment.getSource());
				try {
					if (!fragment.content.trim().equals("")) {
						mysql.addDeleteModify(sql, params);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			mysql.closeconnection();
		}
	}
	
	/**
	 * 存储所有信息
	 */
	public static void storeText(){
		List<Fragment> fragmentList = getText("fragment_yuanhao");
		List<Fragment> fragmentList2 = getText("fragment_shilei");
		fragmentList.addAll(fragmentList2);
		storeInfo(fragmentList);
	}
	
	/**
	 * 得到所有碎片内容
	 * @return
	 */
	public static List<LdaInput> getContent(){
		List<LdaInput> fragmentList = new ArrayList<LdaInput>();
		mysqlUtils mysql = new mysqlUtils();
		String sql = "select * from fragment_分词";
		List<Object> params = new ArrayList<Object>();
		try {
			List<Map<String, Object>> list = mysql.returnMultipleResult(sql, params);
			for (int i = 0; i < list.size(); i++) {
				Map<String, Object> map = list.get(i);
				String tagID = (String) map.get("tagID");
				String content = (String) map.get("fenci");
				LdaInput fragment = new LdaInput(tagID, content);
				fragmentList.add(fragment);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return fragmentList;
	}
	
	/**
	 * 将所有数据存到一个Txt中，第一行值为文档总数，后面每一行是一个文档
	 * @param fragmentList
	 */
	public static void storeTxt(List<LdaInput> fragmentList){
		String path = "D:\\Workspace\\eclipse\\model\\data\\lda";
		String name = "mooc.txt";
		String filePath = path + "\\" + name;
		int size = fragmentList.size();
		String result = size + "\n";
		for (int i = 0; i < fragmentList.size(); i++) {
			LdaInput fragment = fragmentList.get(i);
//			String id = fragment.getId();
			String content = fragment.getContent();
			result += content + "\n";
		}
		
		try {
			FileUtils.writeStringToFile(new File(filePath), result);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 计算每一行元素平均值
	 * @param filePath
	 * @return
	 */
	public static List<Double> getTS(String filePath){
		List<Double> doubleList = new ArrayList<Double>();
		try {
			List<String> lines = FileUtils.readLines(new File(filePath));
			for (int i = 0; i < lines.size(); i++) {
				String line = lines.get(i);
				String[] array = line.split(" ");
				double value = 0;
				for (int j = 0; j < array.length; j++) {
					double single = Double.parseDouble(array[j]);
//					value += single;
					if (single > value) {
						value = single;
					}
				}
//				value = value / array.length;
				doubleList.add(value);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return doubleList;
	}
	
	/**
	 * 将平均值存到txt中
	 * @param doubleList
	 */
	public static void storeTxtTs(List<Double> doubleList){
		String path = "D:\\Workspace\\eclipse\\model\\data\\lda";
		String name = "mooc_ts.txt";
		String filePath = path + "\\" + name;
		String result = "";
		for (int i = 0; i < doubleList.size(); i++) {
			double ts = doubleList.get(i);
			result += ts + "\n";
		}
		try {
			FileUtils.writeStringToFile(new File(filePath), result);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
