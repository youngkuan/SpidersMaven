package com.company.quora;

import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import jxl.Sheet;
import jxl.Workbook;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.company.app.Config;
import com.company.quora.bean.Topic;
import com.company.quora.bean.TopicQuestion;
import com.company.utils.JsoupDao;
import com.company.utils.Log;
import com.company.utils.Time;
import com.company.utils.mysqlUtils;

/**
 * 1. 建立数据库表格，只需要一次
 * 
 * @author 郑元浩
 *
 */

public class ExtractQuoraMysqlDAO {

	public static void main(String[] args) {
//		createTables();
//		topic();
		topicQuestion();
	}

	/**
	 * 创建Quora数据库表格
	 */
	public static boolean createTables(){
		boolean success = true;
		/**
		 * 创建文本表格SQL语句
		 */
		String sqlTopic = "create table topic(" + 
			  "topic_name varchar(255) DEFAULT NULL," +
			  "topic_id int(20) DEFAULT NULL" +
			  ")" + "ENGINE=InnoDB DEFAULT CHARSET=utf8;";
		String sqlTopicQuestion = "create table topicquestion(" + 
			  "question_id varchar(255) NOT NULL," +
			  "question_url varchar(255) DEFAULT NULL," +
			  "topic_id int(20) DEFAULT NULL," +
			  "topic_name varchar(255) DEFAULT NULL," +
			  "scratch_time datetime DEFAULT NULL," +
			  "PRIMARY KEY (" + "question_id)" +
			  ")" + "ENGINE=InnoDB DEFAULT CHARSET=utf8;";
		String sqlQuestion = "create table question(" + 
			  "question_id varchar(255) NOT NULL," +
			  "question_url varchar(255) DEFAULT NULL," +
			  "question_content varchar(255) DEFAULT NULL," +
			  "question_contentMore text," +
			  "question_follow varchar(255) DEFAULT NULL," +
			  "question_comment varchar(255) DEFAULT NULL," +
			  "question_view varchar(255) DEFAULT NULL," +
			  "question_answercount varchar(255) DEFAULT NULL," +
			  "question_postTime datetime DEFAULT NULL," +
			  "question_scratchTime datetime DEFAULT NULL," +
			  "topic_id int(20) DEFAULT NULL," +
			  "PRIMARY KEY (" + "question_id)" +
			  ")" + "ENGINE=InnoDB DEFAULT CHARSET=utf8;";
		String sqlAnswer = "create table answer(" + 
			  "answer_id varchar(255) NOT NULL," +
			  "answer_rank int(20) DEFAULT NULL," +
			  "answer_content text DEFAULT NULL," +
			  "answer_upvote varchar(255) DEFAULT NULL," +
			  "answer_comment varchar(255) DEFAULT NULL," +
			  "answer_view varchar(255) DEFAULT NULL," +
			  "answer_authorurl varchar(255) DEFAULT NULL," +
			  "answer_postTime datetime DEFAULT NULL," +
			  "answer_scratchTime datetime DEFAULT NULL," +
			  "question_id varchar(255) DEFAULT NULL," +
			  "PRIMARY KEY (" + "answer_id)" +
			  ")" + "ENGINE=InnoDB DEFAULT CHARSET=utf8;";
		String sqlAuthor = "create table author(" + 
			  "id int(20) NOT NULL AUTO_INCREMENT," +
			  "author_id varchar(255) DEFAULT NULL," +
			  "author_url varchar(255) DEFAULT NULL," +
			  "author_answers varchar(255) DEFAULT NULL," +
			  "author_questions varchar(255) DEFAULT NULL," +
			  "author_posts varchar(255) DEFAULT NULL," +
			  "author_followers varchar(255) DEFAULT NULL," +
			  "author_following varchar(255) DEFAULT NULL," +
			  "author_edits varchar(255) DEFAULT NULL," +
			  "author_scratchTime datetime DEFAULT NULL," +
			  "answer_id varchar(255) DEFAULT NULL," +
			  "PRIMARY KEY (" + "id)" +
			  ")" + "ENGINE=InnoDB AUTO_INCREMENT=0 DEFAULT CHARSET=utf8;";
		
		/**
		 * 读取表格语句
		 */
		String checkTableTopic = "show tables like \"topic\""; 
		String checkTableTopicQuestion = "show tables like \"topicquestion\""; 
		String checkTableQuestion = "show tables like \"question\""; 
		String checkTableAnswer = "show tables like \"answer\""; 
		String checkTableAuthor = "show tables like \"author\""; 
		
		List<Object> params = new ArrayList<Object>();
		mysqlUtils my = new mysqlUtils();
		try {
			// 判断topic表是否存在，如果不存在就创建
			ResultSet topicResult = my.judge(checkTableTopic);
			if(topicResult.next()){
				Log.log("topic 表已经存在，不需要创建新的表格...");
			} else {
				my.addDeleteModify(sqlTopic, params);
			}
			// 判断topicquestion表是否存在，如果不存在就创建 
			ResultSet topicQuestionResult = my.judge(checkTableTopicQuestion);
			if(topicQuestionResult.next()){
				Log.log("topicquestion 表已经存在，不需要创建新的表格...");
			} else {
				my.addDeleteModify(sqlTopicQuestion, params);
			}
			// 判断question表是否存在，如果不存在就创建
			ResultSet questionResult = my.judge(checkTableQuestion);
			if(questionResult.next()){
				Log.log("question 表已经存在，不需要创建新的表格...");
			} else {
				my.addDeleteModify(sqlQuestion, params);
			}
			// 判断answer表是否存在，如果不存在就创建
			ResultSet answerResult = my.judge(checkTableAnswer);
			if(answerResult.next()){
				Log.log("answer 表已经存在，不需要创建新的表格...");
			} else {
				my.addDeleteModify(sqlAnswer, params);
			}
			// 判断author表是否存在，如果不存在就创建
			ResultSet authorResult = my.judge(checkTableAuthor);
			if(authorResult.next()){
				Log.log("author 表已经存在，不需要创建新的表格...");
			} else {
				my.addDeleteModify(sqlAuthor, params);
			}
		} catch (SQLException e) {
			success = false;
			e.printStackTrace();
		} finally {
			my.closeconnection();
		}
		return success;
	}
	
	/**
	 * 读取人工筛选出来需要爬取的主题表格
	 * @return 
	 * @throws Exception
	 */
	public static List<Topic> getTopic() throws Exception{
		List<Topic> topicList = new ArrayList<Topic>();
		Workbook wb = Workbook.getWorkbook(new File(Config.Expi_PATH + "\\Quora_Topic_Spider.xls"));
		Sheet sheet = wb.getSheet("topics-20161120");
		int rows = sheet.getRows();
		for(int i = 1; i < rows; i++){
			int topic_id = i;
			String topic_name = sheet.getCell(1, i).getContents();
			String topic_url = "https://www.quora.com/topic/" + topic_name + "/all_questions";
			int topic_question = Integer.parseInt(sheet.getCell(2, i).getContents());
			String topic_questionCountTime = Time.getCertainTime("2016-11-20 19:20:00");
			Topic topic = new Topic(topic_id, topic_name, topic_url, topic_question, topic_questionCountTime);
			topicList.add(topic);
		}
		return topicList;
	}
	
	/**
	 * 读取Excel文件获取所有主题信息
	 * @throws Exception 
	 */
	public static boolean topic() {
		boolean result = false;
		mysqlUtils mysql = new mysqlUtils();
		try {
			List<Topic> topicList = getTopic();
			for(int i = 0; i < topicList.size(); i++){
				Topic topic = topicList.get(i);
				String sql = "replace into topic (topic_id, topic_name, topic_url, topic_question, topic_questionCountTime)"
						+ " VALUES(?, ?, ?, ?, ?);";
				List<Object> params = new ArrayList<Object>();
				params.add(topic.getTopic_id());
				params.add(topic.getTopic_name());
				params.add(topic.getTopic_url());
				params.add(topic.getTopic_question());
				params.add(topic.getTopic_questionCountTime());
				try {
					result = mysql.addDeleteModify(sql, params);
					Log.log(result);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			mysql.closeconnection();
		}
		return result;
	}
	
	/**
	 * 解析本地主题（selenium）页面，获取问题链接
	 * @return 
	 * @throws Exception 
	 */
	public static List<TopicQuestion> getTopicQuestion() {
		List<TopicQuestion> topicQuestionsList = new ArrayList<TopicQuestion>();
		File[] files = new File(Config.QUORA_PATH).listFiles();
		for(int m = 0; m < files.length; m++){
			String keyword = files[m].getName();
			String path =  Config.QUORA_PATH + "\\" + keyword;
			String filePath = path + "\\" + keyword + "(selenium).html";
			File file = new File(filePath);
			if (!file.exists()) {
				Log.log(filePath + "  不存在，得不到它的子页面链接！！！");
			} else {
				Document doc = JsoupDao.parsePathText(filePath);
				Elements links = doc.select("a.question_link").select("a[href]");
				for (int i = 0; i < links.size(); i++) {
					Element link = links.get(i);
					String question_url = "http://www.quora.com" + link.attr("href");
					String question_id = keyword + (i+1);
					String topic_name = keyword;
					TopicQuestion topicQuestion = new TopicQuestion(question_id, question_url, topic_name);
					topicQuestionsList.add(topicQuestion);
				}
			}
		}
		return topicQuestionsList;
	}
	
	/**
	 * 读取本地HTML文件获取已经爬取主题和问题的对应信息
	 * @throws Exception 
	 */
	public static boolean topicQuestion() {
		boolean result = false;
		mysqlUtils mysql = new mysqlUtils();
		try {
			List<TopicQuestion> topicQuestionList = getTopicQuestion();
			for(int i = 0; i < topicQuestionList.size(); i++){
				TopicQuestion topicQuestion = topicQuestionList.get(i);
				String sql = "replace into topicquestion (question_id, question_url, topic_name)"
						+ " VALUES(?, ?, ?);";
				List<Object> params = new ArrayList<Object>();
				params.add(topicQuestion.getQuestion_id());
				params.add(topicQuestion.getQuestion_url());
				params.add(topicQuestion.getTopic_name());
				try {
					result = mysql.addDeleteModify(sql, params);
					Log.log(result);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			mysql.closeconnection();
		}
		return result;
	}
	
	

	
}
