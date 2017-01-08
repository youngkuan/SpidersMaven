package com.company.utils;

import java.util.List;
import java.util.Set;

import com.company.wiki_cn.bean.Domain;
import com.company.wiki_cn.bean.FacetRelation;
import com.company.wiki_cn.bean.Term;
import com.company.wiki_cn.bean.Topic;

/**  
 * 类说明   
 *  
 * @author 郑元浩 
 * @date 2016年11月14日
 */
public class Log {
	
	private final static Boolean flag = false;
//	private final static Boolean flag = true;

	public static void main(String[] args) {
		log("test print...");
	}
	
	/**
	 * 打印信息
	 * @param object
	 */
	public static void log(Object object){
		if(flag){
			System.out.println(object);
		}
	}
	
	/**
	 * 打印List<Object>中的每个元素（基本类型：int, String, ...）
	 * @param list
	 */
	public static void log(List<Object> list){
		for (int i = 0; i < list.size(); i++) {
			log(list.get(i));
		}
	}
	
	/**
	 * 打印List<Domain>中的每个元素
	 * @param list
	 */
	public static void logDomain(List<Domain> list){
		for (int i = 0; i < list.size(); i++) {
			Domain domain = list.get(i);
			Log.log("domain id: " + domain.getClassID());
			Log.log("domain name: " + domain.getClassName());
		}
	}
	
	/**
	 * 打印List中的每个元素
	 * @param list
	 */
	public static void logTopic(List<Topic> list){
		for (int i = 0; i < list.size(); i++) {
			Topic topic = list.get(i);
			Log.log("TopicID: " + topic.getTopicID() + 
					" --> TopicName: " + topic.getTopicName() + 
					" --> TopicUrl: " + topic.getTopicUrl());
		}
	}
	
	/**
	 * 
	 * @param list
	 */
	public static void logFacetRelation(List<FacetRelation> list){
		for (int i = 0; i < list.size(); i++) {
			FacetRelation facetRelation = list.get(i);
			Log.log("childFacet: " + facetRelation.childFacet + 
					" --> childLayer: " + facetRelation.childLayer + 
					" --> parentFacet: " + facetRelation.parentFacet + 
					" --> parentLayer: " + facetRelation.parentLayer);
		}
	}
	
	/**
	 * 
	 * @param list
	 */
	public static void logTerm(List<Term> list){
		for (int i = 0; i < list.size(); i++) {
			Term term = list.get(i);
			Log.log("termName: " + term.getTermName() + 
					" --> termUrl: " + term.getTermUrl());
		}
	}
	
	/**
	 * 
	 * @param set
	 */
	public static void logTerm(Set<Term> set){
		for (Term term : set) {
			Log.log("termName: " + term.getTermName() + 
					" --> termUrl: " + term.getTermUrl());
		}
	}
	

}
