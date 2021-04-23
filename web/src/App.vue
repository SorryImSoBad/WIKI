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
.logo {
  width: 120px;
  height: 31px;
  background: rgba(255, 255, 255, 0.2);
  margin: 16px 28px 16px 0;
  float: left;
}
</style>
