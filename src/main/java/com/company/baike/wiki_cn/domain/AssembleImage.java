package com.company.baike.wiki_cn.domain;
/**  
 * 图片数据   
 *  
 * @author 郑元浩 
 * @date 2016年11月30日
 */
public class AssembleImage {
	
	
	public String imageUrl;
	public int imageWidth;
	public int imageHeight;
	public int facetLayer;
	public String facetName;
	
	public String getImageUrl() {
		return imageUrl;
	}
	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}
	public int getImageWidth() {
		return imageWidth;
	}
	public void setImageWidth(int imageWidth) {
		this.imageWidth = imageWidth;
	}
	public int getImageHeight() {
		return imageHeight;
	}
	public void setImageHeight(int imageHeight) {
		this.imageHeight = imageHeight;
	}
	public int getFacetLayer() {
		return facetLayer;
	}
	public void setFacetLayer(int facetLayer) {
		this.facetLayer = facetLayer;
	}
	public String getFacetName() {
		return facetName;
	}
	public void setFacetName(String facetName) {
		this.facetName = facetName;
	}
	/**
	 * @param imageUrl
	 * @param imageWidth
	 * @param imageHeight
	 * @param facetLayer
	 * @param facetName
	 */
	public AssembleImage(String imageUrl, int imageWidth, int imageHeight,
			int facetLayer, String facetName) {
		super();
		this.imageUrl = imageUrl;
		this.imageWidth = imageWidth;
		this.imageHeight = imageHeight;
		this.facetLayer = facetLayer;
		this.facetName = facetName;
	}
	
}
