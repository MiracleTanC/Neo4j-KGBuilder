package com.warmer.web.service.impl;

import com.warmer.base.util.BeanUtils;
import com.warmer.web.dao.KgGraphLinkDao;
import com.warmer.web.dao.KgGraphNodeDao;
import com.warmer.web.entity.KgDomain;
import com.warmer.web.entity.KgGraphLink;
import com.warmer.web.entity.KgGraphNode;
import com.warmer.web.entity.KgGraphNodeMap;
import com.warmer.web.dao.KgGraphNodeMapDao;
import com.warmer.web.request.GraphLinkItem;
import com.warmer.web.request.GraphNodeColumnItem;
import com.warmer.web.request.GraphNodeItem;
import com.warmer.web.request.GraphItem;
import com.warmer.web.service.KgGraphNodeService;
import com.warmer.web.service.KnowledgeGraphService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * (KgGraphNodeMap)表服务实现类
 *
 * @author tanc
 * @since 2021-12-24 15:53:51
 */
@Service
public class KgGraphNodeServiceImpl implements KgGraphNodeService {
    @Autowired
    private KgGraphNodeDao kgGraphNodeDao;
    @Autowired
    private KgGraphNodeMapDao kgGraphNodeMapDao;
    @Autowired
    private KgGraphLinkDao kgGraphLinkDao;
    @Autowired
    private KnowledgeGraphService kgService;
    @Override
    public void createNode(GraphItem submitItem) throws IOException {
        //先删除在保存
        kgGraphNodeDao.deleteByDomainId(submitItem.getDomainId());
        kgGraphNodeMapDao.deleteByDomainId(submitItem.getDomainId());
        kgGraphLinkDao.deleteByDomainId(submitItem.getDomainId());
        List<GraphNodeItem> nodeItems = submitItem.getNodeList();
        List<KgGraphNode> record = BeanUtils.trans(nodeItems, KgGraphNode.class);
        record.stream().map(n->{
            n.setDomainId(submitItem.getDomainId());
            return n;
        }).collect(Collectors.toList());
        kgGraphNodeDao.batchInsert(record);
        for (KgGraphNode kgGraphNode : record) {
            Long nodeId = kgGraphNode.getNodeId();
            String nodeKey = kgGraphNode.getNodeKey();
            Optional<GraphNodeItem> optionalItem = nodeItems.stream().filter(n -> n.getNodeKey().equalsIgnoreCase(nodeKey)).findFirst();
            if (optionalItem.isPresent()) {
                GraphNodeItem columnItem = optionalItem.get();
                List<KgGraphNodeMap> cols = BeanUtils.trans(columnItem.getItems(), KgGraphNodeMap.class);
                List<KgGraphNodeMap> colItems = cols.stream().map(n -> {
                    n.setNodeId(nodeId);
                    n.setDomainId(submitItem.getDomainId());
                    return n;
                }).collect(Collectors.toList());
                kgGraphNodeMapDao.batchInsert(colItems);
            }
        }
        if (submitItem.getLineList() != null && submitItem.getLineList().size() > 0) {
            List<KgGraphLink> links = BeanUtils.trans(submitItem.getLineList(), KgGraphLink.class);
            List<KgGraphLink> linkItems = links.stream().map(n -> {
                n.setDomainId(submitItem.getDomainId());
                return n;
            }).collect(Collectors.toList());
            kgGraphLinkDao.batchInsert(linkItems);
        }
    }
    @Override
    public GraphItem getDomainNode(Integer domainId) throws IOException {
        GraphItem model=new GraphItem();
        KgDomain kgDomain = kgService.selectById(domainId);
        model.setDomainId(kgDomain.getId());
        model.setDomainName(kgDomain.getName());
        List<KgGraphNode> kgGraphNodes = kgGraphNodeDao.selectByDomainId(domainId);
        List<GraphNodeItem> nodeItems = new ArrayList<>();
        for (KgGraphNode nodeItem : kgGraphNodes) {
            GraphNodeItem item=new GraphNodeItem();
            BeanUtils.copyProperties(nodeItem,item);
            List<KgGraphNodeMap> kgGraphNodeMaps = kgGraphNodeMapDao.selectByDomainIdAndNodeId(domainId, nodeItem.getNodeId());
            List<GraphNodeColumnItem> colItems=BeanUtils.trans(kgGraphNodeMaps, GraphNodeColumnItem.class);
            item.setItems(colItems);
            nodeItems.add(item);
        }
        model.setNodeList(nodeItems);
        List<KgGraphLink> kgGraphLinks = kgGraphLinkDao.selectByDomainId(domainId);
        List<GraphLinkItem> linkItems =BeanUtils.trans(kgGraphLinks, GraphLinkItem.class);
        model.setLineList(linkItems);
        return model;
    }
}