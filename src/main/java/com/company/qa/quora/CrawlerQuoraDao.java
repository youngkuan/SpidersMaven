package com.company.qa.quora;

import java.io.File;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;

import com.company.app.Config;
import com.company.utils.Log;
import com.company.utils.SaveHtml;

/**
 * Selenium模拟浏览器爬取Quora
 * @author 郑元浩
 * @date 2016年11月25号
 */
public class CrawlerQuoraDao {
	
	/**
	 * 爬取主题页面
	 * 根据问题数目确定拖动次数
	 * @param filePath
	 * @param url
	 * @param questionLen
	 * @throws Exception
	 */
	public static void seleniumTopic(String filePath, String url, int questionLen) throws Exception {
		/**
		 *  get drag numbers by question numbers
		 */
		int dragTimes = questionLen / 20;
		if (dragTimes > 200) {
			dragTimes = 200;
		}
		Log.log("该主题包含的问题数目为：" + questionLen + "需要拖动次数为：" + dragTimes);
		int randomTimeout = SaveHtml.random(10000, 20000);
		
		/**
		 *  set driver path
		 */
		System.setProperty("webdriver.ie.driver", Config.IE_PATH);
		File file = new File(filePath);
		if(!file.exists()){
			WebDriver driver = new InternetExplorerDriver();
			driver.manage().timeouts().pageLoadTimeout(randomTimeout, TimeUnit.SECONDS);
			while (true){
				try{
					driver.get(url);
				}
				catch (Exception e)
				{
					driver.quit();
					driver = new InternetExplorerDriver();
					int randomTimeout2 = SaveHtml.random(10000, 20000);
					driver.manage().timeouts().pageLoadTimeout(randomTimeout2, TimeUnit.SECONDS);
					continue;
				}
				break;
			}
			Log.log("Page title is: " + driver.getTitle());
			JavascriptExecutor JS = (JavascriptExecutor) driver;
			int line = 0;
			int num = 0;
			while (num < dragTimes){
				try {
					JS.executeScript("scrollTo(" + line + ", " + (line + 5000) + ")");
					line += 5000;
					num++;
					Thread.sleep(SaveHtml.random(3000, 5000));
					Log.log("第 " + num + " 次拖动主题页面...");
				} catch (Exception e) {
					Log.log("...加载页面出错...");
					driver.quit();
				}
			}
			
			/**
			 *  save page
			 */
			String html = driver.getPageSource();
			SaveHtml.saveHtml(filePath, html);
			Log.log("...save finish...");
			
			/**
			 *  Close the browser
			 */
			Thread.sleep(SaveHtml.random(1000, 2000));
			driver.quit();
		}else{
			Log.log(filePath + "已经存在，不必再次爬取...");
		}
	}
	
	
	/**
	 * 爬取问题页面
	 * 拖动页面一次
	 * @param filePath
	 * @param url
	 * @throws Exception
	 */
	public static void seleniumQuestion(String filePath, String url) throws Exception {
		System.setProperty("webdriver.ie.driver", Config.IE_PATH);
		int randomTimeout = SaveHtml.random(10000, 20000);
		File file = new File(filePath);
		if(!file.exists()){
			WebDriver driver = new InternetExplorerDriver();
			int m = 1;
			driver.manage().timeouts().pageLoadTimeout(randomTimeout, TimeUnit.SECONDS);
			while (m < 4) {
				try{
					driver.get(url);
			        driver.manage().window().maximize();
				}
				catch (Exception e) {
					Log.log("第" + m + "次重载页面...");
					m++;
					driver.quit();
					driver = new InternetExplorerDriver();
					int randomTimeout2 = SaveHtml.random(10000, 20000);
					driver.manage().timeouts().pageLoadTimeout(randomTimeout2, TimeUnit.SECONDS);
					continue;
				}
				break;
			}
			Log.log("...begin to save page...");
			Log.log("Page title is: " + driver.getTitle());
			
			/**
			 *  roll the page
			 */
			JavascriptExecutor JS = (JavascriptExecutor) driver;
			try {
				JS.executeScript("scrollTo(0, document.body.scrollHeight)");
				Log.log("1");
				Thread.sleep(SaveHtml.random(3000, 5000));
			} catch (Exception e) {
				Log.log("Error at loading the page ...");
				e.printStackTrace();
				driver.quit();
			}
			
			/**
			 *  save page
			 */
			String html = driver.getPageSource();
			SaveHtml.saveHtml(filePath, html);
			Log.log("...save finish...");
			
			/**
			 *  Close the browser
			 */
			Thread.sleep(SaveHtml.random(1000, 2000));
			driver.quit();
		}else{
			Log.log(filePath + "已经存在，不必再次爬取...");
		}
	}
	
}


