package com.warmer.base.util;

import org.apache.poi.openxml4j.opc.OPCPackage;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class ExcelUtil {
	// *************xlsx文件读取函数************************
	// 在jdbc.properties上加上 excelUrl：xlsx文件的目录
	// excel_name为文件名，arg为需要查询的列号(输入数字则返回对应列 , 输入字符串则固定返回这个字符串)
	// 返回
	@SuppressWarnings({ "resource", "unused" })
	public static ArrayList<ArrayList<String>> xlsx_reader(String excel_name, ArrayList<Object> args)
			throws IOException {
		// 读取excel文件夹url

		String excelUrl = "";
		File xlsxFile = new File(excelUrl + excel_name);
		if (!xlsxFile.exists()) {
			System.err.println("Not found or not a file: " + xlsxFile.getPath());
			return null;
		}
		ArrayList<ArrayList<String>> excel_output = new ArrayList<ArrayList<String>>();
		try {
			OPCPackage p;
			// p = OPCPackage.open(xlsxFile.getPath(), PackageAccess.READ);
			/*
			 * XLSX2CSV xlsx2csv = new XLSX2CSV(p, 20); // 20代表最大列数 xlsx2csv.process();
			 * excel_output = xlsx2csv.get_output();
			 */
			// p.close(); //释放
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


		ArrayList<ArrayList<String>> ans = new ArrayList<ArrayList<String>>();
		// 遍历xlsx中的sheet

		// 对于每个sheet，读取其中的每一行
		for (int rowNum = 0; rowNum < excel_output.size(); rowNum++) {
			ArrayList<String> cur_output = excel_output.get(rowNum);
			ArrayList<String> curarr = new ArrayList<String>();
			for (int columnNum = 0; columnNum < args.size(); columnNum++) {
				Object obj = args.get(columnNum);
				if (obj instanceof String) {
					curarr.add(obj.toString());
				} else if (obj instanceof Integer) {
					String cell = cur_output.get((int) obj);
					curarr.add(cell);
				} else {
					System.out.print("类型错误！");
					return null;
				}
			}
			ans.add(curarr);
		}

		return ans;
	}

	// // 判断后缀为xlsx的excel文件的数据类
	// @SuppressWarnings("deprecation")
	// private static String getValue(XSSFCell xssfRow) {
	// if (xssfRow == null) {
	// return null;
	// }
	// if (xssfRow.getCellType() == xssfRow.CELL_TYPE_BOOLEAN) {
	// return String.valueOf(xssfRow.getBooleanCellValue());
	// } else if (xssfRow.getCellType() == xssfRow.CELL_TYPE_NUMERIC) {
	// double cur = xssfRow.getNumericCellValue();
	// long longVal = Math.round(cur);
	// Object inputValue = null;
	// if (Double.parseDouble(longVal + ".0") == cur)
	// inputValue = longVal;
	// else
	// inputValue = cur;
	// return String.valueOf(inputValue);
	// } else if (xssfRow.getCellType() == xssfRow.CELL_TYPE_BLANK
	// || xssfRow.getCellType() == xssfRow.CELL_TYPE_ERROR) {
	// return "";
	// } else {
	// return String.valueOf(xssfRow.getStringCellValue());
	// }
	// }
	public static boolean isExcel2003(String filePath) {
		return filePath.matches("^.+\\.(?i)(xls)$");
	}

	public static boolean isExcel2007(String filePath) {
		return filePath.matches("^.+\\.(?i)(xlsx)$");
	}
}
