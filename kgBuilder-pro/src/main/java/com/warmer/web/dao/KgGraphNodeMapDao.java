package com.warmer.web.dao;

import com.warmer.web.entity.KgGraphNodeMap;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface KgGraphNodeMapDao {

    int insert(KgGraphNodeMap record);

    int batchInsert(List<KgGraphNodeMap> records);

    int deleteByDomainId(Integer domainId);

    List<KgGraphNodeMap> selectByDomainId(Integer domainId);

    List<KgGraphNodeMap> selectByDomainIdAndNodeId(Integer domainId,Long nodeId);
}