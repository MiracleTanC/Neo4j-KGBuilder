<template>
  <div>
    <img src="../assets/logo.png" />
    <h1>KG_Builder</h1>
    <div>
      <a href="https://github.com/MiracleTanC/Neo4j-KGBuilder"
        ><h1>Github:KG_Builder可视化</h1>
      </a>
    </div>
    <div>
      <div>
        <!-- router-link定义页面中点击触发部分 -->
        <router-link class="link" to="/kg">示例1：单向连线</router-link>
        <router-link class="link" to="/kg2">示例2：多向连线</router-link>
      </div>
      <!-- router-view定义页面中显示部分 -->
      <router-view></router-view>
    </div>
    <h1>交流</h1>
    <img
      src="https://img-blog.csdnimg.cn/20201003145558813.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3BveGlhb21lbmcxODc=,size_16,color_FFFFFF,t_70#pic_center"
    />
  </div>
</template>

<script>
// @ is an alias to /src

/* import KGBuilderSample from '@/components/KGBuilderSample.vue' */
export default {
  name: 'Home',
  components: {},
}
</script>
<style>
.link {
  margin: 10px;
}
</style>
