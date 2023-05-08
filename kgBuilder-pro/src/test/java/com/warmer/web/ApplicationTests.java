package com.warmer.web;

import com.warmer.base.util.Neo4jUtil;
import com.warmer.web.entity.KgDomain;
import com.warmer.web.service.KGGraphService;
import com.warmer.web.service.KGManagerService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@SpringBootTest
class ApplicationTests {

    @Autowired
    private KGGraphService kgGraphService;
    @Autowired
    private KGManagerService kgService;
    @Test
    void contextLoads() {
        List<KgDomain> domains = kgService.getDomains();
        int i=0;
        if(domains!=null&&domains.size()>0){
            List<HashMap<String, Object>> graphIndex = Neo4jUtil.getGraphIndex();
            for (KgDomain domainItem : domains) {
                String cypher=String.format("match(n:`%s`) return count(n)",domainItem.getName());
                long nodeCount = Neo4jUtil.getGraphValue(cypher);
                if(nodeCount<10){
                    System.out.println("正在清理："+domainItem.getName());
                    kgService.deleteDomain(domainItem.getId());
                    kgGraphService.deleteKGDomain(domainItem.getName());
                   // 删除索引
                    Neo4jUtil.deleteIndex(domainItem.getName());
                    //删除索引 drop index index_114254bd
                    List<HashMap<String, Object>> collect = graphIndex.stream().filter(n ->{
                        String[] labelsOrTypes = n.get("labelsOrTypes").toString().split(",");
                        List<String> labels = Arrays.asList(labelsOrTypes);
                        if(labels.contains(domainItem.getName())){
                            return true;
                        }
                        return false;
                    }).collect(Collectors.toList());
                    if(collect.size()>0){
                        HashMap<String, Object> indexMp = collect.get(0);
                        String indexName = indexMp.get("name").toString().replace("\"","");
                        String dropIndexCy=String.format("drop index %s",indexName);
                        Neo4jUtil.runCypherSql(dropIndexCy);
                    }

                    i++;
                }
            }
            System.out.println("清理完成,共清理"+i+"个标签");
        }
    }
    @Test
    void contextLoads2() {
        List<HashMap<String, Object>> domains = Neo4jUtil.getGraphLabels();
        int i=0;
        if(domains!=null&&domains.size()>0){
            List<HashMap<String, Object>> graphIndex = Neo4jUtil.getGraphIndex();
            for (HashMap<String, Object> domainItem : domains) {
                String label=domainItem.get("label").toString();
                String cypher=String.format("match(n:`%s`) return count(n)",label);
                long nodeCount = Neo4jUtil.getGraphValue(cypher);
                if(nodeCount<10){
                    // 删除索引
                    Neo4jUtil.deleteIndex(label);
//                    //删除索引 drop index index_114254bd
//                    List<HashMap<String, Object>> collect = graphIndex.stream().filter(n ->{
//                        String[] labelsOrTypes = n.get("labelsOrTypes").toString().split(",");
//                        List<String> labels = Arrays.asList(labelsOrTypes);
//                        if(labels.contains(domainItem.getName())){
//                            return true;
//                        }
//                        return false;
//                    }).collect(Collectors.toList());
//                    if(collect.size()>0){
//                        HashMap<String, Object> indexMp = collect.get(0);
//                        String indexName = indexMp.get("name").toString().replace("\"","");
//                        String dropIndexCy=String.format("drop index %s",indexName);
//                        Neo4jUtil.runCypherSql(dropIndexCy);
//                    }

                    i++;
                }
            }
            System.out.println("清理完成,共清理"+i+"个标签");
        }
    }
}
