package com.company.webmagic;

import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;

import java.util.List;

public class YangKuanSpider extends Spider{
    public YangKuanSpider(PageProcessor pageProcessor) {
        super(pageProcessor);
    }
    public static YangKuanSpider create(PageProcessor pageProcessor){
        return new YangKuanSpider(pageProcessor);
    }
    public YangKuanSpider addRequests(List<Request> requests){
        for(Request request:requests){
            this.addRequest(request);
        }
        return this;
    }
}
