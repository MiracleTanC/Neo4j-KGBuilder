package com.warmer.base.util;

import com.csvreader.CsvWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 
 * @author ReverseG
 *
 */

public class CSVUtil {
	protected static Logger log = LoggerFactory.getLogger(CSVUtil.class);

	public static void createCsvFile(List<List<String>> list, String path,String fileName) {
		String fileNm=path+fileName;
		log.info("CSVUtil->createFile方法开始. " + fileNm);
		File dir=new File(path);
		if(!dir.exists())
			dir.mkdirs();
		CsvWriter csvWriter = new CsvWriter(fileNm, ',', Charset.forName("UTF-8"));
		int rowSize = list.size();
		int colSize = list.get(0).size();
		for (int i = 0; i < rowSize; i++) {
			List<String> lst = list.get(i);
			String[] cntArr = new String[colSize];
			for (int j = 0; j < colSize; j++) {
				cntArr[j] = lst.get(j);
			}
			try {
				csvWriter.writeRecord(cntArr);
			} catch (IOException e) {
				log.error("CSVUtil->createFile: 文件输出异常" + e.getMessage());
			}
		}

		csvWriter.close();

		log.info("CSVUtil->createFile方法结束. " + fileNm);

	}

	public static List<List<String>> readCsvFile(MultipartFile file) {
		log.info("CSVUtil->readCsvFile方法开始. ");
		try {
			List<List<String>> rowList = new ArrayList<List<String>>();
			try {
				String charset = "utf-8";
				BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream(),charset));
				 String line = null;
				 while ((line = reader.readLine()) != null) {   
				    	String[] rowArr = line.split(",");//CSV格式文件为逗号分隔符文件，这里根据逗号切分
				    	List<String> row = Arrays.asList(rowArr);
				    	rowList.add(row);
		         }
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			return rowList;
		} catch (Exception e) {
			e.printStackTrace();
		}
		log.info("CSVUtil->readCsvFile方法结束. ");
		return null;
	}

	public static List<String> readCsvHead(MultipartFile file) {
		log.info("CSVUtil->readCsvFile方法开始. ");
		try {
			List<String> rowList = new ArrayList<>();
			try {
				String charset = "utf-8";
				BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream(),charset));
				String line = null;
				if ((line = reader.readLine()) != null) {
					String[] rowArr = line.split(",");//CSV格式文件为逗号分隔符文件，这里根据逗号切分
					rowList = Arrays.asList(rowArr);
				}
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			return rowList;
		} catch (Exception e) {
			e.printStackTrace();
		}
		log.info("CSVUtil->readCsvHead方法结束. ");
		return null;
	}

	public static void excelTocsv(String filePath) {
		log.info("CSVUtil->createFile方法开始. ");

		log.info("CSVUtil->createFile方法结束. ");

	}
	// public static void main(String[] args) throws IOException {
	// List<List<String>> list = new ArrayList<List<String>>();
	// List<String> lst = null;
	// lst = new ArrayList<String>();
	// for (int i=1; i<4; i++) {
	// lst.add("第" + i + "列");
	// }
	// list.add(lst);
	//
	// for (int j=0; j<3; j++) {
	// lst = new ArrayList<String>();
	// for (int i=4; i<7; i++) {
	// lst.add(j + i + "");
	// }
	// list.add(lst);
	// }
	//
	// createFile(list, "csvDemo.csv");
	// }
}
