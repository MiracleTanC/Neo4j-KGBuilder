<template>
   <div id="richContainer" :style="richerStyle" v-show="richerShow">
      <div
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
        <el-scrollbar v-show="editorContent != ''" class="mind-fj-p">
          <p v-html="editorContent"></p>
        </el-scrollbar>
      </div>
    </div>
</template>

<script>
import E from "wangeditor"
export default {
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
    init(content,imageList,left,top){
      this.richerShow=true;
      this.left=left;
      this.top=top;
      this.editorContent=content;
      this.showImageList=imageList;
    },
    close(){
      this.richerShow=false;
    }
  }
};
</script>
<style>

</style>
