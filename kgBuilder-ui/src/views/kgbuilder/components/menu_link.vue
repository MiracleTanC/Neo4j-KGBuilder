<template>
<!-- 连线按钮组 -->
<ul
      class="el-dropdown-menu el-popper linkmenubar"
      id="link_menubar2"
      :style="linuMenuStyle"
      @mouseleave="linkMenuBarLeave"
      v-show="linkMenuShow"
    >
      <li class="el-dropdown-menu__item" @click="updateLinkName">
        <svg class="icon" aria-hidden="true">
          <use xlink:href="#icon-editor"></use>
        </svg>
        <span class="pl-15">编辑</span>
      </li>
      <li class="el-dropdown-menu__item" @click="deleteLink">
        <svg class="icon" aria-hidden="true">
          <use xlink:href="#icon-shanchu"></use>
        </svg>
        <span class="pl-15">删除</span>
      </li>
    </ul>
</template>

<script>
// import * as d3 from "d3";

export default {
  props: {

  },
  data() {
    return {
      top: '0px',
      left: '0px',
      linkMenuShow: false
    };
  },
  computed: {
    linuMenuStyle() {
      return {
        position:'absolute',
        top: this.top+ "px",
        left: this.left+ "px"
      };
    },
  },
  components: {

  },
  methods: {
    init(data) {
      //debugger
      this.top=data.top;
      this.left=data.left;
      this.linkMenuShow=data.show;

    },
    updateLinkName(){
      this.$emit("updateLinkName");
    },
    deleteLink(){
      this.$emit("deleteLink");
    },
    linkMenuBarLeave() {
      //d3.select(this).style("display", "none");
      this.linkMenuShow=false;
    },
  }
};
</script>
<style>

</style>
