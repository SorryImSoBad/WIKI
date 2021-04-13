<template>
  <a-layout>
    <the_header/>
    <router-view/>
    <the_footer/>
  </a-layout>
</template>

<script lang="ts">
import { defineComponent } from 'vue';
import the_header from '@/components/the-header.vue';
import the_footer from '@/components/the-footer.vue';

export default defineComponent({
  name: 'App',
  components:{
    the_header,
    the_footer,
  }
});
</script>

<style>
#components-layout-demo-top-side-2 .logo {
  float: left;
  width: 120px;
  height: 31px;
  margin: 16px 24px 16px 0;
  background: rgba(255, 255, 255, 0.3);
}

.ant-row-rtl #components-layout-demo-top-side-2 .logo {
  float: right;
  margin: 16px 0 16px 24px;
}

.site-layout-background {
  background: #fff;
}
</style>
