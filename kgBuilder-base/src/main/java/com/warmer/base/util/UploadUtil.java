package com.warmer.base.util;

import org.apache.commons.fileupload.FileUploadException;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

public interface UploadUtil {
	String uploadFile(MultipartFile multipartFile) throws FileUploadException;

	String uploadFile(String filePath, MultipartFile multipartFile) throws FileUploadException;

	String uploadFile(MultipartFile multipartFile, String fileName) throws FileUploadException;

	String uploadFile(MultipartFile multipartFile, String fileName, String filePath) throws FileUploadException;

	String uploadFile(File file) throws FileUploadException;

	String uploadFile(String filePath, File file) throws FileUploadException;

	String uploadFile(File file, String fileName) throws FileUploadException;

	String uploadFile(File file, String fileName, String filePath) throws FileUploadException, FileUploadException;

	String uploadFile(byte[] data) throws FileUploadException;

	String uploadFile(String filePath, byte[] data) throws FileUploadException;

	String uploadFile(byte[] data, String fileName) throws FileUploadException;

	String uploadFile(byte[] data, String fileName, String filePath) throws FileUploadException;

}
