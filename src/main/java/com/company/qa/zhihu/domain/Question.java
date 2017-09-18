package com.company.qa.zhihu.domain;
/**  
 * 类说明   
 *  
 * @author 郑元浩 
 * @date 2016年12月23日
 */
public class Question {
	
	public String tagID;
	public String url;
	public int topicID;
	public String subject;
	public String source;
	public String getTagID() {
		return tagID;
	}
	public void setTagID(String tagID) {
		this.tagID = tagID;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
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
	 * @param url
	 * @param topicID
	 * @param subject
	 * @param source
	 */
	public Question(String tagID, String url, int topicID, String subject,
			String source) {
		super();
		this.tagID = tagID;
		this.url = url;
		this.topicID = topicID;
		this.subject = subject;
		this.source = source;
	}

}
