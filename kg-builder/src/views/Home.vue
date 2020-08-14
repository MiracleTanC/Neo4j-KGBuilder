<template>
  <div id="kg_container" class="home">
    <KGBuilder pid="kg_container" />
  </div>
</template>

<script>
// @ is an alias to /src
import KGBuilder from "@/components/KGBuilder.vue";
export default {
  name: "Home",
  components: {
    KGBuilder
  }
};
</script>
<style>
#kg_container {
  height: 100vh;
  width: 100%;
}
</style>
