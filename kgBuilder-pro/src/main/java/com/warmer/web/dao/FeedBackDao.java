package com.warmer.web.dao;

import com.warmer.web.entity.KgFeedBack;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
/**
 * 反馈信息数据访问接口
 *
 * 提供反馈记录的新增、删除、主键查询与条件查询方法。
 */
public interface FeedBackDao {
    /**
     * 根据主键删除反馈记录
     * @param id 主键ID
     * @return 受影响行数
     */
    int deleteByPrimaryKey(Integer id);

    /**
     * 新增反馈记录
     * @param record 反馈实体
     * @return 受影响行数
     */
    int insert(KgFeedBack record);

    /**
     * 根据主键查询反馈记录
     * @param id 主键ID
     * @return 反馈实体
     */
    KgFeedBack selectByPrimaryKey(@Param("id") Integer id);

    /**
     * 条件查询反馈记录列表
     * @param queryItem 查询条件
     * @return 反馈列表
     */
    List<KgFeedBack> queryForList(KgFeedBack queryItem);

}
