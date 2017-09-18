package com.company.baike.wiki_cn.domain;
/**  
 * 上下位关系   
 *  
 * @author 郑元浩 
 * @date 2016年12月19日
 */
public class Relation {
	
	public String parent;
	public String child;
	
	public String getParent() {
		return parent;
	}
	public void setParent(String parent) {
		this.parent = parent;
	}
	public String getChild() {
		return child;
	}
	public void setChild(String child) {
		this.child = child;
	}
	/**
	 * @param parent
	 * @param child
	 */
	public Relation(String parent, String child) {
		super();
		this.parent = parent;
		this.child = child;
	}
	
	

}
