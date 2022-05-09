package com.warmer.web.dao.impl;

import com.warmer.base.util.JsonHelper;
import com.warmer.base.util.Neo4jUtil;
import com.warmer.web.dao.KGGraphDao;
import com.warmer.web.model.NodeItem;

import com.warmer.web.request.GraphQuery;
import com.warmer.base.util.GraphPageRecord;
import com.warmer.base.util.StringUtil;
import com.warmer.web.request.NodeCoordinateItem;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class KGGraphRepository implements KGGraphDao {
    /**
     * 领域标签分页
     */
    @Override
    public GraphPageRecord<HashMap<String, Object>> getPageDomain(GraphQuery queryItem) {
        GraphPageRecord<HashMap<String, Object>> resultRecord = new GraphPageRecord<HashMap<String, Object>>();
        try {
            String totalCountQuery = "MATCH (n) RETURN count(distinct labels(n)) as count";
            long totalCount = 0;
            totalCount = Neo4jUtil.getGraphValue(totalCountQuery);
            if (totalCount > 0) {
                int skipCount = (queryItem.getPageIndex() - 1) * queryItem.getPageSize();
                int limitCount = queryItem.getPageSize();
                String domainSql = String.format(
                        "START n=node(*)  RETURN distinct labels(n) as domain,count(n) as nodeCount order by nodeCount desc SKIP %s LIMIT %s",
                        skipCount, limitCount);
                List<HashMap<String, Object>> pageList = Neo4jUtil.getGraphNode(domainSql);
                resultRecord.setPageIndex(queryItem.getPageIndex());
                resultRecord.setPageSize(queryItem.getPageSize());
                resultRecord.setTotalCount(totalCount);
                resultRecord.setNodeList(pageList);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return resultRecord;
    }

    /**
     * 删除Neo4j 标签
     */
    @Override
    public void deleteKgDomain(String domain) {
        try {
            String deleteRelation = String.format("MATCH (n:`%s`)-[r]-(m) detach delete r", domain);
            Neo4jUtil.runCypherSql(deleteRelation);
            String deleteNode = String.format("MATCH (n:`%s`) detach delete n", domain);
            Neo4jUtil.runCypherSql(deleteNode);
        } catch (Exception e) {
            e.printStackTrace();

        }
    }

    /**
     * 查询图谱节点和关系
     */
    @Override
    public HashMap<String, Object> getDomainGraph(GraphQuery query) {
        HashMap<String, Object> nr = new HashMap<String, Object>();
        try {
            String domain = query.getDomain();
            // MATCH (n:`症状`) -[r]-(m:症状) where r.name='治疗' or r.name='危险因素' return n,m
            if (!StringUtil.isBlank(domain)) {
                String cqr = "";
                List<String> lis = new ArrayList<String>();
                if (query.getRelation() != null && query.getRelation().length > 0) {
                    for (String r : query.getRelation()) {
                        String it = String.format("r.name='%s'", r);
                        lis.add(it);
                    }
                    cqr = String.join(" or ", lis);
                }
                String cqWhere = "";
                if (!StringUtil.isBlank(query.getNodeName()) || !StringUtil.isBlank(cqr)) {

                    if (!StringUtil.isBlank(query.getNodeName())) {
                        if (query.getMatchType() == 1) {
                            cqWhere = String.format("where n.name ='%s' ", query.getNodeName());

                        } else {
                            cqWhere = String.format("where n.name contains('%s')", query.getNodeName());
                        }
                    }
                    String nodeOnly = cqWhere;
                    if (!StringUtil.isBlank(cqr)) {
                        if (StringUtil.isBlank(cqWhere)) {
                            cqWhere = String.format(" where ( %s )", cqr);

                        } else {
                            cqWhere += String.format(" and ( %s )", cqr);
                        }

                    }
                    // 下边的查询查不到单个没有关系的节点,考虑要不要左箭头
                    String nodeSql = String.format("MATCH (n:`%s`) <-[r]->(m) %s return * limit %s", domain, cqWhere,
                            query.getPageSize());
                    HashMap<String, Object> graphNode = Neo4jUtil.getGraphNodeAndShip(nodeSql);
                    Object node = graphNode.get("node");
                    // 没有关系显示则显示节点
                    if (node != null) {
                        nr.put("node", graphNode.get("node"));
                        nr.put("relationship", graphNode.get("relationship"));
                    } else {
                        String nodecql = String.format("MATCH (n:`%s`) %s RETURN distinct(n) limit %s", domain,
                                nodeOnly, query.getPageSize());
                        List<HashMap<String, Object>> nodeItem = Neo4jUtil.getGraphNode(nodecql);
                        nr.put("node", nodeItem);
                        nr.put("relationship", new ArrayList<HashMap<String, Object>>());
                    }
                } else {
                    String nodeSql = String.format("MATCH (n:`%s`) %s RETURN distinct(n) limit %s", domain, cqWhere,
                            query.getPageSize());
                    List<HashMap<String, Object>> graphNode = Neo4jUtil.getGraphNode(nodeSql);
                    nr.put("node", graphNode);
                    String domainSql = String.format("MATCH (n:`%s`)<-[r]-> (m) %s RETURN distinct(r) limit %s", domain,
                            cqWhere, query.getPageSize());// m是否加领域
                    List<HashMap<String, Object>> graphRelation = Neo4jUtil.getGraphRelationShip(domainSql);
                    nr.put("relationship", graphRelation);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return nr;
    }

    /**
     * 获取节点列表
     */
    @Override
    public HashMap<String, Object> getDomainNodes(String domain, Integer pageIndex, Integer pageSize) {
        HashMap<String, Object> resultItem = new HashMap<String, Object>();
        List<HashMap<String, Object>> ents = new ArrayList<HashMap<String, Object>>();
        List<HashMap<String, Object>> concepts = new ArrayList<HashMap<String, Object>>();
        List<HashMap<String, Object>> props = new ArrayList<HashMap<String, Object>>();
        List<HashMap<String, Object>> methods = new ArrayList<HashMap<String, Object>>();
        List<HashMap<String, Object>> entitys = new ArrayList<HashMap<String, Object>>();
        try {
            int skipCount = (pageIndex - 1) * pageSize;
            int limitCount = pageSize;
            String domainSql = String.format("START n=node(*) MATCH (n:`%s`) RETURN n SKIP %s LIMIT %s", domain,
                    skipCount, limitCount);
            if (!StringUtil.isBlank(domain)) {
                ents = Neo4jUtil.getGraphNode(domainSql);
                for (HashMap<String, Object> hashMap : ents) {
                    Object et = hashMap.get("entityType");
                    if (et != null) {
                        String typeStr = et.toString();
                        if (StringUtil.isNotBlank(typeStr)) {
                            int type = Integer.parseInt(et.toString());
                            if (type == 0) {
                                concepts.add(hashMap);
                            } else if (type == 1) {
                                entitys.add(hashMap);
                            } else if (type == 2 || type == 3) {
                                props.add(hashMap);// 属性和方法放在一起展示
                            } else {
                                // methods.add(hashMap);
                            }
                        }
                    }
                }
                resultItem.put("concepts", concepts);
                resultItem.put("props", props);
                resultItem.put("methods", methods);
                resultItem.put("entitys", entitys);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return resultItem;
    }

    /**
     * 获取某个领域指定节点拥有的上下级的节点数
     */
    @Override
    public long getRelationNodeCount(String domain, long nodeId) {
        long totalCount = 0;
        try {
            if (!StringUtil.isBlank(domain)) {
                String nodeSql = String.format("MATCH (n:`%s`) <-[r]->(m)  where id(n)=%s return count(m)", domain,
                        nodeId);
                totalCount = Neo4jUtil.getGraphValue(nodeSql);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return totalCount;
    }

    /**
     * 创建领域,默认创建一个新的节点,给节点附上默认属性
     */
    @Override
    public void createDomain(String domain) {
        try {
            String cypherSql = String.format(
                    "create (n:`%s`{entityType:0,name:''}) return id(n)", domain);
            Neo4jUtil.runCypherSql(cypherSql);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Override
    public void quickCreateDomain(String domain,String nodeName) {
        try {
            String cypherSql = String.format(
                    "create (n:`%s`{entityType:0,name:'%s'}) return id(n)", domain,nodeName);
            Neo4jUtil.runCypherSql(cypherSql);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * 获取/展开更多节点,找到和该节点有关系的节点
     */
    @Override
    public HashMap<String, Object> getMoreRelationNode(String domain, String nodeId) {
        HashMap<String, Object> result = new HashMap<String, Object>();
        try {
            String cypherSql = String.format("MATCH (n:`%s`) -[r]-(m) where id(n)=%s  return * limit 100", domain,
                    nodeId);
            result = Neo4jUtil.getGraphNodeAndShip(cypherSql);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 更新节点名称
     */
    @Override
    public HashMap<String, Object> updateNodeName(String domain, String nodeId, String nodeName) {
        HashMap<String, Object> result = new HashMap<String, Object>();
        List<HashMap<String, Object>> graphNodeList = new ArrayList<HashMap<String, Object>>();
        try {
            String cypherSql = String.format("MATCH (n:`%s`) where id(n)=%s set n.name='%s' return n", domain, nodeId,
                    nodeName);
            graphNodeList = Neo4jUtil.getGraphNode(cypherSql);
            if (graphNodeList.size() > 0) {
                return graphNodeList.get(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 创建单个节点
     */
    @Override
    public HashMap<String, Object> createNode(String domain, NodeItem entity) {
        HashMap<String, Object> rss = new HashMap<String, Object>();
        List<HashMap<String, Object>> graphNodeList = new ArrayList<HashMap<String, Object>>();
        try {
            if (entity.getUuid() != 0) {
                String sqlKeyVal = Neo4jUtil.getKeyValCyphersql(entity);
                String cypherSql = String.format("match (n:`%s`) where id(n)=%s set %s return n", domain,
                        entity.getUuid(), sqlKeyVal);
                graphNodeList = Neo4jUtil.getGraphNode(cypherSql);
            } else {
                entity.setColor("#ff4500");// 默认颜色
                entity.setR(30);// 默认半径
                String propertiesString = Neo4jUtil.getFilterPropertiesJson(JsonHelper.toJSONString(entity));
                String cypherSql = String.format("create (n:`%s` %s) return n", domain, propertiesString);
                graphNodeList = Neo4jUtil.getGraphNode(cypherSql);
            }
            if (graphNodeList.size() > 0) {
                rss = graphNodeList.get(0);
                return rss;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return rss;
    }

    /**
     * 自定义uuid,重复则返回节点，不存在则创建
     * @param domain
     * @param entity
     * @return
     */
    @Override
    public HashMap<String, Object> createNodeWithUUid(String domain, NodeItem entity) {
        HashMap<String, Object> rss = new HashMap<String, Object>();
        List<HashMap<String, Object>> graphNodeList = new ArrayList<HashMap<String, Object>>();
        try {
            if (entity.getUuid() != 0) {
                String sqlKeyVal = Neo4jUtil.getKeyValCyphersql(entity);
                String cypherSql = String.format("match (n:`%s`) where id(n)=%s set %s return n", domain,
                        entity.getUuid(), sqlKeyVal);
                graphNodeList = Neo4jUtil.getGraphNode(cypherSql);
            }
            if (graphNodeList.size() > 0) {
                rss = graphNodeList.get(0);
                return rss;
            }
            entity.setName(entity.getName());
            entity.setColor(entity.getColor());
            entity.setR(30);// 默认半径
            entity.setUuid(entity.getUuid());
            String propertiesString = Neo4jUtil.getFilterPropertiesJson(JsonHelper.toJSONString(entity));
            String cypherSql = String.format("create (n:`%s` %s) return n", domain, propertiesString);
            graphNodeList = Neo4jUtil.getGraphNode(cypherSql);
            if (graphNodeList.size() > 0) {
                rss = graphNodeList.get(0);
                return rss;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return rss;
    }

    /**
     * 批量创建节点和关系
     */
    @Override
    public HashMap<String, Object> batchCreateNode(String domain, String sourceName, String relation,
                                                   String[] targetNames) {
        HashMap<String, Object> rss = new HashMap<String, Object>();
        List<HashMap<String, Object>> nodes = new ArrayList<HashMap<String, Object>>();
        List<HashMap<String, Object>> ships = new ArrayList<HashMap<String, Object>>();
        try {
            String cypherSqlFmt = "create (n:`%s` {name:'%s',color:'#ff4500',r:30}) return n";
            String cypherSql = String.format(cypherSqlFmt, domain, sourceName);// 概念实体
            List<HashMap<String, Object>> graphNodeList = Neo4jUtil.getGraphNode(cypherSql);
            if (graphNodeList.size() > 0) {
                HashMap<String, Object> sourceNode = graphNodeList.get(0);
                nodes.add(sourceNode);
                String sourceUuid = String.valueOf(sourceNode.get("uuid"));
                for (String tn : targetNames) {
                    String targetNodeSql = String.format(cypherSqlFmt, domain, tn);
                    List<HashMap<String, Object>> targetNodeList = Neo4jUtil.getGraphNode(targetNodeSql);
                    if (targetNodeList.size() > 0) {
                        HashMap<String, Object> targetNode = targetNodeList.get(0);
                        nodes.add(targetNode);
                        String targetUuid = String.valueOf(targetNode.get("uuid"));
                        String rSql = String.format(
                                "match(n:`%s`),(m:`%s`) where id(n)=%s and id(m)=%s create (n)-[r:RE {name:'%s'}]->(m) return r",
                                domain, domain, sourceUuid, targetUuid, relation);
                        List<HashMap<String, Object>> rShipList = Neo4jUtil.getGraphRelationShip(rSql);
                        ships.addAll(rShipList);
                    }

                }
            }
            rss.put("nodes", nodes);
            rss.put("ships", ships);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return rss;
    }

    /**
     * 批量创建下级节点
     *
     * @param domain      领域
     * @param sourceId    源节点id
     * @param entityType  节点类型
     * @param targetNames 目标节点名称数组
     * @param relation    关系
     */
    @Override
    public HashMap<String, Object> batchCreateChildNode(String domain, String sourceId, Integer entityType,
                                                        String[] targetNames, String relation) {
        HashMap<String, Object> rss = new HashMap<String, Object>();
        List<HashMap<String, Object>> nodes = new ArrayList<HashMap<String, Object>>();
        List<HashMap<String, Object>> ships = new ArrayList<HashMap<String, Object>>();
        try {
            String cypherSqlFmt = "create (n:`%s`{name:'%s',color:'#ff4500',r:30}) return n";
            String cypherSql = String.format("match (n:`%s`) where id(n)=%s return n", domain, sourceId);
            List<HashMap<String, Object>> sourceNodeList = Neo4jUtil.getGraphNode(cypherSql);
            if (sourceNodeList.size() > 0) {
                nodes.addAll(sourceNodeList);
                for (String tn : targetNames) {
                    String targetNodeSql = String.format(cypherSqlFmt, domain, tn);
                    List<HashMap<String, Object>> targetNodeList = Neo4jUtil.getGraphNode(targetNodeSql);
                    if (targetNodeList.size() > 0) {
                        HashMap<String, Object> targetNode = targetNodeList.get(0);
                        nodes.add(targetNode);
                        String targetUuid = String.valueOf(targetNode.get("uuid"));
                        // 创建关系
                        String rSql = String.format(
                                "match(n:`%s`),(m:`%s`) where id(n)=%s and id(m)=%s create (n)-[r:RE {name:'%s'}]->(m) return r",
                                domain, domain, sourceId, targetUuid, relation);
                        List<HashMap<String, Object>> shipList = Neo4jUtil.getGraphRelationShip(rSql);
                        ships.addAll(shipList);
                    }
                }
            }
            rss.put("nodes", nodes);
            rss.put("ships", ships);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return rss;
    }

    /**
     * 批量创建同级节点
     *
     * @param domain      领域
     * @param entityType  节点类型
     * @param sourceNames 节点名称
     */
    @Override
    public List<HashMap<String, Object>> batchCreateSameNode(String domain, Integer entityType, String[] sourceNames) {
        List<HashMap<String, Object>> rss = new ArrayList<HashMap<String, Object>>();
        try {
            String cypherSqlFmt = "create (n:`%s`{name:'%s',color:'#ff4500',r:30}) return n";
            for (String tn : sourceNames) {
                String sourceNodeSql = String.format(cypherSqlFmt, domain, tn, entityType);
                List<HashMap<String, Object>> targetNodeList = Neo4jUtil.getGraphNode(sourceNodeSql);
                rss.addAll(targetNodeList);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return rss;
    }

    /**
     * 添加关系
     *
     * @param domain   领域
     * @param sourceId 源节点id
     * @param targetId 目标节点id
     * @param ship     关系
     */
    @Override
    public HashMap<String, Object> createLink(String domain, long sourceId, long targetId, String ship) {
        HashMap<String, Object> rss = new HashMap<String, Object>();
        try {
            String cypherSql = String.format("MATCH (n:`%s`),(m:`%s`) WHERE id(n)=%s AND id(m) = %s "
                    + "CREATE (n)-[r:RE{name:'%s'}]->(m)" + "RETURN r", domain, domain, sourceId, targetId, ship);
            List<HashMap<String, Object>> cypherResult = Neo4jUtil.getGraphRelationShip(cypherSql);
            if (cypherResult.size() > 0) {
                rss = cypherResult.get(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return rss;
    }

    @Override
    public HashMap<String, Object> createLinkByUuid(String domain, long sourceId, long targetId, String ship) {
        HashMap<String, Object> rss = new HashMap<String, Object>();
        try {
            String cypherSql = String.format("MATCH (n:`%s`),(m:`%s`) WHERE n.uuid=%s AND m.uuid = %s "
                    + "CREATE (n)-[r:RE{name:'%s'}]->(m)" + "RETURN r", domain, domain, sourceId, targetId, ship);
            List<HashMap<String, Object>> cypherResult = Neo4jUtil.getGraphRelationShip(cypherSql);
            if (cypherResult.size() > 0) {
                rss = cypherResult.get(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return rss;
    }
    /**
     * 更新关系
     *
     * @param domain   领域
     * @param shipId   关系id
     * @param shipName 关系名称
     */
    @Override
    public HashMap<String, Object> updateLink(String domain, long shipId, String shipName) {
        HashMap<String, Object> rss = new HashMap<String, Object>();
        try {
            String cypherSql = String.format("MATCH (n:`%s`) -[r]->(m) where id(r)=%s set r.name='%s' return r", domain,
                    shipId, shipName);
            List<HashMap<String, Object>> cypherResult = Neo4jUtil.getGraphRelationShip(cypherSql);
            if (cypherResult.size() > 0) {
                rss = cypherResult.get(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rss;
    }

    /**
     * 删除节点(先删除关系再删除节点)
     *
     */
    @Override
    public List<HashMap<String, Object>> deleteNode(String domain, long nodeId) {
        List<HashMap<String, Object>> result = new ArrayList<HashMap<String, Object>>();
        try {
            String nSql = String.format("MATCH (n:`%s`)  where id(n)=%s return n", domain, nodeId);
            result = Neo4jUtil.getGraphNode(nSql);
            String deleteRelationSql = String.format("MATCH (n:`%s`) -[r]-(m) where id(n)=%s detach delete r", domain, nodeId);
            Neo4jUtil.runCypherSql(deleteRelationSql);
            String deleteNodeSql = String.format("MATCH (n:`%s`) where id(n)=%s detach delete n", domain, nodeId);
            Neo4jUtil.runCypherSql(deleteNodeSql);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 删除关系
     *
     */
    @Override
    public void deleteLink(String domain, long shipId) {
        try {
            String cypherSql = String.format("MATCH (n:`%s`) -[r]-(m) where id(r)=%s detach delete r", domain, shipId);
            Neo4jUtil.runCypherSql(cypherSql);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 段落识别出的三元组生成图谱
     *
     * @param domain 领域名称
     * @param entityType 实体类型
     * @param operateType 操作类型
     * @param sourceId 节点id
     * @param rss         关系三元组
     *                    [[startname;ship;endname],[startname1;ship1;endname1],[startname2;ship2;endname2]]
     * @return node relationship
     */
    @Override
    public HashMap<String, Object> createGraphByText(String domain, Integer entityType, Integer operateType,
                                                     Integer sourceId, String[] rss) {
        HashMap<String, Object> rsList = new HashMap<String, Object>();
        try {
            List<Object> nodeIds = new ArrayList<Object>();
            List<HashMap<String, Object>> nodeList = new ArrayList<HashMap<String, Object>>();
            List<HashMap<String, Object>> shipList = new ArrayList<HashMap<String, Object>>();

            if (rss != null && rss.length > 0) {
                for (String item : rss) {
                    String[] ns = item.split(";");
                    String nodeStart = ns[0];
                    String ship = ns[1];
                    String nodeEnd = ns[2];
                    String nodeStartSql = String.format("MERGE (n:`%s`{name:'%s',entityType:'%s'})  return n", domain,
                            nodeStart, entityType);
                    String nodeEndSql = String.format("MERGE (n:`%s`{name:'%s',entityType:'%s'})  return n", domain,
                            nodeEnd, entityType);
                    // 创建初始节点
                    List<HashMap<String, Object>> startNode = Neo4jUtil.getGraphNode(nodeStartSql);
                    // 创建结束节点
                    List<HashMap<String, Object>> endNode = Neo4jUtil.getGraphNode(nodeEndSql);
                    Object startId = startNode.get(0).get("uuid");
                    if (!nodeIds.contains(startId)) {
                        nodeIds.add(startId);
                        nodeList.addAll(startNode);
                    }
                    Object endId = endNode.get(0).get("uuid");
                    if (!nodeIds.contains(endId)) {
                        nodeIds.add(endId);
                        nodeList.addAll(endNode);
                    }
                    if (sourceId != null && sourceId > 0 && operateType == 2) {// 添加下级
                        String shipSql = String.format(
                                "MATCH (n:`%s`),(m:`%s`) WHERE id(n)=%s AND id(m) = %s "
                                        + "CREATE (n)-[r:RE{name:'%s'}]->(m)" + "RETURN r",
                                domain, domain, sourceId, startId, "");
                        List<HashMap<String, Object>> shipResult = Neo4jUtil.getGraphRelationShip(shipSql);
                        shipList.add(shipResult.get(0));
                    }
                    String shipSql = String.format("MATCH (n:`%s`),(m:`%s`) WHERE id(n)=%s AND id(m) = %s "
                            + "CREATE (n)-[r:RE{name:'%s'}]->(m)" + "RETURN r", domain, domain, startId, endId, ship);
                    List<HashMap<String, Object>> shipResult = Neo4jUtil.getGraphRelationShip(shipSql);
                    shipList.addAll(shipResult);

                }
                rsList.put("node", nodeList);
                rsList.put("relationship", shipList);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return rsList;
    }

    @Override
    public void batchCreateGraph(String domain, List<Map<String, Object>> params) {
        try {
            if (params != null && params.size() > 0) {
                String nodeStr = Neo4jUtil.getFilterPropertiesJson(JsonHelper.toJSONString(params));
                String nodeCypher = String
                        .format("UNWIND %s as row " + " MERGE (n:`%s` {name:row.SourceNode,source:row.Source})"
                                + " MERGE (m:`%s` {name:row.TargetNode,source:row.Source})", nodeStr, domain, domain);
                Neo4jUtil.runCypherSql(nodeCypher);
                String relationShipCypher = String.format("UNWIND %s as row " + " MATCH (n:`%s` {name:row.SourceNode})"
                                + " MATCH (m:`%s` {name:row.TargetNode})" + " MERGE (n)-[:RE{name:row.RelationShip}]->(m)",
                        nodeStr, domain, domain);
                Neo4jUtil.runCypherSql(relationShipCypher);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Override
    public void batchUpdateGraphNodesCoordinate(String domain,List<NodeCoordinateItem> params) {
        try {
            if (params != null && params.size() > 0) {
                String nodeStr = Neo4jUtil.getFilterPropertiesJson(JsonHelper.toJSONString(params));
                String nodeCypher = String
                        .format("UNWIND %s as row " + " MATCH (n:`%s`)  where id(n)=row.uuid SET n.fx=row.fx,n.fy=row.fy", nodeStr, domain);
                Neo4jUtil.runCypherSql(nodeCypher);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * 批量导入csv
     *
     */
    @Override
    public void batchInsertByCsv(String domain, String csvUrl, int isCreateIndex) {
        String loadNodeCypher1 = null;
        String loadNodeCypher2 = null;
        String addIndexCypher = null;
        addIndexCypher = " CREATE INDEX ON :`" + domain + "`(name);";
        loadNodeCypher1 = " USING PERIODIC COMMIT 500 LOAD CSV FROM '" + csvUrl + "' AS line " + " MERGE (:`" + domain
                + "` {name:line[0]});";
        loadNodeCypher2 = " USING PERIODIC COMMIT 500 LOAD CSV FROM '" + csvUrl + "' AS line " + " MERGE (:`" + domain
                + "` {name:line[1]});";
        // 拼接生产关系导入cypher
        String loadRelCypher = null;
        String type = "RE";
        loadRelCypher = " USING PERIODIC COMMIT 500 LOAD CSV FROM  '" + csvUrl + "' AS line " + " MATCH (m:`" + domain
                + "`),(n:`" + domain + "`) WHERE m.name=line[0] AND n.name=line[1] " + " MERGE (m)-[r:" + type + "]->(n) "
                + "	SET r.name=line[2];";
        if(isCreateIndex==0){//已经创建索引的不能重新创建
            Neo4jUtil.runCypherSql(addIndexCypher);
        }
        Neo4jUtil.runCypherSql(loadNodeCypher1);
        Neo4jUtil.runCypherSql(loadNodeCypher2);
        Neo4jUtil.runCypherSql(loadRelCypher);
    }

    @Override
    public void updateNodeFileStatus(String domain, long nodeId, int status) {
        try {
            String nodeCypher = String.format("match (n:`%s`) where id(n)=%s set n.hasFile=%s ", domain, nodeId, status);
            Neo4jUtil.runCypherSql(nodeCypher);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void updateCoordinateOfNode(String domain, String uuid, Double fx, Double fy) {
        String cypher = null;
        if (fx == null && fy == null) {
            cypher = " MATCH (n:`" + domain + "`) where ID(n)=" + uuid
                    + " set n.fx=null, n.fy=null; ";
        } else {
            assert fx != null;
            if ("0.0".equals(fx.toString()) && "0.0".equals(fy.toString())) {
                cypher = " MATCH (n:`" + domain + "`) where ID(n)=" + uuid
                        + " set n.fx=null, n.fy=null; ";
            } else {
                cypher = " MATCH (n:`" + domain + "`) where ID(n)=" + uuid
                        + " set n.fx='" + fx + "', n.fy='" + fy + "';";
            }
        }
        Neo4jUtil.runCypherSql(cypher);
    }
}
