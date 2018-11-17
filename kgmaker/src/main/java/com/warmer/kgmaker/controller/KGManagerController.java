package com.warmer.kgmaker.controller;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.neo4j.driver.v1.AuthTokens;
import org.neo4j.driver.v1.Driver;
import org.neo4j.driver.v1.GraphDatabase;
import org.neo4j.driver.v1.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.csvreader.CsvWriter;
import com.warmer.kgmaker.entity.QAEntityItem;
import com.warmer.kgmaker.query.GraphQuery;
import com.warmer.kgmaker.util.GraphPageRecord;
import com.warmer.kgmaker.util.Neo4jUtil;
import com.warmer.kgmaker.util.R;
import com.warmer.kgmaker.util.StringUtil;
import com.warmer.kgmaker.util.WebAppConfig;

@Controller
@RequestMapping(value = "/kg")
public class KGManagerController extends BaseController {
	@Autowired
	private Neo4jUtil neo4jUtil;
	@Autowired
	private WebAppConfig config;

	@GetMapping("/index")
	public String index(Model model) {
		return "kg/index";
	}

	@ResponseBody
	@RequestMapping(value = "/getgraph") // call db.labels
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

	@ResponseBody
	@RequestMapping(value = "/getdomaingraph")
	public R<HashMap<String, Object>> getdomaingraph(GraphQuery query) {
		R<HashMap<String, Object>> result = new R<HashMap<String, Object>>();
		try {
			HashMap<String, Object> nr = new HashMap<String, Object>();
			String domain = query.getDomain();
			if (!StringUtil.isBlank(domain)) {
				String cqWhere = "";
				if (!StringUtil.isBlank(query.getNodename())) {
					if (query.getMatchtype() == 1) {
						cqWhere = String.format("where n.name ='%s' ", query.getNodename());

					} else {
						cqWhere = String.format("where n.name contains('%s')", query.getNodename());
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
						String nodecql = String.format("MATCH (n:`%s`) %s RETURN distinct(n) limit %s", domain, cqWhere,
								query.getPageSize());
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
				result.code = 200;
				result.data = nr;
			}
		} catch (Exception e) {
			e.printStackTrace();
			result.code = 500;
			result.setMsg("服务器错误");
		}
		return result;
	}

	@ResponseBody
	@RequestMapping(value = "/getlabels")
	public R<List<HashMap<String, Object>>> getdomainnodes(String domain, Integer pageIndex, Integer pageSize) {
		R<List<HashMap<String, Object>>> result = new R<List<HashMap<String, Object>>>();
		try {
			String domaincql = "call db.labels";
			List<HashMap<String, Object>> pageList = neo4jUtil.GetEntityList(domaincql);
			result.code = 200;
			result.setData(pageList);
			;

		} catch (Exception e) {
			e.printStackTrace();
			result.code = 500;
			result.setMsg("服务器错误");
		}

		return result;
	}

	@ResponseBody
	@RequestMapping(value = "/getrelationnodecount")
	public R<String> getrelationnodecount(String domain, long nodeid) {
		R<String> result = new R<String>();
		try {
			long totalcount = 0;
			if (!StringUtil.isBlank(domain)) {
				String nodeSql = String.format("MATCH (n:%s) <-[r]->(m)  where id(n)=%s return count(m)", domain,
						nodeid);
				totalcount = neo4jUtil.GetGraphValue(nodeSql);
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
				String cypherSql = String.format("create (n:%s{name:'%s',color:'#ff4500',r:30}) return id(n)", domain,
						"");
				neo4jUtil.excuteCypherSql(cypherSql);
				result.code = 200;
			}
		} catch (Exception e) {
			e.printStackTrace();
			result.code = 500;
			result.setMsg("服务器错误");
		}
		return result;
	}

	@ResponseBody
	@RequestMapping(value = "/getmorerelationnode")
	public R<HashMap<String, Object>> getmorerelationnode(String domain, String nodeid) {
		R<HashMap<String, Object>> result = new R<HashMap<String, Object>>();
		try {
			if (!StringUtil.isBlank(domain)) {
				String cypherSql = String.format("MATCH (n:%s) -[r]-(m:%s) where id(n)=%s  return * limit 100", domain,
						domain, nodeid);
				HashMap<String, Object> graphModel = neo4jUtil.GetGraphNodeAndShip(cypherSql);
				if (graphModel != null) {
					result.code = 200;
					result.setData(graphModel);
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
	@RequestMapping(value = "/updatenodename")
	public R<HashMap<String, Object>> updatenodename(String domain, String nodeid, String nodename) {
		R<HashMap<String, Object>> result = new R<HashMap<String, Object>>();
		List<HashMap<String, Object>> graphNodeList = new ArrayList<HashMap<String, Object>>();
		try {
			if (!StringUtil.isBlank(domain)) {
				String cypherSql = String.format("MATCH (n:%s) where id(n)=%s set n.name='%s' return n", domain, nodeid,
						nodename);
				graphNodeList = neo4jUtil.GetGraphNode(cypherSql);
				if (graphNodeList.size() > 0) {
					result.code = 200;
					result.setData(graphNodeList.get(0));
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
	public R<HashMap<String, Object>> createnode(QAEntityItem entity, HttpServletRequest request,
			HttpServletResponse response) {
		R<HashMap<String, Object>> result = new R<HashMap<String, Object>>();
		HashMap<String, Object> rss = new HashMap<String, Object>();
		List<HashMap<String, Object>> graphNodeList = new ArrayList<HashMap<String, Object>>();
		try {
			String domain = request.getParameter("domain");
			if (entity.getUuid() != 0) {
				String sqlkeyval = neo4jUtil.getkeyvalCyphersql(entity);
				String cypherSql = String.format("match (n:%s) where id(n)=%s set %s return n", domain,
						entity.getUuid(), sqlkeyval);
				graphNodeList = neo4jUtil.GetGraphNode(cypherSql);
			} else {
				entity.setColor("#ff4500");// 默认颜色
				entity.setR(30);// 默认半径
				String propertiesString = neo4jUtil.getFilterPropertiesJson(JSON.toJSONString(entity));
				String cypherSql = String.format("create (n:%s%s) return n", domain, propertiesString);
				graphNodeList = neo4jUtil.GetGraphNode(cypherSql);
			}
			if (graphNodeList.size() > 0) {
				rss = graphNodeList.get(0);
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
	@RequestMapping(value = "/batchcreatenode")
	public R<HashMap<String, Object>> batchcreatenode(String domain, String sourcename, String[] targetnames,
			String relation) {
		R<HashMap<String, Object>> result = new R<HashMap<String, Object>>();
		HashMap<String, Object> rss = new HashMap<String, Object>();
		List<HashMap<String, Object>> nodes = new ArrayList<HashMap<String, Object>>();
		List<HashMap<String, Object>> ships = new ArrayList<HashMap<String, Object>>();
		try {
			String cypherSqlFmt = "create (n:%s{name:'%s',color:'#ff4500',r:30}) return n";
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
								"match(n:%s),(m:%s) where id(n)=%s and id(m)=%s create (n)-[r:RE {name:'%s'}]->(m) return r",
								domain, domain, sourceuuid, targetuuid, relation);
						List<HashMap<String, Object>> rshipList = neo4jUtil.GetGraphRelationShip(rSql);
						ships.addAll(rshipList);
					}

				}
			}
			rss.put("nodes", nodes);
			rss.put("ships", ships);
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

	@ResponseBody
	@RequestMapping(value = "/batchcreatechildnode")
	public R<HashMap<String, Object>> batchcreatechildnode(String domain, String sourceid, Integer entitytype,
			String[] targetnames, String relation) {
		R<HashMap<String, Object>> result = new R<HashMap<String, Object>>();
		HashMap<String, Object> rss = new HashMap<String, Object>();
		List<HashMap<String, Object>> nodes = new ArrayList<HashMap<String, Object>>();
		List<HashMap<String, Object>> ships = new ArrayList<HashMap<String, Object>>();
		try {
			String cypherSqlFmt = "create (n:%s{name:'%s',color:'#ff4500',r:30}) return n";
			String cypherSql = String.format("match (n:%s) where id(n)=%s return n", domain, sourceid);
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
								"match(n:%s),(m:%s) where id(n)=%s and id(m)=%s create (n)-[r:RE {name:'%s'}]->(m) return r",
								domain, domain, sourceid, targetuuid, relation);
						List<HashMap<String, Object>> shipList = neo4jUtil.GetGraphRelationShip(rSql);
						ships.addAll(shipList);
					}
				}
			}
			rss.put("nodes", nodes);
			rss.put("ships", ships);
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

	@ResponseBody
	@RequestMapping(value = "/batchcreatesamenode")
	public R<List<HashMap<String, Object>>> batchcreatesamenode(String domain, Integer entitytype,
			String[] sourcenames) {
		R<List<HashMap<String, Object>>> result = new R<List<HashMap<String, Object>>>();
		List<HashMap<String, Object>> rss = new ArrayList<HashMap<String, Object>>();
		try {
			String cypherSqlFmt = "create (n:%s{name:'%s',color:'#ff4500',r:30}) return n";
			for (String tn : sourcenames) {
				String sourcenodeSql = String.format(cypherSqlFmt, domain, tn);
				List<HashMap<String, Object>> targetNodeList = neo4jUtil.GetGraphNode(sourcenodeSql);
				rss.addAll(targetNodeList);
			}
			result.code = 200;
			result.setData(rss);
		} catch (Exception e) {
			e.printStackTrace();
			result.code = 500;
			result.setMsg("服务器错误");
		}

		return result;
	}

	@ResponseBody
	@RequestMapping(value = "/createlink")
	public R<HashMap<String, Object>> createlink(String domain, long sourceid, long targetid, String ship) {
		R<HashMap<String, Object>> result = new R<HashMap<String, Object>>();
		try {
			String cypherSql = String.format("MATCH (n:%s),(m:%s) WHERE id(n)=%s AND id(m) = %s "
					+ "CREATE (n)-[r:RE{name:'%s'}]->(m)" + "RETURN r", domain, domain, sourceid, targetid, ship);
			List<HashMap<String, Object>> cypherResult = neo4jUtil.GetGraphRelationShip(cypherSql);
			if (cypherResult.size() > 0) {
				HashMap<String, Object> rss = cypherResult.get(0);
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
	public R<HashMap<String, Object>> updatelink(String domain, long shipid, String shipname) {
		R<HashMap<String, Object>> result = new R<HashMap<String, Object>>();
		try {
			String cypherSql = String.format("MATCH (n:%s) -[r]->(m) where id(r)=%s set r.name='%s' return r", domain,
					shipid, shipname);
			List<HashMap<String, Object>> cypherResult = neo4jUtil.GetGraphRelationShip(cypherSql);
			if (cypherResult.size() > 0) {
				HashMap<String, Object> rss = cypherResult.get(0);
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
	public R<List<HashMap<String, Object>>> deletenode(String domain, long nodeid) {
		R<List<HashMap<String, Object>>> result = new R<List<HashMap<String, Object>>>();
		try {
			String rSql = String.format("MATCH (n:%s) <-[r]->(m) where id(n)=%s return r", domain, nodeid);
			List<HashMap<String, Object>> rList = neo4jUtil.GetGraphRelationShip(rSql);
			String deleteRelationSql = String.format("MATCH (n:%s) <-[r]->(m) where id(n)=%s delete r", domain, nodeid);
			neo4jUtil.excuteCypherSql(deleteRelationSql);
			String deleteNodeSql = String.format("MATCH (n:%s) where id(n)=%s delete n", domain, nodeid);
			neo4jUtil.excuteCypherSql(deleteNodeSql);
			result.code = 200;
			result.setData(rList);
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			result.code = 500;
			result.setMsg("服务器错误");
		}
		return result;
	}

	@ResponseBody
	@RequestMapping(value = "/deletedomain")
	public R<List<HashMap<String, Object>>> deletedomain(String domain) {
		R<List<HashMap<String, Object>>> result = new R<List<HashMap<String, Object>>>();
		try {
			String rSql = String.format("MATCH (n:%s) -[r]-(m)  delete r", domain);
			neo4jUtil.excuteCypherSql(rSql);
			String deleteNodeSql = String.format("MATCH (n:%s) delete n", domain);
			neo4jUtil.excuteCypherSql(deleteNodeSql);
			result.code = 200;
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
	public R<HashMap<String, Object>> deletelink(String domain, long shipid) {
		R<HashMap<String, Object>> result = new R<HashMap<String, Object>>();
		try {
			String cypherSql = String.format("MATCH (n:%s) -[r]->(m) where id(r)=%s delete r", domain, shipid);
			neo4jUtil.excuteCypherSql(cypherSql);
			result.code = 200;
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			result.code = 500;
			result.setMsg("服务器错误");
		}
		return result;
	}

	@ResponseBody
	@RequestMapping(value = "/importgraph")
	public JSONObject importgraph(@RequestParam(value = "file", required = true) MultipartFile file,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		JSONObject res = new JSONObject();
		String label = request.getParameter("domain");
		String path=config.getLocation();
		File fileItem = new File(path);
		if (!fileItem.exists()) {
			fileItem.mkdirs();
		}
		FileInputStream fileInputStream = (FileInputStream) file.getInputStream();
		BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(path + File.separator + file.getOriginalFilename()));
		byte[] bs = new byte[1024];
		int len;
		while ((len = fileInputStream.read(bs)) != -1) {
			bos.write(bs, 0, len);
		}
		bos.flush();
		bos.close();
		String csvUrl=config.getServerurl()+"/kg/download/"+file.getOriginalFilename();
		// 拼接生产节点导入cypher
		String loadNodeCypher1 = null;
		String loadNodeCypher2 = null;
		String addIndexCypher = null;
		addIndexCypher = " CREATE INDEX ON :" + label + "(name);";
		loadNodeCypher1 = " USING PERIODIC COMMIT 500 LOAD CSV FROM '" + csvUrl + "' AS line " + " MERGE (:" + label
				+ "{name:line[0]});";
		loadNodeCypher2 = " USING PERIODIC COMMIT 500 LOAD CSV FROM '" + csvUrl + "' AS line " + " MERGE (:" + label
				+ "{name:line[1]});";
		log.info(addIndexCypher);
		log.info(loadNodeCypher1);
		log.info(loadNodeCypher2);

		// 拼接生产关系导入cypher
		String loadRelCypher = null;
		String type = "RE";
		loadRelCypher = " USING PERIODIC COMMIT 500 LOAD CSV FROM  '" + csvUrl + "' AS line " + " MATCH (m:" + label
				+ "),(n:" + label + ") WHERE m.name=line[0] AND n.name=line[1] " + " MERGE (m)-[r:" + type + "]->(n) "
				+ "	SET r.name=line[2];";
		log.info(loadRelCypher);
		neo4jUtil.excuteCypherSql(addIndexCypher);
		neo4jUtil.excuteCypherSql(loadNodeCypher1);
		neo4jUtil.excuteCypherSql(loadNodeCypher2);
		neo4jUtil.excuteCypherSql(loadRelCypher);

		res.put("code", 200);
		res.put("message", "success!");
		return res;

	}
	@ResponseBody
	@RequestMapping(value = "/exportgraph")
	public JSONObject exportgraph(HttpServletRequest request, HttpServletResponse response) throws Exception {
		JSONObject res = new JSONObject();
		String label = request.getParameter("domain");
		String filePath = config.getLocation() ;
		String fileName=UUID.randomUUID()+".csv"; 
        String fileUrl = filePath+ File.separator + fileName;  
		String cypher=String.format("MATCH (n:%s) -[r]->(m:%s) return n.name as source,m.name as target,r.name as relation", label,label);
		List<HashMap<String, Object>> list=neo4jUtil.GetGraphItem(cypher);
		File file=new File(fileUrl);
		try{
			if(!file.exists()){
				file.createNewFile();
				System.out.println("文件不存在，新建成功！");
			}
			else{
				System.out.println("文件存在！");
			}
		}catch( Exception e){
			e.printStackTrace();
		}
		CsvWriter csvWriter = new CsvWriter(fileUrl, ',', Charset.forName("UTF-8"));
		 String[] header = { "source","target","relation"};
		 csvWriter.writeRecord(header);
		for (HashMap<String, Object> hashMap : list) {
			int colSize=hashMap.size();
			String[] cntArr = new String[colSize];
			cntArr[0] = hashMap.get("source").toString().replace("\"", "");
			cntArr[1] = hashMap.get("target").toString().replace("\"", "");
			cntArr[2] = hashMap.get("relation").toString().replace("\"", "");
			try {
				csvWriter.writeRecord(cntArr);
			} catch (IOException e) {
				log.error("CSVUtil->createFile: 文件输出异常" + e.getMessage());
			}
		}
		csvWriter.close();
		String csvUrl=config.getServerurl()+"/kg/download/"+fileName;
		res.put("code", 200);
		res.put("csvurl", csvUrl);
		res.put("message", "success!");
		return res;

	}
	//文件下载相关代码  
    @GetMapping(value = "/download/{filename}")  
    public String download(@PathVariable("filename")String filename,HttpServletRequest request, HttpServletResponse response) {  
		String filePath = config.getLocation() ;
        String fileUrl = filePath+ File.separator + filename;  
        if (fileUrl != null) {  
            File file = new File(fileUrl);  
            if (file.exists()) {  
                //response.setContentType("application/force-download");// 设置强制下载不打开  
                response.addHeader("Content-Disposition",  
                        "attachment;fileName=" + filename);// 设置文件名  
                byte[] buffer = new byte[1024];  
                FileInputStream fis = null;  
                BufferedInputStream bis = null;  
                try {  
                    fis = new FileInputStream(file);  
                    bis = new BufferedInputStream(fis);  
                    OutputStream os = response.getOutputStream();  
                    int i = bis.read(buffer);  
                    while (i != -1) {  
                        os.write(buffer, 0, i);  
                        i = bis.read(buffer);  
                    }  
                    System.out.println("success");  
                } catch (Exception e) {  
                    e.printStackTrace();  
                } finally {  
                    if (bis != null) {  
                        try {  
                            bis.close();  
                        } catch (IOException e) {  
                            e.printStackTrace();  
                        }  
                    }  
                    if (fis != null) {  
                        try {  
                            fis.close();  
                        } catch (IOException e) {  
                            e.printStackTrace();  
                        }  
                    }  
                }  
            }  
        }  
        return null;  
    }  

}
