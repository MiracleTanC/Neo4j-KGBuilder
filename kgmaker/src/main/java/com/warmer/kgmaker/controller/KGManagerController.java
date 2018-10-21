package com.warmer.kgmaker.controller;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.neo4j.driver.v1.Record;
import org.neo4j.driver.v1.StatementResult;
import org.neo4j.driver.v1.Value;
import org.neo4j.driver.v1.types.Node;
import org.neo4j.driver.v1.types.Relationship;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.warmer.kgmaker.entity.QAEntityItem;
import com.warmer.kgmaker.query.GraphQuery;
import com.warmer.kgmaker.util.GraphPageRecord;
import com.warmer.kgmaker.util.Neo4jUtil;
import com.warmer.kgmaker.util.R;
import com.warmer.kgmaker.util.StringUtil;


@Controller
@RequestMapping(value = "/kg")
public class KGManagerController extends BaseController {
	@Autowired
	private Neo4jUtil neo4jUtil;
	@GetMapping("/index")
	public String index(Model model) {
		return "kg/index";
	}
	@ResponseBody
	@RequestMapping(value = "/getgraph")
	public R<GraphPageRecord<HashMap<String, Object>>> getgraph(GraphQuery queryItem) {
		R<GraphPageRecord<HashMap<String, Object>>> result = new R<GraphPageRecord<HashMap<String, Object>>>();
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
			result.code = 200;
			result.setData(resultRecord);
		} catch (Exception e) {
			e.printStackTrace();
			result.code = 500;
			result.setMsg("服务器错误");
		}

		return result;
	}
	@GetMapping("/edit")
	public String edit(String domain, Model model) {
		model.addAttribute("domain", domain);
		return "kg/edit";
	}
	@ResponseBody
	@RequestMapping(value = "/getdomaingraph")
	public R<HashMap<String, Object>> getdomaingraph(String domain,String querynodename) {
		R<HashMap<String, Object>> result = new R<HashMap<String, Object>>();
		try {
			HashMap<String, Object> nr=new HashMap<String, Object>();
			if (!StringUtil.isBlank(domain)) {
				String nodeSql ="";
				String domainSql ="";
				if(!StringUtil.isBlank(querynodename)) {
					//MATCH (n:症状)-[RE]->(m) where n.name contains('哮喘') return n,m
					nodeSql = String.format("MATCH (n:`%s`) -[r]->(m) where n.name contains('%s') return n,m limit 500", domain);
					List<HashMap<String, Object>> graphNode = neo4jUtil.GetGraphNode(nodeSql);
					nr.put("node", graphNode);
					domainSql = String.format("MATCH (n:`%s`)-[r]-> (m) RETURN r limit 500", domain);//m是否加领域
					List<HashMap<String, Object>> graphRelation = neo4jUtil.GetGraphRelationShip(domainSql);
					nr.put("relationship", graphRelation);
				}
				else {
					nodeSql = String.format("MATCH (n:`%s`) RETURN * limit 500", domain);
					List<HashMap<String, Object>> graphNode = neo4jUtil.GetGraphNode(nodeSql);
					nr.put("node", graphNode);
					domainSql = String.format("MATCH (n:`%s`)-[r]-> (m) RETURN r limit 500", domain);//m是否加领域
					List<HashMap<String, Object>> graphRelation = neo4jUtil.GetGraphRelationShip(domainSql);
					nr.put("relationship", graphRelation);
				}
				
				result.code = 200;
				result.setData(nr);
			}
		} catch (Exception e) {
			e.printStackTrace();
			result.code = 500;
			result.setMsg("服务器错误");
		}
		return result;
	}
	@ResponseBody
	@RequestMapping(value = "/getdomainnodes")
	public R<HashMap<String, Object>> getdomainnodes(String domain,Integer entitytype) {
		R<HashMap<String, Object>> result = new R<HashMap<String, Object>>();
		HashMap<String, Object> resultItem=new HashMap<String, Object>();
		List<HashMap<String, Object>> ents = new ArrayList<HashMap<String, Object>>();
		List<HashMap<String, Object>> concepts = new ArrayList<HashMap<String, Object>>();
		List<HashMap<String, Object>> props = new ArrayList<HashMap<String, Object>>();
		List<HashMap<String, Object>> methods = new ArrayList<HashMap<String, Object>>();
		List<HashMap<String, Object>> entitys = new ArrayList<HashMap<String, Object>>();
		try {
			if (!StringUtil.isBlank(domain)) {
				String nodeSql="";
				if(entitytype!=null) {
					nodeSql= String.format("MATCH (n:%s) where n.entitytype=%s return n", domain,entitytype);
				}else {
					nodeSql = String.format("MATCH (n:%s) return n", domain);
				}
				StatementResult cypherResult =neo4jUtil.excuteCypherSql(nodeSql);
				if (cypherResult.hasNext()) {
					List<Record> records = cypherResult.list();
					for (Record recordItem : records) {
						HashMap<String, Object> rss = new HashMap<String, Object>();
						for (Value nodeValue : recordItem.values()) {
							String t = nodeValue.type().name();
							if (t.equals("NODE")) {// 结果里面只要类型为节点的值
								Node noe4jNode = nodeValue.asNode();
								Map<String, Object> map = noe4jNode.asMap();
								for (Entry<String, Object> entry : map.entrySet()) {
									String key = entry.getKey();
									rss.put(key, entry.getValue());
								}
							}
						}
						ents.add(rss);
					}
				}
				for (HashMap<String, Object> hashMap : ents) {
					Object et=hashMap.get("entitytype");
					if(et!=null) {
						int type=Integer.parseInt(et.toString());
						if(type==0) {
							concepts.add(hashMap);
						}else if(type==1) {
							entitys.add(hashMap);
						}
						else if(type==2) {
							props.add(hashMap);
						}else {
							methods.add(hashMap);
						}
					}
				}
				resultItem.put("concepts", concepts);
				resultItem.put("props", props);
				resultItem.put("methods", methods);
				resultItem.put("entitys", entitys);
				result.code = 200;
				result.setData(resultItem);
			}

		} catch (Exception e) {
			e.printStackTrace();
			result.code = 500;
			result.setMsg("服务器错误");
		}

		return result;
	}
	@ResponseBody
	@RequestMapping(value = "/getrelationnodecount")
	public R<String> getrelationnodecount(String domain,long nodeid) {
		R<String> result = new R<String>();
		try {
			long totalcount=0;
			if (!StringUtil.isBlank(domain)) {
				String nodeSql= String.format("MATCH (n:%s) <-[r]->(m)  where id(n)=%s return count(m)", domain,nodeid);
				totalcount =neo4jUtil.GetGraphValue(nodeSql);
				result.code = 200;
				result.setData(String.valueOf(totalcount));
			}
		} catch (Exception e) {
			e.printStackTrace();
			result.code = 500;
			result.setMsg("服务器错误");
		}
		return result;
	}
	@ResponseBody
	@RequestMapping(value = "/createdomain")
	public R<String> createdomain(String domain) {
		R<String> result = new R<String>();
		try {
			if (!StringUtil.isBlank(domain)) {
				String cypherSql= String.format("create (n:%s{entitytype:0,name:'节点0',filter:[],entitytype:'0',showstyle:'0'}) return id(n)", domain);
				StatementResult cypherResult = neo4jUtil.excuteCypherSql(cypherSql);
				if (cypherResult.hasNext()) {
					Record record = cypherResult.next();
					for (Value value : record.values()) {
						long uuid = value.asLong();
						String nodeSql = String.format("match (n:%s) where id(n)=%s set n.uuid=id(n) ", domain, uuid);
						neo4jUtil.excuteCypherSql(nodeSql);
					}
					result.code = 200;
					return result;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			result.code = 500;
			result.setMsg("服务器错误");
		}
		return result;
	}
	@ResponseBody
	@RequestMapping(value = "/createnode")
	public R<HashMap<String, Object>> createnode(QAEntityItem entity,HttpServletRequest request,HttpServletResponse response) {
		R<HashMap<String, Object>> result = new R<HashMap<String, Object>>();
		HashMap<String, Object> rss = new HashMap<String, Object>();
		try {
			String domain=request.getParameter("domain");
			if(entity.getUuid()!=0) {
				String sqlkeyval=neo4jUtil.getkeyvalCyphersql(entity);
				String cypherSql = String.format("match (n:%s) where id(n)=%s set %s return n", domain,entity.getUuid(), sqlkeyval);
				StatementResult nodeResult = neo4jUtil.excuteCypherSql(cypherSql);
				if (nodeResult.hasNext()) {
					Record nodeRecord = nodeResult.next();
					for (Value nodeValue : nodeRecord.values()) {
						String t = nodeValue.type().name();
						if (t.equals("NODE")) {// 结果里面只要类型为节点的值
							Node noe4jNode = nodeValue.asNode();
							Map<String, Object> map = noe4jNode.asMap();
							for (Entry<String, Object> entry : map.entrySet()) {
								String key = entry.getKey();
								rss.put(key, entry.getValue());
							}

						}
					}
					result.code = 200;
					result.setData(rss);
					return result;
				}
			}else {
				String propertiesString = neo4jUtil.getFilterPropertiesJson(JSON.toJSONString(entity));
				String cypherSql = String.format("create (n:%s%s) return id(n)", domain, propertiesString);
				StatementResult cypherResult = neo4jUtil.excuteCypherSql(cypherSql);
				if (cypherResult.hasNext()) {
					Record record = cypherResult.next();
					for (Value value : record.values()) {
						long uuid = value.asLong();
						String nodeSql = String.format("match (n:%s) where id(n)=%s set n.uuid=id(n) return n", domain, uuid);
						StatementResult nodeResult = neo4jUtil.excuteCypherSql(nodeSql);
						if (nodeResult.hasNext()) {
							Record nodeRecord = nodeResult.next();
							for (Value nodeValue : nodeRecord.values()) {
								String t = nodeValue.type().name();
								if (t.equals("NODE")) {// 结果里面只要类型为节点的值
									Node noe4jNode = nodeValue.asNode();
									Map<String, Object> map = noe4jNode.asMap();
									for (Entry<String, Object> entry : map.entrySet()) {
										String key = entry.getKey();
										rss.put(key, entry.getValue());
									}

								}
							}
						}
					}
					result.code = 200;
					result.setData(rss);
					return result;
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			result.code = 500;
			result.setMsg("服务器错误");
		}

		return result;
	}
	
	@ResponseBody
	@RequestMapping(value = "/createlink")
	public R<HashMap<String, Object>> createlink(String domain,long sourceid,long targetid,String ship) {
		R<HashMap<String, Object>> result = new R<HashMap<String, Object>>();
		HashMap<String, Object> rss = new HashMap<String, Object>();
		try {
			
			String cypherSql = String.format("MATCH (n:%s),(m:%s)\r\n" + 
					"WHERE id(n)=%s AND id(m) = %s \r\n" + 
					"CREATE (n)-[r:RE{name:'%s',uuid:0,sourceid:%s,targetid:%s }]->(m)\r\n" + 
					"RETURN id(r)", domain,domain,sourceid, targetid,ship,sourceid,targetid);
			StatementResult cypherResult = neo4jUtil.excuteCypherSql(cypherSql);
			if (cypherResult.hasNext()) {
				Record record = cypherResult.next();
				for (Value value : record.values()) {
					long uuid = value.asLong();
					String relationSql = String.format("match (n:%s)-[r]-(m) where id(r)=%s set r.uuid=id(r) return r", domain, uuid);
					StatementResult relationResult = neo4jUtil.excuteCypherSql(relationSql);
					Record relationRecord = relationResult.next();
					for (Value relationValue : relationRecord.values()) {
						String t = relationValue.type().name();
						if (t.equals("RELATIONSHIP")) {// 结果里面只要类型为节点的值
							Relationship rship=	relationValue.asRelationship();
							Map<String, Object> map = rship.asMap();
							for (Entry<String, Object> entry : map.entrySet()) {
								String key = entry.getKey();
								rss.put(key, entry.getValue());
							}

						}
					}
				}
				
				result.code = 200;
				result.setData(rss);
				return result;
			}
		} catch (Exception e) {
			e.printStackTrace();
			result.code = 500;
			result.setMsg("服务器错误");
		}

		return result;
	}
	@ResponseBody
	@RequestMapping(value = "/updatelink")
	public R<HashMap<String, Object>> updatelink(String domain,long shipid,String shipname) {
		R<HashMap<String, Object>> result = new R<HashMap<String, Object>>();
		HashMap<String, Object> rss = new HashMap<String, Object>();
		try {
			String cypherSql = String.format("MATCH (n:%s) -[r]->(m) where id(r)=%s set r.name='%s' return r", domain,shipid, shipname);
			StatementResult cypherResult = neo4jUtil.excuteCypherSql(cypherSql);
			if (cypherResult.hasNext()) {
				Record record = cypherResult.next();
				for (Value relationValue : record.values()) {
					String t = relationValue.type().name();
					if (t.equals("RELATIONSHIP")) {// 结果里面只要类型为节点的值
						Relationship rship=	relationValue.asRelationship();
						Map<String, Object> map = rship.asMap();
						for (Entry<String, Object> entry : map.entrySet()) {
							String key = entry.getKey();
							rss.put(key, entry.getValue());
						}

					}
				}
				result.code = 200;
				result.setData(rss);
				return result;
			}
		} catch (Exception e) {
			e.printStackTrace();
			result.code = 500;
			result.setMsg("服务器错误");
		}
		return result;
	}
	@ResponseBody
	@RequestMapping(value = "/deletenode")
	public R<List<HashMap<String, Object>>> deletenode(String domain,long nodeid) {
		R<List<HashMap<String, Object>>> result = new R<List<HashMap<String, Object>>>();
		List<HashMap<String, Object>> ents = new ArrayList<HashMap<String, Object>>();
		try {
			
			String deleteRelationSql = String.format("MATCH (n:%s) <-[r]->(m) where id(n)=%s delete r", domain,nodeid);
			neo4jUtil.excuteCypherSql(deleteRelationSql);
			String deleteNodeSql = String.format("MATCH (n:%s) where id(n)=%s delete n", domain,nodeid);
			neo4jUtil.excuteCypherSql(deleteNodeSql);
			result.code = 200;
			result.setData(ents);
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			result.code = 500;
			result.setMsg("服务器错误");
		}
		return result;
	}
	@ResponseBody
	@RequestMapping(value = "/deletelink")
	public R<HashMap<String, Object>> deletelink(String domain,long shipid) {
		R<HashMap<String, Object>> result = new R<HashMap<String, Object>>();
		HashMap<String, Object> rss = new HashMap<String, Object>();
		try {
			String cypherSql = String.format("MATCH (n:%s) -[r]->(m) where id(r)=%s delete r", domain,shipid);
			neo4jUtil.excuteCypherSql(cypherSql);
			result.code = 200;
			result.setData(rss);
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			result.code = 500;
			result.setMsg("服务器错误");
		}
		return result;
	}
}
