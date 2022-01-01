package com.warmer.web.dao;

import com.warmer.web.entity.KgGraphLink;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface KgGraphLinkDao {
    List<KgGraphLink> selectByDomainId(Integer domainId);
    int insert(KgGraphLink record);
    int batchInsert(List<KgGraphLink> records);
    int deleteByDomainId(Integer domainId);
}