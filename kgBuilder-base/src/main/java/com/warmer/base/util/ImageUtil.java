
package com.warmer.base.util;

import org.springframework.web.multipart.MultipartFile;

import java.io.*;

public class ImageUtil {
	
	public static String getFilePath(String userCode)
	{
		return File.separator +userCode;
	}
    /**
    * 保存文件，直接以multipartFile形式
    * @param multipartFile
    * @param path 文件保存绝对路径
    * @return 返回文件名
    * @throws IOException
    */
   public static String saveImg(MultipartFile multipartFile,String path) throws IOException {
       File file = new File(path);
       if (!file.exists()) {
           file.mkdirs();
       }
       InputStream fileInputStream =  multipartFile.getInputStream();
       String fileName = UuidUtil.getUUID() + ".png";
       BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(path + File.separator + fileName));
       byte[] bs = new byte[1024];
       int len;
       while ((len = fileInputStream.read(bs)) != -1) {
           bos.write(bs, 0, len);
       }
       bos.flush();
       bos.close();
       return fileName;
   }
}
