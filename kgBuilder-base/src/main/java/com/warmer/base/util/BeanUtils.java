package com.warmer.base.util;

import com.github.pagehelper.PageInfo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class BeanUtils {

    public static <T> T trans(Object sourceObject, Class<T> clazz) throws IOException {
        return JsonHelper.parseObject(JsonHelper.toJSONString(sourceObject), clazz);
    }

    public static <T> List<T> trans(List list, Class<T> clazz) throws IOException {
        ArrayList result = new ArrayList();
        for (Object item : list) {
            result.add(trans(item, clazz));
        }
        return result;
    }

    public static void copyProperties(Object source, Object target) {
        org.springframework.beans.BeanUtils.copyProperties(source, target);
    }

    public static <T> PageInfo<T> transPage(PageInfo info, Class<T> clazz) throws IOException {
        PageInfo<T> pageInfo = new PageInfo();
        copyProperties(info, pageInfo);
        List<T> trans = trans(info.getList(), clazz);
        pageInfo.setList(trans);
        return pageInfo;
    }

}
