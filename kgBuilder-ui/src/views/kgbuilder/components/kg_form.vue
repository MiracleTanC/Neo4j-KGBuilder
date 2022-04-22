<template>
  <el-drawer
    ref="drawer"
    title="编辑"
    :with-header="false"
    :visible.sync="drawerShow"
    :direction="direction"
    :append-to-body="true"
  >
    <!--导入-->
    <div v-show="operate == 'import'" class="pd-20">
      <el-form>
        <el-popover
            placement="right"
            width="400"
            trigger="hover">
            <div v-if="uploadParam.type==0">
              <el-table
                :data="tableData"
                style="width: 100%">
                <el-table-column
                  prop="nodeStart"
                  label="起始节点"
                  width="180">
                </el-table-column>
                <el-table-column
                  prop="nodeEnd"
                  label="结束节点"
                  width="180">
                </el-table-column>
                <el-table-column
                  prop="ship"
                  label="关系">
                </el-table-column>
              </el-table>
            </div>
            <div v-else>
              <img style="width: 300px;" :src="uploadTips.img" />
            </div>
            <el-alert slot="reference" :title="uploadTips.tips" type="success"></el-alert>
        </el-popover>
        <el-form-item label="类型" label-width="120px">
          <el-radio-group v-model="uploadParam.type">
            <el-radio
              @change="radioClick(m)"
              :key="m.id"
              v-for="m in uploadTipsArr"
              :label="m.type"
              >{{ m.name }}</el-radio
            >
          </el-radio-group>
        </el-form-item>
        <el-form-item label="图谱领域" label-width="120px">
          <el-input
            style="width:100%"
            v-model="uploadParam.domain"
            placeholder="请输入内容"
          >
          </el-input>
        </el-form-item>
        <el-form-item label="选择文件" label-width="120px">
          <el-upload
            class=""
            ref="uploadExcel"
            :action="uploadUrl"
            accept=".csv,.xls,.xlsx"
            :show-file-list="true"
            :data="uploadParam"
            :on-success="csvSuccess"
            :auto-upload="false"
          >
            <el-button
              slot="trigger"
              class="btn-bo"
            >
              <i class="el-icon-upload"></i>
              选择文件
            </el-button>
          </el-upload>
        </el-form-item>
        <el-form-item label-width="120px">
          <el-button @click="dialogFormVisible = false">取 消</el-button>
          <el-button type="primary" @click="submitUpload">确 定</el-button>
        </el-form-item>
      </el-form>
    </div>
    <!--导出-->
    <div v-show="operate == 'export'" class="pd-20">
      <el-form>
        <el-form-item label="图谱领域" label-width="120px">
          <el-autocomplete
            style="width:100%"
            v-model="uploadParam.domain"
            placeholder="请输入内容"
            ><!--:fetch-suggestions="querySearch"-->
          </el-autocomplete>
        </el-form-item>
        <el-button type="primary" @click="exportCsv">确 定</el-button>
      </el-form>
    </div>
    <!--节点编辑-->
    <div v-show="operate == 'nodeEdit'" class="pd-20">
      <el-tabs
        type="card"
        tab-position="top"
        v-model="propActiveName"
        @tab-click="propHandleClick"
        style="margin: 10px"
      >
        <el-tab-pane label="属性编辑" name="propEdit">
          <el-form :model="graphData">
            <el-form-item label="节点名称" label-width="120px">
              <el-input v-model="graphData.name" style="width:324px"></el-input>
            </el-form-item>
            <el-form-item label="选择颜色" label-width="120px">
              <el-color-picker
                id="colorpicker"
                v-model="graphData.color"
                :predefine="predefineColors"
              >
              </el-color-picker>
            </el-form-item>
            <el-form-item label="节点半径" label-width="120px">
              <el-slider :min="25" v-model="graphData.r" style="width:324px"></el-slider>
            </el-form-item>
          </el-form>
        </el-tab-pane>
        <el-tab-pane label="添加图片" name="propImage">
          <el-form>
            <el-form-item label="本地上传" label-width="120px">
              <el-upload
                class=""
                name="file"
                ref="upload"
                :headers="uploadHeader"
                :action="uploadImageUrl"
                accept=".jpg,.png"
                multiple
                :show-file-list="false"
                :data="uploadImageParam"
                :on-success="uploadSuccess"
                :auto-upload="true"
              >
                <el-button slot="trigger" size="small" type="primary"
                  >选择</el-button
                >
              </el-upload>
            </el-form-item>
            <el-form-item label="网络地址" label-width="120px">
              <el-input v-model="netImageUrl" style="width: 60%"></el-input>
              <a href="javascript:void(0)" @click="addNetImage" class="cg">
                <i class="el-icon-plus"></i>
              </a>
            </el-form-item>
            <el-form-item label="已选图片" label-width="120px">
              <ul class="el-upload-list el-upload-list--picture-card">
                <li
                  v-for="item in nodeImageList"
                  class="el-upload-list__item is-success"
                >
                  <img
                    :src="imageUrlFormat(item)"
                    alt=""
                    class="el-upload-list__item-thumbnail"
                  />
                  <label class="el-upload-list__item-status-label">
                    <i class="el-icon-upload-success el-icon-check"></i>
                  </label>
                  <i class="el-icon-close" @click="imageHandleRemove(item)"></i>
                  <span class="el-upload-list__item-actions">
                    <span class="el-upload-list__item-preview">
                      <i
                        class="el-icon-zoom-in"
                        @click="handlePictureCardPreview(item)"
                      ></i>
                    </span>
                    <span class="el-upload-list__item-delete">
                      <i
                        class="el-icon-delete"
                        @click="imageHandleRemove(item)"
                      ></i>
                    </span>
                  </span>
                </li>
              </ul>
            </el-form-item>
          </el-form>
        </el-tab-pane>
        <el-tab-pane label="添加描述" name="richTextEdit">
          <div
            ref="editorToolbar"
            class="wange-toolbar"
          ></div>
          <div
            ref="editorContent"
            class="wangeditor-form"
          ></div>
        </el-tab-pane>
      </el-tabs>
      <div slot="footer" class="dialog-footer">
        <el-button
          v-show="propActiveName == 'propImage'"
          type="primary"
          @click="saveNodeImage"
          class="btn-line cur"
          >保存</el-button
        >
        <el-button
          v-show="propActiveName == 'richTextEdit'"
          @click="saveNodeContent"
          type="primary"
          class="btn-line cur"
          >保存</el-button
        >
        <el-button
          v-show="propActiveName == 'propEdit' && graphData.uuid != 0"
          type="primary"
          @click="createNode"
          >更新</el-button
        >
        <el-button
          v-show="propActiveName == 'propEdit' && graphData.uuid == 0"
          type="primary"
          @click="createNode"
          >创建</el-button
        >
        <el-button @click="resetSubmit">取消</el-button>
      </div>
    </div>
    <!--段落识别-->
    <div v-show="operate == 'recognition'" class="pd-20">
      <div class="mb-l">段落识别</div>
      开发中。。。
    </div>
    <!--添加下级-->
    <div v-show="operate == 'batchAddChild'" class="pd-20">
      <div class="mb-l">添加下级</div>
      <el-form ref="form"  label-width="120px">
        <el-form-item label="关系">
          <el-input v-model="batchCreateData.relation"></el-input>
        </el-form-item>
        <el-form-item label="子节点名称">
          <el-input v-model="batchCreateData.targetNodeNames"></el-input>
          <span class="mb-label">（多个以英文逗号隔开）</span>
        </el-form-item>
        <el-form-item label-width="120px">
          <el-button type="primary" @click="batchCreateChildNode">确定</el-button>
          <el-button>取消</el-button>
        </el-form-item>
      </el-form>

    </div>
    <!--批量添加-->
    <div v-show="operate == 'batchAdd'">
      <div  class="mb-l">批量添加</div>
      <el-form ref="form"  label-width="120px">
        <el-form-item label="源节点名称">
          <el-input v-model="batchCreateData.sourceNodeName"></el-input>
          <span class="mb-label">（只能添加一个）</span>
        </el-form-item>
        <el-form-item label="关系">
          <el-input v-model="batchCreateData.relation"></el-input>
          <span class="mb-label">（只能添加一个）</span>
        </el-form-item>
        <el-form-item label="子节点名称">
          <el-input v-model="batchCreateData.targetNodeNames"></el-input>
          <span class="mb-label">（多个以英文逗号隔开,可不填）</span>
        </el-form-item>
        <el-form-item label-width="120px">
          <el-button type="primary" @click="batchCreateNode">确定</el-button>
          <el-button @click="resetSubmit">取消</el-button>
        </el-form-item>
      </el-form>
      </div>
    </div>
    <!--添加同级-->
    <div v-show="operate == 'batchAddSame'" class="pd-20">
      <div class="mb-l">添加同级</div>
      <el-form ref="form"  label-width="120px">
        <el-form-item label="源节点名称">
          <el-input v-model="batchCreateData.sourceNodeName"></el-input>
          <span class="mb-label">（多个以英文逗号隔开）</span>
        </el-form-item>
        <el-form-item label-width="120px">
          <el-button type="primary" @click="batchCreateSameNode">确定</el-button>
          <el-button @click="resetSubmit">取消</el-button>
        </el-form-item>
      </el-form>
    </div>
  </el-drawer>
</template>

<script>
import wangEditor from 'wangeditor'
import { kgBuilderApi } from "@/api";
export default {
  props: {
    data: Object
  },
  data() {
    return {
      domainId:0,
      uploadHeader:{
        // 'Content-Type': 'multipart/form-data'
      },
      uploadUrl:  process.env.VUE_APP_BASE_API+"/importGraph",
      direction: "rtl",
      drawerShow: false,
      operate: "",
      batchCreateData:{
        sourceName: '',
        targetNames: '',
        relation: ''
      },
      propActiveName: "propEdit",
      contentActiveName: "propImage",
      uploadImageUrl: process.env.VUE_APP_BASE_API+"/qiniu/upload",
      graphData:{
        uuid: '0',
        color: "ff4500",
        name: "",
        r: 30,
        x: "",
        y: ""
      },
       predefineColors: [
        "#ff4500",
        "#ff8c00",
        "#90ee90",
        "#00ced1",
        "#1e90ff",
        "#c71585"
      ],
      editorContent:"",
      uploadImageParam: {},
      nodeImageList: [],
      netImageUrl: "",
      uploadParam: { domain: "", type: 0 },
      uploadTips: {
        tips: "csv导入，注意字符集为utf-8无bom格式，三元组结构:节点-节点-关系(鼠标滑动到此处看模板)",
        img: ""
      },
      uploadTipsArr: [
      {
        tips: "csv导入，注意字符集为utf-8无bom格式，三元组结构:节点-节点-关系(鼠标滑动到此处看模板)",
        name: "三元组",
        img: "",
        type: 0
      },
      {
        tips:
          "支持合并单元格，设置颜色，设置关系需在节点后以###拼接，只识别一组关系(鼠标滑动到此处看模板)",
        name: "单元格树",
        img: "http://file.miaoleyan.com/image-20211114183140256.png",
        type: 1
      }
    ],
    tableData:[
      {
            nodeStart: '周杰伦',
            nodeEnd: '1979年1月18日',
            ship: '生日'
          }, {
            nodeStart: '周杰伦',
            nodeEnd: '台湾省新北市',
            ship: '籍贯'
          }, {
            nodeStart: '周杰伦',
            nodeEnd: '昆凌',
            ship: '妻子'
          }, {
            nodeStart: '昆凌',
            nodeEnd: 'Romeo',
            ship: '儿子'
          },
          , {
            nodeStart: '周杰伦',
            nodeEnd: 'Romeo',
            ship: '儿子'
          }
    ]
    };
  },
  components: {},
  methods: {
    init(drawerShow,operate) {
      this.operate = operate;
      this.drawerShow = drawerShow;
    },
    initNode(drawerShow,operate,node,domainId) {
      this.operate = operate;
      this.drawerShow = drawerShow;
      // node.r=parseInt(node.r);
      // node.uuid=parseInt(node.uuid);
      this.domainId=domainId;
      this.graphData=node;
    },
    batchCreateNode(){
      this.init(false,"");
       this.$emit("batchCreateNode",this.batchCreateData);
    },
    batchCreateChildNode(){
      this.init(false,"");
       this.$emit("batchCreateChildNode",this.batchCreateData);
    },
     batchCreateSameNode(){
      this.init(false,"");
       this.$emit("batchCreateSameNode",this.batchCreateData);
    },
    createNode(){
      this.init(false,"");
      this.$emit("createNode",this.graphData);
    },
    initImage(imageList){
      this.nodeImageList=imageList;
    },
    initContent(content){
      this.editorContent=content;
    },
    bthRecognition(){

    },
    radioClick(m) {
      this.uploadTips = m;
    },
    resetSubmit() {

    },
    //节点上传图片
    saveNodeImage() {
      let data = {
        domainId: this.domainId,
        nodeId: this.graphData.uuid,
        imageList: JSON.stringify(this.nodeImageList)
      };
      kgBuilderApi.saveNodeImage(JSON.stringify(data)).then(result => {
        if (result.code == 200) {
          this.init(false,"");
          this.$message({
            message: "操作成功",
            type: "success"
          });
        }
      });
    },
    //上传富文本
    saveNodeContent() {
      let data = {
        domainId: this.domainId,
        nodeId: this.graphData.uuid,
        content: this.editorContent
      };
      kgBuilderApi.saveNodeContent(JSON.stringify(data)).then(result => {
        if (result.code == 200) {
          this.init(false,"");
          this.$message({ message: "操作成功", type: "success" });
        }
      });
    },
    //预览图片
    handlePictureCardPreview(item) {
      this.dialogImageUrl = this.imageUrlFormat(item);
      debugger
      this.dialogImageVisible = true;
    },
    //添加网络图片
    addNetImage() {
      if (this.netImageUrl != "") {
        this.nodeImageList.push({ file: this.netImageUrl, imageType: 1 });
        this.netImageUrl = "";
      }
    },
    //移除图片
    imageHandleRemove(url) {
      this.nodeImageList.splice(this.nodeImageList.indexOf(url), 1);
    },
    //图片格式化
    imageUrlFormat(item) {
      return item.file;
    },
    dbImageUrlFormat(item) {
      return item.fileName;
    },
    uploadSuccess(res, file) {
      if (res.success == 1) {
        for (let i = 0; i < res.results.length; i++) {
          let fileItem = res.results[i];
          this.nodeImageList.push({ fileUrl: fileItem.url });
        }
      } else {
        this.$message.error(res.msg);
      }
    },
    initEditor() {
      if (this.editor != null) return;
      let _this=this;
      this.editor = new wangEditor(this.$refs.editorToolbar, this.$refs.editorContent)
      this.editor.config.onchange = function(html) {
        _this.editorContent = html;
      };
      this.editor.config.uploadFileName = "file";
      //this.editor.config.uploadImgHeaders = headers;
      this.editor.config.uploadImgServer = process.env.VUE_APP_BASE_API+"/qiniu/upload"; // 上传图片到服务器
      this.editor.config.uploadImgHooks = {
        // 如果服务器端返回的不是 {errno:0, data: [...]} 这种格式，可使用该配置
        // （但是，服务器端返回的必须是一个 JSON 格式字符串！！！否则会报错）
        customInsert: function(insertImg, res, editor) {
          // 图片上传并返回结果，自定义插入图片的事件（而不是编辑器自动插入图片！！！）
          // insertImg 是插入图片的函数，editor 是编辑器对象，result 是服务器端返回的结果
          for (let i = 0; i < res.results.length; i++) {
            let fileItem = res.results[i];
            insertImg(fileItem.url);
          }
        }
      };
      this.editor.create();
    },
    propHandleClick(tab) {
      if (tab.name == "richTextEdit") {
        this.initEditor();
        this.editorContent = "";
        this.$emit("initNodeContent");

      }
      if (tab.name == "propImage") {
        this.nodeImageList = [];
        this.$emit("initNodeImage");
      }
    },
    exportCsv() {
      let data = { domain: this.uploadParam.domain };
      kgBuilderApi.exportGraph(data).then(result => {
        if (result.code == 200) {
          this.exportFormVisible = false;
          window.open(result.csvUrl);
        }
      });
    },
    submitUpload() {
      this.$refs.uploadExcel.submit();
      //关闭窗口
       this.init(false,"");
       //刷新领域标签
       this.$emit("getDomain",1);
    },
    csvSuccess() {
      this.$refs.upload.clearFiles();
      this.uploadParam.domain = "";
      this.$message({
        message: "正在导入中,请稍后查看",
        type: "success"
      });
    },


  }
};
</script>
<style>
.pd-20{
  padding: 20px;
}
.el-drawer__body {
    padding: 20px;
}
</style>
