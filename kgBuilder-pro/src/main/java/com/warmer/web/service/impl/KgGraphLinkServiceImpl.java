package com.warmer.web.service.impl;

import com.warmer.web.dao.KgGraphLinkDao;
import com.warmer.web.dao.KgGraphNodeMapDao;
import com.warmer.web.entity.KgGraphLink;
import com.warmer.web.service.KgGraphLinkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * (KgGraphLink)表服务接口
 *
 * @author tanc
 * @since 2021-12-24 15:53:53
 */
@Service
public class KgGraphLinkServiceImpl implements KgGraphLinkService {

    @Autowired
    KgGraphLinkDao GraphLinkDao;
    @Override
    public List<KgGraphLink> queryById(Integer domainId) {
        return GraphLinkDao.selectByDomainId(domainId);
    }

    @Override
    public Integer insert(KgGraphLink kgGraphLink) {
        return GraphLinkDao.insert(kgGraphLink);
    }
}