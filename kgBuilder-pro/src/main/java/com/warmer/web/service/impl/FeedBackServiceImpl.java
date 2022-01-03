package com.warmer.web.service.impl;

import com.warmer.web.dao.FeedBackDao;
import com.warmer.web.entity.*;
import com.warmer.web.service.FeedBackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;



@Service
public class FeedBackServiceImpl implements FeedBackService {
    @Autowired
    private FeedBackDao feedBackDao;


    @Override
    public int deleteByPrimaryKey(Integer id) {
        return feedBackDao.deleteByPrimaryKey(id);
    }

    @Override
    public int insert(KgFeedBack record) {
        return feedBackDao.insert(record);
    }

    @Override
    public KgFeedBack selectByPrimaryKey(Integer id) {
        return feedBackDao.selectByPrimaryKey(id);
    }

    @Override
    public List<KgFeedBack> queryForList(KgFeedBack queryItem) {
        return feedBackDao.queryForList(queryItem);
    }
}