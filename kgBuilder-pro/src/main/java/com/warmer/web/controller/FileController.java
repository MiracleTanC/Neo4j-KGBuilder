package com.warmer.web.controller;

import cn.hutool.core.util.IdUtil;
import com.warmer.base.util.FileResponse;
import com.warmer.base.util.FileResult;
import com.warmer.web.config.WebAppConfig;
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
import java.util.List;


@Controller
@RequestMapping("/file")
public class FileController extends BaseController {
	@Autowired
	private WebAppConfig appConfig;


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
				String rootFileName = file.getOriginalFilename();
				String suffix= rootFileName.substring(rootFileName.lastIndexOf("."));
				String fileName =filePath+ IdUtil.getSnowflakeNextIdStr()+"."+suffix;
				File saveFile = new File(fileName);
				file.transferTo(saveFile);        // 文件保存
				if (StringUtils.isNotBlank(fileName)) {
					String success = "上传成功";
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


}