package com.company.webmagic;

import com.company.app.Config;
import com.company.blog.csdn.CSDNProcessor;
import com.company.qa.baiduzhidao.BaiduZhidaoProcessor;
import com.company.qa.quora.QuoraProcessor;
import com.company.qa.zhihu.ZhihuProcessor;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class FragmentCrawler {
    public static void main(String[] args) throws IOException{
        // 爬取多门课程
        /*List<String> domainList = FileUtils.readLines(new File(Config.CLASS_FILE_PATH));*/
        //通过读取数据库的domain表，获取课程
        ProcessorSQL processorSQL = new ProcessorSQL();
        List<String> domainList = processorSQL.getCourses(Config.DOMAIN_TABLE);

        System.out.println(domainList.size());
        for(int i =0;i<1;i++){
            //爬取百度知道
            BaiduZhidaoProcessor baiduZhidaoProcessor = new BaiduZhidaoProcessor();
            baiduZhidaoProcessor.baiduAnswerCrawl(domainList.get(i));
            //System.out.println("百度知道碎片爬取完成");

            //爬取知乎
            ZhihuProcessor zhihuProcessor = new ZhihuProcessor();
            zhihuProcessor.zhihuAnswerCrawl(domainList.get(i));
            //System.out.println("知乎碎片爬取完成");

            //爬取CSDN
            CSDNProcessor csdnProcessor = new CSDNProcessor();
            csdnProcessor.CSDNAnswerCrawl(domainList.get(i));
            //System.out.println("CSDN碎片爬取完成");

            //爬取quora
            /*QuoraProcessor quoraProcessor = new QuoraProcessor();
            quoraProcessor.quoraAnswerCrawl(domainList.get(i));
            System.out.println("QUORA碎片爬取完成");
            */
        }
    }
}
