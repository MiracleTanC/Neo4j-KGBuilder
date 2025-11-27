package com.warmer.web.service;

import com.warmer.web.entity.KgFeedBack;

import java.util.List;

/**
 * 反馈记录管理服务接口
 *
 * 提供反馈的新增、删除、主键查询与条件查询能力。
 */
public interface FeedBackService {
    /**
     * 根据主键删除反馈
     * @param id 主键ID
     * @return 影响行数
     */
    int deleteByPrimaryKey(Integer id);

    /**
     * 新增反馈记录
     * @param record 反馈实体
     * @return 影响行数
     */
    int insert(KgFeedBack record);

    /**
     * 根据主键查询反馈
     * @param id 主键ID
     * @return 反馈实体
     */
    KgFeedBack selectByPrimaryKey(Integer id);

    /**
     * 条件查询反馈列表
     * @param queryItem 查询条件
     * @return 反馈列表
     */
    List<KgFeedBack> queryForList(KgFeedBack queryItem);

}
