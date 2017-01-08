package com.company.quora.bean;
/**  
 * Quora Topic
 *  
 * @author 郑元浩 
 * @date 2016年11月21日
 */
public class TopicExcel {
	private String topicName;
	private String topicLink;
	private int questionNum;
	public String getTopicName() {
		return topicName;
	}
	public void setTopicName(String topicName) {
		this.topicName = topicName;
	}
	public String getTopicLink() {
		return topicLink;
	}
	public void setTopicLink(String topicLink) {
		this.topicLink = topicLink;
	}
	public int getQuestionNum() {
		return questionNum;
	}
	public void setQuestionNum(int questionNum) {
		this.questionNum = questionNum;
	}
	/**
	 * @param topicName
	 * @param topicLink
	 * @param questionNum
	 */
	public TopicExcel(String topicName, String topicLink, int questionNum) {
		super();
		this.topicName = topicName;
		this.topicLink = topicLink;
		this.questionNum = questionNum;
	}
	
	
}
