package com.warmer.web.dao;

import com.warmer.web.entity.KgGraphNode;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface KgGraphNodeDao {
    List<KgGraphNode> selectByDomainId(Integer domainId);

    int insert(KgGraphNode record);

    int batchInsert(List<KgGraphNode> record);

    int deleteByDomainId(Integer domainId);
}