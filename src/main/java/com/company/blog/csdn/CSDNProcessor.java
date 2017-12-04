package com.company.blog.csdn;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.pipeline.ConsolePipeline;
import us.codecraft.webmagic.processor.PageProcessor;
import com.company.app.Config;

import com.company.webmagic.ProcessorSQL;
import com.company.webmagic.SqlPipeline;
import com.company.webmagic.YangKuanSpider;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CSDNProcessor implements PageProcessor{
    private Site site = Site.me().setRetryTimes(3).setSleepTime(1000).setTimeOut(10000);
    public Site getSite() {
        return site;
    }
    public void process(Page page) {
        List<String> fragments =  page.getHtml().xpath("div[@id='article_content']").all();
        page.putField("fragments",fragments);
        //爬取碎片
        List<String> urls;
        urls = page.getHtml().xpath("dl[@class='search-list J_search']/dd[@class='search-link']/a/@href").all();
        //System.out.println("链接数: "+urls.size()+" "+urls.get(0));
        //此处应该添加请求的附加信息，extras
        for(String url:urls){
            Request request = new Request();
            request.setUrl(url);
            //System.out.println(url);
            request.setExtras(page.getRequest().getExtras());
            page.addTargetRequest(request);
        }
    }
    public void CSDNAnswerCrawl(String courseName){
        //1.获取分面名
        ProcessorSQL processorSQL = new ProcessorSQL();
        List<Map<String, Object>> allFacetsInformation = processorSQL.getAllFacets(Config.facetTable,courseName);
        //2.添加连接请求
        List<Request> requests = new ArrayList<Request>();
        for(Map<String, Object> facetInformation:allFacetsInformation){
            Request request = new Request();
            String url = "http://so.csdn.net/so/search/s.do?q="
                    +facetInformation.get("ClassName")+" "
                    +facetInformation.get("TermName")+" "
                    +facetInformation.get("FacetName");
            //添加链接;设置额外信息
            requests.add(request.setUrl(url).setExtras(facetInformation));
        }
        YangKuanSpider.create(new CSDNProcessor())
                .addRequests(requests)
                .thread(5)
                .addPipeline(new SqlPipeline())
                .addPipeline(new ConsolePipeline())
                .runAsync();
    }
}
