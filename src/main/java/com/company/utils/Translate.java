package com.company.utils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.InputStream;
import java.net.URLEncoder;
import java.text.MessageFormat;

/**
 * TranslateUtil
 *
 * <pre>翻譯工具
 * PS: 透過google translate
 * </pre>
 *
 * @author catty
 * @version 1.0, Created on 2011/9/2
 */
public class Translate {

    protected static final String URL_TEMPLATE ="http://www.iciba.com/{0}";
    protected static final String CSSQUERY ="li.clearfix>p>span";
    protected static final String ENCODING ="UTF-8";

    /**
     * <pre>Google翻譯</pre>
     *
     * @param text
     * @return
     * @throws Exception
     */
    public static String translateCh2En(final String text){
        InputStream is =null;
        Document doc =null;
        Element ele =null;
        String result = "";
        try{
            // create URL string
            String url = MessageFormat.format(URL_TEMPLATE,
            URLEncoder.encode(text, ENCODING));

            // parse html by Jsoup
            //li[@class='clearfix']/p
            doc = Jsoup.connect(url).get();
            ele = doc.select(CSSQUERY).first();
            result = ele.text();
            //去除分号
            if(result.indexOf("；")!=-1){
                result = result.substring(0,result.length()-1);
            }
        }catch (Exception err){
            System.out.println(err.getMessage());
        }
        return result;
    }
    /**
     *判断一个字符是否是中文
     *@param c 字符
     *@return 是否是中文
     **/
    public static boolean isChinese(char c) {
        Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
        if (ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS

                || ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS

                || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A

                || ub == Character.UnicodeBlock.GENERAL_PUNCTUATION

                || ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION

                || ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS) {

            return true;

        }
        return false;
    }
    /**将中文翻译成英文，如果是英文就不需要翻译
    * @param text
     * @return 返回的是中文的英文翻译
     * */
    public static String translateCE2En(String text){
        String translatedText = text;
        if(isChinese(text.charAt(0))){
            translatedText = translateCh2En(text);
        }
        return translatedText;
    }

    public static void main(String[] args){
        String str = "中国";
        if(isChinese(str.charAt(0))){
            System.out.println(translateCh2En(str));
        }
        else {
            System.out.println(str);
        }
    }
}
