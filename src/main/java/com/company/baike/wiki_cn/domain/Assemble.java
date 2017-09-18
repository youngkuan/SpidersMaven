package com.company.baike.wiki_cn.domain;
/**  
 * 1. 分面名
 * 2. 分面对应的知识碎片内容
 * 3. 分面层次
 *  
 * @author 郑元浩 
 * @date 2016年11月29日
 */
public class Assemble {
	
	public String facetName;
	public String facetContent;
	public int facetLayer;
	
	public String getFacetName() {
		return facetName;
	}
	public void setFacetName(String facetName) {
		this.facetName = facetName;
	}
	public String getFacetContent() {
		return facetContent;
	}
	public void setFacetContent(String facetContent) {
		this.facetContent = facetContent;
	}
	public int getFacetLayer() {
		return facetLayer;
	}
	public void setFacetLayer(int facetLayer) {
		this.facetLayer = facetLayer;
	}
	/**
	 * @param facetName
	 * @param facetContent
	 * @param facetLayer
	 */
	public Assemble(String facetName, String facetContent, int facetLayer) {
		super();
		this.facetName = facetName;
		this.facetContent = facetContent;
		this.facetLayer = facetLayer;
	}
	
	

}
