<template>
  <div>
    <div id="kg_container" class="home">
      <KGBuilder pid="kg_container" />
    </div>
  </div>
</template>

<script>
import KGBuilder from '@/components/KGBuilder.vue'
export default {
  name: 'Home',
  components: {
    KGBuilder,
  },
}
</script>
<style>
#kg_container {
  height: 100vh;
  width: 100%;
}
#kg_container2 {
  height: 50vh;
  width: 100%;
}
</style>
