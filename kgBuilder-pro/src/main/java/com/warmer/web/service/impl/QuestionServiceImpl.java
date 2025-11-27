package com.warmer.web.service.impl;

import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.seg.common.Term;
import com.warmer.base.util.Neo4jUtil;
import com.warmer.web.service.QuestionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class QuestionServiceImpl implements QuestionService {

    @Override
    public void showDictPath() {
        System.out.println("HanLP Data Root: " + HanLP.Config.CoreDictionaryPath);
    }

    @Override
    public String answer(String question) throws Exception {
        if (question == null || question.trim().isEmpty()) {
            return "问题不能为空";
        }

        // Simple segmentation using HanLP
        List<Term> terms = HanLP.segment(question);
        List<String> keywords = terms.stream().map(t -> t.word).collect(Collectors.toList());
        
        StringBuilder result = new StringBuilder();
        result.append("分词结果: ").append(String.join(" | ", keywords)).append("\n");

        boolean found = false;
        for (String keyword : keywords) {
             // Simple search: nodes with name containing keyword
             // Need to handle Cypher injection if this were production, but ok for now.
             String cypher = "MATCH (n) WHERE n.name CONTAINS '" + keyword.replace("'", "\\'") + "' RETURN n LIMIT 5";
             try {
                 List<HashMap<String, Object>> nodes = Neo4jUtil.getGraphNode(cypher);
                 if (nodes != null && !nodes.isEmpty()) {
                     found = true;
                     result.append("关键词 '").append(keyword).append("' 匹配到的节点:\n");
                     for (HashMap<String, Object> node : nodes) {
                         result.append(" - ").append(node.get("name"));
                         if (node.containsKey("detail")) {
                             result.append(": ").append(node.get("detail"));
                         }
                         result.append("\n");
                     }
                 }
             } catch (Exception e) {
                 log.warn("Query failed for keyword: " + keyword, e);
             }
        }
        
        if (!found) {
            result.append("未找到相关图谱信息。");
        } else {
            result.append("(智能问答功能演示)");
        }
        
        return result.toString();
    }
}
