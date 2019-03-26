package com.warmer.kgmaker.dal.impl;

import com.alibaba.fastjson.JSON;
import com.warmer.kgmaker.dal.IKGraphRepository;
import com.warmer.kgmaker.entity.QAEntityItem;
import com.warmer.kgmaker.query.GraphQuery;
import com.warmer.kgmaker.util.GraphPageRecord;
import com.warmer.kgmaker.util.Neo4jUtil;
import com.warmer.kgmaker.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class KGraphRepository implements IKGraphRepository {
	@Autowired
	private Neo4jUtil neo4jUtil;

	/**
	 * 领域标签分页
	 * 
	 * @param queryItem
	 * @return
	 */
	public GraphPageRecord<HashMap<String, Object>> getPageDomain(GraphQuery queryItem) {
		GraphPageRecord<HashMap<String, Object>> resultRecord = new GraphPageRecord<HashMap<String, Object>>();
		try {
			String totalCountquery = "MATCH (n) RETURN count(distinct labels(n)) as count";
			int totalCount = 0;
			totalCount = neo4jUtil.executeScalar(totalCountquery);
			if (totalCount > 0) {
				int skipCount = (queryItem.getPageIndex() - 1) * queryItem.getPageSize();
				int limitCount = queryItem.getPageSize();
				String domainSql = String.format(
						"START n=node(*)  RETURN distinct labels(n) as domain,count(n) as nodecount order by nodecount desc SKIP %s LIMIT %s",
						skipCount, limitCount);
				List<HashMap<String, Object>> pageList = neo4jUtil.GetEntityList(domainSql);
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
	 * 
	 * @param domain
	 */
	public void deleteKGdomain(String domain) {
		try {
			String rSql = String.format("MATCH (n:`%s`) -[r]-(m)  delete r", domain);
			neo4jUtil.excuteCypherSql(rSql);
			String deleteNodeSql = String.format("MATCH (n:`%s`) delete n", domain);
			neo4jUtil.excuteCypherSql(deleteNodeSql);

		} catch (Exception e) {
			e.printStackTrace();

		}
	}

	/**
	 * 查询图谱节点和关系
	 * 
	 * @param query
	 * @return node relationship
	 */
	public HashMap<String, Object> getdomaingraph(GraphQuery query) {
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
				if (!StringUtil.isBlank(query.getNodename()) || !StringUtil.isBlank(cqr)) {

					if (!StringUtil.isBlank(query.getNodename())) {
						if (query.getMatchtype() == 1) {
							cqWhere = String.format("where n.name ='%s' ", query.getNodename());

						} else {
							cqWhere = String.format("where n.name contains('%s')", query.getNodename());
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
					HashMap<String, Object> graphNode = neo4jUtil.GetGraphNodeAndShip(nodeSql);
					Object node = graphNode.get("node");
					// 没有关系显示则显示节点
					if (node != null) {
						nr.put("node", graphNode.get("node"));
						nr.put("relationship", graphNode.get("relationship"));
					} else {
						String nodecql = String.format("MATCH (n:`%s`) %s RETURN distinct(n) limit %s", domain,
								nodeOnly, query.getPageSize());
						List<HashMap<String, Object>> nodeItem = neo4jUtil.GetGraphNode(nodecql);
						nr.put("node", nodeItem);
						nr.put("relationship", new ArrayList<HashMap<String, Object>>());
					}
				} else {
					String nodeSql = String.format("MATCH (n:`%s`) %s RETURN distinct(n) limit %s", domain, cqWhere,
							query.getPageSize());
					List<HashMap<String, Object>> graphNode = neo4jUtil.GetGraphNode(nodeSql);
					nr.put("node", graphNode);
					String domainSql = String.format("MATCH (n:`%s`)<-[r]-> (m) %s RETURN distinct(r) limit %s", domain,
							cqWhere, query.getPageSize());// m是否加领域
					List<HashMap<String, Object>> graphRelation = neo4jUtil.GetGraphRelationShip(domainSql);
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
	 * 
	 * @param domain
	 * @param pageIndex
	 * @param pageSize
	 * @return
	 */
	public HashMap<String, Object> getdomainnodes(String domain, Integer pageIndex, Integer pageSize) {
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
				ents = neo4jUtil.GetGraphNode(domainSql);
				for (HashMap<String, Object> hashMap : ents) {
					Object et = hashMap.get("entitytype");
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
	 * 
	 * @param domain
	 * @param nodeid
	 * @return long 数值
	 */
	public long getrelationnodecount(String domain, long nodeid) {
		long totalcount = 0;
		try {
			if (!StringUtil.isBlank(domain)) {
				String nodeSql = String.format("MATCH (n:`%s`) <-[r]->(m)  where id(n)=%s return count(m)", domain,
						nodeid);
				totalcount = neo4jUtil.GetGraphValue(nodeSql);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return totalcount;
	}

	/**
	 * 创建领域,默认创建一个新的节点,给节点附上默认属性
	 * 
	 * @param domain
	 */
	public void createdomain(String domain) {
		try {
			String cypherSql = String.format(
					"create (n:`%s`{entitytype:0,name:''}) return id(n)", domain);
			neo4jUtil.excuteCypherSql(cypherSql);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 获取/展开更多节点,找到和该节点有关系的节点
	 * 
	 * @param domain
	 * @param nodeid
	 * @return
	 */
	public HashMap<String, Object> getmorerelationnode(String domain, String nodeid) {
		HashMap<String, Object> result = new HashMap<String, Object>();
		try {
			String cypherSql = String.format("MATCH (n:`%s`) -[r]-(m) where id(n)=%s  return * limit 100", domain,
					nodeid);
			result = neo4jUtil.GetGraphNodeAndShip(cypherSql);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * 更新节点名称
	 * 
	 * @param domain
	 * @param nodeid
	 * @param nodename
	 * @return 修改后的节点
	 */
	public HashMap<String, Object> updatenodename(String domain, String nodeid, String nodename) {
		HashMap<String, Object> result = new HashMap<String, Object>();
		List<HashMap<String, Object>> graphNodeList = new ArrayList<HashMap<String, Object>>();
		try {
			String cypherSql = String.format("MATCH (n:`%s`) where id(n)=%s set n.name='%s' return n", domain, nodeid,
					nodename);
			graphNodeList = neo4jUtil.GetGraphNode(cypherSql);
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
	 * 
	 * @param domain
	 * @param entity
	 * @return
	 */
	public HashMap<String, Object> createnode(String domain, QAEntityItem entity) {
		HashMap<String, Object> rss = new HashMap<String, Object>();
		List<HashMap<String, Object>> graphNodeList = new ArrayList<HashMap<String, Object>>();
		try {
			if (entity.getUuid() != 0) {
				String sqlkeyval = neo4jUtil.getkeyvalCyphersql(entity);
				String cypherSql = String.format("match (n:`%s`) where id(n)=%s set %s return n", domain,
						entity.getUuid(), sqlkeyval);
				graphNodeList = neo4jUtil.GetGraphNode(cypherSql);
			} else {
				entity.setColor("#ff4500");// 默认颜色
				entity.setR(30);// 默认半径
				String propertiesString = neo4jUtil.getFilterPropertiesJson(JSON.toJSONString(entity));
				String cypherSql = String.format("create (n:`%s` %s) return n", domain, propertiesString);
				graphNodeList = neo4jUtil.GetGraphNode(cypherSql);
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
	 * 批量创建节点和关系
	 * 
	 * @param domain
	 *            领域
	 * @param sourcename
	 *            源节点
	 * @param relation
	 *            关系
	 * @param targetnames
	 *            目标节点数组
	 * @return
	 */
	public HashMap<String, Object> batchcreatenode(String domain, String sourcename, String relation,
			String[] targetnames) {
		HashMap<String, Object> rss = new HashMap<String, Object>();
		List<HashMap<String, Object>> nodes = new ArrayList<HashMap<String, Object>>();
		List<HashMap<String, Object>> ships = new ArrayList<HashMap<String, Object>>();
		try {
			String cypherSqlFmt = "create (n:`%s` {name:'%s',color:'#ff4500',r:30}) return n";
			String cypherSql = String.format(cypherSqlFmt, domain, sourcename);// 概念实体
			List<HashMap<String, Object>> graphNodeList = neo4jUtil.GetGraphNode(cypherSql);
			if (graphNodeList.size() > 0) {
				HashMap<String, Object> sourceNode = graphNodeList.get(0);
				nodes.add(sourceNode);
				String sourceuuid = String.valueOf(sourceNode.get("uuid"));
				for (String tn : targetnames) {
					String targetnodeSql = String.format(cypherSqlFmt, domain, tn);
					List<HashMap<String, Object>> targetNodeList = neo4jUtil.GetGraphNode(targetnodeSql);
					if (targetNodeList.size() > 0) {
						HashMap<String, Object> targetNode = targetNodeList.get(0);
						nodes.add(targetNode);
						String targetuuid = String.valueOf(targetNode.get("uuid"));
						String rSql = String.format(
								"match(n:`%s`),(m:`%s`) where id(n)=%s and id(m)=%s create (n)-[r:RE {name:'%s'}]->(m) return r",
								domain, domain, sourceuuid, targetuuid, relation);
						List<HashMap<String, Object>> rshipList = neo4jUtil.GetGraphRelationShip(rSql);
						ships.addAll(rshipList);
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
	 * @param domain
	 *            领域
	 * @param sourceid
	 *            源节点id
	 * @param entitytype
	 *            节点类型
	 * @param targetnames
	 *            目标节点名称数组
	 * @param relation
	 *            关系
	 * @return
	 */
	public HashMap<String, Object> batchcreatechildnode(String domain, String sourceid, Integer entitytype,
			String[] targetnames, String relation) {
		HashMap<String, Object> rss = new HashMap<String, Object>();
		List<HashMap<String, Object>> nodes = new ArrayList<HashMap<String, Object>>();
		List<HashMap<String, Object>> ships = new ArrayList<HashMap<String, Object>>();
		try {
			String cypherSqlFmt = "create (n:`%s`{name:'%s',color:'#ff4500',r:30}) return n";
			String cypherSql = String.format("match (n:`%s`) where id(n)=%s return n", domain, sourceid);
			List<HashMap<String, Object>> sourcenodeList = neo4jUtil.GetGraphNode(cypherSql);
			if (sourcenodeList.size() > 0) {
				nodes.addAll(sourcenodeList);
				for (String tn : targetnames) {
					String targetnodeSql = String.format(cypherSqlFmt, domain, tn);
					List<HashMap<String, Object>> targetNodeList = neo4jUtil.GetGraphNode(targetnodeSql);
					if (targetNodeList.size() > 0) {
						HashMap<String, Object> targetNode = targetNodeList.get(0);
						nodes.add(targetNode);
						String targetuuid = String.valueOf(targetNode.get("uuid"));
						// 创建关系
						String rSql = String.format(
								"match(n:`%s`),(m:`%s`) where id(n)=%s and id(m)=%s create (n)-[r:RE {name:'%s'}]->(m) return r",
								domain, domain, sourceid, targetuuid, relation);
						List<HashMap<String, Object>> shipList = neo4jUtil.GetGraphRelationShip(rSql);
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
	 * @param domain
	 *            领域
	 * @param entitytype
	 *            节点类型
	 * @param sourcenames
	 *            节点名称
	 * @return
	 */
	public List<HashMap<String, Object>> batchcreatesamenode(String domain, Integer entitytype, String[] sourcenames) {
		List<HashMap<String, Object>> rss = new ArrayList<HashMap<String, Object>>();
		try {
			String cypherSqlFmt = "create (n:`%s`{name:'%s',color:'#ff4500',r:30}) return n";
			for (String tn : sourcenames) {
				String sourcenodeSql = String.format(cypherSqlFmt, domain, tn, entitytype);
				List<HashMap<String, Object>> targetNodeList = neo4jUtil.GetGraphNode(sourcenodeSql);
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
	 * @param domain
	 *            领域
	 * @param sourceid
	 *            源节点id
	 * @param targetid
	 *            目标节点id
	 * @param ship
	 *            关系
	 * @return
	 */
	public HashMap<String, Object> createlink(String domain, long sourceid, long targetid, String ship) {
		HashMap<String, Object> rss = new HashMap<String, Object>();
		try {
			String cypherSql = String.format("MATCH (n:`%s`),(m:`%s`) WHERE id(n)=%s AND id(m) = %s "
					+ "CREATE (n)-[r:RE{name:'%s'}]->(m)" + "RETURN r", domain, domain, sourceid, targetid, ship);
			List<HashMap<String, Object>> cypherResult = neo4jUtil.GetGraphRelationShip(cypherSql);
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
	 * @param domain
	 *            领域
	 * @param shipid
	 *            关系id
	 * @param shipname
	 *            关系名称
	 * @return
	 */
	public HashMap<String, Object> updatelink(String domain, long shipid, String shipname) {
		HashMap<String, Object> rss = new HashMap<String, Object>();
		try {
			String cypherSql = String.format("MATCH (n:`%s`) -[r]->(m) where id(r)=%s set r.name='%s' return r", domain,
					shipid, shipname);
			List<HashMap<String, Object>> cypherResult = neo4jUtil.GetGraphRelationShip(cypherSql);
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
	 * @param domain
	 * @param nodeid
	 * @return
	 */
	public List<HashMap<String, Object>> deletenode(String domain, long nodeid) {
		List<HashMap<String, Object>> result = new ArrayList<HashMap<String, Object>>();
		try {
			String nSql = String.format("MATCH (n:`%s`)  where id(n)=%s return n", domain, nodeid);
			result = neo4jUtil.GetGraphNode(nSql);
			String rSql = String.format("MATCH (n:`%s`) <-[r]->(m) where id(n)=%s return r", domain, nodeid);
			neo4jUtil.GetGraphRelationShip(rSql);
			String deleteRelationSql = String.format("MATCH (n:`%s`) <-[r]->(m) where id(n)=%s delete r", domain, nodeid);
			neo4jUtil.excuteCypherSql(deleteRelationSql);
			String deleteNodeSql = String.format("MATCH (n:`%s`) where id(n)=%s delete n", domain, nodeid);
			neo4jUtil.excuteCypherSql(deleteNodeSql);
			return result;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * 删除关系
	 * 
	 * @param domain
	 * @param shipid
	 */
	public void deletelink(String domain, long shipid) {
		try {
			String cypherSql = String.format("MATCH (n:`%s`) -[r]->(m) where id(r)=%s delete r", domain, shipid);
			neo4jUtil.excuteCypherSql(cypherSql);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 段落识别出的三元组生成图谱
	 * 
	 * @param domain
	 * @param entitytype
	 * @param operatetype
	 * @param sourceid
	 * @param rss
	 *            关系三元组
	 *            [[startname;ship;endname],[startname1;ship1;endname1],[startname2;ship2;endname2]]
	 * @return node relationship
	 */
	public HashMap<String, Object> createGraphByText(String domain, Integer entitytype, Integer operatetype,
			Integer sourceid, String[] rss) {
		HashMap<String, Object> rsList = new HashMap<String, Object>();
		try {
			List<Object> nodeIds = new ArrayList<Object>();
			List<HashMap<String, Object>> nodeList = new ArrayList<HashMap<String, Object>>();
			List<HashMap<String, Object>> shipList = new ArrayList<HashMap<String, Object>>();

			if (rss != null && rss.length > 0) {
				for (String item : rss) {
					String[] ns = item.split(";");
					String nodestart = ns[0];
					String ship = ns[1];
					String nodeend = ns[2];
					String nodestartSql = String.format("MERGE (n:`%s`{name:'%s',entitytype:'%s'})  return n", domain,
							nodestart, entitytype);
					String nodeendSql = String.format("MERGE (n:`%s`{name:'%s',entitytype:'%s'})  return n", domain,
							nodeend, entitytype);
					// 创建初始节点
					List<HashMap<String, Object>> startNode = neo4jUtil.GetGraphNode(nodestartSql);
					// 创建结束节点
					List<HashMap<String, Object>> endNode = neo4jUtil.GetGraphNode(nodeendSql);
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
					if (sourceid != null && sourceid > 0 && operatetype == 2) {// 添加下级
						String shipSql = String.format(
								"MATCH (n:`%s`),(m:`%s`) WHERE id(n)=%s AND id(m) = %s "
										+ "CREATE (n)-[r:RE{name:'%s'}]->(m)" + "RETURN r",
								domain, domain, sourceid, startId, "");
						List<HashMap<String, Object>> shipResult = neo4jUtil.GetGraphRelationShip(shipSql);
						shipList.add(shipResult.get(0));
					}
					String shipSql = String.format("MATCH (n:`%s`),(m:`%s`) WHERE id(n)=%s AND id(m) = %s "
							+ "CREATE (n)-[r:RE{name:'%s'}]->(m)" + "RETURN r", domain, domain, startId, endId, ship);
					List<HashMap<String, Object>> shipResult = neo4jUtil.GetGraphRelationShip(shipSql);
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

	public void batchcreateGraph(String domain, List<Map<String, Object>> params) {
		try {
			if (params != null && params.size() > 0) {
				String nodeStr = neo4jUtil.getFilterPropertiesJson(JSON.toJSONString(params));
				String nodeCypher = String
						.format("UNWIND %s as row " + " MERGE (n:`%s` {name:row.SourceNode,source:row.Source})"
								+ " MERGE (m:`%s` {name:row.TargetNode,source:row.Source})", nodeStr, domain, domain);
				neo4jUtil.excuteCypherSql(nodeCypher);
				String relationShipCypher = String.format("UNWIND %s as row " + " MATCH (n:`%s` {name:row.SourceNode})"
						+ " MATCH (m:`%s` {name:row.TargetNode})" + " MERGE (n)-[:RE{name:row.RelationShip}]->(m)",
						nodeStr, domain, domain);
				neo4jUtil.excuteCypherSql(relationShipCypher);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 批量导入csv
	 * 
	 * @param domain
	 * @param csvUrl
	 * @param status
	 */
	public void batchInsertByCSV(String domain, String csvUrl, int status) {
		String loadNodeCypher1 = null;
		String loadNodeCypher2 = null;
		String addIndexCypher = null;
		addIndexCypher = " CREATE INDEX ON :" + domain + "(name);";
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
		neo4jUtil.excuteCypherSql(addIndexCypher);
		neo4jUtil.excuteCypherSql(loadNodeCypher1);
		neo4jUtil.excuteCypherSql(loadNodeCypher2);
		neo4jUtil.excuteCypherSql(loadRelCypher);
	}

	public void updateNodeFileStatus(String domain, long nodeId, int status) {
		try {
			String nodeCypher = String.format("match (n:`%s`) where id(n)=%s set n.hasfile=%s ", domain,nodeId, status);
			neo4jUtil.excuteCypherSql(nodeCypher);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	@Override
	public void updateCorrdOfNode(String domain, String uuid, Double fx, Double fy) {
		String cypher = null;
		if (fx == null && fy==null) {
			cypher = " MATCH (n:" + domain +") where ID(n)=" + uuid
					+ " set n.fx=null, n.fy=null; ";
		} else {
			if ("0.0".equals(fx.toString()) && "0.0".equals(fy.toString())) {
				cypher = " MATCH (n:" + domain +") where ID(n)=" + uuid
						+ " set n.fx=null, n.fy=null; ";
			} else {
				cypher = " MATCH (n:" + domain +") where ID(n)=" + uuid
						+ " set n.fx=" + fx + ", n.fy=" + fy + ";";
			}
		}
		neo4jUtil.excuteCypherSql(cypher);
	}
}
