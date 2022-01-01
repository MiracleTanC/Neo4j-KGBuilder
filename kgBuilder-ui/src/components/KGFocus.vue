<!--
 * @Description: file content
 * @Author: tc
 * @Date: 2022-01-01 12:53:00
 * @LastEditors: your name
 * @LastEditTime: 2022-01-01 14:06:07
-->
<template>
  <div id="follow-us" class="guanzhu" style="padding: 20px;">
          <h2 class="hometitle">交流</h2>
          <ul>
            <li class="wx">
              <img
                src="https://img-blog.csdnimg.cn/20201003145558813.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3BveGlhb21lbmcxODc=,size_16,color_FFFFFF,t_70#pic_center"
              />
              <a target="_blank" href="https://github.com/MiracleTanC?tab=repositories">
                  <img
                    style="position: absolute; top: 0px;width: 100px; right: 0; border: 0;z-index: 999;"
                    src="https://s3.amazonaws.com/github/ribbons/forkme_right_red_aa0000.png"
                    alt="Fork me on GitHub"
                  />
                </a>
            </li>
          </ul>
        </div>
</template>

<script>
export default {
  props: {
    data: Object
  },
  data() {
    return {

    };
  },
  mounted() {},
  components: {},
  methods: {

  }
};
</script>
<style>
.guanzhu ul li {
  font-size: 12px;
  margin-bottom: 10px;
  background: #fff;
  color: #525252;
  line-height: 40px;
  border: 1px solid #ddd;
  border-radius: 2px;
  position: relative;
  text-overflow: ellipsis;
  white-space: nowrap;
  overflow: hidden;
}
.guanzhu .wx img {
  width: 100%;
}
</style>
