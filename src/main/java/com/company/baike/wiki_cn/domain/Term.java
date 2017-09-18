package com.company.baike.wiki_cn.domain;
/**  
 * 领域术语（无ID）
 *  
 * @author 郑元浩 
 * @date 2016年11月26日
 */
public class Term {
	
	public String termName;
	public String termUrl;
	public String getTermName() {
		return termName;
	}
	public void setTermName(String termName) {
		this.termName = termName;
	}
	public String getTermUrl() {
		return termUrl;
	}
	public void setTermUrl(String termUrl) {
		this.termUrl = termUrl;
	}
	/**
	 * @param termName
	 * @param termUrl
	 */
	public Term(String termName, String termUrl) {
		super();
		this.termName = termName;
		this.termUrl = termUrl;
	}
	
	
	
}
