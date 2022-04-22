<template>
  <!-- 连线按钮组 -->
   <ul
      v-show="linkMenuShow"
      id="link_menubar2"
      class="el-dropdown-menu el-popper linkmenubar"
      :style="linuMenuStyle"
      @mouseleave="linkMenuBarLeave"
    >
      <li class="el-dropdown-menu__item" @click="updateLink">
        <span class="pl-15">编辑</span>
      </li>
      <li class="el-dropdown-menu__item" @click="deleteLink">
        <span class="pl-15">删除</span>
      </li>
    </ul>
</template>

<script>
// import * as d3 from "d3";
export default {
  inject: ['deleteLinkName', 'updateLinkName'],
  components: {},
  props: {},
  data() {
    return {
      top: '0px',
      left: '0px',
      linkMenuShow: false,
      clike: false,
      data:null
    }
  },
  computed: {
    linuMenuStyle() {
      return {
        position: 'absolute',
        top: this.top + 'px',
        left: this.left + 'px'
      }
    }
  },
  methods: {
    init(data) {
      this.top = data.top
      this.left = data.left
      this.linkMenuShow = data.show
      this.data = data.sdata
    },
    updateLink() {
      this.updateLinkName(this.data)
    },
    deleteLink() {
      this.deleteLinkName(this.data)
    },
    linkMenuBarLeave() {
      // d3.select(this).style("display", "none");
      this.linkMenuShow = false
    }
  }
}
</script>
<style></style>
