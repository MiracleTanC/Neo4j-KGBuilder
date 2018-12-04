package com.warmer.kgmaker.controller;


import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.alibaba.fastjson.JSONObject;
import com.warmer.kgmaker.config.WebAppConfig;
import com.warmer.kgmaker.util.FileResponse;
import com.warmer.kgmaker.util.FileResult;
import com.warmer.kgmaker.util.ImageUtil;
import com.warmer.kgmaker.util.QiniuUploadService;


@Controller
@RequestMapping("/")
public class FileController extends BaseController {
	@Autowired
	private WebAppConfig appConfig;
	@Autowired
	private QiniuUploadService qiniuUploadService;
	
	@PostMapping("/img/upload")
	@ResponseBody
	public FileResponse uploadImg(HttpServletRequest req) {
		FileResponse res = new FileResponse();
		List<FileResult> fre = new ArrayList<FileResult>();
		List<MultipartFile> files = ((MultipartHttpServletRequest) req).getFiles("file");
		// 获取路径
		//String userCode = AppUserUtil.GetUserCode();
		//String return_path = ImageUtil.getFilePath(userCode);
		String filePath = appConfig.getLocation();

		try {
			for (MultipartFile file : files) {
				FileResult fileResult = new FileResult();
				String contentType = file.getContentType();
				String root_fileName = file.getOriginalFilename();
				if (!contentType.contains("")) {
					String error = "上传的文件类型有误";
					log.error(error);
					fileResult.setMessage(error);
					fileResult.setName(root_fileName);
					fileResult.setStatus(0);
				}
				log.info("上传图片:name={},type={}", root_fileName, contentType);
				log.info("图片保存路径={}", filePath);
				String file_name = null;
				file_name = ImageUtil.saveImg(file, filePath);

				if (StringUtils.isNotBlank(file_name)) {
					String success = "上传成功";
					log.info(success);
					fileResult.setMessage(success);
					fileResult.setName(root_fileName);
					fileResult.setStatus(0);
					String src="/img/download/";
					fileResult.setUrl(src + file_name);
					fre.add(fileResult);
				}
				log.info("返回值：{}", fileResult);
			}
			res.setSuccess(1);
			res.setMessage("ok");
			res.setResults(fre);

		} catch (IOException e) {
			log.error(e.getMessage());
		}
		return res;
	}
	//文件下载相关代码  
    @GetMapping(value = "/img/download/{imageName}")  
    public String downloadImage(@PathVariable("imageName")String imageName,HttpServletRequest request, HttpServletResponse response) {  
    	//String userCode = AppUserUtil.GetUserCode();
		//String return_path = ImageUtil.getFilePath(userCode);
		String filePath = appConfig.getLocation() ;
        String fileUrl = filePath+ File.separator + imageName;  
        if (fileUrl != null) {  
            File file = new File(fileUrl);  
            if (file.exists()) {  
                //response.setContentType("application/force-download");// 设置强制下载不打开  
                response.addHeader("Content-Disposition",  
                        "attachment;fileName=" + imageName);// 设置文件名  
                byte[] buffer = new byte[1024];  
                FileInputStream fis = null;  
                BufferedInputStream bis = null;  
                try {  
                    fis = new FileInputStream(file);  
                    bis = new BufferedInputStream(fis);  
                    OutputStream os = response.getOutputStream();  
                    int i = bis.read(buffer);  
                    while (i != -1) {  
                        os.write(buffer, 0, i);  
                        i = bis.read(buffer);  
                    }  
                    System.out.println("success");  
                } catch (Exception e) {  
                    e.printStackTrace();  
                } finally {  
                    if (bis != null) {  
                        try {  
                            bis.close();  
                        } catch (IOException e) {  
                            e.printStackTrace();  
                        }  
                    }  
                    if (fis != null) {  
                        try {  
                            fis.close();  
                        } catch (IOException e) {  
                            e.printStackTrace();  
                        }  
                    }  
                }  
            }  
        }  
        return null;  
    }  

    @PostMapping("/qiniu/upload")
	@ResponseBody
	public FileResponse qiniuUploadImg(HttpServletRequest req,HttpServletResponse response) throws FileUploadException {
    	FileResponse res = new FileResponse();
		List<FileResult> fre = new ArrayList<FileResult>();
		List<MultipartFile> files = ((MultipartHttpServletRequest) req).getFiles("file");
		try {
			for (MultipartFile file : files) {
				String fileName = file.getOriginalFilename();
				String url="http://"+qiniuUploadService.uploadImage(file,fileName);
				FileResult fileResult = new FileResult();
				if (StringUtils.isNotBlank(url)) {
					String success = "上传成功";
					fileResult.setMessage(success);
					fileResult.setName(fileName);
					fileResult.setStatus(0);
					fileResult.setUrl(url);
					fre.add(fileResult);
				}
			}
			response.setHeader("X-Frame-Options", "SAMEORIGIN");// 解决IFrame拒绝的问题
			res.setSuccess(1);
			res.setMessage("ok");
			res.setResults(fre);

		} catch (FileUploadException e) {
			log.error(e.getMessage());
		}
		return res;
    }
    @RequestMapping("/qiniu/editormdupload")
    @ResponseBody
    public JSONObject editormdPic (@RequestParam(value = "editormd-image-file", required = true) MultipartFile file, HttpServletRequest request,HttpServletResponse response) throws Exception{ 
            String fileName = file.getOriginalFilename();
			String url="http://"+qiniuUploadService.uploadImage(file,fileName);

            JSONObject res = new JSONObject();
            res.put("url", url);
            res.put("success", 1);
            res.put("message", "upload success!");

            return res;

        }
}