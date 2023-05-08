package com.warmer.web.controller;

import com.warmer.base.util.R;
import com.warmer.web.entity.KgDomain;
import com.warmer.web.request.GraphItem;
import com.warmer.web.service.KgGraphNodeService;
import com.warmer.web.service.KGManagerService;
import com.warmer.web.service.impl.WorkFlowDirectorServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping(value = "/er")
public class ERBuilderController extends BaseController {
    @Autowired
    private KGManagerService kgService;

    @Autowired
    private KgGraphNodeService kgGraphNodeService;
    @Autowired
    WorkFlowDirectorServiceImpl workFlowDirectorService;
    @ResponseBody
    @PostMapping(value = "/saveData")
    public R<Map<String, Object>> saveDataSource(@RequestBody GraphItem submitItem) throws IOException {
        Map<String, Object> result = new HashMap<>();
        KgDomain domainItem = kgService.selectById(submitItem.getDomainId());
        if (domainItem == null) {
            return R.error("领域不存在");
        }
        kgGraphNodeService.createNode(submitItem);
        return R.success(result, "操作成功");
    }

    @ResponseBody
    @GetMapping(value = "/getDomainNode")
    public R<GraphItem> saveDataSource(Integer domainId) throws IOException {
        KgDomain domainItem = kgService.selectById(domainId);
        if (domainItem == null) {
            return R.error("领域不存在");
        }
        GraphItem domainNode = kgGraphNodeService.getDomainNode(domainId);
        return R.success(domainNode, "操作成功");
    }

    @ResponseBody
    @GetMapping(value = "/execute")
    public R<GraphItem> execute(Integer domainId){
        KgDomain domainItem = kgService.selectById(domainId);
        if (domainItem == null) {
            return R.error("领域不存在");
        }
         workFlowDirectorService.direct(domainId);
        return R.success();
    }
}
