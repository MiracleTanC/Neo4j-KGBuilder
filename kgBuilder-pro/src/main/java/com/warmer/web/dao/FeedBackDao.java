package com.warmer.web.dao;

import com.warmer.web.entity.KgFeedBack;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface FeedBackDao {
    int deleteByPrimaryKey(Integer id);

    int insert(KgFeedBack record);

    KgFeedBack selectByPrimaryKey(@Param("id") Integer id);

    List<KgFeedBack> queryForList(KgFeedBack queryItem);

}
