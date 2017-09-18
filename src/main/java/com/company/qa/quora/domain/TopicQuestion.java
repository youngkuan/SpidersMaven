package com.company.qa.quora.domain;
/**  
 * 类说明   
 *  
 * @author 郑元浩 
 * @date 2016年11月25日
 */
public class TopicQuestion {

	public String question_id;
	public String question_url;
	public String topic_name;
	public String getQuestion_id() {
		return question_id;
	}
	public void setQuestion_id(String question_id) {
		this.question_id = question_id;
	}
	public String getQuestion_url() {
		return question_url;
	}
	public void setQuestion_url(String question_url) {
		this.question_url = question_url;
	}
	public String getTopic_name() {
		return topic_name;
	}
	public void setTopic_name(String topic_name) {
		this.topic_name = topic_name;
	}
	/**
	 * @param question_id
	 * @param question_url
	 * @param topic_name
	 */
	public TopicQuestion(String question_id, String question_url,
			String topic_name) {
		super();
		this.question_id = question_id;
		this.question_url = question_url;
		this.topic_name = topic_name;
	}
	
	
}
