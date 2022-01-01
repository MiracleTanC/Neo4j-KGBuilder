package com.warmer.web.controller;

import com.warmer.base.util.R;
import com.warmer.base.util.StringUtil;
import com.warmer.web.entity.CategoryNode;
import com.warmer.web.model.TreeExcel;
import com.warmer.web.model.TreeExcelRecordData;
import com.warmer.web.service.CategoryNodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
public class CategoryController extends BaseController{
    @Autowired
    CategoryNodeService categoryNodeService;
    @PostMapping("/importKGNodes")
    @ResponseBody
    public R<String> importKGNodes(Long categoryId, Integer parentId, HttpServletRequest request) {
        if (parentId == null) {
            parentId = 0;

        }
        String  parentCode="";
        CategoryNode searchCategoryDetail = categoryNodeService.selectByPrimaryKey(parentId);
        if(searchCategoryDetail!=null){
            parentCode=searchCategoryDetail.getSystemCode();
        }
        MultipartHttpServletRequest multipartHttpServletRequest = (MultipartHttpServletRequest) request;
        MultipartFile file = multipartHttpServletRequest.getFile("file");
        String count = "";
        try {
            if (file == null) {
                return R.error("请上传 有效的excel的文件");
            }
            String fileName = file.getOriginalFilename();
            String extName = fileName != null ? fileName.substring(fileName.lastIndexOf(".")) : null;

            if (StringUtil.isBlank(fileName) || (!extName.equals(".xls") && !extName.equals(".xlsx"))) {
                return R.error("请上传 xls 或 xlsx 类型的文件");
            }
            TreeExcel treeExcel = new TreeExcel(String.valueOf(parentId), parentCode, fileName, file.getInputStream(), new TreeExcel.IResultHandler() {
                @Override
                public TreeExcelRecordData store(String cellVal, String cellColor, TreeExcelRecordData parent, boolean isLeaf) {
                    CategoryNode submitItem = new CategoryNode();
                    submitItem.setCategoryNodeName(cellVal);
                    submitItem.setCreateUser("tc");
                    submitItem.setUpdateUser("tc");
                    submitItem.setSystemCode("");
                    submitItem.setCategoryId(categoryId);
                    List<CategoryNode> brotherNodes = categoryNodeService.selectByParentIdAndName(categoryId, Integer.parseInt(parent.getRecordId()), cellVal.trim());
                    if (brotherNodes != null && brotherNodes.size() > 0) { // 如果同一级下已有同名的，不在新增。
                        TreeExcelRecordData data = new TreeExcelRecordData();
                        data.setRecordId(String.valueOf(brotherNodes.get(0).getCategoryNodeId()));
                        data.setClassCode(brotherNodes.get(0).getSystemCode());
                        return data;
                    }
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
                    TreeExcelRecordData data = new TreeExcelRecordData();
                    data.setRecordId(String.valueOf(id));
                    data.setClassCode(classCode);
                    return data;
                }
            });
            treeExcel.handleByStream();
            return R.success(count);
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
            return R.error("处理失败");
        }
    }
}
