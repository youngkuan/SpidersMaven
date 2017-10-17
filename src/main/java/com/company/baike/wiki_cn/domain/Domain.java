package com.company.baike.wiki_cn.domain;
/**  
 * 领域名--->课程名，用于数据库读写
 *  
 * @author 郑元浩 
 * @date 2016年11月28日
 */
public class Domain {
	
	public int classID;
	public String className;
	public int getClassID() {
		return classID;
	}
	public void setClassID(int classID) {
		this.classID = classID;
	}
	public String getClassName() {
		return className;
	}
	public void setClassName(String className) {
		this.className = className;
	}

	public Domain() {
	}

	/**
	 * @param classID
	 * @param className
	 */
	public Domain(int classID, String className) {
		super();
		this.classID = classID;
		this.className = className;
	}
	
	

}
