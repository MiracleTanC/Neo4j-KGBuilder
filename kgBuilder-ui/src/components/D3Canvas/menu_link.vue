<template>
  <!-- 连线按钮组 -->

  <div v-if="clike == false">
    <ul
      v-show="linkMenuShow"
      id="link_menubar2"
      class="el-dropdown-menu el-popper linkmenubar"
      :style="linuMenuStyle"
      @mouseleave="linkMenuBarLeave"
    >
      <li class="el-dropdown-menu__item" @click="updateLink">
        <span class="pl-15">克隆</span>
      </li>
    </ul>
  </div>
  <div v-else>
    <ul
      v-show="linkMenuShow"
      id="link_menubar2"
      class="el-dropdown-menu el-popper linkmenubar"
      :style="linuMenuStyle"
      @mouseleave="linkMenuBarLeave"
    >
      <li class="el-dropdown-menu__item" @click="editLink">
        <span class="pl-15">编辑</span>
      </li>
      <li class="el-dropdown-menu__item" @click="typeLink">
        <span class="pl-15">类型</span>
      </li>
      <li class="el-dropdown-menu__item" @click="deleteLink">
        <span class="pl-15">删除</span>
      </li>
    </ul>
  </div>
</template>

<script>
// import * as d3 from "d3";
export default {
  inject: ['deleteLinkName', 'typeLinkName', 'editLinkName', 'updateLinkName'],
  components: {},
  props: {},
  data() {
    return {
      top: '0px',
      left: '0px',
      linkMenuShow: false,
      clike: false
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
      // debugger
      this.top = data.top
      this.left = data.left
      this.linkMenuShow = data.show
      this.clike = data.clike
    },
    updateLink() {
      this.updateLinkName(this)
    },
    editLink() {
      this.editLinkName(this)
    },
    typeLink() {
      this.typeLinkName(this)
    },
    deleteLink() {
      this.deleteLinkName(this)
    },
    linkMenuBarLeave() {
      // d3.select(this).style("display", "none");
      this.linkMenuShow = false
    }
  }
}
</script>
<style></style>
