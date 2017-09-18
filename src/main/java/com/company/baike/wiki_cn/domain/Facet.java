package com.company.baike.wiki_cn.domain;
/**  
 * 分面，用于数据库读写
 *  
 * @author 郑元浩 
 * @date 2016年11月28日
 */
public class Facet {

	public int termID;
	public String termName;
	public String facetName;
	public int facetLayer;
	public String className;
	
	public int getTermID() {
		return termID;
	}
	public void setTermID(int termID) {
		this.termID = termID;
	}
	public String getTermName() {
		return termName;
	}
	public void setTermName(String termName) {
		this.termName = termName;
	}
	public String getFacetName() {
		return facetName;
	}
	public void setFacetName(String facetName) {
		this.facetName = facetName;
	}
	public int getFacetLayer() {
		return facetLayer;
	}
	public void setFacetLayer(int facetLayer) {
		this.facetLayer = facetLayer;
	}
	public String getClassName() {
		return className;
	}
	public void setClassName(String className) {
		this.className = className;
	}
	/**
	 * @param termID
	 * @param termName
	 * @param facetName
	 * @param facetLayer
	 * @param className
	 */
	public Facet(int termID, String termName, String facetName, int facetLayer,
			String className) {
		super();
		this.termID = termID;
		this.termName = termName;
		this.facetName = facetName;
		this.facetLayer = facetLayer;
		this.className = className;
	}
	
	
}
