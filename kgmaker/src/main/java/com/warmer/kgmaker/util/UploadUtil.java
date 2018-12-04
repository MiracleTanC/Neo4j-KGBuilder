package com.warmer.kgmaker.util;

import java.io.File;

import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.springframework.web.multipart.MultipartFile;

public interface UploadUtil {
	String uploadFile(MultipartFile multipartFile) throws FileUploadException;

	String uploadFile(String filePath, MultipartFile multipartFile) throws FileUploadException;

	String uploadFile(MultipartFile multipartFile, String fileName) throws FileUploadException;

	String uploadFile(MultipartFile multipartFile, String fileName, String filePath) throws FileUploadException;

	String uploadFile(File file) throws FileUploadException;

	String uploadFile(String filePath, File file) throws FileUploadException;

	String uploadFile(File file, String fileName) throws FileUploadException;

	String uploadFile(File file, String fileName, String filePath) throws FileUploadException;

	String uploadFile(byte[] data) throws FileUploadException;

	String uploadFile(String filePath, byte[] data) throws FileUploadException;

	String uploadFile(byte[] data, String fileName) throws FileUploadException;

	String uploadFile(byte[] data, String fileName, String filePath) throws FileUploadException;

}
