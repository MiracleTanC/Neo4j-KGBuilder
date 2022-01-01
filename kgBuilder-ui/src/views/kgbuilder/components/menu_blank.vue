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
    blankMenuStyle() {
      return {
        position: "absolute",
        top: this.top+'px',
        left: this.left+'px'
      };
    }
  },
  methods: {
    init(data) {
      this.top = data.top;
      this.left = data.left;
      this.menuBarShow = data.show;
    },
    btnAddSingle() {
      this.$emit("btnAddSingle");
    },
    btnQuickAddNode() {
      this.$emit("btnQuickAddNode");
    },
    menuBarClick() {
      this.menuBarShow=false;
    },
    menuBarLeave() {
     this.menuBarShow=false;
    }
  }
};
</script>
<style></style>
