package com.warmer.base.util;

import com.qiniu.util.Auth;
import org.apache.commons.fileupload.FileUploadException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.multipart.MultipartFile;

@Configuration
public class QiniuUploadService {
	//引入第一步的七牛配置
    @Value("${qiniu.access.key}")
    private String accesskey;

    @Value("${qiniu.secret.key}")
    private String secretKey;

    @Value("${qiniu.bucket.name}")
    private String bucketName;

    @Value("${qiniu.bucket.host.name}")
    private String bucketHostName;
    
    @Value("${qiniu.prefixName}")
    private String prefixName;
    //@Bean
    public String uploadImage(MultipartFile image,String fileName) throws FileUploadException {
    	Auth auth = Auth.create(this.accesskey, this.secretKey);
    	UploadUtil qiniuUtil = new QiniuUtil(this.bucketHostName, this.bucketName,auth);
        //return qiniuUtil.uploadFile(image,fileName);
        return qiniuUtil.uploadFile(this.prefixName,image);
    }
}
