package com.warmer.base.util;

import org.apache.commons.lang3.ObjectUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class StringUtil {
	 /*
	 * 是否为空字符串
     * @param str
     * @return
     */
    public static boolean isBlank(String str){
        int strLen;
        if (str == null || (strLen = str.length()) == 0) {
            return true;
        }
        for (int i = 0; i < strLen; i++) {
            if (Character.isWhitespace(str.charAt(i)) == false) {
                return false;
            }
        }
        return true;
    }

    public static boolean isNotBlank(String str){
        return !isBlank(str);
    }
    /**
     * 连接方法 类似于javascript
     * @param join 连接字符串
     * @param strAry 需要连接的集合
     * @return
     */
    public static String join(String join,String[] strAry){
        StringBuffer sb=new StringBuffer();
        for(int i=0,len =strAry.length;i<len;i++){
            if(i==(len-1)){
                sb.append(strAry[i]);
            }else{
                sb.append(strAry[i]).append(join);
            }
        }
        return sb.toString();
    }

    /**
     * 将结果集中的一列用指定字符连接起来
     * @param join 指定字符
     * @param cols 结果集
     * @param colName 列名
     * @return
     */
    public static String join(String join,List<Map> cols,String colName){
        List<String> aColCons = new ArrayList<String>();
        for (Map map:
             cols) {
            aColCons.add(ObjectUtils.toString(map.get(colName)));
        }
        return join(join,aColCons);
    }

    public static String join(String join,List<String> listStr){
        StringBuffer sb=new StringBuffer();
        for(int i=0,len =listStr.size();i<len;i++){
            if(i==(len-1)){
                sb.append(listStr.get(i));
            }else{
                sb.append(listStr.get(i)).append(join);
            }
        }
        return sb.toString();
    }
}
