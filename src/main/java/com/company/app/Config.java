package com.company.app;

import com.spreada.utils.chinese.ZHConverter;

/**
 * 
 * @author 郑元浩
 * @date 2016年11月25日14:20:20
 * @description 这是一个全局的配置文件
 */

public class Config {

	/**
	 * 表名
	* */
	//主题表
	public static String topicTable = "domain_topic";
	//分面表
	public static String facetTable = "facet";
	//碎片表
	public static String fragmentTable = "assemble_fragment";
	//课程名
	public static String course = "数据结构";

	/**
	 * Selenium Webdriver 配置
	 */
	public static String PHANTOMJS_PATH = "D:\\phantomjs.exe";  // 无界面浏览器
	public static String IE_PATH = "D:\\IEDriverServer.exe";  // IE模拟
	public static String CHROME_PATH = "E:/workspace/java_workspace/chromedriver.exe"; // Chrome模拟
	
	/**
	 * Mysql 配置
	 */
//	public static String MYSQL_URL = "jdbc:mysql://202.117.54.39:3306/quality?user=root&password=199306&characterEncoding=UTF8";
//	public static String MYSQL_URL = "jdbc:mysql://202.117.54.39:3306/yotta2?user=root&password=199306&characterEncoding=UTF8";
	public static String MYSQL_URL = "jdbc:mysql://localhost:3306/yotta_create?user=root&password=root&characterEncoding=UTF8";

	/**
	 * Quora 爬虫参数  配置
	 * @author 郑元浩
	 */
	public static String Expi_PATH = "F:\\02-CQA网站中问题答案质量评估";
	public static String QUORA_PATH = "F:\\02-CQA网站中问题答案质量评估\\00-NewDataSets";
	
	/**
	 * Baidu 爬虫参数  配置
	 * @author 郑元浩
	 */
	public static String SERVER = "localhost:8080";
	public static String PROJECT = "KG";
	public static String IMAGE_API_PATH = "SpiderTest";
	public static String CATALOG_TABLE = "datainput_catalog";
	public static String RELATION_TABLE = "datainput_relation";
	public static String TERM_TABLE = "datainput_term"; // term的表名
	public static String SYSTEM_TABLE = "system_step_status";
	public static int IMAGE_COUNT = 7; // 设置每个分面下的图片数量

	/**
	 * 知乎 爬虫参数  配置
	 * @author 郑元浩
	 */
	public static String ZHIHU_PATH = "F:\\00-Yotta数据爬取---MOOC需求\\00-百度百科7门课程文本数据\\百度百科_数据结构";
	public static String ZHIHU_FILE_PATH = "F:\\03-other\\07-知乎";
	
	/**
	 * 中文维基  爬虫参数  配置
	 * @author 郑元浩
	 */
	public static ZHConverter converter = ZHConverter.getInstance(ZHConverter.SIMPLIFIED);// 转化为简体中文
	public static String DOMAIN_TABLE = "domain";
	public static String FACET_TABLE = "facet";
	public static String FACET_RELATION_TABLE = "facet_relation";
	public static String DOMAIN_LAYER_TABLE = "domain_layer";
	public static String DOMAIN_LAYER_FUZHU_TABLE = "domain_layer_fuzhu";
	public static String DOMAIN_LAYER_FUZHU2_TABLE = "domain_layer_fuzhu2";
	public static String DOMAIN_TOPIC_TABLE = "domain_topic";
	public static String DOMAIN_TOPIC_RELATION_TABLE = "domain_topic_relation";
	public static String DOMAIN_LAYER_RELATION_TABLE = "domain_layer_relation";
	public static String DOMAIN_LAYER_RELATION2_TABLE = "domain_layer_relation2";
	public static String SPIDER_TEXT_TABLE = "spider_text";
	public static String SPIDER_IMAGE_TABLE = "spider_image";
	public static String ASSEMBLE_TEXT_TABLE = "assemble_text";
	public static String ASSEMBLE_IMAGE_TABLE = "assemble_image";
	public static String ASSEMBLE_FRAGMENT_TABLE = "assemble_fragment";
	public static String DEPENDENCY = "dependency";

	public static int CONTENTLENGTH = 200;
//	public static int DEPENDENCEMAX = 100; // 主题认知关系数量，固定值，没有使用

	public static String CLASS_FILE_PATH = "D:\\Workspace\\eclipse\\Yotta_db\\domains.txt";


}
