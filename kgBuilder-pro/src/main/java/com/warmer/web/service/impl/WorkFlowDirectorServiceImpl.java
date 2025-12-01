package com.warmer.web.service.impl;


import com.github.pagehelper.util.StringUtil;
import com.warmer.base.common.PageRecord;
import com.warmer.base.util.DbUtils;
import com.warmer.base.util.Neo4jUtil;
import com.warmer.meta.entity.MetaDataSource;
import com.warmer.meta.entity.MetaDataTable;
import com.warmer.meta.service.MetaDataColumnService;
import com.warmer.meta.service.MetaDataSourceService;
import com.warmer.meta.service.MetaDataTableService;
import com.warmer.meta.vo.DataColumnVo;
import com.warmer.web.domain.ComponentContainer;
import com.warmer.web.domain.DataLink;
import com.warmer.web.domain.DataNode;
import com.warmer.web.entity.KgDomain;
import com.warmer.web.request.GraphItem;
import com.warmer.web.request.GraphLinkItem;
import com.warmer.web.request.GraphNodeColumnItem;
import com.warmer.web.request.GraphNodeItem;
import com.warmer.web.service.AbstractWorkFlowService;
import com.warmer.web.service.IWorkFlowDirectorService;
import com.warmer.web.service.KgGraphNodeService;
import com.warmer.web.service.KGManagerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 流程导演服务类
 */
@Slf4j
@Service
@Component
@Configuration
public class WorkFlowDirectorServiceImpl implements IWorkFlowDirectorService {

    @Autowired
    MetaDataSourceService metaDataSourceService;
    @Autowired
    MetaDataTableService metaDataTableService;
    @Autowired
    MetaDataColumnService metaDataColumnService;
    @Autowired
    private KGManagerService kgService;
    @Autowired
    private KgGraphNodeService kgGraphNodeService;

    @Autowired
    private Map<String, AbstractWorkFlowService> workFlowServices;

    /**
     * 导演方法。
     * 从config json串中提取组件和连接信息
     * 然后依次执行执行流程
     * 1. 先执行开始节点组件，
     * 2. 根据last pre nodes是否执行完毕，调用后置组件服务
     *
     * @param domainId 数据分析配置id
     */
    @Override
    public void direct(int domainId) {
        long startTime = System.currentTimeMillis();
        log.info("进入-->主线程方法,开始执行,开始时间：" + startTime);
        try {
            KgDomain kgDomain = kgService.selectById(domainId);
            if (kgDomain == null) {
                log.error("找不到配置的信息：domainId=" + domainId);
                return;
            }
            GraphItem graphItem = kgGraphNodeService.getDomainNode(domainId);
            //解析组件
            ComponentContainer componentContainer = explainComponentConfig(graphItem);
            //处理起始节点
            log.info("头结点数量：" + componentContainer.getStartNodes().size());
            componentContainer.getStartNodes().forEach(node -> {
                log.info("检查头结点");
                AbstractWorkFlowService service = workFlowServices.get(node.getNodeCode());
                if (service != null) {
                    service.process(node);
                } else {
                    log.error("未找到对应类型的服务: {}", node.getNodeCode());
                }
                componentContainer.markExecutedDone(node);
            });
            //获取除起始节点外的所有节点
            List<String> targetIds = componentContainer.getTargetIds();
            boolean isContinue = true;
            while (isContinue) {
                //处理目标节点
                for (String id : targetIds) {
                    //查看前置节点是否执行完毕
                    boolean isExecutable = componentContainer.isPreNodesExecuted(id);
                    if (isExecutable) {
                        //找到后置节点，执行该节点
                        DataNode node = componentContainer.getDataNodeByNodeId(id);
                        //本身没有执行
                        if (!node.isExecuted()) {
                            AbstractWorkFlowService service = workFlowServices.get(node.getNodeCode());
                            if (service != null) {
                                service.process(node);
                            } else {
                                log.error("未找到对应类型的服务: {}", node.getNodeCode());
                            }
                            componentContainer.markExecutedDone(node);
                            break;
                        }
                    }
                }
                //等待所有节点执行完成，结束
                isContinue = !componentContainer.isAllExecuted();
            }
            //创建关系
            List<DataLink> dataLinkList = componentContainer.getDataLinkList();
            for (DataLink dataLink : dataLinkList) {
                createGraphLink(kgDomain.getName(),dataLink);
            }

        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }

        long endTime = System.currentTimeMillis();
        long totalTime = (endTime - startTime) / 1000;
        log.info("结束-->主线程方法,结束时间：" + endTime + "总共花费时间：" + totalTime + " s");
    }


    /**
     * 创建图谱关系
     * <p>
     * 解析连线配置中的源/目标表与字段，确保源表主键存在；
     * 分页读取源表数据，基于源记录的主键与映射字段，
     * 为匹配的目标记录建立指定标签的关系。
     * </p>
     *
     * @param domain   领域名称
     * @param dataLink 连线配置（源/目标表、字段及关系标签）
     * @return boolean 是否成功触发关系生成流程
     */
    private boolean createGraphLink(String domain,DataLink dataLink) {
        String sourceIdStr = dataLink.getSourceId();
        String[] sourceArr=sourceIdStr.split("-");
        Integer sourceTableId=Integer.parseInt(sourceArr[1]);
        Integer sourceFieldId=Integer.parseInt(sourceArr[2]);
        String targetIdStr = dataLink.getTargetId();
        String[] targetArr=targetIdStr.split("-");
        Integer targetTableId=Integer.parseInt(targetArr[1]);
        //Integer targetFieldId=Integer.parseInt(targetArr[2]);
        MetaDataTable sourceMetaDataTable = metaDataTableService.queryById(sourceTableId);
        Integer sourceDatasourceId = sourceMetaDataTable.getDatasourceId();
        String sourceTableName=sourceMetaDataTable.getDataTableCode();

        MetaDataTable targetMetaDataTable = metaDataTableService.queryById(targetTableId);
        Integer targetDatasourceId = targetMetaDataTable.getDatasourceId();
        //String targetTableName=targetMetaDataTable.getDataTableCode();

        List<DataColumnVo> sourceDataColumns = metaDataColumnService.queryByTableId(sourceTableId);
        DataColumnVo sourceColumnItem=sourceDataColumns.stream().filter(m->m.getDataColumnId().equals(sourceFieldId)).collect(Collectors.toList()).get(0);
        DataColumnVo sourcePrimaryColumnItem=sourceDataColumns.stream().filter(m->m.getIsPrimary().equals(1)).collect(Collectors.toList()).get(0);
        MetaDataSource metaDataSource = metaDataSourceService.queryById(sourceDatasourceId);
        //取出字段名
        List<String> columns=new ArrayList<>();
        columns.add(sourcePrimaryColumnItem.getDataColumnName());
        columns.add(sourceColumnItem.getDataColumnName());
        //读取数据记录
        int totalCount = DbUtils.getTableDataNum(metaDataSource.getDbType(), metaDataSource.getDbName(), metaDataSource.getConnectUrl(), sourceTableName, metaDataSource.getDbUserName(), metaDataSource.getDbPassWord(), metaDataSource.getDriverName(), metaDataSource.getMaxPoolSize());
        int pageSize=500;
        long totalPage = totalCount / pageSize + ((totalCount % pageSize) == 0 ? 0 : 1);
        for (Integer pageIndex = 1; pageIndex <= totalPage; pageIndex++) {
            //逐条生成图谱
            PageRecord<Map<String, Object>> dataItems = DbUtils.getTableInfoByPage(pageIndex, pageSize, metaDataSource.getDbType(), metaDataSource.getDbName(), metaDataSource.getConnectUrl(), sourceTableName, metaDataSource.getDbUserName(), metaDataSource.getDbPassWord(), metaDataSource.getDriverName(), metaDataSource.getMaxPoolSize(), null, columns);
            executeLink(domain,dataLink.getLabel(),sourceDatasourceId,sourceTableId, sourceColumnItem.getDataColumnName(),targetDatasourceId,targetTableId,dataItems.getData(),sourceDataColumns);
        }
        return true;
    }

    /**
     * 执行节点生成
     * <p>
     * 要求：列集合中至少包含一个主实体列（isMainEntity=1）与一个主键列（isPrimary=1）。
     * 对于每条记录：
     * - 合并创建主节点（携带 dataId/tableId/sourceId 属性）；
     * - 其余非空列创建属性节点；
     * - 按列别名/列名建立主节点到属性节点的关系。
     * </p>
     *
     * @param domain  领域名称
     * @param sourceId 数据源ID
     * @param tableId  数据表ID
     * @param nodes   表记录列表
     * @param columns 列配置（含 isMainEntity、isPrimary、itemCode、itemName）
     */
    private void executeNode(String domain,Integer sourceId,Integer tableId, List<Map<String, Object>> nodes,List<GraphNodeColumnItem> columns) {
        for (Map<String, Object> node : nodes) {
            String mainNodeUuid="";
            List<GraphNodeColumnItem> mainEntitys = columns.stream().filter(m -> m.getIsMainEntity().equals(1)).collect(Collectors.toList());
            if(mainEntitys==null||mainEntitys.size()==0){
                return;
            }
            GraphNodeColumnItem mainEntity=mainEntitys.get(0);
            List<GraphNodeColumnItem> primaryItems = columns.stream().filter(m -> m.getIsPrimary().equals(1)).collect(Collectors.toList());
            if(primaryItems==null||primaryItems.size()==0){
                return;
            }
            GraphNodeColumnItem primaryItem=primaryItems.get(0);
            String dataId=node.get(primaryItem.getItemCode()).toString();
            String cy = String.format("merge (n:`%s` {name:'%s',dataId:'%s',tableId:%s,sourceId:%s}) return n",domain,node.get(mainEntity.getItemCode()),dataId,tableId,sourceId);
            HashMap<String, Object> mainNode = Neo4jUtil.getSingleGraphNode(cy);
            mainNodeUuid = mainNode.get("uuid").toString();
            for (String key : node.keySet()) {
                if(key.equalsIgnoreCase(mainEntity.getItemCode())||key.equalsIgnoreCase(primaryItem.getItemCode())) continue;
                if(node.get(key)==null||StringUtil.isEmpty(node.get(key).toString())) continue;
                String name=node.get(key).toString();
                String propertiesString = String.format("merge (n:`%s` {name:'%s'}) return n",domain,name);
                HashMap<String, Object> graphNode = Neo4jUtil.getSingleGraphNode(propertiesString);
                String uuid = graphNode.get("uuid").toString();
                GraphNodeColumnItem item = columns.stream().filter(m -> m.getItemCode().equalsIgnoreCase(key)).collect(Collectors.toList()).get(0);
                String alia= StringUtil.isNotEmpty(item.getItemName())?item.getItemName():item.getItemCode();
                String linkCy=String.format("match(n:`%s`),(m:`%s`) where id(n)=%s and id(m)=%s " +
                        "merge (n)-[r:`%s`]->(m)",domain,domain,mainNodeUuid,uuid,alia);
                Neo4jUtil.runCypherSql(linkCy);
            }
        }
    }


    /**
     * 执行关系生成
     * <p>
     * 基于源表主键列与映射字段，定位源/目标节点并创建关系：
     * match 源节点 (n) 与目标节点 (m)，按 sourceId/tableId/dataId 精确匹配；
     * merge (n)-[r:label]->(m)。
     * </p>
     *
     * @param domain              领域名称
     * @param label               关系标签
     * @param sourceDataSourceId  源数据源ID
     * @param sourceTableId       源表ID
     * @param sourceFieldCode     源字段编码（映射到目标主键）
     * @param targetDataSourceId  目标数据源ID
     * @param targetTableId       目标表ID
     * @param nodes               源表记录列表
     * @param columns             源表列配置（含主键标识）
     */
    private void executeLink(String domain,String label,Integer sourceDataSourceId,Integer sourceTableId,String sourceFieldCode,Integer targetDataSourceId,Integer targetTableId,
                             List<Map<String, Object>> nodes,List<DataColumnVo> columns) {
        for (Map<String, Object> node : nodes) {
            DataColumnVo primaryItem = columns.stream().filter(m -> m.getIsPrimary().equals(1)).collect(Collectors.toList()).get(0);
            String dataId=node.get(primaryItem.getDataColumnName()).toString();
            String targetDataId=node.get(sourceFieldCode).toString();
            String linkCy=String.format("match(n:`%s`),(m:`%s`) where n.sourceId=%s and n.tableId=%s and n.dataId='%s' and m.sourceId=%s and m.tableId=%s and m.dataId='%s'" +
                    "merge (n)-[r:'%s']->(m)",domain,domain,sourceDataSourceId,sourceTableId,dataId,targetDataSourceId,targetTableId,targetDataId,label);
            Neo4jUtil.runCypherSql(linkCy);
        }

    }
    /**
     * 从config中提取流程组件
     * <p>
     * 解析图谱配置中的节点与连线，构建组件容器：
     * - 节点：映射为 DataNode，记录前置/后置关系及是否起始节点；
     * - 连线：映射为 DataLink，记录源/目标及标签；
     * 并根据入度为 0 的节点标记为起始节点。
     * </p>
     *
     * @param graphItem 图谱配置（节点/连线列表及领域名）
     * @return ComponentContainer 解析后的组件容器
     */
    public ComponentContainer explainComponentConfig(GraphItem graphItem) {
        log.info("解析配置信息");
        ComponentContainer componentContainer = new ComponentContainer();
        //解析节点
        List<GraphNodeItem> nodeList = graphItem.getNodeList();
        nodeList.forEach(n -> {
            String id = n.getNodeKey();
            String nodeCode = n.getType(); // Use type instead of nodeKey
            if (nodeCode == null || nodeCode.isEmpty()) {
                nodeCode = "task"; // Default to task for backward compatibility
            }
            String nodeName = n.getNodeName();
            DataNode dataNode = new DataNode();
            dataNode.setId(id);
            dataNode.setNodeCode(nodeCode);
            dataNode.setNodeName(nodeName);
            dataNode.setDomain(graphItem.getDomainName());
            dataNode.setData(n);
            componentContainer.addNode(dataNode);
        });
        //解析连线
        List<GraphLinkItem> links = graphItem.getLineList();
        links.forEach(m -> {
            DataLink dataLink = new DataLink();
            dataLink.setSourceId(m.getFrom());
            dataLink.setTargetId(m.getTo());
            dataLink.setLabel(m.getLabel());
            componentContainer.addLink(dataLink);
        });
        //找到起始结点及当前节点的前置后置节点id
        componentContainer.getNodes().forEach(n -> {
                    //后置节点
                    List<String> nextNodeIds = componentContainer.getDataLinkList().stream().filter(m -> m.getSourceId().equalsIgnoreCase(n.getId())).map(DataLink::getTargetId).collect(Collectors.toList());
                    List<DataNode> nextNodes = componentContainer.getNodes().stream().filter(next -> nextNodeIds.contains(next.getId())).collect(Collectors.toList());
                    if (null != nextNodes) {
                        n.setNextNodes(nextNodes);
                    }

                    //前置节点
                    List<String> prevNodeIds = componentContainer.getDataLinkList().stream().filter(m -> m.getTargetId().equalsIgnoreCase(n.getId())).map(DataLink::getSourceId).collect(Collectors.toList());
                    List<DataNode> prevNodes = componentContainer.getNodes().stream().filter(next -> prevNodeIds.contains(next.getId())).collect(Collectors.toList());
                    if (null != nextNodes) {
                        n.setPrevNodes(prevNodes);
                    }
                    //没有前置节点就是头结点
                    if (prevNodeIds.size() == 0) {
                        n.setStart(true);
                    }
                }
        );
        return componentContainer;
    }
}
