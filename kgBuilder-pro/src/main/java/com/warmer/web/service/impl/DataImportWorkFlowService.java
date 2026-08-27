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
import com.warmer.web.domain.DataNode;
import com.warmer.web.request.GraphNodeColumnItem;
import com.warmer.web.service.AbstractWorkFlowService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 数据导入流程组件
 */
@Service("task") // Matches the "type" field in GraphNodeItem (likely "task")
@Slf4j
public class DataImportWorkFlowService extends AbstractWorkFlowService {

    @Autowired
    MetaDataSourceService metaDataSourceService;
    @Autowired
    MetaDataTableService metaDataTableService;
    @Autowired
    MetaDataColumnService metaDataColumnService;

    @Override
    public boolean process(DataNode dataNode) {
        log.info("执行数据导入节点: {}", dataNode.getNodeName());
        try {
            return createGraphNode(dataNode);
        } catch (Exception e) {
            log.error("数据导入节点执行失败", e);
            return false;
        }
    }

    private boolean createGraphNode(DataNode dataNode) {
        /**
         * 创建图谱节点
         * <p>
         * 根据组件配置读取数据源与数据表元信息，确保包含主键列；
         * 分页拉取表数据，逐条调用 {@link #executeNode(String, Integer, Integer, List, List)}
         * 以主实体列为中心生成节点及其属性节点，并建立属性关系。
         * </p>
         */
        Integer sourceId = dataNode.getData().getSourceId();
        MetaDataSource metaDataSource = metaDataSourceService.queryById(sourceId);
        Integer tableId = dataNode.getData().getTableId();
        MetaDataTable metaDataTable = metaDataTableService.queryById(tableId);
        String tableName=metaDataTable.getDataTableCode();
        List<DataColumnVo> dbColumns = metaDataColumnService.queryByTableId(tableId);
        List<DataColumnVo> primaryItem=dbColumns.stream().filter(n->n.getIsPrimary().equals(1)).collect(Collectors.toList());
        if(primaryItem==null||primaryItem.size()==0){
            log.warn("表 {} 没有主键，跳过", tableName);
            return false;
        }
        //界面上选中的列id
        List<Integer> selectColumns = dataNode.getData().getItems().stream().map(n -> n.getColumnId()).collect(Collectors.toList());
        //转化成列对象集合
        List<GraphNodeColumnItem> dataColumns=dataNode.getData().getItems();
        DataColumnVo primaryModel=primaryItem.get(0);
        if(!selectColumns.contains(primaryModel.getDataColumnId())){
            GraphNodeColumnItem item= new GraphNodeColumnItem();
            item.setColumnId(primaryModel.getDataColumnId());
            item.setItemCode(primaryModel.getDataColumnName());
            item.setItemName(primaryModel.getDataColumnAlia());
            item.setIsPrimary(primaryModel.getIsPrimary());
            dataColumns.add(item);
        }
        //取出字段名
        List<String> columns=dataColumns.stream().map(n->n.getItemCode()).collect(Collectors.toList());
        //读取数据记录
        int totalCount = DbUtils.getTableDataNum(metaDataSource.getDbType(), metaDataSource.getDbName(), metaDataSource.getConnectUrl(), tableName, metaDataSource.getDbUserName(), metaDataSource.getDbPassWord(), metaDataSource.getDriverName(), metaDataSource.getMaxPoolSize());
        int pageSize=500;
        long totalPage = totalCount / pageSize + ((totalCount % pageSize) == 0 ? 0 : 1);
        for (Integer pageIndex = 1; pageIndex <= totalPage; pageIndex++) {
            //逐条生成图谱
            PageRecord<Map<String, Object>> dataItems = DbUtils.getTableInfoByPage(pageIndex, pageSize, metaDataSource.getDbType(), metaDataSource.getDbName(), metaDataSource.getConnectUrl(), tableName, metaDataSource.getDbUserName(), metaDataSource.getDbPassWord(), metaDataSource.getDriverName(), metaDataSource.getMaxPoolSize(), null, columns);
            executeNode(dataNode.getDomain(),sourceId,tableId, dataItems.getData(),dataColumns);
        }
        return true;
    }

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
            if(mainNode != null && mainNode.containsKey("uuid")) {
                mainNodeUuid = mainNode.get("uuid").toString();
            } else {
                continue;
            }
            
            for (String key : node.keySet()) {
                if(key.equalsIgnoreCase(mainEntity.getItemCode())||key.equalsIgnoreCase(primaryItem.getItemCode())) continue;
                if(node.get(key)==null||StringUtil.isEmpty(node.get(key).toString())) continue;
                String name=node.get(key).toString();
                String propertiesString = String.format("merge (n:`%s` {name:'%s'}) return n",domain,name);
                HashMap<String, Object> graphNode = Neo4jUtil.getSingleGraphNode(propertiesString);
                String uuid = graphNode.get("uuid").toString();
                List<GraphNodeColumnItem> items = columns.stream().filter(m -> m.getItemCode().equalsIgnoreCase(key)).collect(Collectors.toList());
                if(items.isEmpty()) continue;
                GraphNodeColumnItem item = items.get(0);
                String alia= StringUtil.isNotEmpty(item.getItemName())?item.getItemName():item.getItemCode();
                String linkCy=String.format("match(n:`%s`),(m:`%s`) where (elementId(n)='%s' or toString(id(n))='%s') and (elementId(m)='%s' or toString(id(m))='%s') " +
                        "merge (n)-[r:`%s`]->(m)",domain,domain,mainNodeUuid,mainNodeUuid,uuid,uuid,alia);
                Neo4jUtil.runCypherSql(linkCy);
            }
        }
    }
}
