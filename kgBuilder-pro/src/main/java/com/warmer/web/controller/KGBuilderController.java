package com.warmer.web.controller;

import cn.hutool.core.text.csv.CsvUtil;
import cn.hutool.core.text.csv.CsvWriter;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.IdUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.warmer.base.enums.ReturnStatus;
import com.warmer.base.util.*;
import com.warmer.web.config.WebAppConfig;
import com.warmer.web.entity.KgDomain;
import com.warmer.web.entity.KgFeedBack;
import com.warmer.web.entity.KgNodeDetail;
import com.warmer.web.entity.KgNodeDetailFile;
import com.warmer.web.model.NodeItem;
import com.warmer.web.request.*;
import com.warmer.web.service.FeedBackService;
import com.warmer.web.service.KGGraphService;
import com.warmer.web.service.KGManagerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import java.util.*;

/**
 * 知识图谱构建控制器
 * <p>
 * 提供知识图谱的增删改查、导入导出、可视化数据查询等接口。
 * </p>
 *
 * @author warmer
 */
@RestController
@RequestMapping(value = "/")
public class KGBuilderController extends BaseController {

    @Autowired
    private WebAppConfig config;
    @Autowired
    private KGGraphService kgGraphService;
    @Autowired
    private KGManagerService kgManagerService;
    @Autowired
    FeedBackService feedBackService;

    /**
     * 获取图谱标签列表。
     * <p>
     * 查询 MySQL 中存储的知识图谱领域列表，支持分页和条件过滤。
     * </p>
     *
     * @param queryItem 查询参数对象，包含分页索引、页大小、领域名称、类型等
     * @return 包含领域列表的分页响应结果 {@link R}
     */
    @PostMapping(value = "/getGraph") // call db.labels
    public R<GraphPageRecord<KgDomain>> getGraph(@RequestBody GraphQuery queryItem) {
        GraphPageRecord<KgDomain> resultRecord = new GraphPageRecord<KgDomain>();
        try {
            PageHelper.startPage(queryItem.getPageIndex(), queryItem.getPageSize(), true);
            List<KgDomain> domainList = kgManagerService.getDomainList(queryItem.getDomain(), queryItem.getType(), queryItem.getCommend());
            PageInfo<KgDomain> pageInfo = new PageInfo<KgDomain>(domainList);
            long total = pageInfo.getTotal();
            resultRecord.setPageIndex(queryItem.getPageIndex());
            resultRecord.setPageSize(queryItem.getPageSize());
            resultRecord.setTotalCount((int) total);
            resultRecord.setNodeList(pageInfo.getList());
            return R.success(resultRecord);
        } catch (Exception e) {
            e.printStackTrace();
            return R.error(e.getMessage());
        }
    }

    /**
     * 查询图谱结果。
     * <p>
     * 根据搜索条件查询相关的节点和关系数据，用于前端图形化展示。
     * </p>
     *
     * @param query 查询参数对象，包含领域、节点名称等
     * @return 包含节点和关系数据的 Map 对象 {@link R}
     */
    @PostMapping(value = "/queryGraphResult")
    public R<HashMap<String, Object>> queryGraphResult(@RequestBody GraphQuery query) {
        try {
            HashMap<String, Object> graphData = kgGraphService.queryGraphResult(query);
            return R.success(graphData);
        } catch (Exception e) {
            e.printStackTrace();
            return R.error(e.getMessage());
        }

    }

    /**
     * 执行 Cypher 查询。
     * <p>
     * 接收前端传入的 Cypher 语句并在 Neo4j 中执行，返回查询结果。
     * </p>
     *
     * @param cypher Cypher 查询语句
     * @return 包含查询结果的 Map 对象 {@link R}
     */
    @RequestMapping(value = "/getCypherResult")
    public R<HashMap<String, Object>> getCypherResult(String cypher) {
        try {
            HashMap<String, Object> graphData = Neo4jUtil.getGraphNodeAndShip(cypher);
            return R.success(graphData);
        } catch (Exception e) {
            e.printStackTrace();
            return R.error(e.getMessage());
        }
    }

    /**
     * 获取关联节点数量。
     * <p>
     * 获取某个领域下指定节点拥有的上下级节点总数。
     * </p>
     *
     * @param domain 领域名称
     * @param nodeId 节点 ID
     * @return 关联节点数量 {@link R}
     */
    @RequestMapping(value = "/getRelationNodeCount")
    public R<Long> getRelationNodeCount(String domain, long nodeId) {
        try {
            long totalCount = 0;
            if (!StringUtil.isBlank(domain)) {
                totalCount = kgGraphService.getRelationNodeCount(domain, nodeId);
                return R.success(totalCount);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return R.error(e.getMessage());
        }
        return R.error("操作失败");
    }

    /**
     * 创建领域。
     * <p>
     * 创建一个新的知识图谱领域（Label），并初始化相关数据。
     * </p>
     *
     * @param domain 领域名称（用户输入）
     * @param type   领域类型
     * @return 新创建的领域 ID {@link R}
     */
    @RequestMapping(value = "/createDomain")
    public R<Integer> createDomain(String domain, Integer type) {
        try {
            if (!StringUtil.isBlank(domain)) {
                List<KgDomain> domainItem = kgManagerService.getDomainByName(domain);
                if (domainItem.size() > 0) {
                    return R.create(ReturnStatus.Error, "领域已存在");
                } else {
                    String label=String.format("%s_%s",domain, IdUtil.nanoId(6));
                    int domainId = kgManagerService.quickCreateDomain(label,domain, type);// 保存到mysql
                    kgGraphService.createDomain(label);// 保存到图数据
                    return R.success(domainId);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return R.error(e.getMessage());
        }
        return R.success();
    }

    /**
     * 获取更多关联节点。
     * <p>
     * 分页或加载更多当前节点的下级关联节点。
     * </p>
     *
     * @param domain 领域名称
     * @param nodeId 节点 ID
     * @return 包含节点和关系的图谱数据 {@link R}
     */
    @RequestMapping(value = "/getMoreRelationNode")
    public R<HashMap<String, Object>> getMoreRelationNode(String domain, String nodeId) {
        try {
            if (!StringUtil.isBlank(domain)) {
                HashMap<String, Object> graphModel = kgGraphService.getMoreRelationNode(domain, nodeId);
                if (graphModel != null) {
                    return R.success(graphModel);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return R.error(e.getMessage());
        }
        return R.error("没有更多数据了");
    }

    /**
     * 更新节点名称。
     *
     * @param request 包含领域、节点ID和新名称的请求对象
     * @return 更新后的节点数据 {@link R}
     */
    @RequestMapping(value = "/updateNodeName")
    public R<HashMap<String, Object>> updateNodeName(@RequestBody KgNodeItem request) {
        HashMap<String, Object> graphNodeList = new HashMap<String, Object>();
        try {
            if (!StringUtil.isBlank(request.getDomain())) {
                graphNodeList = kgGraphService.updateNodeName(request.getDomain(), request.getNodeId(), request.getNodeName());
                if (graphNodeList.size() > 0) {
                    return R.success(graphNodeList);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return R.error(e.getMessage());
        }
        return R.error("操作失败");
    }

    /**
     * 更新节点坐标。
     *
     * @param request 包含节点坐标信息的提交对象
     * @return 响应结果 {@link R}
     */
    @RequestMapping(value = "/updateCoordinateOfNode")
    public R<String> updateCoordinateOfNode(@RequestBody NodeCoordinateSubmitItem request) {
        try {
            String domain = request.getDomain();
            List<NodeCoordinateItem> nodes = request.getNodes();
            kgGraphService.batchUpdateGraphNodesCoordinate(domain, nodes);
            return R.success();
        } catch (Exception e) {
            e.printStackTrace();
            return R.error(e.getMessage());
        }
    }

    /**
     * 创建节点。
     *
     * @param entity 节点对象，包含领域、标签、属性等信息
     * @return 创建成功的节点数据 {@link R}
     */
    @RequestMapping(value = "/createNode")
    public R<HashMap<String, Object>> createNode(@RequestBody NodeItem entity) {
        HashMap<String, Object> graphNode = new HashMap<String, Object>();
        try {
            graphNode = kgGraphService.createNode(entity.getDomain(), entity);
            if (graphNode != null && graphNode.size() > 0) {
                return R.success(graphNode);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return R.error(e.getMessage());
        }
        return R.error("操作失败");

    }

    /**
     * 批量创建节点。
     * <p>
     * 批量创建与源节点有指定关系的目标节点。
     * </p>
     *
     * @param request 批量创建请求对象
     * @return 创建结果 {@link R}
     */
    @RequestMapping(value = "/batchCreateNode")
    public R<HashMap<String, Object>> batchCreateNode(@RequestBody BatchCreateNodeItem request) {

        HashMap<String, Object> rss = new HashMap<String, Object>();
        try {
            String[] tNames = request.getTargetNames().split(",");
            rss = kgGraphService.batchCreateNode(request.getDomain(), request.getSourceName(), request.getRelation(), tNames);
            return R.success(rss);
        } catch (Exception e) {
            e.printStackTrace();

            return R.error(e.getMessage());
        }
    }

    /**
     * 批量创建子节点。
     * <p>
     * 为指定节点批量创建下级节点。
     * </p>
     *
     * @param request 批量创建请求对象
     * @return 创建结果 {@link R}
     */
    @RequestMapping(value = "/batchCreateChildNode")
    public R<HashMap<String, Object>> batchCreateChildNode(@RequestBody BatchCreateNodeItem request) {

        HashMap<String, Object> rss = new HashMap<String, Object>();
        try {
            String[] tNames = request.getTargetNames().split(",");
            rss = kgGraphService.batchCreateChildNode(request.getDomain(), request.getSourceId(), request.getEntityType(), tNames, request.getRelation());
            return R.success(rss);
        } catch (Exception e) {
            e.printStackTrace();

            return R.error(e.getMessage());
        }
    }

    /**
     * 批量创建同级节点。
     *
     * @param request 批量创建请求对象
     * @return 创建成功的节点列表 {@link R}
     */
    @RequestMapping(value = "/batchCreateSameNode")
    public R<List<HashMap<String, Object>>> batchCreateSameNode(@RequestBody BatchCreateNodeItem request) {
        List<HashMap<String, Object>> rss = new ArrayList<HashMap<String, Object>>();
        try {
            rss = kgGraphService.batchCreateSameNode(request.getDomain(), request.getEntityType(), request.getSourceNames());
            return R.success(rss);
        } catch (Exception e) {
            e.printStackTrace();
            return R.error(e.getMessage());
        }
    }

    /**
     * 创建连线。
     *
     * @param request 创建连线请求对象，包含源节点ID、目标节点ID、关系名称等
     * @return 创建成功的连线信息 {@link R}
     */
    @RequestMapping(value = "/createLink")
    public R<HashMap<String, Object>> createLink(@RequestBody CreateLinkItem request) {
        try {
            HashMap<String, Object> cypherResult = kgGraphService.createLink(request.getDomain(), request.getSourceId(), request.getTargetId(), request.getShip());
            return R.success(cypherResult);
        } catch (Exception e) {
            e.printStackTrace();
            return R.error(e.getMessage());
        }
    }

    /**
     * 更新连线信息。
     *
     * @param domain   领域名称
     * @param shipId   连线（关系）ID
     * @param shipName 新的连线名称
     * @return 更新后的连线信息 {@link R}
     */
    @RequestMapping(value = "/updateLink")
    public R<HashMap<String, Object>> updateLink(String domain, long shipId, String shipName) {
        try {
            HashMap<String, Object> cypherResult = kgGraphService.updateLink(domain, shipId, shipName);
            return R.success(cypherResult);
        } catch (Exception e) {
            e.printStackTrace();
            return R.error(e.getMessage());
        }

    }

    /**
     * 删除节点。
     * <p>
     * 删除指定节点及其相关关系。
     * </p>
     *
     * @param domain 领域名称
     * @param nodeId 节点 ID
     * @return 删除操作结果 {@link R}
     */
    @RequestMapping(value = "/deleteNode")
    public R<List<HashMap<String, Object>>> deleteNode(String domain, long nodeId) {
        try {
            List<HashMap<String, Object>> rList = kgGraphService.deleteNode(domain, nodeId);
            return R.success(rList);
        } catch (Exception e) {
            e.printStackTrace();
            return R.error(e.getMessage());
        }
    }

    /**
     * 删除领域。
     * <p>
     * 删除整个领域及其所有数据（慎用）。
     * </p>
     *
     * @param domainId 领域 ID
     * @param domain   领域名称
     * @return 响应结果 {@link R}
     */
    @RequestMapping(value = "/deleteDomain")
    public R<List<HashMap<String, Object>>> deleteDomain(Integer domainId, String domain) {
        try {
            kgManagerService.deleteDomain(domainId);
            kgGraphService.deleteKGDomain(domain);
            return R.success();
        } catch (Exception e) {
            e.printStackTrace();
            return R.error(e.getMessage());
        }
    }

    /**
     * 删除连线。
     *
     * @param domain 领域名称
     * @param shipId 连线 ID
     * @return 响应结果 {@link R}
     */
    @RequestMapping(value = "/deleteLink")
    public R<HashMap<String, Object>> deleteLink(String domain, long shipId) {
        try {
            kgGraphService.deleteLink(domain, shipId);
            return R.success();

        } catch (Exception e) {
            e.printStackTrace();
            return R.error(e.getMessage());
        }

    }

    /**
     * 导入图谱数据
     * 支持三元组导入和分类导入
     *
     * @param file    上传的Excel文件 (.xls 或 .xlsx)
     * @param request HttpServletRequest对象，包含domain(领域名称)和type(导入类型)参数
     * @return R<String> 操作结果
     */
    @RequestMapping(value = "/importGraph")
    public R<String> importGraph(@RequestParam(value = "file", required = true)
                                 @Validated @NotNull(message = "请上传有效的excel的文件") @Pattern(regexp = "^(?:\\w+\\.xlsx|\\w+\\.xls)$",
            message = "请上传有效的excel的文件")
                                 MultipartFile file,
                                 HttpServletRequest request) {
        try {
            String domain = request.getParameter("domain");
            Integer type = Integer.parseInt(request.getParameter("type"));
            List<KgDomain> domainList = kgManagerService.getDomainByName(domain);
            int domainExist = 0;
            if (domainList != null && domainList.size() > 0) {
                //导入已有图谱，更新图谱创建时间
                KgDomain domainItem = domainList.get(0);
                domainItem.setModifyTime(DateUtil.getDateNow());
                kgManagerService.updateDomain(domainItem);
                domainExist = 1;
            } else {
                kgManagerService.quickCreateDomain(domain,domain, type);// 三元组
            }
            if (type.equals(1)) {//三元组导入
                kgGraphService.importBySyz(file, request, domain, domainExist);
            } else {
                kgGraphService.importByCategory(file, request, domain);
            }
            return R.success("操作成功");
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
            return R.error("处理失败");
        }
    }

    /**
     * 导出图谱数据
     * 将图谱数据导出为CSV格式
     *
     * @param request HttpServletRequest对象，包含domain(领域名称)参数
     * @return Map<String, Object> 包含导出结果和下载链接
     */
    @RequestMapping(value = "/exportGraph")
    public Map<String, Object> exportGraph(HttpServletRequest request) {
        Map<String, Object> res = new HashMap<>();
        String label = request.getParameter("domain");
        String filePath = config.getLocation();
        String fileName = UUID.randomUUID() + ".csv";
        String fileUrl = filePath + fileName;
        String cypher = String.format(
                "MATCH (n:`%s`) -[r]->(m:`%s`) return n.name as source,m.name as target,r.name as relation", label, label);
        List<HashMap<String, Object>> list = Neo4jUtil.getGraphTable(cypher);
        if (list.size() == 0) {
            res.put("code", -1);
            res.put("message", "该领域没有任何有关系的实体!");
            return res;
        }
        try {
            CsvWriter csvWriter = CsvUtil.getWriter(fileUrl, CharsetUtil.CHARSET_UTF_8);
            String[] header = {"source", "target", "relation"};
            //写入表头
            csvWriter.write(header);
            for (HashMap<String, Object> hashMap : list) {
                int colSize = hashMap.size();
                String[] cntArr = new String[colSize];
                cntArr[0] = hashMap.get("source").toString().replace("\"", "");
                cntArr[1] = hashMap.get("target").toString().replace("\"", "");
                cntArr[2] = hashMap.get("relation").toString().replace("\"", "");
                csvWriter.write(cntArr);
            }
            csvWriter.close();
            String csvUrl = "/file/download/" + fileName;
            res.put("code", 200);
            res.put("fileName", csvUrl);
            res.put("message", "success!");
            return res;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return res;

    }

    /**
     * 获取节点关联的图片列表
     *
     * @param domainId 领域ID
     * @param nodeId   节点ID
     * @return R<List<KgNodeDetailFile>> 图片列表
     */
    @RequestMapping(value = "/getNodeImage")
    public R<List<KgNodeDetailFile>> getNodeImageList(int domainId, int nodeId) {
        try {
            List<KgNodeDetailFile> images = kgManagerService.getNodeImageList(domainId, nodeId);
            return R.success(images);
        } catch (Exception e) {
            e.printStackTrace();
            return R.error(e.getMessage());
        }
    }

    /**
     * 获取节点详细内容
     *
     * @param domainId 领域ID
     * @param nodeId   节点ID
     * @return R<KgNodeDetail> 节点详情对象
     */
    @RequestMapping(value = "/getNodeContent")
    public R<KgNodeDetail> getNodeContent(int domainId, int nodeId) {
        try {
            List<KgNodeDetail> contents = kgManagerService.getNodeContent(domainId, nodeId);
            if (contents != null && contents.size() > 0) {
                return R.success(contents.get(0));
            }
            return R.success(null);
        } catch (Exception e) {
            e.printStackTrace();
            return R.error(e.getMessage());
        }

    }

    /**
     * 获取节点综合详情（包含内容和图片）
     *
     * @param domainId 领域ID
     * @param nodeId   节点ID
     * @return R<Map<String, Object>> 包含content和imageList的Map
     */
    @RequestMapping(value = "/getNodeDetail")
    public R<Map<String, Object>> getNodeDetail(int domainId, int nodeId) {
        try {
            Map<String, Object> res = new HashMap<String, Object>();
            res.put("content", "");
            res.put("imageList", new String[]{});
            List<KgNodeDetail> contents = kgManagerService.getNodeContent(domainId, nodeId);
            if (contents != null && contents.size() > 0) {
                res.replace("content", contents.get(0).getContent());
            }
            List<KgNodeDetailFile> images = kgManagerService.getNodeImageList(domainId, nodeId);
            if (images != null && images.size() > 0) {
                res.replace("imageList", images);
            }
            return R.success(res);
        } catch (Exception e) {
            e.printStackTrace();
            return R.error(e.getMessage());
        }
    }

    /**
     * 提交用户反馈
     *
     * @param submitItem 反馈信息实体
     * @return R<Map<String, Object>> 操作结果
     */
    @RequestMapping(value = "/feedBack")
    public R<Map<String, Object>> feedBack(KgFeedBack submitItem) {
        try {
            feedBackService.insert(submitItem);
            return R.success();
        } catch (Exception e) {
            e.printStackTrace();
            return R.error(e.getMessage());
        }
    }

    /**
     * 保存或更新节点图片
     *
     * @param params 包含domainId, nodeId, imagePath的Map
     * @return R<String> 操作结果
     */
    @RequestMapping(value = "/saveNodeImage")
    public R<String> saveNodeImage(@RequestBody Map<String, Object> params) {
        try {
            int domainId = (int) params.get("domainId");
            String nodeId = params.get("nodeId").toString();
            String imagePath = params.get("imagePath").toString();
            List<KgDomain> domainList = kgManagerService.getDomainById(domainId);
            if (domainList != null && domainList.size() > 0) {
                String domainName = domainList.get(0).getName();
                if (StringUtil.isNotBlank(imagePath)) {
                    List<Map<String, Object>> submitItemList = new ArrayList<Map<String, Object>>();
                    Map<String, Object> sb = new HashMap<String, Object>();
                    sb.put("file", imagePath);
                    sb.put("imageType", 0);
                    sb.put("domainId", domainId);
                    sb.put("nodeId", nodeId);
                    sb.put("status", 1);
                    sb.put("createUser", "tc");
                    sb.put("createTime", DateUtil.getDateNow());
                    submitItemList.add(sb);
                    kgManagerService.deleteNodeImage(domainId, Integer.parseInt(nodeId));
                    kgManagerService.saveNodeImage(submitItemList);
                    // 更新到图数据库,表明该节点有附件,加个标识,0=没有,1=有
                    kgGraphService.updateNodeImg(domainName, Long.parseLong(nodeId), imagePath);
                    return R.success("操作成功");
                } else {
                    kgManagerService.deleteNodeImage(domainId, Integer.parseInt(nodeId));
                    kgGraphService.removeNodeImg(domainName, Long.parseLong(nodeId));
                    return R.success("操作成功");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return R.error(e.getMessage());
        }
        return R.error("操作失败");
    }

    /**
     * 保存或更新节点正文内容。
     * <p>
     * 前端提交节点正文内容（富文本），若已存在则更新，否则新增。
     * 同步在图数据库中标记该节点拥有附件（fileStatus=1）。
     * </p>
     *
     * @param params 参数 Map，包含：
     *               domainId(int) 领域 ID，nodeId(string) 节点 ID，content(string) 正文内容
     * @return 操作结果描述
     */
    @RequestMapping(value = "/saveNodeContent")
    public R<String> saveNodeContent(@RequestBody Map<String, Object> params) {
        try {
            String username = "tc";
            int domainId = (int) params.get("domainId");
            String nodeId = params.get("nodeId").toString();
            String content = params.get("content").toString();
            List<KgDomain> domainList = kgManagerService.getDomainById(domainId);
            if (domainList != null && domainList.size() > 0) {
                String domainName = domainList.get(0).getName();
                // 检查是否存在
                List<KgNodeDetail> items = kgManagerService.getNodeContent(domainId, Integer.parseInt(nodeId));
                if (items != null && items.size() > 0) {
                    KgNodeDetail oldItem = items.get(0);
                    Map<String, Object> item = new HashMap<String, Object>();
                    item.put("domainId", oldItem.getDomainId());
                    item.put("nodeId", oldItem.getNodeId());
                    item.put("content", content);
                    item.put("modifyUser", username);
                    item.put("modifyTime", DateUtil.getDateNow());
                    kgManagerService.updateNodeContent(item);
                    return R.success("更新成功");
                } else {
                    Map<String, Object> sb = new HashMap<String, Object>();
                    sb.put("content", content);
                    sb.put("domainId", domainId);
                    sb.put("nodeId", nodeId);
                    sb.put("status", 1);
                    sb.put("createUser", username);
                    sb.put("createTime", DateUtil.getDateNow());
                    if (sb.size() > 0) {
                        kgManagerService.saveNodeContent(sb);
                        return R.success("保存成功");
                    }
                }
                // 更新到图数据库,表明该节点有附件,加个标识,0=没有,1=有
                kgGraphService.updateNodeFileStatus(domainName, Long.parseLong(nodeId), 1);
            }

        } catch (Exception e) {
            e.printStackTrace();
            return R.error(e.getMessage());
        }
        return R.error("操作失败");
    }

}
