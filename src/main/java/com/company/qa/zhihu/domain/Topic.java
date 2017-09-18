package com.company.qa.zhihu.domain;
/**  
 * 主题   
 *  
 * @author 郑元浩 
 * @date 2016年12月22日
 */
public class Topic {

	public int id;
	public String name;
	public String subject;
	
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	/**
	 * @param id
	 * @param name
	 * @param subject
	 */
	public Topic(int id, String name, String subject) {
		super();
		this.id = id;
		this.name = name;
		this.subject = subject;
	}
	
	
	
}
