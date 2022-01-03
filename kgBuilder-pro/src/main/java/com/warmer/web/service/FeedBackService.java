package com.warmer.web.service;

import com.warmer.web.entity.KgFeedBack;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface FeedBackService {
    int deleteByPrimaryKey(Integer id);

    int insert(KgFeedBack record);

    KgFeedBack selectByPrimaryKey(Integer id);

    List<KgFeedBack> queryForList(KgFeedBack queryItem);

}
