package com.warmer.web.service;

import com.warmer.web.entity.KgFeedBack;

import java.util.List;

public interface FeedBackService {
    int deleteByPrimaryKey(Integer id);

    int insert(KgFeedBack record);

    KgFeedBack selectByPrimaryKey(Integer id);

    List<KgFeedBack> queryForList(KgFeedBack queryItem);

}
