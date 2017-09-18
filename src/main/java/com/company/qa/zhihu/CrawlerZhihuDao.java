package com.company.qa.zhihu;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;

import us.codecraft.webmagic.downloader.HttpClientDownloader;
import us.codecraft.webmagic.selector.Html;

import com.company.app.Config;
import com.company.utils.Log;
import com.company.utils.SaveHtml;

/**
 * Selenium模拟浏览器爬取Quora
 * @author 郑元浩
 * @date 2016年12月22号
 */
public class CrawlerZhihuDao {
	
	public static void main(String[] args) throws Exception{
		String filePath = "F:\\1.html";
		String url = "https://www.zhihu.com/search?type=content&q=%E4%BA%8C%E5%8F%89%E6%A0%91";
		selenium(filePath, url);
	}
	
	/**
	 * 登陆后的页面
	 * @param filePath
	 * @param url
	 * @throws Exception
	 */
	public static void selenium(String filePath, String url) throws Exception {
		System.setProperty("webdriver.ie.driver", Config.IE_PATH);
		int randomTimeout = SaveHtml.random(1000, 2000);
		File file = new File(filePath);
		if(!file.exists()){
			WebDriver driver = new InternetExplorerDriver();
			int m = 1;
			driver.manage().timeouts().pageLoadTimeout(randomTimeout, TimeUnit.SECONDS);
			while (m < 3) {
				try{
					driver.get(url);
				}
				catch (Exception e) {
					Log.log("第" + m + "次重载页面...");
					m++;
					driver.quit();
					driver = new InternetExplorerDriver();
					int randomTimeout2 = SaveHtml.random(1000, 2000);
					driver.manage().timeouts().pageLoadTimeout(randomTimeout2, TimeUnit.SECONDS);
					continue;
				}
				break;
			}
			Log.log("...begin to save page...");
			Log.log("Page title is: " + driver.getTitle());
			
//			/**
//			 *  roll the page
//			 */
//			JavascriptExecutor JS = (JavascriptExecutor) driver;
//			try {
//				JS.executeScript("scrollTo(0, document.body.scrollHeight)");
//				Log.log("1");
//				Thread.sleep(SaveHtml.random(1000, 2000));
//			} catch (Exception e) {
//				Log.log("Error at loading the page ...");
//				e.printStackTrace();
//				driver.quit();
//			}
			
			/**
			 *  save page
			 */
			String html = driver.getPageSource();
			SaveHtml.saveHtml(filePath, html);
			Log.log("...save finish...");
			
			/**
			 *  Close the browser
			 */
//			Thread.sleep(SaveHtml.random(1000, 2000));
			driver.quit();
		}else{
			Log.log(filePath + "已经存在，不必再次爬取...");
		}
	}
	
	/**
	 * 
	 * @param url
	 * @return
	 * @throws Exception
	 */
	public static String selenium2(String url) throws Exception {
		System.setProperty("webdriver.ie.driver", Config.IE_PATH);
		int randomTimeout = SaveHtml.random(1000, 2000);
		WebDriver driver = new InternetExplorerDriver();
		int m = 1;
		driver.manage().timeouts().pageLoadTimeout(randomTimeout, TimeUnit.SECONDS);
		while (m < 3) {
			try{
				driver.get(url);
			}
			catch (Exception e) {
				Log.log("第" + m + "次重载页面...");
				m++;
				driver.quit();
				driver = new InternetExplorerDriver();
				int randomTimeout2 = SaveHtml.random(1000, 2000);
				driver.manage().timeouts().pageLoadTimeout(randomTimeout2, TimeUnit.SECONDS);
				continue;
			}
			break;
		}
		String html = driver.getPageSource();
		driver.quit();
		return html;
	}
	
	public static String webmagicCrawler(String url) {
		HttpClientDownloader httpClientDownloader = new HttpClientDownloader();
		Html html = httpClientDownloader.download(url, "UTF-8");
		String content = html.toString();
		return content;
	}
	
    public static String inputStream2String(InputStream in_st,String charset) throws IOException{
        BufferedReader buff = new BufferedReader(new InputStreamReader(in_st, charset));
        StringBuffer res = new StringBuffer();
        String line = "";
        while((line = buff.readLine()) != null){
            res.append(line);
        }
        return res.toString();
    }
	
}


