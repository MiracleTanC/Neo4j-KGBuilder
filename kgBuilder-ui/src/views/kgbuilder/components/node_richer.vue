<template>
   <div id="richContainer" :style="richerStyle" v-show="richerShow">
      <!-- <div
        class="mind-fj-box"
        v-show="showImageList.length > 0 || editorContent != ''"
      >
        <div class="mind-carousel" v-show="showImageList.length > 0">
          <el-carousel height="197px" :interval="2000" arrow="always">
            <el-carousel-item v-for="item in showImageList" :key="item.ID">
              <div class="carous-img">
                <img :src="item.fileName" alt="" />
              </div>
            </el-carousel-item>
          </el-carousel>
        </div>

      </div> -->
      <el-scrollbar v-show="editorContent != ''" class="mind-fj-p">
          <p v-html="editorContent"></p>
        </el-scrollbar>
    </div>
</template>

<script>
import E from "wangeditor"
export default {
  /**
   * 节点富文本信息面板
   * 展示节点的富文本内容与图片轮播，支持位置与显隐控制。
   */
  props: {
    data: Object
  },
  data() {
    return {
      richerShow:false,
      left:'-1000px',
      top:'-1000px',
      editor: null,
      editorContent:'',
      showImageList:[],

    };
  },
  computed: {
    /**
     * 面板样式计算（绝对定位）
     * @returns {{width:string,position:string,top:string,left:string}}
     */
    richerStyle() {
      return {
        width: '400px',
        position: "absolute",
        top: this.top+'px',
        left: this.left+'px'
      };
    }
  },
  mounted(){

  } ,
  components: {

  },
  methods: {
    /**
     * 初始化面板内容与位置
     * @param {string} content 富文本 HTML
     * @param {Array} imageList 图片列表
     * @param {number} left 左坐标
     * @param {number} top 上坐标
     */
    init(content,imageList,left,top){
      this.richerShow=true;
      this.left=left;
      this.top=top;
      this.editorContent=content;
      this.showImageList=imageList;
    },
    /**
     * 关闭面板
     */
    close(){
      this.richerShow=false;
    }
  }
};
</script>
<style>

</style>
