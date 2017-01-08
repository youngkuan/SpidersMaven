package com.company.utils;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Random;

/**  
 * 1. 将二进制流保存到本地文件html
 * 2. 产生随机数
 *  
 * @author 郑元浩 
 * @date 2016年11月26日
 */
public class SaveHtml {

	public static void main(String[] args) {

	}
	
	/**
	 * 保存字符串流到本地html文件
	 * @param filePath
	 * @param str
	 */
	public static void saveHtml(String filePath, String str) {
		try {
			OutputStreamWriter outs = new OutputStreamWriter(new FileOutputStream(filePath, true), "utf-8");
			outs.write(str);
			outs.close();
		} catch (IOException e) {
			Log.log("Error at save html...");
			e.printStackTrace();
		}
	}
	
	/**
	 * 输入流转为字符串流
	 * @param in_str
	 * @param charset
	 */
    public static String inStream2String(InputStream in_st,String charset) throws IOException{
        BufferedReader buff = new BufferedReader(new InputStreamReader(in_st, charset));
        StringBuffer res = new StringBuffer();
        String line = "";
        while((line = buff.readLine()) != null){
            res.append(line);
        }
        return res.toString();
    }
    
    /**
     * 产生大小在两个整数区间内的随机数
     * @param min
     * @param max
     * @return
     */
    public static int random(int min, int max){
		Random random = new Random();
		int s = random.nextInt(max)%(max-min+1) + min;
		return s;
	}
    
    /**
	 * 处理文件名，使其成为文件名可以保存的格式
	 * @param str
	 */
	public static String txtdeal(String str){
		Boolean a = str.matches("[^/\\\\<>*?|\"]+\\.[^/\\\\<>*?|\"]+");
		if (!a) {
//			Log.log("文件名不规范:" + str);
			str = str.replace("*", "-");
			str = str.replace("/", "-");
			str = str.replace("|", "-");
			str = str.replace("<", "-");
			str = str.replace(">", "-");
			str = str.replace("?", "-");
			str = str.replace("\"", "-");
			str = str.replace("\\", "-");
			str = str.replace(":", "-");
			str = str.replace("\"", "-");
//			Log.log("修改后文件名:" + str);
		} else {
//			Log.log("文件名规范...");
		}
		return str;
	}
	
}
