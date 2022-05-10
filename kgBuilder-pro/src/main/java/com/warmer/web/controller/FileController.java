package com.warmer.web.controller;

import com.warmer.base.util.*;
import com.warmer.web.config.WebAppConfig;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Controller
@RequestMapping("/file")
public class FileController extends BaseController {
	@Autowired
	private WebAppConfig appConfig;
	@Autowired
	private QiniuUploadService qiniuUploadService;

	@PostMapping("/upload")
	@ResponseBody
	public FileResponse upload(HttpServletRequest req) {
		FileResponse res = new FileResponse();
		List<FileResult> fre = new ArrayList<FileResult>();
		List<MultipartFile> files = ((MultipartHttpServletRequest) req).getFiles("file");
		String filePath = appConfig.getLocation();
		try {
			for (MultipartFile file : files) {
				FileResult fileResult = new FileResult();
				//String contentType = file.getContentType();
				String rootFileName = file.getOriginalFilename();
				String fileName = ImageUtil.saveImg(file, filePath);
				if (StringUtils.isNotBlank(fileName)) {
					String success = "上传成功";
					log.info(success);
					fileResult.setMessage(success);
					fileResult.setName(rootFileName);
					fileResult.setStatus(0);
					String src="/file/download/";
					fileResult.setUrl(src + fileName);
					fre.add(fileResult);
				}
			}
			res.setSuccess(1);
			res.setMessage("操作成功");
			res.setResults(fre);

		} catch (IOException e) {
			log.error(e.getMessage());
		}
		return res;
	}
	//文件下载相关代码
	@GetMapping(value = "/download/{fileName}")
	public String downloadImage(@PathVariable("fileName")String fileName, HttpServletResponse response) {
		String filePath = appConfig.getLocation() ;
		String fileUrl = filePath+ File.separator + fileName;
		String[] arr = fileName.split("\\.");
		String suffix = arr[1];
		File file = new File(fileUrl);

		if (file.exists()) {
			//response.setContentType("application/force-download");// 设置强制下载不打开
			response.addHeader("Content-Disposition","attachment;fileName=" + fileName);// 设置文件名
			byte[] buffer = new byte[1024];
			FileInputStream fis = null;
			BufferedInputStream bis = null;
			try {
				fis = new FileInputStream(file);
				bis = new BufferedInputStream(fis);
				OutputStream os = response.getOutputStream();
				if("csv".equalsIgnoreCase(suffix)){
					//加上UTF-8文件的标识字符,避免乱码
					os.write(new   byte []{( byte ) 0xEF ,( byte ) 0xBB ,( byte ) 0xBF });
				}
				int i = bis.read(buffer);
				while (i != -1) {
					os.write(buffer, 0, i);
					i = bis.read(buffer);
				}
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
		return null;
	}

	@PostMapping("/qiniuUpload")
	@ResponseBody
	public FileResponse qiniuUpload(HttpServletRequest req,HttpServletResponse response){
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

}