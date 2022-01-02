<template>
  <el-dialog title="数据信息" :visible.sync="dialogVisible" width="70%">
    <el-alert
      title="使用说明"
      type="warning"
      description="以下图谱信息可以被存储起来，方便下一次数据加载"
      show-icon
      close-text="知道了"
    >
    </el-alert>
    <br />
    <!--一个高亮显示的插件-->
    <codemirror
      :value="flowJsonData"
      :options="options"
      class="code"
    ></codemirror>
  </el-dialog>
</template>

<script>
import "codemirror/lib/codemirror.css";
import { codemirror } from "vue-codemirror";

require("codemirror/mode/javascript/javascript.js");

export default {
  props: {
    data: Object
  },
  data() {
    return {
      dialogVisible: false,
      flowJsonData: {},
      options: {
        mode: { name: "javascript", json: true },
        lineNumbers: true
      }
    };
  },
  components: {
    codemirror
  },
  methods: {
    init() {
      this.dialogVisible = true;
      this.flowJsonData = JSON.stringify(this.data, null, 4).toString();
    }
  }
};
</script>
<style>
.el-dialog__body {
  padding: 30px 20px;
  color: #606266;
  font-size: 14px;
  word-break: break-all;
  text-align: left;
}
</style>
