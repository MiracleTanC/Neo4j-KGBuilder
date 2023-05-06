package com.warmer.web.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.warmer.base.util.ColorInfo;
import com.warmer.base.util.JsonHelper;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;


public class TreeExcel {
    protected Logger log = LoggerFactory.getLogger(getClass());

    public interface IResultHandler {
        TreeExcelRecordData store(String cellVal, String cellColor, TreeExcelRecordData parent, boolean isLeaf);
    }

    private String filePath;
    private InputStream inputStream;
    private final boolean isExcel2003;
    private Workbook wb = null;
    private final IResultHandler resultHandler;

    private String classId = "0";
    private String classCode = "";

    public TreeExcel(String filePath, IResultHandler handler) {
        this.filePath = filePath;
        this.resultHandler = handler;
        isExcel2003 = filePath.matches("^.+\\.(?i)(xls)$");
    }

    public TreeExcel(String classId, String classCode, String filePath, IResultHandler handler) {
        this.classId = classId;
        this.classCode = classCode;

        this.filePath = filePath;
        this.resultHandler = handler;
        isExcel2003 = filePath.matches("^.+\\.(?i)(xls)$");
    }

    public TreeExcel(String classId, String classCode, String fileName, InputStream inputStream, IResultHandler handler) {
        this.classId = classId;
        this.classCode = classCode;
        this.inputStream = inputStream;
        this.resultHandler = handler;
        isExcel2003 = fileName.matches("^.+\\.(?i)(xls)$");
    }

    /**
     * 处理流数据，无需保存临时excel
     */
    public void handleByStream() throws IOException, InvalidFormatException {
        wb = WorkbookFactory.create(inputStream);
        handExcelData(wb);
    }

    /**
     * 处理业务方法，需要保存临时execl副本，供后续业务使用
     */
    public void handleByFile() throws IOException, InvalidFormatException {
        InputStream stream = new FileInputStream(filePath);
        wb = WorkbookFactory.create(stream);
        handExcelData(wb);
    }

    /**
     * 处理excel数据
     */
    private void handExcelData(Workbook wb) throws JsonProcessingException {
        for (int i = 0; i < wb.getNumberOfSheets(); i++) {
            Sheet sheet = wb.getSheetAt(i);
            if (sheet == null || sheet.getLastRowNum() == 0) {
                return;
            }
            Row row = null;
            int totalRow = sheet.getLastRowNum();
            log.info("共有" + totalRow + "行");
            for (int r = 0; r <= sheet.getLastRowNum(); r++) {
                row = sheet.getRow(r);
                if (null == row) {
                    continue;
                }
                short minColIx = row.getFirstCellNum();
                short maxColIx = row.getLastCellNum();
                for (short colIx = minColIx; colIx < maxColIx; colIx++) {
                    Cell cell = row.getCell(colIx);
                    String cellVal = getCellVal(cell);
                    String cellColor = getCellColor(cell);
                    TreeExcelRecordData data = new TreeExcelRecordData();
                    data.setClassCode(this.classCode);
                    data.setRecordId(this.classId);
                    if (colIx > minColIx) {
                        CellModel cellModel = getCellValIfMerged(wb, sheet, row, r, colIx - 1);
                        if (cellModel != null) {
                            String val = cellModel.getFontName();
                            data = JsonHelper.parseObject(val, TreeExcelRecordData.class);
                        }
                    }
                    String recordId = "";
                    boolean isLeaf = colIx == maxColIx - 1;
                    if (StringUtils.isNotBlank(cellVal)) {
                        TreeExcelRecordData result = resultHandler.store(cellVal, cellColor, data, isLeaf);
                        String json = JsonHelper.toJSONString(result);
                        setFont(wb, json, colIx, row);
                    }
                    log.info(String.format("cellVal:%s recordId:%s parentId:%s isLeaf:%s,color:%s", cellVal, recordId, data.getRecordId(), isLeaf, cellColor));
                }

            }
        }
    }


    private String getCellVal(Cell cell) {
        if (cell == null) {
            return "";
        }

        String cellVal = "";
        if (cell.getCellType() == CellType.NUMERIC) {

            java.text.DecimalFormat formatter = new java.text.DecimalFormat("########.####");
            cellVal = formatter.format(cell.getNumericCellValue());

        } else if (cell.getCellType() == CellType.BOOLEAN) {
            cellVal = String.valueOf(cell.getBooleanCellValue());
        } else if (cell.getCellType() == CellType.FORMULA) {
            try {
                cellVal = cell.getStringCellValue();
            } catch (IllegalStateException e) {
                cellVal = String.valueOf(cell.getNumericCellValue());
            }
            System.out.println(cellVal);
        } else {
            cellVal = cell.getStringCellValue();
        }
        return cellVal;
    }

    private String getCellColor(Cell cell) {
        if (cell == null) {
            return "";
        }
        CellStyle cellStyle = cell.getCellStyle();
        ColorInfo bgColor = null;
        if (cellStyle.getFillPattern() == FillPatternType.SOLID_FOREGROUND) {
            bgColor = excelColor2UOF(cellStyle.getFillForegroundColorColor());
        }
        if (bgColor == null) {
            return "";
        }
        if (bgColor.A > 0) {
            return String.format("rgba(%s,%s,%s,%s)", bgColor.R, bgColor.G, bgColor.B, bgColor.A);
        }
        return String.format("rgb(%s,%s,%s)", bgColor.R, bgColor.G, bgColor.B);

    }

    /**
     * excel(包含97和2007)中颜色转化为uof颜色
     *
     * @param color 颜色序号
     * @return 颜色或者null
     */
    private static ColorInfo excelColor2UOF(Color color) {
        if (color == null) {
            return null;
        }
        ColorInfo ci = null;
        if (color instanceof XSSFColor) {// .xlsx
            XSSFColor xc = (XSSFColor) color;
            byte[] b = xc.getRGB();
            //CTColor ctColor = xc.getCTColor();
            if (b != null) {// 一定是argb
                int t = 0;
                if (b[0] < 0) {
                    t = b[0] + 255;
                }
                if (b.length > 3) {
                    ci = ColorInfo.fromARGB(formatColorIndex(b[0]),formatColorIndex(b[1]),formatColorIndex(b[2]), b[3]);
                } else {
                    ci = ColorInfo.fromARGB(formatColorIndex(b[0]),formatColorIndex(b[1]),formatColorIndex(b[2]));
                }
            }
        } else if (color instanceof HSSFColor) {// .xls
            HSSFColor hc = (HSSFColor) color;
            short[] s = hc.getTriplet();// 一定是rgb
            if (s != null) {
                int t = 0;
                if (s[0] < 0) {
                    t = s[0] + 255;
                }
                if (s.length > 3) {
                    ci = ColorInfo.fromARGB(formatColorIndex(s[0]),formatColorIndex(s[1]),formatColorIndex(s[2]), s[3]);
                } else {
                    ci = ColorInfo.fromARGB(formatColorIndex(s[0]),formatColorIndex(s[1]),formatColorIndex(s[2]));
                }
            }
        }
        return ci;
    }

    private static int formatColorIndex(short v) {
        int t = v;
        if (v < 0) {
            t = v + 255;
        }
        return t;
    }

    private void setFont(Workbook wb, String val, int col, Row row) {
        if (isExcel2003) {
            HSSFCellStyle style = (HSSFCellStyle) wb.createCellStyle();
            HSSFFont font = (HSSFFont) wb.createFont();
            font.setFontName(val);
            style.setFont(font);
            row.getCell(col).setCellStyle(style);
        } else {
            XSSFCellStyle style = (XSSFCellStyle) wb.createCellStyle();
            XSSFFont font = (XSSFFont) wb.createFont();
            font.setFontName(val);
            style.setFont(font);
            row.getCell(col).setCellStyle(style);
        }
    }

    private String getFont(Workbook wb, int col, Row row) {
        String name = "";
        if (row == null || row.getCell(col) == null) {
            return "";
        }
        if (isExcel2003) {
            name = ((HSSFCellStyle) row.getCell(col).getCellStyle()).getFont(wb).getFontName();
        } else {
            name = ((XSSFCellStyle) row.getCell(col).getCellStyle()).getFont().getFontName();
        }
        return name;
    }

    private CellModel getCellValIfMerged(Workbook wb, Sheet sheet, Row row, int rowIndex, int col) {
        Cell cell = row.getCell(col);
        String cellVal = getCellVal(cell);
        String cellColor = getCellColor(cell);
        String fontName = getFont(wb, col, row);
        if (!StringUtils.isNotBlank(cellVal)) {
            return getMergedRegionValue(wb, sheet, rowIndex, col);
        }
        return new CellModel(cellVal, fontName, cellColor);
    }

    private CellModel getMergedRegionValue(Workbook wb, Sheet sheet, int row, int column) {
        int sheetMergeCount = sheet.getNumMergedRegions();

        for (int i = 0; i < sheetMergeCount; i++) {
            CellRangeAddress ca = sheet.getMergedRegion(i);
            int firstColumn = ca.getFirstColumn();
            int lastColumn = ca.getLastColumn();
            int firstRow = ca.getFirstRow();
            int lastRow = ca.getLastRow();

            if (row >= firstRow && row <= lastRow) {
                if (column >= firstColumn && column <= lastColumn) {
                    Row fRow = sheet.getRow(firstRow);
                    Cell fCell = fRow.getCell(firstColumn);
                    String fontName = getFont(wb, firstColumn, fRow);
                    String cellVal = getCellVal(fCell);

                    return new CellModel(cellVal, fontName);
                }
            }
        }

        return null;
    }

    class CellModel {
        private String cellVal;
        private String fontName;
        private String cellColor;

        public CellModel(String cellVal, String fontName) {
            this.cellVal = cellVal;
            this.fontName = fontName;
        }

        public CellModel(String cellVal, String fontName, String cellColor) {
            this.cellVal = cellVal;
            this.fontName = fontName;
            this.cellColor = cellColor;
        }

        public String getCellVal() {
            return cellVal;
        }

        public void setCellVal(String cellVal) {
            this.cellVal = cellVal;
        }

        public String getFontName() {
            return fontName;
        }

        public void setFontName(String fontName) {
            this.fontName = fontName;
        }
    }


}
