package com.company.qa.quora;

/**
 * 
 * @author 郑元浩
 * @date 2016年11月25日14:20:20
 * @description
 * 1.解析本地的问题网页和作者网页
 * 2.实现了对问题页面问题信息的抽取
 * 3.实现了对问题页面答案信息的抽取
 * 4.实现了对作者页面作者信息的抽取
 * 备注：大部分特征信息登录Quora网站才可以拿到信息，适用于Selenium模拟浏览器方式的爬虫
 * 作者页面的解析程序也适用于Jsoup直接解析的页面
 */

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.company.utils.Log;

public class FeatureDao {

	/**
	 * 文件最后一次修改的时间
	 */
	public static String getCrawlerTime(String filePath) throws Exception {
		// 得到文件的上次修改时间
		File file = new File(filePath);
		long time = file.lastModified();
		SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss"); // 设置日期格式 
		String crawlerTime = df.format(new Date(time)); // new Date()为获取当前系统时间
		Log.log("CrawlerTime is : " + crawlerTime);
		return crawlerTime;
	}
	
	/**
	 * 实现功能：解析答案数目，返回答案数目Int
	 * 解析对象：问题网页
	 * 解析标签形式：2 Answers
	 * @param doc
	 */
	public static int countAnswerNumber(Document doc) {
		Elements answer_count = doc.select("div.answer_count");
		String answer_count_s = answer_count.get(0).text();
		String[] a = answer_count_s.split(" ");
		int answerNumber = Integer.parseInt(a[0]);
		Log.log("answer count is : " + answerNumber);
		return answerNumber;
	}

	/**
	 * 实现功能：得到问题网页实际答案数目，返回答案数目Int（问题数量很多时需要拖拽才可以获取到所有答案）
	 * 解析对象：问题网页
	 * 解析标签形式：div.pagedlist_item
	 * @param doc
	 */
	public static int countRealAnswerNumber(Document doc) {
		int number = 0;
		Elements real_answer_count = doc.select("div.pagedlist_item").select("span.feed_item_answer_user");
		Elements a = doc.select("a[class$=_item_overlay]"); // 下面多余卡片
		if(a.size() != 0){
			number = real_answer_count.size() - 3;
		} else {
			number = real_answer_count.size();
		}
		Log.log("real answer count is : " + number);
		return number;
	}
	
	/**
	 * 实现功能：解析问题内容，返回问题内容 string
	 * 解析对象：问题网页
	 * 解析标签形式：
	 * @param doc
	 */
	public static String questionContent(Document doc) {
		Elements as = doc.select("div.grid_page");
		if(as.size() != 0){
			Element a = as.get(0);
			Elements name = a.select("div.header").select("div.question_text_edit").select("h1");
			if (name.size() == 0) {
				return "";
			} else {
				String name_s = name.get(0).text();
				Log.log("question content：" + name_s);
				return name_s;
			}
		} else {
			return "";
		}
	}
	
	/**
	 * 实现功能：解析问题附加内容信息，返回问题附加信息 string
	 * 解析对象：问题网页
	 * 解析标签形式：
	 * @param doc
	 */
	public static String questionExpandInfo(Document doc) {
		Elements as = doc.select("div.grid_page");
		if(as.size() != 0){
			Element a = as.get(0);
			Elements expandInfo = a.select("div.header").select("div.question_details").select("div.expanded_q_text");
			if (expandInfo.size() == 0) {
				return "";
			} else {
				String expandInfo_s = expandInfo.get(0).text();
				Log.log("question expand content：" + expandInfo_s);
				return expandInfo_s;
			}
		} else {
			return "";
		}
	}

	/**
	 * 实现功能：解析问题关注人数，返回关注人数 string
	 * 解析对象：问题网页(最新爬取的网站)
	 * 解析标签形式：
	 * @param doc
	 */
	public static String questionFollow(Document doc) {
		Elements want_answers = doc.select("div.header")
				.select("div.action_item").select("span").select("a[class]");
		if(want_answers.size() != 0){
			Element a = want_answers.get(0);
			Elements b = a.select("span[class]");
			if (b.size() == 0) {
				Log.log("follow is 0...");
				return "0";
			} else {
				String want_answer = b.text();
				if(want_answer.contains("k")){
					String want = want_answer.substring(0, want_answer.indexOf("k"));
					Double num = Double.parseDouble(want)*1000;
					String astr = num.toString();
					want_answer = astr.substring(0, astr.indexOf("."));
				}
				Log.log("follow is : " + want_answer);
				return want_answer;
			}
		} else {
			Log.log("follow is not exist...");
			return "0";
		}
		
	}

	/**
	 * 实现功能：解析问题是否评论，返回评论数
	 * 解析对象：问题网页
	 * 解析标签形式：
	 * @param doc
	 */
	public static String questionComment(Document doc) {
		Elements questions = doc.select("div.header").select("div.action_item");
		if(questions.size() !=0 ){
			Element question = questions.get(1);
			Elements comment = question.select("div[id]").select("a[class]");
			if (comment.size() == 0) {
				Log.log("comment is not exist...");
				return "0";
			} else {
				Elements b = comment.select("span[class]");
				if (b.size() == 0) {
					Log.log("comment is 0...");
					return "0";
				} else {
					String comment_number = b.text();
					Log.log("comment is : " + comment_number);
					return comment_number;
				}
			}
		} else {
			Log.log("comment is not exist...");
			return "";
		}
		
	}

//	/**
//	 * 实现功能：解析与问题相关的话题，返回相关话题 string（需要改进）
//	 * 解析对象：问题网页
//	 * 解析标签形式：
//	 * @param doc
//	 */
//	public static String questionTopics(Document doc) {
//		Element topics = doc.select("div.QuestionArea").get(0);
//		Elements getTopics = topics.select("span.name_text");
//		String related_topics_s = "topic is : ";
//		for (int i = 0; i < getTopics.size(); i++) {
//			Element a = getTopics.get(i);
//			related_topics_s = related_topics_s + a.text() + "\n";
//		}
//		Log.log(related_topics_s);
//		return related_topics_s;
//	}
	
	/**
	 * 实现功能：解析问题的浏览数目Views
	 * 解析对象：问题网页（第n条回答）（最新爬取的网站）
	 * 解析标签形式：
	 * @param doc
	 */
	public static String questionViews(Document doc) {
		String view = "";
		Elements view_numbers = doc.select("div[class$=Stats]").select("div[class$=ViewsRow]");
		if(view_numbers.size() != 0){
			view = view_numbers.text();
			if(view.contains(" Views")){
				view = view.substring(0, view.indexOf(" Views"));
				view = view.replace(",", "");
			} else if(view.contains(" View")){
				view = view.substring(0, view.indexOf(" View"));
			}
			Log.log("view is : " + view);
		} else {
			view = "0";
		}
		return view;
	}
	
	/**
	 * 实现功能：解析问题的发布时间
	 * 解析对象：问题网页（最新爬取的网站）
	 * 解析标签形式：
	 * @param doc
	 */
	public static String questionPostTime(Document doc) {
		String postTime = "";
		Elements time = doc.select("div[class$=Stats]").select("div[class$=AskedRow]");
		if(time.size() != 0){
			postTime = time.get(0).text();
			if(postTime.contains("Last Asked ")){
				postTime = postTime.replace("Last Asked ", "");
			}
			if(!postTime.contains("2016")&&!postTime.contains("2015")&&!postTime.contains("2014")
					&&!postTime.contains("2013")&&!postTime.contains("2012")&&!postTime.contains("2011")
					&&!postTime.contains("2010")&&!postTime.contains("2009")&&!postTime.contains("2008")){
				postTime = postTime + ", 2016";
			}
		} else {
			postTime = "";
		}
		Log.log("postTime is : " + postTime);
		return postTime;
	}
	
	
	/**
	 * 处理从quora爬取过来的日期格式
	 * @param postTime
	 * @return
	 */
	public static String getQuestionPostTime(String postTime){
		java.text.SimpleDateFormat format = new java.text.SimpleDateFormat("yyyy-MM-dd");
		Calendar cal = Calendar.getInstance();// 取当前日期
		
		String[] months = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
		postTime = postTime.replace(",", "");
		String day = "11";
		String month = "11";
		String year = "2016";
		String date = year + "-" + month + "-" + day;
		if(postTime.split(" ").length == 3){ // 本周内的问题格式为 Tue 2016
			if(!postTime.split(" ")[1].equals("ago")){ // 本天内的问题格式为 9d ago 2016
				for (int i = 0; i < months.length; i++) {
					String mon = months[i];
					if(postTime.split(" ")[1].equals(mon)){ // 第二位为月份  20 Jul 2016
						month = i+1+"";
						day = postTime.split(" ")[0];
					} else if(postTime.split(" ")[0].equals(mon)){ // 第一位为月份   Aug 22 2015
						month = i+1+"";
						day = postTime.split(" ")[1];
					}
				}
				year = postTime.split(" ")[2]; // 第三位始终为年
			} else { // 本天内的爬取时间
				date = format.format(cal.getTime());
			}
		} else { // 本周内的爬取时间全部设置为本天时间
			date = format.format(cal.getTime());
		}
		date = year + "-" + month + "-" + day;
		Log.log("postTime is : " + date);
		
		return date;
	}
	

	/**
	 * 实现功能：解析答案内容，返回回答者的答案内容 string
	 * 解析对象：问题网页（第n条回答）
	 * 解析标签形式：
	 * @param doc,n
	 */
	public static String answerContent(Document doc, int n) {
		Element content_i = doc.select("div.pagedlist_item")
				.select("div[class^=Answer Answer]").get(n);
		String content = content_i.attr("id");
		Elements title = doc.select("div.header").select("div.question_text_edit");
		Elements content_j = doc.select("div.Answer").select("div#" + content + "_content");
		Elements content_k = content_j.select("span.inline_editor_value");
		Element per;
		if (content_k.size() == 0) {
			if(content_j.size() == 0){ // 如果答案中没有出现回答，就令答案为题目标题
				per = title.first();
			}else{
				per = content_j.first();
			}
		} else {
			per = content_k.first();
		}

		String answer_context_s = "";
		try{
			answer_context_s = per.text(); // 内容可能副本，取其中的第一个即可
			if(answer_context_s.contains("Written")){
				int index = answer_context_s.indexOf("Written");
				answer_context_s = answer_context_s.substring(0, index);
			} else {
				
			}
		}catch(Exception e){
			e.printStackTrace();
			Log.log("answer contents extract error...");
		}
		Log.log("answer " + (n+1) + " is: " + answer_context_s);
		return answer_context_s;
	}

	/**
	 * 实现功能：解析支持票数，返回回答者答案的支持票数Int
	 * 解析对象：问题网页（第n条回答）
	 * 解析标签形式：
	 * @param doc,n
	 */
	public static String answerUpvotes(Document doc, int n) {
		Elements answer_voters = doc.select("div.pagedlist_item").select("div[class^=action_bar]");
		if(answer_voters.size() != 0){
			Elements a = answer_voters.select("a[action_click=AnswerUpvote]").select("span.count");
			String answer_voters_s = a.get(n).text();
			Log.log("upvote is : " + answer_voters_s);
			return answer_voters_s;
		} else {
			return "";
		}
		
	}

	/**
	 * 实现功能：解析答案是否评论，返回Boolen变量0或1
	 * 解析对象：问题网页（第n条回答）
	 * 解析标签形式：
	 * @param doc,n
	 */
	public static String answerComment(Document doc, int n) {
		Elements comment_numbers = doc.select("div.pagedlist_item")
				.select("div[class^=action_bar]").select("div[class^=action_it]");
		if(comment_numbers.size()!=0){
			Element per_answer = comment_numbers.get(2*n);
			Elements comment = per_answer.select("span[id]").select("a[class^=view_comments]");
			if (comment.size() == 0) {
				Log.log("answer " + (n+1) + " doesn't have comment...");
				return "0";
			} else {
				Elements b = comment.select("span[class]");
				if (b.size() == 0) {
					Log.log("answer " + (n+1) + " has comment 0...");
					return "0";
				} else {
					String comment_number = b.text();
					Log.log("answer " + (n+1) + " has comment: " + comment_number);
					return comment_number;
				}
			}
		}else{
			return "";
		}
	}
	
	/**
	 * 实现功能：解析答案的浏览数目，返回浏览数目
	 * 解析对象：问题网页（第n条回答）
	 * 解析标签形式：
	 * @param doc,n
	 */
	public static String answerViews(Document doc, int n) {
		Elements view_numbers = doc.select("div.pagedlist_item")
				.select("div[class^=Credibility]");
		if(view_numbers.size()!=0){
			Element per_answer = view_numbers.get(2*n);
			Elements comment = per_answer.select("span[class^=meta_num]");
			if (comment.size() != 0) {
				String view_number = comment.get(0).text();
				Log.log("answer" + (n+1) + " has view : " + view_number);
				return view_number;
			} else {
				Log.log("answer" + (n+1)+ " has view 0...");
				return "0";				
			}
		} else {
			return "";
		}
	}
	
	/**
	 * 实现功能：解析内容得到链接，返回链接地址String
	 * 解析对象：问题网页（第n条回答）
	 * 解析标签形式：
	 * @param doc,n
	 */
	public static String answerURLs(Document doc, int n) {
		Element content_i = doc.select("div.pagedlist_item")
				.select("div[class^=Answer Answer]").get(n);
		String content = content_i.attr("id");
		Elements title = doc.select("div.header").select("div.question_text_edit");
		Elements content_j = doc.select("div.Answer").select("div#" + content + "_content");
		Elements content_k = content_j.select("span.inline_editor_value");
		Element per;
		if (content_k.size() == 0) {
			if(content_j.size() == 0){ // 如果答案中没有出现回答，就令答案为题目标题
				per = title.first();
			}else{
				per = content_j.first();
			}
		} else {
			per = content_k.first();
		}
		
		Elements a = per.select("a.external_link"); // 得到内容里面包含的链接
		if (a.size() == 0) {
			Log.log("answer" + (n+1) + " doesn't contains url...");
			return "0";
		} else {
			String urls = ""; // 用于保存链接
			for (int i = 0; i < a.size(); i++) {
				Element b = a.get(i);
				urls = urls + "\n" + b.attr("href");
			}
			Log.log("answer" + (n+1) + " contains url : "+urls);
			String exist = "1";
			return exist;
		}
	}

	/**
	 * 实现功能：解析作者的个人信息，返回作者的个人信息 string
	 * 解析对象：问题网页（第n条回答的作者个人信息）
	 * 解析标签形式：
	 * @param doc,n
	 */
	public static String authorName(Document doc, int n) {
		Elements author_name = doc.select("span.feed_item_answer_user");
		Element a = author_name.get(n); // 单个回答
		// 用户存在Quora User匿名的情况
		Elements c = a.select("a.user");
		String author_name_s = null;
		if (c.size() == 0) {
			author_name_s = "Quora User(匿名用户)";
		} else {
			author_name_s = c.text();
		}
		Log.log("author " + (n + 1) + " name is : " + author_name_s);
		return author_name_s;
	}

	/**
	 * 实现功能：解析作者回答问题提供的答案数目，返回作者领域 string
	 * 解析对象：作者网页（第n条回答的作者页面）（n可去）
	 * 解析标签形式：
	 * @param doc,n
	 */
	public static String authorAnswers(Document doc, int n) {
		Elements author_Answers = doc.select("div.primary").select("span[class]");
		if(author_Answers.size() == 0){
			Log.log("author " + (n + 1) + " doesn't has answers...");
			return null;
		}else{
			String author_Answers_s = author_Answers.get(0).text();
			Log.log("author " + (n + 1) + " answers is : " + author_Answers_s);
			return author_Answers_s;
		}
	}
	
	/**
	 * 实现功能：解析作者回答问题提供的问题数目，返回作者领域 string
	 * 解析对象：作者网页（第n条回答的作者页面）（n可去）
	 * 解析标签形式：
	 * @param doc,n
	 */
	public static String authorQuestions(Document doc, int n) {
		Elements authorQuestion = doc.select("div.primary").select("a").select("span");
		if(authorQuestion.size() == 0){
			Log.log("author " + (n + 1) + " doesn't has questions...");
			return null;
		}else{
			if(authorQuestion.size() != 1){
				String authorQuestionCount = authorQuestion.get(1).text();
				Log.log("author " + (n + 1) + " questions is : " + authorQuestionCount);
				return authorQuestionCount;
			} else {
				Log.log("author " + (n + 1) + " questions can't extract...");
				return "";
			}
			
		}
	}
	
	/**
	 * 实现功能：解析作者回答问题提供的问题数目，返回作者领域 string
	 * 解析对象：作者网页（第n条回答的作者页面）（n可去）
	 * 解析标签形式：
	 * @param doc,n
	 */
	public static String authorPosts(Document doc, int n) {
		Elements authorPost = doc.select("div.primary").select("a").select("span[class]");
		Log.log(authorPost.size());
		if(authorPost.size() == 0){
			Log.log("author " + (n + 1) + " doesn't has posts...");
			return null;
		}else{
			if(authorPost.size() != 2){
				String authorPostCount = authorPost.get(2).text();
				Log.log("author " + (n + 1) + " answers is : " + authorPostCount);
				return authorPostCount;
			} else {
				Log.log("author " + (n + 1) + " posts can't extract...");
				return "";
			}
			
		}
	}

	/**
	 * 实现功能：解析作者的粉丝数目，返回作者的粉丝数 string
	 * 解析对象：作者网页（第n条回答的作者页面）（n可去）
	 * 解析标签形式：
	 * @param doc,n
	 */
	public static String authorFollowers(Document doc, int n) {
		Elements author_followers = doc.select("div.secondary").select("span[class]");
		if(author_followers.size() == 0){
			Log.log("author " + (n + 1) + " doesn't has followers...");
			return null;
		}else{
			String author_followers_s = author_followers.get(0).text();
			Log.log("author " + (n + 1) + " followers is : " + author_followers_s);
			return author_followers_s;
		}
	}

	/**
	 * 实现功能：解析作者偶像数目，返回作者偶像数目 string
	 * 解析对象：作者网页（第n条回答的作者页面）（n可去）
	 * 解析标签形式：
	 * @param doc,n
	 */
	public static String authorFollowing(Document doc, int n) {
		Elements author_following = doc.select("div.secondary").select("span[class]");
		if(author_following.size() == 0){
			Log.log("author " + (n + 1) + " doesn't has following...");
			return null;
		}else{
			if(author_following.size() != 1){
				String author_following_s = author_following.get(1).text();
				Log.log("author " + (n + 1) + " following is : " + author_following_s);
				return author_following_s;
			} else {
				Log.log("author " + (n + 1) + " following can't extract...");
				return "";
			}
		}
	}
	
	/**
	 * 实现功能：解析作者偶像数目，返回作者偶像数目 string
	 * 解析对象：作者网页（第n条回答的作者页面）（n可去）
	 * 解析标签形式：
	 * @param doc,n
	 */
	public static String authorEdits(Document doc, int n) {
		Elements author_edit = doc.select("div.secondary").select("span[class]");
		Log.log(author_edit.size());
		if(author_edit.size() == 0){
			Log.log("author " + (n + 1) + " doesn't has edits...");
			return null;
		}else{
			if(author_edit.size() != 2){
				String author_edit_s = author_edit.get(2).text();
				Log.log("author " + (n + 1) + " edits is : " + author_edit_s);
				return author_edit_s;
			} else {
				Log.log("author " + (n + 1) + " edits can't extract...");
				return "";
			}
		}
	}

	/**
	 * 实现功能：解析作者擅长回答的主要问题领域，返回作者领域 string
	 * 解析对象：作者网页（第n条回答的作者页面）（n可去）
	 * 解析标签形式：
	 * @param doc,n
	 */
	public static String authorKnowAbout(Document doc, int n) {
		Elements author_Answers = doc.select("div.layout_3col_right");
		if(author_Answers.size() == 0){
			Log.log("author " + (n + 1) + " doesn't has knowAbout...");
			return null;
		}else{
			Elements a = author_Answers.select("div[class$=ProfileExperienceList]");
			if (a.size() == 0) {
				Log.log("author " + (n + 1) + " doesn't has knowAbout...");
				return "";
			} else {
				Elements TopicName = a.select("li[class]").select("span.name_text");
				Elements answer_count = a.select("li[class]").select("span.answers_link");
				Elements answer_endorsements = a.select("li[class]")
						.select("div.UserTopicInfoLink").select("span[id]");
				String author_Answers_s = "";
				for (int i = 0; i < answer_count.size(); i++) {
					author_Answers_s = author_Answers_s 
							+ TopicName.get(i).text() + "："
							+ answer_count.get(i).text() + " + "
							+ answer_endorsements.get(i).text() + "\n";
				}
				Log.log("author " + (n + 1) + " knowAbout is : " + author_Answers_s);
				return author_Answers_s;
			}
		}
	}
	
	
	

}