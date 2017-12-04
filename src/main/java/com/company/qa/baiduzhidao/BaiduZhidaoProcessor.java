package com.company.qa.baiduzhidao;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.processor.PageProcessor;
import com.company.app.Config;

import com.company.webmagic.ProcessorSQL;
import com.company.webmagic.SqlPipeline;
import com.company.webmagic.YangKuanSpider;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BaiduZhidaoProcessor implements PageProcessor {
    private Site site = Site.me().setRetryTimes(3).setSleepTime(1000).setTimeOut(10000);
    public Site getSite() {
        return site;
    }
    public void process(Page page) {
        List<String> fragments = page.getHtml().xpath("pre[@class='best-text mb-10']").all();
        page.putField("fragments", fragments);
        //爬取碎片
        List<String> urls;
        urls = page.getHtml().xpath("dl[@class='dl']//a[@class='ti']/@href").all();
        //此处应该添加请求的附加信息，extras
        for (String url : urls) {
            Request request = new Request();
            request.setUrl(url);
            request.setExtras(page.getRequest().getExtras());
            page.addTargetRequest(request);
        }
    }
    public void baiduAnswerCrawl(String courseName){
        //1.获取分面名
        ProcessorSQL processorSQL = new ProcessorSQL();
        List<Map<String, Object>> allFacetsInformation = processorSQL.getAllFacets(Config.facetTable,courseName);
        //2.添加连接请求
        List<Request> requests = new ArrayList<Request>();
        for(Map<String, Object> facetInformation:allFacetsInformation){
            Request request = new Request();
            String url = "https://zhidao.baidu.com/search?lm=0&rn=10&pn=0&fr=search&ie=gbk&word="
                    +facetInformation.get("ClassName")+" "
                    +facetInformation.get("TermName")+" "
                    +facetInformation.get("FacetName");
            //添加链接;设置额外信息
            requests.add(request.setUrl(url).setExtras(facetInformation));
        }
        YangKuanSpider.create(new BaiduZhidaoProcessor())
                .addRequests(requests)
                .thread(5)
                .addPipeline(new SqlPipeline())
                //.addPipeline(new ConsolePipeline())
                .runAsync();
    }
}
