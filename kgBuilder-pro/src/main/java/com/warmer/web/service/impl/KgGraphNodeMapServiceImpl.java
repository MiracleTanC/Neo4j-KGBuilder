package com.warmer.web.service.impl;

import com.warmer.web.dao.KgGraphNodeMapDao;
import com.warmer.web.entity.KgGraphNodeMap;
import com.warmer.web.service.KgGraphNodeMapService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * (KgGraphNodeMap)表服务接口
 *
 * @author tanc
 * @since 2021-12-24 15:53:51
 */
@Service
public class KgGraphNodeMapServiceImpl implements KgGraphNodeMapService {

    @Autowired
    KgGraphNodeMapDao kgGraphNodeMapDao;

    @Override
    public List<KgGraphNodeMap> queryById(Integer id) {
        return kgGraphNodeMapDao.selectByDomainId(id);
    }

    @Override
    public Integer insert(KgGraphNodeMap kgGraphNodeMap) {
        return kgGraphNodeMapDao.insert(kgGraphNodeMap);
    }

}