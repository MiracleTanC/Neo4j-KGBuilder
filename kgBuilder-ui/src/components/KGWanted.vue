<template>
  <el-dialog
    title="反馈"
    :visible.sync="dialogVisible"
    width="70%"
    customClass="flowHelp"
  >
  <el-alert
    style="margin:10px"
    title="有点迫不及待了吧"
    type="success"
    description="不听不听，王八念经，黑灰化肥会挥发发灰黑化肥挥发；灰黑化肥会挥发发黑灰化肥发挥。 黑灰化肥会挥发发灰黑化肥黑灰挥发化为灰……"
    >
  </el-alert>
    <el-form ref="form" :model="form" label-width="80px">
  <el-form-item label="反馈主题">
    <el-input v-model="form.name"></el-input>
  </el-form-item>
  <el-form-item label="反馈类型">
    <el-select v-model="form.type" placeholder="请选择反馈类型">
      <el-option label="需求" value="0"></el-option>
      <el-option label="bug" value="1"></el-option>
      <el-option label="表扬" value="2"></el-option>
      <el-option label="加入" value="3"></el-option>
    </el-select>
  </el-form-item>
  <el-form-item label="内容">
    <el-input type="textarea" v-model="form.desc"  rows="6" :autosize="{ minRows: 6, maxRows: 10}" placeholder="简短描述一下吧，我应该会选择性看到,看到了我也不见得做，O(∩_∩)O哈哈~"></el-input>
  </el-form-item>
  <el-form-item label="联系方式">
    <el-input v-model="form.email"></el-input>
  </el-form-item>
  <el-form-item>
    <el-button type="primary" @click="onSubmit">立即创建</el-button>
    <el-button @click="dialogVisible=!dialogVisible">取消</el-button>
  </el-form-item>
</el-form>
  </el-dialog>
</template>

<script>
import { kgBuilderApi } from "@/api";
export default {
  data() {
    return {
      dialogVisible: false,
      form:{
        name:"",
        type:"0",
        desc:"",
        email:""
      }
    };
  },
  components: {},
  methods: {
    init() {
      this.dialogVisible = true;
    },
    onSubmit(){
        let data = this.form;
      kgBuilderApi.feedBack(data).then(result => {
        if (result.code == 200) {
          this.dialogVisible=false;
             this.$message({
            message: "操作成功",
            type: "success"
          });
        }
      });
    }
  }
};
</script>

<style>
.flowHelp {
  height: 80%;
}
</style>
