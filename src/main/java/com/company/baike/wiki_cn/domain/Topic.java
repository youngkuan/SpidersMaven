package com.company.baike.wiki_cn.domain;
/**  
 * 领域主题（有ID）
 *  
 * @author 郑元浩 
 * @date 2016年11月28日
 */
public class Topic {
	
	public int topicID;
	public String topicName;
	public String topicUrl;
	
	public int getTopicID() {
		return topicID;
	}
	public void setTopicID(int topicID) {
		this.topicID = topicID;
	}
	public String getTopicName() {
		return topicName;
	}
	public void setTopicName(String topicName) {
		this.topicName = topicName;
	}
	public String getTopicUrl() {
		return topicUrl;
	}
	public void setTopicUrl(String topicUrl) {
		this.topicUrl = topicUrl;
	}
	/**
	 * @param topicID
	 * @param topicName
	 * @param topicUrl
	 */
	public Topic(int topicID, String topicName, String topicUrl) {
		super();
		this.topicID = topicID;
		this.topicName = topicName;
		this.topicUrl = topicUrl;
	}
	
	
	
}
