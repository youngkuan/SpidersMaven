package com.company.webmagic;

import com.company.blog.csdn.CSDNProcessor;
import com.company.qa.baiduzhidao.BaiduZhidaoProcessor;
import com.company.qa.quora.QuoraProcessor;
import com.company.qa.zhihu.ZhihuProcessor;

public class FragmentCrawler {
    public static void main(String[] args){
        //爬取百度知道
        BaiduZhidaoProcessor baiduZhidaoProcessor = new BaiduZhidaoProcessor();
        baiduZhidaoProcessor.baiduAnswerCrawl();

        //爬取知乎
        ZhihuProcessor zhihuProcessor = new ZhihuProcessor();
        zhihuProcessor.zhihuAnswerCrawl();

        //爬取CSDN
        CSDNProcessor csdnProcessor = new CSDNProcessor();
        csdnProcessor.CSDNAnswerCrawl();

        //爬取quora
        /*QuoraProcessor quoraProcessor = new QuoraProcessor();
        quoraProcessor.quoraAnswerCrawl();*/
    }
}
