package com.warmer.web.service.impl;

import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.text.csv.*;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.IdUtil;
import com.warmer.base.util.ExcelUtil;
import com.warmer.web.config.WebAppConfig;
import com.warmer.base.util.GraphPageRecord;
import com.warmer.base.util.StringUtil;
import com.warmer.web.dao.impl.KGGraphRepository;
import com.warmer.web.entity.CategoryNode;
import com.warmer.web.model.NodeItem;
import com.warmer.web.model.TreeExcel;
import com.warmer.web.model.TreeExcelRecordData;
import com.warmer.web.request.GraphQuery;

import com.warmer.web.request.NodeCoordinateItem;
import com.warmer.web.service.CategoryNodeService;
import com.warmer.web.service.KGGraphService;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletRequest;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class KGGraphServiceImpl implements KGGraphService {

    @Autowired
    private KGGraphRepository kgRepository;
    @Autowired
    private WebAppConfig config;
    @Autowired
    CategoryNodeService categoryNodeService;
    @Override
    public GraphPageRecord<HashMap<String, Object>> getPageDomain(GraphQuery queryItem) {
        return kgRepository.getPageDomain(queryItem);
    }

    /**
     * 删除图谱领域
     *
     * @param domain 领域名称
     */
    @Override
    public void deleteKGDomain(String domain) {
        kgRepository.deleteKgDomain(domain);
    }

    /**
     * 查询图谱结果
     *
     * @param query 查询条件对象
     * @return HashMap<String, Object> 图谱节点和关系数据
     */
    @Override
    public HashMap<String, Object> queryGraphResult(GraphQuery query) {
        return kgRepository.queryGraphResult(query);
    }

    /**
     * 分页获取领域下的节点
     *
     * @param domain    领域名称
     * @param pageIndex 当前页码
     * @param pageSize  每页数量
     * @return HashMap<String, Object> 节点列表数据
     */
    @Override
    public HashMap<String, Object> getdomainnodes(String domain, Integer pageIndex, Integer pageSize) {
        return kgRepository.getDomainNodes(domain, pageIndex, pageSize);
    }

    /**
     * 获取指定节点的关联节点数量
     *
     * @param domain 领域名称
     * @param nodeId 节点ID
     * @return long 关联节点总数
     */
    @Override
    public long getRelationNodeCount(String domain, long nodeId) {
        return kgRepository.getRelationNodeCount(domain, nodeId);
    }

    /**
     * 创建新的领域
     *
     * @param domain 领域名称
     */
    @Override
    public void createDomain(String domain) {
        kgRepository.createDomain(domain);
    }

    /**
     * 快速创建领域（带默认节点）
     *
     * @param domain   领域名称
     * @param nodeName 默认节点名称
     */
    @Override
    public void quickCreateDomain(String domain, String nodeName) {
        kgRepository.quickCreateDomain(domain,nodeName);
    }

    /**
     * 获取更多关联节点（展开节点）
     *
     * @param domain 领域名称
     * @param nodeId 节点ID
     * @return HashMap<String, Object> 更多关联节点和关系
     */
    @Override
    public HashMap<String, Object> getMoreRelationNode(String domain, String nodeId) {
        return kgRepository.getMoreRelationNode(domain, nodeId);
    }

    /**
     * 更新节点名称
     *
     * @param domain   领域名称
     * @param nodeId   节点ID
     * @param nodeName 新节点名称
     * @return HashMap<String, Object> 更新结果
     */
    @Override
    public HashMap<String, Object> updateNodeName(String domain, String nodeId, String nodeName) {
        return kgRepository.updateNodeName(domain, nodeId, nodeName);
    }

    /**
     * 创建单个节点
     *
     * @param domain 领域名称
     * @param entity 节点对象
     * @return HashMap<String, Object> 创建的节点信息
     */
    @Override
    public HashMap<String, Object> createNode(String domain, NodeItem entity) {
        return kgRepository.createNode(domain, entity);
    }

    /**
     * 批量创建节点（通过源节点和关系）
     *
     * @param domain      领域名称
     * @param sourceName  源节点名称
     * @param relation    关系名称
     * @param targetNames 目标节点名称数组
     * @return HashMap<String, Object> 创建结果
     */
    @Override
    public HashMap<String, Object> batchCreateNode(String domain, String sourceName, String relation,
                                                   String[] targetNames) {
        return kgRepository.batchCreateNode(domain, sourceName, relation, targetNames);
    }

    /**
     * 批量创建子节点
     *
     * @param domain      领域名称
     * @param sourceId    源节点ID
     * @param entityType  实体类型
     * @param targetNames 目标节点名称数组
     * @param relation    关系名称
     * @return HashMap<String, Object> 创建结果
     */
    @Override
    public HashMap<String, Object> batchCreateChildNode(String domain, String sourceId, Integer entityType,
                                                        String[] targetNames, String relation) {
        return kgRepository.batchCreateChildNode(domain, sourceId, entityType, targetNames, relation);
    }

    /**
     * 批量创建同类节点
     *
     * @param domain      领域名称
     * @param entityType  实体类型
     * @param sourceNames 节点名称数组
     * @return List<HashMap<String, Object>> 创建的节点列表
     */
    @Override
    public List<HashMap<String, Object>> batchCreateSameNode(String domain, Integer entityType, String[] sourceNames) {
        return kgRepository.batchCreateSameNode(domain, entityType, sourceNames);
    }

    /**
     * 创建关系（连线）
     *
     * @param domain   领域名称
     * @param sourceId 源节点ID
     * @param targetId 目标节点ID
     * @param ship     关系名称
     * @return HashMap<String, Object> 创建的关系信息
     */
    @Override
    public HashMap<String, Object> createLink(String domain, long sourceId, long targetId, String ship) {
        return kgRepository.createLink(domain, sourceId, targetId, ship);
    }

    /**
     * 更新关系（连线）名称
     *
     * @param domain   领域名称
     * @param shipId   关系ID
     * @param shipName 新关系名称
     * @return HashMap<String, Object> 更新后的关系信息
     */
    @Override
    public HashMap<String, Object> updateLink(String domain, long shipId, String shipName) {
        return kgRepository.updateLink(domain, shipId, shipName);
    }

    /**
     * 删除节点（级联删除关系）
     *
     * @param domain 领域名称
     * @param nodeId 节点ID
     * @return List<HashMap<String, Object>> 删除结果
     */
    @Override
    public List<HashMap<String, Object>> deleteNode(String domain, long nodeId) {
        return kgRepository.deleteNode(domain, nodeId);
    }

    /**
     * 删除关系
     *
     * @param domain 领域名称
     * @param shipId 关系ID
     */
    @Override
    public void deleteLink(String domain, long shipId) {
        kgRepository.deleteLink(domain, shipId);
    }

    /**
     * 根据文本生成图谱（三元组）
     *
     * @param domain      领域名称
     * @param entityType  实体类型
     * @param operateType 操作类型
     * @param sourceId    源节点ID
     * @param rss         三元组关系数组
     * @return HashMap<String, Object> 生成的图谱数据
     */
    @Override
    public HashMap<String, Object> createGraphByText(String domain, Integer entityType, Integer operateType,
                                                     Integer sourceId, String[] rss) {
        return kgRepository.createGraphByText(domain, entityType, operateType, sourceId, rss);
    }

    /**
     * 批量创建图谱
     *
     * @param domain 领域名称
     * @param params 参数列表（包含sourceNode, relationship, targetNode）
     */
    @Override
    public void batchCreateGraph(String domain, List<Map<String, Object>> params) {
        kgRepository.batchCreateGraph(domain, params);
    }

    /**
     * 更新节点文件状态
     *
     * @param domain 领域名称
     * @param nodeId 节点ID
     * @param status 状态值
     */
    @Override
    public void updateNodeFileStatus(String domain, long nodeId, int status) {
        kgRepository.updateNodeFileStatus(domain,nodeId,status);
    }

    /**
     * 更新节点图片
     *
     * @param domain 领域名称
     * @param nodeId 节点ID
     * @param img    图片路径或URL
     */
    @Override
    public void updateNodeImg(String domain, long nodeId, String img) {
        kgRepository.updateNodeImg(domain,nodeId,img);
    }

    /**
     * 移除节点图片
     *
     * @param domain 领域名称
     * @param nodeId 节点ID
     */
    @Override
    public void removeNodeImg(String domain, long nodeId) {

    }

    /**
     * 更新节点坐标
     *
     * @param domain 领域名称
     * @param uuid   节点UUID
     * @param fx     X坐标
     * @param fy     Y坐标
     */
    @Override
    public void updateCoordinateOfNode(String domain, String uuid, Double fx, Double fy) {
        kgRepository.updateCoordinateOfNode(domain,uuid,fx,fy);
    }

    /**
     * 批量更新节点坐标
     *
     * @param domain 领域名称
     * @param nodes  节点坐标列表
     */
    @Override
    public void batchUpdateGraphNodesCoordinate(String domain, List<NodeCoordinateItem> nodes) {
        kgRepository.batchUpdateGraphNodesCoordinate(domain,nodes);
    }

    /**
     * 批量导入CSV数据
     *
     * @param domain 领域名称
     * @param csvUrl CSV文件URL
     * @param status 状态
     */
    @Override
    public void batchInsertByCSV(String domain, String csvUrl, int status) {
        kgRepository.batchInsertByCsv(domain, csvUrl, status);
    }

    /**
     * 导入三元组数据
     * <p>
     * 解析上传的Excel/CSV文件，转换为CSV格式并保存到服务器，
     * 然后调用批量导入接口将数据导入Neo4j。
     * </p>
     *
     * @param file          上传的文件
     * @param request       HttpServletRequest对象
     * @param label         领域标签
     * @param isCreateIndex 是否创建索引
     * @throws Exception 处理过程中的异常
     */
    @Override
    public void importBySyz(MultipartFile file,HttpServletRequest request,String label,Integer isCreateIndex) throws Exception {
        List<Map<String, Object>> dataList = getFormatData(file);
        String filename = IdUtil.getSnowflakeNextIdStr()+ ".csv";
        String fullFileName = config.getLocation()+filename;
        CsvWriter writer = CsvUtil.getWriter(fullFileName, CharsetUtil.CHARSET_UTF_8);
        for (Map<String, Object> item : dataList) {
            String[] lst = new String[3];
            lst[0]=item.get("sourceNode").toString();
            lst[1]=item.get("targetNode").toString();
            lst[2]=item.get("relationship").toString();
            writer.write(lst);
        }
        String serverUrl=request.getServerName() + ":" + request.getServerPort() + request.getContextPath();
        String csvUrl = "http://"+serverUrl+ "/file/download/" + filename;
        batchInsertByCSV(label, csvUrl, isCreateIndex);
    }

    /**
     * 解析上传文件数据
     * <p>
     * 支持 .xls, .xlsx, .csv 格式。
     * 提取文件中的前三列作为 sourceNode, targetNode, relationship。
     * </p>
     *
     * @param file 上传的文件
     * @return List<Map<String, Object>> 解析后的数据列表
     * @throws Exception 解析异常
     */
    private List<Map<String, Object>> getFormatData(MultipartFile file) throws Exception {
        List<Map<String, Object>> mapList = new ArrayList<>();
        try {
            String fileName = file.getOriginalFilename();
            assert fileName != null;
            if (!fileName.endsWith(".csv")) {
                Workbook workbook = null;
                if (ExcelUtil.isExcel2007(fileName)) {
                    workbook = new XSSFWorkbook(file.getInputStream());
                } else {
                    workbook = new HSSFWorkbook(file.getInputStream());
                }
                // 有多少个sheet
                int sheets = workbook.getNumberOfSheets();
                for (int i = 0; i < sheets; i++) {
                    Sheet sheet = workbook.getSheetAt(i);
                    int rowSize = sheet.getPhysicalNumberOfRows();
                    for (int j = 0; j < rowSize; j++) {
                        Row row = sheet.getRow(j);
                        if(row==null) continue;
                        int cellSize = row.getPhysicalNumberOfCells();
                        if (cellSize != 3) continue; //只读取3列
                        row.getCell(0);
                        Cell cell0 = row.getCell(0);//节点1
                        row.getCell(1);
                        Cell cell1 = row.getCell(1);//节点2
                        row.getCell(2);
                        Cell cell2 = row.getCell(2);//关系
                        if (null == cell0 || null == cell1 || null == cell2) {
                            continue;
                        }
                        String sourceNode = cell0.getStringCellValue();
                        String targetNode = cell1.getStringCellValue();
                        String relationShip = cell2.getStringCellValue();
                        if (StringUtil.isBlank(sourceNode) || StringUtils.isBlank(targetNode) || StringUtils.isBlank(relationShip))
                            continue;
                        Map<String, Object> map = new HashMap<String, Object>();
                        map.put("sourceNode", sourceNode);
                        map.put("targetNode", targetNode);
                        map.put("relationship", relationShip);
                        mapList.add(map);
                    }
                }
            } else if (fileName.endsWith(".csv")) {
                CsvReader reader = CsvUtil.getReader();
                String filename = config.getLocation()+IdUtil.getSnowflakeNextIdStr()+ ".csv";
                File fileTemp = new File(filename);
                FileUtils.copyInputStreamToFile(file.getInputStream(), fileTemp);
                CsvData data = reader.read(fileTemp);
                for (CsvRow csvRow : data) {
                    List<String> lst = csvRow.getRawList();
                    Map<String, Object> map = new HashMap<String, Object>();
                    map.put("sourceNode", lst.get(0));
                    map.put("targetNode", lst.get(1));
                    map.put("relationship", lst.get(2));
                    mapList.add(map);
                }

            }
        } catch (Exception ex) {
            throw new Exception(ex);
        }
        return mapList;
    }

    /**
     * 导入分类数据（树形结构）
     * <p>
     * 解析 Excel 文件并按层级构建分类树：
     * - 每个单元格可通过 "节点名称###关系" 指定与父节点的关系标签，仅识别第一组关系；
     * - 顶层节点 `treeLevel` 置为 0，非顶层按父节点层级 +1；
     * - 若父节点原为叶子，则在插入子节点后更新其叶子状态为非叶；
     * - 为每个分类节点创建对应的图谱节点（携带颜色），并按父子关系创建图谱连线；
     * - 空行、缺失单元格或无效值将被跳过。
     * </p>
     *
     * 边界与异常处理：
     * - 空 Sheet 或无数据时不执行任何写入；
     * - `split("###")` 仅在存在分隔符时解析关系名，缺失则按空关系处理；
     * - 字段 `parentId` 不存在时按根节点处理，避免空指针；
     * - 颜色值按原样透传，不校验格式；
     *
     * @param file    上传的文件（支持 xls/xlsx）
     * @param request HttpServletRequest 对象
     * @param label   领域标签，用于在图谱中创建节点与关系
     * @throws Exception 处理异常
     */
    @Override
    public void importByCategory(MultipartFile file,HttpServletRequest request,String label) throws Exception {
        String fileName = file.getOriginalFilename();
        Snowflake snowflake = IdUtil.getSnowflake(1, 1);
        long categoryId = snowflake.nextId();
        TreeExcel treeExcel = new TreeExcel("0", "", fileName, file.getInputStream(), new TreeExcel.IResultHandler() {
            @Override
            public TreeExcelRecordData store(String cellVal, String cellColor, TreeExcelRecordData parent, boolean isLeaf) {
                CategoryNode submitItem = new CategoryNode();
                String[] split = cellVal.split("###");
                String nodeName=split[0];
                String relationName=parent.getLinkName()==null?"":parent.getLinkName();
                submitItem.setCategoryNodeName(nodeName);
                submitItem.setCreateUser("tc");
                submitItem.setUpdateUser("tc");
                submitItem.setSystemCode("");
                submitItem.setColor(cellColor);
                submitItem.setCategoryId(categoryId);
                Integer parentId = parent.getRecordId() != null ? Integer.parseInt(parent.getRecordId()) : 0;
                submitItem.setParentId(parentId);
                int parentIsLeaf = 0;
                if (parentId == 0) {
                    submitItem.setTreeLevel(0);
                } else {
                    CategoryNode parentNode = categoryNodeService.selectByPrimaryKey(parentId);
                    if (parentNode != null) {
                        if(parentNode.getTreeLevel()==null){
                            parentNode.setTreeLevel(0);
                        }
                        submitItem.setTreeLevel(parentNode.getTreeLevel() + 1);
                        parentIsLeaf = parentNode.getIsLeaf();
                    }
                }
                submitItem.setIsLeaf(isLeaf ? 1 : 0);

                categoryNodeService.insert(submitItem);
                Integer id = submitItem.getCategoryNodeId();
                String classCode = String.format("%s%s%s", parent.getClassCode() != null ? parent.getClassCode() : "", StringUtil.isNotBlank(parent.getClassCode()) ? "/" : "", id);
                categoryNodeService.updateCodeByPrimaryKey(id, classCode);
                if (parentIsLeaf == 1) {
                    categoryNodeService.updateLeafStatusByPrimaryKey(parentId, 0);
                }
                //创建节点
                NodeItem nodeItem=new NodeItem(id,nodeName,cellColor);
                kgRepository.createNodeWithUUid(label,nodeItem);
                //创建关系
                if(parentId>0){
                    kgRepository.createLinkByUuid(label,parentId,id,relationName);
                }
                TreeExcelRecordData data = new TreeExcelRecordData();
                data.setRecordId(String.valueOf(id));
                data.setClassCode(classCode);
                data.setLinkName(split.length>1?split[1]:"");
                return data;
            }
        });
        treeExcel.handleByStream();

    }
}
