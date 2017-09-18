package com.company.baike.wiki_cn.domain;
/**  
 * 存储分面之间的关系  
 *  
 * @author 郑元浩 
 * @date 2016年11月29日
 */
public class FacetRelation {
	
	public String childFacet;
	public int childLayer;
	public String parentFacet;
	public int parentLayer;
	
	
	public String getChildFacet() {
		return childFacet;
	}
	public void setChildFacet(String childFacet) {
		this.childFacet = childFacet;
	}
	public int getChildLayer() {
		return childLayer;
	}
	public void setChildLayer(int childLayer) {
		this.childLayer = childLayer;
	}
	public String getParentFacet() {
		return parentFacet;
	}
	public void setParentFacet(String parentFacet) {
		this.parentFacet = parentFacet;
	}
	public int getParentLayer() {
		return parentLayer;
	}
	public void setParentLayer(int parentLayer) {
		this.parentLayer = parentLayer;
	}
	/**
	 * @param childFacet
	 * @param childLayer
	 * @param parentFacet
	 * @param parentLayer
	 */
	public FacetRelation(String childFacet, int childLayer, String parentFacet,
			int parentLayer) {
		super();
		this.childFacet = childFacet;
		this.childLayer = childLayer;
		this.parentFacet = parentFacet;
		this.parentLayer = parentLayer;
	}
	
}
