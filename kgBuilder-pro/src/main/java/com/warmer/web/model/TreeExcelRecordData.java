package com.warmer.web.model;

import lombok.Data;

@Data
public class TreeExcelRecordData {
    private String recordId;
    private String classCode;
    private String classPath;
    private String linkName;

    public String getClassPath() {
        if (classPath == null){
            return "";
        }
        return classPath;
    }
    public String getClassCode(){
        if (classCode == null){
            return "";
        }
        return classCode;
    }
}