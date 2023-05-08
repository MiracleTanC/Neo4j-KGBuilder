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

import javax.servlet.http.HttpServletRequest;
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
    @Override
    public void deleteKGDomain(String domain) {
        kgRepository.deleteKgDomain(domain);
    }

    @Override
    public HashMap<String, Object> queryGraphResult(GraphQuery query) {
        return kgRepository.queryGraphResult(query);
    }

    @Override
    public HashMap<String, Object> getdomainnodes(String domain, Integer pageIndex, Integer pageSize) {
        return kgRepository.getDomainNodes(domain, pageIndex, pageSize);
    }

    @Override
    public long getRelationNodeCount(String domain, long nodeId) {
        return kgRepository.getRelationNodeCount(domain, nodeId);
    }

    @Override
    public void createDomain(String domain) {
        kgRepository.createDomain(domain);
    }

    @Override
    public void quickCreateDomain(String domain, String nodeName) {
        kgRepository.quickCreateDomain(domain,nodeName);
    }

    @Override
    public HashMap<String, Object> getMoreRelationNode(String domain, String nodeId) {
        return kgRepository.getMoreRelationNode(domain, nodeId);
    }

    @Override
    public HashMap<String, Object> updateNodeName(String domain, String nodeId, String nodeName) {
        return kgRepository.updateNodeName(domain, nodeId, nodeName);
    }

    @Override
    public HashMap<String, Object> createNode(String domain, NodeItem entity) {
        return kgRepository.createNode(domain, entity);
    }

    @Override
    public HashMap<String, Object> batchCreateNode(String domain, String sourceName, String relation,
                                                   String[] targetNames) {
        return kgRepository.batchCreateNode(domain, sourceName, relation, targetNames);
    }

    @Override
    public HashMap<String, Object> batchCreateChildNode(String domain, String sourceId, Integer entityType,
                                                        String[] targetNames, String relation) {
        return kgRepository.batchCreateChildNode(domain, sourceId, entityType, targetNames, relation);
    }

    @Override
    public List<HashMap<String, Object>> batchCreateSameNode(String domain, Integer entityType, String[] sourceNames) {
        return kgRepository.batchCreateSameNode(domain, entityType, sourceNames);
    }

    @Override
    public HashMap<String, Object> createLink(String domain, long sourceId, long targetId, String ship) {
        return kgRepository.createLink(domain, sourceId, targetId, ship);
    }

    @Override
    public HashMap<String, Object> updateLink(String domain, long shipId, String shipName) {
        return kgRepository.updateLink(domain, shipId, shipName);
    }

    @Override
    public List<HashMap<String, Object>> deleteNode(String domain, long nodeId) {
        return kgRepository.deleteNode(domain, nodeId);
    }

    @Override
    public void deleteLink(String domain, long shipId) {
        kgRepository.deleteLink(domain, shipId);
    }

    @Override
    public HashMap<String, Object> createGraphByText(String domain, Integer entityType, Integer operateType,
                                                     Integer sourceId, String[] rss) {
        return kgRepository.createGraphByText(domain, entityType, operateType, sourceId, rss);
    }

    @Override
    public void batchCreateGraph(String domain, List<Map<String, Object>> params) {
        kgRepository.batchCreateGraph(domain, params);
    }

    @Override
    public void updateNodeFileStatus(String domain, long nodeId, int status) {
        kgRepository.updateNodeFileStatus(domain,nodeId,status);
    }

    @Override
    public void updateNodeImg(String domain, long nodeId, String img) {
        kgRepository.updateNodeImg(domain,nodeId,img);
    }

    @Override
    public void removeNodeImg(String domain, long nodeId) {

    }

    @Override
    public void updateCoordinateOfNode(String domain, String uuid, Double fx, Double fy) {
        kgRepository.updateCoordinateOfNode(domain,uuid,fx,fy);
    }

    @Override
    public void batchUpdateGraphNodesCoordinate(String domain, List<NodeCoordinateItem> nodes) {
        kgRepository.batchUpdateGraphNodesCoordinate(domain,nodes);
    }

    @Override
    public void batchInsertByCSV(String domain, String csvUrl, int status) {
        kgRepository.batchInsertByCsv(domain, csvUrl, status);
    }

    @Override
    public void importBySyz(MultipartFile file,HttpServletRequest request,String label,Integer isCreateIndex) throws Exception {
        List<Map<String, Object>> dataList = getFormatData(file);
        String filename = config.getLocation()+IdUtil.getSnowflakeNextIdStr()+ ".csv";
        CsvWriter writer = CsvUtil.getWriter(filename, CharsetUtil.CHARSET_UTF_8);
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
