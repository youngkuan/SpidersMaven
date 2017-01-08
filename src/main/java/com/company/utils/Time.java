package com.company.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**  
 * 类说明   
 *  
 * @author 郑元浩 
 * @date 2016年11月25日
 */
public class Time {

	public static void main(String[] args) {
		getCertainTime("2008-07-10 19:20:00");
	}
	
	public static String getSystemTime(){
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
		String time = df.format(new Date());// new Date()为获取当前系统时间
//		Log.log(time);
		return time;
	}
	
	public static String getCertainTime(String dateTime){
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
		Date date = null;
		try {
			date = df.parse(dateTime);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		String time = df.format(date);// new Date()为获取当前系统时间
		Log.log(time);
		return time;
	}

}
