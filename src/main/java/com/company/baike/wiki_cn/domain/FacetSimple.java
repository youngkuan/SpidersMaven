package com.company.baike.wiki_cn.domain;
/**  
 * 简单的分面对象
 * 1. 分面名
 * 2. 分面级数   
 *  
 * @author 郑元浩 
 * @date 2016年11月29日
 */
public class FacetSimple {

	public String facetName;
	public int facetLayer;
	
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
	/**
	 * @param facetName
	 * @param facetLayer
	 */
	public FacetSimple(String facetName, int facetLayer) {
		super();
		this.facetName = facetName;
		this.facetLayer = facetLayer;
	}
	
}
