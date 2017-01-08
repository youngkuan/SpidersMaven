package com.company.quora;

/**
 * 解析本地html数据，存储到excel中
 * @author 郑元浩
 * @date 2016年11月25号
 */

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

import org.apache.commons.io.FileUtils;
import org.jsoup.nodes.Document;

import com.company.utils.JsoupDao;
import com.company.utils.Log;

public class ExtractQuoraExcelDAO {

	public static void main(String[] args) throws Exception {

	}
	
	/**
	 *  得到每个主题的名字和问题数目
	 * @param path
	 * @return
	 */
	public static HashMap<String, Integer> topicLength(String path){
		File[] f = new File(path).listFiles();
		HashMap<String, Integer> map = new HashMap<String, Integer>();
		for(int i = 0; i < f.length; i++){
			String name = f[i].getName();
			String htmlPath = path + "\\" + name + "\\question";
			File[] html = new File(htmlPath).listFiles();
			int question = 0;
			for(int j = 0; j < html.length; j++){
				String fileName = html[j].getName();
				if(fileName.contains(".html")){
					question += 1;
				}
			}
			map.put(name, question);
		}
		return map;
	}

	/**
	 *  保存主题及其问题数目信息保存到本地文件
	 * @param map
	 * @param path
	 */
	public static void storeTopicLength(HashMap<String, Integer> map, String path){
		String info = "";
		for(String key : map.keySet()){
			String name = key;
			int questionNum = map.get(key);
			info = info + name + " ---> " + questionNum + "\n";
		}
		try {
			FileUtils.writeStringToFile(new File(path), info);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 *  解析爬取的问题页面：问题ID、问题内容、问题粉丝数、问题回答数、问题浏览数，问题发布时间
	 * @param keyword
	 * @param pageLength
	 * @param path
	 * @param pathExcel
	 * @throws Exception
	 */
	public static void extractToExcel(String keyword, int pageLength, String path, String pathExcel)
			throws Exception {
		// 建立保存目录
		new File(pathExcel).mkdir();
		String filepath = pathExcel + "\\" + keyword + "_question.xls";
		if(new File(filepath).exists()){
			new File(filepath).delete();
			Log.log(filepath + " already exist...");
		} else {
			WritableWorkbook workbook = Workbook.createWorkbook(new File(filepath));
			WritableSheet sheet = workbook.createSheet("问题列表", 0);
			sheet.addCell(new Label(0, 0, "id"));
			sheet.addCell(new Label(1, 0, "question"));
			sheet.addCell(new Label(2, 0, "follow"));
			sheet.addCell(new Label(3, 0, "answer"));
			sheet.addCell(new Label(4, 0, "view"));
			sheet.addCell(new Label(5, 0, "postTime"));
			sheet.setRowView(0, 700, false);

			// 存放信息
			int number = 1;
			for (int i = 0; i < pageLength; i++) {
				String filePath = path + "\\" + keyword + i + ".html";
				Log.log(filePath);
				File file = new File(filePath);
				if (file.exists()) {
					// 开始解析问题页面，将问题的有关信息填入表格之中
					Document doc = JsoupDao.parsePathText(filePath);
					ArrayList<String> titlelist = new ArrayList<String>();
					titlelist.add(keyword + i);
					titlelist.add(FeatureDao.questionContent(doc));
					titlelist.add(FeatureDao.questionFollow(doc));
					titlelist.add(FeatureDao.countRealAnswerNumber(doc) + "");
					titlelist.add(FeatureDao.questionViews(doc));
					titlelist.add(FeatureDao.questionPostTime(doc));
					for (int j = 0; j < 6; j++) {
						sheet.addCell(new Label(j, number, titlelist.get(j)));
					}
					sheet.setRowView(number, 700, false); // 设置行高
					sheet.setColumnView(0, 20);
					sheet.setColumnView(1, 20);
					sheet.setColumnView(2, 20);
					sheet.setColumnView(3, 20);
					sheet.setColumnView(4, 20);
					sheet.setColumnView(5, 20);

					number = number + 1;
				} else {
					Log.log(keyword + i + ".html is not exist...");
				}
			}
			workbook.write();
			workbook.close();
		}
	}
	

}
