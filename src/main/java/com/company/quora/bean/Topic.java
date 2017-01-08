package com.company.quora.bean;
/**  
 * Topic
 * 1. topic_id：主题唯一ID，从1开始计数
 * 2. topic_name：主题名字
 * 3. topic_url：Quora中主题所有问题页面对应的链接
 * 4. topic_question：统计主题下的问题总数
 * 5. topic_questionCountTime：统计主题下的问题总数的当前时间
 *  
 * @author 郑元浩 
 * @date 2016年11月25日
 */
public class Topic {
	
	public int topic_id;
	public String topic_name;
	public String topic_url;
	public int topic_question;
	public String topic_questionCountTime;
	public int getTopic_id() {
		return topic_id;
	}
	public void setTopic_id(int topic_id) {
		this.topic_id = topic_id;
	}
	public String getTopic_name() {
		return topic_name;
	}
	public void setTopic_name(String topic_name) {
		this.topic_name = topic_name;
	}
	public String getTopic_url() {
		return topic_url;
	}
	public void setTopic_url(String topic_url) {
		this.topic_url = topic_url;
	}
	public int getTopic_question() {
		return topic_question;
	}
	public void setTopic_question(int topic_question) {
		this.topic_question = topic_question;
	}
	public String getTopic_questionCountTime() {
		return topic_questionCountTime;
	}
	public void setTopic_questionCountTime(String topic_questionCountTime) {
		this.topic_questionCountTime = topic_questionCountTime;
	}
	/**
	 * @param topic_id
	 * @param topic_name
	 * @param topic_url
	 * @param topic_question
	 * @param topic_questionCountTime
	 */
	public Topic(int topic_id, String topic_name, String topic_url,
			int topic_question, String topic_questionCountTime) {
		super();
		this.topic_id = topic_id;
		this.topic_name = topic_name;
		this.topic_url = topic_url;
		this.topic_question = topic_question;
		this.topic_questionCountTime = topic_questionCountTime;
	}
	@Override
	public String toString() {
		return "Topic [topic_id=" + topic_id + ", topic_name=" + topic_name
				+ ", topic_url=" + topic_url + ", topic_question="
				+ topic_question + ", topic_questionCountTime="
				+ topic_questionCountTime + "]";
	}

	

}
