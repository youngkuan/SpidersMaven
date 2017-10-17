package com.company.baike.wiki_cn;

import java.io.InputStream;
import java.util.concurrent.TimeUnit;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

import com.company.app.Config;
import com.company.utils.Log;
import com.company.utils.SaveHtml;

import org.openqa.selenium.ie.InternetExplorerDriver;
import us.codecraft.webmagic.downloader.HttpClientDownloader;
import us.codecraft.webmagic.selector.Html;

/**  
 * Selenium模拟浏览器爬取中文维基
 *  
 * @author 郑元浩 
 * @date 2016年11月26日
 */
@SuppressWarnings("deprecation")
public class DownloaderDAO {
	
	/**
	 * 返回中文维基页面加载结果的HTML源码
	 * 拖动页面一次
	 * @param url
	 * @return 
	 * @throws Exception
	 */
	public static String seleniumWikiCN(String url) throws Exception {
//		System.setProperty("webdriver.chrome.driver", Config.CHROME_PATH);
		System.setProperty("webdriver.ie.driver", Config.IE_PATH);
//		System.setProperty("phantomjs.binary.path", Config.PHANTOMJS_PATH);
		int randomTimeout = SaveHtml.random(1000, 2000);
//		WebDriver driver = new ChromeDriver();
		WebDriver driver = new InternetExplorerDriver();
//		WebDriver driver = new PhantomJSDriver();
		int m = 1;
		driver.manage().timeouts().pageLoadTimeout(randomTimeout, TimeUnit.SECONDS);
		while (m < 4) {
			try{
				driver.get(url);
//		        driver.manage().window().maximize();
			} catch (Exception e) {
				Log.log("第" + m + "次重载页面...");
				m++;
				driver.quit();
//				driver = new ChromeDriver();
				driver = new InternetExplorerDriver();
//				driver = new PhantomJSDriver();
				int randomTimeout2 = SaveHtml.random(1000, 2000);
				driver.manage().timeouts().pageLoadTimeout(randomTimeout2, TimeUnit.SECONDS);
				continue;
			}
			break;
		}
//		Log.log("...begin to load page...");
		Log.log("Page title is: " + driver.getTitle());
			
		/**
		 *  roll the page
		 */
		JavascriptExecutor JS = (JavascriptExecutor) driver;
		try {
			JS.executeScript("scrollTo(0, document.body.scrollHeight)");
			Thread.sleep(500);
		} catch (Exception e) {
			Log.log("Error at loading the page ...");
			e.printStackTrace();
			driver.quit();
		}
		
		/**
		 *  save page
		 */
		String html = driver.getPageSource();
		
		/**
		 *  Close the browser
		 */
		Thread.sleep(SaveHtml.random(1000, 2000));
		driver.quit();
		
		return html;
	}
	
//	/**
//	 * 返回中文维基页面加载结果的HTML源码（并将其保存到本地网页）
//	 * 拖动页面一次
//	 * @param filePath
//	 * @param url
//	 * @throws Exception
//	 */
//	public static void seleniumWikiCNHTML(String filePath, String url) throws Exception {
//		System.setProperty("webdriver.ie.driver", Config.IE_PATH);
//		int randomTimeout = SaveHtml.random(1000, 2000);
//		File file = new File(filePath);
//		if(!file.exists()){
//			WebDriver driver = new InternetExplorerDriver();
//			int m = 1;
//			driver.manage().timeouts().pageLoadTimeout(randomTimeout, TimeUnit.SECONDS);
//			while (m < 4) {
//				try{
//					driver.get(url);
//			        driver.manage().window().maximize();
//				}
//				catch (Exception e) {
//					Log.log("第" + m + "次重载页面...");
//					m++;
//					driver.quit();
//					driver = new InternetExplorerDriver();
//					int randomTimeout2 = SaveHtml.random(1000, 2000);
//					driver.manage().timeouts().pageLoadTimeout(randomTimeout2, TimeUnit.SECONDS);
//					continue;
//				}
//				break;
//			}
//			Log.log("...begin to save page...");
//			Log.log("Page title is: " + driver.getTitle());
//			
//			/**
//			 *  roll the page
//			 */
//			JavascriptExecutor JS = (JavascriptExecutor) driver;
//			try {
//				JS.executeScript("scrollTo(0, document.body.scrollHeight)");
//				Log.log("1");
//				Thread.sleep(SaveHtml.random(300, 500));
//			} catch (Exception e) {
//				Log.log("Error at loading the page ...");
//				e.printStackTrace();
//				driver.quit();
//			}
//			
//			/**
//			 *  save page
//			 */
//			String html = driver.getPageSource();
//			SaveHtml.saveHtml(filePath, html);
//			Log.log("...save finish...");
//			
//			/**
//			 *  Close the browser
//			 */
//			Thread.sleep(SaveHtml.random(100, 200));
//			driver.quit();
//		}else{
//			Log.log(filePath + "已经存在，不必再次爬取...");
//		}
//	}

	/**
	 * httpClient方法爬取wiki中文
	 * @return 
	 */
	public static String httpClientWikiCN(String url){
		String html = "";
		@SuppressWarnings("resource")
		HttpClient hc = new DefaultHttpClient();
		try
		{
			String charset = "UTF-8";
		    Log.log(String.format("\nFetching %s...", url));   	        	    
		    HttpGet hg = new HttpGet(url);     
		    hg.setHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
		    hg.setHeader("Accept-Language", "zh-cn,zh;q=0.5");
		    hg.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 5.1; rv:7.0.1) Gecko/20100101 Firefox/7.0.1)");
		    hg.setHeader("Accept-Charset", "utf-8;q=0.7,*;q=0.7");
		    hg.setHeader("Host", "zh.wikipedia.org");
	        hg.setHeader("Connection", "Keep-Alive");
		    HttpResponse response = hc.execute(hg);
		    HttpEntity entity = response.getEntity();   	       	        
		    InputStream htmInput = null;       
		    if(entity != null){
		        htmInput = entity.getContent();
		        html = SaveHtml.inStream2String(htmInput,charset);
		        Log.log("爬取成功:" + " 网页长度为  " + entity.getContentLength());
		    }  
		}
		catch(Exception err) {
			System.err.println("爬取失败...失败原因: " + err.getMessage()); 		
		}
		finally {
	        //关闭连接，释放资源
	        hc.getConnectionManager().shutdown();
	    }
		return html;
	}
	
	/**
	 * 爬取URL
	 */
	public static String webmagicWikiCN(String url) {
		Log.log("connect to " + url + "....");
		HttpClientDownloader httpClientDownloader = new HttpClientDownloader();
//		Html html = httpClientDownloader.download(url);
		Html html = httpClientDownloader.download(url, "utf-8");
		String content = html.toString();
		Log.log("success connect to : " + url);
		return content;
	}
	
	

}
