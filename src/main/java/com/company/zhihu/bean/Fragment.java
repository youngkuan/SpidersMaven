package com.company.zhihu.bean;
/**  
 * 类说明   
 *  
 * @author 郑元浩 
 * @date 2016年12月23日
 */
public class Fragment {
	
	public String tagID;
	public String content;
	public int interest;
	public int answer;
	public int good;
	public int comment;
	public int view;
	public int submit;
	public int fans;
	public int idol;
	public int topicID;
	public String subject;
	public String source;
	public String getTagID() {
		return tagID;
	}
	public void setTagID(String tagID) {
		this.tagID = tagID;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public int getInterest() {
		return interest;
	}
	public void setInterest(int interest) {
		this.interest = interest;
	}
	public int getAnswer() {
		return answer;
	}
	public void setAnswer(int answer) {
		this.answer = answer;
	}
	public int getGood() {
		return good;
	}
	public void setGood(int good) {
		this.good = good;
	}
	public int getComment() {
		return comment;
	}
	public void setComment(int comment) {
		this.comment = comment;
	}
	public int getView() {
		return view;
	}
	public void setView(int view) {
		this.view = view;
	}
	public int getSubmit() {
		return submit;
	}
	public void setSubmit(int submit) {
		this.submit = submit;
	}
	public int getFans() {
		return fans;
	}
	public void setFans(int fans) {
		this.fans = fans;
	}
	public int getIdol() {
		return idol;
	}
	public void setIdol(int idol) {
		this.idol = idol;
	}
	public int getTopicID() {
		return topicID;
	}
	public void setTopicID(int topicID) {
		this.topicID = topicID;
	}
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public String getSource() {
		return source;
	}
	public void setSource(String source) {
		this.source = source;
	}
	/**
	 * @param tagID
	 * @param content
	 * @param interest
	 * @param answer
	 * @param good
	 * @param comment
	 * @param view
	 * @param submit
	 * @param fans
	 * @param idol
	 * @param topicID
	 * @param subject
	 * @param source
	 */
	public Fragment(String tagID, String content, int interest, int answer,
			int good, int comment, int view, int submit, int fans, int idol,
			int topicID, String subject, String source) {
		super();
		this.tagID = tagID;
		this.content = content;
		this.interest = interest;
		this.answer = answer;
		this.good = good;
		this.comment = comment;
		this.view = view;
		this.submit = submit;
		this.fans = fans;
		this.idol = idol;
		this.topicID = topicID;
		this.subject = subject;
		this.source = source;
	}
	
	
}
