package com.company.quora;

/**
 * 解析本地html数据，存储到excel中
 * @author 郑元浩
 * @date 2016年11月25号
 */

import java.util.HashMap;

import com.company.app.Config;

public class ExtractQuoraExcel {

	public static void main(String[] args) throws Exception {
		topicExtraction();
	}
	
	/**
	 *  按主题爬取的数据
	 * @throws Exception
	 */
	public static void topicExtraction() throws Exception{
		HashMap<String, Integer> map = ExtractQuoraExcelDAO.topicLength(Config.QUORA_PATH);
		String txt = "F:\\02-CQA网站中问题答案质量评估\\00-NewDataSets-主题及其问题数目统计.txt";
		ExtractQuoraExcelDAO.storeTopicLength(map, txt);
		for(String key : map.keySet()){
			String name = key;
			int questionNum = map.get(key);
			String question = Config.QUORA_PATH + "\\" + name + "\\question";
			String excel = question + "\\excel";
			ExtractQuoraExcelDAO.extractToExcel(name, questionNum, question, excel);
		}
	}
	
}
