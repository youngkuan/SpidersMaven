package com.company.zhihu;

import java.util.ArrayList;
import java.util.List;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.company.utils.JsoupDao;
import com.company.utils.Log;
import com.company.utils.mysqlUtils;
import com.company.zhihu.bean.Fragment;
import com.company.zhihu.bean.Question;
import com.company.zhihu.bean.Topic;

/**  
 * 类说明   
 *  
 * @author 郑元浩 
 * @date 2016年12月23日
 */
public class Crawler {
	
	public static void main(String[] args) throws Exception{
		test1();
	}
	
	public static void test1() throws Exception{
		List<Topic> topicList = ExtractZhihuMysqlDAO.getTopic();
		/**
		 * 深度优先
		 */
		for (int i = 0; i < topicList.size(); i++) {
			Topic topic = topicList.get(i);
			int id = topic.getId();
			String name = topic.getName();
			String subject = topic.getSubject();
			List<Question> questionList = getQuestionByTopic(id, name, subject);
			List<Fragment> fragmentList = getInfoByQuestion(questionList);
			storeInfo(fragmentList);
		}
		
	}
	
	/**
	 * 爬取主题页面
	 * @throws Exception 
	 */
	public static List<Question> getQuestionByTopic(int id, String keyword, String subject) throws Exception{
		List<Question> questionList = new ArrayList<Question>();
		String source = "知乎";
		Log.log("--------" + keyword + "--------");
		String keyword2 = new String(java.net.URLEncoder.encode(keyword,"utf-8").getBytes());
		String url = "https://www.zhihu.com/search?type=content&q=" + keyword2;
		Document doc = JsoupDao.parseURLText(url);
		Elements links = doc.select("div.title").select("a[href]");
		String urls[] = new String[links.size()];
		for (int i = 0; i < links.size(); i++) {
			Element link = links.get(i);
			urls[i] = "https://www.zhihu.com" + link.attr("href");
			Log.log(urls[i]);
			String tagID = keyword + "_" + i;
			Question question = new Question(tagID, urls[i], id, subject, source);
			questionList.add(question);
		}
		return questionList;
	}
	
	/**
	 * 爬取问题页面和作者页面，获得信息
	 */
	public static List<Fragment> getInfoByQuestion(List<Question> questionList) throws Exception {
		List<Fragment> fragmentList = new ArrayList<Fragment>();
		for (int i = 0; i < questionList.size(); i++) {
			Question question = questionList.get(i);
			String url = question.getUrl();
			// 解析问题网页
			String html = CrawlerZhihuDao.selenium2(url);
			Document doc = JsoupDao.parseHtmlText(html);
			// 问题网页信息
			String tagID = question.getTagID();
			String content = FeatureDao.getQuestionTitle(doc) + FeatureDao.getQuestionDetail(doc) + FeatureDao.getAnswersContent(doc);
			int interest = Integer.parseInt(FeatureDao.getQuestionInterest(doc));
			int answerNum = Integer.parseInt(FeatureDao.getQuestionAnswers(doc));
			int good = FeatureDao.getAnswersUpvote(doc);
			int comment = FeatureDao.getAnswersComment(doc);
			int view = Integer.parseInt(FeatureDao.getQuestionView(doc));
			// 循环作者信息
			int submit = 0;
			int idol = 0;
			int fan = 0;
			List<String> authors = FeatureDao.getAnswersAuthor(doc);
			Log.log(authors.size());
			if (authors.size() != 0) {
				for (int j = 0; j < authors.size(); j++) {
					String authorUrl = authors.get(j);
					Log.log(authorUrl);
					try {
//						Document authorDoc = JsoupDao.parseURLText(authorUrl);
						Document authorDoc = JsoupDao.parseHtmlText(CrawlerZhihuDao.webmagicCrawler(authorUrl));
						submit += FeatureDao.getAuthorSubmit(authorDoc);
						idol += FeatureDao.getAuthorIdol(authorDoc);
						fan += FeatureDao.getAuthorFan(authorDoc);
					} catch (Exception e) {
						Log.log("----------webmagic error-----------");
					}

				}
			}
			// 基本信息
			int topicID = question.getTopicID();
			String subject = question.getSubject();
			String source = question.getSource();
			// 添加碎片信息
			Fragment fragment = new Fragment(tagID, content, interest, answerNum, good, comment, view, submit, fan, idol, topicID, subject, source);
			fragmentList.add(fragment);
		}
		return fragmentList;
	}
	
	/**
	 * 存储信息
	 * @param fragmentList
	 */
	public static void storeInfo(List<Fragment> fragmentList){
		mysqlUtils mysql = new mysqlUtils();
		try {
			for(int i = 0; i < fragmentList.size(); i++){
				Fragment fragment = fragmentList.get(i);
				String sql = "replace into fragment_yuanhao (tagID, content, interest, answer, good, comment, view, submit, fans, idol, "
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
	
}
