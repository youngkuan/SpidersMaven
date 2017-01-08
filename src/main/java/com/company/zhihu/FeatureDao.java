package com.company.zhihu;

/**
 * 
 * @author 郑元浩
 * @date 2016年12月22日21:20:20
 * @description
 */
import java.util.ArrayList;
import java.util.List;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.company.utils.Log;

public class FeatureDao {

	
	/**
	 * 问题标题
	 * @param doc
	 */
	public static String getQuestionTitle(Document doc) {
		String content = "";
		Elements title = doc.select("div#zh-question-title");
		if(title.size() != 0){
			content = title.get(0).text().trim();
		}
		Log.log("title : " + content);
		return content;
	}
	
	/**
	 * 问题详细内容
	 * @param doc
	 */
	public static String getQuestionDetail(Document doc) {
		String content = "";
		Elements detail = doc.select("div#zh-question-detail");
		if(detail.size() != 0){
			content = detail.get(0).text().trim();
		}
		Log.log("detail : " + content);
		return content;
	}
	
	/**
	 * 问题关注数
	 * @param doc
	 */
	public static String getQuestionInterest(Document doc) {
		String content = "0";
		Elements interest = doc.select("div#zh-question-side-header-wrap").select("div[class$=gray-normal]").select("strong");
		if(interest.size() != 0){
			content = interest.get(0).text().trim();
		}
		Log.log("interest : " + content);
		return content;
	}
	
	/**
	 * 问题回答数
	 * @param doc
	 */
	public static String getQuestionAnswers(Document doc) {
		String content = "0";
		Elements answers = doc.select("h3#zh-question-answer-num");
		if(answers.size() != 0){
			content = answers.get(0).attr("data-num").trim();
		}
		Log.log("answers : " + content);
		return content;
	}
	
	/**
	 * 问题浏览数
	 * @param doc
	 */
	public static String getQuestionView(Document doc) {
		String content = "0";
		Elements view = doc.select("div[class$=ion-inner]").select("div[class$=ay-normal]").select("strong");
		if(view.size() != 0){
			content = view.get(1).text().trim();
		}
		Log.log("views : " + content);
		return content;
	}
	
	/**
	 * 所有答案内容
	 * @param doc
	 */
	public static String getAnswersContent(Document doc){
		String content = "";
		Elements answers = doc.select("div#zh-question-answer-wrap").select("div[class^=zm-item-answer]");
		if(answers.size() != 0){
			for (int i = 0; i < answers.size(); i++) {
				Element answer = answers.get(i);
				String text = answer.select("div[class^=zm-item-rich-text]").text().trim();
				content = content + text;
			}
		}
		Log.log("answers.size() : " + answers.size());
		return content;
	}
	
	
	/**
	 * 赞同票总数
	 * @param doc
	 * @return
	 */
	public static int getAnswersUpvote(Document doc){
		int content = 0;
		Elements answers = doc.select("div#zh-question-answer-wrap").select("div[class^=zm-item-vote-info]");
		if(answers.size() != 0){
			for (int i = 0; i < answers.size(); i++) {
				Element answer = answers.get(i);
				String upvote = answer.attr("data-votecount");
				content = content + Integer.parseInt(upvote);
			}
		}
		Log.log("upvote : " + content);
		return content;
	}
	
	/**
	 * 评论总数
	 * @param doc
	 * @return
	 */
	public static int getAnswersComment(Document doc){
		int content = 0;
		Elements answers = doc.select("div#zh-question-answer-wrap").select("a[class^=meta-item toggle-comment]");
		if(answers.size() != 0){
			for (int i = 0; i < answers.size(); i++) {
				Element answer = answers.get(i);
				String comment = answer.text();
				if (comment.equals("添加评论")) {
					comment = "0"; 
				} else {
					comment = comment.replace(" 条评论", "");
				}
				content = content + Integer.parseInt(comment);
			}
		}
		Log.log("comment : " + content);
		return content;
	}
	
	/**
	 * 所有作者链接
	 * @param doc
	 * @return
	 */
	public static List<String> getAnswersAuthor(Document doc){
		List<String> authors = new ArrayList<String>();
		Elements answers = doc.select("div#zh-question-answer-wrap").select("a.author-link");
		if(answers.size() != 0){
			for (int i = 0; i < answers.size(); i++) {
				Element answer = answers.get(i);
				String authorUrl = "https://www.zhihu.com" + answer.attr("href");
				authors.add(authorUrl);
			}
		}
		return authors;
	}
	
	/**
	 * 作者偶像
	 * @param doc
	 * @return
	 */
	public static int getAuthorIdol(Document doc){
		int content = 0;
		Elements answers = doc.select("div[class$=FollowshipCard-counts]").select("div[class^=NumberBoard-val]");
		if(answers.size() != 0){
			try {
				String idol = answers.get(0).text().trim();
				content = Integer.parseInt(idol);
			} catch (Exception e) {
				Log.log("...");
			}
		}
		Log.log("idol : " + content);
		return content;
	}
	
	/**
	 * 作者粉丝
	 * @param doc
	 * @return
	 */
	public static int getAuthorFan(Document doc){
		int content = 0;
		Elements answers = doc.select("div[class$=FollowshipCard-counts]").select("div[class^=NumberBoard-val]");
		if(answers.size() != 0){
			try {
				String fan = answers.get(1).text().trim();
				content = Integer.parseInt(fan);
			} catch (Exception e) {
				Log.log("...");
			}
		}
		Log.log("fan : " + content);
		return content;
	}
	
	/**
	 * 作者关注问题
	 * @param doc
	 * @return
	 */
	public static int getAuthorSubmit(Document doc){
		int content = 0;
		Elements answers = doc.select("div[class$=file-lightList]").select("span[class$=file-lightItemValue]");
		if(answers.size() != 0){
			try {
				String submit = answers.get(2).text().trim();
				content = Integer.parseInt(submit);
			} catch (Exception e) {
				Log.log("...");
			}
		}
		Log.log("submit : " + content);
		return content;
	}
	

}