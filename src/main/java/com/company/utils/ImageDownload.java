package com.company.utils;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
/**
 * 从网上下载和读取图片的基本操作
 * @author 郑元浩
 *
 */
public class ImageDownload {

	/**
	 * 得到图片链接对应的输入流，用于数据库Blob图片字段的存储
	 * @param imageUrl
	 * @return
	 */
	public static InputStream getImageFromNetByUrl(String imageUrl) {
		InputStream inStream = null;
		try {
			URL url = new URL(imageUrl);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.setDoInput(true); // 设置应用程序要从网络连接读取数据
			conn.setConnectTimeout(3000); // 设置网络连接超时时间
			int responseCode = 0;
			try {
				responseCode = conn.getResponseCode();
			} catch (Exception e) {
				responseCode = 0;
				// e.printStackTrace();
			}

			if (responseCode == 200) {
				// 从服务器返回一个输入流
				inStream = conn.getInputStream();
			} else if (responseCode == 301) {
				System.out.println("301 error, 请求的网页已永久移动到新位置...");
			} else if (responseCode == 503) {
				System.out
						.println("503 error, 服务器不支持当前请求所需要的某个功能。当服务器无法识别请求的方法，并且无法支持其对任何资源的请求。...");
			} else if (responseCode == 0) {
				System.out.println("0 eroor, connect timed out...");
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return inStream;
	}

//	// 读取表中图片获取输出流
//	public static void readBin2Image(InputStream in, String targetPath) {
//		File file = new File(targetPath);
//		String path = targetPath.substring(0, targetPath.lastIndexOf("/"));
//		if (!file.exists()) {
//			new File(path).mkdir();
//		}
//		FileOutputStream fos = null;
//		try {
//			fos = new FileOutputStream(file);
//			int len = 0;
//			byte[] buf = new byte[1024];
//			while ((len = in.read(buf)) != -1) {
//				fos.write(buf, 0, len);
//			}
//			fos.flush();
//		} catch (Exception e) {
//			e.printStackTrace();
//		} finally {
//			if (null != fos) {
//				try {
//					fos.close();
//				} catch (IOException e) {
//					e.printStackTrace();
//				}
//			}
//		}
//	}

}
