package com.company.qa.zhihu.domain;
/**  
 * 用于LDA计算   
 *  
 * @author 郑元浩 
 * @date 2016年12月23日
 */
public class LdaInput {
	
	public String id;
	public String content;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	/**
	 * @param id
	 * @param content
	 */
	public LdaInput(String id, String content) {
		super();
		this.id = id;
		this.content = content;
	}
	
}
