<template>
  <!-- 空白处右键 -->
  <ul
    class="el-dropdown-menu el-popper blankmenubar"
    @click="menuBarClick"
    :style="blankMenuStyle"
    @mouseleave="menuBarLeave"
    id="blank_menubar"
    v-show="menuBarShow"
  >
    <li class="el-dropdown-menu__item" @click="btnAddSingle">
      <svg class="icon" aria-hidden="true">
        <use xlink:href="#icon-jiedian"></use>
      </svg>
      <span class="pl-15">添加节点</span>
    </li>
    <li class="el-dropdown-menu__item" @click="btnQuickAddNode">
      <svg class="icon" aria-hidden="true">
        <use xlink:href="#icon-add-rd"></use>
      </svg>
      <span class="pl-15">快速添加</span>
    </li>
  </ul>
</template>

<script>
export default {
  props: {
    data: Object
  },
  data() {
    return {
      top: "0px",
      left: "0px",
      menuBarShow: false
    };
  },
  components: {},
  computed: {
    /**
     * 空白右键菜单样式（绝对定位）
     * @returns {{position:string,top:string,left:string}}
     */
    blankMenuStyle() {
      return {
        position: "absolute",
        top: this.top+'px',
        left: this.left+'px'
      };
    }
  },
  methods: {
    /**
     * 初始化菜单位置与显隐
     * @param {{top:number,left:number,show:boolean}} data 位置信息
     */
    init(data) {
      this.top = data.top;
      this.left = data.left;
      this.menuBarShow = data.show;
    },
    /**
     * 触发添加单节点事件
     * @emits btnAddSingle
     */
    btnAddSingle() {
      this.$emit("btnAddSingle");
    },
    /**
     * 触发快速添加事件
     * @emits btnQuickAddNode
     */
    btnQuickAddNode() {
      this.$emit("btnQuickAddNode");
    },
    /**
     * 点击菜单区域隐藏菜单
     */
    menuBarClick() {
      this.menuBarShow=false;
    },
    /**
     * 鼠标离开时隐藏菜单
     */
    menuBarLeave() {
     this.menuBarShow=false;
    }
  }
};
</script>
<style></style>
